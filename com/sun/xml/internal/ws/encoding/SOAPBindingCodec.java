package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;
import com.sun.xml.internal.ws.api.fastinfoset.FastInfosetFeature;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.protocol.soap.MessageCreationException;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class SOAPBindingCodec
  extends MimeCodec
  implements com.sun.xml.internal.ws.api.pipe.SOAPBindingCodec
{
  public static final String UTF8_ENCODING = "utf-8";
  public static final String DEFAULT_ENCODING = "utf-8";
  private boolean isFastInfosetDisabled;
  private boolean useFastInfosetForEncoding;
  private boolean ignoreContentNegotiationProperty;
  private final StreamSOAPCodec xmlSoapCodec;
  private final Codec fiSoapCodec;
  private final MimeCodec xmlMtomCodec;
  private final MimeCodec xmlSwaCodec;
  private final MimeCodec fiSwaCodec;
  private final String xmlMimeType;
  private final String fiMimeType;
  private final String xmlAccept;
  private final String connegXmlAccept;
  
  public StreamSOAPCodec getXMLCodec()
  {
    return xmlSoapCodec;
  }
  
  private ContentTypeImpl setAcceptHeader(Packet paramPacket, ContentTypeImpl paramContentTypeImpl)
  {
    String str;
    if ((!ignoreContentNegotiationProperty) && (contentNegotiation != ContentNegotiation.none)) {
      str = connegXmlAccept;
    } else {
      str = xmlAccept;
    }
    paramContentTypeImpl.setAcceptHeader(str);
    return paramContentTypeImpl;
  }
  
  public SOAPBindingCodec(WSFeatureList paramWSFeatureList)
  {
    this(paramWSFeatureList, Codecs.createSOAPEnvelopeXmlCodec(paramWSFeatureList));
  }
  
  public SOAPBindingCodec(WSFeatureList paramWSFeatureList, StreamSOAPCodec paramStreamSOAPCodec)
  {
    super(WebServiceFeatureList.getSoapVersion(paramWSFeatureList), paramWSFeatureList);
    xmlSoapCodec = paramStreamSOAPCodec;
    xmlMimeType = paramStreamSOAPCodec.getMimeType();
    xmlMtomCodec = new MtomCodec(version, paramStreamSOAPCodec, paramWSFeatureList);
    xmlSwaCodec = new SwACodec(version, paramWSFeatureList, paramStreamSOAPCodec);
    String str = paramStreamSOAPCodec.getMimeType() + ", " + xmlMtomCodec.getMimeType();
    WebServiceFeature localWebServiceFeature1 = paramWSFeatureList.get(FastInfosetFeature.class);
    isFastInfosetDisabled = ((localWebServiceFeature1 != null) && (!localWebServiceFeature1.isEnabled()));
    if (!isFastInfosetDisabled)
    {
      fiSoapCodec = getFICodec(paramStreamSOAPCodec, version);
      if (fiSoapCodec != null)
      {
        fiMimeType = fiSoapCodec.getMimeType();
        fiSwaCodec = new SwACodec(version, paramWSFeatureList, fiSoapCodec);
        connegXmlAccept = (fiMimeType + ", " + str);
        WebServiceFeature localWebServiceFeature2 = paramWSFeatureList.get(SelectOptimalEncodingFeature.class);
        if (localWebServiceFeature2 != null)
        {
          ignoreContentNegotiationProperty = true;
          if (localWebServiceFeature2.isEnabled())
          {
            if (localWebServiceFeature1 != null) {
              useFastInfosetForEncoding = true;
            }
            str = connegXmlAccept;
          }
          else
          {
            isFastInfosetDisabled = true;
          }
        }
      }
      else
      {
        isFastInfosetDisabled = true;
        fiSwaCodec = null;
        fiMimeType = "";
        connegXmlAccept = str;
        ignoreContentNegotiationProperty = true;
      }
    }
    else
    {
      fiSoapCodec = (fiSwaCodec = null);
      fiMimeType = "";
      connegXmlAccept = str;
      ignoreContentNegotiationProperty = true;
    }
    xmlAccept = str;
    if (WebServiceFeatureList.getSoapVersion(paramWSFeatureList) == null) {
      throw new WebServiceException("Expecting a SOAP binding but found ");
    }
  }
  
  public String getMimeType()
  {
    return null;
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    ContentType localContentType = getEncoder(paramPacket).getStaticContentType(paramPacket);
    return setAcceptHeader(paramPacket, (ContentTypeImpl)localContentType);
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException
  {
    preEncode(paramPacket);
    Object localObject = getEncoder(paramPacket).encode(paramPacket, paramOutputStream);
    localObject = setAcceptHeader(paramPacket, (ContentTypeImpl)localObject);
    postEncode();
    return (ContentType)localObject;
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    preEncode(paramPacket);
    Object localObject = getEncoder(paramPacket).encode(paramPacket, paramWritableByteChannel);
    localObject = setAcceptHeader(paramPacket, (ContentTypeImpl)localObject);
    postEncode();
    return (ContentType)localObject;
  }
  
  private void preEncode(Packet paramPacket) {}
  
  private void postEncode() {}
  
  private void preDecode(Packet paramPacket)
  {
    if (contentNegotiation == null) {
      useFastInfosetForEncoding = false;
    }
  }
  
  private void postDecode(Packet paramPacket)
  {
    paramPacket.setFastInfosetDisabled(isFastInfosetDisabled);
    if (features.isEnabled(MTOMFeature.class)) {
      paramPacket.checkMtomAcceptable();
    }
    MTOMFeature localMTOMFeature = (MTOMFeature)features.get(MTOMFeature.class);
    if (localMTOMFeature != null) {
      paramPacket.setMtomFeature(localMTOMFeature);
    }
    if (!useFastInfosetForEncoding) {
      useFastInfosetForEncoding = paramPacket.getFastInfosetAcceptable(fiMimeType).booleanValue();
    }
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    if (paramString == null) {
      paramString = xmlMimeType;
    }
    paramPacket.setContentType(new ContentTypeImpl(paramString));
    preDecode(paramPacket);
    try
    {
      if (isMultipartRelated(paramString))
      {
        super.decode(paramInputStream, paramString, paramPacket);
      }
      else if (isFastInfoset(paramString))
      {
        if ((!ignoreContentNegotiationProperty) && (contentNegotiation == ContentNegotiation.none)) {
          throw noFastInfosetForDecoding();
        }
        useFastInfosetForEncoding = true;
        fiSoapCodec.decode(paramInputStream, paramString, paramPacket);
      }
      else
      {
        xmlSoapCodec.decode(paramInputStream, paramString, paramPacket);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      if (((localRuntimeException instanceof ExceptionHasMessage)) || ((localRuntimeException instanceof UnsupportedMediaException))) {
        throw localRuntimeException;
      }
      throw new MessageCreationException(version, new Object[] { localRuntimeException });
    }
    postDecode(paramPacket);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket)
  {
    if (paramString == null) {
      throw new UnsupportedMediaException();
    }
    preDecode(paramPacket);
    try
    {
      if (isMultipartRelated(paramString))
      {
        super.decode(paramReadableByteChannel, paramString, paramPacket);
      }
      else if (isFastInfoset(paramString))
      {
        if (contentNegotiation == ContentNegotiation.none) {
          throw noFastInfosetForDecoding();
        }
        useFastInfosetForEncoding = true;
        fiSoapCodec.decode(paramReadableByteChannel, paramString, paramPacket);
      }
      else
      {
        xmlSoapCodec.decode(paramReadableByteChannel, paramString, paramPacket);
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      if (((localRuntimeException instanceof ExceptionHasMessage)) || ((localRuntimeException instanceof UnsupportedMediaException))) {
        throw localRuntimeException;
      }
      throw new MessageCreationException(version, new Object[] { localRuntimeException });
    }
    postDecode(paramPacket);
  }
  
  public SOAPBindingCodec copy()
  {
    return new SOAPBindingCodec(features, (StreamSOAPCodec)xmlSoapCodec.copy());
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket)
    throws IOException
  {
    String str = paramMimeMultipartParser.getRootPart().getContentType();
    boolean bool = isApplicationXopXml(str);
    paramPacket.setMtomRequest(Boolean.valueOf(bool));
    if (bool)
    {
      xmlMtomCodec.decode(paramMimeMultipartParser, paramPacket);
    }
    else if (isFastInfoset(str))
    {
      if (contentNegotiation == ContentNegotiation.none) {
        throw noFastInfosetForDecoding();
      }
      useFastInfosetForEncoding = true;
      fiSwaCodec.decode(paramMimeMultipartParser, paramPacket);
    }
    else if (isXml(str))
    {
      xmlSwaCodec.decode(paramMimeMultipartParser, paramPacket);
    }
    else
    {
      throw new IOException("");
    }
  }
  
  private boolean isMultipartRelated(String paramString)
  {
    return compareStrings(paramString, "multipart/related");
  }
  
  private boolean isApplicationXopXml(String paramString)
  {
    return compareStrings(paramString, "application/xop+xml");
  }
  
  private boolean isXml(String paramString)
  {
    return compareStrings(paramString, xmlMimeType);
  }
  
  private boolean isFastInfoset(String paramString)
  {
    if (isFastInfosetDisabled) {
      return false;
    }
    return compareStrings(paramString, fiMimeType);
  }
  
  private boolean compareStrings(String paramString1, String paramString2)
  {
    return (paramString1.length() >= paramString2.length()) && (paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length())));
  }
  
  private Codec getEncoder(Packet paramPacket)
  {
    if (!ignoreContentNegotiationProperty) {
      if (contentNegotiation == ContentNegotiation.none) {
        useFastInfosetForEncoding = false;
      } else if (contentNegotiation == ContentNegotiation.optimistic) {
        useFastInfosetForEncoding = true;
      }
    }
    if (useFastInfosetForEncoding)
    {
      localMessage = paramPacket.getMessage();
      if ((localMessage == null) || (localMessage.getAttachments().isEmpty()) || (features.isEnabled(MTOMFeature.class))) {
        return fiSoapCodec;
      }
      return fiSwaCodec;
    }
    if ((paramPacket.getBinding() == null) && (features != null)) {
      paramPacket.setMtomFeature((MTOMFeature)features.get(MTOMFeature.class));
    }
    if (paramPacket.shouldUseMtom()) {
      return xmlMtomCodec;
    }
    Message localMessage = paramPacket.getMessage();
    if ((localMessage == null) || (localMessage.getAttachments().isEmpty())) {
      return xmlSoapCodec;
    }
    return xmlSwaCodec;
  }
  
  private RuntimeException noFastInfosetForDecoding()
  {
    return new RuntimeException(StreamingMessages.FASTINFOSET_DECODING_NOT_ACCEPTED());
  }
  
  private static Codec getFICodec(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion)
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec");
      Method localMethod = localClass.getMethod("create", new Class[] { StreamSOAPCodec.class, SOAPVersion.class });
      return (Codec)localMethod.invoke(null, new Object[] { paramStreamSOAPCodec, paramSOAPVersion });
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\SOAPBindingCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
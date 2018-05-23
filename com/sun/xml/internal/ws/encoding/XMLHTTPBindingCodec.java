package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.encoding.xml.XMLCodec;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.MessageDataSource;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.UnknownContent;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage.XMLMultiPart;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.WritableByteChannel;
import java.util.StringTokenizer;
import javax.activation.DataSource;
import javax.xml.ws.WebServiceException;

public final class XMLHTTPBindingCodec
  extends MimeCodec
{
  private static final String BASE_ACCEPT_VALUE = "*";
  private static final String APPLICATION_FAST_INFOSET_MIME_TYPE = "application/fastinfoset";
  private boolean useFastInfosetForEncoding;
  private final Codec xmlCodec;
  private final Codec fiCodec;
  private static final String xmlAccept = null;
  private static final String fiXmlAccept = "application/fastinfoset, *";
  
  private ContentTypeImpl setAcceptHeader(Packet paramPacket, ContentType paramContentType)
  {
    ContentTypeImpl localContentTypeImpl = (ContentTypeImpl)paramContentType;
    if ((contentNegotiation == ContentNegotiation.optimistic) || (contentNegotiation == ContentNegotiation.pessimistic)) {
      localContentTypeImpl.setAcceptHeader("application/fastinfoset, *");
    } else {
      localContentTypeImpl.setAcceptHeader(xmlAccept);
    }
    paramPacket.setContentType(localContentTypeImpl);
    return localContentTypeImpl;
  }
  
  public XMLHTTPBindingCodec(WSFeatureList paramWSFeatureList)
  {
    super(SOAPVersion.SOAP_11, paramWSFeatureList);
    xmlCodec = new XMLCodec(paramWSFeatureList);
    fiCodec = getFICodec();
  }
  
  public String getMimeType()
  {
    return null;
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    if ((paramPacket.getInternalMessage() instanceof XMLMessage.MessageDataSource))
    {
      XMLMessage.MessageDataSource localMessageDataSource = (XMLMessage.MessageDataSource)paramPacket.getInternalMessage();
      if (localMessageDataSource.hasUnconsumedDataSource())
      {
        localContentType = getStaticContentType(localMessageDataSource);
        return localContentType != null ? setAcceptHeader(paramPacket, localContentType) : null;
      }
    }
    ContentType localContentType = super.getStaticContentType(paramPacket);
    return localContentType != null ? setAcceptHeader(paramPacket, localContentType) : null;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramPacket.getInternalMessage() instanceof XMLMessage.MessageDataSource))
    {
      XMLMessage.MessageDataSource localMessageDataSource = (XMLMessage.MessageDataSource)paramPacket.getInternalMessage();
      if (localMessageDataSource.hasUnconsumedDataSource()) {
        return setAcceptHeader(paramPacket, encode(localMessageDataSource, paramOutputStream));
      }
    }
    return setAcceptHeader(paramPacket, super.encode(paramPacket, paramOutputStream));
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    if (contentNegotiation == null) {
      useFastInfosetForEncoding = false;
    }
    if (paramString == null)
    {
      xmlCodec.decode(paramInputStream, paramString, paramPacket);
    }
    else if (isMultipartRelated(paramString))
    {
      paramPacket.setMessage(new XMLMessage.XMLMultiPart(paramString, paramInputStream, features));
    }
    else if (isFastInfoset(paramString))
    {
      if (fiCodec == null) {
        throw new RuntimeException(StreamingMessages.FASTINFOSET_NO_IMPLEMENTATION());
      }
      useFastInfosetForEncoding = true;
      fiCodec.decode(paramInputStream, paramString, paramPacket);
    }
    else if (isXml(paramString))
    {
      xmlCodec.decode(paramInputStream, paramString, paramPacket);
    }
    else
    {
      paramPacket.setMessage(new XMLMessage.UnknownContent(paramString, paramInputStream));
    }
    if (!useFastInfosetForEncoding) {
      useFastInfosetForEncoding = isFastInfosetAcceptable(acceptableMimeTypes);
    }
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket)
    throws IOException
  {}
  
  public MimeCodec copy()
  {
    return new XMLHTTPBindingCodec(features);
  }
  
  private boolean isMultipartRelated(String paramString)
  {
    return compareStrings(paramString, "multipart/related");
  }
  
  private boolean isXml(String paramString)
  {
    return (compareStrings(paramString, "application/xml")) || (compareStrings(paramString, "text/xml")) || ((compareStrings(paramString, "application/")) && (paramString.toLowerCase().indexOf("+xml") != -1));
  }
  
  private boolean isFastInfoset(String paramString)
  {
    return compareStrings(paramString, "application/fastinfoset");
  }
  
  private boolean compareStrings(String paramString1, String paramString2)
  {
    return (paramString1.length() >= paramString2.length()) && (paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length())));
  }
  
  private boolean isFastInfosetAcceptable(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken().trim();
      if (str.equalsIgnoreCase("application/fastinfoset")) {
        return true;
      }
    }
    return false;
  }
  
  private ContentType getStaticContentType(XMLMessage.MessageDataSource paramMessageDataSource)
  {
    String str = paramMessageDataSource.getDataSource().getContentType();
    boolean bool = XMLMessage.isFastInfoset(str);
    if (!requiresTransformationOfDataSource(bool, useFastInfosetForEncoding)) {
      return new ContentTypeImpl(str);
    }
    return null;
  }
  
  private ContentType encode(XMLMessage.MessageDataSource paramMessageDataSource, OutputStream paramOutputStream)
  {
    try
    {
      boolean bool = XMLMessage.isFastInfoset(paramMessageDataSource.getDataSource().getContentType());
      DataSource localDataSource = transformDataSource(paramMessageDataSource.getDataSource(), bool, useFastInfosetForEncoding, features);
      InputStream localInputStream = localDataSource.getInputStream();
      byte[] arrayOfByte = new byte['Ѐ'];
      int i;
      while ((i = localInputStream.read(arrayOfByte)) != -1) {
        paramOutputStream.write(arrayOfByte, 0, i);
      }
      return new ContentTypeImpl(localDataSource.getContentType());
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  protected Codec getMimeRootCodec(Packet paramPacket)
  {
    if (contentNegotiation == ContentNegotiation.none) {
      useFastInfosetForEncoding = false;
    } else if (contentNegotiation == ContentNegotiation.optimistic) {
      useFastInfosetForEncoding = true;
    }
    return (useFastInfosetForEncoding) && (fiCodec != null) ? fiCodec : xmlCodec;
  }
  
  public static boolean requiresTransformationOfDataSource(boolean paramBoolean1, boolean paramBoolean2)
  {
    return ((paramBoolean1) && (!paramBoolean2)) || ((!paramBoolean1) && (paramBoolean2));
  }
  
  public static DataSource transformDataSource(DataSource paramDataSource, boolean paramBoolean1, boolean paramBoolean2, WSFeatureList paramWSFeatureList)
  {
    try
    {
      XMLHTTPBindingCodec localXMLHTTPBindingCodec;
      Packet localPacket;
      ByteArrayBuffer localByteArrayBuffer;
      ContentType localContentType;
      if ((paramBoolean1) && (!paramBoolean2))
      {
        localXMLHTTPBindingCodec = new XMLHTTPBindingCodec(paramWSFeatureList);
        localPacket = new Packet();
        localXMLHTTPBindingCodec.decode(paramDataSource.getInputStream(), paramDataSource.getContentType(), localPacket);
        localPacket.getMessage().getAttachments();
        localXMLHTTPBindingCodec.getStaticContentType(localPacket);
        localByteArrayBuffer = new ByteArrayBuffer();
        localContentType = localXMLHTTPBindingCodec.encode(localPacket, localByteArrayBuffer);
        return XMLMessage.createDataSource(localContentType.getContentType(), localByteArrayBuffer.newInputStream());
      }
      if ((!paramBoolean1) && (paramBoolean2))
      {
        localXMLHTTPBindingCodec = new XMLHTTPBindingCodec(paramWSFeatureList);
        localPacket = new Packet();
        localXMLHTTPBindingCodec.decode(paramDataSource.getInputStream(), paramDataSource.getContentType(), localPacket);
        contentNegotiation = ContentNegotiation.optimistic;
        localPacket.getMessage().getAttachments();
        localXMLHTTPBindingCodec.getStaticContentType(localPacket);
        localByteArrayBuffer = new ByteArrayBuffer();
        localContentType = localXMLHTTPBindingCodec.encode(localPacket, localByteArrayBuffer);
        return XMLMessage.createDataSource(localContentType.getContentType(), localByteArrayBuffer.newInputStream());
      }
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    return paramDataSource;
  }
  
  private static Codec getFICodec()
  {
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetCodec");
      Method localMethod = localClass.getMethod("create", new Class[0]);
      return (Codec)localMethod.invoke(null, new Object[0]);
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\XMLHTTPBindingCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
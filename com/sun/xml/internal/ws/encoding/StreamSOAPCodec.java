package com.sun.xml.internal.ws.encoding;

import com.oracle.webservices.internal.impl.encoding.StreamDecoderImpl;
import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class StreamSOAPCodec
  implements com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec, RootOnlyCodec
{
  private static final String SOAP_ENVELOPE = "Envelope";
  private static final String SOAP_HEADER = "Header";
  private static final String SOAP_BODY = "Body";
  private final SOAPVersion soapVersion;
  protected final SerializationFeature serializationFeature;
  private final StreamDecoder streamDecoder;
  private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";
  
  StreamSOAPCodec(SOAPVersion paramSOAPVersion)
  {
    this(paramSOAPVersion, null);
  }
  
  StreamSOAPCodec(WSBinding paramWSBinding)
  {
    this(paramWSBinding.getSOAPVersion(), (SerializationFeature)paramWSBinding.getFeature(SerializationFeature.class));
  }
  
  StreamSOAPCodec(WSFeatureList paramWSFeatureList)
  {
    this(WebServiceFeatureList.getSoapVersion(paramWSFeatureList), (SerializationFeature)paramWSFeatureList.get(SerializationFeature.class));
  }
  
  private StreamSOAPCodec(SOAPVersion paramSOAPVersion, @Nullable SerializationFeature paramSerializationFeature)
  {
    soapVersion = paramSOAPVersion;
    serializationFeature = paramSerializationFeature;
    streamDecoder = selectStreamDecoder();
  }
  
  private StreamDecoder selectStreamDecoder()
  {
    Iterator localIterator = ServiceFinder.find(StreamDecoder.class).iterator();
    if (localIterator.hasNext())
    {
      StreamDecoder localStreamDecoder = (StreamDecoder)localIterator.next();
      return localStreamDecoder;
    }
    return new StreamDecoderImpl();
  }
  
  public com.sun.xml.internal.ws.api.pipe.ContentType getStaticContentType(Packet paramPacket)
  {
    return getContentType(paramPacket);
  }
  
  public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
  {
    if (paramPacket.getMessage() != null)
    {
      String str = getPacketEncoding(paramPacket);
      invocationProperties.remove("decodedMessageCharset");
      XMLStreamWriter localXMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream, str);
      try
      {
        paramPacket.getMessage().writeTo(localXMLStreamWriter);
        localXMLStreamWriter.flush();
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
      XMLStreamWriterFactory.recycle(localXMLStreamWriter);
    }
    return getContentType(paramPacket);
  }
  
  protected abstract com.sun.xml.internal.ws.api.pipe.ContentType getContentType(Packet paramPacket);
  
  protected abstract String getDefaultContentType();
  
  public com.sun.xml.internal.ws.api.pipe.ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  protected abstract List<String> getExpectedContentTypes();
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException
  {
    decode(paramInputStream, paramString, paramPacket, new AttachmentSetImpl());
  }
  
  private static boolean isContentTypeSupported(String paramString, List<String> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.contains(str)) {
        return true;
      }
    }
    return false;
  }
  
  @NotNull
  public final Message decode(@NotNull XMLStreamReader paramXMLStreamReader)
  {
    return decode(paramXMLStreamReader, new AttachmentSetImpl());
  }
  
  public final Message decode(XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet)
  {
    return decode(soapVersion, paramXMLStreamReader, paramAttachmentSet);
  }
  
  public static final Message decode(SOAPVersion paramSOAPVersion, XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet)
  {
    if (paramXMLStreamReader.getEventType() != 1) {
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    }
    XMLStreamReaderUtil.verifyReaderState(paramXMLStreamReader, 1);
    if (("Envelope".equals(paramXMLStreamReader.getLocalName())) && (!nsUri.equals(paramXMLStreamReader.getNamespaceURI()))) {
      throw new VersionMismatchException(paramSOAPVersion, new Object[] { nsUri, paramXMLStreamReader.getNamespaceURI() });
    }
    XMLStreamReaderUtil.verifyTag(paramXMLStreamReader, nsUri, "Envelope");
    return new StreamMessage(paramSOAPVersion, paramXMLStreamReader, paramAttachmentSet);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket)
  {
    throw new UnsupportedOperationException();
  }
  
  public final StreamSOAPCodec copy()
  {
    return this;
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket, AttachmentSet paramAttachmentSet)
    throws IOException
  {
    List localList = getExpectedContentTypes();
    if ((paramString != null) && (!isContentTypeSupported(paramString, localList))) {
      throw new UnsupportedMediaException(paramString, localList);
    }
    com.oracle.webservices.internal.api.message.ContentType localContentType = paramPacket.getInternalContentType();
    ContentTypeImpl localContentTypeImpl = (localContentType != null) && ((localContentType instanceof ContentTypeImpl)) ? (ContentTypeImpl)localContentType : new ContentTypeImpl(paramString);
    String str = localContentTypeImpl.getCharSet();
    if ((str != null) && (!Charset.isSupported(str))) {
      throw new UnsupportedMediaException(str);
    }
    if (str != null) {
      invocationProperties.put("decodedMessageCharset", str);
    } else {
      invocationProperties.remove("decodedMessageCharset");
    }
    paramPacket.setMessage(streamDecoder.decode(paramInputStream, str, paramAttachmentSet, soapVersion));
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket, AttachmentSet paramAttachmentSet)
  {
    throw new UnsupportedOperationException();
  }
  
  public static StreamSOAPCodec create(SOAPVersion paramSOAPVersion)
  {
    if (paramSOAPVersion == null) {
      throw new IllegalArgumentException();
    }
    switch (paramSOAPVersion)
    {
    case SOAP_11: 
      return new StreamSOAP11Codec();
    case SOAP_12: 
      return new StreamSOAP12Codec();
    }
    throw new AssertionError();
  }
  
  public static StreamSOAPCodec create(WSFeatureList paramWSFeatureList)
  {
    SOAPVersion localSOAPVersion = WebServiceFeatureList.getSoapVersion(paramWSFeatureList);
    if (localSOAPVersion == null) {
      throw new IllegalArgumentException();
    }
    switch (localSOAPVersion)
    {
    case SOAP_11: 
      return new StreamSOAP11Codec(paramWSFeatureList);
    case SOAP_12: 
      return new StreamSOAP12Codec(paramWSFeatureList);
    }
    throw new AssertionError();
  }
  
  /**
   * @deprecated
   */
  public static StreamSOAPCodec create(WSBinding paramWSBinding)
  {
    SOAPVersion localSOAPVersion = paramWSBinding.getSOAPVersion();
    if (localSOAPVersion == null) {
      throw new IllegalArgumentException();
    }
    switch (localSOAPVersion)
    {
    case SOAP_11: 
      return new StreamSOAP11Codec(paramWSBinding);
    case SOAP_12: 
      return new StreamSOAP12Codec(paramWSBinding);
    }
    throw new AssertionError();
  }
  
  private String getPacketEncoding(Packet paramPacket)
  {
    if ((serializationFeature != null) && (serializationFeature.getEncoding() != null)) {
      return serializationFeature.getEncoding().equals("") ? "utf-8" : serializationFeature.getEncoding();
    }
    if ((paramPacket != null) && (endpoint != null))
    {
      String str = (String)invocationProperties.get("decodedMessageCharset");
      return str == null ? "utf-8" : str;
    }
    return "utf-8";
  }
  
  protected ContentTypeImpl.Builder getContenTypeBuilder(Packet paramPacket)
  {
    ContentTypeImpl.Builder localBuilder = new ContentTypeImpl.Builder();
    String str = getPacketEncoding(paramPacket);
    if ("utf-8".equalsIgnoreCase(str))
    {
      contentType = getDefaultContentType();
      charset = "utf-8";
      return localBuilder;
    }
    contentType = (getMimeType() + " ;charset=" + str);
    charset = str;
    return localBuilder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\StreamSOAPCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import com.sun.xml.internal.ws.util.xml.NamespaceContextExAdaper;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;

public class MtomCodec
  extends MimeCodec
{
  public static final String XOP_XML_MIME_TYPE = "application/xop+xml";
  public static final String XOP_LOCALNAME = "Include";
  public static final String XOP_NAMESPACEURI = "http://www.w3.org/2004/08/xop/include";
  private final StreamSOAPCodec codec;
  private final MTOMFeature mtomFeature;
  private final SerializationFeature sf;
  private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";
  
  MtomCodec(SOAPVersion paramSOAPVersion, StreamSOAPCodec paramStreamSOAPCodec, WSFeatureList paramWSFeatureList)
  {
    super(paramSOAPVersion, paramWSFeatureList);
    codec = paramStreamSOAPCodec;
    sf = ((SerializationFeature)paramWSFeatureList.get(SerializationFeature.class));
    MTOMFeature localMTOMFeature = (MTOMFeature)paramWSFeatureList.get(MTOMFeature.class);
    if (localMTOMFeature == null) {
      mtomFeature = new MTOMFeature();
    } else {
      mtomFeature = localMTOMFeature;
    }
  }
  
  public ContentType getStaticContentType(Packet paramPacket)
  {
    return getStaticContentTypeStatic(paramPacket, version);
  }
  
  public static ContentType getStaticContentTypeStatic(Packet paramPacket, SOAPVersion paramSOAPVersion)
  {
    ContentType localContentType = (ContentType)paramPacket.getInternalContentType();
    if (localContentType != null) {
      return localContentType;
    }
    String str1 = UUID.randomUUID().toString();
    String str2 = "uuid:" + str1;
    String str3 = "<rootpart*" + str1 + "@example.jaxws.sun.com>";
    String str4 = SOAPVersion.SOAP_11.equals(paramSOAPVersion) ? null : createActionParameter(paramPacket);
    String str5 = "boundary=\"" + str2 + "\"";
    String str6 = "multipart/related;start=\"" + str3 + "\";type=\"" + "application/xop+xml" + "\";" + str5 + ";start-info=\"" + contentType + (str4 == null ? "" : str4) + "\"";
    ContentTypeImpl localContentTypeImpl = SOAPVersion.SOAP_11.equals(paramSOAPVersion) ? new ContentTypeImpl(str6, soapAction == null ? "" : soapAction, null) : new ContentTypeImpl(str6, null, null);
    localContentTypeImpl.setBoundary(str2);
    localContentTypeImpl.setRootId(str3);
    paramPacket.setContentType(localContentTypeImpl);
    return localContentTypeImpl;
  }
  
  private static String createActionParameter(Packet paramPacket)
  {
    return soapAction != null ? ";action=\\\"" + soapAction + "\\\"" : "";
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException
  {
    ContentTypeImpl localContentTypeImpl = (ContentTypeImpl)getStaticContentType(paramPacket);
    String str1 = localContentTypeImpl.getBoundary();
    String str2 = localContentTypeImpl.getRootId();
    if (paramPacket.getMessage() != null) {
      try
      {
        String str3 = getPacketEncoding(paramPacket);
        invocationProperties.remove("decodedMessageCharset");
        String str4 = getActionParameter(paramPacket, version);
        String str5 = getSOAPXopContentType(str3, version, str4);
        writeln("--" + str1, paramOutputStream);
        writeMimeHeaders(str5, str2, paramOutputStream);
        ArrayList localArrayList = new ArrayList();
        MtomStreamWriterImpl localMtomStreamWriterImpl = new MtomStreamWriterImpl(XMLStreamWriterFactory.create(paramOutputStream, str3), localArrayList, str1, mtomFeature);
        paramPacket.getMessage().writeTo(localMtomStreamWriterImpl);
        XMLStreamWriterFactory.recycle(localMtomStreamWriterImpl);
        writeln(paramOutputStream);
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          ByteArrayBuffer localByteArrayBuffer = (ByteArrayBuffer)localIterator.next();
          localByteArrayBuffer.write(paramOutputStream);
        }
        writeNonMtomAttachments(paramPacket.getMessage().getAttachments(), paramOutputStream, str1);
        writeAsAscii("--" + str1, paramOutputStream);
        writeAsAscii("--", paramOutputStream);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
    }
    return localContentTypeImpl;
  }
  
  public static String getSOAPXopContentType(String paramString1, SOAPVersion paramSOAPVersion, String paramString2)
  {
    return "application/xop+xml;charset=" + paramString1 + ";type=\"" + contentType + paramString2 + "\"";
  }
  
  public static String getActionParameter(Packet paramPacket, SOAPVersion paramSOAPVersion)
  {
    return paramSOAPVersion == SOAPVersion.SOAP_11 ? "" : createActionParameter(paramPacket);
  }
  
  public static void writeMimeHeaders(String paramString1, String paramString2, OutputStream paramOutputStream)
    throws IOException
  {
    String str = paramString2;
    if ((str != null) && (str.length() > 0) && (str.charAt(0) != '<')) {
      str = '<' + str + '>';
    }
    writeln("Content-Id: " + str, paramOutputStream);
    writeln("Content-Type: " + paramString1, paramOutputStream);
    writeln("Content-Transfer-Encoding: binary", paramOutputStream);
    writeln(paramOutputStream);
  }
  
  private void writeNonMtomAttachments(AttachmentSet paramAttachmentSet, OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    Iterator localIterator = paramAttachmentSet.iterator();
    while (localIterator.hasNext())
    {
      Attachment localAttachment = (Attachment)localIterator.next();
      DataHandler localDataHandler = localAttachment.asDataHandler();
      if ((localDataHandler instanceof StreamingDataHandler))
      {
        StreamingDataHandler localStreamingDataHandler = (StreamingDataHandler)localDataHandler;
        if (localStreamingDataHandler.getHrefCid() != null) {}
      }
      else
      {
        writeln("--" + paramString, paramOutputStream);
        writeMimeHeaders(localAttachment.getContentType(), localAttachment.getContentId(), paramOutputStream);
        localAttachment.writeTo(paramOutputStream);
        writeln(paramOutputStream);
      }
    }
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  public MtomCodec copy()
  {
    return new MtomCodec(version, (StreamSOAPCodec)codec.copy(), features);
  }
  
  private static String encodeCid()
  {
    String str1 = "example.jaxws.sun.com";
    String str2 = UUID.randomUUID() + "@";
    return str2 + str1;
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket)
    throws IOException
  {
    String str1 = null;
    String str2 = paramMimeMultipartParser.getRootPart().getContentType();
    if (str2 != null) {
      str1 = new ContentTypeImpl(str2).getCharSet();
    }
    if ((str1 != null) && (!Charset.isSupported(str1))) {
      throw new UnsupportedMediaException(str1);
    }
    if (str1 != null) {
      invocationProperties.put("decodedMessageCharset", str1);
    } else {
      invocationProperties.remove("decodedMessageCharset");
    }
    MtomXMLStreamReaderEx localMtomXMLStreamReaderEx = new MtomXMLStreamReaderEx(paramMimeMultipartParser, XMLStreamReaderFactory.create(null, paramMimeMultipartParser.getRootPart().asInputStream(), str1, true));
    paramPacket.setMessage(codec.decode(localMtomXMLStreamReaderEx, new MimeAttachmentSet(paramMimeMultipartParser)));
    paramPacket.setMtomFeature(mtomFeature);
    paramPacket.setContentType(paramMimeMultipartParser.getContentType());
  }
  
  private String getPacketEncoding(Packet paramPacket)
  {
    if ((sf != null) && (sf.getEncoding() != null)) {
      return sf.getEncoding().equals("") ? "utf-8" : sf.getEncoding();
    }
    return determinePacketEncoding(paramPacket);
  }
  
  public static String determinePacketEncoding(Packet paramPacket)
  {
    if ((paramPacket != null) && (endpoint != null))
    {
      String str = (String)invocationProperties.get("decodedMessageCharset");
      return str == null ? "utf-8" : str;
    }
    return "utf-8";
  }
  
  public static class ByteArrayBuffer
  {
    final String contentId;
    private final DataHandler dh;
    private final String boundary;
    
    ByteArrayBuffer(@NotNull DataHandler paramDataHandler, String paramString)
    {
      dh = paramDataHandler;
      String str = null;
      if ((paramDataHandler instanceof StreamingDataHandler))
      {
        StreamingDataHandler localStreamingDataHandler = (StreamingDataHandler)paramDataHandler;
        if (localStreamingDataHandler.getHrefCid() != null) {
          str = localStreamingDataHandler.getHrefCid();
        }
      }
      contentId = (str != null ? str : MtomCodec.access$000());
      boundary = paramString;
    }
    
    public void write(OutputStream paramOutputStream)
      throws IOException
    {
      MimeCodec.writeln("--" + boundary, paramOutputStream);
      MtomCodec.writeMimeHeaders(dh.getContentType(), contentId, paramOutputStream);
      dh.writeTo(paramOutputStream);
      MimeCodec.writeln(paramOutputStream);
    }
  }
  
  public static class MtomStreamWriterImpl
    extends XMLStreamWriterFilter
    implements XMLStreamWriterEx, MtomStreamWriter, HasEncoding
  {
    private final List<MtomCodec.ByteArrayBuffer> mtomAttachments;
    private final String boundary;
    private final MTOMFeature myMtomFeature;
    
    public MtomStreamWriterImpl(XMLStreamWriter paramXMLStreamWriter, List<MtomCodec.ByteArrayBuffer> paramList, String paramString, MTOMFeature paramMTOMFeature)
    {
      super();
      mtomAttachments = paramList;
      boundary = paramString;
      myMtomFeature = paramMTOMFeature;
    }
    
    public void writeBinary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
      throws XMLStreamException
    {
      if (myMtomFeature.getThreshold() > paramInt2)
      {
        writeCharacters(DatatypeConverterImpl._printBase64Binary(paramArrayOfByte, paramInt1, paramInt2));
        return;
      }
      MtomCodec.ByteArrayBuffer localByteArrayBuffer = new MtomCodec.ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(paramArrayOfByte, paramInt1, paramInt2, paramString)), boundary);
      writeBinary(localByteArrayBuffer);
    }
    
    public void writeBinary(DataHandler paramDataHandler)
      throws XMLStreamException
    {
      writeBinary(new MtomCodec.ByteArrayBuffer(paramDataHandler, boundary));
    }
    
    public OutputStream writeBinary(String paramString)
      throws XMLStreamException
    {
      throw new UnsupportedOperationException();
    }
    
    public void writePCDATA(CharSequence paramCharSequence)
      throws XMLStreamException
    {
      if (paramCharSequence == null) {
        return;
      }
      if ((paramCharSequence instanceof Base64Data))
      {
        Base64Data localBase64Data = (Base64Data)paramCharSequence;
        writeBinary(localBase64Data.getDataHandler());
        return;
      }
      writeCharacters(paramCharSequence.toString());
    }
    
    private void writeBinary(MtomCodec.ByteArrayBuffer paramByteArrayBuffer)
    {
      try
      {
        mtomAttachments.add(paramByteArrayBuffer);
        String str = writer.getPrefix("http://www.w3.org/2004/08/xop/include");
        if ((str == null) || (!str.equals("xop")))
        {
          writer.setPrefix("xop", "http://www.w3.org/2004/08/xop/include");
          writer.writeNamespace("xop", "http://www.w3.org/2004/08/xop/include");
        }
        writer.writeStartElement("http://www.w3.org/2004/08/xop/include", "Include");
        writer.writeAttribute("href", "cid:" + contentId);
        writer.writeEndElement();
        writer.flush();
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(localXMLStreamException);
      }
    }
    
    public Object getProperty(String paramString)
      throws IllegalArgumentException
    {
      if ((paramString.equals("sjsxp-outputstream")) && ((writer instanceof Map)))
      {
        Object localObject = ((Map)writer).get("sjsxp-outputstream");
        if (localObject != null) {
          return localObject;
        }
      }
      return super.getProperty(paramString);
    }
    
    public AttachmentMarshaller getAttachmentMarshaller()
    {
      new AttachmentMarshaller()
      {
        public String addMtomAttachment(DataHandler paramAnonymousDataHandler, String paramAnonymousString1, String paramAnonymousString2)
        {
          MtomCodec.ByteArrayBuffer localByteArrayBuffer = new MtomCodec.ByteArrayBuffer(paramAnonymousDataHandler, boundary);
          mtomAttachments.add(localByteArrayBuffer);
          return "cid:" + contentId;
        }
        
        public String addMtomAttachment(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        {
          if (myMtomFeature.getThreshold() > paramAnonymousInt2) {
            return null;
          }
          MtomCodec.ByteArrayBuffer localByteArrayBuffer = new MtomCodec.ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString1)), boundary);
          mtomAttachments.add(localByteArrayBuffer);
          return "cid:" + contentId;
        }
        
        public String addSwaRefAttachment(DataHandler paramAnonymousDataHandler)
        {
          MtomCodec.ByteArrayBuffer localByteArrayBuffer = new MtomCodec.ByteArrayBuffer(paramAnonymousDataHandler, boundary);
          mtomAttachments.add(localByteArrayBuffer);
          return "cid:" + contentId;
        }
        
        public boolean isXOPPackage()
        {
          return true;
        }
      };
    }
    
    public List<MtomCodec.ByteArrayBuffer> getMtomAttachments()
    {
      return mtomAttachments;
    }
    
    public String getEncoding()
    {
      return XMLStreamWriterUtil.getEncoding(writer);
    }
    
    public NamespaceContextEx getNamespaceContext()
    {
      NamespaceContext localNamespaceContext = writer.getNamespaceContext();
      return new MtomNamespaceContextEx(localNamespaceContext);
    }
    
    private static class MtomNamespaceContextEx
      implements NamespaceContextEx
    {
      private final NamespaceContext nsContext;
      
      public MtomNamespaceContextEx(NamespaceContext paramNamespaceContext)
      {
        nsContext = paramNamespaceContext;
      }
      
      public Iterator<NamespaceContextEx.Binding> iterator()
      {
        throw new UnsupportedOperationException();
      }
      
      public String getNamespaceURI(String paramString)
      {
        return nsContext.getNamespaceURI(paramString);
      }
      
      public String getPrefix(String paramString)
      {
        return nsContext.getPrefix(paramString);
      }
      
      public Iterator getPrefixes(String paramString)
      {
        return nsContext.getPrefixes(paramString);
      }
    }
  }
  
  public static class MtomXMLStreamReaderEx
    extends XMLStreamReaderFilter
    implements XMLStreamReaderEx
  {
    private final MimeMultipartParser mimeMP;
    private boolean xopReferencePresent = false;
    private Base64Data base64AttData;
    private char[] base64EncodedText;
    private String xopHref;
    
    public MtomXMLStreamReaderEx(MimeMultipartParser paramMimeMultipartParser, XMLStreamReader paramXMLStreamReader)
    {
      super();
      mimeMP = paramMimeMultipartParser;
    }
    
    public CharSequence getPCDATA()
      throws XMLStreamException
    {
      if (xopReferencePresent) {
        return base64AttData;
      }
      return reader.getText();
    }
    
    public NamespaceContextEx getNamespaceContext()
    {
      return new NamespaceContextExAdaper(reader.getNamespaceContext());
    }
    
    public String getElementTextTrim()
      throws XMLStreamException
    {
      throw new UnsupportedOperationException();
    }
    
    public int getTextLength()
    {
      if (xopReferencePresent) {
        return base64AttData.length();
      }
      return reader.getTextLength();
    }
    
    public int getTextStart()
    {
      if (xopReferencePresent) {
        return 0;
      }
      return reader.getTextStart();
    }
    
    public int getEventType()
    {
      if (xopReferencePresent) {
        return 4;
      }
      return super.getEventType();
    }
    
    public int next()
      throws XMLStreamException
    {
      int i = reader.next();
      if ((i == 1) && (reader.getLocalName().equals("Include")) && (reader.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")))
      {
        String str = reader.getAttributeValue(null, "href");
        try
        {
          xopHref = str;
          Attachment localAttachment = getAttachment(str);
          if (localAttachment != null)
          {
            DataHandler localDataHandler = localAttachment.asDataHandler();
            if ((localDataHandler instanceof StreamingDataHandler)) {
              ((StreamingDataHandler)localDataHandler).setHrefCid(localAttachment.getContentId());
            }
            base64AttData = new Base64Data();
            base64AttData.set(localDataHandler);
          }
          xopReferencePresent = true;
        }
        catch (IOException localIOException)
        {
          throw new WebServiceException(localIOException);
        }
        XMLStreamReaderUtil.nextElementContent(reader);
        return 4;
      }
      if (xopReferencePresent)
      {
        xopReferencePresent = false;
        base64EncodedText = null;
        xopHref = null;
      }
      return i;
    }
    
    private String decodeCid(String paramString)
    {
      try
      {
        paramString = URLDecoder.decode(paramString, "utf-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
      return paramString;
    }
    
    private Attachment getAttachment(String paramString)
      throws IOException
    {
      if (paramString.startsWith("cid:")) {
        paramString = paramString.substring(4, paramString.length());
      }
      if (paramString.indexOf('%') != -1)
      {
        paramString = decodeCid(paramString);
        return mimeMP.getAttachmentPart(paramString);
      }
      return mimeMP.getAttachmentPart(paramString);
    }
    
    public char[] getTextCharacters()
    {
      if (xopReferencePresent)
      {
        char[] arrayOfChar = new char[base64AttData.length()];
        base64AttData.writeTo(arrayOfChar, 0);
        return arrayOfChar;
      }
      return reader.getTextCharacters();
    }
    
    public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
      throws XMLStreamException
    {
      if (xopReferencePresent)
      {
        if (paramArrayOfChar == null) {
          throw new NullPointerException("target char array can't be null");
        }
        if ((paramInt2 < 0) || (paramInt3 < 0) || (paramInt1 < 0) || (paramInt2 >= paramArrayOfChar.length) || (paramInt2 + paramInt3 > paramArrayOfChar.length)) {
          throw new IndexOutOfBoundsException();
        }
        int i = base64AttData.length();
        if (paramInt1 > i) {
          throw new IndexOutOfBoundsException();
        }
        if (base64EncodedText == null)
        {
          base64EncodedText = new char[base64AttData.length()];
          base64AttData.writeTo(base64EncodedText, 0);
        }
        int j = Math.min(i - paramInt1, paramInt3);
        System.arraycopy(base64EncodedText, paramInt1, paramArrayOfChar, paramInt2, j);
        return j;
      }
      return reader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
    }
    
    public String getText()
    {
      if (xopReferencePresent) {
        return base64AttData.toString();
      }
      return reader.getText();
    }
    
    protected boolean isXopReference()
      throws XMLStreamException
    {
      return xopReferencePresent;
    }
    
    protected String getXopHref()
    {
      return xopHref;
    }
    
    public MimeMultipartParser getMimeMultipartParser()
    {
      return mimeMP;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\MtomCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
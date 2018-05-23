package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.xml.internal.ws.util.xml.StAXSource;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite.ElemInfo;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.NamespaceSupport;

public class StreamMessage
  extends AbstractMessageImpl
  implements StreamingSOAP
{
  @NotNull
  private XMLStreamReader reader;
  @Nullable
  private MessageHeaders headers;
  private String bodyPrologue = null;
  private String bodyEpilogue = null;
  private String payloadLocalName;
  private String payloadNamespaceURI;
  private Throwable consumedAt;
  private XMLStreamReader envelopeReader;
  private static final String SOAP_ENVELOPE = "Envelope";
  private static final String SOAP_HEADER = "Header";
  private static final String SOAP_BODY = "Body";
  static final StreamHeaderDecoder SOAP12StreamHeaderDecoder = new StreamHeaderDecoder()
  {
    public Header decodeHeader(XMLStreamReader paramAnonymousXMLStreamReader, XMLStreamBuffer paramAnonymousXMLStreamBuffer)
    {
      return new StreamHeader12(paramAnonymousXMLStreamReader, paramAnonymousXMLStreamBuffer);
    }
  };
  static final StreamHeaderDecoder SOAP11StreamHeaderDecoder = new StreamHeaderDecoder()
  {
    public Header decodeHeader(XMLStreamReader paramAnonymousXMLStreamReader, XMLStreamBuffer paramAnonymousXMLStreamBuffer)
    {
      return new StreamHeader11(paramAnonymousXMLStreamReader, paramAnonymousXMLStreamBuffer);
    }
  };
  
  public StreamMessage(SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    payloadLocalName = null;
    payloadNamespaceURI = null;
  }
  
  public StreamMessage(SOAPVersion paramSOAPVersion, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet)
  {
    super(paramSOAPVersion);
    envelopeReader = paramXMLStreamReader;
    attachmentSet = paramAttachmentSet;
  }
  
  public XMLStreamReader readEnvelope()
  {
    if (envelopeReader == null)
    {
      ArrayList localArrayList = new ArrayList();
      XMLReaderComposite.ElemInfo localElemInfo1 = new XMLReaderComposite.ElemInfo(envelopeTag, null);
      XMLReaderComposite.ElemInfo localElemInfo2 = headerTag != null ? new XMLReaderComposite.ElemInfo(headerTag, localElemInfo1) : null;
      XMLReaderComposite.ElemInfo localElemInfo3 = new XMLReaderComposite.ElemInfo(bodyTag, localElemInfo1);
      Iterator localIterator = getHeaders().asList().iterator();
      while (localIterator.hasNext())
      {
        localObject = (Header)localIterator.next();
        try
        {
          localArrayList.add(((Header)localObject).readHeader());
        }
        catch (XMLStreamException localXMLStreamException)
        {
          throw new RuntimeException(localXMLStreamException);
        }
      }
      localIterator = localElemInfo2 != null ? new XMLReaderComposite(localElemInfo2, (XMLStreamReader[])localArrayList.toArray(new XMLStreamReader[localArrayList.size()])) : null;
      Object localObject = { readPayload() };
      XMLReaderComposite localXMLReaderComposite = new XMLReaderComposite(localElemInfo3, (XMLStreamReader[])localObject);
      XMLStreamReader[] arrayOfXMLStreamReader = { localIterator != null ? new XMLStreamReader[] { localIterator, localXMLReaderComposite } : localXMLReaderComposite };
      return new XMLReaderComposite(localElemInfo1, arrayOfXMLStreamReader);
    }
    return envelopeReader;
  }
  
  public StreamMessage(@Nullable MessageHeaders paramMessageHeaders, @NotNull AttachmentSet paramAttachmentSet, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    init(paramMessageHeaders, paramAttachmentSet, paramXMLStreamReader, paramSOAPVersion);
  }
  
  private void init(@Nullable MessageHeaders paramMessageHeaders, @NotNull AttachmentSet paramAttachmentSet, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion)
  {
    headers = paramMessageHeaders;
    attachmentSet = paramAttachmentSet;
    reader = paramXMLStreamReader;
    if (paramXMLStreamReader.getEventType() == 7) {
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    }
    if (paramXMLStreamReader.getEventType() == 2)
    {
      String str1 = paramXMLStreamReader.getLocalName();
      String str2 = paramXMLStreamReader.getNamespaceURI();
      assert (str1 != null);
      assert (str2 != null);
      if ((str1.equals("Body")) && (str2.equals(nsUri)))
      {
        payloadLocalName = null;
        payloadNamespaceURI = null;
      }
      else
      {
        throw new WebServiceException("Malformed stream: {" + str2 + "}" + str1);
      }
    }
    else
    {
      payloadLocalName = paramXMLStreamReader.getLocalName();
      payloadNamespaceURI = paramXMLStreamReader.getNamespaceURI();
    }
    int i = paramSOAPVersion.ordinal() * 3;
    envelopeTag = ((TagInfoset)DEFAULT_TAGS.get(i));
    headerTag = ((TagInfoset)DEFAULT_TAGS.get(i + 1));
    bodyTag = ((TagInfoset)DEFAULT_TAGS.get(i + 2));
  }
  
  public StreamMessage(@NotNull TagInfoset paramTagInfoset1, @Nullable TagInfoset paramTagInfoset2, @NotNull AttachmentSet paramAttachmentSet, @Nullable MessageHeaders paramMessageHeaders, @NotNull TagInfoset paramTagInfoset3, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion)
  {
    this(paramTagInfoset1, paramTagInfoset2, paramAttachmentSet, paramMessageHeaders, null, paramTagInfoset3, null, paramXMLStreamReader, paramSOAPVersion);
  }
  
  public StreamMessage(@NotNull TagInfoset paramTagInfoset1, @Nullable TagInfoset paramTagInfoset2, @NotNull AttachmentSet paramAttachmentSet, @Nullable MessageHeaders paramMessageHeaders, @Nullable String paramString1, @NotNull TagInfoset paramTagInfoset3, @Nullable String paramString2, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    init(paramTagInfoset1, paramTagInfoset2, paramAttachmentSet, paramMessageHeaders, paramString1, paramTagInfoset3, paramString2, paramXMLStreamReader, paramSOAPVersion);
  }
  
  private void init(@NotNull TagInfoset paramTagInfoset1, @Nullable TagInfoset paramTagInfoset2, @NotNull AttachmentSet paramAttachmentSet, @Nullable MessageHeaders paramMessageHeaders, @Nullable String paramString1, @NotNull TagInfoset paramTagInfoset3, @Nullable String paramString2, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion)
  {
    init(paramMessageHeaders, paramAttachmentSet, paramXMLStreamReader, paramSOAPVersion);
    if (paramTagInfoset1 == null) {
      throw new IllegalArgumentException("EnvelopeTag TagInfoset cannot be null");
    }
    if (paramTagInfoset3 == null) {
      throw new IllegalArgumentException("BodyTag TagInfoset cannot be null");
    }
    envelopeTag = paramTagInfoset1;
    headerTag = paramTagInfoset2;
    bodyTag = paramTagInfoset3;
    bodyPrologue = paramString1;
    bodyEpilogue = paramString2;
  }
  
  public boolean hasHeaders()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    return (headers != null) && (headers.hasHeaders());
  }
  
  public MessageHeaders getHeaders()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    if (headers == null) {
      headers = new HeaderList(getSOAPVersion());
    }
    return headers;
  }
  
  public String getPayloadLocalPart()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    return payloadLocalName;
  }
  
  public String getPayloadNamespaceURI()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    return payloadNamespaceURI;
  }
  
  public boolean hasPayload()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    return payloadLocalName != null;
  }
  
  public Source readPayloadAsSource()
  {
    if (hasPayload())
    {
      assert (unconsumed());
      return new StAXSource(reader, true, getInscopeNamespaces());
    }
    return null;
  }
  
  private String[] getInscopeNamespaces()
  {
    NamespaceSupport localNamespaceSupport = new NamespaceSupport();
    localNamespaceSupport.pushContext();
    for (int i = 0; i < envelopeTag.ns.length; i += 2) {
      localNamespaceSupport.declarePrefix(envelopeTag.ns[i], envelopeTag.ns[(i + 1)]);
    }
    localNamespaceSupport.pushContext();
    for (i = 0; i < bodyTag.ns.length; i += 2) {
      localNamespaceSupport.declarePrefix(bodyTag.ns[i], bodyTag.ns[(i + 1)]);
    }
    ArrayList localArrayList = new ArrayList();
    Enumeration localEnumeration = localNamespaceSupport.getPrefixes();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      localArrayList.add(str);
      localArrayList.add(localNamespaceSupport.getURI(str));
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public Object readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    if (!hasPayload()) {
      return null;
    }
    assert (unconsumed());
    if (hasAttachments()) {
      paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments()));
    }
    try
    {
      Object localObject1 = paramUnmarshaller.unmarshal(reader);
      return localObject1;
    }
    finally
    {
      paramUnmarshaller.setAttachmentUnmarshaller(null);
      XMLStreamReaderUtil.readRest(reader);
      XMLStreamReaderUtil.close(reader);
      XMLStreamReaderFactory.recycle(reader);
    }
  }
  
  /**
   * @deprecated
   */
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    if (!hasPayload()) {
      return null;
    }
    assert (unconsumed());
    Object localObject = paramBridge.unmarshal(reader, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    XMLStreamReaderUtil.readRest(reader);
    XMLStreamReaderUtil.close(reader);
    XMLStreamReaderFactory.recycle(reader);
    return (T)localObject;
  }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException
  {
    if (!hasPayload()) {
      return null;
    }
    assert (unconsumed());
    Object localObject = paramXMLBridge.unmarshal(reader, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    XMLStreamReaderUtil.readRest(reader);
    XMLStreamReaderUtil.close(reader);
    XMLStreamReaderFactory.recycle(reader);
    return (T)localObject;
  }
  
  public void consume()
  {
    assert (unconsumed());
    XMLStreamReaderUtil.readRest(reader);
    XMLStreamReaderUtil.close(reader);
    XMLStreamReaderFactory.recycle(reader);
  }
  
  public XMLStreamReader readPayload()
  {
    if (!hasPayload()) {
      return null;
    }
    assert (unconsumed());
    return reader;
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    assert (unconsumed());
    if (payloadLocalName == null) {
      return;
    }
    if (bodyPrologue != null) {
      paramXMLStreamWriter.writeCharacters(bodyPrologue);
    }
    XMLStreamReaderToXMLStreamWriter localXMLStreamReaderToXMLStreamWriter = new XMLStreamReaderToXMLStreamWriter();
    while (reader.getEventType() != 8)
    {
      String str1 = reader.getLocalName();
      String str2 = reader.getNamespaceURI();
      if (reader.getEventType() == 2)
      {
        if (isBodyElement(str1, str2)) {
          break;
        }
        String str3 = XMLStreamReaderUtil.nextWhiteSpaceContent(reader);
        if (str3 != null)
        {
          bodyEpilogue = str3;
          paramXMLStreamWriter.writeCharacters(str3);
        }
      }
      else
      {
        localXMLStreamReaderToXMLStreamWriter.bridge(reader, paramXMLStreamWriter);
      }
    }
    XMLStreamReaderUtil.readRest(reader);
    XMLStreamReaderUtil.close(reader);
    XMLStreamReaderFactory.recycle(reader);
  }
  
  private boolean isBodyElement(String paramString1, String paramString2)
  {
    return (paramString1.equals("Body")) && (paramString2.equals(soapVersion.nsUri));
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    writeEnvelope(paramXMLStreamWriter);
  }
  
  private void writeEnvelope(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    paramXMLStreamWriter.writeStartDocument();
    envelopeTag.writeStart(paramXMLStreamWriter);
    MessageHeaders localMessageHeaders = getHeaders();
    if ((localMessageHeaders.hasHeaders()) && (headerTag == null)) {
      headerTag = new TagInfoset(envelopeTag.nsUri, "Header", envelopeTag.prefix, EMPTY_ATTS, new String[0]);
    }
    if (headerTag != null)
    {
      headerTag.writeStart(paramXMLStreamWriter);
      if (localMessageHeaders.hasHeaders())
      {
        Iterator localIterator = localMessageHeaders.asList().iterator();
        while (localIterator.hasNext())
        {
          Header localHeader = (Header)localIterator.next();
          localHeader.writeTo(paramXMLStreamWriter);
        }
      }
      paramXMLStreamWriter.writeEndElement();
    }
    bodyTag.writeStart(paramXMLStreamWriter);
    if (hasPayload()) {
      writePayloadTo(paramXMLStreamWriter);
    }
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndDocument();
  }
  
  public void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    assert (unconsumed());
    try
    {
      if (payloadLocalName == null) {
        return;
      }
      if (bodyPrologue != null)
      {
        localObject1 = bodyPrologue.toCharArray();
        paramContentHandler.characters((char[])localObject1, 0, localObject1.length);
      }
      Object localObject1 = new XMLStreamReaderToContentHandler(reader, paramContentHandler, true, paramBoolean, getInscopeNamespaces());
      while (reader.getEventType() != 8)
      {
        localObject2 = reader.getLocalName();
        localObject3 = reader.getNamespaceURI();
        if (reader.getEventType() == 2)
        {
          if (isBodyElement((String)localObject2, (String)localObject3)) {
            break;
          }
          String str = XMLStreamReaderUtil.nextWhiteSpaceContent(reader);
          if (str != null)
          {
            bodyEpilogue = str;
            char[] arrayOfChar = str.toCharArray();
            paramContentHandler.characters(arrayOfChar, 0, arrayOfChar.length);
          }
        }
        else
        {
          ((XMLStreamReaderToContentHandler)localObject1).bridge();
        }
      }
      XMLStreamReaderUtil.readRest(reader);
      XMLStreamReaderUtil.close(reader);
      XMLStreamReaderFactory.recycle(reader);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      Object localObject2 = localXMLStreamException.getLocation();
      if (localObject2 == null) {
        localObject2 = DummyLocation.INSTANCE;
      }
      Object localObject3 = new SAXParseException(localXMLStreamException.getMessage(), ((Location)localObject2).getPublicId(), ((Location)localObject2).getSystemId(), ((Location)localObject2).getLineNumber(), ((Location)localObject2).getColumnNumber(), localXMLStreamException);
      paramErrorHandler.error((SAXParseException)localObject3);
    }
  }
  
  public Message copy()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    try
    {
      assert (unconsumed());
      consumedAt = null;
      MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      StreamReaderBufferCreator localStreamReaderBufferCreator = new StreamReaderBufferCreator(localMutableXMLStreamBuffer);
      localStreamReaderBufferCreator.storeElement(envelopeTag.nsUri, envelopeTag.localName, envelopeTag.prefix, envelopeTag.ns);
      localStreamReaderBufferCreator.storeElement(bodyTag.nsUri, bodyTag.localName, bodyTag.prefix, bodyTag.ns);
      if (hasPayload()) {
        while (reader.getEventType() != 8)
        {
          localObject = reader.getLocalName();
          String str = reader.getNamespaceURI();
          if ((isBodyElement((String)localObject, str)) || (reader.getEventType() == 8)) {
            break;
          }
          localStreamReaderBufferCreator.create(reader);
          if (reader.isWhiteSpace()) {
            bodyEpilogue = XMLStreamReaderUtil.currentWhiteSpaceContent(reader);
          } else {
            bodyEpilogue = null;
          }
        }
      }
      localStreamReaderBufferCreator.storeEndElement();
      localStreamReaderBufferCreator.storeEndElement();
      localStreamReaderBufferCreator.storeEndElement();
      XMLStreamReaderUtil.readRest(reader);
      XMLStreamReaderUtil.close(reader);
      XMLStreamReaderFactory.recycle(reader);
      reader = localMutableXMLStreamBuffer.readAsXMLStreamReader();
      Object localObject = localMutableXMLStreamBuffer.readAsXMLStreamReader();
      proceedToRootElement(reader);
      proceedToRootElement((XMLStreamReader)localObject);
      return new StreamMessage(envelopeTag, headerTag, attachmentSet, HeaderList.copy(headers), bodyPrologue, bodyTag, bodyEpilogue, (XMLStreamReader)localObject, soapVersion);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException("Failed to copy a message", localXMLStreamException);
    }
  }
  
  private void proceedToRootElement(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    assert (paramXMLStreamReader.getEventType() == 7);
    paramXMLStreamReader.nextTag();
    paramXMLStreamReader.nextTag();
    paramXMLStreamReader.nextTag();
    assert ((paramXMLStreamReader.getEventType() == 1) || (paramXMLStreamReader.getEventType() == 2));
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    paramContentHandler.setDocumentLocator(NULL_LOCATOR);
    paramContentHandler.startDocument();
    envelopeTag.writeStart(paramContentHandler);
    if ((hasHeaders()) && (headerTag == null)) {
      headerTag = new TagInfoset(envelopeTag.nsUri, "Header", envelopeTag.prefix, EMPTY_ATTS, new String[0]);
    }
    if (headerTag != null)
    {
      headerTag.writeStart(paramContentHandler);
      if (hasHeaders())
      {
        MessageHeaders localMessageHeaders = getHeaders();
        Iterator localIterator = localMessageHeaders.asList().iterator();
        while (localIterator.hasNext())
        {
          Header localHeader = (Header)localIterator.next();
          localHeader.writeTo(paramContentHandler, paramErrorHandler);
        }
      }
      headerTag.writeEnd(paramContentHandler);
    }
    bodyTag.writeStart(paramContentHandler);
    writePayloadTo(paramContentHandler, paramErrorHandler, true);
    bodyTag.writeEnd(paramContentHandler);
    envelopeTag.writeEnd(paramContentHandler);
    paramContentHandler.endDocument();
  }
  
  private boolean unconsumed()
  {
    if (payloadLocalName == null) {
      return true;
    }
    if (reader.getEventType() != 1)
    {
      AssertionError localAssertionError = new AssertionError("StreamMessage has been already consumed. See the nested exception for where it's consumed");
      localAssertionError.initCause(consumedAt);
      throw localAssertionError;
    }
    consumedAt = new Exception().fillInStackTrace();
    return true;
  }
  
  public String getBodyPrologue()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    return bodyPrologue;
  }
  
  public String getBodyEpilogue()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    return bodyEpilogue;
  }
  
  public XMLStreamReader getReader()
  {
    if (envelopeReader != null) {
      readEnvelope(this);
    }
    assert (unconsumed());
    return reader;
  }
  
  private static void readEnvelope(StreamMessage paramStreamMessage)
  {
    if (envelopeReader == null) {
      return;
    }
    XMLStreamReader localXMLStreamReader = envelopeReader;
    envelopeReader = null;
    SOAPVersion localSOAPVersion = soapVersion;
    if (localXMLStreamReader.getEventType() != 1) {
      XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
    }
    XMLStreamReaderUtil.verifyReaderState(localXMLStreamReader, 1);
    if (("Envelope".equals(localXMLStreamReader.getLocalName())) && (!nsUri.equals(localXMLStreamReader.getNamespaceURI()))) {
      throw new VersionMismatchException(localSOAPVersion, new Object[] { nsUri, localXMLStreamReader.getNamespaceURI() });
    }
    XMLStreamReaderUtil.verifyTag(localXMLStreamReader, nsUri, "Envelope");
    TagInfoset localTagInfoset1 = new TagInfoset(localXMLStreamReader);
    HashMap localHashMap = new HashMap();
    for (int i = 0; i < localXMLStreamReader.getNamespaceCount(); i++) {
      localHashMap.put(localXMLStreamReader.getNamespacePrefix(i), localXMLStreamReader.getNamespaceURI(i));
    }
    XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
    XMLStreamReaderUtil.verifyReaderState(localXMLStreamReader, 1);
    HeaderList localHeaderList = null;
    TagInfoset localTagInfoset2 = null;
    if ((localXMLStreamReader.getLocalName().equals("Header")) && (localXMLStreamReader.getNamespaceURI().equals(nsUri)))
    {
      localTagInfoset2 = new TagInfoset(localXMLStreamReader);
      for (int j = 0; j < localXMLStreamReader.getNamespaceCount(); j++) {
        localHashMap.put(localXMLStreamReader.getNamespacePrefix(j), localXMLStreamReader.getNamespaceURI(j));
      }
      XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
      if (localXMLStreamReader.getEventType() == 1)
      {
        localHeaderList = new HeaderList(localSOAPVersion);
        try
        {
          StreamHeaderDecoder localStreamHeaderDecoder = SOAPVersion.SOAP_11.equals(localSOAPVersion) ? SOAP11StreamHeaderDecoder : SOAP12StreamHeaderDecoder;
          cacheHeaders(localXMLStreamReader, localHashMap, localHeaderList, localStreamHeaderDecoder);
        }
        catch (XMLStreamException localXMLStreamException)
        {
          throw new WebServiceException(localXMLStreamException);
        }
      }
      XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
    }
    XMLStreamReaderUtil.verifyTag(localXMLStreamReader, nsUri, "Body");
    TagInfoset localTagInfoset3 = new TagInfoset(localXMLStreamReader);
    String str = XMLStreamReaderUtil.nextWhiteSpaceContent(localXMLStreamReader);
    paramStreamMessage.init(localTagInfoset1, localTagInfoset2, attachmentSet, localHeaderList, str, localTagInfoset3, null, localXMLStreamReader, localSOAPVersion);
  }
  
  private static XMLStreamBuffer cacheHeaders(XMLStreamReader paramXMLStreamReader, Map<String, String> paramMap, HeaderList paramHeaderList, StreamHeaderDecoder paramStreamHeaderDecoder)
    throws XMLStreamException
  {
    MutableXMLStreamBuffer localMutableXMLStreamBuffer = createXMLStreamBuffer();
    StreamReaderBufferCreator localStreamReaderBufferCreator = new StreamReaderBufferCreator();
    localStreamReaderBufferCreator.setXMLStreamBuffer(localMutableXMLStreamBuffer);
    while (paramXMLStreamReader.getEventType() == 1)
    {
      Object localObject = paramMap;
      if (paramXMLStreamReader.getNamespaceCount() > 0)
      {
        localObject = new HashMap(paramMap);
        for (int i = 0; i < paramXMLStreamReader.getNamespaceCount(); i++) {
          ((Map)localObject).put(paramXMLStreamReader.getNamespacePrefix(i), paramXMLStreamReader.getNamespaceURI(i));
        }
      }
      XMLStreamBufferMark localXMLStreamBufferMark = new XMLStreamBufferMark((Map)localObject, localStreamReaderBufferCreator);
      paramHeaderList.add(paramStreamHeaderDecoder.decodeHeader(paramXMLStreamReader, localXMLStreamBufferMark));
      localStreamReaderBufferCreator.createElementFragment(paramXMLStreamReader, false);
      if ((paramXMLStreamReader.getEventType() != 1) && (paramXMLStreamReader.getEventType() != 2)) {
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
    }
    return localMutableXMLStreamBuffer;
  }
  
  private static MutableXMLStreamBuffer createXMLStreamBuffer()
  {
    return new MutableXMLStreamBuffer();
  }
  
  protected static abstract interface StreamHeaderDecoder
  {
    public abstract Header decodeHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\stream\StreamMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
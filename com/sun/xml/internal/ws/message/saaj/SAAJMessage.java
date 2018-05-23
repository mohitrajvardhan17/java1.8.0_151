package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.AttachmentEx.MimeHeader;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.ASCIIUtility;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public class SAAJMessage
  extends Message
{
  private boolean parsedMessage;
  private boolean accessedMessage;
  private final SOAPMessage sm;
  private MessageHeaders headers;
  private List<Element> bodyParts;
  private Element payload;
  private String payloadLocalName;
  private String payloadNamespace;
  private SOAPVersion soapVersion;
  private NamedNodeMap bodyAttrs;
  private NamedNodeMap headerAttrs;
  private NamedNodeMap envelopeAttrs;
  private static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
  private static final LocatorImpl NULL_LOCATOR = new LocatorImpl();
  private XMLStreamReader soapBodyFirstChildReader;
  private SOAPElement soapBodyFirstChild;
  
  public SAAJMessage(SOAPMessage paramSOAPMessage)
  {
    sm = paramSOAPMessage;
  }
  
  private SAAJMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, SOAPMessage paramSOAPMessage, SOAPVersion paramSOAPVersion)
  {
    sm = paramSOAPMessage;
    parse();
    if (paramMessageHeaders == null) {
      paramMessageHeaders = new HeaderList(paramSOAPVersion);
    }
    headers = paramMessageHeaders;
    attachmentSet = paramAttachmentSet;
  }
  
  private void parse()
  {
    if (!parsedMessage) {
      try
      {
        access();
        if (headers == null) {
          headers = new HeaderList(getSOAPVersion());
        }
        SOAPHeader localSOAPHeader = sm.getSOAPHeader();
        if (localSOAPHeader != null)
        {
          headerAttrs = localSOAPHeader.getAttributes();
          Iterator localIterator = localSOAPHeader.examineAllHeaderElements();
          while (localIterator.hasNext()) {
            headers.add(new SAAJHeader((SOAPHeaderElement)localIterator.next()));
          }
        }
        attachmentSet = new SAAJAttachmentSet(sm);
        parsedMessage = true;
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
  }
  
  protected void access()
  {
    if (!accessedMessage) {
      try
      {
        envelopeAttrs = sm.getSOAPPart().getEnvelope().getAttributes();
        SOAPBody localSOAPBody = sm.getSOAPBody();
        bodyAttrs = localSOAPBody.getAttributes();
        soapVersion = SOAPVersion.fromNsUri(localSOAPBody.getNamespaceURI());
        bodyParts = DOMUtil.getChildElements(localSOAPBody);
        payload = (bodyParts.size() > 0 ? (Element)bodyParts.get(0) : null);
        if (payload != null)
        {
          payloadLocalName = payload.getLocalName();
          payloadNamespace = payload.getNamespaceURI();
        }
        accessedMessage = true;
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
  }
  
  public boolean hasHeaders()
  {
    parse();
    return headers.hasHeaders();
  }
  
  @NotNull
  public MessageHeaders getHeaders()
  {
    parse();
    return headers;
  }
  
  @NotNull
  public AttachmentSet getAttachments()
  {
    if (attachmentSet == null) {
      attachmentSet = new SAAJAttachmentSet(sm);
    }
    return attachmentSet;
  }
  
  protected boolean hasAttachments()
  {
    return !getAttachments().isEmpty();
  }
  
  @Nullable
  public String getPayloadLocalPart()
  {
    soapBodyFirstChild();
    return payloadLocalName;
  }
  
  public String getPayloadNamespaceURI()
  {
    soapBodyFirstChild();
    return payloadNamespace;
  }
  
  public boolean hasPayload()
  {
    return soapBodyFirstChild() != null;
  }
  
  private void addAttributes(Element paramElement, NamedNodeMap paramNamedNodeMap)
  {
    if (paramNamedNodeMap == null) {
      return;
    }
    String str = paramElement.getPrefix();
    for (int i = 0; i < paramNamedNodeMap.getLength(); i++)
    {
      Attr localAttr = (Attr)paramNamedNodeMap.item(i);
      if (("xmlns".equals(localAttr.getPrefix())) || ("xmlns".equals(localAttr.getLocalName())))
      {
        if (((str != null) || (!localAttr.getLocalName().equals("xmlns"))) && ((str == null) || (!"xmlns".equals(localAttr.getPrefix())) || (!str.equals(localAttr.getLocalName())))) {
          paramElement.setAttributeNS(localAttr.getNamespaceURI(), localAttr.getName(), localAttr.getValue());
        }
      }
      else {
        paramElement.setAttributeNS(localAttr.getNamespaceURI(), localAttr.getName(), localAttr.getValue());
      }
    }
  }
  
  public Source readEnvelopeAsSource()
  {
    try
    {
      if (!parsedMessage)
      {
        localObject1 = sm.getSOAPPart().getEnvelope();
        return new DOMSource((Node)localObject1);
      }
      Object localObject1 = soapVersion.getMessageFactory().createMessage();
      addAttributes(((SOAPMessage)localObject1).getSOAPPart().getEnvelope(), envelopeAttrs);
      SOAPBody localSOAPBody = ((SOAPMessage)localObject1).getSOAPPart().getEnvelope().getBody();
      addAttributes(localSOAPBody, bodyAttrs);
      Object localObject2 = bodyParts.iterator();
      Object localObject3;
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Element)((Iterator)localObject2).next();
        Node localNode = localSOAPBody.getOwnerDocument().importNode((Node)localObject3, true);
        localSOAPBody.appendChild(localNode);
      }
      addAttributes(((SOAPMessage)localObject1).getSOAPHeader(), headerAttrs);
      localObject2 = headers.asList().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Header)((Iterator)localObject2).next();
        ((Header)localObject3).writeTo((SOAPMessage)localObject1);
      }
      localObject2 = ((SOAPMessage)localObject1).getSOAPPart().getEnvelope();
      return new DOMSource((Node)localObject2);
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  public SOAPMessage readAsSOAPMessage()
    throws SOAPException
  {
    if (!parsedMessage) {
      return sm;
    }
    SOAPMessage localSOAPMessage = soapVersion.getMessageFactory().createMessage();
    addAttributes(localSOAPMessage.getSOAPPart().getEnvelope(), envelopeAttrs);
    SOAPBody localSOAPBody = localSOAPMessage.getSOAPPart().getEnvelope().getBody();
    addAttributes(localSOAPBody, bodyAttrs);
    Iterator localIterator = bodyParts.iterator();
    Object localObject1;
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (Element)localIterator.next();
      localObject2 = localSOAPBody.getOwnerDocument().importNode((Node)localObject1, true);
      localSOAPBody.appendChild((Node)localObject2);
    }
    addAttributes(localSOAPMessage.getSOAPHeader(), headerAttrs);
    localIterator = headers.asList().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Header)localIterator.next();
      ((Header)localObject1).writeTo(localSOAPMessage);
    }
    localIterator = getAttachments().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Attachment)localIterator.next();
      localObject2 = localSOAPMessage.createAttachmentPart();
      ((AttachmentPart)localObject2).setDataHandler(((Attachment)localObject1).asDataHandler());
      ((AttachmentPart)localObject2).setContentId('<' + ((Attachment)localObject1).getContentId() + '>');
      addCustomMimeHeaders((Attachment)localObject1, (AttachmentPart)localObject2);
      localSOAPMessage.addAttachmentPart((AttachmentPart)localObject2);
    }
    localSOAPMessage.saveChanges();
    return localSOAPMessage;
  }
  
  private void addCustomMimeHeaders(Attachment paramAttachment, AttachmentPart paramAttachmentPart)
  {
    if ((paramAttachment instanceof AttachmentEx))
    {
      Iterator localIterator = ((AttachmentEx)paramAttachment).getMimeHeaders();
      while (localIterator.hasNext())
      {
        AttachmentEx.MimeHeader localMimeHeader = (AttachmentEx.MimeHeader)localIterator.next();
        String str = localMimeHeader.getName();
        if ((!"Content-Type".equalsIgnoreCase(str)) && (!"Content-Id".equalsIgnoreCase(str))) {
          paramAttachmentPart.addMimeHeader(str, localMimeHeader.getValue());
        }
      }
    }
  }
  
  public Source readPayloadAsSource()
  {
    access();
    return payload != null ? new DOMSource(payload) : null;
  }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    access();
    if (payload != null)
    {
      if (hasAttachments()) {
        paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments()));
      }
      return (T)paramUnmarshaller.unmarshal(payload);
    }
    return null;
  }
  
  /**
   * @deprecated
   */
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    access();
    if (payload != null) {
      return (T)paramBridge.unmarshal(payload, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    }
    return null;
  }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException
  {
    access();
    if (payload != null) {
      return (T)paramXMLBridge.unmarshal(payload, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    }
    return null;
  }
  
  public XMLStreamReader readPayload()
    throws XMLStreamException
  {
    return soapBodyFirstChildReader();
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    access();
    try
    {
      Iterator localIterator = bodyParts.iterator();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        DOMUtil.serializeNode(localElement, paramXMLStreamWriter);
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    try
    {
      paramXMLStreamWriter.writeStartDocument();
      if (!parsedMessage)
      {
        DOMUtil.serializeNode(sm.getSOAPPart().getEnvelope(), paramXMLStreamWriter);
      }
      else
      {
        SOAPEnvelope localSOAPEnvelope = sm.getSOAPPart().getEnvelope();
        DOMUtil.writeTagWithAttributes(localSOAPEnvelope, paramXMLStreamWriter);
        if (hasHeaders())
        {
          if (localSOAPEnvelope.getHeader() != null) {
            DOMUtil.writeTagWithAttributes(localSOAPEnvelope.getHeader(), paramXMLStreamWriter);
          } else {
            paramXMLStreamWriter.writeStartElement(localSOAPEnvelope.getPrefix(), "Header", localSOAPEnvelope.getNamespaceURI());
          }
          Iterator localIterator = headers.asList().iterator();
          while (localIterator.hasNext())
          {
            Header localHeader = (Header)localIterator.next();
            localHeader.writeTo(paramXMLStreamWriter);
          }
          paramXMLStreamWriter.writeEndElement();
        }
        DOMUtil.serializeNode(sm.getSOAPBody(), paramXMLStreamWriter);
        paramXMLStreamWriter.writeEndElement();
      }
      paramXMLStreamWriter.writeEndDocument();
      paramXMLStreamWriter.flush();
    }
    catch (SOAPException localSOAPException)
    {
      throw new XMLStreamException2(localSOAPException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    String str = soapVersion.nsUri;
    Object localObject;
    if (!parsedMessage)
    {
      localObject = new DOMScanner();
      ((DOMScanner)localObject).setContentHandler(paramContentHandler);
      ((DOMScanner)localObject).scan(sm.getSOAPPart());
    }
    else
    {
      paramContentHandler.setDocumentLocator(NULL_LOCATOR);
      paramContentHandler.startDocument();
      paramContentHandler.startPrefixMapping("S", str);
      startPrefixMapping(paramContentHandler, envelopeAttrs, "S");
      paramContentHandler.startElement(str, "Envelope", "S:Envelope", getAttributes(envelopeAttrs));
      if (hasHeaders())
      {
        startPrefixMapping(paramContentHandler, headerAttrs, "S");
        paramContentHandler.startElement(str, "Header", "S:Header", getAttributes(headerAttrs));
        localObject = getHeaders();
        Iterator localIterator = ((MessageHeaders)localObject).asList().iterator();
        while (localIterator.hasNext())
        {
          Header localHeader = (Header)localIterator.next();
          localHeader.writeTo(paramContentHandler, paramErrorHandler);
        }
        endPrefixMapping(paramContentHandler, headerAttrs, "S");
        paramContentHandler.endElement(str, "Header", "S:Header");
      }
      startPrefixMapping(paramContentHandler, bodyAttrs, "S");
      paramContentHandler.startElement(str, "Body", "S:Body", getAttributes(bodyAttrs));
      writePayloadTo(paramContentHandler, paramErrorHandler, true);
      endPrefixMapping(paramContentHandler, bodyAttrs, "S");
      paramContentHandler.endElement(str, "Body", "S:Body");
      endPrefixMapping(paramContentHandler, envelopeAttrs, "S");
      paramContentHandler.endElement(str, "Envelope", "S:Envelope");
    }
  }
  
  private AttributesImpl getAttributes(NamedNodeMap paramNamedNodeMap)
  {
    AttributesImpl localAttributesImpl = new AttributesImpl();
    if (paramNamedNodeMap == null) {
      return EMPTY_ATTS;
    }
    for (int i = 0; i < paramNamedNodeMap.getLength(); i++)
    {
      Attr localAttr = (Attr)paramNamedNodeMap.item(i);
      if ((!"xmlns".equals(localAttr.getPrefix())) && (!"xmlns".equals(localAttr.getLocalName()))) {
        localAttributesImpl.addAttribute(fixNull(localAttr.getNamespaceURI()), localAttr.getLocalName(), localAttr.getName(), localAttr.getSchemaTypeInfo().getTypeName(), localAttr.getValue());
      }
    }
    return localAttributesImpl;
  }
  
  private void startPrefixMapping(ContentHandler paramContentHandler, NamedNodeMap paramNamedNodeMap, String paramString)
    throws SAXException
  {
    if (paramNamedNodeMap == null) {
      return;
    }
    for (int i = 0; i < paramNamedNodeMap.getLength(); i++)
    {
      Attr localAttr = (Attr)paramNamedNodeMap.item(i);
      if ((("xmlns".equals(localAttr.getPrefix())) || ("xmlns".equals(localAttr.getLocalName()))) && (!fixNull(localAttr.getPrefix()).equals(paramString))) {
        paramContentHandler.startPrefixMapping(fixNull(localAttr.getPrefix()), localAttr.getNamespaceURI());
      }
    }
  }
  
  private void endPrefixMapping(ContentHandler paramContentHandler, NamedNodeMap paramNamedNodeMap, String paramString)
    throws SAXException
  {
    if (paramNamedNodeMap == null) {
      return;
    }
    for (int i = 0; i < paramNamedNodeMap.getLength(); i++)
    {
      Attr localAttr = (Attr)paramNamedNodeMap.item(i);
      if ((("xmlns".equals(localAttr.getPrefix())) || ("xmlns".equals(localAttr.getLocalName()))) && (!fixNull(localAttr.getPrefix()).equals(paramString))) {
        paramContentHandler.endPrefixMapping(fixNull(localAttr.getPrefix()));
      }
    }
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  private void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {
    if (paramBoolean) {
      paramContentHandler = new FragmentContentHandler(paramContentHandler);
    }
    DOMScanner localDOMScanner = new DOMScanner();
    localDOMScanner.setContentHandler(paramContentHandler);
    localDOMScanner.scan(payload);
  }
  
  public Message copy()
  {
    try
    {
      if (!parsedMessage) {
        return new SAAJMessage(readAsSOAPMessage());
      }
      SOAPMessage localSOAPMessage = soapVersion.getMessageFactory().createMessage();
      SOAPBody localSOAPBody = localSOAPMessage.getSOAPPart().getEnvelope().getBody();
      Iterator localIterator = bodyParts.iterator();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        Node localNode = localSOAPBody.getOwnerDocument().importNode(localElement, true);
        localSOAPBody.appendChild(localNode);
      }
      addAttributes(localSOAPBody, bodyAttrs);
      return new SAAJMessage(getHeaders(), getAttachments(), localSOAPMessage, soapVersion);
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  public SOAPVersion getSOAPVersion()
  {
    return soapVersion;
  }
  
  protected XMLStreamReader getXMLStreamReader(SOAPElement paramSOAPElement)
  {
    return null;
  }
  
  protected XMLStreamReader createXMLStreamReader(SOAPElement paramSOAPElement)
  {
    DOMStreamReader localDOMStreamReader = new DOMStreamReader();
    localDOMStreamReader.setCurrentNode(paramSOAPElement);
    return localDOMStreamReader;
  }
  
  protected XMLStreamReader soapBodyFirstChildReader()
  {
    if (soapBodyFirstChildReader != null) {
      return soapBodyFirstChildReader;
    }
    soapBodyFirstChild();
    if (soapBodyFirstChild != null)
    {
      soapBodyFirstChildReader = getXMLStreamReader(soapBodyFirstChild);
      if (soapBodyFirstChildReader == null) {
        soapBodyFirstChildReader = createXMLStreamReader(soapBodyFirstChild);
      }
      if (soapBodyFirstChildReader.getEventType() == 7) {
        try
        {
          while (soapBodyFirstChildReader.getEventType() != 1) {
            soapBodyFirstChildReader.next();
          }
        }
        catch (XMLStreamException localXMLStreamException)
        {
          throw new RuntimeException(localXMLStreamException);
        }
      }
      return soapBodyFirstChildReader;
    }
    payloadLocalName = null;
    payloadNamespace = null;
    return null;
  }
  
  SOAPElement soapBodyFirstChild()
  {
    if (soapBodyFirstChild != null) {
      return soapBodyFirstChild;
    }
    try
    {
      int i = 0;
      for (Object localObject1 = sm.getSOAPBody().getFirstChild(); (localObject1 != null) && (i == 0); localObject1 = ((Node)localObject1).getNextSibling()) {
        if (((Node)localObject1).getNodeType() == 1)
        {
          i = 1;
          if ((localObject1 instanceof SOAPElement))
          {
            soapBodyFirstChild = ((SOAPElement)localObject1);
            payloadLocalName = soapBodyFirstChild.getLocalName();
            payloadNamespace = soapBodyFirstChild.getNamespaceURI();
            return soapBodyFirstChild;
          }
        }
      }
      if (i != 0)
      {
        localObject1 = sm.getSOAPBody().getChildElements();
        while (((Iterator)localObject1).hasNext())
        {
          Object localObject2 = ((Iterator)localObject1).next();
          if ((localObject2 instanceof SOAPElement))
          {
            soapBodyFirstChild = ((SOAPElement)localObject2);
            payloadLocalName = soapBodyFirstChild.getLocalName();
            payloadNamespace = soapBodyFirstChild.getNamespaceURI();
            return soapBodyFirstChild;
          }
        }
      }
    }
    catch (SOAPException localSOAPException)
    {
      throw new RuntimeException(localSOAPException);
    }
    return soapBodyFirstChild;
  }
  
  protected static class SAAJAttachment
    implements AttachmentEx
  {
    final AttachmentPart ap;
    String contentIdNoAngleBracket;
    
    public SAAJAttachment(AttachmentPart paramAttachmentPart)
    {
      ap = paramAttachmentPart;
    }
    
    public String getContentId()
    {
      if (contentIdNoAngleBracket == null)
      {
        contentIdNoAngleBracket = ap.getContentId();
        if ((contentIdNoAngleBracket != null) && (contentIdNoAngleBracket.charAt(0) == '<')) {
          contentIdNoAngleBracket = contentIdNoAngleBracket.substring(1, contentIdNoAngleBracket.length() - 1);
        }
      }
      return contentIdNoAngleBracket;
    }
    
    public String getContentType()
    {
      return ap.getContentType();
    }
    
    public byte[] asByteArray()
    {
      try
      {
        return ap.getRawContentBytes();
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public DataHandler asDataHandler()
    {
      try
      {
        return ap.getDataHandler();
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public Source asSource()
    {
      try
      {
        return new StreamSource(ap.getRawContent());
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public InputStream asInputStream()
    {
      try
      {
        return ap.getRawContent();
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public void writeTo(OutputStream paramOutputStream)
      throws IOException
    {
      try
      {
        ASCIIUtility.copyStream(ap.getRawContent(), paramOutputStream);
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    
    public void writeTo(SOAPMessage paramSOAPMessage)
    {
      paramSOAPMessage.addAttachmentPart(ap);
    }
    
    AttachmentPart asAttachmentPart()
    {
      return ap;
    }
    
    public Iterator<AttachmentEx.MimeHeader> getMimeHeaders()
    {
      final Iterator localIterator = ap.getAllMimeHeaders();
      new Iterator()
      {
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public AttachmentEx.MimeHeader next()
        {
          final MimeHeader localMimeHeader = (MimeHeader)localIterator.next();
          new AttachmentEx.MimeHeader()
          {
            public String getName()
            {
              return localMimeHeader.getName();
            }
            
            public String getValue()
            {
              return localMimeHeader.getValue();
            }
          };
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
  }
  
  protected static class SAAJAttachmentSet
    implements AttachmentSet
  {
    private Map<String, Attachment> attMap;
    private Iterator attIter;
    
    public SAAJAttachmentSet(SOAPMessage paramSOAPMessage)
    {
      attIter = paramSOAPMessage.getAttachments();
    }
    
    public Attachment get(String paramString)
    {
      if (attMap == null)
      {
        if (!attIter.hasNext()) {
          return null;
        }
        attMap = createAttachmentMap();
      }
      if (paramString.charAt(0) != '<') {
        return (Attachment)attMap.get('<' + paramString + '>');
      }
      return (Attachment)attMap.get(paramString);
    }
    
    public boolean isEmpty()
    {
      if (attMap != null) {
        return attMap.isEmpty();
      }
      return !attIter.hasNext();
    }
    
    public Iterator<Attachment> iterator()
    {
      if (attMap == null) {
        attMap = createAttachmentMap();
      }
      return attMap.values().iterator();
    }
    
    private Map<String, Attachment> createAttachmentMap()
    {
      HashMap localHashMap = new HashMap();
      while (attIter.hasNext())
      {
        AttachmentPart localAttachmentPart = (AttachmentPart)attIter.next();
        localHashMap.put(localAttachmentPart.getContentId(), new SAAJMessage.SAAJAttachment(localAttachmentPart));
      }
      return localHashMap;
    }
    
    public void add(Attachment paramAttachment)
    {
      attMap.put('<' + paramAttachment.getContentId() + '>', paramAttachment);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\saaj\SAAJMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
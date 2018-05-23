package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.MessageWritable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public abstract class AbstractMessageImpl
  extends Message
{
  protected final SOAPVersion soapVersion;
  @NotNull
  protected TagInfoset envelopeTag;
  @NotNull
  protected TagInfoset headerTag;
  @NotNull
  protected TagInfoset bodyTag;
  protected static final AttributesImpl EMPTY_ATTS;
  protected static final LocatorImpl NULL_LOCATOR = new LocatorImpl();
  protected static final List<TagInfoset> DEFAULT_TAGS;
  
  static void create(SOAPVersion paramSOAPVersion, List paramList)
  {
    int i = paramSOAPVersion.ordinal() * 3;
    paramList.add(i, new TagInfoset(nsUri, "Envelope", "S", EMPTY_ATTS, new String[] { "S", nsUri }));
    paramList.add(i + 1, new TagInfoset(nsUri, "Header", "S", EMPTY_ATTS, new String[0]));
    paramList.add(i + 2, new TagInfoset(nsUri, "Body", "S", EMPTY_ATTS, new String[0]));
  }
  
  protected AbstractMessageImpl(SOAPVersion paramSOAPVersion)
  {
    soapVersion = paramSOAPVersion;
  }
  
  public SOAPVersion getSOAPVersion()
  {
    return soapVersion;
  }
  
  protected AbstractMessageImpl(AbstractMessageImpl paramAbstractMessageImpl)
  {
    soapVersion = soapVersion;
  }
  
  public Source readEnvelopeAsSource()
  {
    return new SAXSource(new XMLReaderImpl(this), XMLReaderImpl.THE_SOURCE);
  }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    if (hasAttachments()) {
      paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments()));
    }
    try
    {
      Object localObject1 = paramUnmarshaller.unmarshal(readPayloadAsSource());
      return (T)localObject1;
    }
    finally
    {
      paramUnmarshaller.setAttachmentUnmarshaller(null);
    }
  }
  
  /**
   * @deprecated
   */
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    return (T)paramBridge.unmarshal(readPayloadAsSource(), hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
  }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException
  {
    return (T)paramXMLBridge.unmarshal(readPayloadAsSource(), hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    String str = soapVersion.nsUri;
    paramXMLStreamWriter.writeStartDocument();
    paramXMLStreamWriter.writeStartElement("S", "Envelope", str);
    paramXMLStreamWriter.writeNamespace("S", str);
    if (hasHeaders())
    {
      paramXMLStreamWriter.writeStartElement("S", "Header", str);
      MessageHeaders localMessageHeaders = getHeaders();
      Iterator localIterator = localMessageHeaders.asList().iterator();
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        localHeader.writeTo(paramXMLStreamWriter);
      }
      paramXMLStreamWriter.writeEndElement();
    }
    paramXMLStreamWriter.writeStartElement("S", "Body", str);
    writePayloadTo(paramXMLStreamWriter);
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndDocument();
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    String str = soapVersion.nsUri;
    paramContentHandler.setDocumentLocator(NULL_LOCATOR);
    paramContentHandler.startDocument();
    paramContentHandler.startPrefixMapping("S", str);
    paramContentHandler.startElement(str, "Envelope", "S:Envelope", EMPTY_ATTS);
    if (hasHeaders())
    {
      paramContentHandler.startElement(str, "Header", "S:Header", EMPTY_ATTS);
      MessageHeaders localMessageHeaders = getHeaders();
      Iterator localIterator = localMessageHeaders.asList().iterator();
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        localHeader.writeTo(paramContentHandler, paramErrorHandler);
      }
      paramContentHandler.endElement(str, "Header", "S:Header");
    }
    paramContentHandler.startElement(str, "Body", "S:Body", EMPTY_ATTS);
    writePayloadTo(paramContentHandler, paramErrorHandler, true);
    paramContentHandler.endElement(str, "Body", "S:Body");
    paramContentHandler.endElement(str, "Envelope", "S:Envelope");
  }
  
  protected abstract void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException;
  
  public Message toSAAJ(Packet paramPacket, Boolean paramBoolean)
    throws SOAPException
  {
    SAAJMessage localSAAJMessage = SAAJFactory.read(paramPacket);
    if ((localSAAJMessage instanceof MessageWritable)) {
      ((MessageWritable)localSAAJMessage).setMTOMConfiguration(paramPacket.getMtomFeature());
    }
    if (paramBoolean != null) {
      transportHeaders(paramPacket, paramBoolean.booleanValue(), localSAAJMessage.readAsSOAPMessage());
    }
    return localSAAJMessage;
  }
  
  public SOAPMessage readAsSOAPMessage()
    throws SOAPException
  {
    return SAAJFactory.read(soapVersion, this);
  }
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean)
    throws SOAPException
  {
    SOAPMessage localSOAPMessage = SAAJFactory.read(soapVersion, this, paramPacket);
    transportHeaders(paramPacket, paramBoolean, localSOAPMessage);
    return localSOAPMessage;
  }
  
  private void transportHeaders(Packet paramPacket, boolean paramBoolean, SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    Map localMap = getTransportHeaders(paramPacket, paramBoolean);
    if (localMap != null) {
      addSOAPMimeHeaders(paramSOAPMessage.getMimeHeaders(), localMap);
    }
    if (paramSOAPMessage.saveRequired()) {
      paramSOAPMessage.saveChanges();
    }
  }
  
  static
  {
    EMPTY_ATTS = new AttributesImpl();
    ArrayList localArrayList = new ArrayList();
    create(SOAPVersion.SOAP_11, localArrayList);
    create(SOAPVersion.SOAP_12, localArrayList);
    DEFAULT_TAGS = Collections.unmodifiableList(localArrayList);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\AbstractMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
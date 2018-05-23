package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

class MessageWrapper
  extends StreamMessage
{
  Packet packet;
  Message delegate;
  StreamMessage streamDelegate;
  
  public void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {
    streamDelegate.writePayloadTo(paramContentHandler, paramErrorHandler, paramBoolean);
  }
  
  public String getBodyPrologue()
  {
    return streamDelegate.getBodyPrologue();
  }
  
  public String getBodyEpilogue()
  {
    return streamDelegate.getBodyEpilogue();
  }
  
  MessageWrapper(Packet paramPacket, Message paramMessage)
  {
    super(paramMessage.getSOAPVersion());
    packet = paramPacket;
    delegate = paramMessage;
    streamDelegate = ((paramMessage instanceof StreamMessage) ? (StreamMessage)paramMessage : null);
    setMessageMedadata(paramPacket);
  }
  
  public int hashCode()
  {
    return delegate.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return delegate.equals(paramObject);
  }
  
  public boolean hasHeaders()
  {
    return delegate.hasHeaders();
  }
  
  public AttachmentSet getAttachments()
  {
    return delegate.getAttachments();
  }
  
  public String toString()
  {
    return delegate.toString();
  }
  
  public boolean isOneWay(WSDLPort paramWSDLPort)
  {
    return delegate.isOneWay(paramWSDLPort);
  }
  
  public String getPayloadLocalPart()
  {
    return delegate.getPayloadLocalPart();
  }
  
  public String getPayloadNamespaceURI()
  {
    return delegate.getPayloadNamespaceURI();
  }
  
  public boolean hasPayload()
  {
    return delegate.hasPayload();
  }
  
  public boolean isFault()
  {
    return delegate.isFault();
  }
  
  public QName getFirstDetailEntryName()
  {
    return delegate.getFirstDetailEntryName();
  }
  
  public Source readEnvelopeAsSource()
  {
    return delegate.readEnvelopeAsSource();
  }
  
  public Source readPayloadAsSource()
  {
    return delegate.readPayloadAsSource();
  }
  
  public SOAPMessage readAsSOAPMessage()
    throws SOAPException
  {
    if (!(delegate instanceof SAAJMessage)) {
      delegate = toSAAJ(packet, null);
    }
    return delegate.readAsSOAPMessage();
  }
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean)
    throws SOAPException
  {
    if (!(delegate instanceof SAAJMessage)) {
      delegate = toSAAJ(paramPacket, Boolean.valueOf(paramBoolean));
    }
    return delegate.readAsSOAPMessage();
  }
  
  public Object readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    return delegate.readPayloadAsJAXB(paramUnmarshaller);
  }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    return (T)delegate.readPayloadAsJAXB(paramBridge);
  }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException
  {
    return (T)delegate.readPayloadAsJAXB(paramXMLBridge);
  }
  
  public XMLStreamReader readPayload()
  {
    try
    {
      return delegate.readPayload();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      localXMLStreamException.printStackTrace();
    }
    return null;
  }
  
  public void consume()
  {
    delegate.consume();
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    delegate.writePayloadTo(paramXMLStreamWriter);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    delegate.writeTo(paramXMLStreamWriter);
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    delegate.writeTo(paramContentHandler, paramErrorHandler);
  }
  
  public Message copy()
  {
    return delegate.copy();
  }
  
  public String getID(WSBinding paramWSBinding)
  {
    return delegate.getID(paramWSBinding);
  }
  
  public String getID(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion)
  {
    return delegate.getID(paramAddressingVersion, paramSOAPVersion);
  }
  
  public SOAPVersion getSOAPVersion()
  {
    return delegate.getSOAPVersion();
  }
  
  @NotNull
  public MessageHeaders getHeaders()
  {
    return delegate.getHeaders();
  }
  
  public void setMessageMedadata(MessageMetadata paramMessageMetadata)
  {
    super.setMessageMedadata(paramMessageMetadata);
    delegate.setMessageMedadata(paramMessageMetadata);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\MessageWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
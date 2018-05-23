package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
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

public class FilterMessageImpl
  extends Message
{
  private final Message delegate;
  
  protected FilterMessageImpl(Message paramMessage)
  {
    delegate = paramMessage;
  }
  
  public boolean hasHeaders()
  {
    return delegate.hasHeaders();
  }
  
  @NotNull
  public MessageHeaders getHeaders()
  {
    return delegate.getHeaders();
  }
  
  @NotNull
  public AttachmentSet getAttachments()
  {
    return delegate.getAttachments();
  }
  
  protected boolean hasAttachments()
  {
    return delegate.hasAttachments();
  }
  
  public boolean isOneWay(@NotNull WSDLPort paramWSDLPort)
  {
    return delegate.isOneWay(paramWSDLPort);
  }
  
  @Nullable
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
  
  @Nullable
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
    return delegate.readAsSOAPMessage();
  }
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean)
    throws SOAPException
  {
    return delegate.readAsSOAPMessage(paramPacket, paramBoolean);
  }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    return (T)delegate.readPayloadAsJAXB(paramUnmarshaller);
  }
  
  /**
   * @deprecated
   */
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
    throws XMLStreamException
  {
    return delegate.readPayload();
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
  
  @NotNull
  public String getID(@NotNull WSBinding paramWSBinding)
  {
    return delegate.getID(paramWSBinding);
  }
  
  @NotNull
  public String getID(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion)
  {
    return delegate.getID(paramAddressingVersion, paramSOAPVersion);
  }
  
  public SOAPVersion getSOAPVersion()
  {
    return delegate.getSOAPVersion();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\FilterMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
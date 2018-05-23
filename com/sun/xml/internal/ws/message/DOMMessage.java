package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class DOMMessage
  extends AbstractMessageImpl
{
  private MessageHeaders headers;
  private final Element payload;
  
  public DOMMessage(SOAPVersion paramSOAPVersion, Element paramElement)
  {
    this(paramSOAPVersion, null, paramElement);
  }
  
  public DOMMessage(SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, Element paramElement)
  {
    this(paramSOAPVersion, paramMessageHeaders, paramElement, null);
  }
  
  public DOMMessage(SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, Element paramElement, AttachmentSet paramAttachmentSet)
  {
    super(paramSOAPVersion);
    headers = paramMessageHeaders;
    payload = paramElement;
    attachmentSet = paramAttachmentSet;
    assert (paramElement != null);
  }
  
  private DOMMessage(DOMMessage paramDOMMessage)
  {
    super(paramDOMMessage);
    headers = HeaderList.copy(headers);
    payload = payload;
  }
  
  public boolean hasHeaders()
  {
    return getHeaders().hasHeaders();
  }
  
  public MessageHeaders getHeaders()
  {
    if (headers == null) {
      headers = new HeaderList(getSOAPVersion());
    }
    return headers;
  }
  
  public String getPayloadLocalPart()
  {
    return payload.getLocalName();
  }
  
  public String getPayloadNamespaceURI()
  {
    return payload.getNamespaceURI();
  }
  
  public boolean hasPayload()
  {
    return true;
  }
  
  public Source readPayloadAsSource()
  {
    return new DOMSource(payload);
  }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    if (hasAttachments()) {
      paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments()));
    }
    try
    {
      Object localObject1 = paramUnmarshaller.unmarshal(payload);
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
    return (T)paramBridge.unmarshal(payload, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
  }
  
  public XMLStreamReader readPayload()
    throws XMLStreamException
  {
    DOMStreamReader localDOMStreamReader = new DOMStreamReader();
    localDOMStreamReader.setCurrentNode(payload);
    localDOMStreamReader.nextTag();
    assert (localDOMStreamReader.getEventType() == 1);
    return localDOMStreamReader;
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
  {
    try
    {
      if (payload != null) {
        DOMUtil.serializeNode(payload, paramXMLStreamWriter);
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
  }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
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
    return new DOMMessage(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\DOMMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
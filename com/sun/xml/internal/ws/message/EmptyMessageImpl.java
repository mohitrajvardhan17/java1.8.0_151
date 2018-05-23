package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class EmptyMessageImpl
  extends AbstractMessageImpl
{
  private final MessageHeaders headers;
  private final AttachmentSet attachmentSet;
  
  public EmptyMessageImpl(SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    headers = new HeaderList(paramSOAPVersion);
    attachmentSet = new AttachmentSetImpl();
  }
  
  public EmptyMessageImpl(MessageHeaders paramMessageHeaders, @NotNull AttachmentSet paramAttachmentSet, SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    if (paramMessageHeaders == null) {
      paramMessageHeaders = new HeaderList(paramSOAPVersion);
    }
    attachmentSet = paramAttachmentSet;
    headers = paramMessageHeaders;
  }
  
  private EmptyMessageImpl(EmptyMessageImpl paramEmptyMessageImpl)
  {
    super(paramEmptyMessageImpl);
    headers = new HeaderList(headers);
    attachmentSet = attachmentSet;
  }
  
  public boolean hasHeaders()
  {
    return headers.hasHeaders();
  }
  
  public MessageHeaders getHeaders()
  {
    return headers;
  }
  
  public String getPayloadLocalPart()
  {
    return null;
  }
  
  public String getPayloadNamespaceURI()
  {
    return null;
  }
  
  public boolean hasPayload()
  {
    return false;
  }
  
  public Source readPayloadAsSource()
  {
    return null;
  }
  
  public XMLStreamReader readPayload()
    throws XMLStreamException
  {
    return null;
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {}
  
  public void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {}
  
  public Message copy()
  {
    return new EmptyMessageImpl(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\EmptyMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
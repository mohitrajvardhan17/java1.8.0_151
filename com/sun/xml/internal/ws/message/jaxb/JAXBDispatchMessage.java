package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.PayloadElementSniffer;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class JAXBDispatchMessage
  extends AbstractMessageImpl
{
  private final Object jaxbObject;
  private final XMLBridge bridge;
  private final JAXBContext rawContext;
  private QName payloadQName;
  
  private JAXBDispatchMessage(JAXBDispatchMessage paramJAXBDispatchMessage)
  {
    super(paramJAXBDispatchMessage);
    jaxbObject = jaxbObject;
    rawContext = rawContext;
    bridge = bridge;
  }
  
  public JAXBDispatchMessage(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    bridge = null;
    rawContext = paramJAXBContext;
    jaxbObject = paramObject;
  }
  
  public JAXBDispatchMessage(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion)
  {
    super(paramSOAPVersion);
    bridge = paramBindingContext.createFragmentBridge();
    rawContext = null;
    jaxbObject = paramObject;
  }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean)
    throws SAXException
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasHeaders()
  {
    return false;
  }
  
  public MessageHeaders getHeaders()
  {
    return null;
  }
  
  public String getPayloadLocalPart()
  {
    if (payloadQName == null) {
      readPayloadElement();
    }
    return payloadQName.getLocalPart();
  }
  
  public String getPayloadNamespaceURI()
  {
    if (payloadQName == null) {
      readPayloadElement();
    }
    return payloadQName.getNamespaceURI();
  }
  
  private void readPayloadElement()
  {
    PayloadElementSniffer localPayloadElementSniffer = new PayloadElementSniffer();
    try
    {
      if (rawContext != null)
      {
        Marshaller localMarshaller = rawContext.createMarshaller();
        localMarshaller.setProperty("jaxb.fragment", Boolean.FALSE);
        localMarshaller.marshal(jaxbObject, localPayloadElementSniffer);
      }
      else
      {
        bridge.marshal(jaxbObject, localPayloadElementSniffer, null);
      }
    }
    catch (JAXBException localJAXBException)
    {
      payloadQName = localPayloadElementSniffer.getPayloadQName();
    }
  }
  
  public boolean hasPayload()
  {
    return true;
  }
  
  public Source readPayloadAsSource()
  {
    throw new UnsupportedOperationException();
  }
  
  public XMLStreamReader readPayload()
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  public Message copy()
  {
    return new JAXBDispatchMessage(this);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    try
    {
      AttachmentMarshallerImpl localAttachmentMarshallerImpl = (paramXMLStreamWriter instanceof MtomStreamWriter) ? ((MtomStreamWriter)paramXMLStreamWriter).getAttachmentMarshaller() : new AttachmentMarshallerImpl(attachmentSet);
      String str = XMLStreamWriterUtil.getEncoding(paramXMLStreamWriter);
      OutputStream localOutputStream = bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(paramXMLStreamWriter) : null;
      if (rawContext != null)
      {
        Marshaller localMarshaller = rawContext.createMarshaller();
        localMarshaller.setProperty("jaxb.fragment", Boolean.FALSE);
        localMarshaller.setAttachmentMarshaller(localAttachmentMarshallerImpl);
        if (localOutputStream != null) {
          localMarshaller.marshal(jaxbObject, localOutputStream);
        } else {
          localMarshaller.marshal(jaxbObject, paramXMLStreamWriter);
        }
      }
      else if ((localOutputStream != null) && (str != null) && (str.equalsIgnoreCase("utf-8")))
      {
        bridge.marshal(jaxbObject, localOutputStream, paramXMLStreamWriter.getNamespaceContext(), localAttachmentMarshallerImpl);
      }
      else
      {
        bridge.marshal(jaxbObject, paramXMLStreamWriter, localAttachmentMarshallerImpl);
      }
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBDispatchMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
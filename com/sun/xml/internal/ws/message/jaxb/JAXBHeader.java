package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBHeader
  extends AbstractHeaderImpl
{
  private final Object jaxbObject;
  private final XMLBridge bridge;
  private String nsUri;
  private String localName;
  private Attributes atts;
  private XMLStreamBuffer infoset;
  
  public JAXBHeader(BindingContext paramBindingContext, Object paramObject)
  {
    jaxbObject = paramObject;
    bridge = paramBindingContext.createFragmentBridge();
    if ((paramObject instanceof JAXBElement))
    {
      JAXBElement localJAXBElement = (JAXBElement)paramObject;
      nsUri = localJAXBElement.getName().getNamespaceURI();
      localName = localJAXBElement.getName().getLocalPart();
    }
  }
  
  public JAXBHeader(XMLBridge paramXMLBridge, Object paramObject)
  {
    jaxbObject = paramObject;
    bridge = paramXMLBridge;
    QName localQName = getTypeInfotagName;
    nsUri = localQName.getNamespaceURI();
    localName = localQName.getLocalPart();
  }
  
  private void parse()
  {
    RootElementSniffer localRootElementSniffer = new RootElementSniffer();
    try
    {
      bridge.marshal(jaxbObject, localRootElementSniffer, null);
    }
    catch (JAXBException localJAXBException)
    {
      nsUri = localRootElementSniffer.getNsUri();
      localName = localRootElementSniffer.getLocalName();
      atts = localRootElementSniffer.getAttributes();
    }
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    if (nsUri == null) {
      parse();
    }
    return nsUri;
  }
  
  @NotNull
  public String getLocalPart()
  {
    if (localName == null) {
      parse();
    }
    return localName;
  }
  
  public String getAttribute(String paramString1, String paramString2)
  {
    if (atts == null) {
      parse();
    }
    return atts.getValue(paramString1, paramString2);
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    if (infoset == null)
    {
      MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      writeTo(localMutableXMLStreamBuffer.createFromXMLStreamWriter());
      infoset = localMutableXMLStreamBuffer;
    }
    return infoset.readAsXMLStreamReader();
  }
  
  public <T> T readAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    try
    {
      JAXBResult localJAXBResult = new JAXBResult(paramUnmarshaller);
      localJAXBResult.getHandler().startDocument();
      bridge.marshal(jaxbObject, localJAXBResult);
      localJAXBResult.getHandler().endDocument();
      return (T)localJAXBResult.getResult();
    }
    catch (SAXException localSAXException)
    {
      throw new JAXBException(localSAXException);
    }
  }
  
  /**
   * @deprecated
   */
  public <T> T readAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    return (T)paramBridge.unmarshal(new JAXBBridgeSource(bridge, jaxbObject));
  }
  
  public <T> T readAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException
  {
    return (T)paramXMLBridge.unmarshal(new JAXBBridgeSource(bridge, jaxbObject), null);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    try
    {
      String str = XMLStreamWriterUtil.getEncoding(paramXMLStreamWriter);
      OutputStream localOutputStream = bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(paramXMLStreamWriter) : null;
      if ((localOutputStream != null) && (str != null) && (str.equalsIgnoreCase("utf-8"))) {
        bridge.marshal(jaxbObject, localOutputStream, paramXMLStreamWriter.getNamespaceContext(), null);
      } else {
        bridge.marshal(jaxbObject, paramXMLStreamWriter, null);
      }
    }
    catch (JAXBException localJAXBException)
    {
      throw new XMLStreamException2(localJAXBException);
    }
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    try
    {
      SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (localSOAPHeader == null) {
        localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
      }
      bridge.marshal(jaxbObject, localSOAPHeader);
    }
    catch (JAXBException localJAXBException)
    {
      throw new SOAPException(localJAXBException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    try
    {
      bridge.marshal(jaxbObject, paramContentHandler, null);
    }
    catch (JAXBException localJAXBException)
    {
      SAXParseException localSAXParseException = new SAXParseException(localJAXBException.getMessage(), null, null, -1, -1, localJAXBException);
      paramErrorHandler.fatalError(localSAXParseException);
      throw localSAXParseException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
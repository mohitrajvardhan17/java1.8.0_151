package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class DOMHeader<N extends Element>
  extends AbstractHeaderImpl
{
  protected final N node;
  private final String nsUri;
  private final String localName;
  
  public DOMHeader(N paramN)
  {
    assert (paramN != null);
    node = paramN;
    nsUri = fixNull(paramN.getNamespaceURI());
    localName = paramN.getLocalName();
  }
  
  public String getNamespaceURI()
  {
    return nsUri;
  }
  
  public String getLocalPart()
  {
    return localName;
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    DOMStreamReader localDOMStreamReader = new DOMStreamReader(node);
    localDOMStreamReader.nextTag();
    return localDOMStreamReader;
  }
  
  public <T> T readAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    return (T)paramUnmarshaller.unmarshal(node);
  }
  
  /**
   * @deprecated
   */
  public <T> T readAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    return (T)paramBridge.unmarshal(node);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    DOMUtil.serializeNode(node, paramXMLStreamWriter);
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString != null) {
      return paramString;
    }
    return "";
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    DOMScanner localDOMScanner = new DOMScanner();
    localDOMScanner.setContentHandler(paramContentHandler);
    localDOMScanner.scan(node);
  }
  
  public String getAttribute(String paramString1, String paramString2)
  {
    if (paramString1.length() == 0) {
      paramString1 = null;
    }
    return node.getAttributeNS(paramString1, paramString2);
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (localSOAPHeader == null) {
      localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
    }
    Node localNode = localSOAPHeader.getOwnerDocument().importNode(node, true);
    localSOAPHeader.appendChild(localNode);
  }
  
  public String getStringContent()
  {
    return node.getTextContent();
  }
  
  public N getWrappedNode()
  {
    return node;
  }
  
  public int hashCode()
  {
    return getWrappedNode().hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DOMHeader)) {
      return getWrappedNode().equals(((DOMHeader)paramObject).getWrappedNode());
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\DOMHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
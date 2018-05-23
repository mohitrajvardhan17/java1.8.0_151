package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

final class EPRHeader
  extends AbstractHeaderImpl
{
  private final String nsUri;
  private final String localName;
  private final WSEndpointReference epr;
  
  EPRHeader(QName paramQName, WSEndpointReference paramWSEndpointReference)
  {
    nsUri = paramQName.getNamespaceURI();
    localName = paramQName.getLocalPart();
    epr = paramWSEndpointReference;
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    return nsUri;
  }
  
  @NotNull
  public String getLocalPart()
  {
    return localName;
  }
  
  @Nullable
  public String getAttribute(@NotNull String paramString1, @NotNull String paramString2)
  {
    try
    {
      XMLStreamReader localXMLStreamReader = epr.read("EndpointReference");
      while (localXMLStreamReader.getEventType() != 1) {
        localXMLStreamReader.next();
      }
      return localXMLStreamReader.getAttributeValue(paramString1, paramString2);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new AssertionError(localXMLStreamException);
    }
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    return epr.read(localName);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    epr.writeTo(localName, paramXMLStreamWriter);
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    try
    {
      Transformer localTransformer = XmlUtil.newTransformer();
      SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (localSOAPHeader == null) {
        localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
      }
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      XMLStreamWriter localXMLStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(localByteArrayOutputStream);
      epr.writeTo(localName, localXMLStreamWriter);
      localXMLStreamWriter.flush();
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      Element localElement = localDocumentBuilderFactory.newDocumentBuilder().parse(localByteArrayInputStream).getDocumentElement();
      Node localNode = localSOAPHeader.getOwnerDocument().importNode(localElement, true);
      localSOAPHeader.appendChild(localNode);
    }
    catch (Exception localException)
    {
      throw new SOAPException(localException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    epr.writeTo(localName, paramContentHandler, paramErrorHandler, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\addressing\EPRHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
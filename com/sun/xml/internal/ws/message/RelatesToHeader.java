package com.sun.xml.internal.ws.message;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class RelatesToHeader
  extends StringHeader
{
  protected String type;
  private final QName typeAttributeName;
  
  public RelatesToHeader(QName paramQName, String paramString1, String paramString2)
  {
    super(paramQName, paramString1);
    type = paramString2;
    typeAttributeName = new QName(paramQName.getNamespaceURI(), "type");
  }
  
  public RelatesToHeader(QName paramQName, String paramString)
  {
    super(paramQName, paramString);
    typeAttributeName = new QName(paramQName.getNamespaceURI(), "type");
  }
  
  public String getType()
  {
    return type;
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    paramXMLStreamWriter.writeStartElement("", name.getLocalPart(), name.getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(name.getNamespaceURI());
    if (type != null) {
      paramXMLStreamWriter.writeAttribute("type", type);
    }
    paramXMLStreamWriter.writeCharacters(value);
    paramXMLStreamWriter.writeEndElement();
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (localSOAPHeader == null) {
      localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
    }
    SOAPHeaderElement localSOAPHeaderElement = localSOAPHeader.addHeaderElement(name);
    if (type != null) {
      localSOAPHeaderElement.addAttribute(typeAttributeName, type);
    }
    localSOAPHeaderElement.addTextNode(value);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\RelatesToHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
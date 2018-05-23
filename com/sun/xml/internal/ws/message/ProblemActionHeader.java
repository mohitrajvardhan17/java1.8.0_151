package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class ProblemActionHeader
  extends AbstractHeaderImpl
{
  @NotNull
  protected String action;
  protected String soapAction;
  @NotNull
  protected AddressingVersion av;
  private static final String actionLocalName = "Action";
  private static final String soapActionLocalName = "SoapAction";
  
  public ProblemActionHeader(@NotNull String paramString, @NotNull AddressingVersion paramAddressingVersion)
  {
    this(paramString, null, paramAddressingVersion);
  }
  
  public ProblemActionHeader(@NotNull String paramString1, String paramString2, @NotNull AddressingVersion paramAddressingVersion)
  {
    assert (paramString1 != null);
    assert (paramAddressingVersion != null);
    action = paramString1;
    soapAction = paramString2;
    av = paramAddressingVersion;
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    return av.nsUri;
  }
  
  @NotNull
  public String getLocalPart()
  {
    return "ProblemAction";
  }
  
  @Nullable
  public String getAttribute(@NotNull String paramString1, @NotNull String paramString2)
  {
    return null;
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    XMLStreamWriter localXMLStreamWriter = localMutableXMLStreamBuffer.createFromXMLStreamWriter();
    writeTo(localXMLStreamWriter);
    return localMutableXMLStreamBuffer.readAsXMLStreamReader();
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    paramXMLStreamWriter.writeStartElement("", getLocalPart(), getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(getNamespaceURI());
    paramXMLStreamWriter.writeStartElement("Action");
    paramXMLStreamWriter.writeCharacters(action);
    paramXMLStreamWriter.writeEndElement();
    if (soapAction != null)
    {
      paramXMLStreamWriter.writeStartElement("SoapAction");
      paramXMLStreamWriter.writeCharacters(soapAction);
      paramXMLStreamWriter.writeEndElement();
    }
    paramXMLStreamWriter.writeEndElement();
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (localSOAPHeader == null) {
      localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
    }
    SOAPHeaderElement localSOAPHeaderElement = localSOAPHeader.addHeaderElement(new QName(getNamespaceURI(), getLocalPart()));
    localSOAPHeaderElement.addChildElement("Action");
    localSOAPHeaderElement.addTextNode(action);
    if (soapAction != null)
    {
      localSOAPHeaderElement.addChildElement("SoapAction");
      localSOAPHeaderElement.addTextNode(soapAction);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    String str1 = getNamespaceURI();
    String str2 = getLocalPart();
    paramContentHandler.startPrefixMapping("", str1);
    paramContentHandler.startElement(str1, str2, str2, EMPTY_ATTS);
    paramContentHandler.startElement(str1, "Action", "Action", EMPTY_ATTS);
    paramContentHandler.characters(action.toCharArray(), 0, action.length());
    paramContentHandler.endElement(str1, "Action", "Action");
    if (soapAction != null)
    {
      paramContentHandler.startElement(str1, "SoapAction", "SoapAction", EMPTY_ATTS);
      paramContentHandler.characters(soapAction.toCharArray(), 0, soapAction.length());
      paramContentHandler.endElement(str1, "SoapAction", "SoapAction");
    }
    paramContentHandler.endElement(str1, str2, str2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\ProblemActionHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
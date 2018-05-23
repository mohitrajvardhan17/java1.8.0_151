package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
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
import org.xml.sax.helpers.AttributesImpl;

public class StringHeader
  extends AbstractHeaderImpl
{
  protected final QName name;
  protected final String value;
  protected boolean mustUnderstand = false;
  protected SOAPVersion soapVersion;
  protected static final String MUST_UNDERSTAND = "mustUnderstand";
  protected static final String S12_MUST_UNDERSTAND_TRUE = "true";
  protected static final String S11_MUST_UNDERSTAND_TRUE = "1";
  
  public StringHeader(@NotNull QName paramQName, @NotNull String paramString)
  {
    assert (paramQName != null);
    assert (paramString != null);
    name = paramQName;
    value = paramString;
  }
  
  public StringHeader(@NotNull QName paramQName, @NotNull String paramString, @NotNull SOAPVersion paramSOAPVersion, boolean paramBoolean)
  {
    name = paramQName;
    value = paramString;
    soapVersion = paramSOAPVersion;
    mustUnderstand = paramBoolean;
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    return name.getNamespaceURI();
  }
  
  @NotNull
  public String getLocalPart()
  {
    return name.getLocalPart();
  }
  
  @Nullable
  public String getAttribute(@NotNull String paramString1, @NotNull String paramString2)
  {
    if ((mustUnderstand) && (soapVersion.nsUri.equals(paramString1)) && ("mustUnderstand".equals(paramString2))) {
      return getMustUnderstandLiteral(soapVersion);
    }
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
    paramXMLStreamWriter.writeStartElement("", name.getLocalPart(), name.getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(name.getNamespaceURI());
    if (mustUnderstand)
    {
      paramXMLStreamWriter.writeNamespace("S", soapVersion.nsUri);
      paramXMLStreamWriter.writeAttribute("S", soapVersion.nsUri, "mustUnderstand", getMustUnderstandLiteral(soapVersion));
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
    if (mustUnderstand) {
      localSOAPHeaderElement.setMustUnderstand(true);
    }
    localSOAPHeaderElement.addTextNode(value);
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    String str1 = name.getNamespaceURI();
    String str2 = name.getLocalPart();
    paramContentHandler.startPrefixMapping("", str1);
    if (mustUnderstand)
    {
      AttributesImpl localAttributesImpl = new AttributesImpl();
      localAttributesImpl.addAttribute(soapVersion.nsUri, "mustUnderstand", "S:mustUnderstand", "CDATA", getMustUnderstandLiteral(soapVersion));
      paramContentHandler.startElement(str1, str2, str2, localAttributesImpl);
    }
    else
    {
      paramContentHandler.startElement(str1, str2, str2, EMPTY_ATTS);
    }
    paramContentHandler.characters(value.toCharArray(), 0, value.length());
    paramContentHandler.endElement(str1, str2, str2);
  }
  
  private static String getMustUnderstandLiteral(SOAPVersion paramSOAPVersion)
  {
    if (paramSOAPVersion == SOAPVersion.SOAP_12) {
      return "true";
    }
    return "1";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\StringHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
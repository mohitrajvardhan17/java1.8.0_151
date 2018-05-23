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

public class FaultDetailHeader
  extends AbstractHeaderImpl
{
  private AddressingVersion av;
  private String wrapper;
  private String problemValue = null;
  
  public FaultDetailHeader(AddressingVersion paramAddressingVersion, String paramString, QName paramQName)
  {
    av = paramAddressingVersion;
    wrapper = paramString;
    problemValue = paramQName.toString();
  }
  
  public FaultDetailHeader(AddressingVersion paramAddressingVersion, String paramString1, String paramString2)
  {
    av = paramAddressingVersion;
    wrapper = paramString1;
    problemValue = paramString2;
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    return av.nsUri;
  }
  
  @NotNull
  public String getLocalPart()
  {
    return av.faultDetailTag.getLocalPart();
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
    paramXMLStreamWriter.writeStartElement("", av.faultDetailTag.getLocalPart(), av.faultDetailTag.getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(av.nsUri);
    paramXMLStreamWriter.writeStartElement("", wrapper, av.nsUri);
    paramXMLStreamWriter.writeCharacters(problemValue);
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndElement();
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (localSOAPHeader == null) {
      localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
    }
    SOAPHeaderElement localSOAPHeaderElement = localSOAPHeader.addHeaderElement(av.faultDetailTag);
    localSOAPHeaderElement = localSOAPHeader.addHeaderElement(new QName(av.nsUri, wrapper));
    localSOAPHeaderElement.addTextNode(problemValue);
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    String str1 = av.nsUri;
    String str2 = av.faultDetailTag.getLocalPart();
    paramContentHandler.startPrefixMapping("", str1);
    paramContentHandler.startElement(str1, str2, str2, EMPTY_ATTS);
    paramContentHandler.startElement(str1, wrapper, wrapper, EMPTY_ATTS);
    paramContentHandler.characters(problemValue.toCharArray(), 0, problemValue.length());
    paramContentHandler.endElement(str1, wrapper, wrapper);
    paramContentHandler.endElement(str1, str2, str2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\FaultDetailHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
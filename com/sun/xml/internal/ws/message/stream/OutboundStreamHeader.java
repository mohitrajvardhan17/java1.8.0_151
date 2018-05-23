package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class OutboundStreamHeader
  extends AbstractHeaderImpl
{
  private final XMLStreamBuffer infoset;
  private final String nsUri;
  private final String localName;
  private FinalArrayList<Attribute> attributes;
  private static final String TRUE_VALUE = "1";
  private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";
  
  public OutboundStreamHeader(XMLStreamBuffer paramXMLStreamBuffer, String paramString1, String paramString2)
  {
    infoset = paramXMLStreamBuffer;
    nsUri = paramString1;
    localName = paramString2;
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
  
  public String getAttribute(String paramString1, String paramString2)
  {
    if (attributes == null) {
      parseAttributes();
    }
    for (int i = attributes.size() - 1; i >= 0; i--)
    {
      Attribute localAttribute = (Attribute)attributes.get(i);
      if ((localName.equals(paramString2)) && (nsUri.equals(paramString1))) {
        return value;
      }
    }
    return null;
  }
  
  private void parseAttributes()
  {
    try
    {
      XMLStreamReader localXMLStreamReader = readHeader();
      attributes = new FinalArrayList();
      for (int i = 0; i < localXMLStreamReader.getAttributeCount(); i++)
      {
        String str1 = localXMLStreamReader.getAttributeLocalName(i);
        String str2 = localXMLStreamReader.getAttributeNamespace(i);
        String str3 = localXMLStreamReader.getAttributeValue(i);
        attributes.add(new Attribute(str2, str1, str3));
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException("Unable to read the attributes for {" + nsUri + "}" + localName + " header", localXMLStreamException);
    }
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    return infoset.readAsXMLStreamReader();
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    infoset.writeToXMLStreamWriter(paramXMLStreamWriter, true);
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
      infoset.writeTo(localSOAPHeader);
    }
    catch (XMLStreamBufferException localXMLStreamBufferException)
    {
      throw new SOAPException(localXMLStreamBufferException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    infoset.writeTo(paramContentHandler, paramErrorHandler);
  }
  
  static final class Attribute
  {
    final String nsUri;
    final String localName;
    final String value;
    
    public Attribute(String paramString1, String paramString2, String paramString3)
    {
      nsUri = fixNull(paramString1);
      localName = paramString2;
      value = paramString3;
    }
    
    private static String fixNull(String paramString)
    {
      if (paramString == null) {
        return "";
      }
      return paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\stream\OutboundStreamHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
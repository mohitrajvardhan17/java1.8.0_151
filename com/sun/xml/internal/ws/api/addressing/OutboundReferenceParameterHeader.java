package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class OutboundReferenceParameterHeader
  extends AbstractHeaderImpl
{
  private final XMLStreamBuffer infoset;
  private final String nsUri;
  private final String localName;
  private FinalArrayList<Attribute> attributes;
  private static final String TRUE_VALUE = "1";
  private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";
  
  OutboundReferenceParameterHeader(XMLStreamBuffer paramXMLStreamBuffer, String paramString1, String paramString2)
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
      localXMLStreamReader.nextTag();
      attributes = new FinalArrayList();
      int i = 0;
      for (int j = 0; j < localXMLStreamReader.getAttributeCount(); j++)
      {
        String str1 = localXMLStreamReader.getAttributeLocalName(j);
        String str2 = localXMLStreamReader.getAttributeNamespace(j);
        String str3 = localXMLStreamReader.getAttributeValue(j);
        if ((str2.equals(W3CnsUri)) && (str1.equals("IS_REFERENCE_PARAMETER"))) {
          i = 1;
        }
        attributes.add(new Attribute(str2, str1, str3));
      }
      if (i == 0) {
        attributes.add(new Attribute(W3CnsUri, "IsReferenceParameter", "1"));
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
    new StreamReaderDelegate(infoset.readAsXMLStreamReader())
    {
      int state = 0;
      
      public int next()
        throws XMLStreamException
      {
        return check(super.next());
      }
      
      public int nextTag()
        throws XMLStreamException
      {
        return check(super.nextTag());
      }
      
      private int check(int paramAnonymousInt)
      {
        switch (state)
        {
        case 0: 
          if (paramAnonymousInt == 1) {
            state = 1;
          }
          break;
        case 1: 
          state = 2;
          break;
        }
        return paramAnonymousInt;
      }
      
      public int getAttributeCount()
      {
        if (state == 1) {
          return super.getAttributeCount() + 1;
        }
        return super.getAttributeCount();
      }
      
      public String getAttributeLocalName(int paramAnonymousInt)
      {
        if ((state == 1) && (paramAnonymousInt == super.getAttributeCount())) {
          return "IsReferenceParameter";
        }
        return super.getAttributeLocalName(paramAnonymousInt);
      }
      
      public String getAttributeNamespace(int paramAnonymousInt)
      {
        if ((state == 1) && (paramAnonymousInt == super.getAttributeCount())) {
          return W3CnsUri;
        }
        return super.getAttributeNamespace(paramAnonymousInt);
      }
      
      public String getAttributePrefix(int paramAnonymousInt)
      {
        if ((state == 1) && (paramAnonymousInt == super.getAttributeCount())) {
          return "wsa";
        }
        return super.getAttributePrefix(paramAnonymousInt);
      }
      
      public String getAttributeType(int paramAnonymousInt)
      {
        if ((state == 1) && (paramAnonymousInt == super.getAttributeCount())) {
          return "CDATA";
        }
        return super.getAttributeType(paramAnonymousInt);
      }
      
      public String getAttributeValue(int paramAnonymousInt)
      {
        if ((state == 1) && (paramAnonymousInt == super.getAttributeCount())) {
          return "1";
        }
        return super.getAttributeValue(paramAnonymousInt);
      }
      
      public QName getAttributeName(int paramAnonymousInt)
      {
        if ((state == 1) && (paramAnonymousInt == super.getAttributeCount())) {
          return new QName(W3CnsUri, "IsReferenceParameter", "wsa");
        }
        return super.getAttributeName(paramAnonymousInt);
      }
      
      public String getAttributeValue(String paramAnonymousString1, String paramAnonymousString2)
      {
        if ((state == 1) && (paramAnonymousString2.equals("IsReferenceParameter")) && (paramAnonymousString1.equals(W3CnsUri))) {
          return "1";
        }
        return super.getAttributeValue(paramAnonymousString1, paramAnonymousString2);
      }
    };
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(paramXMLStreamWriter)
    {
      private boolean root = true;
      private boolean onRootEl = true;
      
      public void writeStartElement(String paramAnonymousString)
        throws XMLStreamException
      {
        super.writeStartElement(paramAnonymousString);
        writeAddedAttribute();
      }
      
      private void writeAddedAttribute()
        throws XMLStreamException
      {
        if (!root)
        {
          onRootEl = false;
          return;
        }
        root = false;
        writeNamespace("wsa", W3CnsUri);
        super.writeAttribute("wsa", W3CnsUri, "IsReferenceParameter", "1");
      }
      
      public void writeStartElement(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        super.writeStartElement(paramAnonymousString1, paramAnonymousString2);
        writeAddedAttribute();
      }
      
      public void writeStartElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws XMLStreamException
      {
        boolean bool = isPrefixDeclared(paramAnonymousString1, paramAnonymousString3);
        super.writeStartElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
        if ((!bool) && (!paramAnonymousString1.equals(""))) {
          super.writeNamespace(paramAnonymousString1, paramAnonymousString3);
        }
        writeAddedAttribute();
      }
      
      public void writeNamespace(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        if (!isPrefixDeclared(paramAnonymousString1, paramAnonymousString2)) {
          super.writeNamespace(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void writeAttribute(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4)
        throws XMLStreamException
      {
        if ((onRootEl) && (paramAnonymousString2.equals(W3CnsUri)) && (paramAnonymousString3.equals("IsReferenceParameter"))) {
          return;
        }
        writer.writeAttribute(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3, paramAnonymousString4);
      }
      
      public void writeAttribute(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws XMLStreamException
      {
        writer.writeAttribute(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
      }
      
      private boolean isPrefixDeclared(String paramAnonymousString1, String paramAnonymousString2)
      {
        return paramAnonymousString2.equals(getNamespaceContext().getNamespaceURI(paramAnonymousString1));
      }
    }, true);
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
      Element localElement = (Element)infoset.writeTo(localSOAPHeader);
      localElement.setAttributeNS(W3CnsUri, AddressingVersion.W3C.getPrefix() + ":" + "IsReferenceParameter", "1");
    }
    catch (XMLStreamBufferException localXMLStreamBufferException)
    {
      throw new SOAPException(localXMLStreamBufferException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    infoset.writeTo(new XMLFilterImpl()
    {
      private int depth = 0;
      
      public void startElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, Attributes paramAnonymousAttributes)
        throws SAXException
      {
        if (depth++ == 0)
        {
          super.startPrefixMapping("wsa", W3CnsUri);
          if (paramAnonymousAttributes.getIndex(W3CnsUri, "IsReferenceParameter") == -1)
          {
            AttributesImpl localAttributesImpl = new AttributesImpl(paramAnonymousAttributes);
            localAttributesImpl.addAttribute(W3CnsUri, "IsReferenceParameter", "wsa:IsReferenceParameter", "CDATA", "1");
            paramAnonymousAttributes = localAttributesImpl;
          }
        }
        super.startElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3, paramAnonymousAttributes);
      }
      
      public void endElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws SAXException
      {
        super.endElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
        if (--depth == 0) {
          super.endPrefixMapping("wsa");
        }
      }
    }, paramErrorHandler);
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
      return paramString == null ? "" : paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\addressing\OutboundReferenceParameterHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
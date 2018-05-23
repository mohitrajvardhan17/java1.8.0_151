package com.sun.istack.internal;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLStreamReaderToContentHandler
{
  private final XMLStreamReader staxStreamReader;
  private final ContentHandler saxHandler;
  private final boolean eagerQuit;
  private final boolean fragment;
  private final String[] inscopeNamespaces;
  
  public XMLStreamReaderToContentHandler(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramXMLStreamReader, paramContentHandler, paramBoolean1, paramBoolean2, new String[0]);
  }
  
  public XMLStreamReaderToContentHandler(XMLStreamReader paramXMLStreamReader, ContentHandler paramContentHandler, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString)
  {
    staxStreamReader = paramXMLStreamReader;
    saxHandler = paramContentHandler;
    eagerQuit = paramBoolean1;
    fragment = paramBoolean2;
    inscopeNamespaces = paramArrayOfString;
    assert (paramArrayOfString.length % 2 == 0);
  }
  
  public void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      int j = staxStreamReader.getEventType();
      if (j == 7) {
        while (!staxStreamReader.isStartElement()) {
          j = staxStreamReader.next();
        }
      }
      if (j != 1) {
        throw new IllegalStateException("The current event is not START_ELEMENT\n but " + j);
      }
      handleStartDocument();
      for (int k = 0; k < inscopeNamespaces.length; k += 2) {
        saxHandler.startPrefixMapping(inscopeNamespaces[k], inscopeNamespaces[(k + 1)]);
      }
      do
      {
        switch (j)
        {
        case 1: 
          i++;
          handleStartElement();
          break;
        case 2: 
          handleEndElement();
          i--;
          if ((i != 0) || (!eagerQuit)) {
            break;
          }
          break;
        case 4: 
          handleCharacters();
          break;
        case 9: 
          handleEntityReference();
          break;
        case 3: 
          handlePI();
          break;
        case 5: 
          handleComment();
          break;
        case 11: 
          handleDTD();
          break;
        case 10: 
          handleAttribute();
          break;
        case 13: 
          handleNamespace();
          break;
        case 12: 
          handleCDATA();
          break;
        case 15: 
          handleEntityDecl();
          break;
        case 14: 
          handleNotationDecl();
          break;
        case 6: 
          handleSpace();
          break;
        case 7: 
        case 8: 
        default: 
          throw new InternalError("processing event: " + j);
        }
        j = staxStreamReader.next();
      } while (i != 0);
      for (k = 0; k < inscopeNamespaces.length; k += 2) {
        saxHandler.endPrefixMapping(inscopeNamespaces[k]);
      }
      handleEndDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException2(localSAXException);
    }
  }
  
  private void handleEndDocument()
    throws SAXException
  {
    if (fragment) {
      return;
    }
    saxHandler.endDocument();
  }
  
  private void handleStartDocument()
    throws SAXException
  {
    if (fragment) {
      return;
    }
    saxHandler.setDocumentLocator(new Locator()
    {
      public int getColumnNumber()
      {
        return staxStreamReader.getLocation().getColumnNumber();
      }
      
      public int getLineNumber()
      {
        return staxStreamReader.getLocation().getLineNumber();
      }
      
      public String getPublicId()
      {
        return staxStreamReader.getLocation().getPublicId();
      }
      
      public String getSystemId()
      {
        return staxStreamReader.getLocation().getSystemId();
      }
    });
    saxHandler.startDocument();
  }
  
  private void handlePI()
    throws XMLStreamException
  {
    try
    {
      saxHandler.processingInstruction(staxStreamReader.getPITarget(), staxStreamReader.getPIData());
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException2(localSAXException);
    }
  }
  
  private void handleCharacters()
    throws XMLStreamException
  {
    try
    {
      saxHandler.characters(staxStreamReader.getTextCharacters(), staxStreamReader.getTextStart(), staxStreamReader.getTextLength());
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException2(localSAXException);
    }
  }
  
  private void handleEndElement()
    throws XMLStreamException
  {
    QName localQName = staxStreamReader.getName();
    try
    {
      String str1 = localQName.getPrefix();
      String str2 = str1 + ':' + localQName.getLocalPart();
      saxHandler.endElement(localQName.getNamespaceURI(), localQName.getLocalPart(), str2);
      int i = staxStreamReader.getNamespaceCount();
      for (int j = i - 1; j >= 0; j--)
      {
        String str3 = staxStreamReader.getNamespacePrefix(j);
        if (str3 == null) {
          str3 = "";
        }
        saxHandler.endPrefixMapping(str3);
      }
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException2(localSAXException);
    }
  }
  
  private void handleStartElement()
    throws XMLStreamException
  {
    try
    {
      int i = staxStreamReader.getNamespaceCount();
      for (int j = 0; j < i; j++) {
        saxHandler.startPrefixMapping(fixNull(staxStreamReader.getNamespacePrefix(j)), fixNull(staxStreamReader.getNamespaceURI(j)));
      }
      QName localQName = staxStreamReader.getName();
      String str1 = localQName.getPrefix();
      String str2;
      if ((str1 == null) || (str1.length() == 0)) {
        str2 = localQName.getLocalPart();
      } else {
        str2 = str1 + ':' + localQName.getLocalPart();
      }
      Attributes localAttributes = getAttributes();
      saxHandler.startElement(localQName.getNamespaceURI(), localQName.getLocalPart(), str2, localAttributes);
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException2(localSAXException);
    }
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  private Attributes getAttributes()
  {
    AttributesImpl localAttributesImpl = new AttributesImpl();
    int i = staxStreamReader.getEventType();
    if ((i != 10) && (i != 1)) {
      throw new InternalError("getAttributes() attempting to process: " + i);
    }
    for (int j = 0; j < staxStreamReader.getAttributeCount(); j++)
    {
      String str1 = staxStreamReader.getAttributeNamespace(j);
      if (str1 == null) {
        str1 = "";
      }
      String str2 = staxStreamReader.getAttributeLocalName(j);
      String str3 = staxStreamReader.getAttributePrefix(j);
      String str4;
      if ((str3 == null) || (str3.length() == 0)) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      }
      String str5 = staxStreamReader.getAttributeType(j);
      String str6 = staxStreamReader.getAttributeValue(j);
      localAttributesImpl.addAttribute(str1, str2, str4, str5, str6);
    }
    return localAttributesImpl;
  }
  
  private void handleNamespace() {}
  
  private void handleAttribute() {}
  
  private void handleDTD() {}
  
  private void handleComment() {}
  
  private void handleEntityReference() {}
  
  private void handleSpace() {}
  
  private void handleNotationDecl() {}
  
  private void handleEntityDecl() {}
  
  private void handleCDATA() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\XMLStreamReaderToContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
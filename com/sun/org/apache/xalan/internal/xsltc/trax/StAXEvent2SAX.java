package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

public class StAXEvent2SAX
  implements XMLReader, Locator
{
  private final XMLEventReader staxEventReader;
  private ContentHandler _sax = null;
  private LexicalHandler _lex = null;
  private SAXImpl _saxImpl = null;
  private String version = null;
  private String encoding = null;
  
  public StAXEvent2SAX(XMLEventReader paramXMLEventReader)
  {
    staxEventReader = paramXMLEventReader;
  }
  
  public ContentHandler getContentHandler()
  {
    return _sax;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
    throws NullPointerException
  {
    _sax = paramContentHandler;
    if ((paramContentHandler instanceof LexicalHandler)) {
      _lex = ((LexicalHandler)paramContentHandler);
    }
    if ((paramContentHandler instanceof SAXImpl)) {
      _saxImpl = ((SAXImpl)paramContentHandler);
    }
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    try
    {
      bridge();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException(localXMLStreamException);
    }
  }
  
  public void parse()
    throws IOException, SAXException, XMLStreamException
  {
    bridge();
  }
  
  private void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      int j = 0;
      XMLEvent localXMLEvent = staxEventReader.peek();
      if ((!localXMLEvent.isStartDocument()) && (!localXMLEvent.isStartElement())) {
        throw new IllegalStateException();
      }
      if (localXMLEvent.getEventType() == 7)
      {
        j = 1;
        version = ((StartDocument)localXMLEvent).getVersion();
        if (((StartDocument)localXMLEvent).encodingSet()) {
          encoding = ((StartDocument)localXMLEvent).getCharacterEncodingScheme();
        }
        localXMLEvent = staxEventReader.nextEvent();
        localXMLEvent = staxEventReader.nextEvent();
      }
      handleStartDocument(localXMLEvent);
      while (localXMLEvent.getEventType() != 1)
      {
        switch (localXMLEvent.getEventType())
        {
        case 4: 
          handleCharacters(localXMLEvent.asCharacters());
          break;
        case 3: 
          handlePI((ProcessingInstruction)localXMLEvent);
          break;
        case 5: 
          handleComment();
          break;
        case 11: 
          handleDTD();
          break;
        case 6: 
          handleSpace();
          break;
        case 7: 
        case 8: 
        case 9: 
        case 10: 
        default: 
          throw new InternalError("processing prolog event: " + localXMLEvent);
        }
        localXMLEvent = staxEventReader.nextEvent();
      }
      do
      {
        switch (localXMLEvent.getEventType())
        {
        case 1: 
          i++;
          handleStartElement(localXMLEvent.asStartElement());
          break;
        case 2: 
          handleEndElement(localXMLEvent.asEndElement());
          i--;
          break;
        case 4: 
          handleCharacters(localXMLEvent.asCharacters());
          break;
        case 9: 
          handleEntityReference();
          break;
        case 3: 
          handlePI((ProcessingInstruction)localXMLEvent);
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
          throw new InternalError("processing event: " + localXMLEvent);
        }
        localXMLEvent = staxEventReader.nextEvent();
      } while (i != 0);
      if (j != 0) {
        while (localXMLEvent.getEventType() != 8)
        {
          switch (localXMLEvent.getEventType())
          {
          case 4: 
            handleCharacters(localXMLEvent.asCharacters());
            break;
          case 3: 
            handlePI((ProcessingInstruction)localXMLEvent);
            break;
          case 5: 
            handleComment();
            break;
          case 6: 
            handleSpace();
            break;
          default: 
            throw new InternalError("processing misc event after document element: " + localXMLEvent);
          }
          localXMLEvent = staxEventReader.nextEvent();
        }
      }
      handleEndDocument();
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleEndDocument()
    throws SAXException
  {
    _sax.endDocument();
  }
  
  private void handleStartDocument(final XMLEvent paramXMLEvent)
    throws SAXException
  {
    _sax.setDocumentLocator(new Locator2()
    {
      public int getColumnNumber()
      {
        return paramXMLEvent.getLocation().getColumnNumber();
      }
      
      public int getLineNumber()
      {
        return paramXMLEvent.getLocation().getLineNumber();
      }
      
      public String getPublicId()
      {
        return paramXMLEvent.getLocation().getPublicId();
      }
      
      public String getSystemId()
      {
        return paramXMLEvent.getLocation().getSystemId();
      }
      
      public String getXMLVersion()
      {
        return version;
      }
      
      public String getEncoding()
      {
        return encoding;
      }
    });
    _sax.startDocument();
  }
  
  private void handlePI(ProcessingInstruction paramProcessingInstruction)
    throws XMLStreamException
  {
    try
    {
      _sax.processingInstruction(paramProcessingInstruction.getTarget(), paramProcessingInstruction.getData());
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleCharacters(Characters paramCharacters)
    throws XMLStreamException
  {
    try
    {
      _sax.characters(paramCharacters.getData().toCharArray(), 0, paramCharacters.getData().length());
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleEndElement(EndElement paramEndElement)
    throws XMLStreamException
  {
    QName localQName = paramEndElement.getName();
    String str1 = "";
    if ((localQName.getPrefix() != null) && (localQName.getPrefix().trim().length() != 0)) {
      str1 = localQName.getPrefix() + ":";
    }
    str1 = str1 + localQName.getLocalPart();
    try
    {
      _sax.endElement(localQName.getNamespaceURI(), localQName.getLocalPart(), str1);
      Iterator localIterator = paramEndElement.getNamespaces();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (str2 == null) {
          str2 = "";
        }
        _sax.endPrefixMapping(str2);
      }
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private void handleStartElement(StartElement paramStartElement)
    throws XMLStreamException
  {
    try
    {
      Object localObject = paramStartElement.getNamespaces();
      while (((Iterator)localObject).hasNext())
      {
        str1 = ((Namespace)((Iterator)localObject).next()).getPrefix();
        if (str1 == null) {
          str1 = "";
        }
        _sax.startPrefixMapping(str1, paramStartElement.getNamespaceURI(str1));
      }
      localObject = paramStartElement.getName();
      String str1 = ((QName)localObject).getPrefix();
      String str2;
      if ((str1 == null) || (str1.length() == 0)) {
        str2 = ((QName)localObject).getLocalPart();
      } else {
        str2 = str1 + ':' + ((QName)localObject).getLocalPart();
      }
      Attributes localAttributes = getAttributes(paramStartElement);
      _sax.startElement(((QName)localObject).getNamespaceURI(), ((QName)localObject).getLocalPart(), str2, localAttributes);
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  private Attributes getAttributes(StartElement paramStartElement)
  {
    AttributesImpl localAttributesImpl = new AttributesImpl();
    if (!paramStartElement.isStartElement()) {
      throw new InternalError("getAttributes() attempting to process: " + paramStartElement);
    }
    Iterator localIterator = paramStartElement.getAttributes();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      String str1 = localAttribute.getName().getNamespaceURI();
      if (str1 == null) {
        str1 = "";
      }
      String str2 = localAttribute.getName().getLocalPart();
      String str3 = localAttribute.getName().getPrefix();
      String str4;
      if ((str3 == null) || (str3.length() == 0)) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      }
      String str5 = localAttribute.getDTDType();
      String str6 = localAttribute.getValue();
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
  
  public DTDHandler getDTDHandler()
  {
    return null;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return null;
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return false;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    throw new IOException("This method is not yet implemented.");
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
    throws NullPointerException
  {}
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
    throws NullPointerException
  {}
  
  public EntityResolver getEntityResolver()
  {
    return null;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
    throws NullPointerException
  {}
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return null;
  }
  
  public int getColumnNumber()
  {
    return 0;
  }
  
  public int getLineNumber()
  {
    return 0;
  }
  
  public String getPublicId()
  {
    return null;
  }
  
  public String getSystemId()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\StAXEvent2SAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
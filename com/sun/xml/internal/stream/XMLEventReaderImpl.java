package com.sun.xml.internal.stream;

import com.sun.xml.internal.stream.events.XMLEventAllocatorImpl;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class XMLEventReaderImpl
  implements XMLEventReader
{
  protected XMLStreamReader fXMLReader;
  protected XMLEventAllocator fXMLEventAllocator;
  private XMLEvent fPeekedEvent;
  private XMLEvent fLastEvent;
  
  public XMLEventReaderImpl(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    fXMLReader = paramXMLStreamReader;
    fXMLEventAllocator = ((XMLEventAllocator)paramXMLStreamReader.getProperty("javax.xml.stream.allocator"));
    if (fXMLEventAllocator == null) {
      fXMLEventAllocator = new XMLEventAllocatorImpl();
    }
    fPeekedEvent = fXMLEventAllocator.allocate(fXMLReader);
  }
  
  public boolean hasNext()
  {
    if (fPeekedEvent != null) {
      return true;
    }
    boolean bool = false;
    try
    {
      bool = fXMLReader.hasNext();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      return false;
    }
    return bool;
  }
  
  public XMLEvent nextEvent()
    throws XMLStreamException
  {
    if (fPeekedEvent != null)
    {
      fLastEvent = fPeekedEvent;
      fPeekedEvent = null;
      return fLastEvent;
    }
    if (fXMLReader.hasNext())
    {
      fXMLReader.next();
      return fLastEvent = fXMLEventAllocator.allocate(fXMLReader);
    }
    fLastEvent = null;
    throw new NoSuchElementException();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public void close()
    throws XMLStreamException
  {
    fXMLReader.close();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    if (fLastEvent.getEventType() != 1) {
      throw new XMLStreamException("parser must be on START_ELEMENT to read next text", fLastEvent.getLocation());
    }
    String str = null;
    if (fPeekedEvent != null)
    {
      XMLEvent localXMLEvent = fPeekedEvent;
      fPeekedEvent = null;
      int i = localXMLEvent.getEventType();
      if ((i == 4) || (i == 6) || (i == 12))
      {
        str = localXMLEvent.asCharacters().getData();
      }
      else if (i == 9)
      {
        str = ((EntityReference)localXMLEvent).getDeclaration().getReplacementText();
      }
      else if ((i != 5) && (i != 3))
      {
        if (i == 1) {
          throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", localXMLEvent.getLocation());
        }
        if (i == 2) {
          return "";
        }
      }
      StringBuffer localStringBuffer = new StringBuffer();
      if ((str != null) && (str.length() > 0)) {
        localStringBuffer.append(str);
      }
      for (localXMLEvent = nextEvent(); localXMLEvent.getEventType() != 2; localXMLEvent = nextEvent())
      {
        if ((i == 4) || (i == 6) || (i == 12))
        {
          str = localXMLEvent.asCharacters().getData();
        }
        else if (i == 9)
        {
          str = ((EntityReference)localXMLEvent).getDeclaration().getReplacementText();
        }
        else if ((i != 5) && (i != 3))
        {
          if (i == 8) {
            throw new XMLStreamException("unexpected end of document when reading element text content");
          }
          if (i == 1) {
            throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", localXMLEvent.getLocation());
          }
          throw new XMLStreamException("Unexpected event type " + i, localXMLEvent.getLocation());
        }
        if ((str != null) && (str.length() > 0)) {
          localStringBuffer.append(str);
        }
      }
      return localStringBuffer.toString();
    }
    str = fXMLReader.getElementText();
    fLastEvent = fXMLEventAllocator.allocate(fXMLReader);
    return str;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return fXMLReader.getProperty(paramString);
  }
  
  public XMLEvent nextTag()
    throws XMLStreamException
  {
    if (fPeekedEvent != null)
    {
      XMLEvent localXMLEvent = fPeekedEvent;
      fPeekedEvent = null;
      int i = localXMLEvent.getEventType();
      if (((localXMLEvent.isCharacters()) && (localXMLEvent.asCharacters().isWhiteSpace())) || (i == 3) || (i == 5) || (i == 7)) {
        localXMLEvent = nextEvent();
      }
      for (i = localXMLEvent.getEventType(); ((localXMLEvent.isCharacters()) && (localXMLEvent.asCharacters().isWhiteSpace())) || (i == 3) || (i == 5); i = localXMLEvent.getEventType()) {
        localXMLEvent = nextEvent();
      }
      if ((i != 1) && (i != 2)) {
        throw new XMLStreamException("expected start or end tag", localXMLEvent.getLocation());
      }
      return localXMLEvent;
    }
    fXMLReader.nextTag();
    return fLastEvent = fXMLEventAllocator.allocate(fXMLReader);
  }
  
  public Object next()
  {
    XMLEvent localXMLEvent = null;
    try
    {
      localXMLEvent = nextEvent();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      fLastEvent = null;
      NoSuchElementException localNoSuchElementException = new NoSuchElementException(localXMLStreamException.getMessage());
      localNoSuchElementException.initCause(localXMLStreamException.getCause());
      throw localNoSuchElementException;
    }
    return localXMLEvent;
  }
  
  public XMLEvent peek()
    throws XMLStreamException
  {
    if (fPeekedEvent != null) {
      return fPeekedEvent;
    }
    if (hasNext())
    {
      fXMLReader.next();
      fPeekedEvent = fXMLEventAllocator.allocate(fXMLReader);
      return fPeekedEvent;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\XMLEventReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
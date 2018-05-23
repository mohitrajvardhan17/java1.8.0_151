package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class StAXEventReader
  implements XMLEventReader
{
  protected XMLStreamReader _streamReader;
  protected XMLEventAllocator _eventAllocator;
  private XMLEvent _currentEvent;
  private XMLEvent[] events = new XMLEvent[3];
  private int size = 3;
  private int currentIndex = 0;
  private boolean hasEvent = false;
  
  public StAXEventReader(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    _streamReader = paramXMLStreamReader;
    _eventAllocator = ((XMLEventAllocator)paramXMLStreamReader.getProperty("javax.xml.stream.allocator"));
    if (_eventAllocator == null) {
      _eventAllocator = new StAXEventAllocatorBase();
    }
    if (_streamReader.hasNext())
    {
      _streamReader.next();
      _currentEvent = _eventAllocator.allocate(_streamReader);
      events[0] = _currentEvent;
      hasEvent = true;
    }
    else
    {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
    }
  }
  
  public boolean hasNext()
  {
    return hasEvent;
  }
  
  public XMLEvent nextEvent()
    throws XMLStreamException
  {
    XMLEvent localXMLEvent1 = null;
    XMLEvent localXMLEvent2 = null;
    if (hasEvent)
    {
      localXMLEvent1 = events[currentIndex];
      events[currentIndex] = null;
      if (_streamReader.hasNext())
      {
        _streamReader.next();
        localXMLEvent2 = _eventAllocator.allocate(_streamReader);
        if (++currentIndex == size) {
          currentIndex = 0;
        }
        events[currentIndex] = localXMLEvent2;
        hasEvent = true;
      }
      else
      {
        _currentEvent = null;
        hasEvent = false;
      }
      return localXMLEvent1;
    }
    throw new NoSuchElementException();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public void close()
    throws XMLStreamException
  {
    _streamReader.close();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    if (!hasEvent) {
      throw new NoSuchElementException();
    }
    if (!_currentEvent.isStartElement())
    {
      StAXDocumentParser localStAXDocumentParser = (StAXDocumentParser)_streamReader;
      return localStAXDocumentParser.getElementText(true);
    }
    return _streamReader.getElementText();
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return _streamReader.getProperty(paramString);
  }
  
  public XMLEvent nextTag()
    throws XMLStreamException
  {
    if (!hasEvent) {
      throw new NoSuchElementException();
    }
    StAXDocumentParser localStAXDocumentParser = (StAXDocumentParser)_streamReader;
    localStAXDocumentParser.nextTag(true);
    return _eventAllocator.allocate(_streamReader);
  }
  
  public Object next()
  {
    try
    {
      return nextEvent();
    }
    catch (XMLStreamException localXMLStreamException) {}
    return null;
  }
  
  public XMLEvent peek()
    throws XMLStreamException
  {
    if (!hasEvent) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
    }
    _currentEvent = events[currentIndex];
    return _currentEvent;
  }
  
  public void setAllocator(XMLEventAllocator paramXMLEventAllocator)
  {
    if (paramXMLEventAllocator == null) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullXMLEventAllocator"));
    }
    _eventAllocator = paramXMLEventAllocator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXEventReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
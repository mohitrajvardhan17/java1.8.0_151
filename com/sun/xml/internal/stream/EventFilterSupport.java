package com.sun.xml.internal.stream;

import java.util.NoSuchElementException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

public class EventFilterSupport
  extends EventReaderDelegate
{
  EventFilter fEventFilter;
  
  public EventFilterSupport(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter)
  {
    setParent(paramXMLEventReader);
    fEventFilter = paramEventFilter;
  }
  
  public Object next()
  {
    try
    {
      return nextEvent();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new NoSuchElementException();
    }
  }
  
  public boolean hasNext()
  {
    try
    {
      return peek() != null;
    }
    catch (XMLStreamException localXMLStreamException) {}
    return false;
  }
  
  public XMLEvent nextEvent()
    throws XMLStreamException
  {
    if (super.hasNext())
    {
      XMLEvent localXMLEvent = super.nextEvent();
      if (fEventFilter.accept(localXMLEvent)) {
        return localXMLEvent;
      }
      return nextEvent();
    }
    throw new NoSuchElementException();
  }
  
  public XMLEvent nextTag()
    throws XMLStreamException
  {
    if (super.hasNext())
    {
      XMLEvent localXMLEvent = super.nextTag();
      if (fEventFilter.accept(localXMLEvent)) {
        return localXMLEvent;
      }
      return nextTag();
    }
    throw new NoSuchElementException();
  }
  
  public XMLEvent peek()
    throws XMLStreamException
  {
    for (;;)
    {
      XMLEvent localXMLEvent = super.peek();
      if (localXMLEvent == null) {
        return null;
      }
      if (fEventFilter.accept(localXMLEvent)) {
        return localXMLEvent;
      }
      super.next();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\EventFilterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
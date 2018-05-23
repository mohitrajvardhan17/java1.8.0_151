package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class StAXFilteredEvent
  implements XMLEventReader
{
  private XMLEventReader eventReader;
  private EventFilter _filter;
  
  public StAXFilteredEvent() {}
  
  public StAXFilteredEvent(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter)
    throws XMLStreamException
  {
    eventReader = paramXMLEventReader;
    _filter = paramEventFilter;
  }
  
  public void setEventReader(XMLEventReader paramXMLEventReader)
  {
    eventReader = paramXMLEventReader;
  }
  
  public void setFilter(EventFilter paramEventFilter)
  {
    _filter = paramEventFilter;
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
  
  public XMLEvent nextEvent()
    throws XMLStreamException
  {
    if (hasNext()) {
      return eventReader.nextEvent();
    }
    return null;
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    XMLEvent localXMLEvent = nextEvent();
    if (!localXMLEvent.isStartElement()) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTART_ELEMENT"));
    }
    while (hasNext())
    {
      localXMLEvent = nextEvent();
      if (localXMLEvent.isStartElement()) {
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"));
      }
      if (localXMLEvent.isCharacters()) {
        localStringBuffer.append(((Characters)localXMLEvent).getData());
      }
      if (localXMLEvent.isEndElement()) {
        return localStringBuffer.toString();
      }
    }
    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.END_ELEMENTnotFound"));
  }
  
  public XMLEvent nextTag()
    throws XMLStreamException
  {
    while (hasNext())
    {
      XMLEvent localXMLEvent = nextEvent();
      if ((localXMLEvent.isStartElement()) || (localXMLEvent.isEndElement())) {
        return localXMLEvent;
      }
    }
    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.startOrEndNotFound"));
  }
  
  public boolean hasNext()
  {
    try
    {
      while (eventReader.hasNext())
      {
        if (_filter.accept(eventReader.peek())) {
          return true;
        }
        eventReader.nextEvent();
      }
      return false;
    }
    catch (XMLStreamException localXMLStreamException) {}
    return false;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public XMLEvent peek()
    throws XMLStreamException
  {
    if (hasNext()) {
      return eventReader.peek();
    }
    return null;
  }
  
  public void close()
    throws XMLStreamException
  {
    eventReader.close();
  }
  
  public Object getProperty(String paramString)
  {
    return eventReader.getProperty(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXFilteredEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
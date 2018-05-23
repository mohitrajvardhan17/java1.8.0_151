package javax.xml.stream.util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class EventReaderDelegate
  implements XMLEventReader
{
  private XMLEventReader reader;
  
  public EventReaderDelegate() {}
  
  public EventReaderDelegate(XMLEventReader paramXMLEventReader)
  {
    reader = paramXMLEventReader;
  }
  
  public void setParent(XMLEventReader paramXMLEventReader)
  {
    reader = paramXMLEventReader;
  }
  
  public XMLEventReader getParent()
  {
    return reader;
  }
  
  public XMLEvent nextEvent()
    throws XMLStreamException
  {
    return reader.nextEvent();
  }
  
  public Object next()
  {
    return reader.next();
  }
  
  public boolean hasNext()
  {
    return reader.hasNext();
  }
  
  public XMLEvent peek()
    throws XMLStreamException
  {
    return reader.peek();
  }
  
  public void close()
    throws XMLStreamException
  {
    reader.close();
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    return reader.getElementText();
  }
  
  public XMLEvent nextTag()
    throws XMLStreamException
  {
    return reader.nextTag();
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return reader.getProperty(paramString);
  }
  
  public void remove()
  {
    reader.remove();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\util\EventReaderDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
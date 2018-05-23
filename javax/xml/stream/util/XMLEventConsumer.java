package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public abstract interface XMLEventConsumer
{
  public abstract void add(XMLEvent paramXMLEvent)
    throws XMLStreamException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\util\XMLEventConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
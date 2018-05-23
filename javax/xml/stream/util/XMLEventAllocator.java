package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public abstract interface XMLEventAllocator
{
  public abstract XMLEventAllocator newInstance();
  
  public abstract XMLEvent allocate(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException;
  
  public abstract void allocate(XMLStreamReader paramXMLStreamReader, XMLEventConsumer paramXMLEventConsumer)
    throws XMLStreamException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\util\XMLEventAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
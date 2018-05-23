package javax.xml.stream;

import javax.xml.stream.events.XMLEvent;

public abstract interface EventFilter
{
  public abstract boolean accept(XMLEvent paramXMLEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\EventFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

public abstract interface XMLEventWriter
  extends XMLEventConsumer
{
  public abstract void flush()
    throws XMLStreamException;
  
  public abstract void close()
    throws XMLStreamException;
  
  public abstract void add(XMLEvent paramXMLEvent)
    throws XMLStreamException;
  
  public abstract void add(XMLEventReader paramXMLEventReader)
    throws XMLStreamException;
  
  public abstract String getPrefix(String paramString)
    throws XMLStreamException;
  
  public abstract void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException;
  
  public abstract void setDefaultNamespace(String paramString)
    throws XMLStreamException;
  
  public abstract void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException;
  
  public abstract NamespaceContext getNamespaceContext();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\XMLEventWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.xml.stream.events;

import java.util.Iterator;
import javax.xml.namespace.QName;

public abstract interface EndElement
  extends XMLEvent
{
  public abstract QName getName();
  
  public abstract Iterator getNamespaces();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\EndElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
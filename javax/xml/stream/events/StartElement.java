package javax.xml.stream.events;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public abstract interface StartElement
  extends XMLEvent
{
  public abstract QName getName();
  
  public abstract Iterator getAttributes();
  
  public abstract Iterator getNamespaces();
  
  public abstract Attribute getAttributeByName(QName paramQName);
  
  public abstract NamespaceContext getNamespaceContext();
  
  public abstract String getNamespaceURI(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\StartElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
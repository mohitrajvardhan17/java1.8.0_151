package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public abstract interface Reference
{
  public abstract String getType();
  
  public abstract String getURI();
  
  public abstract void setURI(String paramString);
  
  public abstract Iterator<Element> getElementRetrievalInformation();
  
  public abstract void addElementRetrievalInformation(Element paramElement);
  
  public abstract void removeElementRetrievalInformation(Element paramElement);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.org.jvnet.staxex;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public abstract interface NamespaceContextEx
  extends NamespaceContext, Iterable<Binding>
{
  public abstract Iterator<Binding> iterator();
  
  public static abstract interface Binding
  {
    public abstract String getPrefix();
    
    public abstract String getNamespaceURI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\NamespaceContextEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.util.Vector;

public class NamespaceContextWrapper
  implements javax.xml.namespace.NamespaceContext
{
  private com.sun.org.apache.xerces.internal.xni.NamespaceContext fNamespaceContext;
  
  public NamespaceContextWrapper(NamespaceSupport paramNamespaceSupport)
  {
    fNamespaceContext = paramNamespaceSupport;
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Prefix can't be null");
    }
    return fNamespaceContext.getURI(paramString.intern());
  }
  
  public String getPrefix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("URI can't be null.");
    }
    return fNamespaceContext.getPrefix(paramString.intern());
  }
  
  public Iterator getPrefixes(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("URI can't be null.");
    }
    Vector localVector = ((NamespaceSupport)fNamespaceContext).getPrefixes(paramString.intern());
    return localVector.iterator();
  }
  
  public com.sun.org.apache.xerces.internal.xni.NamespaceContext getNamespaceContext()
  {
    return fNamespaceContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\NamespaceContextWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
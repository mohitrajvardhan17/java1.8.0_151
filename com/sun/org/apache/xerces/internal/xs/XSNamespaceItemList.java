package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public abstract interface XSNamespaceItemList
  extends List
{
  public abstract int getLength();
  
  public abstract XSNamespaceItem item(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSNamespaceItemList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
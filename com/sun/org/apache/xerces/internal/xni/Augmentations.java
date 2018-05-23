package com.sun.org.apache.xerces.internal.xni;

import java.util.Enumeration;

public abstract interface Augmentations
{
  public abstract Object putItem(String paramString, Object paramObject);
  
  public abstract Object getItem(String paramString);
  
  public abstract Object removeItem(String paramString);
  
  public abstract Enumeration keys();
  
  public abstract void removeAllItems();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\Augmentations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
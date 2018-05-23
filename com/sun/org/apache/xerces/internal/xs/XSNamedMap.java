package com.sun.org.apache.xerces.internal.xs;

import java.util.Map;

public abstract interface XSNamedMap
  extends Map
{
  public abstract int getLength();
  
  public abstract XSObject item(int paramInt);
  
  public abstract XSObject itemByName(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSNamedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
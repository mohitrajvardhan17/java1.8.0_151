package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public abstract interface ValueStore
{
  public abstract void addValue(Field paramField, Object paramObject, short paramShort, ShortList paramShortList);
  
  public abstract void reportError(String paramString, Object[] paramArrayOfObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\ValueStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
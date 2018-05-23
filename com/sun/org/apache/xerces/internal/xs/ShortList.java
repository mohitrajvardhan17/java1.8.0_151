package com.sun.org.apache.xerces.internal.xs;

import java.util.List;

public abstract interface ShortList
  extends List
{
  public abstract int getLength();
  
  public abstract boolean contains(short paramShort);
  
  public abstract short item(int paramInt)
    throws XSException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\ShortList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.util.List;

public abstract interface ObjectList
  extends List
{
  public abstract int getLength();
  
  public abstract boolean contains(Object paramObject);
  
  public abstract Object item(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\ObjectList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
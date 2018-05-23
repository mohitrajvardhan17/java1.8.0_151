package com.sun.org.apache.xerces.internal.xs.datatypes;

import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.List;

public abstract interface ByteList
  extends List
{
  public abstract int getLength();
  
  public abstract boolean contains(byte paramByte);
  
  public abstract byte item(int paramInt)
    throws XSException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\ByteList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.xerces.internal.impl.dv.util;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;
import java.util.AbstractList;

public class ByteListImpl
  extends AbstractList
  implements ByteList
{
  protected final byte[] data;
  protected String canonical;
  
  public ByteListImpl(byte[] paramArrayOfByte)
  {
    data = paramArrayOfByte;
  }
  
  public int getLength()
  {
    return data.length;
  }
  
  public boolean contains(byte paramByte)
  {
    for (int i = 0; i < data.length; i++) {
      if (data[i] == paramByte) {
        return true;
      }
    }
    return false;
  }
  
  public byte item(int paramInt)
    throws XSException
  {
    if ((paramInt < 0) || (paramInt > data.length - 1)) {
      throw new XSException((short)2, null);
    }
    return data[paramInt];
  }
  
  public Object get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < data.length)) {
      return new Byte(data[paramInt]);
    }
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size()
  {
    return getLength();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\util\ByteListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
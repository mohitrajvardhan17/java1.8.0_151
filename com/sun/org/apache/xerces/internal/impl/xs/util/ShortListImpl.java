package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.AbstractList;

public final class ShortListImpl
  extends AbstractList
  implements ShortList
{
  public static final ShortListImpl EMPTY_LIST = new ShortListImpl(new short[0], 0);
  private final short[] fArray;
  private final int fLength;
  
  public ShortListImpl(short[] paramArrayOfShort, int paramInt)
  {
    fArray = paramArrayOfShort;
    fLength = paramInt;
  }
  
  public int getLength()
  {
    return fLength;
  }
  
  public boolean contains(short paramShort)
  {
    for (int i = 0; i < fLength; i++) {
      if (fArray[i] == paramShort) {
        return true;
      }
    }
    return false;
  }
  
  public short item(int paramInt)
    throws XSException
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      throw new XSException((short)2, null);
    }
    return fArray[paramInt];
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof ShortList))) {
      return false;
    }
    ShortList localShortList = (ShortList)paramObject;
    if (fLength != localShortList.getLength()) {
      return false;
    }
    for (int i = 0; i < fLength; i++) {
      if (fArray[i] != localShortList.item(i)) {
        return false;
      }
    }
    return true;
  }
  
  public Object get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < fLength)) {
      return new Short(fArray[paramInt]);
    }
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size()
  {
    return getLength();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\ShortListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
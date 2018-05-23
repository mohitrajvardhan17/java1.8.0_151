package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.lang.reflect.Array;
import java.util.AbstractList;

public final class ObjectListImpl
  extends AbstractList
  implements ObjectList
{
  public static final ObjectListImpl EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
  private final Object[] fArray;
  private final int fLength;
  
  public ObjectListImpl(Object[] paramArrayOfObject, int paramInt)
  {
    fArray = paramArrayOfObject;
    fLength = paramInt;
  }
  
  public int getLength()
  {
    return fLength;
  }
  
  public boolean contains(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      for (i = 0; i < fLength; i++) {
        if (fArray[i] == null) {
          return true;
        }
      }
    } else {
      for (i = 0; i < fLength; i++) {
        if (paramObject.equals(fArray[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  public Object item(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= fLength)) {
      return null;
    }
    return fArray[paramInt];
  }
  
  public Object get(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < fLength)) {
      return fArray[paramInt];
    }
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size()
  {
    return getLength();
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[fLength];
    toArray0(arrayOfObject);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length < fLength)
    {
      Class localClass1 = paramArrayOfObject.getClass();
      Class localClass2 = localClass1.getComponentType();
      paramArrayOfObject = (Object[])Array.newInstance(localClass2, fLength);
    }
    toArray0(paramArrayOfObject);
    if (paramArrayOfObject.length > fLength) {
      paramArrayOfObject[fLength] = null;
    }
    return paramArrayOfObject;
  }
  
  private void toArray0(Object[] paramArrayOfObject)
  {
    if (fLength > 0) {
      System.arraycopy(fArray, 0, paramArrayOfObject, 0, fLength);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\ObjectListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
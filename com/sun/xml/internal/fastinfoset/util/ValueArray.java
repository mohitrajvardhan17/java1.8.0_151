package com.sun.xml.internal.fastinfoset.util;

public abstract class ValueArray
{
  public static final int DEFAULT_CAPACITY = 10;
  public static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;
  protected int _size;
  protected int _readOnlyArraySize;
  protected int _maximumCapacity;
  
  public ValueArray() {}
  
  public int getSize()
  {
    return _size;
  }
  
  public int getMaximumCapacity()
  {
    return _maximumCapacity;
  }
  
  public void setMaximumCapacity(int paramInt)
  {
    _maximumCapacity = paramInt;
  }
  
  public abstract void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean);
  
  public abstract void clear();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\ValueArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
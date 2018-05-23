package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayArray
  extends ValueArray
{
  private CharArray[] _array;
  private CharArrayArray _readOnlyArray;
  
  public CharArrayArray(int paramInt1, int paramInt2)
  {
    _array = new CharArray[paramInt1];
    _maximumCapacity = paramInt2;
  }
  
  public CharArrayArray()
  {
    this(10, Integer.MAX_VALUE);
  }
  
  public final void clear()
  {
    for (int i = 0; i < _size; i++) {
      _array[i] = null;
    }
    _size = 0;
  }
  
  public final CharArray[] getArray()
  {
    if (_array == null) {
      return null;
    }
    CharArray[] arrayOfCharArray = new CharArray[_array.length];
    System.arraycopy(_array, 0, arrayOfCharArray, 0, _array.length);
    return arrayOfCharArray;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean)
  {
    if (!(paramValueArray instanceof CharArrayArray)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray }));
    }
    setReadOnlyArray((CharArrayArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(CharArrayArray paramCharArrayArray, boolean paramBoolean)
  {
    if (paramCharArrayArray != null)
    {
      _readOnlyArray = paramCharArrayArray;
      _readOnlyArraySize = paramCharArrayArray.getSize();
      if (paramBoolean) {
        clear();
      }
    }
  }
  
  public final CharArray get(int paramInt)
  {
    if (_readOnlyArray == null) {
      return _array[paramInt];
    }
    if (paramInt < _readOnlyArraySize) {
      return _readOnlyArray.get(paramInt);
    }
    return _array[(paramInt - _readOnlyArraySize)];
  }
  
  public final void add(CharArray paramCharArray)
  {
    if (_size == _array.length) {
      resize();
    }
    _array[(_size++)] = paramCharArray;
  }
  
  protected final void resize()
  {
    if (_size == _maximumCapacity) {
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
    }
    int i = _size * 3 / 2 + 1;
    if (i > _maximumCapacity) {
      i = _maximumCapacity;
    }
    CharArray[] arrayOfCharArray = new CharArray[i];
    System.arraycopy(_array, 0, arrayOfCharArray, 0, _size);
    _array = arrayOfCharArray;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\CharArrayArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
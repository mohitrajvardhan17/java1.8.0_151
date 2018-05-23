package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringArray
  extends ValueArray
{
  public String[] _array;
  private StringArray _readOnlyArray;
  private boolean _clear;
  
  public StringArray(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    _array = new String[paramInt1];
    _maximumCapacity = paramInt2;
    _clear = paramBoolean;
  }
  
  public StringArray()
  {
    this(10, Integer.MAX_VALUE, false);
  }
  
  public final void clear()
  {
    if (_clear) {
      for (int i = _readOnlyArraySize; i < _size; i++) {
        _array[i] = null;
      }
    }
    _size = _readOnlyArraySize;
  }
  
  public final String[] getArray()
  {
    if (_array == null) {
      return null;
    }
    String[] arrayOfString = new String[_array.length];
    System.arraycopy(_array, 0, arrayOfString, 0, _array.length);
    return arrayOfString;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean)
  {
    if (!(paramValueArray instanceof StringArray)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray }));
    }
    setReadOnlyArray((StringArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(StringArray paramStringArray, boolean paramBoolean)
  {
    if (paramStringArray != null)
    {
      _readOnlyArray = paramStringArray;
      _readOnlyArraySize = paramStringArray.getSize();
      if (paramBoolean) {
        clear();
      }
      _array = getCompleteArray();
      _size = _readOnlyArraySize;
    }
  }
  
  public final String[] getCompleteArray()
  {
    if (_readOnlyArray == null) {
      return getArray();
    }
    String[] arrayOfString1 = _readOnlyArray.getCompleteArray();
    String[] arrayOfString2 = new String[_readOnlyArraySize + _array.length];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, _readOnlyArraySize);
    return arrayOfString2;
  }
  
  public final String get(int paramInt)
  {
    return _array[paramInt];
  }
  
  public final int add(String paramString)
  {
    if (_size == _array.length) {
      resize();
    }
    _array[(_size++)] = paramString;
    return _size;
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
    String[] arrayOfString = new String[i];
    System.arraycopy(_array, 0, arrayOfString, 0, _size);
    _array = arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\StringArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
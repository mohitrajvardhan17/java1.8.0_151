package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class ContiguousCharArrayArray
  extends ValueArray
{
  public static final int INITIAL_CHARACTER_SIZE = 512;
  public static final int MAXIMUM_CHARACTER_SIZE = Integer.MAX_VALUE;
  protected int _maximumCharacterSize;
  public int[] _offset;
  public int[] _length;
  public char[] _array;
  public int _arrayIndex;
  public int _readOnlyArrayIndex;
  private String[] _cachedStrings;
  public int _cachedIndex;
  private ContiguousCharArrayArray _readOnlyArray;
  
  public ContiguousCharArrayArray(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    _offset = new int[paramInt1];
    _length = new int[paramInt1];
    _array = new char[paramInt3];
    _maximumCapacity = paramInt2;
    _maximumCharacterSize = paramInt4;
  }
  
  public ContiguousCharArrayArray()
  {
    this(10, Integer.MAX_VALUE, 512, Integer.MAX_VALUE);
  }
  
  public final void clear()
  {
    _arrayIndex = _readOnlyArrayIndex;
    _size = _readOnlyArraySize;
    if (_cachedStrings != null) {
      for (int i = _readOnlyArraySize; i < _cachedStrings.length; i++) {
        _cachedStrings[i] = null;
      }
    }
  }
  
  public final int getArrayIndex()
  {
    return _arrayIndex;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean)
  {
    if (!(paramValueArray instanceof ContiguousCharArrayArray)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray }));
    }
    setReadOnlyArray((ContiguousCharArrayArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(ContiguousCharArrayArray paramContiguousCharArrayArray, boolean paramBoolean)
  {
    if (paramContiguousCharArrayArray != null)
    {
      _readOnlyArray = paramContiguousCharArrayArray;
      _readOnlyArraySize = paramContiguousCharArrayArray.getSize();
      _readOnlyArrayIndex = paramContiguousCharArrayArray.getArrayIndex();
      if (paramBoolean) {
        clear();
      }
      _array = getCompleteCharArray();
      _offset = getCompleteOffsetArray();
      _length = getCompleteLengthArray();
      _size = _readOnlyArraySize;
      _arrayIndex = _readOnlyArrayIndex;
    }
  }
  
  public final char[] getCompleteCharArray()
  {
    if (_readOnlyArray == null)
    {
      if (_array == null) {
        return null;
      }
      arrayOfChar1 = new char[_array.length];
      System.arraycopy(_array, 0, arrayOfChar1, 0, _array.length);
      return arrayOfChar1;
    }
    char[] arrayOfChar1 = _readOnlyArray.getCompleteCharArray();
    char[] arrayOfChar2 = new char[_readOnlyArrayIndex + _array.length];
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, _readOnlyArrayIndex);
    return arrayOfChar2;
  }
  
  public final int[] getCompleteOffsetArray()
  {
    if (_readOnlyArray == null)
    {
      if (_offset == null) {
        return null;
      }
      arrayOfInt1 = new int[_offset.length];
      System.arraycopy(_offset, 0, arrayOfInt1, 0, _offset.length);
      return arrayOfInt1;
    }
    int[] arrayOfInt1 = _readOnlyArray.getCompleteOffsetArray();
    int[] arrayOfInt2 = new int[_readOnlyArraySize + _offset.length];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, _readOnlyArraySize);
    return arrayOfInt2;
  }
  
  public final int[] getCompleteLengthArray()
  {
    if (_readOnlyArray == null)
    {
      if (_length == null) {
        return null;
      }
      arrayOfInt1 = new int[_length.length];
      System.arraycopy(_length, 0, arrayOfInt1, 0, _length.length);
      return arrayOfInt1;
    }
    int[] arrayOfInt1 = _readOnlyArray.getCompleteLengthArray();
    int[] arrayOfInt2 = new int[_readOnlyArraySize + _length.length];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, _readOnlyArraySize);
    return arrayOfInt2;
  }
  
  public final String getString(int paramInt)
  {
    if ((_cachedStrings != null) && (paramInt < _cachedStrings.length))
    {
      localObject = _cachedStrings[paramInt];
      return (String)(localObject != null ? localObject : (_cachedStrings[paramInt] = new String(_array, _offset[paramInt], _length[paramInt])));
    }
    Object localObject = new String[_offset.length];
    if ((_cachedStrings != null) && (paramInt >= _cachedStrings.length)) {
      System.arraycopy(_cachedStrings, 0, localObject, 0, _cachedStrings.length);
    }
    _cachedStrings = ((String[])localObject);
    return _cachedStrings[paramInt] = new String(_array, _offset[paramInt], _length[paramInt]);
  }
  
  public final void ensureSize(int paramInt)
  {
    if (_arrayIndex + paramInt >= _array.length) {
      resizeArray(_arrayIndex + paramInt);
    }
  }
  
  public final void add(int paramInt)
  {
    if (_size == _offset.length) {
      resize();
    }
    _cachedIndex = _size;
    _offset[_size] = _arrayIndex;
    _length[(_size++)] = paramInt;
    _arrayIndex += paramInt;
  }
  
  public final int add(char[] paramArrayOfChar, int paramInt)
  {
    if (_size == _offset.length) {
      resize();
    }
    int i = _arrayIndex;
    int j = i + paramInt;
    _cachedIndex = _size;
    _offset[_size] = i;
    _length[(_size++)] = paramInt;
    if (j >= _array.length) {
      resizeArray(j);
    }
    System.arraycopy(paramArrayOfChar, 0, _array, i, paramInt);
    _arrayIndex = j;
    return i;
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
    int[] arrayOfInt1 = new int[i];
    System.arraycopy(_offset, 0, arrayOfInt1, 0, _size);
    _offset = arrayOfInt1;
    int[] arrayOfInt2 = new int[i];
    System.arraycopy(_length, 0, arrayOfInt2, 0, _size);
    _length = arrayOfInt2;
  }
  
  protected final void resizeArray(int paramInt)
  {
    if (_arrayIndex == _maximumCharacterSize) {
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.maxNumberOfCharacters"));
    }
    int i = paramInt * 3 / 2 + 1;
    if (i > _maximumCharacterSize) {
      i = _maximumCharacterSize;
    }
    char[] arrayOfChar = new char[i];
    System.arraycopy(_array, 0, arrayOfChar, 0, _arrayIndex);
    _array = arrayOfChar;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\ContiguousCharArrayArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
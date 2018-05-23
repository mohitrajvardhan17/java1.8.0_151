package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayIntMap
  extends KeyIntMap
{
  private CharArrayIntMap _readOnlyMap;
  protected int _totalCharacterCount;
  private Entry[] _table = new Entry[_capacity];
  
  public CharArrayIntMap(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public CharArrayIntMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public CharArrayIntMap()
  {
    this(16, 0.75F);
  }
  
  public final void clear()
  {
    for (int i = 0; i < _table.length; i++) {
      _table[i] = null;
    }
    _size = 0;
    _totalCharacterCount = 0;
  }
  
  public final void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean)
  {
    if (!(paramKeyIntMap instanceof CharArrayIntMap)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap }));
    }
    setReadOnlyMap((CharArrayIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean)
  {
    _readOnlyMap = paramCharArrayIntMap;
    if (_readOnlyMap != null)
    {
      _readOnlyMapSize = _readOnlyMap.size();
      if (paramBoolean) {
        clear();
      }
    }
    else
    {
      _readOnlyMapSize = 0;
    }
  }
  
  public final int get(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = hashHash(CharArray.hashCode(paramArrayOfChar, paramInt1, paramInt2));
    return get(paramArrayOfChar, paramInt1, paramInt2, i);
  }
  
  public final int obtainIndex(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = hashHash(CharArray.hashCode(paramArrayOfChar, paramInt1, paramInt2));
    if (_readOnlyMap != null)
    {
      j = _readOnlyMap.get(paramArrayOfChar, paramInt1, paramInt2, i);
      if (j != -1) {
        return j;
      }
    }
    int j = indexFor(i, _table.length);
    for (Object localObject = _table[j]; localObject != null; localObject = _next) {
      if ((_hash == i) && (((Entry)localObject).equalsCharArray(paramArrayOfChar, paramInt1, paramInt2))) {
        return _value;
      }
    }
    if (paramBoolean)
    {
      localObject = new char[paramInt2];
      System.arraycopy(paramArrayOfChar, paramInt1, localObject, 0, paramInt2);
      paramArrayOfChar = (char[])localObject;
      paramInt1 = 0;
    }
    addEntry(paramArrayOfChar, paramInt1, paramInt2, i, _size + _readOnlyMapSize, j);
    return -1;
  }
  
  public final int getTotalCharacterCount()
  {
    return _totalCharacterCount;
  }
  
  private final int get(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
  {
    if (_readOnlyMap != null)
    {
      i = _readOnlyMap.get(paramArrayOfChar, paramInt1, paramInt2, paramInt3);
      if (i != -1) {
        return i;
      }
    }
    int i = indexFor(paramInt3, _table.length);
    for (Entry localEntry = _table[i]; localEntry != null; localEntry = _next) {
      if ((_hash == paramInt3) && (localEntry.equalsCharArray(paramArrayOfChar, paramInt1, paramInt2))) {
        return _value;
      }
    }
    return -1;
  }
  
  private final void addEntry(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Entry localEntry = _table[paramInt5];
    _table[paramInt5] = new Entry(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4, localEntry);
    _totalCharacterCount += paramInt2;
    if (_size++ >= _threshold) {
      resize(2 * _table.length);
    }
  }
  
  private final void resize(int paramInt)
  {
    _capacity = paramInt;
    Entry[] arrayOfEntry1 = _table;
    int i = arrayOfEntry1.length;
    if (i == 1048576)
    {
      _threshold = Integer.MAX_VALUE;
      return;
    }
    Entry[] arrayOfEntry2 = new Entry[_capacity];
    transfer(arrayOfEntry2);
    _table = arrayOfEntry2;
    _threshold = ((int)(_capacity * _loadFactor));
  }
  
  private final void transfer(Entry[] paramArrayOfEntry)
  {
    Entry[] arrayOfEntry = _table;
    int i = paramArrayOfEntry.length;
    for (int j = 0; j < arrayOfEntry.length; j++)
    {
      Object localObject = arrayOfEntry[j];
      if (localObject != null)
      {
        arrayOfEntry[j] = null;
        do
        {
          Entry localEntry = _next;
          int k = indexFor(_hash, i);
          _next = paramArrayOfEntry[k];
          paramArrayOfEntry[k] = localObject;
          localObject = localEntry;
        } while (localObject != null);
      }
    }
  }
  
  static class Entry
    extends KeyIntMap.BaseEntry
  {
    final char[] _ch;
    final int _start;
    final int _length;
    Entry _next;
    
    public Entry(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Entry paramEntry)
    {
      super(paramInt4);
      _ch = paramArrayOfChar;
      _start = paramInt1;
      _length = paramInt2;
      _next = paramEntry;
    }
    
    public final boolean equalsCharArray(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      if (_length == paramInt2)
      {
        int i = _length;
        int j = _start;
        int k = paramInt1;
        while (i-- != 0) {
          if (_ch[(j++)] != paramArrayOfChar[(k++)]) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\CharArrayIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
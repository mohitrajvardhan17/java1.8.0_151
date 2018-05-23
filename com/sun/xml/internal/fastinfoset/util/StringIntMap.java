package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringIntMap
  extends KeyIntMap
{
  protected static final Entry NULL_ENTRY = new Entry(null, 0, -1, null);
  protected StringIntMap _readOnlyMap;
  protected Entry _lastEntry = NULL_ENTRY;
  protected Entry[] _table = new Entry[_capacity];
  protected int _index;
  protected int _totalCharacterCount;
  
  public StringIntMap(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public StringIntMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public StringIntMap()
  {
    this(16, 0.75F);
  }
  
  public void clear()
  {
    for (int i = 0; i < _table.length; i++) {
      _table[i] = null;
    }
    _lastEntry = NULL_ENTRY;
    _size = 0;
    _index = _readOnlyMapSize;
    _totalCharacterCount = 0;
  }
  
  public void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean)
  {
    if (!(paramKeyIntMap instanceof StringIntMap)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap }));
    }
    setReadOnlyMap((StringIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(StringIntMap paramStringIntMap, boolean paramBoolean)
  {
    _readOnlyMap = paramStringIntMap;
    if (_readOnlyMap != null)
    {
      _readOnlyMapSize = _readOnlyMap.size();
      _index = (_size + _readOnlyMapSize);
      if (paramBoolean) {
        clear();
      }
    }
    else
    {
      _readOnlyMapSize = 0;
      _index = _size;
    }
  }
  
  public final int getNextIndex()
  {
    return _index++;
  }
  
  public final int getIndex()
  {
    return _index;
  }
  
  public final int obtainIndex(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    if (_readOnlyMap != null)
    {
      j = _readOnlyMap.get(paramString, i);
      if (j != -1) {
        return j;
      }
    }
    int j = indexFor(i, _table.length);
    for (Entry localEntry = _table[j]; localEntry != null; localEntry = _next) {
      if ((_hash == i) && (eq(paramString, _key))) {
        return _value;
      }
    }
    addEntry(paramString, i, j);
    return -1;
  }
  
  public final void add(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, _table.length);
    addEntry(paramString, i, j);
  }
  
  public final int get(String paramString)
  {
    if (paramString == _lastEntry._key) {
      return _lastEntry._value;
    }
    return get(paramString, hashHash(paramString.hashCode()));
  }
  
  public final int getTotalCharacterCount()
  {
    return _totalCharacterCount;
  }
  
  private final int get(String paramString, int paramInt)
  {
    if (_readOnlyMap != null)
    {
      i = _readOnlyMap.get(paramString, paramInt);
      if (i != -1) {
        return i;
      }
    }
    int i = indexFor(paramInt, _table.length);
    for (Entry localEntry = _table[i]; localEntry != null; localEntry = _next) {
      if ((_hash == paramInt) && (eq(paramString, _key)))
      {
        _lastEntry = localEntry;
        return _value;
      }
    }
    return -1;
  }
  
  private final void addEntry(String paramString, int paramInt1, int paramInt2)
  {
    Entry localEntry = _table[paramInt2];
    _table[paramInt2] = new Entry(paramString, paramInt1, _index++, localEntry);
    _totalCharacterCount += paramString.length();
    if (_size++ >= _threshold) {
      resize(2 * _table.length);
    }
  }
  
  protected final void resize(int paramInt)
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
  
  private final boolean eq(String paramString1, String paramString2)
  {
    return (paramString1 == paramString2) || (paramString1.equals(paramString2));
  }
  
  protected static class Entry
    extends KeyIntMap.BaseEntry
  {
    final String _key;
    Entry _next;
    
    public Entry(String paramString, int paramInt1, int paramInt2, Entry paramEntry)
    {
      super(paramInt2);
      _key = paramString;
      _next = paramEntry;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\StringIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
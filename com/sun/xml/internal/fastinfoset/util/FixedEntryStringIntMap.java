package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class FixedEntryStringIntMap
  extends StringIntMap
{
  private StringIntMap.Entry _fixedEntry;
  
  public FixedEntryStringIntMap(String paramString, int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, _table.length);
    _table[j] = (_fixedEntry = new StringIntMap.Entry(paramString, i, _index++, null));
    if (_size++ >= _threshold) {
      resize(2 * _table.length);
    }
  }
  
  public FixedEntryStringIntMap(String paramString, int paramInt)
  {
    this(paramString, paramInt, 0.75F);
  }
  
  public FixedEntryStringIntMap(String paramString)
  {
    this(paramString, 16, 0.75F);
  }
  
  public final void clear()
  {
    for (int i = 0; i < _table.length; i++) {
      _table[i] = null;
    }
    _lastEntry = NULL_ENTRY;
    if (_fixedEntry != null)
    {
      i = indexFor(_fixedEntry._hash, _table.length);
      _table[i] = _fixedEntry;
      _fixedEntry._next = null;
      _size = 1;
      _index = (_readOnlyMapSize + 1);
    }
    else
    {
      _size = 0;
      _index = _readOnlyMapSize;
    }
  }
  
  public final void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean)
  {
    if (!(paramKeyIntMap instanceof FixedEntryStringIntMap)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap }));
    }
    setReadOnlyMap((FixedEntryStringIntMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(FixedEntryStringIntMap paramFixedEntryStringIntMap, boolean paramBoolean)
  {
    _readOnlyMap = paramFixedEntryStringIntMap;
    if (_readOnlyMap != null)
    {
      paramFixedEntryStringIntMap.removeFixedEntry();
      _readOnlyMapSize = paramFixedEntryStringIntMap.size();
      _index = (_readOnlyMapSize + _size);
      if (paramBoolean) {
        clear();
      }
    }
    else
    {
      _readOnlyMapSize = 0;
    }
  }
  
  private final void removeFixedEntry()
  {
    if (_fixedEntry != null)
    {
      int i = indexFor(_fixedEntry._hash, _table.length);
      StringIntMap.Entry localEntry1 = _table[i];
      if (localEntry1 == _fixedEntry)
      {
        _table[i] = _fixedEntry._next;
      }
      else
      {
        for (StringIntMap.Entry localEntry2 = localEntry1; _next != _fixedEntry; localEntry2 = _next) {}
        _next = _fixedEntry._next;
      }
      _fixedEntry = null;
      _size -= 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\FixedEntryStringIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
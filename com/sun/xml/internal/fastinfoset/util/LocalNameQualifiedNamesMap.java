package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;

public class LocalNameQualifiedNamesMap
  extends KeyIntMap
{
  private LocalNameQualifiedNamesMap _readOnlyMap;
  private int _index;
  private Entry[] _table = new Entry[_capacity];
  
  public LocalNameQualifiedNamesMap(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
  }
  
  public LocalNameQualifiedNamesMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public LocalNameQualifiedNamesMap()
  {
    this(16, 0.75F);
  }
  
  public final void clear()
  {
    for (int i = 0; i < _table.length; i++) {
      _table[i] = null;
    }
    _size = 0;
    if (_readOnlyMap != null) {
      _index = _readOnlyMap.getIndex();
    } else {
      _index = 0;
    }
  }
  
  public final void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean)
  {
    if (!(paramKeyIntMap instanceof LocalNameQualifiedNamesMap)) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramKeyIntMap }));
    }
    setReadOnlyMap((LocalNameQualifiedNamesMap)paramKeyIntMap, paramBoolean);
  }
  
  public final void setReadOnlyMap(LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap, boolean paramBoolean)
  {
    _readOnlyMap = paramLocalNameQualifiedNamesMap;
    if (_readOnlyMap != null)
    {
      _readOnlyMapSize = _readOnlyMap.size();
      _index = _readOnlyMap.getIndex();
      if (paramBoolean) {
        clear();
      }
    }
    else
    {
      _readOnlyMapSize = 0;
      _index = 0;
    }
  }
  
  public final boolean isQNameFromReadOnlyMap(QualifiedName paramQualifiedName)
  {
    return (_readOnlyMap != null) && (index <= _readOnlyMap.getIndex());
  }
  
  public final int getNextIndex()
  {
    return _index++;
  }
  
  public final int getIndex()
  {
    return _index;
  }
  
  public final Entry obtainEntry(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    if (_readOnlyMap != null)
    {
      Entry localEntry1 = _readOnlyMap.getEntry(paramString, i);
      if (localEntry1 != null) {
        return localEntry1;
      }
    }
    int j = indexFor(i, _table.length);
    for (Entry localEntry2 = _table[j]; localEntry2 != null; localEntry2 = _next) {
      if ((_hash == i) && (eq(paramString, _key))) {
        return localEntry2;
      }
    }
    return addEntry(paramString, i, j);
  }
  
  public final Entry obtainDynamicEntry(String paramString)
  {
    int i = hashHash(paramString.hashCode());
    int j = indexFor(i, _table.length);
    for (Entry localEntry = _table[j]; localEntry != null; localEntry = _next) {
      if ((_hash == i) && (eq(paramString, _key))) {
        return localEntry;
      }
    }
    return addEntry(paramString, i, j);
  }
  
  private final Entry getEntry(String paramString, int paramInt)
  {
    if (_readOnlyMap != null)
    {
      Entry localEntry1 = _readOnlyMap.getEntry(paramString, paramInt);
      if (localEntry1 != null) {
        return localEntry1;
      }
    }
    int i = indexFor(paramInt, _table.length);
    for (Entry localEntry2 = _table[i]; localEntry2 != null; localEntry2 = _next) {
      if ((_hash == paramInt) && (eq(paramString, _key))) {
        return localEntry2;
      }
    }
    return null;
  }
  
  private final Entry addEntry(String paramString, int paramInt1, int paramInt2)
  {
    Entry localEntry = _table[paramInt2];
    _table[paramInt2] = new Entry(paramString, paramInt1, localEntry);
    localEntry = _table[paramInt2];
    if (_size++ >= _threshold) {
      resize(2 * _table.length);
    }
    return localEntry;
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
  
  private final boolean eq(String paramString1, String paramString2)
  {
    return (paramString1 == paramString2) || (paramString1.equals(paramString2));
  }
  
  public static class Entry
  {
    final String _key;
    final int _hash;
    public QualifiedName[] _value;
    public int _valueIndex;
    Entry _next;
    
    public Entry(String paramString, int paramInt, Entry paramEntry)
    {
      _key = paramString;
      _hash = paramInt;
      _next = paramEntry;
      _value = new QualifiedName[1];
    }
    
    public void addQualifiedName(QualifiedName paramQualifiedName)
    {
      if (_valueIndex < _value.length)
      {
        _value[(_valueIndex++)] = paramQualifiedName;
      }
      else if (_valueIndex == _value.length)
      {
        QualifiedName[] arrayOfQualifiedName = new QualifiedName[_valueIndex * 3 / 2 + 1];
        System.arraycopy(_value, 0, arrayOfQualifiedName, 0, _valueIndex);
        _value = arrayOfQualifiedName;
        _value[(_valueIndex++)] = paramQualifiedName;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\LocalNameQualifiedNamesMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
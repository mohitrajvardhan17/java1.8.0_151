package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class CacheTable
{
  private boolean noReverseMap;
  static final int INITIAL_SIZE = 16;
  static final int MAX_SIZE = 1073741824;
  int size;
  int entryCount;
  private Entry[] map;
  private Entry[] rmap;
  private ORB orb;
  private ORBUtilSystemException wrapper;
  
  private CacheTable() {}
  
  public CacheTable(ORB paramORB, boolean paramBoolean)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    noReverseMap = paramBoolean;
    size = 16;
    entryCount = 0;
    initTables();
  }
  
  private void initTables()
  {
    map = new Entry[size];
    rmap = (noReverseMap ? null : new Entry[size]);
  }
  
  private void grow()
  {
    if (size == 1073741824) {
      return;
    }
    Entry[] arrayOfEntry = map;
    int i = size;
    size <<= 1;
    initTables();
    for (int j = 0; j < i; j++) {
      for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
        put_table(key, val);
      }
    }
  }
  
  private int moduloTableSize(int paramInt)
  {
    paramInt += (paramInt << 9 ^ 0xFFFFFFFF);
    paramInt ^= paramInt >>> 14;
    paramInt += (paramInt << 4);
    paramInt ^= paramInt >>> 10;
    return paramInt & size - 1;
  }
  
  private int hash(Object paramObject)
  {
    return moduloTableSize(System.identityHashCode(paramObject));
  }
  
  private int hash(int paramInt)
  {
    return moduloTableSize(paramInt);
  }
  
  public final void put(Object paramObject, int paramInt)
  {
    if (put_table(paramObject, paramInt))
    {
      entryCount += 1;
      if (entryCount > size * 3 / 4) {
        grow();
      }
    }
  }
  
  private boolean put_table(Object paramObject, int paramInt)
  {
    int i = hash(paramObject);
    for (Entry localEntry = map[i]; localEntry != null; localEntry = next) {
      if (key == paramObject)
      {
        if (val != paramInt) {
          throw wrapper.duplicateIndirectionOffset();
        }
        return false;
      }
    }
    localEntry = new Entry(paramObject, paramInt);
    next = map[i];
    map[i] = localEntry;
    if (!noReverseMap)
    {
      int j = hash(paramInt);
      rnext = rmap[j];
      rmap[j] = localEntry;
    }
    return true;
  }
  
  public final boolean containsKey(Object paramObject)
  {
    return getVal(paramObject) != -1;
  }
  
  public final int getVal(Object paramObject)
  {
    int i = hash(paramObject);
    for (Entry localEntry = map[i]; localEntry != null; localEntry = next) {
      if (key == paramObject) {
        return val;
      }
    }
    return -1;
  }
  
  public final boolean containsVal(int paramInt)
  {
    return getKey(paramInt) != null;
  }
  
  public final boolean containsOrderedVal(int paramInt)
  {
    return containsVal(paramInt);
  }
  
  public final Object getKey(int paramInt)
  {
    int i = hash(paramInt);
    for (Entry localEntry = rmap[i]; localEntry != null; localEntry = rnext) {
      if (val == paramInt) {
        return key;
      }
    }
    return null;
  }
  
  public void done()
  {
    map = null;
    rmap = null;
  }
  
  class Entry
  {
    Object key;
    int val;
    Entry next;
    Entry rnext;
    
    public Entry(Object paramObject, int paramInt)
    {
      key = paramObject;
      val = paramInt;
      next = null;
      rnext = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\CacheTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
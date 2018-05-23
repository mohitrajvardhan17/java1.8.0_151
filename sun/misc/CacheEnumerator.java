package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class CacheEnumerator
  implements Enumeration
{
  boolean keys;
  int index;
  CacheEntry[] table;
  CacheEntry entry;
  
  CacheEnumerator(CacheEntry[] paramArrayOfCacheEntry, boolean paramBoolean)
  {
    table = paramArrayOfCacheEntry;
    keys = paramBoolean;
    index = paramArrayOfCacheEntry.length;
  }
  
  public boolean hasMoreElements()
  {
    if (index >= 0)
    {
      while (entry != null)
      {
        if (entry.check() != null) {
          return true;
        }
        entry = entry.next;
      }
      while ((--index >= 0) && ((entry = table[index]) == null)) {}
    }
    return false;
  }
  
  public Object nextElement()
  {
    while (index >= 0)
    {
      while ((entry == null) && (--index >= 0) && ((entry = table[index]) == null)) {}
      if (entry != null)
      {
        CacheEntry localCacheEntry = entry;
        entry = next;
        if (localCacheEntry.check() != null) {
          return keys ? key : localCacheEntry.check();
        }
      }
    }
    throw new NoSuchElementException("CacheEnumerator");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\CacheEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
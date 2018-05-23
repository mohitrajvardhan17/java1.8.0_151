package sun.misc;

import java.util.Dictionary;
import java.util.Enumeration;

public class Cache
  extends Dictionary
{
  private CacheEntry[] table;
  private int count;
  private int threshold;
  private float loadFactor;
  
  private void init(int paramInt, float paramFloat)
  {
    if ((paramInt <= 0) || (paramFloat <= 0.0D)) {
      throw new IllegalArgumentException();
    }
    loadFactor = paramFloat;
    table = new CacheEntry[paramInt];
    threshold = ((int)(paramInt * paramFloat));
  }
  
  public Cache(int paramInt, float paramFloat)
  {
    init(paramInt, paramFloat);
  }
  
  public Cache(int paramInt)
  {
    init(paramInt, 0.75F);
  }
  
  public Cache()
  {
    try
    {
      init(101, 0.75F);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new Error("panic");
    }
  }
  
  public int size()
  {
    return count;
  }
  
  public boolean isEmpty()
  {
    return count == 0;
  }
  
  public synchronized Enumeration keys()
  {
    return new CacheEnumerator(table, true);
  }
  
  public synchronized Enumeration elements()
  {
    return new CacheEnumerator(table, false);
  }
  
  public synchronized Object get(Object paramObject)
  {
    CacheEntry[] arrayOfCacheEntry = table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfCacheEntry.length;
    for (CacheEntry localCacheEntry = arrayOfCacheEntry[j]; localCacheEntry != null; localCacheEntry = next) {
      if ((hash == i) && (key.equals(paramObject))) {
        return localCacheEntry.check();
      }
    }
    return null;
  }
  
  protected void rehash()
  {
    int i = table.length;
    CacheEntry[] arrayOfCacheEntry1 = table;
    int j = i * 2 + 1;
    CacheEntry[] arrayOfCacheEntry2 = new CacheEntry[j];
    threshold = ((int)(j * loadFactor));
    table = arrayOfCacheEntry2;
    int k = i;
    while (k-- > 0)
    {
      CacheEntry localCacheEntry1 = arrayOfCacheEntry1[k];
      while (localCacheEntry1 != null)
      {
        CacheEntry localCacheEntry2 = localCacheEntry1;
        localCacheEntry1 = next;
        if (localCacheEntry2.check() != null)
        {
          int m = (hash & 0x7FFFFFFF) % j;
          next = arrayOfCacheEntry2[m];
          arrayOfCacheEntry2[m] = localCacheEntry2;
        }
        else
        {
          count -= 1;
        }
      }
    }
  }
  
  public synchronized Object put(Object paramObject1, Object paramObject2)
  {
    if (paramObject2 == null) {
      throw new NullPointerException();
    }
    CacheEntry[] arrayOfCacheEntry = table;
    int i = paramObject1.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfCacheEntry.length;
    Object localObject1 = null;
    for (CacheEntry localCacheEntry = arrayOfCacheEntry[j]; localCacheEntry != null; localCacheEntry = next)
    {
      if ((hash == i) && (key.equals(paramObject1)))
      {
        Object localObject2 = localCacheEntry.check();
        localCacheEntry.setThing(paramObject2);
        return localObject2;
      }
      if (localCacheEntry.check() == null) {
        localObject1 = localCacheEntry;
      }
    }
    if (count >= threshold)
    {
      rehash();
      return put(paramObject1, paramObject2);
    }
    if (localObject1 == null)
    {
      localObject1 = new CacheEntry();
      next = arrayOfCacheEntry[j];
      arrayOfCacheEntry[j] = localObject1;
      count += 1;
    }
    hash = i;
    key = paramObject1;
    ((CacheEntry)localObject1).setThing(paramObject2);
    return null;
  }
  
  public synchronized Object remove(Object paramObject)
  {
    CacheEntry[] arrayOfCacheEntry = table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfCacheEntry.length;
    CacheEntry localCacheEntry1 = arrayOfCacheEntry[j];
    CacheEntry localCacheEntry2 = null;
    while (localCacheEntry1 != null)
    {
      if ((hash == i) && (key.equals(paramObject)))
      {
        if (localCacheEntry2 != null) {
          next = next;
        } else {
          arrayOfCacheEntry[j] = next;
        }
        count -= 1;
        return localCacheEntry1.check();
      }
      localCacheEntry2 = localCacheEntry1;
      localCacheEntry1 = next;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
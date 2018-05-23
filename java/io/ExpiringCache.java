package java.io;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ExpiringCache
{
  private long millisUntilExpiration;
  private Map<String, Entry> map;
  private int queryCount;
  private int queryOverflow = 300;
  private int MAX_ENTRIES = 200;
  
  ExpiringCache()
  {
    this(30000L);
  }
  
  ExpiringCache(long paramLong)
  {
    millisUntilExpiration = paramLong;
    map = new LinkedHashMap()
    {
      protected boolean removeEldestEntry(Map.Entry<String, ExpiringCache.Entry> paramAnonymousEntry)
      {
        return size() > MAX_ENTRIES;
      }
    };
  }
  
  synchronized String get(String paramString)
  {
    if (++queryCount >= queryOverflow) {
      cleanup();
    }
    Entry localEntry = entryFor(paramString);
    if (localEntry != null) {
      return localEntry.val();
    }
    return null;
  }
  
  synchronized void put(String paramString1, String paramString2)
  {
    if (++queryCount >= queryOverflow) {
      cleanup();
    }
    Entry localEntry = entryFor(paramString1);
    if (localEntry != null)
    {
      localEntry.setTimestamp(System.currentTimeMillis());
      localEntry.setVal(paramString2);
    }
    else
    {
      map.put(paramString1, new Entry(System.currentTimeMillis(), paramString2));
    }
  }
  
  synchronized void clear()
  {
    map.clear();
  }
  
  private Entry entryFor(String paramString)
  {
    Entry localEntry = (Entry)map.get(paramString);
    if (localEntry != null)
    {
      long l = System.currentTimeMillis() - localEntry.timestamp();
      if ((l < 0L) || (l >= millisUntilExpiration))
      {
        map.remove(paramString);
        localEntry = null;
      }
    }
    return localEntry;
  }
  
  private void cleanup()
  {
    Set localSet = map.keySet();
    String[] arrayOfString = new String[localSet.size()];
    int i = 0;
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      arrayOfString[(i++)] = str;
    }
    for (int j = 0; j < arrayOfString.length; j++) {
      entryFor(arrayOfString[j]);
    }
    queryCount = 0;
  }
  
  static class Entry
  {
    private long timestamp;
    private String val;
    
    Entry(long paramLong, String paramString)
    {
      timestamp = paramLong;
      val = paramString;
    }
    
    long timestamp()
    {
      return timestamp;
    }
    
    void setTimestamp(long paramLong)
    {
      timestamp = paramLong;
    }
    
    String val()
    {
      return val;
    }
    
    void setVal(String paramString)
    {
      val = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ExpiringCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
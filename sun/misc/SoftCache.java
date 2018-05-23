package sun.misc;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class SoftCache
  extends AbstractMap
  implements Map
{
  private Map hash;
  private ReferenceQueue queue = new ReferenceQueue();
  private Set entrySet = null;
  
  private void processQueue()
  {
    ValueCell localValueCell;
    while ((localValueCell = (ValueCell)queue.poll()) != null) {
      if (localValueCell.isValid()) {
        hash.remove(key);
      } else {
        ValueCell.access$210();
      }
    }
  }
  
  public SoftCache(int paramInt, float paramFloat)
  {
    hash = new HashMap(paramInt, paramFloat);
  }
  
  public SoftCache(int paramInt)
  {
    hash = new HashMap(paramInt);
  }
  
  public SoftCache()
  {
    hash = new HashMap();
  }
  
  public int size()
  {
    return entrySet().size();
  }
  
  public boolean isEmpty()
  {
    return entrySet().isEmpty();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return ValueCell.strip(hash.get(paramObject), false) != null;
  }
  
  protected Object fill(Object paramObject)
  {
    return null;
  }
  
  public Object get(Object paramObject)
  {
    processQueue();
    Object localObject = hash.get(paramObject);
    if (localObject == null)
    {
      localObject = fill(paramObject);
      if (localObject != null)
      {
        hash.put(paramObject, ValueCell.create(paramObject, localObject, queue));
        return localObject;
      }
    }
    return ValueCell.strip(localObject, false);
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    processQueue();
    ValueCell localValueCell = ValueCell.create(paramObject1, paramObject2, queue);
    return ValueCell.strip(hash.put(paramObject1, localValueCell), true);
  }
  
  public Object remove(Object paramObject)
  {
    processQueue();
    return ValueCell.strip(hash.remove(paramObject), true);
  }
  
  public void clear()
  {
    processQueue();
    hash.clear();
  }
  
  private static boolean valEquals(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
  
  public Set entrySet()
  {
    if (entrySet == null) {
      entrySet = new EntrySet(null);
    }
    return entrySet;
  }
  
  private class Entry
    implements Map.Entry
  {
    private Map.Entry ent;
    private Object value;
    
    Entry(Map.Entry paramEntry, Object paramObject)
    {
      ent = paramEntry;
      value = paramObject;
    }
    
    public Object getKey()
    {
      return ent.getKey();
    }
    
    public Object getValue()
    {
      return value;
    }
    
    public Object setValue(Object paramObject)
    {
      return ent.setValue(SoftCache.ValueCell.access$400(ent.getKey(), paramObject, queue));
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      return (SoftCache.valEquals(ent.getKey(), localEntry.getKey())) && (SoftCache.valEquals(value, localEntry.getValue()));
    }
    
    public int hashCode()
    {
      Object localObject;
      return ((localObject = getKey()) == null ? 0 : localObject.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }
  }
  
  private class EntrySet
    extends AbstractSet
  {
    Set hashEntries = hash.entrySet();
    
    private EntrySet() {}
    
    public Iterator iterator()
    {
      new Iterator()
      {
        Iterator hashIterator = hashEntries.iterator();
        SoftCache.Entry next = null;
        
        public boolean hasNext()
        {
          while (hashIterator.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)hashIterator.next();
            SoftCache.ValueCell localValueCell = (SoftCache.ValueCell)localEntry.getValue();
            Object localObject = null;
            if ((localValueCell == null) || ((localObject = localValueCell.get()) != null))
            {
              next = new SoftCache.Entry(SoftCache.this, localEntry, localObject);
              return true;
            }
          }
          return false;
        }
        
        public Object next()
        {
          if ((next == null) && (!hasNext())) {
            throw new NoSuchElementException();
          }
          SoftCache.Entry localEntry = next;
          next = null;
          return localEntry;
        }
        
        public void remove()
        {
          hashIterator.remove();
        }
      };
    }
    
    public boolean isEmpty()
    {
      return !iterator().hasNext();
    }
    
    public int size()
    {
      int i = 0;
      Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        i++;
        localIterator.next();
      }
      return i;
    }
    
    public boolean remove(Object paramObject)
    {
      SoftCache.this.processQueue();
      if ((paramObject instanceof SoftCache.Entry)) {
        return hashEntries.remove(ent);
      }
      return false;
    }
  }
  
  private static class ValueCell
    extends SoftReference
  {
    private static Object INVALID_KEY = new Object();
    private static int dropped = 0;
    private Object key;
    
    private ValueCell(Object paramObject1, Object paramObject2, ReferenceQueue paramReferenceQueue)
    {
      super(paramReferenceQueue);
      key = paramObject1;
    }
    
    private static ValueCell create(Object paramObject1, Object paramObject2, ReferenceQueue paramReferenceQueue)
    {
      if (paramObject2 == null) {
        return null;
      }
      return new ValueCell(paramObject1, paramObject2, paramReferenceQueue);
    }
    
    private static Object strip(Object paramObject, boolean paramBoolean)
    {
      if (paramObject == null) {
        return null;
      }
      ValueCell localValueCell = (ValueCell)paramObject;
      Object localObject = localValueCell.get();
      if (paramBoolean) {
        localValueCell.drop();
      }
      return localObject;
    }
    
    private boolean isValid()
    {
      return key != INVALID_KEY;
    }
    
    private void drop()
    {
      super.clear();
      key = INVALID_KEY;
      dropped += 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\SoftCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
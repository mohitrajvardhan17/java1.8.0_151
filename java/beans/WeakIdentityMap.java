package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class WeakIdentityMap<T>
{
  private static final int MAXIMUM_CAPACITY = 1073741824;
  private static final Object NULL = new Object();
  private final ReferenceQueue<Object> queue = new ReferenceQueue();
  private volatile Entry<T>[] table = newTable(8);
  private int threshold = 6;
  private int size = 0;
  
  WeakIdentityMap() {}
  
  public T get(Object paramObject)
  {
    removeStaleEntries();
    if (paramObject == null) {
      paramObject = NULL;
    }
    int i = paramObject.hashCode();
    Entry[] arrayOfEntry = table;
    int j = getIndex(arrayOfEntry, i);
    for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = next) {
      if (localEntry.isMatched(paramObject, i)) {
        return (T)value;
      }
    }
    synchronized (NULL)
    {
      j = getIndex(table, i);
      for (Object localObject1 = table[j]; localObject1 != null; localObject1 = next) {
        if (((Entry)localObject1).isMatched(paramObject, i)) {
          return (T)value;
        }
      }
      localObject1 = create(paramObject);
      table[j] = new Entry(paramObject, i, localObject1, queue, table[j]);
      if (++size >= threshold) {
        if (table.length == 1073741824)
        {
          threshold = Integer.MAX_VALUE;
        }
        else
        {
          removeStaleEntries();
          arrayOfEntry = newTable(table.length * 2);
          transfer(table, arrayOfEntry);
          if (size >= threshold / 2)
          {
            table = arrayOfEntry;
            threshold *= 2;
          }
          else
          {
            transfer(arrayOfEntry, table);
          }
        }
      }
      return (T)localObject1;
    }
  }
  
  protected abstract T create(Object paramObject);
  
  private void removeStaleEntries()
  {
    Reference localReference = queue.poll();
    if (localReference != null) {
      synchronized (NULL)
      {
        do
        {
          Entry localEntry1 = (Entry)localReference;
          int i = getIndex(table, hash);
          Object localObject1 = table[i];
          Entry localEntry2;
          for (Object localObject2 = localObject1; localObject2 != null; localObject2 = localEntry2)
          {
            localEntry2 = next;
            if (localObject2 == localEntry1)
            {
              if (localObject1 == localEntry1) {
                table[i] = localEntry2;
              } else {
                next = localEntry2;
              }
              value = null;
              next = null;
              size -= 1;
              break;
            }
            localObject1 = localObject2;
          }
          localReference = queue.poll();
        } while (localReference != null);
      }
    }
  }
  
  private void transfer(Entry<T>[] paramArrayOfEntry1, Entry<T>[] paramArrayOfEntry2)
  {
    for (int i = 0; i < paramArrayOfEntry1.length; i++)
    {
      Object localObject1 = paramArrayOfEntry1[i];
      paramArrayOfEntry1[i] = null;
      while (localObject1 != null)
      {
        Entry localEntry = next;
        Object localObject2 = ((Entry)localObject1).get();
        if (localObject2 == null)
        {
          value = null;
          next = null;
          size -= 1;
        }
        else
        {
          int j = getIndex(paramArrayOfEntry2, hash);
          next = paramArrayOfEntry2[j];
          paramArrayOfEntry2[j] = localObject1;
        }
        localObject1 = localEntry;
      }
    }
  }
  
  private Entry<T>[] newTable(int paramInt)
  {
    return (Entry[])new Entry[paramInt];
  }
  
  private static int getIndex(Entry<?>[] paramArrayOfEntry, int paramInt)
  {
    return paramInt & paramArrayOfEntry.length - 1;
  }
  
  private static class Entry<T>
    extends WeakReference<Object>
  {
    private final int hash;
    private volatile T value;
    private volatile Entry<T> next;
    
    Entry(Object paramObject, int paramInt, T paramT, ReferenceQueue<Object> paramReferenceQueue, Entry<T> paramEntry)
    {
      super(paramReferenceQueue);
      hash = paramInt;
      value = paramT;
      next = paramEntry;
    }
    
    boolean isMatched(Object paramObject, int paramInt)
    {
      return (hash == paramInt) && (paramObject == get());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\WeakIdentityMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
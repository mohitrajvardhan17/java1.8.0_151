package java.lang;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ThreadLocal<T>
{
  private final int threadLocalHashCode = nextHashCode();
  private static AtomicInteger nextHashCode = new AtomicInteger();
  private static final int HASH_INCREMENT = 1640531527;
  
  private static int nextHashCode()
  {
    return nextHashCode.getAndAdd(1640531527);
  }
  
  protected T initialValue()
  {
    return null;
  }
  
  public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> paramSupplier)
  {
    return new SuppliedThreadLocal(paramSupplier);
  }
  
  public ThreadLocal() {}
  
  public T get()
  {
    Thread localThread = Thread.currentThread();
    ThreadLocalMap localThreadLocalMap = getMap(localThread);
    if (localThreadLocalMap != null)
    {
      ThreadLocal.ThreadLocalMap.Entry localEntry = localThreadLocalMap.getEntry(this);
      if (localEntry != null)
      {
        Object localObject = value;
        return (T)localObject;
      }
    }
    return (T)setInitialValue();
  }
  
  private T setInitialValue()
  {
    Object localObject = initialValue();
    Thread localThread = Thread.currentThread();
    ThreadLocalMap localThreadLocalMap = getMap(localThread);
    if (localThreadLocalMap != null) {
      localThreadLocalMap.set(this, localObject);
    } else {
      createMap(localThread, localObject);
    }
    return (T)localObject;
  }
  
  public void set(T paramT)
  {
    Thread localThread = Thread.currentThread();
    ThreadLocalMap localThreadLocalMap = getMap(localThread);
    if (localThreadLocalMap != null) {
      localThreadLocalMap.set(this, paramT);
    } else {
      createMap(localThread, paramT);
    }
  }
  
  public void remove()
  {
    ThreadLocalMap localThreadLocalMap = getMap(Thread.currentThread());
    if (localThreadLocalMap != null) {
      localThreadLocalMap.remove(this);
    }
  }
  
  ThreadLocalMap getMap(Thread paramThread)
  {
    return threadLocals;
  }
  
  void createMap(Thread paramThread, T paramT)
  {
    threadLocals = new ThreadLocalMap(this, paramT);
  }
  
  static ThreadLocalMap createInheritedMap(ThreadLocalMap paramThreadLocalMap)
  {
    return new ThreadLocalMap(paramThreadLocalMap, null);
  }
  
  T childValue(T paramT)
  {
    throw new UnsupportedOperationException();
  }
  
  static final class SuppliedThreadLocal<T>
    extends ThreadLocal<T>
  {
    private final Supplier<? extends T> supplier;
    
    SuppliedThreadLocal(Supplier<? extends T> paramSupplier)
    {
      supplier = ((Supplier)Objects.requireNonNull(paramSupplier));
    }
    
    protected T initialValue()
    {
      return (T)supplier.get();
    }
  }
  
  static class ThreadLocalMap
  {
    private static final int INITIAL_CAPACITY = 16;
    private Entry[] table;
    private int size = 0;
    private int threshold;
    
    private void setThreshold(int paramInt)
    {
      threshold = (paramInt * 2 / 3);
    }
    
    private static int nextIndex(int paramInt1, int paramInt2)
    {
      return paramInt1 + 1 < paramInt2 ? paramInt1 + 1 : 0;
    }
    
    private static int prevIndex(int paramInt1, int paramInt2)
    {
      return paramInt1 - 1 >= 0 ? paramInt1 - 1 : paramInt2 - 1;
    }
    
    ThreadLocalMap(ThreadLocal<?> paramThreadLocal, Object paramObject)
    {
      table = new Entry[16];
      int i = threadLocalHashCode & 0xF;
      table[i] = new Entry(paramThreadLocal, paramObject);
      size = 1;
      setThreshold(16);
    }
    
    private ThreadLocalMap(ThreadLocalMap paramThreadLocalMap)
    {
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      setThreshold(i);
      table = new Entry[i];
      for (int j = 0; j < i; j++)
      {
        Entry localEntry1 = arrayOfEntry[j];
        if (localEntry1 != null)
        {
          ThreadLocal localThreadLocal = (ThreadLocal)localEntry1.get();
          if (localThreadLocal != null)
          {
            Object localObject = localThreadLocal.childValue(value);
            Entry localEntry2 = new Entry(localThreadLocal, localObject);
            for (int k = threadLocalHashCode & i - 1; table[k] != null; k = nextIndex(k, i)) {}
            table[k] = localEntry2;
            size += 1;
          }
        }
      }
    }
    
    private Entry getEntry(ThreadLocal<?> paramThreadLocal)
    {
      int i = threadLocalHashCode & table.length - 1;
      Entry localEntry = table[i];
      if ((localEntry != null) && (localEntry.get() == paramThreadLocal)) {
        return localEntry;
      }
      return getEntryAfterMiss(paramThreadLocal, i, localEntry);
    }
    
    private Entry getEntryAfterMiss(ThreadLocal<?> paramThreadLocal, int paramInt, Entry paramEntry)
    {
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      while (paramEntry != null)
      {
        ThreadLocal localThreadLocal = (ThreadLocal)paramEntry.get();
        if (localThreadLocal == paramThreadLocal) {
          return paramEntry;
        }
        if (localThreadLocal == null) {
          expungeStaleEntry(paramInt);
        } else {
          paramInt = nextIndex(paramInt, i);
        }
        paramEntry = arrayOfEntry[paramInt];
      }
      return null;
    }
    
    private void set(ThreadLocal<?> paramThreadLocal, Object paramObject)
    {
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      int j = threadLocalHashCode & i - 1;
      for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = arrayOfEntry[(j = nextIndex(j, i))])
      {
        ThreadLocal localThreadLocal = (ThreadLocal)localEntry.get();
        if (localThreadLocal == paramThreadLocal)
        {
          value = paramObject;
          return;
        }
        if (localThreadLocal == null)
        {
          replaceStaleEntry(paramThreadLocal, paramObject, j);
          return;
        }
      }
      arrayOfEntry[j] = new Entry(paramThreadLocal, paramObject);
      int k = ++size;
      if ((!cleanSomeSlots(j, k)) && (k >= threshold)) {
        rehash();
      }
    }
    
    private void remove(ThreadLocal<?> paramThreadLocal)
    {
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      int j = threadLocalHashCode & i - 1;
      for (Entry localEntry = arrayOfEntry[j]; localEntry != null; localEntry = arrayOfEntry[(j = nextIndex(j, i))]) {
        if (localEntry.get() == paramThreadLocal)
        {
          localEntry.clear();
          expungeStaleEntry(j);
          return;
        }
      }
    }
    
    private void replaceStaleEntry(ThreadLocal<?> paramThreadLocal, Object paramObject, int paramInt)
    {
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      int j = paramInt;
      Entry localEntry;
      for (int k = prevIndex(paramInt, i); (localEntry = arrayOfEntry[k]) != null; k = prevIndex(k, i)) {
        if (localEntry.get() == null) {
          j = k;
        }
      }
      for (k = nextIndex(paramInt, i); (localEntry = arrayOfEntry[k]) != null; k = nextIndex(k, i))
      {
        ThreadLocal localThreadLocal = (ThreadLocal)localEntry.get();
        if (localThreadLocal == paramThreadLocal)
        {
          value = paramObject;
          arrayOfEntry[k] = arrayOfEntry[paramInt];
          arrayOfEntry[paramInt] = localEntry;
          if (j == paramInt) {
            j = k;
          }
          cleanSomeSlots(expungeStaleEntry(j), i);
          return;
        }
        if ((localThreadLocal == null) && (j == paramInt)) {
          j = k;
        }
      }
      value = null;
      arrayOfEntry[paramInt] = new Entry(paramThreadLocal, paramObject);
      if (j != paramInt) {
        cleanSomeSlots(expungeStaleEntry(j), i);
      }
    }
    
    private int expungeStaleEntry(int paramInt)
    {
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      value = null;
      arrayOfEntry[paramInt] = null;
      size -= 1;
      Entry localEntry;
      for (int j = nextIndex(paramInt, i); (localEntry = arrayOfEntry[j]) != null; j = nextIndex(j, i))
      {
        ThreadLocal localThreadLocal = (ThreadLocal)localEntry.get();
        if (localThreadLocal == null)
        {
          value = null;
          arrayOfEntry[j] = null;
          size -= 1;
        }
        else
        {
          int k = threadLocalHashCode & i - 1;
          if (k != j)
          {
            arrayOfEntry[j] = null;
            while (arrayOfEntry[k] != null) {
              k = nextIndex(k, i);
            }
            arrayOfEntry[k] = localEntry;
          }
        }
      }
      return j;
    }
    
    private boolean cleanSomeSlots(int paramInt1, int paramInt2)
    {
      boolean bool = false;
      Entry[] arrayOfEntry = table;
      int i = arrayOfEntry.length;
      do
      {
        paramInt1 = nextIndex(paramInt1, i);
        Entry localEntry = arrayOfEntry[paramInt1];
        if ((localEntry != null) && (localEntry.get() == null))
        {
          paramInt2 = i;
          bool = true;
          paramInt1 = expungeStaleEntry(paramInt1);
        }
      } while (paramInt2 >>>= 1 != 0);
      return bool;
    }
    
    private void rehash()
    {
      expungeStaleEntries();
      if (size >= threshold - threshold / 4) {
        resize();
      }
    }
    
    private void resize()
    {
      Entry[] arrayOfEntry1 = table;
      int i = arrayOfEntry1.length;
      int j = i * 2;
      Entry[] arrayOfEntry2 = new Entry[j];
      int k = 0;
      for (int m = 0; m < i; m++)
      {
        Entry localEntry = arrayOfEntry1[m];
        if (localEntry != null)
        {
          ThreadLocal localThreadLocal = (ThreadLocal)localEntry.get();
          if (localThreadLocal == null)
          {
            value = null;
          }
          else
          {
            for (int n = threadLocalHashCode & j - 1; arrayOfEntry2[n] != null; n = nextIndex(n, j)) {}
            arrayOfEntry2[n] = localEntry;
            k++;
          }
        }
      }
      setThreshold(j);
      size = k;
      table = arrayOfEntry2;
    }
    
    private void expungeStaleEntries()
    {
      for (Entry localEntry : table) {
        if ((localEntry != null) && (localEntry.get() == null)) {
          expungeStaleEntry(???);
        }
      }
    }
    
    static class Entry
      extends WeakReference<ThreadLocal<?>>
    {
      Object value;
      
      Entry(ThreadLocal<?> paramThreadLocal, Object paramObject)
      {
        super();
        value = paramObject;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ThreadLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
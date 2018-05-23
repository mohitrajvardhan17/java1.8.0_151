package java.lang;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ClassValue<T>
{
  private static final Entry<?>[] EMPTY_CACHE = { null };
  final int hashCodeForCache = nextHashCode.getAndAdd(1640531527) & 0x3FFFFFFF;
  private static final AtomicInteger nextHashCode = new AtomicInteger();
  private static final int HASH_INCREMENT = 1640531527;
  static final int HASH_MASK = 1073741823;
  final Identity identity = new Identity();
  private volatile Version<T> version = new Version(this);
  private static final Object CRITICAL_SECTION = new Object();
  
  protected ClassValue() {}
  
  protected abstract T computeValue(Class<?> paramClass);
  
  public T get(Class<?> paramClass)
  {
    Entry[] arrayOfEntry;
    Entry localEntry = ClassValueMap.probeHomeLocation(arrayOfEntry = getCacheCarefully(paramClass), this);
    if (match(localEntry)) {
      return (T)localEntry.value();
    }
    return (T)getFromBackup(arrayOfEntry, paramClass);
  }
  
  public void remove(Class<?> paramClass)
  {
    ClassValueMap localClassValueMap = getMap(paramClass);
    localClassValueMap.removeEntry(this);
  }
  
  void put(Class<?> paramClass, T paramT)
  {
    ClassValueMap localClassValueMap = getMap(paramClass);
    localClassValueMap.changeEntry(this, paramT);
  }
  
  private static Entry<?>[] getCacheCarefully(Class<?> paramClass)
  {
    ClassValueMap localClassValueMap = classValueMap;
    if (localClassValueMap == null) {
      return EMPTY_CACHE;
    }
    Entry[] arrayOfEntry = localClassValueMap.getCache();
    return arrayOfEntry;
  }
  
  private T getFromBackup(Entry<?>[] paramArrayOfEntry, Class<?> paramClass)
  {
    Entry localEntry = ClassValueMap.probeBackupLocations(paramArrayOfEntry, this);
    if (localEntry != null) {
      return (T)localEntry.value();
    }
    return (T)getFromHashMap(paramClass);
  }
  
  Entry<T> castEntry(Entry<?> paramEntry)
  {
    return paramEntry;
  }
  
  private T getFromHashMap(Class<?> paramClass)
  {
    ClassValueMap localClassValueMap = getMap(paramClass);
    for (;;)
    {
      Entry localEntry = localClassValueMap.startEntry(this);
      if (!localEntry.isPromise()) {
        return (T)localEntry.value();
      }
      try
      {
        localEntry = makeEntry(localEntry.version(), computeValue(paramClass));
      }
      finally
      {
        localEntry = localClassValueMap.finishEntry(this, localEntry);
      }
      if (localEntry != null) {
        return (T)localEntry.value();
      }
    }
  }
  
  boolean match(Entry<?> paramEntry)
  {
    return (paramEntry != null) && (paramEntry.get() == version);
  }
  
  Version<T> version()
  {
    return version;
  }
  
  void bumpVersion()
  {
    version = new Version(this);
  }
  
  private static ClassValueMap getMap(Class<?> paramClass)
  {
    ClassValueMap localClassValueMap = classValueMap;
    if (localClassValueMap != null) {
      return localClassValueMap;
    }
    return initializeMap(paramClass);
  }
  
  private static ClassValueMap initializeMap(Class<?> paramClass)
  {
    ClassValueMap localClassValueMap;
    synchronized (CRITICAL_SECTION)
    {
      if ((localClassValueMap = classValueMap) == null) {
        classValueMap = (localClassValueMap = new ClassValueMap(paramClass));
      }
    }
    return localClassValueMap;
  }
  
  static <T> Entry<T> makeEntry(Version<T> paramVersion, T paramT)
  {
    return new Entry(paramVersion, paramT);
  }
  
  static class ClassValueMap
    extends WeakHashMap<ClassValue.Identity, ClassValue.Entry<?>>
  {
    private final Class<?> type;
    private ClassValue.Entry<?>[] cacheArray;
    private int cacheLoad;
    private int cacheLoadLimit;
    private static final int INITIAL_ENTRIES = 32;
    private static final int CACHE_LOAD_LIMIT = 67;
    private static final int PROBE_LIMIT = 6;
    
    ClassValueMap(Class<?> paramClass)
    {
      type = paramClass;
      sizeCache(32);
    }
    
    ClassValue.Entry<?>[] getCache()
    {
      return cacheArray;
    }
    
    synchronized <T> ClassValue.Entry<T> startEntry(ClassValue<T> paramClassValue)
    {
      ClassValue.Entry localEntry = (ClassValue.Entry)get(identity);
      ClassValue.Version localVersion = paramClassValue.version();
      if (localEntry == null)
      {
        localEntry = localVersion.promise();
        put(identity, localEntry);
        return localEntry;
      }
      if (localEntry.isPromise())
      {
        if (localEntry.version() != localVersion)
        {
          localEntry = localVersion.promise();
          put(identity, localEntry);
        }
        return localEntry;
      }
      if (localEntry.version() != localVersion)
      {
        localEntry = localEntry.refreshVersion(localVersion);
        put(identity, localEntry);
      }
      checkCacheLoad();
      addToCache(paramClassValue, localEntry);
      return localEntry;
    }
    
    synchronized <T> ClassValue.Entry<T> finishEntry(ClassValue<T> paramClassValue, ClassValue.Entry<T> paramEntry)
    {
      ClassValue.Entry localEntry = (ClassValue.Entry)get(identity);
      if (paramEntry == localEntry)
      {
        assert (paramEntry.isPromise());
        remove(identity);
        return null;
      }
      if ((localEntry != null) && (localEntry.isPromise()) && (localEntry.version() == paramEntry.version()))
      {
        ClassValue.Version localVersion = paramClassValue.version();
        if (paramEntry.version() != localVersion) {
          paramEntry = paramEntry.refreshVersion(localVersion);
        }
        put(identity, paramEntry);
        checkCacheLoad();
        addToCache(paramClassValue, paramEntry);
        return paramEntry;
      }
      return null;
    }
    
    synchronized void removeEntry(ClassValue<?> paramClassValue)
    {
      ClassValue.Entry localEntry = (ClassValue.Entry)remove(identity);
      if (localEntry != null) {
        if (localEntry.isPromise())
        {
          put(identity, localEntry);
        }
        else
        {
          paramClassValue.bumpVersion();
          removeStaleEntries(paramClassValue);
        }
      }
    }
    
    synchronized <T> void changeEntry(ClassValue<T> paramClassValue, T paramT)
    {
      ClassValue.Entry localEntry1 = (ClassValue.Entry)get(identity);
      ClassValue.Version localVersion = paramClassValue.version();
      if (localEntry1 != null)
      {
        if ((localEntry1.version() == localVersion) && (localEntry1.value() == paramT)) {
          return;
        }
        paramClassValue.bumpVersion();
        removeStaleEntries(paramClassValue);
      }
      ClassValue.Entry localEntry2 = ClassValue.makeEntry(localVersion, paramT);
      put(identity, localEntry2);
      checkCacheLoad();
      addToCache(paramClassValue, localEntry2);
    }
    
    static ClassValue.Entry<?> loadFromCache(ClassValue.Entry<?>[] paramArrayOfEntry, int paramInt)
    {
      return paramArrayOfEntry[(paramInt & paramArrayOfEntry.length - 1)];
    }
    
    static <T> ClassValue.Entry<T> probeHomeLocation(ClassValue.Entry<?>[] paramArrayOfEntry, ClassValue<T> paramClassValue)
    {
      return paramClassValue.castEntry(loadFromCache(paramArrayOfEntry, hashCodeForCache));
    }
    
    static <T> ClassValue.Entry<T> probeBackupLocations(ClassValue.Entry<?>[] paramArrayOfEntry, ClassValue<T> paramClassValue)
    {
      int i = paramArrayOfEntry.length - 1;
      int j = hashCodeForCache & i;
      ClassValue.Entry<?> localEntry1 = paramArrayOfEntry[j];
      if (localEntry1 == null) {
        return null;
      }
      int k = -1;
      for (int m = j + 1; m < j + 6; m++)
      {
        ClassValue.Entry<?> localEntry2 = paramArrayOfEntry[(m & i)];
        if (localEntry2 == null) {
          break;
        }
        if (paramClassValue.match(localEntry2))
        {
          paramArrayOfEntry[j] = localEntry2;
          if (k >= 0) {
            paramArrayOfEntry[(m & i)] = ClassValue.Entry.DEAD_ENTRY;
          } else {
            k = m;
          }
          paramArrayOfEntry[(k & i)] = (entryDislocation(paramArrayOfEntry, k, localEntry1) < 6 ? localEntry1 : ClassValue.Entry.DEAD_ENTRY);
          return paramClassValue.castEntry(localEntry2);
        }
        if ((!localEntry2.isLive()) && (k < 0)) {
          k = m;
        }
      }
      return null;
    }
    
    private static int entryDislocation(ClassValue.Entry<?>[] paramArrayOfEntry, int paramInt, ClassValue.Entry<?> paramEntry)
    {
      ClassValue localClassValue = paramEntry.classValueOrNull();
      if (localClassValue == null) {
        return 0;
      }
      int i = paramArrayOfEntry.length - 1;
      return paramInt - hashCodeForCache & i;
    }
    
    private void sizeCache(int paramInt)
    {
      assert ((paramInt & paramInt - 1) == 0);
      cacheLoad = 0;
      cacheLoadLimit = ((int)(paramInt * 67.0D / 100.0D));
      cacheArray = new ClassValue.Entry[paramInt];
    }
    
    private void checkCacheLoad()
    {
      if (cacheLoad >= cacheLoadLimit) {
        reduceCacheLoad();
      }
    }
    
    private void reduceCacheLoad()
    {
      removeStaleEntries();
      if (cacheLoad < cacheLoadLimit) {
        return;
      }
      ClassValue.Entry[] arrayOfEntry1 = getCache();
      if (arrayOfEntry1.length > 1073741823) {
        return;
      }
      sizeCache(arrayOfEntry1.length * 2);
      for (ClassValue.Entry localEntry : arrayOfEntry1) {
        if ((localEntry != null) && (localEntry.isLive())) {
          addToCache(localEntry);
        }
      }
    }
    
    private void removeStaleEntries(ClassValue.Entry<?>[] paramArrayOfEntry, int paramInt1, int paramInt2)
    {
      int i = paramArrayOfEntry.length - 1;
      int j = 0;
      for (int k = paramInt1; k < paramInt1 + paramInt2; k++)
      {
        ClassValue.Entry<?> localEntry = paramArrayOfEntry[(k & i)];
        if ((localEntry != null) && (!localEntry.isLive()))
        {
          ClassValue.Entry localEntry1 = null;
          localEntry1 = findReplacement(paramArrayOfEntry, k);
          paramArrayOfEntry[(k & i)] = localEntry1;
          if (localEntry1 == null) {
            j++;
          }
        }
      }
      cacheLoad = Math.max(0, cacheLoad - j);
    }
    
    private ClassValue.Entry<?> findReplacement(ClassValue.Entry<?>[] paramArrayOfEntry, int paramInt)
    {
      Object localObject = null;
      int i = -1;
      int j = 0;
      int k = paramArrayOfEntry.length - 1;
      for (int m = paramInt + 1; m < paramInt + 6; m++)
      {
        ClassValue.Entry<?> localEntry = paramArrayOfEntry[(m & k)];
        if (localEntry == null) {
          break;
        }
        if (localEntry.isLive())
        {
          int n = entryDislocation(paramArrayOfEntry, m, localEntry);
          if (n != 0)
          {
            int i1 = m - n;
            if (i1 <= paramInt) {
              if (i1 == paramInt)
              {
                i = 1;
                j = m;
                localObject = localEntry;
              }
              else if (i <= 0)
              {
                i = 0;
                j = m;
                localObject = localEntry;
              }
            }
          }
        }
      }
      if (i >= 0) {
        if (paramArrayOfEntry[(j + 1 & k)] != null)
        {
          paramArrayOfEntry[(j & k)] = ClassValue.Entry.DEAD_ENTRY;
        }
        else
        {
          paramArrayOfEntry[(j & k)] = null;
          cacheLoad -= 1;
        }
      }
      return (ClassValue.Entry<?>)localObject;
    }
    
    private void removeStaleEntries(ClassValue<?> paramClassValue)
    {
      removeStaleEntries(getCache(), hashCodeForCache, 6);
    }
    
    private void removeStaleEntries()
    {
      ClassValue.Entry[] arrayOfEntry = getCache();
      removeStaleEntries(arrayOfEntry, 0, arrayOfEntry.length + 6 - 1);
    }
    
    private <T> void addToCache(ClassValue.Entry<T> paramEntry)
    {
      ClassValue localClassValue = paramEntry.classValueOrNull();
      if (localClassValue != null) {
        addToCache(localClassValue, paramEntry);
      }
    }
    
    private <T> void addToCache(ClassValue<T> paramClassValue, ClassValue.Entry<T> paramEntry)
    {
      ClassValue.Entry[] arrayOfEntry = getCache();
      int i = arrayOfEntry.length - 1;
      int j = hashCodeForCache & i;
      ClassValue.Entry localEntry = placeInCache(arrayOfEntry, j, paramEntry, false);
      if (localEntry == null) {
        return;
      }
      int k = entryDislocation(arrayOfEntry, j, localEntry);
      int m = j - k;
      for (int n = m; n < m + 6; n++) {
        if (placeInCache(arrayOfEntry, n & i, localEntry, true) == null) {
          return;
        }
      }
    }
    
    private ClassValue.Entry<?> placeInCache(ClassValue.Entry<?>[] paramArrayOfEntry, int paramInt, ClassValue.Entry<?> paramEntry, boolean paramBoolean)
    {
      ClassValue.Entry localEntry = overwrittenEntry(paramArrayOfEntry[paramInt]);
      if ((paramBoolean) && (localEntry != null)) {
        return paramEntry;
      }
      paramArrayOfEntry[paramInt] = paramEntry;
      return localEntry;
    }
    
    private <T> ClassValue.Entry<T> overwrittenEntry(ClassValue.Entry<T> paramEntry)
    {
      if (paramEntry == null) {
        cacheLoad += 1;
      } else if (paramEntry.isLive()) {
        return paramEntry;
      }
      return null;
    }
  }
  
  static class Entry<T>
    extends WeakReference<ClassValue.Version<T>>
  {
    final Object value = this;
    static final Entry<?> DEAD_ENTRY = new Entry(null, null);
    
    Entry(ClassValue.Version<T> paramVersion, T paramT)
    {
      super();
    }
    
    private void assertNotPromise()
    {
      assert (!isPromise());
    }
    
    Entry(ClassValue.Version<T> paramVersion)
    {
      super();
    }
    
    T value()
    {
      assertNotPromise();
      return (T)value;
    }
    
    boolean isPromise()
    {
      return value == this;
    }
    
    ClassValue.Version<T> version()
    {
      return (ClassValue.Version)get();
    }
    
    ClassValue<T> classValueOrNull()
    {
      ClassValue.Version localVersion = version();
      return localVersion == null ? null : localVersion.classValue();
    }
    
    boolean isLive()
    {
      ClassValue.Version localVersion = version();
      if (localVersion == null) {
        return false;
      }
      if (localVersion.isLive()) {
        return true;
      }
      clear();
      return false;
    }
    
    Entry<T> refreshVersion(ClassValue.Version<T> paramVersion)
    {
      assertNotPromise();
      Entry localEntry = new Entry(paramVersion, value);
      clear();
      return localEntry;
    }
  }
  
  static class Identity
  {
    Identity() {}
  }
  
  static class Version<T>
  {
    private final ClassValue<T> classValue;
    private final ClassValue.Entry<T> promise = new ClassValue.Entry(this);
    
    Version(ClassValue<T> paramClassValue)
    {
      classValue = paramClassValue;
    }
    
    ClassValue<T> classValue()
    {
      return classValue;
    }
    
    ClassValue.Entry<T> promise()
    {
      return promise;
    }
    
    boolean isLive()
    {
      return classValue.version() == this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ClassValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
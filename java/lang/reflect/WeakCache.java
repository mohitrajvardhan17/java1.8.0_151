package java.lang.reflect;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

final class WeakCache<K, P, V>
{
  private final ReferenceQueue<K> refQueue = new ReferenceQueue();
  private final ConcurrentMap<Object, ConcurrentMap<Object, Supplier<V>>> map = new ConcurrentHashMap();
  private final ConcurrentMap<Supplier<V>, Boolean> reverseMap = new ConcurrentHashMap();
  private final BiFunction<K, P, ?> subKeyFactory;
  private final BiFunction<K, P, V> valueFactory;
  
  public WeakCache(BiFunction<K, P, ?> paramBiFunction, BiFunction<K, P, V> paramBiFunction1)
  {
    subKeyFactory = ((BiFunction)Objects.requireNonNull(paramBiFunction));
    valueFactory = ((BiFunction)Objects.requireNonNull(paramBiFunction1));
  }
  
  public V get(K paramK, P paramP)
  {
    Objects.requireNonNull(paramP);
    expungeStaleEntries();
    Object localObject1 = CacheKey.valueOf(paramK, refQueue);
    Object localObject2 = (ConcurrentMap)map.get(localObject1);
    if (localObject2 == null)
    {
      localObject3 = (ConcurrentMap)map.putIfAbsent(localObject1, localObject2 = new ConcurrentHashMap());
      if (localObject3 != null) {
        localObject2 = localObject3;
      }
    }
    Object localObject3 = Objects.requireNonNull(subKeyFactory.apply(paramK, paramP));
    Object localObject4 = (Supplier)((ConcurrentMap)localObject2).get(localObject3);
    Factory localFactory = null;
    for (;;)
    {
      if (localObject4 != null)
      {
        Object localObject5 = ((Supplier)localObject4).get();
        if (localObject5 != null) {
          return (V)localObject5;
        }
      }
      if (localFactory == null) {
        localFactory = new Factory(paramK, paramP, localObject3, (ConcurrentMap)localObject2);
      }
      if (localObject4 == null)
      {
        localObject4 = (Supplier)((ConcurrentMap)localObject2).putIfAbsent(localObject3, localFactory);
        if (localObject4 == null) {
          localObject4 = localFactory;
        }
      }
      else if (((ConcurrentMap)localObject2).replace(localObject3, localObject4, localFactory))
      {
        localObject4 = localFactory;
      }
      else
      {
        localObject4 = (Supplier)((ConcurrentMap)localObject2).get(localObject3);
      }
    }
  }
  
  public boolean containsValue(V paramV)
  {
    Objects.requireNonNull(paramV);
    expungeStaleEntries();
    return reverseMap.containsKey(new LookupValue(paramV));
  }
  
  public int size()
  {
    expungeStaleEntries();
    return reverseMap.size();
  }
  
  private void expungeStaleEntries()
  {
    CacheKey localCacheKey;
    while ((localCacheKey = (CacheKey)refQueue.poll()) != null) {
      localCacheKey.expungeFrom(map, reverseMap);
    }
  }
  
  private static final class CacheKey<K>
    extends WeakReference<K>
  {
    private static final Object NULL_KEY = new Object();
    private final int hash;
    
    static <K> Object valueOf(K paramK, ReferenceQueue<K> paramReferenceQueue)
    {
      return paramK == null ? NULL_KEY : new CacheKey(paramK, paramReferenceQueue);
    }
    
    private CacheKey(K paramK, ReferenceQueue<K> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      hash = System.identityHashCode(paramK);
    }
    
    public int hashCode()
    {
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      Object localObject;
      return (paramObject == this) || ((paramObject != null) && (paramObject.getClass() == getClass()) && ((localObject = get()) != null) && (localObject == ((CacheKey)paramObject).get()));
    }
    
    void expungeFrom(ConcurrentMap<?, ? extends ConcurrentMap<?, ?>> paramConcurrentMap, ConcurrentMap<?, Boolean> paramConcurrentMap1)
    {
      ConcurrentMap localConcurrentMap = (ConcurrentMap)paramConcurrentMap.remove(this);
      if (localConcurrentMap != null)
      {
        Iterator localIterator = localConcurrentMap.values().iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          paramConcurrentMap1.remove(localObject);
        }
      }
    }
  }
  
  private static final class CacheValue<V>
    extends WeakReference<V>
    implements WeakCache.Value<V>
  {
    private final int hash;
    
    CacheValue(V paramV)
    {
      super();
      hash = System.identityHashCode(paramV);
    }
    
    public int hashCode()
    {
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      Object localObject;
      return (paramObject == this) || (((paramObject instanceof WeakCache.Value)) && ((localObject = get()) != null) && (localObject == ((WeakCache.Value)paramObject).get()));
    }
  }
  
  private final class Factory
    implements Supplier<V>
  {
    private final K key;
    private final P parameter;
    private final Object subKey;
    private final ConcurrentMap<Object, Supplier<V>> valuesMap;
    
    Factory(P paramP, Object paramObject, ConcurrentMap<Object, Supplier<V>> paramConcurrentMap)
    {
      key = paramP;
      parameter = paramObject;
      subKey = paramConcurrentMap;
      ConcurrentMap localConcurrentMap;
      valuesMap = localConcurrentMap;
    }
    
    /* Error */
    public synchronized V get()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 95	java/lang/reflect/WeakCache$Factory:valuesMap	Ljava/util/concurrent/ConcurrentMap;
      //   4: aload_0
      //   5: getfield 93	java/lang/reflect/WeakCache$Factory:subKey	Ljava/lang/Object;
      //   8: invokeinterface 104 2 0
      //   13: checkcast 57	java/util/function/Supplier
      //   16: astore_1
      //   17: aload_1
      //   18: aload_0
      //   19: if_acmpeq +5 -> 24
      //   22: aconst_null
      //   23: areturn
      //   24: aconst_null
      //   25: astore_2
      //   26: aload_0
      //   27: getfield 94	java/lang/reflect/WeakCache$Factory:this$0	Ljava/lang/reflect/WeakCache;
      //   30: invokestatic 101	java/lang/reflect/WeakCache:access$000	(Ljava/lang/reflect/WeakCache;)Ljava/util/function/BiFunction;
      //   33: aload_0
      //   34: getfield 91	java/lang/reflect/WeakCache$Factory:key	Ljava/lang/Object;
      //   37: aload_0
      //   38: getfield 92	java/lang/reflect/WeakCache$Factory:parameter	Ljava/lang/Object;
      //   41: invokeinterface 108 3 0
      //   46: invokestatic 103	java/util/Objects:requireNonNull	(Ljava/lang/Object;)Ljava/lang/Object;
      //   49: astore_2
      //   50: aload_2
      //   51: ifnonnull +43 -> 94
      //   54: aload_0
      //   55: getfield 95	java/lang/reflect/WeakCache$Factory:valuesMap	Ljava/util/concurrent/ConcurrentMap;
      //   58: aload_0
      //   59: getfield 93	java/lang/reflect/WeakCache$Factory:subKey	Ljava/lang/Object;
      //   62: aload_0
      //   63: invokeinterface 105 3 0
      //   68: pop
      //   69: goto +25 -> 94
      //   72: astore_3
      //   73: aload_2
      //   74: ifnonnull +18 -> 92
      //   77: aload_0
      //   78: getfield 95	java/lang/reflect/WeakCache$Factory:valuesMap	Ljava/util/concurrent/ConcurrentMap;
      //   81: aload_0
      //   82: getfield 93	java/lang/reflect/WeakCache$Factory:subKey	Ljava/lang/Object;
      //   85: aload_0
      //   86: invokeinterface 105 3 0
      //   91: pop
      //   92: aload_3
      //   93: athrow
      //   94: getstatic 90	java/lang/reflect/WeakCache$Factory:$assertionsDisabled	Z
      //   97: ifne +15 -> 112
      //   100: aload_2
      //   101: ifnonnull +11 -> 112
      //   104: new 47	java/lang/AssertionError
      //   107: dup
      //   108: invokespecial 96	java/lang/AssertionError:<init>	()V
      //   111: athrow
      //   112: new 52	java/lang/reflect/WeakCache$CacheValue
      //   115: dup
      //   116: aload_2
      //   117: invokespecial 102	java/lang/reflect/WeakCache$CacheValue:<init>	(Ljava/lang/Object;)V
      //   120: astore_3
      //   121: aload_0
      //   122: getfield 94	java/lang/reflect/WeakCache$Factory:this$0	Ljava/lang/reflect/WeakCache;
      //   125: invokestatic 100	java/lang/reflect/WeakCache:access$100	(Ljava/lang/reflect/WeakCache;)Ljava/util/concurrent/ConcurrentMap;
      //   128: aload_3
      //   129: getstatic 89	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
      //   132: invokeinterface 106 3 0
      //   137: pop
      //   138: aload_0
      //   139: getfield 95	java/lang/reflect/WeakCache$Factory:valuesMap	Ljava/util/concurrent/ConcurrentMap;
      //   142: aload_0
      //   143: getfield 93	java/lang/reflect/WeakCache$Factory:subKey	Ljava/lang/Object;
      //   146: aload_0
      //   147: aload_3
      //   148: invokeinterface 107 4 0
      //   153: ifne +13 -> 166
      //   156: new 47	java/lang/AssertionError
      //   159: dup
      //   160: ldc 1
      //   162: invokespecial 97	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   165: athrow
      //   166: aload_2
      //   167: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	168	0	this	Factory
      //   16	2	1	localSupplier	Supplier
      //   25	142	2	localObject1	Object
      //   72	21	3	localObject2	Object
      //   120	28	3	localCacheValue	WeakCache.CacheValue
      // Exception table:
      //   from	to	target	type
      //   26	50	72	finally
    }
  }
  
  private static final class LookupValue<V>
    implements WeakCache.Value<V>
  {
    private final V value;
    
    LookupValue(V paramV)
    {
      value = paramV;
    }
    
    public V get()
    {
      return (V)value;
    }
    
    public int hashCode()
    {
      return System.identityHashCode(value);
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject == this) || (((paramObject instanceof WeakCache.Value)) && (value == ((WeakCache.Value)paramObject).get()));
    }
  }
  
  private static abstract interface Value<V>
    extends Supplier<V>
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\WeakCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
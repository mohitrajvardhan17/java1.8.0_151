package com.sun.jmx.mbeanserver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

class WeakIdentityHashMap<K, V>
{
  private Map<WeakReference<K>, V> map = Util.newMap();
  private ReferenceQueue<K> refQueue = new ReferenceQueue();
  
  private WeakIdentityHashMap() {}
  
  static <K, V> WeakIdentityHashMap<K, V> make()
  {
    return new WeakIdentityHashMap();
  }
  
  V get(K paramK)
  {
    expunge();
    WeakReference localWeakReference = makeReference(paramK);
    return (V)map.get(localWeakReference);
  }
  
  public V put(K paramK, V paramV)
  {
    expunge();
    if (paramK == null) {
      throw new IllegalArgumentException("Null key");
    }
    WeakReference localWeakReference = makeReference(paramK, refQueue);
    return (V)map.put(localWeakReference, paramV);
  }
  
  public V remove(K paramK)
  {
    expunge();
    WeakReference localWeakReference = makeReference(paramK);
    return (V)map.remove(localWeakReference);
  }
  
  private void expunge()
  {
    Reference localReference;
    while ((localReference = refQueue.poll()) != null) {
      map.remove(localReference);
    }
  }
  
  private WeakReference<K> makeReference(K paramK)
  {
    return new IdentityWeakReference(paramK);
  }
  
  private WeakReference<K> makeReference(K paramK, ReferenceQueue<K> paramReferenceQueue)
  {
    return new IdentityWeakReference(paramK, paramReferenceQueue);
  }
  
  private static class IdentityWeakReference<T>
    extends WeakReference<T>
  {
    private final int hashCode;
    
    IdentityWeakReference(T paramT)
    {
      this(paramT, null);
    }
    
    IdentityWeakReference(T paramT, ReferenceQueue<T> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      hashCode = (paramT == null ? 0 : System.identityHashCode(paramT));
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof IdentityWeakReference)) {
        return false;
      }
      IdentityWeakReference localIdentityWeakReference = (IdentityWeakReference)paramObject;
      Object localObject = get();
      return (localObject != null) && (localObject == localIdentityWeakReference.get());
    }
    
    public int hashCode()
    {
      return hashCode;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\WeakIdentityHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
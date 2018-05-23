package com.sun.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public final class WeakCache<K, V>
{
  private final Map<K, Reference<V>> map = new WeakHashMap();
  
  public WeakCache() {}
  
  public V get(K paramK)
  {
    Reference localReference = (Reference)map.get(paramK);
    if (localReference == null) {
      return null;
    }
    Object localObject = localReference.get();
    if (localObject == null) {
      map.remove(paramK);
    }
    return (V)localObject;
  }
  
  public void put(K paramK, V paramV)
  {
    if (paramV != null) {
      map.put(paramK, new WeakReference(paramV));
    } else {
      map.remove(paramK);
    }
  }
  
  public void clear()
  {
    map.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\WeakCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
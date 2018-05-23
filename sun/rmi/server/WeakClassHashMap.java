package sun.rmi.server;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class WeakClassHashMap<V>
{
  private Map<Class<?>, ValueCell<V>> internalMap = new WeakHashMap();
  
  protected WeakClassHashMap() {}
  
  public V get(Class<?> paramClass)
  {
    ValueCell localValueCell;
    synchronized (internalMap)
    {
      localValueCell = (ValueCell)internalMap.get(paramClass);
      if (localValueCell == null)
      {
        localValueCell = new ValueCell();
        internalMap.put(paramClass, localValueCell);
      }
    }
    synchronized (localValueCell)
    {
      Object localObject2 = null;
      if (ref != null) {
        localObject2 = ref.get();
      }
      if (localObject2 == null)
      {
        localObject2 = computeValue(paramClass);
        ref = new SoftReference(localObject2);
      }
      return (V)localObject2;
    }
  }
  
  protected abstract V computeValue(Class<?> paramClass);
  
  private static class ValueCell<T>
  {
    Reference<T> ref = null;
    
    ValueCell() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\WeakClassHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.schemagen;

import java.util.Map;
import java.util.TreeMap;

final class MultiMap<K extends Comparable<K>, V>
  extends TreeMap<K, V>
{
  private final V many;
  
  public MultiMap(V paramV)
  {
    many = paramV;
  }
  
  public V put(K paramK, V paramV)
  {
    Object localObject = super.put(paramK, paramV);
    if ((localObject != null) && (!localObject.equals(paramV))) {
      super.put(paramK, many);
    }
    return (V)localObject;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\MultiMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
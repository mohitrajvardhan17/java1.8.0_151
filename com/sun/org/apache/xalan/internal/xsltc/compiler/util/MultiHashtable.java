package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class MultiHashtable<K, V>
{
  static final long serialVersionUID = -6151608290510033572L;
  private final Map<K, Set<V>> map = new HashMap();
  private boolean modifiable = true;
  
  public MultiHashtable() {}
  
  public Set<V> put(K paramK, V paramV)
  {
    if (modifiable)
    {
      Object localObject = (Set)map.get(paramK);
      if (localObject == null)
      {
        localObject = new HashSet();
        map.put(paramK, localObject);
      }
      ((Set)localObject).add(paramV);
      return (Set<V>)localObject;
    }
    throw new UnsupportedOperationException("The MultiHashtable instance is not modifiable.");
  }
  
  public V maps(K paramK, V paramV)
  {
    if (paramK == null) {
      return null;
    }
    Set localSet = (Set)map.get(paramK);
    if (localSet != null)
    {
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (localObject.equals(paramV)) {
          return (V)localObject;
        }
      }
    }
    return null;
  }
  
  public void makeUnmodifiable()
  {
    modifiable = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\MultiHashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
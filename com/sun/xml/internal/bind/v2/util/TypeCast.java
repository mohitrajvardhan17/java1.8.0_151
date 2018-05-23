package com.sun.xml.internal.bind.v2.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TypeCast
{
  public TypeCast() {}
  
  public static <K, V> Map<K, V> checkedCast(Map<?, ?> paramMap, Class<K> paramClass, Class<V> paramClass1)
  {
    if (paramMap == null) {
      return null;
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (!paramClass.isInstance(localEntry.getKey())) {
        throw new ClassCastException(localEntry.getKey().getClass().toString());
      }
      if (!paramClass1.isInstance(localEntry.getValue())) {
        throw new ClassCastException(localEntry.getValue().getClass().toString());
      }
    }
    return paramMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\TypeCast.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
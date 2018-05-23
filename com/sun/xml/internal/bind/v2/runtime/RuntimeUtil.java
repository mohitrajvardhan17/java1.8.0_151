package com.sun.xml.internal.bind.v2.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RuntimeUtil
{
  public static final Map<Class, Class> boxToPrimitive;
  public static final Map<Class, Class> primitiveToBox;
  
  public RuntimeUtil() {}
  
  private static String getTypeName(Object paramObject)
  {
    return paramObject.getClass().getName();
  }
  
  static
  {
    HashMap localHashMap1 = new HashMap();
    localHashMap1.put(Byte.TYPE, Byte.class);
    localHashMap1.put(Short.TYPE, Short.class);
    localHashMap1.put(Integer.TYPE, Integer.class);
    localHashMap1.put(Long.TYPE, Long.class);
    localHashMap1.put(Character.TYPE, Character.class);
    localHashMap1.put(Boolean.TYPE, Boolean.class);
    localHashMap1.put(Float.TYPE, Float.class);
    localHashMap1.put(Double.TYPE, Double.class);
    localHashMap1.put(Void.TYPE, Void.class);
    primitiveToBox = Collections.unmodifiableMap(localHashMap1);
    HashMap localHashMap2 = new HashMap();
    Iterator localIterator = localHashMap1.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      localHashMap2.put(localEntry.getValue(), localEntry.getKey());
    }
    boxToPrimitive = Collections.unmodifiableMap(localHashMap2);
  }
  
  public static final class ToStringAdapter
    extends XmlAdapter<String, Object>
  {
    public ToStringAdapter() {}
    
    public Object unmarshal(String paramString)
    {
      throw new UnsupportedOperationException();
    }
    
    public String marshal(Object paramObject)
    {
      if (paramObject == null) {
        return null;
      }
      return paramObject.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\RuntimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
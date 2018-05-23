package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

final class PrimitiveTypeMap
{
  private static final Map<String, Class<?>> map = new HashMap(9);
  
  static Class<?> getType(String paramString)
  {
    return (Class)map.get(paramString);
  }
  
  private PrimitiveTypeMap() {}
  
  static
  {
    map.put(Boolean.TYPE.getName(), Boolean.TYPE);
    map.put(Character.TYPE.getName(), Character.TYPE);
    map.put(Byte.TYPE.getName(), Byte.TYPE);
    map.put(Short.TYPE.getName(), Short.TYPE);
    map.put(Integer.TYPE.getName(), Integer.TYPE);
    map.put(Long.TYPE.getName(), Long.TYPE);
    map.put(Float.TYPE.getName(), Float.TYPE);
    map.put(Double.TYPE.getName(), Double.TYPE);
    map.put(Void.TYPE.getName(), Void.TYPE);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\PrimitiveTypeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
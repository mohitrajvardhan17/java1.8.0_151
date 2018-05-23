package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

public final class PrimitiveWrapperMap
{
  private static final Map<String, Class<?>> map = new HashMap(9);
  
  static void replacePrimitivesWithWrappers(Class<?>[] paramArrayOfClass)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      if ((paramArrayOfClass[i] != null) && (paramArrayOfClass[i].isPrimitive())) {
        paramArrayOfClass[i] = getType(paramArrayOfClass[i].getName());
      }
    }
  }
  
  public static Class<?> getType(String paramString)
  {
    return (Class)map.get(paramString);
  }
  
  private PrimitiveWrapperMap() {}
  
  static
  {
    map.put(Boolean.TYPE.getName(), Boolean.class);
    map.put(Character.TYPE.getName(), Character.class);
    map.put(Byte.TYPE.getName(), Byte.class);
    map.put(Short.TYPE.getName(), Short.class);
    map.put(Integer.TYPE.getName(), Integer.class);
    map.put(Long.TYPE.getName(), Long.class);
    map.put(Float.TYPE.getName(), Float.class);
    map.put(Double.TYPE.getName(), Double.class);
    map.put(Void.TYPE.getName(), Void.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\PrimitiveWrapperMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
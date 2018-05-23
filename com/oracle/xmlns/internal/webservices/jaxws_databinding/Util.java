package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import com.sun.xml.internal.ws.model.RuntimeModelerException;

class Util
{
  Util() {}
  
  static String nullSafe(String paramString)
  {
    return paramString == null ? "" : paramString;
  }
  
  static <T> T nullSafe(T paramT1, T paramT2)
  {
    return paramT1 == null ? paramT2 : paramT1;
  }
  
  static <T extends Enum> T nullSafe(Enum paramEnum, T paramT)
  {
    return paramEnum == null ? paramT : Enum.valueOf(paramT.getClass(), paramEnum.toString());
  }
  
  public static Class<?> findClass(String paramString)
  {
    try
    {
      return Class.forName(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeModelerException("runtime.modeler.external.metadata.generic", new Object[] { localClassNotFoundException });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
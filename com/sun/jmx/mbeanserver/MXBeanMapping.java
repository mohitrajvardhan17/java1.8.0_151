package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public abstract class MXBeanMapping
{
  private final Type javaType;
  private final OpenType<?> openType;
  private final Class<?> openClass;
  
  protected MXBeanMapping(Type paramType, OpenType<?> paramOpenType)
  {
    if ((paramType == null) || (paramOpenType == null)) {
      throw new NullPointerException("Null argument");
    }
    javaType = paramType;
    openType = paramOpenType;
    openClass = makeOpenClass(paramType, paramOpenType);
  }
  
  public final Type getJavaType()
  {
    return javaType;
  }
  
  public final OpenType<?> getOpenType()
  {
    return openType;
  }
  
  public final Class<?> getOpenClass()
  {
    return openClass;
  }
  
  private static Class<?> makeOpenClass(Type paramType, OpenType<?> paramOpenType)
  {
    if (((paramType instanceof Class)) && (((Class)paramType).isPrimitive())) {
      return (Class)paramType;
    }
    try
    {
      String str = paramOpenType.getClassName();
      return Class.forName(str, false, MXBeanMapping.class.getClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeException(localClassNotFoundException);
    }
  }
  
  public abstract Object fromOpenValue(Object paramObject)
    throws InvalidObjectException;
  
  public abstract Object toOpenValue(Object paramObject)
    throws OpenDataException;
  
  public void checkReconstructible()
    throws InvalidObjectException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\MXBeanMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class LocatableAnnotation
  implements InvocationHandler, Locatable, Location
{
  private final Annotation core;
  private final Locatable upstream;
  private static final Map<Class, Quick> quicks = new HashMap();
  
  public static <A extends Annotation> A create(A paramA, Locatable paramLocatable)
  {
    if (paramA == null) {
      return null;
    }
    Class localClass1 = paramA.annotationType();
    if (quicks.containsKey(localClass1)) {
      return ((Quick)quicks.get(localClass1)).newInstance(paramLocatable, paramA);
    }
    ClassLoader localClassLoader = SecureLoader.getClassClassLoader(LocatableAnnotation.class);
    try
    {
      Class localClass2 = Class.forName(localClass1.getName(), false, localClassLoader);
      if (localClass2 != localClass1) {
        return paramA;
      }
      return (Annotation)Proxy.newProxyInstance(localClassLoader, new Class[] { localClass1, Locatable.class }, new LocatableAnnotation(paramA, paramLocatable));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return paramA;
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return paramA;
  }
  
  LocatableAnnotation(Annotation paramAnnotation, Locatable paramLocatable)
  {
    core = paramAnnotation;
    upstream = paramLocatable;
  }
  
  public Locatable getUpstream()
  {
    return upstream;
  }
  
  public Location getLocation()
  {
    return this;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    try
    {
      if (paramMethod.getDeclaringClass() == Locatable.class) {
        return paramMethod.invoke(this, paramArrayOfObject);
      }
      if (Modifier.isStatic(paramMethod.getModifiers())) {
        throw new IllegalArgumentException();
      }
      return paramMethod.invoke(core, paramArrayOfObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (localInvocationTargetException.getTargetException() != null) {
        throw localInvocationTargetException.getTargetException();
      }
      throw localInvocationTargetException;
    }
  }
  
  public String toString()
  {
    return core.toString();
  }
  
  static
  {
    for (Quick localQuick : Init.getAll()) {
      quicks.put(localQuick.annotationType(), localQuick);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\LocatableAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
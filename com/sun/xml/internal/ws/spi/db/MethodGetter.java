package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class MethodGetter
  extends PropertyGetterBase
{
  private Method method;
  
  public MethodGetter(Method paramMethod)
  {
    method = paramMethod;
    type = paramMethod.getReturnType();
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public <A> A getAnnotation(Class<A> paramClass)
  {
    Class<A> localClass = paramClass;
    return method.getAnnotation(localClass);
  }
  
  public Object get(Object paramObject)
  {
    Object[] arrayOfObject = new Object[0];
    try
    {
      if (method.isAccessible()) {
        return method.invoke(paramObject, arrayOfObject);
      }
      PrivilegedGetter localPrivilegedGetter = new PrivilegedGetter(method, paramObject);
      try
      {
        AccessController.doPrivileged(localPrivilegedGetter);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        localPrivilegedActionException.printStackTrace();
      }
      return value;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  static class PrivilegedGetter
    implements PrivilegedExceptionAction
  {
    private Object value;
    private Method method;
    private Object instance;
    
    public PrivilegedGetter(Method paramMethod, Object paramObject)
    {
      method = paramMethod;
      instance = paramObject;
    }
    
    public Object run()
      throws IllegalAccessException
    {
      if (!method.isAccessible()) {
        method.setAccessible(true);
      }
      try
      {
        value = method.invoke(instance, new Object[0]);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\MethodGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
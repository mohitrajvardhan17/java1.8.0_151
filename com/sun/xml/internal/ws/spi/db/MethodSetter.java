package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class MethodSetter
  extends PropertySetterBase
{
  private Method method;
  
  public MethodSetter(Method paramMethod)
  {
    method = paramMethod;
    type = paramMethod.getParameterTypes()[0];
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
  
  public void set(final Object paramObject1, Object paramObject2)
  {
    final Object[] arrayOfObject = { paramObject2 };
    if (method.isAccessible()) {
      try
      {
        method.invoke(paramObject1, arrayOfObject);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    } else {
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws IllegalAccessException
          {
            if (!method.isAccessible()) {
              method.setAccessible(true);
            }
            try
            {
              method.invoke(paramObject1, arrayOfObject);
            }
            catch (Exception localException)
            {
              localException.printStackTrace();
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        localPrivilegedActionException.printStackTrace();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\MethodSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
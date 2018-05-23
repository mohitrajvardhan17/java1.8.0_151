package com.sun.corba.se.spi.orbutil.proxy;

import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class DelegateInvocationHandlerImpl
{
  private DelegateInvocationHandlerImpl() {}
  
  public static InvocationHandler create(Object paramObject)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new DynamicAccessPermission("access"));
    }
    new InvocationHandler()
    {
      public Object invoke(Object paramAnonymousObject, Method paramAnonymousMethod, Object[] paramAnonymousArrayOfObject)
        throws Throwable
      {
        try
        {
          return paramAnonymousMethod.invoke(val$delegate, paramAnonymousArrayOfObject);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw localInvocationTargetException.getCause();
        }
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\DelegateInvocationHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
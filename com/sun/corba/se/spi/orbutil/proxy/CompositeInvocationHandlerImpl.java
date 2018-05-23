package com.sun.corba.se.spi.orbutil.proxy;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompositeInvocationHandlerImpl
  implements CompositeInvocationHandler
{
  private Map classToInvocationHandler = new LinkedHashMap();
  private InvocationHandler defaultHandler = null;
  private static final DynamicAccessPermission perm = new DynamicAccessPermission("access");
  private static final long serialVersionUID = 4571178305984833743L;
  
  public CompositeInvocationHandlerImpl() {}
  
  public void addInvocationHandler(Class paramClass, InvocationHandler paramInvocationHandler)
  {
    checkAccess();
    classToInvocationHandler.put(paramClass, paramInvocationHandler);
  }
  
  public void setDefaultHandler(InvocationHandler paramInvocationHandler)
  {
    checkAccess();
    defaultHandler = paramInvocationHandler;
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    Class localClass = paramMethod.getDeclaringClass();
    InvocationHandler localInvocationHandler = (InvocationHandler)classToInvocationHandler.get(localClass);
    if (localInvocationHandler == null) {
      if (defaultHandler != null)
      {
        localInvocationHandler = defaultHandler;
      }
      else
      {
        ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("util");
        throw localORBUtilSystemException.noInvocationHandler("\"" + paramMethod.toString() + "\"");
      }
    }
    return localInvocationHandler.invoke(paramObject, paramMethod, paramArrayOfObject);
  }
  
  private void checkAccess()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(perm);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orbutil\proxy\CompositeInvocationHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
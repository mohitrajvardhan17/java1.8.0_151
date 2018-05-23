package com.sun.corba.se.impl.io;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;

class ObjectStreamClassCorbaExt
{
  ObjectStreamClassCorbaExt() {}
  
  static final boolean isAbstractInterface(Class paramClass)
  {
    if ((!paramClass.isInterface()) || (Remote.class.isAssignableFrom(paramClass))) {
      return false;
    }
    Method[] arrayOfMethod = paramClass.getMethods();
    for (int i = 0; i < arrayOfMethod.length; i++)
    {
      Class[] arrayOfClass = arrayOfMethod[i].getExceptionTypes();
      int j = 0;
      for (int k = 0; (k < arrayOfClass.length) && (j == 0); k++) {
        if ((RemoteException.class == arrayOfClass[k]) || (Throwable.class == arrayOfClass[k]) || (Exception.class == arrayOfClass[k]) || (IOException.class == arrayOfClass[k])) {
          j = 1;
        }
      }
      if (j == 0) {
        return false;
      }
    }
    return true;
  }
  
  static final boolean isAny(String paramString)
  {
    int i = 0;
    if ((paramString != null) && ((paramString.equals("Ljava/lang/Object;")) || (paramString.equals("Ljava/io/Serializable;")) || (paramString.equals("Ljava/io/Externalizable;")))) {
      i = 1;
    }
    return i == 1;
  }
  
  private static final Method[] getDeclaredMethods(Class paramClass)
  {
    (Method[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return val$clz.getDeclaredMethods();
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\io\ObjectStreamClassCorbaExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
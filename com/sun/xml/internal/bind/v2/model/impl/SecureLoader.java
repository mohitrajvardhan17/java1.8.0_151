package com.sun.xml.internal.bind.v2.model.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecureLoader
{
  SecureLoader() {}
  
  static ClassLoader getContextClassLoader()
  {
    if (System.getSecurityManager() == null) {
      return Thread.currentThread().getContextClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
  }
  
  static ClassLoader getClassClassLoader(Class paramClass)
  {
    if (System.getSecurityManager() == null) {
      return paramClass.getClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return val$c.getClassLoader();
      }
    });
  }
  
  static ClassLoader getSystemClassLoader()
  {
    if (System.getSecurityManager() == null) {
      return ClassLoader.getSystemClassLoader();
    }
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return ClassLoader.getSystemClassLoader();
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\SecureLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
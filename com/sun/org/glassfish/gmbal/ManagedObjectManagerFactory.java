package com.sun.org.glassfish.gmbal;

import com.sun.org.glassfish.gmbal.util.GenericConstructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.ObjectName;

public final class ManagedObjectManagerFactory
{
  private static GenericConstructor<ManagedObjectManager> objectNameCons = new GenericConstructor(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", new Class[] { ObjectName.class });
  private static GenericConstructor<ManagedObjectManager> stringCons = new GenericConstructor(ManagedObjectManager.class, "com.sun.org.glassfish.gmbal.impl.ManagedObjectManagerImpl", new Class[] { String.class });
  
  private ManagedObjectManagerFactory() {}
  
  public static Method getMethod(Class<?> paramClass, final String paramString, final Class<?>... paramVarArgs)
  {
    try
    {
      (Method)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Method run()
          throws Exception
        {
          return val$cls.getDeclaredMethod(paramString, paramVarArgs);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new GmbalException("Unexpected exception", localPrivilegedActionException);
    }
    catch (SecurityException localSecurityException)
    {
      throw new GmbalException("Unexpected exception", localSecurityException);
    }
  }
  
  public static ManagedObjectManager createStandalone(String paramString)
  {
    ManagedObjectManager localManagedObjectManager = (ManagedObjectManager)stringCons.create(new Object[] { paramString });
    if (localManagedObjectManager == null) {
      return ManagedObjectManagerNOPImpl.self;
    }
    return localManagedObjectManager;
  }
  
  public static ManagedObjectManager createFederated(ObjectName paramObjectName)
  {
    ManagedObjectManager localManagedObjectManager = (ManagedObjectManager)objectNameCons.create(new Object[] { paramObjectName });
    if (localManagedObjectManager == null) {
      return ManagedObjectManagerNOPImpl.self;
    }
    return localManagedObjectManager;
  }
  
  public static ManagedObjectManager createNOOP()
  {
    return ManagedObjectManagerNOPImpl.self;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\gmbal\ManagedObjectManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
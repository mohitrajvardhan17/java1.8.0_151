package com.sun.jndi.toolkit.corba;

import com.sun.jndi.cosnaming.CNCtx;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import org.omg.CORBA.ORB;

public class CorbaUtils
{
  private static Method toStubMethod = null;
  private static Method connectMethod = null;
  private static Class<?> corbaStubClass = null;
  
  public CorbaUtils() {}
  
  public static org.omg.CORBA.Object remoteToCorba(Remote paramRemote, ORB paramORB)
    throws ClassNotFoundException, ConfigurationException
  {
    synchronized (CorbaUtils.class)
    {
      if (toStubMethod == null) {
        initMethodHandles();
      }
    }
    Object localObject2;
    ConfigurationException localConfigurationException;
    try
    {
      ??? = toStubMethod.invoke(null, new Object[] { paramRemote });
    }
    catch (InvocationTargetException localInvocationTargetException1)
    {
      localObject2 = localInvocationTargetException1.getTargetException();
      localConfigurationException = new ConfigurationException("Problem with PortableRemoteObject.toStub(); object not exported or stub not found");
      localConfigurationException.setRootCause((Throwable)localObject2);
      throw localConfigurationException;
    }
    catch (IllegalAccessException localIllegalAccessException1)
    {
      localObject2 = new ConfigurationException("Cannot invoke javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
      ((ConfigurationException)localObject2).setRootCause(localIllegalAccessException1);
      throw ((Throwable)localObject2);
    }
    if (!corbaStubClass.isInstance(???)) {
      return null;
    }
    try
    {
      connectMethod.invoke(???, new Object[] { paramORB });
    }
    catch (InvocationTargetException localInvocationTargetException2)
    {
      localObject2 = localInvocationTargetException2.getTargetException();
      if (!(localObject2 instanceof RemoteException))
      {
        localConfigurationException = new ConfigurationException("Problem invoking javax.rmi.CORBA.Stub.connect()");
        localConfigurationException.setRootCause((Throwable)localObject2);
        throw localConfigurationException;
      }
    }
    catch (IllegalAccessException localIllegalAccessException2)
    {
      localObject2 = new ConfigurationException("Cannot invoke javax.rmi.CORBA.Stub.connect()");
      ((ConfigurationException)localObject2).setRootCause(localIllegalAccessException2);
      throw ((Throwable)localObject2);
    }
    return (org.omg.CORBA.Object)???;
  }
  
  public static ORB getOrb(String paramString, int paramInt, Hashtable<?, ?> paramHashtable)
  {
    Properties localProperties;
    Object localObject1;
    if (paramHashtable != null)
    {
      if ((paramHashtable instanceof Properties))
      {
        localProperties = (Properties)paramHashtable.clone();
      }
      else
      {
        localProperties = new Properties();
        localObject1 = paramHashtable.keys();
        while (((Enumeration)localObject1).hasMoreElements())
        {
          String str = (String)((Enumeration)localObject1).nextElement();
          Object localObject2 = paramHashtable.get(str);
          if ((localObject2 instanceof String)) {
            localProperties.put(str, localObject2);
          }
        }
      }
    }
    else {
      localProperties = new Properties();
    }
    if (paramString != null) {
      localProperties.put("org.omg.CORBA.ORBInitialHost", paramString);
    }
    if (paramInt >= 0) {
      localProperties.put("org.omg.CORBA.ORBInitialPort", "" + paramInt);
    }
    if (paramHashtable != null)
    {
      localObject1 = paramHashtable.get("java.naming.applet");
      if (localObject1 != null) {
        return initAppletORB(localObject1, localProperties);
      }
    }
    return ORB.init(new String[0], localProperties);
  }
  
  public static boolean isObjectFactoryTrusted(Object paramObject)
    throws NamingException
  {
    Reference localReference = null;
    if ((paramObject instanceof Reference)) {
      localReference = (Reference)paramObject;
    } else if ((paramObject instanceof Referenceable)) {
      localReference = ((Referenceable)paramObject).getReference();
    }
    if ((localReference != null) && (localReference.getFactoryClassLocation() != null) && (!CNCtx.trustURLCodebase)) {
      throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.cosnaming.object.trustURLCodebase' to 'true'.");
    }
    return true;
  }
  
  private static ORB initAppletORB(Object paramObject, Properties paramProperties)
  {
    try
    {
      Class localClass = Class.forName("java.applet.Applet", true, null);
      if (!localClass.isInstance(paramObject)) {
        throw new ClassCastException(paramObject.getClass().getName());
      }
      localObject = ORB.class.getMethod("init", new Class[] { localClass, Properties.class });
      return (ORB)((Method)localObject).invoke(null, new Object[] { paramObject, paramProperties });
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ClassCastException(paramObject.getClass().getName());
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new AssertionError(localNoSuchMethodException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject = localInvocationTargetException.getCause();
      if ((localObject instanceof RuntimeException)) {
        throw ((RuntimeException)localObject);
      }
      if ((localObject instanceof Error)) {
        throw ((Error)localObject);
      }
      throw new AssertionError(localInvocationTargetException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError(localIllegalAccessException);
    }
  }
  
  private static void initMethodHandles()
    throws ClassNotFoundException
  {
    corbaStubClass = Class.forName("javax.rmi.CORBA.Stub");
    try
    {
      connectMethod = corbaStubClass.getMethod("connect", new Class[] { ORB.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      throw new IllegalStateException("No method definition for javax.rmi.CORBA.Stub.connect(org.omg.CORBA.ORB)");
    }
    Class localClass = Class.forName("javax.rmi.PortableRemoteObject");
    try
    {
      toStubMethod = localClass.getMethod("toStub", new Class[] { Remote.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException2)
    {
      throw new IllegalStateException("No method definition for javax.rmi.PortableRemoteObject.toStub(java.rmi.Remote)");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\corba\CorbaUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
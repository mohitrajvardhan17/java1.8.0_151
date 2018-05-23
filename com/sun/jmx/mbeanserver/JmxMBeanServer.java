package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import java.io.ObjectInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;

public final class JmxMBeanServer
  implements SunJmxMBeanServer
{
  public static final boolean DEFAULT_FAIR_LOCK_POLICY = true;
  private final MBeanInstantiator instantiator;
  private final SecureClassLoaderRepository secureClr;
  private final boolean interceptorsEnabled;
  private final MBeanServer outerShell;
  private volatile MBeanServer mbsInterceptor = null;
  private final MBeanServerDelegate mBeanServerDelegateObject;
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate)
  {
    this(paramString, paramMBeanServer, paramMBeanServerDelegate, null, false);
  }
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, boolean paramBoolean)
  {
    this(paramString, paramMBeanServer, paramMBeanServerDelegate, null, false);
  }
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, boolean paramBoolean)
  {
    this(paramString, paramMBeanServer, paramMBeanServerDelegate, paramMBeanInstantiator, paramBoolean, true);
  }
  
  JmxMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramMBeanInstantiator == null)
    {
      localObject = new ClassLoaderRepositorySupport();
      paramMBeanInstantiator = new MBeanInstantiator((ModifiableClassLoaderRepository)localObject);
    }
    final Object localObject = paramMBeanInstantiator;
    secureClr = new SecureClassLoaderRepository((ClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoaderRepository run()
      {
        return localObject.getClassLoaderRepository();
      }
    }));
    if (paramMBeanServerDelegate == null) {
      paramMBeanServerDelegate = new MBeanServerDelegateImpl();
    }
    if (paramMBeanServer == null) {
      paramMBeanServer = this;
    }
    instantiator = paramMBeanInstantiator;
    mBeanServerDelegateObject = paramMBeanServerDelegate;
    outerShell = paramMBeanServer;
    Repository localRepository = new Repository(paramString);
    mbsInterceptor = new DefaultMBeanServerInterceptor(paramMBeanServer, paramMBeanServerDelegate, paramMBeanInstantiator, localRepository);
    interceptorsEnabled = paramBoolean1;
    initialize();
  }
  
  public boolean interceptorsEnabled()
  {
    return interceptorsEnabled;
  }
  
  public MBeanInstantiator getMBeanInstantiator()
  {
    if (interceptorsEnabled) {
      return instantiator;
    }
    throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
  {
    return mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName), (Object[])null, (String[])null);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
  {
    return mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName1), paramObjectName2, (Object[])null, (String[])null);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
  {
    return mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName), paramArrayOfObject, paramArrayOfString);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
  {
    return mbsInterceptor.createMBean(paramString, cloneObjectName(paramObjectName1), paramObjectName2, paramArrayOfObject, paramArrayOfString);
  }
  
  public ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
  {
    return mbsInterceptor.registerMBean(paramObject, cloneObjectName(paramObjectName));
  }
  
  public void unregisterMBean(ObjectName paramObjectName)
    throws InstanceNotFoundException, MBeanRegistrationException
  {
    mbsInterceptor.unregisterMBean(cloneObjectName(paramObjectName));
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    return mbsInterceptor.getObjectInstance(cloneObjectName(paramObjectName));
  }
  
  public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    return mbsInterceptor.queryMBeans(cloneObjectName(paramObjectName), paramQueryExp);
  }
  
  public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    return mbsInterceptor.queryNames(cloneObjectName(paramObjectName), paramQueryExp);
  }
  
  public boolean isRegistered(ObjectName paramObjectName)
  {
    return mbsInterceptor.isRegistered(paramObjectName);
  }
  
  public Integer getMBeanCount()
  {
    return mbsInterceptor.getMBeanCount();
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString)
    throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
  {
    return mbsInterceptor.getAttribute(cloneObjectName(paramObjectName), paramString);
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString)
    throws InstanceNotFoundException, ReflectionException
  {
    return mbsInterceptor.getAttributes(cloneObjectName(paramObjectName), paramArrayOfString);
  }
  
  public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute)
    throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    mbsInterceptor.setAttribute(cloneObjectName(paramObjectName), cloneAttribute(paramAttribute));
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList)
    throws InstanceNotFoundException, ReflectionException
  {
    return mbsInterceptor.setAttributes(cloneObjectName(paramObjectName), cloneAttributeList(paramAttributeList));
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws InstanceNotFoundException, MBeanException, ReflectionException
  {
    return mbsInterceptor.invoke(cloneObjectName(paramObjectName), paramString, paramArrayOfObject, paramArrayOfString);
  }
  
  public String getDefaultDomain()
  {
    return mbsInterceptor.getDefaultDomain();
  }
  
  public String[] getDomains()
  {
    return mbsInterceptor.getDomains();
  }
  
  public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException
  {
    mbsInterceptor.addNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException
  {
    mbsInterceptor.addNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName), paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    mbsInterceptor.removeNotificationListener(cloneObjectName(paramObjectName1), paramObjectName2, paramNotificationFilter, paramObject);
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName)
    throws InstanceNotFoundException, IntrospectionException, ReflectionException
  {
    return mbsInterceptor.getMBeanInfo(cloneObjectName(paramObjectName));
  }
  
  public Object instantiate(String paramString)
    throws ReflectionException, MBeanException
  {
    checkMBeanPermission(paramString, null, null, "instantiate");
    return instantiator.instantiate(paramString);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName)
    throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    checkMBeanPermission(paramString, null, null, "instantiate");
    ClassLoader localClassLoader = outerShell.getClass().getClassLoader();
    return instantiator.instantiate(paramString, paramObjectName, localClassLoader);
  }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, MBeanException
  {
    checkMBeanPermission(paramString, null, null, "instantiate");
    ClassLoader localClassLoader = outerShell.getClass().getClassLoader();
    return instantiator.instantiate(paramString, paramArrayOfObject, paramArrayOfString, localClassLoader);
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    checkMBeanPermission(paramString, null, null, "instantiate");
    ClassLoader localClassLoader = outerShell.getClass().getClassLoader();
    return instantiator.instantiate(paramString, paramObjectName, paramArrayOfObject, paramArrayOfString, localClassLoader);
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString)
    throws InstanceNotFoundException
  {
    return mbsInterceptor.isInstanceOf(cloneObjectName(paramObjectName), paramString);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte)
    throws InstanceNotFoundException, OperationsException
  {
    ClassLoader localClassLoader = getClassLoaderFor(paramObjectName);
    return instantiator.deserialize(localClassLoader, paramArrayOfByte);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte)
    throws OperationsException, ReflectionException
  {
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
    }
    ClassLoaderRepository localClassLoaderRepository = getClassLoaderRepository();
    Class localClass;
    try
    {
      if (localClassLoaderRepository == null) {
        throw new ClassNotFoundException(paramString);
      }
      localClass = localClassLoaderRepository.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ReflectionException(localClassNotFoundException, "The given class could not be loaded by the default loader repository");
    }
    return instantiator.deserialize(localClass.getClassLoader(), paramArrayOfByte);
  }
  
  @Deprecated
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte)
    throws InstanceNotFoundException, OperationsException, ReflectionException
  {
    paramObjectName = cloneObjectName(paramObjectName);
    try
    {
      getClassLoader(paramObjectName);
    }
    catch (SecurityException localSecurityException)
    {
      throw localSecurityException;
    }
    catch (Exception localException) {}
    ClassLoader localClassLoader = outerShell.getClass().getClassLoader();
    return instantiator.deserialize(paramString, paramObjectName, paramArrayOfByte, localClassLoader);
  }
  
  private void initialize()
  {
    if (instantiator == null) {
      throw new IllegalStateException("instantiator must not be null.");
    }
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          mbsInterceptor.registerMBean(mBeanServerDelegateObject, MBeanServerDelegate.DELEGATE_NAME);
          return null;
        }
      });
    }
    catch (SecurityException localSecurityException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected security exception occurred", localSecurityException);
      }
      throw localSecurityException;
    }
    catch (Exception localException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected exception occurred", localException);
      }
      throw new IllegalStateException("Can't register delegate.", localException);
    }
    ClassLoader localClassLoader1 = outerShell.getClass().getClassLoader();
    ModifiableClassLoaderRepository localModifiableClassLoaderRepository = (ModifiableClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ModifiableClassLoaderRepository run()
      {
        return instantiator.getClassLoaderRepository();
      }
    });
    if (localModifiableClassLoaderRepository != null)
    {
      localModifiableClassLoaderRepository.addClassLoader(localClassLoader1);
      ClassLoader localClassLoader2 = ClassLoader.getSystemClassLoader();
      if (localClassLoader2 != localClassLoader1) {
        localModifiableClassLoaderRepository.addClassLoader(localClassLoader2);
      }
    }
  }
  
  public synchronized MBeanServer getMBeanServerInterceptor()
  {
    if (interceptorsEnabled) {
      return mbsInterceptor;
    }
    throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
  }
  
  public synchronized void setMBeanServerInterceptor(MBeanServer paramMBeanServer)
  {
    if (!interceptorsEnabled) {
      throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
    }
    if (paramMBeanServer == null) {
      throw new IllegalArgumentException("MBeanServerInterceptor is null");
    }
    mbsInterceptor = paramMBeanServer;
  }
  
  public ClassLoader getClassLoaderFor(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    return mbsInterceptor.getClassLoaderFor(cloneObjectName(paramObjectName));
  }
  
  public ClassLoader getClassLoader(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    return mbsInterceptor.getClassLoader(cloneObjectName(paramObjectName));
  }
  
  public ClassLoaderRepository getClassLoaderRepository()
  {
    checkMBeanPermission(null, null, null, "getClassLoaderRepository");
    return secureClr;
  }
  
  public MBeanServerDelegate getMBeanServerDelegate()
  {
    if (!interceptorsEnabled) {
      throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
    }
    return mBeanServerDelegateObject;
  }
  
  public static MBeanServerDelegate newMBeanServerDelegate()
  {
    return new MBeanServerDelegateImpl();
  }
  
  public static MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, boolean paramBoolean)
  {
    checkNewMBeanServerPermission();
    return new JmxMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, null, paramBoolean, true);
  }
  
  private ObjectName cloneObjectName(ObjectName paramObjectName)
  {
    if (paramObjectName != null) {
      return ObjectName.getInstance(paramObjectName);
    }
    return paramObjectName;
  }
  
  private Attribute cloneAttribute(Attribute paramAttribute)
  {
    if ((paramAttribute != null) && (!paramAttribute.getClass().equals(Attribute.class))) {
      return new Attribute(paramAttribute.getName(), paramAttribute.getValue());
    }
    return paramAttribute;
  }
  
  private AttributeList cloneAttributeList(AttributeList paramAttributeList)
  {
    if (paramAttributeList != null)
    {
      List localList = paramAttributeList.asList();
      Object localObject;
      if (!paramAttributeList.getClass().equals(AttributeList.class))
      {
        AttributeList localAttributeList = new AttributeList(localList.size());
        localObject = localList.iterator();
        while (((Iterator)localObject).hasNext())
        {
          Attribute localAttribute = (Attribute)((Iterator)localObject).next();
          localAttributeList.add(cloneAttribute(localAttribute));
        }
        return localAttributeList;
      }
      for (int i = 0; i < localList.size(); i++)
      {
        localObject = (Attribute)localList.get(i);
        if (!localObject.getClass().equals(Attribute.class)) {
          paramAttributeList.set(i, cloneAttribute((Attribute)localObject));
        }
      }
      return paramAttributeList;
    }
    return paramAttributeList;
  }
  
  private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      MBeanPermission localMBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
      localSecurityManager.checkPermission(localMBeanPermission);
    }
  }
  
  private static void checkNewMBeanServerPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      MBeanServerPermission localMBeanServerPermission = new MBeanServerPermission("newMBeanServer");
      localSecurityManager.checkPermission(localMBeanServerPermission);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\JmxMBeanServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
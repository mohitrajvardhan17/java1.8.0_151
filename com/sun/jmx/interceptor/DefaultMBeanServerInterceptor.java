package com.sun.jmx.interceptor;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.DynamicMBean2;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.MBeanInstantiator;
import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import com.sun.jmx.mbeanserver.Repository.RegistrationContext;
import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MBeanTrustPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;

public class DefaultMBeanServerInterceptor
  implements MBeanServerInterceptor
{
  private final transient MBeanInstantiator instantiator;
  private transient MBeanServer server = null;
  private final transient MBeanServerDelegate delegate;
  private final transient Repository repository;
  private final transient WeakHashMap<ListenerWrapper, WeakReference<ListenerWrapper>> listenerWrappers = new WeakHashMap();
  private final String domain;
  private final Set<ObjectName> beingUnregistered = new HashSet();
  
  public DefaultMBeanServerInterceptor(MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, Repository paramRepository)
  {
    if (paramMBeanServer == null) {
      throw new IllegalArgumentException("outer MBeanServer cannot be null");
    }
    if (paramMBeanServerDelegate == null) {
      throw new IllegalArgumentException("MBeanServerDelegate cannot be null");
    }
    if (paramMBeanInstantiator == null) {
      throw new IllegalArgumentException("MBeanInstantiator cannot be null");
    }
    if (paramRepository == null) {
      throw new IllegalArgumentException("Repository cannot be null");
    }
    server = paramMBeanServer;
    delegate = paramMBeanServerDelegate;
    instantiator = paramMBeanInstantiator;
    repository = paramRepository;
    domain = paramRepository.getDefaultDomain();
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
  {
    return createMBean(paramString, paramObjectName, (Object[])null, (String[])null);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
  {
    return createMBean(paramString, paramObjectName1, paramObjectName2, (Object[])null, (String[])null);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
  {
    try
    {
      return createMBean(paramString, paramObjectName, null, true, paramArrayOfObject, paramArrayOfString);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw ((IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException("Unexpected exception: " + localInstanceNotFoundException), localInstanceNotFoundException));
    }
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
  {
    return createMBean(paramString, paramObjectName1, paramObjectName2, false, paramArrayOfObject, paramArrayOfString);
  }
  
  private ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, boolean paramBoolean, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
  {
    if (paramString == null)
    {
      localObject = new IllegalArgumentException("The class name cannot be null");
      throw new RuntimeOperationsException((RuntimeException)localObject, "Exception occurred during MBean creation");
    }
    if (paramObjectName1 != null)
    {
      if (paramObjectName1.isPattern())
      {
        localObject = new IllegalArgumentException("Invalid name->" + paramObjectName1.toString());
        throw new RuntimeOperationsException((RuntimeException)localObject, "Exception occurred during MBean creation");
      }
      paramObjectName1 = nonDefaultDomain(paramObjectName1);
    }
    checkMBeanPermission(paramString, null, null, "instantiate");
    checkMBeanPermission(paramString, null, paramObjectName1, "registerMBean");
    Class localClass;
    if (paramBoolean)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + paramString + ", ObjectName = " + paramObjectName1);
      }
      localClass = instantiator.findClassWithDefaultLoaderRepository(paramString);
    }
    else if (paramObjectName2 == null)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + paramString + ", ObjectName = " + paramObjectName1 + ", Loader name = null");
      }
      localClass = instantiator.findClass(paramString, server.getClass().getClassLoader());
    }
    else
    {
      paramObjectName2 = nonDefaultDomain(paramObjectName2);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + paramString + ", ObjectName = " + paramObjectName1 + ", Loader name = " + paramObjectName2);
      }
      localClass = instantiator.findClass(paramString, paramObjectName2);
    }
    checkMBeanTrustPermission(localClass);
    Introspector.testCreation(localClass);
    Introspector.checkCompliance(localClass);
    Object localObject = instantiator.instantiate(localClass, paramArrayOfObject, paramArrayOfString, server.getClass().getClassLoader());
    String str = getNewMBeanClassName(localObject);
    return registerObject(str, localObject, paramObjectName1);
  }
  
  public ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
  {
    Class localClass = paramObject.getClass();
    Introspector.checkCompliance(localClass);
    String str = getNewMBeanClassName(paramObject);
    checkMBeanPermission(str, null, paramObjectName, "registerMBean");
    checkMBeanTrustPermission(localClass);
    return registerObject(str, paramObject, paramObjectName);
  }
  
  private static String getNewMBeanClassName(Object paramObject)
    throws NotCompliantMBeanException
  {
    if ((paramObject instanceof DynamicMBean))
    {
      DynamicMBean localDynamicMBean = (DynamicMBean)paramObject;
      String str;
      try
      {
        str = localDynamicMBean.getMBeanInfo().getClassName();
      }
      catch (Exception localException)
      {
        NotCompliantMBeanException localNotCompliantMBeanException = new NotCompliantMBeanException("Bad getMBeanInfo()");
        localNotCompliantMBeanException.initCause(localException);
        throw localNotCompliantMBeanException;
      }
      if (str == null) {
        throw new NotCompliantMBeanException("MBeanInfo has null class name");
      }
      return str;
    }
    return paramObject.getClass().getName();
  }
  
  public void unregisterMBean(ObjectName paramObjectName)
    throws InstanceNotFoundException, MBeanRegistrationException
  {
    if (paramObjectName == null)
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Object name cannot be null");
      throw new RuntimeOperationsException(localIllegalArgumentException, "Exception occurred trying to unregister the MBean");
    }
    paramObjectName = nonDefaultDomain(paramObjectName);
    synchronized (beingUnregistered)
    {
      while (beingUnregistered.contains(paramObjectName)) {
        try
        {
          beingUnregistered.wait();
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new MBeanRegistrationException(localInterruptedException, localInterruptedException.toString());
        }
      }
      beingUnregistered.add(paramObjectName);
    }
    try
    {
      exclusiveUnregisterMBean(paramObjectName);
    }
    finally
    {
      synchronized (beingUnregistered)
      {
        beingUnregistered.remove(paramObjectName);
        beingUnregistered.notifyAll();
      }
    }
  }
  
  private void exclusiveUnregisterMBean(ObjectName paramObjectName)
    throws InstanceNotFoundException, MBeanRegistrationException
  {
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "unregisterMBean");
    if ((localDynamicMBean instanceof MBeanRegistration)) {
      preDeregisterInvoke((MBeanRegistration)localDynamicMBean);
    }
    Object localObject1 = getResource(localDynamicMBean);
    ResourceContext localResourceContext = unregisterFromRepository(localObject1, localDynamicMBean, paramObjectName);
    try
    {
      if ((localDynamicMBean instanceof MBeanRegistration)) {
        postDeregisterInvoke(paramObjectName, (MBeanRegistration)localDynamicMBean);
      }
    }
    finally
    {
      localResourceContext.done();
    }
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    paramObjectName = nonDefaultDomain(paramObjectName);
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "getObjectInstance");
    String str = getClassName(localDynamicMBean);
    return new ObjectInstance(paramObjectName, str);
  }
  
  public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      checkMBeanPermission((String)null, null, null, "queryMBeans");
      Set localSet = queryMBeansImpl(paramObjectName, null);
      HashSet localHashSet = new HashSet(localSet.size());
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        ObjectInstance localObjectInstance = (ObjectInstance)localIterator.next();
        try
        {
          checkMBeanPermission(localObjectInstance.getClassName(), null, localObjectInstance.getObjectName(), "queryMBeans");
          localHashSet.add(localObjectInstance);
        }
        catch (SecurityException localSecurityException) {}
      }
      return filterListOfObjectInstances(localHashSet, paramQueryExp);
    }
    return queryMBeansImpl(paramObjectName, paramQueryExp);
  }
  
  private Set<ObjectInstance> queryMBeansImpl(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    Set localSet = repository.query(paramObjectName, paramQueryExp);
    return objectInstancesFromFilteredNamedObjects(localSet, paramQueryExp);
  }
  
  public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    Object localObject1;
    if (localSecurityManager != null)
    {
      checkMBeanPermission((String)null, null, null, "queryNames");
      Set localSet = queryMBeansImpl(paramObjectName, null);
      HashSet localHashSet = new HashSet(localSet.size());
      Object localObject2 = localSet.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ObjectInstance)((Iterator)localObject2).next();
        try
        {
          checkMBeanPermission(((ObjectInstance)localObject3).getClassName(), null, ((ObjectInstance)localObject3).getObjectName(), "queryNames");
          localHashSet.add(localObject3);
        }
        catch (SecurityException localSecurityException) {}
      }
      localObject2 = filterListOfObjectInstances(localHashSet, paramQueryExp);
      localObject1 = new HashSet(((Set)localObject2).size());
      Object localObject3 = ((Set)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        ObjectInstance localObjectInstance = (ObjectInstance)((Iterator)localObject3).next();
        ((Set)localObject1).add(localObjectInstance.getObjectName());
      }
    }
    else
    {
      localObject1 = queryNamesImpl(paramObjectName, paramQueryExp);
    }
    return (Set<ObjectName>)localObject1;
  }
  
  private Set<ObjectName> queryNamesImpl(ObjectName paramObjectName, QueryExp paramQueryExp)
  {
    Set localSet = repository.query(paramObjectName, paramQueryExp);
    return objectNamesFromFilteredNamedObjects(localSet, paramQueryExp);
  }
  
  public boolean isRegistered(ObjectName paramObjectName)
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Object name cannot be null");
    }
    paramObjectName = nonDefaultDomain(paramObjectName);
    return repository.contains(paramObjectName);
  }
  
  public String[] getDomains()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      checkMBeanPermission((String)null, null, null, "getDomains");
      String[] arrayOfString = repository.getDomains();
      ArrayList localArrayList = new ArrayList(arrayOfString.length);
      for (int i = 0; i < arrayOfString.length; i++) {
        try
        {
          ObjectName localObjectName = Util.newObjectName(arrayOfString[i] + ":x=x");
          checkMBeanPermission((String)null, null, localObjectName, "getDomains");
          localArrayList.add(arrayOfString[i]);
        }
        catch (SecurityException localSecurityException) {}
      }
      return (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    return repository.getDomains();
  }
  
  public Integer getMBeanCount()
  {
    return repository.getCount();
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString)
    throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
    }
    if (paramString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
    }
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getAttribute", "Attribute = " + paramString + ", ObjectName = " + paramObjectName);
    }
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, paramString, paramObjectName, "getAttribute");
    try
    {
      return localDynamicMBean.getAttribute(paramString);
    }
    catch (AttributeNotFoundException localAttributeNotFoundException)
    {
      throw localAttributeNotFoundException;
    }
    catch (Throwable localThrowable)
    {
      rethrowMaybeMBeanException(localThrowable);
      throw new AssertionError();
    }
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString)
    throws InstanceNotFoundException, ReflectionException
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
    }
    if (paramArrayOfString == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attributes cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
    }
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getAttributes", "ObjectName = " + paramObjectName);
    }
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    SecurityManager localSecurityManager = System.getSecurityManager();
    String[] arrayOfString1;
    if (localSecurityManager == null)
    {
      arrayOfString1 = paramArrayOfString;
    }
    else
    {
      String str1 = getClassName(localDynamicMBean);
      checkMBeanPermission(str1, null, paramObjectName, "getAttribute");
      ArrayList localArrayList = new ArrayList(paramArrayOfString.length);
      for (String str2 : paramArrayOfString) {
        try
        {
          checkMBeanPermission(str1, str2, paramObjectName, "getAttribute");
          localArrayList.add(str2);
        }
        catch (SecurityException localSecurityException) {}
      }
      arrayOfString1 = (String[])localArrayList.toArray(new String[localArrayList.size()]);
    }
    try
    {
      return localDynamicMBean.getAttributes(arrayOfString1);
    }
    catch (Throwable localThrowable)
    {
      rethrow(localThrowable);
      throw new AssertionError();
    }
  }
  
  public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute)
    throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
    }
    if (paramAttribute == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
    }
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "setAttribute", "ObjectName = " + paramObjectName + ", Attribute = " + paramAttribute.getName());
    }
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, paramAttribute.getName(), paramObjectName, "setAttribute");
    try
    {
      localDynamicMBean.setAttribute(paramAttribute);
    }
    catch (AttributeNotFoundException localAttributeNotFoundException)
    {
      throw localAttributeNotFoundException;
    }
    catch (InvalidAttributeValueException localInvalidAttributeValueException)
    {
      throw localInvalidAttributeValueException;
    }
    catch (Throwable localThrowable)
    {
      rethrowMaybeMBeanException(localThrowable);
      throw new AssertionError();
    }
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList)
    throws InstanceNotFoundException, ReflectionException
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
    }
    if (paramAttributeList == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList  cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
    }
    paramObjectName = nonDefaultDomain(paramObjectName);
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    SecurityManager localSecurityManager = System.getSecurityManager();
    AttributeList localAttributeList;
    if (localSecurityManager == null)
    {
      localAttributeList = paramAttributeList;
    }
    else
    {
      String str = getClassName(localDynamicMBean);
      checkMBeanPermission(str, null, paramObjectName, "setAttribute");
      localAttributeList = new AttributeList(paramAttributeList.size());
      Iterator localIterator = paramAttributeList.asList().iterator();
      while (localIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)localIterator.next();
        try
        {
          checkMBeanPermission(str, localAttribute.getName(), paramObjectName, "setAttribute");
          localAttributeList.add(localAttribute);
        }
        catch (SecurityException localSecurityException) {}
      }
    }
    try
    {
      return localDynamicMBean.setAttributes(localAttributeList);
    }
    catch (Throwable localThrowable)
    {
      rethrow(localThrowable);
      throw new AssertionError();
    }
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws InstanceNotFoundException, MBeanException, ReflectionException
  {
    paramObjectName = nonDefaultDomain(paramObjectName);
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, paramString, paramObjectName, "invoke");
    try
    {
      return localDynamicMBean.invoke(paramString, paramArrayOfObject, paramArrayOfString);
    }
    catch (Throwable localThrowable)
    {
      rethrowMaybeMBeanException(localThrowable);
      throw new AssertionError();
    }
  }
  
  private static void rethrow(Throwable paramThrowable)
    throws ReflectionException
  {
    try
    {
      throw paramThrowable;
    }
    catch (ReflectionException localReflectionException)
    {
      throw localReflectionException;
    }
    catch (RuntimeOperationsException localRuntimeOperationsException)
    {
      throw localRuntimeOperationsException;
    }
    catch (RuntimeErrorException localRuntimeErrorException)
    {
      throw localRuntimeErrorException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new RuntimeMBeanException(localRuntimeException, localRuntimeException.toString());
    }
    catch (Error localError)
    {
      throw new RuntimeErrorException(localError, localError.toString());
    }
    catch (Throwable localThrowable)
    {
      throw new RuntimeException("Unexpected exception", localThrowable);
    }
  }
  
  private static void rethrowMaybeMBeanException(Throwable paramThrowable)
    throws ReflectionException, MBeanException
  {
    if ((paramThrowable instanceof MBeanException)) {
      throw ((MBeanException)paramThrowable);
    }
    rethrow(paramThrowable);
  }
  
  private ObjectInstance registerObject(String paramString, Object paramObject, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
  {
    if (paramObject == null)
    {
      localObject = new IllegalArgumentException("Cannot add null object");
      throw new RuntimeOperationsException((RuntimeException)localObject, "Exception occurred trying to register the MBean");
    }
    Object localObject = Introspector.makeDynamicMBean(paramObject);
    return registerDynamicMBean(paramString, (DynamicMBean)localObject, paramObjectName);
  }
  
  private ObjectInstance registerDynamicMBean(String paramString, DynamicMBean paramDynamicMBean, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
  {
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "registerMBean", "ObjectName = " + paramObjectName);
    }
    ObjectName localObjectName = preRegister(paramDynamicMBean, server, paramObjectName);
    boolean bool1 = false;
    boolean bool2 = false;
    ResourceContext localResourceContext = null;
    try
    {
      if ((paramDynamicMBean instanceof DynamicMBean2)) {
        try
        {
          ((DynamicMBean2)paramDynamicMBean).preRegister2(server, localObjectName);
          bool2 = true;
        }
        catch (Exception localException)
        {
          if ((localException instanceof RuntimeException)) {
            throw ((RuntimeException)localException);
          }
          if ((localException instanceof InstanceAlreadyExistsException)) {
            throw ((InstanceAlreadyExistsException)localException);
          }
          throw new RuntimeException(localException);
        }
      }
      if ((localObjectName != paramObjectName) && (localObjectName != null)) {
        localObjectName = ObjectName.getInstance(nonDefaultDomain(localObjectName));
      }
      checkMBeanPermission(paramString, null, localObjectName, "registerMBean");
      if (localObjectName == null)
      {
        localObject1 = new IllegalArgumentException("No object name specified");
        throw new RuntimeOperationsException((RuntimeException)localObject1, "Exception occurred trying to register the MBean");
      }
      Object localObject1 = getResource(paramDynamicMBean);
      localResourceContext = registerWithRepository(localObject1, paramDynamicMBean, localObjectName);
      bool2 = false;
      bool1 = true;
    }
    finally
    {
      try
      {
        postRegister(localObjectName, paramDynamicMBean, bool1, bool2);
      }
      finally
      {
        if ((bool1) && (localResourceContext != null)) {
          localResourceContext.done();
        }
      }
    }
    return new ObjectInstance(localObjectName, paramString);
  }
  
  private static void throwMBeanRegistrationException(Throwable paramThrowable, String paramString)
    throws MBeanRegistrationException
  {
    if ((paramThrowable instanceof RuntimeException)) {
      throw new RuntimeMBeanException((RuntimeException)paramThrowable, "RuntimeException thrown " + paramString);
    }
    if ((paramThrowable instanceof Error)) {
      throw new RuntimeErrorException((Error)paramThrowable, "Error thrown " + paramString);
    }
    if ((paramThrowable instanceof MBeanRegistrationException)) {
      throw ((MBeanRegistrationException)paramThrowable);
    }
    if ((paramThrowable instanceof Exception)) {
      throw new MBeanRegistrationException((Exception)paramThrowable, "Exception thrown " + paramString);
    }
    throw new RuntimeException(paramThrowable);
  }
  
  private static ObjectName preRegister(DynamicMBean paramDynamicMBean, MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException, MBeanRegistrationException
  {
    ObjectName localObjectName = null;
    try
    {
      if ((paramDynamicMBean instanceof MBeanRegistration)) {
        localObjectName = ((MBeanRegistration)paramDynamicMBean).preRegister(paramMBeanServer, paramObjectName);
      }
    }
    catch (Throwable localThrowable)
    {
      throwMBeanRegistrationException(localThrowable, "in preRegister method");
    }
    if (localObjectName != null) {
      return localObjectName;
    }
    return paramObjectName;
  }
  
  private static void postRegister(ObjectName paramObjectName, DynamicMBean paramDynamicMBean, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean2) && ((paramDynamicMBean instanceof DynamicMBean2))) {
      ((DynamicMBean2)paramDynamicMBean).registerFailed();
    }
    try
    {
      if ((paramDynamicMBean instanceof MBeanRegistration)) {
        ((MBeanRegistration)paramDynamicMBean).postRegister(Boolean.valueOf(paramBoolean1));
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + paramObjectName + "]: Exception thrown by postRegister: rethrowing <" + localRuntimeException + ">, but keeping the MBean registered");
      throw new RuntimeMBeanException(localRuntimeException, "RuntimeException thrown in postRegister method: rethrowing <" + localRuntimeException + ">, but keeping the MBean registered");
    }
    catch (Error localError)
    {
      JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + paramObjectName + "]: Error thrown by postRegister: rethrowing <" + localError + ">, but keeping the MBean registered");
      throw new RuntimeErrorException(localError, "Error thrown in postRegister method: rethrowing <" + localError + ">, but keeping the MBean registered");
    }
  }
  
  private static void preDeregisterInvoke(MBeanRegistration paramMBeanRegistration)
    throws MBeanRegistrationException
  {
    try
    {
      paramMBeanRegistration.preDeregister();
    }
    catch (Throwable localThrowable)
    {
      throwMBeanRegistrationException(localThrowable, "in preDeregister method");
    }
  }
  
  private static void postDeregisterInvoke(ObjectName paramObjectName, MBeanRegistration paramMBeanRegistration)
  {
    try
    {
      paramMBeanRegistration.postDeregister();
    }
    catch (RuntimeException localRuntimeException)
    {
      JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + paramObjectName + "]: Exception thrown by postDeregister: rethrowing <" + localRuntimeException + ">, although the MBean is succesfully unregistered");
      throw new RuntimeMBeanException(localRuntimeException, "RuntimeException thrown in postDeregister method: rethrowing <" + localRuntimeException + ">, although the MBean is sucessfully unregistered");
    }
    catch (Error localError)
    {
      JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + paramObjectName + "]: Error thrown by postDeregister: rethrowing <" + localError + ">, although the MBean is succesfully unregistered");
      throw new RuntimeErrorException(localError, "Error thrown in postDeregister method: rethrowing <" + localError + ">, although the MBean is sucessfully unregistered");
    }
  }
  
  private DynamicMBean getMBean(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    if (paramObjectName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to get an MBean");
    }
    DynamicMBean localDynamicMBean = repository.retrieve(paramObjectName);
    if (localDynamicMBean == null)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getMBean", paramObjectName + " : Found no object");
      }
      throw new InstanceNotFoundException(paramObjectName.toString());
    }
    return localDynamicMBean;
  }
  
  private static Object getResource(DynamicMBean paramDynamicMBean)
  {
    if ((paramDynamicMBean instanceof DynamicMBean2)) {
      return ((DynamicMBean2)paramDynamicMBean).getResource();
    }
    return paramDynamicMBean;
  }
  
  private ObjectName nonDefaultDomain(ObjectName paramObjectName)
  {
    if ((paramObjectName == null) || (paramObjectName.getDomain().length() > 0)) {
      return paramObjectName;
    }
    String str = domain + paramObjectName;
    return Util.newObjectName(str);
  }
  
  public String getDefaultDomain()
  {
    return domain;
  }
  
  public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addNotificationListener", "ObjectName = " + paramObjectName);
    }
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "addNotificationListener");
    NotificationBroadcaster localNotificationBroadcaster = getNotificationBroadcaster(paramObjectName, localDynamicMBean, NotificationBroadcaster.class);
    if (paramNotificationListener == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Null listener"), "Null listener");
    }
    NotificationListener localNotificationListener = getListenerWrapper(paramNotificationListener, paramObjectName, localDynamicMBean, true);
    localNotificationBroadcaster.addNotificationListener(localNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException
  {
    DynamicMBean localDynamicMBean = getMBean(paramObjectName2);
    Object localObject = getResource(localDynamicMBean);
    if (!(localObject instanceof NotificationListener)) {
      throw new RuntimeOperationsException(new IllegalArgumentException(paramObjectName2.getCanonicalName()), "The MBean " + paramObjectName2.getCanonicalName() + "does not implement the NotificationListener interface");
    }
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addNotificationListener", "ObjectName = " + paramObjectName1 + ", Listener = " + paramObjectName2);
    }
    server.addNotificationListener(paramObjectName1, (NotificationListener)localObject, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    removeNotificationListener(paramObjectName, paramNotificationListener, null, null, true);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    removeNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject, false);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    NotificationListener localNotificationListener = getListener(paramObjectName2);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + paramObjectName1 + ", Listener = " + paramObjectName2);
    }
    server.removeNotificationListener(paramObjectName1, localNotificationListener);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    NotificationListener localNotificationListener = getListener(paramObjectName2);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + paramObjectName1 + ", Listener = " + paramObjectName2);
    }
    server.removeNotificationListener(paramObjectName1, localNotificationListener, paramNotificationFilter, paramObject);
  }
  
  private NotificationListener getListener(ObjectName paramObjectName)
    throws ListenerNotFoundException
  {
    DynamicMBean localDynamicMBean;
    try
    {
      localDynamicMBean = getMBean(paramObjectName);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw ((ListenerNotFoundException)EnvHelp.initCause(new ListenerNotFoundException(localInstanceNotFoundException.getMessage()), localInstanceNotFoundException));
    }
    Object localObject = getResource(localDynamicMBean);
    if (!(localObject instanceof NotificationListener))
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(paramObjectName.getCanonicalName());
      String str = "MBean " + paramObjectName.getCanonicalName() + " does not implement " + NotificationListener.class.getName();
      throw new RuntimeOperationsException(localIllegalArgumentException, str);
    }
    return (NotificationListener)localObject;
  }
  
  private void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject, boolean paramBoolean)
    throws InstanceNotFoundException, ListenerNotFoundException
  {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + paramObjectName);
    }
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "removeNotificationListener");
    Class localClass = paramBoolean ? NotificationBroadcaster.class : NotificationEmitter.class;
    NotificationBroadcaster localNotificationBroadcaster = getNotificationBroadcaster(paramObjectName, localDynamicMBean, localClass);
    NotificationListener localNotificationListener = getListenerWrapper(paramNotificationListener, paramObjectName, localDynamicMBean, false);
    if (localNotificationListener == null) {
      throw new ListenerNotFoundException("Unknown listener");
    }
    if (paramBoolean)
    {
      localNotificationBroadcaster.removeNotificationListener(localNotificationListener);
    }
    else
    {
      NotificationEmitter localNotificationEmitter = (NotificationEmitter)localNotificationBroadcaster;
      localNotificationEmitter.removeNotificationListener(localNotificationListener, paramNotificationFilter, paramObject);
    }
  }
  
  private static <T extends NotificationBroadcaster> T getNotificationBroadcaster(ObjectName paramObjectName, Object paramObject, Class<T> paramClass)
  {
    if (paramClass.isInstance(paramObject)) {
      return (NotificationBroadcaster)paramClass.cast(paramObject);
    }
    if ((paramObject instanceof DynamicMBean2)) {
      paramObject = ((DynamicMBean2)paramObject).getResource();
    }
    if (paramClass.isInstance(paramObject)) {
      return (NotificationBroadcaster)paramClass.cast(paramObject);
    }
    IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(paramObjectName.getCanonicalName());
    String str = "MBean " + paramObjectName.getCanonicalName() + " does not implement " + paramClass.getName();
    throw new RuntimeOperationsException(localIllegalArgumentException, str);
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName)
    throws InstanceNotFoundException, IntrospectionException, ReflectionException
  {
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    MBeanInfo localMBeanInfo;
    try
    {
      localMBeanInfo = localDynamicMBean.getMBeanInfo();
    }
    catch (RuntimeMBeanException localRuntimeMBeanException)
    {
      throw localRuntimeMBeanException;
    }
    catch (RuntimeErrorException localRuntimeErrorException)
    {
      throw localRuntimeErrorException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new RuntimeMBeanException(localRuntimeException, "getMBeanInfo threw RuntimeException");
    }
    catch (Error localError)
    {
      throw new RuntimeErrorException(localError, "getMBeanInfo threw Error");
    }
    if (localMBeanInfo == null) {
      throw new JMRuntimeException("MBean " + paramObjectName + "has no MBeanInfo");
    }
    checkMBeanPermission(localMBeanInfo.getClassName(), null, paramObjectName, "getMBeanInfo");
    return localMBeanInfo;
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString)
    throws InstanceNotFoundException
  {
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "isInstanceOf");
    try
    {
      Object localObject = getResource(localDynamicMBean);
      String str = (localObject instanceof DynamicMBean) ? getClassName((DynamicMBean)localObject) : localObject.getClass().getName();
      if (str.equals(paramString)) {
        return true;
      }
      ClassLoader localClassLoader = localObject.getClass().getClassLoader();
      Class localClass1 = Class.forName(paramString, false, localClassLoader);
      if (localClass1.isInstance(localObject)) {
        return true;
      }
      Class localClass2 = Class.forName(str, false, localClassLoader);
      return localClass1.isAssignableFrom(localClass2);
    }
    catch (Exception localException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class.getName(), "isInstanceOf", "Exception calling isInstanceOf", localException);
      }
    }
    return false;
  }
  
  public ClassLoader getClassLoaderFor(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "getClassLoaderFor");
    return getResource(localDynamicMBean).getClass().getClassLoader();
  }
  
  public ClassLoader getClassLoader(ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    if (paramObjectName == null)
    {
      checkMBeanPermission((String)null, null, null, "getClassLoader");
      return server.getClass().getClassLoader();
    }
    DynamicMBean localDynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(localDynamicMBean, null, paramObjectName, "getClassLoader");
    Object localObject = getResource(localDynamicMBean);
    if (!(localObject instanceof ClassLoader)) {
      throw new InstanceNotFoundException(paramObjectName.toString() + " is not a classloader");
    }
    return (ClassLoader)localObject;
  }
  
  private void sendNotification(String paramString, ObjectName paramObjectName)
  {
    MBeanServerNotification localMBeanServerNotification = new MBeanServerNotification(paramString, MBeanServerDelegate.DELEGATE_NAME, 0L, paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "sendNotification", paramString + " " + paramObjectName);
    }
    delegate.sendNotification(localMBeanServerNotification);
  }
  
  private Set<ObjectName> objectNamesFromFilteredNamedObjects(Set<NamedObject> paramSet, QueryExp paramQueryExp)
  {
    HashSet localHashSet = new HashSet();
    Object localObject1;
    Object localObject2;
    if (paramQueryExp == null)
    {
      localObject1 = paramSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (NamedObject)((Iterator)localObject1).next();
        localHashSet.add(((NamedObject)localObject2).getName());
      }
    }
    else
    {
      localObject1 = QueryEval.getMBeanServer();
      paramQueryExp.setMBeanServer(server);
      try
      {
        localObject2 = paramSet.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          NamedObject localNamedObject = (NamedObject)((Iterator)localObject2).next();
          boolean bool;
          try
          {
            bool = paramQueryExp.apply(localNamedObject.getName());
          }
          catch (Exception localException)
          {
            bool = false;
          }
          if (bool) {
            localHashSet.add(localNamedObject.getName());
          }
        }
      }
      finally
      {
        paramQueryExp.setMBeanServer((MBeanServer)localObject1);
      }
    }
    return localHashSet;
  }
  
  private Set<ObjectInstance> objectInstancesFromFilteredNamedObjects(Set<NamedObject> paramSet, QueryExp paramQueryExp)
  {
    HashSet localHashSet = new HashSet();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if (paramQueryExp == null)
    {
      localObject1 = paramSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (NamedObject)((Iterator)localObject1).next();
        localObject3 = ((NamedObject)localObject2).getObject();
        localObject4 = safeGetClassName((DynamicMBean)localObject3);
        localHashSet.add(new ObjectInstance(((NamedObject)localObject2).getName(), (String)localObject4));
      }
    }
    else
    {
      localObject1 = QueryEval.getMBeanServer();
      paramQueryExp.setMBeanServer(server);
      try
      {
        localObject2 = paramSet.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (NamedObject)((Iterator)localObject2).next();
          localObject4 = ((NamedObject)localObject3).getObject();
          boolean bool;
          try
          {
            bool = paramQueryExp.apply(((NamedObject)localObject3).getName());
          }
          catch (Exception localException)
          {
            bool = false;
          }
          if (bool)
          {
            String str = safeGetClassName((DynamicMBean)localObject4);
            localHashSet.add(new ObjectInstance(((NamedObject)localObject3).getName(), str));
          }
        }
      }
      finally
      {
        paramQueryExp.setMBeanServer((MBeanServer)localObject1);
      }
    }
    return localHashSet;
  }
  
  private static String safeGetClassName(DynamicMBean paramDynamicMBean)
  {
    try
    {
      return getClassName(paramDynamicMBean);
    }
    catch (Exception localException)
    {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class.getName(), "safeGetClassName", "Exception getting MBean class name", localException);
      }
    }
    return null;
  }
  
  private Set<ObjectInstance> filterListOfObjectInstances(Set<ObjectInstance> paramSet, QueryExp paramQueryExp)
  {
    if (paramQueryExp == null) {
      return paramSet;
    }
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      ObjectInstance localObjectInstance = (ObjectInstance)localIterator.next();
      boolean bool = false;
      MBeanServer localMBeanServer = QueryEval.getMBeanServer();
      paramQueryExp.setMBeanServer(server);
      try
      {
        bool = paramQueryExp.apply(localObjectInstance.getObjectName());
      }
      catch (Exception localException)
      {
        bool = false;
      }
      finally
      {
        paramQueryExp.setMBeanServer(localMBeanServer);
      }
      if (bool) {
        localHashSet.add(localObjectInstance);
      }
    }
    return localHashSet;
  }
  
  private NotificationListener getListenerWrapper(NotificationListener paramNotificationListener, ObjectName paramObjectName, DynamicMBean paramDynamicMBean, boolean paramBoolean)
  {
    Object localObject1 = getResource(paramDynamicMBean);
    ListenerWrapper localListenerWrapper = new ListenerWrapper(paramNotificationListener, paramObjectName, localObject1);
    synchronized (listenerWrappers)
    {
      WeakReference localWeakReference = (WeakReference)listenerWrappers.get(localListenerWrapper);
      if (localWeakReference != null)
      {
        NotificationListener localNotificationListener = (NotificationListener)localWeakReference.get();
        if (localNotificationListener != null) {
          return localNotificationListener;
        }
      }
      if (paramBoolean)
      {
        localWeakReference = new WeakReference(localListenerWrapper);
        listenerWrappers.put(localListenerWrapper, localWeakReference);
        return localListenerWrapper;
      }
      return null;
    }
  }
  
  public Object instantiate(String paramString)
    throws ReflectionException, MBeanException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName)
    throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, MBeanException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfByte)
    throws InstanceNotFoundException, OperationsException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ObjectInputStream deserialize(String paramString, byte[] paramArrayOfByte)
    throws OperationsException, ReflectionException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfByte)
    throws InstanceNotFoundException, OperationsException, ReflectionException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ClassLoaderRepository getClassLoaderRepository()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  private static String getClassName(DynamicMBean paramDynamicMBean)
  {
    if ((paramDynamicMBean instanceof DynamicMBean2)) {
      return ((DynamicMBean2)paramDynamicMBean).getClassName();
    }
    return paramDynamicMBean.getMBeanInfo().getClassName();
  }
  
  private static void checkMBeanPermission(DynamicMBean paramDynamicMBean, String paramString1, ObjectName paramObjectName, String paramString2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      checkMBeanPermission(safeGetClassName(paramDynamicMBean), paramString1, paramObjectName, paramString2);
    }
  }
  
  private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      MBeanPermission localMBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
      localSecurityManager.checkPermission(localMBeanPermission);
    }
  }
  
  private static void checkMBeanTrustPermission(Class<?> paramClass)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      MBeanTrustPermission localMBeanTrustPermission = new MBeanTrustPermission("register");
      PrivilegedAction local1 = new PrivilegedAction()
      {
        public ProtectionDomain run()
        {
          return val$theClass.getProtectionDomain();
        }
      };
      ProtectionDomain localProtectionDomain = (ProtectionDomain)AccessController.doPrivileged(local1);
      AccessControlContext localAccessControlContext = new AccessControlContext(new ProtectionDomain[] { localProtectionDomain });
      localSecurityManager.checkPermission(localMBeanTrustPermission, localAccessControlContext);
    }
  }
  
  private ResourceContext registerWithRepository(Object paramObject, DynamicMBean paramDynamicMBean, ObjectName paramObjectName)
    throws InstanceAlreadyExistsException, MBeanRegistrationException
  {
    ResourceContext localResourceContext = makeResourceContextFor(paramObject, paramObjectName);
    repository.addMBean(paramDynamicMBean, paramObjectName, localResourceContext);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addObject", "Send create notification of object " + paramObjectName.getCanonicalName());
    }
    sendNotification("JMX.mbean.registered", paramObjectName);
    return localResourceContext;
  }
  
  private ResourceContext unregisterFromRepository(Object paramObject, DynamicMBean paramDynamicMBean, ObjectName paramObjectName)
    throws InstanceNotFoundException
  {
    ResourceContext localResourceContext = makeResourceContextFor(paramObject, paramObjectName);
    repository.remove(paramObjectName, localResourceContext);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "unregisterMBean", "Send delete notification of object " + paramObjectName.getCanonicalName());
    }
    sendNotification("JMX.mbean.unregistered", paramObjectName);
    return localResourceContext;
  }
  
  private void addClassLoader(ClassLoader paramClassLoader, ObjectName paramObjectName)
  {
    ModifiableClassLoaderRepository localModifiableClassLoaderRepository = getInstantiatorCLR();
    if (localModifiableClassLoaderRepository == null)
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Dynamic addition of class loaders is not supported");
      throw new RuntimeOperationsException(localIllegalArgumentException, "Exception occurred trying to register the MBean as a class loader");
    }
    localModifiableClassLoaderRepository.addClassLoader(paramObjectName, paramClassLoader);
  }
  
  private void removeClassLoader(ClassLoader paramClassLoader, ObjectName paramObjectName)
  {
    if (paramClassLoader != server.getClass().getClassLoader())
    {
      ModifiableClassLoaderRepository localModifiableClassLoaderRepository = getInstantiatorCLR();
      if (localModifiableClassLoaderRepository != null) {
        localModifiableClassLoaderRepository.removeClassLoader(paramObjectName);
      }
    }
  }
  
  private ResourceContext createClassLoaderContext(final ClassLoader paramClassLoader, final ObjectName paramObjectName)
  {
    new ResourceContext()
    {
      public void registering()
      {
        DefaultMBeanServerInterceptor.this.addClassLoader(paramClassLoader, paramObjectName);
      }
      
      public void unregistered()
      {
        DefaultMBeanServerInterceptor.this.removeClassLoader(paramClassLoader, paramObjectName);
      }
      
      public void done() {}
    };
  }
  
  private ResourceContext makeResourceContextFor(Object paramObject, ObjectName paramObjectName)
  {
    if ((paramObject instanceof ClassLoader)) {
      return createClassLoaderContext((ClassLoader)paramObject, paramObjectName);
    }
    return ResourceContext.NONE;
  }
  
  private ModifiableClassLoaderRepository getInstantiatorCLR()
  {
    (ModifiableClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ModifiableClassLoaderRepository run()
      {
        return instantiator != null ? instantiator.getClassLoaderRepository() : null;
      }
    });
  }
  
  private static class ListenerWrapper
    implements NotificationListener
  {
    private NotificationListener listener;
    private ObjectName name;
    private Object mbean;
    
    ListenerWrapper(NotificationListener paramNotificationListener, ObjectName paramObjectName, Object paramObject)
    {
      listener = paramNotificationListener;
      name = paramObjectName;
      mbean = paramObject;
    }
    
    public void handleNotification(Notification paramNotification, Object paramObject)
    {
      if ((paramNotification != null) && (paramNotification.getSource() == mbean)) {
        paramNotification.setSource(name);
      }
      listener.handleNotification(paramNotification, paramObject);
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof ListenerWrapper)) {
        return false;
      }
      ListenerWrapper localListenerWrapper = (ListenerWrapper)paramObject;
      return (listener == listener) && (mbean == mbean) && (name.equals(name));
    }
    
    public int hashCode()
    {
      return System.identityHashCode(listener) ^ System.identityHashCode(mbean);
    }
  }
  
  private static abstract interface ResourceContext
    extends Repository.RegistrationContext
  {
    public static final ResourceContext NONE = new ResourceContext()
    {
      public void done() {}
      
      public void registering() {}
      
      public void unregistered() {}
    };
    
    public abstract void done();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\interceptor\DefaultMBeanServerInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.management;

import com.sun.jmx.mbeanserver.MXBeanProxy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.WeakHashMap;

public class MBeanServerInvocationHandler
  implements InvocationHandler
{
  private static final WeakHashMap<Class<?>, WeakReference<MXBeanProxy>> mxbeanProxies = new WeakHashMap();
  private final MBeanServerConnection connection;
  private final ObjectName objectName;
  private final boolean isMXBean;
  
  public MBeanServerInvocationHandler(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName)
  {
    this(paramMBeanServerConnection, paramObjectName, false);
  }
  
  public MBeanServerInvocationHandler(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, boolean paramBoolean)
  {
    if (paramMBeanServerConnection == null) {
      throw new IllegalArgumentException("Null connection");
    }
    if ((Proxy.isProxyClass(paramMBeanServerConnection.getClass())) && (MBeanServerInvocationHandler.class.isAssignableFrom(Proxy.getInvocationHandler(paramMBeanServerConnection).getClass()))) {
      throw new IllegalArgumentException("Wrapping MBeanServerInvocationHandler");
    }
    if (paramObjectName == null) {
      throw new IllegalArgumentException("Null object name");
    }
    connection = paramMBeanServerConnection;
    objectName = paramObjectName;
    isMXBean = paramBoolean;
  }
  
  public MBeanServerConnection getMBeanServerConnection()
  {
    return connection;
  }
  
  public ObjectName getObjectName()
  {
    return objectName;
  }
  
  public boolean isMXBean()
  {
    return isMXBean;
  }
  
  public static <T> T newProxyInstance(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean)
  {
    return (T)JMX.newMBeanProxy(paramMBeanServerConnection, paramObjectName, paramClass, paramBoolean);
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    Class localClass1 = paramMethod.getDeclaringClass();
    if ((localClass1.equals(NotificationBroadcaster.class)) || (localClass1.equals(NotificationEmitter.class))) {
      return invokeBroadcasterMethod(paramObject, paramMethod, paramArrayOfObject);
    }
    if (shouldDoLocally(paramObject, paramMethod)) {
      return doLocally(paramObject, paramMethod, paramArrayOfObject);
    }
    try
    {
      if (isMXBean())
      {
        localObject1 = findMXBeanProxy(localClass1);
        return ((MXBeanProxy)localObject1).invoke(connection, objectName, paramMethod, paramArrayOfObject);
      }
      Object localObject1 = paramMethod.getName();
      Class[] arrayOfClass = paramMethod.getParameterTypes();
      Class localClass2 = paramMethod.getReturnType();
      int i = paramArrayOfObject == null ? 0 : paramArrayOfObject.length;
      if ((((String)localObject1).startsWith("get")) && (((String)localObject1).length() > 3) && (i == 0) && (!localClass2.equals(Void.TYPE))) {
        return connection.getAttribute(objectName, ((String)localObject1).substring(3));
      }
      if ((((String)localObject1).startsWith("is")) && (((String)localObject1).length() > 2) && (i == 0) && ((localClass2.equals(Boolean.TYPE)) || (localClass2.equals(Boolean.class)))) {
        return connection.getAttribute(objectName, ((String)localObject1).substring(2));
      }
      if ((((String)localObject1).startsWith("set")) && (((String)localObject1).length() > 3) && (i == 1) && (localClass2.equals(Void.TYPE)))
      {
        localObject2 = new Attribute(((String)localObject1).substring(3), paramArrayOfObject[0]);
        connection.setAttribute(objectName, (Attribute)localObject2);
        return null;
      }
      Object localObject2 = new String[arrayOfClass.length];
      for (int j = 0; j < arrayOfClass.length; j++) {
        localObject2[j] = arrayOfClass[j].getName();
      }
      return connection.invoke(objectName, (String)localObject1, paramArrayOfObject, (String[])localObject2);
    }
    catch (MBeanException localMBeanException)
    {
      throw localMBeanException.getTargetException();
    }
    catch (RuntimeMBeanException localRuntimeMBeanException)
    {
      throw localRuntimeMBeanException.getTargetException();
    }
    catch (RuntimeErrorException localRuntimeErrorException)
    {
      throw localRuntimeErrorException.getTargetError();
    }
  }
  
  private static MXBeanProxy findMXBeanProxy(Class<?> paramClass)
  {
    synchronized (mxbeanProxies)
    {
      WeakReference localWeakReference = (WeakReference)mxbeanProxies.get(paramClass);
      MXBeanProxy localMXBeanProxy = localWeakReference == null ? null : (MXBeanProxy)localWeakReference.get();
      if (localMXBeanProxy == null)
      {
        try
        {
          localMXBeanProxy = new MXBeanProxy(paramClass);
        }
        catch (IllegalArgumentException localIllegalArgumentException1)
        {
          String str = "Cannot make MXBean proxy for " + paramClass.getName() + ": " + localIllegalArgumentException1.getMessage();
          IllegalArgumentException localIllegalArgumentException2 = new IllegalArgumentException(str, localIllegalArgumentException1.getCause());
          localIllegalArgumentException2.setStackTrace(localIllegalArgumentException1.getStackTrace());
          throw localIllegalArgumentException2;
        }
        mxbeanProxies.put(paramClass, new WeakReference(localMXBeanProxy));
      }
      return localMXBeanProxy;
    }
  }
  
  private Object invokeBroadcasterMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Exception
  {
    String str1 = paramMethod.getName();
    int i = paramArrayOfObject == null ? 0 : paramArrayOfObject.length;
    Object localObject1;
    NotificationFilter localNotificationFilter;
    Object localObject2;
    if (str1.equals("addNotificationListener"))
    {
      if (i != 3)
      {
        localObject1 = "Bad arg count to addNotificationListener: " + i;
        throw new IllegalArgumentException((String)localObject1);
      }
      localObject1 = (NotificationListener)paramArrayOfObject[0];
      localNotificationFilter = (NotificationFilter)paramArrayOfObject[1];
      localObject2 = paramArrayOfObject[2];
      connection.addNotificationListener(objectName, (NotificationListener)localObject1, localNotificationFilter, localObject2);
      return null;
    }
    if (str1.equals("removeNotificationListener"))
    {
      localObject1 = (NotificationListener)paramArrayOfObject[0];
      switch (i)
      {
      case 1: 
        connection.removeNotificationListener(objectName, (NotificationListener)localObject1);
        return null;
      case 3: 
        localNotificationFilter = (NotificationFilter)paramArrayOfObject[1];
        localObject2 = paramArrayOfObject[2];
        connection.removeNotificationListener(objectName, (NotificationListener)localObject1, localNotificationFilter, localObject2);
        return null;
      }
      String str2 = "Bad arg count to removeNotificationListener: " + i;
      throw new IllegalArgumentException(str2);
    }
    if (str1.equals("getNotificationInfo"))
    {
      if (paramArrayOfObject != null) {
        throw new IllegalArgumentException("getNotificationInfo has args");
      }
      localObject1 = connection.getMBeanInfo(objectName);
      return ((MBeanInfo)localObject1).getNotifications();
    }
    throw new IllegalArgumentException("Bad method name: " + str1);
  }
  
  private boolean shouldDoLocally(Object paramObject, Method paramMethod)
  {
    String str = paramMethod.getName();
    if (((str.equals("hashCode")) || (str.equals("toString"))) && (paramMethod.getParameterTypes().length == 0) && (isLocal(paramObject, paramMethod))) {
      return true;
    }
    if (str.equals("equals")) {
      if ((Arrays.equals(paramMethod.getParameterTypes(), new Class[] { Object.class })) && (isLocal(paramObject, paramMethod))) {
        return true;
      }
    }
    return (str.equals("finalize")) && (paramMethod.getParameterTypes().length == 0);
  }
  
  private Object doLocally(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    String str = paramMethod.getName();
    if (str.equals("equals"))
    {
      if (this == paramArrayOfObject[0]) {
        return Boolean.valueOf(true);
      }
      if (!(paramArrayOfObject[0] instanceof Proxy)) {
        return Boolean.valueOf(false);
      }
      InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(paramArrayOfObject[0]);
      if ((localInvocationHandler == null) || (!(localInvocationHandler instanceof MBeanServerInvocationHandler))) {
        return Boolean.valueOf(false);
      }
      MBeanServerInvocationHandler localMBeanServerInvocationHandler = (MBeanServerInvocationHandler)localInvocationHandler;
      return Boolean.valueOf((connection.equals(connection)) && (objectName.equals(objectName)) && (paramObject.getClass().equals(paramArrayOfObject[0].getClass())));
    }
    if (str.equals("toString")) {
      return (isMXBean() ? "MX" : "M") + "BeanProxy(" + connection + "[" + objectName + "])";
    }
    if (str.equals("hashCode")) {
      return Integer.valueOf(objectName.hashCode() + connection.hashCode());
    }
    if (str.equals("finalize")) {
      return null;
    }
    throw new RuntimeException("Unexpected method name: " + str);
  }
  
  private static boolean isLocal(Object paramObject, Method paramMethod)
  {
    Class[] arrayOfClass1 = paramObject.getClass().getInterfaces();
    if (arrayOfClass1 == null) {
      return true;
    }
    String str = paramMethod.getName();
    Class[] arrayOfClass2 = paramMethod.getParameterTypes();
    Class[] arrayOfClass3 = arrayOfClass1;
    int i = arrayOfClass3.length;
    int j = 0;
    while (j < i)
    {
      Class localClass = arrayOfClass3[j];
      try
      {
        localClass.getMethod(str, arrayOfClass2);
        return false;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        j++;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanServerInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
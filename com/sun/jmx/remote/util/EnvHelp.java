package com.sun.jmx.remote.util;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.remote.security.NotificationAccessController;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class EnvHelp
{
  public static final String CREDENTIAL_TYPES = "jmx.remote.rmi.server.credential.types";
  private static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
  private static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
  public static final String BUFFER_SIZE_PROPERTY = "jmx.remote.x.notification.buffer.size";
  public static final String MAX_FETCH_NOTIFS = "jmx.remote.x.notification.fetch.max";
  public static final String FETCH_TIMEOUT = "jmx.remote.x.notification.fetch.timeout";
  public static final String NOTIF_ACCESS_CONTROLLER = "com.sun.jmx.remote.notification.access.controller";
  public static final String DEFAULT_ORB = "java.naming.corba.orb";
  public static final String HIDDEN_ATTRIBUTES = "jmx.remote.x.hidden.attributes";
  public static final String DEFAULT_HIDDEN_ATTRIBUTES = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
  private static final SortedSet<String> defaultHiddenStrings = new TreeSet();
  private static final SortedSet<String> defaultHiddenPrefixes = new TreeSet();
  public static final String SERVER_CONNECTION_TIMEOUT = "jmx.remote.x.server.connection.timeout";
  public static final String CLIENT_CONNECTION_CHECK_PERIOD = "jmx.remote.x.client.connection.check.period";
  public static final String JMX_SERVER_DAEMON = "jmx.remote.x.daemon";
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "EnvHelp");
  
  public EnvHelp() {}
  
  public static ClassLoader resolveServerClassLoader(Map<String, ?> paramMap, MBeanServer paramMBeanServer)
    throws InstanceNotFoundException
  {
    if (paramMap == null) {
      return Thread.currentThread().getContextClassLoader();
    }
    Object localObject1 = paramMap.get("jmx.remote.default.class.loader");
    Object localObject2 = paramMap.get("jmx.remote.default.class.loader.name");
    if ((localObject1 != null) && (localObject2 != null)) {
      throw new IllegalArgumentException("Only one of jmx.remote.default.class.loader or jmx.remote.default.class.loader.name should be specified.");
    }
    if ((localObject1 == null) && (localObject2 == null)) {
      return Thread.currentThread().getContextClassLoader();
    }
    Object localObject3;
    if (localObject1 != null)
    {
      if ((localObject1 instanceof ClassLoader)) {
        return (ClassLoader)localObject1;
      }
      localObject3 = "ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + localObject1.getClass().getName();
      throw new IllegalArgumentException((String)localObject3);
    }
    if ((localObject2 instanceof ObjectName))
    {
      localObject3 = (ObjectName)localObject2;
    }
    else
    {
      String str = "ClassLoader name is not an instance of " + ObjectName.class.getName() + " : " + localObject2.getClass().getName();
      throw new IllegalArgumentException(str);
    }
    if (paramMBeanServer == null) {
      throw new IllegalArgumentException("Null MBeanServer object");
    }
    return paramMBeanServer.getClassLoader((ObjectName)localObject3);
  }
  
  public static ClassLoader resolveClientClassLoader(Map<String, ?> paramMap)
  {
    if (paramMap == null) {
      return Thread.currentThread().getContextClassLoader();
    }
    Object localObject = paramMap.get("jmx.remote.default.class.loader");
    if (localObject == null) {
      return Thread.currentThread().getContextClassLoader();
    }
    if ((localObject instanceof ClassLoader)) {
      return (ClassLoader)localObject;
    }
    String str = "ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + localObject.getClass().getName();
    throw new IllegalArgumentException(str);
  }
  
  public static <T extends Throwable> T initCause(T paramT, Throwable paramThrowable)
  {
    paramT.initCause(paramThrowable);
    return paramT;
  }
  
  public static Throwable getCause(Throwable paramThrowable)
  {
    Throwable localThrowable = paramThrowable;
    try
    {
      Method localMethod = paramThrowable.getClass().getMethod("getCause", (Class[])null);
      localThrowable = (Throwable)localMethod.invoke(paramThrowable, (Object[])null);
    }
    catch (Exception localException) {}
    return localThrowable != null ? localThrowable : paramThrowable;
  }
  
  public static int getNotifBufferSize(Map<String, ?> paramMap)
  {
    RuntimeException localRuntimeException1 = 1000;
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.remote.x.notification.buffer.size");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      if (str != null)
      {
        localRuntimeException1 = Integer.parseInt(str);
      }
      else
      {
        localGetPropertyAction = new GetPropertyAction("jmx.remote.x.buffer.size");
        str = (String)AccessController.doPrivileged(localGetPropertyAction);
        if (str != null) {
          localRuntimeException1 = Integer.parseInt(str);
        }
      }
    }
    catch (RuntimeException localRuntimeException2)
    {
      logger.warning("getNotifBufferSize", "Can't use System property jmx.remote.x.notification.buffer.size: " + localRuntimeException2);
      logger.debug("getNotifBufferSize", localRuntimeException2);
    }
    localRuntimeException2 = localRuntimeException1;
    int i;
    try
    {
      if (paramMap.containsKey("jmx.remote.x.notification.buffer.size")) {
        i = (int)getIntegerAttribute(paramMap, "jmx.remote.x.notification.buffer.size", localRuntimeException1, 0L, 2147483647L);
      } else {
        i = (int)getIntegerAttribute(paramMap, "jmx.remote.x.buffer.size", localRuntimeException1, 0L, 2147483647L);
      }
    }
    catch (RuntimeException localRuntimeException3)
    {
      logger.warning("getNotifBufferSize", "Can't determine queuesize (using default): " + localRuntimeException3);
      logger.debug("getNotifBufferSize", localRuntimeException3);
    }
    return i;
  }
  
  public static int getMaxFetchNotifNumber(Map<String, ?> paramMap)
  {
    return (int)getIntegerAttribute(paramMap, "jmx.remote.x.notification.fetch.max", 1000L, 1L, 2147483647L);
  }
  
  public static long getFetchTimeout(Map<String, ?> paramMap)
  {
    return getIntegerAttribute(paramMap, "jmx.remote.x.notification.fetch.timeout", 60000L, 0L, Long.MAX_VALUE);
  }
  
  public static NotificationAccessController getNotificationAccessController(Map<String, ?> paramMap)
  {
    return paramMap == null ? null : (NotificationAccessController)paramMap.get("com.sun.jmx.remote.notification.access.controller");
  }
  
  public static long getIntegerAttribute(Map<String, ?> paramMap, String paramString, long paramLong1, long paramLong2, long paramLong3)
  {
    Object localObject;
    if ((paramMap == null) || ((localObject = paramMap.get(paramString)) == null)) {
      return paramLong1;
    }
    long l;
    String str;
    if ((localObject instanceof Number))
    {
      l = ((Number)localObject).longValue();
    }
    else if ((localObject instanceof String))
    {
      l = Long.parseLong((String)localObject);
    }
    else
    {
      str = "Attribute " + paramString + " value must be Integer or String: " + localObject;
      throw new IllegalArgumentException(str);
    }
    if (l < paramLong2)
    {
      str = "Attribute " + paramString + " value must be at least " + paramLong2 + ": " + l;
      throw new IllegalArgumentException(str);
    }
    if (l > paramLong3)
    {
      str = "Attribute " + paramString + " value must be at most " + paramLong3 + ": " + l;
      throw new IllegalArgumentException(str);
    }
    return l;
  }
  
  public static void checkAttributes(Map<?, ?> paramMap)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (!(localObject instanceof String))
      {
        String str = "Attributes contain key that is not a string: " + localObject;
        throw new IllegalArgumentException(str);
      }
    }
  }
  
  public static <V> Map<String, V> filterAttributes(Map<String, V> paramMap)
  {
    if (logger.traceOn()) {
      logger.trace("filterAttributes", "starts");
    }
    TreeMap localTreeMap = new TreeMap(paramMap);
    purgeUnserializable(localTreeMap.values());
    hideAttributes(localTreeMap);
    return localTreeMap;
  }
  
  private static void purgeUnserializable(Collection<?> paramCollection)
  {
    logger.trace("purgeUnserializable", "starts");
    ObjectOutputStream localObjectOutputStream = null;
    int i = 0;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject == null) || ((localObject instanceof String)))
      {
        if (logger.traceOn()) {
          logger.trace("purgeUnserializable", "Value trivially serializable: " + localObject);
        }
      }
      else {
        try
        {
          if (localObjectOutputStream == null) {
            localObjectOutputStream = new ObjectOutputStream(new SinkOutputStream(null));
          }
          localObjectOutputStream.writeObject(localObject);
          if (logger.traceOn()) {
            logger.trace("purgeUnserializable", "Value serializable: " + localObject);
          }
        }
        catch (IOException localIOException)
        {
          if (logger.traceOn()) {
            logger.trace("purgeUnserializable", "Value not serializable: " + localObject + ": " + localIOException);
          }
          localIterator.remove();
          localObjectOutputStream = null;
        }
      }
      i++;
    }
  }
  
  private static void hideAttributes(SortedMap<String, ?> paramSortedMap)
  {
    if (paramSortedMap.isEmpty()) {
      return;
    }
    String str1 = (String)paramSortedMap.get("jmx.remote.x.hidden.attributes");
    Object localObject1;
    Object localObject2;
    if (str1 != null)
    {
      if (str1.startsWith("=")) {
        str1 = str1.substring(1);
      } else {
        str1 = str1 + " java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
      }
      localObject1 = new TreeSet();
      localObject2 = new TreeSet();
      parseHiddenAttributes(str1, (SortedSet)localObject1, (SortedSet)localObject2);
    }
    else
    {
      str1 = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
      synchronized (defaultHiddenStrings)
      {
        if (defaultHiddenStrings.isEmpty()) {
          parseHiddenAttributes(str1, defaultHiddenStrings, defaultHiddenPrefixes);
        }
        localObject1 = defaultHiddenStrings;
        localObject2 = defaultHiddenPrefixes;
      }
    }
    ??? = (String)paramSortedMap.lastKey() + "X";
    Iterator localIterator1 = paramSortedMap.keySet().iterator();
    Iterator localIterator2 = ((SortedSet)localObject1).iterator();
    Iterator localIterator3 = ((SortedSet)localObject2).iterator();
    Object localObject4;
    if (localIterator2.hasNext()) {
      localObject4 = (String)localIterator2.next();
    } else {
      localObject4 = ???;
    }
    Object localObject5;
    if (localIterator3.hasNext()) {
      localObject5 = (String)localIterator3.next();
    } else {
      localObject5 = ???;
    }
    label405:
    while (localIterator1.hasNext())
    {
      String str2 = (String)localIterator1.next();
      int i = 1;
      while ((i = ((String)localObject4).compareTo(str2)) < 0) {
        if (localIterator2.hasNext()) {
          localObject4 = (String)localIterator2.next();
        } else {
          localObject4 = ???;
        }
      }
      if (i == 0) {
        localIterator1.remove();
      } else {
        for (;;)
        {
          if (((String)localObject5).compareTo(str2) > 0) {
            break label405;
          }
          if (str2.startsWith((String)localObject5))
          {
            localIterator1.remove();
            break;
          }
          if (localIterator3.hasNext()) {
            localObject5 = (String)localIterator3.next();
          } else {
            localObject5 = ???;
          }
        }
      }
    }
  }
  
  private static void parseHiddenAttributes(String paramString, SortedSet<String> paramSortedSet1, SortedSet<String> paramSortedSet2)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      if (str.endsWith("*")) {
        paramSortedSet2.add(str.substring(0, str.length() - 1));
      } else {
        paramSortedSet1.add(str);
      }
    }
  }
  
  public static long getServerConnectionTimeout(Map<String, ?> paramMap)
  {
    return getIntegerAttribute(paramMap, "jmx.remote.x.server.connection.timeout", 120000L, 0L, Long.MAX_VALUE);
  }
  
  public static long getConnectionCheckPeriod(Map<String, ?> paramMap)
  {
    return getIntegerAttribute(paramMap, "jmx.remote.x.client.connection.check.period", 60000L, 0L, Long.MAX_VALUE);
  }
  
  public static boolean computeBooleanFromString(String paramString)
  {
    return computeBooleanFromString(paramString, false);
  }
  
  public static boolean computeBooleanFromString(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return paramBoolean;
    }
    if (paramString.equalsIgnoreCase("true")) {
      return true;
    }
    if (paramString.equalsIgnoreCase("false")) {
      return false;
    }
    throw new IllegalArgumentException("Property value must be \"true\" or \"false\" instead of \"" + paramString + "\"");
  }
  
  public static <K, V> Hashtable<K, V> mapToHashtable(Map<K, V> paramMap)
  {
    HashMap localHashMap = new HashMap(paramMap);
    if (localHashMap.containsKey(null)) {
      localHashMap.remove(null);
    }
    Iterator localIterator = localHashMap.values().iterator();
    while (localIterator.hasNext()) {
      if (localIterator.next() == null) {
        localIterator.remove();
      }
    }
    return new Hashtable(localHashMap);
  }
  
  public static boolean isServerDaemon(Map<String, ?> paramMap)
  {
    return (paramMap != null) && ("true".equalsIgnoreCase((String)paramMap.get("jmx.remote.x.daemon")));
  }
  
  private static final class SinkOutputStream
    extends OutputStream
  {
    private SinkOutputStream() {}
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {}
    
    public void write(int paramInt) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\util\EnvHelp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
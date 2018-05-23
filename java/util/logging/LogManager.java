package java.util.logging;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import sun.misc.JavaAWTAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public class LogManager
{
  private static final LogManager manager = (LogManager)AccessController.doPrivileged(new PrivilegedAction()
  {
    public LogManager run()
    {
      LogManager localLogManager = null;
      String str = null;
      try
      {
        str = System.getProperty("java.util.logging.manager");
        if (str != null) {
          try
          {
            Class localClass1 = ClassLoader.getSystemClassLoader().loadClass(str);
            localLogManager = (LogManager)localClass1.newInstance();
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            Class localClass2 = Thread.currentThread().getContextClassLoader().loadClass(str);
            localLogManager = (LogManager)localClass2.newInstance();
          }
        }
      }
      catch (Exception localException)
      {
        System.err.println("Could not load Logmanager \"" + str + "\"");
        localException.printStackTrace();
      }
      if (localLogManager == null) {
        localLogManager = new LogManager();
      }
      return localLogManager;
    }
  });
  private volatile Properties props = new Properties();
  private static final Level defaultLevel;
  private final Map<Object, Integer> listenerMap = new HashMap();
  private final LoggerContext systemContext = new SystemLoggerContext();
  private final LoggerContext userContext = new LoggerContext(null);
  private volatile Logger rootLogger;
  private volatile boolean readPrimordialConfiguration;
  private boolean initializedGlobalHandlers = true;
  private boolean deathImminent;
  private boolean initializedCalled = false;
  private volatile boolean initializationDone = false;
  private WeakHashMap<Object, LoggerContext> contextsMap = null;
  private final ReferenceQueue<Logger> loggerRefQueue = new ReferenceQueue();
  private static final int MAX_ITERATIONS = 400;
  private final Permission controlPermission = new LoggingPermission("control", null);
  private static LoggingMXBean loggingMXBean = null;
  public static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
  
  protected LogManager()
  {
    this(checkSubclassPermissions());
  }
  
  private LogManager(Void paramVoid)
  {
    try
    {
      Runtime.getRuntime().addShutdownHook(new Cleaner(null));
    }
    catch (IllegalStateException localIllegalStateException) {}
  }
  
  private static Void checkSubclassPermissions()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localSecurityManager.checkPermission(new RuntimePermission("shutdownHooks"));
      localSecurityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
    }
    return null;
  }
  
  final void ensureLogManagerInitialized()
  {
    final LogManager localLogManager = this;
    if ((initializationDone) || (localLogManager != manager)) {
      return;
    }
    synchronized (this)
    {
      int i = initializedCalled == true ? 1 : 0;
      assert ((initializedCalled) || (!initializationDone)) : "Initialization can't be done if initialized has not been called!";
      if ((i != 0) || (initializationDone)) {
        return;
      }
      initializedCalled = true;
      try
      {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            assert (rootLogger == null);
            assert ((initializedCalled) && (!initializationDone));
            localLogManager.readPrimordialConfiguration();
            LogManager tmp77_74 = localLogManager;
            tmp77_74.getClass();
            localLogManagerrootLogger = new LogManager.RootLogger(tmp77_74, null);
            localLogManager.addLogger(localLogManagerrootLogger);
            if (!localLogManagerrootLogger.isLevelInitialized()) {
              localLogManagerrootLogger.setLevel(LogManager.defaultLevel);
            }
            Logger localLogger = Logger.global;
            localLogManager.addLogger(localLogger);
            return null;
          }
        });
      }
      finally
      {
        initializationDone = true;
      }
    }
  }
  
  public static LogManager getLogManager()
  {
    if (manager != null) {
      manager.ensureLogManagerInitialized();
    }
    return manager;
  }
  
  private void readPrimordialConfiguration()
  {
    if (!readPrimordialConfiguration) {
      synchronized (this)
      {
        if (!readPrimordialConfiguration)
        {
          if (System.out == null) {
            return;
          }
          readPrimordialConfiguration = true;
          try
          {
            AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public Void run()
                throws Exception
              {
                readConfiguration();
                PlatformLogger.redirectPlatformLoggers();
                return null;
              }
            });
          }
          catch (Exception localException)
          {
            if (!$assertionsDisabled) {
              throw new AssertionError("Exception raised while reading logging configuration: " + localException);
            }
          }
        }
      }
    }
  }
  
  @Deprecated
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    throws SecurityException
  {
    PropertyChangeListener localPropertyChangeListener = (PropertyChangeListener)Objects.requireNonNull(paramPropertyChangeListener);
    checkPermission();
    synchronized (listenerMap)
    {
      Integer localInteger = (Integer)listenerMap.get(localPropertyChangeListener);
      localInteger = Integer.valueOf(localInteger == null ? 1 : localInteger.intValue() + 1);
      listenerMap.put(localPropertyChangeListener, localInteger);
    }
  }
  
  @Deprecated
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    throws SecurityException
  {
    checkPermission();
    if (paramPropertyChangeListener != null)
    {
      PropertyChangeListener localPropertyChangeListener = paramPropertyChangeListener;
      synchronized (listenerMap)
      {
        Integer localInteger = (Integer)listenerMap.get(localPropertyChangeListener);
        if (localInteger != null)
        {
          int i = localInteger.intValue();
          if (i == 1)
          {
            listenerMap.remove(localPropertyChangeListener);
          }
          else
          {
            assert (i > 1);
            listenerMap.put(localPropertyChangeListener, Integer.valueOf(i - 1));
          }
        }
      }
    }
  }
  
  private LoggerContext getUserContext()
  {
    LoggerContext localLoggerContext = null;
    SecurityManager localSecurityManager = System.getSecurityManager();
    JavaAWTAccess localJavaAWTAccess = SharedSecrets.getJavaAWTAccess();
    if ((localSecurityManager != null) && (localJavaAWTAccess != null))
    {
      Object localObject1 = localJavaAWTAccess.getAppletContext();
      if (localObject1 != null) {
        synchronized (localJavaAWTAccess)
        {
          if (contextsMap == null) {
            contextsMap = new WeakHashMap();
          }
          localLoggerContext = (LoggerContext)contextsMap.get(localObject1);
          if (localLoggerContext == null)
          {
            localLoggerContext = new LoggerContext(null);
            contextsMap.put(localObject1, localLoggerContext);
          }
        }
      }
    }
    return localLoggerContext != null ? localLoggerContext : userContext;
  }
  
  final LoggerContext getSystemContext()
  {
    return systemContext;
  }
  
  private List<LoggerContext> contexts()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(getSystemContext());
    localArrayList.add(getUserContext());
    return localArrayList;
  }
  
  Logger demandLogger(String paramString1, String paramString2, Class<?> paramClass)
  {
    Logger localLogger1 = getLogger(paramString1);
    if (localLogger1 == null)
    {
      Logger localLogger2 = new Logger(paramString1, paramString2, paramClass, this, false);
      do
      {
        if (addLogger(localLogger2)) {
          return localLogger2;
        }
        localLogger1 = getLogger(paramString1);
      } while (localLogger1 == null);
    }
    return localLogger1;
  }
  
  Logger demandSystemLogger(String paramString1, String paramString2)
  {
    final Logger localLogger1 = getSystemContext().demandLogger(paramString1, paramString2);
    Logger localLogger2;
    do
    {
      if (addLogger(localLogger1)) {
        localLogger2 = localLogger1;
      } else {
        localLogger2 = getLogger(paramString1);
      }
    } while (localLogger2 == null);
    if ((localLogger2 != localLogger1) && (localLogger1.accessCheckedHandlers().length == 0))
    {
      final Logger localLogger3 = localLogger2;
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          for (Handler localHandler : localLogger3.accessCheckedHandlers()) {
            localLogger1.addHandler(localHandler);
          }
          return null;
        }
      });
    }
    return localLogger1;
  }
  
  private void loadLoggerHandlers(final Logger paramLogger, String paramString1, final String paramString2)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        String[] arrayOfString = LogManager.this.parseClassNames(paramString2);
        for (int i = 0; i < arrayOfString.length; i++)
        {
          String str1 = arrayOfString[i];
          try
          {
            Class localClass = ClassLoader.getSystemClassLoader().loadClass(str1);
            Handler localHandler = (Handler)localClass.newInstance();
            String str2 = getProperty(str1 + ".level");
            if (str2 != null)
            {
              Level localLevel = Level.findLevel(str2);
              if (localLevel != null) {
                localHandler.setLevel(localLevel);
              } else {
                System.err.println("Can't set level for " + str1);
              }
            }
            paramLogger.addHandler(localHandler);
          }
          catch (Exception localException)
          {
            System.err.println("Can't load log handler \"" + str1 + "\"");
            System.err.println("" + localException);
            localException.printStackTrace();
          }
        }
        return null;
      }
    });
  }
  
  final void drainLoggerRefQueueBounded()
  {
    for (int i = 0; (i < 400) && (loggerRefQueue != null); i++)
    {
      LoggerWeakRef localLoggerWeakRef = (LoggerWeakRef)loggerRefQueue.poll();
      if (localLoggerWeakRef == null) {
        break;
      }
      localLoggerWeakRef.dispose();
    }
  }
  
  public boolean addLogger(Logger paramLogger)
  {
    String str = paramLogger.getName();
    if (str == null) {
      throw new NullPointerException();
    }
    drainLoggerRefQueueBounded();
    LoggerContext localLoggerContext = getUserContext();
    if (localLoggerContext.addLocalLogger(paramLogger))
    {
      loadLoggerHandlers(paramLogger, str, str + ".handlers");
      return true;
    }
    return false;
  }
  
  private static void doSetLevel(Logger paramLogger, final Level paramLevel)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null)
    {
      paramLogger.setLevel(paramLevel);
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        val$logger.setLevel(paramLevel);
        return null;
      }
    });
  }
  
  private static void doSetParent(Logger paramLogger1, final Logger paramLogger2)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null)
    {
      paramLogger1.setParent(paramLogger2);
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        val$logger.setParent(paramLogger2);
        return null;
      }
    });
  }
  
  public Logger getLogger(String paramString)
  {
    return getUserContext().findLogger(paramString);
  }
  
  public Enumeration<String> getLoggerNames()
  {
    return getUserContext().getLoggerNames();
  }
  
  public void readConfiguration()
    throws IOException, SecurityException
  {
    checkPermission();
    String str1 = System.getProperty("java.util.logging.config.class");
    if (str1 != null) {
      try
      {
        Class localClass = ClassLoader.getSystemClassLoader().loadClass(str1);
        localClass.newInstance();
        return;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localObject1 = Thread.currentThread().getContextClassLoader().loadClass(str1);
        ((Class)localObject1).newInstance();
        return;
      }
      catch (Exception localException)
      {
        System.err.println("Logging configuration class \"" + str1 + "\" failed");
        System.err.println("" + localException);
      }
    }
    String str2 = System.getProperty("java.util.logging.config.file");
    if (str2 == null)
    {
      str2 = System.getProperty("java.home");
      if (str2 == null) {
        throw new Error("Can't find java.home ??");
      }
      localObject1 = new File(str2, "lib");
      localObject1 = new File((File)localObject1, "logging.properties");
      str2 = ((File)localObject1).getCanonicalPath();
    }
    Object localObject1 = new FileInputStream(str2);
    Object localObject2 = null;
    try
    {
      BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject1);
      readConfiguration(localBufferedInputStream);
    }
    catch (Throwable localThrowable2)
    {
      localObject2 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localObject1 != null) {
        if (localObject2 != null) {
          try
          {
            ((InputStream)localObject1).close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject2).addSuppressed(localThrowable3);
          }
        } else {
          ((InputStream)localObject1).close();
        }
      }
    }
  }
  
  public void reset()
    throws SecurityException
  {
    checkPermission();
    synchronized (this)
    {
      props = new Properties();
      initializedGlobalHandlers = true;
    }
    ??? = contexts().iterator();
    while (((Iterator)???).hasNext())
    {
      LoggerContext localLoggerContext = (LoggerContext)((Iterator)???).next();
      Enumeration localEnumeration = localLoggerContext.getLoggerNames();
      while (localEnumeration.hasMoreElements())
      {
        String str = (String)localEnumeration.nextElement();
        Logger localLogger = localLoggerContext.findLogger(str);
        if (localLogger != null) {
          resetLogger(localLogger);
        }
      }
    }
  }
  
  private void resetLogger(Logger paramLogger)
  {
    Handler[] arrayOfHandler = paramLogger.getHandlers();
    for (int i = 0; i < arrayOfHandler.length; i++)
    {
      Handler localHandler = arrayOfHandler[i];
      paramLogger.removeHandler(localHandler);
      try
      {
        localHandler.close();
      }
      catch (Exception localException) {}
    }
    String str = paramLogger.getName();
    if ((str != null) && (str.equals(""))) {
      paramLogger.setLevel(defaultLevel);
    } else {
      paramLogger.setLevel(null);
    }
  }
  
  private String[] parseClassNames(String paramString)
  {
    String str1 = getProperty(paramString);
    if (str1 == null) {
      return new String[0];
    }
    str1 = str1.trim();
    int i = 0;
    ArrayList localArrayList = new ArrayList();
    while (i < str1.length())
    {
      for (int j = i; (j < str1.length()) && (!Character.isWhitespace(str1.charAt(j))) && (str1.charAt(j) != ','); j++) {}
      String str2 = str1.substring(i, j);
      i = j + 1;
      str2 = str2.trim();
      if (str2.length() != 0) {
        localArrayList.add(str2);
      }
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public void readConfiguration(InputStream paramInputStream)
    throws IOException, SecurityException
  {
    checkPermission();
    reset();
    props.load(paramInputStream);
    String[] arrayOfString = parseClassNames("config");
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String str = arrayOfString[i];
      try
      {
        Class localClass = ClassLoader.getSystemClassLoader().loadClass(str);
        localClass.newInstance();
      }
      catch (Exception localException)
      {
        System.err.println("Can't load config class \"" + str + "\"");
        System.err.println("" + localException);
      }
    }
    setLevelsOnExistingLoggers();
    HashMap localHashMap = null;
    synchronized (listenerMap)
    {
      if (!listenerMap.isEmpty()) {
        localHashMap = new HashMap(listenerMap);
      }
    }
    if (localHashMap != null)
    {
      assert (Beans.isBeansPresent());
      ??? = Beans.newPropertyChangeEvent(LogManager.class, null, null, null);
      Iterator localIterator = localHashMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject2 = localEntry.getKey();
        int j = ((Integer)localEntry.getValue()).intValue();
        for (int k = 0; k < j; k++) {
          Beans.invokePropertyChange(localObject2, ???);
        }
      }
    }
    synchronized (this)
    {
      initializedGlobalHandlers = false;
    }
  }
  
  public String getProperty(String paramString)
  {
    return props.getProperty(paramString);
  }
  
  String getStringProperty(String paramString1, String paramString2)
  {
    String str = getProperty(paramString1);
    if (str == null) {
      return paramString2;
    }
    return str.trim();
  }
  
  int getIntProperty(String paramString, int paramInt)
  {
    String str = getProperty(paramString);
    if (str == null) {
      return paramInt;
    }
    try
    {
      return Integer.parseInt(str.trim());
    }
    catch (Exception localException) {}
    return paramInt;
  }
  
  boolean getBooleanProperty(String paramString, boolean paramBoolean)
  {
    String str = getProperty(paramString);
    if (str == null) {
      return paramBoolean;
    }
    str = str.toLowerCase();
    if ((str.equals("true")) || (str.equals("1"))) {
      return true;
    }
    if ((str.equals("false")) || (str.equals("0"))) {
      return false;
    }
    return paramBoolean;
  }
  
  Level getLevelProperty(String paramString, Level paramLevel)
  {
    String str = getProperty(paramString);
    if (str == null) {
      return paramLevel;
    }
    Level localLevel = Level.findLevel(str.trim());
    return localLevel != null ? localLevel : paramLevel;
  }
  
  Filter getFilterProperty(String paramString, Filter paramFilter)
  {
    String str = getProperty(paramString);
    try
    {
      if (str != null)
      {
        Class localClass = ClassLoader.getSystemClassLoader().loadClass(str);
        return (Filter)localClass.newInstance();
      }
    }
    catch (Exception localException) {}
    return paramFilter;
  }
  
  Formatter getFormatterProperty(String paramString, Formatter paramFormatter)
  {
    String str = getProperty(paramString);
    try
    {
      if (str != null)
      {
        Class localClass = ClassLoader.getSystemClassLoader().loadClass(str);
        return (Formatter)localClass.newInstance();
      }
    }
    catch (Exception localException) {}
    return paramFormatter;
  }
  
  private synchronized void initializeGlobalHandlers()
  {
    if (initializedGlobalHandlers) {
      return;
    }
    initializedGlobalHandlers = true;
    if (deathImminent) {
      return;
    }
    loadLoggerHandlers(rootLogger, null, "handlers");
  }
  
  void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(controlPermission);
    }
  }
  
  public void checkAccess()
    throws SecurityException
  {
    checkPermission();
  }
  
  private synchronized void setLevelsOnExistingLoggers()
  {
    Enumeration localEnumeration = props.propertyNames();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = (String)localEnumeration.nextElement();
      if (str1.endsWith(".level"))
      {
        int i = str1.length() - 6;
        String str2 = str1.substring(0, i);
        Level localLevel = getLevelProperty(str1, null);
        if (localLevel == null)
        {
          System.err.println("Bad level value for property: " + str1);
        }
        else
        {
          Iterator localIterator = contexts().iterator();
          while (localIterator.hasNext())
          {
            LoggerContext localLoggerContext = (LoggerContext)localIterator.next();
            Logger localLogger = localLoggerContext.findLogger(str2);
            if (localLogger != null) {
              localLogger.setLevel(localLevel);
            }
          }
        }
      }
    }
  }
  
  public static synchronized LoggingMXBean getLoggingMXBean()
  {
    if (loggingMXBean == null) {
      loggingMXBean = new Logging();
    }
    return loggingMXBean;
  }
  
  static
  {
    defaultLevel = Level.INFO;
  }
  
  private static class Beans
  {
    private static final Class<?> propertyChangeListenerClass = getClass("java.beans.PropertyChangeListener");
    private static final Class<?> propertyChangeEventClass = getClass("java.beans.PropertyChangeEvent");
    private static final Method propertyChangeMethod = getMethod(propertyChangeListenerClass, "propertyChange", new Class[] { propertyChangeEventClass });
    private static final Constructor<?> propertyEventCtor = getConstructor(propertyChangeEventClass, new Class[] { Object.class, String.class, Object.class, Object.class });
    
    private Beans() {}
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, Beans.class.getClassLoader());
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return null;
    }
    
    private static Constructor<?> getConstructor(Class<?> paramClass, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass == null ? null : paramClass.getDeclaredConstructor(paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass == null ? null : paramClass.getMethod(paramString, paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new AssertionError(localNoSuchMethodException);
      }
    }
    
    static boolean isBeansPresent()
    {
      return (propertyChangeListenerClass != null) && (propertyChangeEventClass != null);
    }
    
    static Object newPropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3)
    {
      try
      {
        return propertyEventCtor.newInstance(new Object[] { paramObject1, paramString, paramObject2, paramObject3 });
      }
      catch (InstantiationException|IllegalAccessException localInstantiationException)
      {
        throw new AssertionError(localInstantiationException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
    }
    
    static void invokePropertyChange(Object paramObject1, Object paramObject2)
    {
      try
      {
        propertyChangeMethod.invoke(paramObject1, new Object[] { paramObject2 });
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
    }
  }
  
  private class Cleaner
    extends Thread
  {
    private Cleaner()
    {
      setContextClassLoader(null);
    }
    
    public void run()
    {
      LogManager localLogManager = LogManager.manager;
      synchronized (LogManager.this)
      {
        deathImminent = true;
        initializedGlobalHandlers = true;
      }
      reset();
    }
  }
  
  private static class LogNode
  {
    HashMap<String, LogNode> children;
    LogManager.LoggerWeakRef loggerRef;
    LogNode parent;
    final LogManager.LoggerContext context;
    
    LogNode(LogNode paramLogNode, LogManager.LoggerContext paramLoggerContext)
    {
      parent = paramLogNode;
      context = paramLoggerContext;
    }
    
    void walkAndSetParent(Logger paramLogger)
    {
      if (children == null) {
        return;
      }
      Iterator localIterator = children.values().iterator();
      while (localIterator.hasNext())
      {
        LogNode localLogNode = (LogNode)localIterator.next();
        LogManager.LoggerWeakRef localLoggerWeakRef = loggerRef;
        Logger localLogger = localLoggerWeakRef == null ? null : (Logger)localLoggerWeakRef.get();
        if (localLogger == null) {
          localLogNode.walkAndSetParent(paramLogger);
        } else {
          LogManager.doSetParent(localLogger, paramLogger);
        }
      }
    }
  }
  
  class LoggerContext
  {
    private final Hashtable<String, LogManager.LoggerWeakRef> namedLoggers = new Hashtable();
    private final LogManager.LogNode root = new LogManager.LogNode(null, this);
    
    private LoggerContext() {}
    
    final boolean requiresDefaultLoggers()
    {
      boolean bool = getOwner() == LogManager.manager;
      if (bool) {
        getOwner().ensureLogManagerInitialized();
      }
      return bool;
    }
    
    final LogManager getOwner()
    {
      return LogManager.this;
    }
    
    final Logger getRootLogger()
    {
      return getOwnerrootLogger;
    }
    
    final Logger getGlobalLogger()
    {
      Logger localLogger = Logger.global;
      return localLogger;
    }
    
    Logger demandLogger(String paramString1, String paramString2)
    {
      LogManager localLogManager = getOwner();
      return localLogManager.demandLogger(paramString1, paramString2, null);
    }
    
    private void ensureInitialized()
    {
      if (requiresDefaultLoggers())
      {
        ensureDefaultLogger(getRootLogger());
        ensureDefaultLogger(getGlobalLogger());
      }
    }
    
    synchronized Logger findLogger(String paramString)
    {
      ensureInitialized();
      LogManager.LoggerWeakRef localLoggerWeakRef = (LogManager.LoggerWeakRef)namedLoggers.get(paramString);
      if (localLoggerWeakRef == null) {
        return null;
      }
      Logger localLogger = (Logger)localLoggerWeakRef.get();
      if (localLogger == null) {
        localLoggerWeakRef.dispose();
      }
      return localLogger;
    }
    
    private void ensureAllDefaultLoggers(Logger paramLogger)
    {
      if (requiresDefaultLoggers())
      {
        String str = paramLogger.getName();
        if (!str.isEmpty())
        {
          ensureDefaultLogger(getRootLogger());
          if (!"global".equals(str)) {
            ensureDefaultLogger(getGlobalLogger());
          }
        }
      }
    }
    
    private void ensureDefaultLogger(Logger paramLogger)
    {
      if ((!requiresDefaultLoggers()) || (paramLogger == null) || ((paramLogger != Logger.global) && (paramLogger != rootLogger)))
      {
        assert (paramLogger == null);
        return;
      }
      if (!namedLoggers.containsKey(paramLogger.getName())) {
        addLocalLogger(paramLogger, false);
      }
    }
    
    boolean addLocalLogger(Logger paramLogger)
    {
      return addLocalLogger(paramLogger, requiresDefaultLoggers());
    }
    
    synchronized boolean addLocalLogger(Logger paramLogger, boolean paramBoolean)
    {
      if (paramBoolean) {
        ensureAllDefaultLoggers(paramLogger);
      }
      String str = paramLogger.getName();
      if (str == null) {
        throw new NullPointerException();
      }
      LogManager.LoggerWeakRef localLoggerWeakRef1 = (LogManager.LoggerWeakRef)namedLoggers.get(str);
      if (localLoggerWeakRef1 != null) {
        if (localLoggerWeakRef1.get() == null) {
          localLoggerWeakRef1.dispose();
        } else {
          return false;
        }
      }
      LogManager localLogManager = getOwner();
      paramLogger.setLogManager(localLogManager);
      LogManager tmp80_78 = localLogManager;
      tmp80_78.getClass();
      localLoggerWeakRef1 = new LogManager.LoggerWeakRef(tmp80_78, paramLogger);
      namedLoggers.put(str, localLoggerWeakRef1);
      Level localLevel = localLogManager.getLevelProperty(str + ".level", null);
      if ((localLevel != null) && (!paramLogger.isLevelInitialized())) {
        LogManager.doSetLevel(paramLogger, localLevel);
      }
      processParentHandlers(paramLogger, str);
      LogManager.LogNode localLogNode1 = getNode(str);
      loggerRef = localLoggerWeakRef1;
      Logger localLogger = null;
      for (LogManager.LogNode localLogNode2 = parent; localLogNode2 != null; localLogNode2 = parent)
      {
        LogManager.LoggerWeakRef localLoggerWeakRef2 = loggerRef;
        if (localLoggerWeakRef2 != null)
        {
          localLogger = (Logger)localLoggerWeakRef2.get();
          if (localLogger != null) {
            break;
          }
        }
      }
      if (localLogger != null) {
        LogManager.doSetParent(paramLogger, localLogger);
      }
      localLogNode1.walkAndSetParent(paramLogger);
      localLoggerWeakRef1.setNode(localLogNode1);
      return true;
    }
    
    synchronized void removeLoggerRef(String paramString, LogManager.LoggerWeakRef paramLoggerWeakRef)
    {
      namedLoggers.remove(paramString, paramLoggerWeakRef);
    }
    
    synchronized Enumeration<String> getLoggerNames()
    {
      ensureInitialized();
      return namedLoggers.keys();
    }
    
    private void processParentHandlers(final Logger paramLogger, final String paramString)
    {
      final LogManager localLogManager = getOwner();
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          if (paramLogger != localLogManagerrootLogger)
          {
            boolean bool = localLogManager.getBooleanProperty(paramString + ".useParentHandlers", true);
            if (!bool) {
              paramLogger.setUseParentHandlers(false);
            }
          }
          return null;
        }
      });
      int j;
      for (int i = 1;; i = j + 1)
      {
        j = paramString.indexOf(".", i);
        if (j < 0) {
          break;
        }
        String str = paramString.substring(0, j);
        if ((localLogManager.getProperty(str + ".level") != null) || (localLogManager.getProperty(str + ".handlers") != null)) {
          demandLogger(str, null);
        }
      }
    }
    
    LogManager.LogNode getNode(String paramString)
    {
      if ((paramString == null) || (paramString.equals(""))) {
        return root;
      }
      LogManager.LogNode localLogNode;
      for (Object localObject = root; paramString.length() > 0; localObject = localLogNode)
      {
        int i = paramString.indexOf(".");
        String str;
        if (i > 0)
        {
          str = paramString.substring(0, i);
          paramString = paramString.substring(i + 1);
        }
        else
        {
          str = paramString;
          paramString = "";
        }
        if (children == null) {
          children = new HashMap();
        }
        localLogNode = (LogManager.LogNode)children.get(str);
        if (localLogNode == null)
        {
          localLogNode = new LogManager.LogNode((LogManager.LogNode)localObject, this);
          children.put(str, localLogNode);
        }
      }
      return (LogManager.LogNode)localObject;
    }
  }
  
  final class LoggerWeakRef
    extends WeakReference<Logger>
  {
    private String name;
    private LogManager.LogNode node;
    private WeakReference<Logger> parentRef;
    private boolean disposed = false;
    
    LoggerWeakRef(Logger paramLogger)
    {
      super(loggerRefQueue);
      name = paramLogger.getName();
    }
    
    void dispose()
    {
      synchronized (this)
      {
        if (disposed) {
          return;
        }
        disposed = true;
      }
      ??? = node;
      if (??? != null) {
        synchronized (context)
        {
          context.removeLoggerRef(name, this);
          name = null;
          if (loggerRef == this) {
            loggerRef = null;
          }
          node = null;
        }
      }
      if (parentRef != null)
      {
        ??? = (Logger)parentRef.get();
        if (??? != null) {
          ((Logger)???).removeChildLogger(this);
        }
        parentRef = null;
      }
    }
    
    void setNode(LogManager.LogNode paramLogNode)
    {
      node = paramLogNode;
    }
    
    void setParentRef(WeakReference<Logger> paramWeakReference)
    {
      parentRef = paramWeakReference;
    }
  }
  
  private final class RootLogger
    extends Logger
  {
    private RootLogger()
    {
      super(null, null, LogManager.this, true);
    }
    
    public void log(LogRecord paramLogRecord)
    {
      LogManager.this.initializeGlobalHandlers();
      super.log(paramLogRecord);
    }
    
    public void addHandler(Handler paramHandler)
    {
      LogManager.this.initializeGlobalHandlers();
      super.addHandler(paramHandler);
    }
    
    public void removeHandler(Handler paramHandler)
    {
      LogManager.this.initializeGlobalHandlers();
      super.removeHandler(paramHandler);
    }
    
    Handler[] accessCheckedHandlers()
    {
      LogManager.this.initializeGlobalHandlers();
      return super.accessCheckedHandlers();
    }
  }
  
  final class SystemLoggerContext
    extends LogManager.LoggerContext
  {
    SystemLoggerContext()
    {
      super(null);
    }
    
    Logger demandLogger(String paramString1, String paramString2)
    {
      Object localObject = findLogger(paramString1);
      if (localObject == null)
      {
        Logger localLogger = new Logger(paramString1, paramString2, null, getOwner(), true);
        do
        {
          if (addLocalLogger(localLogger)) {
            localObject = localLogger;
          } else {
            localObject = findLogger(paramString1);
          }
        } while (localObject == null);
      }
      return (Logger)localObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\LogManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
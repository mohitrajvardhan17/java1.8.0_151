package java.util.logging;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Logger
{
  private static final Handler[] emptyHandlers = new Handler[0];
  private static final int offValue = Level.OFF.intValue();
  static final String SYSTEM_LOGGER_RB_NAME = "sun.util.logging.resources.logging";
  private static final LoggerBundle SYSTEM_BUNDLE = new LoggerBundle("sun.util.logging.resources.logging", null, null);
  private static final LoggerBundle NO_RESOURCE_BUNDLE = new LoggerBundle(null, null, null);
  private volatile LogManager manager;
  private String name;
  private final CopyOnWriteArrayList<Handler> handlers = new CopyOnWriteArrayList();
  private volatile LoggerBundle loggerBundle = NO_RESOURCE_BUNDLE;
  private volatile boolean useParentHandlers = true;
  private volatile Filter filter;
  private boolean anonymous;
  private ResourceBundle catalog;
  private String catalogName;
  private Locale catalogLocale;
  private static final Object treeLock = new Object();
  private volatile Logger parent;
  private ArrayList<LogManager.LoggerWeakRef> kids;
  private volatile Level levelObject;
  private volatile int levelValue;
  private WeakReference<ClassLoader> callersClassLoaderRef;
  private final boolean isSystemLogger;
  public static final String GLOBAL_LOGGER_NAME = "global";
  @Deprecated
  public static final Logger global = new Logger("global");
  
  public static final Logger getGlobal()
  {
    LogManager.getLogManager();
    return global;
  }
  
  protected Logger(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null, LogManager.getLogManager(), false);
  }
  
  Logger(String paramString1, String paramString2, Class<?> paramClass, LogManager paramLogManager, boolean paramBoolean)
  {
    manager = paramLogManager;
    isSystemLogger = paramBoolean;
    setupResourceInfo(paramString2, paramClass);
    name = paramString1;
    levelValue = Level.INFO.intValue();
  }
  
  private void setCallersClassLoaderRef(Class<?> paramClass)
  {
    Object localObject = paramClass != null ? paramClass.getClassLoader() : null;
    if (localObject != null) {
      callersClassLoaderRef = new WeakReference(localObject);
    }
  }
  
  private ClassLoader getCallersClassLoader()
  {
    return callersClassLoaderRef != null ? (ClassLoader)callersClassLoaderRef.get() : null;
  }
  
  private Logger(String paramString)
  {
    name = paramString;
    isSystemLogger = true;
    levelValue = Level.INFO.intValue();
  }
  
  void setLogManager(LogManager paramLogManager)
  {
    manager = paramLogManager;
  }
  
  private void checkPermission()
    throws SecurityException
  {
    if (!anonymous)
    {
      if (manager == null) {
        manager = LogManager.getLogManager();
      }
      manager.checkPermission();
    }
  }
  
  private static Logger demandLogger(String paramString1, String paramString2, Class<?> paramClass)
  {
    LogManager localLogManager = LogManager.getLogManager();
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (!SystemLoggerHelper.disableCallerCheck) && (paramClass.getClassLoader() == null)) {
      return localLogManager.demandSystemLogger(paramString1, paramString2);
    }
    return localLogManager.demandLogger(paramString1, paramString2, paramClass);
  }
  
  @CallerSensitive
  public static Logger getLogger(String paramString)
  {
    return demandLogger(paramString, null, Reflection.getCallerClass());
  }
  
  @CallerSensitive
  public static Logger getLogger(String paramString1, String paramString2)
  {
    Class localClass = Reflection.getCallerClass();
    Logger localLogger = demandLogger(paramString1, paramString2, localClass);
    localLogger.setupResourceInfo(paramString2, localClass);
    return localLogger;
  }
  
  static Logger getPlatformLogger(String paramString)
  {
    LogManager localLogManager = LogManager.getLogManager();
    Logger localLogger = localLogManager.demandSystemLogger(paramString, "sun.util.logging.resources.logging");
    return localLogger;
  }
  
  public static Logger getAnonymousLogger()
  {
    return getAnonymousLogger(null);
  }
  
  @CallerSensitive
  public static Logger getAnonymousLogger(String paramString)
  {
    LogManager localLogManager = LogManager.getLogManager();
    localLogManager.drainLoggerRefQueueBounded();
    Logger localLogger1 = new Logger(null, paramString, Reflection.getCallerClass(), localLogManager, false);
    anonymous = true;
    Logger localLogger2 = localLogManager.getLogger("");
    localLogger1.doSetParent(localLogger2);
    return localLogger1;
  }
  
  public ResourceBundle getResourceBundle()
  {
    return findResourceBundle(getResourceBundleName(), true);
  }
  
  public String getResourceBundleName()
  {
    return loggerBundle.resourceBundleName;
  }
  
  public void setFilter(Filter paramFilter)
    throws SecurityException
  {
    checkPermission();
    filter = paramFilter;
  }
  
  public Filter getFilter()
  {
    return filter;
  }
  
  public void log(LogRecord paramLogRecord)
  {
    if (!isLoggable(paramLogRecord.getLevel())) {
      return;
    }
    Filter localFilter = filter;
    if ((localFilter != null) && (!localFilter.isLoggable(paramLogRecord))) {
      return;
    }
    for (Logger localLogger = this; localLogger != null; localLogger = isSystemLogger ? parent : localLogger.getParent())
    {
      Handler[] arrayOfHandler1 = isSystemLogger ? localLogger.accessCheckedHandlers() : localLogger.getHandlers();
      for (Handler localHandler : arrayOfHandler1) {
        localHandler.publish(paramLogRecord);
      }
      boolean bool = isSystemLogger ? useParentHandlers : localLogger.getUseParentHandlers();
      if (!bool) {
        break;
      }
    }
  }
  
  private void doLog(LogRecord paramLogRecord)
  {
    paramLogRecord.setLoggerName(name);
    LoggerBundle localLoggerBundle = getEffectiveLoggerBundle();
    ResourceBundle localResourceBundle = userBundle;
    String str = resourceBundleName;
    if ((str != null) && (localResourceBundle != null))
    {
      paramLogRecord.setResourceBundleName(str);
      paramLogRecord.setResourceBundle(localResourceBundle);
    }
    log(paramLogRecord);
  }
  
  public void log(Level paramLevel, String paramString)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
    doLog(localLogRecord);
  }
  
  public void log(Level paramLevel, Supplier<String> paramSupplier)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    doLog(localLogRecord);
  }
  
  public void log(Level paramLevel, String paramString, Object paramObject)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
    Object[] arrayOfObject = { paramObject };
    localLogRecord.setParameters(arrayOfObject);
    doLog(localLogRecord);
  }
  
  public void log(Level paramLevel, String paramString, Object[] paramArrayOfObject)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
    localLogRecord.setParameters(paramArrayOfObject);
    doLog(localLogRecord);
  }
  
  public void log(Level paramLevel, String paramString, Throwable paramThrowable)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString);
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord);
  }
  
  public void log(Level paramLevel, Throwable paramThrowable, Supplier<String> paramSupplier)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    doLog(localLogRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, Supplier<String> paramSupplier)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    doLog(localLogRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    Object[] arrayOfObject = { paramObject };
    localLogRecord.setParameters(arrayOfObject);
    doLog(localLogRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setParameters(paramArrayOfObject);
    doLog(localLogRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Throwable paramThrowable)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, Throwable paramThrowable, Supplier<String> paramSupplier)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord);
  }
  
  private void doLog(LogRecord paramLogRecord, String paramString)
  {
    paramLogRecord.setLoggerName(name);
    if (paramString != null)
    {
      paramLogRecord.setResourceBundleName(paramString);
      paramLogRecord.setResourceBundle(findResourceBundle(paramString, false));
    }
    log(paramLogRecord);
  }
  
  private void doLog(LogRecord paramLogRecord, ResourceBundle paramResourceBundle)
  {
    paramLogRecord.setLoggerName(name);
    if (paramResourceBundle != null)
    {
      paramLogRecord.setResourceBundleName(paramResourceBundle.getBaseBundleName());
      paramLogRecord.setResourceBundle(paramResourceBundle);
    }
    log(paramLogRecord);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    doLog(localLogRecord, paramString3);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    Object[] arrayOfObject = { paramObject };
    localLogRecord.setParameters(arrayOfObject);
    doLog(localLogRecord, paramString3);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object[] paramArrayOfObject)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setParameters(paramArrayOfObject);
    doLog(localLogRecord, paramString3);
  }
  
  public void logrb(Level paramLevel, String paramString1, String paramString2, ResourceBundle paramResourceBundle, String paramString3, Object... paramVarArgs)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    if ((paramVarArgs != null) && (paramVarArgs.length != 0)) {
      localLogRecord.setParameters(paramVarArgs);
    }
    doLog(localLogRecord, paramResourceBundle);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Throwable paramThrowable)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString4);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord, paramString3);
  }
  
  public void logrb(Level paramLevel, String paramString1, String paramString2, ResourceBundle paramResourceBundle, String paramString3, Throwable paramThrowable)
  {
    if (!isLoggable(paramLevel)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(paramLevel, paramString3);
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord, paramResourceBundle);
  }
  
  public void entering(String paramString1, String paramString2)
  {
    logp(Level.FINER, paramString1, paramString2, "ENTRY");
  }
  
  public void entering(String paramString1, String paramString2, Object paramObject)
  {
    logp(Level.FINER, paramString1, paramString2, "ENTRY {0}", paramObject);
  }
  
  public void entering(String paramString1, String paramString2, Object[] paramArrayOfObject)
  {
    String str = "ENTRY";
    if (paramArrayOfObject == null)
    {
      logp(Level.FINER, paramString1, paramString2, str);
      return;
    }
    if (!isLoggable(Level.FINER)) {
      return;
    }
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      str = str + " {" + i + "}";
    }
    logp(Level.FINER, paramString1, paramString2, str, paramArrayOfObject);
  }
  
  public void exiting(String paramString1, String paramString2)
  {
    logp(Level.FINER, paramString1, paramString2, "RETURN");
  }
  
  public void exiting(String paramString1, String paramString2, Object paramObject)
  {
    logp(Level.FINER, paramString1, paramString2, "RETURN {0}", paramObject);
  }
  
  public void throwing(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (!isLoggable(Level.FINER)) {
      return;
    }
    LogRecord localLogRecord = new LogRecord(Level.FINER, "THROW");
    localLogRecord.setSourceClassName(paramString1);
    localLogRecord.setSourceMethodName(paramString2);
    localLogRecord.setThrown(paramThrowable);
    doLog(localLogRecord);
  }
  
  public void severe(String paramString)
  {
    log(Level.SEVERE, paramString);
  }
  
  public void warning(String paramString)
  {
    log(Level.WARNING, paramString);
  }
  
  public void info(String paramString)
  {
    log(Level.INFO, paramString);
  }
  
  public void config(String paramString)
  {
    log(Level.CONFIG, paramString);
  }
  
  public void fine(String paramString)
  {
    log(Level.FINE, paramString);
  }
  
  public void finer(String paramString)
  {
    log(Level.FINER, paramString);
  }
  
  public void finest(String paramString)
  {
    log(Level.FINEST, paramString);
  }
  
  public void severe(Supplier<String> paramSupplier)
  {
    log(Level.SEVERE, paramSupplier);
  }
  
  public void warning(Supplier<String> paramSupplier)
  {
    log(Level.WARNING, paramSupplier);
  }
  
  public void info(Supplier<String> paramSupplier)
  {
    log(Level.INFO, paramSupplier);
  }
  
  public void config(Supplier<String> paramSupplier)
  {
    log(Level.CONFIG, paramSupplier);
  }
  
  public void fine(Supplier<String> paramSupplier)
  {
    log(Level.FINE, paramSupplier);
  }
  
  public void finer(Supplier<String> paramSupplier)
  {
    log(Level.FINER, paramSupplier);
  }
  
  public void finest(Supplier<String> paramSupplier)
  {
    log(Level.FINEST, paramSupplier);
  }
  
  public void setLevel(Level paramLevel)
    throws SecurityException
  {
    checkPermission();
    synchronized (treeLock)
    {
      levelObject = paramLevel;
      updateEffectiveLevel();
    }
  }
  
  final boolean isLevelInitialized()
  {
    return levelObject != null;
  }
  
  public Level getLevel()
  {
    return levelObject;
  }
  
  public boolean isLoggable(Level paramLevel)
  {
    return (paramLevel.intValue() >= levelValue) && (levelValue != offValue);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void addHandler(Handler paramHandler)
    throws SecurityException
  {
    paramHandler.getClass();
    checkPermission();
    handlers.add(paramHandler);
  }
  
  public void removeHandler(Handler paramHandler)
    throws SecurityException
  {
    checkPermission();
    if (paramHandler == null) {
      return;
    }
    handlers.remove(paramHandler);
  }
  
  public Handler[] getHandlers()
  {
    return accessCheckedHandlers();
  }
  
  Handler[] accessCheckedHandlers()
  {
    return (Handler[])handlers.toArray(emptyHandlers);
  }
  
  public void setUseParentHandlers(boolean paramBoolean)
  {
    checkPermission();
    useParentHandlers = paramBoolean;
  }
  
  public boolean getUseParentHandlers()
  {
    return useParentHandlers;
  }
  
  private static ResourceBundle findSystemResourceBundle(Locale paramLocale)
  {
    (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ResourceBundle run()
      {
        try
        {
          return ResourceBundle.getBundle("sun.util.logging.resources.logging", val$locale, ClassLoader.getSystemClassLoader());
        }
        catch (MissingResourceException localMissingResourceException)
        {
          throw new InternalError(localMissingResourceException.toString());
        }
      }
    });
  }
  
  private synchronized ResourceBundle findResourceBundle(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return null;
    }
    Locale localLocale = Locale.getDefault();
    LoggerBundle localLoggerBundle = loggerBundle;
    if ((userBundle != null) && (paramString.equals(resourceBundleName))) {
      return userBundle;
    }
    if ((catalog != null) && (localLocale.equals(catalogLocale)) && (paramString.equals(catalogName))) {
      return catalog;
    }
    if (paramString.equals("sun.util.logging.resources.logging"))
    {
      catalog = findSystemResourceBundle(localLocale);
      catalogName = paramString;
      catalogLocale = localLocale;
      return catalog;
    }
    ClassLoader localClassLoader1 = Thread.currentThread().getContextClassLoader();
    if (localClassLoader1 == null) {
      localClassLoader1 = ClassLoader.getSystemClassLoader();
    }
    try
    {
      catalog = ResourceBundle.getBundle(paramString, localLocale, localClassLoader1);
      catalogName = paramString;
      catalogLocale = localLocale;
      return catalog;
    }
    catch (MissingResourceException localMissingResourceException1)
    {
      if (paramBoolean)
      {
        ClassLoader localClassLoader2 = getCallersClassLoader();
        if ((localClassLoader2 == null) || (localClassLoader2 == localClassLoader1)) {
          return null;
        }
        try
        {
          catalog = ResourceBundle.getBundle(paramString, localLocale, localClassLoader2);
          catalogName = paramString;
          catalogLocale = localLocale;
          return catalog;
        }
        catch (MissingResourceException localMissingResourceException2)
        {
          return null;
        }
      }
    }
    return null;
  }
  
  private synchronized void setupResourceInfo(String paramString, Class<?> paramClass)
  {
    LoggerBundle localLoggerBundle = loggerBundle;
    if (resourceBundleName != null)
    {
      if (resourceBundleName.equals(paramString)) {
        return;
      }
      throw new IllegalArgumentException(resourceBundleName + " != " + paramString);
    }
    if (paramString == null) {
      return;
    }
    setCallersClassLoaderRef(paramClass);
    if ((isSystemLogger) && (getCallersClassLoader() != null)) {
      checkPermission();
    }
    if (findResourceBundle(paramString, true) == null)
    {
      callersClassLoaderRef = null;
      throw new MissingResourceException("Can't find " + paramString + " bundle", paramString, "");
    }
    assert (userBundle == null);
    loggerBundle = LoggerBundle.get(paramString, null);
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle)
  {
    checkPermission();
    String str = paramResourceBundle.getBaseBundleName();
    if ((str == null) || (str.isEmpty())) {
      throw new IllegalArgumentException("resource bundle must have a name");
    }
    synchronized (this)
    {
      LoggerBundle localLoggerBundle = loggerBundle;
      int i = (resourceBundleName == null) || (resourceBundleName.equals(str)) ? 1 : 0;
      if (i == 0) {
        throw new IllegalArgumentException("can't replace resource bundle");
      }
      loggerBundle = LoggerBundle.get(str, paramResourceBundle);
    }
  }
  
  public Logger getParent()
  {
    return parent;
  }
  
  public void setParent(Logger paramLogger)
  {
    if (paramLogger == null) {
      throw new NullPointerException();
    }
    if (manager == null) {
      manager = LogManager.getLogManager();
    }
    manager.checkPermission();
    doSetParent(paramLogger);
  }
  
  private void doSetParent(Logger paramLogger)
  {
    synchronized (treeLock)
    {
      LogManager.LoggerWeakRef localLoggerWeakRef = null;
      if (parent != null)
      {
        Iterator localIterator = parent.kids.iterator();
        while (localIterator.hasNext())
        {
          localLoggerWeakRef = (LogManager.LoggerWeakRef)localIterator.next();
          Logger localLogger = (Logger)localLoggerWeakRef.get();
          if (localLogger == this)
          {
            localIterator.remove();
            break;
          }
          localLoggerWeakRef = null;
        }
      }
      parent = paramLogger;
      if (parent.kids == null) {
        parent.kids = new ArrayList(2);
      }
      if (localLoggerWeakRef == null)
      {
        LogManager tmp120_117 = manager;
        tmp120_117.getClass();
        localLoggerWeakRef = new LogManager.LoggerWeakRef(tmp120_117, this);
      }
      localLoggerWeakRef.setParentRef(new WeakReference(parent));
      parent.kids.add(localLoggerWeakRef);
      updateEffectiveLevel();
    }
  }
  
  final void removeChildLogger(LogManager.LoggerWeakRef paramLoggerWeakRef)
  {
    synchronized (treeLock)
    {
      Iterator localIterator = kids.iterator();
      while (localIterator.hasNext())
      {
        LogManager.LoggerWeakRef localLoggerWeakRef = (LogManager.LoggerWeakRef)localIterator.next();
        if (localLoggerWeakRef == paramLoggerWeakRef)
        {
          localIterator.remove();
          return;
        }
      }
    }
  }
  
  private void updateEffectiveLevel()
  {
    int i;
    if (levelObject != null) {
      i = levelObject.intValue();
    } else if (parent != null) {
      i = parent.levelValue;
    } else {
      i = Level.INFO.intValue();
    }
    if (levelValue == i) {
      return;
    }
    levelValue = i;
    if (kids != null) {
      for (int j = 0; j < kids.size(); j++)
      {
        LogManager.LoggerWeakRef localLoggerWeakRef = (LogManager.LoggerWeakRef)kids.get(j);
        Logger localLogger = (Logger)localLoggerWeakRef.get();
        if (localLogger != null) {
          localLogger.updateEffectiveLevel();
        }
      }
    }
  }
  
  private LoggerBundle getEffectiveLoggerBundle()
  {
    LoggerBundle localLoggerBundle1 = loggerBundle;
    if (localLoggerBundle1.isSystemBundle()) {
      return SYSTEM_BUNDLE;
    }
    ResourceBundle localResourceBundle = getResourceBundle();
    if ((localResourceBundle != null) && (localResourceBundle == userBundle)) {
      return localLoggerBundle1;
    }
    if (localResourceBundle != null)
    {
      localObject = getResourceBundleName();
      return LoggerBundle.get((String)localObject, localResourceBundle);
    }
    for (Object localObject = parent; localObject != null; localObject = isSystemLogger ? parent : ((Logger)localObject).getParent())
    {
      LoggerBundle localLoggerBundle2 = loggerBundle;
      if (localLoggerBundle2.isSystemBundle()) {
        return SYSTEM_BUNDLE;
      }
      if (userBundle != null) {
        return localLoggerBundle2;
      }
      String str = isSystemLogger ? null : isSystemLogger ? resourceBundleName : ((Logger)localObject).getResourceBundleName();
      if (str != null) {
        return LoggerBundle.get(str, findResourceBundle(str, true));
      }
    }
    return NO_RESOURCE_BUNDLE;
  }
  
  private static final class LoggerBundle
  {
    final String resourceBundleName;
    final ResourceBundle userBundle;
    
    private LoggerBundle(String paramString, ResourceBundle paramResourceBundle)
    {
      resourceBundleName = paramString;
      userBundle = paramResourceBundle;
    }
    
    boolean isSystemBundle()
    {
      return "sun.util.logging.resources.logging".equals(resourceBundleName);
    }
    
    static LoggerBundle get(String paramString, ResourceBundle paramResourceBundle)
    {
      if ((paramString == null) && (paramResourceBundle == null)) {
        return Logger.NO_RESOURCE_BUNDLE;
      }
      if (("sun.util.logging.resources.logging".equals(paramString)) && (paramResourceBundle == null)) {
        return Logger.SYSTEM_BUNDLE;
      }
      return new LoggerBundle(paramString, paramResourceBundle);
    }
  }
  
  private static class SystemLoggerHelper
  {
    static boolean disableCallerCheck = getBooleanProperty("sun.util.logging.disableCallerCheck");
    
    private SystemLoggerHelper() {}
    
    private static boolean getBooleanProperty(String paramString)
    {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty(val$key);
        }
      });
      return Boolean.valueOf(str).booleanValue();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
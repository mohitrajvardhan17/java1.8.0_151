package sun.util.logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class PlatformLogger
{
  private static final int OFF = Integer.MAX_VALUE;
  private static final int SEVERE = 1000;
  private static final int WARNING = 900;
  private static final int INFO = 800;
  private static final int CONFIG = 700;
  private static final int FINE = 500;
  private static final int FINER = 400;
  private static final int FINEST = 300;
  private static final int ALL = Integer.MIN_VALUE;
  private static final Level DEFAULT_LEVEL = Level.INFO;
  private static boolean loggingEnabled = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      String str1 = System.getProperty("java.util.logging.config.class");
      String str2 = System.getProperty("java.util.logging.config.file");
      return Boolean.valueOf((str1 != null) || (str2 != null));
    }
  })).booleanValue();
  private static Map<String, WeakReference<PlatformLogger>> loggers = new HashMap();
  private volatile LoggerProxy loggerProxy;
  private volatile JavaLoggerProxy javaLoggerProxy;
  
  public static synchronized PlatformLogger getLogger(String paramString)
  {
    PlatformLogger localPlatformLogger = null;
    WeakReference localWeakReference = (WeakReference)loggers.get(paramString);
    if (localWeakReference != null) {
      localPlatformLogger = (PlatformLogger)localWeakReference.get();
    }
    if (localPlatformLogger == null)
    {
      localPlatformLogger = new PlatformLogger(paramString);
      loggers.put(paramString, new WeakReference(localPlatformLogger));
    }
    return localPlatformLogger;
  }
  
  public static synchronized void redirectPlatformLoggers()
  {
    if ((loggingEnabled) || (!LoggingSupport.isAvailable())) {
      return;
    }
    loggingEnabled = true;
    Iterator localIterator = loggers.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      WeakReference localWeakReference = (WeakReference)localEntry.getValue();
      PlatformLogger localPlatformLogger = (PlatformLogger)localWeakReference.get();
      if (localPlatformLogger != null) {
        localPlatformLogger.redirectToJavaLoggerProxy();
      }
    }
  }
  
  private void redirectToJavaLoggerProxy()
  {
    DefaultLoggerProxy localDefaultLoggerProxy = (DefaultLoggerProxy)DefaultLoggerProxy.class.cast(loggerProxy);
    JavaLoggerProxy localJavaLoggerProxy = new JavaLoggerProxy(name, level);
    javaLoggerProxy = localJavaLoggerProxy;
    loggerProxy = localJavaLoggerProxy;
  }
  
  private PlatformLogger(String paramString)
  {
    if (loggingEnabled) {
      loggerProxy = (javaLoggerProxy = new JavaLoggerProxy(paramString));
    } else {
      loggerProxy = new DefaultLoggerProxy(paramString);
    }
  }
  
  public boolean isEnabled()
  {
    return loggerProxy.isEnabled();
  }
  
  public String getName()
  {
    return loggerProxy.name;
  }
  
  public boolean isLoggable(Level paramLevel)
  {
    if (paramLevel == null) {
      throw new NullPointerException();
    }
    JavaLoggerProxy localJavaLoggerProxy = javaLoggerProxy;
    return localJavaLoggerProxy != null ? localJavaLoggerProxy.isLoggable(paramLevel) : loggerProxy.isLoggable(paramLevel);
  }
  
  public Level level()
  {
    return loggerProxy.getLevel();
  }
  
  public void setLevel(Level paramLevel)
  {
    loggerProxy.setLevel(paramLevel);
  }
  
  public void severe(String paramString)
  {
    loggerProxy.doLog(Level.SEVERE, paramString);
  }
  
  public void severe(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.SEVERE, paramString, paramThrowable);
  }
  
  public void severe(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.SEVERE, paramString, paramVarArgs);
  }
  
  public void warning(String paramString)
  {
    loggerProxy.doLog(Level.WARNING, paramString);
  }
  
  public void warning(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.WARNING, paramString, paramThrowable);
  }
  
  public void warning(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.WARNING, paramString, paramVarArgs);
  }
  
  public void info(String paramString)
  {
    loggerProxy.doLog(Level.INFO, paramString);
  }
  
  public void info(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.INFO, paramString, paramThrowable);
  }
  
  public void info(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.INFO, paramString, paramVarArgs);
  }
  
  public void config(String paramString)
  {
    loggerProxy.doLog(Level.CONFIG, paramString);
  }
  
  public void config(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.CONFIG, paramString, paramThrowable);
  }
  
  public void config(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.CONFIG, paramString, paramVarArgs);
  }
  
  public void fine(String paramString)
  {
    loggerProxy.doLog(Level.FINE, paramString);
  }
  
  public void fine(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.FINE, paramString, paramThrowable);
  }
  
  public void fine(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.FINE, paramString, paramVarArgs);
  }
  
  public void finer(String paramString)
  {
    loggerProxy.doLog(Level.FINER, paramString);
  }
  
  public void finer(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.FINER, paramString, paramThrowable);
  }
  
  public void finer(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.FINER, paramString, paramVarArgs);
  }
  
  public void finest(String paramString)
  {
    loggerProxy.doLog(Level.FINEST, paramString);
  }
  
  public void finest(String paramString, Throwable paramThrowable)
  {
    loggerProxy.doLog(Level.FINEST, paramString, paramThrowable);
  }
  
  public void finest(String paramString, Object... paramVarArgs)
  {
    loggerProxy.doLog(Level.FINEST, paramString, paramVarArgs);
  }
  
  static
  {
    try
    {
      Class.forName("sun.util.logging.PlatformLogger$DefaultLoggerProxy", false, PlatformLogger.class.getClassLoader());
      Class.forName("sun.util.logging.PlatformLogger$JavaLoggerProxy", false, PlatformLogger.class.getClassLoader());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InternalError(localClassNotFoundException);
    }
  }
  
  private static final class DefaultLoggerProxy
    extends PlatformLogger.LoggerProxy
  {
    volatile PlatformLogger.Level effectiveLevel = deriveEffectiveLevel(null);
    volatile PlatformLogger.Level level = null;
    private static final String formatString = LoggingSupport.getSimpleFormat(false);
    private Date date = new Date();
    
    private static PrintStream outputStream()
    {
      return System.err;
    }
    
    DefaultLoggerProxy(String paramString)
    {
      super();
    }
    
    boolean isEnabled()
    {
      return effectiveLevel != PlatformLogger.Level.OFF;
    }
    
    PlatformLogger.Level getLevel()
    {
      return level;
    }
    
    void setLevel(PlatformLogger.Level paramLevel)
    {
      PlatformLogger.Level localLevel = level;
      if (localLevel != paramLevel)
      {
        level = paramLevel;
        effectiveLevel = deriveEffectiveLevel(paramLevel);
      }
    }
    
    void doLog(PlatformLogger.Level paramLevel, String paramString)
    {
      if (isLoggable(paramLevel)) {
        outputStream().print(format(paramLevel, paramString, null));
      }
    }
    
    void doLog(PlatformLogger.Level paramLevel, String paramString, Throwable paramThrowable)
    {
      if (isLoggable(paramLevel)) {
        outputStream().print(format(paramLevel, paramString, paramThrowable));
      }
    }
    
    void doLog(PlatformLogger.Level paramLevel, String paramString, Object... paramVarArgs)
    {
      if (isLoggable(paramLevel))
      {
        String str = formatMessage(paramString, paramVarArgs);
        outputStream().print(format(paramLevel, str, null));
      }
    }
    
    boolean isLoggable(PlatformLogger.Level paramLevel)
    {
      PlatformLogger.Level localLevel = effectiveLevel;
      return (paramLevel.intValue() >= localLevel.intValue()) && (localLevel != PlatformLogger.Level.OFF);
    }
    
    private PlatformLogger.Level deriveEffectiveLevel(PlatformLogger.Level paramLevel)
    {
      return paramLevel == null ? PlatformLogger.DEFAULT_LEVEL : paramLevel;
    }
    
    private String formatMessage(String paramString, Object... paramVarArgs)
    {
      try
      {
        if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
          return paramString;
        }
        if ((paramString.indexOf("{0") >= 0) || (paramString.indexOf("{1") >= 0) || (paramString.indexOf("{2") >= 0) || (paramString.indexOf("{3") >= 0)) {
          return MessageFormat.format(paramString, paramVarArgs);
        }
        return paramString;
      }
      catch (Exception localException) {}
      return paramString;
    }
    
    private synchronized String format(PlatformLogger.Level paramLevel, String paramString, Throwable paramThrowable)
    {
      date.setTime(System.currentTimeMillis());
      String str = "";
      if (paramThrowable != null)
      {
        StringWriter localStringWriter = new StringWriter();
        PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
        localPrintWriter.println();
        paramThrowable.printStackTrace(localPrintWriter);
        localPrintWriter.close();
        str = localStringWriter.toString();
      }
      return String.format(formatString, new Object[] { date, getCallerInfo(), name, paramLevel.name(), paramString, str });
    }
    
    private String getCallerInfo()
    {
      Object localObject = null;
      String str1 = null;
      JavaLangAccess localJavaLangAccess = SharedSecrets.getJavaLangAccess();
      Throwable localThrowable = new Throwable();
      int i = localJavaLangAccess.getStackTraceDepth(localThrowable);
      String str2 = "sun.util.logging.PlatformLogger";
      int j = 1;
      for (int k = 0; k < i; k++)
      {
        StackTraceElement localStackTraceElement = localJavaLangAccess.getStackTraceElement(localThrowable, k);
        String str3 = localStackTraceElement.getClassName();
        if (j != 0)
        {
          if (str3.equals(str2)) {
            j = 0;
          }
        }
        else if (!str3.equals(str2))
        {
          localObject = str3;
          str1 = localStackTraceElement.getMethodName();
          break;
        }
      }
      if (localObject != null) {
        return (String)localObject + " " + str1;
      }
      return name;
    }
  }
  
  private static final class JavaLoggerProxy
    extends PlatformLogger.LoggerProxy
  {
    private final Object javaLogger;
    
    JavaLoggerProxy(String paramString)
    {
      this(paramString, null);
    }
    
    JavaLoggerProxy(String paramString, PlatformLogger.Level paramLevel)
    {
      super();
      javaLogger = LoggingSupport.getLogger(paramString);
      if (paramLevel != null) {
        LoggingSupport.setLevel(javaLogger, javaLevel);
      }
    }
    
    void doLog(PlatformLogger.Level paramLevel, String paramString)
    {
      LoggingSupport.log(javaLogger, javaLevel, paramString);
    }
    
    void doLog(PlatformLogger.Level paramLevel, String paramString, Throwable paramThrowable)
    {
      LoggingSupport.log(javaLogger, javaLevel, paramString, paramThrowable);
    }
    
    void doLog(PlatformLogger.Level paramLevel, String paramString, Object... paramVarArgs)
    {
      if (!isLoggable(paramLevel)) {
        return;
      }
      int i = paramVarArgs != null ? paramVarArgs.length : 0;
      String[] arrayOfString = new String[i];
      for (int j = 0; j < i; j++) {
        arrayOfString[j] = String.valueOf(paramVarArgs[j]);
      }
      LoggingSupport.log(javaLogger, javaLevel, paramString, arrayOfString);
    }
    
    boolean isEnabled()
    {
      return LoggingSupport.isLoggable(javaLogger, OFFjavaLevel);
    }
    
    PlatformLogger.Level getLevel()
    {
      Object localObject = LoggingSupport.getLevel(javaLogger);
      if (localObject == null) {
        return null;
      }
      try
      {
        return PlatformLogger.Level.valueOf(LoggingSupport.getLevelName(localObject));
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return PlatformLogger.Level.valueOf(LoggingSupport.getLevelValue(localObject));
    }
    
    void setLevel(PlatformLogger.Level paramLevel)
    {
      LoggingSupport.setLevel(javaLogger, paramLevel == null ? null : javaLevel);
    }
    
    boolean isLoggable(PlatformLogger.Level paramLevel)
    {
      return LoggingSupport.isLoggable(javaLogger, javaLevel);
    }
    
    static
    {
      for (PlatformLogger.Level localLevel : ) {
        javaLevel = LoggingSupport.parseLevel(localLevel.name());
      }
    }
  }
  
  public static enum Level
  {
    ALL,  FINEST,  FINER,  FINE,  CONFIG,  INFO,  WARNING,  SEVERE,  OFF;
    
    Object javaLevel;
    private static final int[] LEVEL_VALUES = { Integer.MIN_VALUE, 300, 400, 500, 700, 800, 900, 1000, Integer.MAX_VALUE };
    
    private Level() {}
    
    public int intValue()
    {
      return LEVEL_VALUES[ordinal()];
    }
    
    static Level valueOf(int paramInt)
    {
      switch (paramInt)
      {
      case 300: 
        return FINEST;
      case 500: 
        return FINE;
      case 400: 
        return FINER;
      case 800: 
        return INFO;
      case 900: 
        return WARNING;
      case 700: 
        return CONFIG;
      case 1000: 
        return SEVERE;
      case 2147483647: 
        return OFF;
      case -2147483648: 
        return ALL;
      }
      int i = Arrays.binarySearch(LEVEL_VALUES, 0, LEVEL_VALUES.length - 2, paramInt);
      return values()[(-i - 1)];
    }
  }
  
  private static abstract class LoggerProxy
  {
    final String name;
    
    protected LoggerProxy(String paramString)
    {
      name = paramString;
    }
    
    abstract boolean isEnabled();
    
    abstract PlatformLogger.Level getLevel();
    
    abstract void setLevel(PlatformLogger.Level paramLevel);
    
    abstract void doLog(PlatformLogger.Level paramLevel, String paramString);
    
    abstract void doLog(PlatformLogger.Level paramLevel, String paramString, Throwable paramThrowable);
    
    abstract void doLog(PlatformLogger.Level paramLevel, String paramString, Object... paramVarArgs);
    
    abstract boolean isLoggable(PlatformLogger.Level paramLevel);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\logging\PlatformLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.management.snmp.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MibLogger
{
  final Logger logger;
  final String className;
  
  static String getClassName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    if (paramClass.isArray()) {
      return getClassName(paramClass.getComponentType()) + "[]";
    }
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    int j = str.length();
    if ((i < 0) || (i >= j)) {
      return str;
    }
    return str.substring(i + 1, j);
  }
  
  static String getLoggerName(Class<?> paramClass)
  {
    if (paramClass == null) {
      return "sun.management.snmp.jvminstr";
    }
    Package localPackage = paramClass.getPackage();
    if (localPackage == null) {
      return "sun.management.snmp.jvminstr";
    }
    String str = localPackage.getName();
    if (str == null) {
      return "sun.management.snmp.jvminstr";
    }
    return str;
  }
  
  public MibLogger(Class<?> paramClass)
  {
    this(getLoggerName(paramClass), getClassName(paramClass));
  }
  
  public MibLogger(Class<?> paramClass, String paramString)
  {
    this(getLoggerName(paramClass) + (paramString == null ? "" : new StringBuilder().append(".").append(paramString).toString()), getClassName(paramClass));
  }
  
  public MibLogger(String paramString)
  {
    this("sun.management.snmp.jvminstr", paramString);
  }
  
  public MibLogger(String paramString1, String paramString2)
  {
    Logger localLogger = null;
    try
    {
      localLogger = Logger.getLogger(paramString1);
    }
    catch (Exception localException) {}
    logger = localLogger;
    className = paramString2;
  }
  
  protected Logger getLogger()
  {
    return logger;
  }
  
  public boolean isTraceOn()
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return false;
    }
    return localLogger.isLoggable(Level.FINE);
  }
  
  public boolean isDebugOn()
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return false;
    }
    return localLogger.isLoggable(Level.FINEST);
  }
  
  public boolean isInfoOn()
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return false;
    }
    return localLogger.isLoggable(Level.INFO);
  }
  
  public boolean isConfigOn()
  {
    Logger localLogger = getLogger();
    if (localLogger == null) {
      return false;
    }
    return localLogger.isLoggable(Level.CONFIG);
  }
  
  public void config(String paramString1, String paramString2)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.CONFIG, className, paramString1, paramString2);
    }
  }
  
  public void config(String paramString, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.CONFIG, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public void config(String paramString1, String paramString2, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.CONFIG, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public void error(String paramString1, String paramString2)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.SEVERE, className, paramString1, paramString2);
    }
  }
  
  public void info(String paramString1, String paramString2)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.INFO, className, paramString1, paramString2);
    }
  }
  
  public void info(String paramString, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.INFO, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public void info(String paramString1, String paramString2, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.INFO, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public void warning(String paramString1, String paramString2)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.WARNING, className, paramString1, paramString2);
    }
  }
  
  public void warning(String paramString, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.WARNING, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public void warning(String paramString1, String paramString2, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.WARNING, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public void trace(String paramString1, String paramString2)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.FINE, className, paramString1, paramString2);
    }
  }
  
  public void trace(String paramString, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.FINE, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public void trace(String paramString1, String paramString2, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.FINE, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public void debug(String paramString1, String paramString2)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.FINEST, className, paramString1, paramString2);
    }
  }
  
  public void debug(String paramString, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.FINEST, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public void debug(String paramString1, String paramString2, Throwable paramThrowable)
  {
    Logger localLogger = getLogger();
    if (localLogger != null) {
      localLogger.logp(Level.FINEST, className, paramString1, paramString2, paramThrowable);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\util\MibLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
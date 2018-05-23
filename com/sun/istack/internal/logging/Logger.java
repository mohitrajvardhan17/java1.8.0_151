package com.sun.istack.internal.logging;

import com.sun.istack.internal.NotNull;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class Logger
{
  private static final String WS_LOGGING_SUBSYSTEM_NAME_ROOT = "com.sun.metro";
  private static final String ROOT_WS_PACKAGE = "com.sun.xml.internal.ws.";
  private static final Level METHOD_CALL_LEVEL_VALUE = Level.FINEST;
  private final String componentClassName;
  private final java.util.logging.Logger logger;
  
  protected Logger(String paramString1, String paramString2)
  {
    componentClassName = ("[" + paramString2 + "] ");
    logger = java.util.logging.Logger.getLogger(paramString1);
  }
  
  @NotNull
  public static Logger getLogger(@NotNull Class<?> paramClass)
  {
    return new Logger(getSystemLoggerName(paramClass), paramClass.getName());
  }
  
  @NotNull
  public static Logger getLogger(@NotNull String paramString, @NotNull Class<?> paramClass)
  {
    return new Logger(paramString, paramClass.getName());
  }
  
  static final String getSystemLoggerName(@NotNull Class<?> paramClass)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramClass.getPackage().getName());
    int i = localStringBuilder.lastIndexOf("com.sun.xml.internal.ws.");
    if (i > -1)
    {
      localStringBuilder.replace(0, i + "com.sun.xml.internal.ws.".length(), "");
      StringTokenizer localStringTokenizer = new StringTokenizer(localStringBuilder.toString(), ".");
      localStringBuilder = new StringBuilder("com.sun.metro").append(".");
      if (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        if ("api".equals(str)) {
          str = localStringTokenizer.nextToken();
        }
        localStringBuilder.append(str);
      }
    }
    return localStringBuilder.toString();
  }
  
  public void log(Level paramLevel, String paramString)
  {
    if (!logger.isLoggable(paramLevel)) {
      return;
    }
    logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void log(Level paramLevel, String paramString, Object paramObject)
  {
    if (!logger.isLoggable(paramLevel)) {
      return;
    }
    logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramString, paramObject);
  }
  
  public void log(Level paramLevel, String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(paramLevel)) {
      return;
    }
    logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void log(Level paramLevel, String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(paramLevel)) {
      return;
    }
    logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void finest(String paramString)
  {
    if (!logger.isLoggable(Level.FINEST)) {
      return;
    }
    logger.logp(Level.FINEST, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void finest(String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(Level.FINEST)) {
      return;
    }
    logger.logp(Level.FINEST, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void finest(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.FINEST)) {
      return;
    }
    logger.logp(Level.FINEST, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void finer(String paramString)
  {
    if (!logger.isLoggable(Level.FINER)) {
      return;
    }
    logger.logp(Level.FINER, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void finer(String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(Level.FINER)) {
      return;
    }
    logger.logp(Level.FINER, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void finer(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.FINER)) {
      return;
    }
    logger.logp(Level.FINER, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void fine(String paramString)
  {
    if (!logger.isLoggable(Level.FINE)) {
      return;
    }
    logger.logp(Level.FINE, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void fine(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.FINE)) {
      return;
    }
    logger.logp(Level.FINE, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void info(String paramString)
  {
    if (!logger.isLoggable(Level.INFO)) {
      return;
    }
    logger.logp(Level.INFO, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void info(String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(Level.INFO)) {
      return;
    }
    logger.logp(Level.INFO, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void info(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.INFO)) {
      return;
    }
    logger.logp(Level.INFO, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void config(String paramString)
  {
    if (!logger.isLoggable(Level.CONFIG)) {
      return;
    }
    logger.logp(Level.CONFIG, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void config(String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(Level.CONFIG)) {
      return;
    }
    logger.logp(Level.CONFIG, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void config(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.CONFIG)) {
      return;
    }
    logger.logp(Level.CONFIG, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void warning(String paramString)
  {
    if (!logger.isLoggable(Level.WARNING)) {
      return;
    }
    logger.logp(Level.WARNING, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void warning(String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(Level.WARNING)) {
      return;
    }
    logger.logp(Level.WARNING, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void warning(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.WARNING)) {
      return;
    }
    logger.logp(Level.WARNING, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void severe(String paramString)
  {
    if (!logger.isLoggable(Level.SEVERE)) {
      return;
    }
    logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramString);
  }
  
  public void severe(String paramString, Object[] paramArrayOfObject)
  {
    if (!logger.isLoggable(Level.SEVERE)) {
      return;
    }
    logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void severe(String paramString, Throwable paramThrowable)
  {
    if (!logger.isLoggable(Level.SEVERE)) {
      return;
    }
    logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public boolean isMethodCallLoggable()
  {
    return logger.isLoggable(METHOD_CALL_LEVEL_VALUE);
  }
  
  public boolean isLoggable(Level paramLevel)
  {
    return logger.isLoggable(paramLevel);
  }
  
  public void setLevel(Level paramLevel)
  {
    logger.setLevel(paramLevel);
  }
  
  public void entering()
  {
    if (!logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
      return;
    }
    logger.entering(componentClassName, getCallerMethodName());
  }
  
  public void entering(Object... paramVarArgs)
  {
    if (!logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
      return;
    }
    logger.entering(componentClassName, getCallerMethodName(), paramVarArgs);
  }
  
  public void exiting()
  {
    if (!logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
      return;
    }
    logger.exiting(componentClassName, getCallerMethodName());
  }
  
  public void exiting(Object paramObject)
  {
    if (!logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
      return;
    }
    logger.exiting(componentClassName, getCallerMethodName(), paramObject);
  }
  
  public <T extends Throwable> T logSevereException(T paramT, Throwable paramThrowable)
  {
    if (logger.isLoggable(Level.SEVERE)) {
      if (paramThrowable == null)
      {
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramT.getMessage());
      }
      else
      {
        paramT.initCause(paramThrowable);
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramT.getMessage(), paramThrowable);
      }
    }
    return paramT;
  }
  
  public <T extends Throwable> T logSevereException(T paramT, boolean paramBoolean)
  {
    if (logger.isLoggable(Level.SEVERE)) {
      if ((paramBoolean) && (paramT.getCause() != null)) {
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      } else {
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramT.getMessage());
      }
    }
    return paramT;
  }
  
  public <T extends Throwable> T logSevereException(T paramT)
  {
    if (logger.isLoggable(Level.SEVERE)) {
      if (paramT.getCause() == null) {
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramT.getMessage());
      } else {
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      }
    }
    return paramT;
  }
  
  public <T extends Throwable> T logException(T paramT, Throwable paramThrowable, Level paramLevel)
  {
    if (logger.isLoggable(paramLevel)) {
      if (paramThrowable == null)
      {
        logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramT.getMessage());
      }
      else
      {
        paramT.initCause(paramThrowable);
        logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramT.getMessage(), paramThrowable);
      }
    }
    return paramT;
  }
  
  public <T extends Throwable> T logException(T paramT, boolean paramBoolean, Level paramLevel)
  {
    if (logger.isLoggable(paramLevel)) {
      if ((paramBoolean) && (paramT.getCause() != null)) {
        logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      } else {
        logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramT.getMessage());
      }
    }
    return paramT;
  }
  
  public <T extends Throwable> T logException(T paramT, Level paramLevel)
  {
    if (logger.isLoggable(paramLevel)) {
      if (paramT.getCause() == null) {
        logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramT.getMessage());
      } else {
        logger.logp(paramLevel, componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      }
    }
    return paramT;
  }
  
  private static String getCallerMethodName()
  {
    return getStackMethodName(5);
  }
  
  private static String getStackMethodName(int paramInt)
  {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    String str;
    if (arrayOfStackTraceElement.length > paramInt + 1) {
      str = arrayOfStackTraceElement[paramInt].getMethodName();
    } else {
      str = "UNKNOWN METHOD";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
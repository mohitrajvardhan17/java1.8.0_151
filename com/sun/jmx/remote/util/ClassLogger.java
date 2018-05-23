package com.sun.jmx.remote.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassLogger
{
  private static final boolean ok;
  private final String className;
  private final Logger logger;
  
  public ClassLogger(String paramString1, String paramString2)
  {
    if (ok) {
      logger = Logger.getLogger(paramString1);
    } else {
      logger = null;
    }
    className = paramString2;
  }
  
  public final boolean traceOn()
  {
    return finerOn();
  }
  
  public final boolean debugOn()
  {
    return finestOn();
  }
  
  public final boolean warningOn()
  {
    return (ok) && (logger.isLoggable(Level.WARNING));
  }
  
  public final boolean infoOn()
  {
    return (ok) && (logger.isLoggable(Level.INFO));
  }
  
  public final boolean configOn()
  {
    return (ok) && (logger.isLoggable(Level.CONFIG));
  }
  
  public final boolean fineOn()
  {
    return (ok) && (logger.isLoggable(Level.FINE));
  }
  
  public final boolean finerOn()
  {
    return (ok) && (logger.isLoggable(Level.FINER));
  }
  
  public final boolean finestOn()
  {
    return (ok) && (logger.isLoggable(Level.FINEST));
  }
  
  public final void debug(String paramString1, String paramString2)
  {
    finest(paramString1, paramString2);
  }
  
  public final void debug(String paramString, Throwable paramThrowable)
  {
    finest(paramString, paramThrowable);
  }
  
  public final void debug(String paramString1, String paramString2, Throwable paramThrowable)
  {
    finest(paramString1, paramString2, paramThrowable);
  }
  
  public final void trace(String paramString1, String paramString2)
  {
    finer(paramString1, paramString2);
  }
  
  public final void trace(String paramString, Throwable paramThrowable)
  {
    finer(paramString, paramThrowable);
  }
  
  public final void trace(String paramString1, String paramString2, Throwable paramThrowable)
  {
    finer(paramString1, paramString2, paramThrowable);
  }
  
  public final void error(String paramString1, String paramString2)
  {
    severe(paramString1, paramString2);
  }
  
  public final void error(String paramString, Throwable paramThrowable)
  {
    severe(paramString, paramThrowable);
  }
  
  public final void error(String paramString1, String paramString2, Throwable paramThrowable)
  {
    severe(paramString1, paramString2, paramThrowable);
  }
  
  public final void finest(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.FINEST, className, paramString1, paramString2);
    }
  }
  
  public final void finest(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.FINEST, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void finest(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.FINEST, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public final void finer(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.FINER, className, paramString1, paramString2);
    }
  }
  
  public final void finer(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.FINER, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void finer(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.FINER, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public final void fine(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.FINE, className, paramString1, paramString2);
    }
  }
  
  public final void fine(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.FINE, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void fine(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.FINE, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public final void config(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.CONFIG, className, paramString1, paramString2);
    }
  }
  
  public final void config(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.CONFIG, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void config(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.CONFIG, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public final void info(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.INFO, className, paramString1, paramString2);
    }
  }
  
  public final void info(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.INFO, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void info(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.INFO, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public final void warning(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.WARNING, className, paramString1, paramString2);
    }
  }
  
  public final void warning(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.WARNING, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void warning(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.WARNING, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  public final void severe(String paramString1, String paramString2)
  {
    if (ok) {
      logger.logp(Level.SEVERE, className, paramString1, paramString2);
    }
  }
  
  public final void severe(String paramString, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.SEVERE, className, paramString, paramThrowable.toString(), paramThrowable);
    }
  }
  
  public final void severe(String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (ok) {
      logger.logp(Level.SEVERE, className, paramString1, paramString2, paramThrowable);
    }
  }
  
  static
  {
    boolean bool = false;
    try
    {
      Class localClass = Logger.class;
      bool = true;
    }
    catch (Error localError) {}
    ok = bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\util\ClassLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.rmi.runtime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import sun.security.action.GetPropertyAction;

public abstract class Log
{
  public static final Level BRIEF = Level.FINE;
  public static final Level VERBOSE = Level.FINER;
  private static final LogFactory logFactory = bool ? new LogStreamLogFactory() : new LoggerLogFactory();
  
  public Log() {}
  
  public abstract boolean isLoggable(Level paramLevel);
  
  public abstract void log(Level paramLevel, String paramString);
  
  public abstract void log(Level paramLevel, String paramString, Throwable paramThrowable);
  
  public abstract void setOutputStream(OutputStream paramOutputStream);
  
  public abstract PrintStream getPrintStream();
  
  public static Log getLog(String paramString1, String paramString2, int paramInt)
  {
    Level localLevel;
    if (paramInt < 0) {
      localLevel = null;
    } else if (paramInt == 0) {
      localLevel = Level.OFF;
    } else if ((paramInt > 0) && (paramInt <= 10)) {
      localLevel = BRIEF;
    } else if ((paramInt > 10) && (paramInt <= 20)) {
      localLevel = VERBOSE;
    } else {
      localLevel = Level.FINEST;
    }
    return logFactory.createLog(paramString1, paramString2, localLevel);
  }
  
  public static Log getLog(String paramString1, String paramString2, boolean paramBoolean)
  {
    Level localLevel = paramBoolean ? VERBOSE : null;
    return logFactory.createLog(paramString1, paramString2, localLevel);
  }
  
  private static String[] getSource()
  {
    StackTraceElement[] arrayOfStackTraceElement = new Exception().getStackTrace();
    return new String[] { arrayOfStackTraceElement[3].getClassName(), arrayOfStackTraceElement[3].getMethodName() };
  }
  
  static
  {
    boolean bool = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.useOld"))).booleanValue();
  }
  
  private static class InternalStreamHandler
    extends StreamHandler
  {
    InternalStreamHandler(OutputStream paramOutputStream)
    {
      super(new SimpleFormatter());
    }
    
    public void publish(LogRecord paramLogRecord)
    {
      super.publish(paramLogRecord);
      flush();
    }
    
    public void close()
    {
      flush();
    }
  }
  
  private static abstract interface LogFactory
  {
    public abstract Log createLog(String paramString1, String paramString2, Level paramLevel);
  }
  
  private static class LogStreamLog
    extends Log
  {
    private final LogStream stream;
    private int levelValue = Level.OFF.intValue();
    
    private LogStreamLog(LogStream paramLogStream, Level paramLevel)
    {
      if ((paramLogStream != null) && (paramLevel != null)) {
        levelValue = paramLevel.intValue();
      }
      stream = paramLogStream;
    }
    
    public synchronized boolean isLoggable(Level paramLevel)
    {
      return paramLevel.intValue() >= levelValue;
    }
    
    public void log(Level paramLevel, String paramString)
    {
      if (isLoggable(paramLevel))
      {
        String[] arrayOfString = Log.access$200();
        stream.println(unqualifiedName(arrayOfString[0]) + "." + arrayOfString[1] + ": " + paramString);
      }
    }
    
    public void log(Level paramLevel, String paramString, Throwable paramThrowable)
    {
      if (isLoggable(paramLevel)) {
        synchronized (stream)
        {
          String[] arrayOfString = Log.access$200();
          stream.println(unqualifiedName(arrayOfString[0]) + "." + arrayOfString[1] + ": " + paramString);
          paramThrowable.printStackTrace(stream);
        }
      }
    }
    
    public PrintStream getPrintStream()
    {
      return stream;
    }
    
    public synchronized void setOutputStream(OutputStream paramOutputStream)
    {
      if (paramOutputStream != null)
      {
        if (VERBOSE.intValue() < levelValue) {
          levelValue = VERBOSE.intValue();
        }
        stream.setOutputStream(paramOutputStream);
      }
      else
      {
        levelValue = Level.OFF.intValue();
      }
    }
    
    private static String unqualifiedName(String paramString)
    {
      int i = paramString.lastIndexOf(".");
      if (i >= 0) {
        paramString = paramString.substring(i + 1);
      }
      paramString = paramString.replace('$', '.');
      return paramString;
    }
  }
  
  private static class LogStreamLogFactory
    implements Log.LogFactory
  {
    LogStreamLogFactory() {}
    
    public Log createLog(String paramString1, String paramString2, Level paramLevel)
    {
      LogStream localLogStream = null;
      if (paramString2 != null) {
        localLogStream = LogStream.log(paramString2);
      }
      return new Log.LogStreamLog(localLogStream, paramLevel, null);
    }
  }
  
  private static class LoggerLog
    extends Log
  {
    private static final Handler alternateConsole = (Handler)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Handler run()
      {
        Log.InternalStreamHandler localInternalStreamHandler = new Log.InternalStreamHandler(System.err);
        localInternalStreamHandler.setLevel(Level.ALL);
        return localInternalStreamHandler;
      }
    });
    private Log.InternalStreamHandler copyHandler = null;
    private final Logger logger;
    private Log.LoggerPrintStream loggerSandwich;
    
    private LoggerLog(final Logger paramLogger, final Level paramLevel)
    {
      logger = paramLogger;
      if (paramLevel != null) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            if (!paramLogger.isLoggable(paramLevel)) {
              paramLogger.setLevel(paramLevel);
            }
            paramLogger.addHandler(Log.LoggerLog.alternateConsole);
            return null;
          }
        });
      }
    }
    
    public boolean isLoggable(Level paramLevel)
    {
      return logger.isLoggable(paramLevel);
    }
    
    public void log(Level paramLevel, String paramString)
    {
      if (isLoggable(paramLevel))
      {
        String[] arrayOfString = Log.access$200();
        logger.logp(paramLevel, arrayOfString[0], arrayOfString[1], Thread.currentThread().getName() + ": " + paramString);
      }
    }
    
    public void log(Level paramLevel, String paramString, Throwable paramThrowable)
    {
      if (isLoggable(paramLevel))
      {
        String[] arrayOfString = Log.access$200();
        logger.logp(paramLevel, arrayOfString[0], arrayOfString[1], Thread.currentThread().getName() + ": " + paramString, paramThrowable);
      }
    }
    
    public synchronized void setOutputStream(OutputStream paramOutputStream)
    {
      if (paramOutputStream != null)
      {
        if (!logger.isLoggable(VERBOSE)) {
          logger.setLevel(VERBOSE);
        }
        copyHandler = new Log.InternalStreamHandler(paramOutputStream);
        copyHandler.setLevel(Log.VERBOSE);
        logger.addHandler(copyHandler);
      }
      else
      {
        if (copyHandler != null) {
          logger.removeHandler(copyHandler);
        }
        copyHandler = null;
      }
    }
    
    public synchronized PrintStream getPrintStream()
    {
      if (loggerSandwich == null) {
        loggerSandwich = new Log.LoggerPrintStream(logger, null);
      }
      return loggerSandwich;
    }
  }
  
  private static class LoggerLogFactory
    implements Log.LogFactory
  {
    LoggerLogFactory() {}
    
    public Log createLog(String paramString1, String paramString2, Level paramLevel)
    {
      Logger localLogger = Logger.getLogger(paramString1);
      return new Log.LoggerLog(localLogger, paramLevel, null);
    }
  }
  
  private static class LoggerPrintStream
    extends PrintStream
  {
    private final Logger logger;
    private int last = -1;
    private final ByteArrayOutputStream bufOut = (ByteArrayOutputStream)out;
    
    private LoggerPrintStream(Logger paramLogger)
    {
      super();
      logger = paramLogger;
    }
    
    /* Error */
    public void write(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 80	sun/rmi/runtime/Log$LoggerPrintStream:last	I
      //   4: bipush 13
      //   6: if_icmpne +15 -> 21
      //   9: iload_1
      //   10: bipush 10
      //   12: if_icmpne +9 -> 21
      //   15: aload_0
      //   16: iconst_m1
      //   17: putfield 80	sun/rmi/runtime/Log$LoggerPrintStream:last	I
      //   20: return
      //   21: iload_1
      //   22: bipush 10
      //   24: if_icmpeq +9 -> 33
      //   27: iload_1
      //   28: bipush 13
      //   30: if_icmpne +76 -> 106
      //   33: new 44	java/lang/StringBuilder
      //   36: dup
      //   37: invokespecial 89	java/lang/StringBuilder:<init>	()V
      //   40: invokestatic 93	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   43: invokevirtual 92	java/lang/Thread:getName	()Ljava/lang/String;
      //   46: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   49: ldc 1
      //   51: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   54: aload_0
      //   55: getfield 81	sun/rmi/runtime/Log$LoggerPrintStream:bufOut	Ljava/io/ByteArrayOutputStream;
      //   58: invokevirtual 85	java/io/ByteArrayOutputStream:toString	()Ljava/lang/String;
      //   61: invokevirtual 91	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   64: invokevirtual 90	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   67: astore_2
      //   68: aload_0
      //   69: getfield 82	sun/rmi/runtime/Log$LoggerPrintStream:logger	Ljava/util/logging/Logger;
      //   72: getstatic 79	java/util/logging/Level:INFO	Ljava/util/logging/Level;
      //   75: ldc 2
      //   77: ldc 4
      //   79: aload_2
      //   80: invokevirtual 94	java/util/logging/Logger:logp	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      //   83: aload_0
      //   84: getfield 81	sun/rmi/runtime/Log$LoggerPrintStream:bufOut	Ljava/io/ByteArrayOutputStream;
      //   87: invokevirtual 84	java/io/ByteArrayOutputStream:reset	()V
      //   90: goto +13 -> 103
      //   93: astore_3
      //   94: aload_0
      //   95: getfield 81	sun/rmi/runtime/Log$LoggerPrintStream:bufOut	Ljava/io/ByteArrayOutputStream;
      //   98: invokevirtual 84	java/io/ByteArrayOutputStream:reset	()V
      //   101: aload_3
      //   102: athrow
      //   103: goto +8 -> 111
      //   106: aload_0
      //   107: iload_1
      //   108: invokespecial 86	java/io/PrintStream:write	(I)V
      //   111: aload_0
      //   112: iload_1
      //   113: putfield 80	sun/rmi/runtime/Log$LoggerPrintStream:last	I
      //   116: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	117	0	this	LoggerPrintStream
      //   0	117	1	paramInt	int
      //   67	13	2	str	String
      //   93	9	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   33	83	93	finally
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (paramInt2 < 0) {
        throw new ArrayIndexOutOfBoundsException(paramInt2);
      }
      for (int i = 0; i < paramInt2; i++) {
        write(paramArrayOfByte[(paramInt1 + i)]);
      }
    }
    
    public String toString()
    {
      return "RMI";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\runtime\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
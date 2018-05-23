package java.util.logging;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

public abstract class Handler
{
  private static final int offValue = Level.OFF.intValue();
  private final LogManager manager = LogManager.getLogManager();
  private volatile Filter filter;
  private volatile Formatter formatter;
  private volatile Level logLevel = Level.ALL;
  private volatile ErrorManager errorManager = new ErrorManager();
  private volatile String encoding;
  boolean sealed = true;
  
  protected Handler() {}
  
  public abstract void publish(LogRecord paramLogRecord);
  
  public abstract void flush();
  
  public abstract void close()
    throws SecurityException;
  
  public synchronized void setFormatter(Formatter paramFormatter)
    throws SecurityException
  {
    checkPermission();
    paramFormatter.getClass();
    formatter = paramFormatter;
  }
  
  public Formatter getFormatter()
  {
    return formatter;
  }
  
  public synchronized void setEncoding(String paramString)
    throws SecurityException, UnsupportedEncodingException
  {
    checkPermission();
    if (paramString != null) {
      try
      {
        if (!Charset.isSupported(paramString)) {
          throw new UnsupportedEncodingException(paramString);
        }
      }
      catch (IllegalCharsetNameException localIllegalCharsetNameException)
      {
        throw new UnsupportedEncodingException(paramString);
      }
    }
    encoding = paramString;
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public synchronized void setFilter(Filter paramFilter)
    throws SecurityException
  {
    checkPermission();
    filter = paramFilter;
  }
  
  public Filter getFilter()
  {
    return filter;
  }
  
  public synchronized void setErrorManager(ErrorManager paramErrorManager)
  {
    checkPermission();
    if (paramErrorManager == null) {
      throw new NullPointerException();
    }
    errorManager = paramErrorManager;
  }
  
  public ErrorManager getErrorManager()
  {
    checkPermission();
    return errorManager;
  }
  
  protected void reportError(String paramString, Exception paramException, int paramInt)
  {
    try
    {
      errorManager.error(paramString, paramException, paramInt);
    }
    catch (Exception localException)
    {
      System.err.println("Handler.reportError caught:");
      localException.printStackTrace();
    }
  }
  
  public synchronized void setLevel(Level paramLevel)
    throws SecurityException
  {
    if (paramLevel == null) {
      throw new NullPointerException();
    }
    checkPermission();
    logLevel = paramLevel;
  }
  
  public Level getLevel()
  {
    return logLevel;
  }
  
  public boolean isLoggable(LogRecord paramLogRecord)
  {
    int i = getLevel().intValue();
    if ((paramLogRecord.getLevel().intValue() < i) || (i == offValue)) {
      return false;
    }
    Filter localFilter = getFilter();
    if (localFilter == null) {
      return true;
    }
    return localFilter.isLoggable(paramLogRecord);
  }
  
  void checkPermission()
    throws SecurityException
  {
    if (sealed) {
      manager.checkPermission();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util.logging;

import java.util.List;
import sun.util.logging.LoggingProxy;

class LoggingProxyImpl
  implements LoggingProxy
{
  static final LoggingProxy INSTANCE = new LoggingProxyImpl();
  
  private LoggingProxyImpl() {}
  
  public Object getLogger(String paramString)
  {
    return Logger.getPlatformLogger(paramString);
  }
  
  public Object getLevel(Object paramObject)
  {
    return ((Logger)paramObject).getLevel();
  }
  
  public void setLevel(Object paramObject1, Object paramObject2)
  {
    ((Logger)paramObject1).setLevel((Level)paramObject2);
  }
  
  public boolean isLoggable(Object paramObject1, Object paramObject2)
  {
    return ((Logger)paramObject1).isLoggable((Level)paramObject2);
  }
  
  public void log(Object paramObject1, Object paramObject2, String paramString)
  {
    ((Logger)paramObject1).log((Level)paramObject2, paramString);
  }
  
  public void log(Object paramObject1, Object paramObject2, String paramString, Throwable paramThrowable)
  {
    ((Logger)paramObject1).log((Level)paramObject2, paramString, paramThrowable);
  }
  
  public void log(Object paramObject1, Object paramObject2, String paramString, Object... paramVarArgs)
  {
    ((Logger)paramObject1).log((Level)paramObject2, paramString, paramVarArgs);
  }
  
  public List<String> getLoggerNames()
  {
    return LogManager.getLoggingMXBean().getLoggerNames();
  }
  
  public String getLoggerLevel(String paramString)
  {
    return LogManager.getLoggingMXBean().getLoggerLevel(paramString);
  }
  
  public void setLoggerLevel(String paramString1, String paramString2)
  {
    LogManager.getLoggingMXBean().setLoggerLevel(paramString1, paramString2);
  }
  
  public String getParentLoggerName(String paramString)
  {
    return LogManager.getLoggingMXBean().getParentLoggerName(paramString);
  }
  
  public Object parseLevel(String paramString)
  {
    Level localLevel = Level.findLevel(paramString);
    if (localLevel == null) {
      throw new IllegalArgumentException("Unknown level \"" + paramString + "\"");
    }
    return localLevel;
  }
  
  public String getLevelName(Object paramObject)
  {
    return ((Level)paramObject).getLevelName();
  }
  
  public int getLevelValue(Object paramObject)
  {
    return ((Level)paramObject).intValue();
  }
  
  public String getProperty(String paramString)
  {
    return LogManager.getLogManager().getProperty(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\logging\LoggingProxyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import sun.security.action.GetPropertyAction;

public class PerformanceLogger
{
  private static final int START_INDEX = 0;
  private static final int LAST_RESERVED = 0;
  private static boolean perfLoggingOn = false;
  private static boolean useNanoTime = false;
  private static Vector<TimeData> times;
  private static String logFileName = null;
  private static Writer logWriter = null;
  private static long baseTime;
  
  public PerformanceLogger() {}
  
  public static boolean loggingEnabled()
  {
    return perfLoggingOn;
  }
  
  private static long getCurrentTime()
  {
    if (useNanoTime) {
      return System.nanoTime();
    }
    return System.currentTimeMillis();
  }
  
  public static void setStartTime(String paramString)
  {
    if (loggingEnabled())
    {
      long l = getCurrentTime();
      setStartTime(paramString, l);
    }
  }
  
  public static void setBaseTime(long paramLong)
  {
    if (loggingEnabled()) {
      baseTime = paramLong;
    }
  }
  
  public static void setStartTime(String paramString, long paramLong)
  {
    if (loggingEnabled()) {
      times.set(0, new TimeData(paramString, paramLong));
    }
  }
  
  public static long getStartTime()
  {
    if (loggingEnabled()) {
      return ((TimeData)times.get(0)).getTime();
    }
    return 0L;
  }
  
  public static int setTime(String paramString)
  {
    if (loggingEnabled())
    {
      long l = getCurrentTime();
      return setTime(paramString, l);
    }
    return 0;
  }
  
  public static int setTime(String paramString, long paramLong)
  {
    if (loggingEnabled()) {
      synchronized (times)
      {
        times.add(new TimeData(paramString, paramLong));
        return times.size() - 1;
      }
    }
    return 0;
  }
  
  public static long getTimeAtIndex(int paramInt)
  {
    if (loggingEnabled()) {
      return ((TimeData)times.get(paramInt)).getTime();
    }
    return 0L;
  }
  
  public static String getMessageAtIndex(int paramInt)
  {
    if (loggingEnabled()) {
      return ((TimeData)times.get(paramInt)).getMessage();
    }
    return null;
  }
  
  public static void outputLog(Writer paramWriter)
  {
    if (loggingEnabled()) {
      try
      {
        synchronized (times)
        {
          for (int i = 0; i < times.size(); i++)
          {
            TimeData localTimeData = (TimeData)times.get(i);
            if (localTimeData != null) {
              paramWriter.write(i + " " + localTimeData.getMessage() + ": " + (localTimeData.getTime() - baseTime) + "\n");
            }
          }
        }
        paramWriter.flush();
      }
      catch (Exception localException)
      {
        System.out.println(localException + ": Writing performance log to " + paramWriter);
      }
    }
  }
  
  public static void outputLog()
  {
    outputLog(logWriter);
  }
  
  static
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.perflog"));
    if (str1 != null)
    {
      perfLoggingOn = true;
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.perflog.nano"));
      if (str2 != null) {
        useNanoTime = true;
      }
      if (str1.regionMatches(true, 0, "file:", 0, 5)) {
        logFileName = str1.substring(5);
      }
      if ((logFileName != null) && (logWriter == null)) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Void run()
          {
            try
            {
              File localFile = new File(PerformanceLogger.logFileName);
              localFile.createNewFile();
              PerformanceLogger.access$102(new FileWriter(localFile));
            }
            catch (Exception localException)
            {
              System.out.println(localException + ": Creating logfile " + PerformanceLogger.logFileName + ".  Log to console");
            }
            return null;
          }
        });
      }
      if (logWriter == null) {
        logWriter = new OutputStreamWriter(System.out);
      }
    }
    times = new Vector(10);
    for (int i = 0; i <= 0; i++) {
      times.add(new TimeData("Time " + i + " not set", 0L));
    }
  }
  
  static class TimeData
  {
    String message;
    long time;
    
    TimeData(String paramString, long paramLong)
    {
      message = paramString;
      time = paramLong;
    }
    
    String getMessage()
    {
      return message;
    }
    
    long getTime()
    {
      return time;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\PerformanceLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
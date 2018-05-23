package sun.util.calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.security.action.GetPropertyAction;

public abstract class CalendarSystem
{
  private static volatile boolean initialized = false;
  private static ConcurrentMap<String, String> names;
  private static ConcurrentMap<String, CalendarSystem> calendars;
  private static final String PACKAGE_NAME = "sun.util.calendar.";
  private static final String[] namePairs = { "gregorian", "Gregorian", "japanese", "LocalGregorianCalendar", "julian", "JulianCalendar" };
  private static final Gregorian GREGORIAN_INSTANCE = new Gregorian();
  
  public CalendarSystem() {}
  
  private static void initNames()
  {
    ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < namePairs.length; i += 2)
    {
      localStringBuilder.setLength(0);
      String str = "sun.util.calendar." + namePairs[(i + 1)];
      localConcurrentHashMap.put(namePairs[i], str);
    }
    synchronized (CalendarSystem.class)
    {
      if (!initialized)
      {
        names = localConcurrentHashMap;
        calendars = new ConcurrentHashMap();
        initialized = true;
      }
    }
  }
  
  public static Gregorian getGregorianCalendar()
  {
    return GREGORIAN_INSTANCE;
  }
  
  public static CalendarSystem forName(String paramString)
  {
    if ("gregorian".equals(paramString)) {
      return GREGORIAN_INSTANCE;
    }
    if (!initialized) {
      initNames();
    }
    Object localObject = (CalendarSystem)calendars.get(paramString);
    if (localObject != null) {
      return (CalendarSystem)localObject;
    }
    String str = (String)names.get(paramString);
    if (str == null) {
      return null;
    }
    if (str.endsWith("LocalGregorianCalendar")) {
      localObject = LocalGregorianCalendar.getLocalGregorianCalendar(paramString);
    } else {
      try
      {
        Class localClass = Class.forName(str);
        localObject = (CalendarSystem)localClass.newInstance();
      }
      catch (Exception localException)
      {
        throw new InternalError(localException);
      }
    }
    if (localObject == null) {
      return null;
    }
    CalendarSystem localCalendarSystem = (CalendarSystem)calendars.putIfAbsent(paramString, localObject);
    return (CalendarSystem)(localCalendarSystem == null ? localObject : localCalendarSystem);
  }
  
  public static Properties getCalendarProperties()
    throws IOException
  {
    Properties localProperties = null;
    try
    {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
      localObject = str + File.separator + "lib" + File.separator + "calendars.properties";
      localProperties = (Properties)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Properties run()
          throws IOException
        {
          Properties localProperties = new Properties();
          FileInputStream localFileInputStream = new FileInputStream(val$fname);
          Object localObject1 = null;
          try
          {
            localProperties.load(localFileInputStream);
          }
          catch (Throwable localThrowable2)
          {
            localObject1 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localFileInputStream != null) {
              if (localObject1 != null) {
                try
                {
                  localFileInputStream.close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject1).addSuppressed(localThrowable3);
                }
              } else {
                localFileInputStream.close();
              }
            }
          }
          return localProperties;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Object localObject = localPrivilegedActionException.getCause();
      if ((localObject instanceof IOException)) {
        throw ((IOException)localObject);
      }
      if ((localObject instanceof IllegalArgumentException)) {
        throw ((IllegalArgumentException)localObject);
      }
      throw new InternalError((Throwable)localObject);
    }
    return localProperties;
  }
  
  public abstract String getName();
  
  public abstract CalendarDate getCalendarDate();
  
  public abstract CalendarDate getCalendarDate(long paramLong);
  
  public abstract CalendarDate getCalendarDate(long paramLong, CalendarDate paramCalendarDate);
  
  public abstract CalendarDate getCalendarDate(long paramLong, TimeZone paramTimeZone);
  
  public abstract CalendarDate newCalendarDate();
  
  public abstract CalendarDate newCalendarDate(TimeZone paramTimeZone);
  
  public abstract long getTime(CalendarDate paramCalendarDate);
  
  public abstract int getYearLength(CalendarDate paramCalendarDate);
  
  public abstract int getYearLengthInMonths(CalendarDate paramCalendarDate);
  
  public abstract int getMonthLength(CalendarDate paramCalendarDate);
  
  public abstract int getWeekLength();
  
  public abstract Era getEra(String paramString);
  
  public abstract Era[] getEras();
  
  public abstract void setEra(CalendarDate paramCalendarDate, String paramString);
  
  public abstract CalendarDate getNthDayOfWeek(int paramInt1, int paramInt2, CalendarDate paramCalendarDate);
  
  public abstract CalendarDate setTimeOfDay(CalendarDate paramCalendarDate, int paramInt);
  
  public abstract boolean validate(CalendarDate paramCalendarDate);
  
  public abstract boolean normalize(CalendarDate paramCalendarDate);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\CalendarSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
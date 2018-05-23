package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public class Timer
  extends NotificationBroadcasterSupport
  implements TimerMBean, MBeanRegistration
{
  public static final long ONE_SECOND = 1000L;
  public static final long ONE_MINUTE = 60000L;
  public static final long ONE_HOUR = 3600000L;
  public static final long ONE_DAY = 86400000L;
  public static final long ONE_WEEK = 604800000L;
  private final Map<Integer, Object[]> timerTable = new HashMap();
  private boolean sendPastNotifications = false;
  private transient boolean isActive = false;
  private transient long sequenceNumber = 0L;
  private static final int TIMER_NOTIF_INDEX = 0;
  private static final int TIMER_DATE_INDEX = 1;
  private static final int TIMER_PERIOD_INDEX = 2;
  private static final int TIMER_NB_OCCUR_INDEX = 3;
  private static final int ALARM_CLOCK_INDEX = 4;
  private static final int FIXED_RATE_INDEX = 5;
  private volatile int counterID = 0;
  private java.util.Timer timer;
  
  public Timer() {}
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister()
    throws Exception
  {
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "preDeregister", "stop the timer");
    stop();
  }
  
  public void postDeregister() {}
  
  public synchronized MBeanNotificationInfo[] getNotificationInfo()
  {
    TreeSet localTreeSet = new TreeSet();
    Object localObject = timerTable.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Object[] arrayOfObject = (Object[])((Iterator)localObject).next();
      TimerNotification localTimerNotification = (TimerNotification)arrayOfObject[0];
      localTreeSet.add(localTimerNotification.getType());
    }
    localObject = (String[])localTreeSet.toArray(new String[0]);
    return new MBeanNotificationInfo[] { new MBeanNotificationInfo((String[])localObject, TimerNotification.class.getName(), "Notification sent by Timer MBean") };
  }
  
  public synchronized void start()
  {
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "starting the timer");
    if (!isActive)
    {
      timer = new java.util.Timer();
      Date localDate2 = new Date();
      sendPastNotifications(localDate2, sendPastNotifications);
      Iterator localIterator = timerTable.values().iterator();
      while (localIterator.hasNext())
      {
        Object[] arrayOfObject = (Object[])localIterator.next();
        Date localDate1 = (Date)arrayOfObject[1];
        boolean bool = ((Boolean)arrayOfObject[5]).booleanValue();
        TimerAlarmClock localTimerAlarmClock;
        if (bool)
        {
          localTimerAlarmClock = new TimerAlarmClock(this, localDate1);
          arrayOfObject[4] = localTimerAlarmClock;
          timer.schedule(localTimerAlarmClock, next);
        }
        else
        {
          localTimerAlarmClock = new TimerAlarmClock(this, localDate1.getTime() - localDate2.getTime());
          arrayOfObject[4] = localTimerAlarmClock;
          timer.schedule(localTimerAlarmClock, timeout);
        }
      }
      isActive = true;
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "timer started");
    }
    else
    {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "the timer is already activated");
    }
  }
  
  public synchronized void stop()
  {
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "stopping the timer");
    if (isActive == true)
    {
      Iterator localIterator = timerTable.values().iterator();
      while (localIterator.hasNext())
      {
        Object[] arrayOfObject = (Object[])localIterator.next();
        TimerAlarmClock localTimerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
        if (localTimerAlarmClock != null) {
          localTimerAlarmClock.cancel();
        }
      }
      timer.cancel();
      isActive = false;
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "timer stopped");
    }
    else
    {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "the timer is already deactivated");
    }
  }
  
  public synchronized Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong1, long paramLong2, boolean paramBoolean)
    throws IllegalArgumentException
  {
    if (paramDate == null) {
      throw new IllegalArgumentException("Timer notification date cannot be null.");
    }
    if ((paramLong1 < 0L) || (paramLong2 < 0L)) {
      throw new IllegalArgumentException("Negative values for the periodicity");
    }
    Date localDate1 = new Date();
    if (localDate1.after(paramDate))
    {
      paramDate.setTime(localDate1.getTime());
      if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "update timer notification to add with:\n\tNotification date = " + paramDate);
      }
    }
    Integer localInteger = Integer.valueOf(++counterID);
    TimerNotification localTimerNotification = new TimerNotification(paramString1, this, 0L, 0L, paramString2, localInteger);
    localTimerNotification.setUserData(paramObject);
    Object[] arrayOfObject = new Object[6];
    TimerAlarmClock localTimerAlarmClock;
    if (paramBoolean) {
      localTimerAlarmClock = new TimerAlarmClock(this, paramDate);
    } else {
      localTimerAlarmClock = new TimerAlarmClock(this, paramDate.getTime() - localDate1.getTime());
    }
    Date localDate2 = new Date(paramDate.getTime());
    arrayOfObject[0] = localTimerNotification;
    arrayOfObject[1] = localDate2;
    arrayOfObject[2] = Long.valueOf(paramLong1);
    arrayOfObject[3] = Long.valueOf(paramLong2);
    arrayOfObject[4] = localTimerAlarmClock;
    arrayOfObject[5] = Boolean.valueOf(paramBoolean);
    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("adding timer notification:\n\t").append("Notification source = ").append(localTimerNotification.getSource()).append("\n\tNotification type = ").append(localTimerNotification.getType()).append("\n\tNotification ID = ").append(localInteger).append("\n\tNotification date = ").append(localDate2).append("\n\tNotification period = ").append(paramLong1).append("\n\tNotification nb of occurrences = ").append(paramLong2).append("\n\tNotification executes at fixed rate = ").append(paramBoolean);
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", localStringBuilder.toString());
    }
    timerTable.put(localInteger, arrayOfObject);
    if (isActive == true) {
      if (paramBoolean) {
        timer.schedule(localTimerAlarmClock, next);
      } else {
        timer.schedule(localTimerAlarmClock, timeout);
      }
    }
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "timer notification added");
    return localInteger;
  }
  
  public synchronized Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong1, long paramLong2)
    throws IllegalArgumentException
  {
    return addNotification(paramString1, paramString2, paramObject, paramDate, paramLong1, paramLong2, false);
  }
  
  public synchronized Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong)
    throws IllegalArgumentException
  {
    return addNotification(paramString1, paramString2, paramObject, paramDate, paramLong, 0L);
  }
  
  public synchronized Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate)
    throws IllegalArgumentException
  {
    return addNotification(paramString1, paramString2, paramObject, paramDate, 0L, 0L);
  }
  
  public synchronized void removeNotification(Integer paramInteger)
    throws InstanceNotFoundException
  {
    if (!timerTable.containsKey(paramInteger)) {
      throw new InstanceNotFoundException("Timer notification to remove not in the list of notifications");
    }
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    TimerAlarmClock localTimerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
    if (localTimerAlarmClock != null) {
      localTimerAlarmClock.cancel();
    }
    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("removing timer notification:").append("\n\tNotification source = ").append(((TimerNotification)arrayOfObject[0]).getSource()).append("\n\tNotification type = ").append(((TimerNotification)arrayOfObject[0]).getType()).append("\n\tNotification ID = ").append(((TimerNotification)arrayOfObject[0]).getNotificationID()).append("\n\tNotification date = ").append(arrayOfObject[1]).append("\n\tNotification period = ").append(arrayOfObject[2]).append("\n\tNotification nb of occurrences = ").append(arrayOfObject[3]).append("\n\tNotification executes at fixed rate = ").append(arrayOfObject[5]);
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", localStringBuilder.toString());
    }
    timerTable.remove(paramInteger);
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", "timer notification removed");
  }
  
  public synchronized void removeNotifications(String paramString)
    throws InstanceNotFoundException
  {
    Vector localVector = getNotificationIDs(paramString);
    if (localVector.isEmpty()) {
      throw new InstanceNotFoundException("Timer notifications to remove not in the list of notifications");
    }
    Iterator localIterator = localVector.iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      removeNotification(localInteger);
    }
  }
  
  public synchronized void removeAllNotifications()
  {
    Iterator localIterator = timerTable.values().iterator();
    while (localIterator.hasNext())
    {
      Object[] arrayOfObject = (Object[])localIterator.next();
      TimerAlarmClock localTimerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
      localTimerAlarmClock.cancel();
    }
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "removing all timer notifications");
    timerTable.clear();
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "all timer notifications removed");
    counterID = 0;
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "timer notification counter ID reset");
  }
  
  public synchronized int getNbNotifications()
  {
    return timerTable.size();
  }
  
  public synchronized Vector<Integer> getAllNotificationIDs()
  {
    return new Vector(timerTable.keySet());
  }
  
  public synchronized Vector<Integer> getNotificationIDs(String paramString)
  {
    Vector localVector = new Vector();
    Iterator localIterator = timerTable.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object[] arrayOfObject = (Object[])localEntry.getValue();
      String str = ((TimerNotification)arrayOfObject[0]).getType();
      if (paramString == null ? str == null : paramString.equals(str)) {
        localVector.addElement(localEntry.getKey());
      }
    }
    return localVector;
  }
  
  public synchronized String getNotificationType(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null) {
      return ((TimerNotification)arrayOfObject[0]).getType();
    }
    return null;
  }
  
  public synchronized String getNotificationMessage(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null) {
      return ((TimerNotification)arrayOfObject[0]).getMessage();
    }
    return null;
  }
  
  public synchronized Object getNotificationUserData(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null) {
      return ((TimerNotification)arrayOfObject[0]).getUserData();
    }
    return null;
  }
  
  public synchronized Date getDate(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null)
    {
      Date localDate = (Date)arrayOfObject[1];
      return new Date(localDate.getTime());
    }
    return null;
  }
  
  public synchronized Long getPeriod(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null) {
      return (Long)arrayOfObject[2];
    }
    return null;
  }
  
  public synchronized Long getNbOccurences(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null) {
      return (Long)arrayOfObject[3];
    }
    return null;
  }
  
  public synchronized Boolean getFixedRate(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    if (arrayOfObject != null)
    {
      Boolean localBoolean = (Boolean)arrayOfObject[5];
      return Boolean.valueOf(localBoolean.booleanValue());
    }
    return null;
  }
  
  public boolean getSendPastNotifications()
  {
    return sendPastNotifications;
  }
  
  public void setSendPastNotifications(boolean paramBoolean)
  {
    sendPastNotifications = paramBoolean;
  }
  
  public boolean isActive()
  {
    return isActive;
  }
  
  public synchronized boolean isEmpty()
  {
    return timerTable.isEmpty();
  }
  
  private synchronized void sendPastNotifications(Date paramDate, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(timerTable.values());
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Object[] arrayOfObject = (Object[])localIterator.next();
      TimerNotification localTimerNotification = (TimerNotification)arrayOfObject[0];
      Integer localInteger = localTimerNotification.getNotificationID();
      Date localDate = (Date)arrayOfObject[1];
      while ((paramDate.after(localDate)) && (timerTable.containsKey(localInteger)))
      {
        if (paramBoolean == true)
        {
          if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER))
          {
            StringBuilder localStringBuilder = new StringBuilder().append("sending past timer notification:").append("\n\tNotification source = ").append(localTimerNotification.getSource()).append("\n\tNotification type = ").append(localTimerNotification.getType()).append("\n\tNotification ID = ").append(localTimerNotification.getNotificationID()).append("\n\tNotification date = ").append(localDate).append("\n\tNotification period = ").append(arrayOfObject[2]).append("\n\tNotification nb of occurrences = ").append(arrayOfObject[3]).append("\n\tNotification executes at fixed rate = ").append(arrayOfObject[5]);
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", localStringBuilder.toString());
          }
          sendNotification(localDate, localTimerNotification);
          JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", "past timer notification sent");
        }
        updateTimerTable(localTimerNotification.getNotificationID());
      }
    }
  }
  
  private synchronized void updateTimerTable(Integer paramInteger)
  {
    Object[] arrayOfObject = (Object[])timerTable.get(paramInteger);
    Date localDate = (Date)arrayOfObject[1];
    Long localLong1 = (Long)arrayOfObject[2];
    Long localLong2 = (Long)arrayOfObject[3];
    Boolean localBoolean = (Boolean)arrayOfObject[5];
    TimerAlarmClock localTimerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
    if (localLong1.longValue() != 0L)
    {
      if ((localLong2.longValue() == 0L) || (localLong2.longValue() > 1L))
      {
        localDate.setTime(localDate.getTime() + localLong1.longValue());
        arrayOfObject[3] = Long.valueOf(Math.max(0L, localLong2.longValue() - 1L));
        localLong2 = (Long)arrayOfObject[3];
        if (isActive == true) {
          if (localBoolean.booleanValue())
          {
            localTimerAlarmClock = new TimerAlarmClock(this, localDate);
            arrayOfObject[4] = localTimerAlarmClock;
            timer.schedule(localTimerAlarmClock, next);
          }
          else
          {
            localTimerAlarmClock = new TimerAlarmClock(this, localLong1.longValue());
            arrayOfObject[4] = localTimerAlarmClock;
            timer.schedule(localTimerAlarmClock, timeout);
          }
        }
        if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER))
        {
          TimerNotification localTimerNotification = (TimerNotification)arrayOfObject[0];
          StringBuilder localStringBuilder = new StringBuilder().append("update timer notification with:").append("\n\tNotification source = ").append(localTimerNotification.getSource()).append("\n\tNotification type = ").append(localTimerNotification.getType()).append("\n\tNotification ID = ").append(paramInteger).append("\n\tNotification date = ").append(localDate).append("\n\tNotification period = ").append(localLong1).append("\n\tNotification nb of occurrences = ").append(localLong2).append("\n\tNotification executes at fixed rate = ").append(localBoolean);
          JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "updateTimerTable", localStringBuilder.toString());
        }
      }
      else
      {
        if (localTimerAlarmClock != null) {
          localTimerAlarmClock.cancel();
        }
        timerTable.remove(paramInteger);
      }
    }
    else
    {
      if (localTimerAlarmClock != null) {
        localTimerAlarmClock.cancel();
      }
      timerTable.remove(paramInteger);
    }
  }
  
  void notifyAlarmClock(TimerAlarmClockNotification paramTimerAlarmClockNotification)
  {
    TimerNotification localTimerNotification = null;
    Date localDate = null;
    TimerAlarmClock localTimerAlarmClock = (TimerAlarmClock)paramTimerAlarmClockNotification.getSource();
    synchronized (this)
    {
      Iterator localIterator = timerTable.values().iterator();
      while (localIterator.hasNext())
      {
        Object[] arrayOfObject = (Object[])localIterator.next();
        if (arrayOfObject[4] == localTimerAlarmClock)
        {
          localTimerNotification = (TimerNotification)arrayOfObject[0];
          localDate = (Date)arrayOfObject[1];
          break;
        }
      }
    }
    sendNotification(localDate, localTimerNotification);
    updateTimerTable(localTimerNotification.getNotificationID());
  }
  
  void sendNotification(Date paramDate, TimerNotification paramTimerNotification)
  {
    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("sending timer notification:").append("\n\tNotification source = ").append(paramTimerNotification.getSource()).append("\n\tNotification type = ").append(paramTimerNotification.getType()).append("\n\tNotification ID = ").append(paramTimerNotification.getNotificationID()).append("\n\tNotification date = ").append(paramDate);
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendNotification", localStringBuilder.toString());
    }
    long l;
    synchronized (this)
    {
      sequenceNumber += 1L;
      l = sequenceNumber;
    }
    synchronized (paramTimerNotification)
    {
      paramTimerNotification.setTimeStamp(paramDate.getTime());
      paramTimerNotification.setSequenceNumber(l);
      sendNotification((TimerNotification)paramTimerNotification.cloneTimerNotification());
    }
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendNotification", "timer notification sent");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\timer\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
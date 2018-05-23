package sun.management;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.Set;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public class ThreadInfoCompositeData
  extends LazyCompositeData
{
  private final ThreadInfo threadInfo;
  private final CompositeData cdata;
  private final boolean currentVersion;
  private static final String THREAD_ID = "threadId";
  private static final String THREAD_NAME = "threadName";
  private static final String THREAD_STATE = "threadState";
  private static final String BLOCKED_TIME = "blockedTime";
  private static final String BLOCKED_COUNT = "blockedCount";
  private static final String WAITED_TIME = "waitedTime";
  private static final String WAITED_COUNT = "waitedCount";
  private static final String LOCK_INFO = "lockInfo";
  private static final String LOCK_NAME = "lockName";
  private static final String LOCK_OWNER_ID = "lockOwnerId";
  private static final String LOCK_OWNER_NAME = "lockOwnerName";
  private static final String STACK_TRACE = "stackTrace";
  private static final String SUSPENDED = "suspended";
  private static final String IN_NATIVE = "inNative";
  private static final String LOCKED_MONITORS = "lockedMonitors";
  private static final String LOCKED_SYNCS = "lockedSynchronizers";
  private static final String[] threadInfoItemNames = { "threadId", "threadName", "threadState", "blockedTime", "blockedCount", "waitedTime", "waitedCount", "lockInfo", "lockName", "lockOwnerId", "lockOwnerName", "stackTrace", "suspended", "inNative", "lockedMonitors", "lockedSynchronizers" };
  private static final String[] threadInfoV6Attributes = { "lockInfo", "lockedMonitors", "lockedSynchronizers" };
  private static final CompositeType threadInfoCompositeType;
  private static final CompositeType threadInfoV5CompositeType;
  private static final CompositeType lockInfoCompositeType;
  private static final long serialVersionUID = 2464378539119753175L;
  
  private ThreadInfoCompositeData(ThreadInfo paramThreadInfo)
  {
    threadInfo = paramThreadInfo;
    currentVersion = true;
    cdata = null;
  }
  
  private ThreadInfoCompositeData(CompositeData paramCompositeData)
  {
    threadInfo = null;
    currentVersion = isCurrentVersion(paramCompositeData);
    cdata = paramCompositeData;
  }
  
  public ThreadInfo getThreadInfo()
  {
    return threadInfo;
  }
  
  public boolean isCurrentVersion()
  {
    return currentVersion;
  }
  
  public static ThreadInfoCompositeData getInstance(CompositeData paramCompositeData)
  {
    validateCompositeData(paramCompositeData);
    return new ThreadInfoCompositeData(paramCompositeData);
  }
  
  public static CompositeData toCompositeData(ThreadInfo paramThreadInfo)
  {
    ThreadInfoCompositeData localThreadInfoCompositeData = new ThreadInfoCompositeData(paramThreadInfo);
    return localThreadInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData()
  {
    StackTraceElement[] arrayOfStackTraceElement = threadInfo.getStackTrace();
    CompositeData[] arrayOfCompositeData1 = new CompositeData[arrayOfStackTraceElement.length];
    for (int i = 0; i < arrayOfStackTraceElement.length; i++)
    {
      localObject1 = arrayOfStackTraceElement[i];
      arrayOfCompositeData1[i] = StackTraceElementCompositeData.toCompositeData((StackTraceElement)localObject1);
    }
    CompositeData localCompositeData = LockInfoCompositeData.toCompositeData(threadInfo.getLockInfo());
    Object localObject1 = threadInfo.getLockedSynchronizers();
    CompositeData[] arrayOfCompositeData2 = new CompositeData[localObject1.length];
    for (int j = 0; j < localObject1.length; j++)
    {
      localObject2 = localObject1[j];
      arrayOfCompositeData2[j] = LockInfoCompositeData.toCompositeData((LockInfo)localObject2);
    }
    MonitorInfo[] arrayOfMonitorInfo = threadInfo.getLockedMonitors();
    Object localObject2 = new CompositeData[arrayOfMonitorInfo.length];
    for (int k = 0; k < arrayOfMonitorInfo.length; k++)
    {
      MonitorInfo localMonitorInfo = arrayOfMonitorInfo[k];
      localObject2[k] = MonitorInfoCompositeData.toCompositeData(localMonitorInfo);
    }
    Object[] arrayOfObject = { new Long(threadInfo.getThreadId()), threadInfo.getThreadName(), threadInfo.getThreadState().name(), new Long(threadInfo.getBlockedTime()), new Long(threadInfo.getBlockedCount()), new Long(threadInfo.getWaitedTime()), new Long(threadInfo.getWaitedCount()), localCompositeData, threadInfo.getLockName(), new Long(threadInfo.getLockOwnerId()), threadInfo.getLockOwnerName(), arrayOfCompositeData1, new Boolean(threadInfo.isSuspended()), new Boolean(threadInfo.isInNative()), localObject2, arrayOfCompositeData2 };
    try
    {
      return new CompositeDataSupport(threadInfoCompositeType, threadInfoItemNames, arrayOfObject);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw new AssertionError(localOpenDataException);
    }
  }
  
  private static boolean isV5Attribute(String paramString)
  {
    for (String str : threadInfoV6Attributes) {
      if (paramString.equals(str)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isCurrentVersion(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      throw new NullPointerException("Null CompositeData");
    }
    return isTypeMatched(threadInfoCompositeType, paramCompositeData.getCompositeType());
  }
  
  public long threadId()
  {
    return getLong(cdata, "threadId");
  }
  
  public String threadName()
  {
    String str = getString(cdata, "threadName");
    if (str == null) {
      throw new IllegalArgumentException("Invalid composite data: Attribute threadName has null value");
    }
    return str;
  }
  
  public Thread.State threadState()
  {
    return Thread.State.valueOf(getString(cdata, "threadState"));
  }
  
  public long blockedTime()
  {
    return getLong(cdata, "blockedTime");
  }
  
  public long blockedCount()
  {
    return getLong(cdata, "blockedCount");
  }
  
  public long waitedTime()
  {
    return getLong(cdata, "waitedTime");
  }
  
  public long waitedCount()
  {
    return getLong(cdata, "waitedCount");
  }
  
  public String lockName()
  {
    return getString(cdata, "lockName");
  }
  
  public long lockOwnerId()
  {
    return getLong(cdata, "lockOwnerId");
  }
  
  public String lockOwnerName()
  {
    return getString(cdata, "lockOwnerName");
  }
  
  public boolean suspended()
  {
    return getBoolean(cdata, "suspended");
  }
  
  public boolean inNative()
  {
    return getBoolean(cdata, "inNative");
  }
  
  public StackTraceElement[] stackTrace()
  {
    CompositeData[] arrayOfCompositeData = (CompositeData[])cdata.get("stackTrace");
    StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[arrayOfCompositeData.length];
    for (int i = 0; i < arrayOfCompositeData.length; i++)
    {
      CompositeData localCompositeData = arrayOfCompositeData[i];
      arrayOfStackTraceElement[i] = StackTraceElementCompositeData.from(localCompositeData);
    }
    return arrayOfStackTraceElement;
  }
  
  public LockInfo lockInfo()
  {
    CompositeData localCompositeData = (CompositeData)cdata.get("lockInfo");
    return LockInfo.from(localCompositeData);
  }
  
  public MonitorInfo[] lockedMonitors()
  {
    CompositeData[] arrayOfCompositeData = (CompositeData[])cdata.get("lockedMonitors");
    MonitorInfo[] arrayOfMonitorInfo = new MonitorInfo[arrayOfCompositeData.length];
    for (int i = 0; i < arrayOfCompositeData.length; i++)
    {
      CompositeData localCompositeData = arrayOfCompositeData[i];
      arrayOfMonitorInfo[i] = MonitorInfo.from(localCompositeData);
    }
    return arrayOfMonitorInfo;
  }
  
  public LockInfo[] lockedSynchronizers()
  {
    CompositeData[] arrayOfCompositeData = (CompositeData[])cdata.get("lockedSynchronizers");
    LockInfo[] arrayOfLockInfo = new LockInfo[arrayOfCompositeData.length];
    for (int i = 0; i < arrayOfCompositeData.length; i++)
    {
      CompositeData localCompositeData = arrayOfCompositeData[i];
      arrayOfLockInfo[i] = LockInfo.from(localCompositeData);
    }
    return arrayOfLockInfo;
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      throw new NullPointerException("Null CompositeData");
    }
    CompositeType localCompositeType = paramCompositeData.getCompositeType();
    int i = 1;
    if (!isTypeMatched(threadInfoCompositeType, localCompositeType))
    {
      i = 0;
      if (!isTypeMatched(threadInfoV5CompositeType, localCompositeType)) {
        throw new IllegalArgumentException("Unexpected composite type for ThreadInfo");
      }
    }
    CompositeData[] arrayOfCompositeData1 = (CompositeData[])paramCompositeData.get("stackTrace");
    if (arrayOfCompositeData1 == null) {
      throw new IllegalArgumentException("StackTraceElement[] is missing");
    }
    if (arrayOfCompositeData1.length > 0) {
      StackTraceElementCompositeData.validateCompositeData(arrayOfCompositeData1[0]);
    }
    if (i != 0)
    {
      CompositeData localCompositeData = (CompositeData)paramCompositeData.get("lockInfo");
      if ((localCompositeData != null) && (!isTypeMatched(lockInfoCompositeType, localCompositeData.getCompositeType()))) {
        throw new IllegalArgumentException("Unexpected composite type for \"lockInfo\" attribute.");
      }
      CompositeData[] arrayOfCompositeData2 = (CompositeData[])paramCompositeData.get("lockedMonitors");
      if (arrayOfCompositeData2 == null) {
        throw new IllegalArgumentException("MonitorInfo[] is null");
      }
      if (arrayOfCompositeData2.length > 0) {
        MonitorInfoCompositeData.validateCompositeData(arrayOfCompositeData2[0]);
      }
      CompositeData[] arrayOfCompositeData3 = (CompositeData[])paramCompositeData.get("lockedSynchronizers");
      if (arrayOfCompositeData3 == null) {
        throw new IllegalArgumentException("LockInfo[] is null");
      }
      if ((arrayOfCompositeData3.length > 0) && (!isTypeMatched(lockInfoCompositeType, arrayOfCompositeData3[0].getCompositeType()))) {
        throw new IllegalArgumentException("Unexpected composite type for \"lockedSynchronizers\" attribute.");
      }
    }
  }
  
  static
  {
    try
    {
      threadInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(ThreadInfo.class);
      String[] arrayOfString1 = (String[])threadInfoCompositeType.keySet().toArray(new String[0]);
      int i = threadInfoItemNames.length - threadInfoV6Attributes.length;
      localObject2 = new String[i];
      String[] arrayOfString2 = new String[i];
      OpenType[] arrayOfOpenType = new OpenType[i];
      int j = 0;
      for (String str : arrayOfString1) {
        if (isV5Attribute(str))
        {
          localObject2[j] = str;
          arrayOfString2[j] = threadInfoCompositeType.getDescription(str);
          arrayOfOpenType[j] = threadInfoCompositeType.getType(str);
          j++;
        }
      }
      threadInfoV5CompositeType = new CompositeType("java.lang.management.ThreadInfo", "J2SE 5.0 java.lang.management.ThreadInfo", (String[])localObject2, arrayOfString2, arrayOfOpenType);
    }
    catch (OpenDataException localOpenDataException)
    {
      throw new AssertionError(localOpenDataException);
    }
    Object localObject1 = new Object();
    LockInfo localLockInfo = new LockInfo(localObject1.getClass().getName(), System.identityHashCode(localObject1));
    Object localObject2 = LockInfoCompositeData.toCompositeData(localLockInfo);
    lockInfoCompositeType = ((CompositeData)localObject2).getCompositeType();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ThreadInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
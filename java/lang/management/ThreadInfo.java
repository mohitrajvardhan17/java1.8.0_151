package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.ManagementFactoryHelper;
import sun.management.ThreadInfoCompositeData;

public class ThreadInfo
{
  private String threadName;
  private long threadId;
  private long blockedTime;
  private long blockedCount;
  private long waitedTime;
  private long waitedCount;
  private LockInfo lock;
  private String lockName;
  private long lockOwnerId;
  private String lockOwnerName;
  private boolean inNative;
  private boolean suspended;
  private Thread.State threadState;
  private StackTraceElement[] stackTrace;
  private MonitorInfo[] lockedMonitors;
  private LockInfo[] lockedSynchronizers;
  private static MonitorInfo[] EMPTY_MONITORS = new MonitorInfo[0];
  private static LockInfo[] EMPTY_SYNCS = new LockInfo[0];
  private static final int MAX_FRAMES = 8;
  private static final StackTraceElement[] NO_STACK_TRACE = new StackTraceElement[0];
  
  private ThreadInfo(Thread paramThread1, int paramInt, Object paramObject, Thread paramThread2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    initialize(paramThread1, paramInt, paramObject, paramThread2, paramLong1, paramLong2, paramLong3, paramLong4, paramArrayOfStackTraceElement, EMPTY_MONITORS, EMPTY_SYNCS);
  }
  
  private ThreadInfo(Thread paramThread1, int paramInt, Object paramObject, Thread paramThread2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, StackTraceElement[] paramArrayOfStackTraceElement, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2)
  {
    int i = paramArrayOfObject1 == null ? 0 : paramArrayOfObject1.length;
    MonitorInfo[] arrayOfMonitorInfo;
    Object localObject1;
    if (i == 0)
    {
      arrayOfMonitorInfo = EMPTY_MONITORS;
    }
    else
    {
      arrayOfMonitorInfo = new MonitorInfo[i];
      for (j = 0; j < i; j++)
      {
        localObject1 = paramArrayOfObject1[j];
        String str1 = localObject1.getClass().getName();
        int m = System.identityHashCode(localObject1);
        int n = paramArrayOfInt[j];
        StackTraceElement localStackTraceElement = n >= 0 ? paramArrayOfStackTraceElement[n] : null;
        arrayOfMonitorInfo[j] = new MonitorInfo(str1, m, n, localStackTraceElement);
      }
    }
    int j = paramArrayOfObject2 == null ? 0 : paramArrayOfObject2.length;
    if (j == 0)
    {
      localObject1 = EMPTY_SYNCS;
    }
    else
    {
      localObject1 = new LockInfo[j];
      for (int k = 0; k < j; k++)
      {
        Object localObject2 = paramArrayOfObject2[k];
        String str2 = localObject2.getClass().getName();
        int i1 = System.identityHashCode(localObject2);
        localObject1[k] = new LockInfo(str2, i1);
      }
    }
    initialize(paramThread1, paramInt, paramObject, paramThread2, paramLong1, paramLong2, paramLong3, paramLong4, paramArrayOfStackTraceElement, arrayOfMonitorInfo, (LockInfo[])localObject1);
  }
  
  private void initialize(Thread paramThread1, int paramInt, Object paramObject, Thread paramThread2, long paramLong1, long paramLong2, long paramLong3, long paramLong4, StackTraceElement[] paramArrayOfStackTraceElement, MonitorInfo[] paramArrayOfMonitorInfo, LockInfo[] paramArrayOfLockInfo)
  {
    threadId = paramThread1.getId();
    threadName = paramThread1.getName();
    threadState = ManagementFactoryHelper.toThreadState(paramInt);
    suspended = ManagementFactoryHelper.isThreadSuspended(paramInt);
    inNative = ManagementFactoryHelper.isThreadRunningNative(paramInt);
    blockedCount = paramLong1;
    blockedTime = paramLong2;
    waitedCount = paramLong3;
    waitedTime = paramLong4;
    if (paramObject == null)
    {
      lock = null;
      lockName = null;
    }
    else
    {
      lock = new LockInfo(paramObject);
      lockName = (lock.getClassName() + '@' + Integer.toHexString(lock.getIdentityHashCode()));
    }
    if (paramThread2 == null)
    {
      lockOwnerId = -1L;
      lockOwnerName = null;
    }
    else
    {
      lockOwnerId = paramThread2.getId();
      lockOwnerName = paramThread2.getName();
    }
    if (paramArrayOfStackTraceElement == null) {
      stackTrace = NO_STACK_TRACE;
    } else {
      stackTrace = paramArrayOfStackTraceElement;
    }
    lockedMonitors = paramArrayOfMonitorInfo;
    lockedSynchronizers = paramArrayOfLockInfo;
  }
  
  private ThreadInfo(CompositeData paramCompositeData)
  {
    ThreadInfoCompositeData localThreadInfoCompositeData = ThreadInfoCompositeData.getInstance(paramCompositeData);
    threadId = localThreadInfoCompositeData.threadId();
    threadName = localThreadInfoCompositeData.threadName();
    blockedTime = localThreadInfoCompositeData.blockedTime();
    blockedCount = localThreadInfoCompositeData.blockedCount();
    waitedTime = localThreadInfoCompositeData.waitedTime();
    waitedCount = localThreadInfoCompositeData.waitedCount();
    lockName = localThreadInfoCompositeData.lockName();
    lockOwnerId = localThreadInfoCompositeData.lockOwnerId();
    lockOwnerName = localThreadInfoCompositeData.lockOwnerName();
    threadState = localThreadInfoCompositeData.threadState();
    suspended = localThreadInfoCompositeData.suspended();
    inNative = localThreadInfoCompositeData.inNative();
    stackTrace = localThreadInfoCompositeData.stackTrace();
    if (localThreadInfoCompositeData.isCurrentVersion())
    {
      lock = localThreadInfoCompositeData.lockInfo();
      lockedMonitors = localThreadInfoCompositeData.lockedMonitors();
      lockedSynchronizers = localThreadInfoCompositeData.lockedSynchronizers();
    }
    else
    {
      if (lockName != null)
      {
        String[] arrayOfString = lockName.split("@");
        if (arrayOfString.length == 2)
        {
          int i = Integer.parseInt(arrayOfString[1], 16);
          lock = new LockInfo(arrayOfString[0], i);
        }
        else
        {
          assert (arrayOfString.length == 2);
          lock = null;
        }
      }
      else
      {
        lock = null;
      }
      lockedMonitors = EMPTY_MONITORS;
      lockedSynchronizers = EMPTY_SYNCS;
    }
  }
  
  public long getThreadId()
  {
    return threadId;
  }
  
  public String getThreadName()
  {
    return threadName;
  }
  
  public Thread.State getThreadState()
  {
    return threadState;
  }
  
  public long getBlockedTime()
  {
    return blockedTime;
  }
  
  public long getBlockedCount()
  {
    return blockedCount;
  }
  
  public long getWaitedTime()
  {
    return waitedTime;
  }
  
  public long getWaitedCount()
  {
    return waitedCount;
  }
  
  public LockInfo getLockInfo()
  {
    return lock;
  }
  
  public String getLockName()
  {
    return lockName;
  }
  
  public long getLockOwnerId()
  {
    return lockOwnerId;
  }
  
  public String getLockOwnerName()
  {
    return lockOwnerName;
  }
  
  public StackTraceElement[] getStackTrace()
  {
    return stackTrace;
  }
  
  public boolean isSuspended()
  {
    return suspended;
  }
  
  public boolean isInNative()
  {
    return inNative;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("\"" + getThreadName() + "\" Id=" + getThreadId() + " " + getThreadState());
    if (getLockName() != null) {
      localStringBuilder.append(" on " + getLockName());
    }
    if (getLockOwnerName() != null) {
      localStringBuilder.append(" owned by \"" + getLockOwnerName() + "\" Id=" + getLockOwnerId());
    }
    if (isSuspended()) {
      localStringBuilder.append(" (suspended)");
    }
    if (isInNative()) {
      localStringBuilder.append(" (in native)");
    }
    localStringBuilder.append('\n');
    Object localObject2;
    Object localObject3;
    for (int i = 0; (i < stackTrace.length) && (i < 8); i++)
    {
      localObject1 = stackTrace[i];
      localStringBuilder.append("\tat " + ((StackTraceElement)localObject1).toString());
      localStringBuilder.append('\n');
      if ((i == 0) && (getLockInfo() != null))
      {
        localObject2 = getThreadState();
        switch (localObject2)
        {
        case BLOCKED: 
          localStringBuilder.append("\t-  blocked on " + getLockInfo());
          localStringBuilder.append('\n');
          break;
        case WAITING: 
          localStringBuilder.append("\t-  waiting on " + getLockInfo());
          localStringBuilder.append('\n');
          break;
        case TIMED_WAITING: 
          localStringBuilder.append("\t-  waiting on " + getLockInfo());
          localStringBuilder.append('\n');
          break;
        }
      }
      for (localObject3 : lockedMonitors) {
        if (((MonitorInfo)localObject3).getLockedStackDepth() == i)
        {
          localStringBuilder.append("\t-  locked " + localObject3);
          localStringBuilder.append('\n');
        }
      }
    }
    if (i < stackTrace.length)
    {
      localStringBuilder.append("\t...");
      localStringBuilder.append('\n');
    }
    Object localObject1 = getLockedSynchronizers();
    if (localObject1.length > 0)
    {
      localStringBuilder.append("\n\tNumber of locked synchronizers = " + localObject1.length);
      localStringBuilder.append('\n');
      for (localObject3 : localObject1)
      {
        localStringBuilder.append("\t- " + localObject3);
        localStringBuilder.append('\n');
      }
    }
    localStringBuilder.append('\n');
    return localStringBuilder.toString();
  }
  
  public static ThreadInfo from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof ThreadInfoCompositeData)) {
      return ((ThreadInfoCompositeData)paramCompositeData).getThreadInfo();
    }
    return new ThreadInfo(paramCompositeData);
  }
  
  public MonitorInfo[] getLockedMonitors()
  {
    return lockedMonitors;
  }
  
  public LockInfo[] getLockedSynchronizers()
  {
    return lockedSynchronizers;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\management\ThreadInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
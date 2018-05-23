package sun.management;

import com.sun.management.ThreadMXBean;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import javax.management.ObjectName;

class ThreadImpl
  implements ThreadMXBean
{
  private final VMManagement jvm;
  private boolean contentionMonitoringEnabled = false;
  private boolean cpuTimeEnabled;
  private boolean allocatedMemoryEnabled;
  
  ThreadImpl(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
    cpuTimeEnabled = jvm.isThreadCpuTimeEnabled();
    allocatedMemoryEnabled = jvm.isThreadAllocatedMemoryEnabled();
  }
  
  public int getThreadCount()
  {
    return jvm.getLiveThreadCount();
  }
  
  public int getPeakThreadCount()
  {
    return jvm.getPeakThreadCount();
  }
  
  public long getTotalStartedThreadCount()
  {
    return jvm.getTotalThreadCount();
  }
  
  public int getDaemonThreadCount()
  {
    return jvm.getDaemonThreadCount();
  }
  
  public boolean isThreadContentionMonitoringSupported()
  {
    return jvm.isThreadContentionMonitoringSupported();
  }
  
  public synchronized boolean isThreadContentionMonitoringEnabled()
  {
    if (!isThreadContentionMonitoringSupported()) {
      throw new UnsupportedOperationException("Thread contention monitoring is not supported.");
    }
    return contentionMonitoringEnabled;
  }
  
  public boolean isThreadCpuTimeSupported()
  {
    return jvm.isOtherThreadCpuTimeSupported();
  }
  
  public boolean isCurrentThreadCpuTimeSupported()
  {
    return jvm.isCurrentThreadCpuTimeSupported();
  }
  
  public boolean isThreadAllocatedMemorySupported()
  {
    return jvm.isThreadAllocatedMemorySupported();
  }
  
  public boolean isThreadCpuTimeEnabled()
  {
    if ((!isThreadCpuTimeSupported()) && (!isCurrentThreadCpuTimeSupported())) {
      throw new UnsupportedOperationException("Thread CPU time measurement is not supported");
    }
    return cpuTimeEnabled;
  }
  
  public boolean isThreadAllocatedMemoryEnabled()
  {
    if (!isThreadAllocatedMemorySupported()) {
      throw new UnsupportedOperationException("Thread allocated memory measurement is not supported");
    }
    return allocatedMemoryEnabled;
  }
  
  public long[] getAllThreadIds()
  {
    Util.checkMonitorAccess();
    Thread[] arrayOfThread = getThreads();
    int i = arrayOfThread.length;
    long[] arrayOfLong = new long[i];
    for (int j = 0; j < i; j++)
    {
      Thread localThread = arrayOfThread[j];
      arrayOfLong[j] = localThread.getId();
    }
    return arrayOfLong;
  }
  
  public ThreadInfo getThreadInfo(long paramLong)
  {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = paramLong;
    ThreadInfo[] arrayOfThreadInfo = getThreadInfo(arrayOfLong, 0);
    return arrayOfThreadInfo[0];
  }
  
  public ThreadInfo getThreadInfo(long paramLong, int paramInt)
  {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = paramLong;
    ThreadInfo[] arrayOfThreadInfo = getThreadInfo(arrayOfLong, paramInt);
    return arrayOfThreadInfo[0];
  }
  
  public ThreadInfo[] getThreadInfo(long[] paramArrayOfLong)
  {
    return getThreadInfo(paramArrayOfLong, 0);
  }
  
  private void verifyThreadIds(long[] paramArrayOfLong)
  {
    if (paramArrayOfLong == null) {
      throw new NullPointerException("Null ids parameter.");
    }
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      if (paramArrayOfLong[i] <= 0L) {
        throw new IllegalArgumentException("Invalid thread ID parameter: " + paramArrayOfLong[i]);
      }
    }
  }
  
  public ThreadInfo[] getThreadInfo(long[] paramArrayOfLong, int paramInt)
  {
    verifyThreadIds(paramArrayOfLong);
    if (paramInt < 0) {
      throw new IllegalArgumentException("Invalid maxDepth parameter: " + paramInt);
    }
    if (paramArrayOfLong.length == 0) {
      return new ThreadInfo[0];
    }
    Util.checkMonitorAccess();
    ThreadInfo[] arrayOfThreadInfo = new ThreadInfo[paramArrayOfLong.length];
    if (paramInt == Integer.MAX_VALUE) {
      getThreadInfo1(paramArrayOfLong, -1, arrayOfThreadInfo);
    } else {
      getThreadInfo1(paramArrayOfLong, paramInt, arrayOfThreadInfo);
    }
    return arrayOfThreadInfo;
  }
  
  public void setThreadContentionMonitoringEnabled(boolean paramBoolean)
  {
    if (!isThreadContentionMonitoringSupported()) {
      throw new UnsupportedOperationException("Thread contention monitoring is not supported");
    }
    Util.checkControlAccess();
    synchronized (this)
    {
      if (contentionMonitoringEnabled != paramBoolean)
      {
        if (paramBoolean) {
          resetContentionTimes0(0L);
        }
        setThreadContentionMonitoringEnabled0(paramBoolean);
        contentionMonitoringEnabled = paramBoolean;
      }
    }
  }
  
  private boolean verifyCurrentThreadCpuTime()
  {
    if (!isCurrentThreadCpuTimeSupported()) {
      throw new UnsupportedOperationException("Current thread CPU time measurement is not supported.");
    }
    return isThreadCpuTimeEnabled();
  }
  
  public long getCurrentThreadCpuTime()
  {
    if (verifyCurrentThreadCpuTime()) {
      return getThreadTotalCpuTime0(0L);
    }
    return -1L;
  }
  
  public long getThreadCpuTime(long paramLong)
  {
    long[] arrayOfLong1 = new long[1];
    arrayOfLong1[0] = paramLong;
    long[] arrayOfLong2 = getThreadCpuTime(arrayOfLong1);
    return arrayOfLong2[0];
  }
  
  private boolean verifyThreadCpuTime(long[] paramArrayOfLong)
  {
    verifyThreadIds(paramArrayOfLong);
    if ((!isThreadCpuTimeSupported()) && (!isCurrentThreadCpuTimeSupported())) {
      throw new UnsupportedOperationException("Thread CPU time measurement is not supported.");
    }
    if (!isThreadCpuTimeSupported()) {
      for (int i = 0; i < paramArrayOfLong.length; i++) {
        if (paramArrayOfLong[i] != Thread.currentThread().getId()) {
          throw new UnsupportedOperationException("Thread CPU time measurement is only supported for the current thread.");
        }
      }
    }
    return isThreadCpuTimeEnabled();
  }
  
  public long[] getThreadCpuTime(long[] paramArrayOfLong)
  {
    boolean bool = verifyThreadCpuTime(paramArrayOfLong);
    int i = paramArrayOfLong.length;
    long[] arrayOfLong = new long[i];
    Arrays.fill(arrayOfLong, -1L);
    if (bool) {
      if (i == 1)
      {
        long l = paramArrayOfLong[0];
        if (l == Thread.currentThread().getId()) {
          l = 0L;
        }
        arrayOfLong[0] = getThreadTotalCpuTime0(l);
      }
      else
      {
        getThreadTotalCpuTime1(paramArrayOfLong, arrayOfLong);
      }
    }
    return arrayOfLong;
  }
  
  public long getCurrentThreadUserTime()
  {
    if (verifyCurrentThreadCpuTime()) {
      return getThreadUserCpuTime0(0L);
    }
    return -1L;
  }
  
  public long getThreadUserTime(long paramLong)
  {
    long[] arrayOfLong1 = new long[1];
    arrayOfLong1[0] = paramLong;
    long[] arrayOfLong2 = getThreadUserTime(arrayOfLong1);
    return arrayOfLong2[0];
  }
  
  public long[] getThreadUserTime(long[] paramArrayOfLong)
  {
    boolean bool = verifyThreadCpuTime(paramArrayOfLong);
    int i = paramArrayOfLong.length;
    long[] arrayOfLong = new long[i];
    Arrays.fill(arrayOfLong, -1L);
    if (bool) {
      if (i == 1)
      {
        long l = paramArrayOfLong[0];
        if (l == Thread.currentThread().getId()) {
          l = 0L;
        }
        arrayOfLong[0] = getThreadUserCpuTime0(l);
      }
      else
      {
        getThreadUserCpuTime1(paramArrayOfLong, arrayOfLong);
      }
    }
    return arrayOfLong;
  }
  
  public void setThreadCpuTimeEnabled(boolean paramBoolean)
  {
    if ((!isThreadCpuTimeSupported()) && (!isCurrentThreadCpuTimeSupported())) {
      throw new UnsupportedOperationException("Thread CPU time measurement is not supported");
    }
    Util.checkControlAccess();
    synchronized (this)
    {
      if (cpuTimeEnabled != paramBoolean)
      {
        setThreadCpuTimeEnabled0(paramBoolean);
        cpuTimeEnabled = paramBoolean;
      }
    }
  }
  
  public long getThreadAllocatedBytes(long paramLong)
  {
    long[] arrayOfLong1 = new long[1];
    arrayOfLong1[0] = paramLong;
    long[] arrayOfLong2 = getThreadAllocatedBytes(arrayOfLong1);
    return arrayOfLong2[0];
  }
  
  private boolean verifyThreadAllocatedMemory(long[] paramArrayOfLong)
  {
    verifyThreadIds(paramArrayOfLong);
    if (!isThreadAllocatedMemorySupported()) {
      throw new UnsupportedOperationException("Thread allocated memory measurement is not supported.");
    }
    return isThreadAllocatedMemoryEnabled();
  }
  
  public long[] getThreadAllocatedBytes(long[] paramArrayOfLong)
  {
    boolean bool = verifyThreadAllocatedMemory(paramArrayOfLong);
    long[] arrayOfLong = new long[paramArrayOfLong.length];
    Arrays.fill(arrayOfLong, -1L);
    if (bool) {
      getThreadAllocatedMemory1(paramArrayOfLong, arrayOfLong);
    }
    return arrayOfLong;
  }
  
  public void setThreadAllocatedMemoryEnabled(boolean paramBoolean)
  {
    if (!isThreadAllocatedMemorySupported()) {
      throw new UnsupportedOperationException("Thread allocated memory measurement is not supported.");
    }
    Util.checkControlAccess();
    synchronized (this)
    {
      if (allocatedMemoryEnabled != paramBoolean)
      {
        setThreadAllocatedMemoryEnabled0(paramBoolean);
        allocatedMemoryEnabled = paramBoolean;
      }
    }
  }
  
  public long[] findMonitorDeadlockedThreads()
  {
    Util.checkMonitorAccess();
    Thread[] arrayOfThread = findMonitorDeadlockedThreads0();
    if (arrayOfThread == null) {
      return null;
    }
    long[] arrayOfLong = new long[arrayOfThread.length];
    for (int i = 0; i < arrayOfThread.length; i++)
    {
      Thread localThread = arrayOfThread[i];
      arrayOfLong[i] = localThread.getId();
    }
    return arrayOfLong;
  }
  
  public long[] findDeadlockedThreads()
  {
    if (!isSynchronizerUsageSupported()) {
      throw new UnsupportedOperationException("Monitoring of Synchronizer Usage is not supported.");
    }
    Util.checkMonitorAccess();
    Thread[] arrayOfThread = findDeadlockedThreads0();
    if (arrayOfThread == null) {
      return null;
    }
    long[] arrayOfLong = new long[arrayOfThread.length];
    for (int i = 0; i < arrayOfThread.length; i++)
    {
      Thread localThread = arrayOfThread[i];
      arrayOfLong[i] = localThread.getId();
    }
    return arrayOfLong;
  }
  
  public void resetPeakThreadCount()
  {
    Util.checkControlAccess();
    resetPeakThreadCount0();
  }
  
  public boolean isObjectMonitorUsageSupported()
  {
    return jvm.isObjectMonitorUsageSupported();
  }
  
  public boolean isSynchronizerUsageSupported()
  {
    return jvm.isSynchronizerUsageSupported();
  }
  
  private void verifyDumpThreads(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!isObjectMonitorUsageSupported())) {
      throw new UnsupportedOperationException("Monitoring of Object Monitor Usage is not supported.");
    }
    if ((paramBoolean2) && (!isSynchronizerUsageSupported())) {
      throw new UnsupportedOperationException("Monitoring of Synchronizer Usage is not supported.");
    }
    Util.checkMonitorAccess();
  }
  
  public ThreadInfo[] getThreadInfo(long[] paramArrayOfLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    verifyThreadIds(paramArrayOfLong);
    if (paramArrayOfLong.length == 0) {
      return new ThreadInfo[0];
    }
    verifyDumpThreads(paramBoolean1, paramBoolean2);
    return dumpThreads0(paramArrayOfLong, paramBoolean1, paramBoolean2);
  }
  
  public ThreadInfo[] dumpAllThreads(boolean paramBoolean1, boolean paramBoolean2)
  {
    verifyDumpThreads(paramBoolean1, paramBoolean2);
    return dumpThreads0(null, paramBoolean1, paramBoolean2);
  }
  
  private static native Thread[] getThreads();
  
  private static native void getThreadInfo1(long[] paramArrayOfLong, int paramInt, ThreadInfo[] paramArrayOfThreadInfo);
  
  private static native long getThreadTotalCpuTime0(long paramLong);
  
  private static native void getThreadTotalCpuTime1(long[] paramArrayOfLong1, long[] paramArrayOfLong2);
  
  private static native long getThreadUserCpuTime0(long paramLong);
  
  private static native void getThreadUserCpuTime1(long[] paramArrayOfLong1, long[] paramArrayOfLong2);
  
  private static native void getThreadAllocatedMemory1(long[] paramArrayOfLong1, long[] paramArrayOfLong2);
  
  private static native void setThreadCpuTimeEnabled0(boolean paramBoolean);
  
  private static native void setThreadAllocatedMemoryEnabled0(boolean paramBoolean);
  
  private static native void setThreadContentionMonitoringEnabled0(boolean paramBoolean);
  
  private static native Thread[] findMonitorDeadlockedThreads0();
  
  private static native Thread[] findDeadlockedThreads0();
  
  private static native void resetPeakThreadCount0();
  
  private static native ThreadInfo[] dumpThreads0(long[] paramArrayOfLong, boolean paramBoolean1, boolean paramBoolean2);
  
  private static native void resetContentionTimes0(long paramLong);
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=Threading");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\ThreadImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
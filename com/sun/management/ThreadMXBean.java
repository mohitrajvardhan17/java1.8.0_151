package com.sun.management;

import jdk.Exported;

@Exported
public abstract interface ThreadMXBean
  extends java.lang.management.ThreadMXBean
{
  public abstract long[] getThreadCpuTime(long[] paramArrayOfLong);
  
  public abstract long[] getThreadUserTime(long[] paramArrayOfLong);
  
  public abstract long getThreadAllocatedBytes(long paramLong);
  
  public abstract long[] getThreadAllocatedBytes(long[] paramArrayOfLong);
  
  public abstract boolean isThreadAllocatedMemorySupported();
  
  public abstract boolean isThreadAllocatedMemoryEnabled();
  
  public abstract void setThreadAllocatedMemoryEnabled(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\ThreadMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
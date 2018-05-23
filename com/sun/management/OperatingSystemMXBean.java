package com.sun.management;

import jdk.Exported;

@Exported
public abstract interface OperatingSystemMXBean
  extends java.lang.management.OperatingSystemMXBean
{
  public abstract long getCommittedVirtualMemorySize();
  
  public abstract long getTotalSwapSpaceSize();
  
  public abstract long getFreeSwapSpaceSize();
  
  public abstract long getProcessCpuTime();
  
  public abstract long getFreePhysicalMemorySize();
  
  public abstract long getTotalPhysicalMemorySize();
  
  public abstract double getSystemCpuLoad();
  
  public abstract double getProcessCpuLoad();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\OperatingSystemMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
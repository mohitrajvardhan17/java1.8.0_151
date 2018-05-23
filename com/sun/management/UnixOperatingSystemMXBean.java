package com.sun.management;

import jdk.Exported;

@Exported
public abstract interface UnixOperatingSystemMXBean
  extends OperatingSystemMXBean
{
  public abstract long getOpenFileDescriptorCount();
  
  public abstract long getMaxFileDescriptorCount();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\UnixOperatingSystemMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
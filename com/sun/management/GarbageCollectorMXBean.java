package com.sun.management;

import jdk.Exported;

@Exported
public abstract interface GarbageCollectorMXBean
  extends java.lang.management.GarbageCollectorMXBean
{
  public abstract GcInfo getLastGcInfo();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\GarbageCollectorMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
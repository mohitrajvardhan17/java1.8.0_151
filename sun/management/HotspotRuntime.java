package sun.management;

import java.util.List;
import sun.management.counter.Counter;

class HotspotRuntime
  implements HotspotRuntimeMBean
{
  private VMManagement jvm;
  private static final String JAVA_RT = "java.rt.";
  private static final String COM_SUN_RT = "com.sun.rt.";
  private static final String SUN_RT = "sun.rt.";
  private static final String JAVA_PROPERTY = "java.property.";
  private static final String COM_SUN_PROPERTY = "com.sun.property.";
  private static final String SUN_PROPERTY = "sun.property.";
  private static final String RT_COUNTER_NAME_PATTERN = "java.rt.|com.sun.rt.|sun.rt.|java.property.|com.sun.property.|sun.property.";
  
  HotspotRuntime(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
  }
  
  public long getSafepointCount()
  {
    return jvm.getSafepointCount();
  }
  
  public long getTotalSafepointTime()
  {
    return jvm.getTotalSafepointTime();
  }
  
  public long getSafepointSyncTime()
  {
    return jvm.getSafepointSyncTime();
  }
  
  public List<Counter> getInternalRuntimeCounters()
  {
    return jvm.getInternalCounters("java.rt.|com.sun.rt.|sun.rt.|java.property.|com.sun.property.|sun.property.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\HotspotRuntime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
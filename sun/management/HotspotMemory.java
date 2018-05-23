package sun.management;

import java.util.List;
import sun.management.counter.Counter;

class HotspotMemory
  implements HotspotMemoryMBean
{
  private VMManagement jvm;
  private static final String JAVA_GC = "java.gc.";
  private static final String COM_SUN_GC = "com.sun.gc.";
  private static final String SUN_GC = "sun.gc.";
  private static final String GC_COUNTER_NAME_PATTERN = "java.gc.|com.sun.gc.|sun.gc.";
  
  HotspotMemory(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
  }
  
  public List<Counter> getInternalMemoryCounters()
  {
    return jvm.getInternalCounters("java.gc.|com.sun.gc.|sun.gc.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\HotspotMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
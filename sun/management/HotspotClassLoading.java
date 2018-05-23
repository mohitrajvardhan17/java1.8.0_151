package sun.management;

import java.util.List;
import sun.management.counter.Counter;

class HotspotClassLoading
  implements HotspotClassLoadingMBean
{
  private VMManagement jvm;
  private static final String JAVA_CLS = "java.cls.";
  private static final String COM_SUN_CLS = "com.sun.cls.";
  private static final String SUN_CLS = "sun.cls.";
  private static final String CLS_COUNTER_NAME_PATTERN = "java.cls.|com.sun.cls.|sun.cls.";
  
  HotspotClassLoading(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
  }
  
  public long getLoadedClassSize()
  {
    return jvm.getLoadedClassSize();
  }
  
  public long getUnloadedClassSize()
  {
    return jvm.getUnloadedClassSize();
  }
  
  public long getClassLoadingTime()
  {
    return jvm.getClassLoadingTime();
  }
  
  public long getMethodDataSize()
  {
    return jvm.getMethodDataSize();
  }
  
  public long getInitializedClassCount()
  {
    return jvm.getInitializedClassCount();
  }
  
  public long getClassInitializationTime()
  {
    return jvm.getClassInitializationTime();
  }
  
  public long getClassVerificationTime()
  {
    return jvm.getClassVerificationTime();
  }
  
  public List<Counter> getInternalClassLoadingCounters()
  {
    return jvm.getInternalCounters("java.cls.|com.sun.cls.|sun.cls.");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\HotspotClassLoading.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
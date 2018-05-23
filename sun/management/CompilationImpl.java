package sun.management;

import java.lang.management.CompilationMXBean;
import javax.management.ObjectName;

class CompilationImpl
  implements CompilationMXBean
{
  private final VMManagement jvm;
  private final String name;
  
  CompilationImpl(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
    name = jvm.getCompilerName();
    if (name == null) {
      throw new AssertionError("Null compiler name");
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean isCompilationTimeMonitoringSupported()
  {
    return jvm.isCompilationTimeMonitoringSupported();
  }
  
  public long getTotalCompilationTime()
  {
    if (!isCompilationTimeMonitoringSupported()) {
      throw new UnsupportedOperationException("Compilation time monitoring is not supported.");
    }
    return jvm.getTotalCompileTime();
  }
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=Compilation");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\CompilationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
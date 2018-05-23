package sun.management;

import java.lang.management.OperatingSystemMXBean;
import javax.management.ObjectName;
import sun.misc.Unsafe;

public class BaseOperatingSystemImpl
  implements OperatingSystemMXBean
{
  private final VMManagement jvm;
  private static final Unsafe unsafe = ;
  private double[] loadavg = new double[1];
  
  protected BaseOperatingSystemImpl(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
  }
  
  public String getName()
  {
    return jvm.getOsName();
  }
  
  public String getArch()
  {
    return jvm.getOsArch();
  }
  
  public String getVersion()
  {
    return jvm.getOsVersion();
  }
  
  public int getAvailableProcessors()
  {
    return jvm.getAvailableProcessors();
  }
  
  public double getSystemLoadAverage()
  {
    if (unsafe.getLoadAverage(loadavg, 1) == 1) {
      return loadavg[0];
    }
    return -1.0D;
  }
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=OperatingSystem");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\BaseOperatingSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
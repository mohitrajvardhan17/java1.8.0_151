package sun.management;

import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.management.ObjectName;

class RuntimeImpl
  implements RuntimeMXBean
{
  private final VMManagement jvm;
  private final long vmStartupTime;
  
  RuntimeImpl(VMManagement paramVMManagement)
  {
    jvm = paramVMManagement;
    vmStartupTime = jvm.getStartupTime();
  }
  
  public String getName()
  {
    return jvm.getVmId();
  }
  
  public String getManagementSpecVersion()
  {
    return jvm.getManagementVersion();
  }
  
  public String getVmName()
  {
    return jvm.getVmName();
  }
  
  public String getVmVendor()
  {
    return jvm.getVmVendor();
  }
  
  public String getVmVersion()
  {
    return jvm.getVmVersion();
  }
  
  public String getSpecName()
  {
    return jvm.getVmSpecName();
  }
  
  public String getSpecVendor()
  {
    return jvm.getVmSpecVendor();
  }
  
  public String getSpecVersion()
  {
    return jvm.getVmSpecVersion();
  }
  
  public String getClassPath()
  {
    return jvm.getClassPath();
  }
  
  public String getLibraryPath()
  {
    return jvm.getLibraryPath();
  }
  
  public String getBootClassPath()
  {
    if (!isBootClassPathSupported()) {
      throw new UnsupportedOperationException("Boot class path mechanism is not supported");
    }
    Util.checkMonitorAccess();
    return jvm.getBootClassPath();
  }
  
  public List<String> getInputArguments()
  {
    Util.checkMonitorAccess();
    return jvm.getVmArguments();
  }
  
  public long getUptime()
  {
    return jvm.getUptime();
  }
  
  public long getStartTime()
  {
    return vmStartupTime;
  }
  
  public boolean isBootClassPathSupported()
  {
    return jvm.isBootClassPathSupported();
  }
  
  public Map<String, String> getSystemProperties()
  {
    Properties localProperties = System.getProperties();
    HashMap localHashMap = new HashMap();
    Set localSet = localProperties.stringPropertyNames();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = localProperties.getProperty(str1);
      localHashMap.put(str1, str2);
    }
    return localHashMap;
  }
  
  public ObjectName getObjectName()
  {
    return Util.newObjectName("java.lang:type=Runtime");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\RuntimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
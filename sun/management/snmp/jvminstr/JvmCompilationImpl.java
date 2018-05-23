package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmJITCompilerTimeMonitoring;
import sun.management.snmp.jvmmib.JvmCompilationMBean;

public class JvmCompilationImpl
  implements JvmCompilationMBean
{
  static final EnumJvmJITCompilerTimeMonitoring JvmJITCompilerTimeMonitoringSupported = new EnumJvmJITCompilerTimeMonitoring("supported");
  static final EnumJvmJITCompilerTimeMonitoring JvmJITCompilerTimeMonitoringUnsupported = new EnumJvmJITCompilerTimeMonitoring("unsupported");
  
  public JvmCompilationImpl(SnmpMib paramSnmpMib) {}
  
  public JvmCompilationImpl(SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) {}
  
  private static CompilationMXBean getCompilationMXBean()
  {
    return ManagementFactory.getCompilationMXBean();
  }
  
  public EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring()
    throws SnmpStatusException
  {
    if (getCompilationMXBean().isCompilationTimeMonitoringSupported()) {
      return JvmJITCompilerTimeMonitoringSupported;
    }
    return JvmJITCompilerTimeMonitoringUnsupported;
  }
  
  public Long getJvmJITCompilerTimeMs()
    throws SnmpStatusException
  {
    long l;
    if (getCompilationMXBean().isCompilationTimeMonitoringSupported()) {
      l = getCompilationMXBean().getTotalCompilationTime();
    } else {
      l = 0L;
    }
    return new Long(l);
  }
  
  public String getJvmJITCompilerName()
    throws SnmpStatusException
  {
    return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(getCompilationMXBean().getName());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmCompilationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
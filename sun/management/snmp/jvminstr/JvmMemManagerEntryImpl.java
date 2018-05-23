package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.MemoryManagerMXBean;
import sun.management.snmp.jvmmib.EnumJvmMemManagerState;
import sun.management.snmp.jvmmib.JvmMemManagerEntryMBean;

public class JvmMemManagerEntryImpl
  implements JvmMemManagerEntryMBean
{
  protected final int JvmMemManagerIndex;
  protected MemoryManagerMXBean manager;
  private static final EnumJvmMemManagerState JvmMemManagerStateValid = new EnumJvmMemManagerState("valid");
  private static final EnumJvmMemManagerState JvmMemManagerStateInvalid = new EnumJvmMemManagerState("invalid");
  
  public JvmMemManagerEntryImpl(MemoryManagerMXBean paramMemoryManagerMXBean, int paramInt)
  {
    manager = paramMemoryManagerMXBean;
    JvmMemManagerIndex = paramInt;
  }
  
  public String getJvmMemManagerName()
    throws SnmpStatusException
  {
    return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(manager.getName());
  }
  
  public Integer getJvmMemManagerIndex()
    throws SnmpStatusException
  {
    return new Integer(JvmMemManagerIndex);
  }
  
  public EnumJvmMemManagerState getJvmMemManagerState()
    throws SnmpStatusException
  {
    if (manager.isValid()) {
      return JvmMemManagerStateValid;
    }
    return JvmMemManagerStateInvalid;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvminstr\JvmMemManagerEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
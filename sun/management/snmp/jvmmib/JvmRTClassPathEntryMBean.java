package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTClassPathEntryMBean
{
  public abstract String getJvmRTClassPathItem()
    throws SnmpStatusException;
  
  public abstract Integer getJvmRTClassPathIndex()
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmRTClassPathEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmMemGCEntryMBean
{
  public abstract Long getJvmMemGCTimeMs()
    throws SnmpStatusException;
  
  public abstract Long getJvmMemGCCount()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemManagerIndex()
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmMemGCEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
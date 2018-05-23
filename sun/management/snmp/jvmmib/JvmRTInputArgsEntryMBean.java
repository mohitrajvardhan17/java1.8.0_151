package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmRTInputArgsEntryMBean
{
  public abstract String getJvmRTInputArgsItem()
    throws SnmpStatusException;
  
  public abstract Integer getJvmRTInputArgsIndex()
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmRTInputArgsEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
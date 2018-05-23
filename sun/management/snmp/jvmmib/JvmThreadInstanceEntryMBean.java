package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmThreadInstanceEntryMBean
{
  public abstract String getJvmThreadInstName()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadInstCpuTimeNs()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadInstWaitTimeMs()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadInstWaitCount()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadInstBlockTimeMs()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadInstBlockCount()
    throws SnmpStatusException;
  
  public abstract Byte[] getJvmThreadInstState()
    throws SnmpStatusException;
  
  public abstract String getJvmThreadInstLockOwnerPtr()
    throws SnmpStatusException;
  
  public abstract Long getJvmThreadInstId()
    throws SnmpStatusException;
  
  public abstract String getJvmThreadInstLockName()
    throws SnmpStatusException;
  
  public abstract Byte[] getJvmThreadInstIndex()
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\snmp\jvmmib\JvmThreadInstanceEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
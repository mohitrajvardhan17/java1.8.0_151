package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public abstract interface SnmpStandardMetaServer
{
  public abstract SnmpValue get(long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  public abstract SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  public abstract void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpStandardMetaServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
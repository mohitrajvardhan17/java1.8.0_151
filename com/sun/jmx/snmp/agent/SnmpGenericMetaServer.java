package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public abstract interface SnmpGenericMetaServer
{
  public abstract Object buildAttributeValue(long paramLong, SnmpValue paramSnmpValue)
    throws SnmpStatusException;
  
  public abstract SnmpValue buildSnmpValue(long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  public abstract String getAttributeName(long paramLong)
    throws SnmpStatusException;
  
  public abstract void checkSetAccess(SnmpValue paramSnmpValue, long paramLong, Object paramObject)
    throws SnmpStatusException;
  
  public abstract void checkGetAccess(long paramLong, Object paramObject)
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpGenericMetaServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
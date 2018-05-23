package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import javax.management.ObjectName;

public abstract interface SnmpTableCallbackHandler
{
  public abstract void addEntryCb(int paramInt, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject, SnmpMibTable paramSnmpMibTable)
    throws SnmpStatusException;
  
  public abstract void removeEntryCb(int paramInt, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject, SnmpMibTable paramSnmpMibTable)
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
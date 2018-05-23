package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface SnmpUserDataFactory
{
  public abstract Object allocateUserData(SnmpPdu paramSnmpPdu)
    throws SnmpStatusException;
  
  public abstract void releaseUserData(Object paramObject, SnmpPdu paramSnmpPdu)
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpUserDataFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
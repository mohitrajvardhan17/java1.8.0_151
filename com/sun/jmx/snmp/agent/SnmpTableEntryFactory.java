package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface SnmpTableEntryFactory
  extends SnmpTableCallbackHandler
{
  public abstract void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt, SnmpMibTable paramSnmpMibTable)
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableEntryFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
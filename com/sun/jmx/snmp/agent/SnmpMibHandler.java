package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;

public abstract interface SnmpMibHandler
{
  public abstract SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent)
    throws IllegalArgumentException;
  
  public abstract SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid)
    throws IllegalArgumentException;
  
  public abstract SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, String paramString)
    throws IllegalArgumentException;
  
  public abstract SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, String paramString, SnmpOid[] paramArrayOfSnmpOid)
    throws IllegalArgumentException;
  
  public abstract boolean removeMib(SnmpMibAgent paramSnmpMibAgent);
  
  public abstract boolean removeMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid);
  
  public abstract boolean removeMib(SnmpMibAgent paramSnmpMibAgent, String paramString);
  
  public abstract boolean removeMib(SnmpMibAgent paramSnmpMibAgent, String paramString, SnmpOid[] paramArrayOfSnmpOid);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
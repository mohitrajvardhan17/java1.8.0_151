package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Vector;

public abstract interface SnmpMibSubRequest
  extends SnmpMibRequest
{
  public abstract Enumeration<SnmpVarBind> getElements();
  
  public abstract Vector<SnmpVarBind> getSubList();
  
  public abstract SnmpOid getEntryOid();
  
  public abstract boolean isNewEntry();
  
  public abstract SnmpVarBind getRowStatusVarBind();
  
  public abstract void registerGetException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
    throws SnmpStatusException;
  
  public abstract void registerSetException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
    throws SnmpStatusException;
  
  public abstract void registerCheckException(SnmpVarBind paramSnmpVarBind, SnmpStatusException paramSnmpStatusException)
    throws SnmpStatusException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibSubRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
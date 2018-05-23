package com.sun.jmx.snmp;

import java.util.Vector;

public abstract interface SnmpOidDatabase
  extends SnmpOidTable
{
  public abstract void add(SnmpOidTable paramSnmpOidTable);
  
  public abstract void remove(SnmpOidTable paramSnmpOidTable)
    throws SnmpStatusException;
  
  public abstract void removeAll();
  
  public abstract SnmpOidRecord resolveVarName(String paramString)
    throws SnmpStatusException;
  
  public abstract SnmpOidRecord resolveVarOid(String paramString)
    throws SnmpStatusException;
  
  public abstract Vector<?> getAllEntries();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOidDatabase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
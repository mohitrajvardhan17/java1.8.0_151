package com.sun.jmx.snmp;

import java.util.Vector;

public abstract interface SnmpOidTable
{
  public abstract SnmpOidRecord resolveVarName(String paramString)
    throws SnmpStatusException;
  
  public abstract SnmpOidRecord resolveVarOid(String paramString)
    throws SnmpStatusException;
  
  public abstract Vector<?> getAllEntries();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOidTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
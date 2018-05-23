package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpUnknownModelException;

public abstract interface SnmpSubSystem
{
  public abstract SnmpEngine getEngine();
  
  public abstract void addModel(int paramInt, SnmpModel paramSnmpModel);
  
  public abstract SnmpModel removeModel(int paramInt)
    throws SnmpUnknownModelException;
  
  public abstract SnmpModel getModel(int paramInt)
    throws SnmpUnknownModelException;
  
  public abstract int[] getModelIds();
  
  public abstract String[] getModelNames();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpSubSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
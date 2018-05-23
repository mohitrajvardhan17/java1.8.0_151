package com.sun.jmx.snmp;

public abstract interface SnmpEngine
{
  public abstract int getEngineTime();
  
  public abstract SnmpEngineId getEngineId();
  
  public abstract int getEngineBoots();
  
  public abstract SnmpUsmKeyHandler getUsmKeyHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
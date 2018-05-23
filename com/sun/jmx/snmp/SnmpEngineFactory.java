package com.sun.jmx.snmp;

public abstract interface SnmpEngineFactory
{
  public abstract SnmpEngine createEngine(SnmpEngineParameters paramSnmpEngineParameters);
  
  public abstract SnmpEngine createEngine(SnmpEngineParameters paramSnmpEngineParameters, InetAddressAcl paramInetAddressAcl);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
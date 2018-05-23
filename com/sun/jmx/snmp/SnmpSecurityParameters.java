package com.sun.jmx.snmp;

public abstract interface SnmpSecurityParameters
{
  public abstract int encode(byte[] paramArrayOfByte)
    throws SnmpTooBigException;
  
  public abstract void decode(byte[] paramArrayOfByte)
    throws SnmpStatusException;
  
  public abstract String getPrincipal();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpSecurityParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
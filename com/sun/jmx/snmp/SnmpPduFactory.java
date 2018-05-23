package com.sun.jmx.snmp;

public abstract interface SnmpPduFactory
{
  public abstract SnmpPdu decodeSnmpPdu(SnmpMsg paramSnmpMsg)
    throws SnmpStatusException;
  
  public abstract SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
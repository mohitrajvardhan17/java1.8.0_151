package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;

public abstract interface SnmpOutgoingRequest
{
  public abstract SnmpSecurityCache getSecurityCache();
  
  public abstract int encodeMessage(byte[] paramArrayOfByte)
    throws SnmpStatusException, SnmpTooBigException, SnmpSecurityException, SnmpUnknownSecModelException, SnmpBadSecurityLevelException;
  
  public abstract SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException;
  
  public abstract String printMessage();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpOutgoingRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;
import java.net.InetAddress;

public abstract interface SnmpIncomingRequest
{
  public abstract SnmpSecurityParameters getSecurityParameters();
  
  public abstract boolean isReport();
  
  public abstract boolean isResponse();
  
  public abstract void noResponse();
  
  public abstract String getPrincipal();
  
  public abstract int getSecurityLevel();
  
  public abstract int getSecurityModel();
  
  public abstract byte[] getContextName();
  
  public abstract byte[] getContextEngineId();
  
  public abstract byte[] getAccessContext();
  
  public abstract int encodeMessage(byte[] paramArrayOfByte)
    throws SnmpTooBigException;
  
  public abstract void decodeMessage(byte[] paramArrayOfByte, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws SnmpStatusException, SnmpUnknownSecModelException, SnmpBadSecurityLevelException;
  
  public abstract SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException;
  
  public abstract SnmpPdu decodeSnmpPdu()
    throws SnmpStatusException;
  
  public abstract String printRequestMessage();
  
  public abstract String printResponseMessage();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpIncomingRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
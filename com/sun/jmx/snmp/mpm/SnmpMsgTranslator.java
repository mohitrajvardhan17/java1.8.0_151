package com.sun.jmx.snmp.mpm;

import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpSecurityParameters;

public abstract interface SnmpMsgTranslator
{
  public abstract int getMsgId(SnmpMsg paramSnmpMsg);
  
  public abstract int getMsgMaxSize(SnmpMsg paramSnmpMsg);
  
  public abstract byte getMsgFlags(SnmpMsg paramSnmpMsg);
  
  public abstract int getMsgSecurityModel(SnmpMsg paramSnmpMsg);
  
  public abstract int getSecurityLevel(SnmpMsg paramSnmpMsg);
  
  public abstract byte[] getFlatSecurityParameters(SnmpMsg paramSnmpMsg);
  
  public abstract SnmpSecurityParameters getSecurityParameters(SnmpMsg paramSnmpMsg);
  
  public abstract byte[] getContextEngineId(SnmpMsg paramSnmpMsg);
  
  public abstract byte[] getContextName(SnmpMsg paramSnmpMsg);
  
  public abstract byte[] getRawContextName(SnmpMsg paramSnmpMsg);
  
  public abstract byte[] getAccessContext(SnmpMsg paramSnmpMsg);
  
  public abstract byte[] getEncryptedPdu(SnmpMsg paramSnmpMsg);
  
  public abstract void setContextName(SnmpMsg paramSnmpMsg, byte[] paramArrayOfByte);
  
  public abstract void setContextEngineId(SnmpMsg paramSnmpMsg, byte[] paramArrayOfByte);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\mpm\SnmpMsgTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
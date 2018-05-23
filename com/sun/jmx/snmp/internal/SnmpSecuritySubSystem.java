package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;

public abstract interface SnmpSecuritySubSystem
  extends SnmpSubSystem
{
  public abstract SnmpSecurityCache createSecurityCache(int paramInt)
    throws SnmpUnknownSecModelException;
  
  public abstract void releaseSecurityCache(int paramInt, SnmpSecurityCache paramSnmpSecurityCache)
    throws SnmpUnknownSecModelException;
  
  public abstract int generateRequestMsg(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, SnmpSecurityParameters paramSnmpSecurityParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4)
    throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
  
  public abstract int generateResponseMsg(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, SnmpSecurityParameters paramSnmpSecurityParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4)
    throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
  
  public abstract SnmpSecurityParameters processIncomingRequest(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, byte[] paramArrayOfByte5, SnmpDecryptedPdu paramSnmpDecryptedPdu)
    throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
  
  public abstract SnmpSecurityParameters processIncomingResponse(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, byte[] paramArrayOfByte5, SnmpDecryptedPdu paramSnmpDecryptedPdu)
    throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpSecuritySubSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface SnmpAccessControlModel
  extends SnmpModel
{
  public abstract void checkAccess(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, SnmpOid paramSnmpOid)
    throws SnmpStatusException;
  
  public abstract void checkPduAccess(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, SnmpPdu paramSnmpPdu)
    throws SnmpStatusException;
  
  public abstract boolean enableSnmpV1V2SetRequest();
  
  public abstract boolean disableSnmpV1V2SetRequest();
  
  public abstract boolean isSnmpV1V2SetRequestAuthorized();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\internal\SnmpAccessControlModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
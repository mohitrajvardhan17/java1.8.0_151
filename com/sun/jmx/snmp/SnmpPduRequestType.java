package com.sun.jmx.snmp;

public abstract interface SnmpPduRequestType
  extends SnmpAckPdu
{
  public abstract void setErrorIndex(int paramInt);
  
  public abstract void setErrorStatus(int paramInt);
  
  public abstract int getErrorIndex();
  
  public abstract int getErrorStatus();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduRequestType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
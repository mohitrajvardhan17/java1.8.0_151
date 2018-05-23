package com.sun.jmx.snmp;

public abstract interface SnmpPduBulkType
  extends SnmpAckPdu
{
  public abstract void setMaxRepetitions(int paramInt);
  
  public abstract void setNonRepeaters(int paramInt);
  
  public abstract int getMaxRepetitions();
  
  public abstract int getNonRepeaters();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduBulkType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
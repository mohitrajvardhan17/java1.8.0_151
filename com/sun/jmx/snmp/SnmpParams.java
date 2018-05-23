package com.sun.jmx.snmp;

public abstract class SnmpParams
  implements SnmpDefinitions
{
  private int protocolVersion = 0;
  
  SnmpParams(int paramInt)
  {
    protocolVersion = paramInt;
  }
  
  SnmpParams() {}
  
  public abstract boolean allowSnmpSets();
  
  public int getProtocolVersion()
  {
    return protocolVersion;
  }
  
  public void setProtocolVersion(int paramInt)
  {
    protocolVersion = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
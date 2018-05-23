package com.sun.jmx.snmp;

public class SnmpPduTrap
  extends SnmpPduPacket
{
  private static final long serialVersionUID = -3670886636491433011L;
  public SnmpOid enterprise;
  public SnmpIpAddress agentAddr;
  public int genericTrap;
  public int specificTrap;
  public long timeStamp;
  
  public SnmpPduTrap()
  {
    type = 164;
    version = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduTrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
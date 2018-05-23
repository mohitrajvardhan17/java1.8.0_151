package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpPduPacket
  extends SnmpPdu
  implements Serializable
{
  public byte[] community;
  
  public SnmpPduPacket() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
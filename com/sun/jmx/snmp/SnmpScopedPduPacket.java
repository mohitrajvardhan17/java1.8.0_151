package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpScopedPduPacket
  extends SnmpPdu
  implements Serializable
{
  public int msgMaxSize = 0;
  public int msgId = 0;
  public byte msgFlags = 0;
  public int msgSecurityModel = 0;
  public byte[] contextEngineId = null;
  public byte[] contextName = null;
  public SnmpSecurityParameters securityParameters = null;
  
  protected SnmpScopedPduPacket()
  {
    version = 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpScopedPduPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
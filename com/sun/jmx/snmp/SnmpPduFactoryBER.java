package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpPduFactoryBER
  implements SnmpPduFactory, Serializable
{
  private static final long serialVersionUID = -3525318344000547635L;
  
  public SnmpPduFactoryBER() {}
  
  public SnmpPdu decodeSnmpPdu(SnmpMsg paramSnmpMsg)
    throws SnmpStatusException
  {
    return paramSnmpMsg.decodeSnmpPdu();
  }
  
  public SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException
  {
    Object localObject;
    switch (version)
    {
    case 0: 
    case 1: 
      localObject = new SnmpMessage();
      ((SnmpMessage)localObject).encodeSnmpPdu((SnmpPduPacket)paramSnmpPdu, paramInt);
      return (SnmpMsg)localObject;
    case 3: 
      localObject = new SnmpV3Message();
      ((SnmpV3Message)localObject).encodeSnmpPdu(paramSnmpPdu, paramInt);
      return (SnmpMsg)localObject;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduFactoryBER.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
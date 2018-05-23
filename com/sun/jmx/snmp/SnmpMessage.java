package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpMessage
  extends SnmpMsg
  implements SnmpDefinitions
{
  public byte[] community;
  
  public SnmpMessage() {}
  
  public int encodeMessage(byte[] paramArrayOfByte)
    throws SnmpTooBigException
  {
    int i = 0;
    if (data == null) {
      throw new IllegalArgumentException("Data field is null");
    }
    try
    {
      BerEncoder localBerEncoder = new BerEncoder(paramArrayOfByte);
      localBerEncoder.openSequence();
      localBerEncoder.putAny(data, dataLength);
      localBerEncoder.putOctetString(community != null ? community : new byte[0]);
      localBerEncoder.putInteger(version);
      localBerEncoder.closeSequence();
      i = localBerEncoder.trim();
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SnmpTooBigException();
    }
    return i;
  }
  
  public int getRequestId(byte[] paramArrayOfByte)
    throws SnmpStatusException
  {
    int i = 0;
    BerDecoder localBerDecoder1 = null;
    BerDecoder localBerDecoder2 = null;
    byte[] arrayOfByte = null;
    try
    {
      localBerDecoder1 = new BerDecoder(paramArrayOfByte);
      localBerDecoder1.openSequence();
      localBerDecoder1.fetchInteger();
      localBerDecoder1.fetchOctetString();
      arrayOfByte = localBerDecoder1.fetchAny();
      localBerDecoder2 = new BerDecoder(arrayOfByte);
      int j = localBerDecoder2.getTag();
      localBerDecoder2.openSequence(j);
      i = localBerDecoder2.fetchInteger();
    }
    catch (BerException localBerException1)
    {
      throw new SnmpStatusException("Invalid encoding");
    }
    try
    {
      localBerDecoder1.closeSequence();
    }
    catch (BerException localBerException2) {}
    try
    {
      localBerDecoder2.closeSequence();
    }
    catch (BerException localBerException3) {}
    return i;
  }
  
  public void decodeMessage(byte[] paramArrayOfByte, int paramInt)
    throws SnmpStatusException
  {
    try
    {
      BerDecoder localBerDecoder = new BerDecoder(paramArrayOfByte);
      localBerDecoder.openSequence();
      version = localBerDecoder.fetchInteger();
      community = localBerDecoder.fetchOctetString();
      data = localBerDecoder.fetchAny();
      dataLength = data.length;
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException)
    {
      throw new SnmpStatusException("Invalid encoding");
    }
  }
  
  public void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException
  {
    SnmpPduPacket localSnmpPduPacket = (SnmpPduPacket)paramSnmpPdu;
    version = version;
    community = community;
    address = address;
    port = port;
    data = new byte[paramInt];
    try
    {
      BerEncoder localBerEncoder = new BerEncoder(data);
      localBerEncoder.openSequence();
      encodeVarBindList(localBerEncoder, varBindList);
      switch (type)
      {
      case 160: 
      case 161: 
      case 162: 
      case 163: 
      case 166: 
      case 167: 
      case 168: 
        SnmpPduRequest localSnmpPduRequest = (SnmpPduRequest)localSnmpPduPacket;
        localBerEncoder.putInteger(errorIndex);
        localBerEncoder.putInteger(errorStatus);
        localBerEncoder.putInteger(requestId);
        break;
      case 165: 
        SnmpPduBulk localSnmpPduBulk = (SnmpPduBulk)localSnmpPduPacket;
        localBerEncoder.putInteger(maxRepetitions);
        localBerEncoder.putInteger(nonRepeaters);
        localBerEncoder.putInteger(requestId);
        break;
      case 164: 
        SnmpPduTrap localSnmpPduTrap = (SnmpPduTrap)localSnmpPduPacket;
        localBerEncoder.putInteger(timeStamp, 67);
        localBerEncoder.putInteger(specificTrap);
        localBerEncoder.putInteger(genericTrap);
        if (agentAddr != null) {
          localBerEncoder.putOctetString(agentAddr.byteValue(), 64);
        } else {
          localBerEncoder.putOctetString(new byte[0], 64);
        }
        localBerEncoder.putOid(enterprise.longValue());
        break;
      default: 
        throw new SnmpStatusException("Invalid pdu type " + String.valueOf(type));
      }
      localBerEncoder.closeSequence(type);
      dataLength = localBerEncoder.trim();
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SnmpTooBigException();
    }
  }
  
  public SnmpPdu decodeSnmpPdu()
    throws SnmpStatusException
  {
    Object localObject = null;
    BerDecoder localBerDecoder = new BerDecoder(data);
    try
    {
      int i = localBerDecoder.getTag();
      localBerDecoder.openSequence(i);
      switch (i)
      {
      case 160: 
      case 161: 
      case 162: 
      case 163: 
      case 166: 
      case 167: 
      case 168: 
        SnmpPduRequest localSnmpPduRequest = new SnmpPduRequest();
        requestId = localBerDecoder.fetchInteger();
        errorStatus = localBerDecoder.fetchInteger();
        errorIndex = localBerDecoder.fetchInteger();
        localObject = localSnmpPduRequest;
        break;
      case 165: 
        SnmpPduBulk localSnmpPduBulk = new SnmpPduBulk();
        requestId = localBerDecoder.fetchInteger();
        nonRepeaters = localBerDecoder.fetchInteger();
        maxRepetitions = localBerDecoder.fetchInteger();
        localObject = localSnmpPduBulk;
        break;
      case 164: 
        SnmpPduTrap localSnmpPduTrap = new SnmpPduTrap();
        enterprise = new SnmpOid(localBerDecoder.fetchOid());
        byte[] arrayOfByte = localBerDecoder.fetchOctetString(64);
        if (arrayOfByte.length != 0) {
          agentAddr = new SnmpIpAddress(arrayOfByte);
        } else {
          agentAddr = null;
        }
        genericTrap = localBerDecoder.fetchInteger();
        specificTrap = localBerDecoder.fetchInteger();
        timeStamp = localBerDecoder.fetchInteger(67);
        localObject = localSnmpPduTrap;
        break;
      default: 
        throw new SnmpStatusException(9);
      }
      type = i;
      varBindList = decodeVarBindList(localBerDecoder);
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpMessage.class.getName(), "decodeSnmpPdu", "BerException", localBerException);
      }
      throw new SnmpStatusException(9);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpMessage.class.getName(), "decodeSnmpPdu", "IllegalArgumentException", localIllegalArgumentException);
      }
      throw new SnmpStatusException(9);
    }
    version = version;
    community = community;
    address = address;
    port = port;
    return (SnmpPdu)localObject;
  }
  
  public String printMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (community == null)
    {
      localStringBuffer.append("Community: null");
    }
    else
    {
      localStringBuffer.append("Community: {\n");
      localStringBuffer.append(dumpHexBuffer(community, 0, community.length));
      localStringBuffer.append("\n}\n");
    }
    return super.printMessage();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
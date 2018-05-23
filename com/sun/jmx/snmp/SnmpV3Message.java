package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnmpV3Message
  extends SnmpMsg
{
  public int msgId = 0;
  public int msgMaxSize = 0;
  public byte msgFlags = 0;
  public int msgSecurityModel = 0;
  public byte[] msgSecurityParameters = null;
  public byte[] contextEngineId = null;
  public byte[] contextName = null;
  public byte[] encryptedPdu = null;
  
  public SnmpV3Message() {}
  
  public int encodeMessage(byte[] paramArrayOfByte)
    throws SnmpTooBigException
  {
    int i = 0;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeMessage", "Can't encode directly V3Message! Need a SecuritySubSystem");
    }
    throw new IllegalArgumentException("Can't encode");
  }
  
  public void decodeMessage(byte[] paramArrayOfByte, int paramInt)
    throws SnmpStatusException
  {
    try
    {
      BerDecoder localBerDecoder = new BerDecoder(paramArrayOfByte);
      localBerDecoder.openSequence();
      version = localBerDecoder.fetchInteger();
      localBerDecoder.openSequence();
      msgId = localBerDecoder.fetchInteger();
      msgMaxSize = localBerDecoder.fetchInteger();
      msgFlags = localBerDecoder.fetchOctetString()[0];
      msgSecurityModel = localBerDecoder.fetchInteger();
      localBerDecoder.closeSequence();
      msgSecurityParameters = localBerDecoder.fetchOctetString();
      if ((msgFlags & 0x2) == 0)
      {
        localBerDecoder.openSequence();
        contextEngineId = localBerDecoder.fetchOctetString();
        contextName = localBerDecoder.fetchOctetString();
        data = localBerDecoder.fetchAny();
        dataLength = data.length;
        localBerDecoder.closeSequence();
      }
      else
      {
        encryptedPdu = localBerDecoder.fetchOctetString();
      }
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException)
    {
      localBerException.printStackTrace();
      throw new SnmpStatusException("Invalid encoding");
    }
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Unmarshalled message : \n").append("version : ").append(version).append("\n").append("msgId : ").append(msgId).append("\n").append("msgMaxSize : ").append(msgMaxSize).append("\n").append("msgFlags : ").append(msgFlags).append("\n").append("msgSecurityModel : ").append(msgSecurityModel).append("\n").append("contextEngineId : ").append(contextEngineId == null ? null : SnmpEngineId.createEngineId(contextEngineId)).append("\n").append("contextName : ").append(contextName).append("\n").append("data : ").append(data).append("\n").append("dat len : ").append(data == null ? 0 : data.length).append("\n").append("encryptedPdu : ").append(encryptedPdu).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeMessage", localStringBuilder.toString());
    }
  }
  
  public int getRequestId(byte[] paramArrayOfByte)
    throws SnmpStatusException
  {
    BerDecoder localBerDecoder = null;
    int i = 0;
    try
    {
      localBerDecoder = new BerDecoder(paramArrayOfByte);
      localBerDecoder.openSequence();
      localBerDecoder.fetchInteger();
      localBerDecoder.openSequence();
      i = localBerDecoder.fetchInteger();
    }
    catch (BerException localBerException1)
    {
      throw new SnmpStatusException("Invalid encoding");
    }
    try
    {
      localBerDecoder.closeSequence();
    }
    catch (BerException localBerException2) {}
    return i;
  }
  
  public void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt)
    throws SnmpStatusException, SnmpTooBigException
  {
    SnmpScopedPduPacket localSnmpScopedPduPacket = (SnmpScopedPduPacket)paramSnmpPdu;
    Object localObject;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
    {
      localObject = new StringBuilder().append("PDU to marshall: \n").append("security parameters : ").append(securityParameters).append("\n").append("type : ").append(type).append("\n").append("version : ").append(version).append("\n").append("requestId : ").append(requestId).append("\n").append("msgId : ").append(msgId).append("\n").append("msgMaxSize : ").append(msgMaxSize).append("\n").append("msgFlags : ").append(msgFlags).append("\n").append("msgSecurityModel : ").append(msgSecurityModel).append("\n").append("contextEngineId : ").append(contextEngineId).append("\n").append("contextName : ").append(contextName).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeSnmpPdu", ((StringBuilder)localObject).toString());
    }
    version = version;
    address = address;
    port = port;
    msgId = msgId;
    msgMaxSize = msgMaxSize;
    msgFlags = msgFlags;
    msgSecurityModel = msgSecurityModel;
    contextEngineId = contextEngineId;
    contextName = contextName;
    securityParameters = securityParameters;
    data = new byte[paramInt];
    try
    {
      localObject = new BerEncoder(data);
      ((BerEncoder)localObject).openSequence();
      encodeVarBindList((BerEncoder)localObject, varBindList);
      switch (type)
      {
      case 160: 
      case 161: 
      case 162: 
      case 163: 
      case 166: 
      case 167: 
      case 168: 
        SnmpPduRequestType localSnmpPduRequestType = (SnmpPduRequestType)localSnmpScopedPduPacket;
        ((BerEncoder)localObject).putInteger(localSnmpPduRequestType.getErrorIndex());
        ((BerEncoder)localObject).putInteger(localSnmpPduRequestType.getErrorStatus());
        ((BerEncoder)localObject).putInteger(requestId);
        break;
      case 165: 
        SnmpPduBulkType localSnmpPduBulkType = (SnmpPduBulkType)localSnmpScopedPduPacket;
        ((BerEncoder)localObject).putInteger(localSnmpPduBulkType.getMaxRepetitions());
        ((BerEncoder)localObject).putInteger(localSnmpPduBulkType.getNonRepeaters());
        ((BerEncoder)localObject).putInteger(requestId);
        break;
      case 164: 
      default: 
        throw new SnmpStatusException("Invalid pdu type " + String.valueOf(type));
      }
      ((BerEncoder)localObject).closeSequence(type);
      dataLength = ((BerEncoder)localObject).trim();
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
        SnmpScopedPduRequest localSnmpScopedPduRequest = new SnmpScopedPduRequest();
        requestId = localBerDecoder.fetchInteger();
        localSnmpScopedPduRequest.setErrorStatus(localBerDecoder.fetchInteger());
        localSnmpScopedPduRequest.setErrorIndex(localBerDecoder.fetchInteger());
        localObject = localSnmpScopedPduRequest;
        break;
      case 165: 
        SnmpScopedPduBulk localSnmpScopedPduBulk = new SnmpScopedPduBulk();
        requestId = localBerDecoder.fetchInteger();
        localSnmpScopedPduBulk.setNonRepeaters(localBerDecoder.fetchInteger());
        localSnmpScopedPduBulk.setMaxRepetitions(localBerDecoder.fetchInteger());
        localObject = localSnmpScopedPduBulk;
        break;
      case 164: 
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
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpV3Message.class.getName(), "decodeSnmpPdu", "BerException", localBerException);
      }
      throw new SnmpStatusException(9);
    }
    address = address;
    port = port;
    msgFlags = msgFlags;
    version = version;
    msgId = msgId;
    msgMaxSize = msgMaxSize;
    msgSecurityModel = msgSecurityModel;
    contextEngineId = contextEngineId;
    contextName = contextName;
    securityParameters = securityParameters;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Unmarshalled PDU : \n").append("type : ").append(type).append("\n").append("version : ").append(version).append("\n").append("requestId : ").append(requestId).append("\n").append("msgId : ").append(msgId).append("\n").append("msgMaxSize : ").append(msgMaxSize).append("\n").append("msgFlags : ").append(msgFlags).append("\n").append("msgSecurityModel : ").append(msgSecurityModel).append("\n").append("contextEngineId : ").append(contextEngineId).append("\n").append("contextName : ").append(contextName).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeSnmpPdu", localStringBuilder.toString());
    }
    return (SnmpPdu)localObject;
  }
  
  public String printMessage()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("msgId : " + msgId + "\n");
    localStringBuffer.append("msgMaxSize : " + msgMaxSize + "\n");
    localStringBuffer.append("msgFlags : " + msgFlags + "\n");
    localStringBuffer.append("msgSecurityModel : " + msgSecurityModel + "\n");
    if (contextEngineId == null)
    {
      localStringBuffer.append("contextEngineId : null");
    }
    else
    {
      localStringBuffer.append("contextEngineId : {\n");
      localStringBuffer.append(dumpHexBuffer(contextEngineId, 0, contextEngineId.length));
      localStringBuffer.append("\n}\n");
    }
    if (contextName == null)
    {
      localStringBuffer.append("contextName : null");
    }
    else
    {
      localStringBuffer.append("contextName : {\n");
      localStringBuffer.append(dumpHexBuffer(contextName, 0, contextName.length));
      localStringBuffer.append("\n}\n");
    }
    return super.printMessage();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpV3Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
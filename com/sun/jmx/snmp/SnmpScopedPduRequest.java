package com.sun.jmx.snmp;

public class SnmpScopedPduRequest
  extends SnmpScopedPduPacket
  implements SnmpPduRequestType
{
  private static final long serialVersionUID = 6463060973056773680L;
  int errorStatus = 0;
  int errorIndex = 0;
  
  public SnmpScopedPduRequest() {}
  
  public void setErrorIndex(int paramInt)
  {
    errorIndex = paramInt;
  }
  
  public void setErrorStatus(int paramInt)
  {
    errorStatus = paramInt;
  }
  
  public int getErrorIndex()
  {
    return errorIndex;
  }
  
  public int getErrorStatus()
  {
    return errorStatus;
  }
  
  public SnmpPdu getResponsePdu()
  {
    SnmpScopedPduRequest localSnmpScopedPduRequest = new SnmpScopedPduRequest();
    address = address;
    port = port;
    version = version;
    requestId = requestId;
    msgId = msgId;
    msgMaxSize = msgMaxSize;
    msgFlags = msgFlags;
    msgSecurityModel = msgSecurityModel;
    contextEngineId = contextEngineId;
    contextName = contextName;
    securityParameters = securityParameters;
    type = 162;
    errorStatus = 0;
    errorIndex = 0;
    return localSnmpScopedPduRequest;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpScopedPduRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
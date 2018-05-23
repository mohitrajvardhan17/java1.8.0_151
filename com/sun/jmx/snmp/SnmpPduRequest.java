package com.sun.jmx.snmp;

public class SnmpPduRequest
  extends SnmpPduPacket
  implements SnmpPduRequestType
{
  private static final long serialVersionUID = 2218754017025258979L;
  public int errorStatus = 0;
  public int errorIndex = 0;
  
  public SnmpPduRequest() {}
  
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
    SnmpPduRequest localSnmpPduRequest = new SnmpPduRequest();
    address = address;
    port = port;
    version = version;
    community = community;
    type = 162;
    requestId = requestId;
    errorStatus = 0;
    errorIndex = 0;
    return localSnmpPduRequest;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
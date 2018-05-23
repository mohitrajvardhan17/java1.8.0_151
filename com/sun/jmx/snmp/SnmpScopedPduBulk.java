package com.sun.jmx.snmp;

public class SnmpScopedPduBulk
  extends SnmpScopedPduPacket
  implements SnmpPduBulkType
{
  private static final long serialVersionUID = -1648623646227038885L;
  int nonRepeaters;
  int maxRepetitions;
  
  public SnmpScopedPduBulk()
  {
    type = 165;
    version = 3;
  }
  
  public void setMaxRepetitions(int paramInt)
  {
    maxRepetitions = paramInt;
  }
  
  public void setNonRepeaters(int paramInt)
  {
    nonRepeaters = paramInt;
  }
  
  public int getMaxRepetitions()
  {
    return maxRepetitions;
  }
  
  public int getNonRepeaters()
  {
    return nonRepeaters;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpScopedPduBulk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
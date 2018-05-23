package com.sun.jmx.snmp;

public class SnmpPduBulk
  extends SnmpPduPacket
  implements SnmpPduBulkType
{
  private static final long serialVersionUID = -7431306775883371046L;
  public int nonRepeaters;
  public int maxRepetitions;
  
  public SnmpPduBulk()
  {
    type = 165;
    version = 1;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPduBulk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpVarBindList;

public abstract interface SnmpInformHandler
  extends SnmpDefinitions
{
  public abstract void processSnmpPollData(SnmpInformRequest paramSnmpInformRequest, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList);
  
  public abstract void processSnmpPollTimeout(SnmpInformRequest paramSnmpInformRequest);
  
  public abstract void processSnmpInternalError(SnmpInformRequest paramSnmpInformRequest, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpInformHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
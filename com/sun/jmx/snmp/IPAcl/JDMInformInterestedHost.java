package com.sun.jmx.snmp.IPAcl;

class JDMInformInterestedHost
  extends SimpleNode
{
  JDMInformInterestedHost(int paramInt)
  {
    super(paramInt);
  }
  
  JDMInformInterestedHost(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMInformInterestedHost(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMInformInterestedHost(paramParser, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMInformInterestedHost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.jmx.snmp.IPAcl;

class JDMTrapInterestedHost
  extends SimpleNode
{
  JDMTrapInterestedHost(int paramInt)
  {
    super(paramInt);
  }
  
  JDMTrapInterestedHost(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMTrapInterestedHost(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMTrapInterestedHost(paramParser, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMTrapInterestedHost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
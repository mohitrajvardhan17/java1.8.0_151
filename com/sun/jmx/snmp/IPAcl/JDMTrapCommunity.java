package com.sun.jmx.snmp.IPAcl;

class JDMTrapCommunity
  extends SimpleNode
{
  protected String community = "";
  
  JDMTrapCommunity(int paramInt)
  {
    super(paramInt);
  }
  
  JDMTrapCommunity(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMTrapCommunity(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMTrapCommunity(paramParser, paramInt);
  }
  
  public String getCommunity()
  {
    return community;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMTrapCommunity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
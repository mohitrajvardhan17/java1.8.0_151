package com.sun.jmx.snmp.IPAcl;

class JDMInformCommunity
  extends SimpleNode
{
  protected String community = "";
  
  JDMInformCommunity(int paramInt)
  {
    super(paramInt);
  }
  
  JDMInformCommunity(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMInformCommunity(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMInformCommunity(paramParser, paramInt);
  }
  
  public String getCommunity()
  {
    return community;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMInformCommunity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
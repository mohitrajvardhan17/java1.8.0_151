package com.sun.jmx.snmp.IPAcl;

class JDMCommunity
  extends SimpleNode
{
  protected String communityString = "";
  
  JDMCommunity(int paramInt)
  {
    super(paramInt);
  }
  
  JDMCommunity(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMCommunity(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMCommunity(paramParser, paramInt);
  }
  
  public String getCommunity()
  {
    return communityString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMCommunity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.jmx.snmp.IPAcl;

class JDMInformItem
  extends SimpleNode
{
  protected JDMInformCommunity comm = null;
  
  JDMInformItem(int paramInt)
  {
    super(paramInt);
  }
  
  JDMInformItem(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMInformItem(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMInformItem(paramParser, paramInt);
  }
  
  public JDMInformCommunity getCommunity()
  {
    return comm;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMInformItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
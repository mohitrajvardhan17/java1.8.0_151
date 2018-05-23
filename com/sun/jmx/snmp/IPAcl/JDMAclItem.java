package com.sun.jmx.snmp.IPAcl;

class JDMAclItem
  extends SimpleNode
{
  protected JDMAccess access = null;
  protected JDMCommunities com = null;
  
  JDMAclItem(int paramInt)
  {
    super(paramInt);
  }
  
  JDMAclItem(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMAclItem(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMAclItem(paramParser, paramInt);
  }
  
  public JDMAccess getAccess()
  {
    return access;
  }
  
  public JDMCommunities getCommunities()
  {
    return com;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMAclItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
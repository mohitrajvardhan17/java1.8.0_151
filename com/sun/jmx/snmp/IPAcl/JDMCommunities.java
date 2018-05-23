package com.sun.jmx.snmp.IPAcl;

class JDMCommunities
  extends SimpleNode
{
  JDMCommunities(int paramInt)
  {
    super(paramInt);
  }
  
  JDMCommunities(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMCommunities(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMCommunities(paramParser, paramInt);
  }
  
  public void buildCommunities(AclEntryImpl paramAclEntryImpl)
  {
    for (int i = 0; i < children.length; i++) {
      paramAclEntryImpl.addCommunity(((JDMCommunity)children[i]).getCommunity());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMCommunities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
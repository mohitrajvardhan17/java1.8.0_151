package com.sun.jmx.snmp.IPAcl;

class JDMAccess
  extends SimpleNode
{
  protected int access = -1;
  
  JDMAccess(int paramInt)
  {
    super(paramInt);
  }
  
  JDMAccess(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMAccess(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMAccess(paramParser, paramInt);
  }
  
  protected void putPermission(AclEntryImpl paramAclEntryImpl)
  {
    if (access == 17) {
      paramAclEntryImpl.addPermission(SnmpAcl.getREAD());
    }
    if (access == 18)
    {
      paramAclEntryImpl.addPermission(SnmpAcl.getREAD());
      paramAclEntryImpl.addPermission(SnmpAcl.getWRITE());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
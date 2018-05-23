package com.sun.jmx.snmp.IPAcl;

class JDMSecurityDefs
  extends SimpleNode
{
  JDMSecurityDefs(int paramInt)
  {
    super(paramInt);
  }
  
  JDMSecurityDefs(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMSecurityDefs(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMSecurityDefs(paramParser, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMSecurityDefs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
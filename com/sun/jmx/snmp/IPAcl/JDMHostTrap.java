package com.sun.jmx.snmp.IPAcl;

class JDMHostTrap
  extends SimpleNode
{
  protected String name = "";
  
  JDMHostTrap(int paramInt)
  {
    super(paramInt);
  }
  
  JDMHostTrap(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMHostTrap(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMHostTrap(paramParser, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMHostTrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
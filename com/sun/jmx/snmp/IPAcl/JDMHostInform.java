package com.sun.jmx.snmp.IPAcl;

class JDMHostInform
  extends SimpleNode
{
  protected String name = "";
  
  JDMHostInform(int paramInt)
  {
    super(paramInt);
  }
  
  JDMHostInform(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMHostInform(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMHostInform(paramParser, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMHostInform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
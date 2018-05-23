package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMIpMask
  extends Host
{
  private static final long serialVersionUID = -8211312690652331386L;
  protected StringBuffer address = new StringBuffer();
  
  JDMIpMask(int paramInt)
  {
    super(paramInt);
  }
  
  JDMIpMask(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMIpMask(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMIpMask(paramParser, paramInt);
  }
  
  protected String getHname()
  {
    return address.toString();
  }
  
  protected PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException
  {
    return new GroupImpl(address.toString());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMIpMask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
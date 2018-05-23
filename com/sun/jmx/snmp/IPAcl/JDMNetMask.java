package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMNetMask
  extends Host
{
  private static final long serialVersionUID = -1979318280250821787L;
  protected StringBuffer address = new StringBuffer();
  protected String mask = null;
  
  public JDMNetMask(int paramInt)
  {
    super(paramInt);
  }
  
  public JDMNetMask(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  public static Node jjtCreate(int paramInt)
  {
    return new JDMNetMask(paramInt);
  }
  
  public static Node jjtCreate(Parser paramParser, int paramInt)
  {
    return new JDMNetMask(paramParser, paramInt);
  }
  
  protected String getHname()
  {
    return address.toString();
  }
  
  protected PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException
  {
    return new NetMaskImpl(address.toString(), Integer.parseInt(mask));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMNetMask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMNetMaskV6
  extends JDMNetMask
{
  private static final long serialVersionUID = 4505256777680576645L;
  
  public JDMNetMaskV6(int paramInt)
  {
    super(paramInt);
  }
  
  public JDMNetMaskV6(Parser paramParser, int paramInt)
  {
    super(paramParser, paramInt);
  }
  
  protected PrincipalImpl createAssociatedPrincipal()
    throws UnknownHostException
  {
    return new NetMaskImpl(address.toString(), Integer.parseInt(mask));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMNetMaskV6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
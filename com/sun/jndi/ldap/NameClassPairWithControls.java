package com.sun.jndi.ldap;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

class NameClassPairWithControls
  extends NameClassPair
  implements HasControls
{
  private Control[] controls;
  private static final long serialVersionUID = 2010738921219112944L;
  
  public NameClassPairWithControls(String paramString1, String paramString2, Control[] paramArrayOfControl)
  {
    super(paramString1, paramString2);
    controls = paramArrayOfControl;
  }
  
  public Control[] getControls()
    throws NamingException
  {
    return controls;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\NameClassPairWithControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
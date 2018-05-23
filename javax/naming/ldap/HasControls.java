package javax.naming.ldap;

import javax.naming.NamingException;

public abstract interface HasControls
{
  public abstract Control[] getControls()
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\HasControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
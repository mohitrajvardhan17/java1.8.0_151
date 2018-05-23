package javax.naming.ldap;

import javax.naming.NamingException;

public abstract interface UnsolicitedNotification
  extends ExtendedResponse, HasControls
{
  public abstract String[] getReferrals();
  
  public abstract NamingException getException();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\UnsolicitedNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
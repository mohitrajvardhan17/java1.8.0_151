package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public abstract interface LdapContext
  extends DirContext
{
  public static final String CONTROL_FACTORIES = "java.naming.factory.control";
  
  public abstract ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest)
    throws NamingException;
  
  public abstract LdapContext newInstance(Control[] paramArrayOfControl)
    throws NamingException;
  
  public abstract void reconnect(Control[] paramArrayOfControl)
    throws NamingException;
  
  public abstract Control[] getConnectControls()
    throws NamingException;
  
  public abstract void setRequestControls(Control[] paramArrayOfControl)
    throws NamingException;
  
  public abstract Control[] getRequestControls()
    throws NamingException;
  
  public abstract Control[] getResponseControls()
    throws NamingException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\LdapContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
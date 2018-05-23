package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public final class SunProvider
  extends Provider
{
  private static final long serialVersionUID = -238911724858694198L;
  private static final String INFO = "Sun (Kerberos v5, SPNEGO)";
  public static final SunProvider INSTANCE = new SunProvider();
  
  public SunProvider()
  {
    super("SunJGSS", 1.8D, "Sun (Kerberos v5, SPNEGO)");
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        put("GssApiMechanism.1.2.840.113554.1.2.2", "sun.security.jgss.krb5.Krb5MechFactory");
        put("GssApiMechanism.1.3.6.1.5.5.2", "sun.security.jgss.spnego.SpNegoMechFactory");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\SunProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
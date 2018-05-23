package sun.security.jgss.krb5;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import javax.security.auth.DestroyFailedException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;

public class Krb5AcceptCredential
  implements Krb5CredElement
{
  private final Krb5NameElement name;
  private final ServiceCreds screds;
  
  private Krb5AcceptCredential(Krb5NameElement paramKrb5NameElement, ServiceCreds paramServiceCreds)
  {
    name = paramKrb5NameElement;
    screds = paramServiceCreds;
  }
  
  static Krb5AcceptCredential getInstance(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement)
    throws GSSException
  {
    final String str1 = paramKrb5NameElement == null ? null : paramKrb5NameElement.getKrb5PrincipalName().getName();
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    ServiceCreds localServiceCreds = null;
    try
    {
      localServiceCreds = (ServiceCreds)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public ServiceCreds run()
          throws Exception
        {
          return Krb5Util.getServiceCreds(val$caller == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_ACCEPT : val$caller, str1, localAccessControlContext);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      GSSException localGSSException = new GSSException(13, -1, "Attempt to obtain new ACCEPT credentials failed!");
      localGSSException.initCause(localPrivilegedActionException.getException());
      throw localGSSException;
    }
    if (localServiceCreds == null) {
      throw new GSSException(13, -1, "Failed to find any Kerberos credentails");
    }
    if (paramKrb5NameElement == null)
    {
      String str2 = localServiceCreds.getName();
      if (str2 != null) {
        paramKrb5NameElement = Krb5NameElement.getInstance(str2, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
      }
    }
    return new Krb5AcceptCredential(paramKrb5NameElement, localServiceCreds);
  }
  
  public final GSSNameSpi getName()
    throws GSSException
  {
    return name;
  }
  
  public int getInitLifetime()
    throws GSSException
  {
    return 0;
  }
  
  public int getAcceptLifetime()
    throws GSSException
  {
    return Integer.MAX_VALUE;
  }
  
  public boolean isInitiatorCredential()
    throws GSSException
  {
    return false;
  }
  
  public boolean isAcceptorCredential()
    throws GSSException
  {
    return true;
  }
  
  public final Oid getMechanism()
  {
    return Krb5MechFactory.GSS_KRB5_MECH_OID;
  }
  
  public final Provider getProvider()
  {
    return Krb5MechFactory.PROVIDER;
  }
  
  public EncryptionKey[] getKrb5EncryptionKeys(PrincipalName paramPrincipalName)
  {
    return screds.getEKeys(paramPrincipalName);
  }
  
  public void dispose()
    throws GSSException
  {
    try
    {
      destroy();
    }
    catch (DestroyFailedException localDestroyFailedException)
    {
      GSSException localGSSException = new GSSException(11, -1, "Could not destroy credentials - " + localDestroyFailedException.getMessage());
      localGSSException.initCause(localDestroyFailedException);
    }
  }
  
  public void destroy()
    throws DestroyFailedException
  {
    screds.destroy();
  }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    Credentials localCredentials = screds.getInitCred();
    if (localCredentials != null) {
      return Krb5InitCredential.getInstance(name, localCredentials).impersonate(paramGSSNameSpi);
    }
    throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\Krb5AcceptCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
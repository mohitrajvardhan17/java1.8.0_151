package sun.security.jgss.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public class Krb5InitCredential
  extends KerberosTicket
  implements Krb5CredElement
{
  private static final long serialVersionUID = 7723415700837898232L;
  private Krb5NameElement name;
  private Credentials krb5Credentials;
  
  private Krb5InitCredential(Krb5NameElement paramKrb5NameElement, byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
    throws GSSException
  {
    super(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
    name = paramKrb5NameElement;
    try
    {
      krb5Credentials = new Credentials(paramArrayOfByte1, paramKerberosPrincipal1.getName(), paramKerberosPrincipal2.getName(), paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
    }
    catch (KrbException localKrbException)
    {
      throw new GSSException(13, -1, localKrbException.getMessage());
    }
    catch (IOException localIOException)
    {
      throw new GSSException(13, -1, localIOException.getMessage());
    }
  }
  
  private Krb5InitCredential(Krb5NameElement paramKrb5NameElement, Credentials paramCredentials, byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
    throws GSSException
  {
    super(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
    name = paramKrb5NameElement;
    krb5Credentials = paramCredentials;
  }
  
  static Krb5InitCredential getInstance(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, int paramInt)
    throws GSSException
  {
    KerberosTicket localKerberosTicket = getTgt(paramGSSCaller, paramKrb5NameElement, paramInt);
    if (localKerberosTicket == null) {
      throw new GSSException(13, -1, "Failed to find any Kerberos tgt");
    }
    if (paramKrb5NameElement == null)
    {
      String str = localKerberosTicket.getClient().getName();
      paramKrb5NameElement = Krb5NameElement.getInstance(str, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
    }
    return new Krb5InitCredential(paramKrb5NameElement, localKerberosTicket.getEncoded(), localKerberosTicket.getClient(), localKerberosTicket.getServer(), localKerberosTicket.getSessionKey().getEncoded(), localKerberosTicket.getSessionKeyType(), localKerberosTicket.getFlags(), localKerberosTicket.getAuthTime(), localKerberosTicket.getStartTime(), localKerberosTicket.getEndTime(), localKerberosTicket.getRenewTill(), localKerberosTicket.getClientAddresses());
  }
  
  static Krb5InitCredential getInstance(Krb5NameElement paramKrb5NameElement, Credentials paramCredentials)
    throws GSSException
  {
    EncryptionKey localEncryptionKey = paramCredentials.getSessionKey();
    PrincipalName localPrincipalName1 = paramCredentials.getClient();
    PrincipalName localPrincipalName2 = paramCredentials.getServer();
    KerberosPrincipal localKerberosPrincipal1 = null;
    KerberosPrincipal localKerberosPrincipal2 = null;
    Krb5NameElement localKrb5NameElement = null;
    if (localPrincipalName1 != null)
    {
      String str = localPrincipalName1.getName();
      localKrb5NameElement = Krb5NameElement.getInstance(str, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
      localKerberosPrincipal1 = new KerberosPrincipal(str);
    }
    if (localPrincipalName2 != null) {
      localKerberosPrincipal2 = new KerberosPrincipal(localPrincipalName2.getName(), 2);
    }
    return new Krb5InitCredential(localKrb5NameElement, paramCredentials, paramCredentials.getEncoded(), localKerberosPrincipal1, localKerberosPrincipal2, localEncryptionKey.getBytes(), localEncryptionKey.getEType(), paramCredentials.getFlags(), paramCredentials.getAuthTime(), paramCredentials.getStartTime(), paramCredentials.getEndTime(), paramCredentials.getRenewTill(), paramCredentials.getClientAddresses());
  }
  
  public final GSSNameSpi getName()
    throws GSSException
  {
    return name;
  }
  
  public int getInitLifetime()
    throws GSSException
  {
    int i = 0;
    i = (int)(getEndTime().getTime() - new Date().getTime());
    return i / 1000;
  }
  
  public int getAcceptLifetime()
    throws GSSException
  {
    return 0;
  }
  
  public boolean isInitiatorCredential()
    throws GSSException
  {
    return true;
  }
  
  public boolean isAcceptorCredential()
    throws GSSException
  {
    return false;
  }
  
  public final Oid getMechanism()
  {
    return Krb5MechFactory.GSS_KRB5_MECH_OID;
  }
  
  public final Provider getProvider()
  {
    return Krb5MechFactory.PROVIDER;
  }
  
  Credentials getKrb5Credentials()
  {
    return krb5Credentials;
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
  
  private static KerberosTicket getTgt(GSSCaller paramGSSCaller, Krb5NameElement paramKrb5NameElement, int paramInt)
    throws GSSException
  {
    final String str;
    if (paramKrb5NameElement != null) {
      str = paramKrb5NameElement.getKrb5PrincipalName().getName();
    } else {
      str = null;
    }
    final AccessControlContext localAccessControlContext = AccessController.getContext();
    try
    {
      GSSCaller localGSSCaller = paramGSSCaller == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_INITIATE : paramGSSCaller;
      (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public KerberosTicket run()
          throws Exception
        {
          return Krb5Util.getTicket(val$realCaller, str, null, localAccessControlContext);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      GSSException localGSSException = new GSSException(13, -1, "Attempt to obtain new INITIATE credentials failed! (" + localPrivilegedActionException.getMessage() + ")");
      localGSSException.initCause(localPrivilegedActionException.getException());
      throw localGSSException;
    }
  }
  
  public GSSCredentialSpi impersonate(GSSNameSpi paramGSSNameSpi)
    throws GSSException
  {
    try
    {
      Krb5NameElement localKrb5NameElement = (Krb5NameElement)paramGSSNameSpi;
      localObject = Credentials.acquireS4U2selfCreds(localKrb5NameElement.getKrb5PrincipalName(), krb5Credentials);
      return new Krb5ProxyCredential(this, localKrb5NameElement, ((Credentials)localObject).getTicket());
    }
    catch (IOException|KrbException localIOException)
    {
      Object localObject = new GSSException(11, -1, "Attempt to obtain S4U2self credentials failed!");
      ((GSSException)localObject).initCause(localIOException);
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\Krb5InitCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
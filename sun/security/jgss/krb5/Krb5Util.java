package sun.security.jgss.krb5;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginException;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.JavaxSecurityAuthKerberosAccess;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public class Krb5Util
{
  static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug"))).booleanValue();
  
  private Krb5Util() {}
  
  public static KerberosTicket getTicketFromSubjectAndTgs(GSSCaller paramGSSCaller, String paramString1, String paramString2, String paramString3, AccessControlContext paramAccessControlContext)
    throws LoginException, KrbException, IOException
  {
    Subject localSubject1 = Subject.getSubject(paramAccessControlContext);
    KerberosTicket localKerberosTicket1 = (KerberosTicket)SubjectComber.find(localSubject1, paramString2, paramString1, KerberosTicket.class);
    if (localKerberosTicket1 != null) {
      return localKerberosTicket1;
    }
    Subject localSubject2 = null;
    if (!GSSUtil.useSubjectCredsOnly(paramGSSCaller)) {
      try
      {
        localSubject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
        localKerberosTicket1 = (KerberosTicket)SubjectComber.find(localSubject2, paramString2, paramString1, KerberosTicket.class);
        if (localKerberosTicket1 != null) {
          return localKerberosTicket1;
        }
      }
      catch (LoginException localLoginException) {}
    }
    KerberosTicket localKerberosTicket2 = (KerberosTicket)SubjectComber.find(localSubject1, paramString3, paramString1, KerberosTicket.class);
    int i;
    if ((localKerberosTicket2 == null) && (localSubject2 != null))
    {
      localKerberosTicket2 = (KerberosTicket)SubjectComber.find(localSubject2, paramString3, paramString1, KerberosTicket.class);
      i = 0;
    }
    else
    {
      i = 1;
    }
    if (localKerberosTicket2 != null)
    {
      Credentials localCredentials1 = ticketToCreds(localKerberosTicket2);
      Credentials localCredentials2 = Credentials.acquireServiceCreds(paramString2, localCredentials1);
      if (localCredentials2 != null)
      {
        localKerberosTicket1 = credsToTicket(localCredentials2);
        if ((i != 0) && (localSubject1 != null) && (!localSubject1.isReadOnly())) {
          localSubject1.getPrivateCredentials().add(localKerberosTicket1);
        }
      }
    }
    return localKerberosTicket1;
  }
  
  static KerberosTicket getTicket(GSSCaller paramGSSCaller, String paramString1, String paramString2, AccessControlContext paramAccessControlContext)
    throws LoginException
  {
    Subject localSubject1 = Subject.getSubject(paramAccessControlContext);
    KerberosTicket localKerberosTicket = (KerberosTicket)SubjectComber.find(localSubject1, paramString2, paramString1, KerberosTicket.class);
    if ((localKerberosTicket == null) && (!GSSUtil.useSubjectCredsOnly(paramGSSCaller)))
    {
      Subject localSubject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
      localKerberosTicket = (KerberosTicket)SubjectComber.find(localSubject2, paramString2, paramString1, KerberosTicket.class);
    }
    return localKerberosTicket;
  }
  
  public static Subject getSubject(GSSCaller paramGSSCaller, AccessControlContext paramAccessControlContext)
    throws LoginException
  {
    Subject localSubject = Subject.getSubject(paramAccessControlContext);
    if ((localSubject == null) && (!GSSUtil.useSubjectCredsOnly(paramGSSCaller))) {
      localSubject = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
    }
    return localSubject;
  }
  
  public static ServiceCreds getServiceCreds(GSSCaller paramGSSCaller, String paramString, AccessControlContext paramAccessControlContext)
    throws LoginException
  {
    Subject localSubject1 = Subject.getSubject(paramAccessControlContext);
    ServiceCreds localServiceCreds = null;
    if (localSubject1 != null) {
      localServiceCreds = ServiceCreds.getInstance(localSubject1, paramString);
    }
    if ((localServiceCreds == null) && (!GSSUtil.useSubjectCredsOnly(paramGSSCaller)))
    {
      Subject localSubject2 = GSSUtil.login(paramGSSCaller, GSSUtil.GSS_KRB5_MECH_OID);
      localServiceCreds = ServiceCreds.getInstance(localSubject2, paramString);
    }
    return localServiceCreds;
  }
  
  public static KerberosTicket credsToTicket(Credentials paramCredentials)
  {
    EncryptionKey localEncryptionKey = paramCredentials.getSessionKey();
    return new KerberosTicket(paramCredentials.getEncoded(), new KerberosPrincipal(paramCredentials.getClient().getName()), new KerberosPrincipal(paramCredentials.getServer().getName(), 2), localEncryptionKey.getBytes(), localEncryptionKey.getEType(), paramCredentials.getFlags(), paramCredentials.getAuthTime(), paramCredentials.getStartTime(), paramCredentials.getEndTime(), paramCredentials.getRenewTill(), paramCredentials.getClientAddresses());
  }
  
  public static Credentials ticketToCreds(KerberosTicket paramKerberosTicket)
    throws KrbException, IOException
  {
    return new Credentials(paramKerberosTicket.getEncoded(), paramKerberosTicket.getClient().getName(), paramKerberosTicket.getServer().getName(), paramKerberosTicket.getSessionKey().getEncoded(), paramKerberosTicket.getSessionKeyType(), paramKerberosTicket.getFlags(), paramKerberosTicket.getAuthTime(), paramKerberosTicket.getStartTime(), paramKerberosTicket.getEndTime(), paramKerberosTicket.getRenewTill(), paramKerberosTicket.getClientAddresses());
  }
  
  public static sun.security.krb5.internal.ktab.KeyTab snapshotFromJavaxKeyTab(javax.security.auth.kerberos.KeyTab paramKeyTab)
  {
    return KerberosSecrets.getJavaxSecurityAuthKerberosAccess().keyTabTakeSnapshot(paramKeyTab);
  }
  
  public static EncryptionKey[] keysFromJavaxKeyTab(javax.security.auth.kerberos.KeyTab paramKeyTab, PrincipalName paramPrincipalName)
  {
    return snapshotFromJavaxKeyTab(paramKeyTab).readServiceKeys(paramPrincipalName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\Krb5Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
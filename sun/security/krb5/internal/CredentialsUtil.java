package sun.security.krb5.internal;

import java.io.IOException;
import java.io.PrintStream;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbException;
import sun.security.krb5.KrbTgsReq;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;

public class CredentialsUtil
{
  private static boolean DEBUG = Krb5.DEBUG;
  
  public CredentialsUtil() {}
  
  public static Credentials acquireS4U2selfCreds(PrincipalName paramPrincipalName, Credentials paramCredentials)
    throws KrbException, IOException
  {
    String str1 = paramPrincipalName.getRealmString();
    String str2 = paramCredentials.getClient().getRealmString();
    if (!str1.equals(str2)) {
      throw new KrbException("Cross realm impersonation not supported");
    }
    if (!paramCredentials.isForwardable()) {
      throw new KrbException("S4U2self needs a FORWARDABLE ticket");
    }
    KrbTgsReq localKrbTgsReq = new KrbTgsReq(paramCredentials, paramCredentials.getClient(), new PAData(129, new PAForUserEnc(paramPrincipalName, paramCredentials.getSessionKey()).asn1Encode()));
    Credentials localCredentials = localKrbTgsReq.sendAndGetCreds();
    if (!localCredentials.getClient().equals(paramPrincipalName)) {
      throw new KrbException("S4U2self request not honored by KDC");
    }
    if (!localCredentials.isForwardable()) {
      throw new KrbException("S4U2self ticket must be FORWARDABLE");
    }
    return localCredentials;
  }
  
  public static Credentials acquireS4U2proxyCreds(String paramString, Ticket paramTicket, PrincipalName paramPrincipalName, Credentials paramCredentials)
    throws KrbException, IOException
  {
    KrbTgsReq localKrbTgsReq = new KrbTgsReq(paramCredentials, paramTicket, new PrincipalName(paramString));
    Credentials localCredentials = localKrbTgsReq.sendAndGetCreds();
    if (!localCredentials.getClient().equals(paramPrincipalName)) {
      throw new KrbException("S4U2proxy request not honored by KDC");
    }
    return localCredentials;
  }
  
  public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials)
    throws KrbException, IOException
  {
    PrincipalName localPrincipalName = new PrincipalName(paramString);
    String str1 = localPrincipalName.getRealmString();
    String str2 = paramCredentials.getClient().getRealmString();
    if (str2.equals(str1))
    {
      if (DEBUG) {
        System.out.println(">>> Credentials acquireServiceCreds: same realm");
      }
      return serviceCreds(localPrincipalName, paramCredentials);
    }
    Credentials localCredentials1 = null;
    boolean[] arrayOfBoolean = new boolean[1];
    Credentials localCredentials2 = getTGTforRealm(str2, str1, paramCredentials, arrayOfBoolean);
    if (localCredentials2 != null)
    {
      if (DEBUG)
      {
        System.out.println(">>> Credentials acquireServiceCreds: got right tgt");
        System.out.println(">>> Credentials acquireServiceCreds: obtaining service creds for " + localPrincipalName);
      }
      try
      {
        localCredentials1 = serviceCreds(localPrincipalName, localCredentials2);
      }
      catch (Exception localException)
      {
        if (DEBUG) {
          System.out.println(localException);
        }
        localCredentials1 = null;
      }
    }
    if (localCredentials1 != null)
    {
      if (DEBUG)
      {
        System.out.println(">>> Credentials acquireServiceCreds: returning creds:");
        Credentials.printDebug(localCredentials1);
      }
      if (arrayOfBoolean[0] == 0) {
        localCredentials1.resetDelegate();
      }
      return localCredentials1;
    }
    throw new KrbApErrException(63, "No service creds");
  }
  
  private static Credentials getTGTforRealm(String paramString1, String paramString2, Credentials paramCredentials, boolean[] paramArrayOfBoolean)
    throws KrbException
  {
    String[] arrayOfString = Realm.getRealmsList(paramString1, paramString2);
    int i = 0;
    int j = 0;
    Object localObject = null;
    Credentials localCredentials1 = null;
    Credentials localCredentials2 = null;
    PrincipalName localPrincipalName = null;
    String str = null;
    paramArrayOfBoolean[0] = true;
    localObject = paramCredentials;
    i = 0;
    while (i < arrayOfString.length)
    {
      localPrincipalName = PrincipalName.tgsService(paramString2, arrayOfString[i]);
      if (DEBUG) {
        System.out.println(">>> Credentials acquireServiceCreds: main loop: [" + i + "] tempService=" + localPrincipalName);
      }
      try
      {
        localCredentials1 = serviceCreds(localPrincipalName, (Credentials)localObject);
      }
      catch (Exception localException1)
      {
        localCredentials1 = null;
      }
      if (localCredentials1 == null)
      {
        if (DEBUG) {
          System.out.println(">>> Credentials acquireServiceCreds: no tgt; searching thru capath");
        }
        localCredentials1 = null;
        for (j = i + 1; (localCredentials1 == null) && (j < arrayOfString.length); j++)
        {
          localPrincipalName = PrincipalName.tgsService(arrayOfString[j], arrayOfString[i]);
          if (DEBUG) {
            System.out.println(">>> Credentials acquireServiceCreds: inner loop: [" + j + "] tempService=" + localPrincipalName);
          }
          try
          {
            localCredentials1 = serviceCreds(localPrincipalName, (Credentials)localObject);
          }
          catch (Exception localException2)
          {
            localCredentials1 = null;
          }
        }
      }
      if (localCredentials1 == null)
      {
        if (!DEBUG) {
          break;
        }
        System.out.println(">>> Credentials acquireServiceCreds: no tgt; cannot get creds");
        break;
      }
      str = localCredentials1.getServer().getInstanceComponent();
      if ((paramArrayOfBoolean[0] != 0) && (!localCredentials1.checkDelegate()))
      {
        if (DEBUG) {
          System.out.println(">>> Credentials acquireServiceCreds: global OK-AS-DELEGATE turned off at " + localCredentials1.getServer());
        }
        paramArrayOfBoolean[0] = false;
      }
      if (DEBUG) {
        System.out.println(">>> Credentials acquireServiceCreds: got tgt");
      }
      if (str.equals(paramString2))
      {
        localCredentials2 = localCredentials1;
        break;
      }
      for (j = i + 1; (j < arrayOfString.length) && (!str.equals(arrayOfString[j])); j++) {}
      if (j >= arrayOfString.length) {
        break;
      }
      i = j;
      localObject = localCredentials1;
      if (DEBUG) {
        System.out.println(">>> Credentials acquireServiceCreds: continuing with main loop counter reset to " + i);
      }
    }
    return localCredentials2;
  }
  
  private static Credentials serviceCreds(PrincipalName paramPrincipalName, Credentials paramCredentials)
    throws KrbException, IOException
  {
    return new KrbTgsReq(paramCredentials, paramPrincipalName).sendAndGetCreds();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\CredentialsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
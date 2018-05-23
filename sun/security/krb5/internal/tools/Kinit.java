package sun.security.krb5.internal.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.Config;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ccache.Credentials;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.util.Password;

public class Kinit
{
  private KinitOptions options;
  private static final boolean DEBUG = Krb5.DEBUG;
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      Kinit localKinit = new Kinit(paramArrayOfString);
    }
    catch (Exception localException)
    {
      String str = null;
      if ((localException instanceof KrbException)) {
        str = ((KrbException)localException).krbErrorMessage() + " " + ((KrbException)localException).returnCodeMessage();
      } else {
        str = localException.getMessage();
      }
      if (str != null) {
        System.err.println("Exception: " + str);
      } else {
        System.out.println("Exception: " + localException);
      }
      localException.printStackTrace();
      System.exit(-1);
    }
  }
  
  private Kinit(String[] paramArrayOfString)
    throws IOException, RealmException, KrbException
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      options = new KinitOptions();
    } else {
      options = new KinitOptions(paramArrayOfString);
    }
    String str1 = null;
    PrincipalName localPrincipalName1 = options.getPrincipal();
    if (localPrincipalName1 != null) {
      str1 = localPrincipalName1.toString();
    }
    if (DEBUG) {
      System.out.println("Principal is " + localPrincipalName1);
    }
    char[] arrayOfChar = options.password;
    boolean bool = options.useKeytabFile();
    KrbAsReqBuilder localKrbAsReqBuilder;
    if (!bool)
    {
      if (str1 == null) {
        throw new IllegalArgumentException(" Can not obtain principal name");
      }
      if (arrayOfChar == null)
      {
        System.out.print("Password for " + str1 + ":");
        System.out.flush();
        arrayOfChar = Password.readPassword(System.in);
        if (DEBUG) {
          System.out.println(">>> Kinit console input " + new String(arrayOfChar));
        }
      }
      localKrbAsReqBuilder = new KrbAsReqBuilder(localPrincipalName1, arrayOfChar);
    }
    else
    {
      if (DEBUG) {
        System.out.println(">>> Kinit using keytab");
      }
      if (str1 == null) {
        throw new IllegalArgumentException("Principal name must be specified.");
      }
      localObject = options.keytabFileName();
      if ((localObject != null) && (DEBUG)) {
        System.out.println(">>> Kinit keytab file name: " + (String)localObject);
      }
      localKrbAsReqBuilder = new KrbAsReqBuilder(localPrincipalName1, localObject == null ? KeyTab.getInstance() : KeyTab.getInstance(new File((String)localObject)));
    }
    Object localObject = new KDCOptions();
    setOptions(1, options.forwardable, (KDCOptions)localObject);
    setOptions(3, options.proxiable, (KDCOptions)localObject);
    localKrbAsReqBuilder.setOptions((KDCOptions)localObject);
    String str2 = options.getKDCRealm();
    if (str2 == null) {
      str2 = Config.getInstance().getDefaultRealm();
    }
    if (DEBUG) {
      System.out.println(">>> Kinit realm name is " + str2);
    }
    PrincipalName localPrincipalName2 = PrincipalName.tgsService(str2, str2);
    localKrbAsReqBuilder.setTarget(localPrincipalName2);
    if (DEBUG) {
      System.out.println(">>> Creating KrbAsReq");
    }
    if (options.getAddressOption()) {
      localKrbAsReqBuilder.setAddresses(HostAddresses.getLocalAddresses());
    }
    localKrbAsReqBuilder.action();
    Credentials localCredentials = localKrbAsReqBuilder.getCCreds();
    localKrbAsReqBuilder.destroy();
    CredentialsCache localCredentialsCache = CredentialsCache.create(localPrincipalName1, options.cachename);
    if (localCredentialsCache == null) {
      throw new IOException("Unable to create the cache file " + options.cachename);
    }
    localCredentialsCache.update(localCredentials);
    localCredentialsCache.save();
    if (options.password == null) {
      System.out.println("New ticket is stored in cache file " + options.cachename);
    } else {
      Arrays.fill(options.password, '0');
    }
    if (arrayOfChar != null) {
      Arrays.fill(arrayOfChar, '0');
    }
    options = null;
  }
  
  private static void setOptions(int paramInt1, int paramInt2, KDCOptions paramKDCOptions)
  {
    switch (paramInt2)
    {
    case 0: 
      break;
    case -1: 
      paramKDCOptions.set(paramInt1, false);
      break;
    case 1: 
      paramKDCOptions.set(paramInt1, true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\tools\Kinit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
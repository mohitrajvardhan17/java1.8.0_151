package sun.security.krb5.internal.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ccache.CCacheInputStream;
import sun.security.krb5.internal.ccache.FileCredentialsCache;

class KinitOptions
{
  public boolean validate = false;
  public short forwardable = -1;
  public short proxiable = -1;
  public boolean renew = false;
  public KerberosTime lifetime;
  public KerberosTime renewable_lifetime;
  public String target_service;
  public String keytab_file;
  public String cachename;
  private PrincipalName principal;
  public String realm;
  char[] password = null;
  public boolean keytab;
  private boolean DEBUG = Krb5.DEBUG;
  private boolean includeAddresses = true;
  private boolean useKeytab = false;
  private String ktabName;
  
  public KinitOptions()
    throws RuntimeException, RealmException
  {
    cachename = FileCredentialsCache.getDefaultCacheName();
    if (cachename == null) {
      throw new RuntimeException("default cache name error");
    }
    principal = getDefaultPrincipal();
  }
  
  public void setKDCRealm(String paramString)
    throws RealmException
  {
    realm = paramString;
  }
  
  public String getKDCRealm()
  {
    if ((realm == null) && (principal != null)) {
      return principal.getRealmString();
    }
    return null;
  }
  
  public KinitOptions(String[] paramArrayOfString)
    throws KrbException, RuntimeException, IOException
  {
    String str = null;
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equals("-f"))
      {
        forwardable = 1;
      }
      else if (paramArrayOfString[i].equals("-p"))
      {
        proxiable = 1;
      }
      else if (paramArrayOfString[i].equals("-c"))
      {
        if (paramArrayOfString[(i + 1)].startsWith("-")) {
          throw new IllegalArgumentException("input format  not correct:  -c  option must be followed by the cache name");
        }
        cachename = paramArrayOfString[(++i)];
        if ((cachename.length() >= 5) && (cachename.substring(0, 5).equalsIgnoreCase("FILE:"))) {
          cachename = cachename.substring(5);
        }
      }
      else if (paramArrayOfString[i].equals("-A"))
      {
        includeAddresses = false;
      }
      else if (paramArrayOfString[i].equals("-k"))
      {
        useKeytab = true;
      }
      else if (paramArrayOfString[i].equals("-t"))
      {
        if (ktabName != null) {
          throw new IllegalArgumentException("-t option/keytab file name repeated");
        }
        if (i + 1 < paramArrayOfString.length) {
          ktabName = paramArrayOfString[(++i)];
        } else {
          throw new IllegalArgumentException("-t option requires keytab file name");
        }
        useKeytab = true;
      }
      else if (paramArrayOfString[i].equalsIgnoreCase("-help"))
      {
        printHelp();
        System.exit(0);
      }
      else if (str == null)
      {
        str = paramArrayOfString[i];
        try
        {
          principal = new PrincipalName(str);
        }
        catch (Exception localException)
        {
          throw new IllegalArgumentException("invalid Principal name: " + str + localException.getMessage());
        }
      }
      else if (password == null)
      {
        password = paramArrayOfString[i].toCharArray();
      }
      else
      {
        throw new IllegalArgumentException("too many parameters");
      }
    }
    if (cachename == null)
    {
      cachename = FileCredentialsCache.getDefaultCacheName();
      if (cachename == null) {
        throw new RuntimeException("default cache name error");
      }
    }
    if (principal == null) {
      principal = getDefaultPrincipal();
    }
  }
  
  PrincipalName getDefaultPrincipal()
  {
    try
    {
      CCacheInputStream localCCacheInputStream = new CCacheInputStream(new FileInputStream(cachename));
      int i;
      if ((i = localCCacheInputStream.readVersion()) == 1284) {
        localCCacheInputStream.readTag();
      } else if ((i == 1281) || (i == 1282)) {
        localCCacheInputStream.setNativeByteOrder();
      }
      PrincipalName localPrincipalName2 = localCCacheInputStream.readPrincipal(i);
      localCCacheInputStream.close();
      if (DEBUG) {
        System.out.println(">>>KinitOptions principal name from the cache is :" + localPrincipalName2);
      }
      return localPrincipalName2;
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        localIOException.printStackTrace();
      }
    }
    catch (RealmException localRealmException1)
    {
      if (DEBUG) {
        localRealmException1.printStackTrace();
      }
    }
    String str = System.getProperty("user.name");
    if (DEBUG) {
      System.out.println(">>>KinitOptions default username is :" + str);
    }
    try
    {
      PrincipalName localPrincipalName1 = new PrincipalName(str);
      return localPrincipalName1;
    }
    catch (RealmException localRealmException2)
    {
      if (DEBUG)
      {
        System.out.println("Exception in getting principal name " + localRealmException2.getMessage());
        localRealmException2.printStackTrace();
      }
    }
    return null;
  }
  
  void printHelp()
  {
    System.out.println("Usage: kinit [-A] [-f] [-p] [-c cachename] [[-k [-t keytab_file_name]] [principal] [password]");
    System.out.println("\tavailable options to Kerberos 5 ticket request:");
    System.out.println("\t    -A   do not include addresses");
    System.out.println("\t    -f   forwardable");
    System.out.println("\t    -p   proxiable");
    System.out.println("\t    -c   cache name (i.e., FILE:\\d:\\myProfiles\\mykrb5cache)");
    System.out.println("\t    -k   use keytab");
    System.out.println("\t    -t   keytab file name");
    System.out.println("\t    principal   the principal name (i.e., qweadf@ATHENA.MIT.EDU qweadf)");
    System.out.println("\t    password   the principal's Kerberos password");
  }
  
  public boolean getAddressOption()
  {
    return includeAddresses;
  }
  
  public boolean useKeytabFile()
  {
    return useKeytab;
  }
  
  public String keytabFileName()
  {
    return ktabName;
  }
  
  public PrincipalName getPrincipal()
  {
    return principal;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\tools\KinitOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
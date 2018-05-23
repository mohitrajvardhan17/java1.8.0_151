package sun.security.krb5.internal.ccache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.LoginOptions;
import sun.security.krb5.internal.TicketFlags;

public class FileCredentialsCache
  extends CredentialsCache
  implements FileCCacheConstants
{
  public int version;
  public Tag tag;
  public PrincipalName primaryPrincipal;
  private Vector<Credentials> credentialsList;
  private static String dir;
  private static boolean DEBUG = Krb5.DEBUG;
  
  public static synchronized FileCredentialsCache acquireInstance(PrincipalName paramPrincipalName, String paramString)
  {
    try
    {
      FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
      if (paramString == null) {
        cacheName = getDefaultCacheName();
      } else {
        cacheName = checkValidation(paramString);
      }
      if ((cacheName == null) || (!new File(cacheName).exists())) {
        return null;
      }
      if (paramPrincipalName != null) {
        primaryPrincipal = paramPrincipalName;
      }
      localFileCredentialsCache.load(cacheName);
      return localFileCredentialsCache;
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        localIOException.printStackTrace();
      }
    }
    catch (KrbException localKrbException)
    {
      if (DEBUG) {
        localKrbException.printStackTrace();
      }
    }
    return null;
  }
  
  public static FileCredentialsCache acquireInstance()
  {
    return acquireInstance(null, null);
  }
  
  static synchronized FileCredentialsCache New(PrincipalName paramPrincipalName, String paramString)
  {
    try
    {
      FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
      cacheName = checkValidation(paramString);
      if (cacheName == null) {
        return null;
      }
      localFileCredentialsCache.init(paramPrincipalName, cacheName);
      return localFileCredentialsCache;
    }
    catch (IOException localIOException) {}catch (KrbException localKrbException) {}
    return null;
  }
  
  static synchronized FileCredentialsCache New(PrincipalName paramPrincipalName)
  {
    try
    {
      FileCredentialsCache localFileCredentialsCache = new FileCredentialsCache();
      cacheName = getDefaultCacheName();
      localFileCredentialsCache.init(paramPrincipalName, cacheName);
      return localFileCredentialsCache;
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        localIOException.printStackTrace();
      }
    }
    catch (KrbException localKrbException)
    {
      if (DEBUG) {
        localKrbException.printStackTrace();
      }
    }
    return null;
  }
  
  private FileCredentialsCache() {}
  
  boolean exists(String paramString)
  {
    File localFile = new File(paramString);
    return localFile.exists();
  }
  
  synchronized void init(PrincipalName paramPrincipalName, String paramString)
    throws IOException, KrbException
  {
    primaryPrincipal = paramPrincipalName;
    FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
    Object localObject1 = null;
    try
    {
      CCacheOutputStream localCCacheOutputStream = new CCacheOutputStream(localFileOutputStream);
      Object localObject2 = null;
      try
      {
        version = 1283;
        localCCacheOutputStream.writeHeader(primaryPrincipal, version);
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally {}
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileOutputStream.close();
          }
          catch (Throwable localThrowable6)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable6);
          }
        } else {
          localFileOutputStream.close();
        }
      }
    }
    load(paramString);
  }
  
  synchronized void load(String paramString)
    throws IOException, KrbException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramString);
    Object localObject1 = null;
    try
    {
      CCacheInputStream localCCacheInputStream = new CCacheInputStream(localFileInputStream);
      Object localObject2 = null;
      try
      {
        version = localCCacheInputStream.readVersion();
        if (version == 1284)
        {
          tag = localCCacheInputStream.readTag();
        }
        else
        {
          tag = null;
          if ((version == 1281) || (version == 1282)) {
            localCCacheInputStream.setNativeByteOrder();
          }
        }
        PrincipalName localPrincipalName = localCCacheInputStream.readPrincipal(version);
        if (primaryPrincipal != null)
        {
          if (!primaryPrincipal.match(localPrincipalName)) {
            throw new IOException("Primary principals don't match.");
          }
        }
        else {
          primaryPrincipal = localPrincipalName;
        }
        credentialsList = new Vector();
        while (localCCacheInputStream.available() > 0)
        {
          Credentials localCredentials = localCCacheInputStream.readCred(version);
          if (localCredentials != null) {
            credentialsList.addElement(localCredentials);
          }
        }
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally {}
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileInputStream.close();
          }
          catch (Throwable localThrowable6)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable6);
          }
        } else {
          localFileInputStream.close();
        }
      }
    }
  }
  
  public synchronized void update(Credentials paramCredentials)
  {
    if (credentialsList != null) {
      if (credentialsList.isEmpty())
      {
        credentialsList.addElement(paramCredentials);
      }
      else
      {
        Credentials localCredentials = null;
        int i = 0;
        for (int j = 0; j < credentialsList.size(); j++)
        {
          localCredentials = (Credentials)credentialsList.elementAt(j);
          if ((match(sname.getNameStrings(), sname.getNameStrings())) && (sname.getRealmString().equalsIgnoreCase(sname.getRealmString())))
          {
            i = 1;
            if (endtime.getTime() >= endtime.getTime())
            {
              if (DEBUG) {
                System.out.println(" >>> FileCredentialsCache Ticket matched, overwrite the old one.");
              }
              credentialsList.removeElementAt(j);
              credentialsList.addElement(paramCredentials);
            }
          }
        }
        if (i == 0)
        {
          if (DEBUG) {
            System.out.println(" >>> FileCredentialsCache Ticket not exactly matched, add new one into cache.");
          }
          credentialsList.addElement(paramCredentials);
        }
      }
    }
  }
  
  public synchronized PrincipalName getPrimaryPrincipal()
  {
    return primaryPrincipal;
  }
  
  public synchronized void save()
    throws IOException, Asn1Exception
  {
    FileOutputStream localFileOutputStream = new FileOutputStream(cacheName);
    Object localObject1 = null;
    try
    {
      CCacheOutputStream localCCacheOutputStream = new CCacheOutputStream(localFileOutputStream);
      Object localObject2 = null;
      try
      {
        localCCacheOutputStream.writeHeader(primaryPrincipal, version);
        Credentials[] arrayOfCredentials = null;
        if ((arrayOfCredentials = getCredsList()) != null) {
          for (int i = 0; i < arrayOfCredentials.length; i++) {
            localCCacheOutputStream.addCreds(arrayOfCredentials[i]);
          }
        }
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally {}
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localFileOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localFileOutputStream.close();
          }
          catch (Throwable localThrowable6)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable6);
          }
        } else {
          localFileOutputStream.close();
        }
      }
    }
  }
  
  boolean match(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    if (paramArrayOfString1.length != paramArrayOfString2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfString1.length; i++) {
      if (!paramArrayOfString1[i].equalsIgnoreCase(paramArrayOfString2[i])) {
        return false;
      }
    }
    return true;
  }
  
  public synchronized Credentials[] getCredsList()
  {
    if ((credentialsList == null) || (credentialsList.isEmpty())) {
      return null;
    }
    Credentials[] arrayOfCredentials = new Credentials[credentialsList.size()];
    for (int i = 0; i < credentialsList.size(); i++) {
      arrayOfCredentials[i] = ((Credentials)credentialsList.elementAt(i));
    }
    return arrayOfCredentials;
  }
  
  public Credentials getCreds(LoginOptions paramLoginOptions, PrincipalName paramPrincipalName)
  {
    if (paramLoginOptions == null) {
      return getCreds(paramPrincipalName);
    }
    Credentials[] arrayOfCredentials = getCredsList();
    if (arrayOfCredentials == null) {
      return null;
    }
    for (int i = 0; i < arrayOfCredentials.length; i++) {
      if ((paramPrincipalName.match(sname)) && (flags.match(paramLoginOptions))) {
        return arrayOfCredentials[i];
      }
    }
    return null;
  }
  
  public Credentials getCreds(PrincipalName paramPrincipalName)
  {
    Credentials[] arrayOfCredentials = getCredsList();
    if (arrayOfCredentials == null) {
      return null;
    }
    for (int i = 0; i < arrayOfCredentials.length; i++) {
      if (paramPrincipalName.match(sname)) {
        return arrayOfCredentials[i];
      }
    }
    return null;
  }
  
  public Credentials getDefaultCreds()
  {
    Credentials[] arrayOfCredentials = getCredsList();
    if (arrayOfCredentials == null) {
      return null;
    }
    for (int i = arrayOfCredentials.length - 1; i >= 0; i--) {
      if (sname.toString().startsWith("krbtgt"))
      {
        String[] arrayOfString = sname.getNameStrings();
        if (arrayOfString[1].equals(sname.getRealm().toString())) {
          return arrayOfCredentials[i];
        }
      }
    }
    return null;
  }
  
  public static String getDefaultCacheName()
  {
    String str1 = "krb5cc";
    String str2 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        String str = System.getenv("KRB5CCNAME");
        if ((str != null) && (str.length() >= 5) && (str.regionMatches(true, 0, "FILE:", 0, 5))) {
          str = str.substring(5);
        }
        return str;
      }
    });
    if (str2 != null)
    {
      if (DEBUG) {
        System.out.println(">>>KinitOptions cache name is " + str2);
      }
      return str2;
    }
    String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
    if (str3 != null)
    {
      str4 = null;
      str5 = null;
      long l = 0L;
      if (!str3.startsWith("Windows")) {
        try
        {
          Class localClass = Class.forName("com.sun.security.auth.module.UnixSystem");
          Constructor localConstructor = localClass.getConstructor(new Class[0]);
          Object localObject = localConstructor.newInstance(new Object[0]);
          Method localMethod = localClass.getMethod("getUid", new Class[0]);
          l = ((Long)localMethod.invoke(localObject, new Object[0])).longValue();
          str2 = File.separator + "tmp" + File.separator + str1 + "_" + l;
          if (DEBUG) {
            System.out.println(">>>KinitOptions cache name is " + str2);
          }
          return str2;
        }
        catch (Exception localException)
        {
          if (DEBUG)
          {
            System.out.println("Exception in obtaining uid for Unix platforms Using user's home directory");
            localException.printStackTrace();
          }
        }
      }
    }
    String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
    String str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
    if (str5 == null) {
      str5 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir"));
    }
    if (str4 != null) {
      str2 = str5 + File.separator + str1 + "_" + str4;
    } else {
      str2 = str5 + File.separator + str1;
    }
    if (DEBUG) {
      System.out.println(">>>KinitOptions cache name is " + str2);
    }
    return str2;
  }
  
  public static String checkValidation(String paramString)
  {
    String str = null;
    if (paramString == null) {
      return null;
    }
    try
    {
      str = new File(paramString).getCanonicalPath();
      File localFile1 = new File(str);
      if (!localFile1.exists())
      {
        File localFile2 = new File(localFile1.getParent());
        if (!localFile2.isDirectory()) {
          str = null;
        }
        localFile2 = null;
      }
      localFile1 = null;
    }
    catch (IOException localIOException)
    {
      str = null;
    }
    return str;
  }
  
  private static String exec(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    Vector localVector = new Vector();
    while (localStringTokenizer.hasMoreTokens()) {
      localVector.addElement(localStringTokenizer.nextToken());
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    try
    {
      Process localProcess = (Process)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Process run()
        {
          try
          {
            return Runtime.getRuntime().exec(val$command);
          }
          catch (IOException localIOException)
          {
            if (FileCredentialsCache.DEBUG) {
              localIOException.printStackTrace();
            }
          }
          return null;
        }
      });
      if (localProcess == null) {
        return null;
      }
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getInputStream(), "8859_1"));
      String str = null;
      if ((arrayOfString.length == 1) && (arrayOfString[0].equals("/usr/bin/env"))) {}
      while ((str = localBufferedReader.readLine()) != null) {
        if ((str.length() >= 11) && (str.substring(0, 11).equalsIgnoreCase("KRB5CCNAME=")))
        {
          str = str.substring(11);
          break;
          str = localBufferedReader.readLine();
        }
      }
      localBufferedReader.close();
      return str;
    }
    catch (Exception localException)
    {
      if (DEBUG) {
        localException.printStackTrace();
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ccache\FileCredentialsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
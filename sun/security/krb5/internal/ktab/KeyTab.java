package sun.security.krb5.internal.ktab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.Config;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.crypto.EType;

public class KeyTab
  implements KeyTabConstants
{
  private static final boolean DEBUG = Krb5.DEBUG;
  private static String defaultTabName = null;
  private static Map<String, KeyTab> map = new HashMap();
  private boolean isMissing = false;
  private boolean isValid = true;
  private final String tabName;
  private long lastModified;
  private int kt_vno = 1282;
  private Vector<KeyTabEntry> entries = new Vector();
  
  private KeyTab(String paramString)
  {
    tabName = paramString;
    try
    {
      lastModified = new File(tabName).lastModified();
      KeyTabInputStream localKeyTabInputStream = new KeyTabInputStream(new FileInputStream(paramString));
      Object localObject1 = null;
      try
      {
        load(localKeyTabInputStream);
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localKeyTabInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localKeyTabInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localKeyTabInputStream.close();
          }
        }
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      entries.clear();
      isMissing = true;
    }
    catch (Exception localException)
    {
      entries.clear();
      isValid = false;
    }
  }
  
  private static synchronized KeyTab getInstance0(String paramString)
  {
    long l = new File(paramString).lastModified();
    KeyTab localKeyTab1 = (KeyTab)map.get(paramString);
    if ((localKeyTab1 != null) && (localKeyTab1.isValid()) && (lastModified == l)) {
      return localKeyTab1;
    }
    KeyTab localKeyTab2 = new KeyTab(paramString);
    if (localKeyTab2.isValid())
    {
      map.put(paramString, localKeyTab2);
      return localKeyTab2;
    }
    if (localKeyTab1 != null) {
      return localKeyTab1;
    }
    return localKeyTab2;
  }
  
  public static KeyTab getInstance(String paramString)
  {
    if (paramString == null) {
      return getInstance();
    }
    return getInstance0(normalize(paramString));
  }
  
  public static KeyTab getInstance(File paramFile)
  {
    if (paramFile == null) {
      return getInstance();
    }
    return getInstance0(paramFile.getPath());
  }
  
  public static KeyTab getInstance()
  {
    return getInstance(getDefaultTabName());
  }
  
  public boolean isMissing()
  {
    return isMissing;
  }
  
  public boolean isValid()
  {
    return isValid;
  }
  
  private static String getDefaultTabName()
  {
    if (defaultTabName != null) {
      return defaultTabName;
    }
    String str1 = null;
    try
    {
      String str2 = Config.getInstance().get(new String[] { "libdefaults", "default_keytab_name" });
      if (str2 != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str2, " ");
        while (localStringTokenizer.hasMoreTokens())
        {
          str1 = normalize(localStringTokenizer.nextToken());
          if (new File(str1).exists()) {
            break;
          }
        }
      }
    }
    catch (KrbException localKrbException)
    {
      str1 = null;
    }
    if (str1 == null)
    {
      String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
      if (str3 == null) {
        str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.dir"));
      }
      str1 = str3 + File.separator + "krb5.keytab";
    }
    defaultTabName = str1;
    return str1;
  }
  
  public static String normalize(String paramString)
  {
    String str;
    if ((paramString.length() >= 5) && (paramString.substring(0, 5).equalsIgnoreCase("FILE:"))) {
      str = paramString.substring(5);
    } else if ((paramString.length() >= 9) && (paramString.substring(0, 9).equalsIgnoreCase("ANY:FILE:"))) {
      str = paramString.substring(9);
    } else if ((paramString.length() >= 7) && (paramString.substring(0, 7).equalsIgnoreCase("SRVTAB:"))) {
      str = paramString.substring(7);
    } else {
      str = paramString;
    }
    return str;
  }
  
  private void load(KeyTabInputStream paramKeyTabInputStream)
    throws IOException, RealmException
  {
    entries.clear();
    kt_vno = paramKeyTabInputStream.readVersion();
    if (kt_vno == 1281) {
      paramKeyTabInputStream.setNativeByteOrder();
    }
    int i = 0;
    while (paramKeyTabInputStream.available() > 0)
    {
      i = paramKeyTabInputStream.readEntryLength();
      KeyTabEntry localKeyTabEntry = paramKeyTabInputStream.readEntry(i, kt_vno);
      if (DEBUG) {
        System.out.println(">>> KeyTab: load() entry length: " + i + "; type: " + (localKeyTabEntry != null ? keyType : 0));
      }
      if (localKeyTabEntry != null) {
        entries.addElement(localKeyTabEntry);
      }
    }
  }
  
  public PrincipalName getOneName()
  {
    int i = entries.size();
    return i > 0 ? entries.elementAt(i - 1)).service : null;
  }
  
  public EncryptionKey[] readServiceKeys(PrincipalName paramPrincipalName)
  {
    int i = entries.size();
    ArrayList localArrayList = new ArrayList(i);
    if (DEBUG) {
      System.out.println("Looking for keys for: " + paramPrincipalName);
    }
    for (int j = i - 1; j >= 0; j--)
    {
      KeyTabEntry localKeyTabEntry = (KeyTabEntry)entries.elementAt(j);
      if (service.match(paramPrincipalName)) {
        if (EType.isSupported(keyType))
        {
          EncryptionKey localEncryptionKey = new EncryptionKey(keyblock, keyType, new Integer(keyVersion));
          localArrayList.add(localEncryptionKey);
          if (DEBUG) {
            System.out.println("Added key: " + keyType + "version: " + keyVersion);
          }
        }
        else if (DEBUG)
        {
          System.out.println("Found unsupported keytype (" + keyType + ") for " + paramPrincipalName);
        }
      }
    }
    i = localArrayList.size();
    EncryptionKey[] arrayOfEncryptionKey = (EncryptionKey[])localArrayList.toArray(new EncryptionKey[i]);
    Arrays.sort(arrayOfEncryptionKey, new Comparator()
    {
      public int compare(EncryptionKey paramAnonymousEncryptionKey1, EncryptionKey paramAnonymousEncryptionKey2)
      {
        return paramAnonymousEncryptionKey2.getKeyVersionNumber().intValue() - paramAnonymousEncryptionKey1.getKeyVersionNumber().intValue();
      }
    });
    return arrayOfEncryptionKey;
  }
  
  public boolean findServiceEntry(PrincipalName paramPrincipalName)
  {
    for (int i = 0; i < entries.size(); i++)
    {
      KeyTabEntry localKeyTabEntry = (KeyTabEntry)entries.elementAt(i);
      if (service.match(paramPrincipalName))
      {
        if (EType.isSupported(keyType)) {
          return true;
        }
        if (DEBUG) {
          System.out.println("Found unsupported keytype (" + keyType + ") for " + paramPrincipalName);
        }
      }
    }
    return false;
  }
  
  public String tabName()
  {
    return tabName;
  }
  
  public void addEntry(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, boolean paramBoolean)
    throws KrbException
  {
    addEntry(paramPrincipalName, paramPrincipalName.getSalt(), paramArrayOfChar, paramInt, paramBoolean);
  }
  
  public void addEntry(PrincipalName paramPrincipalName, String paramString, char[] paramArrayOfChar, int paramInt, boolean paramBoolean)
    throws KrbException
  {
    EncryptionKey[] arrayOfEncryptionKey = EncryptionKey.acquireSecretKeys(paramArrayOfChar, paramString);
    int i = 0;
    for (int j = entries.size() - 1; j >= 0; j--)
    {
      KeyTabEntry localKeyTabEntry1 = (KeyTabEntry)entries.get(j);
      if (service.match(paramPrincipalName))
      {
        if (keyVersion > i) {
          i = keyVersion;
        }
        if ((!paramBoolean) || (keyVersion == paramInt)) {
          entries.removeElementAt(j);
        }
      }
    }
    if (paramInt == -1) {
      paramInt = i + 1;
    }
    for (j = 0; (arrayOfEncryptionKey != null) && (j < arrayOfEncryptionKey.length); j++)
    {
      int k = arrayOfEncryptionKey[j].getEType();
      byte[] arrayOfByte = arrayOfEncryptionKey[j].getBytes();
      KeyTabEntry localKeyTabEntry2 = new KeyTabEntry(paramPrincipalName, paramPrincipalName.getRealm(), new KerberosTime(System.currentTimeMillis()), paramInt, k, arrayOfByte);
      entries.addElement(localKeyTabEntry2);
    }
  }
  
  public KeyTabEntry[] getEntries()
  {
    KeyTabEntry[] arrayOfKeyTabEntry = new KeyTabEntry[entries.size()];
    for (int i = 0; i < arrayOfKeyTabEntry.length; i++) {
      arrayOfKeyTabEntry[i] = ((KeyTabEntry)entries.elementAt(i));
    }
    return arrayOfKeyTabEntry;
  }
  
  public static synchronized KeyTab create()
    throws IOException, RealmException
  {
    String str = getDefaultTabName();
    return create(str);
  }
  
  public static synchronized KeyTab create(String paramString)
    throws IOException, RealmException
  {
    KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(paramString));
    Object localObject1 = null;
    try
    {
      localKeyTabOutputStream.writeVersion(1282);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localKeyTabOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localKeyTabOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localKeyTabOutputStream.close();
        }
      }
    }
    return new KeyTab(paramString);
  }
  
  public synchronized void save()
    throws IOException
  {
    KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(tabName));
    Object localObject1 = null;
    try
    {
      localKeyTabOutputStream.writeVersion(kt_vno);
      for (int i = 0; i < entries.size(); i++) {
        localKeyTabOutputStream.writeEntry((KeyTabEntry)entries.elementAt(i));
      }
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localKeyTabOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localKeyTabOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localKeyTabOutputStream.close();
        }
      }
    }
  }
  
  public int deleteEntries(PrincipalName paramPrincipalName, int paramInt1, int paramInt2)
  {
    int i = 0;
    HashMap localHashMap = new HashMap();
    KeyTabEntry localKeyTabEntry;
    int k;
    for (int j = entries.size() - 1; j >= 0; j--)
    {
      localKeyTabEntry = (KeyTabEntry)entries.get(j);
      if ((paramPrincipalName.match(localKeyTabEntry.getService())) && ((paramInt1 == -1) || (keyType == paramInt1))) {
        if (paramInt2 == -2)
        {
          if (localHashMap.containsKey(Integer.valueOf(keyType)))
          {
            k = ((Integer)localHashMap.get(Integer.valueOf(keyType))).intValue();
            if (keyVersion > k) {
              localHashMap.put(Integer.valueOf(keyType), Integer.valueOf(keyVersion));
            }
          }
          else
          {
            localHashMap.put(Integer.valueOf(keyType), Integer.valueOf(keyVersion));
          }
        }
        else if ((paramInt2 == -1) || (keyVersion == paramInt2))
        {
          entries.removeElementAt(j);
          i++;
        }
      }
    }
    if (paramInt2 == -2) {
      for (j = entries.size() - 1; j >= 0; j--)
      {
        localKeyTabEntry = (KeyTabEntry)entries.get(j);
        if ((paramPrincipalName.match(localKeyTabEntry.getService())) && ((paramInt1 == -1) || (keyType == paramInt1)))
        {
          k = ((Integer)localHashMap.get(Integer.valueOf(keyType))).intValue();
          if (keyVersion != k)
          {
            entries.removeElementAt(j);
            i++;
          }
        }
      }
    }
    return i;
  }
  
  public synchronized void createVersion(File paramFile)
    throws IOException
  {
    KeyTabOutputStream localKeyTabOutputStream = new KeyTabOutputStream(new FileOutputStream(paramFile));
    Object localObject1 = null;
    try
    {
      localKeyTabOutputStream.write16(1282);
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localKeyTabOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localKeyTabOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localKeyTabOutputStream.close();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ktab\KeyTab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import sun.security.krb5.internal.Krb5;

public class SCDynamicStoreConfig
{
  private static boolean DEBUG = Krb5.DEBUG;
  
  public SCDynamicStoreConfig() {}
  
  private static native void installNotificationCallback();
  
  private static native Hashtable<String, Object> getKerberosConfig();
  
  private static Vector<String> unwrapHost(Collection<Hashtable<String, String>> paramCollection)
  {
    Vector localVector = new Vector();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Hashtable localHashtable = (Hashtable)localIterator.next();
      localVector.add(localHashtable.get("host"));
    }
    return localVector;
  }
  
  private static Hashtable<String, Object> convertRealmConfigs(Hashtable<String, ?> paramHashtable)
  {
    Hashtable localHashtable1 = new Hashtable();
    Iterator localIterator = paramHashtable.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Hashtable localHashtable2 = (Hashtable)paramHashtable.get(str);
      Hashtable localHashtable3 = new Hashtable();
      Collection localCollection1 = (Collection)localHashtable2.get("kdc");
      if (localCollection1 != null) {
        localHashtable3.put("kdc", unwrapHost(localCollection1));
      }
      Collection localCollection2 = (Collection)localHashtable2.get("kadmin");
      if (localCollection2 != null) {
        localHashtable3.put("admin_server", unwrapHost(localCollection2));
      }
      localHashtable1.put(str, localHashtable3);
    }
    return localHashtable1;
  }
  
  public static Hashtable<String, Object> getConfig()
    throws IOException
  {
    Hashtable localHashtable = getKerberosConfig();
    if (localHashtable == null) {
      throw new IOException("Could not load configuration from SCDynamicStore");
    }
    if (DEBUG) {
      System.out.println("Raw map from JNI: " + localHashtable);
    }
    return convertNativeConfig(localHashtable);
  }
  
  private static Hashtable<String, Object> convertNativeConfig(Hashtable<String, Object> paramHashtable)
  {
    Hashtable localHashtable1 = (Hashtable)paramHashtable.get("realms");
    if (localHashtable1 != null)
    {
      paramHashtable.remove("realms");
      Hashtable localHashtable2 = convertRealmConfigs(localHashtable1);
      paramHashtable.put("realms", localHashtable2);
    }
    WrapAllStringInVector(paramHashtable);
    if (DEBUG) {
      System.out.println("stanzaTable : " + paramHashtable);
    }
    return paramHashtable;
  }
  
  private static void WrapAllStringInVector(Hashtable<String, Object> paramHashtable)
  {
    Iterator localIterator = paramHashtable.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = paramHashtable.get(str);
      if ((localObject instanceof Hashtable))
      {
        WrapAllStringInVector((Hashtable)localObject);
      }
      else if ((localObject instanceof String))
      {
        Vector localVector = new Vector();
        localVector.add((String)localObject);
        paramHashtable.put(str, localVector);
      }
    }
  }
  
  static
  {
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        String str = System.getProperty("os.name");
        if (str.contains("OS X"))
        {
          System.loadLibrary("osx");
          return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
      }
    })).booleanValue();
    if (bool) {
      installNotificationCallback();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\SCDynamicStoreConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
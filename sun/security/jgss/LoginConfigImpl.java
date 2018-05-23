package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import org.ietf.jgss.Oid;
import sun.security.util.Debug;

public class LoginConfigImpl
  extends Configuration
{
  private final Configuration config;
  private final GSSCaller caller;
  private final String mechName;
  private static final Debug debug = Debug.getInstance("gssloginconfig", "\t[GSS LoginConfigImpl]");
  
  public LoginConfigImpl(GSSCaller paramGSSCaller, Oid paramOid)
  {
    caller = paramGSSCaller;
    if (paramOid.equals(GSSUtil.GSS_KRB5_MECH_OID)) {
      mechName = "krb5";
    } else {
      throw new IllegalArgumentException(paramOid.toString() + " not supported");
    }
    config = ((Configuration)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Configuration run()
      {
        return Configuration.getConfiguration();
      }
    }));
  }
  
  public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
  {
    AppConfigurationEntry[] arrayOfAppConfigurationEntry = null;
    if ("OTHER".equalsIgnoreCase(paramString)) {
      return null;
    }
    String[] arrayOfString1 = null;
    if ("krb5".equals(mechName))
    {
      if (caller == GSSCaller.CALLER_INITIATE) {
        arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.security.jgss.initiate" };
      } else if (caller == GSSCaller.CALLER_ACCEPT) {
        arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.security.jgss.accept" };
      } else if (caller == GSSCaller.CALLER_SSL_CLIENT) {
        arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.net.ssl.client" };
      } else if (caller == GSSCaller.CALLER_SSL_SERVER) {
        arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.accept", "com.sun.net.ssl.server" };
      } else if ((caller instanceof HttpCaller)) {
        arrayOfString1 = new String[] { "com.sun.security.jgss.krb5.initiate" };
      } else if (caller == GSSCaller.CALLER_UNKNOWN) {
        throw new AssertionError("caller not defined");
      }
    }
    else {
      throw new IllegalArgumentException(mechName + " not supported");
    }
    for (String str : arrayOfString1)
    {
      arrayOfAppConfigurationEntry = config.getAppConfigurationEntry(str);
      if (debug != null) {
        debug.println("Trying " + str + (arrayOfAppConfigurationEntry == null ? ": does not exist." : ": Found!"));
      }
      if (arrayOfAppConfigurationEntry != null) {
        break;
      }
    }
    if (arrayOfAppConfigurationEntry == null)
    {
      if (debug != null) {
        debug.println("Cannot read JGSS entry, use default values instead.");
      }
      arrayOfAppConfigurationEntry = getDefaultConfigurationEntry();
    }
    return arrayOfAppConfigurationEntry;
  }
  
  private AppConfigurationEntry[] getDefaultConfigurationEntry()
  {
    HashMap localHashMap = new HashMap(2);
    if ((mechName == null) || (mechName.equals("krb5")))
    {
      if (isServerSide(caller))
      {
        localHashMap.put("useKeyTab", "true");
        localHashMap.put("storeKey", "true");
        localHashMap.put("doNotPrompt", "true");
        localHashMap.put("principal", "*");
        localHashMap.put("isInitiator", "false");
      }
      else
      {
        localHashMap.put("useTicketCache", "true");
        localHashMap.put("doNotPrompt", "false");
      }
      return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, localHashMap) };
    }
    return null;
  }
  
  private static boolean isServerSide(GSSCaller paramGSSCaller)
  {
    return (GSSCaller.CALLER_ACCEPT == paramGSSCaller) || (GSSCaller.CALLER_SSL_SERVER == paramGSSCaller);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\LoginConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
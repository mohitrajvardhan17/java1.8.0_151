package sun.security.jgss.wrapper;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.HashMap;
import org.ietf.jgss.Oid;
import sun.security.action.PutAllAction;

public final class SunNativeProvider
  extends Provider
{
  private static final long serialVersionUID = -238911724858694204L;
  private static final String NAME = "SunNativeGSS";
  private static final String INFO = "Sun Native GSS provider";
  private static final String MF_CLASS = "sun.security.jgss.wrapper.NativeGSSFactory";
  private static final String LIB_PROP = "sun.security.jgss.lib";
  private static final String DEBUG_PROP = "sun.security.nativegss.debug";
  private static HashMap<String, String> MECH_MAP = (HashMap)AccessController.doPrivileged(new PrivilegedAction()
  {
    public HashMap<String, String> run()
    {
      SunNativeProvider.DEBUG = Boolean.parseBoolean(System.getProperty("sun.security.nativegss.debug"));
      try
      {
        System.loadLibrary("j2gss");
      }
      catch (Error localError)
      {
        SunNativeProvider.debug("No j2gss library found!");
        if (SunNativeProvider.DEBUG) {
          localError.printStackTrace();
        }
        return null;
      }
      String[] arrayOfString = new String[0];
      String str1 = System.getProperty("sun.security.jgss.lib");
      Object localObject;
      if ((str1 == null) || (str1.trim().equals("")))
      {
        localObject = System.getProperty("os.name");
        if (((String)localObject).startsWith("SunOS")) {
          arrayOfString = new String[] { "libgss.so" };
        } else if (((String)localObject).startsWith("Linux")) {
          arrayOfString = new String[] { "libgssapi.so", "libgssapi_krb5.so", "libgssapi_krb5.so.2" };
        } else if (((String)localObject).contains("OS X")) {
          arrayOfString = new String[] { "libgssapi_krb5.dylib", "/usr/lib/sasl2/libgssapiv2.2.so" };
        }
      }
      else
      {
        arrayOfString = new String[] { str1 };
      }
      for (String str2 : arrayOfString) {
        if (GSSLibStub.init(str2, SunNativeProvider.DEBUG))
        {
          SunNativeProvider.debug("Loaded GSS library: " + str2);
          Oid[] arrayOfOid = GSSLibStub.indicateMechs();
          HashMap localHashMap = new HashMap();
          for (int k = 0; k < arrayOfOid.length; k++)
          {
            SunNativeProvider.debug("Native MF for " + arrayOfOid[k]);
            localHashMap.put("GssApiMechanism." + arrayOfOid[k], "sun.security.jgss.wrapper.NativeGSSFactory");
          }
          return localHashMap;
        }
      }
      return null;
    }
  });
  static final Provider INSTANCE = new SunNativeProvider();
  static boolean DEBUG;
  
  static void debug(String paramString)
  {
    if (DEBUG)
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      System.out.println("SunNativeGSS: " + paramString);
    }
  }
  
  public SunNativeProvider()
  {
    super("SunNativeGSS", 1.8D, "Sun Native GSS provider");
    if (MECH_MAP != null) {
      AccessController.doPrivileged(new PutAllAction(this, MECH_MAP));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\wrapper\SunNativeProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
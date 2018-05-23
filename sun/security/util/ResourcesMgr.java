package sun.security.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;

public class ResourcesMgr
{
  private static ResourceBundle bundle;
  private static ResourceBundle altBundle;
  
  public ResourcesMgr() {}
  
  public static String getString(String paramString)
  {
    if (bundle == null) {
      bundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ResourceBundle run()
        {
          return ResourceBundle.getBundle("sun.security.util.Resources");
        }
      });
    }
    return bundle.getString(paramString);
  }
  
  public static String getString(String paramString1, String paramString2)
  {
    if (altBundle == null) {
      altBundle = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ResourceBundle run()
        {
          return ResourceBundle.getBundle(val$altBundleName);
        }
      });
    }
    return altBundle.getString(paramString1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ResourcesMgr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
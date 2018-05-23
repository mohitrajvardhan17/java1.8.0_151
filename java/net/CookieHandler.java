package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import sun.security.util.SecurityConstants;

public abstract class CookieHandler
{
  private static CookieHandler cookieHandler;
  
  public CookieHandler() {}
  
  public static synchronized CookieHandler getDefault()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_COOKIEHANDLER_PERMISSION);
    }
    return cookieHandler;
  }
  
  public static synchronized void setDefault(CookieHandler paramCookieHandler)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.SET_COOKIEHANDLER_PERMISSION);
    }
    cookieHandler = paramCookieHandler;
  }
  
  public abstract Map<String, List<String>> get(URI paramURI, Map<String, List<String>> paramMap)
    throws IOException;
  
  public abstract void put(URI paramURI, Map<String, List<String>> paramMap)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\CookieHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
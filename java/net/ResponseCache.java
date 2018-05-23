package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import sun.security.util.SecurityConstants;

public abstract class ResponseCache
{
  private static ResponseCache theResponseCache;
  
  public ResponseCache() {}
  
  public static synchronized ResponseCache getDefault()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_RESPONSECACHE_PERMISSION);
    }
    return theResponseCache;
  }
  
  public static synchronized void setDefault(ResponseCache paramResponseCache)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.SET_RESPONSECACHE_PERMISSION);
    }
    theResponseCache = paramResponseCache;
  }
  
  public abstract CacheResponse get(URI paramURI, String paramString, Map<String, List<String>> paramMap)
    throws IOException;
  
  public abstract CacheRequest put(URI paramURI, URLConnection paramURLConnection)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\ResponseCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.net;

import java.io.IOException;
import java.util.List;
import sun.security.util.SecurityConstants;

public abstract class ProxySelector
{
  private static ProxySelector theProxySelector;
  
  public ProxySelector() {}
  
  public static ProxySelector getDefault()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_PROXYSELECTOR_PERMISSION);
    }
    return theProxySelector;
  }
  
  public static void setDefault(ProxySelector paramProxySelector)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.SET_PROXYSELECTOR_PERMISSION);
    }
    theProxySelector = paramProxySelector;
  }
  
  public abstract List<Proxy> select(URI paramURI);
  
  public abstract void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException);
  
  static
  {
    try
    {
      Class localClass = Class.forName("sun.net.spi.DefaultProxySelector");
      if ((localClass != null) && (ProxySelector.class.isAssignableFrom(localClass))) {
        theProxySelector = (ProxySelector)localClass.newInstance();
      }
    }
    catch (Exception localException)
    {
      theProxySelector = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\ProxySelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.net;

import java.security.Permission;

public abstract class Authenticator
{
  private static Authenticator theAuthenticator;
  private String requestingHost;
  private InetAddress requestingSite;
  private int requestingPort;
  private String requestingProtocol;
  private String requestingPrompt;
  private String requestingScheme;
  private URL requestingURL;
  private RequestorType requestingAuthType;
  
  public Authenticator() {}
  
  private void reset()
  {
    requestingHost = null;
    requestingSite = null;
    requestingPort = -1;
    requestingProtocol = null;
    requestingPrompt = null;
    requestingScheme = null;
    requestingURL = null;
    requestingAuthType = RequestorType.SERVER;
  }
  
  public static synchronized void setDefault(Authenticator paramAuthenticator)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      NetPermission localNetPermission = new NetPermission("setDefaultAuthenticator");
      localSecurityManager.checkPermission(localNetPermission);
    }
    theAuthenticator = paramAuthenticator;
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject1 = new NetPermission("requestPasswordAuthentication");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    Object localObject1 = theAuthenticator;
    if (localObject1 == null) {
      return null;
    }
    synchronized (localObject1)
    {
      ((Authenticator)localObject1).reset();
      requestingSite = paramInetAddress;
      requestingPort = paramInt;
      requestingProtocol = paramString1;
      requestingPrompt = paramString2;
      requestingScheme = paramString3;
      return ((Authenticator)localObject1).getPasswordAuthentication();
    }
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject1 = new NetPermission("requestPasswordAuthentication");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    Object localObject1 = theAuthenticator;
    if (localObject1 == null) {
      return null;
    }
    synchronized (localObject1)
    {
      ((Authenticator)localObject1).reset();
      requestingHost = paramString1;
      requestingSite = paramInetAddress;
      requestingPort = paramInt;
      requestingProtocol = paramString2;
      requestingPrompt = paramString3;
      requestingScheme = paramString4;
      return ((Authenticator)localObject1).getPasswordAuthentication();
    }
  }
  
  public static PasswordAuthentication requestPasswordAuthentication(String paramString1, InetAddress paramInetAddress, int paramInt, String paramString2, String paramString3, String paramString4, URL paramURL, RequestorType paramRequestorType)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      localObject1 = new NetPermission("requestPasswordAuthentication");
      localSecurityManager.checkPermission((Permission)localObject1);
    }
    Object localObject1 = theAuthenticator;
    if (localObject1 == null) {
      return null;
    }
    synchronized (localObject1)
    {
      ((Authenticator)localObject1).reset();
      requestingHost = paramString1;
      requestingSite = paramInetAddress;
      requestingPort = paramInt;
      requestingProtocol = paramString2;
      requestingPrompt = paramString3;
      requestingScheme = paramString4;
      requestingURL = paramURL;
      requestingAuthType = paramRequestorType;
      return ((Authenticator)localObject1).getPasswordAuthentication();
    }
  }
  
  protected final String getRequestingHost()
  {
    return requestingHost;
  }
  
  protected final InetAddress getRequestingSite()
  {
    return requestingSite;
  }
  
  protected final int getRequestingPort()
  {
    return requestingPort;
  }
  
  protected final String getRequestingProtocol()
  {
    return requestingProtocol;
  }
  
  protected final String getRequestingPrompt()
  {
    return requestingPrompt;
  }
  
  protected final String getRequestingScheme()
  {
    return requestingScheme;
  }
  
  protected PasswordAuthentication getPasswordAuthentication()
  {
    return null;
  }
  
  protected URL getRequestingURL()
  {
    return requestingURL;
  }
  
  protected RequestorType getRequestorType()
  {
    return requestingAuthType;
  }
  
  public static enum RequestorType
  {
    PROXY,  SERVER;
    
    private RequestorType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
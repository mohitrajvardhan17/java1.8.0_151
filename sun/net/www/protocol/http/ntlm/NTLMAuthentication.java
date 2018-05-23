package sun.net.www.protocol.http.ntlm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.AuthenticationInfo;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

public class NTLMAuthentication
  extends AuthenticationInfo
{
  private static final long serialVersionUID = 100L;
  private static final NTLMAuthenticationCallback NTLMAuthCallback = ;
  private String hostname;
  private static String defaultDomain = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.ntlm.domain", "domain"));
  private static final boolean ntlmCache;
  String username;
  String ntdomain;
  String password;
  
  private void init0()
  {
    hostname = ((String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        String str;
        try
        {
          str = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException localUnknownHostException)
        {
          str = "localhost";
        }
        return str;
      }
    }));
    int i = hostname.indexOf('.');
    if (i != -1) {
      hostname = hostname.substring(0, i);
    }
  }
  
  public NTLMAuthentication(boolean paramBoolean, URL paramURL, PasswordAuthentication paramPasswordAuthentication)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.NTLM, paramURL, "");
    init(paramPasswordAuthentication);
  }
  
  private void init(PasswordAuthentication paramPasswordAuthentication)
  {
    pw = paramPasswordAuthentication;
    if (paramPasswordAuthentication != null)
    {
      String str = paramPasswordAuthentication.getUserName();
      int i = str.indexOf('\\');
      if (i == -1)
      {
        username = str;
        ntdomain = defaultDomain;
      }
      else
      {
        ntdomain = str.substring(0, i).toUpperCase();
        username = str.substring(i + 1);
      }
      password = new String(paramPasswordAuthentication.getPassword());
    }
    else
    {
      username = null;
      ntdomain = null;
      password = null;
    }
    init0();
  }
  
  public NTLMAuthentication(boolean paramBoolean, String paramString, int paramInt, PasswordAuthentication paramPasswordAuthentication)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.NTLM, paramString, paramInt, "");
    init(paramPasswordAuthentication);
  }
  
  protected boolean useAuthCache()
  {
    return (ntlmCache) && (super.useAuthCache());
  }
  
  public boolean supportsPreemptiveAuthorization()
  {
    return false;
  }
  
  public static boolean supportsTransparentAuth()
  {
    return true;
  }
  
  public static boolean isTrustedSite(URL paramURL)
  {
    return NTLMAuthCallback.isTrustedSite(paramURL);
  }
  
  public String getHeaderValue(URL paramURL, String paramString)
  {
    throw new RuntimeException("getHeaderValue not supported");
  }
  
  public boolean isAuthorizationStale(String paramString)
  {
    return false;
  }
  
  public synchronized boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
  {
    try
    {
      NTLMAuthSequence localNTLMAuthSequence = (NTLMAuthSequence)paramHttpURLConnection.authObj();
      if (localNTLMAuthSequence == null)
      {
        localNTLMAuthSequence = new NTLMAuthSequence(username, password, ntdomain);
        paramHttpURLConnection.authObj(localNTLMAuthSequence);
      }
      String str = "NTLM " + localNTLMAuthSequence.getAuthHeader(paramString.length() > 6 ? paramString.substring(5) : null);
      paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
      if (localNTLMAuthSequence.isComplete()) {
        paramHttpURLConnection.authObj(null);
      }
      return true;
    }
    catch (IOException localIOException)
    {
      paramHttpURLConnection.authObj(null);
    }
    return false;
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.ntlm.cache", "true"));
    ntlmCache = Boolean.parseBoolean(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\ntlm\NTLMAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
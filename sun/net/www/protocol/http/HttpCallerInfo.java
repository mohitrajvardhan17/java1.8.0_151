package sun.net.www.protocol.http;

import java.net.Authenticator.RequestorType;
import java.net.InetAddress;
import java.net.URL;

public final class HttpCallerInfo
{
  public final URL url;
  public final String host;
  public final String protocol;
  public final String prompt;
  public final String scheme;
  public final int port;
  public final InetAddress addr;
  public final Authenticator.RequestorType authType;
  
  public HttpCallerInfo(HttpCallerInfo paramHttpCallerInfo, String paramString)
  {
    url = url;
    host = host;
    protocol = protocol;
    prompt = prompt;
    port = port;
    addr = addr;
    authType = authType;
    scheme = paramString;
  }
  
  public HttpCallerInfo(URL paramURL)
  {
    url = paramURL;
    prompt = "";
    host = paramURL.getHost();
    int i = paramURL.getPort();
    if (i == -1) {
      port = paramURL.getDefaultPort();
    } else {
      port = i;
    }
    InetAddress localInetAddress;
    try
    {
      localInetAddress = InetAddress.getByName(paramURL.getHost());
    }
    catch (Exception localException)
    {
      localInetAddress = null;
    }
    addr = localInetAddress;
    protocol = paramURL.getProtocol();
    authType = Authenticator.RequestorType.SERVER;
    scheme = "";
  }
  
  public HttpCallerInfo(URL paramURL, String paramString, int paramInt)
  {
    url = paramURL;
    host = paramString;
    port = paramInt;
    prompt = "";
    addr = null;
    protocol = paramURL.getProtocol();
    authType = Authenticator.RequestorType.PROXY;
    scheme = "";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\HttpCallerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler
  extends URLStreamHandler
{
  protected String proxy;
  protected int proxyPort;
  
  protected int getDefaultPort()
  {
    return 80;
  }
  
  public Handler()
  {
    proxy = null;
    proxyPort = -1;
  }
  
  public Handler(String paramString, int paramInt)
  {
    proxy = paramString;
    proxyPort = paramInt;
  }
  
  protected URLConnection openConnection(URL paramURL)
    throws IOException
  {
    return openConnection(paramURL, (Proxy)null);
  }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy)
    throws IOException
  {
    return new HttpURLConnection(paramURL, paramProxy, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
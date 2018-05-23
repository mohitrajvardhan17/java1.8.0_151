package sun.awt.image;

import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import sun.net.util.URLUtil;

public class URLImageSource
  extends InputStreamImageSource
{
  URL url;
  URLConnection conn;
  String actualHost;
  int actualPort;
  
  public URLImageSource(URL paramURL)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        Permission localPermission = URLUtil.getConnectPermission(paramURL);
        if (localPermission != null) {
          try
          {
            localSecurityManager.checkPermission(localPermission);
          }
          catch (SecurityException localSecurityException)
          {
            if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1)) {
              localSecurityManager.checkRead(localPermission.getName());
            } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1)) {
              localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
            } else {
              throw localSecurityException;
            }
          }
        }
      }
      catch (IOException localIOException)
      {
        localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
      }
    }
    url = paramURL;
  }
  
  public URLImageSource(String paramString)
    throws MalformedURLException
  {
    this(new URL(null, paramString));
  }
  
  public URLImageSource(URL paramURL, URLConnection paramURLConnection)
  {
    this(paramURL);
    conn = paramURLConnection;
  }
  
  public URLImageSource(URLConnection paramURLConnection)
  {
    this(paramURLConnection.getURL(), paramURLConnection);
  }
  
  final boolean checkSecurity(Object paramObject, boolean paramBoolean)
  {
    if (actualHost != null) {
      try
      {
        SecurityManager localSecurityManager = System.getSecurityManager();
        if (localSecurityManager != null) {
          localSecurityManager.checkConnect(actualHost, actualPort, paramObject);
        }
      }
      catch (SecurityException localSecurityException)
      {
        if (!paramBoolean) {
          throw localSecurityException;
        }
        return false;
      }
    }
    return true;
  }
  
  private synchronized URLConnection getConnection()
    throws IOException
  {
    URLConnection localURLConnection;
    if (conn != null)
    {
      localURLConnection = conn;
      conn = null;
    }
    else
    {
      localURLConnection = url.openConnection();
    }
    return localURLConnection;
  }
  
  protected ImageDecoder getDecoder()
  {
    InputStream localInputStream = null;
    String str = null;
    URLConnection localURLConnection = null;
    try
    {
      localURLConnection = getConnection();
      localInputStream = localURLConnection.getInputStream();
      str = localURLConnection.getContentType();
      URL localURL = localURLConnection.getURL();
      if ((localURL != url) && ((!localURL.getHost().equals(url.getHost())) || (localURL.getPort() != url.getPort())))
      {
        if ((actualHost != null) && ((!actualHost.equals(localURL.getHost())) || (actualPort != localURL.getPort()))) {
          throw new SecurityException("image moved!");
        }
        actualHost = localURL.getHost();
        actualPort = localURL.getPort();
      }
    }
    catch (IOException localIOException1)
    {
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException2) {}
      } else if ((localURLConnection instanceof HttpURLConnection)) {
        ((HttpURLConnection)localURLConnection).disconnect();
      }
      return null;
    }
    ImageDecoder localImageDecoder = decoderForType(localInputStream, str);
    if (localImageDecoder == null) {
      localImageDecoder = getDecoder(localInputStream);
    }
    if (localImageDecoder == null) {
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException3) {}
      } else if ((localURLConnection instanceof HttpURLConnection)) {
        ((HttpURLConnection)localURLConnection).disconnect();
      }
    }
    return localImageDecoder;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\URLImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
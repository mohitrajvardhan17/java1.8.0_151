package sun.net.www.protocol.ftp;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler
  extends URLStreamHandler
{
  public Handler() {}
  
  protected int getDefaultPort()
  {
    return 21;
  }
  
  protected boolean equals(URL paramURL1, URL paramURL2)
  {
    String str1 = paramURL1.getUserInfo();
    String str2 = paramURL2.getUserInfo();
    return (super.equals(paramURL1, paramURL2)) && (str1 == null ? str2 == null : str1.equals(str2));
  }
  
  protected URLConnection openConnection(URL paramURL)
    throws IOException
  {
    return openConnection(paramURL, null);
  }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy)
    throws IOException
  {
    return new FtpURLConnection(paramURL, paramProxy);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\ftp\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
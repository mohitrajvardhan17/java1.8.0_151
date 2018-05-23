package sun.net.www.protocol.mailto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Permission;
import sun.net.smtp.SmtpClient;
import sun.net.www.MessageHeader;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;

public class MailToURLConnection
  extends URLConnection
{
  InputStream is = null;
  OutputStream os = null;
  SmtpClient client;
  Permission permission;
  private int connectTimeout = -1;
  private int readTimeout = -1;
  
  MailToURLConnection(URL paramURL)
  {
    super(paramURL);
    MessageHeader localMessageHeader = new MessageHeader();
    localMessageHeader.add("content-type", "text/html");
    setProperties(localMessageHeader);
  }
  
  String getFromAddress()
  {
    String str1 = System.getProperty("user.fromaddr");
    if (str1 == null)
    {
      str1 = System.getProperty("user.name");
      if (str1 != null)
      {
        String str2 = System.getProperty("mail.host");
        if (str2 == null) {
          try
          {
            str2 = InetAddress.getLocalHost().getHostName();
          }
          catch (UnknownHostException localUnknownHostException) {}
        }
        str1 = str1 + "@" + str2;
      }
      else
      {
        str1 = "";
      }
    }
    return str1;
  }
  
  public void connect()
    throws IOException
  {
    client = new SmtpClient(connectTimeout);
    client.setReadTimeout(readTimeout);
  }
  
  public synchronized OutputStream getOutputStream()
    throws IOException
  {
    if (os != null) {
      return os;
    }
    if (is != null) {
      throw new IOException("Cannot write output after reading input.");
    }
    connect();
    String str = ParseUtil.decode(url.getPath());
    client.from(getFromAddress());
    client.to(str);
    os = client.startMessage();
    return os;
  }
  
  public Permission getPermission()
    throws IOException
  {
    if (permission == null)
    {
      connect();
      String str = client.getMailHost() + ":" + 25;
      permission = new SocketPermission(str, "connect");
    }
    return permission;
  }
  
  public void setConnectTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("timeouts can't be negative");
    }
    connectTimeout = paramInt;
  }
  
  public int getConnectTimeout()
  {
    return connectTimeout < 0 ? 0 : connectTimeout;
  }
  
  public void setReadTimeout(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("timeouts can't be negative");
    }
    readTimeout = paramInt;
  }
  
  public int getReadTimeout()
  {
    return readTimeout < 0 ? 0 : readTimeout;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\mailto\MailToURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.rmi.transport.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.AccessController;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

class HttpSendSocket
  extends Socket
  implements RMISocketInfo
{
  protected String host;
  protected int port;
  protected URL url;
  protected URLConnection conn = null;
  protected InputStream in = null;
  protected OutputStream out = null;
  protected HttpSendInputStream inNotifier;
  protected HttpSendOutputStream outNotifier;
  private String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  
  public HttpSendSocket(String paramString, int paramInt, URL paramURL)
    throws IOException
  {
    super((SocketImpl)null);
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "host = " + paramString + ", port = " + paramInt + ", url = " + paramURL);
    }
    host = paramString;
    port = paramInt;
    url = paramURL;
    inNotifier = new HttpSendInputStream(null, this);
    outNotifier = new HttpSendOutputStream(writeNotify(), this);
  }
  
  public HttpSendSocket(String paramString, int paramInt)
    throws IOException
  {
    this(paramString, paramInt, new URL("http", paramString, paramInt, "/"));
  }
  
  public HttpSendSocket(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    this(paramInetAddress.getHostName(), paramInt);
  }
  
  public boolean isReusable()
  {
    return false;
  }
  
  public synchronized OutputStream writeNotify()
    throws IOException
  {
    if (conn != null) {
      throw new IOException("attempt to write on HttpSendSocket after request has been sent");
    }
    conn = url.openConnection();
    conn.setDoOutput(true);
    conn.setUseCaches(false);
    conn.setRequestProperty("Content-type", "application/octet-stream");
    inNotifier.deactivate();
    in = null;
    return out = conn.getOutputStream();
  }
  
  public synchronized InputStream readNotify()
    throws IOException
  {
    RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "sending request and activating input stream");
    outNotifier.deactivate();
    out.close();
    out = null;
    try
    {
      in = conn.getInputStream();
    }
    catch (IOException localIOException1)
    {
      RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "failed to get input stream, exception: ", localIOException1);
      throw new IOException("HTTP request failed");
    }
    String str1 = conn.getContentType();
    if ((str1 == null) || (!conn.getContentType().equals("application/octet-stream")))
    {
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF))
      {
        if (str1 == null) {
          str2 = "missing content type in response" + lineSeparator;
        } else {
          str2 = "invalid content type in response: " + str1 + lineSeparator;
        }
        String str2 = str2 + "HttpSendSocket.readNotify: response body: ";
        try
        {
          BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(in));
          String str3;
          while ((str3 = localBufferedReader.readLine()) != null) {
            str2 = str2 + str3 + lineSeparator;
          }
        }
        catch (IOException localIOException2) {}
        RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, str2);
      }
      throw new IOException("HTTP request failed");
    }
    return in;
  }
  
  public InetAddress getInetAddress()
  {
    try
    {
      return InetAddress.getByName(host);
    }
    catch (UnknownHostException localUnknownHostException) {}
    return null;
  }
  
  public InetAddress getLocalAddress()
  {
    try
    {
      return InetAddress.getLocalHost();
    }
    catch (UnknownHostException localUnknownHostException) {}
    return null;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public int getLocalPort()
  {
    return -1;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return inNotifier;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    return outNotifier;
  }
  
  public void setTcpNoDelay(boolean paramBoolean)
    throws SocketException
  {}
  
  public boolean getTcpNoDelay()
    throws SocketException
  {
    return false;
  }
  
  public void setSoLinger(boolean paramBoolean, int paramInt)
    throws SocketException
  {}
  
  public int getSoLinger()
    throws SocketException
  {
    return -1;
  }
  
  public synchronized void setSoTimeout(int paramInt)
    throws SocketException
  {}
  
  public synchronized int getSoTimeout()
    throws SocketException
  {
    return 0;
  }
  
  public synchronized void close()
    throws IOException
  {
    if (out != null) {
      out.close();
    }
  }
  
  public String toString()
  {
    return "HttpSendSocket[host=" + host + ",port=" + port + ",url=" + url + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\HttpSendSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
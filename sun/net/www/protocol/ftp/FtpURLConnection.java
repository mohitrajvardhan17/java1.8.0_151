package sun.net.www.protocol.ftp;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpLoginException;
import sun.net.ftp.FtpProtocolException;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;

public class FtpURLConnection
  extends URLConnection
{
  HttpURLConnection http = null;
  private Proxy instProxy;
  InputStream is = null;
  OutputStream os = null;
  FtpClient ftp = null;
  Permission permission;
  String password;
  String user;
  String host;
  String pathname;
  String filename;
  String fullpath;
  int port;
  static final int NONE = 0;
  static final int ASCII = 1;
  static final int BIN = 2;
  static final int DIR = 3;
  int type = 0;
  private int connectTimeout = -1;
  private int readTimeout = -1;
  
  public FtpURLConnection(URL paramURL)
  {
    this(paramURL, null);
  }
  
  FtpURLConnection(URL paramURL, Proxy paramProxy)
  {
    super(paramURL);
    instProxy = paramProxy;
    host = paramURL.getHost();
    port = paramURL.getPort();
    String str = paramURL.getUserInfo();
    if (str != null)
    {
      int i = str.indexOf(':');
      if (i == -1)
      {
        user = ParseUtil.decode(str);
        password = null;
      }
      else
      {
        user = ParseUtil.decode(str.substring(0, i++));
        password = ParseUtil.decode(str.substring(i));
      }
    }
  }
  
  private void setTimeouts()
  {
    if (ftp != null)
    {
      if (connectTimeout >= 0) {
        ftp.setConnectTimeout(connectTimeout);
      }
      if (readTimeout >= 0) {
        ftp.setReadTimeout(readTimeout);
      }
    }
  }
  
  public synchronized void connect()
    throws IOException
  {
    if (connected) {
      return;
    }
    Proxy localProxy = null;
    Object localObject;
    if (instProxy == null)
    {
      localObject = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ProxySelector run()
        {
          return ProxySelector.getDefault();
        }
      });
      if (localObject != null)
      {
        URI localURI = ParseUtil.toURI(url);
        Iterator localIterator = ((ProxySelector)localObject).select(localURI).iterator();
        while (localIterator.hasNext())
        {
          localProxy = (Proxy)localIterator.next();
          if ((localProxy == null) || (localProxy == Proxy.NO_PROXY) || (localProxy.type() == Proxy.Type.SOCKS)) {
            break;
          }
          if ((localProxy.type() != Proxy.Type.HTTP) || (!(localProxy.address() instanceof InetSocketAddress)))
          {
            ((ProxySelector)localObject).connectFailed(localURI, localProxy.address(), new IOException("Wrong proxy type"));
          }
          else
          {
            InetSocketAddress localInetSocketAddress = (InetSocketAddress)localProxy.address();
            try
            {
              http = new HttpURLConnection(url, localProxy);
              http.setDoInput(getDoInput());
              http.setDoOutput(getDoOutput());
              if (connectTimeout >= 0) {
                http.setConnectTimeout(connectTimeout);
              }
              if (readTimeout >= 0) {
                http.setReadTimeout(readTimeout);
              }
              http.connect();
              connected = true;
              return;
            }
            catch (IOException localIOException2)
            {
              ((ProxySelector)localObject).connectFailed(localURI, localInetSocketAddress, localIOException2);
              http = null;
            }
          }
        }
      }
    }
    else
    {
      localProxy = instProxy;
      if (localProxy.type() == Proxy.Type.HTTP)
      {
        http = new HttpURLConnection(url, instProxy);
        http.setDoInput(getDoInput());
        http.setDoOutput(getDoOutput());
        if (connectTimeout >= 0) {
          http.setConnectTimeout(connectTimeout);
        }
        if (readTimeout >= 0) {
          http.setReadTimeout(readTimeout);
        }
        http.connect();
        connected = true;
        return;
      }
    }
    if (user == null)
    {
      user = "anonymous";
      localObject = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
      password = ((String)AccessController.doPrivileged(new GetPropertyAction("ftp.protocol.user", "Java" + (String)localObject + "@")));
    }
    try
    {
      ftp = FtpClient.create();
      if (localProxy != null) {
        ftp.setProxy(localProxy);
      }
      setTimeouts();
      if (port != -1) {
        ftp.connect(new InetSocketAddress(host, port));
      } else {
        ftp.connect(new InetSocketAddress(host, FtpClient.defaultPort()));
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      throw localUnknownHostException;
    }
    catch (FtpProtocolException localFtpProtocolException1)
    {
      if (ftp != null) {
        try
        {
          ftp.close();
        }
        catch (IOException localIOException1)
        {
          localFtpProtocolException1.addSuppressed(localIOException1);
        }
      }
      throw new IOException(localFtpProtocolException1);
    }
    try
    {
      ftp.login(user, password == null ? null : password.toCharArray());
    }
    catch (FtpProtocolException localFtpProtocolException2)
    {
      ftp.close();
      throw new FtpLoginException("Invalid username/password");
    }
    connected = true;
  }
  
  private void decodePath(String paramString)
  {
    int i = paramString.indexOf(";type=");
    if (i >= 0)
    {
      String str = paramString.substring(i + 6, paramString.length());
      if ("i".equalsIgnoreCase(str)) {
        type = 2;
      }
      if ("a".equalsIgnoreCase(str)) {
        type = 1;
      }
      if ("d".equalsIgnoreCase(str)) {
        type = 3;
      }
      paramString = paramString.substring(0, i);
    }
    if ((paramString != null) && (paramString.length() > 1) && (paramString.charAt(0) == '/')) {
      paramString = paramString.substring(1);
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      paramString = "./";
    }
    if (!paramString.endsWith("/"))
    {
      i = paramString.lastIndexOf('/');
      if (i > 0)
      {
        filename = paramString.substring(i + 1, paramString.length());
        filename = ParseUtil.decode(filename);
        pathname = paramString.substring(0, i);
      }
      else
      {
        filename = ParseUtil.decode(paramString);
        pathname = null;
      }
    }
    else
    {
      pathname = paramString.substring(0, paramString.length() - 1);
      filename = null;
    }
    if (pathname != null) {
      fullpath = (pathname + "/" + (filename != null ? filename : ""));
    } else {
      fullpath = filename;
    }
  }
  
  private void cd(String paramString)
    throws FtpProtocolException, IOException
  {
    if ((paramString == null) || (paramString.isEmpty())) {
      return;
    }
    if (paramString.indexOf('/') == -1)
    {
      ftp.changeDirectory(ParseUtil.decode(paramString));
      return;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "/");
    while (localStringTokenizer.hasMoreTokens()) {
      ftp.changeDirectory(ParseUtil.decode(localStringTokenizer.nextToken()));
    }
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (!connected) {
      connect();
    }
    if (http != null) {
      return http.getInputStream();
    }
    if (os != null) {
      throw new IOException("Already opened for output");
    }
    if (is != null) {
      return is;
    }
    MessageHeader localMessageHeader = new MessageHeader();
    int i = 0;
    try
    {
      decodePath(url.getPath());
      if ((filename == null) || (type == 3))
      {
        ftp.setAsciiType();
        cd(pathname);
        if (filename == null) {
          is = new FtpInputStream(ftp, ftp.list(null));
        } else {
          is = new FtpInputStream(ftp, ftp.nameList(filename));
        }
      }
      else
      {
        if (type == 1) {
          ftp.setAsciiType();
        } else {
          ftp.setBinaryType();
        }
        cd(pathname);
        is = new FtpInputStream(ftp, ftp.getFileStream(filename));
      }
      try
      {
        long l = ftp.getLastTransferSize();
        localMessageHeader.add("content-length", Long.toString(l));
        if (l > 0L)
        {
          boolean bool = ProgressMonitor.getDefault().shouldMeterInput(url, "GET");
          ProgressSource localProgressSource = null;
          if (bool)
          {
            localProgressSource = new ProgressSource(url, "GET", l);
            localProgressSource.beginTracking();
          }
          is = new MeteredStream(is, localProgressSource, l);
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      if (i != 0)
      {
        localMessageHeader.add("content-type", "text/plain");
        localMessageHeader.add("access-type", "directory");
      }
      else
      {
        localMessageHeader.add("access-type", "file");
        String str = guessContentTypeFromName(fullpath);
        if ((str == null) && (is.markSupported())) {
          str = guessContentTypeFromStream(is);
        }
        if (str != null) {
          localMessageHeader.add("content-type", str);
        }
      }
    }
    catch (FileNotFoundException localFileNotFoundException1)
    {
      try
      {
        cd(fullpath);
        ftp.setAsciiType();
        is = new FtpInputStream(ftp, ftp.list(null));
        localMessageHeader.add("content-type", "text/plain");
        localMessageHeader.add("access-type", "directory");
      }
      catch (IOException localIOException1)
      {
        localFileNotFoundException2 = new FileNotFoundException(fullpath);
        if (ftp != null) {
          try
          {
            ftp.close();
          }
          catch (IOException localIOException3)
          {
            localFileNotFoundException2.addSuppressed(localIOException3);
          }
        }
        throw localFileNotFoundException2;
      }
      catch (FtpProtocolException localFtpProtocolException2)
      {
        FileNotFoundException localFileNotFoundException2 = new FileNotFoundException(fullpath);
        if (ftp != null) {
          try
          {
            ftp.close();
          }
          catch (IOException localIOException4)
          {
            localFileNotFoundException2.addSuppressed(localIOException4);
          }
        }
        throw localFileNotFoundException2;
      }
    }
    catch (FtpProtocolException localFtpProtocolException1)
    {
      if (ftp != null) {
        try
        {
          ftp.close();
        }
        catch (IOException localIOException2)
        {
          localFtpProtocolException1.addSuppressed(localIOException2);
        }
      }
      throw new IOException(localFtpProtocolException1);
    }
    setProperties(localMessageHeader);
    return is;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (!connected) {
      connect();
    }
    if (http != null)
    {
      OutputStream localOutputStream = http.getOutputStream();
      http.getInputStream();
      return localOutputStream;
    }
    if (is != null) {
      throw new IOException("Already opened for input");
    }
    if (os != null) {
      return os;
    }
    decodePath(url.getPath());
    if ((filename == null) || (filename.length() == 0)) {
      throw new IOException("illegal filename for a PUT");
    }
    try
    {
      if (pathname != null) {
        cd(pathname);
      }
      if (type == 1) {
        ftp.setAsciiType();
      } else {
        ftp.setBinaryType();
      }
      os = new FtpOutputStream(ftp, ftp.putFileStream(filename, false));
    }
    catch (FtpProtocolException localFtpProtocolException)
    {
      throw new IOException(localFtpProtocolException);
    }
    return os;
  }
  
  String guessContentTypeFromFilename(String paramString)
  {
    return guessContentTypeFromName(paramString);
  }
  
  public Permission getPermission()
  {
    if (permission == null)
    {
      int i = url.getPort();
      i = i < 0 ? FtpClient.defaultPort() : i;
      String str = host + ":" + i;
      permission = new SocketPermission(str, "connect");
    }
    return permission;
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    super.setRequestProperty(paramString1, paramString2);
    if ("type".equals(paramString1)) {
      if ("i".equalsIgnoreCase(paramString2)) {
        type = 2;
      } else if ("a".equalsIgnoreCase(paramString2)) {
        type = 1;
      } else if ("d".equalsIgnoreCase(paramString2)) {
        type = 3;
      } else {
        throw new IllegalArgumentException("Value of '" + paramString1 + "' request property was '" + paramString2 + "' when it must be either 'i', 'a' or 'd'");
      }
    }
  }
  
  public String getRequestProperty(String paramString)
  {
    String str = super.getRequestProperty(paramString);
    if ((str == null) && ("type".equals(paramString))) {
      str = type == 3 ? "d" : type == 1 ? "a" : "i";
    }
    return str;
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
  
  protected class FtpInputStream
    extends FilterInputStream
  {
    FtpClient ftp;
    
    FtpInputStream(FtpClient paramFtpClient, InputStream paramInputStream)
    {
      super();
      ftp = paramFtpClient;
    }
    
    public void close()
      throws IOException
    {
      super.close();
      if (ftp != null) {
        ftp.close();
      }
    }
  }
  
  protected class FtpOutputStream
    extends FilterOutputStream
  {
    FtpClient ftp;
    
    FtpOutputStream(FtpClient paramFtpClient, OutputStream paramOutputStream)
    {
      super();
      ftp = paramFtpClient;
    }
    
    public void close()
      throws IOException
    {
      super.close();
      if (ftp != null) {
        ftp.close();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\ftp\FtpURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
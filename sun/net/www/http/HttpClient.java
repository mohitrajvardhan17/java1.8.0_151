package sun.net.www.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.CookieHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import sun.net.NetworkClient;
import sun.net.ProgressSource;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.net.www.protocol.http.HttpURLConnection.TunnelState;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class HttpClient
  extends NetworkClient
{
  protected boolean cachedHttpClient = false;
  protected boolean inCache;
  MessageHeader requests;
  PosterOutputStream poster = null;
  boolean streaming;
  boolean failedOnce = false;
  private boolean ignoreContinue = true;
  private static final int HTTP_CONTINUE = 100;
  static final int httpPortNumber = 80;
  protected boolean proxyDisabled;
  public boolean usingProxy = false;
  protected String host;
  protected int port;
  protected static KeepAliveCache kac;
  private static boolean keepAliveProp;
  private static boolean retryPostProp;
  private static final boolean cacheNTLMProp;
  private static final boolean cacheSPNEGOProp;
  volatile boolean keepingAlive = false;
  volatile boolean disableKeepAlive;
  int keepAliveConnections = -1;
  int keepAliveTimeout = 0;
  private CacheRequest cacheRequest = null;
  protected URL url;
  public boolean reuse = false;
  private HttpCapture capture = null;
  private static final PlatformLogger logger;
  
  protected int getDefaultPort()
  {
    return 80;
  }
  
  private static int getDefaultPort(String paramString)
  {
    if ("http".equalsIgnoreCase(paramString)) {
      return 80;
    }
    if ("https".equalsIgnoreCase(paramString)) {
      return 443;
    }
    return -1;
  }
  
  private static void logFinest(String paramString)
  {
    if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
      logger.finest(paramString);
    }
  }
  
  @Deprecated
  public static synchronized void resetProperties() {}
  
  int getKeepAliveTimeout()
  {
    return keepAliveTimeout;
  }
  
  public boolean getHttpKeepAliveSet()
  {
    return keepAliveProp;
  }
  
  protected HttpClient() {}
  
  private HttpClient(URL paramURL)
    throws IOException
  {
    this(paramURL, (String)null, -1, false);
  }
  
  protected HttpClient(URL paramURL, boolean paramBoolean)
    throws IOException
  {
    this(paramURL, null, -1, paramBoolean);
  }
  
  public HttpClient(URL paramURL, String paramString, int paramInt)
    throws IOException
  {
    this(paramURL, paramString, paramInt, false);
  }
  
  protected HttpClient(URL paramURL, Proxy paramProxy, int paramInt)
    throws IOException
  {
    proxy = (paramProxy == null ? Proxy.NO_PROXY : paramProxy);
    host = paramURL.getHost();
    url = paramURL;
    port = paramURL.getPort();
    if (port == -1) {
      port = getDefaultPort();
    }
    setConnectTimeout(paramInt);
    capture = HttpCapture.getCapture(paramURL);
    openServer();
  }
  
  protected static Proxy newHttpProxy(String paramString1, int paramInt, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return Proxy.NO_PROXY;
    }
    int i = paramInt < 0 ? getDefaultPort(paramString2) : paramInt;
    InetSocketAddress localInetSocketAddress = InetSocketAddress.createUnresolved(paramString1, i);
    return new Proxy(Proxy.Type.HTTP, localInetSocketAddress);
  }
  
  private HttpClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    this(paramURL, paramBoolean ? Proxy.NO_PROXY : newHttpProxy(paramString, paramInt, "http"), -1);
  }
  
  public HttpClient(URL paramURL, String paramString, int paramInt1, boolean paramBoolean, int paramInt2)
    throws IOException
  {
    this(paramURL, paramBoolean ? Proxy.NO_PROXY : newHttpProxy(paramString, paramInt1, "http"), paramInt2);
  }
  
  public static HttpClient New(URL paramURL)
    throws IOException
  {
    return New(paramURL, Proxy.NO_PROXY, -1, true, null);
  }
  
  public static HttpClient New(URL paramURL, boolean paramBoolean)
    throws IOException
  {
    return New(paramURL, Proxy.NO_PROXY, -1, paramBoolean, null);
  }
  
  public static HttpClient New(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    if (paramProxy == null) {
      paramProxy = Proxy.NO_PROXY;
    }
    HttpClient localHttpClient = null;
    if (paramBoolean)
    {
      localHttpClient = kac.get(paramURL, null);
      if ((localHttpClient != null) && (paramHttpURLConnection != null) && (paramHttpURLConnection.streaming()) && (paramHttpURLConnection.getRequestMethod() == "POST") && (!localHttpClient.available()))
      {
        inCache = false;
        localHttpClient.closeServer();
        localHttpClient = null;
      }
      if (localHttpClient != null) {
        if (((proxy != null) && (proxy.equals(paramProxy))) || ((proxy == null) && (paramProxy == null)))
        {
          synchronized (localHttpClient)
          {
            cachedHttpClient = true;
            assert (inCache);
            inCache = false;
            if ((paramHttpURLConnection != null) && (localHttpClient.needsTunneling())) {
              paramHttpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
            }
            logFinest("KeepAlive stream retrieved from the cache, " + localHttpClient);
          }
        }
        else
        {
          synchronized (localHttpClient)
          {
            inCache = false;
            localHttpClient.closeServer();
          }
          localHttpClient = null;
        }
      }
    }
    if (localHttpClient == null)
    {
      localHttpClient = new HttpClient(paramURL, paramProxy, paramInt);
    }
    else
    {
      ??? = System.getSecurityManager();
      if (??? != null) {
        if ((proxy == Proxy.NO_PROXY) || (proxy == null)) {
          ((SecurityManager)???).checkConnect(InetAddress.getByName(paramURL.getHost()).getHostAddress(), paramURL.getPort());
        } else {
          ((SecurityManager)???).checkConnect(paramURL.getHost(), paramURL.getPort());
        }
      }
      url = paramURL;
    }
    return localHttpClient;
  }
  
  public static HttpClient New(URL paramURL, Proxy paramProxy, int paramInt, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramURL, paramProxy, paramInt, true, paramHttpURLConnection);
  }
  
  public static HttpClient New(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return New(paramURL, newHttpProxy(paramString, paramInt, "http"), -1, paramBoolean, null);
  }
  
  public static HttpClient New(URL paramURL, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramURL, newHttpProxy(paramString, paramInt1, "http"), paramInt2, paramBoolean, paramHttpURLConnection);
  }
  
  public void finished()
  {
    if (reuse) {
      return;
    }
    keepAliveConnections -= 1;
    poster = null;
    if ((keepAliveConnections > 0) && (isKeepingAlive()) && (!serverOutput.checkError())) {
      putInKeepAliveCache();
    } else {
      closeServer();
    }
  }
  
  protected synchronized boolean available()
  {
    boolean bool = true;
    int i = -1;
    try
    {
      try
      {
        i = serverSocket.getSoTimeout();
        serverSocket.setSoTimeout(1);
        BufferedInputStream localBufferedInputStream = new BufferedInputStream(serverSocket.getInputStream());
        int j = localBufferedInputStream.read();
        if (j == -1)
        {
          logFinest("HttpClient.available(): read returned -1: not available");
          bool = false;
        }
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
        logFinest("HttpClient.available(): SocketTimeout: its available");
      }
      finally
      {
        if (i != -1) {
          serverSocket.setSoTimeout(i);
        }
      }
    }
    catch (IOException localIOException)
    {
      logFinest("HttpClient.available(): SocketException: not available");
      bool = false;
    }
    return bool;
  }
  
  protected synchronized void putInKeepAliveCache()
  {
    if (inCache)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError("Duplicate put to keep alive cache");
      }
      return;
    }
    inCache = true;
    kac.put(url, null, this);
  }
  
  protected synchronized boolean isInKeepAliveCache()
  {
    return inCache;
  }
  
  public void closeIdleConnection()
  {
    HttpClient localHttpClient = kac.get(url, null);
    if (localHttpClient != null) {
      localHttpClient.closeServer();
    }
  }
  
  public void openServer(String paramString, int paramInt)
    throws IOException
  {
    serverSocket = doConnect(paramString, paramInt);
    try
    {
      Object localObject = serverSocket.getOutputStream();
      if (capture != null) {
        localObject = new HttpCaptureOutputStream((OutputStream)localObject, capture);
      }
      serverOutput = new PrintStream(new BufferedOutputStream((OutputStream)localObject), false, encoding);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(encoding + " encoding not found", localUnsupportedEncodingException);
    }
    serverSocket.setTcpNoDelay(true);
  }
  
  public boolean needsTunneling()
  {
    return false;
  }
  
  public synchronized boolean isCachedConnection()
  {
    return cachedHttpClient;
  }
  
  public void afterConnect()
    throws IOException, UnknownHostException
  {}
  
  private synchronized void privilegedOpenServer(final InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws IOException
        {
          openServer(paramInetSocketAddress.getHostString(), paramInetSocketAddress.getPort());
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  private void superOpenServer(String paramString, int paramInt)
    throws IOException, UnknownHostException
  {
    super.openServer(paramString, paramInt);
  }
  
  protected synchronized void openServer()
    throws IOException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkConnect(host, port);
    }
    if (keepingAlive) {
      return;
    }
    if ((url.getProtocol().equals("http")) || (url.getProtocol().equals("https")))
    {
      if ((proxy != null) && (proxy.type() == Proxy.Type.HTTP))
      {
        URLConnection.setProxiedHost(host);
        privilegedOpenServer((InetSocketAddress)proxy.address());
        usingProxy = true;
        return;
      }
      openServer(host, port);
      usingProxy = false;
      return;
    }
    if ((proxy != null) && (proxy.type() == Proxy.Type.HTTP))
    {
      URLConnection.setProxiedHost(host);
      privilegedOpenServer((InetSocketAddress)proxy.address());
      usingProxy = true;
      return;
    }
    super.openServer(host, port);
    usingProxy = false;
  }
  
  public String getURLFile()
    throws IOException
  {
    String str;
    if ((usingProxy) && (!proxyDisabled))
    {
      StringBuffer localStringBuffer = new StringBuffer(128);
      localStringBuffer.append(url.getProtocol());
      localStringBuffer.append(":");
      if ((url.getAuthority() != null) && (url.getAuthority().length() > 0))
      {
        localStringBuffer.append("//");
        localStringBuffer.append(url.getAuthority());
      }
      if (url.getPath() != null) {
        localStringBuffer.append(url.getPath());
      }
      if (url.getQuery() != null)
      {
        localStringBuffer.append('?');
        localStringBuffer.append(url.getQuery());
      }
      str = localStringBuffer.toString();
    }
    else
    {
      str = url.getFile();
      if ((str == null) || (str.length() == 0)) {
        str = "/";
      } else if (str.charAt(0) == '?') {
        str = "/" + str;
      }
    }
    if (str.indexOf('\n') == -1) {
      return str;
    }
    throw new MalformedURLException("Illegal character in URL");
  }
  
  @Deprecated
  public void writeRequests(MessageHeader paramMessageHeader)
  {
    requests = paramMessageHeader;
    requests.print(serverOutput);
    serverOutput.flush();
  }
  
  public void writeRequests(MessageHeader paramMessageHeader, PosterOutputStream paramPosterOutputStream)
    throws IOException
  {
    requests = paramMessageHeader;
    requests.print(serverOutput);
    poster = paramPosterOutputStream;
    if (poster != null) {
      poster.writeTo(serverOutput);
    }
    serverOutput.flush();
  }
  
  public void writeRequests(MessageHeader paramMessageHeader, PosterOutputStream paramPosterOutputStream, boolean paramBoolean)
    throws IOException
  {
    streaming = paramBoolean;
    writeRequests(paramMessageHeader, paramPosterOutputStream);
  }
  
  public boolean parseHTTP(MessageHeader paramMessageHeader, ProgressSource paramProgressSource, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    try
    {
      serverInput = serverSocket.getInputStream();
      if (capture != null) {
        serverInput = new HttpCaptureInputStream(serverInput, capture);
      }
      serverInput = new BufferedInputStream(serverInput);
      return parseHTTPHeader(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
    }
    catch (SocketTimeoutException localSocketTimeoutException)
    {
      if (ignoreContinue) {
        closeServer();
      }
      throw localSocketTimeoutException;
    }
    catch (IOException localIOException)
    {
      closeServer();
      cachedHttpClient = false;
      if ((!failedOnce) && (requests != null))
      {
        failedOnce = true;
        if ((!getRequestMethod().equals("CONNECT")) && (!streaming) && ((!paramHttpURLConnection.getRequestMethod().equals("POST")) || (retryPostProp)))
        {
          openServer();
          if (needsTunneling())
          {
            MessageHeader localMessageHeader = requests;
            paramHttpURLConnection.doTunneling();
            requests = localMessageHeader;
          }
          afterConnect();
          writeRequests(requests, poster);
          return parseHTTP(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
        }
      }
      throw localIOException;
    }
  }
  
  private boolean parseHTTPHeader(MessageHeader paramMessageHeader, ProgressSource paramProgressSource, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    keepAliveConnections = -1;
    keepAliveTimeout = 0;
    boolean bool = false;
    byte[] arrayOfByte = new byte[8];
    String str1;
    try
    {
      int i = 0;
      serverInput.mark(10);
      while (i < 8)
      {
        int k = serverInput.read(arrayOfByte, i, 8 - i);
        if (k < 0) {
          break;
        }
        i += k;
      }
      str1 = null;
      String str2 = null;
      bool = (arrayOfByte[0] == 72) && (arrayOfByte[1] == 84) && (arrayOfByte[2] == 84) && (arrayOfByte[3] == 80) && (arrayOfByte[4] == 47) && (arrayOfByte[5] == 49) && (arrayOfByte[6] == 46);
      serverInput.reset();
      if (bool)
      {
        paramMessageHeader.parseHeader(serverInput);
        localObject = paramHttpURLConnection.getCookieHandler();
        if (localObject != null)
        {
          URI localURI = ParseUtil.toURI(url);
          if (localURI != null) {
            ((CookieHandler)localObject).put(localURI, paramMessageHeader.getHeaders());
          }
        }
        if (usingProxy)
        {
          str1 = paramMessageHeader.findValue("Proxy-Connection");
          str2 = paramMessageHeader.findValue("Proxy-Authenticate");
        }
        if (str1 == null)
        {
          str1 = paramMessageHeader.findValue("Connection");
          str2 = paramMessageHeader.findValue("WWW-Authenticate");
        }
        int n = !disableKeepAlive ? 1 : 0;
        if ((n != 0) && ((!cacheNTLMProp) || (!cacheSPNEGOProp)) && (str2 != null))
        {
          str2 = str2.toLowerCase(Locale.US);
          if (!cacheNTLMProp) {
            n &= (!str2.startsWith("ntlm ") ? 1 : 0);
          }
          if (!cacheSPNEGOProp)
          {
            n &= (!str2.startsWith("negotiate ") ? 1 : 0);
            n &= (!str2.startsWith("kerberos ") ? 1 : 0);
          }
        }
        disableKeepAlive |= n == 0;
        if ((str1 != null) && (str1.toLowerCase(Locale.US).equals("keep-alive")))
        {
          if (disableKeepAlive)
          {
            keepAliveConnections = 1;
          }
          else
          {
            HeaderParser localHeaderParser = new HeaderParser(paramMessageHeader.findValue("Keep-Alive"));
            keepAliveConnections = localHeaderParser.findInt("max", usingProxy ? 50 : 5);
            keepAliveTimeout = localHeaderParser.findInt("timeout", usingProxy ? 60 : 5);
          }
        }
        else if (arrayOfByte[7] != 48) {
          if ((str1 != null) || (disableKeepAlive)) {
            keepAliveConnections = 1;
          } else {
            keepAliveConnections = 5;
          }
        }
      }
      else
      {
        if (i != 8)
        {
          if ((!failedOnce) && (requests != null))
          {
            failedOnce = true;
            if ((!getRequestMethod().equals("CONNECT")) && (!streaming) && ((!paramHttpURLConnection.getRequestMethod().equals("POST")) || (retryPostProp)))
            {
              closeServer();
              cachedHttpClient = false;
              openServer();
              if (needsTunneling())
              {
                localObject = requests;
                paramHttpURLConnection.doTunneling();
                requests = ((MessageHeader)localObject);
              }
              afterConnect();
              writeRequests(requests, poster);
              return parseHTTP(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
            }
          }
          throw new SocketException("Unexpected end of file from server");
        }
        paramMessageHeader.set("Content-type", "unknown/unknown");
      }
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    int j = -1;
    try
    {
      str1 = paramMessageHeader.getValue(0);
      for (int m = str1.indexOf(' '); str1.charAt(m) == ' '; m++) {}
      j = Integer.parseInt(str1.substring(m, m + 3));
    }
    catch (Exception localException) {}
    if ((j == 100) && (ignoreContinue))
    {
      paramMessageHeader.reset();
      return parseHTTPHeader(paramMessageHeader, paramProgressSource, paramHttpURLConnection);
    }
    long l = -1L;
    Object localObject = paramMessageHeader.findValue("Transfer-Encoding");
    if ((localObject != null) && (((String)localObject).equalsIgnoreCase("chunked")))
    {
      serverInput = new ChunkedInputStream(serverInput, this, paramMessageHeader);
      if (keepAliveConnections <= 1)
      {
        keepAliveConnections = 1;
        keepingAlive = false;
      }
      else
      {
        keepingAlive = (!disableKeepAlive);
      }
      failedOnce = false;
    }
    else
    {
      String str3 = paramMessageHeader.findValue("content-length");
      if (str3 != null) {
        try
        {
          l = Long.parseLong(str3);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          l = -1L;
        }
      }
      String str4 = requests.getKey(0);
      if (((str4 != null) && (str4.startsWith("HEAD"))) || (j == 304) || (j == 204)) {
        l = 0L;
      }
      if ((keepAliveConnections > 1) && ((l >= 0L) || (j == 304) || (j == 204)))
      {
        keepingAlive = (!disableKeepAlive);
        failedOnce = false;
      }
      else if (keepingAlive)
      {
        keepingAlive = false;
      }
    }
    if (l > 0L)
    {
      if (paramProgressSource != null) {
        paramProgressSource.setContentType(paramMessageHeader.findValue("content-type"));
      }
      int i1 = (isKeepingAlive()) || (disableKeepAlive) ? 1 : 0;
      if (i1 != 0)
      {
        logFinest("KeepAlive stream used: " + url);
        serverInput = new KeepAliveStream(serverInput, paramProgressSource, l, this);
        failedOnce = false;
      }
      else
      {
        serverInput = new MeteredStream(serverInput, paramProgressSource, l);
      }
    }
    else if (l == -1L)
    {
      if (paramProgressSource != null)
      {
        paramProgressSource.setContentType(paramMessageHeader.findValue("content-type"));
        serverInput = new MeteredStream(serverInput, paramProgressSource, l);
      }
    }
    else if (paramProgressSource != null)
    {
      paramProgressSource.finishTracking();
    }
    return bool;
  }
  
  public synchronized InputStream getInputStream()
  {
    return serverInput;
  }
  
  public OutputStream getOutputStream()
  {
    return serverOutput;
  }
  
  public String toString()
  {
    return getClass().getName() + "(" + url + ")";
  }
  
  public final boolean isKeepingAlive()
  {
    return (getHttpKeepAliveSet()) && (keepingAlive);
  }
  
  public void setCacheRequest(CacheRequest paramCacheRequest)
  {
    cacheRequest = paramCacheRequest;
  }
  
  CacheRequest getCacheRequest()
  {
    return cacheRequest;
  }
  
  String getRequestMethod()
  {
    if (requests != null)
    {
      String str = requests.getKey(0);
      if (str != null) {
        return str.split("\\s+")[0];
      }
    }
    return "";
  }
  
  protected void finalize()
    throws Throwable
  {}
  
  public void setDoNotRetry(boolean paramBoolean)
  {
    failedOnce = paramBoolean;
  }
  
  public void setIgnoreContinue(boolean paramBoolean)
  {
    ignoreContinue = paramBoolean;
  }
  
  public void closeServer()
  {
    try
    {
      keepingAlive = false;
      serverSocket.close();
    }
    catch (Exception localException) {}
  }
  
  public String getProxyHostUsed()
  {
    if (!usingProxy) {
      return null;
    }
    return ((InetSocketAddress)proxy.address()).getHostString();
  }
  
  public int getProxyPortUsed()
  {
    if (usingProxy) {
      return ((InetSocketAddress)proxy.address()).getPort();
    }
    return -1;
  }
  
  static
  {
    kac = new KeepAliveCache();
    keepAliveProp = true;
    retryPostProp = true;
    logger = HttpURLConnection.getHttpLogger();
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("http.keepAlive"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.net.http.retryPost"));
    String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.ntlm.cache"));
    String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.spnego.cache"));
    if (str1 != null) {
      keepAliveProp = Boolean.valueOf(str1).booleanValue();
    } else {
      keepAliveProp = true;
    }
    if (str2 != null) {
      retryPostProp = Boolean.valueOf(str2).booleanValue();
    } else {
      retryPostProp = true;
    }
    if (str3 != null) {
      cacheNTLMProp = Boolean.parseBoolean(str3);
    } else {
      cacheNTLMProp = true;
    }
    if (str4 != null) {
      cacheSPNEGOProp = Boolean.parseBoolean(str4);
    } else {
      cacheSPNEGOProp = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\HttpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
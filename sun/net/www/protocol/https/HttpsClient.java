package sun.net.www.protocol.https;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.net.www.http.HttpClient;
import sun.net.www.http.KeepAliveCache;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.net.www.protocol.http.HttpURLConnection.TunnelState;
import sun.security.action.GetPropertyAction;
import sun.security.ssl.SSLSocketImpl;
import sun.security.util.HostnameChecker;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

final class HttpsClient
  extends HttpClient
  implements HandshakeCompletedListener
{
  private static final int httpsPortNumber = 443;
  private static final String defaultHVCanonicalName = "javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
  private HostnameVerifier hv;
  private SSLSocketFactory sslSocketFactory;
  private SSLSession session;
  
  protected int getDefaultPort()
  {
    return 443;
  }
  
  private String[] getCipherSuites()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.cipherSuites"));
    String[] arrayOfString;
    if ((str == null) || ("".equals(str)))
    {
      arrayOfString = null;
    }
    else
    {
      Vector localVector = new Vector();
      StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
      while (localStringTokenizer.hasMoreTokens()) {
        localVector.addElement(localStringTokenizer.nextToken());
      }
      arrayOfString = new String[localVector.size()];
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = ((String)localVector.elementAt(i));
      }
    }
    return arrayOfString;
  }
  
  private String[] getProtocols()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.protocols"));
    String[] arrayOfString;
    if ((str == null) || ("".equals(str)))
    {
      arrayOfString = null;
    }
    else
    {
      Vector localVector = new Vector();
      StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
      while (localStringTokenizer.hasMoreTokens()) {
        localVector.addElement(localStringTokenizer.nextToken());
      }
      arrayOfString = new String[localVector.size()];
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = ((String)localVector.elementAt(i));
      }
    }
    return arrayOfString;
  }
  
  private String getUserAgent()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("https.agent"));
    if ((str == null) || (str.length() == 0)) {
      str = "JSSE";
    }
    return str;
  }
  
  private HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL)
    throws IOException
  {
    this(paramSSLSocketFactory, paramURL, (String)null, -1);
  }
  
  HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt)
    throws IOException
  {
    this(paramSSLSocketFactory, paramURL, paramString, paramInt, -1);
  }
  
  HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    this(paramSSLSocketFactory, paramURL, paramString == null ? null : HttpClient.newHttpProxy(paramString, paramInt1, "https"), paramInt2);
  }
  
  HttpsClient(SSLSocketFactory paramSSLSocketFactory, URL paramURL, Proxy paramProxy, int paramInt)
    throws IOException
  {
    PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
    if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
      localPlatformLogger.finest("Creating new HttpsClient with url:" + paramURL + " and proxy:" + paramProxy + " with connect timeout:" + paramInt);
    }
    proxy = paramProxy;
    setSSLSocketFactory(paramSSLSocketFactory);
    proxyDisabled = true;
    host = paramURL.getHost();
    url = paramURL;
    port = paramURL.getPort();
    if (port == -1) {
      port = getDefaultPort();
    }
    setConnectTimeout(paramInt);
    openServer();
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, true, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, boolean paramBoolean, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, (String)null, -1, paramBoolean, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, true, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt, boolean paramBoolean, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString, paramInt, paramBoolean, -1, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    return New(paramSSLSocketFactory, paramURL, paramHostnameVerifier, paramString == null ? null : HttpClient.newHttpProxy(paramString, paramInt1, "https"), paramBoolean, paramInt2, paramHttpURLConnection);
  }
  
  static HttpClient New(SSLSocketFactory paramSSLSocketFactory, URL paramURL, HostnameVerifier paramHostnameVerifier, Proxy paramProxy, boolean paramBoolean, int paramInt, HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    if (paramProxy == null) {
      paramProxy = Proxy.NO_PROXY;
    }
    PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
    if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
      localPlatformLogger.finest("Looking for HttpClient for URL " + paramURL + " and proxy value of " + paramProxy);
    }
    HttpsClient localHttpsClient = null;
    if (paramBoolean)
    {
      localHttpsClient = (HttpsClient)kac.get(paramURL, paramSSLSocketFactory);
      if ((localHttpsClient != null) && (paramHttpURLConnection != null) && (paramHttpURLConnection.streaming()) && (paramHttpURLConnection.getRequestMethod() == "POST") && (!localHttpsClient.available())) {
        localHttpsClient = null;
      }
      if (localHttpsClient != null) {
        if (((proxy != null) && (proxy.equals(paramProxy))) || ((proxy == null) && (paramProxy == Proxy.NO_PROXY)))
        {
          synchronized (localHttpsClient)
          {
            cachedHttpClient = true;
            assert (inCache);
            inCache = false;
            if ((paramHttpURLConnection != null) && (localHttpsClient.needsTunneling())) {
              paramHttpURLConnection.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
            }
            if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
              localPlatformLogger.finest("KeepAlive stream retrieved from the cache, " + localHttpsClient);
            }
          }
        }
        else
        {
          synchronized (localHttpsClient)
          {
            if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
              localPlatformLogger.finest("Not returning this connection to cache: " + localHttpsClient);
            }
            inCache = false;
            localHttpsClient.closeServer();
          }
          localHttpsClient = null;
        }
      }
    }
    if (localHttpsClient == null)
    {
      localHttpsClient = new HttpsClient(paramSSLSocketFactory, paramURL, paramProxy, paramInt);
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
    localHttpsClient.setHostnameVerifier(paramHostnameVerifier);
    return localHttpsClient;
  }
  
  void setHostnameVerifier(HostnameVerifier paramHostnameVerifier)
  {
    hv = paramHostnameVerifier;
  }
  
  void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory)
  {
    sslSocketFactory = paramSSLSocketFactory;
  }
  
  SSLSocketFactory getSSLSocketFactory()
  {
    return sslSocketFactory;
  }
  
  protected Socket createSocket()
    throws IOException
  {
    try
    {
      return sslSocketFactory.createSocket();
    }
    catch (SocketException localSocketException)
    {
      Throwable localThrowable = localSocketException.getCause();
      if ((localThrowable != null) && ((localThrowable instanceof UnsupportedOperationException))) {
        return super.createSocket();
      }
      throw localSocketException;
    }
  }
  
  public boolean needsTunneling()
  {
    return (proxy != null) && (proxy.type() != Proxy.Type.DIRECT) && (proxy.type() != Proxy.Type.SOCKS);
  }
  
  public void afterConnect()
    throws IOException, UnknownHostException
  {
    if (!isCachedConnection())
    {
      SSLSocket localSSLSocket = null;
      SSLSocketFactory localSSLSocketFactory = sslSocketFactory;
      try
      {
        if (!(serverSocket instanceof SSLSocket))
        {
          localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(serverSocket, host, port, true);
        }
        else
        {
          localSSLSocket = (SSLSocket)serverSocket;
          if ((localSSLSocket instanceof SSLSocketImpl)) {
            ((SSLSocketImpl)localSSLSocket).setHost(host);
          }
        }
      }
      catch (IOException localIOException1)
      {
        try
        {
          localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(host, port);
        }
        catch (IOException localIOException2)
        {
          throw localIOException1;
        }
      }
      String[] arrayOfString1 = getProtocols();
      String[] arrayOfString2 = getCipherSuites();
      if (arrayOfString1 != null) {
        localSSLSocket.setEnabledProtocols(arrayOfString1);
      }
      if (arrayOfString2 != null) {
        localSSLSocket.setEnabledCipherSuites(arrayOfString2);
      }
      localSSLSocket.addHandshakeCompletedListener(this);
      int i = 1;
      String str = localSSLSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
      if ((str != null) && (str.length() != 0))
      {
        if (str.equalsIgnoreCase("HTTPS")) {
          i = 0;
        }
      }
      else
      {
        int j = 0;
        Object localObject;
        if (hv != null)
        {
          localObject = hv.getClass().getCanonicalName();
          if ((localObject != null) && (((String)localObject).equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier"))) {
            j = 1;
          }
        }
        else
        {
          j = 1;
        }
        if (j != 0)
        {
          localObject = localSSLSocket.getSSLParameters();
          ((SSLParameters)localObject).setEndpointIdentificationAlgorithm("HTTPS");
          localSSLSocket.setSSLParameters((SSLParameters)localObject);
          i = 0;
        }
      }
      localSSLSocket.startHandshake();
      session = localSSLSocket.getSession();
      serverSocket = localSSLSocket;
      try
      {
        serverOutput = new PrintStream(new BufferedOutputStream(serverSocket.getOutputStream()), false, encoding);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new InternalError(encoding + " encoding not found");
      }
      if (i != 0) {
        checkURLSpoofing(hv);
      }
    }
    else
    {
      session = ((SSLSocket)serverSocket).getSession();
    }
  }
  
  private void checkURLSpoofing(HostnameVerifier paramHostnameVerifier)
    throws IOException
  {
    String str1 = url.getHost();
    if ((str1 != null) && (str1.startsWith("[")) && (str1.endsWith("]"))) {
      str1 = str1.substring(1, str1.length() - 1);
    }
    Certificate[] arrayOfCertificate = null;
    String str2 = session.getCipherSuite();
    try
    {
      HostnameChecker localHostnameChecker = HostnameChecker.getInstance((byte)1);
      if (str2.startsWith("TLS_KRB5"))
      {
        if (!HostnameChecker.match(str1, getPeerPrincipal())) {
          throw new SSLPeerUnverifiedException("Hostname checker failed for Kerberos");
        }
      }
      else
      {
        arrayOfCertificate = session.getPeerCertificates();
        java.security.cert.X509Certificate localX509Certificate;
        if ((arrayOfCertificate[0] instanceof java.security.cert.X509Certificate)) {
          localX509Certificate = (java.security.cert.X509Certificate)arrayOfCertificate[0];
        } else {
          throw new SSLPeerUnverifiedException("");
        }
        localHostnameChecker.match(str1, localX509Certificate);
      }
      return;
    }
    catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {}catch (CertificateException localCertificateException) {}
    if ((str2 != null) && (str2.indexOf("_anon_") != -1)) {
      return;
    }
    if ((paramHostnameVerifier != null) && (paramHostnameVerifier.verify(str1, session))) {
      return;
    }
    serverSocket.close();
    session.invalidate();
    throw new IOException("HTTPS hostname wrong:  should be <" + url.getHost() + ">");
  }
  
  protected void putInKeepAliveCache()
  {
    if (inCache)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError("Duplicate put to keep alive cache");
      }
      return;
    }
    inCache = true;
    kac.put(url, sslSocketFactory, this);
  }
  
  public void closeIdleConnection()
  {
    HttpClient localHttpClient = kac.get(url, sslSocketFactory);
    if (localHttpClient != null) {
      localHttpClient.closeServer();
    }
  }
  
  String getCipherSuite()
  {
    return session.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates()
  {
    return session.getLocalCertificates();
  }
  
  Certificate[] getServerCertificates()
    throws SSLPeerUnverifiedException
  {
    return session.getPeerCertificates();
  }
  
  javax.security.cert.X509Certificate[] getServerCertificateChain()
    throws SSLPeerUnverifiedException
  {
    return session.getPeerCertificateChain();
  }
  
  Principal getPeerPrincipal()
    throws SSLPeerUnverifiedException
  {
    Object localObject;
    try
    {
      localObject = session.getPeerPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      Certificate[] arrayOfCertificate = session.getPeerCertificates();
      localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
    }
    return (Principal)localObject;
  }
  
  Principal getLocalPrincipal()
  {
    Object localObject;
    try
    {
      localObject = session.getLocalPrincipal();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      localObject = null;
      Certificate[] arrayOfCertificate = session.getLocalCertificates();
      if (arrayOfCertificate != null) {
        localObject = ((java.security.cert.X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
      }
    }
    return (Principal)localObject;
  }
  
  public void handshakeCompleted(HandshakeCompletedEvent paramHandshakeCompletedEvent)
  {
    session = paramHandshakeCompletedEvent.getSession();
  }
  
  public String getProxyHostUsed()
  {
    if (!needsTunneling()) {
      return null;
    }
    return super.getProxyHostUsed();
  }
  
  public int getProxyPortUsed()
  {
    return (proxy == null) || (proxy.type() == Proxy.Type.DIRECT) || (proxy.type() == Proxy.Type.SOCKS) ? -1 : ((InetSocketAddress)proxy.address()).getPort();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\https\HttpsClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
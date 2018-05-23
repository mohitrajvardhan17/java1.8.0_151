package sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.SecureCacheResponse;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.Handler;
import sun.net.www.protocol.http.HttpURLConnection;

public abstract class AbstractDelegateHttpsURLConnection
  extends HttpURLConnection
{
  protected AbstractDelegateHttpsURLConnection(URL paramURL, Handler paramHandler)
    throws IOException
  {
    this(paramURL, null, paramHandler);
  }
  
  protected AbstractDelegateHttpsURLConnection(URL paramURL, Proxy paramProxy, Handler paramHandler)
    throws IOException
  {
    super(paramURL, paramProxy, paramHandler);
  }
  
  protected abstract SSLSocketFactory getSSLSocketFactory();
  
  protected abstract HostnameVerifier getHostnameVerifier();
  
  public void setNewClient(URL paramURL)
    throws IOException
  {
    setNewClient(paramURL, false);
  }
  
  public void setNewClient(URL paramURL, boolean paramBoolean)
    throws IOException
  {
    http = HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramBoolean, this);
    ((HttpsClient)http).afterConnect();
  }
  
  public void setProxiedClient(URL paramURL, String paramString, int paramInt)
    throws IOException
  {
    setProxiedClient(paramURL, paramString, paramInt, false);
  }
  
  public void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    proxiedConnect(paramURL, paramString, paramInt, paramBoolean);
    if (!http.isCachedConnection()) {
      doTunneling();
    }
    ((HttpsClient)http).afterConnect();
  }
  
  protected void proxiedConnect(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (connected) {
      return;
    }
    http = HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramString, paramInt, paramBoolean, this);
    connected = true;
  }
  
  public boolean isConnected()
  {
    return connected;
  }
  
  public void setConnected(boolean paramBoolean)
  {
    connected = paramBoolean;
  }
  
  public void connect()
    throws IOException
  {
    if (connected) {
      return;
    }
    plainConnect();
    if (cachedResponse != null) {
      return;
    }
    if ((!http.isCachedConnection()) && (http.needsTunneling())) {
      doTunneling();
    }
    ((HttpsClient)http).afterConnect();
  }
  
  protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt)
    throws IOException
  {
    return HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramProxy, true, paramInt, this);
  }
  
  protected HttpClient getNewHttpClient(URL paramURL, Proxy paramProxy, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return HttpsClient.New(getSSLSocketFactory(), paramURL, getHostnameVerifier(), paramProxy, paramBoolean, paramInt, this);
  }
  
  public String getCipherSuite()
  {
    if (cachedResponse != null) {
      return ((SecureCacheResponse)cachedResponse).getCipherSuite();
    }
    if (http == null) {
      throw new IllegalStateException("connection not yet open");
    }
    return ((HttpsClient)http).getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates()
  {
    if (cachedResponse != null)
    {
      List localList = ((SecureCacheResponse)cachedResponse).getLocalCertificateChain();
      if (localList == null) {
        return null;
      }
      return (Certificate[])localList.toArray(new Certificate[0]);
    }
    if (http == null) {
      throw new IllegalStateException("connection not yet open");
    }
    return ((HttpsClient)http).getLocalCertificates();
  }
  
  public Certificate[] getServerCertificates()
    throws SSLPeerUnverifiedException
  {
    if (cachedResponse != null)
    {
      List localList = ((SecureCacheResponse)cachedResponse).getServerCertificateChain();
      if (localList == null) {
        return null;
      }
      return (Certificate[])localList.toArray(new Certificate[0]);
    }
    if (http == null) {
      throw new IllegalStateException("connection not yet open");
    }
    return ((HttpsClient)http).getServerCertificates();
  }
  
  public X509Certificate[] getServerCertificateChain()
    throws SSLPeerUnverifiedException
  {
    if (cachedResponse != null) {
      throw new UnsupportedOperationException("this method is not supported when using cache");
    }
    if (http == null) {
      throw new IllegalStateException("connection not yet open");
    }
    return ((HttpsClient)http).getServerCertificateChain();
  }
  
  Principal getPeerPrincipal()
    throws SSLPeerUnverifiedException
  {
    if (cachedResponse != null) {
      return ((SecureCacheResponse)cachedResponse).getPeerPrincipal();
    }
    if (http == null) {
      throw new IllegalStateException("connection not yet open");
    }
    return ((HttpsClient)http).getPeerPrincipal();
  }
  
  Principal getLocalPrincipal()
  {
    if (cachedResponse != null) {
      return ((SecureCacheResponse)cachedResponse).getLocalPrincipal();
    }
    if (http == null) {
      throw new IllegalStateException("connection not yet open");
    }
    return ((HttpsClient)http).getLocalPrincipal();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\https\AbstractDelegateHttpsURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
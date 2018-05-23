package com.sun.net.ssl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;

@Deprecated
public abstract class HttpsURLConnection
  extends HttpURLConnection
{
  private static HostnameVerifier defaultHostnameVerifier = new HostnameVerifier()
  {
    public boolean verify(String paramAnonymousString1, String paramAnonymousString2)
    {
      return false;
    }
  };
  protected HostnameVerifier hostnameVerifier = defaultHostnameVerifier;
  private static SSLSocketFactory defaultSSLSocketFactory = null;
  private SSLSocketFactory sslSocketFactory = getDefaultSSLSocketFactory();
  
  public HttpsURLConnection(URL paramURL)
    throws IOException
  {
    super(paramURL);
  }
  
  public abstract String getCipherSuite();
  
  public abstract X509Certificate[] getServerCertificateChain();
  
  public static void setDefaultHostnameVerifier(HostnameVerifier paramHostnameVerifier)
  {
    if (paramHostnameVerifier == null) {
      throw new IllegalArgumentException("no default HostnameVerifier specified");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SSLPermission("setHostnameVerifier"));
    }
    defaultHostnameVerifier = paramHostnameVerifier;
  }
  
  public static HostnameVerifier getDefaultHostnameVerifier()
  {
    return defaultHostnameVerifier;
  }
  
  public void setHostnameVerifier(HostnameVerifier paramHostnameVerifier)
  {
    if (paramHostnameVerifier == null) {
      throw new IllegalArgumentException("no HostnameVerifier specified");
    }
    hostnameVerifier = paramHostnameVerifier;
  }
  
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }
  
  public static void setDefaultSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory)
  {
    if (paramSSLSocketFactory == null) {
      throw new IllegalArgumentException("no default SSLSocketFactory specified");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    defaultSSLSocketFactory = paramSSLSocketFactory;
  }
  
  public static SSLSocketFactory getDefaultSSLSocketFactory()
  {
    if (defaultSSLSocketFactory == null) {
      defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    }
    return defaultSSLSocketFactory;
  }
  
  public void setSSLSocketFactory(SSLSocketFactory paramSSLSocketFactory)
  {
    if (paramSSLSocketFactory == null) {
      throw new IllegalArgumentException("no SSLSocketFactory specified");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    sslSocketFactory = paramSSLSocketFactory;
  }
  
  public SSLSocketFactory getSSLSocketFactory()
  {
    return sslSocketFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\HttpsURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

final class X509TrustManagerComSunWrapper
  implements X509TrustManager
{
  private javax.net.ssl.X509TrustManager theX509TrustManager;
  
  X509TrustManagerComSunWrapper(javax.net.ssl.X509TrustManager paramX509TrustManager)
  {
    theX509TrustManager = paramX509TrustManager;
  }
  
  public boolean isClientTrusted(X509Certificate[] paramArrayOfX509Certificate)
  {
    try
    {
      theX509TrustManager.checkClientTrusted(paramArrayOfX509Certificate, "UNKNOWN");
      return true;
    }
    catch (CertificateException localCertificateException) {}
    return false;
  }
  
  public boolean isServerTrusted(X509Certificate[] paramArrayOfX509Certificate)
  {
    try
    {
      theX509TrustManager.checkServerTrusted(paramArrayOfX509Certificate, "UNKNOWN");
      return true;
    }
    catch (CertificateException localCertificateException) {}
    return false;
  }
  
  public X509Certificate[] getAcceptedIssuers()
  {
    return theX509TrustManager.getAcceptedIssuers();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\X509TrustManagerComSunWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
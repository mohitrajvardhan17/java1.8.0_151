package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

final class X509TrustManagerJavaxWrapper
  implements javax.net.ssl.X509TrustManager
{
  private X509TrustManager theX509TrustManager;
  
  X509TrustManagerJavaxWrapper(X509TrustManager paramX509TrustManager)
  {
    theX509TrustManager = paramX509TrustManager;
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    if (!theX509TrustManager.isClientTrusted(paramArrayOfX509Certificate)) {
      throw new CertificateException("Untrusted Client Certificate Chain");
    }
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    if (!theX509TrustManager.isServerTrusted(paramArrayOfX509Certificate)) {
      throw new CertificateException("Untrusted Server Certificate Chain");
    }
  }
  
  public X509Certificate[] getAcceptedIssuers()
  {
    return theX509TrustManager.getAcceptedIssuers();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\X509TrustManagerJavaxWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract interface X509TrustManager
  extends TrustManager
{
  public abstract void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException;
  
  public abstract void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException;
  
  public abstract X509Certificate[] getAcceptedIssuers();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\X509TrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
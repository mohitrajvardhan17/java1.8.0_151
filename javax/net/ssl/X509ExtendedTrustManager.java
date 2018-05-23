package javax.net.ssl;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract class X509ExtendedTrustManager
  implements X509TrustManager
{
  public X509ExtendedTrustManager() {}
  
  public abstract void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
    throws CertificateException;
  
  public abstract void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket)
    throws CertificateException;
  
  public abstract void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
    throws CertificateException;
  
  public abstract void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine)
    throws CertificateException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\X509ExtendedTrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
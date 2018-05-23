package javax.security.auth.x500;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.security.auth.Destroyable;

public final class X500PrivateCredential
  implements Destroyable
{
  private X509Certificate cert;
  private PrivateKey key;
  private String alias;
  
  public X500PrivateCredential(X509Certificate paramX509Certificate, PrivateKey paramPrivateKey)
  {
    if ((paramX509Certificate == null) || (paramPrivateKey == null)) {
      throw new IllegalArgumentException();
    }
    cert = paramX509Certificate;
    key = paramPrivateKey;
    alias = null;
  }
  
  public X500PrivateCredential(X509Certificate paramX509Certificate, PrivateKey paramPrivateKey, String paramString)
  {
    if ((paramX509Certificate == null) || (paramPrivateKey == null) || (paramString == null)) {
      throw new IllegalArgumentException();
    }
    cert = paramX509Certificate;
    key = paramPrivateKey;
    alias = paramString;
  }
  
  public X509Certificate getCertificate()
  {
    return cert;
  }
  
  public PrivateKey getPrivateKey()
  {
    return key;
  }
  
  public String getAlias()
  {
    return alias;
  }
  
  public void destroy()
  {
    cert = null;
    key = null;
    alias = null;
  }
  
  public boolean isDestroyed()
  {
    return (cert == null) && (key == null) && (alias == null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\x500\X500PrivateCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
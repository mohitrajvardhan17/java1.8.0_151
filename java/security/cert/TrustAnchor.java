package java.security.cert;

import java.io.IOException;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.NameConstraintsExtension;

public class TrustAnchor
{
  private final PublicKey pubKey;
  private final String caName;
  private final X500Principal caPrincipal;
  private final X509Certificate trustedCert;
  private byte[] ncBytes;
  private NameConstraintsExtension nc;
  
  public TrustAnchor(X509Certificate paramX509Certificate, byte[] paramArrayOfByte)
  {
    if (paramX509Certificate == null) {
      throw new NullPointerException("the trustedCert parameter must be non-null");
    }
    trustedCert = paramX509Certificate;
    pubKey = null;
    caName = null;
    caPrincipal = null;
    setNameConstraints(paramArrayOfByte);
  }
  
  public TrustAnchor(X500Principal paramX500Principal, PublicKey paramPublicKey, byte[] paramArrayOfByte)
  {
    if ((paramX500Principal == null) || (paramPublicKey == null)) {
      throw new NullPointerException();
    }
    trustedCert = null;
    caPrincipal = paramX500Principal;
    caName = paramX500Principal.getName();
    pubKey = paramPublicKey;
    setNameConstraints(paramArrayOfByte);
  }
  
  public TrustAnchor(String paramString, PublicKey paramPublicKey, byte[] paramArrayOfByte)
  {
    if (paramPublicKey == null) {
      throw new NullPointerException("the pubKey parameter must be non-null");
    }
    if (paramString == null) {
      throw new NullPointerException("the caName parameter must be non-null");
    }
    if (paramString.length() == 0) {
      throw new IllegalArgumentException("the caName parameter must be a non-empty String");
    }
    caPrincipal = new X500Principal(paramString);
    pubKey = paramPublicKey;
    caName = paramString;
    trustedCert = null;
    setNameConstraints(paramArrayOfByte);
  }
  
  public final X509Certificate getTrustedCert()
  {
    return trustedCert;
  }
  
  public final X500Principal getCA()
  {
    return caPrincipal;
  }
  
  public final String getCAName()
  {
    return caName;
  }
  
  public final PublicKey getCAPublicKey()
  {
    return pubKey;
  }
  
  private void setNameConstraints(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
    {
      ncBytes = null;
      nc = null;
    }
    else
    {
      ncBytes = ((byte[])paramArrayOfByte.clone());
      try
      {
        nc = new NameConstraintsExtension(Boolean.FALSE, paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException(localIOException.getMessage());
        localIllegalArgumentException.initCause(localIOException);
        throw localIllegalArgumentException;
      }
    }
  }
  
  public final byte[] getNameConstraints()
  {
    return ncBytes == null ? null : (byte[])ncBytes.clone();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("[\n");
    if (pubKey != null)
    {
      localStringBuffer.append("  Trusted CA Public Key: " + pubKey.toString() + "\n");
      localStringBuffer.append("  Trusted CA Issuer Name: " + String.valueOf(caName) + "\n");
    }
    else
    {
      localStringBuffer.append("  Trusted CA cert: " + trustedCert.toString() + "\n");
    }
    if (nc != null) {
      localStringBuffer.append("  Name Constraints: " + nc.toString() + "\n");
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\TrustAnchor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
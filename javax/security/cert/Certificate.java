package javax.security.cert;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;

public abstract class Certificate
{
  public Certificate() {}
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Certificate)) {
      return false;
    }
    try
    {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = ((Certificate)paramObject).getEncoded();
      if (arrayOfByte1.length != arrayOfByte2.length) {
        return false;
      }
      for (int i = 0; i < arrayOfByte1.length; i++) {
        if (arrayOfByte1[i] != arrayOfByte2[i]) {
          return false;
        }
      }
      return true;
    }
    catch (CertificateException localCertificateException) {}
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    try
    {
      byte[] arrayOfByte = getEncoded();
      for (int j = 1; j < arrayOfByte.length; j++) {
        i += arrayOfByte[j] * j;
      }
      return i;
    }
    catch (CertificateException localCertificateException) {}
    return i;
  }
  
  public abstract byte[] getEncoded()
    throws CertificateEncodingException;
  
  public abstract void verify(PublicKey paramPublicKey)
    throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public abstract void verify(PublicKey paramPublicKey, String paramString)
    throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
  
  public abstract String toString();
  
  public abstract PublicKey getPublicKey();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\cert\Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
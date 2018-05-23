package sun.security.krb5.internal.crypto;

import java.security.MessageDigest;
import sun.security.krb5.KrbCryptoException;

public final class DesCbcMd5EType
  extends DesCbcEType
{
  public DesCbcMd5EType() {}
  
  public int eType()
  {
    return 3;
  }
  
  public int minimumPadSize()
  {
    return 0;
  }
  
  public int confounderSize()
  {
    return 8;
  }
  
  public int checksumType()
  {
    return 7;
  }
  
  public int checksumSize()
  {
    return 16;
  }
  
  protected byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt)
    throws KrbCryptoException
  {
    MessageDigest localMessageDigest = null;
    try
    {
      localMessageDigest = MessageDigest.getInstance("MD5");
    }
    catch (Exception localException1)
    {
      throw new KrbCryptoException("JCE provider may not be installed. " + localException1.getMessage());
    }
    try
    {
      localMessageDigest.update(paramArrayOfByte);
      return localMessageDigest.digest();
    }
    catch (Exception localException2)
    {
      throw new KrbCryptoException(localException2.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\crypto\DesCbcMd5EType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
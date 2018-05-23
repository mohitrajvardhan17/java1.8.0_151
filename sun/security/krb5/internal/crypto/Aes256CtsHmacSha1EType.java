package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KrbApErrException;

public final class Aes256CtsHmacSha1EType
  extends EType
{
  public Aes256CtsHmacSha1EType() {}
  
  public int eType()
  {
    return 18;
  }
  
  public int minimumPadSize()
  {
    return 0;
  }
  
  public int confounderSize()
  {
    return blockSize();
  }
  
  public int checksumType()
  {
    return 16;
  }
  
  public int checksumSize()
  {
    return Aes256.getChecksumLength();
  }
  
  public int blockSize()
  {
    return 16;
  }
  
  public int keyType()
  {
    return 3;
  }
  
  public int keySize()
  {
    return 32;
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws KrbCryptoException
  {
    byte[] arrayOfByte = new byte[blockSize()];
    return encrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
    throws KrbCryptoException
  {
    try
    {
      return Aes256.encrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
      localKrbCryptoException.initCause(localGeneralSecurityException);
      throw localKrbCryptoException;
    }
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws KrbApErrException, KrbCryptoException
  {
    byte[] arrayOfByte = new byte[blockSize()];
    return decrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
    throws KrbApErrException, KrbCryptoException
  {
    try
    {
      return Aes256.decrypt(paramArrayOfByte2, paramInt, paramArrayOfByte3, paramArrayOfByte1, 0, paramArrayOfByte1.length);
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
      localKrbCryptoException.initCause(localGeneralSecurityException);
      throw localKrbCryptoException;
    }
  }
  
  public byte[] decryptedData(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\crypto\Aes256CtsHmacSha1EType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.security.krb5.internal.crypto;

import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KrbApErrException;

abstract class DesCbcEType
  extends EType
{
  DesCbcEType() {}
  
  protected abstract byte[] calculateChecksum(byte[] paramArrayOfByte, int paramInt)
    throws KrbCryptoException;
  
  public int blockSize()
  {
    return 8;
  }
  
  public int keyType()
  {
    return 1;
  }
  
  public int keySize()
  {
    return 8;
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws KrbCryptoException
  {
    byte[] arrayOfByte = new byte[keySize()];
    return encrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
    throws KrbCryptoException
  {
    if (paramArrayOfByte2.length > 8) {
      throw new KrbCryptoException("Invalid DES Key!");
    }
    int i = paramArrayOfByte1.length + confounderSize() + checksumSize();
    byte[] arrayOfByte1;
    int j;
    if (i % blockSize() == 0)
    {
      arrayOfByte1 = new byte[i + blockSize()];
      j = 8;
    }
    else
    {
      arrayOfByte1 = new byte[i + blockSize() - i % blockSize()];
      j = (byte)(blockSize() - i % blockSize());
    }
    for (int k = i; k < arrayOfByte1.length; k++) {
      arrayOfByte1[k] = j;
    }
    byte[] arrayOfByte2 = Confounder.bytes(confounderSize());
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, confounderSize());
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte1, startOfData(), paramArrayOfByte1.length);
    byte[] arrayOfByte3 = calculateChecksum(arrayOfByte1, arrayOfByte1.length);
    System.arraycopy(arrayOfByte3, 0, arrayOfByte1, startOfChecksum(), checksumSize());
    byte[] arrayOfByte4 = new byte[arrayOfByte1.length];
    Des.cbc_encrypt(arrayOfByte1, arrayOfByte4, paramArrayOfByte2, paramArrayOfByte3, true);
    return arrayOfByte4;
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws KrbApErrException, KrbCryptoException
  {
    byte[] arrayOfByte = new byte[keySize()];
    return decrypt(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt);
  }
  
  public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt)
    throws KrbApErrException, KrbCryptoException
  {
    if (paramArrayOfByte2.length > 8) {
      throw new KrbCryptoException("Invalid DES Key!");
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length];
    Des.cbc_encrypt(paramArrayOfByte1, arrayOfByte, paramArrayOfByte2, paramArrayOfByte3, false);
    if (!isChecksumValid(arrayOfByte)) {
      throw new KrbApErrException(31);
    }
    return arrayOfByte;
  }
  
  private void copyChecksumField(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    for (int i = 0; i < checksumSize(); i++) {
      paramArrayOfByte1[(startOfChecksum() + i)] = paramArrayOfByte2[i];
    }
  }
  
  private byte[] checksumField(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[checksumSize()];
    for (int i = 0; i < checksumSize(); i++) {
      arrayOfByte[i] = paramArrayOfByte[(startOfChecksum() + i)];
    }
    return arrayOfByte;
  }
  
  private void resetChecksumField(byte[] paramArrayOfByte)
  {
    for (int i = startOfChecksum(); i < startOfChecksum() + checksumSize(); i++) {
      paramArrayOfByte[i] = 0;
    }
  }
  
  private byte[] generateChecksum(byte[] paramArrayOfByte)
    throws KrbCryptoException
  {
    byte[] arrayOfByte1 = checksumField(paramArrayOfByte);
    resetChecksumField(paramArrayOfByte);
    byte[] arrayOfByte2 = calculateChecksum(paramArrayOfByte, paramArrayOfByte.length);
    copyChecksumField(paramArrayOfByte, arrayOfByte1);
    return arrayOfByte2;
  }
  
  private boolean isChecksumEqual(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1 == paramArrayOfByte2) {
      return true;
    }
    if (((paramArrayOfByte1 == null) && (paramArrayOfByte2 != null)) || ((paramArrayOfByte1 != null) && (paramArrayOfByte2 == null))) {
      return false;
    }
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfByte1.length; i++) {
      if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean isChecksumValid(byte[] paramArrayOfByte)
    throws KrbCryptoException
  {
    byte[] arrayOfByte1 = checksumField(paramArrayOfByte);
    byte[] arrayOfByte2 = generateChecksum(paramArrayOfByte);
    return isChecksumEqual(arrayOfByte1, arrayOfByte2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\crypto\DesCbcEType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
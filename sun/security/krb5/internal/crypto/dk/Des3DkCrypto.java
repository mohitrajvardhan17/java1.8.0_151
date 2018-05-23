package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Des3DkCrypto
  extends DkCrypto
{
  private static final byte[] ZERO_IV = { 0, 0, 0, 0, 0, 0, 0, 0 };
  
  public Des3DkCrypto() {}
  
  protected int getKeySeedLength()
  {
    return 168;
  }
  
  public byte[] stringToKey(char[] paramArrayOfChar)
    throws GeneralSecurityException
  {
    byte[] arrayOfByte1 = null;
    try
    {
      arrayOfByte1 = charToUtf8(paramArrayOfChar);
      byte[] arrayOfByte2 = stringToKey(arrayOfByte1, null);
      return arrayOfByte2;
    }
    finally
    {
      if (arrayOfByte1 != null) {
        Arrays.fill(arrayOfByte1, (byte)0);
      }
    }
  }
  
  private byte[] stringToKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws GeneralSecurityException
  {
    if ((paramArrayOfByte2 != null) && (paramArrayOfByte2.length > 0)) {
      throw new RuntimeException("Invalid parameter to stringToKey");
    }
    byte[] arrayOfByte = randomToKey(nfold(paramArrayOfByte1, getKeySeedLength()));
    return dk(arrayOfByte, KERBEROS_CONSTANT);
  }
  
  public byte[] parityFix(byte[] paramArrayOfByte)
    throws GeneralSecurityException
  {
    setParityBit(paramArrayOfByte);
    return paramArrayOfByte;
  }
  
  protected byte[] randomToKey(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length != 21) {
      throw new IllegalArgumentException("input must be 168 bits");
    }
    byte[] arrayOfByte1 = keyCorrection(des3Expand(paramArrayOfByte, 0, 7));
    byte[] arrayOfByte2 = keyCorrection(des3Expand(paramArrayOfByte, 7, 14));
    byte[] arrayOfByte3 = keyCorrection(des3Expand(paramArrayOfByte, 14, 21));
    byte[] arrayOfByte4 = new byte[24];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte4, 0, 8);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 8, 8);
    System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 16, 8);
    return arrayOfByte4;
  }
  
  private static byte[] keyCorrection(byte[] paramArrayOfByte)
  {
    try
    {
      if (DESKeySpec.isWeak(paramArrayOfByte, 0)) {
        paramArrayOfByte[7] = ((byte)(paramArrayOfByte[7] ^ 0xF0));
      }
    }
    catch (InvalidKeyException localInvalidKeyException) {}
    return paramArrayOfByte;
  }
  
  private static byte[] des3Expand(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 - paramInt1 != 7) {
      throw new IllegalArgumentException("Invalid length of DES Key Value:" + paramInt1 + "," + paramInt2);
    }
    byte[] arrayOfByte = new byte[8];
    int i = 0;
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, 7);
    int j = 0;
    for (int k = paramInt1; k < paramInt2; k++)
    {
      int m = (byte)(paramArrayOfByte[k] & 0x1);
      j = (byte)(j + 1);
      if (m != 0) {
        i = (byte)(i | m << j);
      }
    }
    arrayOfByte[7] = i;
    setParityBit(arrayOfByte);
    return arrayOfByte;
  }
  
  private static void setParityBit(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i] & 0xFE;
      j |= Integer.bitCount(j) & 0x1 ^ 0x1;
      paramArrayOfByte[i] = ((byte)j);
    }
  }
  
  protected Cipher getCipher(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws GeneralSecurityException
  {
    SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("desede");
    DESedeKeySpec localDESedeKeySpec = new DESedeKeySpec(paramArrayOfByte1, 0);
    SecretKey localSecretKey = localSecretKeyFactory.generateSecret(localDESedeKeySpec);
    if (paramArrayOfByte2 == null) {
      paramArrayOfByte2 = ZERO_IV;
    }
    Cipher localCipher = Cipher.getInstance("DESede/CBC/NoPadding");
    IvParameterSpec localIvParameterSpec = new IvParameterSpec(paramArrayOfByte2, 0, paramArrayOfByte2.length);
    localCipher.init(paramInt, localSecretKey, localIvParameterSpec);
    return localCipher;
  }
  
  public int getChecksumLength()
  {
    return 20;
  }
  
  protected byte[] getHmac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws GeneralSecurityException
  {
    SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "HmacSHA1");
    Mac localMac = Mac.getInstance("HmacSHA1");
    localMac.init(localSecretKeySpec);
    return localMac.doFinal(paramArrayOfByte2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\crypto\dk\Des3DkCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
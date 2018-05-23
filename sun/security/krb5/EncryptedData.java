package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.crypto.EType;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncryptedData
  implements Cloneable
{
  int eType;
  Integer kvno;
  byte[] cipher;
  byte[] plain;
  public static final int ETYPE_NULL = 0;
  public static final int ETYPE_DES_CBC_CRC = 1;
  public static final int ETYPE_DES_CBC_MD4 = 2;
  public static final int ETYPE_DES_CBC_MD5 = 3;
  public static final int ETYPE_ARCFOUR_HMAC = 23;
  public static final int ETYPE_ARCFOUR_HMAC_EXP = 24;
  public static final int ETYPE_DES3_CBC_HMAC_SHA1_KD = 16;
  public static final int ETYPE_AES128_CTS_HMAC_SHA1_96 = 17;
  public static final int ETYPE_AES256_CTS_HMAC_SHA1_96 = 18;
  
  private EncryptedData() {}
  
  public Object clone()
  {
    EncryptedData localEncryptedData = new EncryptedData();
    eType = eType;
    if (kvno != null) {
      kvno = new Integer(kvno.intValue());
    }
    if (cipher != null)
    {
      cipher = new byte[cipher.length];
      System.arraycopy(cipher, 0, cipher, 0, cipher.length);
    }
    return localEncryptedData;
  }
  
  public EncryptedData(int paramInt, Integer paramInteger, byte[] paramArrayOfByte)
  {
    eType = paramInt;
    kvno = paramInteger;
    cipher = paramArrayOfByte;
  }
  
  public EncryptedData(EncryptionKey paramEncryptionKey, byte[] paramArrayOfByte, int paramInt)
    throws KdcErrException, KrbCryptoException
  {
    EType localEType = EType.getInstance(paramEncryptionKey.getEType());
    cipher = localEType.encrypt(paramArrayOfByte, paramEncryptionKey.getBytes(), paramInt);
    eType = paramEncryptionKey.getEType();
    kvno = paramEncryptionKey.getKeyVersionNumber();
  }
  
  public byte[] decrypt(EncryptionKey paramEncryptionKey, int paramInt)
    throws KdcErrException, KrbApErrException, KrbCryptoException
  {
    if (eType != paramEncryptionKey.getEType()) {
      throw new KrbCryptoException("EncryptedData is encrypted using keytype " + EType.toString(eType) + " but decryption key is of type " + EType.toString(paramEncryptionKey.getEType()));
    }
    EType localEType = EType.getInstance(eType);
    plain = localEType.decrypt(cipher, paramEncryptionKey.getBytes(), paramInt);
    return localEType.decryptedData(plain);
  }
  
  private byte[] decryptedData()
    throws KdcErrException
  {
    if (plain != null)
    {
      EType localEType = EType.getInstance(eType);
      return localEType.decryptedData(plain);
    }
    return null;
  }
  
  private EncryptedData(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    DerValue localDerValue = null;
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      eType = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    if ((paramDerValue.getData().peekByte() & 0x1F) == 1)
    {
      localDerValue = paramDerValue.getData().getDerValue();
      int i = localDerValue.getData().getBigInteger().intValue();
      kvno = new Integer(i);
    }
    else
    {
      kvno = null;
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 2) {
      cipher = localDerValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(BigInteger.valueOf(eType));
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    if (kvno != null)
    {
      localDerOutputStream2.putInteger(BigInteger.valueOf(kvno.longValue()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
      localDerOutputStream2 = new DerOutputStream();
    }
    localDerOutputStream2.putOctetString(cipher);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public static EncryptedData parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new EncryptedData(localDerValue2);
  }
  
  public byte[] reset(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = null;
    if ((paramArrayOfByte[1] & 0xFF) < 128)
    {
      arrayOfByte = new byte[paramArrayOfByte[1] + 2];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte[1] + 2);
    }
    else if ((paramArrayOfByte[1] & 0xFF) > 128)
    {
      int i = paramArrayOfByte[1] & 0x7F;
      int j = 0;
      for (int k = 0; k < i; k++) {
        j |= (paramArrayOfByte[(k + 2)] & 0xFF) << 8 * (i - k - 1);
      }
      arrayOfByte = new byte[j + i + 2];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, j + i + 2);
    }
    return arrayOfByte;
  }
  
  public int getEType()
  {
    return eType;
  }
  
  public Integer getKeyVersionNumber()
  {
    return kvno;
  }
  
  public byte[] getBytes()
  {
    return cipher;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\EncryptedData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
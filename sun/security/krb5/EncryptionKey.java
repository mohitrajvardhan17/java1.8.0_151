package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData.SaltAndParams;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import sun.security.krb5.internal.crypto.Des;
import sun.security.krb5.internal.crypto.Des3;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncryptionKey
  implements Cloneable
{
  public static final EncryptionKey NULL_KEY = new EncryptionKey(new byte[0], 0, null);
  private int keyType;
  private byte[] keyValue;
  private Integer kvno;
  private static final boolean DEBUG = Krb5.DEBUG;
  
  public synchronized int getEType()
  {
    return keyType;
  }
  
  public final Integer getKeyVersionNumber()
  {
    return kvno;
  }
  
  public final byte[] getBytes()
  {
    return keyValue;
  }
  
  public synchronized Object clone()
  {
    return new EncryptionKey(keyValue, keyType, kvno);
  }
  
  public static EncryptionKey[] acquireSecretKeys(PrincipalName paramPrincipalName, String paramString)
  {
    if (paramPrincipalName == null) {
      throw new IllegalArgumentException("Cannot have null pricipal name to look in keytab.");
    }
    KeyTab localKeyTab = KeyTab.getInstance(paramString);
    return localKeyTab.readServiceKeys(paramPrincipalName);
  }
  
  public static EncryptionKey acquireSecretKey(PrincipalName paramPrincipalName, char[] paramArrayOfChar, int paramInt, PAData.SaltAndParams paramSaltAndParams)
    throws KrbException
  {
    String str;
    byte[] arrayOfByte;
    if (paramSaltAndParams != null)
    {
      str = salt != null ? salt : paramPrincipalName.getSalt();
      arrayOfByte = params;
    }
    else
    {
      str = paramPrincipalName.getSalt();
      arrayOfByte = null;
    }
    return acquireSecretKey(paramArrayOfChar, str, paramInt, arrayOfByte);
  }
  
  public static EncryptionKey acquireSecretKey(char[] paramArrayOfChar, String paramString, int paramInt, byte[] paramArrayOfByte)
    throws KrbException
  {
    return new EncryptionKey(stringToKey(paramArrayOfChar, paramString, paramArrayOfByte, paramInt), paramInt, null);
  }
  
  public static EncryptionKey[] acquireSecretKeys(char[] paramArrayOfChar, String paramString)
    throws KrbException
  {
    int[] arrayOfInt = EType.getDefaults("default_tkt_enctypes");
    EncryptionKey[] arrayOfEncryptionKey = new EncryptionKey[arrayOfInt.length];
    for (int i = 0; i < arrayOfInt.length; i++) {
      if (EType.isSupported(arrayOfInt[i])) {
        arrayOfEncryptionKey[i] = new EncryptionKey(stringToKey(paramArrayOfChar, paramString, null, arrayOfInt[i]), arrayOfInt[i], null);
      } else if (DEBUG) {
        System.out.println("Encryption Type " + EType.toString(arrayOfInt[i]) + " is not supported/enabled");
      }
    }
    return arrayOfEncryptionKey;
  }
  
  public EncryptionKey(byte[] paramArrayOfByte, int paramInt, Integer paramInteger)
  {
    if (paramArrayOfByte != null)
    {
      keyValue = new byte[paramArrayOfByte.length];
      System.arraycopy(paramArrayOfByte, 0, keyValue, 0, paramArrayOfByte.length);
    }
    else
    {
      throw new IllegalArgumentException("EncryptionKey: Key bytes cannot be null!");
    }
    keyType = paramInt;
    kvno = paramInteger;
  }
  
  public EncryptionKey(int paramInt, byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramInt, null);
  }
  
  private static byte[] stringToKey(char[] paramArrayOfChar, String paramString, byte[] paramArrayOfByte, int paramInt)
    throws KrbCryptoException
  {
    char[] arrayOfChar1 = paramString.toCharArray();
    char[] arrayOfChar2 = new char[paramArrayOfChar.length + arrayOfChar1.length];
    System.arraycopy(paramArrayOfChar, 0, arrayOfChar2, 0, paramArrayOfChar.length);
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, paramArrayOfChar.length, arrayOfChar1.length);
    Arrays.fill(arrayOfChar1, '0');
    try
    {
      byte[] arrayOfByte;
      switch (paramInt)
      {
      case 1: 
      case 3: 
        arrayOfByte = Des.string_to_key_bytes(arrayOfChar2);
        return arrayOfByte;
      case 16: 
        arrayOfByte = Des3.stringToKey(arrayOfChar2);
        return arrayOfByte;
      case 23: 
        arrayOfByte = ArcFourHmac.stringToKey(paramArrayOfChar);
        return arrayOfByte;
      case 17: 
        arrayOfByte = Aes128.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
        return arrayOfByte;
      case 18: 
        arrayOfByte = Aes256.stringToKey(paramArrayOfChar, paramString, paramArrayOfByte);
        return arrayOfByte;
      }
      throw new IllegalArgumentException("encryption type " + EType.toString(paramInt) + " not supported");
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
      localKrbCryptoException.initCause(localGeneralSecurityException);
      throw localKrbCryptoException;
    }
    finally
    {
      Arrays.fill(arrayOfChar2, '0');
    }
  }
  
  public EncryptionKey(char[] paramArrayOfChar, String paramString1, String paramString2)
    throws KrbCryptoException
  {
    if ((paramString2 == null) || (paramString2.equalsIgnoreCase("DES")))
    {
      keyType = 3;
    }
    else if (paramString2.equalsIgnoreCase("DESede"))
    {
      keyType = 16;
    }
    else if (paramString2.equalsIgnoreCase("AES128"))
    {
      keyType = 17;
    }
    else if (paramString2.equalsIgnoreCase("ArcFourHmac"))
    {
      keyType = 23;
    }
    else if (paramString2.equalsIgnoreCase("AES256"))
    {
      keyType = 18;
      if (!EType.isSupported(keyType)) {
        throw new IllegalArgumentException("Algorithm " + paramString2 + " not enabled");
      }
    }
    else
    {
      throw new IllegalArgumentException("Algorithm " + paramString2 + " not supported");
    }
    keyValue = stringToKey(paramArrayOfChar, paramString1, null, keyType);
    kvno = null;
  }
  
  public EncryptionKey(EncryptionKey paramEncryptionKey)
    throws KrbCryptoException
  {
    keyValue = Confounder.bytes(keyValue.length);
    for (int i = 0; i < keyValue.length; i++)
    {
      int tmp32_31 = i;
      byte[] tmp32_28 = keyValue;
      tmp32_28[tmp32_31] = ((byte)(tmp32_28[tmp32_31] ^ keyValue[i]));
    }
    keyType = keyType;
    try
    {
      if ((keyType == 3) || (keyType == 1))
      {
        if (!DESKeySpec.isParityAdjusted(keyValue, 0)) {
          keyValue = Des.set_parity(keyValue);
        }
        if (DESKeySpec.isWeak(keyValue, 0)) {
          keyValue[7] = ((byte)(keyValue[7] ^ 0xF0));
        }
      }
      if (keyType == 16)
      {
        if (!DESedeKeySpec.isParityAdjusted(keyValue, 0)) {
          keyValue = Des3.parityFix(keyValue);
        }
        byte[] arrayOfByte = new byte[8];
        for (int j = 0; j < keyValue.length; j += 8)
        {
          System.arraycopy(keyValue, j, arrayOfByte, 0, 8);
          if (DESKeySpec.isWeak(arrayOfByte, 0)) {
            keyValue[(j + 7)] = ((byte)(keyValue[(j + 7)] ^ 0xF0));
          }
        }
      }
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      KrbCryptoException localKrbCryptoException = new KrbCryptoException(localGeneralSecurityException.getMessage());
      localKrbCryptoException.initCause(localGeneralSecurityException);
      throw localKrbCryptoException;
    }
  }
  
  public EncryptionKey(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 0) {
      keyType = localDerValue.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue = paramDerValue.getData().getDerValue();
    if ((localDerValue.getTag() & 0x1F) == 1) {
      keyValue = localDerValue.getData().getOctetString();
    } else {
      throw new Asn1Exception(906);
    }
    if (localDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public synchronized byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(keyType);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putOctetString(keyValue);
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public synchronized void destroy()
  {
    if (keyValue != null) {
      for (int i = 0; i < keyValue.length; i++) {
        keyValue[i] = 0;
      }
    }
  }
  
  public static EncryptionKey parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
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
    return new EncryptionKey(localDerValue2);
  }
  
  public synchronized void writeKey(CCacheOutputStream paramCCacheOutputStream)
    throws IOException
  {
    paramCCacheOutputStream.write16(keyType);
    paramCCacheOutputStream.write16(keyType);
    paramCCacheOutputStream.write32(keyValue.length);
    for (int i = 0; i < keyValue.length; i++) {
      paramCCacheOutputStream.write8(keyValue[i]);
    }
  }
  
  public String toString()
  {
    return new String("EncryptionKey: keyType=" + keyType + " kvno=" + kvno + " keyValue (hex dump)=" + ((keyValue == null) || (keyValue.length == 0) ? " Empty Key" : new StringBuilder().append('\n').append(Krb5.hexDumper.encodeBuffer(keyValue)).append('\n').toString()));
  }
  
  public static EncryptionKey findKey(int paramInt, EncryptionKey[] paramArrayOfEncryptionKey)
    throws KrbException
  {
    return findKey(paramInt, null, paramArrayOfEncryptionKey);
  }
  
  private static boolean versionMatches(Integer paramInteger1, Integer paramInteger2)
  {
    if ((paramInteger1 == null) || (paramInteger1.intValue() == 0) || (paramInteger2 == null) || (paramInteger2.intValue() == 0)) {
      return true;
    }
    return paramInteger1.equals(paramInteger2);
  }
  
  public static EncryptionKey findKey(int paramInt, Integer paramInteger, EncryptionKey[] paramArrayOfEncryptionKey)
    throws KrbException
  {
    if (!EType.isSupported(paramInt)) {
      throw new KrbException("Encryption type " + EType.toString(paramInt) + " is not supported/enabled");
    }
    int j = 0;
    int k = 0;
    EncryptionKey localEncryptionKey = null;
    int i;
    Integer localInteger;
    for (int m = 0; m < paramArrayOfEncryptionKey.length; m++)
    {
      i = paramArrayOfEncryptionKey[m].getEType();
      if (EType.isSupported(i))
      {
        localInteger = paramArrayOfEncryptionKey[m].getKeyVersionNumber();
        if (paramInt == i)
        {
          j = 1;
          if (versionMatches(paramInteger, localInteger)) {
            return paramArrayOfEncryptionKey[m];
          }
          if (localInteger.intValue() > k)
          {
            localEncryptionKey = paramArrayOfEncryptionKey[m];
            k = localInteger.intValue();
          }
        }
      }
    }
    if ((paramInt == 1) || (paramInt == 3)) {
      for (m = 0; m < paramArrayOfEncryptionKey.length; m++)
      {
        i = paramArrayOfEncryptionKey[m].getEType();
        if ((i == 1) || (i == 3))
        {
          localInteger = paramArrayOfEncryptionKey[m].getKeyVersionNumber();
          j = 1;
          if (versionMatches(paramInteger, localInteger)) {
            return new EncryptionKey(paramInt, paramArrayOfEncryptionKey[m].getBytes());
          }
          if (localInteger.intValue() > k)
          {
            localEncryptionKey = new EncryptionKey(paramInt, paramArrayOfEncryptionKey[m].getBytes());
            k = localInteger.intValue();
          }
        }
      }
    }
    if (j != 0) {
      return localEncryptionKey;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\EncryptionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
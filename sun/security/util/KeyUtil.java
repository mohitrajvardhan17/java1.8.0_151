package sun.security.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import sun.security.jca.JCAUtil;

public final class KeyUtil
{
  public KeyUtil() {}
  
  public static final int getKeySize(Key paramKey)
  {
    int i = -1;
    if ((paramKey instanceof Length))
    {
      try
      {
        Length localLength = (Length)paramKey;
        i = localLength.length();
      }
      catch (UnsupportedOperationException localUnsupportedOperationException) {}
      if (i >= 0) {
        return i;
      }
    }
    Object localObject1;
    Object localObject2;
    if ((paramKey instanceof SecretKey))
    {
      localObject1 = (SecretKey)paramKey;
      localObject2 = ((SecretKey)localObject1).getFormat();
      if (("RAW".equals(localObject2)) && (((SecretKey)localObject1).getEncoded() != null)) {
        i = ((SecretKey)localObject1).getEncoded().length * 8;
      }
    }
    else if ((paramKey instanceof RSAKey))
    {
      localObject1 = (RSAKey)paramKey;
      i = ((RSAKey)localObject1).getModulus().bitLength();
    }
    else if ((paramKey instanceof ECKey))
    {
      localObject1 = (ECKey)paramKey;
      i = ((ECKey)localObject1).getParams().getOrder().bitLength();
    }
    else if ((paramKey instanceof DSAKey))
    {
      localObject1 = (DSAKey)paramKey;
      localObject2 = ((DSAKey)localObject1).getParams();
      i = localObject2 != null ? ((DSAParams)localObject2).getP().bitLength() : -1;
    }
    else if ((paramKey instanceof DHKey))
    {
      localObject1 = (DHKey)paramKey;
      i = ((DHKey)localObject1).getParams().getP().bitLength();
    }
    return i;
  }
  
  public static final void validate(Key paramKey)
    throws InvalidKeyException
  {
    if (paramKey == null) {
      throw new NullPointerException("The key to be validated cannot be null");
    }
    if ((paramKey instanceof DHPublicKey)) {
      validateDHPublicKey((DHPublicKey)paramKey);
    }
  }
  
  public static final void validate(KeySpec paramKeySpec)
    throws InvalidKeyException
  {
    if (paramKeySpec == null) {
      throw new NullPointerException("The key spec to be validated cannot be null");
    }
    if ((paramKeySpec instanceof DHPublicKeySpec)) {
      validateDHPublicKey((DHPublicKeySpec)paramKeySpec);
    }
  }
  
  public static final boolean isOracleJCEProvider(String paramString)
  {
    return (paramString != null) && ((paramString.equals("SunJCE")) || (paramString.equals("SunMSCAPI")) || (paramString.equals("OracleUcrypto")) || (paramString.startsWith("SunPKCS11")));
  }
  
  public static byte[] checkTlsPreMasterSecretKey(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramSecureRandom == null) {
      paramSecureRandom = JCAUtil.getSecureRandom();
    }
    byte[] arrayOfByte = new byte[48];
    paramSecureRandom.nextBytes(arrayOfByte);
    if ((!paramBoolean) && (paramArrayOfByte != null))
    {
      if (paramArrayOfByte.length != 48) {
        return arrayOfByte;
      }
      int i = (paramArrayOfByte[0] & 0xFF) << 8 | paramArrayOfByte[1] & 0xFF;
      if ((paramInt1 != i) && ((paramInt1 > 769) || (paramInt2 != i))) {
        paramArrayOfByte = arrayOfByte;
      }
      return paramArrayOfByte;
    }
    return arrayOfByte;
  }
  
  private static void validateDHPublicKey(DHPublicKey paramDHPublicKey)
    throws InvalidKeyException
  {
    DHParameterSpec localDHParameterSpec = paramDHPublicKey.getParams();
    BigInteger localBigInteger1 = localDHParameterSpec.getP();
    BigInteger localBigInteger2 = localDHParameterSpec.getG();
    BigInteger localBigInteger3 = paramDHPublicKey.getY();
    validateDHPublicKey(localBigInteger1, localBigInteger2, localBigInteger3);
  }
  
  private static void validateDHPublicKey(DHPublicKeySpec paramDHPublicKeySpec)
    throws InvalidKeyException
  {
    validateDHPublicKey(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec.getG(), paramDHPublicKeySpec.getY());
  }
  
  private static void validateDHPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
    throws InvalidKeyException
  {
    BigInteger localBigInteger1 = BigInteger.ONE;
    BigInteger localBigInteger2 = paramBigInteger1.subtract(BigInteger.ONE);
    if (paramBigInteger3.compareTo(localBigInteger1) <= 0) {
      throw new InvalidKeyException("Diffie-Hellman public key is too small");
    }
    if (paramBigInteger3.compareTo(localBigInteger2) >= 0) {
      throw new InvalidKeyException("Diffie-Hellman public key is too large");
    }
    BigInteger localBigInteger3 = paramBigInteger1.remainder(paramBigInteger3);
    if (localBigInteger3.equals(BigInteger.ZERO)) {
      throw new InvalidKeyException("Invalid Diffie-Hellman parameters");
    }
  }
  
  public static byte[] trimZeroes(byte[] paramArrayOfByte)
  {
    for (int i = 0; (i < paramArrayOfByte.length - 1) && (paramArrayOfByte[i] == 0); i++) {}
    if (i == 0) {
      return paramArrayOfByte;
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length - i];
    System.arraycopy(paramArrayOfByte, i, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\KeyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
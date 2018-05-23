package sun.security.rsa;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.SecurityProviderConstants;

public final class RSAKeyPairGenerator
  extends KeyPairGeneratorSpi
{
  private BigInteger publicExponent;
  private int keySize;
  private SecureRandom random;
  
  public RSAKeyPairGenerator()
  {
    initialize(SecurityProviderConstants.DEF_RSA_KEY_SIZE, null);
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom)
  {
    try
    {
      RSAKeyFactory.checkKeyLengths(paramInt, RSAKeyGenParameterSpec.F4, 512, 65536);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new InvalidParameterException(localInvalidKeyException.getMessage());
    }
    keySize = paramInt;
    random = paramSecureRandom;
    publicExponent = RSAKeyGenParameterSpec.F4;
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramAlgorithmParameterSpec instanceof RSAKeyGenParameterSpec)) {
      throw new InvalidAlgorithmParameterException("Params must be instance of RSAKeyGenParameterSpec");
    }
    RSAKeyGenParameterSpec localRSAKeyGenParameterSpec = (RSAKeyGenParameterSpec)paramAlgorithmParameterSpec;
    int i = localRSAKeyGenParameterSpec.getKeysize();
    BigInteger localBigInteger = localRSAKeyGenParameterSpec.getPublicExponent();
    if (localBigInteger == null)
    {
      localBigInteger = RSAKeyGenParameterSpec.F4;
    }
    else
    {
      if (localBigInteger.compareTo(RSAKeyGenParameterSpec.F0) < 0) {
        throw new InvalidAlgorithmParameterException("Public exponent must be 3 or larger");
      }
      if (localBigInteger.bitLength() > i) {
        throw new InvalidAlgorithmParameterException("Public exponent must be smaller than key size");
      }
    }
    try
    {
      RSAKeyFactory.checkKeyLengths(i, localBigInteger, 512, 65536);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new InvalidAlgorithmParameterException("Invalid key sizes", localInvalidKeyException);
    }
    keySize = i;
    publicExponent = localBigInteger;
    random = paramSecureRandom;
  }
  
  public KeyPair generateKeyPair()
  {
    int i = keySize + 1 >> 1;
    int j = keySize - i;
    if (random == null) {
      random = JCAUtil.getSecureRandom();
    }
    BigInteger localBigInteger1 = publicExponent;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    BigInteger localBigInteger2;
    BigInteger localBigInteger3;
    BigInteger localBigInteger4;
    do
    {
      localObject1 = BigInteger.probablePrime(i, random);
      do
      {
        localObject2 = BigInteger.probablePrime(j, random);
        if (((BigInteger)localObject1).compareTo((BigInteger)localObject2) < 0)
        {
          localObject3 = localObject1;
          localObject1 = localObject2;
          localObject2 = localObject3;
        }
        localBigInteger2 = ((BigInteger)localObject1).multiply((BigInteger)localObject2);
      } while (localBigInteger2.bitLength() < keySize);
      localObject3 = ((BigInteger)localObject1).subtract(BigInteger.ONE);
      localBigInteger3 = ((BigInteger)localObject2).subtract(BigInteger.ONE);
      localBigInteger4 = ((BigInteger)localObject3).multiply(localBigInteger3);
    } while (!localBigInteger1.gcd(localBigInteger4).equals(BigInteger.ONE));
    BigInteger localBigInteger5 = localBigInteger1.modInverse(localBigInteger4);
    BigInteger localBigInteger6 = localBigInteger5.mod((BigInteger)localObject3);
    BigInteger localBigInteger7 = localBigInteger5.mod(localBigInteger3);
    BigInteger localBigInteger8 = ((BigInteger)localObject2).modInverse((BigInteger)localObject1);
    try
    {
      RSAPublicKeyImpl localRSAPublicKeyImpl = new RSAPublicKeyImpl(localBigInteger2, localBigInteger1);
      RSAPrivateCrtKeyImpl localRSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(localBigInteger2, localBigInteger1, localBigInteger5, (BigInteger)localObject1, (BigInteger)localObject2, localBigInteger6, localBigInteger7, localBigInteger8);
      return new KeyPair(localRSAPublicKeyImpl, localRSAPrivateCrtKeyImpl);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new RuntimeException(localInvalidKeyException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\rsa\RSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
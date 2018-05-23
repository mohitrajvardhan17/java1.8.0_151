package sun.security.provider;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.SecurityProviderConstants;

class DSAKeyPairGenerator
  extends KeyPairGenerator
{
  private int plen;
  private int qlen;
  boolean forceNewParameters;
  private DSAParameterSpec params;
  private SecureRandom random;
  
  DSAKeyPairGenerator(int paramInt)
  {
    super("DSA");
    initialize(paramInt, null);
  }
  
  private static void checkStrength(int paramInt1, int paramInt2)
  {
    if (((paramInt1 < 512) || (paramInt1 > 1024) || (paramInt1 % 64 != 0) || (paramInt2 != 160)) && ((paramInt1 != 2048) || ((paramInt2 != 224) && (paramInt2 != 256)))) {
      throw new InvalidParameterException("Unsupported prime and subprime size combination: " + paramInt1 + ", " + paramInt2);
    }
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom)
  {
    init(paramInt, paramSecureRandom, false);
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec)) {
      throw new InvalidAlgorithmParameterException("Inappropriate parameter");
    }
    init((DSAParameterSpec)paramAlgorithmParameterSpec, paramSecureRandom, false);
  }
  
  void init(int paramInt, SecureRandom paramSecureRandom, boolean paramBoolean)
  {
    int i = SecurityProviderConstants.getDefDSASubprimeSize(paramInt);
    checkStrength(paramInt, i);
    plen = paramInt;
    qlen = i;
    params = null;
    random = paramSecureRandom;
    forceNewParameters = paramBoolean;
  }
  
  void init(DSAParameterSpec paramDSAParameterSpec, SecureRandom paramSecureRandom, boolean paramBoolean)
  {
    int i = paramDSAParameterSpec.getP().bitLength();
    int j = paramDSAParameterSpec.getQ().bitLength();
    checkStrength(i, j);
    plen = i;
    qlen = j;
    params = paramDSAParameterSpec;
    random = paramSecureRandom;
    forceNewParameters = paramBoolean;
  }
  
  public KeyPair generateKeyPair()
  {
    if (random == null) {
      random = JCAUtil.getSecureRandom();
    }
    DSAParameterSpec localDSAParameterSpec;
    try
    {
      if (forceNewParameters)
      {
        localDSAParameterSpec = ParameterCache.getNewDSAParameterSpec(plen, qlen, random);
      }
      else
      {
        if (params == null) {
          params = ParameterCache.getDSAParameterSpec(plen, qlen, random);
        }
        localDSAParameterSpec = params;
      }
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new ProviderException(localGeneralSecurityException);
    }
    return generateKeyPair(localDSAParameterSpec.getP(), localDSAParameterSpec.getQ(), localDSAParameterSpec.getG(), random);
  }
  
  private KeyPair generateKeyPair(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, SecureRandom paramSecureRandom)
  {
    BigInteger localBigInteger1 = generateX(paramSecureRandom, paramBigInteger2);
    BigInteger localBigInteger2 = generateY(localBigInteger1, paramBigInteger1, paramBigInteger3);
    try
    {
      Object localObject;
      if (DSAKeyFactory.SERIAL_INTEROP) {
        localObject = new DSAPublicKey(localBigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      } else {
        localObject = new DSAPublicKeyImpl(localBigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      }
      DSAPrivateKey localDSAPrivateKey = new DSAPrivateKey(localBigInteger1, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      KeyPair localKeyPair = new KeyPair((PublicKey)localObject, localDSAPrivateKey);
      return localKeyPair;
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new ProviderException(localInvalidKeyException);
    }
  }
  
  private BigInteger generateX(SecureRandom paramSecureRandom, BigInteger paramBigInteger)
  {
    BigInteger localBigInteger = null;
    byte[] arrayOfByte = new byte[qlen];
    do
    {
      paramSecureRandom.nextBytes(arrayOfByte);
      localBigInteger = new BigInteger(1, arrayOfByte).mod(paramBigInteger);
    } while ((localBigInteger.signum() <= 0) || (localBigInteger.compareTo(paramBigInteger) >= 0));
    return localBigInteger;
  }
  
  BigInteger generateY(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
  {
    BigInteger localBigInteger = paramBigInteger3.modPow(paramBigInteger1, paramBigInteger2);
    return localBigInteger;
  }
  
  public static final class Current
    extends DSAKeyPairGenerator
  {
    public Current()
    {
      super();
    }
  }
  
  public static final class Legacy
    extends DSAKeyPairGenerator
    implements java.security.interfaces.DSAKeyPairGenerator
  {
    public Legacy()
    {
      super();
    }
    
    public void initialize(int paramInt, boolean paramBoolean, SecureRandom paramSecureRandom)
      throws InvalidParameterException
    {
      if (paramBoolean)
      {
        super.init(paramInt, paramSecureRandom, true);
      }
      else
      {
        DSAParameterSpec localDSAParameterSpec = ParameterCache.getCachedDSAParameterSpec(paramInt, SecurityProviderConstants.getDefDSASubprimeSize(paramInt));
        if (localDSAParameterSpec == null) {
          throw new InvalidParameterException("No precomputed parameters for requested modulus size available");
        }
        super.init(localDSAParameterSpec, paramSecureRandom, false);
      }
    }
    
    public void initialize(DSAParams paramDSAParams, SecureRandom paramSecureRandom)
      throws InvalidParameterException
    {
      if (paramDSAParams == null) {
        throw new InvalidParameterException("Params must not be null");
      }
      DSAParameterSpec localDSAParameterSpec = new DSAParameterSpec(paramDSAParams.getP(), paramDSAParams.getQ(), paramDSAParams.getG());
      super.init(localDSAParameterSpec, paramSecureRandom, false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
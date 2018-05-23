package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class KeyPairGeneratorSpi
{
  public KeyPairGeneratorSpi() {}
  
  public abstract void initialize(int paramInt, SecureRandom paramSecureRandom);
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract KeyPair generateKeyPair();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyPairGeneratorSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
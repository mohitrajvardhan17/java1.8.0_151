package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class AlgorithmParameterGeneratorSpi
{
  public AlgorithmParameterGeneratorSpi() {}
  
  protected abstract void engineInit(int paramInt, SecureRandom paramSecureRandom);
  
  protected abstract void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException;
  
  protected abstract AlgorithmParameters engineGenerateParameters();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AlgorithmParameterGeneratorSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
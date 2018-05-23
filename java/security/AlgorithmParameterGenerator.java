package java.security;

import java.security.spec.AlgorithmParameterSpec;

public class AlgorithmParameterGenerator
{
  private Provider provider;
  private AlgorithmParameterGeneratorSpi paramGenSpi;
  private String algorithm;
  
  protected AlgorithmParameterGenerator(AlgorithmParameterGeneratorSpi paramAlgorithmParameterGeneratorSpi, Provider paramProvider, String paramString)
  {
    paramGenSpi = paramAlgorithmParameterGeneratorSpi;
    provider = paramProvider;
    algorithm = paramString;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public static AlgorithmParameterGenerator getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    try
    {
      Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameterGenerator", (String)null);
      return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new NoSuchAlgorithmException(paramString + " not found");
    }
  }
  
  public static AlgorithmParameterGenerator getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = Security.getImpl(paramString1, "AlgorithmParameterGenerator", paramString2);
    return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static AlgorithmParameterGenerator getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameterGenerator", paramProvider);
    return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final void init(int paramInt)
  {
    paramGenSpi.engineInit(paramInt, new SecureRandom());
  }
  
  public final void init(int paramInt, SecureRandom paramSecureRandom)
  {
    paramGenSpi.engineInit(paramInt, paramSecureRandom);
  }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    paramGenSpi.engineInit(paramAlgorithmParameterSpec, new SecureRandom());
  }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom)
    throws InvalidAlgorithmParameterException
  {
    paramGenSpi.engineInit(paramAlgorithmParameterSpec, paramSecureRandom);
  }
  
  public final AlgorithmParameters generateParameters()
  {
    return paramGenSpi.engineGenerateParameters();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AlgorithmParameterGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
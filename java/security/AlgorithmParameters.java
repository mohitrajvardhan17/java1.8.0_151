package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class AlgorithmParameters
{
  private Provider provider;
  private AlgorithmParametersSpi paramSpi;
  private String algorithm;
  private boolean initialized = false;
  
  protected AlgorithmParameters(AlgorithmParametersSpi paramAlgorithmParametersSpi, Provider paramProvider, String paramString)
  {
    paramSpi = paramAlgorithmParametersSpi;
    provider = paramProvider;
    algorithm = paramString;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public static AlgorithmParameters getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    try
    {
      Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameters", (String)null);
      return new AlgorithmParameters((AlgorithmParametersSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new NoSuchAlgorithmException(paramString + " not found");
    }
  }
  
  public static AlgorithmParameters getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = Security.getImpl(paramString1, "AlgorithmParameters", paramString2);
    return new AlgorithmParameters((AlgorithmParametersSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static AlgorithmParameters getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameters", paramProvider);
    return new AlgorithmParameters((AlgorithmParametersSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidParameterSpecException
  {
    if (initialized) {
      throw new InvalidParameterSpecException("already initialized");
    }
    paramSpi.engineInit(paramAlgorithmParameterSpec);
    initialized = true;
  }
  
  public final void init(byte[] paramArrayOfByte)
    throws IOException
  {
    if (initialized) {
      throw new IOException("already initialized");
    }
    paramSpi.engineInit(paramArrayOfByte);
    initialized = true;
  }
  
  public final void init(byte[] paramArrayOfByte, String paramString)
    throws IOException
  {
    if (initialized) {
      throw new IOException("already initialized");
    }
    paramSpi.engineInit(paramArrayOfByte, paramString);
    initialized = true;
  }
  
  public final <T extends AlgorithmParameterSpec> T getParameterSpec(Class<T> paramClass)
    throws InvalidParameterSpecException
  {
    if (!initialized) {
      throw new InvalidParameterSpecException("not initialized");
    }
    return paramSpi.engineGetParameterSpec(paramClass);
  }
  
  public final byte[] getEncoded()
    throws IOException
  {
    if (!initialized) {
      throw new IOException("not initialized");
    }
    return paramSpi.engineGetEncoded();
  }
  
  public final byte[] getEncoded(String paramString)
    throws IOException
  {
    if (!initialized) {
      throw new IOException("not initialized");
    }
    return paramSpi.engineGetEncoded(paramString);
  }
  
  public final String toString()
  {
    if (!initialized) {
      return null;
    }
    return paramSpi.engineToString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AlgorithmParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
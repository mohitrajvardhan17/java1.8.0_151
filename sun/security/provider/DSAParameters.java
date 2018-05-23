package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DSAParameters
  extends AlgorithmParametersSpi
{
  protected BigInteger p;
  protected BigInteger q;
  protected BigInteger g;
  
  public DSAParameters() {}
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws InvalidParameterSpecException
  {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec)) {
      throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    p = ((DSAParameterSpec)paramAlgorithmParameterSpec).getP();
    q = ((DSAParameterSpec)paramAlgorithmParameterSpec).getQ();
    g = ((DSAParameterSpec)paramAlgorithmParameterSpec).getG();
  }
  
  protected void engineInit(byte[] paramArrayOfByte)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramArrayOfByte);
    if (tag != 48) {
      throw new IOException("DSA params parsing error");
    }
    data.reset();
    p = data.getBigInteger();
    q = data.getBigInteger();
    g = data.getBigInteger();
    if (data.available() != 0) {
      throw new IOException("encoded params have " + data.available() + " extra bytes");
    }
  }
  
  protected void engineInit(byte[] paramArrayOfByte, String paramString)
    throws IOException
  {
    engineInit(paramArrayOfByte);
  }
  
  protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> paramClass)
    throws InvalidParameterSpecException
  {
    try
    {
      Class localClass = Class.forName("java.security.spec.DSAParameterSpec");
      if (localClass.isAssignableFrom(paramClass)) {
        return (AlgorithmParameterSpec)paramClass.cast(new DSAParameterSpec(p, q, g));
      }
      throw new InvalidParameterSpecException("Inappropriate parameter Specification");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InvalidParameterSpecException("Unsupported parameter specification: " + localClassNotFoundException.getMessage());
    }
  }
  
  protected byte[] engineGetEncoded()
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.putInteger(p);
    localDerOutputStream2.putInteger(q);
    localDerOutputStream2.putInteger(g);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    return localDerOutputStream1.toByteArray();
  }
  
  protected byte[] engineGetEncoded(String paramString)
    throws IOException
  {
    return engineGetEncoded();
  }
  
  protected String engineToString()
  {
    return "\n\tp: " + Debug.toHexString(p) + "\n\tq: " + Debug.toHexString(q) + "\n\tg: " + Debug.toHexString(g) + "\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DSAParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
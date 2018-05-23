package javax.xml.crypto;

import java.security.spec.AlgorithmParameterSpec;

public abstract interface AlgorithmMethod
{
  public abstract String getAlgorithm();
  
  public abstract AlgorithmParameterSpec getParameterSpec();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\AlgorithmMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
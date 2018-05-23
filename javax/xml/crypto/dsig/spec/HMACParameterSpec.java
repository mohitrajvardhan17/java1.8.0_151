package javax.xml.crypto.dsig.spec;

public final class HMACParameterSpec
  implements SignatureMethodParameterSpec
{
  private int outputLength;
  
  public HMACParameterSpec(int paramInt)
  {
    outputLength = paramInt;
  }
  
  public int getOutputLength()
  {
    return outputLength;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\spec\HMACParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
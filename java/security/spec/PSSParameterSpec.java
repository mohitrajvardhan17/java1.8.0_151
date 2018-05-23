package java.security.spec;

public class PSSParameterSpec
  implements AlgorithmParameterSpec
{
  private String mdName = "SHA-1";
  private String mgfName = "MGF1";
  private AlgorithmParameterSpec mgfSpec = MGF1ParameterSpec.SHA1;
  private int saltLen = 20;
  private int trailerField = 1;
  public static final PSSParameterSpec DEFAULT = new PSSParameterSpec();
  
  private PSSParameterSpec() {}
  
  public PSSParameterSpec(String paramString1, String paramString2, AlgorithmParameterSpec paramAlgorithmParameterSpec, int paramInt1, int paramInt2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("digest algorithm is null");
    }
    if (paramString2 == null) {
      throw new NullPointerException("mask generation function algorithm is null");
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("negative saltLen value: " + paramInt1);
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("negative trailerField: " + paramInt2);
    }
    mdName = paramString1;
    mgfName = paramString2;
    mgfSpec = paramAlgorithmParameterSpec;
    saltLen = paramInt1;
    trailerField = paramInt2;
  }
  
  public PSSParameterSpec(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("negative saltLen value: " + paramInt);
    }
    saltLen = paramInt;
  }
  
  public String getDigestAlgorithm()
  {
    return mdName;
  }
  
  public String getMGFAlgorithm()
  {
    return mgfName;
  }
  
  public AlgorithmParameterSpec getMGFParameters()
  {
    return mgfSpec;
  }
  
  public int getSaltLength()
  {
    return saltLen;
  }
  
  public int getTrailerField()
  {
    return trailerField;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\PSSParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
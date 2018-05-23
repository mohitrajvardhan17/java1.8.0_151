package java.security.spec;

public class ECGenParameterSpec
  implements AlgorithmParameterSpec
{
  private String name;
  
  public ECGenParameterSpec(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("stdName is null");
    }
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\ECGenParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
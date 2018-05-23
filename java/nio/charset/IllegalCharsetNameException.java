package java.nio.charset;

public class IllegalCharsetNameException
  extends IllegalArgumentException
{
  private static final long serialVersionUID = 1457525358470002989L;
  private String charsetName;
  
  public IllegalCharsetNameException(String paramString)
  {
    super(String.valueOf(paramString));
    charsetName = paramString;
  }
  
  public String getCharsetName()
  {
    return charsetName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\IllegalCharsetNameException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.nio.charset;

public class UnsupportedCharsetException
  extends IllegalArgumentException
{
  private static final long serialVersionUID = 1490765524727386367L;
  private String charsetName;
  
  public UnsupportedCharsetException(String paramString)
  {
    super(String.valueOf(paramString));
    charsetName = paramString;
  }
  
  public String getCharsetName()
  {
    return charsetName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\UnsupportedCharsetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
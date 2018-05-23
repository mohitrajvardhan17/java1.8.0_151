package java.util;

public class MissingFormatWidthException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 15560123L;
  private String s;
  
  public MissingFormatWidthException(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    s = paramString;
  }
  
  public String getFormatSpecifier()
  {
    return s;
  }
  
  public String getMessage()
  {
    return s;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\MissingFormatWidthException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
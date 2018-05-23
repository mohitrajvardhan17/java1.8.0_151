package java.util;

public class MissingFormatArgumentException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 19190115L;
  private String s;
  
  public MissingFormatArgumentException(String paramString)
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
    return "Format specifier '" + s + "'";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\MissingFormatArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
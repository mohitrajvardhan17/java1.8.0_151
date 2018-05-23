package java.util;

public class FormatFlagsConversionMismatchException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 19120414L;
  private String f;
  private char c;
  
  public FormatFlagsConversionMismatchException(String paramString, char paramChar)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    f = paramString;
    c = paramChar;
  }
  
  public String getFlags()
  {
    return f;
  }
  
  public char getConversion()
  {
    return c;
  }
  
  public String getMessage()
  {
    return "Conversion = " + c + ", Flags = " + f;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\FormatFlagsConversionMismatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
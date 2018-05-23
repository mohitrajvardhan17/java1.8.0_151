package java.util;

public class UnknownFormatConversionException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 19060418L;
  private String s;
  
  public UnknownFormatConversionException(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    s = paramString;
  }
  
  public String getConversion()
  {
    return s;
  }
  
  public String getMessage()
  {
    return String.format("Conversion = '%s'", new Object[] { s });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\UnknownFormatConversionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
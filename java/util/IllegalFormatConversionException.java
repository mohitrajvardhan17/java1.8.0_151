package java.util;

public class IllegalFormatConversionException
  extends IllegalFormatException
{
  private static final long serialVersionUID = 17000126L;
  private char c;
  private Class<?> arg;
  
  public IllegalFormatConversionException(char paramChar, Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException();
    }
    c = paramChar;
    arg = paramClass;
  }
  
  public char getConversion()
  {
    return c;
  }
  
  public Class<?> getArgumentClass()
  {
    return arg;
  }
  
  public String getMessage()
  {
    return String.format("%c != %s", new Object[] { Character.valueOf(c), arg.getName() });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\IllegalFormatConversionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
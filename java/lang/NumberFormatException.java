package java.lang;

public class NumberFormatException
  extends IllegalArgumentException
{
  static final long serialVersionUID = -2848938806368998894L;
  
  public NumberFormatException() {}
  
  public NumberFormatException(String paramString)
  {
    super(paramString);
  }
  
  static NumberFormatException forInputString(String paramString)
  {
    return new NumberFormatException("For input string: \"" + paramString + "\"");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\NumberFormatException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
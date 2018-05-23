package java.text;

public class ParseException
  extends Exception
{
  private static final long serialVersionUID = 2703218443322787634L;
  private int errorOffset;
  
  public ParseException(String paramString, int paramInt)
  {
    super(paramString);
    errorOffset = paramInt;
  }
  
  public int getErrorOffset()
  {
    return errorOffset;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
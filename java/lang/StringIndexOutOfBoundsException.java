package java.lang;

public class StringIndexOutOfBoundsException
  extends IndexOutOfBoundsException
{
  private static final long serialVersionUID = -6762910422159637258L;
  
  public StringIndexOutOfBoundsException() {}
  
  public StringIndexOutOfBoundsException(String paramString)
  {
    super(paramString);
  }
  
  public StringIndexOutOfBoundsException(int paramInt)
  {
    super("String index out of range: " + paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\StringIndexOutOfBoundsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
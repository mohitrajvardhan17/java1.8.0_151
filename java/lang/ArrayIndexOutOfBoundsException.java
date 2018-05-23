package java.lang;

public class ArrayIndexOutOfBoundsException
  extends IndexOutOfBoundsException
{
  private static final long serialVersionUID = -5116101128118950844L;
  
  public ArrayIndexOutOfBoundsException() {}
  
  public ArrayIndexOutOfBoundsException(int paramInt)
  {
    super("Array index out of range: " + paramInt);
  }
  
  public ArrayIndexOutOfBoundsException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ArrayIndexOutOfBoundsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
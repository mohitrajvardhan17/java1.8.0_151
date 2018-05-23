package java.lang.invoke;

public class WrongMethodTypeException
  extends RuntimeException
{
  private static final long serialVersionUID = 292L;
  
  public WrongMethodTypeException() {}
  
  public WrongMethodTypeException(String paramString)
  {
    super(paramString);
  }
  
  WrongMethodTypeException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  WrongMethodTypeException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\WrongMethodTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
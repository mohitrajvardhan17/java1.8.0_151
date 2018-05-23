package java.lang;

public class SecurityException
  extends RuntimeException
{
  private static final long serialVersionUID = 6878364983674394167L;
  
  public SecurityException() {}
  
  public SecurityException(String paramString)
  {
    super(paramString);
  }
  
  public SecurityException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SecurityException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\SecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.lang;

public class IllegalArgumentException
  extends RuntimeException
{
  private static final long serialVersionUID = -5365630128856068164L;
  
  public IllegalArgumentException() {}
  
  public IllegalArgumentException(String paramString)
  {
    super(paramString);
  }
  
  public IllegalArgumentException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public IllegalArgumentException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\IllegalArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
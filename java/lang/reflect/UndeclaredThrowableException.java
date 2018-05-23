package java.lang.reflect;

public class UndeclaredThrowableException
  extends RuntimeException
{
  static final long serialVersionUID = 330127114055056639L;
  private Throwable undeclaredThrowable;
  
  public UndeclaredThrowableException(Throwable paramThrowable)
  {
    super((Throwable)null);
    undeclaredThrowable = paramThrowable;
  }
  
  public UndeclaredThrowableException(Throwable paramThrowable, String paramString)
  {
    super(paramString, null);
    undeclaredThrowable = paramThrowable;
  }
  
  public Throwable getUndeclaredThrowable()
  {
    return undeclaredThrowable;
  }
  
  public Throwable getCause()
  {
    return undeclaredThrowable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\UndeclaredThrowableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
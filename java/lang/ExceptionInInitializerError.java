package java.lang;

public class ExceptionInInitializerError
  extends LinkageError
{
  private static final long serialVersionUID = 1521711792217232256L;
  private Throwable exception;
  
  public ExceptionInInitializerError()
  {
    initCause(null);
  }
  
  public ExceptionInInitializerError(Throwable paramThrowable)
  {
    initCause(null);
    exception = paramThrowable;
  }
  
  public ExceptionInInitializerError(String paramString)
  {
    super(paramString);
    initCause(null);
  }
  
  public Throwable getException()
  {
    return exception;
  }
  
  public Throwable getCause()
  {
    return exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ExceptionInInitializerError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.lang.reflect;

public class InvocationTargetException
  extends ReflectiveOperationException
{
  private static final long serialVersionUID = 4085088731926701167L;
  private Throwable target;
  
  protected InvocationTargetException()
  {
    super((Throwable)null);
  }
  
  public InvocationTargetException(Throwable paramThrowable)
  {
    super((Throwable)null);
    target = paramThrowable;
  }
  
  public InvocationTargetException(Throwable paramThrowable, String paramString)
  {
    super(paramString, null);
    target = paramThrowable;
  }
  
  public Throwable getTargetException()
  {
    return target;
  }
  
  public Throwable getCause()
  {
    return target;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\InvocationTargetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
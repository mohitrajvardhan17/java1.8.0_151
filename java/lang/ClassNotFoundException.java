package java.lang;

public class ClassNotFoundException
  extends ReflectiveOperationException
{
  private static final long serialVersionUID = 9176873029745254542L;
  private Throwable ex;
  
  public ClassNotFoundException()
  {
    super((Throwable)null);
  }
  
  public ClassNotFoundException(String paramString)
  {
    super(paramString, null);
  }
  
  public ClassNotFoundException(String paramString, Throwable paramThrowable)
  {
    super(paramString, null);
    ex = paramThrowable;
  }
  
  public Throwable getException()
  {
    return ex;
  }
  
  public Throwable getCause()
  {
    return ex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\ClassNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
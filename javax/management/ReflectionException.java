package javax.management;

public class ReflectionException
  extends JMException
{
  private static final long serialVersionUID = 9170809325636915553L;
  private Exception exception;
  
  public ReflectionException(Exception paramException)
  {
    exception = paramException;
  }
  
  public ReflectionException(Exception paramException, String paramString)
  {
    super(paramString);
    exception = paramException;
  }
  
  public Exception getTargetException()
  {
    return exception;
  }
  
  public Throwable getCause()
  {
    return exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\ReflectionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
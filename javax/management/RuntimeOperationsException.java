package javax.management;

public class RuntimeOperationsException
  extends JMRuntimeException
{
  private static final long serialVersionUID = -8408923047489133588L;
  private RuntimeException runtimeException;
  
  public RuntimeOperationsException(RuntimeException paramRuntimeException)
  {
    runtimeException = paramRuntimeException;
  }
  
  public RuntimeOperationsException(RuntimeException paramRuntimeException, String paramString)
  {
    super(paramString);
    runtimeException = paramRuntimeException;
  }
  
  public RuntimeException getTargetException()
  {
    return runtimeException;
  }
  
  public Throwable getCause()
  {
    return runtimeException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\RuntimeOperationsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
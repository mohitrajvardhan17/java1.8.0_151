package javax.management;

public class RuntimeMBeanException
  extends JMRuntimeException
{
  private static final long serialVersionUID = 5274912751982730171L;
  private RuntimeException runtimeException;
  
  public RuntimeMBeanException(RuntimeException paramRuntimeException)
  {
    runtimeException = paramRuntimeException;
  }
  
  public RuntimeMBeanException(RuntimeException paramRuntimeException, String paramString)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\RuntimeMBeanException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
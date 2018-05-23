package javax.management;

public class RuntimeErrorException
  extends JMRuntimeException
{
  private static final long serialVersionUID = 704338937753949796L;
  private Error error;
  
  public RuntimeErrorException(Error paramError)
  {
    error = paramError;
  }
  
  public RuntimeErrorException(Error paramError, String paramString)
  {
    super(paramString);
    error = paramError;
  }
  
  public Error getTargetError()
  {
    return error;
  }
  
  public Throwable getCause()
  {
    return error;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\RuntimeErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
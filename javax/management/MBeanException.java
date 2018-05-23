package javax.management;

public class MBeanException
  extends JMException
{
  private static final long serialVersionUID = 4066342430588744142L;
  private Exception exception;
  
  public MBeanException(Exception paramException)
  {
    exception = paramException;
  }
  
  public MBeanException(Exception paramException, String paramString)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
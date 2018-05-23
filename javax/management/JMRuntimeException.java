package javax.management;

public class JMRuntimeException
  extends RuntimeException
{
  private static final long serialVersionUID = 6573344628407841861L;
  
  public JMRuntimeException() {}
  
  public JMRuntimeException(String paramString)
  {
    super(paramString);
  }
  
  JMRuntimeException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\JMRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.rmi.activation;

public class ActivationException
  extends Exception
{
  public Throwable detail;
  private static final long serialVersionUID = -4320118837291406071L;
  
  public ActivationException()
  {
    initCause(null);
  }
  
  public ActivationException(String paramString)
  {
    super(paramString);
    initCause(null);
  }
  
  public ActivationException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(null);
    detail = paramThrowable;
  }
  
  public String getMessage()
  {
    if (detail == null) {
      return super.getMessage();
    }
    return super.getMessage() + "; nested exception is: \n\t" + detail.toString();
  }
  
  public Throwable getCause()
  {
    return detail;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
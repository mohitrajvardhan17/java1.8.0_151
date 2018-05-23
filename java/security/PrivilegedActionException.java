package java.security;

public class PrivilegedActionException
  extends Exception
{
  private static final long serialVersionUID = 4724086851538908602L;
  private Exception exception;
  
  public PrivilegedActionException(Exception paramException)
  {
    super((Throwable)null);
    exception = paramException;
  }
  
  public Exception getException()
  {
    return exception;
  }
  
  public Throwable getCause()
  {
    return exception;
  }
  
  public String toString()
  {
    String str = getClass().getName();
    return exception != null ? str + ": " + exception.toString() : str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PrivilegedActionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
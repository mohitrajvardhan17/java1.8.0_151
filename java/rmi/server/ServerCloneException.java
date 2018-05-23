package java.rmi.server;

public class ServerCloneException
  extends CloneNotSupportedException
{
  public Exception detail;
  private static final long serialVersionUID = 6617456357664815945L;
  
  public ServerCloneException(String paramString)
  {
    super(paramString);
    initCause(null);
  }
  
  public ServerCloneException(String paramString, Exception paramException)
  {
    super(paramString);
    initCause(null);
    detail = paramException;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\ServerCloneException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
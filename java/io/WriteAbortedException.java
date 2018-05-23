package java.io;

public class WriteAbortedException
  extends ObjectStreamException
{
  private static final long serialVersionUID = -3326426625597282442L;
  public Exception detail;
  
  public WriteAbortedException(String paramString, Exception paramException)
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
    return super.getMessage() + "; " + detail.toString();
  }
  
  public Throwable getCause()
  {
    return detail;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\WriteAbortedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
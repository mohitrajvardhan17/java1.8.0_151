package javax.xml.parsers;

public class FactoryConfigurationError
  extends Error
{
  private static final long serialVersionUID = -827108682472263355L;
  private Exception exception;
  
  public FactoryConfigurationError()
  {
    exception = null;
  }
  
  public FactoryConfigurationError(String paramString)
  {
    super(paramString);
    exception = null;
  }
  
  public FactoryConfigurationError(Exception paramException)
  {
    super(paramException.toString());
    exception = paramException;
  }
  
  public FactoryConfigurationError(Exception paramException, String paramString)
  {
    super(paramString);
    exception = paramException;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if ((str == null) && (exception != null)) {
      return exception.getMessage();
    }
    return str;
  }
  
  public Exception getException()
  {
    return exception;
  }
  
  public Throwable getCause()
  {
    return exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\parsers\FactoryConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
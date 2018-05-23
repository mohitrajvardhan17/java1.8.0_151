package javax.xml.transform;

public class TransformerFactoryConfigurationError
  extends Error
{
  private static final long serialVersionUID = -6527718720676281516L;
  private Exception exception;
  
  public TransformerFactoryConfigurationError()
  {
    exception = null;
  }
  
  public TransformerFactoryConfigurationError(String paramString)
  {
    super(paramString);
    exception = null;
  }
  
  public TransformerFactoryConfigurationError(Exception paramException)
  {
    super(paramException.toString());
    exception = paramException;
  }
  
  public TransformerFactoryConfigurationError(Exception paramException, String paramString)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\TransformerFactoryConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
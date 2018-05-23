package javax.xml.stream;

public class FactoryConfigurationError
  extends Error
{
  private static final long serialVersionUID = -2994412584589975744L;
  Exception nested;
  
  public FactoryConfigurationError() {}
  
  public FactoryConfigurationError(Exception paramException)
  {
    nested = paramException;
  }
  
  public FactoryConfigurationError(Exception paramException, String paramString)
  {
    super(paramString);
    nested = paramException;
  }
  
  public FactoryConfigurationError(String paramString, Exception paramException)
  {
    super(paramString);
    nested = paramException;
  }
  
  public FactoryConfigurationError(String paramString)
  {
    super(paramString);
  }
  
  public Exception getException()
  {
    return nested;
  }
  
  public Throwable getCause()
  {
    return nested;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if (str != null) {
      return str;
    }
    if (nested != null)
    {
      str = nested.getMessage();
      if (str == null) {
        str = nested.getClass().toString();
      }
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\FactoryConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
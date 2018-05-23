package javax.xml.ws;

public class WebServiceException
  extends RuntimeException
{
  public WebServiceException() {}
  
  public WebServiceException(String paramString)
  {
    super(paramString);
  }
  
  public WebServiceException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public WebServiceException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\WebServiceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
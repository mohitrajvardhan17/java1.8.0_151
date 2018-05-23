package javax.security.sasl;

public class AuthenticationException
  extends SaslException
{
  private static final long serialVersionUID = -3579708765071815007L;
  
  public AuthenticationException() {}
  
  public AuthenticationException(String paramString)
  {
    super(paramString);
  }
  
  public AuthenticationException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\sasl\AuthenticationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.security;

public class SignatureException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = 7509989324975124438L;
  
  public SignatureException() {}
  
  public SignatureException(String paramString)
  {
    super(paramString);
  }
  
  public SignatureException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public SignatureException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\SignatureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
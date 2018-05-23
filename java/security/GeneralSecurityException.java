package java.security;

public class GeneralSecurityException
  extends Exception
{
  private static final long serialVersionUID = 894798122053539237L;
  
  public GeneralSecurityException() {}
  
  public GeneralSecurityException(String paramString)
  {
    super(paramString);
  }
  
  public GeneralSecurityException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public GeneralSecurityException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\GeneralSecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
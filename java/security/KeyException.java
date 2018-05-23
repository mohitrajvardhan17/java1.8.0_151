package java.security;

public class KeyException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = -7483676942812432108L;
  
  public KeyException() {}
  
  public KeyException(String paramString)
  {
    super(paramString);
  }
  
  public KeyException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public KeyException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
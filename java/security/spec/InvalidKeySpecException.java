package java.security.spec;

import java.security.GeneralSecurityException;

public class InvalidKeySpecException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = 3546139293998810778L;
  
  public InvalidKeySpecException() {}
  
  public InvalidKeySpecException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidKeySpecException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public InvalidKeySpecException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\InvalidKeySpecException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
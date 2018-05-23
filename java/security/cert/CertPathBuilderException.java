package java.security.cert;

import java.security.GeneralSecurityException;

public class CertPathBuilderException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = 5316471420178794402L;
  
  public CertPathBuilderException() {}
  
  public CertPathBuilderException(String paramString)
  {
    super(paramString);
  }
  
  public CertPathBuilderException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public CertPathBuilderException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathBuilderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
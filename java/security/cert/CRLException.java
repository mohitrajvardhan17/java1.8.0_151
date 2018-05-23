package java.security.cert;

import java.security.GeneralSecurityException;

public class CRLException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = -6694728944094197147L;
  
  public CRLException() {}
  
  public CRLException(String paramString)
  {
    super(paramString);
  }
  
  public CRLException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public CRLException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CRLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
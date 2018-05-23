package java.security.cert;

import java.security.GeneralSecurityException;

public class CertStoreException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = 2395296107471573245L;
  
  public CertStoreException() {}
  
  public CertStoreException(String paramString)
  {
    super(paramString);
  }
  
  public CertStoreException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public CertStoreException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertStoreException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
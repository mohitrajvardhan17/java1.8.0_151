package java.security.cert;

public class CertificateParsingException
  extends CertificateException
{
  private static final long serialVersionUID = -7989222416793322029L;
  
  public CertificateParsingException() {}
  
  public CertificateParsingException(String paramString)
  {
    super(paramString);
  }
  
  public CertificateParsingException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public CertificateParsingException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertificateParsingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
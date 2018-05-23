package java.security.cert;

public abstract class CRL
{
  private String type;
  
  protected CRL(String paramString)
  {
    type = paramString;
  }
  
  public final String getType()
  {
    return type;
  }
  
  public abstract String toString();
  
  public abstract boolean isRevoked(Certificate paramCertificate);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CRL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
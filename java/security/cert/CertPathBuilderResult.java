package java.security.cert;

public abstract interface CertPathBuilderResult
  extends Cloneable
{
  public abstract CertPath getCertPath();
  
  public abstract Object clone();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathBuilderResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
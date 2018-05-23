package java.security.cert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public abstract class PKIXCertPathChecker
  implements CertPathChecker, Cloneable
{
  protected PKIXCertPathChecker() {}
  
  public abstract void init(boolean paramBoolean)
    throws CertPathValidatorException;
  
  public abstract boolean isForwardCheckingSupported();
  
  public abstract Set<String> getSupportedExtensions();
  
  public abstract void check(Certificate paramCertificate, Collection<String> paramCollection)
    throws CertPathValidatorException;
  
  public void check(Certificate paramCertificate)
    throws CertPathValidatorException
  {
    check(paramCertificate, Collections.emptySet());
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PKIXCertPathChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
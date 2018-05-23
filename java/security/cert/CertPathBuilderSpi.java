package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathBuilderSpi
{
  public CertPathBuilderSpi() {}
  
  public abstract CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters)
    throws CertPathBuilderException, InvalidAlgorithmParameterException;
  
  public CertPathChecker engineGetRevocationChecker()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathBuilderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
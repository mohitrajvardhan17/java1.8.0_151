package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathValidatorSpi
{
  public CertPathValidatorSpi() {}
  
  public abstract CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters)
    throws CertPathValidatorException, InvalidAlgorithmParameterException;
  
  public CertPathChecker engineGetRevocationChecker()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathValidatorSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
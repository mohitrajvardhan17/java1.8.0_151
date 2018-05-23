package javax.net.ssl;

import java.security.cert.CertPathParameters;

public class CertPathTrustManagerParameters
  implements ManagerFactoryParameters
{
  private final CertPathParameters parameters;
  
  public CertPathTrustManagerParameters(CertPathParameters paramCertPathParameters)
  {
    parameters = ((CertPathParameters)paramCertPathParameters.clone());
  }
  
  public CertPathParameters getParameters()
  {
    return (CertPathParameters)parameters.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\CertPathTrustManagerParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public class CertPathValidator
{
  private static final String CPV_TYPE = "certpathvalidator.type";
  private final CertPathValidatorSpi validatorSpi;
  private final Provider provider;
  private final String algorithm;
  
  protected CertPathValidator(CertPathValidatorSpi paramCertPathValidatorSpi, Provider paramProvider, String paramString)
  {
    validatorSpi = paramCertPathValidatorSpi;
    provider = paramProvider;
    algorithm = paramString;
  }
  
  public static CertPathValidator getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, paramString);
    return new CertPathValidator((CertPathValidatorSpi)impl, provider, paramString);
  }
  
  public static CertPathValidator getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, paramString1, paramString2);
    return new CertPathValidator((CertPathValidatorSpi)impl, provider, paramString1);
  }
  
  public static CertPathValidator getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, paramString, paramProvider);
    return new CertPathValidator((CertPathValidatorSpi)impl, provider, paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public final CertPathValidatorResult validate(CertPath paramCertPath, CertPathParameters paramCertPathParameters)
    throws CertPathValidatorException, InvalidAlgorithmParameterException
  {
    return validatorSpi.engineValidate(paramCertPath, paramCertPathParameters);
  }
  
  public static final String getDefaultType()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("certpathvalidator.type");
      }
    });
    return str == null ? "PKIX" : str;
  }
  
  public final CertPathChecker getRevocationChecker()
  {
    return validatorSpi.engineGetRevocationChecker();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
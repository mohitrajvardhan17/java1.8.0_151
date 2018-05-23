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

public class CertPathBuilder
{
  private static final String CPB_TYPE = "certpathbuilder.type";
  private final CertPathBuilderSpi builderSpi;
  private final Provider provider;
  private final String algorithm;
  
  protected CertPathBuilder(CertPathBuilderSpi paramCertPathBuilderSpi, Provider paramProvider, String paramString)
  {
    builderSpi = paramCertPathBuilderSpi;
    provider = paramProvider;
    algorithm = paramString;
  }
  
  public static CertPathBuilder getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, paramString);
    return new CertPathBuilder((CertPathBuilderSpi)impl, provider, paramString);
  }
  
  public static CertPathBuilder getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, paramString1, paramString2);
    return new CertPathBuilder((CertPathBuilderSpi)impl, provider, paramString1);
  }
  
  public static CertPathBuilder getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, paramString, paramProvider);
    return new CertPathBuilder((CertPathBuilderSpi)impl, provider, paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public final CertPathBuilderResult build(CertPathParameters paramCertPathParameters)
    throws CertPathBuilderException, InvalidAlgorithmParameterException
  {
    return builderSpi.engineBuild(paramCertPathParameters);
  }
  
  public static final String getDefaultType()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("certpathbuilder.type");
      }
    });
    return str == null ? "PKIX" : str;
  }
  
  public final CertPathChecker getRevocationChecker()
  {
    return builderSpi.engineGetRevocationChecker();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertPathBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.security.cert;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public class CertificateFactory
{
  private String type;
  private Provider provider;
  private CertificateFactorySpi certFacSpi;
  
  protected CertificateFactory(CertificateFactorySpi paramCertificateFactorySpi, Provider paramProvider, String paramString)
  {
    certFacSpi = paramCertificateFactorySpi;
    provider = paramProvider;
    type = paramString;
  }
  
  public static final CertificateFactory getInstance(String paramString)
    throws CertificateException
  {
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, paramString);
      return new CertificateFactory((CertificateFactorySpi)impl, provider, paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new CertificateException(paramString + " not found", localNoSuchAlgorithmException);
    }
  }
  
  public static final CertificateFactory getInstance(String paramString1, String paramString2)
    throws CertificateException, NoSuchProviderException
  {
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, paramString1, paramString2);
      return new CertificateFactory((CertificateFactorySpi)impl, provider, paramString1);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new CertificateException(paramString1 + " not found", localNoSuchAlgorithmException);
    }
  }
  
  public static final CertificateFactory getInstance(String paramString, Provider paramProvider)
    throws CertificateException
  {
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("CertificateFactory", CertificateFactorySpi.class, paramString, paramProvider);
      return new CertificateFactory((CertificateFactorySpi)impl, provider, paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new CertificateException(paramString + " not found", localNoSuchAlgorithmException);
    }
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final String getType()
  {
    return type;
  }
  
  public final Certificate generateCertificate(InputStream paramInputStream)
    throws CertificateException
  {
    return certFacSpi.engineGenerateCertificate(paramInputStream);
  }
  
  public final Iterator<String> getCertPathEncodings()
  {
    return certFacSpi.engineGetCertPathEncodings();
  }
  
  public final CertPath generateCertPath(InputStream paramInputStream)
    throws CertificateException
  {
    return certFacSpi.engineGenerateCertPath(paramInputStream);
  }
  
  public final CertPath generateCertPath(InputStream paramInputStream, String paramString)
    throws CertificateException
  {
    return certFacSpi.engineGenerateCertPath(paramInputStream, paramString);
  }
  
  public final CertPath generateCertPath(List<? extends Certificate> paramList)
    throws CertificateException
  {
    return certFacSpi.engineGenerateCertPath(paramList);
  }
  
  public final Collection<? extends Certificate> generateCertificates(InputStream paramInputStream)
    throws CertificateException
  {
    return certFacSpi.engineGenerateCertificates(paramInputStream);
  }
  
  public final CRL generateCRL(InputStream paramInputStream)
    throws CRLException
  {
    return certFacSpi.engineGenerateCRL(paramInputStream);
  }
  
  public final Collection<? extends CRL> generateCRLs(InputStream paramInputStream)
    throws CRLException
  {
    return certFacSpi.engineGenerateCRLs(paramInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertificateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
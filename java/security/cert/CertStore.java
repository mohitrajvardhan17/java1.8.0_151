package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public class CertStore
{
  private static final String CERTSTORE_TYPE = "certstore.type";
  private CertStoreSpi storeSpi;
  private Provider provider;
  private String type;
  private CertStoreParameters params;
  
  protected CertStore(CertStoreSpi paramCertStoreSpi, Provider paramProvider, String paramString, CertStoreParameters paramCertStoreParameters)
  {
    storeSpi = paramCertStoreSpi;
    provider = paramProvider;
    type = paramString;
    if (paramCertStoreParameters != null) {
      params = ((CertStoreParameters)paramCertStoreParameters.clone());
    }
  }
  
  public final Collection<? extends Certificate> getCertificates(CertSelector paramCertSelector)
    throws CertStoreException
  {
    return storeSpi.engineGetCertificates(paramCertSelector);
  }
  
  public final Collection<? extends CRL> getCRLs(CRLSelector paramCRLSelector)
    throws CertStoreException
  {
    return storeSpi.engineGetCRLs(paramCRLSelector);
  }
  
  public static CertStore getInstance(String paramString, CertStoreParameters paramCertStoreParameters)
    throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
  {
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("CertStore", CertStoreSpi.class, paramString, paramCertStoreParameters);
      return new CertStore((CertStoreSpi)impl, provider, paramString, paramCertStoreParameters);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  private static CertStore handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
  {
    Throwable localThrowable = paramNoSuchAlgorithmException.getCause();
    if ((localThrowable instanceof InvalidAlgorithmParameterException)) {
      throw ((InvalidAlgorithmParameterException)localThrowable);
    }
    throw paramNoSuchAlgorithmException;
  }
  
  public static CertStore getInstance(String paramString1, CertStoreParameters paramCertStoreParameters, String paramString2)
    throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException
  {
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("CertStore", CertStoreSpi.class, paramString1, paramCertStoreParameters, paramString2);
      return new CertStore((CertStoreSpi)impl, provider, paramString1, paramCertStoreParameters);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  public static CertStore getInstance(String paramString, CertStoreParameters paramCertStoreParameters, Provider paramProvider)
    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
  {
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("CertStore", CertStoreSpi.class, paramString, paramCertStoreParameters, paramProvider);
      return new CertStore((CertStoreSpi)impl, provider, paramString, paramCertStoreParameters);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  public final CertStoreParameters getCertStoreParameters()
  {
    return params == null ? null : (CertStoreParameters)params.clone();
  }
  
  public final String getType()
  {
    return type;
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public static final String getDefaultType()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("certstore.type");
      }
    });
    if (str == null) {
      str = "LDAP";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
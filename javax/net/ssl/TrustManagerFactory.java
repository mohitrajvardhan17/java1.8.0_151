package javax.net.ssl;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public class TrustManagerFactory
{
  private Provider provider;
  private TrustManagerFactorySpi factorySpi;
  private String algorithm;
  
  public static final String getDefaultAlgorithm()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("ssl.TrustManagerFactory.algorithm");
      }
    });
    if (str == null) {
      str = "SunX509";
    }
    return str;
  }
  
  protected TrustManagerFactory(TrustManagerFactorySpi paramTrustManagerFactorySpi, Provider paramProvider, String paramString)
  {
    factorySpi = paramTrustManagerFactorySpi;
    provider = paramProvider;
    algorithm = paramString;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public static final TrustManagerFactory getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, paramString);
    return new TrustManagerFactory((TrustManagerFactorySpi)impl, provider, paramString);
  }
  
  public static final TrustManagerFactory getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, paramString1, paramString2);
    return new TrustManagerFactory((TrustManagerFactorySpi)impl, provider, paramString1);
  }
  
  public static final TrustManagerFactory getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("TrustManagerFactory", TrustManagerFactorySpi.class, paramString, paramProvider);
    return new TrustManagerFactory((TrustManagerFactorySpi)impl, provider, paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final void init(KeyStore paramKeyStore)
    throws KeyStoreException
  {
    factorySpi.engineInit(paramKeyStore);
  }
  
  public final void init(ManagerFactoryParameters paramManagerFactoryParameters)
    throws InvalidAlgorithmParameterException
  {
    factorySpi.engineInit(paramManagerFactoryParameters);
  }
  
  public final TrustManager[] getTrustManagers()
  {
    return factorySpi.engineGetTrustManagers();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\TrustManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
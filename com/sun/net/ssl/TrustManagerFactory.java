package com.sun.net.ssl;

import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;

@Deprecated
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
        return Security.getProperty("sun.ssl.trustmanager.type");
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
    try
    {
      Object[] arrayOfObject = SSLSecurity.getImpl(paramString, "TrustManagerFactory", (String)null);
      return new TrustManagerFactory((TrustManagerFactorySpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new NoSuchAlgorithmException(paramString + " not found");
    }
  }
  
  public static final TrustManagerFactory getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = SSLSecurity.getImpl(paramString1, "TrustManagerFactory", paramString2);
    return new TrustManagerFactory((TrustManagerFactorySpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static final TrustManagerFactory getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = SSLSecurity.getImpl(paramString, "TrustManagerFactory", paramProvider);
    return new TrustManagerFactory((TrustManagerFactorySpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public void init(KeyStore paramKeyStore)
    throws KeyStoreException
  {
    factorySpi.engineInit(paramKeyStore);
  }
  
  public TrustManager[] getTrustManagers()
  {
    return factorySpi.engineGetTrustManagers();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\TrustManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
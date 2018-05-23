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
import java.security.UnrecoverableKeyException;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public class KeyManagerFactory
{
  private Provider provider;
  private KeyManagerFactorySpi factorySpi;
  private String algorithm;
  
  public static final String getDefaultAlgorithm()
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("ssl.KeyManagerFactory.algorithm");
      }
    });
    if (str == null) {
      str = "SunX509";
    }
    return str;
  }
  
  protected KeyManagerFactory(KeyManagerFactorySpi paramKeyManagerFactorySpi, Provider paramProvider, String paramString)
  {
    factorySpi = paramKeyManagerFactorySpi;
    provider = paramProvider;
    algorithm = paramString;
  }
  
  public final String getAlgorithm()
  {
    return algorithm;
  }
  
  public static final KeyManagerFactory getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyManagerFactory", KeyManagerFactorySpi.class, paramString);
    return new KeyManagerFactory((KeyManagerFactorySpi)impl, provider, paramString);
  }
  
  public static final KeyManagerFactory getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyManagerFactory", KeyManagerFactorySpi.class, paramString1, paramString2);
    return new KeyManagerFactory((KeyManagerFactorySpi)impl, provider, paramString1);
  }
  
  public static final KeyManagerFactory getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("KeyManagerFactory", KeyManagerFactorySpi.class, paramString, paramProvider);
    return new KeyManagerFactory((KeyManagerFactorySpi)impl, provider, paramString);
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final void init(KeyStore paramKeyStore, char[] paramArrayOfChar)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
  {
    factorySpi.engineInit(paramKeyStore, paramArrayOfChar);
  }
  
  public final void init(ManagerFactoryParameters paramManagerFactoryParameters)
    throws InvalidAlgorithmParameterException
  {
    factorySpi.engineInit(paramManagerFactoryParameters);
  }
  
  public final KeyManager[] getKeyManagers()
  {
    return factorySpi.engineGetKeyManagers();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\KeyManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
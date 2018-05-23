package javax.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public class SSLContext
{
  private final Provider provider;
  private final SSLContextSpi contextSpi;
  private final String protocol;
  private static SSLContext defaultContext;
  
  protected SSLContext(SSLContextSpi paramSSLContextSpi, Provider paramProvider, String paramString)
  {
    contextSpi = paramSSLContextSpi;
    provider = paramProvider;
    protocol = paramString;
  }
  
  public static synchronized SSLContext getDefault()
    throws NoSuchAlgorithmException
  {
    if (defaultContext == null) {
      defaultContext = getInstance("Default");
    }
    return defaultContext;
  }
  
  public static synchronized void setDefault(SSLContext paramSSLContext)
  {
    if (paramSSLContext == null) {
      throw new NullPointerException();
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SSLPermission("setDefaultSSLContext"));
    }
    defaultContext = paramSSLContext;
  }
  
  public static SSLContext getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("SSLContext", SSLContextSpi.class, paramString);
    return new SSLContext((SSLContextSpi)impl, provider, paramString);
  }
  
  public static SSLContext getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("SSLContext", SSLContextSpi.class, paramString1, paramString2);
    return new SSLContext((SSLContextSpi)impl, provider, paramString1);
  }
  
  public static SSLContext getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("SSLContext", SSLContextSpi.class, paramString, paramProvider);
    return new SSLContext((SSLContextSpi)impl, provider, paramString);
  }
  
  public final String getProtocol()
  {
    return protocol;
  }
  
  public final Provider getProvider()
  {
    return provider;
  }
  
  public final void init(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom)
    throws KeyManagementException
  {
    contextSpi.engineInit(paramArrayOfKeyManager, paramArrayOfTrustManager, paramSecureRandom);
  }
  
  public final SSLSocketFactory getSocketFactory()
  {
    return contextSpi.engineGetSocketFactory();
  }
  
  public final SSLServerSocketFactory getServerSocketFactory()
  {
    return contextSpi.engineGetServerSocketFactory();
  }
  
  public final SSLEngine createSSLEngine()
  {
    try
    {
      return contextSpi.engineCreateSSLEngine();
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      UnsupportedOperationException localUnsupportedOperationException = new UnsupportedOperationException("Provider: " + getProvider() + " doesn't support this operation");
      localUnsupportedOperationException.initCause(localAbstractMethodError);
      throw localUnsupportedOperationException;
    }
  }
  
  public final SSLEngine createSSLEngine(String paramString, int paramInt)
  {
    try
    {
      return contextSpi.engineCreateSSLEngine(paramString, paramInt);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      UnsupportedOperationException localUnsupportedOperationException = new UnsupportedOperationException("Provider: " + getProvider() + " does not support this operation");
      localUnsupportedOperationException.initCause(localAbstractMethodError);
      throw localUnsupportedOperationException;
    }
  }
  
  public final SSLSessionContext getServerSessionContext()
  {
    return contextSpi.engineGetServerSessionContext();
  }
  
  public final SSLSessionContext getClientSessionContext()
  {
    return contextSpi.engineGetClientSessionContext();
  }
  
  public final SSLParameters getDefaultSSLParameters()
  {
    return contextSpi.engineGetDefaultSSLParameters();
  }
  
  public final SSLParameters getSupportedSSLParameters()
  {
    return contextSpi.engineGetSupportedSSLParameters();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

@Deprecated
public class SSLContext
{
  private Provider provider;
  private SSLContextSpi contextSpi;
  private String protocol;
  
  protected SSLContext(SSLContextSpi paramSSLContextSpi, Provider paramProvider, String paramString)
  {
    contextSpi = paramSSLContextSpi;
    provider = paramProvider;
    protocol = paramString;
  }
  
  public static SSLContext getInstance(String paramString)
    throws NoSuchAlgorithmException
  {
    try
    {
      Object[] arrayOfObject = SSLSecurity.getImpl(paramString, "SSLContext", (String)null);
      return new SSLContext((SSLContextSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new NoSuchAlgorithmException(paramString + " not found");
    }
  }
  
  public static SSLContext getInstance(String paramString1, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = SSLSecurity.getImpl(paramString1, "SSLContext", paramString2);
    return new SSLContext((SSLContextSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static SSLContext getInstance(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    Object[] arrayOfObject = SSLSecurity.getImpl(paramString, "SSLContext", paramProvider);
    return new SSLContext((SSLContextSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\SSLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
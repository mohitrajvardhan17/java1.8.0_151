package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

final class SSLContextSpiWrapper
  extends SSLContextSpi
{
  private SSLContext theSSLContext;
  
  SSLContextSpiWrapper(String paramString, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    theSSLContext = SSLContext.getInstance(paramString, paramProvider);
  }
  
  protected void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom)
    throws KeyManagementException
  {
    javax.net.ssl.KeyManager[] arrayOfKeyManager;
    int j;
    int i;
    if (paramArrayOfKeyManager != null)
    {
      arrayOfKeyManager = new javax.net.ssl.KeyManager[paramArrayOfKeyManager.length];
      j = 0;
      i = 0;
      while (j < paramArrayOfKeyManager.length)
      {
        if (!(paramArrayOfKeyManager[j] instanceof javax.net.ssl.KeyManager))
        {
          if ((paramArrayOfKeyManager[j] instanceof X509KeyManager))
          {
            arrayOfKeyManager[i] = new X509KeyManagerJavaxWrapper((X509KeyManager)paramArrayOfKeyManager[j]);
            i++;
          }
        }
        else
        {
          arrayOfKeyManager[i] = ((javax.net.ssl.KeyManager)paramArrayOfKeyManager[j]);
          i++;
        }
        j++;
      }
      if (i != j) {
        arrayOfKeyManager = (javax.net.ssl.KeyManager[])SSLSecurity.truncateArray(arrayOfKeyManager, new javax.net.ssl.KeyManager[i]);
      }
    }
    else
    {
      arrayOfKeyManager = null;
    }
    javax.net.ssl.TrustManager[] arrayOfTrustManager;
    if (paramArrayOfTrustManager != null)
    {
      arrayOfTrustManager = new javax.net.ssl.TrustManager[paramArrayOfTrustManager.length];
      j = 0;
      i = 0;
      while (j < paramArrayOfTrustManager.length)
      {
        if (!(paramArrayOfTrustManager[j] instanceof javax.net.ssl.TrustManager))
        {
          if ((paramArrayOfTrustManager[j] instanceof X509TrustManager))
          {
            arrayOfTrustManager[i] = new X509TrustManagerJavaxWrapper((X509TrustManager)paramArrayOfTrustManager[j]);
            i++;
          }
        }
        else
        {
          arrayOfTrustManager[i] = ((javax.net.ssl.TrustManager)paramArrayOfTrustManager[j]);
          i++;
        }
        j++;
      }
      if (i != j) {
        arrayOfTrustManager = (javax.net.ssl.TrustManager[])SSLSecurity.truncateArray(arrayOfTrustManager, new javax.net.ssl.TrustManager[i]);
      }
    }
    else
    {
      arrayOfTrustManager = null;
    }
    theSSLContext.init(arrayOfKeyManager, arrayOfTrustManager, paramSecureRandom);
  }
  
  protected SSLSocketFactory engineGetSocketFactory()
  {
    return theSSLContext.getSocketFactory();
  }
  
  protected SSLServerSocketFactory engineGetServerSocketFactory()
  {
    return theSSLContext.getServerSocketFactory();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\SSLContextSpiWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
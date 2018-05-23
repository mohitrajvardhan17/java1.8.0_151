package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

@Deprecated
public abstract class SSLContextSpi
{
  public SSLContextSpi() {}
  
  protected abstract void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom)
    throws KeyManagementException;
  
  protected abstract SSLSocketFactory engineGetSocketFactory();
  
  protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\SSLContextSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
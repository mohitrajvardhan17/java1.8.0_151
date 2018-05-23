package javax.net.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.SecureRandom;

public abstract class SSLContextSpi
{
  public SSLContextSpi() {}
  
  protected abstract void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom)
    throws KeyManagementException;
  
  protected abstract SSLSocketFactory engineGetSocketFactory();
  
  protected abstract SSLServerSocketFactory engineGetServerSocketFactory();
  
  protected abstract SSLEngine engineCreateSSLEngine();
  
  protected abstract SSLEngine engineCreateSSLEngine(String paramString, int paramInt);
  
  protected abstract SSLSessionContext engineGetServerSessionContext();
  
  protected abstract SSLSessionContext engineGetClientSessionContext();
  
  private SSLSocket getDefaultSocket()
  {
    try
    {
      SSLSocketFactory localSSLSocketFactory = engineGetSocketFactory();
      return (SSLSocket)localSSLSocketFactory.createSocket();
    }
    catch (IOException localIOException)
    {
      throw new UnsupportedOperationException("Could not obtain parameters", localIOException);
    }
  }
  
  protected SSLParameters engineGetDefaultSSLParameters()
  {
    SSLSocket localSSLSocket = getDefaultSocket();
    return localSSLSocket.getSSLParameters();
  }
  
  protected SSLParameters engineGetSupportedSSLParameters()
  {
    SSLSocket localSSLSocket = getDefaultSocket();
    SSLParameters localSSLParameters = new SSLParameters();
    localSSLParameters.setCipherSuites(localSSLSocket.getSupportedCipherSuites());
    localSSLParameters.setProtocols(localSSLSocket.getSupportedProtocols());
    return localSSLParameters;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLContextSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
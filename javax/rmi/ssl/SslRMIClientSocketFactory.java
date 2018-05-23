package javax.rmi.ssl;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.util.StringTokenizer;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SslRMIClientSocketFactory
  implements RMIClientSocketFactory, Serializable
{
  private static SocketFactory defaultSocketFactory = null;
  private static final long serialVersionUID = -8310631444933958385L;
  
  public SslRMIClientSocketFactory() {}
  
  public Socket createSocket(String paramString, int paramInt)
    throws IOException
  {
    SocketFactory localSocketFactory = getDefaultClientSocketFactory();
    SSLSocket localSSLSocket = (SSLSocket)localSocketFactory.createSocket(paramString, paramInt);
    String str = System.getProperty("javax.rmi.ssl.client.enabledCipherSuites");
    if (str != null)
    {
      localObject = new StringTokenizer(str, ",");
      int i = ((StringTokenizer)localObject).countTokens();
      String[] arrayOfString1 = new String[i];
      for (int k = 0; k < i; k++) {
        arrayOfString1[k] = ((StringTokenizer)localObject).nextToken();
      }
      try
      {
        localSSLSocket.setEnabledCipherSuites(arrayOfString1);
      }
      catch (IllegalArgumentException localIllegalArgumentException1)
      {
        throw ((IOException)new IOException(localIllegalArgumentException1.getMessage()).initCause(localIllegalArgumentException1));
      }
    }
    Object localObject = System.getProperty("javax.rmi.ssl.client.enabledProtocols");
    if (localObject != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer((String)localObject, ",");
      int j = localStringTokenizer.countTokens();
      String[] arrayOfString2 = new String[j];
      for (int m = 0; m < j; m++) {
        arrayOfString2[m] = localStringTokenizer.nextToken();
      }
      try
      {
        localSSLSocket.setEnabledProtocols(arrayOfString2);
      }
      catch (IllegalArgumentException localIllegalArgumentException2)
      {
        throw ((IOException)new IOException(localIllegalArgumentException2.getMessage()).initCause(localIllegalArgumentException2));
      }
    }
    return localSSLSocket;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    return getClass().equals(paramObject.getClass());
  }
  
  public int hashCode()
  {
    return getClass().hashCode();
  }
  
  private static synchronized SocketFactory getDefaultClientSocketFactory()
  {
    if (defaultSocketFactory == null) {
      defaultSocketFactory = SSLSocketFactory.getDefault();
    }
    return defaultSocketFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\ssl\SslRMIClientSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
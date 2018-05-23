package javax.rmi.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SslRMIServerSocketFactory
  implements RMIServerSocketFactory
{
  private static SSLSocketFactory defaultSSLSocketFactory = null;
  private final String[] enabledCipherSuites;
  private final String[] enabledProtocols;
  private final boolean needClientAuth;
  private List<String> enabledCipherSuitesList;
  private List<String> enabledProtocolsList;
  private SSLContext context;
  
  public SslRMIServerSocketFactory()
  {
    this(null, null, false);
  }
  
  public SslRMIServerSocketFactory(String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
    throws IllegalArgumentException
  {
    this(null, paramArrayOfString1, paramArrayOfString2, paramBoolean);
  }
  
  public SslRMIServerSocketFactory(SSLContext paramSSLContext, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
    throws IllegalArgumentException
  {
    enabledCipherSuites = (paramArrayOfString1 == null ? null : (String[])paramArrayOfString1.clone());
    enabledProtocols = (paramArrayOfString2 == null ? null : (String[])paramArrayOfString2.clone());
    needClientAuth = paramBoolean;
    context = paramSSLContext;
    SSLSocketFactory localSSLSocketFactory = paramSSLContext == null ? getDefaultSSLSocketFactory() : paramSSLContext.getSocketFactory();
    SSLSocket localSSLSocket = null;
    if ((enabledCipherSuites != null) || (enabledProtocols != null)) {
      try
      {
        localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket();
      }
      catch (Exception localException)
      {
        throw ((IllegalArgumentException)new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported").initCause(localException));
      }
    }
    if (enabledCipherSuites != null)
    {
      localSSLSocket.setEnabledCipherSuites(enabledCipherSuites);
      enabledCipherSuitesList = Arrays.asList(enabledCipherSuites);
    }
    if (enabledProtocols != null)
    {
      localSSLSocket.setEnabledProtocols(enabledProtocols);
      enabledProtocolsList = Arrays.asList(enabledProtocols);
    }
  }
  
  public final String[] getEnabledCipherSuites()
  {
    return enabledCipherSuites == null ? null : (String[])enabledCipherSuites.clone();
  }
  
  public final String[] getEnabledProtocols()
  {
    return enabledProtocols == null ? null : (String[])enabledProtocols.clone();
  }
  
  public final boolean getNeedClientAuth()
  {
    return needClientAuth;
  }
  
  public ServerSocket createServerSocket(int paramInt)
    throws IOException
  {
    final SSLSocketFactory localSSLSocketFactory = context == null ? getDefaultSSLSocketFactory() : context.getSocketFactory();
    new ServerSocket(paramInt)
    {
      public Socket accept()
        throws IOException
      {
        Socket localSocket = super.accept();
        SSLSocket localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(localSocket, localSocket.getInetAddress().getHostName(), localSocket.getPort(), true);
        localSSLSocket.setUseClientMode(false);
        if (enabledCipherSuites != null) {
          localSSLSocket.setEnabledCipherSuites(enabledCipherSuites);
        }
        if (enabledProtocols != null) {
          localSSLSocket.setEnabledProtocols(enabledProtocols);
        }
        localSSLSocket.setNeedClientAuth(needClientAuth);
        return localSSLSocket;
      }
    };
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof SslRMIServerSocketFactory)) {
      return false;
    }
    SslRMIServerSocketFactory localSslRMIServerSocketFactory = (SslRMIServerSocketFactory)paramObject;
    return (getClass().equals(localSslRMIServerSocketFactory.getClass())) && (checkParameters(localSslRMIServerSocketFactory));
  }
  
  private boolean checkParameters(SslRMIServerSocketFactory paramSslRMIServerSocketFactory)
  {
    if (context == null ? context != null : !context.equals(context)) {
      return false;
    }
    if (needClientAuth != needClientAuth) {
      return false;
    }
    if (((enabledCipherSuites == null) && (enabledCipherSuites != null)) || ((enabledCipherSuites != null) && (enabledCipherSuites == null))) {
      return false;
    }
    List localList;
    if ((enabledCipherSuites != null) && (enabledCipherSuites != null))
    {
      localList = Arrays.asList(enabledCipherSuites);
      if (!enabledCipherSuitesList.equals(localList)) {
        return false;
      }
    }
    if (((enabledProtocols == null) && (enabledProtocols != null)) || ((enabledProtocols != null) && (enabledProtocols == null))) {
      return false;
    }
    if ((enabledProtocols != null) && (enabledProtocols != null))
    {
      localList = Arrays.asList(enabledProtocols);
      if (!enabledProtocolsList.equals(localList)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    return getClass().hashCode() + (context == null ? 0 : context.hashCode()) + (needClientAuth ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode()) + (enabledCipherSuites == null ? 0 : enabledCipherSuitesList.hashCode()) + (enabledProtocols == null ? 0 : enabledProtocolsList.hashCode());
  }
  
  private static synchronized SSLSocketFactory getDefaultSSLSocketFactory()
  {
    if (defaultSSLSocketFactory == null) {
      defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
    }
    return defaultSSLSocketFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\ssl\SslRMIServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
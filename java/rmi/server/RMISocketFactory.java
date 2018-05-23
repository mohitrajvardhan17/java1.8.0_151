package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import sun.rmi.transport.proxy.RMIMasterSocketFactory;

public abstract class RMISocketFactory
  implements RMIClientSocketFactory, RMIServerSocketFactory
{
  private static RMISocketFactory factory = null;
  private static RMISocketFactory defaultSocketFactory;
  private static RMIFailureHandler handler = null;
  
  public RMISocketFactory() {}
  
  public abstract Socket createSocket(String paramString, int paramInt)
    throws IOException;
  
  public abstract ServerSocket createServerSocket(int paramInt)
    throws IOException;
  
  public static synchronized void setSocketFactory(RMISocketFactory paramRMISocketFactory)
    throws IOException
  {
    if (factory != null) {
      throw new SocketException("factory already defined");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    factory = paramRMISocketFactory;
  }
  
  public static synchronized RMISocketFactory getSocketFactory()
  {
    return factory;
  }
  
  public static synchronized RMISocketFactory getDefaultSocketFactory()
  {
    if (defaultSocketFactory == null) {
      defaultSocketFactory = new RMIMasterSocketFactory();
    }
    return defaultSocketFactory;
  }
  
  public static synchronized void setFailureHandler(RMIFailureHandler paramRMIFailureHandler)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    handler = paramRMIFailureHandler;
  }
  
  public static synchronized RMIFailureHandler getFailureHandler()
  {
    return handler;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RMISocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

public abstract class ServerSocketFactory
{
  private static ServerSocketFactory theFactory;
  
  protected ServerSocketFactory() {}
  
  public static ServerSocketFactory getDefault()
  {
    synchronized (ServerSocketFactory.class)
    {
      if (theFactory == null) {
        theFactory = new DefaultServerSocketFactory();
      }
    }
    return theFactory;
  }
  
  public ServerSocket createServerSocket()
    throws IOException
  {
    throw new SocketException("Unbound server sockets not implemented");
  }
  
  public abstract ServerSocket createServerSocket(int paramInt)
    throws IOException;
  
  public abstract ServerSocket createServerSocket(int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract ServerSocket createServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
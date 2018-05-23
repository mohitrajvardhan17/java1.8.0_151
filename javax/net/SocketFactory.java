package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class SocketFactory
{
  private static SocketFactory theFactory;
  
  protected SocketFactory() {}
  
  public static SocketFactory getDefault()
  {
    synchronized (SocketFactory.class)
    {
      if (theFactory == null) {
        theFactory = new DefaultSocketFactory();
      }
    }
    return theFactory;
  }
  
  public Socket createSocket()
    throws IOException
  {
    UnsupportedOperationException localUnsupportedOperationException = new UnsupportedOperationException();
    SocketException localSocketException = new SocketException("Unconnected sockets not implemented");
    localSocketException.initCause(localUnsupportedOperationException);
    throw localSocketException;
  }
  
  public abstract Socket createSocket(String paramString, int paramInt)
    throws IOException, UnknownHostException;
  
  public abstract Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException, UnknownHostException;
  
  public abstract Socket createSocket(InetAddress paramInetAddress, int paramInt)
    throws IOException;
  
  public abstract Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\SocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
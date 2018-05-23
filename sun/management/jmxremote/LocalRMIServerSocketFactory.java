package sun.management.jmxremote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Enumeration;

public final class LocalRMIServerSocketFactory
  implements RMIServerSocketFactory
{
  public LocalRMIServerSocketFactory() {}
  
  public ServerSocket createServerSocket(int paramInt)
    throws IOException
  {
    new ServerSocket(paramInt)
    {
      public Socket accept()
        throws IOException
      {
        Socket localSocket = super.accept();
        InetAddress localInetAddress1 = localSocket.getInetAddress();
        Object localObject;
        if (localInetAddress1 == null)
        {
          localObject = "";
          if (localSocket.isClosed()) {
            localObject = " Socket is closed.";
          } else if (!localSocket.isConnected()) {
            localObject = " Socket is not connected";
          }
          try
          {
            localSocket.close();
          }
          catch (Exception localException) {}
          throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported. Couldn't determine client address." + (String)localObject);
        }
        if (localInetAddress1.isLoopbackAddress()) {
          return localSocket;
        }
        try
        {
          localObject = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException localSocketException)
        {
          try
          {
            localSocket.close();
          }
          catch (IOException localIOException2) {}
          throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.", localSocketException);
        }
        while (((Enumeration)localObject).hasMoreElements())
        {
          NetworkInterface localNetworkInterface = (NetworkInterface)((Enumeration)localObject).nextElement();
          Enumeration localEnumeration = localNetworkInterface.getInetAddresses();
          while (localEnumeration.hasMoreElements())
          {
            InetAddress localInetAddress2 = (InetAddress)localEnumeration.nextElement();
            if (localInetAddress2.equals(localInetAddress1)) {
              return localSocket;
            }
          }
        }
        try
        {
          localSocket.close();
        }
        catch (IOException localIOException1) {}
        throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.");
      }
    };
  }
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof LocalRMIServerSocketFactory;
  }
  
  public int hashCode()
  {
    return getClass().hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\jmxremote\LocalRMIServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
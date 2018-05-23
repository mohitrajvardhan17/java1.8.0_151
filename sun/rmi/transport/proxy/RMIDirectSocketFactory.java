package sun.rmi.transport.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class RMIDirectSocketFactory
  extends RMISocketFactory
{
  public RMIDirectSocketFactory() {}
  
  public Socket createSocket(String paramString, int paramInt)
    throws IOException
  {
    return new Socket(paramString, paramInt);
  }
  
  public ServerSocket createServerSocket(int paramInt)
    throws IOException
  {
    return new ServerSocket(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\RMIDirectSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
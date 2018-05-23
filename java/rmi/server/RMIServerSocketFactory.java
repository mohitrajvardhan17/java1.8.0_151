package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;

public abstract interface RMIServerSocketFactory
{
  public abstract ServerSocket createServerSocket(int paramInt)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RMIServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.rmi.server;

import java.io.IOException;
import java.net.Socket;

public abstract interface RMIClientSocketFactory
{
  public abstract Socket createSocket(String paramString, int paramInt)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RMIClientSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
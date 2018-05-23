package sun.rmi.transport;

import java.rmi.RemoteException;

public abstract interface Channel
{
  public abstract Connection newConnection()
    throws RemoteException;
  
  public abstract Endpoint getEndpoint();
  
  public abstract void free(Connection paramConnection, boolean paramBoolean)
    throws RemoteException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface RMIServer
  extends Remote
{
  public abstract String getVersion()
    throws RemoteException;
  
  public abstract RMIConnection newClient(Object paramObject)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\remote\rmi\RMIServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

@Deprecated
public abstract interface ServerRef
  extends RemoteRef
{
  public static final long serialVersionUID = -4557750989390278438L;
  
  public abstract RemoteStub exportObject(Remote paramRemote, Object paramObject)
    throws RemoteException;
  
  public abstract String getClientHost()
    throws ServerNotActiveException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\ServerRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
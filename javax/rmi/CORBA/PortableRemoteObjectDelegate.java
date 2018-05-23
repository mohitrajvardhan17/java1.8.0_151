package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface PortableRemoteObjectDelegate
{
  public abstract void exportObject(Remote paramRemote)
    throws RemoteException;
  
  public abstract Remote toStub(Remote paramRemote)
    throws NoSuchObjectException;
  
  public abstract void unexportObject(Remote paramRemote)
    throws NoSuchObjectException;
  
  public abstract Object narrow(Object paramObject, Class paramClass)
    throws ClassCastException;
  
  public abstract void connect(Remote paramRemote1, Remote paramRemote2)
    throws RemoteException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\CORBA\PortableRemoteObjectDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
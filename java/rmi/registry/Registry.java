package java.rmi.registry;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface Registry
  extends Remote
{
  public static final int REGISTRY_PORT = 1099;
  
  public abstract Remote lookup(String paramString)
    throws RemoteException, NotBoundException, AccessException;
  
  public abstract void bind(String paramString, Remote paramRemote)
    throws RemoteException, AlreadyBoundException, AccessException;
  
  public abstract void unbind(String paramString)
    throws RemoteException, NotBoundException, AccessException;
  
  public abstract void rebind(String paramString, Remote paramRemote)
    throws RemoteException, AccessException;
  
  public abstract String[] list()
    throws RemoteException, AccessException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\registry\Registry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
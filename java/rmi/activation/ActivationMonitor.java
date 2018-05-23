package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface ActivationMonitor
  extends Remote
{
  public abstract void inactiveObject(ActivationID paramActivationID)
    throws UnknownObjectException, RemoteException;
  
  public abstract void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject)
    throws UnknownObjectException, RemoteException;
  
  public abstract void inactiveGroup(ActivationGroupID paramActivationGroupID, long paramLong)
    throws UnknownGroupException, RemoteException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
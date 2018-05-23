package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface Activator
  extends Remote
{
  public abstract MarshalledObject<? extends Remote> activate(ActivationID paramActivationID, boolean paramBoolean)
    throws ActivationException, UnknownObjectException, RemoteException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\Activator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
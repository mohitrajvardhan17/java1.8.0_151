package java.rmi.dgc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;

public abstract interface DGC
  extends Remote
{
  public abstract Lease dirty(ObjID[] paramArrayOfObjID, long paramLong, Lease paramLease)
    throws RemoteException;
  
  public abstract void clean(ObjID[] paramArrayOfObjID, long paramLong, VMID paramVMID, boolean paramBoolean)
    throws RemoteException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\dgc\DGC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
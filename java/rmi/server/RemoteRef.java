package java.rmi.server;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;

public abstract interface RemoteRef
  extends Externalizable
{
  public static final long serialVersionUID = 3632638527362204081L;
  public static final String packagePrefix = "sun.rmi.server";
  
  public abstract Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong)
    throws Exception;
  
  @Deprecated
  public abstract RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong)
    throws RemoteException;
  
  @Deprecated
  public abstract void invoke(RemoteCall paramRemoteCall)
    throws Exception;
  
  @Deprecated
  public abstract void done(RemoteCall paramRemoteCall)
    throws RemoteException;
  
  public abstract String getRefClass(ObjectOutput paramObjectOutput);
  
  public abstract int remoteHashCode();
  
  public abstract boolean remoteEquals(RemoteRef paramRemoteRef);
  
  public abstract String remoteToString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RemoteRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
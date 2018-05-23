package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;

public class ProxyRef
  implements RemoteRef
{
  private static final long serialVersionUID = -6503061366316814723L;
  protected RemoteRef ref;
  
  public ProxyRef(RemoteRef paramRemoteRef)
  {
    ref = paramRemoteRef;
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    ref.readExternal(paramObjectInput);
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    ref.writeExternal(paramObjectOutput);
  }
  
  @Deprecated
  public void invoke(RemoteCall paramRemoteCall)
    throws Exception
  {
    ref.invoke(paramRemoteCall);
  }
  
  public Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong)
    throws Exception
  {
    return ref.invoke(paramRemote, paramMethod, paramArrayOfObject, paramLong);
  }
  
  @Deprecated
  public void done(RemoteCall paramRemoteCall)
    throws RemoteException
  {
    ref.done(paramRemoteCall);
  }
  
  public String getRefClass(ObjectOutput paramObjectOutput)
  {
    return ref.getRefClass(paramObjectOutput);
  }
  
  @Deprecated
  public RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong)
    throws RemoteException
  {
    return ref.newCall(paramRemoteObject, paramArrayOfOperation, paramInt, paramLong);
  }
  
  public boolean remoteEquals(RemoteRef paramRemoteRef)
  {
    return ref.remoteEquals(paramRemoteRef);
  }
  
  public int remoteHashCode()
  {
    return ref.remoteHashCode();
  }
  
  public String remoteToString()
  {
    return ref.remoteToString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ProxyRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
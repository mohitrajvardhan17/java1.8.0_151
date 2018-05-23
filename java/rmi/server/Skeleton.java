package java.rmi.server;

import java.rmi.Remote;

@Deprecated
public abstract interface Skeleton
{
  @Deprecated
  public abstract void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong)
    throws Exception;
  
  @Deprecated
  public abstract Operation[] getOperations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\Skeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
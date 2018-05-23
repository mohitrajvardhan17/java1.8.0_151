package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.transport.ObjectTable;

public class UnicastRemoteObject
  extends RemoteServer
{
  private int port = 0;
  private RMIClientSocketFactory csf = null;
  private RMIServerSocketFactory ssf = null;
  private static final long serialVersionUID = 4974527148936298033L;
  
  protected UnicastRemoteObject()
    throws RemoteException
  {
    this(0);
  }
  
  protected UnicastRemoteObject(int paramInt)
    throws RemoteException
  {
    port = paramInt;
    exportObject(this, paramInt);
  }
  
  protected UnicastRemoteObject(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws RemoteException
  {
    port = paramInt;
    csf = paramRMIClientSocketFactory;
    ssf = paramRMIServerSocketFactory;
    exportObject(this, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    reexport();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      UnicastRemoteObject localUnicastRemoteObject = (UnicastRemoteObject)super.clone();
      localUnicastRemoteObject.reexport();
      return localUnicastRemoteObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw new ServerCloneException("Clone failed", localRemoteException);
    }
  }
  
  private void reexport()
    throws RemoteException
  {
    if ((csf == null) && (ssf == null)) {
      exportObject(this, port);
    } else {
      exportObject(this, port, csf, ssf);
    }
  }
  
  @Deprecated
  public static RemoteStub exportObject(Remote paramRemote)
    throws RemoteException
  {
    return (RemoteStub)exportObject(paramRemote, new UnicastServerRef(true));
  }
  
  public static Remote exportObject(Remote paramRemote, int paramInt)
    throws RemoteException
  {
    return exportObject(paramRemote, new UnicastServerRef(paramInt));
  }
  
  public static Remote exportObject(Remote paramRemote, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws RemoteException
  {
    return exportObject(paramRemote, new UnicastServerRef2(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory));
  }
  
  public static boolean unexportObject(Remote paramRemote, boolean paramBoolean)
    throws NoSuchObjectException
  {
    return ObjectTable.unexportObject(paramRemote, paramBoolean);
  }
  
  private static Remote exportObject(Remote paramRemote, UnicastServerRef paramUnicastServerRef)
    throws RemoteException
  {
    if ((paramRemote instanceof UnicastRemoteObject)) {
      ref = paramUnicastServerRef;
    }
    return paramUnicastServerRef.exportObject(paramRemote, null, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\UnicastRemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
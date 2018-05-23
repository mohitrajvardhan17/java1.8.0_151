package java.rmi.registry;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import sun.rmi.registry.RegistryImpl;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastRef2;
import sun.rmi.server.Util;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

public final class LocateRegistry
{
  private LocateRegistry() {}
  
  public static Registry getRegistry()
    throws RemoteException
  {
    return getRegistry(null, 1099);
  }
  
  public static Registry getRegistry(int paramInt)
    throws RemoteException
  {
    return getRegistry(null, paramInt);
  }
  
  public static Registry getRegistry(String paramString)
    throws RemoteException
  {
    return getRegistry(paramString, 1099);
  }
  
  public static Registry getRegistry(String paramString, int paramInt)
    throws RemoteException
  {
    return getRegistry(paramString, paramInt, null);
  }
  
  public static Registry getRegistry(String paramString, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory)
    throws RemoteException
  {
    Object localObject = null;
    if (paramInt <= 0) {
      paramInt = 1099;
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      try
      {
        paramString = InetAddress.getLocalHost().getHostAddress();
      }
      catch (Exception localException)
      {
        paramString = "";
      }
    }
    LiveRef localLiveRef = new LiveRef(new ObjID(0), new TCPEndpoint(paramString, paramInt, paramRMIClientSocketFactory, null), false);
    UnicastRef2 localUnicastRef2 = paramRMIClientSocketFactory == null ? new UnicastRef(localLiveRef) : new UnicastRef2(localLiveRef);
    return (Registry)Util.createProxy(RegistryImpl.class, localUnicastRef2, false);
  }
  
  public static Registry createRegistry(int paramInt)
    throws RemoteException
  {
    return new RegistryImpl(paramInt);
  }
  
  public static Registry createRegistry(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws RemoteException
  {
    return new RegistryImpl(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\registry\LocateRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
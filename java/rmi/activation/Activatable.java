package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteServer;
import sun.rmi.server.ActivatableRef;
import sun.rmi.server.ActivatableServerRef;
import sun.rmi.transport.ObjectTable;

public abstract class Activatable
  extends RemoteServer
{
  private ActivationID id;
  private static final long serialVersionUID = -3120617863591563455L;
  
  protected Activatable(String paramString, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean, int paramInt)
    throws ActivationException, RemoteException
  {
    id = exportObject(this, paramString, paramMarshalledObject, paramBoolean, paramInt);
  }
  
  protected Activatable(String paramString, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws ActivationException, RemoteException
  {
    id = exportObject(this, paramString, paramMarshalledObject, paramBoolean, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
  }
  
  protected Activatable(ActivationID paramActivationID, int paramInt)
    throws RemoteException
  {
    id = paramActivationID;
    exportObject(this, paramActivationID, paramInt);
  }
  
  protected Activatable(ActivationID paramActivationID, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws RemoteException
  {
    id = paramActivationID;
    exportObject(this, paramActivationID, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
  }
  
  protected ActivationID getID()
  {
    return id;
  }
  
  public static Remote register(ActivationDesc paramActivationDesc)
    throws UnknownGroupException, ActivationException, RemoteException
  {
    ActivationID localActivationID = ActivationGroup.getSystem().registerObject(paramActivationDesc);
    return ActivatableRef.getStub(paramActivationDesc, localActivationID);
  }
  
  public static boolean inactive(ActivationID paramActivationID)
    throws UnknownObjectException, ActivationException, RemoteException
  {
    return ActivationGroup.currentGroup().inactiveObject(paramActivationID);
  }
  
  public static void unregister(ActivationID paramActivationID)
    throws UnknownObjectException, ActivationException, RemoteException
  {
    ActivationGroup.getSystem().unregisterObject(paramActivationID);
  }
  
  public static ActivationID exportObject(Remote paramRemote, String paramString, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean, int paramInt)
    throws ActivationException, RemoteException
  {
    return exportObject(paramRemote, paramString, paramMarshalledObject, paramBoolean, paramInt, null, null);
  }
  
  public static ActivationID exportObject(Remote paramRemote, String paramString, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws ActivationException, RemoteException
  {
    ActivationDesc localActivationDesc = new ActivationDesc(paramRemote.getClass().getName(), paramString, paramMarshalledObject, paramBoolean);
    ActivationSystem localActivationSystem = ActivationGroup.getSystem();
    ActivationID localActivationID = localActivationSystem.registerObject(localActivationDesc);
    try
    {
      exportObject(paramRemote, localActivationID, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
    }
    catch (RemoteException localRemoteException)
    {
      try
      {
        localActivationSystem.unregisterObject(localActivationID);
      }
      catch (Exception localException) {}
      throw localRemoteException;
    }
    ActivationGroup.currentGroup().activeObject(localActivationID, paramRemote);
    return localActivationID;
  }
  
  public static Remote exportObject(Remote paramRemote, ActivationID paramActivationID, int paramInt)
    throws RemoteException
  {
    return exportObject(paramRemote, new ActivatableServerRef(paramActivationID, paramInt));
  }
  
  public static Remote exportObject(Remote paramRemote, ActivationID paramActivationID, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
    throws RemoteException
  {
    return exportObject(paramRemote, new ActivatableServerRef(paramActivationID, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory));
  }
  
  public static boolean unexportObject(Remote paramRemote, boolean paramBoolean)
    throws NoSuchObjectException
  {
    return ObjectTable.unexportObject(paramRemote, paramBoolean);
  }
  
  private static Remote exportObject(Remote paramRemote, ActivatableServerRef paramActivatableServerRef)
    throws RemoteException
  {
    if ((paramRemote instanceof Activatable)) {
      ref = paramActivatableServerRef;
    }
    return paramActivatableServerRef.exportObject(paramRemote, null, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\Activatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
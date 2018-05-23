package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Arrays;
import sun.rmi.transport.tcp.TCPEndpoint;

public class LiveRef
  implements Cloneable
{
  private final Endpoint ep;
  private final ObjID id;
  private transient Channel ch;
  private final boolean isLocal;
  
  public LiveRef(ObjID paramObjID, Endpoint paramEndpoint, boolean paramBoolean)
  {
    ep = paramEndpoint;
    id = paramObjID;
    isLocal = paramBoolean;
  }
  
  public LiveRef(int paramInt)
  {
    this(new ObjID(), paramInt);
  }
  
  public LiveRef(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
  {
    this(new ObjID(), paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
  }
  
  public LiveRef(ObjID paramObjID, int paramInt)
  {
    this(paramObjID, TCPEndpoint.getLocalEndpoint(paramInt), true);
  }
  
  public LiveRef(ObjID paramObjID, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
  {
    this(paramObjID, TCPEndpoint.getLocalEndpoint(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory), true);
  }
  
  public Object clone()
  {
    try
    {
      LiveRef localLiveRef = (LiveRef)super.clone();
      return localLiveRef;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public int getPort()
  {
    return ((TCPEndpoint)ep).getPort();
  }
  
  public RMIClientSocketFactory getClientSocketFactory()
  {
    return ((TCPEndpoint)ep).getClientSocketFactory();
  }
  
  public RMIServerSocketFactory getServerSocketFactory()
  {
    return ((TCPEndpoint)ep).getServerSocketFactory();
  }
  
  public void exportObject(Target paramTarget)
    throws RemoteException
  {
    ep.exportObject(paramTarget);
  }
  
  public Channel getChannel()
    throws RemoteException
  {
    if (ch == null) {
      ch = ep.getChannel();
    }
    return ch;
  }
  
  public ObjID getObjID()
  {
    return id;
  }
  
  Endpoint getEndpoint()
  {
    return ep;
  }
  
  public String toString()
  {
    String str;
    if (isLocal) {
      str = "local";
    } else {
      str = "remote";
    }
    return "[endpoint:" + ep + "(" + str + "),objID:" + id + "]";
  }
  
  public int hashCode()
  {
    return id.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof LiveRef)))
    {
      LiveRef localLiveRef = (LiveRef)paramObject;
      return (ep.equals(ep)) && (id.equals(id)) && (isLocal == isLocal);
    }
    return false;
  }
  
  public boolean remoteEquals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof LiveRef)))
    {
      LiveRef localLiveRef = (LiveRef)paramObject;
      TCPEndpoint localTCPEndpoint1 = (TCPEndpoint)ep;
      TCPEndpoint localTCPEndpoint2 = (TCPEndpoint)ep;
      RMIClientSocketFactory localRMIClientSocketFactory1 = localTCPEndpoint1.getClientSocketFactory();
      RMIClientSocketFactory localRMIClientSocketFactory2 = localTCPEndpoint2.getClientSocketFactory();
      if ((localTCPEndpoint1.getPort() != localTCPEndpoint2.getPort()) || (!localTCPEndpoint1.getHost().equals(localTCPEndpoint2.getHost()))) {
        return false;
      }
      if (((localRMIClientSocketFactory1 == null ? 1 : 0) ^ (localRMIClientSocketFactory2 == null ? 1 : 0)) != 0) {
        return false;
      }
      if ((localRMIClientSocketFactory1 != null) && ((localRMIClientSocketFactory1.getClass() != localRMIClientSocketFactory2.getClass()) || (!localRMIClientSocketFactory1.equals(localRMIClientSocketFactory2)))) {
        return false;
      }
      return id.equals(id);
    }
    return false;
  }
  
  public void write(ObjectOutput paramObjectOutput, boolean paramBoolean)
    throws IOException
  {
    boolean bool = false;
    if ((paramObjectOutput instanceof ConnectionOutputStream))
    {
      ConnectionOutputStream localConnectionOutputStream = (ConnectionOutputStream)paramObjectOutput;
      bool = localConnectionOutputStream.isResultStream();
      if (isLocal)
      {
        ObjectEndpoint localObjectEndpoint = new ObjectEndpoint(id, ep.getInboundTransport());
        Target localTarget = ObjectTable.getTarget(localObjectEndpoint);
        if (localTarget != null)
        {
          Remote localRemote = localTarget.getImpl();
          if (localRemote != null) {
            localConnectionOutputStream.saveObject(localRemote);
          }
        }
      }
      else
      {
        localConnectionOutputStream.saveObject(this);
      }
    }
    if (paramBoolean) {
      ((TCPEndpoint)ep).write(paramObjectOutput);
    } else {
      ((TCPEndpoint)ep).writeHostPortFormat(paramObjectOutput);
    }
    id.write(paramObjectOutput);
    paramObjectOutput.writeBoolean(bool);
  }
  
  public static LiveRef read(ObjectInput paramObjectInput, boolean paramBoolean)
    throws IOException, ClassNotFoundException
  {
    TCPEndpoint localTCPEndpoint;
    if (paramBoolean) {
      localTCPEndpoint = TCPEndpoint.read(paramObjectInput);
    } else {
      localTCPEndpoint = TCPEndpoint.readHostPortFormat(paramObjectInput);
    }
    ObjID localObjID = ObjID.read(paramObjectInput);
    boolean bool = paramObjectInput.readBoolean();
    LiveRef localLiveRef = new LiveRef(localObjID, localTCPEndpoint, false);
    if ((paramObjectInput instanceof ConnectionInputStream))
    {
      ConnectionInputStream localConnectionInputStream = (ConnectionInputStream)paramObjectInput;
      localConnectionInputStream.saveRef(localLiveRef);
      if (bool) {
        localConnectionInputStream.setAckNeeded();
      }
    }
    else
    {
      DGCClient.registerRefs(localTCPEndpoint, Arrays.asList(new LiveRef[] { localLiveRef }));
    }
    return localLiveRef;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\LiveRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
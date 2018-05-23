package sun.rmi.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sun.rmi.runtime.Log;
import sun.rmi.server.MarshalInputStream;

class ConnectionInputStream
  extends MarshalInputStream
{
  private boolean dgcAckNeeded = false;
  private Map<Endpoint, List<LiveRef>> incomingRefTable = new HashMap(5);
  private UID ackID;
  
  ConnectionInputStream(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
  }
  
  void readID()
    throws IOException
  {
    ackID = UID.read(this);
  }
  
  void saveRef(LiveRef paramLiveRef)
  {
    Endpoint localEndpoint = paramLiveRef.getEndpoint();
    Object localObject = (List)incomingRefTable.get(localEndpoint);
    if (localObject == null)
    {
      localObject = new ArrayList();
      incomingRefTable.put(localEndpoint, localObject);
    }
    ((List)localObject).add(paramLiveRef);
  }
  
  void discardRefs()
  {
    incomingRefTable.clear();
  }
  
  void registerRefs()
    throws IOException
  {
    if (!incomingRefTable.isEmpty())
    {
      Iterator localIterator = incomingRefTable.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        DGCClient.registerRefs((Endpoint)localEntry.getKey(), (List)localEntry.getValue());
      }
    }
  }
  
  void setAckNeeded()
  {
    dgcAckNeeded = true;
  }
  
  void done(Connection paramConnection)
  {
    if (dgcAckNeeded)
    {
      Connection localConnection = null;
      Channel localChannel = null;
      boolean bool = true;
      DGCImpl.dgcLog.log(Log.VERBOSE, "send ack");
      try
      {
        localChannel = paramConnection.getChannel();
        localConnection = localChannel.newConnection();
        DataOutputStream localDataOutputStream = new DataOutputStream(localConnection.getOutputStream());
        localDataOutputStream.writeByte(84);
        if (ackID == null) {
          ackID = new UID();
        }
        ackID.write(localDataOutputStream);
        localConnection.releaseOutputStream();
        localConnection.getInputStream().available();
        localConnection.releaseInputStream();
      }
      catch (RemoteException localRemoteException1)
      {
        bool = false;
      }
      catch (IOException localIOException)
      {
        bool = false;
      }
      try
      {
        if (localConnection != null) {
          localChannel.free(localConnection, bool);
        }
      }
      catch (RemoteException localRemoteException2) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\ConnectionInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
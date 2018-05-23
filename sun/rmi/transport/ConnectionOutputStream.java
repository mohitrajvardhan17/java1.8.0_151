package sun.rmi.transport;

import java.io.IOException;
import java.rmi.server.UID;
import sun.rmi.server.MarshalOutputStream;

class ConnectionOutputStream
  extends MarshalOutputStream
{
  private final Connection conn;
  private final boolean resultStream;
  private final UID ackID;
  private DGCAckHandler dgcAckHandler = null;
  
  ConnectionOutputStream(Connection paramConnection, boolean paramBoolean)
    throws IOException
  {
    super(paramConnection.getOutputStream());
    conn = paramConnection;
    resultStream = paramBoolean;
    ackID = (paramBoolean ? new UID() : null);
  }
  
  void writeID()
    throws IOException
  {
    assert (resultStream);
    ackID.write(this);
  }
  
  boolean isResultStream()
  {
    return resultStream;
  }
  
  void saveObject(Object paramObject)
  {
    if (dgcAckHandler == null) {
      dgcAckHandler = new DGCAckHandler(ackID);
    }
    dgcAckHandler.add(paramObject);
  }
  
  DGCAckHandler getDGCAckHandler()
  {
    return dgcAckHandler;
  }
  
  void done()
  {
    if (dgcAckHandler != null) {
      dgcAckHandler.startTimer();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\ConnectionOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
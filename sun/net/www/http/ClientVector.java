package sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

class ClientVector
  extends Stack<KeepAliveEntry>
{
  private static final long serialVersionUID = -8680532108106489459L;
  int nap;
  
  ClientVector(int paramInt)
  {
    nap = paramInt;
  }
  
  synchronized HttpClient get()
  {
    if (empty()) {
      return null;
    }
    HttpClient localHttpClient = null;
    long l = System.currentTimeMillis();
    do
    {
      KeepAliveEntry localKeepAliveEntry = (KeepAliveEntry)pop();
      if (l - idleStartTime > nap) {
        hc.closeServer();
      } else {
        localHttpClient = hc;
      }
    } while ((localHttpClient == null) && (!empty()));
    return localHttpClient;
  }
  
  synchronized void put(HttpClient paramHttpClient)
  {
    if (size() >= KeepAliveCache.getMaxConnections()) {
      paramHttpClient.closeServer();
    } else {
      push(new KeepAliveEntry(paramHttpClient, System.currentTimeMillis()));
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    throw new NotSerializableException();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    throw new NotSerializableException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\ClientVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
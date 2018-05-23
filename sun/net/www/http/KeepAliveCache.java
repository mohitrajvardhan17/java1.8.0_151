package sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.security.action.GetIntegerAction;

public class KeepAliveCache
  extends HashMap<KeepAliveKey, ClientVector>
  implements Runnable
{
  private static final long serialVersionUID = -2937172892064557949L;
  static final int MAX_CONNECTIONS = 5;
  static int result = -1;
  static final int LIFETIME = 5000;
  private Thread keepAliveTimer = null;
  
  static int getMaxConnections()
  {
    if (result == -1)
    {
      result = ((Integer)AccessController.doPrivileged(new GetIntegerAction("http.maxConnections", 5))).intValue();
      if (result <= 0) {
        result = 5;
      }
    }
    return result;
  }
  
  public KeepAliveCache() {}
  
  public synchronized void put(URL paramURL, Object paramObject, HttpClient paramHttpClient)
  {
    int i = keepAliveTimer == null ? 1 : 0;
    if ((i == 0) && (!keepAliveTimer.isAlive())) {
      i = 1;
    }
    if (i != 0)
    {
      clear();
      localObject = this;
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          Object localObject = Thread.currentThread().getThreadGroup();
          ThreadGroup localThreadGroup = null;
          while ((localThreadGroup = ((ThreadGroup)localObject).getParent()) != null) {
            localObject = localThreadGroup;
          }
          keepAliveTimer = new Thread((ThreadGroup)localObject, localObject, "Keep-Alive-Timer");
          keepAliveTimer.setDaemon(true);
          keepAliveTimer.setPriority(8);
          keepAliveTimer.setContextClassLoader(null);
          keepAliveTimer.start();
          return null;
        }
      });
    }
    final Object localObject = new KeepAliveKey(paramURL, paramObject);
    ClientVector localClientVector = (ClientVector)super.get(localObject);
    if (localClientVector == null)
    {
      int j = paramHttpClient.getKeepAliveTimeout();
      localClientVector = new ClientVector(j > 0 ? j * 1000 : 5000);
      localClientVector.put(paramHttpClient);
      super.put(localObject, localClientVector);
    }
    else
    {
      localClientVector.put(paramHttpClient);
    }
  }
  
  public synchronized void remove(HttpClient paramHttpClient, Object paramObject)
  {
    KeepAliveKey localKeepAliveKey = new KeepAliveKey(url, paramObject);
    ClientVector localClientVector = (ClientVector)super.get(localKeepAliveKey);
    if (localClientVector != null)
    {
      localClientVector.remove(paramHttpClient);
      if (localClientVector.empty()) {
        removeVector(localKeepAliveKey);
      }
    }
  }
  
  synchronized void removeVector(KeepAliveKey paramKeepAliveKey)
  {
    super.remove(paramKeepAliveKey);
  }
  
  public synchronized HttpClient get(URL paramURL, Object paramObject)
  {
    KeepAliveKey localKeepAliveKey = new KeepAliveKey(paramURL, paramObject);
    ClientVector localClientVector = (ClientVector)super.get(localKeepAliveKey);
    if (localClientVector == null) {
      return null;
    }
    return localClientVector.get();
  }
  
  public void run()
  {
    do
    {
      try
      {
        Thread.sleep(5000L);
      }
      catch (InterruptedException localInterruptedException) {}
      synchronized (this)
      {
        long l = System.currentTimeMillis();
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = keySet().iterator();
        KeepAliveKey localKeepAliveKey;
        while (localIterator.hasNext())
        {
          localKeepAliveKey = (KeepAliveKey)localIterator.next();
          ClientVector localClientVector = (ClientVector)get(localKeepAliveKey);
          synchronized (localClientVector)
          {
            for (int i = 0; i < localClientVector.size(); i++)
            {
              KeepAliveEntry localKeepAliveEntry = (KeepAliveEntry)localClientVector.elementAt(i);
              if (l - idleStartTime <= nap) {
                break;
              }
              HttpClient localHttpClient = hc;
              localHttpClient.closeServer();
            }
            localClientVector.subList(0, i).clear();
            if (localClientVector.size() == 0) {
              localArrayList.add(localKeepAliveKey);
            }
          }
        }
        localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          localKeepAliveKey = (KeepAliveKey)localIterator.next();
          removeVector(localKeepAliveKey);
        }
      }
    } while (size() > 0);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\KeepAliveCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
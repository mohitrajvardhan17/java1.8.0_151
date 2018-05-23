package sun.awt.windows;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

abstract class WObjectPeer
{
  volatile long pData;
  private volatile boolean destroyed;
  volatile Object target;
  private volatile boolean disposed;
  volatile Error createError = null;
  private final Object stateLock = new Object();
  private volatile Map<WObjectPeer, WObjectPeer> childPeers;
  
  WObjectPeer() {}
  
  public static WObjectPeer getPeerForTarget(Object paramObject)
  {
    WObjectPeer localWObjectPeer = (WObjectPeer)WToolkit.targetToPeer(paramObject);
    return localWObjectPeer;
  }
  
  public long getData()
  {
    return pData;
  }
  
  public Object getTarget()
  {
    return target;
  }
  
  public final Object getStateLock()
  {
    return stateLock;
  }
  
  protected abstract void disposeImpl();
  
  public final void dispose()
  {
    int i = 0;
    synchronized (this)
    {
      if (!disposed) {
        disposed = (i = 1);
      }
    }
    if (i != 0)
    {
      if (childPeers != null) {
        disposeChildPeers();
      }
      disposeImpl();
    }
  }
  
  protected final boolean isDisposed()
  {
    return disposed;
  }
  
  private static native void initIDs();
  
  final void addChildPeer(WObjectPeer paramWObjectPeer)
  {
    synchronized (getStateLock())
    {
      if (childPeers == null) {
        childPeers = new WeakHashMap();
      }
      if (isDisposed()) {
        throw new IllegalStateException("Parent peer is disposed");
      }
      childPeers.put(paramWObjectPeer, this);
    }
  }
  
  private void disposeChildPeers()
  {
    synchronized (getStateLock())
    {
      Iterator localIterator = childPeers.keySet().iterator();
      while (localIterator.hasNext())
      {
        WObjectPeer localWObjectPeer = (WObjectPeer)localIterator.next();
        if (localWObjectPeer != null) {
          try
          {
            localWObjectPeer.dispose();
          }
          catch (Exception localException) {}
        }
      }
    }
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WObjectPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
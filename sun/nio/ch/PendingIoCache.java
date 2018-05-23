package sun.nio.ch;

import java.nio.channels.AsynchronousCloseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import sun.misc.Unsafe;

class PendingIoCache
{
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  private static final int addressSize = unsafe.addressSize();
  private static final int SIZEOF_OVERLAPPED = dependsArch(20, 32);
  private boolean closed;
  private boolean closePending;
  private final Map<Long, PendingFuture> pendingIoMap = new HashMap();
  private long[] overlappedCache = new long[4];
  private int overlappedCacheCount = 0;
  
  private static int dependsArch(int paramInt1, int paramInt2)
  {
    return addressSize == 4 ? paramInt1 : paramInt2;
  }
  
  PendingIoCache() {}
  
  long add(PendingFuture<?, ?> paramPendingFuture)
  {
    synchronized (this)
    {
      if (closed) {
        throw new AssertionError("Should not get here");
      }
      long l;
      if (overlappedCacheCount > 0) {
        l = overlappedCache[(--overlappedCacheCount)];
      } else {
        l = unsafe.allocateMemory(SIZEOF_OVERLAPPED);
      }
      pendingIoMap.put(Long.valueOf(l), paramPendingFuture);
      return l;
    }
  }
  
  <V, A> PendingFuture<V, A> remove(long paramLong)
  {
    synchronized (this)
    {
      PendingFuture localPendingFuture = (PendingFuture)pendingIoMap.remove(Long.valueOf(paramLong));
      if (localPendingFuture != null)
      {
        if (overlappedCacheCount < overlappedCache.length) {
          overlappedCache[(overlappedCacheCount++)] = paramLong;
        } else {
          unsafe.freeMemory(paramLong);
        }
        if (closePending) {
          notifyAll();
        }
      }
      return localPendingFuture;
    }
  }
  
  void close()
  {
    synchronized (this)
    {
      if (closed) {
        return;
      }
      if (!pendingIoMap.isEmpty()) {
        clearPendingIoMap();
      }
      while (overlappedCacheCount > 0) {
        unsafe.freeMemory(overlappedCache[(--overlappedCacheCount)]);
      }
      closed = true;
    }
  }
  
  private void clearPendingIoMap()
  {
    assert (Thread.holdsLock(this));
    closePending = true;
    try
    {
      wait(50L);
    }
    catch (InterruptedException localInterruptedException)
    {
      Thread.currentThread().interrupt();
    }
    closePending = false;
    if (pendingIoMap.isEmpty()) {
      return;
    }
    Iterator localIterator = pendingIoMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Long localLong = (Long)localIterator.next();
      PendingFuture localPendingFuture = (PendingFuture)pendingIoMap.get(localLong);
      assert (!localPendingFuture.isDone());
      Iocp localIocp = (Iocp)((Groupable)localPendingFuture.channel()).group();
      localIocp.makeStale(localLong);
      final Iocp.ResultHandler localResultHandler = (Iocp.ResultHandler)localPendingFuture.getContext();
      Runnable local1 = new Runnable()
      {
        public void run()
        {
          localResultHandler.failed(-1, new AsynchronousCloseException());
        }
      };
      localIocp.executeOnPooledThread(local1);
    }
    pendingIoMap.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\PendingIoCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class Semaphore
  implements Serializable
{
  private static final long serialVersionUID = -3222578661600680210L;
  private final Sync sync;
  
  public Semaphore(int paramInt)
  {
    sync = new NonfairSync(paramInt);
  }
  
  public Semaphore(int paramInt, boolean paramBoolean)
  {
    sync = (paramBoolean ? new FairSync(paramInt) : new NonfairSync(paramInt));
  }
  
  public void acquire()
    throws InterruptedException
  {
    sync.acquireSharedInterruptibly(1);
  }
  
  public void acquireUninterruptibly()
  {
    sync.acquireShared(1);
  }
  
  public boolean tryAcquire()
  {
    return sync.nonfairTryAcquireShared(1) >= 0;
  }
  
  public boolean tryAcquire(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
  }
  
  public void release()
  {
    sync.releaseShared(1);
  }
  
  public void acquire(int paramInt)
    throws InterruptedException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    sync.acquireSharedInterruptibly(paramInt);
  }
  
  public void acquireUninterruptibly(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    sync.acquireShared(paramInt);
  }
  
  public boolean tryAcquire(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    return sync.nonfairTryAcquireShared(paramInt) >= 0;
  }
  
  public boolean tryAcquire(int paramInt, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    return sync.tryAcquireSharedNanos(paramInt, paramTimeUnit.toNanos(paramLong));
  }
  
  public void release(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    sync.releaseShared(paramInt);
  }
  
  public int availablePermits()
  {
    return sync.getPermits();
  }
  
  public int drainPermits()
  {
    return sync.drainPermits();
  }
  
  protected void reducePermits(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    sync.reducePermits(paramInt);
  }
  
  public boolean isFair()
  {
    return sync instanceof FairSync;
  }
  
  public final boolean hasQueuedThreads()
  {
    return sync.hasQueuedThreads();
  }
  
  public final int getQueueLength()
  {
    return sync.getQueueLength();
  }
  
  protected Collection<Thread> getQueuedThreads()
  {
    return sync.getQueuedThreads();
  }
  
  public String toString()
  {
    return super.toString() + "[Permits = " + sync.getPermits() + "]";
  }
  
  static final class FairSync
    extends Semaphore.Sync
  {
    private static final long serialVersionUID = 2014338818796000944L;
    
    FairSync(int paramInt)
    {
      super();
    }
    
    protected int tryAcquireShared(int paramInt)
    {
      for (;;)
      {
        if (hasQueuedPredecessors()) {
          return -1;
        }
        int i = getState();
        int j = i - paramInt;
        if ((j < 0) || (compareAndSetState(i, j))) {
          return j;
        }
      }
    }
  }
  
  static final class NonfairSync
    extends Semaphore.Sync
  {
    private static final long serialVersionUID = -2694183684443567898L;
    
    NonfairSync(int paramInt)
    {
      super();
    }
    
    protected int tryAcquireShared(int paramInt)
    {
      return nonfairTryAcquireShared(paramInt);
    }
  }
  
  static abstract class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = 1192457210091910933L;
    
    Sync(int paramInt)
    {
      setState(paramInt);
    }
    
    final int getPermits()
    {
      return getState();
    }
    
    final int nonfairTryAcquireShared(int paramInt)
    {
      for (;;)
      {
        int i = getState();
        int j = i - paramInt;
        if ((j < 0) || (compareAndSetState(i, j))) {
          return j;
        }
      }
    }
    
    protected final boolean tryReleaseShared(int paramInt)
    {
      for (;;)
      {
        int i = getState();
        int j = i + paramInt;
        if (j < i) {
          throw new Error("Maximum permit count exceeded");
        }
        if (compareAndSetState(i, j)) {
          return true;
        }
      }
    }
    
    final void reducePermits(int paramInt)
    {
      for (;;)
      {
        int i = getState();
        int j = i - paramInt;
        if (j > i) {
          throw new Error("Permit count underflow");
        }
        if (compareAndSetState(i, j)) {
          return;
        }
      }
    }
    
    final int drainPermits()
    {
      for (;;)
      {
        int i = getState();
        if ((i == 0) || (compareAndSetState(i, 0))) {
          return i;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\Semaphore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
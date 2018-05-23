package java.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CountDownLatch
{
  private final Sync sync;
  
  public CountDownLatch(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("count < 0");
    }
    sync = new Sync(paramInt);
  }
  
  public void await()
    throws InterruptedException
  {
    sync.acquireSharedInterruptibly(1);
  }
  
  public boolean await(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
  }
  
  public void countDown()
  {
    sync.releaseShared(1);
  }
  
  public long getCount()
  {
    return sync.getCount();
  }
  
  public String toString()
  {
    return super.toString() + "[Count = " + sync.getCount() + "]";
  }
  
  private static final class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = 4982264981922014374L;
    
    Sync(int paramInt)
    {
      setState(paramInt);
    }
    
    int getCount()
    {
      return getState();
    }
    
    protected int tryAcquireShared(int paramInt)
    {
      return getState() == 0 ? 1 : -1;
    }
    
    protected boolean tryReleaseShared(int paramInt)
    {
      for (;;)
      {
        int i = getState();
        if (i == 0) {
          return false;
        }
        int j = i - 1;
        if (compareAndSetState(i, j)) {
          return j == 0;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CountDownLatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
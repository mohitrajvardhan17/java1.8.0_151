package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ReentrantLock
  implements Lock, Serializable
{
  private static final long serialVersionUID = 7373984872572414699L;
  private final Sync sync;
  
  public ReentrantLock()
  {
    sync = new NonfairSync();
  }
  
  public ReentrantLock(boolean paramBoolean)
  {
    sync = (paramBoolean ? new FairSync() : new NonfairSync());
  }
  
  public void lock()
  {
    sync.lock();
  }
  
  public void lockInterruptibly()
    throws InterruptedException
  {
    sync.acquireInterruptibly(1);
  }
  
  public boolean tryLock()
  {
    return sync.nonfairTryAcquire(1);
  }
  
  public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return sync.tryAcquireNanos(1, paramTimeUnit.toNanos(paramLong));
  }
  
  public void unlock()
  {
    sync.release(1);
  }
  
  public Condition newCondition()
  {
    return sync.newCondition();
  }
  
  public int getHoldCount()
  {
    return sync.getHoldCount();
  }
  
  public boolean isHeldByCurrentThread()
  {
    return sync.isHeldExclusively();
  }
  
  public boolean isLocked()
  {
    return sync.isLocked();
  }
  
  public final boolean isFair()
  {
    return sync instanceof FairSync;
  }
  
  protected Thread getOwner()
  {
    return sync.getOwner();
  }
  
  public final boolean hasQueuedThreads()
  {
    return sync.hasQueuedThreads();
  }
  
  public final boolean hasQueuedThread(Thread paramThread)
  {
    return sync.isQueued(paramThread);
  }
  
  public final int getQueueLength()
  {
    return sync.getQueueLength();
  }
  
  protected Collection<Thread> getQueuedThreads()
  {
    return sync.getQueuedThreads();
  }
  
  public boolean hasWaiters(Condition paramCondition)
  {
    if (paramCondition == null) {
      throw new NullPointerException();
    }
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject)) {
      throw new IllegalArgumentException("not owner");
    }
    return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  public int getWaitQueueLength(Condition paramCondition)
  {
    if (paramCondition == null) {
      throw new NullPointerException();
    }
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject)) {
      throw new IllegalArgumentException("not owner");
    }
    return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  protected Collection<Thread> getWaitingThreads(Condition paramCondition)
  {
    if (paramCondition == null) {
      throw new NullPointerException();
    }
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject)) {
      throw new IllegalArgumentException("not owner");
    }
    return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  public String toString()
  {
    Thread localThread = sync.getOwner();
    return super.toString() + (localThread == null ? "[Unlocked]" : new StringBuilder().append("[Locked by thread ").append(localThread.getName()).append("]").toString());
  }
  
  static final class FairSync
    extends ReentrantLock.Sync
  {
    private static final long serialVersionUID = -3000897897090466540L;
    
    FairSync() {}
    
    final void lock()
    {
      acquire(1);
    }
    
    protected final boolean tryAcquire(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if (i == 0)
      {
        if ((!hasQueuedPredecessors()) && (compareAndSetState(0, paramInt)))
        {
          setExclusiveOwnerThread(localThread);
          return true;
        }
      }
      else if (localThread == getExclusiveOwnerThread())
      {
        int j = i + paramInt;
        if (j < 0) {
          throw new Error("Maximum lock count exceeded");
        }
        setState(j);
        return true;
      }
      return false;
    }
  }
  
  static final class NonfairSync
    extends ReentrantLock.Sync
  {
    private static final long serialVersionUID = 7316153563782823691L;
    
    NonfairSync() {}
    
    final void lock()
    {
      if (compareAndSetState(0, 1)) {
        setExclusiveOwnerThread(Thread.currentThread());
      } else {
        acquire(1);
      }
    }
    
    protected final boolean tryAcquire(int paramInt)
    {
      return nonfairTryAcquire(paramInt);
    }
  }
  
  static abstract class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = -5179523762034025860L;
    
    Sync() {}
    
    abstract void lock();
    
    final boolean nonfairTryAcquire(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if (i == 0)
      {
        if (compareAndSetState(0, paramInt))
        {
          setExclusiveOwnerThread(localThread);
          return true;
        }
      }
      else if (localThread == getExclusiveOwnerThread())
      {
        int j = i + paramInt;
        if (j < 0) {
          throw new Error("Maximum lock count exceeded");
        }
        setState(j);
        return true;
      }
      return false;
    }
    
    protected final boolean tryRelease(int paramInt)
    {
      int i = getState() - paramInt;
      if (Thread.currentThread() != getExclusiveOwnerThread()) {
        throw new IllegalMonitorStateException();
      }
      boolean bool = false;
      if (i == 0)
      {
        bool = true;
        setExclusiveOwnerThread(null);
      }
      setState(i);
      return bool;
    }
    
    protected final boolean isHeldExclusively()
    {
      return getExclusiveOwnerThread() == Thread.currentThread();
    }
    
    final AbstractQueuedSynchronizer.ConditionObject newCondition()
    {
      return new AbstractQueuedSynchronizer.ConditionObject(this);
    }
    
    final Thread getOwner()
    {
      return getState() == 0 ? null : getExclusiveOwnerThread();
    }
    
    final int getHoldCount()
    {
      return isHeldExclusively() ? getState() : 0;
    }
    
    final boolean isLocked()
    {
      return getState() != 0;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      setState(0);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\locks\ReentrantLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
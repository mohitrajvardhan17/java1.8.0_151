package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ReentrantReadWriteLock
  implements ReadWriteLock, Serializable
{
  private static final long serialVersionUID = -6992448646407690164L;
  private final ReadLock readerLock = new ReadLock(this);
  private final WriteLock writerLock = new WriteLock(this);
  final Sync sync = paramBoolean ? new FairSync() : new NonfairSync();
  private static final Unsafe UNSAFE;
  private static final long TID_OFFSET;
  
  public ReentrantReadWriteLock()
  {
    this(false);
  }
  
  public ReentrantReadWriteLock(boolean paramBoolean) {}
  
  public WriteLock writeLock()
  {
    return writerLock;
  }
  
  public ReadLock readLock()
  {
    return readerLock;
  }
  
  public final boolean isFair()
  {
    return sync instanceof FairSync;
  }
  
  protected Thread getOwner()
  {
    return sync.getOwner();
  }
  
  public int getReadLockCount()
  {
    return sync.getReadLockCount();
  }
  
  public boolean isWriteLocked()
  {
    return sync.isWriteLocked();
  }
  
  public boolean isWriteLockedByCurrentThread()
  {
    return sync.isHeldExclusively();
  }
  
  public int getWriteHoldCount()
  {
    return sync.getWriteHoldCount();
  }
  
  public int getReadHoldCount()
  {
    return sync.getReadHoldCount();
  }
  
  protected Collection<Thread> getQueuedWriterThreads()
  {
    return sync.getExclusiveQueuedThreads();
  }
  
  protected Collection<Thread> getQueuedReaderThreads()
  {
    return sync.getSharedQueuedThreads();
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
    int i = sync.getCount();
    int j = Sync.exclusiveCount(i);
    int k = Sync.sharedCount(i);
    return super.toString() + "[Write locks = " + j + ", Read locks = " + k + "]";
  }
  
  static final long getThreadId(Thread paramThread)
  {
    return UNSAFE.getLongVolatile(paramThread, TID_OFFSET);
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = Thread.class;
      TID_OFFSET = UNSAFE.objectFieldOffset(localClass.getDeclaredField("tid"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class FairSync
    extends ReentrantReadWriteLock.Sync
  {
    private static final long serialVersionUID = -2274990926593161451L;
    
    FairSync() {}
    
    final boolean writerShouldBlock()
    {
      return hasQueuedPredecessors();
    }
    
    final boolean readerShouldBlock()
    {
      return hasQueuedPredecessors();
    }
  }
  
  static final class NonfairSync
    extends ReentrantReadWriteLock.Sync
  {
    private static final long serialVersionUID = -8159625535654395037L;
    
    NonfairSync() {}
    
    final boolean writerShouldBlock()
    {
      return false;
    }
    
    final boolean readerShouldBlock()
    {
      return apparentlyFirstQueuedIsExclusive();
    }
  }
  
  public static class ReadLock
    implements Lock, Serializable
  {
    private static final long serialVersionUID = -5992448646407690164L;
    private final ReentrantReadWriteLock.Sync sync;
    
    protected ReadLock(ReentrantReadWriteLock paramReentrantReadWriteLock)
    {
      sync = sync;
    }
    
    public void lock()
    {
      sync.acquireShared(1);
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      sync.acquireSharedInterruptibly(1);
    }
    
    public boolean tryLock()
    {
      return sync.tryReadLock();
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
    }
    
    public void unlock()
    {
      sync.releaseShared(1);
    }
    
    public Condition newCondition()
    {
      throw new UnsupportedOperationException();
    }
    
    public String toString()
    {
      int i = sync.getReadLockCount();
      return super.toString() + "[Read locks = " + i + "]";
    }
  }
  
  static abstract class Sync
    extends AbstractQueuedSynchronizer
  {
    private static final long serialVersionUID = 6317671515068378041L;
    static final int SHARED_SHIFT = 16;
    static final int SHARED_UNIT = 65536;
    static final int MAX_COUNT = 65535;
    static final int EXCLUSIVE_MASK = 65535;
    private transient ThreadLocalHoldCounter readHolds = new ThreadLocalHoldCounter();
    private transient HoldCounter cachedHoldCounter;
    private transient Thread firstReader = null;
    private transient int firstReaderHoldCount;
    
    static int sharedCount(int paramInt)
    {
      return paramInt >>> 16;
    }
    
    static int exclusiveCount(int paramInt)
    {
      return paramInt & 0xFFFF;
    }
    
    Sync()
    {
      setState(getState());
    }
    
    abstract boolean readerShouldBlock();
    
    abstract boolean writerShouldBlock();
    
    protected final boolean tryRelease(int paramInt)
    {
      if (!isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      int i = getState() - paramInt;
      boolean bool = exclusiveCount(i) == 0;
      if (bool) {
        setExclusiveOwnerThread(null);
      }
      setState(i);
      return bool;
    }
    
    protected final boolean tryAcquire(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      int j = exclusiveCount(i);
      if (i != 0)
      {
        if ((j == 0) || (localThread != getExclusiveOwnerThread())) {
          return false;
        }
        if (j + exclusiveCount(paramInt) > 65535) {
          throw new Error("Maximum lock count exceeded");
        }
        setState(i + paramInt);
        return true;
      }
      if ((writerShouldBlock()) || (!compareAndSetState(i, i + paramInt))) {
        return false;
      }
      setExclusiveOwnerThread(localThread);
      return true;
    }
    
    protected final boolean tryReleaseShared(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int j;
      if (firstReader == localThread)
      {
        if (firstReaderHoldCount == 1) {
          firstReader = null;
        } else {
          firstReaderHoldCount -= 1;
        }
      }
      else
      {
        HoldCounter localHoldCounter = cachedHoldCounter;
        if ((localHoldCounter == null) || (tid != ReentrantReadWriteLock.getThreadId(localThread))) {
          localHoldCounter = (HoldCounter)readHolds.get();
        }
        j = count;
        if (j <= 1)
        {
          readHolds.remove();
          if (j <= 0) {
            throw unmatchedUnlockException();
          }
        }
        count -= 1;
      }
      for (;;)
      {
        int i = getState();
        j = i - 65536;
        if (compareAndSetState(i, j)) {
          return j == 0;
        }
      }
    }
    
    private IllegalMonitorStateException unmatchedUnlockException()
    {
      return new IllegalMonitorStateException("attempt to unlock read lock, not locked by current thread");
    }
    
    protected final int tryAcquireShared(int paramInt)
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if ((exclusiveCount(i) != 0) && (getExclusiveOwnerThread() != localThread)) {
        return -1;
      }
      int j = sharedCount(i);
      if ((!readerShouldBlock()) && (j < 65535) && (compareAndSetState(i, i + 65536)))
      {
        if (j == 0)
        {
          firstReader = localThread;
          firstReaderHoldCount = 1;
        }
        else if (firstReader == localThread)
        {
          firstReaderHoldCount += 1;
        }
        else
        {
          HoldCounter localHoldCounter = cachedHoldCounter;
          if ((localHoldCounter == null) || (tid != ReentrantReadWriteLock.getThreadId(localThread))) {
            cachedHoldCounter = (localHoldCounter = (HoldCounter)readHolds.get());
          } else if (count == 0) {
            readHolds.set(localHoldCounter);
          }
          count += 1;
        }
        return 1;
      }
      return fullTryAcquireShared(localThread);
    }
    
    final int fullTryAcquireShared(Thread paramThread)
    {
      HoldCounter localHoldCounter = null;
      for (;;)
      {
        int i = getState();
        if (exclusiveCount(i) != 0)
        {
          if (getExclusiveOwnerThread() != paramThread) {
            return -1;
          }
        }
        else if ((readerShouldBlock()) && (firstReader != paramThread))
        {
          if (localHoldCounter == null)
          {
            localHoldCounter = cachedHoldCounter;
            if ((localHoldCounter == null) || (tid != ReentrantReadWriteLock.getThreadId(paramThread)))
            {
              localHoldCounter = (HoldCounter)readHolds.get();
              if (count == 0) {
                readHolds.remove();
              }
            }
          }
          if (count == 0) {
            return -1;
          }
        }
        if (sharedCount(i) == 65535) {
          throw new Error("Maximum lock count exceeded");
        }
        if (compareAndSetState(i, i + 65536))
        {
          if (sharedCount(i) == 0)
          {
            firstReader = paramThread;
            firstReaderHoldCount = 1;
          }
          else if (firstReader == paramThread)
          {
            firstReaderHoldCount += 1;
          }
          else
          {
            if (localHoldCounter == null) {
              localHoldCounter = cachedHoldCounter;
            }
            if ((localHoldCounter == null) || (tid != ReentrantReadWriteLock.getThreadId(paramThread))) {
              localHoldCounter = (HoldCounter)readHolds.get();
            } else if (count == 0) {
              readHolds.set(localHoldCounter);
            }
            count += 1;
            cachedHoldCounter = localHoldCounter;
          }
          return 1;
        }
      }
    }
    
    final boolean tryWriteLock()
    {
      Thread localThread = Thread.currentThread();
      int i = getState();
      if (i != 0)
      {
        int j = exclusiveCount(i);
        if ((j == 0) || (localThread != getExclusiveOwnerThread())) {
          return false;
        }
        if (j == 65535) {
          throw new Error("Maximum lock count exceeded");
        }
      }
      if (!compareAndSetState(i, i + 1)) {
        return false;
      }
      setExclusiveOwnerThread(localThread);
      return true;
    }
    
    final boolean tryReadLock()
    {
      Thread localThread = Thread.currentThread();
      for (;;)
      {
        int i = getState();
        if ((exclusiveCount(i) != 0) && (getExclusiveOwnerThread() != localThread)) {
          return false;
        }
        int j = sharedCount(i);
        if (j == 65535) {
          throw new Error("Maximum lock count exceeded");
        }
        if (compareAndSetState(i, i + 65536))
        {
          if (j == 0)
          {
            firstReader = localThread;
            firstReaderHoldCount = 1;
          }
          else if (firstReader == localThread)
          {
            firstReaderHoldCount += 1;
          }
          else
          {
            HoldCounter localHoldCounter = cachedHoldCounter;
            if ((localHoldCounter == null) || (tid != ReentrantReadWriteLock.getThreadId(localThread))) {
              cachedHoldCounter = (localHoldCounter = (HoldCounter)readHolds.get());
            } else if (count == 0) {
              readHolds.set(localHoldCounter);
            }
            count += 1;
          }
          return true;
        }
      }
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
      return exclusiveCount(getState()) == 0 ? null : getExclusiveOwnerThread();
    }
    
    final int getReadLockCount()
    {
      return sharedCount(getState());
    }
    
    final boolean isWriteLocked()
    {
      return exclusiveCount(getState()) != 0;
    }
    
    final int getWriteHoldCount()
    {
      return isHeldExclusively() ? exclusiveCount(getState()) : 0;
    }
    
    final int getReadHoldCount()
    {
      if (getReadLockCount() == 0) {
        return 0;
      }
      Thread localThread = Thread.currentThread();
      if (firstReader == localThread) {
        return firstReaderHoldCount;
      }
      HoldCounter localHoldCounter = cachedHoldCounter;
      if ((localHoldCounter != null) && (tid == ReentrantReadWriteLock.getThreadId(localThread))) {
        return count;
      }
      int i = readHolds.get()).count;
      if (i == 0) {
        readHolds.remove();
      }
      return i;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      readHolds = new ThreadLocalHoldCounter();
      setState(0);
    }
    
    final int getCount()
    {
      return getState();
    }
    
    static final class HoldCounter
    {
      int count = 0;
      final long tid = ReentrantReadWriteLock.getThreadId(Thread.currentThread());
      
      HoldCounter() {}
    }
    
    static final class ThreadLocalHoldCounter
      extends ThreadLocal<ReentrantReadWriteLock.Sync.HoldCounter>
    {
      ThreadLocalHoldCounter() {}
      
      public ReentrantReadWriteLock.Sync.HoldCounter initialValue()
      {
        return new ReentrantReadWriteLock.Sync.HoldCounter();
      }
    }
  }
  
  public static class WriteLock
    implements Lock, Serializable
  {
    private static final long serialVersionUID = -4992448646407690164L;
    private final ReentrantReadWriteLock.Sync sync;
    
    protected WriteLock(ReentrantReadWriteLock paramReentrantReadWriteLock)
    {
      sync = sync;
    }
    
    public void lock()
    {
      sync.acquire(1);
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      sync.acquireInterruptibly(1);
    }
    
    public boolean tryLock()
    {
      return sync.tryWriteLock();
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
    
    public String toString()
    {
      Thread localThread = sync.getOwner();
      return super.toString() + (localThread == null ? "[Unlocked]" : new StringBuilder().append("[Locked by thread ").append(localThread.getName()).append("]").toString());
    }
    
    public boolean isHeldByCurrentThread()
    {
      return sync.isHeldExclusively();
    }
    
    public int getHoldCount()
    {
      return sync.getWriteHoldCount();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\locks\ReentrantReadWriteLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
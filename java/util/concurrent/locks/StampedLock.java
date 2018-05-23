package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class StampedLock
  implements Serializable
{
  private static final long serialVersionUID = -6001602636862214147L;
  private static final int NCPU = Runtime.getRuntime().availableProcessors();
  private static final int SPINS = NCPU > 1 ? 64 : 0;
  private static final int HEAD_SPINS = NCPU > 1 ? 1024 : 0;
  private static final int MAX_HEAD_SPINS = NCPU > 1 ? 65536 : 0;
  private static final int OVERFLOW_YIELD_RATE = 7;
  private static final int LG_READERS = 7;
  private static final long RUNIT = 1L;
  private static final long WBIT = 128L;
  private static final long RBITS = 127L;
  private static final long RFULL = 126L;
  private static final long ABITS = 255L;
  private static final long SBITS = -128L;
  private static final long ORIGIN = 256L;
  private static final long INTERRUPTED = 1L;
  private static final int WAITING = -1;
  private static final int CANCELLED = 1;
  private static final int RMODE = 0;
  private static final int WMODE = 1;
  private volatile transient WNode whead;
  private volatile transient WNode wtail;
  transient ReadLockView readLockView;
  transient WriteLockView writeLockView;
  transient ReadWriteLockView readWriteLockView;
  private volatile transient long state = 256L;
  private transient int readerOverflow;
  private static final Unsafe U;
  private static final long STATE;
  private static final long WHEAD;
  private static final long WTAIL;
  private static final long WNEXT;
  private static final long WSTATUS;
  private static final long WCOWAIT;
  private static final long PARKBLOCKER;
  
  public StampedLock() {}
  
  public long writeLock()
  {
    long l1;
    long l2;
    return (((l1 = state) & 0xFF) == 0L) && (U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 128L)) ? l2 : acquireWrite(false, 0L);
  }
  
  public long tryWriteLock()
  {
    long l1;
    long l2;
    return (((l1 = state) & 0xFF) == 0L) && (U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 128L)) ? l2 : 0L;
  }
  
  public long tryWriteLock(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l1 = paramTimeUnit.toNanos(paramLong);
    if (!Thread.interrupted())
    {
      long l2;
      if ((l2 = tryWriteLock()) != 0L) {
        return l2;
      }
      if (l1 <= 0L) {
        return 0L;
      }
      long l3;
      if ((l3 = System.nanoTime() + l1) == 0L) {
        l3 = 1L;
      }
      if ((l2 = acquireWrite(true, l3)) != 1L) {
        return l2;
      }
    }
    throw new InterruptedException();
  }
  
  public long writeLockInterruptibly()
    throws InterruptedException
  {
    long l;
    if ((!Thread.interrupted()) && ((l = acquireWrite(true, 0L)) != 1L)) {
      return l;
    }
    throw new InterruptedException();
  }
  
  public long readLock()
  {
    long l1 = state;
    long l2;
    return (whead == wtail) && ((l1 & 0xFF) < 126L) && (U.compareAndSwapLong(this, STATE, l1, l2 = l1 + 1L)) ? l2 : acquireRead(false, 0L);
  }
  
  public long tryReadLock()
  {
    for (;;)
    {
      long l1;
      long l2;
      if ((l2 = (l1 = state) & 0xFF) == 128L) {
        return 0L;
      }
      long l3;
      if (l2 < 126L)
      {
        if (U.compareAndSwapLong(this, STATE, l1, l3 = l1 + 1L)) {
          return l3;
        }
      }
      else if ((l3 = tryIncReaderOverflow(l1)) != 0L) {
        return l3;
      }
    }
  }
  
  public long tryReadLock(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l5 = paramTimeUnit.toNanos(paramLong);
    if (!Thread.interrupted())
    {
      long l1;
      long l2;
      long l3;
      if ((l2 = (l1 = state) & 0xFF) != 128L) {
        if (l2 < 126L)
        {
          if (U.compareAndSwapLong(this, STATE, l1, l3 = l1 + 1L)) {
            return l3;
          }
        }
        else if ((l3 = tryIncReaderOverflow(l1)) != 0L) {
          return l3;
        }
      }
      if (l5 <= 0L) {
        return 0L;
      }
      long l4;
      if ((l4 = System.nanoTime() + l5) == 0L) {
        l4 = 1L;
      }
      if ((l3 = acquireRead(true, l4)) != 1L) {
        return l3;
      }
    }
    throw new InterruptedException();
  }
  
  public long readLockInterruptibly()
    throws InterruptedException
  {
    long l;
    if ((!Thread.interrupted()) && ((l = acquireRead(true, 0L)) != 1L)) {
      return l;
    }
    throw new InterruptedException();
  }
  
  public long tryOptimisticRead()
  {
    long l;
    return ((l = state) & 0x80) == 0L ? l & 0xFFFFFFFFFFFFFF80 : 0L;
  }
  
  public boolean validate(long paramLong)
  {
    U.loadFence();
    return (paramLong & 0xFFFFFFFFFFFFFF80) == (state & 0xFFFFFFFFFFFFFF80);
  }
  
  public void unlockWrite(long paramLong)
  {
    if ((state != paramLong) || ((paramLong & 0x80) == 0L)) {
      throw new IllegalMonitorStateException();
    }
    state = (paramLong += 128L == 0L ? 256L : paramLong);
    WNode localWNode;
    if (((localWNode = whead) != null) && (status != 0)) {
      release(localWNode);
    }
  }
  
  public void unlockRead(long paramLong)
  {
    for (;;)
    {
      long l1;
      long l2;
      if ((((l1 = state) & 0xFFFFFFFFFFFFFF80) != (paramLong & 0xFFFFFFFFFFFFFF80)) || ((paramLong & 0xFF) == 0L) || ((l2 = l1 & 0xFF) == 0L) || (l2 == 128L)) {
        throw new IllegalMonitorStateException();
      }
      if (l2 < 126L)
      {
        if (U.compareAndSwapLong(this, STATE, l1, l1 - 1L))
        {
          WNode localWNode;
          if ((l2 == 1L) && ((localWNode = whead) != null) && (status != 0)) {
            release(localWNode);
          }
        }
      }
      else if (tryDecReaderOverflow(l1) != 0L) {
        break;
      }
    }
  }
  
  public void unlock(long paramLong)
  {
    long l1 = paramLong & 0xFF;
    long l3;
    long l2;
    while ((((l3 = state) & 0xFFFFFFFFFFFFFF80) == (paramLong & 0xFFFFFFFFFFFFFF80)) && ((l2 = l3 & 0xFF) != 0L))
    {
      WNode localWNode;
      if (l2 == 128L)
      {
        if (l1 == l2)
        {
          state = (l3 += 128L == 0L ? 256L : l3);
          if (((localWNode = whead) != null) && (status != 0)) {
            release(localWNode);
          }
        }
      }
      else if ((l1 != 0L) && (l1 < 128L)) {
        if (l2 < 126L)
        {
          if (U.compareAndSwapLong(this, STATE, l3, l3 - 1L)) {
            if ((l2 == 1L) && ((localWNode = whead) != null) && (status != 0)) {
              release(localWNode);
            }
          }
        }
        else if (tryDecReaderOverflow(l3) != 0L) {
          return;
        }
      }
    }
    throw new IllegalMonitorStateException();
  }
  
  public long tryConvertToWriteLock(long paramLong)
  {
    long l1 = paramLong & 0xFF;
    long l3;
    while (((l3 = state) & 0xFFFFFFFFFFFFFF80) == (paramLong & 0xFFFFFFFFFFFFFF80))
    {
      long l2;
      long l4;
      if ((l2 = l3 & 0xFF) == 0L)
      {
        if (l1 == 0L) {
          if (U.compareAndSwapLong(this, STATE, l3, l4 = l3 + 128L)) {
            return l4;
          }
        }
      }
      else if (l2 == 128L)
      {
        if (l1 == l2) {
          return paramLong;
        }
      }
      else if ((l2 == 1L) && (l1 != 0L)) {
        if (U.compareAndSwapLong(this, STATE, l3, l4 = l3 - 1L + 128L)) {
          return l4;
        }
      }
    }
    return 0L;
  }
  
  public long tryConvertToReadLock(long paramLong)
  {
    long l1 = paramLong & 0xFF;
    long l3;
    while (((l3 = state) & 0xFFFFFFFFFFFFFF80) == (paramLong & 0xFFFFFFFFFFFFFF80))
    {
      long l2;
      long l4;
      if ((l2 = l3 & 0xFF) == 0L)
      {
        if (l1 == 0L) {
          if (l2 < 126L)
          {
            if (U.compareAndSwapLong(this, STATE, l3, l4 = l3 + 1L)) {
              return l4;
            }
          }
          else if ((l4 = tryIncReaderOverflow(l3)) != 0L) {
            return l4;
          }
        }
      }
      else if (l2 == 128L)
      {
        if (l1 == l2)
        {
          state = (l4 = l3 + 129L);
          WNode localWNode;
          if (((localWNode = whead) != null) && (status != 0)) {
            release(localWNode);
          }
          return l4;
        }
      }
      else if ((l1 != 0L) && (l1 < 128L)) {
        return paramLong;
      }
    }
    return 0L;
  }
  
  public long tryConvertToOptimisticRead(long paramLong)
  {
    long l1 = paramLong & 0xFF;
    U.loadFence();
    long l3;
    while (((l3 = state) & 0xFFFFFFFFFFFFFF80) == (paramLong & 0xFFFFFFFFFFFFFF80))
    {
      long l2;
      if ((l2 = l3 & 0xFF) == 0L)
      {
        if (l1 == 0L) {
          return l3;
        }
      }
      else
      {
        long l4;
        WNode localWNode;
        if (l2 == 128L)
        {
          if (l1 == l2)
          {
            state = (l4 = l3 += 128L == 0L ? 256L : l3);
            if (((localWNode = whead) != null) && (status != 0)) {
              release(localWNode);
            }
            return l4;
          }
        }
        else if ((l1 != 0L) && (l1 < 128L)) {
          if (l2 < 126L)
          {
            if (U.compareAndSwapLong(this, STATE, l3, l4 = l3 - 1L))
            {
              if ((l2 == 1L) && ((localWNode = whead) != null) && (status != 0)) {
                release(localWNode);
              }
              return l4 & 0xFFFFFFFFFFFFFF80;
            }
          }
          else if ((l4 = tryDecReaderOverflow(l3)) != 0L) {
            return l4 & 0xFFFFFFFFFFFFFF80;
          }
        }
      }
    }
    return 0L;
  }
  
  public boolean tryUnlockWrite()
  {
    long l;
    if (((l = state) & 0x80) != 0L)
    {
      state = (l += 128L == 0L ? 256L : l);
      WNode localWNode;
      if (((localWNode = whead) != null) && (status != 0)) {
        release(localWNode);
      }
      return true;
    }
    return false;
  }
  
  public boolean tryUnlockRead()
  {
    long l1;
    long l2;
    while (((l2 = (l1 = state) & 0xFF) != 0L) && (l2 < 128L)) {
      if (l2 < 126L)
      {
        if (U.compareAndSwapLong(this, STATE, l1, l1 - 1L))
        {
          WNode localWNode;
          if ((l2 == 1L) && ((localWNode = whead) != null) && (status != 0)) {
            release(localWNode);
          }
          return true;
        }
      }
      else if (tryDecReaderOverflow(l1) != 0L) {
        return true;
      }
    }
    return false;
  }
  
  private int getReadLockCount(long paramLong)
  {
    long l;
    if ((l = paramLong & 0x7F) >= 126L) {
      l = 126L + readerOverflow;
    }
    return (int)l;
  }
  
  public boolean isWriteLocked()
  {
    return (state & 0x80) != 0L;
  }
  
  public boolean isReadLocked()
  {
    return (state & 0x7F) != 0L;
  }
  
  public int getReadLockCount()
  {
    return getReadLockCount(state);
  }
  
  public String toString()
  {
    long l = state;
    return super.toString() + ((l & 0x80) != 0L ? "[Write-locked]" : (l & 0xFF) == 0L ? "[Unlocked]" : new StringBuilder().append("[Read-locks:").append(getReadLockCount(l)).append("]").toString());
  }
  
  public Lock asReadLock()
  {
    ReadLockView localReadLockView;
    return (localReadLockView = readLockView) != null ? localReadLockView : (readLockView = new ReadLockView());
  }
  
  public Lock asWriteLock()
  {
    WriteLockView localWriteLockView;
    return (localWriteLockView = writeLockView) != null ? localWriteLockView : (writeLockView = new WriteLockView());
  }
  
  public ReadWriteLock asReadWriteLock()
  {
    ReadWriteLockView localReadWriteLockView;
    return (localReadWriteLockView = readWriteLockView) != null ? localReadWriteLockView : (readWriteLockView = new ReadWriteLockView());
  }
  
  final void unstampedUnlockWrite()
  {
    long l;
    if (((l = state) & 0x80) == 0L) {
      throw new IllegalMonitorStateException();
    }
    state = (l += 128L == 0L ? 256L : l);
    WNode localWNode;
    if (((localWNode = whead) != null) && (status != 0)) {
      release(localWNode);
    }
  }
  
  final void unstampedUnlockRead()
  {
    for (;;)
    {
      long l1;
      long l2;
      if (((l2 = (l1 = state) & 0xFF) == 0L) || (l2 >= 128L)) {
        throw new IllegalMonitorStateException();
      }
      if (l2 < 126L)
      {
        if (U.compareAndSwapLong(this, STATE, l1, l1 - 1L))
        {
          WNode localWNode;
          if ((l2 != 1L) || ((localWNode = whead) == null) || (status == 0)) {
            break;
          }
          release(localWNode);
          break;
        }
      }
      else {
        if (tryDecReaderOverflow(l1) != 0L) {
          break;
        }
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    state = 256L;
  }
  
  private long tryIncReaderOverflow(long paramLong)
  {
    if ((paramLong & 0xFF) == 126L)
    {
      if (U.compareAndSwapLong(this, STATE, paramLong, paramLong | 0x7F))
      {
        readerOverflow += 1;
        state = paramLong;
        return paramLong;
      }
    }
    else if ((LockSupport.nextSecondarySeed() & 0x7) == 0) {
      Thread.yield();
    }
    return 0L;
  }
  
  private long tryDecReaderOverflow(long paramLong)
  {
    if ((paramLong & 0xFF) == 126L)
    {
      if (U.compareAndSwapLong(this, STATE, paramLong, paramLong | 0x7F))
      {
        int i;
        long l;
        if ((i = readerOverflow) > 0)
        {
          readerOverflow = (i - 1);
          l = paramLong;
        }
        else
        {
          l = paramLong - 1L;
        }
        state = l;
        return l;
      }
    }
    else if ((LockSupport.nextSecondarySeed() & 0x7) == 0) {
      Thread.yield();
    }
    return 0L;
  }
  
  private void release(WNode paramWNode)
  {
    if (paramWNode != null)
    {
      U.compareAndSwapInt(paramWNode, WSTATUS, -1, 0);
      Object localObject;
      if (((localObject = next) == null) || (status == 1)) {
        for (WNode localWNode = wtail; (localWNode != null) && (localWNode != paramWNode); localWNode = prev) {
          if (status <= 0) {
            localObject = localWNode;
          }
        }
      }
      Thread localThread;
      if ((localObject != null) && ((localThread = thread) != null)) {
        U.unpark(localThread);
      }
    }
  }
  
  private long acquireWrite(boolean paramBoolean, long paramLong)
  {
    WNode localWNode1 = null;
    long l1 = -1;
    long l4;
    Object localObject1;
    Object localObject2;
    for (;;)
    {
      long l3;
      long l2;
      if ((l2 = (l3 = state) & 0xFF) == 0L)
      {
        if (U.compareAndSwapLong(this, STATE, l3, l4 = l3 + 128L)) {
          return l4;
        }
      }
      else if (l1 < 0)
      {
        l1 = (l2 == 128L) && (wtail == whead) ? SPINS : 0;
      }
      else if (l1 > 0)
      {
        if (LockSupport.nextSecondarySeed() >= 0) {
          l1--;
        }
      }
      else if ((localObject1 = wtail) == null)
      {
        localObject2 = new WNode(1, null);
        if (U.compareAndSwapObject(this, WHEAD, null, localObject2)) {
          wtail = ((WNode)localObject2);
        }
      }
      else if (localWNode1 == null)
      {
        localWNode1 = new WNode(1, (WNode)localObject1);
      }
      else if (prev != localObject1)
      {
        prev = ((WNode)localObject1);
      }
      else if (U.compareAndSwapObject(this, WTAIL, localObject1, localWNode1))
      {
        next = localWNode1;
        break;
      }
    }
    l1 = -1;
    for (;;)
    {
      WNode localWNode2;
      if ((localWNode2 = whead) == localObject1)
      {
        if (l1 < 0) {
          l1 = HEAD_SPINS;
        } else if (l1 < MAX_HEAD_SPINS) {
          l1 <<= 1;
        }
        l4 = l1;
        for (;;)
        {
          long l6;
          if (((l6 = state) & 0xFF) == 0L)
          {
            long l7;
            if (U.compareAndSwapLong(this, STATE, l6, l7 = l6 + 128L))
            {
              whead = localWNode1;
              prev = null;
              return l7;
            }
          }
          else if (LockSupport.nextSecondarySeed() >= 0)
          {
            l4--;
            if (l4 <= 0) {
              break;
            }
          }
        }
      }
      else if (localWNode2 != null)
      {
        WNode localWNode5;
        while ((localWNode5 = cowait) != null)
        {
          Thread localThread;
          if ((U.compareAndSwapObject(localWNode2, WCOWAIT, localWNode5, cowait)) && ((localThread = thread) != null)) {
            U.unpark(localThread);
          }
        }
      }
      if (whead == localWNode2)
      {
        WNode localWNode3;
        if ((localWNode3 = prev) != localObject1)
        {
          if (localWNode3 != null) {
            next = localWNode1;
          }
        }
        else
        {
          int i;
          if ((i = status) == 0)
          {
            U.compareAndSwapInt(localObject1, WSTATUS, 0, -1);
          }
          else if (i == 1)
          {
            WNode localWNode4;
            if ((localWNode4 = prev) != null)
            {
              prev = localWNode4;
              next = localWNode1;
            }
          }
          else
          {
            long l5;
            if (paramLong == 0L) {
              l5 = 0L;
            } else if ((l5 = paramLong - System.nanoTime()) <= 0L) {
              return cancelWaiter(localWNode1, localWNode1, false);
            }
            localObject2 = Thread.currentThread();
            U.putObject(localObject2, PARKBLOCKER, this);
            thread = ((Thread)localObject2);
            if ((status < 0) && ((localObject1 != localWNode2) || ((state & 0xFF) != 0L)) && (whead == localWNode2) && (prev == localObject1)) {
              U.park(false, l5);
            }
            thread = null;
            U.putObject(localObject2, PARKBLOCKER, null);
            if ((paramBoolean) && (Thread.interrupted())) {
              return cancelWaiter(localWNode1, localWNode1, true);
            }
          }
        }
      }
    }
  }
  
  private long acquireRead(boolean paramBoolean, long paramLong)
  {
    WNode localWNode1 = null;
    long l1 = -1;
    Object localObject2;
    Object localObject1;
    long l6;
    WNode localWNode2;
    WNode localWNode3;
    long l4;
    Thread localThread3;
    for (;;)
    {
      if ((localObject2 = whead) == (localObject1 = wtail)) {
        for (;;)
        {
          long l3;
          long l2;
          if ((l2 = (l3 = state) & 0xFF) < 126L ? U.compareAndSwapLong(this, STATE, l3, l6 = l3 + 1L) : (l2 < 128L) && ((l6 = tryIncReaderOverflow(l3)) != 0L)) {
            return l6;
          }
          if (l2 >= 128L) {
            if (l1 > 0)
            {
              if (LockSupport.nextSecondarySeed() >= 0) {
                l1--;
              }
            }
            else
            {
              if (l1 == 0)
              {
                WNode localWNode5 = whead;
                WNode localWNode6 = wtail;
                if (((localWNode5 == localObject2) && (localWNode6 == localObject1)) || ((localObject2 = localWNode5) != (localObject1 = localWNode6))) {
                  break;
                }
              }
              l1 = SPINS;
            }
          }
        }
      }
      if (localObject1 == null)
      {
        localWNode2 = new WNode(1, null);
        if (U.compareAndSwapObject(this, WHEAD, null, localWNode2)) {
          wtail = localWNode2;
        }
      }
      else if (localWNode1 == null)
      {
        localWNode1 = new WNode(0, (WNode)localObject1);
      }
      else if ((localObject2 == localObject1) || (mode != 0))
      {
        if (prev != localObject1)
        {
          prev = ((WNode)localObject1);
        }
        else if (U.compareAndSwapObject(this, WTAIL, localObject1, localWNode1))
        {
          next = localWNode1;
          break;
        }
      }
      else if (!U.compareAndSwapObject(localObject1, WCOWAIT, cowait = cowait, localWNode1))
      {
        cowait = null;
      }
      else
      {
        for (;;)
        {
          Thread localThread1;
          if (((localObject2 = whead) != null) && ((localWNode3 = cowait) != null) && (U.compareAndSwapObject(localObject2, WCOWAIT, localWNode3, cowait)) && ((localThread1 = thread) != null)) {
            U.unpark(localThread1);
          }
          if ((localObject2 == (localWNode2 = prev)) || (localObject2 == localObject1) || (localWNode2 == null)) {
            do
            {
              long l7;
              long l9;
              if ((l4 = (l7 = state) & 0xFF) < 126L ? U.compareAndSwapLong(this, STATE, l7, l9 = l7 + 1L) : (l4 < 128L) && ((l9 = tryIncReaderOverflow(l7)) != 0L)) {
                return l9;
              }
            } while (l4 < 128L);
          }
          if ((whead == localObject2) && (prev == localWNode2))
          {
            if ((localWNode2 == null) || (localObject2 == localObject1) || (status > 0))
            {
              localWNode1 = null;
              break;
            }
            if (paramLong == 0L) {
              l4 = 0L;
            } else if ((l4 = paramLong - System.nanoTime()) <= 0L) {
              return cancelWaiter(localWNode1, (WNode)localObject1, false);
            }
            localThread3 = Thread.currentThread();
            U.putObject(localThread3, PARKBLOCKER, this);
            thread = localThread3;
            if (((localObject2 != localWNode2) || ((state & 0xFF) == 128L)) && (whead == localObject2) && (prev == localWNode2)) {
              U.park(false, l4);
            }
            thread = null;
            U.putObject(localThread3, PARKBLOCKER, null);
            if ((paramBoolean) && (Thread.interrupted())) {
              return cancelWaiter(localWNode1, (WNode)localObject1, true);
            }
          }
        }
      }
    }
    l1 = -1;
    for (;;)
    {
      if ((localObject2 = whead) == localObject1)
      {
        if (l1 < 0) {
          l1 = HEAD_SPINS;
        } else if (l1 < MAX_HEAD_SPINS) {
          l1 <<= 1;
        }
        l4 = l1;
        for (;;)
        {
          long l8;
          long l10;
          if ((l6 = (l8 = state) & 0xFF) < 126L ? U.compareAndSwapLong(this, STATE, l8, l10 = l8 + 1L) : (l6 < 128L) && ((l10 = tryIncReaderOverflow(l8)) != 0L))
          {
            whead = localWNode1;
            prev = null;
            WNode localWNode7;
            while ((localWNode7 = cowait) != null)
            {
              Thread localThread4;
              if ((U.compareAndSwapObject(localWNode1, WCOWAIT, localWNode7, cowait)) && ((localThread4 = thread) != null)) {
                U.unpark(localThread4);
              }
            }
            return l10;
          }
          if ((l6 >= 128L) && (LockSupport.nextSecondarySeed() >= 0))
          {
            l4--;
            if (l4 <= 0) {
              break;
            }
          }
        }
      }
      else if (localObject2 != null)
      {
        WNode localWNode4;
        while ((localWNode4 = cowait) != null)
        {
          Thread localThread2;
          if ((U.compareAndSwapObject(localObject2, WCOWAIT, localWNode4, cowait)) && ((localThread2 = thread) != null)) {
            U.unpark(localThread2);
          }
        }
      }
      if (whead == localObject2) {
        if ((localWNode2 = prev) != localObject1)
        {
          if (localWNode2 != null) {
            next = localWNode1;
          }
        }
        else
        {
          int i;
          if ((i = status) == 0)
          {
            U.compareAndSwapInt(localObject1, WSTATUS, 0, -1);
          }
          else if (i == 1)
          {
            if ((localWNode3 = prev) != null)
            {
              prev = localWNode3;
              next = localWNode1;
            }
          }
          else
          {
            long l5;
            if (paramLong == 0L) {
              l5 = 0L;
            } else if ((l5 = paramLong - System.nanoTime()) <= 0L) {
              return cancelWaiter(localWNode1, localWNode1, false);
            }
            localThread3 = Thread.currentThread();
            U.putObject(localThread3, PARKBLOCKER, this);
            thread = localThread3;
            if ((status < 0) && ((localObject1 != localObject2) || ((state & 0xFF) == 128L)) && (whead == localObject2) && (prev == localObject1)) {
              U.park(false, l5);
            }
            thread = null;
            U.putObject(localThread3, PARKBLOCKER, null);
            if ((paramBoolean) && (Thread.interrupted())) {
              return cancelWaiter(localWNode1, localWNode1, true);
            }
          }
        }
      }
    }
  }
  
  private long cancelWaiter(WNode paramWNode1, WNode paramWNode2, boolean paramBoolean)
  {
    Object localObject1;
    Object localObject5;
    Object localObject4;
    if ((paramWNode1 != null) && (paramWNode2 != null))
    {
      status = 1;
      Object localObject2 = paramWNode2;
      Object localObject3;
      while ((localObject3 = cowait) != null) {
        if (status == 1)
        {
          U.compareAndSwapObject(localObject2, WCOWAIT, localObject3, cowait);
          localObject2 = paramWNode2;
        }
        else
        {
          localObject2 = localObject3;
        }
      }
      if (paramWNode2 == paramWNode1)
      {
        for (localObject2 = cowait; localObject2 != null; localObject2 = cowait) {
          if ((localObject1 = thread) != null) {
            U.unpark(localObject1);
          }
        }
        for (localObject2 = prev; localObject2 != null; localObject2 = localObject4)
        {
          while (((localObject3 = next) == null) || (status == 1))
          {
            localObject5 = null;
            for (WNode localWNode = wtail; (localWNode != null) && (localWNode != paramWNode1); localWNode = prev) {
              if (status != 1) {
                localObject5 = localWNode;
              }
            }
            if ((localObject3 == localObject5) || (U.compareAndSwapObject(paramWNode1, WNEXT, localObject3, localObject3 = localObject5)))
            {
              if ((localObject3 != null) || (paramWNode1 != wtail)) {
                break;
              }
              U.compareAndSwapObject(this, WTAIL, paramWNode1, localObject2);
              break;
            }
          }
          if (next == paramWNode1) {
            U.compareAndSwapObject(localObject2, WNEXT, paramWNode1, localObject3);
          }
          if ((localObject3 != null) && ((localObject1 = thread) != null))
          {
            thread = null;
            U.unpark(localObject1);
          }
          if ((status != 1) || ((localObject4 = prev) == null)) {
            break;
          }
          prev = ((WNode)localObject4);
          U.compareAndSwapObject(localObject4, WNEXT, localObject2, localObject3);
        }
      }
    }
    while ((localObject1 = whead) != null)
    {
      if (((localObject4 = next) == null) || (status == 1)) {
        for (localObject5 = wtail; (localObject5 != null) && (localObject5 != localObject1); localObject5 = prev) {
          if (status <= 0) {
            localObject4 = localObject5;
          }
        }
      }
      if (localObject1 == whead)
      {
        long l;
        if ((localObject4 == null) || (status != 0) || (((l = state) & 0xFF) == 128L) || ((l != 0L) && (mode != 0))) {
          break;
        }
        release((WNode)localObject1);
        break;
      }
    }
    return (paramBoolean) || (Thread.interrupted()) ? 1L : 0L;
  }
  
  static
  {
    try
    {
      U = Unsafe.getUnsafe();
      Class localClass1 = StampedLock.class;
      Class localClass2 = WNode.class;
      STATE = U.objectFieldOffset(localClass1.getDeclaredField("state"));
      WHEAD = U.objectFieldOffset(localClass1.getDeclaredField("whead"));
      WTAIL = U.objectFieldOffset(localClass1.getDeclaredField("wtail"));
      WSTATUS = U.objectFieldOffset(localClass2.getDeclaredField("status"));
      WNEXT = U.objectFieldOffset(localClass2.getDeclaredField("next"));
      WCOWAIT = U.objectFieldOffset(localClass2.getDeclaredField("cowait"));
      Class localClass3 = Thread.class;
      PARKBLOCKER = U.objectFieldOffset(localClass3.getDeclaredField("parkBlocker"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  final class ReadLockView
    implements Lock
  {
    ReadLockView() {}
    
    public void lock()
    {
      readLock();
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      readLockInterruptibly();
    }
    
    public boolean tryLock()
    {
      return tryReadLock() != 0L;
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return tryReadLock(paramLong, paramTimeUnit) != 0L;
    }
    
    public void unlock()
    {
      unstampedUnlockRead();
    }
    
    public Condition newCondition()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  final class ReadWriteLockView
    implements ReadWriteLock
  {
    ReadWriteLockView() {}
    
    public Lock readLock()
    {
      return asReadLock();
    }
    
    public Lock writeLock()
    {
      return asWriteLock();
    }
  }
  
  static final class WNode
  {
    volatile WNode prev;
    volatile WNode next;
    volatile WNode cowait;
    volatile Thread thread;
    volatile int status;
    final int mode;
    
    WNode(int paramInt, WNode paramWNode)
    {
      mode = paramInt;
      prev = paramWNode;
    }
  }
  
  final class WriteLockView
    implements Lock
  {
    WriteLockView() {}
    
    public void lock()
    {
      writeLock();
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      writeLockInterruptibly();
    }
    
    public boolean tryLock()
    {
      return tryWriteLock() != 0L;
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return tryWriteLock(paramLong, paramTimeUnit) != 0L;
    }
    
    public void unlock()
    {
      unstampedUnlockWrite();
    }
    
    public Condition newCondition()
    {
      throw new UnsupportedOperationException();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\locks\StampedLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
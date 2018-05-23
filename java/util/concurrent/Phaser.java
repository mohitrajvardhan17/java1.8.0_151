package java.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class Phaser
{
  private volatile long state;
  private static final int MAX_PARTIES = 65535;
  private static final int MAX_PHASE = Integer.MAX_VALUE;
  private static final int PARTIES_SHIFT = 16;
  private static final int PHASE_SHIFT = 32;
  private static final int UNARRIVED_MASK = 65535;
  private static final long PARTIES_MASK = 4294901760L;
  private static final long COUNTS_MASK = 4294967295L;
  private static final long TERMINATION_BIT = Long.MIN_VALUE;
  private static final int ONE_ARRIVAL = 1;
  private static final int ONE_PARTY = 65536;
  private static final int ONE_DEREGISTER = 65537;
  private static final int EMPTY = 1;
  private final Phaser parent;
  private final Phaser root;
  private final AtomicReference<QNode> evenQ;
  private final AtomicReference<QNode> oddQ;
  private static final int NCPU = Runtime.getRuntime().availableProcessors();
  static final int SPINS_PER_ARRIVAL = NCPU < 2 ? 1 : 256;
  private static final Unsafe UNSAFE;
  private static final long stateOffset;
  
  private static int unarrivedOf(long paramLong)
  {
    int i = (int)paramLong;
    return i == 1 ? 0 : i & 0xFFFF;
  }
  
  private static int partiesOf(long paramLong)
  {
    return (int)paramLong >>> 16;
  }
  
  private static int phaseOf(long paramLong)
  {
    return (int)(paramLong >>> 32);
  }
  
  private static int arrivedOf(long paramLong)
  {
    int i = (int)paramLong;
    return i == 1 ? 0 : (i >>> 16) - (i & 0xFFFF);
  }
  
  private AtomicReference<QNode> queueFor(int paramInt)
  {
    return (paramInt & 0x1) == 0 ? evenQ : oddQ;
  }
  
  private String badArrive(long paramLong)
  {
    return "Attempted arrival of unregistered party for " + stateToString(paramLong);
  }
  
  private String badRegister(long paramLong)
  {
    return "Attempt to register more than 65535 parties for " + stateToString(paramLong);
  }
  
  private int doArrive(int paramInt)
  {
    Phaser localPhaser = root;
    for (;;)
    {
      long l1 = localPhaser == this ? state : reconcileState();
      int i = (int)(l1 >>> 32);
      if (i < 0) {
        return i;
      }
      int j = (int)l1;
      int k = j == 1 ? 0 : j & 0xFFFF;
      if (k <= 0) {
        throw new IllegalStateException(badArrive(l1));
      }
      if (UNSAFE.compareAndSwapLong(this, stateOffset, l1, l1 -= paramInt))
      {
        if (k == 1)
        {
          long l2 = l1 & 0xFFFF0000;
          int m = (int)l2 >>> 16;
          if (localPhaser == this)
          {
            if (onAdvance(i, m)) {
              l2 |= 0x8000000000000000;
            } else if (m == 0) {
              l2 |= 1L;
            } else {
              l2 |= m;
            }
            int n = i + 1 & 0x7FFFFFFF;
            l2 |= n << 32;
            UNSAFE.compareAndSwapLong(this, stateOffset, l1, l2);
            releaseWaiters(i);
          }
          else if (m == 0)
          {
            i = parent.doArrive(65537);
            UNSAFE.compareAndSwapLong(this, stateOffset, l1, l1 | 1L);
          }
          else
          {
            i = parent.doArrive(1);
          }
        }
        return i;
      }
    }
  }
  
  private int doRegister(int paramInt)
  {
    long l1 = paramInt << 16 | paramInt;
    Phaser localPhaser = parent;
    int i;
    for (;;)
    {
      long l2 = localPhaser == null ? state : reconcileState();
      int j = (int)l2;
      int k = j >>> 16;
      int m = j & 0xFFFF;
      if (paramInt > 65535 - k) {
        throw new IllegalStateException(badRegister(l2));
      }
      i = (int)(l2 >>> 32);
      if (i < 0) {
        break;
      }
      if (j != 1)
      {
        if ((localPhaser == null) || (reconcileState() == l2)) {
          if (m == 0) {
            root.internalAwaitAdvance(i, null);
          } else if (UNSAFE.compareAndSwapLong(this, stateOffset, l2, l2 + l1)) {
            break;
          }
        }
      }
      else if (localPhaser == null)
      {
        long l3 = i << 32 | l1;
        if (UNSAFE.compareAndSwapLong(this, stateOffset, l2, l3)) {
          break;
        }
      }
      else
      {
        synchronized (this)
        {
          if (state == l2)
          {
            i = localPhaser.doRegister(1);
            if (i < 0) {
              break;
            }
            while (!UNSAFE.compareAndSwapLong(this, stateOffset, l2, i << 32 | l1))
            {
              l2 = state;
              i = (int)(root.state >>> 32);
            }
            break;
          }
        }
      }
    }
    return i;
  }
  
  private long reconcileState()
  {
    Phaser localPhaser = root;
    long l = state;
    if (localPhaser != this)
    {
      int i;
      while ((i = (int)(state >>> 32)) != (int)(l >>> 32))
      {
        int j;
        if (UNSAFE.compareAndSwapLong(this, stateOffset, l, l = i << 32 | ((j = (int)l >>> 16) == 0 ? 1L : i < 0 ? l & 0xFFFFFFFF : l & 0xFFFF0000 | j))) {
          break;
        }
        l = state;
      }
    }
    return l;
  }
  
  public Phaser()
  {
    this(null, 0);
  }
  
  public Phaser(int paramInt)
  {
    this(null, paramInt);
  }
  
  public Phaser(Phaser paramPhaser)
  {
    this(paramPhaser, 0);
  }
  
  public Phaser(Phaser paramPhaser, int paramInt)
  {
    if (paramInt >>> 16 != 0) {
      throw new IllegalArgumentException("Illegal number of parties");
    }
    int i = 0;
    parent = paramPhaser;
    if (paramPhaser != null)
    {
      Phaser localPhaser = root;
      root = localPhaser;
      evenQ = evenQ;
      oddQ = oddQ;
      if (paramInt != 0) {
        i = paramPhaser.doRegister(1);
      }
    }
    else
    {
      root = this;
      evenQ = new AtomicReference();
      oddQ = new AtomicReference();
    }
    state = (paramInt == 0 ? 1L : i << 32 | paramInt << 16 | paramInt);
  }
  
  public int register()
  {
    return doRegister(1);
  }
  
  public int bulkRegister(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    if (paramInt == 0) {
      return getPhase();
    }
    return doRegister(paramInt);
  }
  
  public int arrive()
  {
    return doArrive(1);
  }
  
  public int arriveAndDeregister()
  {
    return doArrive(65537);
  }
  
  public int arriveAndAwaitAdvance()
  {
    Phaser localPhaser = root;
    for (;;)
    {
      long l1 = localPhaser == this ? state : reconcileState();
      int i = (int)(l1 >>> 32);
      if (i < 0) {
        return i;
      }
      int j = (int)l1;
      int k = j == 1 ? 0 : j & 0xFFFF;
      if (k <= 0) {
        throw new IllegalStateException(badArrive(l1));
      }
      if (UNSAFE.compareAndSwapLong(this, stateOffset, l1, --l1))
      {
        if (k > 1) {
          return localPhaser.internalAwaitAdvance(i, null);
        }
        if (localPhaser != this) {
          return parent.arriveAndAwaitAdvance();
        }
        long l2 = l1 & 0xFFFF0000;
        int m = (int)l2 >>> 16;
        if (onAdvance(i, m)) {
          l2 |= 0x8000000000000000;
        } else if (m == 0) {
          l2 |= 1L;
        } else {
          l2 |= m;
        }
        int n = i + 1 & 0x7FFFFFFF;
        l2 |= n << 32;
        if (!UNSAFE.compareAndSwapLong(this, stateOffset, l1, l2)) {
          return (int)(state >>> 32);
        }
        releaseWaiters(i);
        return n;
      }
    }
  }
  
  public int awaitAdvance(int paramInt)
  {
    Phaser localPhaser = root;
    long l = localPhaser == this ? state : reconcileState();
    int i = (int)(l >>> 32);
    if (paramInt < 0) {
      return paramInt;
    }
    if (i == paramInt) {
      return localPhaser.internalAwaitAdvance(paramInt, null);
    }
    return i;
  }
  
  public int awaitAdvanceInterruptibly(int paramInt)
    throws InterruptedException
  {
    Phaser localPhaser = root;
    long l = localPhaser == this ? state : reconcileState();
    int i = (int)(l >>> 32);
    if (paramInt < 0) {
      return paramInt;
    }
    if (i == paramInt)
    {
      QNode localQNode = new QNode(this, paramInt, true, false, 0L);
      i = localPhaser.internalAwaitAdvance(paramInt, localQNode);
      if (wasInterrupted) {
        throw new InterruptedException();
      }
    }
    return i;
  }
  
  public int awaitAdvanceInterruptibly(int paramInt, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, TimeoutException
  {
    long l1 = paramTimeUnit.toNanos(paramLong);
    Phaser localPhaser = root;
    long l2 = localPhaser == this ? state : reconcileState();
    int i = (int)(l2 >>> 32);
    if (paramInt < 0) {
      return paramInt;
    }
    if (i == paramInt)
    {
      QNode localQNode = new QNode(this, paramInt, true, true, l1);
      i = localPhaser.internalAwaitAdvance(paramInt, localQNode);
      if (wasInterrupted) {
        throw new InterruptedException();
      }
      if (i == paramInt) {
        throw new TimeoutException();
      }
    }
    return i;
  }
  
  public void forceTermination()
  {
    Phaser localPhaser = root;
    long l;
    while ((l = state) >= 0L) {
      if (UNSAFE.compareAndSwapLong(localPhaser, stateOffset, l, l | 0x8000000000000000))
      {
        releaseWaiters(0);
        releaseWaiters(1);
        return;
      }
    }
  }
  
  public final int getPhase()
  {
    return (int)(root.state >>> 32);
  }
  
  public int getRegisteredParties()
  {
    return partiesOf(state);
  }
  
  public int getArrivedParties()
  {
    return arrivedOf(reconcileState());
  }
  
  public int getUnarrivedParties()
  {
    return unarrivedOf(reconcileState());
  }
  
  public Phaser getParent()
  {
    return parent;
  }
  
  public Phaser getRoot()
  {
    return root;
  }
  
  public boolean isTerminated()
  {
    return root.state < 0L;
  }
  
  protected boolean onAdvance(int paramInt1, int paramInt2)
  {
    return paramInt2 == 0;
  }
  
  public String toString()
  {
    return stateToString(reconcileState());
  }
  
  private String stateToString(long paramLong)
  {
    return super.toString() + "[phase = " + phaseOf(paramLong) + " parties = " + partiesOf(paramLong) + " arrived = " + arrivedOf(paramLong) + "]";
  }
  
  private void releaseWaiters(int paramInt)
  {
    AtomicReference localAtomicReference = (paramInt & 0x1) == 0 ? evenQ : oddQ;
    QNode localQNode;
    while (((localQNode = (QNode)localAtomicReference.get()) != null) && (phase != (int)(root.state >>> 32)))
    {
      Thread localThread;
      if ((localAtomicReference.compareAndSet(localQNode, next)) && ((localThread = thread) != null))
      {
        thread = null;
        LockSupport.unpark(localThread);
      }
    }
  }
  
  private int abortWait(int paramInt)
  {
    AtomicReference localAtomicReference = (paramInt & 0x1) == 0 ? evenQ : oddQ;
    for (;;)
    {
      QNode localQNode = (QNode)localAtomicReference.get();
      int i = (int)(root.state >>> 32);
      Thread localThread;
      if ((localQNode == null) || (((localThread = thread) != null) && (phase == i))) {
        return i;
      }
      if ((localAtomicReference.compareAndSet(localQNode, next)) && (localThread != null))
      {
        thread = null;
        LockSupport.unpark(localThread);
      }
    }
  }
  
  private int internalAwaitAdvance(int paramInt, QNode paramQNode)
  {
    releaseWaiters(paramInt - 1);
    boolean bool1 = false;
    int i = 0;
    int j = SPINS_PER_ARRIVAL;
    long l;
    int k;
    while ((k = (int)((l = state) >>> 32)) == paramInt) {
      if (paramQNode == null)
      {
        int m = (int)l & 0xFFFF;
        if ((m != i) && ((i = m) < NCPU)) {
          j += SPINS_PER_ARRIVAL;
        }
        boolean bool2 = Thread.interrupted();
        if (!bool2)
        {
          j--;
          if (j >= 0) {}
        }
        else
        {
          paramQNode = new QNode(this, paramInt, false, false, 0L);
          wasInterrupted = bool2;
        }
      }
      else
      {
        if (paramQNode.isReleasable()) {
          break;
        }
        if (!bool1)
        {
          AtomicReference localAtomicReference = (paramInt & 0x1) == 0 ? evenQ : oddQ;
          QNode localQNode = next = (QNode)localAtomicReference.get();
          if (((localQNode == null) || (phase == paramInt)) && ((int)(state >>> 32) == paramInt)) {
            bool1 = localAtomicReference.compareAndSet(localQNode, paramQNode);
          }
        }
        else
        {
          try
          {
            ForkJoinPool.managedBlock(paramQNode);
          }
          catch (InterruptedException localInterruptedException)
          {
            wasInterrupted = true;
          }
        }
      }
    }
    if (paramQNode != null)
    {
      if (thread != null) {
        thread = null;
      }
      if ((wasInterrupted) && (!interruptible)) {
        Thread.currentThread().interrupt();
      }
      if ((k == paramInt) && ((k = (int)(state >>> 32)) == paramInt)) {
        return abortWait(paramInt);
      }
    }
    releaseWaiters(paramInt);
    return k;
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = Phaser.class;
      stateOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("state"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class QNode
    implements ForkJoinPool.ManagedBlocker
  {
    final Phaser phaser;
    final int phase;
    final boolean interruptible;
    final boolean timed;
    boolean wasInterrupted;
    long nanos;
    final long deadline;
    volatile Thread thread;
    QNode next;
    
    QNode(Phaser paramPhaser, int paramInt, boolean paramBoolean1, boolean paramBoolean2, long paramLong)
    {
      phaser = paramPhaser;
      phase = paramInt;
      interruptible = paramBoolean1;
      nanos = paramLong;
      timed = paramBoolean2;
      deadline = (paramBoolean2 ? System.nanoTime() + paramLong : 0L);
      thread = Thread.currentThread();
    }
    
    public boolean isReleasable()
    {
      if (thread == null) {
        return true;
      }
      if (phaser.getPhase() != phase)
      {
        thread = null;
        return true;
      }
      if (Thread.interrupted()) {
        wasInterrupted = true;
      }
      if ((wasInterrupted) && (interruptible))
      {
        thread = null;
        return true;
      }
      if (timed)
      {
        if (nanos > 0L) {
          nanos = (deadline - System.nanoTime());
        }
        if (nanos <= 0L)
        {
          thread = null;
          return true;
        }
      }
      return false;
    }
    
    public boolean block()
    {
      if (isReleasable()) {
        return true;
      }
      if (!timed) {
        LockSupport.park(this);
      } else if (nanos > 0L) {
        LockSupport.parkNanos(this, nanos);
      }
      return isReleasable();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\Phaser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
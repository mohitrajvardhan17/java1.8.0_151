package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.Contended;
import sun.misc.Unsafe;

@Contended
public class ForkJoinPool
  extends AbstractExecutorService
{
  static final int SMASK = 65535;
  static final int MAX_CAP = 32767;
  static final int EVENMASK = 65534;
  static final int SQMASK = 126;
  static final int SCANNING = 1;
  static final int INACTIVE = Integer.MIN_VALUE;
  static final int SS_SEQ = 65536;
  static final int MODE_MASK = -65536;
  static final int LIFO_QUEUE = 0;
  static final int FIFO_QUEUE = 65536;
  static final int SHARED_QUEUE = Integer.MIN_VALUE;
  public static final ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
  private static final RuntimePermission modifyThreadPermission;
  static final ForkJoinPool common;
  static final int commonParallelism;
  private static int commonMaxSpares;
  private static int poolNumberSequence;
  private static final long IDLE_TIMEOUT = 2000000000L;
  private static final long TIMEOUT_SLOP = 20000000L;
  private static final int DEFAULT_COMMON_MAX_SPARES = 256;
  private static final int SPINS = 0;
  private static final int SEED_INCREMENT = -1640531527;
  private static final long SP_MASK = 4294967295L;
  private static final long UC_MASK = -4294967296L;
  private static final int AC_SHIFT = 48;
  private static final long AC_UNIT = 281474976710656L;
  private static final long AC_MASK = -281474976710656L;
  private static final int TC_SHIFT = 32;
  private static final long TC_UNIT = 4294967296L;
  private static final long TC_MASK = 281470681743360L;
  private static final long ADD_WORKER = 140737488355328L;
  private static final int RSLOCK = 1;
  private static final int RSIGNAL = 2;
  private static final int STARTED = 4;
  private static final int STOP = 536870912;
  private static final int TERMINATED = 1073741824;
  private static final int SHUTDOWN = Integer.MIN_VALUE;
  volatile long ctl;
  volatile int runState;
  final int config;
  int indexSeed;
  volatile WorkQueue[] workQueues;
  final ForkJoinWorkerThreadFactory factory;
  final Thread.UncaughtExceptionHandler ueh;
  final String workerNamePrefix;
  volatile AtomicLong stealCounter;
  private static final Unsafe U;
  private static final int ABASE;
  private static final int ASHIFT;
  private static final long CTL;
  private static final long RUNSTATE;
  private static final long STEALCOUNTER;
  private static final long PARKBLOCKER;
  private static final long QTOP;
  private static final long QLOCK;
  private static final long QSCANSTATE;
  private static final long QPARKER;
  private static final long QCURRENTSTEAL;
  private static final long QCURRENTJOIN;
  
  private static void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(modifyThreadPermission);
    }
  }
  
  private static final synchronized int nextPoolId()
  {
    return ++poolNumberSequence;
  }
  
  private int lockRunState()
  {
    int i;
    return (((i = runState) & 0x1) != 0) || (!U.compareAndSwapInt(this, RUNSTATE, i, i |= 0x1)) ? awaitRunStateLock() : i;
  }
  
  private int awaitRunStateLock()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    for (;;)
    {
      int m;
      if (((m = runState) & 0x1) == 0)
      {
        int n;
        if (U.compareAndSwapInt(this, RUNSTATE, m, n = m | 0x1))
        {
          if (i != 0) {
            try
            {
              Thread.currentThread().interrupt();
            }
            catch (SecurityException localSecurityException) {}
          }
          return n;
        }
      }
      else if (k == 0)
      {
        k = ThreadLocalRandom.nextSecondarySeed();
      }
      else if (j > 0)
      {
        k ^= k << 6;
        k ^= k >>> 21;
        k ^= k << 7;
        if (k >= 0) {
          j--;
        }
      }
      else
      {
        AtomicLong localAtomicLong;
        if (((m & 0x4) == 0) || ((localAtomicLong = stealCounter) == null)) {
          Thread.yield();
        } else if (U.compareAndSwapInt(this, RUNSTATE, m, m | 0x2)) {
          synchronized (localAtomicLong)
          {
            if ((runState & 0x2) != 0) {
              try
              {
                localAtomicLong.wait();
              }
              catch (InterruptedException localInterruptedException)
              {
                if (!(Thread.currentThread() instanceof ForkJoinWorkerThread)) {
                  i = 1;
                }
              }
            } else {
              localAtomicLong.notifyAll();
            }
          }
        }
      }
    }
  }
  
  private void unlockRunState(int paramInt1, int paramInt2)
  {
    if (!U.compareAndSwapInt(this, RUNSTATE, paramInt1, paramInt2))
    {
      AtomicLong localAtomicLong = stealCounter;
      runState = paramInt2;
      if (localAtomicLong != null) {
        synchronized (localAtomicLong)
        {
          localAtomicLong.notifyAll();
        }
      }
    }
  }
  
  private boolean createWorker()
  {
    ForkJoinWorkerThreadFactory localForkJoinWorkerThreadFactory = factory;
    Object localObject = null;
    ForkJoinWorkerThread localForkJoinWorkerThread = null;
    try
    {
      if ((localForkJoinWorkerThreadFactory != null) && ((localForkJoinWorkerThread = localForkJoinWorkerThreadFactory.newThread(this)) != null))
      {
        localForkJoinWorkerThread.start();
        return true;
      }
    }
    catch (Throwable localThrowable)
    {
      localObject = localThrowable;
    }
    deregisterWorker(localForkJoinWorkerThread, (Throwable)localObject);
    return false;
  }
  
  private void tryAddWorker(long paramLong)
  {
    boolean bool = false;
    do
    {
      long l = 0xFFFF000000000000 & paramLong + 281474976710656L | 0xFFFF00000000 & paramLong + 4294967296L;
      if (ctl == paramLong)
      {
        int i;
        int j;
        if ((j = (i = lockRunState()) & 0x20000000) == 0) {
          bool = U.compareAndSwapLong(this, CTL, paramLong, l);
        }
        unlockRunState(i, i & 0xFFFFFFFE);
        if (j != 0) {
          break;
        }
        if (bool)
        {
          createWorker();
          break;
        }
      }
    } while ((((paramLong = ctl) & 0x800000000000) != 0L) && ((int)paramLong == 0));
  }
  
  /* Error */
  final WorkQueue registerWorker(ForkJoinWorkerThread paramForkJoinWorkerThread)
  {
    // Byte code:
    //   0: aload_1
    //   1: iconst_1
    //   2: invokevirtual 885	java/util/concurrent/ForkJoinWorkerThread:setDaemon	(Z)V
    //   5: aload_0
    //   6: getfield 755	java/util/concurrent/ForkJoinPool:ueh	Ljava/lang/Thread$UncaughtExceptionHandler;
    //   9: dup
    //   10: astore_2
    //   11: ifnull +8 -> 19
    //   14: aload_1
    //   15: aload_2
    //   16: invokevirtual 887	java/util/concurrent/ForkJoinWorkerThread:setUncaughtExceptionHandler	(Ljava/lang/Thread$UncaughtExceptionHandler;)V
    //   19: new 31	java/util/concurrent/ForkJoinPool$WorkQueue
    //   22: dup
    //   23: aload_0
    //   24: aload_1
    //   25: invokespecial 871	java/util/concurrent/ForkJoinPool$WorkQueue:<init>	(Ljava/util/concurrent/ForkJoinPool;Ljava/util/concurrent/ForkJoinWorkerThread;)V
    //   28: astore_3
    //   29: iconst_0
    //   30: istore 4
    //   32: aload_0
    //   33: getfield 738	java/util/concurrent/ForkJoinPool:config	I
    //   36: ldc 3
    //   38: iand
    //   39: istore 5
    //   41: aload_0
    //   42: invokespecial 823	java/util/concurrent/ForkJoinPool:lockRunState	()I
    //   45: istore 6
    //   47: aload_0
    //   48: getfield 759	java/util/concurrent/ForkJoinPool:workQueues	[Ljava/util/concurrent/ForkJoinPool$WorkQueue;
    //   51: dup
    //   52: astore 7
    //   54: ifnull +163 -> 217
    //   57: aload 7
    //   59: arraylength
    //   60: dup
    //   61: istore 8
    //   63: ifle +154 -> 217
    //   66: aload_0
    //   67: dup
    //   68: getfield 739	java/util/concurrent/ForkJoinPool:indexSeed	I
    //   71: ldc 2
    //   73: iadd
    //   74: dup_x1
    //   75: putfield 739	java/util/concurrent/ForkJoinPool:indexSeed	I
    //   78: istore 9
    //   80: iload 8
    //   82: iconst_1
    //   83: isub
    //   84: istore 10
    //   86: iload 9
    //   88: iconst_1
    //   89: ishl
    //   90: iconst_1
    //   91: ior
    //   92: iload 10
    //   94: iand
    //   95: istore 4
    //   97: aload 7
    //   99: iload 4
    //   101: aaload
    //   102: ifnull +88 -> 190
    //   105: iconst_0
    //   106: istore 11
    //   108: iload 8
    //   110: iconst_4
    //   111: if_icmpgt +7 -> 118
    //   114: iconst_2
    //   115: goto +12 -> 127
    //   118: iload 8
    //   120: iconst_1
    //   121: iushr
    //   122: ldc 4
    //   124: iand
    //   125: iconst_2
    //   126: iadd
    //   127: istore 12
    //   129: aload 7
    //   131: iload 4
    //   133: iload 12
    //   135: iadd
    //   136: iload 10
    //   138: iand
    //   139: dup
    //   140: istore 4
    //   142: aaload
    //   143: ifnull +47 -> 190
    //   146: iinc 11 1
    //   149: iload 11
    //   151: iload 8
    //   153: if_icmplt -24 -> 129
    //   156: aload_0
    //   157: aload 7
    //   159: iload 8
    //   161: iconst_1
    //   162: ishl
    //   163: dup
    //   164: istore 8
    //   166: invokestatic 818	java/util/Arrays:copyOf	([Ljava/lang/Object;I)[Ljava/lang/Object;
    //   169: checkcast 406	[Ljava/util/concurrent/ForkJoinPool$WorkQueue;
    //   172: dup
    //   173: astore 7
    //   175: putfield 759	java/util/concurrent/ForkJoinPool:workQueues	[Ljava/util/concurrent/ForkJoinPool$WorkQueue;
    //   178: iload 8
    //   180: iconst_1
    //   181: isub
    //   182: istore 10
    //   184: iconst_0
    //   185: istore 11
    //   187: goto -58 -> 129
    //   190: aload_3
    //   191: iload 9
    //   193: putfield 764	java/util/concurrent/ForkJoinPool$WorkQueue:hint	I
    //   196: aload_3
    //   197: iload 4
    //   199: iload 5
    //   201: ior
    //   202: putfield 763	java/util/concurrent/ForkJoinPool$WorkQueue:config	I
    //   205: aload_3
    //   206: iload 4
    //   208: putfield 767	java/util/concurrent/ForkJoinPool$WorkQueue:scanState	I
    //   211: aload 7
    //   213: iload 4
    //   215: aload_3
    //   216: aastore
    //   217: aload_0
    //   218: iload 6
    //   220: iload 6
    //   222: bipush -2
    //   224: iand
    //   225: invokespecial 830	java/util/concurrent/ForkJoinPool:unlockRunState	(II)V
    //   228: goto +19 -> 247
    //   231: astore 13
    //   233: aload_0
    //   234: iload 6
    //   236: iload 6
    //   238: bipush -2
    //   240: iand
    //   241: invokespecial 830	java/util/concurrent/ForkJoinPool:unlockRunState	(II)V
    //   244: aload 13
    //   246: athrow
    //   247: aload_1
    //   248: aload_0
    //   249: getfield 754	java/util/concurrent/ForkJoinPool:workerNamePrefix	Ljava/lang/String;
    //   252: iload 4
    //   254: iconst_1
    //   255: iushr
    //   256: invokestatic 787	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   259: invokevirtual 800	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   262: invokevirtual 886	java/util/concurrent/ForkJoinWorkerThread:setName	(Ljava/lang/String;)V
    //   265: aload_3
    //   266: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	267	0	this	ForkJoinPool
    //   0	267	1	paramForkJoinWorkerThread	ForkJoinWorkerThread
    //   10	6	2	localUncaughtExceptionHandler	Thread.UncaughtExceptionHandler
    //   28	238	3	localWorkQueue	WorkQueue
    //   30	226	4	i	int
    //   39	163	5	j	int
    //   45	196	6	k	int
    //   52	160	7	arrayOfWorkQueue	WorkQueue[]
    //   61	121	8	m	int
    //   78	114	9	n	int
    //   84	99	10	i1	int
    //   106	80	11	i2	int
    //   127	9	12	i3	int
    //   231	14	13	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   47	217	231	finally
    //   231	233	231	finally
  }
  
  final void deregisterWorker(ForkJoinWorkerThread paramForkJoinWorkerThread, Throwable paramThrowable)
  {
    WorkQueue localWorkQueue = null;
    if ((paramForkJoinWorkerThread != null) && ((localWorkQueue = workQueue) != null))
    {
      int i = config & 0xFFFF;
      int j = lockRunState();
      WorkQueue[] arrayOfWorkQueue1;
      if (((arrayOfWorkQueue1 = workQueues) != null) && (arrayOfWorkQueue1.length > i) && (arrayOfWorkQueue1[i] == localWorkQueue)) {
        arrayOfWorkQueue1[i] = null;
      }
      unlockRunState(j, j & 0xFFFFFFFE);
    }
    long l;
    while (!U.compareAndSwapLong(this, CTL, l = ctl, 0xFFFF000000000000 & l - 281474976710656L | 0xFFFF00000000 & l - 4294967296L | 0xFFFFFFFF & l)) {}
    if (localWorkQueue != null)
    {
      qlock = -1;
      localWorkQueue.transferStealCount(this);
      localWorkQueue.cancelAll();
    }
    WorkQueue[] arrayOfWorkQueue2;
    int k;
    while ((!tryTerminate(false, false)) && (localWorkQueue != null) && (array != null) && ((runState & 0x20000000) == 0) && ((arrayOfWorkQueue2 = workQueues) != null) && ((k = arrayOfWorkQueue2.length - 1) >= 0))
    {
      int m;
      if ((m = (int)(l = ctl)) != 0)
      {
        if (tryRelease(l, arrayOfWorkQueue2[(m & k)], 281474976710656L)) {
          break;
        }
      }
      else
      {
        if ((paramThrowable == null) || ((l & 0x800000000000) == 0L)) {
          break;
        }
        tryAddWorker(l);
        break;
      }
    }
    if (paramThrowable == null) {
      ForkJoinTask.helpExpungeStaleExceptions();
    } else {
      ForkJoinTask.rethrow(paramThrowable);
    }
  }
  
  final void signalWork(WorkQueue[] paramArrayOfWorkQueue, WorkQueue paramWorkQueue)
  {
    long l1;
    while ((l1 = ctl) < 0L)
    {
      int i;
      if ((i = (int)l1) == 0)
      {
        if ((l1 & 0x800000000000) != 0L) {
          tryAddWorker(l1);
        }
      }
      else
      {
        int j;
        WorkQueue localWorkQueue;
        if ((paramArrayOfWorkQueue != null) && (paramArrayOfWorkQueue.length > (j = i & 0xFFFF)) && ((localWorkQueue = paramArrayOfWorkQueue[j]) != null))
        {
          int k = i + 65536 & 0x7FFFFFFF;
          int m = i - scanState;
          long l2 = 0xFFFFFFFF00000000 & l1 + 281474976710656L | 0xFFFFFFFF & stackPred;
          if ((m == 0) && (U.compareAndSwapLong(this, CTL, l1, l2)))
          {
            scanState = k;
            Thread localThread;
            if ((localThread = parker) != null) {
              U.unpark(localThread);
            }
          }
          else
          {
            if ((paramWorkQueue != null) && (base == top)) {
              break;
            }
          }
        }
      }
    }
  }
  
  private boolean tryRelease(long paramLong1, WorkQueue paramWorkQueue, long paramLong2)
  {
    int i = (int)paramLong1;
    int j = i + 65536 & 0x7FFFFFFF;
    if ((paramWorkQueue != null) && (scanState == i))
    {
      long l = 0xFFFFFFFF00000000 & paramLong1 + paramLong2 | 0xFFFFFFFF & stackPred;
      if (U.compareAndSwapLong(this, CTL, paramLong1, l))
      {
        scanState = j;
        Thread localThread;
        if ((localThread = parker) != null) {
          U.unpark(localThread);
        }
        return true;
      }
    }
    return false;
  }
  
  final void runWorker(WorkQueue paramWorkQueue)
  {
    paramWorkQueue.growArray();
    int i = hint;
    int j = i == 0 ? 1 : i;
    for (;;)
    {
      ForkJoinTask localForkJoinTask;
      if ((localForkJoinTask = scan(paramWorkQueue, j)) != null) {
        paramWorkQueue.runTask(localForkJoinTask);
      } else {
        if (!awaitWork(paramWorkQueue, j)) {
          break;
        }
      }
      j ^= j << 13;
      j ^= j >>> 17;
      j ^= j << 5;
    }
  }
  
  private ForkJoinTask<?> scan(WorkQueue paramWorkQueue, int paramInt)
  {
    WorkQueue[] arrayOfWorkQueue;
    int i;
    if (((arrayOfWorkQueue = workQueues) != null) && ((i = arrayOfWorkQueue.length - 1) > 0) && (paramWorkQueue != null))
    {
      int j = scanState;
      int k = paramInt & i;
      int m = k;
      int n = 0;
      int i1 = 0;
      for (;;)
      {
        WorkQueue localWorkQueue;
        long l1;
        if ((localWorkQueue = arrayOfWorkQueue[m]) != null)
        {
          int i2;
          int i3;
          ForkJoinTask[] arrayOfForkJoinTask;
          if (((i3 = (i2 = base) - top) < 0) && ((arrayOfForkJoinTask = array) != null))
          {
            long l2 = ((arrayOfForkJoinTask.length - 1 & i2) << ASHIFT) + ABASE;
            ForkJoinTask localForkJoinTask;
            if (((localForkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, l2)) != null) && (base == i2)) {
              if (j >= 0)
              {
                if (U.compareAndSwapObject(arrayOfForkJoinTask, l2, localForkJoinTask, null))
                {
                  base = (i2 + 1);
                  if (i3 < -1) {
                    signalWork(arrayOfWorkQueue, localWorkQueue);
                  }
                  return localForkJoinTask;
                }
              }
              else if ((n == 0) && (scanState < 0)) {
                tryRelease(l1 = ctl, arrayOfWorkQueue[(i & (int)l1)], 281474976710656L);
              }
            }
            if (j < 0) {
              j = scanState;
            }
            paramInt ^= paramInt << 1;
            paramInt ^= paramInt >>> 3;
            paramInt ^= paramInt << 10;
            k = m = paramInt & i;
            n = i1 = 0;
          }
          else
          {
            i1 += i2;
          }
        }
        else if ((m = m + 1 & i) == k)
        {
          if (((j >= 0) || (j == (j = scanState))) && (n == (n = i1)))
          {
            if ((j < 0) || (qlock < 0)) {
              break;
            }
            int i4 = j | 0x80000000;
            long l3 = 0xFFFFFFFF & i4 | 0xFFFFFFFF00000000 & (l1 = ctl) - 281474976710656L;
            stackPred = ((int)l1);
            U.putInt(paramWorkQueue, QSCANSTATE, i4);
            if (U.compareAndSwapLong(this, CTL, l1, l3)) {
              j = i4;
            } else {
              scanState = j;
            }
          }
          i1 = 0;
        }
      }
    }
    return null;
  }
  
  private boolean awaitWork(WorkQueue paramWorkQueue, int paramInt)
  {
    if ((paramWorkQueue == null) || (qlock < 0)) {
      return false;
    }
    int i = stackPred;
    int j = 0;
    int k;
    while ((k = scanState) < 0) {
      if (j > 0)
      {
        paramInt ^= paramInt << 6;
        paramInt ^= paramInt >>> 21;
        paramInt ^= paramInt << 7;
        if (paramInt >= 0)
        {
          j--;
          if (j == 0)
          {
            WorkQueue[] arrayOfWorkQueue;
            int m;
            WorkQueue localWorkQueue;
            if ((i != 0) && ((arrayOfWorkQueue = workQueues) != null) && ((m = i & 0xFFFF) < arrayOfWorkQueue.length) && ((localWorkQueue = arrayOfWorkQueue[m]) != null) && ((parker == null) || (scanState >= 0))) {
              j = 0;
            }
          }
        }
      }
      else
      {
        if (qlock < 0) {
          return false;
        }
        if (!Thread.interrupted())
        {
          long l1;
          int n = (int)((l1 = ctl) >> 48) + (config & 0xFFFF);
          if (((n <= 0) && (tryTerminate(false, false))) || ((runState & 0x20000000) != 0)) {
            return false;
          }
          long l2;
          long l3;
          long l4;
          if ((n <= 0) && (k == (int)l1))
          {
            l2 = 0xFFFFFFFF00000000 & l1 + 281474976710656L | 0xFFFFFFFF & i;
            int i1 = (short)(int)(l1 >>> 32);
            if ((i1 > 2) && (U.compareAndSwapLong(this, CTL, l1, l2))) {
              return false;
            }
            l3 = 2000000000L * (i1 >= 0 ? 1 : 1 - i1);
            l4 = System.nanoTime() + l3 - 20000000L;
          }
          else
          {
            l2 = l3 = l4 = 0L;
          }
          Thread localThread = Thread.currentThread();
          U.putObject(localThread, PARKBLOCKER, this);
          parker = localThread;
          if ((scanState < 0) && (ctl == l1)) {
            U.park(false, l3);
          }
          U.putOrderedObject(paramWorkQueue, QPARKER, null);
          U.putObject(localThread, PARKBLOCKER, null);
          if (scanState >= 0) {
            break;
          }
          if ((l3 != 0L) && (ctl == l1) && (l4 - System.nanoTime() <= 0L) && (U.compareAndSwapLong(this, CTL, l1, l2))) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  final int helpComplete(WorkQueue paramWorkQueue, CountedCompleter<?> paramCountedCompleter, int paramInt)
  {
    int i = 0;
    WorkQueue[] arrayOfWorkQueue;
    int j;
    if (((arrayOfWorkQueue = workQueues) != null) && ((j = arrayOfWorkQueue.length - 1) >= 0) && (paramCountedCompleter != null) && (paramWorkQueue != null))
    {
      int k = config;
      int m = hint ^ top;
      int n = m & j;
      int i1 = 1;
      int i2 = n;
      int i3 = 0;
      int i4 = 0;
      while ((i = status) >= 0)
      {
        CountedCompleter localCountedCompleter;
        if ((i1 == 1) && ((localCountedCompleter = paramWorkQueue.popCC(paramCountedCompleter, k)) != null))
        {
          localCountedCompleter.doExec();
          if (paramInt != 0)
          {
            paramInt--;
            if (paramInt == 0) {
              break;
            }
          }
          n = i2;
          i3 = i4 = 0;
        }
        else
        {
          WorkQueue localWorkQueue;
          if ((localWorkQueue = arrayOfWorkQueue[i2]) == null) {
            i1 = 0;
          } else if ((i1 = localWorkQueue.pollAndExecCC(paramCountedCompleter)) < 0) {
            i4 += i1;
          }
          if (i1 > 0)
          {
            if ((i1 == 1) && (paramInt != 0))
            {
              paramInt--;
              if (paramInt == 0) {
                break;
              }
            }
            m ^= m << 13;
            m ^= m >>> 17;
            m ^= m << 5;
            n = i2 = m & j;
            i3 = i4 = 0;
          }
          else if ((i2 = i2 + 1 & j) == n)
          {
            if (i3 == (i3 = i4)) {
              break;
            }
            i4 = 0;
          }
        }
      }
    }
    return i;
  }
  
  private void helpStealer(WorkQueue paramWorkQueue, ForkJoinTask<?> paramForkJoinTask)
  {
    WorkQueue[] arrayOfWorkQueue = workQueues;
    int i = 0;
    int k;
    if ((arrayOfWorkQueue != null) && ((k = arrayOfWorkQueue.length - 1) >= 0) && (paramWorkQueue != null) && (paramForkJoinTask != null))
    {
      int j;
      label376:
      do
      {
        j = 0;
        Object localObject2 = paramWorkQueue;
        Object localObject1 = paramForkJoinTask;
        if (status >= 0)
        {
          int m = hint | 0x1;
          int n = 0;
          while (n <= k)
          {
            int i1;
            WorkQueue localWorkQueue;
            if ((localWorkQueue = arrayOfWorkQueue[(i1 = m + n & k)]) != null)
            {
              if (currentSteal == localObject1) {
                hint = i1;
              } else {
                j += base;
              }
            }
            else
            {
              n += 2;
              continue;
            }
            for (;;)
            {
              j += (n = base);
              ForkJoinTask localForkJoinTask1 = currentJoin;
              if ((status < 0) || (currentJoin != localObject1) || (currentSteal != localObject1)) {
                break label376;
              }
              ForkJoinTask[] arrayOfForkJoinTask;
              if ((n - top >= 0) || ((arrayOfForkJoinTask = array) == null))
              {
                if ((localObject1 = localForkJoinTask1) == null) {
                  break label376;
                }
                localObject2 = localWorkQueue;
                break;
              }
              int i2 = ((arrayOfForkJoinTask.length - 1 & n) << ASHIFT) + ABASE;
              ForkJoinTask localForkJoinTask2 = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, i2);
              if (base == n)
              {
                if (localForkJoinTask2 == null) {
                  break label376;
                }
                if (U.compareAndSwapObject(arrayOfForkJoinTask, i2, localForkJoinTask2, null))
                {
                  base = (n + 1);
                  ForkJoinTask localForkJoinTask3 = currentSteal;
                  int i3 = top;
                  do
                  {
                    U.putOrderedObject(paramWorkQueue, QCURRENTSTEAL, localForkJoinTask2);
                    localForkJoinTask2.doExec();
                  } while ((status >= 0) && (top != i3) && ((localForkJoinTask2 = paramWorkQueue.pop()) != null));
                  U.putOrderedObject(paramWorkQueue, QCURRENTSTEAL, localForkJoinTask3);
                  if (base != top) {
                    return;
                  }
                }
              }
            }
          }
        }
      } while ((status >= 0) && (i != (i = j)));
    }
  }
  
  private boolean tryCompensate(WorkQueue paramWorkQueue)
  {
    WorkQueue[] arrayOfWorkQueue;
    int i;
    int j;
    boolean bool1;
    if ((paramWorkQueue == null) || (qlock < 0) || ((arrayOfWorkQueue = workQueues) == null) || ((i = arrayOfWorkQueue.length - 1) <= 0) || ((j = config & 0xFFFF) == 0))
    {
      bool1 = false;
    }
    else
    {
      long l1;
      int k;
      if ((k = (int)(l1 = ctl)) != 0)
      {
        bool1 = tryRelease(l1, arrayOfWorkQueue[(k & i)], 0L);
      }
      else
      {
        int m = (int)(l1 >> 48) + j;
        int n = (short)(int)(l1 >> 32) + j;
        int i1 = 0;
        for (int i2 = 0; i2 <= i; i2++)
        {
          WorkQueue localWorkQueue;
          if ((localWorkQueue = arrayOfWorkQueue[((i2 << 1 | 0x1) & i)]) != null)
          {
            if ((scanState & 0x1) != 0) {
              break;
            }
            i1++;
          }
        }
        if ((i1 != n << 1) || (ctl != l1))
        {
          bool1 = false;
        }
        else if ((n >= j) && (m > 1) && (paramWorkQueue.isEmpty()))
        {
          long l2 = 0xFFFF000000000000 & l1 - 281474976710656L | 0xFFFFFFFFFFFF & l1;
          bool1 = U.compareAndSwapLong(this, CTL, l1, l2);
        }
        else
        {
          if ((n >= 32767) || ((this == common) && (n >= j + commonMaxSpares))) {
            throw new RejectedExecutionException("Thread limit exceeded replacing blocked worker");
          }
          boolean bool2 = false;
          long l3 = 0xFFFF000000000000 & l1 | 0xFFFF00000000 & l1 + 4294967296L;
          int i3;
          if (((i3 = lockRunState()) & 0x20000000) == 0) {
            bool2 = U.compareAndSwapLong(this, CTL, l1, l3);
          }
          unlockRunState(i3, i3 & 0xFFFFFFFE);
          bool1 = (bool2) && (createWorker());
        }
      }
    }
    return bool1;
  }
  
  final int awaitJoin(WorkQueue paramWorkQueue, ForkJoinTask<?> paramForkJoinTask, long paramLong)
  {
    int i = 0;
    if ((paramForkJoinTask != null) && (paramWorkQueue != null))
    {
      ForkJoinTask localForkJoinTask = currentJoin;
      U.putOrderedObject(paramWorkQueue, QCURRENTJOIN, paramForkJoinTask);
      CountedCompleter localCountedCompleter = (paramForkJoinTask instanceof CountedCompleter) ? (CountedCompleter)paramForkJoinTask : null;
      while ((i = status) >= 0)
      {
        if (localCountedCompleter != null) {
          helpComplete(paramWorkQueue, localCountedCompleter, 0);
        } else if ((base == top) || (paramWorkQueue.tryRemoveAndExec(paramForkJoinTask))) {
          helpStealer(paramWorkQueue, paramForkJoinTask);
        }
        if ((i = status) < 0) {
          break;
        }
        long l1;
        if (paramLong == 0L)
        {
          l1 = 0L;
        }
        else
        {
          long l2;
          if ((l2 = paramLong - System.nanoTime()) <= 0L) {
            break;
          }
          if ((l1 = TimeUnit.NANOSECONDS.toMillis(l2)) <= 0L) {
            l1 = 1L;
          }
        }
        if (tryCompensate(paramWorkQueue))
        {
          paramForkJoinTask.internalWait(l1);
          U.getAndAddLong(this, CTL, 281474976710656L);
        }
      }
      U.putOrderedObject(paramWorkQueue, QCURRENTJOIN, localForkJoinTask);
    }
    return i;
  }
  
  private WorkQueue findNonEmptyStealQueue()
  {
    int j = ThreadLocalRandom.nextSecondarySeed();
    WorkQueue[] arrayOfWorkQueue;
    int i;
    if (((arrayOfWorkQueue = workQueues) != null) && ((i = arrayOfWorkQueue.length - 1) >= 0))
    {
      int k = j & i;
      int m = k;
      int n = 0;
      int i1 = 0;
      for (;;)
      {
        WorkQueue localWorkQueue;
        if ((localWorkQueue = arrayOfWorkQueue[m]) != null)
        {
          int i2;
          if ((i2 = base) - top < 0) {
            return localWorkQueue;
          }
          i1 += i2;
        }
        if ((m = m + 1 & i) == k)
        {
          if (n == (n = i1)) {
            break;
          }
          i1 = 0;
        }
      }
    }
    return null;
  }
  
  final void helpQuiescePool(WorkQueue paramWorkQueue)
  {
    ForkJoinTask localForkJoinTask1 = currentSteal;
    int i = 1;
    for (;;)
    {
      paramWorkQueue.execLocalTasks();
      WorkQueue localWorkQueue;
      if ((localWorkQueue = findNonEmptyStealQueue()) != null)
      {
        if (i == 0)
        {
          i = 1;
          U.getAndAddLong(this, CTL, 281474976710656L);
        }
        int j;
        ForkJoinTask localForkJoinTask2;
        if (((j = base) - top < 0) && ((localForkJoinTask2 = localWorkQueue.pollAt(j)) != null))
        {
          U.putOrderedObject(paramWorkQueue, QCURRENTSTEAL, localForkJoinTask2);
          localForkJoinTask2.doExec();
          if (++nsteals < 0) {
            paramWorkQueue.transferStealCount(this);
          }
        }
      }
      else
      {
        long l1;
        if (i != 0)
        {
          long l2 = 0xFFFF000000000000 & (l1 = ctl) - 281474976710656L | 0xFFFFFFFFFFFF & l1;
          if ((int)(l2 >> 48) + (config & 0xFFFF) <= 0) {
            break;
          }
          if (U.compareAndSwapLong(this, CTL, l1, l2)) {
            i = 0;
          }
        }
        else
        {
          if (((int)((l1 = ctl) >> 48) + (config & 0xFFFF) <= 0) && (U.compareAndSwapLong(this, CTL, l1, l1 + 281474976710656L))) {
            break;
          }
        }
      }
    }
    U.putOrderedObject(paramWorkQueue, QCURRENTSTEAL, localForkJoinTask1);
  }
  
  final ForkJoinTask<?> nextTaskFor(WorkQueue paramWorkQueue)
  {
    for (;;)
    {
      ForkJoinTask localForkJoinTask;
      if ((localForkJoinTask = paramWorkQueue.nextLocalTask()) != null) {
        return localForkJoinTask;
      }
      WorkQueue localWorkQueue;
      if ((localWorkQueue = findNonEmptyStealQueue()) == null) {
        return null;
      }
      int i;
      if (((i = base) - top < 0) && ((localForkJoinTask = localWorkQueue.pollAt(i)) != null)) {
        return localForkJoinTask;
      }
    }
  }
  
  static int getSurplusQueuedTaskCount()
  {
    Thread localThread;
    if (((localThread = Thread.currentThread()) instanceof ForkJoinWorkerThread))
    {
      ForkJoinWorkerThread localForkJoinWorkerThread;
      ForkJoinPool localForkJoinPool;
      int i = pool).config & 0xFFFF;
      WorkQueue localWorkQueue;
      int j = workQueue).top - base;
      int k = (int)(ctl >> 48) + i;
      return j - (k > i >>>= 1 ? 4 : k > i >>>= 1 ? 2 : k > i >>>= 1 ? 1 : k > i >>>= 1 ? 0 : 8);
    }
    return 0;
  }
  
  private boolean tryTerminate(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this == common) {
      return false;
    }
    int i;
    if ((i = runState) >= 0)
    {
      if (!paramBoolean2) {
        return false;
      }
      i = lockRunState();
      unlockRunState(i, i & 0xFFFFFFFE | 0x80000000);
    }
    Object localObject2;
    if ((i & 0x20000000) == 0)
    {
      if (!paramBoolean1)
      {
        long l1 = 0L;
        for (;;)
        {
          long l5 = ctl;
          if ((int)(l5 >> 48) + (config & 0xFFFF) > 0) {
            return false;
          }
          WorkQueue[] arrayOfWorkQueue;
          int j;
          if (((arrayOfWorkQueue = workQueues) == null) || ((j = arrayOfWorkQueue.length - 1) <= 0)) {
            break;
          }
          for (int m = 0; m <= j; m++) {
            if ((localObject2 = arrayOfWorkQueue[m]) != null)
            {
              int k;
              if (((k = base) != top) || (scanState >= 0) || (currentSteal != null))
              {
                long l3;
                tryRelease(l3 = ctl, arrayOfWorkQueue[(j & (int)l3)], 281474976710656L);
                return false;
              }
              l5 += k;
              if ((m & 0x1) == 0) {
                qlock = -1;
              }
            }
          }
          if (l1 == (l1 = l5)) {
            break;
          }
        }
      }
      if ((runState & 0x20000000) == 0)
      {
        i = lockRunState();
        unlockRunState(i, i & 0xFFFFFFFE | 0x20000000);
      }
    }
    Object localObject1 = 0;
    long l2 = 0L;
    for (;;)
    {
      long l4 = ctl;
      Object localObject4;
      if (((short)(int)(l4 >>> 32) + (config & 0xFFFF) <= 0) || ((localObject2 = workQueues) == null) || ((localObject4 = localObject2.length - 1) <= 0))
      {
        if ((runState & 0x40000000) != 0) {
          break;
        }
        i = lockRunState();
        unlockRunState(i, i & 0xFFFFFFFE | 0x40000000);
        synchronized (this)
        {
          notifyAll();
        }
        break;
      }
      for (??? = 0; ??? <= localObject4; ???++)
      {
        Object localObject3;
        if ((localObject3 = localObject2[???]) != null)
        {
          l4 += base;
          qlock = -1;
          if (localObject1 > 0)
          {
            ((WorkQueue)localObject3).cancelAll();
            ForkJoinWorkerThread localForkJoinWorkerThread;
            if ((localObject1 > 1) && ((localForkJoinWorkerThread = owner) != null))
            {
              if (!localForkJoinWorkerThread.isInterrupted()) {
                try
                {
                  localForkJoinWorkerThread.interrupt();
                }
                catch (Throwable localThrowable) {}
              }
              if (scanState < 0) {
                U.unpark(localForkJoinWorkerThread);
              }
            }
          }
        }
      }
      if (l4 != l2)
      {
        l2 = l4;
        localObject1 = 0;
      }
      else
      {
        if ((localObject1 > 3) && (localObject1 > localObject4)) {
          break;
        }
        localObject1++;
        if (localObject1 > 1)
        {
          int n = 0;
          long l6;
          Object localObject6;
          while ((n++ <= localObject4) && ((localObject6 = (int)(l6 = ctl)) != 0)) {
            tryRelease(l6, localObject2[(localObject6 & localObject4)], 281474976710656L);
          }
        }
      }
    }
    return true;
  }
  
  /* Error */
  private void externalSubmit(ForkJoinTask<?> paramForkJoinTask)
  {
    // Byte code:
    //   0: invokestatic 890	java/util/concurrent/ThreadLocalRandom:getProbe	()I
    //   3: dup
    //   4: istore_2
    //   5: ifne +10 -> 15
    //   8: invokestatic 892	java/util/concurrent/ThreadLocalRandom:localInit	()V
    //   11: invokestatic 890	java/util/concurrent/ThreadLocalRandom:getProbe	()I
    //   14: istore_2
    //   15: iconst_0
    //   16: istore 8
    //   18: aload_0
    //   19: getfield 741	java/util/concurrent/ForkJoinPool:runState	I
    //   22: dup
    //   23: istore 5
    //   25: ifge +18 -> 43
    //   28: aload_0
    //   29: iconst_0
    //   30: iconst_0
    //   31: invokespecial 832	java/util/concurrent/ForkJoinPool:tryTerminate	(ZZ)Z
    //   34: pop
    //   35: new 451	java/util/concurrent/RejectedExecutionException
    //   38: dup
    //   39: invokespecial 888	java/util/concurrent/RejectedExecutionException:<init>	()V
    //   42: athrow
    //   43: iload 5
    //   45: iconst_4
    //   46: iand
    //   47: ifeq +22 -> 69
    //   50: aload_0
    //   51: getfield 759	java/util/concurrent/ForkJoinPool:workQueues	[Ljava/util/concurrent/ForkJoinPool$WorkQueue;
    //   54: dup
    //   55: astore_3
    //   56: ifnull +13 -> 69
    //   59: aload_3
    //   60: arraylength
    //   61: iconst_1
    //   62: isub
    //   63: dup
    //   64: istore 6
    //   66: ifge +169 -> 235
    //   69: iconst_0
    //   70: istore 9
    //   72: aload_0
    //   73: invokespecial 823	java/util/concurrent/ForkJoinPool:lockRunState	()I
    //   76: istore 5
    //   78: iload 5
    //   80: iconst_4
    //   81: iand
    //   82: ifne +114 -> 196
    //   85: getstatic 761	java/util/concurrent/ForkJoinPool:U	Lsun/misc/Unsafe;
    //   88: aload_0
    //   89: getstatic 751	java/util/concurrent/ForkJoinPool:STEALCOUNTER	J
    //   92: aconst_null
    //   93: new 454	java/util/concurrent/atomic/AtomicLong
    //   96: dup
    //   97: invokespecial 897	java/util/concurrent/atomic/AtomicLong:<init>	()V
    //   100: invokevirtual 914	sun/misc/Unsafe:compareAndSwapObject	(Ljava/lang/Object;JLjava/lang/Object;Ljava/lang/Object;)Z
    //   103: pop
    //   104: aload_0
    //   105: getfield 738	java/util/concurrent/ForkJoinPool:config	I
    //   108: ldc 5
    //   110: iand
    //   111: istore 10
    //   113: iload 10
    //   115: iconst_1
    //   116: if_icmple +10 -> 126
    //   119: iload 10
    //   121: iconst_1
    //   122: isub
    //   123: goto +4 -> 127
    //   126: iconst_1
    //   127: istore 11
    //   129: iload 11
    //   131: iload 11
    //   133: iconst_1
    //   134: iushr
    //   135: ior
    //   136: istore 11
    //   138: iload 11
    //   140: iload 11
    //   142: iconst_2
    //   143: iushr
    //   144: ior
    //   145: istore 11
    //   147: iload 11
    //   149: iload 11
    //   151: iconst_4
    //   152: iushr
    //   153: ior
    //   154: istore 11
    //   156: iload 11
    //   158: iload 11
    //   160: bipush 8
    //   162: iushr
    //   163: ior
    //   164: istore 11
    //   166: iload 11
    //   168: iload 11
    //   170: bipush 16
    //   172: iushr
    //   173: ior
    //   174: istore 11
    //   176: iload 11
    //   178: iconst_1
    //   179: iadd
    //   180: iconst_1
    //   181: ishl
    //   182: istore 11
    //   184: aload_0
    //   185: iload 11
    //   187: anewarray 31	java/util/concurrent/ForkJoinPool$WorkQueue
    //   190: putfield 759	java/util/concurrent/ForkJoinPool:workQueues	[Ljava/util/concurrent/ForkJoinPool$WorkQueue;
    //   193: iconst_4
    //   194: istore 9
    //   196: aload_0
    //   197: iload 5
    //   199: iload 5
    //   201: bipush -2
    //   203: iand
    //   204: iload 9
    //   206: ior
    //   207: invokespecial 830	java/util/concurrent/ForkJoinPool:unlockRunState	(II)V
    //   210: goto +22 -> 232
    //   213: astore 12
    //   215: aload_0
    //   216: iload 5
    //   218: iload 5
    //   220: bipush -2
    //   222: iand
    //   223: iload 9
    //   225: ior
    //   226: invokespecial 830	java/util/concurrent/ForkJoinPool:unlockRunState	(II)V
    //   229: aload 12
    //   231: athrow
    //   232: goto +300 -> 532
    //   235: aload_3
    //   236: iload_2
    //   237: iload 6
    //   239: iand
    //   240: bipush 126
    //   242: iand
    //   243: dup
    //   244: istore 7
    //   246: aaload
    //   247: dup
    //   248: astore 4
    //   250: ifnull +179 -> 429
    //   253: aload 4
    //   255: getfield 766	java/util/concurrent/ForkJoinPool$WorkQueue:qlock	I
    //   258: ifne +165 -> 423
    //   261: getstatic 761	java/util/concurrent/ForkJoinPool:U	Lsun/misc/Unsafe;
    //   264: aload 4
    //   266: getstatic 746	java/util/concurrent/ForkJoinPool:QLOCK	J
    //   269: iconst_0
    //   270: iconst_1
    //   271: invokevirtual 905	sun/misc/Unsafe:compareAndSwapInt	(Ljava/lang/Object;JII)Z
    //   274: ifeq +149 -> 423
    //   277: aload 4
    //   279: getfield 773	java/util/concurrent/ForkJoinPool$WorkQueue:array	[Ljava/util/concurrent/ForkJoinTask;
    //   282: astore 9
    //   284: aload 4
    //   286: getfield 769	java/util/concurrent/ForkJoinPool$WorkQueue:top	I
    //   289: istore 10
    //   291: iconst_0
    //   292: istore 11
    //   294: aload 9
    //   296: ifnull +19 -> 315
    //   299: aload 9
    //   301: arraylength
    //   302: iload 10
    //   304: iconst_1
    //   305: iadd
    //   306: aload 4
    //   308: getfield 762	java/util/concurrent/ForkJoinPool$WorkQueue:base	I
    //   311: isub
    //   312: if_icmpgt +14 -> 326
    //   315: aload 4
    //   317: invokevirtual 866	java/util/concurrent/ForkJoinPool$WorkQueue:growArray	()[Ljava/util/concurrent/ForkJoinTask;
    //   320: dup
    //   321: astore 9
    //   323: ifnull +51 -> 374
    //   326: aload 9
    //   328: arraylength
    //   329: iconst_1
    //   330: isub
    //   331: iload 10
    //   333: iand
    //   334: getstatic 735	java/util/concurrent/ForkJoinPool:ASHIFT	I
    //   337: ishl
    //   338: getstatic 734	java/util/concurrent/ForkJoinPool:ABASE	I
    //   341: iadd
    //   342: istore 12
    //   344: getstatic 761	java/util/concurrent/ForkJoinPool:U	Lsun/misc/Unsafe;
    //   347: aload 9
    //   349: iload 12
    //   351: i2l
    //   352: aload_1
    //   353: invokevirtual 913	sun/misc/Unsafe:putOrderedObject	(Ljava/lang/Object;JLjava/lang/Object;)V
    //   356: getstatic 761	java/util/concurrent/ForkJoinPool:U	Lsun/misc/Unsafe;
    //   359: aload 4
    //   361: getstatic 749	java/util/concurrent/ForkJoinPool:QTOP	J
    //   364: iload 10
    //   366: iconst_1
    //   367: iadd
    //   368: invokevirtual 904	sun/misc/Unsafe:putOrderedInt	(Ljava/lang/Object;JI)V
    //   371: iconst_1
    //   372: istore 11
    //   374: getstatic 761	java/util/concurrent/ForkJoinPool:U	Lsun/misc/Unsafe;
    //   377: aload 4
    //   379: getstatic 746	java/util/concurrent/ForkJoinPool:QLOCK	J
    //   382: iconst_1
    //   383: iconst_0
    //   384: invokevirtual 905	sun/misc/Unsafe:compareAndSwapInt	(Ljava/lang/Object;JII)Z
    //   387: pop
    //   388: goto +22 -> 410
    //   391: astore 13
    //   393: getstatic 761	java/util/concurrent/ForkJoinPool:U	Lsun/misc/Unsafe;
    //   396: aload 4
    //   398: getstatic 746	java/util/concurrent/ForkJoinPool:QLOCK	J
    //   401: iconst_1
    //   402: iconst_0
    //   403: invokevirtual 905	sun/misc/Unsafe:compareAndSwapInt	(Ljava/lang/Object;JII)Z
    //   406: pop
    //   407: aload 13
    //   409: athrow
    //   410: iload 11
    //   412: ifeq +11 -> 423
    //   415: aload_0
    //   416: aload_3
    //   417: aload 4
    //   419: invokevirtual 846	java/util/concurrent/ForkJoinPool:signalWork	([Ljava/util/concurrent/ForkJoinPool$WorkQueue;Ljava/util/concurrent/ForkJoinPool$WorkQueue;)V
    //   422: return
    //   423: iconst_1
    //   424: istore 8
    //   426: goto +106 -> 532
    //   429: aload_0
    //   430: getfield 741	java/util/concurrent/ForkJoinPool:runState	I
    //   433: dup
    //   434: istore 5
    //   436: iconst_1
    //   437: iand
    //   438: ifne +91 -> 529
    //   441: new 31	java/util/concurrent/ForkJoinPool$WorkQueue
    //   444: dup
    //   445: aload_0
    //   446: aconst_null
    //   447: invokespecial 871	java/util/concurrent/ForkJoinPool$WorkQueue:<init>	(Ljava/util/concurrent/ForkJoinPool;Ljava/util/concurrent/ForkJoinWorkerThread;)V
    //   450: astore 4
    //   452: aload 4
    //   454: iload_2
    //   455: putfield 764	java/util/concurrent/ForkJoinPool$WorkQueue:hint	I
    //   458: aload 4
    //   460: iload 7
    //   462: ldc 1
    //   464: ior
    //   465: putfield 763	java/util/concurrent/ForkJoinPool$WorkQueue:config	I
    //   468: aload 4
    //   470: ldc 1
    //   472: putfield 767	java/util/concurrent/ForkJoinPool$WorkQueue:scanState	I
    //   475: aload_0
    //   476: invokespecial 823	java/util/concurrent/ForkJoinPool:lockRunState	()I
    //   479: istore 5
    //   481: iload 5
    //   483: ifle +32 -> 515
    //   486: aload_0
    //   487: getfield 759	java/util/concurrent/ForkJoinPool:workQueues	[Ljava/util/concurrent/ForkJoinPool$WorkQueue;
    //   490: dup
    //   491: astore_3
    //   492: ifnull +23 -> 515
    //   495: iload 7
    //   497: aload_3
    //   498: arraylength
    //   499: if_icmpge +16 -> 515
    //   502: aload_3
    //   503: iload 7
    //   505: aaload
    //   506: ifnonnull +9 -> 515
    //   509: aload_3
    //   510: iload 7
    //   512: aload 4
    //   514: aastore
    //   515: aload_0
    //   516: iload 5
    //   518: iload 5
    //   520: bipush -2
    //   522: iand
    //   523: invokespecial 830	java/util/concurrent/ForkJoinPool:unlockRunState	(II)V
    //   526: goto +6 -> 532
    //   529: iconst_1
    //   530: istore 8
    //   532: iload 8
    //   534: ifeq +8 -> 542
    //   537: iload_2
    //   538: invokestatic 893	java/util/concurrent/ThreadLocalRandom:advanceProbe	(I)I
    //   541: istore_2
    //   542: goto -527 -> 15
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	545	0	this	ForkJoinPool
    //   0	545	1	paramForkJoinTask	ForkJoinTask<?>
    //   4	538	2	i	int
    //   55	455	3	arrayOfWorkQueue	WorkQueue[]
    //   248	265	4	localWorkQueue	WorkQueue
    //   23	500	5	j	int
    //   64	176	6	k	int
    //   244	267	7	m	int
    //   16	517	8	n	int
    //   70	156	9	i1	int
    //   282	66	9	arrayOfForkJoinTask	ForkJoinTask[]
    //   111	257	10	i2	int
    //   127	284	11	i3	int
    //   213	17	12	localObject1	Object
    //   342	8	12	i4	int
    //   391	17	13	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   78	196	213	finally
    //   213	215	213	finally
    //   294	374	391	finally
    //   391	393	391	finally
  }
  
  final void externalPush(ForkJoinTask<?> paramForkJoinTask)
  {
    int j = ThreadLocalRandom.getProbe();
    int k = runState;
    WorkQueue[] arrayOfWorkQueue;
    int i;
    WorkQueue localWorkQueue;
    if (((arrayOfWorkQueue = workQueues) != null) && ((i = arrayOfWorkQueue.length - 1) >= 0) && ((localWorkQueue = arrayOfWorkQueue[(i & j & 0x7E)]) != null) && (j != 0) && (k > 0) && (U.compareAndSwapInt(localWorkQueue, QLOCK, 0, 1)))
    {
      ForkJoinTask[] arrayOfForkJoinTask;
      int m;
      int i1;
      int n;
      if (((arrayOfForkJoinTask = array) != null) && ((m = arrayOfForkJoinTask.length - 1) > (n = (i1 = top) - base)))
      {
        int i2 = ((m & i1) << ASHIFT) + ABASE;
        U.putOrderedObject(arrayOfForkJoinTask, i2, paramForkJoinTask);
        U.putOrderedInt(localWorkQueue, QTOP, i1 + 1);
        U.putIntVolatile(localWorkQueue, QLOCK, 0);
        if (n <= 1) {
          signalWork(arrayOfWorkQueue, localWorkQueue);
        }
        return;
      }
      U.compareAndSwapInt(localWorkQueue, QLOCK, 1, 0);
    }
    externalSubmit(paramForkJoinTask);
  }
  
  static WorkQueue commonSubmitterQueue()
  {
    ForkJoinPool localForkJoinPool = common;
    int i = ThreadLocalRandom.getProbe();
    WorkQueue[] arrayOfWorkQueue;
    int j;
    return (localForkJoinPool != null) && ((arrayOfWorkQueue = workQueues) != null) && ((j = arrayOfWorkQueue.length - 1) >= 0) ? arrayOfWorkQueue[(j & i & 0x7E)] : null;
  }
  
  final boolean tryExternalUnpush(ForkJoinTask<?> paramForkJoinTask)
  {
    int k = ThreadLocalRandom.getProbe();
    WorkQueue[] arrayOfWorkQueue;
    int i;
    WorkQueue localWorkQueue;
    ForkJoinTask[] arrayOfForkJoinTask;
    int j;
    if (((arrayOfWorkQueue = workQueues) != null) && ((i = arrayOfWorkQueue.length - 1) >= 0) && ((localWorkQueue = arrayOfWorkQueue[(i & k & 0x7E)]) != null) && ((arrayOfForkJoinTask = array) != null) && ((j = top) != base))
    {
      long l = ((arrayOfForkJoinTask.length - 1 & j - 1) << ASHIFT) + ABASE;
      if (U.compareAndSwapInt(localWorkQueue, QLOCK, 0, 1))
      {
        if ((top == j) && (array == arrayOfForkJoinTask) && (U.getObject(arrayOfForkJoinTask, l) == paramForkJoinTask) && (U.compareAndSwapObject(arrayOfForkJoinTask, l, paramForkJoinTask, null)))
        {
          U.putOrderedInt(localWorkQueue, QTOP, j - 1);
          U.putOrderedInt(localWorkQueue, QLOCK, 0);
          return true;
        }
        U.compareAndSwapInt(localWorkQueue, QLOCK, 1, 0);
      }
    }
    return false;
  }
  
  final int externalHelpComplete(CountedCompleter<?> paramCountedCompleter, int paramInt)
  {
    int j = ThreadLocalRandom.getProbe();
    WorkQueue[] arrayOfWorkQueue;
    int i;
    return ((arrayOfWorkQueue = workQueues) == null) || ((i = arrayOfWorkQueue.length) == 0) ? 0 : helpComplete(arrayOfWorkQueue[(i - 1 & j & 0x7E)], paramCountedCompleter, paramInt);
  }
  
  public ForkJoinPool()
  {
    this(Math.min(32767, Runtime.getRuntime().availableProcessors()), defaultForkJoinWorkerThreadFactory, null, false);
  }
  
  public ForkJoinPool(int paramInt)
  {
    this(paramInt, defaultForkJoinWorkerThreadFactory, null, false);
  }
  
  public ForkJoinPool(int paramInt, ForkJoinWorkerThreadFactory paramForkJoinWorkerThreadFactory, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, boolean paramBoolean)
  {
    this(checkParallelism(paramInt), checkFactory(paramForkJoinWorkerThreadFactory), paramUncaughtExceptionHandler, paramBoolean ? 65536 : 0, "ForkJoinPool-" + nextPoolId() + "-worker-");
    checkPermission();
  }
  
  private static int checkParallelism(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > 32767)) {
      throw new IllegalArgumentException();
    }
    return paramInt;
  }
  
  private static ForkJoinWorkerThreadFactory checkFactory(ForkJoinWorkerThreadFactory paramForkJoinWorkerThreadFactory)
  {
    if (paramForkJoinWorkerThreadFactory == null) {
      throw new NullPointerException();
    }
    return paramForkJoinWorkerThreadFactory;
  }
  
  private ForkJoinPool(int paramInt1, ForkJoinWorkerThreadFactory paramForkJoinWorkerThreadFactory, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler, int paramInt2, String paramString)
  {
    workerNamePrefix = paramString;
    factory = paramForkJoinWorkerThreadFactory;
    ueh = paramUncaughtExceptionHandler;
    config = (paramInt1 & 0xFFFF | paramInt2);
    long l = -paramInt1;
    ctl = (l << 48 & 0xFFFF000000000000 | l << 32 & 0xFFFF00000000);
  }
  
  public static ForkJoinPool commonPool()
  {
    return common;
  }
  
  public <T> T invoke(ForkJoinTask<T> paramForkJoinTask)
  {
    if (paramForkJoinTask == null) {
      throw new NullPointerException();
    }
    externalPush(paramForkJoinTask);
    return (T)paramForkJoinTask.join();
  }
  
  public void execute(ForkJoinTask<?> paramForkJoinTask)
  {
    if (paramForkJoinTask == null) {
      throw new NullPointerException();
    }
    externalPush(paramForkJoinTask);
  }
  
  public void execute(Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    Object localObject;
    if ((paramRunnable instanceof ForkJoinTask)) {
      localObject = (ForkJoinTask)paramRunnable;
    } else {
      localObject = new ForkJoinTask.RunnableExecuteAction(paramRunnable);
    }
    externalPush((ForkJoinTask)localObject);
  }
  
  public <T> ForkJoinTask<T> submit(ForkJoinTask<T> paramForkJoinTask)
  {
    if (paramForkJoinTask == null) {
      throw new NullPointerException();
    }
    externalPush(paramForkJoinTask);
    return paramForkJoinTask;
  }
  
  public <T> ForkJoinTask<T> submit(Callable<T> paramCallable)
  {
    ForkJoinTask.AdaptedCallable localAdaptedCallable = new ForkJoinTask.AdaptedCallable(paramCallable);
    externalPush(localAdaptedCallable);
    return localAdaptedCallable;
  }
  
  public <T> ForkJoinTask<T> submit(Runnable paramRunnable, T paramT)
  {
    ForkJoinTask.AdaptedRunnable localAdaptedRunnable = new ForkJoinTask.AdaptedRunnable(paramRunnable, paramT);
    externalPush(localAdaptedRunnable);
    return localAdaptedRunnable;
  }
  
  public ForkJoinTask<?> submit(Runnable paramRunnable)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    Object localObject;
    if ((paramRunnable instanceof ForkJoinTask)) {
      localObject = (ForkJoinTask)paramRunnable;
    } else {
      localObject = new ForkJoinTask.AdaptedRunnableAction(paramRunnable);
    }
    externalPush((ForkJoinTask)localObject);
    return (ForkJoinTask<?>)localObject;
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection)
  {
    ArrayList localArrayList1 = new ArrayList(paramCollection.size());
    int i = 0;
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Callable localCallable = (Callable)localIterator.next();
        ForkJoinTask.AdaptedCallable localAdaptedCallable = new ForkJoinTask.AdaptedCallable(localCallable);
        localArrayList1.add(localAdaptedCallable);
        externalPush(localAdaptedCallable);
      }
      int j = 0;
      int k = localArrayList1.size();
      while (j < k)
      {
        ((ForkJoinTask)localArrayList1.get(j)).quietlyJoin();
        j++;
      }
      i = 1;
      ArrayList localArrayList2 = localArrayList1;
      int m;
      return localArrayList2;
    }
    finally
    {
      if (i == 0)
      {
        int n = 0;
        int i1 = localArrayList1.size();
        while (n < i1)
        {
          ((Future)localArrayList1.get(n)).cancel(false);
          n++;
        }
      }
    }
  }
  
  public ForkJoinWorkerThreadFactory getFactory()
  {
    return factory;
  }
  
  public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler()
  {
    return ueh;
  }
  
  public int getParallelism()
  {
    int i;
    return (i = config & 0xFFFF) > 0 ? i : 1;
  }
  
  public static int getCommonPoolParallelism()
  {
    return commonParallelism;
  }
  
  public int getPoolSize()
  {
    return (config & 0xFFFF) + (short)(int)(ctl >>> 32);
  }
  
  public boolean getAsyncMode()
  {
    return (config & 0x10000) != 0;
  }
  
  public int getRunningThreadCount()
  {
    int i = 0;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int j = 1; j < arrayOfWorkQueue.length; j += 2)
      {
        WorkQueue localWorkQueue;
        if (((localWorkQueue = arrayOfWorkQueue[j]) != null) && (localWorkQueue.isApparentlyUnblocked())) {
          i++;
        }
      }
    }
    return i;
  }
  
  public int getActiveThreadCount()
  {
    int i = (config & 0xFFFF) + (int)(ctl >> 48);
    return i <= 0 ? 0 : i;
  }
  
  public boolean isQuiescent()
  {
    return (config & 0xFFFF) + (int)(ctl >> 48) <= 0;
  }
  
  public long getStealCount()
  {
    AtomicLong localAtomicLong = stealCounter;
    long l = localAtomicLong == null ? 0L : localAtomicLong.get();
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int i = 1; i < arrayOfWorkQueue.length; i += 2)
      {
        WorkQueue localWorkQueue;
        if ((localWorkQueue = arrayOfWorkQueue[i]) != null) {
          l += nsteals;
        }
      }
    }
    return l;
  }
  
  public long getQueuedTaskCount()
  {
    long l = 0L;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int i = 1; i < arrayOfWorkQueue.length; i += 2)
      {
        WorkQueue localWorkQueue;
        if ((localWorkQueue = arrayOfWorkQueue[i]) != null) {
          l += localWorkQueue.queueSize();
        }
      }
    }
    return l;
  }
  
  public int getQueuedSubmissionCount()
  {
    int i = 0;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int j = 0; j < arrayOfWorkQueue.length; j += 2)
      {
        WorkQueue localWorkQueue;
        if ((localWorkQueue = arrayOfWorkQueue[j]) != null) {
          i += localWorkQueue.queueSize();
        }
      }
    }
    return i;
  }
  
  public boolean hasQueuedSubmissions()
  {
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int i = 0; i < arrayOfWorkQueue.length; i += 2)
      {
        WorkQueue localWorkQueue;
        if (((localWorkQueue = arrayOfWorkQueue[i]) != null) && (!localWorkQueue.isEmpty())) {
          return true;
        }
      }
    }
    return false;
  }
  
  protected ForkJoinTask<?> pollSubmission()
  {
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int i = 0; i < arrayOfWorkQueue.length; i += 2)
      {
        WorkQueue localWorkQueue;
        ForkJoinTask localForkJoinTask;
        if (((localWorkQueue = arrayOfWorkQueue[i]) != null) && ((localForkJoinTask = localWorkQueue.poll()) != null)) {
          return localForkJoinTask;
        }
      }
    }
    return null;
  }
  
  protected int drainTasksTo(Collection<? super ForkJoinTask<?>> paramCollection)
  {
    int i = 0;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (int j = 0; j < arrayOfWorkQueue.length; j++)
      {
        WorkQueue localWorkQueue;
        if ((localWorkQueue = arrayOfWorkQueue[j]) != null)
        {
          ForkJoinTask localForkJoinTask;
          while ((localForkJoinTask = localWorkQueue.poll()) != null)
          {
            paramCollection.add(localForkJoinTask);
            i++;
          }
        }
      }
    }
    return i;
  }
  
  public String toString()
  {
    long l1 = 0L;
    long l2 = 0L;
    int i = 0;
    AtomicLong localAtomicLong = stealCounter;
    long l3 = localAtomicLong == null ? 0L : localAtomicLong.get();
    long l4 = ctl;
    WorkQueue[] arrayOfWorkQueue;
    if ((arrayOfWorkQueue = workQueues) != null) {
      for (j = 0; j < arrayOfWorkQueue.length; j++)
      {
        WorkQueue localWorkQueue;
        if ((localWorkQueue = arrayOfWorkQueue[j]) != null)
        {
          k = localWorkQueue.queueSize();
          if ((j & 0x1) == 0)
          {
            l2 += k;
          }
          else
          {
            l1 += k;
            l3 += nsteals;
            if (localWorkQueue.isApparentlyUnblocked()) {
              i++;
            }
          }
        }
      }
    }
    int j = config & 0xFFFF;
    int k = j + (short)(int)(l4 >>> 32);
    int m = j + (int)(l4 >> 48);
    if (m < 0) {
      m = 0;
    }
    int n = runState;
    String str = (n & 0x80000000) != 0 ? "Shutting down" : (n & 0x20000000) != 0 ? "Terminating" : (n & 0x40000000) != 0 ? "Terminated" : "Running";
    return super.toString() + "[" + str + ", parallelism = " + j + ", size = " + k + ", active = " + m + ", running = " + i + ", steals = " + l3 + ", tasks = " + l1 + ", submissions = " + l2 + "]";
  }
  
  public void shutdown()
  {
    checkPermission();
    tryTerminate(false, true);
  }
  
  public List<Runnable> shutdownNow()
  {
    checkPermission();
    tryTerminate(true, true);
    return Collections.emptyList();
  }
  
  public boolean isTerminated()
  {
    return (runState & 0x40000000) != 0;
  }
  
  public boolean isTerminating()
  {
    int i = runState;
    return ((i & 0x20000000) != 0) && ((i & 0x40000000) == 0);
  }
  
  public boolean isShutdown()
  {
    return (runState & 0x80000000) != 0;
  }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (this == common)
    {
      awaitQuiescence(paramLong, paramTimeUnit);
      return false;
    }
    long l1 = paramTimeUnit.toNanos(paramLong);
    if (isTerminated()) {
      return true;
    }
    if (l1 <= 0L) {
      return false;
    }
    long l2 = System.nanoTime() + l1;
    synchronized (this)
    {
      if (isTerminated()) {
        return true;
      }
      if (l1 <= 0L) {
        return false;
      }
      long l3 = TimeUnit.NANOSECONDS.toMillis(l1);
      wait(l3 > 0L ? l3 : 1L);
      l1 = l2 - System.nanoTime();
    }
  }
  
  public boolean awaitQuiescence(long paramLong, TimeUnit paramTimeUnit)
  {
    long l1 = paramTimeUnit.toNanos(paramLong);
    Thread localThread = Thread.currentThread();
    ForkJoinWorkerThread localForkJoinWorkerThread;
    if (((localThread instanceof ForkJoinWorkerThread)) && (pool == this))
    {
      helpQuiescePool(workQueue);
      return true;
    }
    long l2 = System.nanoTime();
    int i = 0;
    int k = 1;
    WorkQueue[] arrayOfWorkQueue;
    int j;
    while ((!isQuiescent()) && ((arrayOfWorkQueue = workQueues) != null) && ((j = arrayOfWorkQueue.length - 1) >= 0))
    {
      if (k == 0)
      {
        if (System.nanoTime() - l2 > l1) {
          return false;
        }
        Thread.yield();
      }
      k = 0;
      for (int m = j + 1 << 2; m >= 0; m--)
      {
        int i1;
        WorkQueue localWorkQueue;
        int n;
        if (((i1 = i++ & j) <= j) && (i1 >= 0) && ((localWorkQueue = arrayOfWorkQueue[i1]) != null) && ((n = base) - top < 0))
        {
          k = 1;
          ForkJoinTask localForkJoinTask;
          if ((localForkJoinTask = localWorkQueue.pollAt(n)) == null) {
            break;
          }
          localForkJoinTask.doExec();
          break;
        }
      }
    }
    return true;
  }
  
  static void quiesceCommonPool()
  {
    common.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }
  
  public static void managedBlock(ManagedBlocker paramManagedBlocker)
    throws InterruptedException
  {
    Thread localThread = Thread.currentThread();
    ForkJoinWorkerThread localForkJoinWorkerThread;
    ForkJoinPool localForkJoinPool;
    if (((localThread instanceof ForkJoinWorkerThread)) && ((localForkJoinPool = pool) != null))
    {
      WorkQueue localWorkQueue = workQueue;
      while (!paramManagedBlocker.isReleasable()) {
        if (localForkJoinPool.tryCompensate(localWorkQueue)) {
          try
          {
            do
            {
              if (paramManagedBlocker.isReleasable()) {
                break;
              }
            } while (!paramManagedBlocker.block());
          }
          finally
          {
            U.getAndAddLong(localForkJoinPool, CTL, 281474976710656L);
          }
        }
      }
    }
    while ((!paramManagedBlocker.isReleasable()) && (!paramManagedBlocker.block())) {}
  }
  
  protected <T> RunnableFuture<T> newTaskFor(Runnable paramRunnable, T paramT)
  {
    return new ForkJoinTask.AdaptedRunnable(paramRunnable, paramT);
  }
  
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> paramCallable)
  {
    return new ForkJoinTask.AdaptedCallable(paramCallable);
  }
  
  private static ForkJoinPool makeCommonPool()
  {
    int i = -1;
    Object localObject = null;
    Thread.UncaughtExceptionHandler localUncaughtExceptionHandler = null;
    try
    {
      String str1 = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
      String str2 = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
      String str3 = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
      if (str1 != null) {
        i = Integer.parseInt(str1);
      }
      if (str2 != null) {
        localObject = (ForkJoinWorkerThreadFactory)ClassLoader.getSystemClassLoader().loadClass(str2).newInstance();
      }
      if (str3 != null) {
        localUncaughtExceptionHandler = (Thread.UncaughtExceptionHandler)ClassLoader.getSystemClassLoader().loadClass(str3).newInstance();
      }
    }
    catch (Exception localException) {}
    if (localObject == null) {
      if (System.getSecurityManager() == null) {
        localObject = defaultForkJoinWorkerThreadFactory;
      } else {
        localObject = new InnocuousForkJoinWorkerThreadFactory();
      }
    }
    if ((i < 0) && ((i = Runtime.getRuntime().availableProcessors() - 1) <= 0)) {
      i = 1;
    }
    if (i > 32767) {
      i = 32767;
    }
    return new ForkJoinPool(i, (ForkJoinWorkerThreadFactory)localObject, localUncaughtExceptionHandler, 0, "ForkJoinPool.commonPool-worker-");
  }
  
  static
  {
    try
    {
      U = Unsafe.getUnsafe();
      Class localClass1 = ForkJoinPool.class;
      CTL = U.objectFieldOffset(localClass1.getDeclaredField("ctl"));
      RUNSTATE = U.objectFieldOffset(localClass1.getDeclaredField("runState"));
      STEALCOUNTER = U.objectFieldOffset(localClass1.getDeclaredField("stealCounter"));
      Class localClass2 = Thread.class;
      PARKBLOCKER = U.objectFieldOffset(localClass2.getDeclaredField("parkBlocker"));
      Class localClass3 = WorkQueue.class;
      QTOP = U.objectFieldOffset(localClass3.getDeclaredField("top"));
      QLOCK = U.objectFieldOffset(localClass3.getDeclaredField("qlock"));
      QSCANSTATE = U.objectFieldOffset(localClass3.getDeclaredField("scanState"));
      QPARKER = U.objectFieldOffset(localClass3.getDeclaredField("parker"));
      QCURRENTSTEAL = U.objectFieldOffset(localClass3.getDeclaredField("currentSteal"));
      QCURRENTJOIN = U.objectFieldOffset(localClass3.getDeclaredField("currentJoin"));
      Class localClass4 = ForkJoinTask[].class;
      ABASE = U.arrayBaseOffset(localClass4);
      int j = U.arrayIndexScale(localClass4);
      if ((j & j - 1) != 0) {
        throw new Error("data type scale not a power of two");
      }
      ASHIFT = 31 - Integer.numberOfLeadingZeros(j);
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
    commonMaxSpares = 256;
    defaultForkJoinWorkerThreadFactory = new DefaultForkJoinWorkerThreadFactory();
    modifyThreadPermission = new RuntimePermission("modifyThread");
    common = (ForkJoinPool)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ForkJoinPool run()
      {
        return ForkJoinPool.access$000();
      }
    });
    int i = commonconfig & 0xFFFF;
    commonParallelism = i > 0 ? i : 1;
  }
  
  static final class DefaultForkJoinWorkerThreadFactory
    implements ForkJoinPool.ForkJoinWorkerThreadFactory
  {
    DefaultForkJoinWorkerThreadFactory() {}
    
    public final ForkJoinWorkerThread newThread(ForkJoinPool paramForkJoinPool)
    {
      return new ForkJoinWorkerThread(paramForkJoinPool);
    }
  }
  
  static final class EmptyTask
    extends ForkJoinTask<Void>
  {
    private static final long serialVersionUID = -7721805057305804111L;
    
    EmptyTask()
    {
      status = -268435456;
    }
    
    public final Void getRawResult()
    {
      return null;
    }
    
    public final void setRawResult(Void paramVoid) {}
    
    public final boolean exec()
    {
      return true;
    }
  }
  
  public static abstract interface ForkJoinWorkerThreadFactory
  {
    public abstract ForkJoinWorkerThread newThread(ForkJoinPool paramForkJoinPool);
  }
  
  static final class InnocuousForkJoinWorkerThreadFactory
    implements ForkJoinPool.ForkJoinWorkerThreadFactory
  {
    private static final AccessControlContext innocuousAcc;
    
    InnocuousForkJoinWorkerThreadFactory() {}
    
    public final ForkJoinWorkerThread newThread(final ForkJoinPool paramForkJoinPool)
    {
      (ForkJoinWorkerThread.InnocuousForkJoinWorkerThread)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ForkJoinWorkerThread run()
        {
          return new ForkJoinWorkerThread.InnocuousForkJoinWorkerThread(paramForkJoinPool);
        }
      }, innocuousAcc);
    }
    
    static
    {
      Permissions localPermissions = new Permissions();
      localPermissions.add(ForkJoinPool.modifyThreadPermission);
      localPermissions.add(new RuntimePermission("enableContextClassLoaderOverride"));
      localPermissions.add(new RuntimePermission("modifyThreadGroup"));
      innocuousAcc = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, localPermissions) });
    }
  }
  
  public static abstract interface ManagedBlocker
  {
    public abstract boolean block()
      throws InterruptedException;
    
    public abstract boolean isReleasable();
  }
  
  @Contended
  static final class WorkQueue
  {
    static final int INITIAL_QUEUE_CAPACITY = 8192;
    static final int MAXIMUM_QUEUE_CAPACITY = 67108864;
    volatile int scanState;
    int stackPred;
    int nsteals;
    int hint;
    int config;
    volatile int qlock;
    volatile int base;
    int top;
    ForkJoinTask<?>[] array;
    final ForkJoinPool pool;
    final ForkJoinWorkerThread owner;
    volatile Thread parker;
    volatile ForkJoinTask<?> currentJoin;
    volatile ForkJoinTask<?> currentSteal;
    private static final Unsafe U;
    private static final int ABASE;
    private static final int ASHIFT;
    private static final long QTOP;
    private static final long QLOCK;
    private static final long QCURRENTSTEAL;
    
    WorkQueue(ForkJoinPool paramForkJoinPool, ForkJoinWorkerThread paramForkJoinWorkerThread)
    {
      pool = paramForkJoinPool;
      owner = paramForkJoinWorkerThread;
      base = (top = 'က');
    }
    
    final int getPoolIndex()
    {
      return (config & 0xFFFF) >>> 1;
    }
    
    final int queueSize()
    {
      int i = base - top;
      return i >= 0 ? 0 : -i;
    }
    
    final boolean isEmpty()
    {
      int k;
      int i;
      ForkJoinTask[] arrayOfForkJoinTask;
      int j;
      return ((i = base - (k = top)) >= 0) || ((i == -1) && (((arrayOfForkJoinTask = array) == null) || ((j = arrayOfForkJoinTask.length - 1) < 0) || (U.getObject(arrayOfForkJoinTask, ((j & k - 1) << ASHIFT) + ABASE) == null)));
    }
    
    final void push(ForkJoinTask<?> paramForkJoinTask)
    {
      int i = base;
      int j = top;
      ForkJoinTask[] arrayOfForkJoinTask;
      if ((arrayOfForkJoinTask = array) != null)
      {
        int m = arrayOfForkJoinTask.length - 1;
        U.putOrderedObject(arrayOfForkJoinTask, ((m & j) << ASHIFT) + ABASE, paramForkJoinTask);
        U.putOrderedInt(this, QTOP, j + 1);
        int k;
        if ((k = j - i) <= 1)
        {
          ForkJoinPool localForkJoinPool;
          if ((localForkJoinPool = pool) != null) {
            localForkJoinPool.signalWork(workQueues, this);
          }
        }
        else if (k >= m)
        {
          growArray();
        }
      }
    }
    
    final ForkJoinTask<?>[] growArray()
    {
      ForkJoinTask[] arrayOfForkJoinTask1 = array;
      int i = arrayOfForkJoinTask1 != null ? arrayOfForkJoinTask1.length << 1 : 8192;
      if (i > 67108864) {
        throw new RejectedExecutionException("Queue capacity exceeded");
      }
      ForkJoinTask[] arrayOfForkJoinTask2 = array = new ForkJoinTask[i];
      int j;
      int k;
      int m;
      if ((arrayOfForkJoinTask1 != null) && ((j = arrayOfForkJoinTask1.length - 1) >= 0) && ((k = top) - (m = base) > 0))
      {
        int n = i - 1;
        do
        {
          int i1 = ((m & j) << ASHIFT) + ABASE;
          int i2 = ((m & n) << ASHIFT) + ABASE;
          ForkJoinTask localForkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask1, i1);
          if ((localForkJoinTask != null) && (U.compareAndSwapObject(arrayOfForkJoinTask1, i1, localForkJoinTask, null))) {
            U.putObjectVolatile(arrayOfForkJoinTask2, i2, localForkJoinTask);
          }
          m++;
        } while (m != k);
      }
      return arrayOfForkJoinTask2;
    }
    
    final ForkJoinTask<?> pop()
    {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      if (((arrayOfForkJoinTask = array) != null) && ((i = arrayOfForkJoinTask.length - 1) >= 0))
      {
        int j;
        while ((j = top - 1) - base >= 0)
        {
          long l = ((i & j) << ASHIFT) + ABASE;
          ForkJoinTask localForkJoinTask;
          if ((localForkJoinTask = (ForkJoinTask)U.getObject(arrayOfForkJoinTask, l)) == null) {
            break;
          }
          if (U.compareAndSwapObject(arrayOfForkJoinTask, l, localForkJoinTask, null))
          {
            U.putOrderedInt(this, QTOP, j);
            return localForkJoinTask;
          }
        }
      }
      return null;
    }
    
    final ForkJoinTask<?> pollAt(int paramInt)
    {
      ForkJoinTask[] arrayOfForkJoinTask;
      if ((arrayOfForkJoinTask = array) != null)
      {
        int i = ((arrayOfForkJoinTask.length - 1 & paramInt) << ASHIFT) + ABASE;
        ForkJoinTask localForkJoinTask;
        if (((localForkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, i)) != null) && (base == paramInt) && (U.compareAndSwapObject(arrayOfForkJoinTask, i, localForkJoinTask, null)))
        {
          base = (paramInt + 1);
          return localForkJoinTask;
        }
      }
      return null;
    }
    
    final ForkJoinTask<?> poll()
    {
      int i;
      ForkJoinTask[] arrayOfForkJoinTask;
      while (((i = base) - top < 0) && ((arrayOfForkJoinTask = array) != null))
      {
        int j = ((arrayOfForkJoinTask.length - 1 & i) << ASHIFT) + ABASE;
        ForkJoinTask localForkJoinTask = (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, j);
        if (base == i) {
          if (localForkJoinTask != null)
          {
            if (U.compareAndSwapObject(arrayOfForkJoinTask, j, localForkJoinTask, null))
            {
              base = (i + 1);
              return localForkJoinTask;
            }
          }
          else {
            if (i + 1 == top) {
              break;
            }
          }
        }
      }
      return null;
    }
    
    final ForkJoinTask<?> nextLocalTask()
    {
      return (config & 0x10000) == 0 ? pop() : poll();
    }
    
    final ForkJoinTask<?> peek()
    {
      ForkJoinTask[] arrayOfForkJoinTask = array;
      int i;
      if ((arrayOfForkJoinTask == null) || ((i = arrayOfForkJoinTask.length - 1) < 0)) {
        return null;
      }
      int j = (config & 0x10000) == 0 ? top - 1 : base;
      int k = ((j & i) << ASHIFT) + ABASE;
      return (ForkJoinTask)U.getObjectVolatile(arrayOfForkJoinTask, k);
    }
    
    final boolean tryUnpush(ForkJoinTask<?> paramForkJoinTask)
    {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      if (((arrayOfForkJoinTask = array) != null) && ((i = top) != base) && (U.compareAndSwapObject(arrayOfForkJoinTask, ((arrayOfForkJoinTask.length - 1 & --i) << ASHIFT) + ABASE, paramForkJoinTask, null)))
      {
        U.putOrderedInt(this, QTOP, i);
        return true;
      }
      return false;
    }
    
    final void cancelAll()
    {
      ForkJoinTask localForkJoinTask;
      if ((localForkJoinTask = currentJoin) != null)
      {
        currentJoin = null;
        ForkJoinTask.cancelIgnoringExceptions(localForkJoinTask);
      }
      if ((localForkJoinTask = currentSteal) != null)
      {
        currentSteal = null;
        ForkJoinTask.cancelIgnoringExceptions(localForkJoinTask);
      }
      while ((localForkJoinTask = poll()) != null) {
        ForkJoinTask.cancelIgnoringExceptions(localForkJoinTask);
      }
    }
    
    final void pollAndExecAll()
    {
      ForkJoinTask localForkJoinTask;
      while ((localForkJoinTask = poll()) != null) {
        localForkJoinTask.doExec();
      }
    }
    
    final void execLocalTasks()
    {
      int i = base;
      ForkJoinTask[] arrayOfForkJoinTask = array;
      int k;
      int j;
      if ((i - (k = top - 1) <= 0) && (arrayOfForkJoinTask != null) && ((j = arrayOfForkJoinTask.length - 1) >= 0)) {
        if ((config & 0x10000) == 0)
        {
          ForkJoinTask localForkJoinTask;
          while ((localForkJoinTask = (ForkJoinTask)U.getAndSetObject(arrayOfForkJoinTask, ((j & k) << ASHIFT) + ABASE, null)) != null)
          {
            U.putOrderedInt(this, QTOP, k);
            localForkJoinTask.doExec();
            if (base - (k = top - 1) > 0) {
              break;
            }
          }
        }
        else
        {
          pollAndExecAll();
        }
      }
    }
    
    final void runTask(ForkJoinTask<?> paramForkJoinTask)
    {
      if (paramForkJoinTask != null)
      {
        scanState &= 0xFFFFFFFE;
        (currentSteal = paramForkJoinTask).doExec();
        U.putOrderedObject(this, QCURRENTSTEAL, null);
        execLocalTasks();
        ForkJoinWorkerThread localForkJoinWorkerThread = owner;
        if (++nsteals < 0) {
          transferStealCount(pool);
        }
        scanState |= 0x1;
        if (localForkJoinWorkerThread != null) {
          localForkJoinWorkerThread.afterTopLevelExec();
        }
      }
    }
    
    final void transferStealCount(ForkJoinPool paramForkJoinPool)
    {
      AtomicLong localAtomicLong;
      if ((paramForkJoinPool != null) && ((localAtomicLong = stealCounter) != null))
      {
        int i = nsteals;
        nsteals = 0;
        localAtomicLong.getAndAdd(i < 0 ? Integer.MAX_VALUE : i);
      }
    }
    
    final boolean tryRemoveAndExec(ForkJoinTask<?> paramForkJoinTask)
    {
      ForkJoinTask[] arrayOfForkJoinTask;
      int i;
      if (((arrayOfForkJoinTask = array) != null) && ((i = arrayOfForkJoinTask.length - 1) >= 0) && (paramForkJoinTask != null))
      {
        int j;
        int k;
        int m;
        while ((m = (j = top) - (k = base)) > 0)
        {
          for (;;)
          {
            j--;
            long l = ((j & i) << ASHIFT) + ABASE;
            ForkJoinTask localForkJoinTask;
            if ((localForkJoinTask = (ForkJoinTask)U.getObject(arrayOfForkJoinTask, l)) == null) {
              return j + 1 == top;
            }
            if (localForkJoinTask == paramForkJoinTask)
            {
              boolean bool = false;
              if (j + 1 == top)
              {
                if (U.compareAndSwapObject(arrayOfForkJoinTask, l, paramForkJoinTask, null))
                {
                  U.putOrderedInt(this, QTOP, j);
                  bool = true;
                }
              }
              else if (base == k) {
                bool = U.compareAndSwapObject(arrayOfForkJoinTask, l, paramForkJoinTask, new ForkJoinPool.EmptyTask());
              }
              if (!bool) {
                break;
              }
              paramForkJoinTask.doExec();
              break;
            }
            if ((status < 0) && (j + 1 == top))
            {
              if (!U.compareAndSwapObject(arrayOfForkJoinTask, l, localForkJoinTask, null)) {
                break;
              }
              U.putOrderedInt(this, QTOP, j);
              break;
            }
            m--;
            if (m == 0) {
              return false;
            }
          }
          if (status < 0) {
            return false;
          }
        }
      }
      return true;
    }
    
    final CountedCompleter<?> popCC(CountedCompleter<?> paramCountedCompleter, int paramInt)
    {
      int i;
      ForkJoinTask[] arrayOfForkJoinTask;
      if ((base - (i = top) < 0) && ((arrayOfForkJoinTask = array) != null))
      {
        long l = ((arrayOfForkJoinTask.length - 1 & i - 1) << ASHIFT) + ABASE;
        Object localObject;
        if (((localObject = U.getObjectVolatile(arrayOfForkJoinTask, l)) != null) && ((localObject instanceof CountedCompleter)))
        {
          CountedCompleter localCountedCompleter1 = (CountedCompleter)localObject;
          CountedCompleter localCountedCompleter2 = localCountedCompleter1;
          for (;;)
          {
            if (localCountedCompleter2 == paramCountedCompleter)
            {
              if (paramInt < 0)
              {
                if (U.compareAndSwapInt(this, QLOCK, 0, 1))
                {
                  if ((top == i) && (array == arrayOfForkJoinTask) && (U.compareAndSwapObject(arrayOfForkJoinTask, l, localCountedCompleter1, null)))
                  {
                    U.putOrderedInt(this, QTOP, i - 1);
                    U.putOrderedInt(this, QLOCK, 0);
                    return localCountedCompleter1;
                  }
                  U.compareAndSwapInt(this, QLOCK, 1, 0);
                }
              }
              else if (U.compareAndSwapObject(arrayOfForkJoinTask, l, localCountedCompleter1, null))
              {
                U.putOrderedInt(this, QTOP, i - 1);
                return localCountedCompleter1;
              }
            }
            else if ((localCountedCompleter2 = completer) == null) {
              break;
            }
          }
        }
      }
      return null;
    }
    
    final int pollAndExecCC(CountedCompleter<?> paramCountedCompleter)
    {
      int i;
      ForkJoinTask[] arrayOfForkJoinTask;
      int j;
      if (((i = base) - top >= 0) || ((arrayOfForkJoinTask = array) == null))
      {
        j = i | 0x80000000;
      }
      else
      {
        long l = ((arrayOfForkJoinTask.length - 1 & i) << ASHIFT) + ABASE;
        Object localObject;
        if ((localObject = U.getObjectVolatile(arrayOfForkJoinTask, l)) == null)
        {
          j = 2;
        }
        else if (!(localObject instanceof CountedCompleter))
        {
          j = -1;
        }
        else
        {
          CountedCompleter localCountedCompleter1 = (CountedCompleter)localObject;
          CountedCompleter localCountedCompleter2 = localCountedCompleter1;
          do
          {
            if (localCountedCompleter2 == paramCountedCompleter)
            {
              if ((base == i) && (U.compareAndSwapObject(arrayOfForkJoinTask, l, localCountedCompleter1, null)))
              {
                base = (i + 1);
                localCountedCompleter1.doExec();
                j = 1;
                break;
              }
              j = 2;
              break;
            }
          } while ((localCountedCompleter2 = completer) != null);
          j = -1;
        }
      }
      return j;
    }
    
    final boolean isApparentlyUnblocked()
    {
      ForkJoinWorkerThread localForkJoinWorkerThread;
      Thread.State localState;
      return (scanState >= 0) && ((localForkJoinWorkerThread = owner) != null) && ((localState = localForkJoinWorkerThread.getState()) != Thread.State.BLOCKED) && (localState != Thread.State.WAITING) && (localState != Thread.State.TIMED_WAITING);
    }
    
    static
    {
      try
      {
        U = Unsafe.getUnsafe();
        Class localClass1 = WorkQueue.class;
        Class localClass2 = ForkJoinTask[].class;
        QTOP = U.objectFieldOffset(localClass1.getDeclaredField("top"));
        QLOCK = U.objectFieldOffset(localClass1.getDeclaredField("qlock"));
        QCURRENTSTEAL = U.objectFieldOffset(localClass1.getDeclaredField("currentSteal"));
        ABASE = U.arrayBaseOffset(localClass2);
        int i = U.arrayIndexScale(localClass2);
        if ((i & i - 1) != 0) {
          throw new Error("data type scale not a power of two");
        }
        ASHIFT = 31 - Integer.numberOfLeadingZeros(i);
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ForkJoinPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
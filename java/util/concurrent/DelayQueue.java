package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueue<E extends Delayed>
  extends AbstractQueue<E>
  implements BlockingQueue<E>
{
  private final transient ReentrantLock lock = new ReentrantLock();
  private final PriorityQueue<E> q = new PriorityQueue();
  private Thread leader = null;
  private final Condition available = lock.newCondition();
  
  public DelayQueue() {}
  
  public DelayQueue(Collection<? extends E> paramCollection)
  {
    addAll(paramCollection);
  }
  
  public boolean add(E paramE)
  {
    return offer(paramE);
  }
  
  public boolean offer(E paramE)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      q.offer(paramE);
      if (q.peek() == paramE)
      {
        leader = null;
        available.signal();
      }
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void put(E paramE)
  {
    offer(paramE);
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
  {
    return offer(paramE);
  }
  
  public E poll()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Delayed localDelayed = (Delayed)q.peek();
      if ((localDelayed == null) || (localDelayed.getDelay(TimeUnit.NANOSECONDS) > 0L))
      {
        localObject1 = null;
        return (E)localObject1;
      }
      Object localObject1 = (Delayed)q.poll();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public E take()
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 161	java/util/concurrent/DelayQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 192	java/util/concurrent/locks/ReentrantLock:lockInterruptibly	()V
    //   9: aload_0
    //   10: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   13: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   16: checkcast 81	java/util/concurrent/Delayed
    //   19: astore_2
    //   20: aload_2
    //   21: ifnonnull +15 -> 36
    //   24: aload_0
    //   25: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   28: invokeinterface 200 1 0
    //   33: goto +143 -> 176
    //   36: aload_2
    //   37: getstatic 162	java/util/concurrent/TimeUnit:NANOSECONDS	Ljava/util/concurrent/TimeUnit;
    //   40: invokeinterface 199 2 0
    //   45: lstore_3
    //   46: lload_3
    //   47: lconst_0
    //   48: lcmp
    //   49: ifgt +48 -> 97
    //   52: aload_0
    //   53: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   56: invokevirtual 171	java/util/PriorityQueue:poll	()Ljava/lang/Object;
    //   59: checkcast 81	java/util/concurrent/Delayed
    //   62: astore 5
    //   64: aload_0
    //   65: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   68: ifnonnull +22 -> 90
    //   71: aload_0
    //   72: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   75: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   78: ifnull +12 -> 90
    //   81: aload_0
    //   82: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   85: invokeinterface 201 1 0
    //   90: aload_1
    //   91: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   94: aload 5
    //   96: areturn
    //   97: aconst_null
    //   98: astore_2
    //   99: aload_0
    //   100: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   103: ifnull +15 -> 118
    //   106: aload_0
    //   107: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   110: invokeinterface 200 1 0
    //   115: goto +61 -> 176
    //   118: invokestatic 165	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   121: astore 5
    //   123: aload_0
    //   124: aload 5
    //   126: putfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   129: aload_0
    //   130: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   133: lload_3
    //   134: invokeinterface 202 3 0
    //   139: pop2
    //   140: aload_0
    //   141: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   144: aload 5
    //   146: if_acmpne +30 -> 176
    //   149: aload_0
    //   150: aconst_null
    //   151: putfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   154: goto +22 -> 176
    //   157: astore 6
    //   159: aload_0
    //   160: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   163: aload 5
    //   165: if_acmpne +8 -> 173
    //   168: aload_0
    //   169: aconst_null
    //   170: putfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   173: aload 6
    //   175: athrow
    //   176: goto -167 -> 9
    //   179: astore 7
    //   181: aload_0
    //   182: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   185: ifnonnull +22 -> 207
    //   188: aload_0
    //   189: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   192: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   195: ifnull +12 -> 207
    //   198: aload_0
    //   199: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   202: invokeinterface 201 1 0
    //   207: aload_1
    //   208: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   211: aload 7
    //   213: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	this	DelayQueue
    //   4	204	1	localReentrantLock	ReentrantLock
    //   19	80	2	localDelayed	Delayed
    //   45	89	3	l	long
    //   62	102	5	localObject1	Object
    //   157	17	6	localObject2	Object
    //   179	33	7	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   129	140	157	finally
    //   157	159	157	finally
    //   9	64	179	finally
    //   97	181	179	finally
  }
  
  /* Error */
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_3
    //   1: lload_1
    //   2: invokevirtual 189	java/util/concurrent/TimeUnit:toNanos	(J)J
    //   5: lstore 4
    //   7: aload_0
    //   8: getfield 161	java/util/concurrent/DelayQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   11: astore 6
    //   13: aload 6
    //   15: invokevirtual 192	java/util/concurrent/locks/ReentrantLock:lockInterruptibly	()V
    //   18: aload_0
    //   19: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   22: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   25: checkcast 81	java/util/concurrent/Delayed
    //   28: astore 7
    //   30: aload 7
    //   32: ifnonnull +63 -> 95
    //   35: lload 4
    //   37: lconst_0
    //   38: lcmp
    //   39: ifgt +40 -> 79
    //   42: aconst_null
    //   43: astore 8
    //   45: aload_0
    //   46: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   49: ifnonnull +22 -> 71
    //   52: aload_0
    //   53: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   56: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   59: ifnull +12 -> 71
    //   62: aload_0
    //   63: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   66: invokeinterface 201 1 0
    //   71: aload 6
    //   73: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   76: aload 8
    //   78: areturn
    //   79: aload_0
    //   80: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   83: lload 4
    //   85: invokeinterface 202 3 0
    //   90: lstore 4
    //   92: goto +216 -> 308
    //   95: aload 7
    //   97: getstatic 162	java/util/concurrent/TimeUnit:NANOSECONDS	Ljava/util/concurrent/TimeUnit;
    //   100: invokeinterface 199 2 0
    //   105: lstore 8
    //   107: lload 8
    //   109: lconst_0
    //   110: lcmp
    //   111: ifgt +49 -> 160
    //   114: aload_0
    //   115: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   118: invokevirtual 171	java/util/PriorityQueue:poll	()Ljava/lang/Object;
    //   121: checkcast 81	java/util/concurrent/Delayed
    //   124: astore 10
    //   126: aload_0
    //   127: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   130: ifnonnull +22 -> 152
    //   133: aload_0
    //   134: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   137: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   140: ifnull +12 -> 152
    //   143: aload_0
    //   144: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   147: invokeinterface 201 1 0
    //   152: aload 6
    //   154: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   157: aload 10
    //   159: areturn
    //   160: lload 4
    //   162: lconst_0
    //   163: lcmp
    //   164: ifgt +40 -> 204
    //   167: aconst_null
    //   168: astore 10
    //   170: aload_0
    //   171: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   174: ifnonnull +22 -> 196
    //   177: aload_0
    //   178: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   181: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   184: ifnull +12 -> 196
    //   187: aload_0
    //   188: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   191: invokeinterface 201 1 0
    //   196: aload 6
    //   198: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   201: aload 10
    //   203: areturn
    //   204: aconst_null
    //   205: astore 7
    //   207: lload 4
    //   209: lload 8
    //   211: lcmp
    //   212: iflt +10 -> 222
    //   215: aload_0
    //   216: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   219: ifnull +19 -> 238
    //   222: aload_0
    //   223: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   226: lload 4
    //   228: invokeinterface 202 3 0
    //   233: lstore 4
    //   235: goto +73 -> 308
    //   238: invokestatic 165	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   241: astore 10
    //   243: aload_0
    //   244: aload 10
    //   246: putfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   249: aload_0
    //   250: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   253: lload 8
    //   255: invokeinterface 202 3 0
    //   260: lstore 11
    //   262: lload 4
    //   264: lload 8
    //   266: lload 11
    //   268: lsub
    //   269: lsub
    //   270: lstore 4
    //   272: aload_0
    //   273: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   276: aload 10
    //   278: if_acmpne +30 -> 308
    //   281: aload_0
    //   282: aconst_null
    //   283: putfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   286: goto +22 -> 308
    //   289: astore 13
    //   291: aload_0
    //   292: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   295: aload 10
    //   297: if_acmpne +8 -> 305
    //   300: aload_0
    //   301: aconst_null
    //   302: putfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   305: aload 13
    //   307: athrow
    //   308: goto -290 -> 18
    //   311: astore 14
    //   313: aload_0
    //   314: getfield 158	java/util/concurrent/DelayQueue:leader	Ljava/lang/Thread;
    //   317: ifnonnull +22 -> 339
    //   320: aload_0
    //   321: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   324: invokevirtual 170	java/util/PriorityQueue:peek	()Ljava/lang/Object;
    //   327: ifnull +12 -> 339
    //   330: aload_0
    //   331: getfield 160	java/util/concurrent/DelayQueue:available	Ljava/util/concurrent/locks/Condition;
    //   334: invokeinterface 201 1 0
    //   339: aload 6
    //   341: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   344: aload 14
    //   346: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	347	0	this	DelayQueue
    //   0	347	1	paramLong	long
    //   0	347	3	paramTimeUnit	TimeUnit
    //   5	266	4	l1	long
    //   11	329	6	localReentrantLock	ReentrantLock
    //   28	178	7	localDelayed	Delayed
    //   43	34	8	?	E
    //   105	160	8	l2	long
    //   124	172	10	localObject1	Object
    //   260	7	11	l3	long
    //   289	17	13	localObject2	Object
    //   311	34	14	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   249	272	289	finally
    //   289	291	289	finally
    //   18	45	311	finally
    //   79	126	311	finally
    //   160	170	311	finally
    //   204	313	311	finally
  }
  
  public E peek()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Delayed localDelayed = (Delayed)q.peek();
      return localDelayed;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int size()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = q.size();
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private E peekExpired()
  {
    Delayed localDelayed = (Delayed)q.peek();
    return (localDelayed == null) || (localDelayed.getDelay(TimeUnit.NANOSECONDS) > 0L) ? null : localDelayed;
  }
  
  public int drainTo(Collection<? super E> paramCollection)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      for (Delayed localDelayed1 = 0; (localDelayed2 = peekExpired()) != null; localDelayed1++)
      {
        paramCollection.add(localDelayed2);
        q.poll();
      }
      Delayed localDelayed2 = localDelayed1;
      return localDelayed2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    if (paramInt <= 0) {
      return 0;
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Delayed localDelayed;
      for (int i = 0; (i < paramInt) && ((localDelayed = peekExpired()) != null); i++)
      {
        paramCollection.add(localDelayed);
        q.poll();
      }
      int j = i;
      return j;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public void clear()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 161	java/util/concurrent/DelayQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 191	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: getfield 159	java/util/concurrent/DelayQueue:q	Ljava/util/PriorityQueue;
    //   13: invokevirtual 169	java/util/PriorityQueue:clear	()V
    //   16: aload_1
    //   17: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   20: goto +10 -> 30
    //   23: astore_2
    //   24: aload_1
    //   25: invokevirtual 193	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   28: aload_2
    //   29: athrow
    //   30: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	DelayQueue
    //   4	21	1	localReentrantLock	ReentrantLock
    //   23	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	16	23	finally
  }
  
  public int remainingCapacity()
  {
    return Integer.MAX_VALUE;
  }
  
  public Object[] toArray()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject = q.toArray();
      return arrayOfObject;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject = q.toArray(paramArrayOfT);
      return arrayOfObject;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean remove(Object paramObject)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      boolean bool = q.remove(paramObject);
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  void removeEQ(Object paramObject)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Iterator localIterator = q.iterator();
      while (localIterator.hasNext()) {
        if (paramObject == localIterator.next()) {
          localIterator.remove();
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public Iterator<E> iterator()
  {
    return new Itr(toArray());
  }
  
  private class Itr
    implements Iterator<E>
  {
    final Object[] array;
    int cursor;
    int lastRet = -1;
    
    Itr(Object[] paramArrayOfObject)
    {
      array = paramArrayOfObject;
    }
    
    public boolean hasNext()
    {
      return cursor < array.length;
    }
    
    public E next()
    {
      if (cursor >= array.length) {
        throw new NoSuchElementException();
      }
      lastRet = cursor;
      return (Delayed)array[(cursor++)];
    }
    
    public void remove()
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      removeEQ(array[lastRet]);
      lastRet = -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\DelayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
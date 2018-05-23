package java.util.concurrent;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue<E>
  extends AbstractQueue<E>
  implements BlockingQueue<E>, Serializable
{
  private static final long serialVersionUID = -817911632652898426L;
  final Object[] items;
  int takeIndex;
  int putIndex;
  int count;
  final ReentrantLock lock;
  private final Condition notEmpty;
  private final Condition notFull;
  transient ArrayBlockingQueue<E>.Itrs itrs = null;
  
  final int dec(int paramInt)
  {
    return (paramInt == 0 ? items.length : paramInt) - 1;
  }
  
  final E itemAt(int paramInt)
  {
    return (E)items[paramInt];
  }
  
  private static void checkNotNull(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
  }
  
  private void enqueue(E paramE)
  {
    Object[] arrayOfObject = items;
    arrayOfObject[putIndex] = paramE;
    if (++putIndex == arrayOfObject.length) {
      putIndex = 0;
    }
    count += 1;
    notEmpty.signal();
  }
  
  private E dequeue()
  {
    Object[] arrayOfObject = items;
    Object localObject = arrayOfObject[takeIndex];
    arrayOfObject[takeIndex] = null;
    if (++takeIndex == arrayOfObject.length) {
      takeIndex = 0;
    }
    count -= 1;
    if (itrs != null) {
      itrs.elementDequeued();
    }
    notFull.signal();
    return (E)localObject;
  }
  
  void removeAt(int paramInt)
  {
    Object[] arrayOfObject = items;
    if (paramInt == takeIndex)
    {
      arrayOfObject[takeIndex] = null;
      if (++takeIndex == arrayOfObject.length) {
        takeIndex = 0;
      }
      count -= 1;
      if (itrs != null) {
        itrs.elementDequeued();
      }
    }
    else
    {
      int i = putIndex;
      int j = paramInt;
      for (;;)
      {
        int k = j + 1;
        if (k == arrayOfObject.length) {
          k = 0;
        }
        if (k != i)
        {
          arrayOfObject[j] = arrayOfObject[k];
          j = k;
        }
        else
        {
          arrayOfObject[j] = null;
          putIndex = j;
          break;
        }
      }
      count -= 1;
      if (itrs != null) {
        itrs.removedAt(paramInt);
      }
    }
    notFull.signal();
  }
  
  public ArrayBlockingQueue(int paramInt)
  {
    this(paramInt, false);
  }
  
  public ArrayBlockingQueue(int paramInt, boolean paramBoolean)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    items = new Object[paramInt];
    lock = new ReentrantLock(paramBoolean);
    notEmpty = lock.newCondition();
    notFull = lock.newCondition();
  }
  
  public ArrayBlockingQueue(int paramInt, boolean paramBoolean, Collection<? extends E> paramCollection)
  {
    this(paramInt, paramBoolean);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = 0;
      try
      {
        Iterator localIterator = paramCollection.iterator();
        while (localIterator.hasNext())
        {
          Object localObject1 = localIterator.next();
          checkNotNull(localObject1);
          items[(i++)] = localObject1;
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new IllegalArgumentException();
      }
      count = i;
      putIndex = (i == paramInt ? 0 : i);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean add(E paramE)
  {
    return super.add(paramE);
  }
  
  public boolean offer(E paramE)
  {
    checkNotNull(paramE);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      if (count == items.length)
      {
        bool = false;
        return bool;
      }
      enqueue(paramE);
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public void put(E paramE)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 238	java/util/concurrent/ArrayBlockingQueue:checkNotNull	(Ljava/lang/Object;)V
    //   4: aload_0
    //   5: getfield 218	java/util/concurrent/ArrayBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   8: astore_2
    //   9: aload_2
    //   10: invokevirtual 248	java/util/concurrent/locks/ReentrantLock:lockInterruptibly	()V
    //   13: aload_0
    //   14: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   17: aload_0
    //   18: getfield 214	java/util/concurrent/ArrayBlockingQueue:items	[Ljava/lang/Object;
    //   21: arraylength
    //   22: if_icmpne +15 -> 37
    //   25: aload_0
    //   26: getfield 217	java/util/concurrent/ArrayBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   29: invokeinterface 257 1 0
    //   34: goto -21 -> 13
    //   37: aload_0
    //   38: aload_1
    //   39: invokespecial 239	java/util/concurrent/ArrayBlockingQueue:enqueue	(Ljava/lang/Object;)V
    //   42: aload_2
    //   43: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   46: goto +10 -> 56
    //   49: astore_3
    //   50: aload_2
    //   51: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   54: aload_3
    //   55: athrow
    //   56: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	ArrayBlockingQueue
    //   0	57	1	paramE	E
    //   8	43	2	localReentrantLock	ReentrantLock
    //   49	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	42	49	finally
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    checkNotNull(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (count == items.length)
      {
        if (l <= 0L)
        {
          bool = false;
          return bool;
        }
        l = notFull.awaitNanos(l);
      }
      enqueue(paramE);
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E poll()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = count == 0 ? null : dequeue();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E take()
    throws InterruptedException
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (count == 0) {
        notEmpty.await();
      }
      Object localObject1 = dequeue();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (count == 0)
      {
        if (l <= 0L)
        {
          localObject1 = null;
          return (E)localObject1;
        }
        l = notEmpty.awaitNanos(l);
      }
      Object localObject1 = dequeue();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E peek()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = itemAt(takeIndex);
      return (E)localObject1;
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
      int i = count;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int remainingCapacity()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = items.length - count;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean remove(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Object[] arrayOfObject = items;
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      if (count > 0)
      {
        i = putIndex;
        int j = takeIndex;
        do
        {
          if (paramObject.equals(arrayOfObject[j]))
          {
            removeAt(j);
            boolean bool = true;
            return bool;
          }
          j++;
          if (j == arrayOfObject.length) {
            j = 0;
          }
        } while (j != i);
      }
      int i = 0;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    Object[] arrayOfObject = items;
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      if (count > 0)
      {
        i = putIndex;
        int j = takeIndex;
        do
        {
          if (paramObject.equals(arrayOfObject[j]))
          {
            boolean bool = true;
            return bool;
          }
          j++;
          if (j == arrayOfObject.length) {
            j = 0;
          }
        } while (j != i);
      }
      int i = 0;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public Object[] toArray()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    Object[] arrayOfObject;
    try
    {
      int i = count;
      arrayOfObject = new Object[i];
      int j = items.length - takeIndex;
      if (i <= j)
      {
        System.arraycopy(items, takeIndex, arrayOfObject, 0, i);
      }
      else
      {
        System.arraycopy(items, takeIndex, arrayOfObject, 0, j);
        System.arraycopy(items, 0, arrayOfObject, j, i - j);
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    Object[] arrayOfObject = items;
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = count;
      int j = paramArrayOfT.length;
      if (j < i) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
      }
      int k = arrayOfObject.length - takeIndex;
      if (i <= k)
      {
        System.arraycopy(arrayOfObject, takeIndex, paramArrayOfT, 0, i);
      }
      else
      {
        System.arraycopy(arrayOfObject, takeIndex, paramArrayOfT, 0, k);
        System.arraycopy(arrayOfObject, 0, paramArrayOfT, k, i - k);
      }
      if (j > i) {
        paramArrayOfT[i] = null;
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    return paramArrayOfT;
  }
  
  /* Error */
  public String toString()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 218	java/util/concurrent/ArrayBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 247	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   13: istore_2
    //   14: iload_2
    //   15: ifne +12 -> 27
    //   18: ldc 3
    //   20: astore_3
    //   21: aload_1
    //   22: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   25: aload_3
    //   26: areturn
    //   27: aload_0
    //   28: getfield 214	java/util/concurrent/ArrayBlockingQueue:items	[Ljava/lang/Object;
    //   31: astore_3
    //   32: new 118	java/lang/StringBuilder
    //   35: dup
    //   36: invokespecial 225	java/lang/StringBuilder:<init>	()V
    //   39: astore 4
    //   41: aload 4
    //   43: bipush 91
    //   45: invokevirtual 227	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   48: pop
    //   49: aload_0
    //   50: getfield 213	java/util/concurrent/ArrayBlockingQueue:takeIndex	I
    //   53: istore 5
    //   55: aload_3
    //   56: iload 5
    //   58: aaload
    //   59: astore 6
    //   61: aload 4
    //   63: aload 6
    //   65: aload_0
    //   66: if_acmpne +8 -> 74
    //   69: ldc 2
    //   71: goto +5 -> 76
    //   74: aload 6
    //   76: invokevirtual 228	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   79: pop
    //   80: iinc 2 -1
    //   83: iload_2
    //   84: ifne +22 -> 106
    //   87: aload 4
    //   89: bipush 93
    //   91: invokevirtual 227	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   94: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   97: astore 7
    //   99: aload_1
    //   100: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   103: aload 7
    //   105: areturn
    //   106: aload 4
    //   108: bipush 44
    //   110: invokevirtual 227	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   113: bipush 32
    //   115: invokevirtual 227	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   118: pop
    //   119: iinc 5 1
    //   122: iload 5
    //   124: aload_3
    //   125: arraylength
    //   126: if_icmpne +6 -> 132
    //   129: iconst_0
    //   130: istore 5
    //   132: goto -77 -> 55
    //   135: astore 8
    //   137: aload_1
    //   138: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   141: aload 8
    //   143: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	144	0	this	ArrayBlockingQueue
    //   4	134	1	localReentrantLock	ReentrantLock
    //   13	71	2	i	int
    //   20	105	3	localObject1	Object
    //   39	68	4	localStringBuilder	StringBuilder
    //   53	78	5	j	int
    //   59	16	6	localObject2	Object
    //   97	7	7	str	String
    //   135	7	8	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	135	finally
    //   27	99	135	finally
    //   106	137	135	finally
  }
  
  public void clear()
  {
    Object[] arrayOfObject = items;
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = count;
      if (i > 0)
      {
        int j = putIndex;
        int k = takeIndex;
        do
        {
          arrayOfObject[k] = null;
          k++;
          if (k == arrayOfObject.length) {
            k = 0;
          }
        } while (k != j);
        takeIndex = j;
        count = 0;
        if (itrs != null) {
          itrs.queueIsEmpty();
        }
        while ((i > 0) && (localReentrantLock.hasWaiters(notFull)))
        {
          notFull.signal();
          i--;
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int drainTo(Collection<? super E> paramCollection)
  {
    return drainTo(paramCollection, Integer.MAX_VALUE);
  }
  
  /* Error */
  public int drainTo(Collection<? super E> paramCollection, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 238	java/util/concurrent/ArrayBlockingQueue:checkNotNull	(Ljava/lang/Object;)V
    //   4: aload_1
    //   5: aload_0
    //   6: if_acmpne +11 -> 17
    //   9: new 113	java/lang/IllegalArgumentException
    //   12: dup
    //   13: invokespecial 220	java/lang/IllegalArgumentException:<init>	()V
    //   16: athrow
    //   17: iload_2
    //   18: ifgt +5 -> 23
    //   21: iconst_0
    //   22: ireturn
    //   23: aload_0
    //   24: getfield 214	java/util/concurrent/ArrayBlockingQueue:items	[Ljava/lang/Object;
    //   27: astore_3
    //   28: aload_0
    //   29: getfield 218	java/util/concurrent/ArrayBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   32: astore 4
    //   34: aload 4
    //   36: invokevirtual 247	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   39: iload_2
    //   40: aload_0
    //   41: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   44: invokestatic 221	java/lang/Math:min	(II)I
    //   47: istore 5
    //   49: aload_0
    //   50: getfield 213	java/util/concurrent/ArrayBlockingQueue:takeIndex	I
    //   53: istore 6
    //   55: iconst_0
    //   56: istore 7
    //   58: iload 7
    //   60: iload 5
    //   62: if_icmpge +42 -> 104
    //   65: aload_3
    //   66: iload 6
    //   68: aaload
    //   69: astore 8
    //   71: aload_1
    //   72: aload 8
    //   74: invokeinterface 253 2 0
    //   79: pop
    //   80: aload_3
    //   81: iload 6
    //   83: aconst_null
    //   84: aastore
    //   85: iinc 6 1
    //   88: iload 6
    //   90: aload_3
    //   91: arraylength
    //   92: if_icmpne +6 -> 98
    //   95: iconst_0
    //   96: istore 6
    //   98: iinc 7 1
    //   101: goto -43 -> 58
    //   104: iload 5
    //   106: istore 8
    //   108: iload 7
    //   110: ifle +90 -> 200
    //   113: aload_0
    //   114: dup
    //   115: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   118: iload 7
    //   120: isub
    //   121: putfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   124: aload_0
    //   125: iload 6
    //   127: putfield 213	java/util/concurrent/ArrayBlockingQueue:takeIndex	I
    //   130: aload_0
    //   131: getfield 215	java/util/concurrent/ArrayBlockingQueue:itrs	Ljava/util/concurrent/ArrayBlockingQueue$Itrs;
    //   134: ifnull +34 -> 168
    //   137: aload_0
    //   138: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   141: ifne +13 -> 154
    //   144: aload_0
    //   145: getfield 215	java/util/concurrent/ArrayBlockingQueue:itrs	Ljava/util/concurrent/ArrayBlockingQueue$Itrs;
    //   148: invokevirtual 243	java/util/concurrent/ArrayBlockingQueue$Itrs:queueIsEmpty	()V
    //   151: goto +17 -> 168
    //   154: iload 7
    //   156: iload 6
    //   158: if_icmple +10 -> 168
    //   161: aload_0
    //   162: getfield 215	java/util/concurrent/ArrayBlockingQueue:itrs	Ljava/util/concurrent/ArrayBlockingQueue$Itrs;
    //   165: invokevirtual 244	java/util/concurrent/ArrayBlockingQueue$Itrs:takeIndexWrapped	()V
    //   168: iload 7
    //   170: ifle +30 -> 200
    //   173: aload 4
    //   175: aload_0
    //   176: getfield 217	java/util/concurrent/ArrayBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   179: invokevirtual 252	java/util/concurrent/locks/ReentrantLock:hasWaiters	(Ljava/util/concurrent/locks/Condition;)Z
    //   182: ifeq +18 -> 200
    //   185: aload_0
    //   186: getfield 217	java/util/concurrent/ArrayBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   189: invokeinterface 258 1 0
    //   194: iinc 7 -1
    //   197: goto -29 -> 168
    //   200: aload 4
    //   202: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   205: iload 8
    //   207: ireturn
    //   208: astore 9
    //   210: iload 7
    //   212: ifle +90 -> 302
    //   215: aload_0
    //   216: dup
    //   217: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   220: iload 7
    //   222: isub
    //   223: putfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   226: aload_0
    //   227: iload 6
    //   229: putfield 213	java/util/concurrent/ArrayBlockingQueue:takeIndex	I
    //   232: aload_0
    //   233: getfield 215	java/util/concurrent/ArrayBlockingQueue:itrs	Ljava/util/concurrent/ArrayBlockingQueue$Itrs;
    //   236: ifnull +34 -> 270
    //   239: aload_0
    //   240: getfield 211	java/util/concurrent/ArrayBlockingQueue:count	I
    //   243: ifne +13 -> 256
    //   246: aload_0
    //   247: getfield 215	java/util/concurrent/ArrayBlockingQueue:itrs	Ljava/util/concurrent/ArrayBlockingQueue$Itrs;
    //   250: invokevirtual 243	java/util/concurrent/ArrayBlockingQueue$Itrs:queueIsEmpty	()V
    //   253: goto +17 -> 270
    //   256: iload 7
    //   258: iload 6
    //   260: if_icmple +10 -> 270
    //   263: aload_0
    //   264: getfield 215	java/util/concurrent/ArrayBlockingQueue:itrs	Ljava/util/concurrent/ArrayBlockingQueue$Itrs;
    //   267: invokevirtual 244	java/util/concurrent/ArrayBlockingQueue$Itrs:takeIndexWrapped	()V
    //   270: iload 7
    //   272: ifle +30 -> 302
    //   275: aload 4
    //   277: aload_0
    //   278: getfield 217	java/util/concurrent/ArrayBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   281: invokevirtual 252	java/util/concurrent/locks/ReentrantLock:hasWaiters	(Ljava/util/concurrent/locks/Condition;)Z
    //   284: ifeq +18 -> 302
    //   287: aload_0
    //   288: getfield 217	java/util/concurrent/ArrayBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   291: invokeinterface 258 1 0
    //   296: iinc 7 -1
    //   299: goto -29 -> 270
    //   302: aload 9
    //   304: athrow
    //   305: astore 10
    //   307: aload 4
    //   309: invokevirtual 249	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   312: aload 10
    //   314: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	315	0	this	ArrayBlockingQueue
    //   0	315	1	paramCollection	Collection<? super E>
    //   0	315	2	paramInt	int
    //   27	64	3	arrayOfObject	Object[]
    //   32	276	4	localReentrantLock	ReentrantLock
    //   47	58	5	localObject1	Object
    //   53	208	6	i	int
    //   56	241	7	j	int
    //   69	137	8	localObject2	Object
    //   208	95	9	localObject3	Object
    //   305	8	10	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   58	108	208	finally
    //   208	210	208	finally
    //   39	200	305	finally
    //   208	307	305	finally
  }
  
  public Iterator<E> iterator()
  {
    return new Itr();
  }
  
  public Spliterator<E> spliterator()
  {
    return Spliterators.spliterator(this, 4368);
  }
  
  private class Itr
    implements Iterator<E>
  {
    private int cursor;
    private E nextItem;
    private int nextIndex;
    private E lastItem;
    private int lastRet = -1;
    private int prevTakeIndex;
    private int prevCycles;
    private static final int NONE = -1;
    private static final int REMOVED = -2;
    private static final int DETACHED = -3;
    
    Itr()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        if (count == 0)
        {
          cursor = -1;
          nextIndex = -1;
          prevTakeIndex = -3;
        }
        else
        {
          int i = takeIndex;
          prevTakeIndex = i;
          nextItem = itemAt(nextIndex = i);
          cursor = incCursor(i);
          if (itrs == null)
          {
            itrs = new ArrayBlockingQueue.Itrs(ArrayBlockingQueue.this, this);
          }
          else
          {
            itrs.register(this);
            itrs.doSomeSweeping(false);
          }
          prevCycles = itrs.cycles;
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    boolean isDetached()
    {
      return prevTakeIndex < 0;
    }
    
    private int incCursor(int paramInt)
    {
      
      if (paramInt == items.length) {
        paramInt = 0;
      }
      if (paramInt == putIndex) {
        paramInt = -1;
      }
      return paramInt;
    }
    
    private boolean invalidated(int paramInt1, int paramInt2, long paramLong, int paramInt3)
    {
      if (paramInt1 < 0) {
        return false;
      }
      int i = paramInt1 - paramInt2;
      if (i < 0) {
        i += paramInt3;
      }
      return paramLong > i;
    }
    
    private void incorporateDequeues()
    {
      int i = itrs.cycles;
      int j = takeIndex;
      int k = prevCycles;
      int m = prevTakeIndex;
      if ((i != k) || (j != m))
      {
        int n = items.length;
        long l = (i - k) * n + (j - m);
        if (invalidated(lastRet, m, l, n)) {
          lastRet = -2;
        }
        if (invalidated(nextIndex, m, l, n)) {
          nextIndex = -2;
        }
        if (invalidated(cursor, m, l, n)) {
          cursor = j;
        }
        if ((cursor < 0) && (nextIndex < 0) && (lastRet < 0))
        {
          detach();
        }
        else
        {
          prevCycles = i;
          prevTakeIndex = j;
        }
      }
    }
    
    private void detach()
    {
      if (prevTakeIndex >= 0)
      {
        prevTakeIndex = -3;
        itrs.doSomeSweeping(true);
      }
    }
    
    public boolean hasNext()
    {
      if (nextItem != null) {
        return true;
      }
      noNext();
      return false;
    }
    
    /* Error */
    private void noNext()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 132	java/util/concurrent/ArrayBlockingQueue$Itr:this$0	Ljava/util/concurrent/ArrayBlockingQueue;
      //   4: getfield 124	java/util/concurrent/ArrayBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   7: astore_1
      //   8: aload_1
      //   9: invokevirtual 151	java/util/concurrent/locks/ReentrantLock:lock	()V
      //   12: aload_0
      //   13: invokevirtual 144	java/util/concurrent/ArrayBlockingQueue$Itr:isDetached	()Z
      //   16: ifne +33 -> 49
      //   19: aload_0
      //   20: invokespecial 141	java/util/concurrent/ArrayBlockingQueue$Itr:incorporateDequeues	()V
      //   23: aload_0
      //   24: getfield 126	java/util/concurrent/ArrayBlockingQueue$Itr:lastRet	I
      //   27: iflt +22 -> 49
      //   30: aload_0
      //   31: aload_0
      //   32: getfield 132	java/util/concurrent/ArrayBlockingQueue$Itr:this$0	Ljava/util/concurrent/ArrayBlockingQueue;
      //   35: aload_0
      //   36: getfield 126	java/util/concurrent/ArrayBlockingQueue$Itr:lastRet	I
      //   39: invokevirtual 139	java/util/concurrent/ArrayBlockingQueue:itemAt	(I)Ljava/lang/Object;
      //   42: putfield 130	java/util/concurrent/ArrayBlockingQueue$Itr:lastItem	Ljava/lang/Object;
      //   45: aload_0
      //   46: invokespecial 140	java/util/concurrent/ArrayBlockingQueue$Itr:detach	()V
      //   49: aload_1
      //   50: invokevirtual 152	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   53: goto +10 -> 63
      //   56: astore_2
      //   57: aload_1
      //   58: invokevirtual 152	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   61: aload_2
      //   62: athrow
      //   63: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	64	0	this	Itr
      //   7	51	1	localReentrantLock	ReentrantLock
      //   56	6	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   12	49	56	finally
    }
    
    public E next()
    {
      Object localObject1 = nextItem;
      if (localObject1 == null) {
        throw new NoSuchElementException();
      }
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        if (!isDetached()) {
          incorporateDequeues();
        }
        lastRet = nextIndex;
        int i = cursor;
        if (i >= 0)
        {
          nextItem = itemAt(nextIndex = i);
          cursor = incCursor(i);
        }
        else
        {
          nextIndex = -1;
          nextItem = null;
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
      return (E)localObject1;
    }
    
    public void remove()
    {
      ReentrantLock localReentrantLock = lock;
      localReentrantLock.lock();
      try
      {
        if (!isDetached()) {
          incorporateDequeues();
        }
        int i = lastRet;
        lastRet = -1;
        if (i >= 0)
        {
          if (!isDetached())
          {
            removeAt(i);
          }
          else
          {
            Object localObject1 = lastItem;
            lastItem = null;
            if (itemAt(i) == localObject1) {
              removeAt(i);
            }
          }
        }
        else if (i == -1) {
          throw new IllegalStateException();
        }
        if ((cursor < 0) && (nextIndex < 0)) {
          detach();
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    void shutdown()
    {
      cursor = -1;
      if (nextIndex >= 0) {
        nextIndex = -2;
      }
      if (lastRet >= 0)
      {
        lastRet = -2;
        lastItem = null;
      }
      prevTakeIndex = -3;
    }
    
    private int distance(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt1 - paramInt2;
      if (i < 0) {
        i += paramInt3;
      }
      return i;
    }
    
    boolean removedAt(int paramInt)
    {
      if (isDetached()) {
        return true;
      }
      int i = itrs.cycles;
      int j = takeIndex;
      int k = prevCycles;
      int m = prevTakeIndex;
      int n = items.length;
      int i1 = i - k;
      if (paramInt < j) {
        i1++;
      }
      int i2 = i1 * n + (paramInt - m);
      int i3 = cursor;
      if (i3 >= 0)
      {
        i4 = distance(i3, m, n);
        if (i4 == i2)
        {
          if (i3 == putIndex) {
            cursor = (i3 = -1);
          }
        }
        else if (i4 > i2) {
          cursor = (i3 = dec(i3));
        }
      }
      int i4 = lastRet;
      if (i4 >= 0)
      {
        i5 = distance(i4, m, n);
        if (i5 == i2) {
          lastRet = (i4 = -2);
        } else if (i5 > i2) {
          lastRet = (i4 = dec(i4));
        }
      }
      int i5 = nextIndex;
      if (i5 >= 0)
      {
        int i6 = distance(i5, m, n);
        if (i6 == i2) {
          nextIndex = (i5 = -2);
        } else if (i6 > i2) {
          nextIndex = (i5 = dec(i5));
        }
      }
      else if ((i3 < 0) && (i5 < 0) && (i4 < 0))
      {
        prevTakeIndex = -3;
        return true;
      }
      return false;
    }
    
    boolean takeIndexWrapped()
    {
      if (isDetached()) {
        return true;
      }
      if (itrs.cycles - prevCycles > 1)
      {
        shutdown();
        return true;
      }
      return false;
    }
  }
  
  class Itrs
  {
    int cycles = 0;
    private ArrayBlockingQueue<E>.Itrs.Node head;
    private ArrayBlockingQueue<E>.Itrs.Node sweeper = null;
    private static final int SHORT_SWEEP_PROBES = 4;
    private static final int LONG_SWEEP_PROBES = 16;
    
    Itrs()
    {
      ArrayBlockingQueue.Itr localItr;
      register(localItr);
    }
    
    void doSomeSweeping(boolean paramBoolean)
    {
      int i = paramBoolean ? 16 : 4;
      Node localNode1 = sweeper;
      Object localObject1;
      Object localObject2;
      int j;
      if (localNode1 == null)
      {
        localObject1 = null;
        localObject2 = head;
        j = 1;
      }
      else
      {
        localObject1 = localNode1;
        localObject2 = next;
        j = 0;
      }
      while (i > 0)
      {
        if (localObject2 == null)
        {
          if (j != 0) {
            break;
          }
          localObject1 = null;
          localObject2 = head;
          j = 1;
        }
        ArrayBlockingQueue.Itr localItr = (ArrayBlockingQueue.Itr)((Node)localObject2).get();
        Node localNode2 = next;
        if ((localItr == null) || (localItr.isDetached()))
        {
          i = 16;
          ((Node)localObject2).clear();
          next = null;
          if (localObject1 == null)
          {
            head = localNode2;
            if (localNode2 == null) {
              itrs = null;
            }
          }
          else
          {
            next = localNode2;
          }
        }
        else
        {
          localObject1 = localObject2;
        }
        localObject2 = localNode2;
        i--;
      }
      sweeper = (localObject2 == null ? null : (Node)localObject1);
    }
    
    void register(ArrayBlockingQueue<E>.Itr paramArrayBlockingQueue)
    {
      head = new Node(paramArrayBlockingQueue, head);
    }
    
    void takeIndexWrapped()
    {
      cycles += 1;
      Object localObject1 = null;
      Node localNode;
      for (Object localObject2 = head; localObject2 != null; localObject2 = localNode)
      {
        ArrayBlockingQueue.Itr localItr = (ArrayBlockingQueue.Itr)((Node)localObject2).get();
        localNode = next;
        if ((localItr == null) || (localItr.takeIndexWrapped()))
        {
          ((Node)localObject2).clear();
          next = null;
          if (localObject1 == null) {
            head = localNode;
          } else {
            next = localNode;
          }
        }
        else
        {
          localObject1 = localObject2;
        }
      }
      if (head == null) {
        itrs = null;
      }
    }
    
    void removedAt(int paramInt)
    {
      Object localObject1 = null;
      Node localNode;
      for (Object localObject2 = head; localObject2 != null; localObject2 = localNode)
      {
        ArrayBlockingQueue.Itr localItr = (ArrayBlockingQueue.Itr)((Node)localObject2).get();
        localNode = next;
        if ((localItr == null) || (localItr.removedAt(paramInt)))
        {
          ((Node)localObject2).clear();
          next = null;
          if (localObject1 == null) {
            head = localNode;
          } else {
            next = localNode;
          }
        }
        else
        {
          localObject1 = localObject2;
        }
      }
      if (head == null) {
        itrs = null;
      }
    }
    
    void queueIsEmpty()
    {
      for (Node localNode = head; localNode != null; localNode = next)
      {
        ArrayBlockingQueue.Itr localItr = (ArrayBlockingQueue.Itr)localNode.get();
        if (localItr != null)
        {
          localNode.clear();
          localItr.shutdown();
        }
      }
      head = null;
      itrs = null;
    }
    
    void elementDequeued()
    {
      if (count == 0) {
        queueIsEmpty();
      } else if (takeIndex == 0) {
        takeIndexWrapped();
      }
    }
    
    private class Node
      extends WeakReference<ArrayBlockingQueue<E>.Itr>
    {
      ArrayBlockingQueue<E>.Itrs.Node next;
      
      Node(ArrayBlockingQueue<E>.Itrs.Node paramArrayBlockingQueue)
      {
        super();
        Node localNode;
        next = localNode;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ArrayBlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
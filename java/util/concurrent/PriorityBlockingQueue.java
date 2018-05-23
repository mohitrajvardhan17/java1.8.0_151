package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class PriorityBlockingQueue<E>
  extends AbstractQueue<E>
  implements BlockingQueue<E>, Serializable
{
  private static final long serialVersionUID = 5595510919245408276L;
  private static final int DEFAULT_INITIAL_CAPACITY = 11;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  private transient Object[] queue;
  private transient int size;
  private transient Comparator<? super E> comparator;
  private final ReentrantLock lock;
  private final Condition notEmpty;
  private volatile transient int allocationSpinLock;
  private PriorityQueue<E> q;
  private static final Unsafe UNSAFE;
  private static final long allocationSpinLockOffset;
  
  public PriorityBlockingQueue()
  {
    this(11, null);
  }
  
  public PriorityBlockingQueue(int paramInt)
  {
    this(paramInt, null);
  }
  
  public PriorityBlockingQueue(int paramInt, Comparator<? super E> paramComparator)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException();
    }
    lock = new ReentrantLock();
    notEmpty = lock.newCondition();
    comparator = paramComparator;
    queue = new Object[paramInt];
  }
  
  public PriorityBlockingQueue(Collection<? extends E> paramCollection)
  {
    lock = new ReentrantLock();
    notEmpty = lock.newCondition();
    int i = 1;
    int j = 1;
    if ((paramCollection instanceof SortedSet))
    {
      localObject = (SortedSet)paramCollection;
      comparator = ((SortedSet)localObject).comparator();
      i = 0;
    }
    else if ((paramCollection instanceof PriorityBlockingQueue))
    {
      localObject = (PriorityBlockingQueue)paramCollection;
      comparator = ((PriorityBlockingQueue)localObject).comparator();
      j = 0;
      if (localObject.getClass() == PriorityBlockingQueue.class) {
        i = 0;
      }
    }
    Object localObject = paramCollection.toArray();
    int k = localObject.length;
    if (localObject.getClass() != Object[].class) {
      localObject = Arrays.copyOf((Object[])localObject, k, Object[].class);
    }
    if ((j != 0) && ((k == 1) || (comparator != null))) {
      for (int m = 0; m < k; m++) {
        if (localObject[m] == null) {
          throw new NullPointerException();
        }
      }
    }
    queue = ((Object[])localObject);
    size = k;
    if (i != 0) {
      heapify();
    }
  }
  
  private void tryGrow(Object[] paramArrayOfObject, int paramInt)
  {
    lock.unlock();
    Object[] arrayOfObject = null;
    if ((allocationSpinLock == 0) && (UNSAFE.compareAndSwapInt(this, allocationSpinLockOffset, 0, 1))) {
      try
      {
        int i = paramInt + (paramInt < 64 ? paramInt + 2 : paramInt >> 1);
        if (i - 2147483639 > 0)
        {
          int j = paramInt + 1;
          if ((j < 0) || (j > 2147483639)) {
            throw new OutOfMemoryError();
          }
          i = 2147483639;
        }
        if ((i > paramInt) && (queue == paramArrayOfObject)) {
          arrayOfObject = new Object[i];
        }
      }
      finally
      {
        allocationSpinLock = 0;
      }
    }
    if (arrayOfObject == null) {
      Thread.yield();
    }
    lock.lock();
    if ((arrayOfObject != null) && (queue == paramArrayOfObject))
    {
      queue = arrayOfObject;
      System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramInt);
    }
  }
  
  private E dequeue()
  {
    int i = size - 1;
    if (i < 0) {
      return null;
    }
    Object[] arrayOfObject = queue;
    Object localObject1 = arrayOfObject[0];
    Object localObject2 = arrayOfObject[i];
    arrayOfObject[i] = null;
    Comparator localComparator = comparator;
    if (localComparator == null) {
      siftDownComparable(0, localObject2, arrayOfObject, i);
    } else {
      siftDownUsingComparator(0, localObject2, arrayOfObject, i, localComparator);
    }
    size = i;
    return (E)localObject1;
  }
  
  private static <T> void siftUpComparable(int paramInt, T paramT, Object[] paramArrayOfObject)
  {
    Comparable localComparable = (Comparable)paramT;
    while (paramInt > 0)
    {
      int i = paramInt - 1 >>> 1;
      Object localObject = paramArrayOfObject[i];
      if (localComparable.compareTo(localObject) >= 0) {
        break;
      }
      paramArrayOfObject[paramInt] = localObject;
      paramInt = i;
    }
    paramArrayOfObject[paramInt] = localComparable;
  }
  
  private static <T> void siftUpUsingComparator(int paramInt, T paramT, Object[] paramArrayOfObject, Comparator<? super T> paramComparator)
  {
    while (paramInt > 0)
    {
      int i = paramInt - 1 >>> 1;
      Object localObject = paramArrayOfObject[i];
      if (paramComparator.compare(paramT, localObject) >= 0) {
        break;
      }
      paramArrayOfObject[paramInt] = localObject;
      paramInt = i;
    }
    paramArrayOfObject[paramInt] = paramT;
  }
  
  private static <T> void siftDownComparable(int paramInt1, T paramT, Object[] paramArrayOfObject, int paramInt2)
  {
    if (paramInt2 > 0)
    {
      Comparable localComparable = (Comparable)paramT;
      int i = paramInt2 >>> 1;
      while (paramInt1 < i)
      {
        int j = (paramInt1 << 1) + 1;
        Object localObject = paramArrayOfObject[j];
        int k = j + 1;
        if ((k < paramInt2) && (((Comparable)localObject).compareTo(paramArrayOfObject[k]) > 0)) {
          localObject = paramArrayOfObject[(j = k)];
        }
        if (localComparable.compareTo(localObject) <= 0) {
          break;
        }
        paramArrayOfObject[paramInt1] = localObject;
        paramInt1 = j;
      }
      paramArrayOfObject[paramInt1] = localComparable;
    }
  }
  
  private static <T> void siftDownUsingComparator(int paramInt1, T paramT, Object[] paramArrayOfObject, int paramInt2, Comparator<? super T> paramComparator)
  {
    if (paramInt2 > 0)
    {
      int i = paramInt2 >>> 1;
      while (paramInt1 < i)
      {
        int j = (paramInt1 << 1) + 1;
        Object localObject = paramArrayOfObject[j];
        int k = j + 1;
        if ((k < paramInt2) && (paramComparator.compare(localObject, paramArrayOfObject[k]) > 0)) {
          localObject = paramArrayOfObject[(j = k)];
        }
        if (paramComparator.compare(paramT, localObject) <= 0) {
          break;
        }
        paramArrayOfObject[paramInt1] = localObject;
        paramInt1 = j;
      }
      paramArrayOfObject[paramInt1] = paramT;
    }
  }
  
  private void heapify()
  {
    Object[] arrayOfObject = queue;
    int i = size;
    int j = (i >>> 1) - 1;
    Comparator localComparator = comparator;
    int k;
    if (localComparator == null) {
      for (k = j; k >= 0; k--) {
        siftDownComparable(k, arrayOfObject[k], arrayOfObject, i);
      }
    } else {
      for (k = j; k >= 0; k--) {
        siftDownUsingComparator(k, arrayOfObject[k], arrayOfObject, i, localComparator);
      }
    }
  }
  
  public boolean add(E paramE)
  {
    return offer(paramE);
  }
  
  public boolean offer(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    int i;
    Object[] arrayOfObject;
    int j;
    while ((i = size) >= (j = (arrayOfObject = queue).length)) {
      tryGrow(arrayOfObject, j);
    }
    try
    {
      Comparator localComparator = comparator;
      if (localComparator == null) {
        siftUpComparable(i, paramE, arrayOfObject);
      } else {
        siftUpUsingComparator(i, paramE, arrayOfObject, localComparator);
      }
      size = (i + 1);
      notEmpty.signal();
    }
    finally
    {
      localReentrantLock.unlock();
    }
    return true;
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
      Object localObject1 = dequeue();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public E take()
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 285	java/util/concurrent/PriorityBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 331	java/util/concurrent/locks/ReentrantLock:lockInterruptibly	()V
    //   9: aload_0
    //   10: invokespecial 313	java/util/concurrent/PriorityBlockingQueue:dequeue	()Ljava/lang/Object;
    //   13: dup
    //   14: astore_2
    //   15: ifnonnull +15 -> 30
    //   18: aload_0
    //   19: getfield 284	java/util/concurrent/PriorityBlockingQueue:notEmpty	Ljava/util/concurrent/locks/Condition;
    //   22: invokeinterface 342 1 0
    //   27: goto -18 -> 9
    //   30: aload_1
    //   31: invokevirtual 332	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   34: goto +10 -> 44
    //   37: astore_3
    //   38: aload_1
    //   39: invokevirtual 332	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   42: aload_3
    //   43: athrow
    //   44: aload_2
    //   45: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	46	0	this	PriorityBlockingQueue
    //   4	35	1	localReentrantLock	ReentrantLock
    //   14	31	2	localObject1	Object
    //   37	6	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   9	30	37	finally
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    Object localObject1;
    try
    {
      while (((localObject1 = dequeue()) == null) && (l > 0L)) {
        l = notEmpty.awaitNanos(l);
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    return (E)localObject1;
  }
  
  public E peek()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = size == 0 ? null : queue[0];
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public Comparator<? super E> comparator()
  {
    return comparator;
  }
  
  public int size()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = size;
      return i;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int remainingCapacity()
  {
    return Integer.MAX_VALUE;
  }
  
  private int indexOf(Object paramObject)
  {
    if (paramObject != null)
    {
      Object[] arrayOfObject = queue;
      int i = size;
      for (int j = 0; j < i; j++) {
        if (paramObject.equals(arrayOfObject[j])) {
          return j;
        }
      }
    }
    return -1;
  }
  
  private void removeAt(int paramInt)
  {
    Object[] arrayOfObject = queue;
    int i = size - 1;
    if (i == paramInt)
    {
      arrayOfObject[paramInt] = null;
    }
    else
    {
      Object localObject = arrayOfObject[i];
      arrayOfObject[i] = null;
      Comparator localComparator = comparator;
      if (localComparator == null) {
        siftDownComparable(paramInt, localObject, arrayOfObject, i);
      } else {
        siftDownUsingComparator(paramInt, localObject, arrayOfObject, i, localComparator);
      }
      if (arrayOfObject[paramInt] == localObject) {
        if (localComparator == null) {
          siftUpComparable(paramInt, localObject, arrayOfObject);
        } else {
          siftUpUsingComparator(paramInt, localObject, arrayOfObject, localComparator);
        }
      }
    }
    size = i;
  }
  
  public boolean remove(Object paramObject)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = indexOf(paramObject);
      if (i == -1)
      {
        bool = false;
        return bool;
      }
      removeAt(i);
      boolean bool = true;
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
      Object[] arrayOfObject = queue;
      int i = 0;
      int j = size;
      while (i < j)
      {
        if (paramObject == arrayOfObject[i])
        {
          removeAt(i);
          break;
        }
        i++;
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean contains(Object paramObject)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      boolean bool = indexOf(paramObject) != -1;
      return bool;
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
    try
    {
      Object[] arrayOfObject = Arrays.copyOf(queue, size);
      return arrayOfObject;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public String toString()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = size;
      if (i == 0)
      {
        localObject1 = "[]";
        return (String)localObject1;
      }
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append('[');
      for (int j = 0; j < i; j++)
      {
        Object localObject2 = queue[j];
        ((StringBuilder)localObject1).append(localObject2 == this ? "(this Collection)" : localObject2);
        if (j != i - 1) {
          ((StringBuilder)localObject1).append(',').append(' ');
        }
      }
      String str = ']';
      return str;
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
      int i = Math.min(size, paramInt);
      for (int j = 0; j < i; j++)
      {
        paramCollection.add(queue[0]);
        dequeue();
      }
      j = i;
      return j;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void clear()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject = queue;
      int i = size;
      size = 0;
      for (int j = 0; j < i; j++) {
        arrayOfObject[j] = null;
      }
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
      int i = size;
      if (paramArrayOfT.length < i)
      {
        localObject1 = (Object[])Arrays.copyOf(queue, size, paramArrayOfT.getClass());
        return (T[])localObject1;
      }
      System.arraycopy(queue, 0, paramArrayOfT, 0, i);
      if (paramArrayOfT.length > i) {
        paramArrayOfT[i] = null;
      }
      Object localObject1 = paramArrayOfT;
      return (T[])localObject1;
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
  
  /* Error */
  private void writeObject(java.io.ObjectOutputStream paramObjectOutputStream)
    throws java.io.IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 285	java/util/concurrent/PriorityBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 330	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: new 158	java/util/PriorityQueue
    //   11: dup
    //   12: aload_0
    //   13: getfield 279	java/util/concurrent/PriorityBlockingQueue:size	I
    //   16: iconst_1
    //   17: invokestatic 292	java/lang/Math:max	(II)I
    //   20: aload_0
    //   21: getfield 282	java/util/concurrent/PriorityBlockingQueue:comparator	Ljava/util/Comparator;
    //   24: invokespecial 310	java/util/PriorityQueue:<init>	(ILjava/util/Comparator;)V
    //   27: putfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   30: aload_0
    //   31: getfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   34: aload_0
    //   35: invokevirtual 308	java/util/PriorityQueue:addAll	(Ljava/util/Collection;)Z
    //   38: pop
    //   39: aload_1
    //   40: invokevirtual 288	java/io/ObjectOutputStream:defaultWriteObject	()V
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   48: aload_0
    //   49: getfield 285	java/util/concurrent/PriorityBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   52: invokevirtual 332	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   55: goto +18 -> 73
    //   58: astore_2
    //   59: aload_0
    //   60: aconst_null
    //   61: putfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   64: aload_0
    //   65: getfield 285	java/util/concurrent/PriorityBlockingQueue:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   68: invokevirtual 332	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   71: aload_2
    //   72: athrow
    //   73: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	PriorityBlockingQueue
    //   0	74	1	paramObjectOutputStream	java.io.ObjectOutputStream
    //   58	14	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	43	58	finally
  }
  
  /* Error */
  private void readObject(java.io.ObjectInputStream paramObjectInputStream)
    throws java.io.IOException, java.lang.ClassNotFoundException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 287	java/io/ObjectInputStream:defaultReadObject	()V
    //   4: aload_0
    //   5: aload_0
    //   6: getfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   9: invokevirtual 307	java/util/PriorityQueue:size	()I
    //   12: anewarray 148	java/lang/Object
    //   15: putfield 281	java/util/concurrent/PriorityBlockingQueue:queue	[Ljava/lang/Object;
    //   18: aload_0
    //   19: aload_0
    //   20: getfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   23: invokevirtual 309	java/util/PriorityQueue:comparator	()Ljava/util/Comparator;
    //   26: putfield 282	java/util/concurrent/PriorityBlockingQueue:comparator	Ljava/util/Comparator;
    //   29: aload_0
    //   30: aload_0
    //   31: getfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   34: invokevirtual 318	java/util/concurrent/PriorityBlockingQueue:addAll	(Ljava/util/Collection;)Z
    //   37: pop
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   43: goto +11 -> 54
    //   46: astore_2
    //   47: aload_0
    //   48: aconst_null
    //   49: putfield 283	java/util/concurrent/PriorityBlockingQueue:q	Ljava/util/PriorityQueue;
    //   52: aload_2
    //   53: athrow
    //   54: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	PriorityBlockingQueue
    //   0	55	1	paramObjectInputStream	java.io.ObjectInputStream
    //   46	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	38	46	finally
  }
  
  public Spliterator<E> spliterator()
  {
    return new PBQSpliterator(this, null, 0, -1);
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = PriorityBlockingQueue.class;
      allocationSpinLockOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("allocationSpinLock"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  final class Itr
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
      return (E)array[(cursor++)];
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
  
  static final class PBQSpliterator<E>
    implements Spliterator<E>
  {
    final PriorityBlockingQueue<E> queue;
    Object[] array;
    int index;
    int fence;
    
    PBQSpliterator(PriorityBlockingQueue<E> paramPriorityBlockingQueue, Object[] paramArrayOfObject, int paramInt1, int paramInt2)
    {
      queue = paramPriorityBlockingQueue;
      array = paramArrayOfObject;
      index = paramInt1;
      fence = paramInt2;
    }
    
    final int getFence()
    {
      int i;
      if ((i = fence) < 0) {
        i = fence = (array = queue.toArray()).length;
      }
      return i;
    }
    
    public Spliterator<E> trySplit()
    {
      int i = getFence();
      int j = index;
      int k = j + i >>> 1;
      return j >= k ? null : new PBQSpliterator(queue, array, j, index = k);
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject;
      if ((arrayOfObject = array) == null) {
        fence = (arrayOfObject = queue.toArray()).length;
      }
      int j;
      int i;
      if (((j = fence) <= arrayOfObject.length) && ((i = index) >= 0) && (i < (index = j))) {
        do
        {
          paramConsumer.accept(arrayOfObject[i]);
          i++;
        } while (i < j);
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      if ((getFence() > index) && (index >= 0))
      {
        Object localObject = array[(index++)];
        paramConsumer.accept(localObject);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      return getFence() - index;
    }
    
    public int characteristics()
    {
      return 16704;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\PriorityBlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
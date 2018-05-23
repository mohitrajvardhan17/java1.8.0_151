package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingQueue<E>
  extends AbstractQueue<E>
  implements BlockingQueue<E>, Serializable
{
  private static final long serialVersionUID = -6903933977591709194L;
  private final int capacity;
  private final AtomicInteger count = new AtomicInteger();
  transient Node<E> head;
  private transient Node<E> last;
  private final ReentrantLock takeLock = new ReentrantLock();
  private final Condition notEmpty = takeLock.newCondition();
  private final ReentrantLock putLock = new ReentrantLock();
  private final Condition notFull = putLock.newCondition();
  
  /* Error */
  private void signalNotEmpty()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 246	java/util/concurrent/LinkedBlockingQueue:takeLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 288	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: getfield 243	java/util/concurrent/LinkedBlockingQueue:notEmpty	Ljava/util/concurrent/locks/Condition;
    //   13: invokeinterface 297 1 0
    //   18: aload_1
    //   19: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   22: goto +10 -> 32
    //   25: astore_2
    //   26: aload_1
    //   27: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   30: aload_2
    //   31: athrow
    //   32: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	LinkedBlockingQueue
    //   4	23	1	localReentrantLock	ReentrantLock
    //   25	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	18	25	finally
  }
  
  /* Error */
  private void signalNotFull()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 245	java/util/concurrent/LinkedBlockingQueue:putLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 288	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: getfield 244	java/util/concurrent/LinkedBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   13: invokeinterface 297 1 0
    //   18: aload_1
    //   19: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   22: goto +10 -> 32
    //   25: astore_2
    //   26: aload_1
    //   27: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   30: aload_2
    //   31: athrow
    //   32: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	LinkedBlockingQueue
    //   4	23	1	localReentrantLock	ReentrantLock
    //   25	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	18	25	finally
  }
  
  private void enqueue(Node<E> paramNode)
  {
    last = (last.next = paramNode);
  }
  
  private E dequeue()
  {
    Node localNode1 = head;
    Node localNode2 = next;
    next = localNode1;
    head = localNode2;
    Object localObject = item;
    item = null;
    return (E)localObject;
  }
  
  void fullyLock()
  {
    putLock.lock();
    takeLock.lock();
  }
  
  void fullyUnlock()
  {
    takeLock.unlock();
    putLock.unlock();
  }
  
  public LinkedBlockingQueue()
  {
    this(Integer.MAX_VALUE);
  }
  
  public LinkedBlockingQueue(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    capacity = paramInt;
    last = (head = new Node(null));
  }
  
  public LinkedBlockingQueue(Collection<? extends E> paramCollection)
  {
    this(Integer.MAX_VALUE);
    ReentrantLock localReentrantLock = putLock;
    localReentrantLock.lock();
    try
    {
      int i = 0;
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        if (localObject1 == null) {
          throw new NullPointerException();
        }
        if (i == capacity) {
          throw new IllegalStateException("Queue full");
        }
        enqueue(new Node(localObject1));
        i++;
      }
      count.set(i);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int size()
  {
    return count.get();
  }
  
  public int remainingCapacity()
  {
    return capacity - count.get();
  }
  
  public void put(E paramE)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    int i = -1;
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = putLock;
    AtomicInteger localAtomicInteger = count;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (localAtomicInteger.get() == capacity) {
        notFull.await();
      }
      enqueue(localNode);
      i = localAtomicInteger.getAndIncrement();
      if (i + 1 < capacity) {
        notFull.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == 0) {
      signalNotEmpty();
    }
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    long l = paramTimeUnit.toNanos(paramLong);
    int i = -1;
    ReentrantLock localReentrantLock = putLock;
    AtomicInteger localAtomicInteger = count;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (localAtomicInteger.get() == capacity)
      {
        if (l <= 0L)
        {
          boolean bool = false;
          return bool;
        }
        l = notFull.awaitNanos(l);
      }
      enqueue(new Node(paramE));
      i = localAtomicInteger.getAndIncrement();
      if (i + 1 < capacity) {
        notFull.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == 0) {
      signalNotEmpty();
    }
    return true;
  }
  
  public boolean offer(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    AtomicInteger localAtomicInteger = count;
    if (localAtomicInteger.get() == capacity) {
      return false;
    }
    int i = -1;
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = putLock;
    localReentrantLock.lock();
    try
    {
      if (localAtomicInteger.get() < capacity)
      {
        enqueue(localNode);
        i = localAtomicInteger.getAndIncrement();
        if (i + 1 < capacity) {
          notFull.signal();
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == 0) {
      signalNotEmpty();
    }
    return i >= 0;
  }
  
  public E take()
    throws InterruptedException
  {
    int i = -1;
    AtomicInteger localAtomicInteger = count;
    ReentrantLock localReentrantLock = takeLock;
    localReentrantLock.lockInterruptibly();
    Object localObject1;
    try
    {
      while (localAtomicInteger.get() == 0) {
        notEmpty.await();
      }
      localObject1 = dequeue();
      i = localAtomicInteger.getAndDecrement();
      if (i > 1) {
        notEmpty.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == capacity) {
      signalNotFull();
    }
    return (E)localObject1;
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    Object localObject1 = null;
    int i = -1;
    long l = paramTimeUnit.toNanos(paramLong);
    AtomicInteger localAtomicInteger = count;
    ReentrantLock localReentrantLock = takeLock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (localAtomicInteger.get() == 0)
      {
        if (l <= 0L)
        {
          E ? = null;
          return ?;
        }
        l = notEmpty.awaitNanos(l);
      }
      localObject1 = dequeue();
      i = localAtomicInteger.getAndDecrement();
      if (i > 1) {
        notEmpty.signal();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == capacity) {
      signalNotFull();
    }
    return (E)localObject1;
  }
  
  public E poll()
  {
    AtomicInteger localAtomicInteger = count;
    if (localAtomicInteger.get() == 0) {
      return null;
    }
    Object localObject1 = null;
    int i = -1;
    ReentrantLock localReentrantLock = takeLock;
    localReentrantLock.lock();
    try
    {
      if (localAtomicInteger.get() > 0)
      {
        localObject1 = dequeue();
        i = localAtomicInteger.getAndDecrement();
        if (i > 1) {
          notEmpty.signal();
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
    if (i == capacity) {
      signalNotFull();
    }
    return (E)localObject1;
  }
  
  public E peek()
  {
    if (count.get() == 0) {
      return null;
    }
    ReentrantLock localReentrantLock = takeLock;
    localReentrantLock.lock();
    try
    {
      Node localNode = head.next;
      if (localNode == null)
      {
        localObject1 = null;
        return (E)localObject1;
      }
      Object localObject1 = item;
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  void unlink(Node<E> paramNode1, Node<E> paramNode2)
  {
    item = null;
    next = next;
    if (last == paramNode1) {
      last = paramNode2;
    }
    if (count.getAndDecrement() == capacity) {
      notFull.signal();
    }
  }
  
  public boolean remove(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    fullyLock();
    try
    {
      Object localObject1 = head;
      for (Node localNode = next; localNode != null; localNode = next)
      {
        if (paramObject.equals(item))
        {
          unlink(localNode, (Node)localObject1);
          boolean bool2 = true;
          return bool2;
        }
        localObject1 = localNode;
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    fullyLock();
    try
    {
      for (Node localNode = head.next; localNode != null; localNode = next) {
        if (paramObject.equals(item))
        {
          boolean bool2 = true;
          return bool2;
        }
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public Object[] toArray()
  {
    fullyLock();
    try
    {
      int i = count.get();
      Object[] arrayOfObject = new Object[i];
      int j = 0;
      for (Object localObject1 = head.next; localObject1 != null; localObject1 = next) {
        arrayOfObject[(j++)] = item;
      }
      localObject1 = arrayOfObject;
      return (Object[])localObject1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    fullyLock();
    try
    {
      int i = count.get();
      if (paramArrayOfT.length < i) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
      }
      int j = 0;
      for (Object localObject1 = head.next; localObject1 != null; localObject1 = next) {
        paramArrayOfT[(j++)] = item;
      }
      if (paramArrayOfT.length > j) {
        paramArrayOfT[j] = null;
      }
      localObject1 = paramArrayOfT;
      return (T[])localObject1;
    }
    finally
    {
      fullyUnlock();
    }
  }
  
  /* Error */
  public String toString()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 266	java/util/concurrent/LinkedBlockingQueue:fullyLock	()V
    //   4: aload_0
    //   5: getfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   8: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   11: astore_1
    //   12: aload_1
    //   13: ifnonnull +12 -> 25
    //   16: ldc 4
    //   18: astore_2
    //   19: aload_0
    //   20: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   23: aload_2
    //   24: areturn
    //   25: new 137	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 260	java/lang/StringBuilder:<init>	()V
    //   32: astore_2
    //   33: aload_2
    //   34: bipush 91
    //   36: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   39: pop
    //   40: aload_1
    //   41: getfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   44: astore_3
    //   45: aload_2
    //   46: aload_3
    //   47: aload_0
    //   48: if_acmpne +8 -> 56
    //   51: ldc 2
    //   53: goto +4 -> 57
    //   56: aload_3
    //   57: invokevirtual 263	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   60: pop
    //   61: aload_1
    //   62: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   65: astore_1
    //   66: aload_1
    //   67: ifnonnull +21 -> 88
    //   70: aload_2
    //   71: bipush 93
    //   73: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   76: invokevirtual 261	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   79: astore 4
    //   81: aload_0
    //   82: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   85: aload 4
    //   87: areturn
    //   88: aload_2
    //   89: bipush 44
    //   91: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   94: bipush 32
    //   96: invokevirtual 262	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   99: pop
    //   100: goto -60 -> 40
    //   103: astore 5
    //   105: aload_0
    //   106: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   109: aload 5
    //   111: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	112	0	this	LinkedBlockingQueue
    //   11	56	1	localNode	Node
    //   18	71	2	localObject1	Object
    //   44	13	3	localObject2	Object
    //   79	7	4	str	String
    //   103	7	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   4	19	103	finally
    //   25	81	103	finally
    //   88	105	103	finally
  }
  
  /* Error */
  public void clear()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 266	java/util/concurrent/LinkedBlockingQueue:fullyLock	()V
    //   4: aload_0
    //   5: getfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   8: astore_2
    //   9: aload_2
    //   10: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   13: dup
    //   14: astore_1
    //   15: ifnull +18 -> 33
    //   18: aload_2
    //   19: aload_2
    //   20: putfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   23: aload_1
    //   24: aconst_null
    //   25: putfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   28: aload_1
    //   29: astore_2
    //   30: goto -21 -> 9
    //   33: aload_0
    //   34: aload_0
    //   35: getfield 241	java/util/concurrent/LinkedBlockingQueue:last	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   38: putfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   41: aload_0
    //   42: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   45: iconst_0
    //   46: invokevirtual 285	java/util/concurrent/atomic/AtomicInteger:getAndSet	(I)I
    //   49: aload_0
    //   50: getfield 239	java/util/concurrent/LinkedBlockingQueue:capacity	I
    //   53: if_icmpne +12 -> 65
    //   56: aload_0
    //   57: getfield 244	java/util/concurrent/LinkedBlockingQueue:notFull	Ljava/util/concurrent/locks/Condition;
    //   60: invokeinterface 297 1 0
    //   65: aload_0
    //   66: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   69: goto +10 -> 79
    //   72: astore_3
    //   73: aload_0
    //   74: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   77: aload_3
    //   78: athrow
    //   79: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	LinkedBlockingQueue
    //   14	15	1	localNode	Node
    //   8	22	2	localObject1	Object
    //   72	6	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   4	65	72	finally
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
    //   1: ifnonnull +11 -> 12
    //   4: new 135	java/lang/NullPointerException
    //   7: dup
    //   8: invokespecial 257	java/lang/NullPointerException:<init>	()V
    //   11: athrow
    //   12: aload_1
    //   13: aload_0
    //   14: if_acmpne +11 -> 25
    //   17: new 131	java/lang/IllegalArgumentException
    //   20: dup
    //   21: invokespecial 254	java/lang/IllegalArgumentException:<init>	()V
    //   24: athrow
    //   25: iload_2
    //   26: ifgt +5 -> 31
    //   29: iconst_0
    //   30: ireturn
    //   31: iconst_0
    //   32: istore_3
    //   33: aload_0
    //   34: getfield 246	java/util/concurrent/LinkedBlockingQueue:takeLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   37: astore 4
    //   39: aload 4
    //   41: invokevirtual 288	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   44: iload_2
    //   45: aload_0
    //   46: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   49: invokevirtual 280	java/util/concurrent/atomic/AtomicInteger:get	()I
    //   52: invokestatic 256	java/lang/Math:min	(II)I
    //   55: istore 5
    //   57: aload_0
    //   58: getfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   61: astore 6
    //   63: iconst_0
    //   64: istore 7
    //   66: iload 7
    //   68: iload 5
    //   70: if_icmpge +45 -> 115
    //   73: aload 6
    //   75: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   78: astore 8
    //   80: aload_1
    //   81: aload 8
    //   83: getfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   86: invokeinterface 292 2 0
    //   91: pop
    //   92: aload 8
    //   94: aconst_null
    //   95: putfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   98: aload 6
    //   100: aload 6
    //   102: putfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   105: aload 8
    //   107: astore 6
    //   109: iinc 7 1
    //   112: goto -46 -> 66
    //   115: iload 5
    //   117: istore 8
    //   119: iload 7
    //   121: ifle +32 -> 153
    //   124: aload_0
    //   125: aload 6
    //   127: putfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   130: aload_0
    //   131: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   134: iload 7
    //   136: ineg
    //   137: invokevirtual 284	java/util/concurrent/atomic/AtomicInteger:getAndAdd	(I)I
    //   140: aload_0
    //   141: getfield 239	java/util/concurrent/LinkedBlockingQueue:capacity	I
    //   144: if_icmpne +7 -> 151
    //   147: iconst_1
    //   148: goto +4 -> 152
    //   151: iconst_0
    //   152: istore_3
    //   153: aload 4
    //   155: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   158: iload_3
    //   159: ifeq +7 -> 166
    //   162: aload_0
    //   163: invokespecial 269	java/util/concurrent/LinkedBlockingQueue:signalNotFull	()V
    //   166: iload 8
    //   168: ireturn
    //   169: astore 9
    //   171: iload 7
    //   173: ifle +32 -> 205
    //   176: aload_0
    //   177: aload 6
    //   179: putfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   182: aload_0
    //   183: getfield 242	java/util/concurrent/LinkedBlockingQueue:count	Ljava/util/concurrent/atomic/AtomicInteger;
    //   186: iload 7
    //   188: ineg
    //   189: invokevirtual 284	java/util/concurrent/atomic/AtomicInteger:getAndAdd	(I)I
    //   192: aload_0
    //   193: getfield 239	java/util/concurrent/LinkedBlockingQueue:capacity	I
    //   196: if_icmpne +7 -> 203
    //   199: iconst_1
    //   200: goto +4 -> 204
    //   203: iconst_0
    //   204: istore_3
    //   205: aload 9
    //   207: athrow
    //   208: astore 10
    //   210: aload 4
    //   212: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   215: iload_3
    //   216: ifeq +7 -> 223
    //   219: aload_0
    //   220: invokespecial 269	java/util/concurrent/LinkedBlockingQueue:signalNotFull	()V
    //   223: aload 10
    //   225: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	226	0	this	LinkedBlockingQueue
    //   0	226	1	paramCollection	Collection<? super E>
    //   0	226	2	paramInt	int
    //   32	184	3	i	int
    //   37	174	4	localReentrantLock	ReentrantLock
    //   55	61	5	localNode1	Node
    //   61	117	6	localObject1	Object
    //   64	123	7	j	int
    //   78	89	8	localNode2	Node
    //   169	37	9	localObject2	Object
    //   208	16	10	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   66	119	169	finally
    //   169	171	169	finally
    //   44	153	208	finally
    //   169	210	208	finally
  }
  
  public Iterator<E> iterator()
  {
    return new Itr();
  }
  
  public Spliterator<E> spliterator()
  {
    return new LBQSpliterator(this);
  }
  
  /* Error */
  private void writeObject(java.io.ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 266	java/util/concurrent/LinkedBlockingQueue:fullyLock	()V
    //   4: aload_1
    //   5: invokevirtual 251	java/io/ObjectOutputStream:defaultWriteObject	()V
    //   8: aload_0
    //   9: getfield 240	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   12: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   15: astore_2
    //   16: aload_2
    //   17: ifnull +19 -> 36
    //   20: aload_1
    //   21: aload_2
    //   22: getfield 247	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
    //   25: invokevirtual 252	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   28: aload_2
    //   29: getfield 248	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
    //   32: astore_2
    //   33: goto -17 -> 16
    //   36: aload_1
    //   37: aconst_null
    //   38: invokevirtual 252	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   41: aload_0
    //   42: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   45: goto +10 -> 55
    //   48: astore_3
    //   49: aload_0
    //   50: invokevirtual 267	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
    //   53: aload_3
    //   54: athrow
    //   55: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	LinkedBlockingQueue
    //   0	56	1	paramObjectOutputStream	java.io.ObjectOutputStream
    //   15	18	2	localNode	Node
    //   48	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	41	48	finally
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    count.set(0);
    last = (head = new Node(null));
    for (;;)
    {
      Object localObject = paramObjectInputStream.readObject();
      if (localObject == null) {
        break;
      }
      add(localObject);
    }
  }
  
  private class Itr
    implements Iterator<E>
  {
    private LinkedBlockingQueue.Node<E> current;
    private LinkedBlockingQueue.Node<E> lastRet;
    private E currentElement;
    
    /* Error */
    Itr()
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: putfield 64	java/util/concurrent/LinkedBlockingQueue$Itr:this$0	Ljava/util/concurrent/LinkedBlockingQueue;
      //   5: aload_0
      //   6: invokespecial 70	java/lang/Object:<init>	()V
      //   9: aload_1
      //   10: invokevirtual 72	java/util/concurrent/LinkedBlockingQueue:fullyLock	()V
      //   13: aload_0
      //   14: aload_1
      //   15: getfield 62	java/util/concurrent/LinkedBlockingQueue:head	Ljava/util/concurrent/LinkedBlockingQueue$Node;
      //   18: getfield 68	java/util/concurrent/LinkedBlockingQueue$Node:next	Ljava/util/concurrent/LinkedBlockingQueue$Node;
      //   21: putfield 65	java/util/concurrent/LinkedBlockingQueue$Itr:current	Ljava/util/concurrent/LinkedBlockingQueue$Node;
      //   24: aload_0
      //   25: getfield 65	java/util/concurrent/LinkedBlockingQueue$Itr:current	Ljava/util/concurrent/LinkedBlockingQueue$Node;
      //   28: ifnull +14 -> 42
      //   31: aload_0
      //   32: aload_0
      //   33: getfield 65	java/util/concurrent/LinkedBlockingQueue$Itr:current	Ljava/util/concurrent/LinkedBlockingQueue$Node;
      //   36: getfield 67	java/util/concurrent/LinkedBlockingQueue$Node:item	Ljava/lang/Object;
      //   39: putfield 63	java/util/concurrent/LinkedBlockingQueue$Itr:currentElement	Ljava/lang/Object;
      //   42: aload_1
      //   43: invokevirtual 73	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
      //   46: goto +10 -> 56
      //   49: astore_2
      //   50: aload_1
      //   51: invokevirtual 73	java/util/concurrent/LinkedBlockingQueue:fullyUnlock	()V
      //   54: aload_2
      //   55: athrow
      //   56: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	57	0	this	Itr
      //   0	57	1	this$1	LinkedBlockingQueue
      //   49	6	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   13	42	49	finally
    }
    
    public boolean hasNext()
    {
      return current != null;
    }
    
    private LinkedBlockingQueue.Node<E> nextNode(LinkedBlockingQueue.Node<E> paramNode)
    {
      for (;;)
      {
        LinkedBlockingQueue.Node localNode = next;
        if (localNode == paramNode) {
          return head.next;
        }
        if ((localNode == null) || (item != null)) {
          return localNode;
        }
        paramNode = localNode;
      }
    }
    
    public E next()
    {
      fullyLock();
      try
      {
        if (current == null) {
          throw new NoSuchElementException();
        }
        Object localObject1 = currentElement;
        lastRet = current;
        current = nextNode(current);
        currentElement = (current == null ? null : current.item);
        Object localObject2 = localObject1;
        return (E)localObject2;
      }
      finally
      {
        fullyUnlock();
      }
    }
    
    public void remove()
    {
      if (lastRet == null) {
        throw new IllegalStateException();
      }
      fullyLock();
      try
      {
        LinkedBlockingQueue.Node localNode1 = lastRet;
        lastRet = null;
        Object localObject1 = head;
        for (LinkedBlockingQueue.Node localNode2 = next; localNode2 != null; localNode2 = next)
        {
          if (localNode2 == localNode1)
          {
            unlink(localNode2, (LinkedBlockingQueue.Node)localObject1);
            break;
          }
          localObject1 = localNode2;
        }
      }
      finally
      {
        fullyUnlock();
      }
    }
  }
  
  static final class LBQSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final LinkedBlockingQueue<E> queue;
    LinkedBlockingQueue.Node<E> current;
    int batch;
    boolean exhausted;
    long est;
    
    LBQSpliterator(LinkedBlockingQueue<E> paramLinkedBlockingQueue)
    {
      queue = paramLinkedBlockingQueue;
      est = paramLinkedBlockingQueue.size();
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public Spliterator<E> trySplit()
    {
      LinkedBlockingQueue localLinkedBlockingQueue = queue;
      int i = batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      LinkedBlockingQueue.Node localNode1;
      if ((!exhausted) && (((localNode1 = current) != null) || ((localNode1 = head.next) != null)) && (next != null))
      {
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        LinkedBlockingQueue.Node localNode2 = current;
        localLinkedBlockingQueue.fullyLock();
        try
        {
          if ((localNode2 != null) || ((localNode2 = head.next) != null)) {
            do
            {
              if ((arrayOfObject[k] = item) != null) {
                k++;
              }
              if ((localNode2 = next) == null) {
                break;
              }
            } while (k < j);
          }
        }
        finally
        {
          localLinkedBlockingQueue.fullyUnlock();
        }
        if ((current = localNode2) == null)
        {
          est = 0L;
          exhausted = true;
        }
        else if (est -= k < 0L)
        {
          est = 0L;
        }
        if (k > 0)
        {
          batch = k;
          return Spliterators.spliterator(arrayOfObject, 0, k, 4368);
        }
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedBlockingQueue localLinkedBlockingQueue = queue;
      if (!exhausted)
      {
        exhausted = true;
        LinkedBlockingQueue.Node localNode = current;
        do
        {
          Object localObject1 = null;
          localLinkedBlockingQueue.fullyLock();
          try
          {
            if (localNode == null) {
              localNode = head.next;
            }
            while (localNode != null)
            {
              localObject1 = item;
              localNode = next;
              if (localObject1 != null) {
                break;
              }
            }
          }
          finally
          {
            localLinkedBlockingQueue.fullyUnlock();
          }
          if (localObject1 != null) {
            paramConsumer.accept(localObject1);
          }
        } while (localNode != null);
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedBlockingQueue localLinkedBlockingQueue = queue;
      if (!exhausted)
      {
        Object localObject1 = null;
        localLinkedBlockingQueue.fullyLock();
        try
        {
          if (current == null) {
            current = head.next;
          }
          while (current != null)
          {
            localObject1 = current.item;
            current = current.next;
            if (localObject1 != null) {
              break;
            }
          }
        }
        finally
        {
          localLinkedBlockingQueue.fullyUnlock();
        }
        if (current == null) {
          exhausted = true;
        }
        if (localObject1 != null)
        {
          paramConsumer.accept(localObject1);
          return true;
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return 4368;
    }
  }
  
  static class Node<E>
  {
    E item;
    Node<E> next;
    
    Node(E paramE)
    {
      item = paramE;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\LinkedBlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
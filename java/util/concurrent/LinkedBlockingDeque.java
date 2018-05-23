package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingDeque<E>
  extends AbstractQueue<E>
  implements BlockingDeque<E>, Serializable
{
  private static final long serialVersionUID = -387911632671998426L;
  transient Node<E> first;
  transient Node<E> last;
  private transient int count;
  private final int capacity;
  final ReentrantLock lock = new ReentrantLock();
  private final Condition notEmpty = lock.newCondition();
  private final Condition notFull = lock.newCondition();
  
  public LinkedBlockingDeque()
  {
    this(Integer.MAX_VALUE);
  }
  
  public LinkedBlockingDeque(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    capacity = paramInt;
  }
  
  public LinkedBlockingDeque(Collection<? extends E> paramCollection)
  {
    this(Integer.MAX_VALUE);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        if (localObject1 == null) {
          throw new NullPointerException();
        }
        if (!linkLast(new Node(localObject1))) {
          throw new IllegalStateException("Deque full");
        }
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private boolean linkFirst(Node<E> paramNode)
  {
    if (count >= capacity) {
      return false;
    }
    Node localNode = first;
    next = localNode;
    first = paramNode;
    if (last == null) {
      last = paramNode;
    } else {
      prev = paramNode;
    }
    count += 1;
    notEmpty.signal();
    return true;
  }
  
  private boolean linkLast(Node<E> paramNode)
  {
    if (count >= capacity) {
      return false;
    }
    Node localNode = last;
    prev = localNode;
    last = paramNode;
    if (first == null) {
      first = paramNode;
    } else {
      next = paramNode;
    }
    count += 1;
    notEmpty.signal();
    return true;
  }
  
  private E unlinkFirst()
  {
    Node localNode1 = first;
    if (localNode1 == null) {
      return null;
    }
    Node localNode2 = next;
    Object localObject = item;
    item = null;
    next = localNode1;
    first = localNode2;
    if (localNode2 == null) {
      last = null;
    } else {
      prev = null;
    }
    count -= 1;
    notFull.signal();
    return (E)localObject;
  }
  
  private E unlinkLast()
  {
    Node localNode1 = last;
    if (localNode1 == null) {
      return null;
    }
    Node localNode2 = prev;
    Object localObject = item;
    item = null;
    prev = localNode1;
    last = localNode2;
    if (localNode2 == null) {
      first = null;
    } else {
      next = null;
    }
    count -= 1;
    notFull.signal();
    return (E)localObject;
  }
  
  void unlink(Node<E> paramNode)
  {
    Node localNode1 = prev;
    Node localNode2 = next;
    if (localNode1 == null)
    {
      unlinkFirst();
    }
    else if (localNode2 == null)
    {
      unlinkLast();
    }
    else
    {
      next = localNode2;
      prev = localNode1;
      item = null;
      count -= 1;
      notFull.signal();
    }
  }
  
  public void addFirst(E paramE)
  {
    if (!offerFirst(paramE)) {
      throw new IllegalStateException("Deque full");
    }
  }
  
  public void addLast(E paramE)
  {
    if (!offerLast(paramE)) {
      throw new IllegalStateException("Deque full");
    }
  }
  
  public boolean offerFirst(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      boolean bool = linkFirst(localNode);
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean offerLast(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      boolean bool = linkLast(localNode);
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void putFirst(E paramE)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      while (!linkFirst(localNode)) {
        notFull.await();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void putLast(E paramE)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    Node localNode = new Node(paramE);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      while (!linkLast(localNode)) {
        notFull.await();
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean offerFirst(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    Node localNode = new Node(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (!linkFirst(localNode))
      {
        if (l <= 0L)
        {
          bool = false;
          return bool;
        }
        l = notFull.awaitNanos(l);
      }
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean offerLast(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    Node localNode = new Node(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      while (!linkLast(localNode))
      {
        if (l <= 0L)
        {
          bool = false;
          return bool;
        }
        l = notFull.awaitNanos(l);
      }
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E removeFirst()
  {
    Object localObject = pollFirst();
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (E)localObject;
  }
  
  public E removeLast()
  {
    Object localObject = pollLast();
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (E)localObject;
  }
  
  public E pollFirst()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = unlinkFirst();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E pollLast()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = unlinkLast();
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E takeFirst()
    throws InterruptedException
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1;
      while ((localObject1 = unlinkFirst()) == null) {
        notEmpty.await();
      }
      Object localObject2 = localObject1;
      return (E)localObject2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E takeLast()
    throws InterruptedException
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1;
      while ((localObject1 = unlinkLast()) == null) {
        notEmpty.await();
      }
      Object localObject2 = localObject1;
      return (E)localObject2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E pollFirst(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      Object localObject1;
      while ((localObject1 = unlinkFirst()) == null)
      {
        if (l <= 0L)
        {
          localObject2 = null;
          return (E)localObject2;
        }
        l = notEmpty.awaitNanos(l);
      }
      Object localObject2 = localObject1;
      return (E)localObject2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E pollLast(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lockInterruptibly();
    try
    {
      Object localObject1;
      while ((localObject1 = unlinkLast()) == null)
      {
        if (l <= 0L)
        {
          localObject2 = null;
          return (E)localObject2;
        }
        l = notEmpty.awaitNanos(l);
      }
      Object localObject2 = localObject1;
      return (E)localObject2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E getFirst()
  {
    Object localObject = peekFirst();
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (E)localObject;
  }
  
  public E getLast()
  {
    Object localObject = peekLast();
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (E)localObject;
  }
  
  public E peekFirst()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = first == null ? null : first.item;
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E peekLast()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = last == null ? null : last.item;
      return (E)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      for (Node localNode = first; localNode != null; localNode = next) {
        if (paramObject.equals(item))
        {
          unlink(localNode);
          boolean bool2 = true;
          return bool2;
        }
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      for (Node localNode = last; localNode != null; localNode = prev) {
        if (paramObject.equals(item))
        {
          unlink(localNode);
          boolean bool2 = true;
          return bool2;
        }
      }
      boolean bool1 = false;
      return bool1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean add(E paramE)
  {
    addLast(paramE);
    return true;
  }
  
  public boolean offer(E paramE)
  {
    return offerLast(paramE);
  }
  
  public void put(E paramE)
    throws InterruptedException
  {
    putLast(paramE);
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return offerLast(paramE, paramLong, paramTimeUnit);
  }
  
  public E remove()
  {
    return (E)removeFirst();
  }
  
  public E poll()
  {
    return (E)pollFirst();
  }
  
  public E take()
    throws InterruptedException
  {
    return (E)takeFirst();
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return (E)pollFirst(paramLong, paramTimeUnit);
  }
  
  public E element()
  {
    return (E)getFirst();
  }
  
  public E peek()
  {
    return (E)peekFirst();
  }
  
  public int remainingCapacity()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      int i = capacity - count;
      return i;
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
      int i = Math.min(paramInt, count);
      for (int j = 0; j < i; j++)
      {
        paramCollection.add(first.item);
        unlinkFirst();
      }
      j = i;
      return j;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public E pop()
  {
    return (E)removeFirst();
  }
  
  public boolean remove(Object paramObject)
  {
    return removeFirstOccurrence(paramObject);
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
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      for (Node localNode = first; localNode != null; localNode = next) {
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
      localReentrantLock.unlock();
    }
  }
  
  public Object[] toArray()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject = new Object[count];
      int i = 0;
      for (Object localObject1 = first; localObject1 != null; localObject1 = next) {
        arrayOfObject[(i++)] = item;
      }
      localObject1 = arrayOfObject;
      return (Object[])localObject1;
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
      if (paramArrayOfT.length < count) {
        paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), count);
      }
      int i = 0;
      for (Object localObject1 = first; localObject1 != null; localObject1 = next) {
        paramArrayOfT[(i++)] = item;
      }
      if (paramArrayOfT.length > i) {
        paramArrayOfT[i] = null;
      }
      localObject1 = paramArrayOfT;
      return (T[])localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public String toString()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 273	java/util/concurrent/LinkedBlockingDeque:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 324	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: getfield 269	java/util/concurrent/LinkedBlockingDeque:first	Ljava/util/concurrent/LinkedBlockingDeque$Node;
    //   13: astore_2
    //   14: aload_2
    //   15: ifnonnull +12 -> 27
    //   18: ldc 4
    //   20: astore_3
    //   21: aload_1
    //   22: invokevirtual 326	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   25: aload_3
    //   26: areturn
    //   27: new 153	java/lang/StringBuilder
    //   30: dup
    //   31: invokespecial 288	java/lang/StringBuilder:<init>	()V
    //   34: astore_3
    //   35: aload_3
    //   36: bipush 91
    //   38: invokevirtual 290	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   41: pop
    //   42: aload_2
    //   43: getfield 274	java/util/concurrent/LinkedBlockingDeque$Node:item	Ljava/lang/Object;
    //   46: astore 4
    //   48: aload_3
    //   49: aload 4
    //   51: aload_0
    //   52: if_acmpne +8 -> 60
    //   55: ldc 2
    //   57: goto +5 -> 62
    //   60: aload 4
    //   62: invokevirtual 291	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   65: pop
    //   66: aload_2
    //   67: getfield 275	java/util/concurrent/LinkedBlockingDeque$Node:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
    //   70: astore_2
    //   71: aload_2
    //   72: ifnonnull +21 -> 93
    //   75: aload_3
    //   76: bipush 93
    //   78: invokevirtual 290	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   81: invokevirtual 289	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: astore 5
    //   86: aload_1
    //   87: invokevirtual 326	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   90: aload 5
    //   92: areturn
    //   93: aload_3
    //   94: bipush 44
    //   96: invokevirtual 290	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   99: bipush 32
    //   101: invokevirtual 290	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   104: pop
    //   105: goto -63 -> 42
    //   108: astore 6
    //   110: aload_1
    //   111: invokevirtual 326	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   114: aload 6
    //   116: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	117	0	this	LinkedBlockingDeque
    //   4	107	1	localReentrantLock	ReentrantLock
    //   13	59	2	localNode	Node
    //   20	74	3	localObject1	Object
    //   46	15	4	localObject2	Object
    //   84	7	5	str	String
    //   108	7	6	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   9	21	108	finally
    //   27	86	108	finally
    //   93	110	108	finally
  }
  
  public void clear()
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Node localNode;
      for (Object localObject1 = first; localObject1 != null; localObject1 = localNode)
      {
        item = null;
        localNode = next;
        prev = null;
        next = null;
      }
      first = (last = null);
      count = 0;
      notFull.signalAll();
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public Iterator<E> descendingIterator()
  {
    return new DescendingItr(null);
  }
  
  public Spliterator<E> spliterator()
  {
    return new LBDSpliterator(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      paramObjectOutputStream.defaultWriteObject();
      for (Node localNode = first; localNode != null; localNode = next) {
        paramObjectOutputStream.writeObject(item);
      }
      paramObjectOutputStream.writeObject(null);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    count = 0;
    first = null;
    last = null;
    for (;;)
    {
      Object localObject = paramObjectInputStream.readObject();
      if (localObject == null) {
        break;
      }
      add(localObject);
    }
  }
  
  private abstract class AbstractItr
    implements Iterator<E>
  {
    LinkedBlockingDeque.Node<E> next;
    E nextItem;
    private LinkedBlockingDeque.Node<E> lastRet;
    
    abstract LinkedBlockingDeque.Node<E> firstNode();
    
    abstract LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> paramNode);
    
    /* Error */
    AbstractItr()
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: putfield 72	java/util/concurrent/LinkedBlockingDeque$AbstractItr:this$0	Ljava/util/concurrent/LinkedBlockingDeque;
      //   5: aload_0
      //   6: invokespecial 77	java/lang/Object:<init>	()V
      //   9: aload_1
      //   10: getfield 70	java/util/concurrent/LinkedBlockingDeque:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   13: astore_2
      //   14: aload_2
      //   15: invokevirtual 84	java/util/concurrent/locks/ReentrantLock:lock	()V
      //   18: aload_0
      //   19: aload_0
      //   20: invokevirtual 81	java/util/concurrent/LinkedBlockingDeque$AbstractItr:firstNode	()Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   23: putfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   26: aload_0
      //   27: aload_0
      //   28: getfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   31: ifnonnull +7 -> 38
      //   34: aconst_null
      //   35: goto +10 -> 45
      //   38: aload_0
      //   39: getfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   42: getfield 75	java/util/concurrent/LinkedBlockingDeque$Node:item	Ljava/lang/Object;
      //   45: putfield 71	java/util/concurrent/LinkedBlockingDeque$AbstractItr:nextItem	Ljava/lang/Object;
      //   48: aload_2
      //   49: invokevirtual 85	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   52: goto +10 -> 62
      //   55: astore_3
      //   56: aload_2
      //   57: invokevirtual 85	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   60: aload_3
      //   61: athrow
      //   62: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	63	0	this	AbstractItr
      //   0	63	1	this$1	LinkedBlockingDeque
      //   13	44	2	localReentrantLock	ReentrantLock
      //   55	6	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   18	48	55	finally
    }
    
    private LinkedBlockingDeque.Node<E> succ(LinkedBlockingDeque.Node<E> paramNode)
    {
      for (;;)
      {
        LinkedBlockingDeque.Node localNode = nextNode(paramNode);
        if (localNode == null) {
          return null;
        }
        if (item != null) {
          return localNode;
        }
        if (localNode == paramNode) {
          return firstNode();
        }
        paramNode = localNode;
      }
    }
    
    /* Error */
    void advance()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 72	java/util/concurrent/LinkedBlockingDeque$AbstractItr:this$0	Ljava/util/concurrent/LinkedBlockingDeque;
      //   4: getfield 70	java/util/concurrent/LinkedBlockingDeque:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   7: astore_1
      //   8: aload_1
      //   9: invokevirtual 84	java/util/concurrent/locks/ReentrantLock:lock	()V
      //   12: aload_0
      //   13: aload_0
      //   14: aload_0
      //   15: getfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   18: invokespecial 83	java/util/concurrent/LinkedBlockingDeque$AbstractItr:succ	(Ljava/util/concurrent/LinkedBlockingDeque$Node;)Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   21: putfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   24: aload_0
      //   25: aload_0
      //   26: getfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   29: ifnonnull +7 -> 36
      //   32: aconst_null
      //   33: goto +10 -> 43
      //   36: aload_0
      //   37: getfield 74	java/util/concurrent/LinkedBlockingDeque$AbstractItr:next	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   40: getfield 75	java/util/concurrent/LinkedBlockingDeque$Node:item	Ljava/lang/Object;
      //   43: putfield 71	java/util/concurrent/LinkedBlockingDeque$AbstractItr:nextItem	Ljava/lang/Object;
      //   46: aload_1
      //   47: invokevirtual 85	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   50: goto +10 -> 60
      //   53: astore_2
      //   54: aload_1
      //   55: invokevirtual 85	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   58: aload_2
      //   59: athrow
      //   60: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	61	0	this	AbstractItr
      //   7	48	1	localReentrantLock	ReentrantLock
      //   53	6	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   12	46	53	finally
    }
    
    public boolean hasNext()
    {
      return next != null;
    }
    
    public E next()
    {
      if (next == null) {
        throw new NoSuchElementException();
      }
      lastRet = next;
      Object localObject = nextItem;
      advance();
      return (E)localObject;
    }
    
    /* Error */
    public void remove()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 73	java/util/concurrent/LinkedBlockingDeque$AbstractItr:lastRet	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   4: astore_1
      //   5: aload_1
      //   6: ifnonnull +11 -> 17
      //   9: new 36	java/lang/IllegalStateException
      //   12: dup
      //   13: invokespecial 76	java/lang/IllegalStateException:<init>	()V
      //   16: athrow
      //   17: aload_0
      //   18: aconst_null
      //   19: putfield 73	java/util/concurrent/LinkedBlockingDeque$AbstractItr:lastRet	Ljava/util/concurrent/LinkedBlockingDeque$Node;
      //   22: aload_0
      //   23: getfield 72	java/util/concurrent/LinkedBlockingDeque$AbstractItr:this$0	Ljava/util/concurrent/LinkedBlockingDeque;
      //   26: getfield 70	java/util/concurrent/LinkedBlockingDeque:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   29: astore_2
      //   30: aload_2
      //   31: invokevirtual 84	java/util/concurrent/locks/ReentrantLock:lock	()V
      //   34: aload_1
      //   35: getfield 75	java/util/concurrent/LinkedBlockingDeque$Node:item	Ljava/lang/Object;
      //   38: ifnull +11 -> 49
      //   41: aload_0
      //   42: getfield 72	java/util/concurrent/LinkedBlockingDeque$AbstractItr:this$0	Ljava/util/concurrent/LinkedBlockingDeque;
      //   45: aload_1
      //   46: invokevirtual 79	java/util/concurrent/LinkedBlockingDeque:unlink	(Ljava/util/concurrent/LinkedBlockingDeque$Node;)V
      //   49: aload_2
      //   50: invokevirtual 85	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   53: goto +10 -> 63
      //   56: astore_3
      //   57: aload_2
      //   58: invokevirtual 85	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   61: aload_3
      //   62: athrow
      //   63: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	64	0	this	AbstractItr
      //   4	42	1	localNode	LinkedBlockingDeque.Node
      //   29	29	2	localReentrantLock	ReentrantLock
      //   56	6	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   34	49	56	finally
    }
  }
  
  private class DescendingItr
    extends LinkedBlockingDeque<E>.AbstractItr
  {
    private DescendingItr()
    {
      super();
    }
    
    LinkedBlockingDeque.Node<E> firstNode()
    {
      return last;
    }
    
    LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> paramNode)
    {
      return prev;
    }
  }
  
  private class Itr
    extends LinkedBlockingDeque<E>.AbstractItr
  {
    private Itr()
    {
      super();
    }
    
    LinkedBlockingDeque.Node<E> firstNode()
    {
      return first;
    }
    
    LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> paramNode)
    {
      return next;
    }
  }
  
  static final class LBDSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final LinkedBlockingDeque<E> queue;
    LinkedBlockingDeque.Node<E> current;
    int batch;
    boolean exhausted;
    long est;
    
    LBDSpliterator(LinkedBlockingDeque<E> paramLinkedBlockingDeque)
    {
      queue = paramLinkedBlockingDeque;
      est = paramLinkedBlockingDeque.size();
    }
    
    public long estimateSize()
    {
      return est;
    }
    
    public Spliterator<E> trySplit()
    {
      LinkedBlockingDeque localLinkedBlockingDeque = queue;
      int i = batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      LinkedBlockingDeque.Node localNode1;
      if ((!exhausted) && (((localNode1 = current) != null) || ((localNode1 = first) != null)) && (next != null))
      {
        Object[] arrayOfObject = new Object[j];
        ReentrantLock localReentrantLock = lock;
        int k = 0;
        LinkedBlockingDeque.Node localNode2 = current;
        localReentrantLock.lock();
        try
        {
          if ((localNode2 != null) || ((localNode2 = first) != null)) {
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
          localReentrantLock.unlock();
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
      LinkedBlockingDeque localLinkedBlockingDeque = queue;
      ReentrantLock localReentrantLock = lock;
      if (!exhausted)
      {
        exhausted = true;
        LinkedBlockingDeque.Node localNode = current;
        do
        {
          Object localObject1 = null;
          localReentrantLock.lock();
          try
          {
            if (localNode == null) {
              localNode = first;
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
            localReentrantLock.unlock();
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
      LinkedBlockingDeque localLinkedBlockingDeque = queue;
      ReentrantLock localReentrantLock = lock;
      if (!exhausted)
      {
        Object localObject1 = null;
        localReentrantLock.lock();
        try
        {
          if (current == null) {
            current = first;
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
          localReentrantLock.unlock();
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
  
  static final class Node<E>
  {
    E item;
    Node<E> prev;
    Node<E> next;
    
    Node(E paramE)
    {
      item = paramE;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\LinkedBlockingDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
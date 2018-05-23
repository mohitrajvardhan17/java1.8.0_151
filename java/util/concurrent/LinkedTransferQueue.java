package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class LinkedTransferQueue<E>
  extends AbstractQueue<E>
  implements TransferQueue<E>, Serializable
{
  private static final long serialVersionUID = -3223113410248163686L;
  private static final boolean MP = Runtime.getRuntime().availableProcessors() > 1;
  private static final int FRONT_SPINS = 128;
  private static final int CHAINED_SPINS = 64;
  static final int SWEEP_THRESHOLD = 32;
  volatile transient Node head;
  private volatile transient Node tail;
  private volatile transient int sweepVotes;
  private static final int NOW = 0;
  private static final int ASYNC = 1;
  private static final int SYNC = 2;
  private static final int TIMED = 3;
  private static final Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
  private static final long sweepVotesOffset;
  
  private boolean casTail(Node paramNode1, Node paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  private boolean casHead(Node paramNode1, Node paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2);
  }
  
  private boolean casSweepVotes(int paramInt1, int paramInt2)
  {
    return UNSAFE.compareAndSwapInt(this, sweepVotesOffset, paramInt1, paramInt2);
  }
  
  static <E> E cast(Object paramObject)
  {
    return (E)paramObject;
  }
  
  private E xfer(E paramE, boolean paramBoolean, int paramInt, long paramLong)
  {
    if ((paramBoolean) && (paramE == null)) {
      throw new NullPointerException();
    }
    Node localNode1 = null;
    Node localNode2;
    do
    {
      localNode2 = head;
      Node localNode4;
      for (Node localNode3 = localNode2; localNode3 != null; localNode3 = localNode3 != localNode4 ? localNode4 : (localNode2 = head))
      {
        boolean bool = isData;
        Object localObject = item;
        if (localObject != localNode3) {
          if ((localObject != null) == bool)
          {
            if (bool == paramBoolean) {
              break;
            }
            if (localNode3.casItem(localObject, paramE))
            {
              localNode4 = localNode3;
              while (localNode4 != localNode2)
              {
                Node localNode5 = next;
                if (head == localNode2) {
                  if (casHead(localNode2, localNode5 == null ? localNode4 : localNode5))
                  {
                    localNode2.forgetNext();
                    break;
                  }
                }
                if (((localNode2 = head) == null) || ((localNode4 = next) == null) || (!localNode4.isMatched())) {
                  break;
                }
              }
              LockSupport.unpark(waiter);
              return (E)cast(localObject);
            }
          }
        }
        localNode4 = next;
      }
      if (paramInt == 0) {
        break;
      }
      if (localNode1 == null) {
        localNode1 = new Node(paramE, paramBoolean);
      }
      localNode2 = tryAppend(localNode1, paramBoolean);
    } while (localNode2 == null);
    if (paramInt != 1) {
      return (E)awaitMatch(localNode1, localNode2, paramE, paramInt == 3, paramLong);
    }
    return paramE;
  }
  
  private Node tryAppend(Node paramNode, boolean paramBoolean)
  {
    Object localObject1 = tail;
    Object localObject2 = localObject1;
    for (;;)
    {
      if ((localObject2 == null) && ((localObject2 = head) == null))
      {
        if (casHead(null, paramNode)) {
          return paramNode;
        }
      }
      else
      {
        if (((Node)localObject2).cannotPrecede(paramBoolean)) {
          return null;
        }
        Node localNode1;
        if ((localNode1 = next) != null)
        {
          Node localNode2;
          localObject2 = localObject2 != localNode1 ? localNode1 : (localObject2 != localObject1) && (localObject1 != (localNode2 = tail)) ? (localObject1 = localNode2) : null;
        }
        else if (!((Node)localObject2).casNext(null, paramNode))
        {
          localObject2 = next;
        }
        else
        {
          while ((localObject2 != localObject1) && ((tail != localObject1) || (!casTail((Node)localObject1, paramNode))) && ((localObject1 = tail) != null) && ((paramNode = next) != null) && ((paramNode = next) != null) && (paramNode != localObject1)) {}
          return (Node)localObject2;
        }
      }
    }
  }
  
  private E awaitMatch(Node paramNode1, Node paramNode2, E paramE, boolean paramBoolean, long paramLong)
  {
    long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
    Thread localThread = Thread.currentThread();
    int i = -1;
    ThreadLocalRandom localThreadLocalRandom = null;
    for (;;)
    {
      Object localObject = item;
      if (localObject != paramE)
      {
        paramNode1.forgetContents();
        return (E)cast(localObject);
      }
      if (((localThread.isInterrupted()) || ((paramBoolean) && (paramLong <= 0L))) && (paramNode1.casItem(paramE, paramNode1)))
      {
        unsplice(paramNode2, paramNode1);
        return paramE;
      }
      if (i < 0)
      {
        if ((i = spinsFor(paramNode2, isData)) > 0) {
          localThreadLocalRandom = ThreadLocalRandom.current();
        }
      }
      else if (i > 0)
      {
        i--;
        if (localThreadLocalRandom.nextInt(64) == 0) {
          Thread.yield();
        }
      }
      else if (waiter == null)
      {
        waiter = localThread;
      }
      else if (paramBoolean)
      {
        paramLong = l - System.nanoTime();
        if (paramLong > 0L) {
          LockSupport.parkNanos(this, paramLong);
        }
      }
      else
      {
        LockSupport.park(this);
      }
    }
  }
  
  private static int spinsFor(Node paramNode, boolean paramBoolean)
  {
    if ((MP) && (paramNode != null))
    {
      if (isData != paramBoolean) {
        return 192;
      }
      if (paramNode.isMatched()) {
        return 128;
      }
      if (waiter == null) {
        return 64;
      }
    }
    return 0;
  }
  
  final Node succ(Node paramNode)
  {
    Node localNode = next;
    return paramNode == localNode ? head : localNode;
  }
  
  private Node firstOfMode(boolean paramBoolean)
  {
    for (Node localNode = head; localNode != null; localNode = succ(localNode)) {
      if (!localNode.isMatched()) {
        return isData == paramBoolean ? localNode : null;
      }
    }
    return null;
  }
  
  final Node firstDataNode()
  {
    Node localNode = head;
    while (localNode != null)
    {
      Object localObject = item;
      if (isData)
      {
        if ((localObject != null) && (localObject != localNode)) {
          return localNode;
        }
      }
      else {
        if (localObject == null) {
          break;
        }
      }
      if (localNode == (localNode = next)) {
        localNode = head;
      }
    }
    return null;
  }
  
  private E firstDataItem()
  {
    for (Node localNode = head; localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if (isData)
      {
        if ((localObject != null) && (localObject != localNode)) {
          return (E)cast(localObject);
        }
      }
      else if (localObject == null) {
        return null;
      }
    }
    return null;
  }
  
  private int countOfMode(boolean paramBoolean)
  {
    int i = 0;
    Object localObject = head;
    while (localObject != null)
    {
      if (!((Node)localObject).isMatched())
      {
        if (isData != paramBoolean) {
          return 0;
        }
        i++;
        if (i == Integer.MAX_VALUE) {
          break;
        }
      }
      Node localNode = next;
      if (localNode != localObject)
      {
        localObject = localNode;
      }
      else
      {
        i = 0;
        localObject = head;
      }
    }
    return i;
  }
  
  public Spliterator<E> spliterator()
  {
    return new LTQSpliterator(this);
  }
  
  final void unsplice(Node paramNode1, Node paramNode2)
  {
    paramNode2.forgetContents();
    if ((paramNode1 != null) && (paramNode1 != paramNode2) && (next == paramNode2))
    {
      Node localNode1 = next;
      if ((localNode1 == null) || ((localNode1 != paramNode2) && (paramNode1.casNext(paramNode2, localNode1)) && (paramNode1.isMatched())))
      {
        for (;;)
        {
          Node localNode2 = head;
          if ((localNode2 == paramNode1) || (localNode2 == paramNode2) || (localNode2 == null)) {
            return;
          }
          if (!localNode2.isMatched()) {
            break;
          }
          Node localNode3 = next;
          if (localNode3 == null) {
            return;
          }
          if ((localNode3 != localNode2) && (casHead(localNode2, localNode3))) {
            localNode2.forgetNext();
          }
        }
        if ((next != paramNode1) && (next != paramNode2)) {
          for (;;)
          {
            int i = sweepVotes;
            if (i < 32)
            {
              if (casSweepVotes(i, i + 1)) {
                break;
              }
            }
            else if (casSweepVotes(i, 0))
            {
              sweep();
              break;
            }
          }
        }
      }
    }
  }
  
  private void sweep()
  {
    Object localObject = head;
    Node localNode1;
    while ((localObject != null) && ((localNode1 = next) != null)) {
      if (!localNode1.isMatched())
      {
        localObject = localNode1;
      }
      else
      {
        Node localNode2;
        if ((localNode2 = next) == null) {
          break;
        }
        if (localNode1 == localNode2) {
          localObject = head;
        } else {
          ((Node)localObject).casNext(localNode1, localNode2);
        }
      }
    }
  }
  
  private boolean findAndRemove(Object paramObject)
  {
    if (paramObject != null)
    {
      Object localObject1 = null;
      Node localNode = head;
      while (localNode != null)
      {
        Object localObject2 = item;
        if (isData)
        {
          if ((localObject2 != null) && (localObject2 != localNode) && (paramObject.equals(localObject2)) && (localNode.tryMatchData()))
          {
            unsplice((Node)localObject1, localNode);
            return true;
          }
        }
        else {
          if (localObject2 == null) {
            break;
          }
        }
        localObject1 = localNode;
        if ((localNode = next) == localObject1)
        {
          localObject1 = null;
          localNode = head;
        }
      }
    }
    return false;
  }
  
  public LinkedTransferQueue() {}
  
  public LinkedTransferQueue(Collection<? extends E> paramCollection)
  {
    this();
    addAll(paramCollection);
  }
  
  public void put(E paramE)
  {
    xfer(paramE, true, 1, 0L);
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
  {
    xfer(paramE, true, 1, 0L);
    return true;
  }
  
  public boolean offer(E paramE)
  {
    xfer(paramE, true, 1, 0L);
    return true;
  }
  
  public boolean add(E paramE)
  {
    xfer(paramE, true, 1, 0L);
    return true;
  }
  
  public boolean tryTransfer(E paramE)
  {
    return xfer(paramE, true, 0, 0L) == null;
  }
  
  public void transfer(E paramE)
    throws InterruptedException
  {
    if (xfer(paramE, true, 2, 0L) != null)
    {
      Thread.interrupted();
      throw new InterruptedException();
    }
  }
  
  public boolean tryTransfer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (xfer(paramE, true, 3, paramTimeUnit.toNanos(paramLong)) == null) {
      return true;
    }
    if (!Thread.interrupted()) {
      return false;
    }
    throw new InterruptedException();
  }
  
  public E take()
    throws InterruptedException
  {
    Object localObject = xfer(null, false, 2, 0L);
    if (localObject != null) {
      return (E)localObject;
    }
    Thread.interrupted();
    throw new InterruptedException();
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    Object localObject = xfer(null, false, 3, paramTimeUnit.toNanos(paramLong));
    if ((localObject != null) || (!Thread.interrupted())) {
      return (E)localObject;
    }
    throw new InterruptedException();
  }
  
  public E poll()
  {
    return (E)xfer(null, false, 0, 0L);
  }
  
  public int drainTo(Collection<? super E> paramCollection)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    Object localObject;
    for (int i = 0; (localObject = poll()) != null; i++) {
      paramCollection.add(localObject);
    }
    return i;
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    Object localObject;
    for (int i = 0; (i < paramInt) && ((localObject = poll()) != null); i++) {
      paramCollection.add(localObject);
    }
    return i;
  }
  
  public Iterator<E> iterator()
  {
    return new Itr();
  }
  
  public E peek()
  {
    return (E)firstDataItem();
  }
  
  public boolean isEmpty()
  {
    for (Node localNode = head; localNode != null; localNode = succ(localNode)) {
      if (!localNode.isMatched()) {
        return !isData;
      }
    }
    return true;
  }
  
  public boolean hasWaitingConsumer()
  {
    return firstOfMode(false) != null;
  }
  
  public int size()
  {
    return countOfMode(true);
  }
  
  public int getWaitingConsumerCount()
  {
    return countOfMode(false);
  }
  
  public boolean remove(Object paramObject)
  {
    return findAndRemove(paramObject);
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    for (Node localNode = head; localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if (isData)
      {
        if ((localObject != null) && (localObject != localNode) && (paramObject.equals(localObject))) {
          return true;
        }
      }
      else {
        if (localObject == null) {
          break;
        }
      }
    }
    return false;
  }
  
  public int remainingCapacity()
  {
    return Integer.MAX_VALUE;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramObjectOutputStream.writeObject(localObject);
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    for (;;)
    {
      Object localObject = paramObjectInputStream.readObject();
      if (localObject == null) {
        break;
      }
      offer(localObject);
    }
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = LinkedTransferQueue.class;
      headOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("tail"));
      sweepVotesOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("sweepVotes"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  final class Itr
    implements Iterator<E>
  {
    private LinkedTransferQueue.Node nextNode;
    private E nextItem;
    private LinkedTransferQueue.Node lastRet;
    private LinkedTransferQueue.Node lastPred;
    
    private void advance(LinkedTransferQueue.Node paramNode)
    {
      LinkedTransferQueue.Node localNode1;
      LinkedTransferQueue.Node localNode3;
      if (((localNode1 = lastRet) != null) && (!localNode1.isMatched()))
      {
        lastPred = localNode1;
      }
      else
      {
        LinkedTransferQueue.Node localNode2;
        if (((localNode2 = lastPred) == null) || (localNode2.isMatched())) {
          lastPred = null;
        } else {
          while (((localObject1 = next) != null) && (localObject1 != localNode2) && (((LinkedTransferQueue.Node)localObject1).isMatched()) && ((localNode3 = next) != null) && (localNode3 != localObject1)) {
            localNode2.casNext((LinkedTransferQueue.Node)localObject1, localNode3);
          }
        }
      }
      lastRet = paramNode;
      Object localObject1 = paramNode;
      for (;;)
      {
        localNode3 = localObject1 == null ? head : next;
        if (localNode3 == null) {
          break;
        }
        if (localNode3 == localObject1)
        {
          localObject1 = null;
        }
        else
        {
          Object localObject2 = item;
          if (isData)
          {
            if ((localObject2 != null) && (localObject2 != localNode3))
            {
              nextItem = LinkedTransferQueue.cast(localObject2);
              nextNode = localNode3;
            }
          }
          else {
            if (localObject2 == null) {
              break;
            }
          }
          if (localObject1 == null)
          {
            localObject1 = localNode3;
          }
          else
          {
            LinkedTransferQueue.Node localNode4;
            if ((localNode4 = next) == null) {
              break;
            }
            if (localNode3 == localNode4) {
              localObject1 = null;
            } else {
              ((LinkedTransferQueue.Node)localObject1).casNext(localNode3, localNode4);
            }
          }
        }
      }
      nextNode = null;
      nextItem = null;
    }
    
    Itr()
    {
      advance(null);
    }
    
    public final boolean hasNext()
    {
      return nextNode != null;
    }
    
    public final E next()
    {
      LinkedTransferQueue.Node localNode = nextNode;
      if (localNode == null) {
        throw new NoSuchElementException();
      }
      Object localObject = nextItem;
      advance(localNode);
      return (E)localObject;
    }
    
    public final void remove()
    {
      LinkedTransferQueue.Node localNode = lastRet;
      if (localNode == null) {
        throw new IllegalStateException();
      }
      lastRet = null;
      if (localNode.tryMatchData()) {
        unsplice(lastPred, localNode);
      }
    }
  }
  
  static final class LTQSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final LinkedTransferQueue<E> queue;
    LinkedTransferQueue.Node current;
    int batch;
    boolean exhausted;
    
    LTQSpliterator(LinkedTransferQueue<E> paramLinkedTransferQueue)
    {
      queue = paramLinkedTransferQueue;
    }
    
    public Spliterator<E> trySplit()
    {
      LinkedTransferQueue localLinkedTransferQueue = queue;
      int i = batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      LinkedTransferQueue.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localLinkedTransferQueue.firstDataNode()) != null)) && (next != null))
      {
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        do
        {
          Object localObject = item;
          if ((localObject != localNode) && ((arrayOfObject[k] = localObject) != null)) {
            k++;
          }
          if (localNode == (localNode = next)) {
            localNode = localLinkedTransferQueue.firstDataNode();
          }
        } while ((localNode != null) && (k < j) && (isData));
        if ((current = localNode) == null) {
          exhausted = true;
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
      LinkedTransferQueue localLinkedTransferQueue = queue;
      LinkedTransferQueue.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localLinkedTransferQueue.firstDataNode()) != null)))
      {
        exhausted = true;
        do
        {
          Object localObject = item;
          if ((localObject != null) && (localObject != localNode)) {
            paramConsumer.accept(localObject);
          }
          if (localNode == (localNode = next)) {
            localNode = localLinkedTransferQueue.firstDataNode();
          }
        } while ((localNode != null) && (isData));
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedTransferQueue localLinkedTransferQueue = queue;
      LinkedTransferQueue.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localLinkedTransferQueue.firstDataNode()) != null)))
      {
        Object localObject;
        do
        {
          if ((localObject = item) == localNode) {
            localObject = null;
          }
          if (localNode == (localNode = next)) {
            localNode = localLinkedTransferQueue.firstDataNode();
          }
        } while ((localObject == null) && (localNode != null) && (isData));
        if ((current = localNode) == null) {
          exhausted = true;
        }
        if (localObject != null)
        {
          paramConsumer.accept(localObject);
          return true;
        }
      }
      return false;
    }
    
    public long estimateSize()
    {
      return Long.MAX_VALUE;
    }
    
    public int characteristics()
    {
      return 4368;
    }
  }
  
  static final class Node
  {
    final boolean isData;
    volatile Object item;
    volatile Node next;
    volatile Thread waiter;
    private static final long serialVersionUID = -3375979862319811754L;
    private static final Unsafe UNSAFE;
    private static final long itemOffset;
    private static final long nextOffset;
    private static final long waiterOffset;
    
    final boolean casNext(Node paramNode1, Node paramNode2)
    {
      return UNSAFE.compareAndSwapObject(this, nextOffset, paramNode1, paramNode2);
    }
    
    final boolean casItem(Object paramObject1, Object paramObject2)
    {
      return UNSAFE.compareAndSwapObject(this, itemOffset, paramObject1, paramObject2);
    }
    
    Node(Object paramObject, boolean paramBoolean)
    {
      UNSAFE.putObject(this, itemOffset, paramObject);
      isData = paramBoolean;
    }
    
    final void forgetNext()
    {
      UNSAFE.putObject(this, nextOffset, this);
    }
    
    final void forgetContents()
    {
      UNSAFE.putObject(this, itemOffset, this);
      UNSAFE.putObject(this, waiterOffset, null);
    }
    
    final boolean isMatched()
    {
      Object localObject = item;
      if (localObject != this) {}
      return (localObject == null) == isData;
    }
    
    final boolean isUnmatchedRequest()
    {
      return (!isData) && (item == null);
    }
    
    final boolean cannotPrecede(boolean paramBoolean)
    {
      boolean bool = isData;
      Object localObject;
      if ((bool != paramBoolean) && ((localObject = item) != this)) {}
      return (localObject != null) == bool;
    }
    
    final boolean tryMatchData()
    {
      Object localObject = item;
      if ((localObject != null) && (localObject != this) && (casItem(localObject, null)))
      {
        LockSupport.unpark(waiter);
        return true;
      }
      return false;
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Class localClass = Node.class;
        itemOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("next"));
        waiterOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("waiter"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\LinkedTransferQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
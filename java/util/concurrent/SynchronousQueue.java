package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class SynchronousQueue<E>
  extends AbstractQueue<E>
  implements BlockingQueue<E>, Serializable
{
  private static final long serialVersionUID = -3223113410248163686L;
  static final int NCPUS = Runtime.getRuntime().availableProcessors();
  static final int maxTimedSpins = NCPUS < 2 ? 0 : 32;
  static final int maxUntimedSpins = maxTimedSpins * 16;
  static final long spinForTimeoutThreshold = 1000L;
  private volatile transient Transferer<E> transferer = paramBoolean ? new TransferQueue() : new TransferStack();
  private ReentrantLock qlock;
  private WaitQueue waitingProducers;
  private WaitQueue waitingConsumers;
  
  public SynchronousQueue()
  {
    this(false);
  }
  
  public SynchronousQueue(boolean paramBoolean) {}
  
  public void put(E paramE)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    if (transferer.transfer(paramE, false, 0L) == null)
    {
      Thread.interrupted();
      throw new InterruptedException();
    }
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    if (transferer.transfer(paramE, true, paramTimeUnit.toNanos(paramLong)) != null) {
      return true;
    }
    if (!Thread.interrupted()) {
      return false;
    }
    throw new InterruptedException();
  }
  
  public boolean offer(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    return transferer.transfer(paramE, true, 0L) != null;
  }
  
  public E take()
    throws InterruptedException
  {
    Object localObject = transferer.transfer(null, false, 0L);
    if (localObject != null) {
      return (E)localObject;
    }
    Thread.interrupted();
    throw new InterruptedException();
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    Object localObject = transferer.transfer(null, true, paramTimeUnit.toNanos(paramLong));
    if ((localObject != null) || (!Thread.interrupted())) {
      return (E)localObject;
    }
    throw new InterruptedException();
  }
  
  public E poll()
  {
    return (E)transferer.transfer(null, true, 0L);
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public int size()
  {
    return 0;
  }
  
  public int remainingCapacity()
  {
    return 0;
  }
  
  public void clear() {}
  
  public boolean contains(Object paramObject)
  {
    return false;
  }
  
  public boolean remove(Object paramObject)
  {
    return false;
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    return paramCollection.isEmpty();
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    return false;
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    return false;
  }
  
  public E peek()
  {
    return null;
  }
  
  public Iterator<E> iterator()
  {
    return Collections.emptyIterator();
  }
  
  public Spliterator<E> spliterator()
  {
    return Spliterators.emptySpliterator();
  }
  
  public Object[] toArray()
  {
    return new Object[0];
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length > 0) {
      paramArrayOfT[0] = null;
    }
    return paramArrayOfT;
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
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    boolean bool = transferer instanceof TransferQueue;
    if (bool)
    {
      qlock = new ReentrantLock(true);
      waitingProducers = new FifoWaitQueue();
      waitingConsumers = new FifoWaitQueue();
    }
    else
    {
      qlock = new ReentrantLock();
      waitingProducers = new LifoWaitQueue();
      waitingConsumers = new LifoWaitQueue();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if ((waitingProducers instanceof FifoWaitQueue)) {
      transferer = new TransferQueue();
    } else {
      transferer = new TransferStack();
    }
  }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass)
  {
    try
    {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      NoSuchFieldError localNoSuchFieldError = new NoSuchFieldError(paramString);
      localNoSuchFieldError.initCause(localNoSuchFieldException);
      throw localNoSuchFieldError;
    }
  }
  
  static class FifoWaitQueue
    extends SynchronousQueue.WaitQueue
  {
    private static final long serialVersionUID = -3623113410248163686L;
    
    FifoWaitQueue() {}
  }
  
  static class LifoWaitQueue
    extends SynchronousQueue.WaitQueue
  {
    private static final long serialVersionUID = -3633113410248163686L;
    
    LifoWaitQueue() {}
  }
  
  static final class TransferQueue<E>
    extends SynchronousQueue.Transferer<E>
  {
    volatile transient QNode head;
    volatile transient QNode tail;
    volatile transient QNode cleanMe;
    private static final Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long cleanMeOffset;
    
    TransferQueue()
    {
      QNode localQNode = new QNode(null, false);
      head = localQNode;
      tail = localQNode;
    }
    
    void advanceHead(QNode paramQNode1, QNode paramQNode2)
    {
      if ((paramQNode1 == head) && (UNSAFE.compareAndSwapObject(this, headOffset, paramQNode1, paramQNode2))) {
        next = paramQNode1;
      }
    }
    
    void advanceTail(QNode paramQNode1, QNode paramQNode2)
    {
      if (tail == paramQNode1) {
        UNSAFE.compareAndSwapObject(this, tailOffset, paramQNode1, paramQNode2);
      }
    }
    
    boolean casCleanMe(QNode paramQNode1, QNode paramQNode2)
    {
      return (cleanMe == paramQNode1) && (UNSAFE.compareAndSwapObject(this, cleanMeOffset, paramQNode1, paramQNode2));
    }
    
    E transfer(E paramE, boolean paramBoolean, long paramLong)
    {
      QNode localQNode1 = null;
      boolean bool = paramE != null;
      QNode localQNode3;
      QNode localQNode4;
      Object localObject;
      for (;;)
      {
        QNode localQNode2 = tail;
        localQNode3 = head;
        if ((localQNode2 != null) && (localQNode3 != null)) {
          if ((localQNode3 == localQNode2) || (isData == bool))
          {
            localQNode4 = next;
            if (localQNode2 == tail) {
              if (localQNode4 != null)
              {
                advanceTail(localQNode2, localQNode4);
              }
              else
              {
                if ((paramBoolean) && (paramLong <= 0L)) {
                  return null;
                }
                if (localQNode1 == null) {
                  localQNode1 = new QNode(paramE, bool);
                }
                if (localQNode2.casNext(null, localQNode1))
                {
                  advanceTail(localQNode2, localQNode1);
                  localObject = awaitFulfill(localQNode1, paramE, paramBoolean, paramLong);
                  if (localObject == localQNode1)
                  {
                    clean(localQNode2, localQNode1);
                    return null;
                  }
                  if (!localQNode1.isOffList())
                  {
                    advanceHead(localQNode2, localQNode1);
                    if (localObject != null) {
                      item = localQNode1;
                    }
                    waiter = null;
                  }
                  return (E)(localObject != null ? localObject : paramE);
                }
              }
            }
          }
          else
          {
            localQNode4 = next;
            if ((localQNode2 == tail) && (localQNode4 != null) && (localQNode3 == head))
            {
              localObject = item;
              if (bool != (localObject != null)) {
                if ((localObject != localQNode4) && (localQNode4.casItem(localObject, paramE))) {
                  break;
                }
              }
              advanceHead(localQNode3, localQNode4);
            }
          }
        }
      }
      advanceHead(localQNode3, localQNode4);
      LockSupport.unpark(waiter);
      return (E)(localObject != null ? localObject : paramE);
    }
    
    Object awaitFulfill(QNode paramQNode, E paramE, boolean paramBoolean, long paramLong)
    {
      long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
      Thread localThread = Thread.currentThread();
      int i = head.next == paramQNode ? SynchronousQueue.maxUntimedSpins : paramBoolean ? SynchronousQueue.maxTimedSpins : 0;
      for (;;)
      {
        if (localThread.isInterrupted()) {
          paramQNode.tryCancel(paramE);
        }
        Object localObject = item;
        if (localObject != paramE) {
          return localObject;
        }
        if (paramBoolean)
        {
          paramLong = l - System.nanoTime();
          if (paramLong <= 0L)
          {
            paramQNode.tryCancel(paramE);
            continue;
          }
        }
        if (i > 0) {
          i--;
        } else if (waiter == null) {
          waiter = localThread;
        } else if (!paramBoolean) {
          LockSupport.park(this);
        } else if (paramLong > 1000L) {
          LockSupport.parkNanos(this, paramLong);
        }
      }
    }
    
    void clean(QNode paramQNode1, QNode paramQNode2)
    {
      waiter = null;
      while (next == paramQNode2)
      {
        QNode localQNode1 = head;
        QNode localQNode2 = next;
        if ((localQNode2 != null) && (localQNode2.isCancelled()))
        {
          advanceHead(localQNode1, localQNode2);
        }
        else
        {
          QNode localQNode3 = tail;
          if (localQNode3 == localQNode1) {
            return;
          }
          QNode localQNode4 = next;
          if (localQNode3 == tail) {
            if (localQNode4 != null)
            {
              advanceTail(localQNode3, localQNode4);
            }
            else
            {
              if (paramQNode2 != localQNode3)
              {
                localQNode5 = next;
                if ((localQNode5 == paramQNode2) || (paramQNode1.casNext(paramQNode2, localQNode5))) {
                  return;
                }
              }
              QNode localQNode5 = cleanMe;
              if (localQNode5 != null)
              {
                QNode localQNode6 = next;
                QNode localQNode7;
                if ((localQNode6 == null) || (localQNode6 == localQNode5) || (!localQNode6.isCancelled()) || ((localQNode6 != localQNode3) && ((localQNode7 = next) != null) && (localQNode7 != localQNode6) && (localQNode5.casNext(localQNode6, localQNode7)))) {
                  casCleanMe(localQNode5, null);
                }
                if (localQNode5 == paramQNode1) {
                  return;
                }
              }
              else if (casCleanMe(null, paramQNode1))
              {
                return;
              }
            }
          }
        }
      }
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Class localClass = TransferQueue.class;
        headOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("head"));
        tailOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("tail"));
        cleanMeOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("cleanMe"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
    
    static final class QNode
    {
      volatile QNode next;
      volatile Object item;
      volatile Thread waiter;
      final boolean isData;
      private static final Unsafe UNSAFE;
      private static final long itemOffset;
      private static final long nextOffset;
      
      QNode(Object paramObject, boolean paramBoolean)
      {
        item = paramObject;
        isData = paramBoolean;
      }
      
      boolean casNext(QNode paramQNode1, QNode paramQNode2)
      {
        return (next == paramQNode1) && (UNSAFE.compareAndSwapObject(this, nextOffset, paramQNode1, paramQNode2));
      }
      
      boolean casItem(Object paramObject1, Object paramObject2)
      {
        return (item == paramObject1) && (UNSAFE.compareAndSwapObject(this, itemOffset, paramObject1, paramObject2));
      }
      
      void tryCancel(Object paramObject)
      {
        UNSAFE.compareAndSwapObject(this, itemOffset, paramObject, this);
      }
      
      boolean isCancelled()
      {
        return item == this;
      }
      
      boolean isOffList()
      {
        return next == this;
      }
      
      static
      {
        try
        {
          UNSAFE = Unsafe.getUnsafe();
          Class localClass = QNode.class;
          itemOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("item"));
          nextOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("next"));
        }
        catch (Exception localException)
        {
          throw new Error(localException);
        }
      }
    }
  }
  
  static final class TransferStack<E>
    extends SynchronousQueue.Transferer<E>
  {
    static final int REQUEST = 0;
    static final int DATA = 1;
    static final int FULFILLING = 2;
    volatile SNode head;
    private static final Unsafe UNSAFE;
    private static final long headOffset;
    
    TransferStack() {}
    
    static boolean isFulfilling(int paramInt)
    {
      return (paramInt & 0x2) != 0;
    }
    
    boolean casHead(SNode paramSNode1, SNode paramSNode2)
    {
      return (paramSNode1 == head) && (UNSAFE.compareAndSwapObject(this, headOffset, paramSNode1, paramSNode2));
    }
    
    static SNode snode(SNode paramSNode1, Object paramObject, SNode paramSNode2, int paramInt)
    {
      if (paramSNode1 == null) {
        paramSNode1 = new SNode(paramObject);
      }
      mode = paramInt;
      next = paramSNode2;
      return paramSNode1;
    }
    
    E transfer(E paramE, boolean paramBoolean, long paramLong)
    {
      SNode localSNode1 = null;
      int i = paramE == null ? 0 : 1;
      for (;;)
      {
        SNode localSNode2 = head;
        SNode localSNode3;
        if ((localSNode2 == null) || (mode == i))
        {
          if ((paramBoolean) && (paramLong <= 0L))
          {
            if ((localSNode2 != null) && (localSNode2.isCancelled())) {
              casHead(localSNode2, next);
            } else {
              return null;
            }
          }
          else if (casHead(localSNode2, localSNode1 = snode(localSNode1, paramE, localSNode2, i)))
          {
            localSNode3 = awaitFulfill(localSNode1, paramBoolean, paramLong);
            if (localSNode3 == localSNode1)
            {
              clean(localSNode1);
              return null;
            }
            if (((localSNode2 = head) != null) && (next == localSNode1)) {
              casHead(localSNode2, next);
            }
            return (E)(i == 0 ? item : item);
          }
        }
        else
        {
          SNode localSNode4;
          if (!isFulfilling(mode))
          {
            if (localSNode2.isCancelled()) {
              casHead(localSNode2, next);
            } else if (casHead(localSNode2, localSNode1 = snode(localSNode1, paramE, localSNode2, 0x2 | i))) {
              for (;;)
              {
                localSNode3 = next;
                if (localSNode3 == null)
                {
                  casHead(localSNode1, null);
                  localSNode1 = null;
                  break;
                }
                localSNode4 = next;
                if (localSNode3.tryMatch(localSNode1))
                {
                  casHead(localSNode1, localSNode4);
                  return (E)(i == 0 ? item : item);
                }
                localSNode1.casNext(localSNode3, localSNode4);
              }
            }
          }
          else
          {
            localSNode3 = next;
            if (localSNode3 == null)
            {
              casHead(localSNode2, null);
            }
            else
            {
              localSNode4 = next;
              if (localSNode3.tryMatch(localSNode2)) {
                casHead(localSNode2, localSNode4);
              } else {
                localSNode2.casNext(localSNode3, localSNode4);
              }
            }
          }
        }
      }
    }
    
    SNode awaitFulfill(SNode paramSNode, boolean paramBoolean, long paramLong)
    {
      long l = paramBoolean ? System.nanoTime() + paramLong : 0L;
      Thread localThread = Thread.currentThread();
      int i = shouldSpin(paramSNode) ? SynchronousQueue.maxUntimedSpins : paramBoolean ? SynchronousQueue.maxTimedSpins : 0;
      for (;;)
      {
        if (localThread.isInterrupted()) {
          paramSNode.tryCancel();
        }
        SNode localSNode = match;
        if (localSNode != null) {
          return localSNode;
        }
        if (paramBoolean)
        {
          paramLong = l - System.nanoTime();
          if (paramLong <= 0L)
          {
            paramSNode.tryCancel();
            continue;
          }
        }
        if (i > 0) {
          i = shouldSpin(paramSNode) ? i - 1 : 0;
        } else if (waiter == null) {
          waiter = localThread;
        } else if (!paramBoolean) {
          LockSupport.park(this);
        } else if (paramLong > 1000L) {
          LockSupport.parkNanos(this, paramLong);
        }
      }
    }
    
    boolean shouldSpin(SNode paramSNode)
    {
      SNode localSNode = head;
      return (localSNode == paramSNode) || (localSNode == null) || (isFulfilling(mode));
    }
    
    void clean(SNode paramSNode)
    {
      item = null;
      waiter = null;
      SNode localSNode1 = next;
      if ((localSNode1 != null) && (localSNode1.isCancelled())) {
        localSNode1 = next;
      }
      Object localObject;
      while (((localObject = head) != null) && (localObject != localSNode1) && (((SNode)localObject).isCancelled())) {
        casHead((SNode)localObject, next);
      }
      while ((localObject != null) && (localObject != localSNode1))
      {
        SNode localSNode2 = next;
        if ((localSNode2 != null) && (localSNode2.isCancelled())) {
          ((SNode)localObject).casNext(localSNode2, next);
        } else {
          localObject = localSNode2;
        }
      }
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Class localClass = TransferStack.class;
        headOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("head"));
      }
      catch (Exception localException)
      {
        throw new Error(localException);
      }
    }
    
    static final class SNode
    {
      volatile SNode next;
      volatile SNode match;
      volatile Thread waiter;
      Object item;
      int mode;
      private static final Unsafe UNSAFE;
      private static final long matchOffset;
      private static final long nextOffset;
      
      SNode(Object paramObject)
      {
        item = paramObject;
      }
      
      boolean casNext(SNode paramSNode1, SNode paramSNode2)
      {
        return (paramSNode1 == next) && (UNSAFE.compareAndSwapObject(this, nextOffset, paramSNode1, paramSNode2));
      }
      
      boolean tryMatch(SNode paramSNode)
      {
        if ((match == null) && (UNSAFE.compareAndSwapObject(this, matchOffset, null, paramSNode)))
        {
          Thread localThread = waiter;
          if (localThread != null)
          {
            waiter = null;
            LockSupport.unpark(localThread);
          }
          return true;
        }
        return match == paramSNode;
      }
      
      void tryCancel()
      {
        UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
      }
      
      boolean isCancelled()
      {
        return match == this;
      }
      
      static
      {
        try
        {
          UNSAFE = Unsafe.getUnsafe();
          Class localClass = SNode.class;
          matchOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("match"));
          nextOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("next"));
        }
        catch (Exception localException)
        {
          throw new Error(localException);
        }
      }
    }
  }
  
  static abstract class Transferer<E>
  {
    Transferer() {}
    
    abstract E transfer(E paramE, boolean paramBoolean, long paramLong);
  }
  
  static class WaitQueue
    implements Serializable
  {
    WaitQueue() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\SynchronousQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
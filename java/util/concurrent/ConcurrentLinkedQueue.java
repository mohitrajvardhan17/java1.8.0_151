package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedQueue<E>
  extends AbstractQueue<E>
  implements Queue<E>, Serializable
{
  private static final long serialVersionUID = 196745693267521676L;
  private volatile transient Node<E> head;
  private volatile transient Node<E> tail;
  private static final Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
  
  public ConcurrentLinkedQueue()
  {
    head = (tail = new Node(null));
  }
  
  public ConcurrentLinkedQueue(Collection<? extends E> paramCollection)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject3 = localIterator.next();
      checkNotNull(localObject3);
      Node localNode = new Node(localObject3);
      if (localObject1 == null)
      {
        localObject1 = localObject2 = localNode;
      }
      else
      {
        ((Node)localObject2).lazySetNext(localNode);
        localObject2 = localNode;
      }
    }
    if (localObject1 == null) {
      localObject1 = localObject2 = new Node(null);
    }
    head = ((Node)localObject1);
    tail = ((Node)localObject2);
  }
  
  public boolean add(E paramE)
  {
    return offer(paramE);
  }
  
  final void updateHead(Node<E> paramNode1, Node<E> paramNode2)
  {
    if ((paramNode1 != paramNode2) && (casHead(paramNode1, paramNode2))) {
      paramNode1.lazySetNext(paramNode1);
    }
  }
  
  final Node<E> succ(Node<E> paramNode)
  {
    Node localNode = next;
    return paramNode == localNode ? head : localNode;
  }
  
  public boolean offer(E paramE)
  {
    checkNotNull(paramE);
    Node localNode1 = new Node(paramE);
    Node localNode2 = tail;
    Object localObject = localNode2;
    for (;;)
    {
      Node localNode3 = next;
      if (localNode3 == null)
      {
        if (((Node)localObject).casNext(null, localNode1))
        {
          if (localObject != localNode2) {
            casTail(localNode2, localNode1);
          }
          return true;
        }
      }
      else if (localObject == localNode3) {
        localObject = localNode2 != (localNode2 = tail) ? localNode2 : head;
      } else {
        localObject = (localObject != localNode2) && (localNode2 != (localNode2 = tail)) ? localNode2 : localNode3;
      }
    }
  }
  
  public E poll()
  {
    Node localNode1 = head;
    Node localNode2;
    for (Object localObject1 = localNode1;; localObject1 = localNode2)
    {
      Object localObject2 = item;
      if ((localObject2 != null) && (((Node)localObject1).casItem(localObject2, null)))
      {
        if (localObject1 != localNode1) {
          updateHead(localNode1, (localNode2 = next) != null ? localNode2 : (Node)localObject1);
        }
        return (E)localObject2;
      }
      if ((localNode2 = next) == null)
      {
        updateHead(localNode1, (Node)localObject1);
        return null;
      }
      if (localObject1 == localNode2) {
        break;
      }
    }
  }
  
  public E peek()
  {
    Node localNode1 = head;
    Node localNode2;
    for (Object localObject1 = localNode1;; localObject1 = localNode2)
    {
      Object localObject2 = item;
      if ((localObject2 != null) || ((localNode2 = next) == null))
      {
        updateHead(localNode1, (Node)localObject1);
        return (E)localObject2;
      }
      if (localObject1 == localNode2) {
        break;
      }
    }
  }
  
  Node<E> first()
  {
    Node localNode1 = head;
    Node localNode2;
    for (Object localObject = localNode1;; localObject = localNode2)
    {
      int i = item != null ? 1 : 0;
      if ((i != 0) || ((localNode2 = next) == null))
      {
        updateHead(localNode1, (Node)localObject);
        return (Node<E>)(i != 0 ? localObject : null);
      }
      if (localObject == localNode2) {
        break;
      }
    }
  }
  
  public boolean isEmpty()
  {
    return first() == null;
  }
  
  public int size()
  {
    int i = 0;
    for (Node localNode = first(); localNode != null; localNode = succ(localNode)) {
      if (item != null)
      {
        i++;
        if (i == Integer.MAX_VALUE) {
          break;
        }
      }
    }
    return i;
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if ((localObject != null) && (paramObject.equals(localObject))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean remove(Object paramObject)
  {
    if (paramObject != null)
    {
      Object localObject1 = null;
      Node localNode;
      for (Object localObject2 = first(); localObject2 != null; localObject2 = localNode)
      {
        boolean bool = false;
        Object localObject3 = item;
        if (localObject3 != null)
        {
          if (!paramObject.equals(localObject3)) {
            localNode = succ((Node)localObject2);
          } else {
            bool = ((Node)localObject2).casItem(localObject3, null);
          }
        }
        else
        {
          localNode = succ((Node)localObject2);
          if ((localObject1 != null) && (localNode != null)) {
            ((Node)localObject1).casNext((Node)localObject2, localNode);
          }
          if (bool) {
            return true;
          }
        }
        localObject1 = localObject2;
      }
    }
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    if (paramCollection == this) {
      throw new IllegalArgumentException();
    }
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = paramCollection.iterator();
    Node localNode;
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = ((Iterator)localObject3).next();
      checkNotNull(localObject4);
      localNode = new Node(localObject4);
      if (localObject1 == null)
      {
        localObject1 = localObject2 = localNode;
      }
      else
      {
        ((Node)localObject2).lazySetNext(localNode);
        localObject2 = localNode;
      }
    }
    if (localObject1 == null) {
      return false;
    }
    localObject3 = tail;
    Object localObject4 = localObject3;
    for (;;)
    {
      localNode = next;
      if (localNode == null)
      {
        if (((Node)localObject4).casNext(null, (Node)localObject1))
        {
          if (!casTail((Node)localObject3, (Node)localObject2))
          {
            localObject3 = tail;
            if (next == null) {
              casTail((Node)localObject3, (Node)localObject2);
            }
          }
          return true;
        }
      }
      else if (localObject4 == localNode) {
        localObject4 = localObject3 != (localObject3 = tail) ? localObject3 : head;
      } else {
        localObject4 = (localObject4 != localObject3) && (localObject3 != (localObject3 = tail)) ? localObject3 : localNode;
      }
    }
  }
  
  public Object[] toArray()
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if (localObject != null) {
        localArrayList.add(localObject);
      }
    }
    return localArrayList.toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    int i = 0;
    for (Node localNode1 = first(); (localNode1 != null) && (i < paramArrayOfT.length); localNode1 = succ(localNode1))
    {
      localObject1 = item;
      if (localObject1 != null) {
        paramArrayOfT[(i++)] = localObject1;
      }
    }
    if (localNode1 == null)
    {
      if (i < paramArrayOfT.length) {
        paramArrayOfT[i] = null;
      }
      return paramArrayOfT;
    }
    Object localObject1 = new ArrayList();
    for (Node localNode2 = first(); localNode2 != null; localNode2 = succ(localNode2))
    {
      Object localObject2 = item;
      if (localObject2 != null) {
        ((ArrayList)localObject1).add(localObject2);
      }
    }
    return ((ArrayList)localObject1).toArray(paramArrayOfT);
  }
  
  public Iterator<E> iterator()
  {
    return new Itr();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if (localObject != null) {
        paramObjectOutputStream.writeObject(localObject);
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3;
    while ((localObject3 = paramObjectInputStream.readObject()) != null)
    {
      Node localNode = new Node(localObject3);
      if (localObject1 == null)
      {
        localObject1 = localObject2 = localNode;
      }
      else
      {
        ((Node)localObject2).lazySetNext(localNode);
        localObject2 = localNode;
      }
    }
    if (localObject1 == null) {
      localObject1 = localObject2 = new Node(null);
    }
    head = ((Node)localObject1);
    tail = ((Node)localObject2);
  }
  
  public Spliterator<E> spliterator()
  {
    return new CLQSpliterator(this);
  }
  
  private static void checkNotNull(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
  }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2);
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = ConcurrentLinkedQueue.class;
      headOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("tail"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class CLQSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final ConcurrentLinkedQueue<E> queue;
    ConcurrentLinkedQueue.Node<E> current;
    int batch;
    boolean exhausted;
    
    CLQSpliterator(ConcurrentLinkedQueue<E> paramConcurrentLinkedQueue)
    {
      queue = paramConcurrentLinkedQueue;
    }
    
    public Spliterator<E> trySplit()
    {
      ConcurrentLinkedQueue localConcurrentLinkedQueue = queue;
      int i = batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      ConcurrentLinkedQueue.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localConcurrentLinkedQueue.first()) != null)) && (next != null))
      {
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        do
        {
          if ((arrayOfObject[k] = item) != null) {
            k++;
          }
          if (localNode == (localNode = next)) {
            localNode = localConcurrentLinkedQueue.first();
          }
        } while ((localNode != null) && (k < j));
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
      ConcurrentLinkedQueue localConcurrentLinkedQueue = queue;
      ConcurrentLinkedQueue.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localConcurrentLinkedQueue.first()) != null)))
      {
        exhausted = true;
        do
        {
          Object localObject = item;
          if (localNode == (localNode = next)) {
            localNode = localConcurrentLinkedQueue.first();
          }
          if (localObject != null) {
            paramConsumer.accept(localObject);
          }
        } while (localNode != null);
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentLinkedQueue localConcurrentLinkedQueue = queue;
      ConcurrentLinkedQueue.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localConcurrentLinkedQueue.first()) != null)))
      {
        Object localObject;
        do
        {
          localObject = item;
          if (localNode == (localNode = next)) {
            localNode = localConcurrentLinkedQueue.first();
          }
        } while ((localObject == null) && (localNode != null));
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
  
  private class Itr
    implements Iterator<E>
  {
    private ConcurrentLinkedQueue.Node<E> nextNode;
    private E nextItem;
    private ConcurrentLinkedQueue.Node<E> lastRet;
    
    Itr()
    {
      advance();
    }
    
    private E advance()
    {
      lastRet = nextNode;
      Object localObject1 = nextItem;
      ConcurrentLinkedQueue.Node localNode1;
      if (nextNode == null)
      {
        localObject2 = first();
        localNode1 = null;
      }
      else
      {
        localNode1 = nextNode;
      }
      ConcurrentLinkedQueue.Node localNode2;
      for (Object localObject2 = succ(nextNode);; localObject2 = localNode2)
      {
        if (localObject2 == null)
        {
          nextNode = null;
          nextItem = null;
          return (E)localObject1;
        }
        Object localObject3 = item;
        if (localObject3 != null)
        {
          nextNode = ((ConcurrentLinkedQueue.Node)localObject2);
          nextItem = localObject3;
          return (E)localObject1;
        }
        localNode2 = succ((ConcurrentLinkedQueue.Node)localObject2);
        if ((localNode1 != null) && (localNode2 != null)) {
          localNode1.casNext((ConcurrentLinkedQueue.Node)localObject2, localNode2);
        }
      }
    }
    
    public boolean hasNext()
    {
      return nextNode != null;
    }
    
    public E next()
    {
      if (nextNode == null) {
        throw new NoSuchElementException();
      }
      return (E)advance();
    }
    
    public void remove()
    {
      ConcurrentLinkedQueue.Node localNode = lastRet;
      if (localNode == null) {
        throw new IllegalStateException();
      }
      item = null;
      lastRet = null;
    }
  }
  
  private static class Node<E>
  {
    volatile E item;
    volatile Node<E> next;
    private static final Unsafe UNSAFE;
    private static final long itemOffset;
    private static final long nextOffset;
    
    Node(E paramE)
    {
      UNSAFE.putObject(this, itemOffset, paramE);
    }
    
    boolean casItem(E paramE1, E paramE2)
    {
      return UNSAFE.compareAndSwapObject(this, itemOffset, paramE1, paramE2);
    }
    
    void lazySetNext(Node<E> paramNode)
    {
      UNSAFE.putOrderedObject(this, nextOffset, paramNode);
    }
    
    boolean casNext(Node<E> paramNode1, Node<E> paramNode2)
    {
      return UNSAFE.compareAndSwapObject(this, nextOffset, paramNode1, paramNode2);
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Class localClass = Node.class;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ConcurrentLinkedQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedDeque<E>
  extends AbstractCollection<E>
  implements Deque<E>, Serializable
{
  private static final long serialVersionUID = 876323262645176354L;
  private volatile transient Node<E> head;
  private volatile transient Node<E> tail;
  private static final Node<Object> PREV_TERMINATOR = new Node();
  private static final Node<Object> NEXT_TERMINATOR;
  private static final int HOPS = 2;
  private static final Unsafe UNSAFE;
  private static final long headOffset;
  private static final long tailOffset;
  
  Node<E> prevTerminator()
  {
    return PREV_TERMINATOR;
  }
  
  Node<E> nextTerminator()
  {
    return NEXT_TERMINATOR;
  }
  
  private void linkFirst(E paramE)
  {
    checkNotNull(paramE);
    Node localNode1 = new Node(paramE);
    Node localNode2 = head;
    Object localObject = localNode2;
    do
    {
      Node localNode3;
      while (((localNode3 = prev) != null) && ((localNode3 = prev) != null)) {
        localObject = localNode2 != (localNode2 = head) ? localNode2 : localNode3;
      }
      if (next == localObject) {
        break;
      }
      localNode1.lazySetNext((Node)localObject);
    } while (!((Node)localObject).casPrev(null, localNode1));
    if (localObject != localNode2) {
      casHead(localNode2, localNode1);
    }
  }
  
  private void linkLast(E paramE)
  {
    checkNotNull(paramE);
    Node localNode1 = new Node(paramE);
    Node localNode2 = tail;
    Object localObject = localNode2;
    do
    {
      Node localNode3;
      while (((localNode3 = next) != null) && ((localNode3 = next) != null)) {
        localObject = localNode2 != (localNode2 = tail) ? localNode2 : localNode3;
      }
      if (prev == localObject) {
        break;
      }
      localNode1.lazySetPrev((Node)localObject);
    } while (!((Node)localObject).casNext(null, localNode1));
    if (localObject != localNode2) {
      casTail(localNode2, localNode1);
    }
  }
  
  void unlink(Node<E> paramNode)
  {
    Node localNode1 = prev;
    Node localNode2 = next;
    if (localNode1 == null)
    {
      unlinkFirst(paramNode, localNode2);
    }
    else if (localNode2 == null)
    {
      unlinkLast(paramNode, localNode1);
    }
    else
    {
      int k = 1;
      Object localObject3 = localNode1;
      Object localObject1;
      int i;
      Node localNode3;
      for (;;)
      {
        if (item != null)
        {
          localObject1 = localObject3;
          i = 0;
          break;
        }
        localNode3 = prev;
        if (localNode3 == null)
        {
          if (next == localObject3) {
            return;
          }
          localObject1 = localObject3;
          i = 1;
          break;
        }
        if (localObject3 == localNode3) {
          return;
        }
        localObject3 = localNode3;
        k++;
      }
      localObject3 = localNode2;
      Object localObject2;
      int j;
      for (;;)
      {
        if (item != null)
        {
          localObject2 = localObject3;
          j = 0;
          break;
        }
        localNode3 = next;
        if (localNode3 == null)
        {
          if (prev == localObject3) {
            return;
          }
          localObject2 = localObject3;
          j = 1;
          break;
        }
        if (localObject3 == localNode3) {
          return;
        }
        localObject3 = localNode3;
        k++;
      }
      if ((k < 2) && ((i | j) != 0)) {
        return;
      }
      skipDeletedSuccessors((Node)localObject1);
      skipDeletedPredecessors((Node)localObject2);
      if (((i | j) != 0) && (next == localObject2) && (prev == localObject1) && (i != 0 ? prev == null : item != null) && (j != 0 ? next == null : item != null))
      {
        updateHead();
        updateTail();
        paramNode.lazySetPrev(i != 0 ? prevTerminator() : paramNode);
        paramNode.lazySetNext(j != 0 ? nextTerminator() : paramNode);
      }
    }
  }
  
  private void unlinkFirst(Node<E> paramNode1, Node<E> paramNode2)
  {
    Object localObject1 = null;
    Node localNode;
    for (Object localObject2 = paramNode2;; localObject2 = localNode)
    {
      if ((item != null) || ((localNode = next) == null))
      {
        if ((localObject1 != null) && (prev != localObject2) && (paramNode1.casNext(paramNode2, (Node)localObject2)))
        {
          skipDeletedPredecessors((Node)localObject2);
          if ((prev == null) && ((next == null) || (item != null)) && (prev == paramNode1))
          {
            updateHead();
            updateTail();
            ((Node)localObject1).lazySetNext((Node)localObject1);
            ((Node)localObject1).lazySetPrev(prevTerminator());
          }
        }
        return;
      }
      if (localObject2 == localNode) {
        return;
      }
      localObject1 = localObject2;
    }
  }
  
  private void unlinkLast(Node<E> paramNode1, Node<E> paramNode2)
  {
    Object localObject1 = null;
    Node localNode;
    for (Object localObject2 = paramNode2;; localObject2 = localNode)
    {
      if ((item != null) || ((localNode = prev) == null))
      {
        if ((localObject1 != null) && (next != localObject2) && (paramNode1.casPrev(paramNode2, (Node)localObject2)))
        {
          skipDeletedSuccessors((Node)localObject2);
          if ((next == null) && ((prev == null) || (item != null)) && (next == paramNode1))
          {
            updateHead();
            updateTail();
            ((Node)localObject1).lazySetPrev((Node)localObject1);
            ((Node)localObject1).lazySetNext(nextTerminator());
          }
        }
        return;
      }
      if (localObject2 == localNode) {
        return;
      }
      localObject1 = localObject2;
    }
  }
  
  private final void updateHead()
  {
    Node localNode1;
    Object localObject;
    if ((head).item == null) && ((localObject = prev) != null)) {
      for (;;)
      {
        Node localNode2;
        if (((localNode2 = prev) == null) || ((localNode2 = prev) == null))
        {
          if (!casHead(localNode1, (Node)localObject)) {
            break;
          }
          return;
        }
        if (localNode1 != head) {
          break;
        }
        localObject = localNode2;
      }
    }
  }
  
  private final void updateTail()
  {
    Node localNode1;
    Object localObject;
    if ((tail).item == null) && ((localObject = next) != null)) {
      for (;;)
      {
        Node localNode2;
        if (((localNode2 = next) == null) || ((localNode2 = next) == null))
        {
          if (!casTail(localNode1, (Node)localObject)) {
            break;
          }
          return;
        }
        if (localNode1 != tail) {
          break;
        }
        localObject = localNode2;
      }
    }
  }
  
  private void skipDeletedPredecessors(Node<E> paramNode)
  {
    label69:
    do
    {
      Node localNode1 = prev;
      Node localNode2;
      for (Object localObject = localNode1; item == null; localObject = localNode2)
      {
        localNode2 = prev;
        if (localNode2 == null)
        {
          if (next != localObject) {
            break;
          }
          break label69;
        }
        if (localObject == localNode2) {
          break label69;
        }
      }
      if ((localNode1 == localObject) || (paramNode.casPrev(localNode1, (Node)localObject))) {
        return;
      }
    } while ((item != null) || (next == null));
  }
  
  private void skipDeletedSuccessors(Node<E> paramNode)
  {
    label69:
    do
    {
      Node localNode1 = next;
      Node localNode2;
      for (Object localObject = localNode1; item == null; localObject = localNode2)
      {
        localNode2 = next;
        if (localNode2 == null)
        {
          if (prev != localObject) {
            break;
          }
          break label69;
        }
        if (localObject == localNode2) {
          break label69;
        }
      }
      if ((localNode1 == localObject) || (paramNode.casNext(localNode1, (Node)localObject))) {
        return;
      }
    } while ((item != null) || (prev == null));
  }
  
  final Node<E> succ(Node<E> paramNode)
  {
    Node localNode = next;
    return paramNode == localNode ? first() : localNode;
  }
  
  final Node<E> pred(Node<E> paramNode)
  {
    Node localNode = prev;
    return paramNode == localNode ? last() : localNode;
  }
  
  Node<E> first()
  {
    Node localNode1;
    Object localObject;
    do
    {
      localNode1 = head;
      Node localNode2;
      for (localObject = localNode1; ((localNode2 = prev) != null) && ((localNode2 = prev) != null); localObject = localNode1 != (localNode1 = head) ? localNode1 : localNode2) {}
    } while ((localObject != localNode1) && (!casHead(localNode1, (Node)localObject)));
    return (Node<E>)localObject;
  }
  
  Node<E> last()
  {
    Node localNode1;
    Object localObject;
    do
    {
      localNode1 = tail;
      Node localNode2;
      for (localObject = localNode1; ((localNode2 = next) != null) && ((localNode2 = next) != null); localObject = localNode1 != (localNode1 = tail) ? localNode1 : localNode2) {}
    } while ((localObject != localNode1) && (!casTail(localNode1, (Node)localObject)));
    return (Node<E>)localObject;
  }
  
  private static void checkNotNull(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
  }
  
  private E screenNullResult(E paramE)
  {
    if (paramE == null) {
      throw new NoSuchElementException();
    }
    return paramE;
  }
  
  private ArrayList<E> toArrayList()
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if (localObject != null) {
        localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }
  
  public ConcurrentLinkedDeque()
  {
    head = (tail = new Node(null));
  }
  
  public ConcurrentLinkedDeque(Collection<? extends E> paramCollection)
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
        localNode.lazySetPrev((Node)localObject2);
        localObject2 = localNode;
      }
    }
    initHeadTail((Node)localObject1, (Node)localObject2);
  }
  
  private void initHeadTail(Node<E> paramNode1, Node<E> paramNode2)
  {
    if (paramNode1 == paramNode2) {
      if (paramNode1 == null)
      {
        paramNode1 = paramNode2 = new Node(null);
      }
      else
      {
        Node localNode = new Node(null);
        paramNode2.lazySetNext(localNode);
        localNode.lazySetPrev(paramNode2);
        paramNode2 = localNode;
      }
    }
    head = paramNode1;
    tail = paramNode2;
  }
  
  public void addFirst(E paramE)
  {
    linkFirst(paramE);
  }
  
  public void addLast(E paramE)
  {
    linkLast(paramE);
  }
  
  public boolean offerFirst(E paramE)
  {
    linkFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE)
  {
    linkLast(paramE);
    return true;
  }
  
  public E peekFirst()
  {
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if (localObject != null) {
        return (E)localObject;
      }
    }
    return null;
  }
  
  public E peekLast()
  {
    for (Node localNode = last(); localNode != null; localNode = pred(localNode))
    {
      Object localObject = item;
      if (localObject != null) {
        return (E)localObject;
      }
    }
    return null;
  }
  
  public E getFirst()
  {
    return (E)screenNullResult(peekFirst());
  }
  
  public E getLast()
  {
    return (E)screenNullResult(peekLast());
  }
  
  public E pollFirst()
  {
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if ((localObject != null) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return (E)localObject;
      }
    }
    return null;
  }
  
  public E pollLast()
  {
    for (Node localNode = last(); localNode != null; localNode = pred(localNode))
    {
      Object localObject = item;
      if ((localObject != null) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return (E)localObject;
      }
    }
    return null;
  }
  
  public E removeFirst()
  {
    return (E)screenNullResult(pollFirst());
  }
  
  public E removeLast()
  {
    return (E)screenNullResult(pollLast());
  }
  
  public boolean offer(E paramE)
  {
    return offerLast(paramE);
  }
  
  public boolean add(E paramE)
  {
    return offerLast(paramE);
  }
  
  public E poll()
  {
    return (E)pollFirst();
  }
  
  public E peek()
  {
    return (E)peekFirst();
  }
  
  public E remove()
  {
    return (E)removeFirst();
  }
  
  public E pop()
  {
    return (E)removeFirst();
  }
  
  public E element()
  {
    return (E)getFirst();
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    checkNotNull(paramObject);
    for (Node localNode = first(); localNode != null; localNode = succ(localNode))
    {
      Object localObject = item;
      if ((localObject != null) && (paramObject.equals(localObject)) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return true;
      }
    }
    return false;
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    checkNotNull(paramObject);
    for (Node localNode = last(); localNode != null; localNode = pred(localNode))
    {
      Object localObject = item;
      if ((localObject != null) && (paramObject.equals(localObject)) && (localNode.casItem(localObject, null)))
      {
        unlink(localNode);
        return true;
      }
    }
    return false;
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
  
  public boolean isEmpty()
  {
    return peekFirst() == null;
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
  
  public boolean remove(Object paramObject)
  {
    return removeFirstOccurrence(paramObject);
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
        localNode.lazySetPrev((Node)localObject2);
        localObject2 = localNode;
      }
    }
    if (localObject1 == null) {
      return false;
    }
    localObject3 = tail;
    Object localObject4 = localObject3;
    do
    {
      while (((localNode = next) != null) && ((localNode = next) != null)) {
        localObject4 = localObject3 != (localObject3 = tail) ? localObject3 : localNode;
      }
      if (prev == localObject4) {
        break;
      }
      ((Node)localObject1).lazySetPrev((Node)localObject4);
    } while (!((Node)localObject4).casNext(null, (Node)localObject1));
    if (!casTail((Node)localObject3, (Node)localObject2))
    {
      localObject3 = tail;
      if (next == null) {
        casTail((Node)localObject3, (Node)localObject2);
      }
    }
    return true;
  }
  
  public void clear()
  {
    while (pollFirst() != null) {}
  }
  
  public Object[] toArray()
  {
    return toArrayList().toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    return toArrayList().toArray(paramArrayOfT);
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
    return new CLDSpliterator(this);
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
        localNode.lazySetPrev((Node)localObject2);
        localObject2 = localNode;
      }
    }
    initHeadTail((Node)localObject1, (Node)localObject2);
  }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2);
  }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2)
  {
    return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  static
  {
    PREV_TERMINATORnext = PREV_TERMINATOR;
    NEXT_TERMINATOR = new Node();
    NEXT_TERMINATORprev = NEXT_TERMINATOR;
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = ConcurrentLinkedDeque.class;
      headOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("tail"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  private abstract class AbstractItr
    implements Iterator<E>
  {
    private ConcurrentLinkedDeque.Node<E> nextNode;
    private E nextItem;
    private ConcurrentLinkedDeque.Node<E> lastRet;
    
    abstract ConcurrentLinkedDeque.Node<E> startNode();
    
    abstract ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> paramNode);
    
    AbstractItr()
    {
      advance();
    }
    
    private void advance()
    {
      lastRet = nextNode;
      for (ConcurrentLinkedDeque.Node localNode = nextNode == null ? startNode() : nextNode(nextNode);; localNode = nextNode(localNode))
      {
        if (localNode == null)
        {
          nextNode = null;
          nextItem = null;
          break;
        }
        Object localObject = item;
        if (localObject != null)
        {
          nextNode = localNode;
          nextItem = localObject;
          break;
        }
      }
    }
    
    public boolean hasNext()
    {
      return nextItem != null;
    }
    
    public E next()
    {
      Object localObject = nextItem;
      if (localObject == null) {
        throw new NoSuchElementException();
      }
      advance();
      return (E)localObject;
    }
    
    public void remove()
    {
      ConcurrentLinkedDeque.Node localNode = lastRet;
      if (localNode == null) {
        throw new IllegalStateException();
      }
      item = null;
      unlink(localNode);
      lastRet = null;
    }
  }
  
  static final class CLDSpliterator<E>
    implements Spliterator<E>
  {
    static final int MAX_BATCH = 33554432;
    final ConcurrentLinkedDeque<E> queue;
    ConcurrentLinkedDeque.Node<E> current;
    int batch;
    boolean exhausted;
    
    CLDSpliterator(ConcurrentLinkedDeque<E> paramConcurrentLinkedDeque)
    {
      queue = paramConcurrentLinkedDeque;
    }
    
    public Spliterator<E> trySplit()
    {
      ConcurrentLinkedDeque localConcurrentLinkedDeque = queue;
      int i = batch;
      int j = i >= 33554432 ? 33554432 : i <= 0 ? 1 : i + 1;
      ConcurrentLinkedDeque.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localConcurrentLinkedDeque.first()) != null)))
      {
        if ((item == null) && (localNode == (localNode = next))) {
          current = (localNode = localConcurrentLinkedDeque.first());
        }
        if ((localNode != null) && (next != null))
        {
          Object[] arrayOfObject = new Object[j];
          int k = 0;
          do
          {
            if ((arrayOfObject[k] = item) != null) {
              k++;
            }
            if (localNode == (localNode = next)) {
              localNode = localConcurrentLinkedDeque.first();
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
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      ConcurrentLinkedDeque localConcurrentLinkedDeque = queue;
      ConcurrentLinkedDeque.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localConcurrentLinkedDeque.first()) != null)))
      {
        exhausted = true;
        do
        {
          Object localObject = item;
          if (localNode == (localNode = next)) {
            localNode = localConcurrentLinkedDeque.first();
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
      ConcurrentLinkedDeque localConcurrentLinkedDeque = queue;
      ConcurrentLinkedDeque.Node localNode;
      if ((!exhausted) && (((localNode = current) != null) || ((localNode = localConcurrentLinkedDeque.first()) != null)))
      {
        Object localObject;
        do
        {
          localObject = item;
          if (localNode == (localNode = next)) {
            localNode = localConcurrentLinkedDeque.first();
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
  
  private class DescendingItr
    extends ConcurrentLinkedDeque<E>.AbstractItr
  {
    private DescendingItr()
    {
      super();
    }
    
    ConcurrentLinkedDeque.Node<E> startNode()
    {
      return last();
    }
    
    ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> paramNode)
    {
      return pred(paramNode);
    }
  }
  
  private class Itr
    extends ConcurrentLinkedDeque<E>.AbstractItr
  {
    private Itr()
    {
      super();
    }
    
    ConcurrentLinkedDeque.Node<E> startNode()
    {
      return first();
    }
    
    ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> paramNode)
    {
      return succ(paramNode);
    }
  }
  
  static final class Node<E>
  {
    volatile Node<E> prev;
    volatile E item;
    volatile Node<E> next;
    private static final Unsafe UNSAFE;
    private static final long prevOffset;
    private static final long itemOffset;
    private static final long nextOffset;
    
    Node() {}
    
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
    
    void lazySetPrev(Node<E> paramNode)
    {
      UNSAFE.putOrderedObject(this, prevOffset, paramNode);
    }
    
    boolean casPrev(Node<E> paramNode1, Node<E> paramNode2)
    {
      return UNSAFE.compareAndSwapObject(this, prevOffset, paramNode1, paramNode2);
    }
    
    static
    {
      try
      {
        UNSAFE = Unsafe.getUnsafe();
        Class localClass = Node.class;
        prevOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("prev"));
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\ConcurrentLinkedDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
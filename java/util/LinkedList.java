package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;

public class LinkedList<E>
  extends AbstractSequentialList<E>
  implements List<E>, Deque<E>, Cloneable, Serializable
{
  transient int size = 0;
  transient Node<E> first;
  transient Node<E> last;
  private static final long serialVersionUID = 876323262645176354L;
  
  public LinkedList() {}
  
  public LinkedList(Collection<? extends E> paramCollection)
  {
    this();
    addAll(paramCollection);
  }
  
  private void linkFirst(E paramE)
  {
    Node localNode1 = first;
    Node localNode2 = new Node(null, paramE, localNode1);
    first = localNode2;
    if (localNode1 == null) {
      last = localNode2;
    } else {
      prev = localNode2;
    }
    size += 1;
    modCount += 1;
  }
  
  void linkLast(E paramE)
  {
    Node localNode1 = last;
    Node localNode2 = new Node(localNode1, paramE, null);
    last = localNode2;
    if (localNode1 == null) {
      first = localNode2;
    } else {
      next = localNode2;
    }
    size += 1;
    modCount += 1;
  }
  
  void linkBefore(E paramE, Node<E> paramNode)
  {
    Node localNode1 = prev;
    Node localNode2 = new Node(localNode1, paramE, paramNode);
    prev = localNode2;
    if (localNode1 == null) {
      first = localNode2;
    } else {
      next = localNode2;
    }
    size += 1;
    modCount += 1;
  }
  
  private E unlinkFirst(Node<E> paramNode)
  {
    Object localObject = item;
    Node localNode = next;
    item = null;
    next = null;
    first = localNode;
    if (localNode == null) {
      last = null;
    } else {
      prev = null;
    }
    size -= 1;
    modCount += 1;
    return (E)localObject;
  }
  
  private E unlinkLast(Node<E> paramNode)
  {
    Object localObject = item;
    Node localNode = prev;
    item = null;
    prev = null;
    last = localNode;
    if (localNode == null) {
      first = null;
    } else {
      next = null;
    }
    size -= 1;
    modCount += 1;
    return (E)localObject;
  }
  
  E unlink(Node<E> paramNode)
  {
    Object localObject = item;
    Node localNode1 = next;
    Node localNode2 = prev;
    if (localNode2 == null)
    {
      first = localNode1;
    }
    else
    {
      next = localNode1;
      prev = null;
    }
    if (localNode1 == null)
    {
      last = localNode2;
    }
    else
    {
      prev = localNode2;
      next = null;
    }
    item = null;
    size -= 1;
    modCount += 1;
    return (E)localObject;
  }
  
  public E getFirst()
  {
    Node localNode = first;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return (E)item;
  }
  
  public E getLast()
  {
    Node localNode = last;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return (E)item;
  }
  
  public E removeFirst()
  {
    Node localNode = first;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return (E)unlinkFirst(localNode);
  }
  
  public E removeLast()
  {
    Node localNode = last;
    if (localNode == null) {
      throw new NoSuchElementException();
    }
    return (E)unlinkLast(localNode);
  }
  
  public void addFirst(E paramE)
  {
    linkFirst(paramE);
  }
  
  public void addLast(E paramE)
  {
    linkLast(paramE);
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject) != -1;
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean add(E paramE)
  {
    linkLast(paramE);
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    Node localNode;
    if (paramObject == null) {
      for (localNode = first; localNode != null; localNode = next) {
        if (item == null)
        {
          unlink(localNode);
          return true;
        }
      }
    } else {
      for (localNode = first; localNode != null; localNode = next) {
        if (paramObject.equals(item))
        {
          unlink(localNode);
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    return addAll(size, paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    checkPositionIndex(paramInt);
    Object[] arrayOfObject1 = paramCollection.toArray();
    int i = arrayOfObject1.length;
    if (i == 0) {
      return false;
    }
    Node localNode1;
    Object localObject1;
    if (paramInt == size)
    {
      localNode1 = null;
      localObject1 = last;
    }
    else
    {
      localNode1 = node(paramInt);
      localObject1 = prev;
    }
    for (Object localObject2 : arrayOfObject1)
    {
      Object localObject3 = localObject2;
      Node localNode2 = new Node((Node)localObject1, localObject3, null);
      if (localObject1 == null) {
        first = localNode2;
      } else {
        next = localNode2;
      }
      localObject1 = localNode2;
    }
    if (localNode1 == null)
    {
      last = ((Node)localObject1);
    }
    else
    {
      next = localNode1;
      prev = ((Node)localObject1);
    }
    size += i;
    modCount += 1;
    return true;
  }
  
  public void clear()
  {
    Node localNode;
    for (Object localObject = first; localObject != null; localObject = localNode)
    {
      localNode = next;
      item = null;
      next = null;
      prev = null;
    }
    first = (last = null);
    size = 0;
    modCount += 1;
  }
  
  public E get(int paramInt)
  {
    checkElementIndex(paramInt);
    return (E)nodeitem;
  }
  
  public E set(int paramInt, E paramE)
  {
    checkElementIndex(paramInt);
    Node localNode = node(paramInt);
    Object localObject = item;
    item = paramE;
    return (E)localObject;
  }
  
  public void add(int paramInt, E paramE)
  {
    checkPositionIndex(paramInt);
    if (paramInt == size) {
      linkLast(paramE);
    } else {
      linkBefore(paramE, node(paramInt));
    }
  }
  
  public E remove(int paramInt)
  {
    checkElementIndex(paramInt);
    return (E)unlink(node(paramInt));
  }
  
  private boolean isElementIndex(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < size);
  }
  
  private boolean isPositionIndex(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= size);
  }
  
  private String outOfBoundsMsg(int paramInt)
  {
    return "Index: " + paramInt + ", Size: " + size;
  }
  
  private void checkElementIndex(int paramInt)
  {
    if (!isElementIndex(paramInt)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private void checkPositionIndex(int paramInt)
  {
    if (!isPositionIndex(paramInt)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  Node<E> node(int paramInt)
  {
    if (paramInt < size >> 1)
    {
      localNode = first;
      for (i = 0; i < paramInt; i++) {
        localNode = next;
      }
      return localNode;
    }
    Node localNode = last;
    for (int i = size - 1; i > paramInt; i--) {
      localNode = prev;
    }
    return localNode;
  }
  
  public int indexOf(Object paramObject)
  {
    int i = 0;
    Node localNode;
    if (paramObject == null) {
      for (localNode = first; localNode != null; localNode = next)
      {
        if (item == null) {
          return i;
        }
        i++;
      }
    } else {
      for (localNode = first; localNode != null; localNode = next)
      {
        if (paramObject.equals(item)) {
          return i;
        }
        i++;
      }
    }
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    int i = size;
    Node localNode;
    if (paramObject == null) {
      for (localNode = last; localNode != null; localNode = prev)
      {
        i--;
        if (item == null) {
          return i;
        }
      }
    } else {
      for (localNode = last; localNode != null; localNode = prev)
      {
        i--;
        if (paramObject.equals(item)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public E peek()
  {
    Node localNode = first;
    return localNode == null ? null : item;
  }
  
  public E element()
  {
    return (E)getFirst();
  }
  
  public E poll()
  {
    Node localNode = first;
    return localNode == null ? null : unlinkFirst(localNode);
  }
  
  public E remove()
  {
    return (E)removeFirst();
  }
  
  public boolean offer(E paramE)
  {
    return add(paramE);
  }
  
  public boolean offerFirst(E paramE)
  {
    addFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE)
  {
    addLast(paramE);
    return true;
  }
  
  public E peekFirst()
  {
    Node localNode = first;
    return localNode == null ? null : item;
  }
  
  public E peekLast()
  {
    Node localNode = last;
    return localNode == null ? null : item;
  }
  
  public E pollFirst()
  {
    Node localNode = first;
    return localNode == null ? null : unlinkFirst(localNode);
  }
  
  public E pollLast()
  {
    Node localNode = last;
    return localNode == null ? null : unlinkLast(localNode);
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public E pop()
  {
    return (E)removeFirst();
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    return remove(paramObject);
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    Node localNode;
    if (paramObject == null) {
      for (localNode = last; localNode != null; localNode = prev) {
        if (item == null)
        {
          unlink(localNode);
          return true;
        }
      }
    } else {
      for (localNode = last; localNode != null; localNode = prev) {
        if (paramObject.equals(item))
        {
          unlink(localNode);
          return true;
        }
      }
    }
    return false;
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    checkPositionIndex(paramInt);
    return new ListItr(paramInt);
  }
  
  public Iterator<E> descendingIterator()
  {
    return new DescendingIterator(null);
  }
  
  private LinkedList<E> superClone()
  {
    try
    {
      return (LinkedList)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public Object clone()
  {
    LinkedList localLinkedList = superClone();
    first = (last = null);
    size = 0;
    modCount = 0;
    for (Node localNode = first; localNode != null; localNode = next) {
      localLinkedList.add(item);
    }
    return localLinkedList;
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[size];
    int i = 0;
    for (Node localNode = first; localNode != null; localNode = next) {
      arrayOfObject[(i++)] = item;
    }
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length < size) {
      paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), size);
    }
    int i = 0;
    T[] arrayOfT = paramArrayOfT;
    for (Node localNode = first; localNode != null; localNode = next) {
      arrayOfT[(i++)] = item;
    }
    if (paramArrayOfT.length > size) {
      paramArrayOfT[size] = null;
    }
    return paramArrayOfT;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size);
    for (Node localNode = first; localNode != null; localNode = next) {
      paramObjectOutputStream.writeObject(item);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    for (int j = 0; j < i; j++) {
      linkLast(paramObjectInputStream.readObject());
    }
  }
  
  public Spliterator<E> spliterator()
  {
    return new LLSpliterator(this, -1, 0);
  }
  
  private class DescendingIterator
    implements Iterator<E>
  {
    private final LinkedList<E>.ListItr itr = new LinkedList.ListItr(LinkedList.this, size());
    
    private DescendingIterator() {}
    
    public boolean hasNext()
    {
      return itr.hasPrevious();
    }
    
    public E next()
    {
      return (E)itr.previous();
    }
    
    public void remove()
    {
      itr.remove();
    }
  }
  
  static final class LLSpliterator<E>
    implements Spliterator<E>
  {
    static final int BATCH_UNIT = 1024;
    static final int MAX_BATCH = 33554432;
    final LinkedList<E> list;
    LinkedList.Node<E> current;
    int est;
    int expectedModCount;
    int batch;
    
    LLSpliterator(LinkedList<E> paramLinkedList, int paramInt1, int paramInt2)
    {
      list = paramLinkedList;
      est = paramInt1;
      expectedModCount = paramInt2;
    }
    
    final int getEst()
    {
      int i;
      if ((i = est) < 0)
      {
        LinkedList localLinkedList;
        if ((localLinkedList = list) == null)
        {
          i = est = 0;
        }
        else
        {
          expectedModCount = modCount;
          current = first;
          i = est = size;
        }
      }
      return i;
    }
    
    public long estimateSize()
    {
      return getEst();
    }
    
    public Spliterator<E> trySplit()
    {
      int i = getEst();
      LinkedList.Node localNode;
      if ((i > 1) && ((localNode = current) != null))
      {
        int j = batch + 1024;
        if (j > i) {
          j = i;
        }
        if (j > 33554432) {
          j = 33554432;
        }
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        do
        {
          arrayOfObject[(k++)] = item;
        } while (((localNode = next) != null) && (k < j));
        current = localNode;
        batch = k;
        est = (i - k);
        return Spliterators.spliterator(arrayOfObject, 0, k, 16);
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i;
      LinkedList.Node localNode;
      if (((i = getEst()) > 0) && ((localNode = current) != null))
      {
        current = null;
        est = 0;
        do
        {
          Object localObject = item;
          localNode = next;
          paramConsumer.accept(localObject);
          if (localNode == null) {
            break;
          }
          i--;
        } while (i > 0);
      }
      if (list.modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      LinkedList.Node localNode;
      if ((getEst() > 0) && ((localNode = current) != null))
      {
        est -= 1;
        Object localObject = item;
        current = next;
        paramConsumer.accept(localObject);
        if (list.modCount != expectedModCount) {
          throw new ConcurrentModificationException();
        }
        return true;
      }
      return false;
    }
    
    public int characteristics()
    {
      return 16464;
    }
  }
  
  private class ListItr
    implements ListIterator<E>
  {
    private LinkedList.Node<E> lastReturned;
    private LinkedList.Node<E> next;
    private int nextIndex;
    private int expectedModCount = modCount;
    
    ListItr(int paramInt)
    {
      next = (paramInt == size ? null : node(paramInt));
      nextIndex = paramInt;
    }
    
    public boolean hasNext()
    {
      return nextIndex < size;
    }
    
    public E next()
    {
      checkForComodification();
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      lastReturned = next;
      next = next.next;
      nextIndex += 1;
      return (E)lastReturned.item;
    }
    
    public boolean hasPrevious()
    {
      return nextIndex > 0;
    }
    
    public E previous()
    {
      checkForComodification();
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      lastReturned = (next = next == null ? last : next.prev);
      nextIndex -= 1;
      return (E)lastReturned.item;
    }
    
    public int nextIndex()
    {
      return nextIndex;
    }
    
    public int previousIndex()
    {
      return nextIndex - 1;
    }
    
    public void remove()
    {
      checkForComodification();
      if (lastReturned == null) {
        throw new IllegalStateException();
      }
      LinkedList.Node localNode = lastReturned.next;
      unlink(lastReturned);
      if (next == lastReturned) {
        next = localNode;
      } else {
        nextIndex -= 1;
      }
      lastReturned = null;
      expectedModCount += 1;
    }
    
    public void set(E paramE)
    {
      if (lastReturned == null) {
        throw new IllegalStateException();
      }
      checkForComodification();
      lastReturned.item = paramE;
    }
    
    public void add(E paramE)
    {
      checkForComodification();
      lastReturned = null;
      if (next == null) {
        linkLast(paramE);
      } else {
        linkBefore(paramE, next);
      }
      nextIndex += 1;
      expectedModCount += 1;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      while ((modCount == expectedModCount) && (nextIndex < size))
      {
        paramConsumer.accept(next.item);
        lastReturned = next;
        next = next.next;
        nextIndex += 1;
      }
      checkForComodification();
    }
    
    final void checkForComodification()
    {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private static class Node<E>
  {
    E item;
    Node<E> next;
    Node<E> prev;
    
    Node(Node<E> paramNode1, E paramE, Node<E> paramNode2)
    {
      item = paramE;
      next = paramNode2;
      prev = paramNode1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\LinkedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
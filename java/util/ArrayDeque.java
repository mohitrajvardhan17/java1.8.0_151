package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;
import sun.misc.JavaOISAccess;
import sun.misc.SharedSecrets;

public class ArrayDeque<E>
  extends AbstractCollection<E>
  implements Deque<E>, Cloneable, Serializable
{
  transient Object[] elements;
  transient int head;
  transient int tail;
  private static final int MIN_INITIAL_CAPACITY = 8;
  private static final long serialVersionUID = 2340985798034038923L;
  
  private static int calculateSize(int paramInt)
  {
    int i = 8;
    if (paramInt >= i)
    {
      i = paramInt;
      i |= i >>> 1;
      i |= i >>> 2;
      i |= i >>> 4;
      i |= i >>> 8;
      i |= i >>> 16;
      i++;
      if (i < 0) {
        i >>>= 1;
      }
    }
    return i;
  }
  
  private void allocateElements(int paramInt)
  {
    elements = new Object[calculateSize(paramInt)];
  }
  
  private void doubleCapacity()
  {
    assert (head == tail);
    int i = head;
    int j = elements.length;
    int k = j - i;
    int m = j << 1;
    if (m < 0) {
      throw new IllegalStateException("Sorry, deque too big");
    }
    Object[] arrayOfObject = new Object[m];
    System.arraycopy(elements, i, arrayOfObject, 0, k);
    System.arraycopy(elements, 0, arrayOfObject, k, i);
    elements = arrayOfObject;
    head = 0;
    tail = j;
  }
  
  private <T> T[] copyElements(T[] paramArrayOfT)
  {
    if (head < tail)
    {
      System.arraycopy(elements, head, paramArrayOfT, 0, size());
    }
    else if (head > tail)
    {
      int i = elements.length - head;
      System.arraycopy(elements, head, paramArrayOfT, 0, i);
      System.arraycopy(elements, 0, paramArrayOfT, i, tail);
    }
    return paramArrayOfT;
  }
  
  public ArrayDeque()
  {
    elements = new Object[16];
  }
  
  public ArrayDeque(int paramInt)
  {
    allocateElements(paramInt);
  }
  
  public ArrayDeque(Collection<? extends E> paramCollection)
  {
    allocateElements(paramCollection.size());
    addAll(paramCollection);
  }
  
  public void addFirst(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    elements[(head = head - 1 & elements.length - 1)] = paramE;
    if (head == tail) {
      doubleCapacity();
    }
  }
  
  public void addLast(E paramE)
  {
    if (paramE == null) {
      throw new NullPointerException();
    }
    elements[tail] = paramE;
    if ((tail = tail + 1 & elements.length - 1) == head) {
      doubleCapacity();
    }
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
    int i = head;
    Object localObject = elements[i];
    if (localObject == null) {
      return null;
    }
    elements[i] = null;
    head = (i + 1 & elements.length - 1);
    return (E)localObject;
  }
  
  public E pollLast()
  {
    int i = tail - 1 & elements.length - 1;
    Object localObject = elements[i];
    if (localObject == null) {
      return null;
    }
    elements[i] = null;
    tail = i;
    return (E)localObject;
  }
  
  public E getFirst()
  {
    Object localObject = elements[head];
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (E)localObject;
  }
  
  public E getLast()
  {
    Object localObject = elements[(tail - 1 & elements.length - 1)];
    if (localObject == null) {
      throw new NoSuchElementException();
    }
    return (E)localObject;
  }
  
  public E peekFirst()
  {
    return (E)elements[head];
  }
  
  public E peekLast()
  {
    return (E)elements[(tail - 1 & elements.length - 1)];
  }
  
  public boolean removeFirstOccurrence(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    int i = elements.length - 1;
    Object localObject;
    for (int j = head; (localObject = elements[j]) != null; j = j + 1 & i) {
      if (paramObject.equals(localObject))
      {
        delete(j);
        return true;
      }
    }
    return false;
  }
  
  public boolean removeLastOccurrence(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    int i = elements.length - 1;
    Object localObject;
    for (int j = tail - 1 & i; (localObject = elements[j]) != null; j = j - 1 & i) {
      if (paramObject.equals(localObject))
      {
        delete(j);
        return true;
      }
    }
    return false;
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
  
  public E remove()
  {
    return (E)removeFirst();
  }
  
  public E poll()
  {
    return (E)pollFirst();
  }
  
  public E element()
  {
    return (E)getFirst();
  }
  
  public E peek()
  {
    return (E)peekFirst();
  }
  
  public void push(E paramE)
  {
    addFirst(paramE);
  }
  
  public E pop()
  {
    return (E)removeFirst();
  }
  
  private void checkInvariants()
  {
    assert (elements[tail] == null);
    assert (head == tail ? elements[head] != null : (elements[head] != null) && (elements[(tail - 1 & elements.length - 1)] != null));
    assert (elements[(head - 1 & elements.length - 1)] == null);
  }
  
  private boolean delete(int paramInt)
  {
    checkInvariants();
    Object[] arrayOfObject = elements;
    int i = arrayOfObject.length - 1;
    int j = head;
    int k = tail;
    int m = paramInt - j & i;
    int n = k - paramInt & i;
    if (m >= (k - j & i)) {
      throw new ConcurrentModificationException();
    }
    if (m < n)
    {
      if (j <= paramInt)
      {
        System.arraycopy(arrayOfObject, j, arrayOfObject, j + 1, m);
      }
      else
      {
        System.arraycopy(arrayOfObject, 0, arrayOfObject, 1, paramInt);
        arrayOfObject[0] = arrayOfObject[i];
        System.arraycopy(arrayOfObject, j, arrayOfObject, j + 1, i - j);
      }
      arrayOfObject[j] = null;
      head = (j + 1 & i);
      return false;
    }
    if (paramInt < k)
    {
      System.arraycopy(arrayOfObject, paramInt + 1, arrayOfObject, paramInt, n);
      tail = (k - 1);
    }
    else
    {
      System.arraycopy(arrayOfObject, paramInt + 1, arrayOfObject, paramInt, i - paramInt);
      arrayOfObject[i] = arrayOfObject[0];
      System.arraycopy(arrayOfObject, 1, arrayOfObject, 0, k);
      tail = (k - 1 & i);
    }
    return true;
  }
  
  public int size()
  {
    return tail - head & elements.length - 1;
  }
  
  public boolean isEmpty()
  {
    return head == tail;
  }
  
  public Iterator<E> iterator()
  {
    return new DeqIterator(null);
  }
  
  public Iterator<E> descendingIterator()
  {
    return new DescendingIterator(null);
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    int i = elements.length - 1;
    Object localObject;
    for (int j = head; (localObject = elements[j]) != null; j = j + 1 & i) {
      if (paramObject.equals(localObject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean remove(Object paramObject)
  {
    return removeFirstOccurrence(paramObject);
  }
  
  public void clear()
  {
    int i = head;
    int j = tail;
    if (i != j)
    {
      head = (tail = 0);
      int k = i;
      int m = elements.length - 1;
      do
      {
        elements[k] = null;
        k = k + 1 & m;
      } while (k != j);
    }
  }
  
  public Object[] toArray()
  {
    return copyElements(new Object[size()]);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    int i = size();
    if (paramArrayOfT.length < i) {
      paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
    }
    copyElements(paramArrayOfT);
    if (paramArrayOfT.length > i) {
      paramArrayOfT[i] = null;
    }
    return paramArrayOfT;
  }
  
  public ArrayDeque<E> clone()
  {
    try
    {
      ArrayDeque localArrayDeque = (ArrayDeque)super.clone();
      elements = Arrays.copyOf(elements, elements.length);
      return localArrayDeque;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size());
    int i = elements.length - 1;
    for (int j = head; j != tail; j = j + 1 & i) {
      paramObjectOutputStream.writeObject(elements[j]);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    int j = calculateSize(i);
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, j);
    allocateElements(i);
    head = 0;
    tail = i;
    for (int k = 0; k < i; k++) {
      elements[k] = paramObjectInputStream.readObject();
    }
  }
  
  public Spliterator<E> spliterator()
  {
    return new DeqSpliterator(this, -1, -1);
  }
  
  private class DeqIterator
    implements Iterator<E>
  {
    private int cursor = head;
    private int fence = tail;
    private int lastRet = -1;
    
    private DeqIterator() {}
    
    public boolean hasNext()
    {
      return cursor != fence;
    }
    
    public E next()
    {
      if (cursor == fence) {
        throw new NoSuchElementException();
      }
      Object localObject = elements[cursor];
      if ((tail != fence) || (localObject == null)) {
        throw new ConcurrentModificationException();
      }
      lastRet = cursor;
      cursor = (cursor + 1 & elements.length - 1);
      return (E)localObject;
    }
    
    public void remove()
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      if (ArrayDeque.this.delete(lastRet))
      {
        cursor = (cursor - 1 & elements.length - 1);
        fence = tail;
      }
      lastRet = -1;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      Object[] arrayOfObject = elements;
      int i = arrayOfObject.length - 1;
      int j = fence;
      int k = cursor;
      cursor = j;
      while (k != j)
      {
        Object localObject = arrayOfObject[k];
        k = k + 1 & i;
        if (localObject == null) {
          throw new ConcurrentModificationException();
        }
        paramConsumer.accept(localObject);
      }
    }
  }
  
  static final class DeqSpliterator<E>
    implements Spliterator<E>
  {
    private final ArrayDeque<E> deq;
    private int fence;
    private int index;
    
    DeqSpliterator(ArrayDeque<E> paramArrayDeque, int paramInt1, int paramInt2)
    {
      deq = paramArrayDeque;
      index = paramInt1;
      fence = paramInt2;
    }
    
    private int getFence()
    {
      int i;
      if ((i = fence) < 0)
      {
        i = fence = deq.tail;
        index = deq.head;
      }
      return i;
    }
    
    public DeqSpliterator<E> trySplit()
    {
      int i = getFence();
      int j = index;
      int k = deq.elements.length;
      if ((j != i) && ((j + 1 & k - 1) != i))
      {
        if (j > i) {
          i += k;
        }
        int m = j + i >>> 1 & k - 1;
        return new DeqSpliterator(deq, j, index = m);
      }
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject = deq.elements;
      int i = arrayOfObject.length - 1;
      int j = getFence();
      int k = index;
      index = j;
      while (k != j)
      {
        Object localObject = arrayOfObject[k];
        k = k + 1 & i;
        if (localObject == null) {
          throw new ConcurrentModificationException();
        }
        paramConsumer.accept(localObject);
      }
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Object[] arrayOfObject = deq.elements;
      int i = arrayOfObject.length - 1;
      int j = getFence();
      int k = index;
      if (k != fence)
      {
        Object localObject = arrayOfObject[k];
        index = (k + 1 & i);
        if (localObject == null) {
          throw new ConcurrentModificationException();
        }
        paramConsumer.accept(localObject);
        return true;
      }
      return false;
    }
    
    public long estimateSize()
    {
      int i = getFence() - index;
      if (i < 0) {
        i += deq.elements.length;
      }
      return i;
    }
    
    public int characteristics()
    {
      return 16720;
    }
  }
  
  private class DescendingIterator
    implements Iterator<E>
  {
    private int cursor = tail;
    private int fence = head;
    private int lastRet = -1;
    
    private DescendingIterator() {}
    
    public boolean hasNext()
    {
      return cursor != fence;
    }
    
    public E next()
    {
      if (cursor == fence) {
        throw new NoSuchElementException();
      }
      cursor = (cursor - 1 & elements.length - 1);
      Object localObject = elements[cursor];
      if ((head != fence) || (localObject == null)) {
        throw new ConcurrentModificationException();
      }
      lastRet = cursor;
      return (E)localObject;
    }
    
    public void remove()
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      if (!ArrayDeque.this.delete(lastRet))
      {
        cursor = (cursor + 1 & elements.length - 1);
        fence = head;
      }
      lastRet = -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ArrayDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
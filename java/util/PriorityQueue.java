package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;
import sun.misc.JavaOISAccess;
import sun.misc.SharedSecrets;

public class PriorityQueue<E>
  extends AbstractQueue<E>
  implements Serializable
{
  private static final long serialVersionUID = -7720805057305804111L;
  private static final int DEFAULT_INITIAL_CAPACITY = 11;
  transient Object[] queue;
  private int size = 0;
  private final Comparator<? super E> comparator;
  transient int modCount = 0;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public PriorityQueue()
  {
    this(11, null);
  }
  
  public PriorityQueue(int paramInt)
  {
    this(paramInt, null);
  }
  
  public PriorityQueue(Comparator<? super E> paramComparator)
  {
    this(11, paramComparator);
  }
  
  public PriorityQueue(int paramInt, Comparator<? super E> paramComparator)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException();
    }
    queue = new Object[paramInt];
    comparator = paramComparator;
  }
  
  public PriorityQueue(Collection<? extends E> paramCollection)
  {
    Object localObject;
    if ((paramCollection instanceof SortedSet))
    {
      localObject = (SortedSet)paramCollection;
      comparator = ((SortedSet)localObject).comparator();
      initElementsFromCollection((Collection)localObject);
    }
    else if ((paramCollection instanceof PriorityQueue))
    {
      localObject = (PriorityQueue)paramCollection;
      comparator = ((PriorityQueue)localObject).comparator();
      initFromPriorityQueue((PriorityQueue)localObject);
    }
    else
    {
      comparator = null;
      initFromCollection(paramCollection);
    }
  }
  
  public PriorityQueue(PriorityQueue<? extends E> paramPriorityQueue)
  {
    comparator = paramPriorityQueue.comparator();
    initFromPriorityQueue(paramPriorityQueue);
  }
  
  public PriorityQueue(SortedSet<? extends E> paramSortedSet)
  {
    comparator = paramSortedSet.comparator();
    initElementsFromCollection(paramSortedSet);
  }
  
  private void initFromPriorityQueue(PriorityQueue<? extends E> paramPriorityQueue)
  {
    if (paramPriorityQueue.getClass() == PriorityQueue.class)
    {
      queue = paramPriorityQueue.toArray();
      size = paramPriorityQueue.size();
    }
    else
    {
      initFromCollection(paramPriorityQueue);
    }
  }
  
  private void initElementsFromCollection(Collection<? extends E> paramCollection)
  {
    Object[] arrayOfObject = paramCollection.toArray();
    if (arrayOfObject.getClass() != Object[].class) {
      arrayOfObject = Arrays.copyOf(arrayOfObject, arrayOfObject.length, Object[].class);
    }
    int i = arrayOfObject.length;
    if ((i == 1) || (comparator != null)) {
      for (int j = 0; j < i; j++) {
        if (arrayOfObject[j] == null) {
          throw new NullPointerException();
        }
      }
    }
    queue = arrayOfObject;
    size = arrayOfObject.length;
  }
  
  private void initFromCollection(Collection<? extends E> paramCollection)
  {
    initElementsFromCollection(paramCollection);
    heapify();
  }
  
  private void grow(int paramInt)
  {
    int i = queue.length;
    int j = i + (i < 64 ? i + 2 : i >> 1);
    if (j - 2147483639 > 0) {
      j = hugeCapacity(paramInt);
    }
    queue = Arrays.copyOf(queue, j);
  }
  
  private static int hugeCapacity(int paramInt)
  {
    if (paramInt < 0) {
      throw new OutOfMemoryError();
    }
    return paramInt > 2147483639 ? Integer.MAX_VALUE : 2147483639;
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
    modCount += 1;
    int i = size;
    if (i >= queue.length) {
      grow(i + 1);
    }
    size = (i + 1);
    if (i == 0) {
      queue[0] = paramE;
    } else {
      siftUp(i, paramE);
    }
    return true;
  }
  
  public E peek()
  {
    return size == 0 ? null : queue[0];
  }
  
  private int indexOf(Object paramObject)
  {
    if (paramObject != null) {
      for (int i = 0; i < size; i++) {
        if (paramObject.equals(queue[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public boolean remove(Object paramObject)
  {
    int i = indexOf(paramObject);
    if (i == -1) {
      return false;
    }
    removeAt(i);
    return true;
  }
  
  boolean removeEq(Object paramObject)
  {
    for (int i = 0; i < size; i++) {
      if (paramObject == queue[i])
      {
        removeAt(i);
        return true;
      }
    }
    return false;
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject) != -1;
  }
  
  public Object[] toArray()
  {
    return Arrays.copyOf(queue, size);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    int i = size;
    if (paramArrayOfT.length < i) {
      return (Object[])Arrays.copyOf(queue, i, paramArrayOfT.getClass());
    }
    System.arraycopy(queue, 0, paramArrayOfT, 0, i);
    if (paramArrayOfT.length > i) {
      paramArrayOfT[i] = null;
    }
    return paramArrayOfT;
  }
  
  public Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public int size()
  {
    return size;
  }
  
  public void clear()
  {
    modCount += 1;
    for (int i = 0; i < size; i++) {
      queue[i] = null;
    }
    size = 0;
  }
  
  public E poll()
  {
    if (size == 0) {
      return null;
    }
    int i = --size;
    modCount += 1;
    Object localObject1 = queue[0];
    Object localObject2 = queue[i];
    queue[i] = null;
    if (i != 0) {
      siftDown(0, localObject2);
    }
    return (E)localObject1;
  }
  
  private E removeAt(int paramInt)
  {
    modCount += 1;
    int i = --size;
    if (i == paramInt)
    {
      queue[paramInt] = null;
    }
    else
    {
      Object localObject = queue[i];
      queue[i] = null;
      siftDown(paramInt, localObject);
      if (queue[paramInt] == localObject)
      {
        siftUp(paramInt, localObject);
        if (queue[paramInt] != localObject) {
          return (E)localObject;
        }
      }
    }
    return null;
  }
  
  private void siftUp(int paramInt, E paramE)
  {
    if (comparator != null) {
      siftUpUsingComparator(paramInt, paramE);
    } else {
      siftUpComparable(paramInt, paramE);
    }
  }
  
  private void siftUpComparable(int paramInt, E paramE)
  {
    Comparable localComparable = (Comparable)paramE;
    while (paramInt > 0)
    {
      int i = paramInt - 1 >>> 1;
      Object localObject = queue[i];
      if (localComparable.compareTo(localObject) >= 0) {
        break;
      }
      queue[paramInt] = localObject;
      paramInt = i;
    }
    queue[paramInt] = localComparable;
  }
  
  private void siftUpUsingComparator(int paramInt, E paramE)
  {
    while (paramInt > 0)
    {
      int i = paramInt - 1 >>> 1;
      Object localObject = queue[i];
      if (comparator.compare(paramE, localObject) >= 0) {
        break;
      }
      queue[paramInt] = localObject;
      paramInt = i;
    }
    queue[paramInt] = paramE;
  }
  
  private void siftDown(int paramInt, E paramE)
  {
    if (comparator != null) {
      siftDownUsingComparator(paramInt, paramE);
    } else {
      siftDownComparable(paramInt, paramE);
    }
  }
  
  private void siftDownComparable(int paramInt, E paramE)
  {
    Comparable localComparable = (Comparable)paramE;
    int i = size >>> 1;
    while (paramInt < i)
    {
      int j = (paramInt << 1) + 1;
      Object localObject = queue[j];
      int k = j + 1;
      if ((k < size) && (((Comparable)localObject).compareTo(queue[k]) > 0)) {
        localObject = queue[(j = k)];
      }
      if (localComparable.compareTo(localObject) <= 0) {
        break;
      }
      queue[paramInt] = localObject;
      paramInt = j;
    }
    queue[paramInt] = localComparable;
  }
  
  private void siftDownUsingComparator(int paramInt, E paramE)
  {
    int i = size >>> 1;
    while (paramInt < i)
    {
      int j = (paramInt << 1) + 1;
      Object localObject = queue[j];
      int k = j + 1;
      if ((k < size) && (comparator.compare(localObject, queue[k]) > 0)) {
        localObject = queue[(j = k)];
      }
      if (comparator.compare(paramE, localObject) <= 0) {
        break;
      }
      queue[paramInt] = localObject;
      paramInt = j;
    }
    queue[paramInt] = paramE;
  }
  
  private void heapify()
  {
    for (int i = (size >>> 1) - 1; i >= 0; i--) {
      siftDown(i, queue[i]);
    }
  }
  
  public Comparator<? super E> comparator()
  {
    return comparator;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(Math.max(2, size + 1));
    for (int i = 0; i < size; i++) {
      paramObjectOutputStream.writeObject(queue[i]);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readInt();
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, size);
    queue = new Object[size];
    for (int i = 0; i < size; i++) {
      queue[i] = paramObjectInputStream.readObject();
    }
    heapify();
  }
  
  public final Spliterator<E> spliterator()
  {
    return new PriorityQueueSpliterator(this, 0, -1, 0);
  }
  
  private final class Itr
    implements Iterator<E>
  {
    private int cursor = 0;
    private int lastRet = -1;
    private ArrayDeque<E> forgetMeNot = null;
    private E lastRetElt = null;
    private int expectedModCount = modCount;
    
    private Itr() {}
    
    public boolean hasNext()
    {
      return (cursor < size) || ((forgetMeNot != null) && (!forgetMeNot.isEmpty()));
    }
    
    public E next()
    {
      if (expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      if (cursor < size) {
        return (E)queue[(lastRet = cursor++)];
      }
      if (forgetMeNot != null)
      {
        lastRet = -1;
        lastRetElt = forgetMeNot.poll();
        if (lastRetElt != null) {
          return (E)lastRetElt;
        }
      }
      throw new NoSuchElementException();
    }
    
    public void remove()
    {
      if (expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      if (lastRet != -1)
      {
        Object localObject = PriorityQueue.this.removeAt(lastRet);
        lastRet = -1;
        if (localObject == null)
        {
          cursor -= 1;
        }
        else
        {
          if (forgetMeNot == null) {
            forgetMeNot = new ArrayDeque();
          }
          forgetMeNot.add(localObject);
        }
      }
      else if (lastRetElt != null)
      {
        removeEq(lastRetElt);
        lastRetElt = null;
      }
      else
      {
        throw new IllegalStateException();
      }
      expectedModCount = modCount;
    }
  }
  
  static final class PriorityQueueSpliterator<E>
    implements Spliterator<E>
  {
    private final PriorityQueue<E> pq;
    private int index;
    private int fence;
    private int expectedModCount;
    
    PriorityQueueSpliterator(PriorityQueue<E> paramPriorityQueue, int paramInt1, int paramInt2, int paramInt3)
    {
      pq = paramPriorityQueue;
      index = paramInt1;
      fence = paramInt2;
      expectedModCount = paramInt3;
    }
    
    private int getFence()
    {
      int i;
      if ((i = fence) < 0)
      {
        expectedModCount = pq.modCount;
        i = fence = pq.size;
      }
      return i;
    }
    
    public PriorityQueueSpliterator<E> trySplit()
    {
      int i = getFence();
      int j = index;
      int k = j + i >>> 1;
      return j >= k ? null : new PriorityQueueSpliterator(pq, j, index = k, expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      PriorityQueue localPriorityQueue;
      Object[] arrayOfObject;
      if (((localPriorityQueue = pq) != null) && ((arrayOfObject = queue) != null))
      {
        int j;
        int k;
        if ((j = fence) < 0)
        {
          k = modCount;
          j = size;
        }
        else
        {
          k = expectedModCount;
        }
        int i;
        if (((i = index) >= 0) && ((index = j) <= arrayOfObject.length)) {
          for (;;)
          {
            if (i < j)
            {
              Object localObject;
              if ((localObject = arrayOfObject[i]) == null) {
                break;
              }
              paramConsumer.accept(localObject);
            }
            else
            {
              if (modCount != k) {
                break;
              }
              return;
            }
            i++;
          }
        }
      }
      throw new ConcurrentModificationException();
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i = getFence();
      int j = index;
      if ((j >= 0) && (j < i))
      {
        index = (j + 1);
        Object localObject = pq.queue[j];
        if (localObject == null) {
          throw new ConcurrentModificationException();
        }
        paramConsumer.accept(localObject);
        if (pq.modCount != expectedModCount) {
          throw new ConcurrentModificationException();
        }
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\PriorityQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Vector<E>
  extends AbstractList<E>
  implements List<E>, RandomAccess, Cloneable, Serializable
{
  protected Object[] elementData;
  protected int elementCount;
  protected int capacityIncrement;
  private static final long serialVersionUID = -2767605614048989439L;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public Vector(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt1);
    }
    elementData = new Object[paramInt1];
    capacityIncrement = paramInt2;
  }
  
  public Vector(int paramInt)
  {
    this(paramInt, 0);
  }
  
  public Vector()
  {
    this(10);
  }
  
  public Vector(Collection<? extends E> paramCollection)
  {
    elementData = paramCollection.toArray();
    elementCount = elementData.length;
    if (elementData.getClass() != Object[].class) {
      elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
    }
  }
  
  public synchronized void copyInto(Object[] paramArrayOfObject)
  {
    System.arraycopy(elementData, 0, paramArrayOfObject, 0, elementCount);
  }
  
  public synchronized void trimToSize()
  {
    modCount += 1;
    int i = elementData.length;
    if (elementCount < i) {
      elementData = Arrays.copyOf(elementData, elementCount);
    }
  }
  
  public synchronized void ensureCapacity(int paramInt)
  {
    if (paramInt > 0)
    {
      modCount += 1;
      ensureCapacityHelper(paramInt);
    }
  }
  
  private void ensureCapacityHelper(int paramInt)
  {
    if (paramInt - elementData.length > 0) {
      grow(paramInt);
    }
  }
  
  private void grow(int paramInt)
  {
    int i = elementData.length;
    int j = i + (capacityIncrement > 0 ? capacityIncrement : i);
    if (j - paramInt < 0) {
      j = paramInt;
    }
    if (j - 2147483639 > 0) {
      j = hugeCapacity(paramInt);
    }
    elementData = Arrays.copyOf(elementData, j);
  }
  
  private static int hugeCapacity(int paramInt)
  {
    if (paramInt < 0) {
      throw new OutOfMemoryError();
    }
    return paramInt > 2147483639 ? Integer.MAX_VALUE : 2147483639;
  }
  
  public synchronized void setSize(int paramInt)
  {
    modCount += 1;
    if (paramInt > elementCount) {
      ensureCapacityHelper(paramInt);
    } else {
      for (int i = paramInt; i < elementCount; i++) {
        elementData[i] = null;
      }
    }
    elementCount = paramInt;
  }
  
  public synchronized int capacity()
  {
    return elementData.length;
  }
  
  public synchronized int size()
  {
    return elementCount;
  }
  
  public synchronized boolean isEmpty()
  {
    return elementCount == 0;
  }
  
  public Enumeration<E> elements()
  {
    new Enumeration()
    {
      int count = 0;
      
      public boolean hasMoreElements()
      {
        return count < elementCount;
      }
      
      public E nextElement()
      {
        synchronized (Vector.this)
        {
          if (count < elementCount) {
            return (E)elementData(count++);
          }
        }
        throw new NoSuchElementException("Vector Enumeration");
      }
    };
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject, 0) >= 0;
  }
  
  public int indexOf(Object paramObject)
  {
    return indexOf(paramObject, 0);
  }
  
  public synchronized int indexOf(Object paramObject, int paramInt)
  {
    int i;
    if (paramObject == null) {
      for (i = paramInt; i < elementCount; i++) {
        if (elementData[i] == null) {
          return i;
        }
      }
    } else {
      for (i = paramInt; i < elementCount; i++) {
        if (paramObject.equals(elementData[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public synchronized int lastIndexOf(Object paramObject)
  {
    return lastIndexOf(paramObject, elementCount - 1);
  }
  
  public synchronized int lastIndexOf(Object paramObject, int paramInt)
  {
    if (paramInt >= elementCount) {
      throw new IndexOutOfBoundsException(paramInt + " >= " + elementCount);
    }
    int i;
    if (paramObject == null) {
      for (i = paramInt; i >= 0; i--) {
        if (elementData[i] == null) {
          return i;
        }
      }
    } else {
      for (i = paramInt; i >= 0; i--) {
        if (paramObject.equals(elementData[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public synchronized E elementAt(int paramInt)
  {
    if (paramInt >= elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + elementCount);
    }
    return (E)elementData(paramInt);
  }
  
  public synchronized E firstElement()
  {
    if (elementCount == 0) {
      throw new NoSuchElementException();
    }
    return (E)elementData(0);
  }
  
  public synchronized E lastElement()
  {
    if (elementCount == 0) {
      throw new NoSuchElementException();
    }
    return (E)elementData(elementCount - 1);
  }
  
  public synchronized void setElementAt(E paramE, int paramInt)
  {
    if (paramInt >= elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + elementCount);
    }
    elementData[paramInt] = paramE;
  }
  
  public synchronized void removeElementAt(int paramInt)
  {
    modCount += 1;
    if (paramInt >= elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + elementCount);
    }
    if (paramInt < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    int i = elementCount - paramInt - 1;
    if (i > 0) {
      System.arraycopy(elementData, paramInt + 1, elementData, paramInt, i);
    }
    elementCount -= 1;
    elementData[elementCount] = null;
  }
  
  public synchronized void insertElementAt(E paramE, int paramInt)
  {
    modCount += 1;
    if (paramInt > elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " > " + elementCount);
    }
    ensureCapacityHelper(elementCount + 1);
    System.arraycopy(elementData, paramInt, elementData, paramInt + 1, elementCount - paramInt);
    elementData[paramInt] = paramE;
    elementCount += 1;
  }
  
  public synchronized void addElement(E paramE)
  {
    modCount += 1;
    ensureCapacityHelper(elementCount + 1);
    elementData[(elementCount++)] = paramE;
  }
  
  public synchronized boolean removeElement(Object paramObject)
  {
    modCount += 1;
    int i = indexOf(paramObject);
    if (i >= 0)
    {
      removeElementAt(i);
      return true;
    }
    return false;
  }
  
  public synchronized void removeAllElements()
  {
    modCount += 1;
    for (int i = 0; i < elementCount; i++) {
      elementData[i] = null;
    }
    elementCount = 0;
  }
  
  public synchronized Object clone()
  {
    try
    {
      Vector localVector = (Vector)super.clone();
      elementData = Arrays.copyOf(elementData, elementCount);
      modCount = 0;
      return localVector;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public synchronized Object[] toArray()
  {
    return Arrays.copyOf(elementData, elementCount);
  }
  
  public synchronized <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length < elementCount) {
      return (Object[])Arrays.copyOf(elementData, elementCount, paramArrayOfT.getClass());
    }
    System.arraycopy(elementData, 0, paramArrayOfT, 0, elementCount);
    if (paramArrayOfT.length > elementCount) {
      paramArrayOfT[elementCount] = null;
    }
    return paramArrayOfT;
  }
  
  E elementData(int paramInt)
  {
    return (E)elementData[paramInt];
  }
  
  public synchronized E get(int paramInt)
  {
    if (paramInt >= elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    return (E)elementData(paramInt);
  }
  
  public synchronized E set(int paramInt, E paramE)
  {
    if (paramInt >= elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    Object localObject = elementData(paramInt);
    elementData[paramInt] = paramE;
    return (E)localObject;
  }
  
  public synchronized boolean add(E paramE)
  {
    modCount += 1;
    ensureCapacityHelper(elementCount + 1);
    elementData[(elementCount++)] = paramE;
    return true;
  }
  
  public boolean remove(Object paramObject)
  {
    return removeElement(paramObject);
  }
  
  public void add(int paramInt, E paramE)
  {
    insertElementAt(paramE, paramInt);
  }
  
  public synchronized E remove(int paramInt)
  {
    modCount += 1;
    if (paramInt >= elementCount) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    Object localObject = elementData(paramInt);
    int i = elementCount - paramInt - 1;
    if (i > 0) {
      System.arraycopy(elementData, paramInt + 1, elementData, paramInt, i);
    }
    elementData[(--elementCount)] = null;
    return (E)localObject;
  }
  
  public void clear()
  {
    removeAllElements();
  }
  
  public synchronized boolean containsAll(Collection<?> paramCollection)
  {
    return super.containsAll(paramCollection);
  }
  
  public synchronized boolean addAll(Collection<? extends E> paramCollection)
  {
    modCount += 1;
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityHelper(elementCount + i);
    System.arraycopy(arrayOfObject, 0, elementData, elementCount, i);
    elementCount += i;
    return i != 0;
  }
  
  public synchronized boolean removeAll(Collection<?> paramCollection)
  {
    return super.removeAll(paramCollection);
  }
  
  public synchronized boolean retainAll(Collection<?> paramCollection)
  {
    return super.retainAll(paramCollection);
  }
  
  public synchronized boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    modCount += 1;
    if ((paramInt < 0) || (paramInt > elementCount)) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityHelper(elementCount + i);
    int j = elementCount - paramInt;
    if (j > 0) {
      System.arraycopy(elementData, paramInt, elementData, paramInt + i, j);
    }
    System.arraycopy(arrayOfObject, 0, elementData, paramInt, i);
    elementCount += i;
    return i != 0;
  }
  
  public synchronized boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
  
  public synchronized int hashCode()
  {
    return super.hashCode();
  }
  
  public synchronized String toString()
  {
    return super.toString();
  }
  
  public synchronized List<E> subList(int paramInt1, int paramInt2)
  {
    return Collections.synchronizedList(super.subList(paramInt1, paramInt2), this);
  }
  
  protected synchronized void removeRange(int paramInt1, int paramInt2)
  {
    modCount += 1;
    int i = elementCount - paramInt2;
    System.arraycopy(elementData, paramInt2, elementData, paramInt1, i);
    int j = elementCount - (paramInt2 - paramInt1);
    while (elementCount != j) {
      elementData[(--elementCount)] = null;
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    Object[] arrayOfObject;
    synchronized (this)
    {
      localPutField.put("capacityIncrement", capacityIncrement);
      localPutField.put("elementCount", elementCount);
      arrayOfObject = (Object[])elementData.clone();
    }
    localPutField.put("elementData", arrayOfObject);
    paramObjectOutputStream.writeFields();
  }
  
  public synchronized ListIterator<E> listIterator(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > elementCount)) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
    return new ListItr(paramInt);
  }
  
  public synchronized ListIterator<E> listIterator()
  {
    return new ListItr(0);
  }
  
  public synchronized Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public synchronized void forEach(Consumer<? super E> paramConsumer)
  {
    Objects.requireNonNull(paramConsumer);
    int i = modCount;
    Object[] arrayOfObject = (Object[])elementData;
    int j = elementCount;
    for (int k = 0; (modCount == i) && (k < j); k++) {
      paramConsumer.accept(arrayOfObject[k]);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  public synchronized boolean removeIf(Predicate<? super E> paramPredicate)
  {
    Objects.requireNonNull(paramPredicate);
    int i = 0;
    int j = elementCount;
    BitSet localBitSet = new BitSet(j);
    int k = modCount;
    for (int m = 0; (modCount == k) && (m < j); m++)
    {
      Object localObject = elementData[m];
      if (paramPredicate.test(localObject))
      {
        localBitSet.set(m);
        i++;
      }
    }
    if (modCount != k) {
      throw new ConcurrentModificationException();
    }
    m = i > 0 ? 1 : 0;
    if (m != 0)
    {
      int n = j - i;
      int i1 = 0;
      for (int i2 = 0; (i1 < j) && (i2 < n); i2++)
      {
        i1 = localBitSet.nextClearBit(i1);
        elementData[i2] = elementData[i1];
        i1++;
      }
      for (i1 = n; i1 < j; i1++) {
        elementData[i1] = null;
      }
      elementCount = n;
      if (modCount != k) {
        throw new ConcurrentModificationException();
      }
      modCount += 1;
    }
    return m;
  }
  
  public synchronized void replaceAll(UnaryOperator<E> paramUnaryOperator)
  {
    Objects.requireNonNull(paramUnaryOperator);
    int i = modCount;
    int j = elementCount;
    for (int k = 0; (modCount == i) && (k < j); k++) {
      elementData[k] = paramUnaryOperator.apply(elementData[k]);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
    modCount += 1;
  }
  
  public synchronized void sort(Comparator<? super E> paramComparator)
  {
    int i = modCount;
    Arrays.sort((Object[])elementData, 0, elementCount, paramComparator);
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
    modCount += 1;
  }
  
  public Spliterator<E> spliterator()
  {
    return new VectorSpliterator(this, null, 0, -1, 0);
  }
  
  private class Itr
    implements Iterator<E>
  {
    int cursor;
    int lastRet = -1;
    int expectedModCount = modCount;
    
    private Itr() {}
    
    public boolean hasNext()
    {
      return cursor != elementCount;
    }
    
    public E next()
    {
      synchronized (Vector.this)
      {
        checkForComodification();
        int i = cursor;
        if (i >= elementCount) {
          throw new NoSuchElementException();
        }
        cursor = (i + 1);
        return (E)elementData(lastRet = i);
      }
    }
    
    public void remove()
    {
      if (lastRet == -1) {
        throw new IllegalStateException();
      }
      synchronized (Vector.this)
      {
        checkForComodification();
        remove(lastRet);
        expectedModCount = modCount;
      }
      cursor = lastRet;
      lastRet = -1;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      synchronized (Vector.this)
      {
        int i = elementCount;
        int j = cursor;
        if (j >= i) {
          return;
        }
        Object[] arrayOfObject = (Object[])elementData;
        if (j >= arrayOfObject.length) {
          throw new ConcurrentModificationException();
        }
        while ((j != i) && (modCount == expectedModCount)) {
          paramConsumer.accept(arrayOfObject[(j++)]);
        }
        cursor = j;
        lastRet = (j - 1);
        checkForComodification();
      }
    }
    
    final void checkForComodification()
    {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  final class ListItr
    extends Vector<E>.Itr
    implements ListIterator<E>
  {
    ListItr(int paramInt)
    {
      super(null);
      cursor = paramInt;
    }
    
    public boolean hasPrevious()
    {
      return cursor != 0;
    }
    
    public int nextIndex()
    {
      return cursor;
    }
    
    public int previousIndex()
    {
      return cursor - 1;
    }
    
    public E previous()
    {
      synchronized (Vector.this)
      {
        checkForComodification();
        int i = cursor - 1;
        if (i < 0) {
          throw new NoSuchElementException();
        }
        cursor = i;
        return (E)elementData(lastRet = i);
      }
    }
    
    public void set(E paramE)
    {
      if (lastRet == -1) {
        throw new IllegalStateException();
      }
      synchronized (Vector.this)
      {
        checkForComodification();
        set(lastRet, paramE);
      }
    }
    
    public void add(E paramE)
    {
      int i = cursor;
      synchronized (Vector.this)
      {
        checkForComodification();
        add(i, paramE);
        expectedModCount = modCount;
      }
      cursor = (i + 1);
      lastRet = -1;
    }
  }
  
  static final class VectorSpliterator<E>
    implements Spliterator<E>
  {
    private final Vector<E> list;
    private Object[] array;
    private int index;
    private int fence;
    private int expectedModCount;
    
    VectorSpliterator(Vector<E> paramVector, Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3)
    {
      list = paramVector;
      array = paramArrayOfObject;
      index = paramInt1;
      fence = paramInt2;
      expectedModCount = paramInt3;
    }
    
    private int getFence()
    {
      int i;
      if ((i = fence) < 0) {
        synchronized (list)
        {
          array = list.elementData;
          expectedModCount = list.modCount;
          i = fence = list.elementCount;
        }
      }
      return i;
    }
    
    public Spliterator<E> trySplit()
    {
      int i = getFence();
      int j = index;
      int k = j + i >>> 1;
      return j >= k ? null : new VectorSpliterator(list, array, j, index = k, expectedModCount);
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i;
      if (getFence() > (i = index))
      {
        index = (i + 1);
        paramConsumer.accept(array[i]);
        if (list.modCount != expectedModCount) {
          throw new ConcurrentModificationException();
        }
        return true;
      }
      return false;
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      Vector localVector;
      if ((localVector = list) != null)
      {
        int j;
        Object[] arrayOfObject;
        if ((j = fence) < 0) {
          synchronized (localVector)
          {
            expectedModCount = modCount;
            arrayOfObject = array = elementData;
            j = fence = elementCount;
          }
        } else {
          arrayOfObject = array;
        }
        int i;
        if ((arrayOfObject != null) && ((i = index) >= 0) && ((index = j) <= arrayOfObject.length))
        {
          while (i < j) {
            paramConsumer.accept(arrayOfObject[(i++)]);
          }
          if (modCount == expectedModCount) {
            return;
          }
        }
      }
      throw new ConcurrentModificationException();
    }
    
    public long estimateSize()
    {
      return getFence() - index;
    }
    
    public int characteristics()
    {
      return 16464;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Vector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
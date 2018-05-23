package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.JavaOISAccess;
import sun.misc.SharedSecrets;

public class ArrayList<E>
  extends AbstractList<E>
  implements List<E>, RandomAccess, Cloneable, Serializable
{
  private static final long serialVersionUID = 8683452581122892189L;
  private static final int DEFAULT_CAPACITY = 10;
  private static final Object[] EMPTY_ELEMENTDATA = new Object[0];
  private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = new Object[0];
  transient Object[] elementData;
  private int size;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public ArrayList(int paramInt)
  {
    if (paramInt > 0) {
      elementData = new Object[paramInt];
    } else if (paramInt == 0) {
      elementData = EMPTY_ELEMENTDATA;
    } else {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
  }
  
  public ArrayList()
  {
    elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
  }
  
  public ArrayList(Collection<? extends E> paramCollection)
  {
    elementData = paramCollection.toArray();
    if ((size = elementData.length) != 0)
    {
      if (elementData.getClass() != Object[].class) {
        elementData = Arrays.copyOf(elementData, size, Object[].class);
      }
    }
    else {
      elementData = EMPTY_ELEMENTDATA;
    }
  }
  
  public void trimToSize()
  {
    modCount += 1;
    if (size < elementData.length) {
      elementData = (size == 0 ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size));
    }
  }
  
  public void ensureCapacity(int paramInt)
  {
    int i = elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA ? 0 : 10;
    if (paramInt > i) {
      ensureExplicitCapacity(paramInt);
    }
  }
  
  private static int calculateCapacity(Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfObject == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
      return Math.max(10, paramInt);
    }
    return paramInt;
  }
  
  private void ensureCapacityInternal(int paramInt)
  {
    ensureExplicitCapacity(calculateCapacity(elementData, paramInt));
  }
  
  private void ensureExplicitCapacity(int paramInt)
  {
    modCount += 1;
    if (paramInt - elementData.length > 0) {
      grow(paramInt);
    }
  }
  
  private void grow(int paramInt)
  {
    int i = elementData.length;
    int j = i + (i >> 1);
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
  
  public int size()
  {
    return size;
  }
  
  public boolean isEmpty()
  {
    return size == 0;
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject) >= 0;
  }
  
  public int indexOf(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      for (i = 0; i < size; i++) {
        if (elementData[i] == null) {
          return i;
        }
      }
    } else {
      for (i = 0; i < size; i++) {
        if (paramObject.equals(elementData[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      for (i = size - 1; i >= 0; i--) {
        if (elementData[i] == null) {
          return i;
        }
      }
    } else {
      for (i = size - 1; i >= 0; i--) {
        if (paramObject.equals(elementData[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public Object clone()
  {
    try
    {
      ArrayList localArrayList = (ArrayList)super.clone();
      elementData = Arrays.copyOf(elementData, size);
      modCount = 0;
      return localArrayList;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public Object[] toArray()
  {
    return Arrays.copyOf(elementData, size);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    if (paramArrayOfT.length < size) {
      return (Object[])Arrays.copyOf(elementData, size, paramArrayOfT.getClass());
    }
    System.arraycopy(elementData, 0, paramArrayOfT, 0, size);
    if (paramArrayOfT.length > size) {
      paramArrayOfT[size] = null;
    }
    return paramArrayOfT;
  }
  
  E elementData(int paramInt)
  {
    return (E)elementData[paramInt];
  }
  
  public E get(int paramInt)
  {
    rangeCheck(paramInt);
    return (E)elementData(paramInt);
  }
  
  public E set(int paramInt, E paramE)
  {
    rangeCheck(paramInt);
    Object localObject = elementData(paramInt);
    elementData[paramInt] = paramE;
    return (E)localObject;
  }
  
  public boolean add(E paramE)
  {
    ensureCapacityInternal(size + 1);
    elementData[(size++)] = paramE;
    return true;
  }
  
  public void add(int paramInt, E paramE)
  {
    rangeCheckForAdd(paramInt);
    ensureCapacityInternal(size + 1);
    System.arraycopy(elementData, paramInt, elementData, paramInt + 1, size - paramInt);
    elementData[paramInt] = paramE;
    size += 1;
  }
  
  public E remove(int paramInt)
  {
    rangeCheck(paramInt);
    modCount += 1;
    Object localObject = elementData(paramInt);
    int i = size - paramInt - 1;
    if (i > 0) {
      System.arraycopy(elementData, paramInt + 1, elementData, paramInt, i);
    }
    elementData[(--size)] = null;
    return (E)localObject;
  }
  
  public boolean remove(Object paramObject)
  {
    int i;
    if (paramObject == null) {
      for (i = 0; i < size; i++) {
        if (elementData[i] == null)
        {
          fastRemove(i);
          return true;
        }
      }
    } else {
      for (i = 0; i < size; i++) {
        if (paramObject.equals(elementData[i]))
        {
          fastRemove(i);
          return true;
        }
      }
    }
    return false;
  }
  
  private void fastRemove(int paramInt)
  {
    modCount += 1;
    int i = size - paramInt - 1;
    if (i > 0) {
      System.arraycopy(elementData, paramInt + 1, elementData, paramInt, i);
    }
    elementData[(--size)] = null;
  }
  
  public void clear()
  {
    modCount += 1;
    for (int i = 0; i < size; i++) {
      elementData[i] = null;
    }
    size = 0;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityInternal(size + i);
    System.arraycopy(arrayOfObject, 0, elementData, size, i);
    size += i;
    return i != 0;
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    rangeCheckForAdd(paramInt);
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacityInternal(size + i);
    int j = size - paramInt;
    if (j > 0) {
      System.arraycopy(elementData, paramInt, elementData, paramInt + i, j);
    }
    System.arraycopy(arrayOfObject, 0, elementData, paramInt, i);
    size += i;
    return i != 0;
  }
  
  protected void removeRange(int paramInt1, int paramInt2)
  {
    modCount += 1;
    int i = size - paramInt2;
    System.arraycopy(elementData, paramInt2, elementData, paramInt1, i);
    int j = size - (paramInt2 - paramInt1);
    for (int k = j; k < size; k++) {
      elementData[k] = null;
    }
    size = j;
  }
  
  private void rangeCheck(int paramInt)
  {
    if (paramInt >= size) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private void rangeCheckForAdd(int paramInt)
  {
    if ((paramInt > size) || (paramInt < 0)) {
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
    }
  }
  
  private String outOfBoundsMsg(int paramInt)
  {
    return "Index: " + paramInt + ", Size: " + size;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    Objects.requireNonNull(paramCollection);
    return batchRemove(paramCollection, false);
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    Objects.requireNonNull(paramCollection);
    return batchRemove(paramCollection, true);
  }
  
  /* Error */
  private boolean batchRemove(Collection<?> paramCollection, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 298	java/util/ArrayList:elementData	[Ljava/lang/Object;
    //   4: astore_3
    //   5: iconst_0
    //   6: istore 4
    //   8: iconst_0
    //   9: istore 5
    //   11: iconst_0
    //   12: istore 6
    //   14: iload 4
    //   16: aload_0
    //   17: getfield 295	java/util/ArrayList:size	I
    //   20: if_icmpge +34 -> 54
    //   23: aload_1
    //   24: aload_3
    //   25: iload 4
    //   27: aaload
    //   28: invokeinterface 346 2 0
    //   33: iload_2
    //   34: if_icmpne +14 -> 48
    //   37: aload_3
    //   38: iload 5
    //   40: iinc 5 1
    //   43: aload_3
    //   44: iload 4
    //   46: aaload
    //   47: aastore
    //   48: iinc 4 1
    //   51: goto -37 -> 14
    //   54: iload 4
    //   56: aload_0
    //   57: getfield 295	java/util/ArrayList:size	I
    //   60: if_icmpeq +31 -> 91
    //   63: aload_3
    //   64: iload 4
    //   66: aload_3
    //   67: iload 5
    //   69: aload_0
    //   70: getfield 295	java/util/ArrayList:size	I
    //   73: iload 4
    //   75: isub
    //   76: invokestatic 317	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   79: iload 5
    //   81: aload_0
    //   82: getfield 295	java/util/ArrayList:size	I
    //   85: iload 4
    //   87: isub
    //   88: iadd
    //   89: istore 5
    //   91: iload 5
    //   93: aload_0
    //   94: getfield 295	java/util/ArrayList:size	I
    //   97: if_icmpeq +155 -> 252
    //   100: iload 5
    //   102: istore 7
    //   104: iload 7
    //   106: aload_0
    //   107: getfield 295	java/util/ArrayList:size	I
    //   110: if_icmpge +14 -> 124
    //   113: aload_3
    //   114: iload 7
    //   116: aconst_null
    //   117: aastore
    //   118: iinc 7 1
    //   121: goto -17 -> 104
    //   124: aload_0
    //   125: dup
    //   126: getfield 294	java/util/ArrayList:modCount	I
    //   129: aload_0
    //   130: getfield 295	java/util/ArrayList:size	I
    //   133: iload 5
    //   135: isub
    //   136: iadd
    //   137: putfield 294	java/util/ArrayList:modCount	I
    //   140: aload_0
    //   141: iload 5
    //   143: putfield 295	java/util/ArrayList:size	I
    //   146: iconst_1
    //   147: istore 6
    //   149: goto +103 -> 252
    //   152: astore 8
    //   154: iload 4
    //   156: aload_0
    //   157: getfield 295	java/util/ArrayList:size	I
    //   160: if_icmpeq +31 -> 191
    //   163: aload_3
    //   164: iload 4
    //   166: aload_3
    //   167: iload 5
    //   169: aload_0
    //   170: getfield 295	java/util/ArrayList:size	I
    //   173: iload 4
    //   175: isub
    //   176: invokestatic 317	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   179: iload 5
    //   181: aload_0
    //   182: getfield 295	java/util/ArrayList:size	I
    //   185: iload 4
    //   187: isub
    //   188: iadd
    //   189: istore 5
    //   191: iload 5
    //   193: aload_0
    //   194: getfield 295	java/util/ArrayList:size	I
    //   197: if_icmpeq +52 -> 249
    //   200: iload 5
    //   202: istore 9
    //   204: iload 9
    //   206: aload_0
    //   207: getfield 295	java/util/ArrayList:size	I
    //   210: if_icmpge +14 -> 224
    //   213: aload_3
    //   214: iload 9
    //   216: aconst_null
    //   217: aastore
    //   218: iinc 9 1
    //   221: goto -17 -> 204
    //   224: aload_0
    //   225: dup
    //   226: getfield 294	java/util/ArrayList:modCount	I
    //   229: aload_0
    //   230: getfield 295	java/util/ArrayList:size	I
    //   233: iload 5
    //   235: isub
    //   236: iadd
    //   237: putfield 294	java/util/ArrayList:modCount	I
    //   240: aload_0
    //   241: iload 5
    //   243: putfield 295	java/util/ArrayList:size	I
    //   246: iconst_1
    //   247: istore 6
    //   249: aload 8
    //   251: athrow
    //   252: iload 6
    //   254: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	255	0	this	ArrayList
    //   0	255	1	paramCollection	Collection<?>
    //   0	255	2	paramBoolean	boolean
    //   4	210	3	arrayOfObject	Object[]
    //   6	182	4	i	int
    //   9	233	5	j	int
    //   12	241	6	bool	boolean
    //   102	17	7	k	int
    //   152	98	8	localObject	Object
    //   202	17	9	m	int
    // Exception table:
    //   from	to	target	type
    //   14	54	152	finally
    //   152	154	152	finally
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    int i = modCount;
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size);
    for (int j = 0; j < size; j++) {
      paramObjectOutputStream.writeObject(elementData[j]);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    elementData = EMPTY_ELEMENTDATA;
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readInt();
    if (size > 0)
    {
      int i = calculateCapacity(elementData, size);
      SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, i);
      ensureCapacityInternal(size);
      Object[] arrayOfObject = elementData;
      for (int j = 0; j < size; j++) {
        arrayOfObject[j] = paramObjectInputStream.readObject();
      }
    }
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > size)) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
    return new ListItr(paramInt);
  }
  
  public ListIterator<E> listIterator()
  {
    return new ListItr(0);
  }
  
  public Iterator<E> iterator()
  {
    return new Itr(null);
  }
  
  public List<E> subList(int paramInt1, int paramInt2)
  {
    subListRangeCheck(paramInt1, paramInt2, size);
    return new SubList(this, 0, paramInt1, paramInt2);
  }
  
  static void subListRangeCheck(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < 0) {
      throw new IndexOutOfBoundsException("fromIndex = " + paramInt1);
    }
    if (paramInt2 > paramInt3) {
      throw new IndexOutOfBoundsException("toIndex = " + paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("fromIndex(" + paramInt1 + ") > toIndex(" + paramInt2 + ")");
    }
  }
  
  public void forEach(Consumer<? super E> paramConsumer)
  {
    Objects.requireNonNull(paramConsumer);
    int i = modCount;
    Object[] arrayOfObject = (Object[])elementData;
    int j = size;
    for (int k = 0; (modCount == i) && (k < j); k++) {
      paramConsumer.accept(arrayOfObject[k]);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
  }
  
  public Spliterator<E> spliterator()
  {
    return new ArrayListSpliterator(this, 0, -1, 0);
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate)
  {
    Objects.requireNonNull(paramPredicate);
    int i = 0;
    BitSet localBitSet = new BitSet(size);
    int j = modCount;
    int k = size;
    for (int m = 0; (modCount == j) && (m < k); m++)
    {
      Object localObject = elementData[m];
      if (paramPredicate.test(localObject))
      {
        localBitSet.set(m);
        i++;
      }
    }
    if (modCount != j) {
      throw new ConcurrentModificationException();
    }
    m = i > 0 ? 1 : 0;
    if (m != 0)
    {
      int n = k - i;
      int i1 = 0;
      for (int i2 = 0; (i1 < k) && (i2 < n); i2++)
      {
        i1 = localBitSet.nextClearBit(i1);
        elementData[i2] = elementData[i1];
        i1++;
      }
      for (i1 = n; i1 < k; i1++) {
        elementData[i1] = null;
      }
      size = n;
      if (modCount != j) {
        throw new ConcurrentModificationException();
      }
      modCount += 1;
    }
    return m;
  }
  
  public void replaceAll(UnaryOperator<E> paramUnaryOperator)
  {
    Objects.requireNonNull(paramUnaryOperator);
    int i = modCount;
    int j = size;
    for (int k = 0; (modCount == i) && (k < j); k++) {
      elementData[k] = paramUnaryOperator.apply(elementData[k]);
    }
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
    modCount += 1;
  }
  
  public void sort(Comparator<? super E> paramComparator)
  {
    int i = modCount;
    Arrays.sort((Object[])elementData, 0, size, paramComparator);
    if (modCount != i) {
      throw new ConcurrentModificationException();
    }
    modCount += 1;
  }
  
  static final class ArrayListSpliterator<E>
    implements Spliterator<E>
  {
    private final ArrayList<E> list;
    private int index;
    private int fence;
    private int expectedModCount;
    
    ArrayListSpliterator(ArrayList<E> paramArrayList, int paramInt1, int paramInt2, int paramInt3)
    {
      list = paramArrayList;
      index = paramInt1;
      fence = paramInt2;
      expectedModCount = paramInt3;
    }
    
    private int getFence()
    {
      int i;
      if ((i = fence) < 0)
      {
        ArrayList localArrayList;
        if ((localArrayList = list) == null)
        {
          i = fence = 0;
        }
        else
        {
          expectedModCount = modCount;
          i = fence = size;
        }
      }
      return i;
    }
    
    public ArrayListSpliterator<E> trySplit()
    {
      int i = getFence();
      int j = index;
      int k = j + i >>> 1;
      return j >= k ? null : new ArrayListSpliterator(list, j, index = k, expectedModCount);
    }
    
    public boolean tryAdvance(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i = getFence();
      int j = index;
      if (j < i)
      {
        index = (j + 1);
        Object localObject = list.elementData[j];
        paramConsumer.accept(localObject);
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
      ArrayList localArrayList;
      Object[] arrayOfObject;
      if (((localArrayList = list) != null) && ((arrayOfObject = elementData) != null))
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
        if (((i = index) >= 0) && ((index = j) <= arrayOfObject.length))
        {
          while (i < j)
          {
            Object localObject = arrayOfObject[i];
            paramConsumer.accept(localObject);
            i++;
          }
          if (modCount == k) {
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
  
  private class Itr
    implements Iterator<E>
  {
    int cursor;
    int lastRet = -1;
    int expectedModCount = modCount;
    
    private Itr() {}
    
    public boolean hasNext()
    {
      return cursor != size;
    }
    
    public E next()
    {
      checkForComodification();
      int i = cursor;
      if (i >= size) {
        throw new NoSuchElementException();
      }
      Object[] arrayOfObject = elementData;
      if (i >= arrayOfObject.length) {
        throw new ConcurrentModificationException();
      }
      cursor = (i + 1);
      return (E)arrayOfObject[(lastRet = i)];
    }
    
    public void remove()
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();
      try
      {
        remove(lastRet);
        cursor = lastRet;
        lastRet = -1;
        expectedModCount = modCount;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new ConcurrentModificationException();
      }
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      int i = size;
      int j = cursor;
      if (j >= i) {
        return;
      }
      Object[] arrayOfObject = elementData;
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
    
    final void checkForComodification()
    {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private class ListItr
    extends ArrayList<E>.Itr
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
      checkForComodification();
      int i = cursor - 1;
      if (i < 0) {
        throw new NoSuchElementException();
      }
      Object[] arrayOfObject = elementData;
      if (i >= arrayOfObject.length) {
        throw new ConcurrentModificationException();
      }
      cursor = i;
      return (E)arrayOfObject[(lastRet = i)];
    }
    
    public void set(E paramE)
    {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();
      try
      {
        set(lastRet, paramE);
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new ConcurrentModificationException();
      }
    }
    
    public void add(E paramE)
    {
      checkForComodification();
      try
      {
        int i = cursor;
        add(i, paramE);
        cursor = (i + 1);
        lastRet = -1;
        expectedModCount = modCount;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  private class SubList
    extends AbstractList<E>
    implements RandomAccess
  {
    private final AbstractList<E> parent;
    private final int parentOffset;
    private final int offset;
    int size;
    
    SubList(int paramInt1, int paramInt2, int paramInt3)
    {
      parent = paramInt1;
      parentOffset = paramInt3;
      offset = (paramInt2 + paramInt3);
      int i;
      size = (i - paramInt3);
      modCount = modCount;
    }
    
    public E set(int paramInt, E paramE)
    {
      rangeCheck(paramInt);
      checkForComodification();
      Object localObject = elementData(offset + paramInt);
      elementData[(offset + paramInt)] = paramE;
      return (E)localObject;
    }
    
    public E get(int paramInt)
    {
      rangeCheck(paramInt);
      checkForComodification();
      return (E)elementData(offset + paramInt);
    }
    
    public int size()
    {
      checkForComodification();
      return size;
    }
    
    public void add(int paramInt, E paramE)
    {
      rangeCheckForAdd(paramInt);
      checkForComodification();
      parent.add(parentOffset + paramInt, paramE);
      modCount = parent.modCount;
      size += 1;
    }
    
    public E remove(int paramInt)
    {
      rangeCheck(paramInt);
      checkForComodification();
      Object localObject = parent.remove(parentOffset + paramInt);
      modCount = parent.modCount;
      size -= 1;
      return (E)localObject;
    }
    
    protected void removeRange(int paramInt1, int paramInt2)
    {
      checkForComodification();
      parent.removeRange(parentOffset + paramInt1, parentOffset + paramInt2);
      modCount = parent.modCount;
      size -= paramInt2 - paramInt1;
    }
    
    public boolean addAll(Collection<? extends E> paramCollection)
    {
      return addAll(size, paramCollection);
    }
    
    public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
    {
      rangeCheckForAdd(paramInt);
      int i = paramCollection.size();
      if (i == 0) {
        return false;
      }
      checkForComodification();
      parent.addAll(parentOffset + paramInt, paramCollection);
      modCount = parent.modCount;
      size += i;
      return true;
    }
    
    public Iterator<E> iterator()
    {
      return listIterator();
    }
    
    public ListIterator<E> listIterator(final int paramInt)
    {
      checkForComodification();
      rangeCheckForAdd(paramInt);
      final int i = offset;
      new ListIterator()
      {
        int cursor = paramInt;
        int lastRet = -1;
        int expectedModCount = modCount;
        
        public boolean hasNext()
        {
          return cursor != size;
        }
        
        public E next()
        {
          checkForComodification();
          int i = cursor;
          if (i >= size) {
            throw new NoSuchElementException();
          }
          Object[] arrayOfObject = elementData;
          if (i + i >= arrayOfObject.length) {
            throw new ConcurrentModificationException();
          }
          cursor = (i + 1);
          return (E)arrayOfObject[(i + (lastRet = i))];
        }
        
        public boolean hasPrevious()
        {
          return cursor != 0;
        }
        
        public E previous()
        {
          checkForComodification();
          int i = cursor - 1;
          if (i < 0) {
            throw new NoSuchElementException();
          }
          Object[] arrayOfObject = elementData;
          if (i + i >= arrayOfObject.length) {
            throw new ConcurrentModificationException();
          }
          cursor = i;
          return (E)arrayOfObject[(i + (lastRet = i))];
        }
        
        public void forEachRemaining(Consumer<? super E> paramAnonymousConsumer)
        {
          Objects.requireNonNull(paramAnonymousConsumer);
          int i = size;
          int j = cursor;
          if (j >= i) {
            return;
          }
          Object[] arrayOfObject = elementData;
          if (i + j >= arrayOfObject.length) {
            throw new ConcurrentModificationException();
          }
          while ((j != i) && (modCount == expectedModCount)) {
            paramAnonymousConsumer.accept(arrayOfObject[(i + j++)]);
          }
          lastRet = (cursor = j);
          checkForComodification();
        }
        
        public int nextIndex()
        {
          return cursor;
        }
        
        public int previousIndex()
        {
          return cursor - 1;
        }
        
        public void remove()
        {
          if (lastRet < 0) {
            throw new IllegalStateException();
          }
          checkForComodification();
          try
          {
            remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = modCount;
          }
          catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
          {
            throw new ConcurrentModificationException();
          }
        }
        
        public void set(E paramAnonymousE)
        {
          if (lastRet < 0) {
            throw new IllegalStateException();
          }
          checkForComodification();
          try
          {
            ArrayList.this.set(i + lastRet, paramAnonymousE);
          }
          catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
          {
            throw new ConcurrentModificationException();
          }
        }
        
        public void add(E paramAnonymousE)
        {
          checkForComodification();
          try
          {
            int i = cursor;
            add(i, paramAnonymousE);
            cursor = (i + 1);
            lastRet = -1;
            expectedModCount = modCount;
          }
          catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
          {
            throw new ConcurrentModificationException();
          }
        }
        
        final void checkForComodification()
        {
          if (expectedModCount != modCount) {
            throw new ConcurrentModificationException();
          }
        }
      };
    }
    
    public List<E> subList(int paramInt1, int paramInt2)
    {
      ArrayList.subListRangeCheck(paramInt1, paramInt2, size);
      return new SubList(ArrayList.this, this, offset, paramInt1, paramInt2);
    }
    
    private void rangeCheck(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= size)) {
        throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
      }
    }
    
    private void rangeCheckForAdd(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > size)) {
        throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt));
      }
    }
    
    private String outOfBoundsMsg(int paramInt)
    {
      return "Index: " + paramInt + ", Size: " + size;
    }
    
    private void checkForComodification()
    {
      if (modCount != modCount) {
        throw new ConcurrentModificationException();
      }
    }
    
    public Spliterator<E> spliterator()
    {
      checkForComodification();
      return new ArrayList.ArrayListSpliterator(ArrayList.this, offset, offset + size, modCount);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\ArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
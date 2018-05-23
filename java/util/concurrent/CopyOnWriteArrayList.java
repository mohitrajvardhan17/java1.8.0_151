package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.JavaOISAccess;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

public class CopyOnWriteArrayList<E>
  implements List<E>, RandomAccess, Cloneable, Serializable
{
  private static final long serialVersionUID = 8673264195747942595L;
  final transient ReentrantLock lock = new ReentrantLock();
  private volatile transient Object[] array;
  private static final Unsafe UNSAFE;
  private static final long lockOffset;
  
  final Object[] getArray()
  {
    return array;
  }
  
  final void setArray(Object[] paramArrayOfObject)
  {
    array = paramArrayOfObject;
  }
  
  public CopyOnWriteArrayList()
  {
    setArray(new Object[0]);
  }
  
  public CopyOnWriteArrayList(Collection<? extends E> paramCollection)
  {
    Object[] arrayOfObject;
    if (paramCollection.getClass() == CopyOnWriteArrayList.class)
    {
      arrayOfObject = ((CopyOnWriteArrayList)paramCollection).getArray();
    }
    else
    {
      arrayOfObject = paramCollection.toArray();
      if (arrayOfObject.getClass() != Object[].class) {
        arrayOfObject = Arrays.copyOf(arrayOfObject, arrayOfObject.length, Object[].class);
      }
    }
    setArray(arrayOfObject);
  }
  
  public CopyOnWriteArrayList(E[] paramArrayOfE)
  {
    setArray(Arrays.copyOf(paramArrayOfE, paramArrayOfE.length, Object[].class));
  }
  
  public int size()
  {
    return getArray().length;
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  private static boolean eq(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
  
  private static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt1, int paramInt2)
  {
    int i;
    if (paramObject == null) {
      for (i = paramInt1; i < paramInt2; i++) {
        if (paramArrayOfObject[i] == null) {
          return i;
        }
      }
    } else {
      for (i = paramInt1; i < paramInt2; i++) {
        if (paramObject.equals(paramArrayOfObject[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  private static int lastIndexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt)
  {
    int i;
    if (paramObject == null) {
      for (i = paramInt; i >= 0; i--) {
        if (paramArrayOfObject[i] == null) {
          return i;
        }
      }
    } else {
      for (i = paramInt; i >= 0; i--) {
        if (paramObject.equals(paramArrayOfObject[i])) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public boolean contains(Object paramObject)
  {
    Object[] arrayOfObject = getArray();
    return indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length) >= 0;
  }
  
  public int indexOf(Object paramObject)
  {
    Object[] arrayOfObject = getArray();
    return indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length);
  }
  
  public int indexOf(E paramE, int paramInt)
  {
    Object[] arrayOfObject = getArray();
    return indexOf(paramE, arrayOfObject, paramInt, arrayOfObject.length);
  }
  
  public int lastIndexOf(Object paramObject)
  {
    Object[] arrayOfObject = getArray();
    return lastIndexOf(paramObject, arrayOfObject, arrayOfObject.length - 1);
  }
  
  public int lastIndexOf(E paramE, int paramInt)
  {
    Object[] arrayOfObject = getArray();
    return lastIndexOf(paramE, arrayOfObject, paramInt);
  }
  
  public Object clone()
  {
    try
    {
      CopyOnWriteArrayList localCopyOnWriteArrayList = (CopyOnWriteArrayList)super.clone();
      localCopyOnWriteArrayList.resetLock();
      return localCopyOnWriteArrayList;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = getArray();
    return Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT)
  {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    if (paramArrayOfT.length < i) {
      return (Object[])Arrays.copyOf(arrayOfObject, i, paramArrayOfT.getClass());
    }
    System.arraycopy(arrayOfObject, 0, paramArrayOfT, 0, i);
    if (paramArrayOfT.length > i) {
      paramArrayOfT[i] = null;
    }
    return paramArrayOfT;
  }
  
  private E get(Object[] paramArrayOfObject, int paramInt)
  {
    return (E)paramArrayOfObject[paramInt];
  }
  
  public E get(int paramInt)
  {
    return (E)get(getArray(), paramInt);
  }
  
  public E set(int paramInt, E paramE)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      Object localObject1 = get(arrayOfObject1, paramInt);
      if (localObject1 != paramE)
      {
        int i = arrayOfObject1.length;
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i);
        arrayOfObject2[paramInt] = paramE;
        setArray(arrayOfObject2);
      }
      else
      {
        setArray(arrayOfObject1);
      }
      Object localObject2 = localObject1;
      return (E)localObject2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean add(E paramE)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      arrayOfObject2[i] = paramE;
      setArray(arrayOfObject2);
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void add(int paramInt, E paramE)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if ((paramInt > i) || (paramInt < 0)) {
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + i);
      }
      int j = i - paramInt;
      Object[] arrayOfObject2;
      if (j == 0)
      {
        arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      }
      else
      {
        arrayOfObject2 = new Object[i + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
        System.arraycopy(arrayOfObject1, paramInt, arrayOfObject2, paramInt + 1, j);
      }
      arrayOfObject2[paramInt] = paramE;
      setArray(arrayOfObject2);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public E remove(int paramInt)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      Object localObject1 = get(arrayOfObject, paramInt);
      int j = i - paramInt - 1;
      if (j == 0)
      {
        setArray(Arrays.copyOf(arrayOfObject, i - 1));
      }
      else
      {
        localObject2 = new Object[i - 1];
        System.arraycopy(arrayOfObject, 0, localObject2, 0, paramInt);
        System.arraycopy(arrayOfObject, paramInt + 1, localObject2, paramInt, j);
        setArray((Object[])localObject2);
      }
      Object localObject2 = localObject1;
      return (E)localObject2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean remove(Object paramObject)
  {
    Object[] arrayOfObject = getArray();
    int i = indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length);
    return i < 0 ? false : remove(paramObject, arrayOfObject, i);
  }
  
  private boolean remove(Object paramObject, Object[] paramArrayOfObject, int paramInt)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramArrayOfObject != arrayOfObject1)
      {
        int j = Math.min(paramInt, i);
        for (int k = 0; k < j; k++) {
          if ((arrayOfObject1[k] != paramArrayOfObject[k]) && (eq(paramObject, arrayOfObject1[k])))
          {
            paramInt = k;
            break label135;
          }
        }
        if (paramInt >= i)
        {
          k = 0;
          return k;
        }
        if (arrayOfObject1[paramInt] != paramObject)
        {
          paramInt = indexOf(paramObject, arrayOfObject1, paramInt, i);
          if (paramInt < 0)
          {
            bool = false;
            return bool;
          }
        }
      }
      label135:
      Object[] arrayOfObject2 = new Object[i - 1];
      System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
      System.arraycopy(arrayOfObject1, paramInt + 1, arrayOfObject2, paramInt, i - paramInt - 1);
      setArray(arrayOfObject2);
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  void removeRange(int paramInt1, int paramInt2)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if ((paramInt1 < 0) || (paramInt2 > i) || (paramInt2 < paramInt1)) {
        throw new IndexOutOfBoundsException();
      }
      int j = i - (paramInt2 - paramInt1);
      int k = i - paramInt2;
      if (k == 0)
      {
        setArray(Arrays.copyOf(arrayOfObject1, j));
      }
      else
      {
        Object[] arrayOfObject2 = new Object[j];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt1);
        System.arraycopy(arrayOfObject1, paramInt2, arrayOfObject2, paramInt1, k);
        setArray(arrayOfObject2);
      }
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean addIfAbsent(E paramE)
  {
    Object[] arrayOfObject = getArray();
    return indexOf(paramE, arrayOfObject, 0, arrayOfObject.length) >= 0 ? false : addIfAbsent(paramE, arrayOfObject);
  }
  
  private boolean addIfAbsent(E paramE, Object[] paramArrayOfObject)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramArrayOfObject != arrayOfObject1)
      {
        int j = Math.min(paramArrayOfObject.length, i);
        for (int k = 0; k < j; k++) {
          if ((arrayOfObject1[k] != paramArrayOfObject[k]) && (eq(paramE, arrayOfObject1[k])))
          {
            boolean bool2 = false;
            return bool2;
          }
        }
        if (indexOf(paramE, arrayOfObject1, j, i) >= 0)
        {
          k = 0;
          return k;
        }
      }
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      arrayOfObject2[i] = paramE;
      setArray(arrayOfObject2);
      boolean bool1 = true;
      return bool1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean containsAll(Collection<?> paramCollection)
  {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (indexOf(localObject, arrayOfObject, 0, i) < 0) {
        return false;
      }
    }
    return true;
  }
  
  public boolean removeAll(Collection<?> paramCollection)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (i != 0)
      {
        j = 0;
        Object[] arrayOfObject2 = new Object[i];
        for (int k = 0; k < i; k++)
        {
          Object localObject1 = arrayOfObject1[k];
          if (!paramCollection.contains(localObject1)) {
            arrayOfObject2[(j++)] = localObject1;
          }
        }
        if (j != i)
        {
          setArray(Arrays.copyOf(arrayOfObject2, j));
          k = 1;
          return k;
        }
      }
      int j = 0;
      return j;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean retainAll(Collection<?> paramCollection)
  {
    if (paramCollection == null) {
      throw new NullPointerException();
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (i != 0)
      {
        j = 0;
        Object[] arrayOfObject2 = new Object[i];
        for (int k = 0; k < i; k++)
        {
          Object localObject1 = arrayOfObject1[k];
          if (paramCollection.contains(localObject1)) {
            arrayOfObject2[(j++)] = localObject1;
          }
        }
        if (j != i)
        {
          setArray(Arrays.copyOf(arrayOfObject2, j));
          k = 1;
          return k;
        }
      }
      int j = 0;
      return j;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public int addAllAbsent(Collection<? extends E> paramCollection)
  {
    Object[] arrayOfObject1 = paramCollection.toArray();
    if (arrayOfObject1.length == 0) {
      return 0;
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject2 = getArray();
      int i = arrayOfObject2.length;
      Object[] arrayOfObject3 = 0;
      for (int j = 0; j < arrayOfObject1.length; j++)
      {
        Object localObject1 = arrayOfObject1[j];
        if ((indexOf(localObject1, arrayOfObject2, 0, i) < 0) && (indexOf(localObject1, arrayOfObject1, 0, arrayOfObject3) < 0)) {
          arrayOfObject1[(arrayOfObject3++)] = localObject1;
        }
      }
      if (arrayOfObject3 > 0)
      {
        arrayOfObject4 = Arrays.copyOf(arrayOfObject2, i + arrayOfObject3);
        System.arraycopy(arrayOfObject1, 0, arrayOfObject4, i, arrayOfObject3);
        setArray(arrayOfObject4);
      }
      Object[] arrayOfObject4 = arrayOfObject3;
      return arrayOfObject4;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  /* Error */
  public void clear()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 297	java/util/concurrent/CopyOnWriteArrayList:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_1
    //   5: aload_1
    //   6: invokevirtual 340	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: iconst_0
    //   11: anewarray 153	java/lang/Object
    //   14: invokevirtual 330	java/util/concurrent/CopyOnWriteArrayList:setArray	([Ljava/lang/Object;)V
    //   17: aload_1
    //   18: invokevirtual 341	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   21: goto +10 -> 31
    //   24: astore_2
    //   25: aload_1
    //   26: invokevirtual 341	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   29: aload_2
    //   30: athrow
    //   31: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	CopyOnWriteArrayList
    //   4	22	1	localReentrantLock	ReentrantLock
    //   24	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	17	24	finally
  }
  
  public boolean addAll(Collection<? extends E> paramCollection)
  {
    Object[] arrayOfObject1 = paramCollection.getClass() == CopyOnWriteArrayList.class ? ((CopyOnWriteArrayList)paramCollection).getArray() : paramCollection.toArray();
    if (arrayOfObject1.length == 0) {
      return false;
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject2 = getArray();
      int i = arrayOfObject2.length;
      if ((i == 0) && (arrayOfObject1.getClass() == Object[].class))
      {
        setArray(arrayOfObject1);
      }
      else
      {
        Object[] arrayOfObject3 = Arrays.copyOf(arrayOfObject2, i + arrayOfObject1.length);
        System.arraycopy(arrayOfObject1, 0, arrayOfObject3, i, arrayOfObject1.length);
        setArray(arrayOfObject3);
      }
      boolean bool = true;
      return bool;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    Object[] arrayOfObject1 = paramCollection.toArray();
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject2 = getArray();
      int i = arrayOfObject2.length;
      if ((paramInt > i) || (paramInt < 0)) {
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + i);
      }
      if (arrayOfObject1.length == 0)
      {
        boolean bool1 = false;
        return bool1;
      }
      int j = i - paramInt;
      Object[] arrayOfObject3;
      if (j == 0)
      {
        arrayOfObject3 = Arrays.copyOf(arrayOfObject2, i + arrayOfObject1.length);
      }
      else
      {
        arrayOfObject3 = new Object[i + arrayOfObject1.length];
        System.arraycopy(arrayOfObject2, 0, arrayOfObject3, 0, paramInt);
        System.arraycopy(arrayOfObject2, paramInt, arrayOfObject3, paramInt + arrayOfObject1.length, j);
      }
      System.arraycopy(arrayOfObject1, 0, arrayOfObject3, paramInt, arrayOfObject1.length);
      setArray(arrayOfObject3);
      boolean bool2 = true;
      return bool2;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void forEach(Consumer<? super E> paramConsumer)
  {
    if (paramConsumer == null) {
      throw new NullPointerException();
    }
    for (Object localObject : getArray()) {
      paramConsumer.accept(localObject);
    }
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate)
  {
    if (paramPredicate == null) {
      throw new NullPointerException();
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (i != 0)
      {
        j = 0;
        Object[] arrayOfObject2 = new Object[i];
        for (int k = 0; k < i; k++)
        {
          Object localObject1 = arrayOfObject1[k];
          if (!paramPredicate.test(localObject1)) {
            arrayOfObject2[(j++)] = localObject1;
          }
        }
        if (j != i)
        {
          setArray(Arrays.copyOf(arrayOfObject2, j));
          k = 1;
          return k;
        }
      }
      int j = 0;
      return j;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void replaceAll(UnaryOperator<E> paramUnaryOperator)
  {
    if (paramUnaryOperator == null) {
      throw new NullPointerException();
    }
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i);
      for (int j = 0; j < i; j++)
      {
        Object localObject1 = arrayOfObject1[j];
        arrayOfObject2[j] = paramUnaryOperator.apply(localObject1);
      }
      setArray(arrayOfObject2);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public void sort(Comparator<? super E> paramComparator)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject1 = getArray();
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, arrayOfObject1.length);
      Object[] arrayOfObject3 = (Object[])arrayOfObject2;
      Arrays.sort(arrayOfObject3, paramComparator);
      setArray(arrayOfObject2);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Object[] arrayOfObject1 = getArray();
    paramObjectOutputStream.writeInt(arrayOfObject1.length);
    for (Object localObject : arrayOfObject1) {
      paramObjectOutputStream.writeObject(localObject);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    resetLock();
    int i = paramObjectInputStream.readInt();
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, i);
    Object[] arrayOfObject = new Object[i];
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = paramObjectInputStream.readObject();
    }
    setArray(arrayOfObject);
  }
  
  public String toString()
  {
    return Arrays.toString(getArray());
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof List)) {
      return false;
    }
    List localList = (List)paramObject;
    Iterator localIterator = localList.iterator();
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    for (int j = 0; j < i; j++) {
      if ((!localIterator.hasNext()) || (!eq(arrayOfObject[j], localIterator.next()))) {
        return false;
      }
    }
    return !localIterator.hasNext();
  }
  
  public int hashCode()
  {
    int i = 1;
    for (Object localObject : getArray()) {
      i = 31 * i + (localObject == null ? 0 : localObject.hashCode());
    }
    return i;
  }
  
  public Iterator<E> iterator()
  {
    return new COWIterator(getArray(), 0, null);
  }
  
  public ListIterator<E> listIterator()
  {
    return new COWIterator(getArray(), 0, null);
  }
  
  public ListIterator<E> listIterator(int paramInt)
  {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    if ((paramInt < 0) || (paramInt > i)) {
      throw new IndexOutOfBoundsException("Index: " + paramInt);
    }
    return new COWIterator(arrayOfObject, paramInt, null);
  }
  
  public Spliterator<E> spliterator()
  {
    return Spliterators.spliterator(getArray(), 1040);
  }
  
  public List<E> subList(int paramInt1, int paramInt2)
  {
    ReentrantLock localReentrantLock = lock;
    localReentrantLock.lock();
    try
    {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if ((paramInt1 < 0) || (paramInt2 > i) || (paramInt1 > paramInt2)) {
        throw new IndexOutOfBoundsException();
      }
      COWSubList localCOWSubList = new COWSubList(this, paramInt1, paramInt2);
      return localCOWSubList;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  private void resetLock()
  {
    UNSAFE.putObjectVolatile(this, lockOffset, new ReentrantLock());
  }
  
  static
  {
    try
    {
      UNSAFE = Unsafe.getUnsafe();
      Class localClass = CopyOnWriteArrayList.class;
      lockOffset = UNSAFE.objectFieldOffset(localClass.getDeclaredField("lock"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  static final class COWIterator<E>
    implements ListIterator<E>
  {
    private final Object[] snapshot;
    private int cursor;
    
    private COWIterator(Object[] paramArrayOfObject, int paramInt)
    {
      cursor = paramInt;
      snapshot = paramArrayOfObject;
    }
    
    public boolean hasNext()
    {
      return cursor < snapshot.length;
    }
    
    public boolean hasPrevious()
    {
      return cursor > 0;
    }
    
    public E next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return (E)snapshot[(cursor++)];
    }
    
    public E previous()
    {
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      return (E)snapshot[(--cursor)];
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
      throw new UnsupportedOperationException();
    }
    
    public void set(E paramE)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(E paramE)
    {
      throw new UnsupportedOperationException();
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      Object[] arrayOfObject = snapshot;
      int i = arrayOfObject.length;
      for (int j = cursor; j < i; j++)
      {
        Object localObject = arrayOfObject[j];
        paramConsumer.accept(localObject);
      }
      cursor = i;
    }
  }
  
  private static class COWSubList<E>
    extends AbstractList<E>
    implements RandomAccess
  {
    private final CopyOnWriteArrayList<E> l;
    private final int offset;
    private int size;
    private Object[] expectedArray;
    
    COWSubList(CopyOnWriteArrayList<E> paramCopyOnWriteArrayList, int paramInt1, int paramInt2)
    {
      l = paramCopyOnWriteArrayList;
      expectedArray = l.getArray();
      offset = paramInt1;
      size = (paramInt2 - paramInt1);
    }
    
    private void checkForComodification()
    {
      if (l.getArray() != expectedArray) {
        throw new ConcurrentModificationException();
      }
    }
    
    private void rangeCheck(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= size)) {
        throw new IndexOutOfBoundsException("Index: " + paramInt + ",Size: " + size);
      }
    }
    
    public E set(int paramInt, E paramE)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        rangeCheck(paramInt);
        checkForComodification();
        Object localObject1 = l.set(paramInt + offset, paramE);
        expectedArray = l.getArray();
        Object localObject2 = localObject1;
        return (E)localObject2;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public E get(int paramInt)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        rangeCheck(paramInt);
        checkForComodification();
        Object localObject1 = l.get(paramInt + offset);
        return (E)localObject1;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public int size()
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        checkForComodification();
        int i = size;
        return i;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public void add(int paramInt, E paramE)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        checkForComodification();
        if ((paramInt < 0) || (paramInt > size)) {
          throw new IndexOutOfBoundsException();
        }
        l.add(paramInt + offset, paramE);
        expectedArray = l.getArray();
        size += 1;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    /* Error */
    public void clear()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 175	java/util/concurrent/CopyOnWriteArrayList$COWSubList:l	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   4: getfield 171	java/util/concurrent/CopyOnWriteArrayList:lock	Ljava/util/concurrent/locks/ReentrantLock;
      //   7: astore_1
      //   8: aload_1
      //   9: invokevirtual 202	java/util/concurrent/locks/ReentrantLock:lock	()V
      //   12: aload_0
      //   13: invokespecial 196	java/util/concurrent/CopyOnWriteArrayList$COWSubList:checkForComodification	()V
      //   16: aload_0
      //   17: getfield 175	java/util/concurrent/CopyOnWriteArrayList$COWSubList:l	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   20: aload_0
      //   21: getfield 172	java/util/concurrent/CopyOnWriteArrayList$COWSubList:offset	I
      //   24: aload_0
      //   25: getfield 172	java/util/concurrent/CopyOnWriteArrayList$COWSubList:offset	I
      //   28: aload_0
      //   29: getfield 173	java/util/concurrent/CopyOnWriteArrayList$COWSubList:size	I
      //   32: iadd
      //   33: invokevirtual 189	java/util/concurrent/CopyOnWriteArrayList:removeRange	(II)V
      //   36: aload_0
      //   37: aload_0
      //   38: getfield 175	java/util/concurrent/CopyOnWriteArrayList$COWSubList:l	Ljava/util/concurrent/CopyOnWriteArrayList;
      //   41: invokevirtual 190	java/util/concurrent/CopyOnWriteArrayList:getArray	()[Ljava/lang/Object;
      //   44: putfield 174	java/util/concurrent/CopyOnWriteArrayList$COWSubList:expectedArray	[Ljava/lang/Object;
      //   47: aload_0
      //   48: iconst_0
      //   49: putfield 173	java/util/concurrent/CopyOnWriteArrayList$COWSubList:size	I
      //   52: aload_1
      //   53: invokevirtual 203	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   56: goto +10 -> 66
      //   59: astore_2
      //   60: aload_1
      //   61: invokevirtual 203	java/util/concurrent/locks/ReentrantLock:unlock	()V
      //   64: aload_2
      //   65: athrow
      //   66: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	67	0	this	COWSubList
      //   7	54	1	localReentrantLock	ReentrantLock
      //   59	6	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   12	52	59	finally
    }
    
    public E remove(int paramInt)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        rangeCheck(paramInt);
        checkForComodification();
        Object localObject1 = l.remove(paramInt + offset);
        expectedArray = l.getArray();
        size -= 1;
        Object localObject2 = localObject1;
        return (E)localObject2;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public boolean remove(Object paramObject)
    {
      int i = indexOf(paramObject);
      if (i == -1) {
        return false;
      }
      remove(i);
      return true;
    }
    
    public Iterator<E> iterator()
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        checkForComodification();
        CopyOnWriteArrayList.COWSubListIterator localCOWSubListIterator = new CopyOnWriteArrayList.COWSubListIterator(l, 0, offset, size);
        return localCOWSubListIterator;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public ListIterator<E> listIterator(int paramInt)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        checkForComodification();
        if ((paramInt < 0) || (paramInt > size)) {
          throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + size);
        }
        CopyOnWriteArrayList.COWSubListIterator localCOWSubListIterator = new CopyOnWriteArrayList.COWSubListIterator(l, paramInt, offset, size);
        return localCOWSubListIterator;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public List<E> subList(int paramInt1, int paramInt2)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        checkForComodification();
        if ((paramInt1 < 0) || (paramInt2 > size) || (paramInt1 > paramInt2)) {
          throw new IndexOutOfBoundsException();
        }
        COWSubList localCOWSubList = new COWSubList(l, paramInt1 + offset, paramInt2 + offset);
        return localCOWSubList;
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public void forEach(Consumer<? super E> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      int i = offset;
      int j = offset + size;
      Object[] arrayOfObject = expectedArray;
      if (l.getArray() != arrayOfObject) {
        throw new ConcurrentModificationException();
      }
      if ((i < 0) || (j > arrayOfObject.length)) {
        throw new IndexOutOfBoundsException();
      }
      for (int k = i; k < j; k++)
      {
        Object localObject = arrayOfObject[k];
        paramConsumer.accept(localObject);
      }
    }
    
    public void replaceAll(UnaryOperator<E> paramUnaryOperator)
    {
      if (paramUnaryOperator == null) {
        throw new NullPointerException();
      }
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        int i = offset;
        int j = offset + size;
        Object[] arrayOfObject1 = expectedArray;
        if (l.getArray() != arrayOfObject1) {
          throw new ConcurrentModificationException();
        }
        int k = arrayOfObject1.length;
        if ((i < 0) || (j > k)) {
          throw new IndexOutOfBoundsException();
        }
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, k);
        for (int m = i; m < j; m++)
        {
          Object localObject1 = arrayOfObject1[m];
          arrayOfObject2[m] = paramUnaryOperator.apply(localObject1);
        }
        l.setArray(expectedArray = arrayOfObject2);
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public void sort(Comparator<? super E> paramComparator)
    {
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        int i = offset;
        int j = offset + size;
        Object[] arrayOfObject1 = expectedArray;
        if (l.getArray() != arrayOfObject1) {
          throw new ConcurrentModificationException();
        }
        int k = arrayOfObject1.length;
        if ((i < 0) || (j > k)) {
          throw new IndexOutOfBoundsException();
        }
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, k);
        Object[] arrayOfObject3 = (Object[])arrayOfObject2;
        Arrays.sort(arrayOfObject3, i, j, paramComparator);
        l.setArray(expectedArray = arrayOfObject2);
      }
      finally
      {
        localReentrantLock.unlock();
      }
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      if (paramCollection == null) {
        throw new NullPointerException();
      }
      boolean bool = false;
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        int i = size;
        if (i > 0)
        {
          int j = offset;
          int k = offset + i;
          Object[] arrayOfObject1 = expectedArray;
          if (l.getArray() != arrayOfObject1) {
            throw new ConcurrentModificationException();
          }
          int m = arrayOfObject1.length;
          if ((j < 0) || (k > m)) {
            throw new IndexOutOfBoundsException();
          }
          int n = 0;
          Object[] arrayOfObject2 = new Object[i];
          for (int i1 = j; i1 < k; i1++)
          {
            Object localObject1 = arrayOfObject1[i1];
            if (!paramCollection.contains(localObject1)) {
              arrayOfObject2[(n++)] = localObject1;
            }
          }
          if (n != i)
          {
            Object[] arrayOfObject3 = new Object[m - i + n];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject3, 0, j);
            System.arraycopy(arrayOfObject2, 0, arrayOfObject3, j, n);
            System.arraycopy(arrayOfObject1, k, arrayOfObject3, j + n, m - k);
            size = n;
            bool = true;
            l.setArray(expectedArray = arrayOfObject3);
          }
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
      return bool;
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      if (paramCollection == null) {
        throw new NullPointerException();
      }
      boolean bool = false;
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        int i = size;
        if (i > 0)
        {
          int j = offset;
          int k = offset + i;
          Object[] arrayOfObject1 = expectedArray;
          if (l.getArray() != arrayOfObject1) {
            throw new ConcurrentModificationException();
          }
          int m = arrayOfObject1.length;
          if ((j < 0) || (k > m)) {
            throw new IndexOutOfBoundsException();
          }
          int n = 0;
          Object[] arrayOfObject2 = new Object[i];
          for (int i1 = j; i1 < k; i1++)
          {
            Object localObject1 = arrayOfObject1[i1];
            if (paramCollection.contains(localObject1)) {
              arrayOfObject2[(n++)] = localObject1;
            }
          }
          if (n != i)
          {
            Object[] arrayOfObject3 = new Object[m - i + n];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject3, 0, j);
            System.arraycopy(arrayOfObject2, 0, arrayOfObject3, j, n);
            System.arraycopy(arrayOfObject1, k, arrayOfObject3, j + n, m - k);
            size = n;
            bool = true;
            l.setArray(expectedArray = arrayOfObject3);
          }
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
      return bool;
    }
    
    public boolean removeIf(Predicate<? super E> paramPredicate)
    {
      if (paramPredicate == null) {
        throw new NullPointerException();
      }
      boolean bool = false;
      ReentrantLock localReentrantLock = l.lock;
      localReentrantLock.lock();
      try
      {
        int i = size;
        if (i > 0)
        {
          int j = offset;
          int k = offset + i;
          Object[] arrayOfObject1 = expectedArray;
          if (l.getArray() != arrayOfObject1) {
            throw new ConcurrentModificationException();
          }
          int m = arrayOfObject1.length;
          if ((j < 0) || (k > m)) {
            throw new IndexOutOfBoundsException();
          }
          int n = 0;
          Object[] arrayOfObject2 = new Object[i];
          for (int i1 = j; i1 < k; i1++)
          {
            Object localObject1 = arrayOfObject1[i1];
            if (!paramPredicate.test(localObject1)) {
              arrayOfObject2[(n++)] = localObject1;
            }
          }
          if (n != i)
          {
            Object[] arrayOfObject3 = new Object[m - i + n];
            System.arraycopy(arrayOfObject1, 0, arrayOfObject3, 0, j);
            System.arraycopy(arrayOfObject2, 0, arrayOfObject3, j, n);
            System.arraycopy(arrayOfObject1, k, arrayOfObject3, j + n, m - k);
            size = n;
            bool = true;
            l.setArray(expectedArray = arrayOfObject3);
          }
        }
      }
      finally
      {
        localReentrantLock.unlock();
      }
      return bool;
    }
    
    public Spliterator<E> spliterator()
    {
      int i = offset;
      int j = offset + size;
      Object[] arrayOfObject = expectedArray;
      if (l.getArray() != arrayOfObject) {
        throw new ConcurrentModificationException();
      }
      if ((i < 0) || (j > arrayOfObject.length)) {
        throw new IndexOutOfBoundsException();
      }
      return Spliterators.spliterator(arrayOfObject, i, j, 1040);
    }
  }
  
  private static class COWSubListIterator<E>
    implements ListIterator<E>
  {
    private final ListIterator<E> it;
    private final int offset;
    private final int size;
    
    COWSubListIterator(List<E> paramList, int paramInt1, int paramInt2, int paramInt3)
    {
      offset = paramInt2;
      size = paramInt3;
      it = paramList.listIterator(paramInt1 + paramInt2);
    }
    
    public boolean hasNext()
    {
      return nextIndex() < size;
    }
    
    public E next()
    {
      if (hasNext()) {
        return (E)it.next();
      }
      throw new NoSuchElementException();
    }
    
    public boolean hasPrevious()
    {
      return previousIndex() >= 0;
    }
    
    public E previous()
    {
      if (hasPrevious()) {
        return (E)it.previous();
      }
      throw new NoSuchElementException();
    }
    
    public int nextIndex()
    {
      return it.nextIndex() - offset;
    }
    
    public int previousIndex()
    {
      return it.previousIndex() - offset;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    public void set(E paramE)
    {
      throw new UnsupportedOperationException();
    }
    
    public void add(E paramE)
    {
      throw new UnsupportedOperationException();
    }
    
    public void forEachRemaining(Consumer<? super E> paramConsumer)
    {
      Objects.requireNonNull(paramConsumer);
      int i = size;
      ListIterator localListIterator = it;
      while (nextIndex() < i) {
        paramConsumer.accept(localListIterator.next());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\concurrent\CopyOnWriteArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
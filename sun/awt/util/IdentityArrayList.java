package sun.awt.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public class IdentityArrayList<E>
  extends AbstractList<E>
  implements List<E>, RandomAccess
{
  private transient Object[] elementData;
  private int size;
  
  public IdentityArrayList(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
    elementData = new Object[paramInt];
  }
  
  public IdentityArrayList()
  {
    this(10);
  }
  
  public IdentityArrayList(Collection<? extends E> paramCollection)
  {
    elementData = paramCollection.toArray();
    size = elementData.length;
    if (elementData.getClass() != Object[].class) {
      elementData = Arrays.copyOf(elementData, size, Object[].class);
    }
  }
  
  public void trimToSize()
  {
    modCount += 1;
    int i = elementData.length;
    if (size < i) {
      elementData = Arrays.copyOf(elementData, size);
    }
  }
  
  public void ensureCapacity(int paramInt)
  {
    modCount += 1;
    int i = elementData.length;
    if (paramInt > i)
    {
      Object[] arrayOfObject = elementData;
      int j = i * 3 / 2 + 1;
      if (j < paramInt) {
        j = paramInt;
      }
      elementData = Arrays.copyOf(elementData, j);
    }
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
    for (int i = 0; i < size; i++) {
      if (paramObject == elementData[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public int lastIndexOf(Object paramObject)
  {
    for (int i = size - 1; i >= 0; i--) {
      if (paramObject == elementData[i]) {
        return i;
      }
    }
    return -1;
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
  
  public E get(int paramInt)
  {
    rangeCheck(paramInt);
    return (E)elementData[paramInt];
  }
  
  public E set(int paramInt, E paramE)
  {
    rangeCheck(paramInt);
    Object localObject = elementData[paramInt];
    elementData[paramInt] = paramE;
    return (E)localObject;
  }
  
  public boolean add(E paramE)
  {
    ensureCapacity(size + 1);
    elementData[(size++)] = paramE;
    return true;
  }
  
  public void add(int paramInt, E paramE)
  {
    rangeCheckForAdd(paramInt);
    ensureCapacity(size + 1);
    System.arraycopy(elementData, paramInt, elementData, paramInt + 1, size - paramInt);
    elementData[paramInt] = paramE;
    size += 1;
  }
  
  public E remove(int paramInt)
  {
    rangeCheck(paramInt);
    modCount += 1;
    Object localObject = elementData[paramInt];
    int i = size - paramInt - 1;
    if (i > 0) {
      System.arraycopy(elementData, paramInt + 1, elementData, paramInt, i);
    }
    elementData[(--size)] = null;
    return (E)localObject;
  }
  
  public boolean remove(Object paramObject)
  {
    for (int i = 0; i < size; i++) {
      if (paramObject == elementData[i])
      {
        fastRemove(i);
        return true;
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
    ensureCapacity(size + i);
    System.arraycopy(arrayOfObject, 0, elementData, size, i);
    size += i;
    return i != 0;
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection)
  {
    rangeCheckForAdd(paramInt);
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    ensureCapacity(size + i);
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
    while (size != j) {
      elementData[(--size)] = null;
    }
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\util\IdentityArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
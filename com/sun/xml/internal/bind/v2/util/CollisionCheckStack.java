package com.sun.xml.internal.bind.v2.util;

import java.util.AbstractList;
import java.util.Arrays;

public final class CollisionCheckStack<E>
  extends AbstractList<E>
{
  private Object[] data = new Object[16];
  private int[] next = new int[16];
  private int size = 0;
  private boolean latestPushResult = false;
  private boolean useIdentity = true;
  private final int[] initialHash = new int[17];
  
  public CollisionCheckStack() {}
  
  public void setUseIdentity(boolean paramBoolean)
  {
    useIdentity = paramBoolean;
  }
  
  public boolean getUseIdentity()
  {
    return useIdentity;
  }
  
  public boolean getLatestPushResult()
  {
    return latestPushResult;
  }
  
  public boolean push(E paramE)
  {
    if (data.length == size) {
      expandCapacity();
    }
    data[size] = paramE;
    int i = hash(paramE);
    boolean bool = findDuplicate(paramE, i);
    next[size] = initialHash[i];
    initialHash[i] = (size + 1);
    size += 1;
    latestPushResult = bool;
    return latestPushResult;
  }
  
  public void pushNocheck(E paramE)
  {
    if (data.length == size) {
      expandCapacity();
    }
    data[size] = paramE;
    next[size] = -1;
    size += 1;
  }
  
  public boolean findDuplicate(E paramE)
  {
    int i = hash(paramE);
    return findDuplicate(paramE, i);
  }
  
  public E get(int paramInt)
  {
    return (E)data[paramInt];
  }
  
  public int size()
  {
    return size;
  }
  
  private int hash(Object paramObject)
  {
    return ((useIdentity ? System.identityHashCode(paramObject) : paramObject.hashCode()) & 0x7FFFFFFF) % initialHash.length;
  }
  
  public E pop()
  {
    size -= 1;
    Object localObject = data[size];
    data[size] = null;
    int i = next[size];
    if (i >= 0)
    {
      int j = hash(localObject);
      assert (initialHash[j] == size + 1);
      initialHash[j] = i;
    }
    return (E)localObject;
  }
  
  public E peek()
  {
    return (E)data[(size - 1)];
  }
  
  private boolean findDuplicate(E paramE, int paramInt)
  {
    for (int i = initialHash[paramInt]; i != 0; i = next[i])
    {
      i--;
      Object localObject = data[i];
      if (useIdentity)
      {
        if (localObject == paramE) {
          return true;
        }
      }
      else if (paramE.equals(localObject)) {
        return true;
      }
    }
    return false;
  }
  
  private void expandCapacity()
  {
    int i = data.length;
    int j = i * 2;
    Object[] arrayOfObject = new Object[j];
    int[] arrayOfInt = new int[j];
    System.arraycopy(data, 0, arrayOfObject, 0, i);
    System.arraycopy(next, 0, arrayOfInt, 0, i);
    data = arrayOfObject;
    next = arrayOfInt;
  }
  
  public void reset()
  {
    if (size > 0)
    {
      size = 0;
      Arrays.fill(initialHash, 0);
    }
  }
  
  public String getCycleString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = size() - 1;
    Object localObject1 = get(i);
    localStringBuilder.append(localObject1);
    Object localObject2;
    do
    {
      localStringBuilder.append(" -> ");
      localObject2 = get(--i);
      localStringBuilder.append(localObject2);
    } while (localObject1 != localObject2);
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\CollisionCheckStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
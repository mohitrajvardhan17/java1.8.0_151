package com.sun.jmx.remote.internal;

import java.util.AbstractList;

public class ArrayQueue<T>
  extends AbstractList<T>
{
  private int capacity;
  private T[] queue;
  private int head;
  private int tail;
  
  public ArrayQueue(int paramInt)
  {
    capacity = (paramInt + 1);
    queue = newArray(paramInt + 1);
    head = 0;
    tail = 0;
  }
  
  public void resize(int paramInt)
  {
    int i = size();
    if (paramInt < i) {
      throw new IndexOutOfBoundsException("Resizing would lose data");
    }
    paramInt++;
    if (paramInt == capacity) {
      return;
    }
    Object[] arrayOfObject = newArray(paramInt);
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = get(j);
    }
    capacity = paramInt;
    queue = arrayOfObject;
    head = 0;
    tail = i;
  }
  
  private T[] newArray(int paramInt)
  {
    return (Object[])new Object[paramInt];
  }
  
  public boolean add(T paramT)
  {
    queue[tail] = paramT;
    int i = (tail + 1) % capacity;
    if (i == head) {
      throw new IndexOutOfBoundsException("Queue full");
    }
    tail = i;
    return true;
  }
  
  public T remove(int paramInt)
  {
    if (paramInt != 0) {
      throw new IllegalArgumentException("Can only remove head of queue");
    }
    if (head == tail) {
      throw new IndexOutOfBoundsException("Queue empty");
    }
    Object localObject = queue[head];
    queue[head] = null;
    head = ((head + 1) % capacity);
    return (T)localObject;
  }
  
  public T get(int paramInt)
  {
    int i = size();
    if ((paramInt < 0) || (paramInt >= i))
    {
      String str = "Index " + paramInt + ", queue size " + i;
      throw new IndexOutOfBoundsException(str);
    }
    int j = (head + paramInt) % capacity;
    return (T)queue[j];
  }
  
  public int size()
  {
    int i = tail - head;
    if (i < 0) {
      i += capacity;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\ArrayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
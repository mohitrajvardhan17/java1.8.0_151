package com.sun.jmx.snmp.agent;

final class LongList
{
  public static int DEFAULT_CAPACITY = 10;
  public static int DEFAULT_INCREMENT = 10;
  private final int DELTA;
  private int size = 0;
  public long[] list;
  
  LongList()
  {
    this(DEFAULT_CAPACITY, DEFAULT_INCREMENT);
  }
  
  LongList(int paramInt)
  {
    this(paramInt, DEFAULT_INCREMENT);
  }
  
  LongList(int paramInt1, int paramInt2)
  {
    DELTA = paramInt2;
    list = allocate(paramInt1);
  }
  
  public final int size()
  {
    return size;
  }
  
  public final boolean add(long paramLong)
  {
    if (size >= list.length) {
      resize();
    }
    list[(size++)] = paramLong;
    return true;
  }
  
  public final void add(int paramInt, long paramLong)
  {
    if (paramInt > size) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt >= list.length) {
      resize();
    }
    if (paramInt == size)
    {
      list[(size++)] = paramLong;
      return;
    }
    System.arraycopy(list, paramInt, list, paramInt + 1, size - paramInt);
    list[paramInt] = paramLong;
    size += 1;
  }
  
  public final void add(int paramInt1, long[] paramArrayOfLong, int paramInt2, int paramInt3)
  {
    if (paramInt3 <= 0) {
      return;
    }
    if (paramInt1 > size) {
      throw new IndexOutOfBoundsException();
    }
    ensure(size + paramInt3);
    if (paramInt1 < size) {
      System.arraycopy(list, paramInt1, list, paramInt1 + paramInt3, size - paramInt1);
    }
    System.arraycopy(paramArrayOfLong, paramInt2, list, paramInt1, paramInt3);
    size += paramInt3;
  }
  
  public final long remove(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 1) || (paramInt1 < 0)) {
      return -1L;
    }
    if (paramInt1 + paramInt2 > size) {
      return -1L;
    }
    long l = list[paramInt1];
    int i = size;
    size -= paramInt2;
    if (paramInt1 == size) {
      return l;
    }
    System.arraycopy(list, paramInt1 + paramInt2, list, paramInt1, size - paramInt1);
    return l;
  }
  
  public final long remove(int paramInt)
  {
    if (paramInt >= size) {
      return -1L;
    }
    long l = list[paramInt];
    list[paramInt] = 0L;
    if (paramInt == --size) {
      return l;
    }
    System.arraycopy(list, paramInt + 1, list, paramInt, size - paramInt);
    return l;
  }
  
  public final long[] toArray(long[] paramArrayOfLong)
  {
    System.arraycopy(list, 0, paramArrayOfLong, 0, size);
    return paramArrayOfLong;
  }
  
  public final long[] toArray()
  {
    return toArray(new long[size]);
  }
  
  private final void resize()
  {
    long[] arrayOfLong = allocate(list.length + DELTA);
    System.arraycopy(list, 0, arrayOfLong, 0, size);
    list = arrayOfLong;
  }
  
  private final void ensure(int paramInt)
  {
    if (list.length < paramInt)
    {
      int i = list.length + DELTA;
      paramInt = paramInt < i ? i : paramInt;
      long[] arrayOfLong = allocate(paramInt);
      System.arraycopy(list, 0, arrayOfLong, 0, size);
      list = arrayOfLong;
    }
  }
  
  private final long[] allocate(int paramInt)
  {
    return new long[paramInt];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\LongList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.nio;

public abstract class LongBuffer
  extends Buffer
  implements Comparable<LongBuffer>
{
  final long[] hb;
  final int offset;
  boolean isReadOnly;
  
  LongBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long[] paramArrayOfLong, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    hb = paramArrayOfLong;
    offset = paramInt5;
  }
  
  LongBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0);
  }
  
  public static LongBuffer allocate(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    return new HeapLongBuffer(paramInt, paramInt);
  }
  
  public static LongBuffer wrap(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    try
    {
      return new HeapLongBuffer(paramArrayOfLong, paramInt1, paramInt2);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IndexOutOfBoundsException();
    }
  }
  
  public static LongBuffer wrap(long[] paramArrayOfLong)
  {
    return wrap(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
  
  public abstract LongBuffer slice();
  
  public abstract LongBuffer duplicate();
  
  public abstract LongBuffer asReadOnlyBuffer();
  
  public abstract long get();
  
  public abstract LongBuffer put(long paramLong);
  
  public abstract long get(int paramInt);
  
  public abstract LongBuffer put(int paramInt, long paramLong);
  
  public LongBuffer get(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      paramArrayOfLong[j] = get();
    }
    return this;
  }
  
  public LongBuffer get(long[] paramArrayOfLong)
  {
    return get(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
  
  public LongBuffer put(LongBuffer paramLongBuffer)
  {
    if (paramLongBuffer == this) {
      throw new IllegalArgumentException();
    }
    if (isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    int i = paramLongBuffer.remaining();
    if (i > remaining()) {
      throw new BufferOverflowException();
    }
    for (int j = 0; j < i; j++) {
      put(paramLongBuffer.get());
    }
    return this;
  }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      put(paramArrayOfLong[j]);
    }
    return this;
  }
  
  public final LongBuffer put(long[] paramArrayOfLong)
  {
    return put(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
  
  public final boolean hasArray()
  {
    return (hb != null) && (!isReadOnly);
  }
  
  public final long[] array()
  {
    if (hb == null) {
      throw new UnsupportedOperationException();
    }
    if (isReadOnly) {
      throw new ReadOnlyBufferException();
    }
    return hb;
  }
  
  public final int arrayOffset()
  {
    if (hb == null) {
      throw new UnsupportedOperationException();
    }
    if (isReadOnly) {
      throw new ReadOnlyBufferException();
    }
    return offset;
  }
  
  public abstract LongBuffer compact();
  
  public abstract boolean isDirect();
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getClass().getName());
    localStringBuffer.append("[pos=");
    localStringBuffer.append(position());
    localStringBuffer.append(" lim=");
    localStringBuffer.append(limit());
    localStringBuffer.append(" cap=");
    localStringBuffer.append(capacity());
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  public int hashCode()
  {
    int i = 1;
    int j = position();
    for (int k = limit() - 1; k >= j; k--) {
      i = 31 * i + (int)get(k);
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof LongBuffer)) {
      return false;
    }
    LongBuffer localLongBuffer = (LongBuffer)paramObject;
    if (remaining() != localLongBuffer.remaining()) {
      return false;
    }
    int i = position();
    int j = limit() - 1;
    for (int k = localLongBuffer.limit() - 1; j >= i; k--)
    {
      if (!equals(get(j), localLongBuffer.get(k))) {
        return false;
      }
      j--;
    }
    return true;
  }
  
  private static boolean equals(long paramLong1, long paramLong2)
  {
    return paramLong1 == paramLong2;
  }
  
  public int compareTo(LongBuffer paramLongBuffer)
  {
    int i = position() + Math.min(remaining(), paramLongBuffer.remaining());
    int j = position();
    for (int k = paramLongBuffer.position(); j < i; k++)
    {
      int m = compare(get(j), paramLongBuffer.get(k));
      if (m != 0) {
        return m;
      }
      j++;
    }
    return remaining() - paramLongBuffer.remaining();
  }
  
  private static int compare(long paramLong1, long paramLong2)
  {
    return Long.compare(paramLong1, paramLong2);
  }
  
  public abstract ByteOrder order();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\LongBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
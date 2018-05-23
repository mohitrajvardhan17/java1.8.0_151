package java.nio;

public abstract class ShortBuffer
  extends Buffer
  implements Comparable<ShortBuffer>
{
  final short[] hb;
  final int offset;
  boolean isReadOnly;
  
  ShortBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short[] paramArrayOfShort, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    hb = paramArrayOfShort;
    offset = paramInt5;
  }
  
  ShortBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0);
  }
  
  public static ShortBuffer allocate(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    return new HeapShortBuffer(paramInt, paramInt);
  }
  
  public static ShortBuffer wrap(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    try
    {
      return new HeapShortBuffer(paramArrayOfShort, paramInt1, paramInt2);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IndexOutOfBoundsException();
    }
  }
  
  public static ShortBuffer wrap(short[] paramArrayOfShort)
  {
    return wrap(paramArrayOfShort, 0, paramArrayOfShort.length);
  }
  
  public abstract ShortBuffer slice();
  
  public abstract ShortBuffer duplicate();
  
  public abstract ShortBuffer asReadOnlyBuffer();
  
  public abstract short get();
  
  public abstract ShortBuffer put(short paramShort);
  
  public abstract short get(int paramInt);
  
  public abstract ShortBuffer put(int paramInt, short paramShort);
  
  public ShortBuffer get(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      paramArrayOfShort[j] = get();
    }
    return this;
  }
  
  public ShortBuffer get(short[] paramArrayOfShort)
  {
    return get(paramArrayOfShort, 0, paramArrayOfShort.length);
  }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer)
  {
    if (paramShortBuffer == this) {
      throw new IllegalArgumentException();
    }
    if (isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    int i = paramShortBuffer.remaining();
    if (i > remaining()) {
      throw new BufferOverflowException();
    }
    for (int j = 0; j < i; j++) {
      put(paramShortBuffer.get());
    }
    return this;
  }
  
  public ShortBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      put(paramArrayOfShort[j]);
    }
    return this;
  }
  
  public final ShortBuffer put(short[] paramArrayOfShort)
  {
    return put(paramArrayOfShort, 0, paramArrayOfShort.length);
  }
  
  public final boolean hasArray()
  {
    return (hb != null) && (!isReadOnly);
  }
  
  public final short[] array()
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
  
  public abstract ShortBuffer compact();
  
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
      i = 31 * i + get(k);
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ShortBuffer)) {
      return false;
    }
    ShortBuffer localShortBuffer = (ShortBuffer)paramObject;
    if (remaining() != localShortBuffer.remaining()) {
      return false;
    }
    int i = position();
    int j = limit() - 1;
    for (int k = localShortBuffer.limit() - 1; j >= i; k--)
    {
      if (!equals(get(j), localShortBuffer.get(k))) {
        return false;
      }
      j--;
    }
    return true;
  }
  
  private static boolean equals(short paramShort1, short paramShort2)
  {
    return paramShort1 == paramShort2;
  }
  
  public int compareTo(ShortBuffer paramShortBuffer)
  {
    int i = position() + Math.min(remaining(), paramShortBuffer.remaining());
    int j = position();
    for (int k = paramShortBuffer.position(); j < i; k++)
    {
      int m = compare(get(j), paramShortBuffer.get(k));
      if (m != 0) {
        return m;
      }
      j++;
    }
    return remaining() - paramShortBuffer.remaining();
  }
  
  private static int compare(short paramShort1, short paramShort2)
  {
    return Short.compare(paramShort1, paramShort2);
  }
  
  public abstract ByteOrder order();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ShortBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
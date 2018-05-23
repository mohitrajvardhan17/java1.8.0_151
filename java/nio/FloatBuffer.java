package java.nio;

public abstract class FloatBuffer
  extends Buffer
  implements Comparable<FloatBuffer>
{
  final float[] hb;
  final int offset;
  boolean isReadOnly;
  
  FloatBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    hb = paramArrayOfFloat;
    offset = paramInt5;
  }
  
  FloatBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0);
  }
  
  public static FloatBuffer allocate(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    return new HeapFloatBuffer(paramInt, paramInt);
  }
  
  public static FloatBuffer wrap(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    try
    {
      return new HeapFloatBuffer(paramArrayOfFloat, paramInt1, paramInt2);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IndexOutOfBoundsException();
    }
  }
  
  public static FloatBuffer wrap(float[] paramArrayOfFloat)
  {
    return wrap(paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
  
  public abstract FloatBuffer slice();
  
  public abstract FloatBuffer duplicate();
  
  public abstract FloatBuffer asReadOnlyBuffer();
  
  public abstract float get();
  
  public abstract FloatBuffer put(float paramFloat);
  
  public abstract float get(int paramInt);
  
  public abstract FloatBuffer put(int paramInt, float paramFloat);
  
  public FloatBuffer get(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      paramArrayOfFloat[j] = get();
    }
    return this;
  }
  
  public FloatBuffer get(float[] paramArrayOfFloat)
  {
    return get(paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer)
  {
    if (paramFloatBuffer == this) {
      throw new IllegalArgumentException();
    }
    if (isReadOnly()) {
      throw new ReadOnlyBufferException();
    }
    int i = paramFloatBuffer.remaining();
    if (i > remaining()) {
      throw new BufferOverflowException();
    }
    for (int j = 0; j < i; j++) {
      put(paramFloatBuffer.get());
    }
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      put(paramArrayOfFloat[j]);
    }
    return this;
  }
  
  public final FloatBuffer put(float[] paramArrayOfFloat)
  {
    return put(paramArrayOfFloat, 0, paramArrayOfFloat.length);
  }
  
  public final boolean hasArray()
  {
    return (hb != null) && (!isReadOnly);
  }
  
  public final float[] array()
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
  
  public abstract FloatBuffer compact();
  
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
    if (!(paramObject instanceof FloatBuffer)) {
      return false;
    }
    FloatBuffer localFloatBuffer = (FloatBuffer)paramObject;
    if (remaining() != localFloatBuffer.remaining()) {
      return false;
    }
    int i = position();
    int j = limit() - 1;
    for (int k = localFloatBuffer.limit() - 1; j >= i; k--)
    {
      if (!equals(get(j), localFloatBuffer.get(k))) {
        return false;
      }
      j--;
    }
    return true;
  }
  
  private static boolean equals(float paramFloat1, float paramFloat2)
  {
    return (paramFloat1 == paramFloat2) || ((Float.isNaN(paramFloat1)) && (Float.isNaN(paramFloat2)));
  }
  
  public int compareTo(FloatBuffer paramFloatBuffer)
  {
    int i = position() + Math.min(remaining(), paramFloatBuffer.remaining());
    int j = position();
    for (int k = paramFloatBuffer.position(); j < i; k++)
    {
      int m = compare(get(j), paramFloatBuffer.get(k));
      if (m != 0) {
        return m;
      }
      j++;
    }
    return remaining() - paramFloatBuffer.remaining();
  }
  
  private static int compare(float paramFloat1, float paramFloat2)
  {
    return Float.isNaN(paramFloat1) ? 1 : Float.isNaN(paramFloat2) ? 0 : paramFloat1 == paramFloat2 ? 0 : paramFloat1 > paramFloat2 ? 1 : paramFloat1 < paramFloat2 ? -1 : -1;
  }
  
  public abstract ByteOrder order();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\FloatBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
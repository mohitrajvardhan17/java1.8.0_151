package java.nio;

class HeapByteBufferR
  extends HeapByteBuffer
{
  HeapByteBufferR(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  HeapByteBufferR(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(paramArrayOfByte, paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  protected HeapByteBufferR(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    isReadOnly = true;
  }
  
  public ByteBuffer slice()
  {
    return new HeapByteBufferR(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public ByteBuffer duplicate()
  {
    return new HeapByteBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public ByteBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public ByteBuffer put(byte paramByte)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer put(int paramInt, byte paramByte)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  byte _get(int paramInt)
  {
    return hb[paramInt];
  }
  
  void _put(int paramInt, byte paramByte)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putChar(char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putChar(int paramInt, char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer asCharBuffer()
  {
    int i = remaining() >> 1;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, i, i, j) : new ByteBufferAsCharBufferRL(this, -1, 0, i, i, j);
  }
  
  public ByteBuffer putShort(short paramShort)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putShort(int paramInt, short paramShort)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ShortBuffer asShortBuffer()
  {
    int i = remaining() >> 1;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, i, i, j) : new ByteBufferAsShortBufferRL(this, -1, 0, i, i, j);
  }
  
  public ByteBuffer putInt(int paramInt)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putInt(int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer asIntBuffer()
  {
    int i = remaining() >> 2;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, i, i, j) : new ByteBufferAsIntBufferRL(this, -1, 0, i, i, j);
  }
  
  public ByteBuffer putLong(long paramLong)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putLong(int paramInt, long paramLong)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer asLongBuffer()
  {
    int i = remaining() >> 3;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, i, i, j) : new ByteBufferAsLongBufferRL(this, -1, 0, i, i, j);
  }
  
  public ByteBuffer putFloat(float paramFloat)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putFloat(int paramInt, float paramFloat)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer asFloatBuffer()
  {
    int i = remaining() >> 2;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, i, i, j) : new ByteBufferAsFloatBufferRL(this, -1, 0, i, i, j);
  }
  
  public ByteBuffer putDouble(double paramDouble)
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteBuffer putDouble(int paramInt, double paramDouble)
  {
    throw new ReadOnlyBufferException();
  }
  
  public DoubleBuffer asDoubleBuffer()
  {
    int i = remaining() >> 3;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, i, i, j) : new ByteBufferAsDoubleBufferRL(this, -1, 0, i, i, j);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapByteBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
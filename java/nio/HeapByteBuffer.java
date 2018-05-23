package java.nio;

class HeapByteBuffer
  extends ByteBuffer
{
  HeapByteBuffer(int paramInt1, int paramInt2)
  {
    super(-1, 0, paramInt2, paramInt1, new byte[paramInt1], 0);
  }
  
  HeapByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfByte.length, paramArrayOfByte, 0);
  }
  
  protected HeapByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5);
  }
  
  public ByteBuffer slice()
  {
    return new HeapByteBuffer(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public ByteBuffer duplicate()
  {
    return new HeapByteBuffer(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public ByteBuffer asReadOnlyBuffer()
  {
    return new HeapByteBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return paramInt + offset;
  }
  
  public byte get()
  {
    return hb[ix(nextGetIndex())];
  }
  
  public byte get(int paramInt)
  {
    return hb[ix(checkIndex(paramInt))];
  }
  
  public ByteBuffer get(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    System.arraycopy(hb, ix(position()), paramArrayOfByte, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect()
  {
    return false;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public ByteBuffer put(byte paramByte)
  {
    hb[ix(nextPutIndex())] = paramByte;
    return this;
  }
  
  public ByteBuffer put(int paramInt, byte paramByte)
  {
    hb[ix(checkIndex(paramInt))] = paramByte;
    return this;
  }
  
  public ByteBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfByte.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    System.arraycopy(paramArrayOfByte, paramInt1, hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public ByteBuffer put(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer instanceof HeapByteBuffer))
    {
      if (paramByteBuffer == this) {
        throw new IllegalArgumentException();
      }
      HeapByteBuffer localHeapByteBuffer = (HeapByteBuffer)paramByteBuffer;
      int j = localHeapByteBuffer.remaining();
      if (j > remaining()) {
        throw new BufferOverflowException();
      }
      System.arraycopy(hb, localHeapByteBuffer.ix(localHeapByteBuffer.position()), hb, ix(position()), j);
      localHeapByteBuffer.position(localHeapByteBuffer.position() + j);
      position(position() + j);
    }
    else if (paramByteBuffer.isDirect())
    {
      int i = paramByteBuffer.remaining();
      if (i > remaining()) {
        throw new BufferOverflowException();
      }
      paramByteBuffer.get(hb, ix(position()), i);
      position(position() + i);
    }
    else
    {
      super.put(paramByteBuffer);
    }
    return this;
  }
  
  public ByteBuffer compact()
  {
    System.arraycopy(hb, ix(position()), hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  byte _get(int paramInt)
  {
    return hb[paramInt];
  }
  
  void _put(int paramInt, byte paramByte)
  {
    hb[paramInt] = paramByte;
  }
  
  public char getChar()
  {
    return Bits.getChar(this, ix(nextGetIndex(2)), bigEndian);
  }
  
  public char getChar(int paramInt)
  {
    return Bits.getChar(this, ix(checkIndex(paramInt, 2)), bigEndian);
  }
  
  public ByteBuffer putChar(char paramChar)
  {
    Bits.putChar(this, ix(nextPutIndex(2)), paramChar, bigEndian);
    return this;
  }
  
  public ByteBuffer putChar(int paramInt, char paramChar)
  {
    Bits.putChar(this, ix(checkIndex(paramInt, 2)), paramChar, bigEndian);
    return this;
  }
  
  public CharBuffer asCharBuffer()
  {
    int i = remaining() >> 1;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, i, i, j) : new ByteBufferAsCharBufferL(this, -1, 0, i, i, j);
  }
  
  public short getShort()
  {
    return Bits.getShort(this, ix(nextGetIndex(2)), bigEndian);
  }
  
  public short getShort(int paramInt)
  {
    return Bits.getShort(this, ix(checkIndex(paramInt, 2)), bigEndian);
  }
  
  public ByteBuffer putShort(short paramShort)
  {
    Bits.putShort(this, ix(nextPutIndex(2)), paramShort, bigEndian);
    return this;
  }
  
  public ByteBuffer putShort(int paramInt, short paramShort)
  {
    Bits.putShort(this, ix(checkIndex(paramInt, 2)), paramShort, bigEndian);
    return this;
  }
  
  public ShortBuffer asShortBuffer()
  {
    int i = remaining() >> 1;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, i, i, j) : new ByteBufferAsShortBufferL(this, -1, 0, i, i, j);
  }
  
  public int getInt()
  {
    return Bits.getInt(this, ix(nextGetIndex(4)), bigEndian);
  }
  
  public int getInt(int paramInt)
  {
    return Bits.getInt(this, ix(checkIndex(paramInt, 4)), bigEndian);
  }
  
  public ByteBuffer putInt(int paramInt)
  {
    Bits.putInt(this, ix(nextPutIndex(4)), paramInt, bigEndian);
    return this;
  }
  
  public ByteBuffer putInt(int paramInt1, int paramInt2)
  {
    Bits.putInt(this, ix(checkIndex(paramInt1, 4)), paramInt2, bigEndian);
    return this;
  }
  
  public IntBuffer asIntBuffer()
  {
    int i = remaining() >> 2;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, i, i, j) : new ByteBufferAsIntBufferL(this, -1, 0, i, i, j);
  }
  
  public long getLong()
  {
    return Bits.getLong(this, ix(nextGetIndex(8)), bigEndian);
  }
  
  public long getLong(int paramInt)
  {
    return Bits.getLong(this, ix(checkIndex(paramInt, 8)), bigEndian);
  }
  
  public ByteBuffer putLong(long paramLong)
  {
    Bits.putLong(this, ix(nextPutIndex(8)), paramLong, bigEndian);
    return this;
  }
  
  public ByteBuffer putLong(int paramInt, long paramLong)
  {
    Bits.putLong(this, ix(checkIndex(paramInt, 8)), paramLong, bigEndian);
    return this;
  }
  
  public LongBuffer asLongBuffer()
  {
    int i = remaining() >> 3;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, i, i, j) : new ByteBufferAsLongBufferL(this, -1, 0, i, i, j);
  }
  
  public float getFloat()
  {
    return Bits.getFloat(this, ix(nextGetIndex(4)), bigEndian);
  }
  
  public float getFloat(int paramInt)
  {
    return Bits.getFloat(this, ix(checkIndex(paramInt, 4)), bigEndian);
  }
  
  public ByteBuffer putFloat(float paramFloat)
  {
    Bits.putFloat(this, ix(nextPutIndex(4)), paramFloat, bigEndian);
    return this;
  }
  
  public ByteBuffer putFloat(int paramInt, float paramFloat)
  {
    Bits.putFloat(this, ix(checkIndex(paramInt, 4)), paramFloat, bigEndian);
    return this;
  }
  
  public FloatBuffer asFloatBuffer()
  {
    int i = remaining() >> 2;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, i, i, j) : new ByteBufferAsFloatBufferL(this, -1, 0, i, i, j);
  }
  
  public double getDouble()
  {
    return Bits.getDouble(this, ix(nextGetIndex(8)), bigEndian);
  }
  
  public double getDouble(int paramInt)
  {
    return Bits.getDouble(this, ix(checkIndex(paramInt, 8)), bigEndian);
  }
  
  public ByteBuffer putDouble(double paramDouble)
  {
    Bits.putDouble(this, ix(nextPutIndex(8)), paramDouble, bigEndian);
    return this;
  }
  
  public ByteBuffer putDouble(int paramInt, double paramDouble)
  {
    Bits.putDouble(this, ix(checkIndex(paramInt, 8)), paramDouble, bigEndian);
    return this;
  }
  
  public DoubleBuffer asDoubleBuffer()
  {
    int i = remaining() >> 3;
    int j = offset + position();
    return bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, i, i, j) : new ByteBufferAsDoubleBufferL(this, -1, 0, i, i, j);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
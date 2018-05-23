package java.nio;

class ByteBufferAsDoubleBufferL
  extends DoubleBuffer
{
  protected final ByteBuffer bb;
  protected final int offset;
  
  ByteBufferAsDoubleBufferL(ByteBuffer paramByteBuffer)
  {
    super(-1, 0, paramByteBuffer.remaining() >> 3, paramByteBuffer.remaining() >> 3);
    bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert (j <= i);
    offset = j;
  }
  
  ByteBufferAsDoubleBufferL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    bb = paramByteBuffer;
    offset = paramInt5;
  }
  
  public DoubleBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 3) + offset;
    assert (m >= 0);
    return new ByteBufferAsDoubleBufferL(bb, -1, 0, k, k, m);
  }
  
  public DoubleBuffer duplicate()
  {
    return new ByteBufferAsDoubleBufferL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public DoubleBuffer asReadOnlyBuffer()
  {
    return new ByteBufferAsDoubleBufferRL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return (paramInt << 3) + offset;
  }
  
  public double get()
  {
    return Bits.getDoubleL(bb, ix(nextGetIndex()));
  }
  
  public double get(int paramInt)
  {
    return Bits.getDoubleL(bb, ix(checkIndex(paramInt)));
  }
  
  public DoubleBuffer put(double paramDouble)
  {
    Bits.putDoubleL(bb, ix(nextPutIndex()), paramDouble);
    return this;
  }
  
  public DoubleBuffer put(int paramInt, double paramDouble)
  {
    Bits.putDoubleL(bb, ix(checkIndex(paramInt)), paramDouble);
    return this;
  }
  
  public DoubleBuffer compact()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    ByteBuffer localByteBuffer1 = bb.duplicate();
    localByteBuffer1.limit(ix(j));
    localByteBuffer1.position(ix(0));
    ByteBuffer localByteBuffer2 = localByteBuffer1.slice();
    localByteBuffer2.position(i << 3);
    localByteBuffer2.compact();
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect()
  {
    return bb.isDirect();
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.LITTLE_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsDoubleBufferL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
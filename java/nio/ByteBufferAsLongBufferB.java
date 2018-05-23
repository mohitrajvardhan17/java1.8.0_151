package java.nio;

class ByteBufferAsLongBufferB
  extends LongBuffer
{
  protected final ByteBuffer bb;
  protected final int offset;
  
  ByteBufferAsLongBufferB(ByteBuffer paramByteBuffer)
  {
    super(-1, 0, paramByteBuffer.remaining() >> 3, paramByteBuffer.remaining() >> 3);
    bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert (j <= i);
    offset = j;
  }
  
  ByteBufferAsLongBufferB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    bb = paramByteBuffer;
    offset = paramInt5;
  }
  
  public LongBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 3) + offset;
    assert (m >= 0);
    return new ByteBufferAsLongBufferB(bb, -1, 0, k, k, m);
  }
  
  public LongBuffer duplicate()
  {
    return new ByteBufferAsLongBufferB(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public LongBuffer asReadOnlyBuffer()
  {
    return new ByteBufferAsLongBufferRB(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return (paramInt << 3) + offset;
  }
  
  public long get()
  {
    return Bits.getLongB(bb, ix(nextGetIndex()));
  }
  
  public long get(int paramInt)
  {
    return Bits.getLongB(bb, ix(checkIndex(paramInt)));
  }
  
  public LongBuffer put(long paramLong)
  {
    Bits.putLongB(bb, ix(nextPutIndex()), paramLong);
    return this;
  }
  
  public LongBuffer put(int paramInt, long paramLong)
  {
    Bits.putLongB(bb, ix(checkIndex(paramInt)), paramLong);
    return this;
  }
  
  public LongBuffer compact()
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
    return ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsLongBufferB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
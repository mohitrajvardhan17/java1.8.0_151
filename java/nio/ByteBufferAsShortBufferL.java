package java.nio;

class ByteBufferAsShortBufferL
  extends ShortBuffer
{
  protected final ByteBuffer bb;
  protected final int offset;
  
  ByteBufferAsShortBufferL(ByteBuffer paramByteBuffer)
  {
    super(-1, 0, paramByteBuffer.remaining() >> 1, paramByteBuffer.remaining() >> 1);
    bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert (j <= i);
    offset = j;
  }
  
  ByteBufferAsShortBufferL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    bb = paramByteBuffer;
    offset = paramInt5;
  }
  
  public ShortBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 1) + offset;
    assert (m >= 0);
    return new ByteBufferAsShortBufferL(bb, -1, 0, k, k, m);
  }
  
  public ShortBuffer duplicate()
  {
    return new ByteBufferAsShortBufferL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public ShortBuffer asReadOnlyBuffer()
  {
    return new ByteBufferAsShortBufferRL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return (paramInt << 1) + offset;
  }
  
  public short get()
  {
    return Bits.getShortL(bb, ix(nextGetIndex()));
  }
  
  public short get(int paramInt)
  {
    return Bits.getShortL(bb, ix(checkIndex(paramInt)));
  }
  
  public ShortBuffer put(short paramShort)
  {
    Bits.putShortL(bb, ix(nextPutIndex()), paramShort);
    return this;
  }
  
  public ShortBuffer put(int paramInt, short paramShort)
  {
    Bits.putShortL(bb, ix(checkIndex(paramInt)), paramShort);
    return this;
  }
  
  public ShortBuffer compact()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    ByteBuffer localByteBuffer1 = bb.duplicate();
    localByteBuffer1.limit(ix(j));
    localByteBuffer1.position(ix(0));
    ByteBuffer localByteBuffer2 = localByteBuffer1.slice();
    localByteBuffer2.position(i << 1);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsShortBufferL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
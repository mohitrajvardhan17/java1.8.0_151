package java.nio;

class ByteBufferAsIntBufferB
  extends IntBuffer
{
  protected final ByteBuffer bb;
  protected final int offset;
  
  ByteBufferAsIntBufferB(ByteBuffer paramByteBuffer)
  {
    super(-1, 0, paramByteBuffer.remaining() >> 2, paramByteBuffer.remaining() >> 2);
    bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert (j <= i);
    offset = j;
  }
  
  ByteBufferAsIntBufferB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    bb = paramByteBuffer;
    offset = paramInt5;
  }
  
  public IntBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 2) + offset;
    assert (m >= 0);
    return new ByteBufferAsIntBufferB(bb, -1, 0, k, k, m);
  }
  
  public IntBuffer duplicate()
  {
    return new ByteBufferAsIntBufferB(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public IntBuffer asReadOnlyBuffer()
  {
    return new ByteBufferAsIntBufferRB(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return (paramInt << 2) + offset;
  }
  
  public int get()
  {
    return Bits.getIntB(bb, ix(nextGetIndex()));
  }
  
  public int get(int paramInt)
  {
    return Bits.getIntB(bb, ix(checkIndex(paramInt)));
  }
  
  public IntBuffer put(int paramInt)
  {
    Bits.putIntB(bb, ix(nextPutIndex()), paramInt);
    return this;
  }
  
  public IntBuffer put(int paramInt1, int paramInt2)
  {
    Bits.putIntB(bb, ix(checkIndex(paramInt1)), paramInt2);
    return this;
  }
  
  public IntBuffer compact()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    ByteBuffer localByteBuffer1 = bb.duplicate();
    localByteBuffer1.limit(ix(j));
    localByteBuffer1.position(ix(0));
    ByteBuffer localByteBuffer2 = localByteBuffer1.slice();
    localByteBuffer2.position(i << 2);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsIntBufferB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
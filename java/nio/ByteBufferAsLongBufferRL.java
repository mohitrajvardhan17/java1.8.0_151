package java.nio;

class ByteBufferAsLongBufferRL
  extends ByteBufferAsLongBufferL
{
  ByteBufferAsLongBufferRL(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }
  
  ByteBufferAsLongBufferRL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public LongBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 3) + offset;
    assert (m >= 0);
    return new ByteBufferAsLongBufferRL(bb, -1, 0, k, k, m);
  }
  
  public LongBuffer duplicate()
  {
    return new ByteBufferAsLongBufferRL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public LongBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public LongBuffer put(long paramLong)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer put(int paramInt, long paramLong)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public boolean isDirect()
  {
    return bb.isDirect();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.LITTLE_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsLongBufferRL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.nio;

class HeapLongBufferR
  extends HeapLongBuffer
{
  HeapLongBufferR(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  HeapLongBufferR(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    super(paramArrayOfLong, paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  protected HeapLongBufferR(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramArrayOfLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    isReadOnly = true;
  }
  
  public LongBuffer slice()
  {
    return new HeapLongBufferR(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public LongBuffer duplicate()
  {
    return new HeapLongBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public LongBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public LongBuffer put(long paramLong)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer put(int paramInt, long paramLong)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer put(LongBuffer paramLongBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapLongBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
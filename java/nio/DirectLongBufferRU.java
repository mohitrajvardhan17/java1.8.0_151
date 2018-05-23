package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectLongBufferRU
  extends DirectLongBufferU
  implements DirectBuffer
{
  DirectLongBufferRU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public LongBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = i << 3;
    assert (m >= 0);
    return new DirectLongBufferRU(this, -1, 0, k, k, m);
  }
  
  public LongBuffer duplicate()
  {
    return new DirectLongBufferRU(this, markValue(), position(), limit(), capacity(), 0);
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
  
  public LongBuffer put(LongBuffer paramLongBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public LongBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public boolean isDirect()
  {
    return true;
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\DirectLongBufferRU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
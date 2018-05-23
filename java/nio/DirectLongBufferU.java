package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectLongBufferU
  extends LongBuffer
  implements DirectBuffer
{
  protected static final Unsafe unsafe = Bits.unsafe();
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(long[].class);
  protected static final boolean unaligned = Bits.unaligned();
  private final Object att;
  
  public Object attachment()
  {
    return att;
  }
  
  public Cleaner cleaner()
  {
    return null;
  }
  
  DirectLongBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    address = (paramDirectBuffer.address() + paramInt5);
    att = paramDirectBuffer;
  }
  
  public LongBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = i << 3;
    assert (m >= 0);
    return new DirectLongBufferU(this, -1, 0, k, k, m);
  }
  
  public LongBuffer duplicate()
  {
    return new DirectLongBufferU(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public LongBuffer asReadOnlyBuffer()
  {
    return new DirectLongBufferRU(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public long address()
  {
    return address;
  }
  
  private long ix(int paramInt)
  {
    return address + (paramInt << 3);
  }
  
  public long get()
  {
    return unsafe.getLong(ix(nextGetIndex()));
  }
  
  public long get(int paramInt)
  {
    return unsafe.getLong(ix(checkIndex(paramInt)));
  }
  
  public LongBuffer get(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramInt2 << 3 > 6L)
    {
      checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
      int i = position();
      int j = limit();
      assert (i <= j);
      int k = i <= j ? j - i : 0;
      if (paramInt2 > k) {
        throw new BufferUnderflowException();
      }
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToLongArray(ix(i), paramArrayOfLong, paramInt1 << 3, paramInt2 << 3);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfLong, arrayBaseOffset, paramInt1 << 3, paramInt2 << 3);
      }
      position(i + paramInt2);
    }
    else
    {
      super.get(paramArrayOfLong, paramInt1, paramInt2);
    }
    return this;
  }
  
  public LongBuffer put(long paramLong)
  {
    unsafe.putLong(ix(nextPutIndex()), paramLong);
    return this;
  }
  
  public LongBuffer put(int paramInt, long paramLong)
  {
    unsafe.putLong(ix(checkIndex(paramInt)), paramLong);
    return this;
  }
  
  public LongBuffer put(LongBuffer paramLongBuffer)
  {
    int j;
    int k;
    if ((paramLongBuffer instanceof DirectLongBufferU))
    {
      if (paramLongBuffer == this) {
        throw new IllegalArgumentException();
      }
      DirectLongBufferU localDirectLongBufferU = (DirectLongBufferU)paramLongBuffer;
      j = localDirectLongBufferU.position();
      k = localDirectLongBufferU.limit();
      assert (j <= k);
      int m = j <= k ? k - j : 0;
      int n = position();
      int i1 = limit();
      assert (n <= i1);
      int i2 = n <= i1 ? i1 - n : 0;
      if (m > i2) {
        throw new BufferOverflowException();
      }
      unsafe.copyMemory(localDirectLongBufferU.ix(j), ix(n), m << 3);
      localDirectLongBufferU.position(j + m);
      position(n + m);
    }
    else if (hb != null)
    {
      int i = paramLongBuffer.position();
      j = paramLongBuffer.limit();
      assert (i <= j);
      k = i <= j ? j - i : 0;
      put(hb, offset + i, k);
      paramLongBuffer.position(i + k);
    }
    else
    {
      super.put(paramLongBuffer);
    }
    return this;
  }
  
  public LongBuffer put(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    if (paramInt2 << 3 > 6L)
    {
      checkBounds(paramInt1, paramInt2, paramArrayOfLong.length);
      int i = position();
      int j = limit();
      assert (i <= j);
      int k = i <= j ? j - i : 0;
      if (paramInt2 > k) {
        throw new BufferOverflowException();
      }
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromLongArray(paramArrayOfLong, paramInt1 << 3, ix(i), paramInt2 << 3);
      } else {
        Bits.copyFromArray(paramArrayOfLong, arrayBaseOffset, paramInt1 << 3, ix(i), paramInt2 << 3);
      }
      position(i + paramInt2);
    }
    else
    {
      super.put(paramArrayOfLong, paramInt1, paramInt2);
    }
    return this;
  }
  
  public LongBuffer compact()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    unsafe.copyMemory(ix(i), ix(0), k << 3);
    position(k);
    limit(capacity());
    discardMark();
    return this;
  }
  
  public boolean isDirect()
  {
    return true;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\DirectLongBufferU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
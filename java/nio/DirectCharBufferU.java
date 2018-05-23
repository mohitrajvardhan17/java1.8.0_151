package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectCharBufferU
  extends CharBuffer
  implements DirectBuffer
{
  protected static final Unsafe unsafe = Bits.unsafe();
  private static final long arrayBaseOffset = unsafe.arrayBaseOffset(char[].class);
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
  
  DirectCharBufferU(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    address = (paramDirectBuffer.address() + paramInt5);
    att = paramDirectBuffer;
  }
  
  public CharBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = i << 1;
    assert (m >= 0);
    return new DirectCharBufferU(this, -1, 0, k, k, m);
  }
  
  public CharBuffer duplicate()
  {
    return new DirectCharBufferU(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public CharBuffer asReadOnlyBuffer()
  {
    return new DirectCharBufferRU(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public long address()
  {
    return address;
  }
  
  private long ix(int paramInt)
  {
    return address + (paramInt << 1);
  }
  
  public char get()
  {
    return unsafe.getChar(ix(nextGetIndex()));
  }
  
  public char get(int paramInt)
  {
    return unsafe.getChar(ix(checkIndex(paramInt)));
  }
  
  char getUnchecked(int paramInt)
  {
    return unsafe.getChar(ix(paramInt));
  }
  
  public CharBuffer get(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 << 1 > 6L)
    {
      checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
      int i = position();
      int j = limit();
      assert (i <= j);
      int k = i <= j ? j - i : 0;
      if (paramInt2 > k) {
        throw new BufferUnderflowException();
      }
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyToCharArray(ix(i), paramArrayOfChar, paramInt1 << 1, paramInt2 << 1);
      } else {
        Bits.copyToArray(ix(i), paramArrayOfChar, arrayBaseOffset, paramInt1 << 1, paramInt2 << 1);
      }
      position(i + paramInt2);
    }
    else
    {
      super.get(paramArrayOfChar, paramInt1, paramInt2);
    }
    return this;
  }
  
  public CharBuffer put(char paramChar)
  {
    unsafe.putChar(ix(nextPutIndex()), paramChar);
    return this;
  }
  
  public CharBuffer put(int paramInt, char paramChar)
  {
    unsafe.putChar(ix(checkIndex(paramInt)), paramChar);
    return this;
  }
  
  public CharBuffer put(CharBuffer paramCharBuffer)
  {
    int j;
    int k;
    if ((paramCharBuffer instanceof DirectCharBufferU))
    {
      if (paramCharBuffer == this) {
        throw new IllegalArgumentException();
      }
      DirectCharBufferU localDirectCharBufferU = (DirectCharBufferU)paramCharBuffer;
      j = localDirectCharBufferU.position();
      k = localDirectCharBufferU.limit();
      assert (j <= k);
      int m = j <= k ? k - j : 0;
      int n = position();
      int i1 = limit();
      assert (n <= i1);
      int i2 = n <= i1 ? i1 - n : 0;
      if (m > i2) {
        throw new BufferOverflowException();
      }
      unsafe.copyMemory(localDirectCharBufferU.ix(j), ix(n), m << 1);
      localDirectCharBufferU.position(j + m);
      position(n + m);
    }
    else if (hb != null)
    {
      int i = paramCharBuffer.position();
      j = paramCharBuffer.limit();
      assert (i <= j);
      k = i <= j ? j - i : 0;
      put(hb, offset + i, k);
      paramCharBuffer.position(i + k);
    }
    else
    {
      super.put(paramCharBuffer);
    }
    return this;
  }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 << 1 > 6L)
    {
      checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
      int i = position();
      int j = limit();
      assert (i <= j);
      int k = i <= j ? j - i : 0;
      if (paramInt2 > k) {
        throw new BufferOverflowException();
      }
      if (order() != ByteOrder.nativeOrder()) {
        Bits.copyFromCharArray(paramArrayOfChar, paramInt1 << 1, ix(i), paramInt2 << 1);
      } else {
        Bits.copyFromArray(paramArrayOfChar, arrayBaseOffset, paramInt1 << 1, ix(i), paramInt2 << 1);
      }
      position(i + paramInt2);
    }
    else
    {
      super.put(paramArrayOfChar, paramInt1, paramInt2);
    }
    return this;
  }
  
  public CharBuffer compact()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    unsafe.copyMemory(ix(i), ix(0), k << 1);
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
  
  public String toString(int paramInt1, int paramInt2)
  {
    if ((paramInt2 > limit()) || (paramInt1 > paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    try
    {
      int i = paramInt2 - paramInt1;
      char[] arrayOfChar = new char[i];
      CharBuffer localCharBuffer1 = CharBuffer.wrap(arrayOfChar);
      CharBuffer localCharBuffer2 = duplicate();
      localCharBuffer2.position(paramInt1);
      localCharBuffer2.limit(paramInt2);
      localCharBuffer1.put(localCharBuffer2);
      return new String(arrayOfChar);
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new IndexOutOfBoundsException();
    }
  }
  
  public CharBuffer subSequence(int paramInt1, int paramInt2)
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    i = i <= j ? i : j;
    int k = j - i;
    if ((paramInt1 < 0) || (paramInt2 > k) || (paramInt1 > paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    return new DirectCharBufferU(this, -1, i + paramInt1, i + paramInt2, capacity(), offset);
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\DirectCharBufferU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
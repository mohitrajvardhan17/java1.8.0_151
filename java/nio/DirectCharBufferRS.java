package java.nio;

import sun.nio.ch.DirectBuffer;

class DirectCharBufferRS
  extends DirectCharBufferS
  implements DirectBuffer
{
  DirectCharBufferRS(DirectBuffer paramDirectBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramDirectBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public CharBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = i << 1;
    assert (m >= 0);
    return new DirectCharBufferRS(this, -1, 0, k, k, m);
  }
  
  public CharBuffer duplicate()
  {
    return new DirectCharBufferRS(this, markValue(), position(), limit(), capacity(), 0);
  }
  
  public CharBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public CharBuffer put(char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer put(int paramInt, char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer put(CharBuffer paramCharBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer compact()
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
    return new DirectCharBufferRS(this, -1, i + paramInt1, i + paramInt2, capacity(), offset);
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\DirectCharBufferRS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
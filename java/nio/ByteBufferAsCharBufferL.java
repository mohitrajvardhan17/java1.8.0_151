package java.nio;

class ByteBufferAsCharBufferL
  extends CharBuffer
{
  protected final ByteBuffer bb;
  protected final int offset;
  
  ByteBufferAsCharBufferL(ByteBuffer paramByteBuffer)
  {
    super(-1, 0, paramByteBuffer.remaining() >> 1, paramByteBuffer.remaining() >> 1);
    bb = paramByteBuffer;
    int i = capacity();
    limit(i);
    int j = position();
    assert (j <= i);
    offset = j;
  }
  
  ByteBufferAsCharBufferL(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    bb = paramByteBuffer;
    offset = paramInt5;
  }
  
  public CharBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 1) + offset;
    assert (m >= 0);
    return new ByteBufferAsCharBufferL(bb, -1, 0, k, k, m);
  }
  
  public CharBuffer duplicate()
  {
    return new ByteBufferAsCharBufferL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public CharBuffer asReadOnlyBuffer()
  {
    return new ByteBufferAsCharBufferRL(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return (paramInt << 1) + offset;
  }
  
  public char get()
  {
    return Bits.getCharL(bb, ix(nextGetIndex()));
  }
  
  public char get(int paramInt)
  {
    return Bits.getCharL(bb, ix(checkIndex(paramInt)));
  }
  
  char getUnchecked(int paramInt)
  {
    return Bits.getCharL(bb, ix(paramInt));
  }
  
  public CharBuffer put(char paramChar)
  {
    Bits.putCharL(bb, ix(nextPutIndex()), paramChar);
    return this;
  }
  
  public CharBuffer put(int paramInt, char paramChar)
  {
    Bits.putCharL(bb, ix(checkIndex(paramInt)), paramChar);
    return this;
  }
  
  public CharBuffer compact()
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
    return new ByteBufferAsCharBufferL(bb, -1, i + paramInt1, i + paramInt2, capacity(), offset);
  }
  
  public ByteOrder order()
  {
    return ByteOrder.LITTLE_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsCharBufferL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
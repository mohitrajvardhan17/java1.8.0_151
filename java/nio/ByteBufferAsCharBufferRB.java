package java.nio;

class ByteBufferAsCharBufferRB
  extends ByteBufferAsCharBufferB
{
  ByteBufferAsCharBufferRB(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }
  
  ByteBufferAsCharBufferRB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public CharBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 1) + offset;
    assert (m >= 0);
    return new ByteBufferAsCharBufferRB(bb, -1, 0, k, k, m);
  }
  
  public CharBuffer duplicate()
  {
    return new ByteBufferAsCharBufferRB(bb, markValue(), position(), limit(), capacity(), offset);
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
  
  public CharBuffer compact()
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
    return new ByteBufferAsCharBufferRB(bb, -1, i + paramInt1, i + paramInt2, capacity(), offset);
  }
  
  public ByteOrder order()
  {
    return ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsCharBufferRB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
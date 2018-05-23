package java.nio;

class HeapCharBufferR
  extends HeapCharBuffer
{
  HeapCharBufferR(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  HeapCharBufferR(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    super(paramArrayOfChar, paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  protected HeapCharBufferR(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    isReadOnly = true;
  }
  
  public CharBuffer slice()
  {
    return new HeapCharBufferR(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public CharBuffer duplicate()
  {
    return new HeapCharBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public CharBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public CharBuffer put(char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer put(int paramInt, char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer put(CharBuffer paramCharBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public CharBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  String toString(int paramInt1, int paramInt2)
  {
    try
    {
      return new String(hb, paramInt1 + offset, paramInt2 - paramInt1);
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new IndexOutOfBoundsException();
    }
  }
  
  public CharBuffer subSequence(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 > length()) || (paramInt1 > paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    int i = position();
    return new HeapCharBufferR(hb, -1, i + paramInt1, i + paramInt2, capacity(), offset);
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapCharBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
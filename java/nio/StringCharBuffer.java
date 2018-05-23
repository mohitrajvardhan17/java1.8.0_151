package java.nio;

class StringCharBuffer
  extends CharBuffer
{
  CharSequence str;
  
  StringCharBuffer(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt2, paramCharSequence.length());
    int i = paramCharSequence.length();
    if ((paramInt1 < 0) || (paramInt1 > i) || (paramInt2 < paramInt1) || (paramInt2 > i)) {
      throw new IndexOutOfBoundsException();
    }
    str = paramCharSequence;
  }
  
  public CharBuffer slice()
  {
    return new StringCharBuffer(str, -1, 0, remaining(), remaining(), offset + position());
  }
  
  private StringCharBuffer(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, null, paramInt5);
    str = paramCharSequence;
  }
  
  public CharBuffer duplicate()
  {
    return new StringCharBuffer(str, markValue(), position(), limit(), capacity(), offset);
  }
  
  public CharBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public final char get()
  {
    return str.charAt(nextGetIndex() + offset);
  }
  
  public final char get(int paramInt)
  {
    return str.charAt(checkIndex(paramInt) + offset);
  }
  
  char getUnchecked(int paramInt)
  {
    return str.charAt(paramInt + offset);
  }
  
  public final CharBuffer put(char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public final CharBuffer put(int paramInt, char paramChar)
  {
    throw new ReadOnlyBufferException();
  }
  
  public final CharBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public final boolean isReadOnly()
  {
    return true;
  }
  
  final String toString(int paramInt1, int paramInt2)
  {
    return str.toString().substring(paramInt1 + offset, paramInt2 + offset);
  }
  
  public final CharBuffer subSequence(int paramInt1, int paramInt2)
  {
    try
    {
      int i = position();
      return new StringCharBuffer(str, -1, i + checkIndex(paramInt1, i), i + checkIndex(paramInt2, i), capacity(), offset);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IndexOutOfBoundsException();
    }
  }
  
  public boolean isDirect()
  {
    return false;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\StringCharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
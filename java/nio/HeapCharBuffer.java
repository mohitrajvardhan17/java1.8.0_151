package java.nio;

class HeapCharBuffer
  extends CharBuffer
{
  HeapCharBuffer(int paramInt1, int paramInt2)
  {
    super(-1, 0, paramInt2, paramInt1, new char[paramInt1], 0);
  }
  
  HeapCharBuffer(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfChar.length, paramArrayOfChar, 0);
  }
  
  protected HeapCharBuffer(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfChar, paramInt5);
  }
  
  public CharBuffer slice()
  {
    return new HeapCharBuffer(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public CharBuffer duplicate()
  {
    return new HeapCharBuffer(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public CharBuffer asReadOnlyBuffer()
  {
    return new HeapCharBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return paramInt + offset;
  }
  
  public char get()
  {
    return hb[ix(nextGetIndex())];
  }
  
  public char get(int paramInt)
  {
    return hb[ix(checkIndex(paramInt))];
  }
  
  char getUnchecked(int paramInt)
  {
    return hb[ix(paramInt)];
  }
  
  public CharBuffer get(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    System.arraycopy(hb, ix(position()), paramArrayOfChar, paramInt1, paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public boolean isDirect()
  {
    return false;
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
  
  public CharBuffer put(char paramChar)
  {
    hb[ix(nextPutIndex())] = paramChar;
    return this;
  }
  
  public CharBuffer put(int paramInt, char paramChar)
  {
    hb[ix(checkIndex(paramInt))] = paramChar;
    return this;
  }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    System.arraycopy(paramArrayOfChar, paramInt1, hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public CharBuffer put(CharBuffer paramCharBuffer)
  {
    if ((paramCharBuffer instanceof HeapCharBuffer))
    {
      if (paramCharBuffer == this) {
        throw new IllegalArgumentException();
      }
      HeapCharBuffer localHeapCharBuffer = (HeapCharBuffer)paramCharBuffer;
      int j = localHeapCharBuffer.remaining();
      if (j > remaining()) {
        throw new BufferOverflowException();
      }
      System.arraycopy(hb, localHeapCharBuffer.ix(localHeapCharBuffer.position()), hb, ix(position()), j);
      localHeapCharBuffer.position(localHeapCharBuffer.position() + j);
      position(position() + j);
    }
    else if (paramCharBuffer.isDirect())
    {
      int i = paramCharBuffer.remaining();
      if (i > remaining()) {
        throw new BufferOverflowException();
      }
      paramCharBuffer.get(hb, ix(position()), i);
      position(position() + i);
    }
    else
    {
      super.put(paramCharBuffer);
    }
    return this;
  }
  
  public CharBuffer compact()
  {
    System.arraycopy(hb, ix(position()), hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
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
    return new HeapCharBuffer(hb, -1, i + paramInt1, i + paramInt2, capacity(), offset);
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapCharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
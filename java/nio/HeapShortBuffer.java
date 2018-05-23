package java.nio;

class HeapShortBuffer
  extends ShortBuffer
{
  HeapShortBuffer(int paramInt1, int paramInt2)
  {
    super(-1, 0, paramInt2, paramInt1, new short[paramInt1], 0);
  }
  
  HeapShortBuffer(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfShort.length, paramArrayOfShort, 0);
  }
  
  protected HeapShortBuffer(short[] paramArrayOfShort, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfShort, paramInt5);
  }
  
  public ShortBuffer slice()
  {
    return new HeapShortBuffer(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public ShortBuffer duplicate()
  {
    return new HeapShortBuffer(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public ShortBuffer asReadOnlyBuffer()
  {
    return new HeapShortBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return paramInt + offset;
  }
  
  public short get()
  {
    return hb[ix(nextGetIndex())];
  }
  
  public short get(int paramInt)
  {
    return hb[ix(checkIndex(paramInt))];
  }
  
  public ShortBuffer get(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    System.arraycopy(hb, ix(position()), paramArrayOfShort, paramInt1, paramInt2);
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
  
  public ShortBuffer put(short paramShort)
  {
    hb[ix(nextPutIndex())] = paramShort;
    return this;
  }
  
  public ShortBuffer put(int paramInt, short paramShort)
  {
    hb[ix(checkIndex(paramInt))] = paramShort;
    return this;
  }
  
  public ShortBuffer put(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfShort.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    System.arraycopy(paramArrayOfShort, paramInt1, hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public ShortBuffer put(ShortBuffer paramShortBuffer)
  {
    if ((paramShortBuffer instanceof HeapShortBuffer))
    {
      if (paramShortBuffer == this) {
        throw new IllegalArgumentException();
      }
      HeapShortBuffer localHeapShortBuffer = (HeapShortBuffer)paramShortBuffer;
      int j = localHeapShortBuffer.remaining();
      if (j > remaining()) {
        throw new BufferOverflowException();
      }
      System.arraycopy(hb, localHeapShortBuffer.ix(localHeapShortBuffer.position()), hb, ix(position()), j);
      localHeapShortBuffer.position(localHeapShortBuffer.position() + j);
      position(position() + j);
    }
    else if (paramShortBuffer.isDirect())
    {
      int i = paramShortBuffer.remaining();
      if (i > remaining()) {
        throw new BufferOverflowException();
      }
      paramShortBuffer.get(hb, ix(position()), i);
      position(position() + i);
    }
    else
    {
      super.put(paramShortBuffer);
    }
    return this;
  }
  
  public ShortBuffer compact()
  {
    System.arraycopy(hb, ix(position()), hb, ix(0), remaining());
    position(remaining());
    limit(capacity());
    discardMark();
    return this;
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapShortBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
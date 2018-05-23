package java.nio;

class HeapIntBuffer
  extends IntBuffer
{
  HeapIntBuffer(int paramInt1, int paramInt2)
  {
    super(-1, 0, paramInt2, paramInt1, new int[paramInt1], 0);
  }
  
  HeapIntBuffer(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfInt.length, paramArrayOfInt, 0);
  }
  
  protected HeapIntBuffer(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5);
  }
  
  public IntBuffer slice()
  {
    return new HeapIntBuffer(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public IntBuffer duplicate()
  {
    return new HeapIntBuffer(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public IntBuffer asReadOnlyBuffer()
  {
    return new HeapIntBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return paramInt + offset;
  }
  
  public int get()
  {
    return hb[ix(nextGetIndex())];
  }
  
  public int get(int paramInt)
  {
    return hb[ix(checkIndex(paramInt))];
  }
  
  public IntBuffer get(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfInt.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    System.arraycopy(hb, ix(position()), paramArrayOfInt, paramInt1, paramInt2);
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
  
  public IntBuffer put(int paramInt)
  {
    hb[ix(nextPutIndex())] = paramInt;
    return this;
  }
  
  public IntBuffer put(int paramInt1, int paramInt2)
  {
    hb[ix(checkIndex(paramInt1))] = paramInt2;
    return this;
  }
  
  public IntBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfInt.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    System.arraycopy(paramArrayOfInt, paramInt1, hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public IntBuffer put(IntBuffer paramIntBuffer)
  {
    if ((paramIntBuffer instanceof HeapIntBuffer))
    {
      if (paramIntBuffer == this) {
        throw new IllegalArgumentException();
      }
      HeapIntBuffer localHeapIntBuffer = (HeapIntBuffer)paramIntBuffer;
      int j = localHeapIntBuffer.remaining();
      if (j > remaining()) {
        throw new BufferOverflowException();
      }
      System.arraycopy(hb, localHeapIntBuffer.ix(localHeapIntBuffer.position()), hb, ix(position()), j);
      localHeapIntBuffer.position(localHeapIntBuffer.position() + j);
      position(position() + j);
    }
    else if (paramIntBuffer.isDirect())
    {
      int i = paramIntBuffer.remaining();
      if (i > remaining()) {
        throw new BufferOverflowException();
      }
      paramIntBuffer.get(hb, ix(position()), i);
      position(position() + i);
    }
    else
    {
      super.put(paramIntBuffer);
    }
    return this;
  }
  
  public IntBuffer compact()
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapIntBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
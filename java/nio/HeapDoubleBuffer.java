package java.nio;

class HeapDoubleBuffer
  extends DoubleBuffer
{
  HeapDoubleBuffer(int paramInt1, int paramInt2)
  {
    super(-1, 0, paramInt2, paramInt1, new double[paramInt1], 0);
  }
  
  HeapDoubleBuffer(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfDouble.length, paramArrayOfDouble, 0);
  }
  
  protected HeapDoubleBuffer(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfDouble, paramInt5);
  }
  
  public DoubleBuffer slice()
  {
    return new HeapDoubleBuffer(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public DoubleBuffer duplicate()
  {
    return new HeapDoubleBuffer(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public DoubleBuffer asReadOnlyBuffer()
  {
    return new HeapDoubleBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return paramInt + offset;
  }
  
  public double get()
  {
    return hb[ix(nextGetIndex())];
  }
  
  public double get(int paramInt)
  {
    return hb[ix(checkIndex(paramInt))];
  }
  
  public DoubleBuffer get(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfDouble.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    System.arraycopy(hb, ix(position()), paramArrayOfDouble, paramInt1, paramInt2);
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
  
  public DoubleBuffer put(double paramDouble)
  {
    hb[ix(nextPutIndex())] = paramDouble;
    return this;
  }
  
  public DoubleBuffer put(int paramInt, double paramDouble)
  {
    hb[ix(checkIndex(paramInt))] = paramDouble;
    return this;
  }
  
  public DoubleBuffer put(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfDouble.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    System.arraycopy(paramArrayOfDouble, paramInt1, hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer)
  {
    if ((paramDoubleBuffer instanceof HeapDoubleBuffer))
    {
      if (paramDoubleBuffer == this) {
        throw new IllegalArgumentException();
      }
      HeapDoubleBuffer localHeapDoubleBuffer = (HeapDoubleBuffer)paramDoubleBuffer;
      int j = localHeapDoubleBuffer.remaining();
      if (j > remaining()) {
        throw new BufferOverflowException();
      }
      System.arraycopy(hb, localHeapDoubleBuffer.ix(localHeapDoubleBuffer.position()), hb, ix(position()), j);
      localHeapDoubleBuffer.position(localHeapDoubleBuffer.position() + j);
      position(position() + j);
    }
    else if (paramDoubleBuffer.isDirect())
    {
      int i = paramDoubleBuffer.remaining();
      if (i > remaining()) {
        throw new BufferOverflowException();
      }
      paramDoubleBuffer.get(hb, ix(position()), i);
      position(position() + i);
    }
    else
    {
      super.put(paramDoubleBuffer);
    }
    return this;
  }
  
  public DoubleBuffer compact()
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapDoubleBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.nio;

class HeapDoubleBufferR
  extends HeapDoubleBuffer
{
  HeapDoubleBufferR(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  HeapDoubleBufferR(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    super(paramArrayOfDouble, paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  protected HeapDoubleBufferR(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramArrayOfDouble, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    isReadOnly = true;
  }
  
  public DoubleBuffer slice()
  {
    return new HeapDoubleBufferR(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public DoubleBuffer duplicate()
  {
    return new HeapDoubleBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public DoubleBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public DoubleBuffer put(double paramDouble)
  {
    throw new ReadOnlyBufferException();
  }
  
  public DoubleBuffer put(int paramInt, double paramDouble)
  {
    throw new ReadOnlyBufferException();
  }
  
  public DoubleBuffer put(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public DoubleBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapDoubleBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.nio;

class HeapIntBufferR
  extends HeapIntBuffer
{
  HeapIntBufferR(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  HeapIntBufferR(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    super(paramArrayOfInt, paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  protected HeapIntBufferR(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    isReadOnly = true;
  }
  
  public IntBuffer slice()
  {
    return new HeapIntBufferR(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public IntBuffer duplicate()
  {
    return new HeapIntBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public IntBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public IntBuffer put(int paramInt)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer put(int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer put(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer put(IntBuffer paramIntBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapIntBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
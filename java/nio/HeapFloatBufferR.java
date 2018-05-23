package java.nio;

class HeapFloatBufferR
  extends HeapFloatBuffer
{
  HeapFloatBufferR(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  HeapFloatBufferR(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    super(paramArrayOfFloat, paramInt1, paramInt2);
    isReadOnly = true;
  }
  
  protected HeapFloatBufferR(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramArrayOfFloat, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    isReadOnly = true;
  }
  
  public FloatBuffer slice()
  {
    return new HeapFloatBufferR(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public FloatBuffer duplicate()
  {
    return new HeapFloatBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public FloatBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public boolean isReadOnly()
  {
    return true;
  }
  
  public FloatBuffer put(float paramFloat)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer put(int paramInt, float paramFloat)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer compact()
  {
    throw new ReadOnlyBufferException();
  }
  
  public ByteOrder order()
  {
    return ByteOrder.nativeOrder();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapFloatBufferR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
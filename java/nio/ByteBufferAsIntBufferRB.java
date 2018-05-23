package java.nio;

class ByteBufferAsIntBufferRB
  extends ByteBufferAsIntBufferB
{
  ByteBufferAsIntBufferRB(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }
  
  ByteBufferAsIntBufferRB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public IntBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 2) + offset;
    assert (m >= 0);
    return new ByteBufferAsIntBufferRB(bb, -1, 0, k, k, m);
  }
  
  public IntBuffer duplicate()
  {
    return new ByteBufferAsIntBufferRB(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public IntBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public IntBuffer put(int paramInt)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer put(int paramInt1, int paramInt2)
  {
    throw new ReadOnlyBufferException();
  }
  
  public IntBuffer compact()
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
  
  public ByteOrder order()
  {
    return ByteOrder.BIG_ENDIAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsIntBufferRB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
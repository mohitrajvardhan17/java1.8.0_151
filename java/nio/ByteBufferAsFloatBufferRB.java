package java.nio;

class ByteBufferAsFloatBufferRB
  extends ByteBufferAsFloatBufferB
{
  ByteBufferAsFloatBufferRB(ByteBuffer paramByteBuffer)
  {
    super(paramByteBuffer);
  }
  
  ByteBufferAsFloatBufferRB(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public FloatBuffer slice()
  {
    int i = position();
    int j = limit();
    assert (i <= j);
    int k = i <= j ? j - i : 0;
    int m = (i << 2) + offset;
    assert (m >= 0);
    return new ByteBufferAsFloatBufferRB(bb, -1, 0, k, k, m);
  }
  
  public FloatBuffer duplicate()
  {
    return new ByteBufferAsFloatBufferRB(bb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public FloatBuffer asReadOnlyBuffer()
  {
    return duplicate();
  }
  
  public FloatBuffer put(float paramFloat)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer put(int paramInt, float paramFloat)
  {
    throw new ReadOnlyBufferException();
  }
  
  public FloatBuffer compact()
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\ByteBufferAsFloatBufferRB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
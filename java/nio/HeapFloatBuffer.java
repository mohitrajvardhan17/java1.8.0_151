package java.nio;

class HeapFloatBuffer
  extends FloatBuffer
{
  HeapFloatBuffer(int paramInt1, int paramInt2)
  {
    super(-1, 0, paramInt2, paramInt1, new float[paramInt1], 0);
  }
  
  HeapFloatBuffer(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt1 + paramInt2, paramArrayOfFloat.length, paramArrayOfFloat, 0);
  }
  
  protected HeapFloatBuffer(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfFloat, paramInt5);
  }
  
  public FloatBuffer slice()
  {
    return new HeapFloatBuffer(hb, -1, 0, remaining(), remaining(), position() + offset);
  }
  
  public FloatBuffer duplicate()
  {
    return new HeapFloatBuffer(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  public FloatBuffer asReadOnlyBuffer()
  {
    return new HeapFloatBufferR(hb, markValue(), position(), limit(), capacity(), offset);
  }
  
  protected int ix(int paramInt)
  {
    return paramInt + offset;
  }
  
  public float get()
  {
    return hb[ix(nextGetIndex())];
  }
  
  public float get(int paramInt)
  {
    return hb[ix(checkIndex(paramInt))];
  }
  
  public FloatBuffer get(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining()) {
      throw new BufferUnderflowException();
    }
    System.arraycopy(hb, ix(position()), paramArrayOfFloat, paramInt1, paramInt2);
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
  
  public FloatBuffer put(float paramFloat)
  {
    hb[ix(nextPutIndex())] = paramFloat;
    return this;
  }
  
  public FloatBuffer put(int paramInt, float paramFloat)
  {
    hb[ix(checkIndex(paramInt))] = paramFloat;
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining()) {
      throw new BufferOverflowException();
    }
    System.arraycopy(paramArrayOfFloat, paramInt1, hb, ix(position()), paramInt2);
    position(position() + paramInt2);
    return this;
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer)
  {
    if ((paramFloatBuffer instanceof HeapFloatBuffer))
    {
      if (paramFloatBuffer == this) {
        throw new IllegalArgumentException();
      }
      HeapFloatBuffer localHeapFloatBuffer = (HeapFloatBuffer)paramFloatBuffer;
      int j = localHeapFloatBuffer.remaining();
      if (j > remaining()) {
        throw new BufferOverflowException();
      }
      System.arraycopy(hb, localHeapFloatBuffer.ix(localHeapFloatBuffer.position()), hb, ix(position()), j);
      localHeapFloatBuffer.position(localHeapFloatBuffer.position() + j);
      position(position() + j);
    }
    else if (paramFloatBuffer.isDirect())
    {
      int i = paramFloatBuffer.remaining();
      if (i > remaining()) {
        throw new BufferOverflowException();
      }
      paramFloatBuffer.get(hb, ix(position()), i);
      position(position() + i);
    }
    else
    {
      super.put(paramFloatBuffer);
    }
    return this;
  }
  
  public FloatBuffer compact()
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\HeapFloatBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
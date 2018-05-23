package com.sun.corba.se.impl.ior;

public class ByteBuffer
{
  protected byte[] elementData;
  protected int elementCount;
  protected int capacityIncrement;
  
  public ByteBuffer(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt1);
    }
    elementData = new byte[paramInt1];
    capacityIncrement = paramInt2;
  }
  
  public ByteBuffer(int paramInt)
  {
    this(paramInt, 0);
  }
  
  public ByteBuffer()
  {
    this(200);
  }
  
  public void trimToSize()
  {
    int i = elementData.length;
    if (elementCount < i)
    {
      byte[] arrayOfByte = elementData;
      elementData = new byte[elementCount];
      System.arraycopy(arrayOfByte, 0, elementData, 0, elementCount);
    }
  }
  
  private void ensureCapacityHelper(int paramInt)
  {
    int i = elementData.length;
    if (paramInt > i)
    {
      byte[] arrayOfByte = elementData;
      int j = capacityIncrement > 0 ? i + capacityIncrement : i * 2;
      if (j < paramInt) {
        j = paramInt;
      }
      elementData = new byte[j];
      System.arraycopy(arrayOfByte, 0, elementData, 0, elementCount);
    }
  }
  
  public int capacity()
  {
    return elementData.length;
  }
  
  public int size()
  {
    return elementCount;
  }
  
  public boolean isEmpty()
  {
    return elementCount == 0;
  }
  
  public void append(byte paramByte)
  {
    ensureCapacityHelper(elementCount + 1);
    elementData[(elementCount++)] = paramByte;
  }
  
  public void append(int paramInt)
  {
    ensureCapacityHelper(elementCount + 4);
    doAppend(paramInt);
  }
  
  private void doAppend(int paramInt)
  {
    int i = paramInt;
    for (int j = 0; j < 4; j++)
    {
      elementData[(elementCount + j)] = ((byte)(i & 0xFF));
      i >>= 8;
    }
    elementCount += 4;
  }
  
  public void append(String paramString)
  {
    byte[] arrayOfByte = paramString.getBytes();
    ensureCapacityHelper(elementCount + arrayOfByte.length + 4);
    doAppend(arrayOfByte.length);
    System.arraycopy(arrayOfByte, 0, elementData, elementCount, arrayOfByte.length);
    elementCount += arrayOfByte.length;
  }
  
  public byte[] toArray()
  {
    return elementData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\ByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
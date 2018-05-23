package sun.reflect;

class ByteVectorImpl
  implements ByteVector
{
  private byte[] data;
  private int pos;
  
  public ByteVectorImpl()
  {
    this(100);
  }
  
  public ByteVectorImpl(int paramInt)
  {
    data = new byte[paramInt];
    pos = -1;
  }
  
  public int getLength()
  {
    return pos + 1;
  }
  
  public byte get(int paramInt)
  {
    if (paramInt >= data.length)
    {
      resize(paramInt);
      pos = paramInt;
    }
    return data[paramInt];
  }
  
  public void put(int paramInt, byte paramByte)
  {
    if (paramInt >= data.length)
    {
      resize(paramInt);
      pos = paramInt;
    }
    data[paramInt] = paramByte;
  }
  
  public void add(byte paramByte)
  {
    if (++pos >= data.length) {
      resize(pos);
    }
    data[pos] = paramByte;
  }
  
  public void trim()
  {
    if (pos != data.length - 1)
    {
      byte[] arrayOfByte = new byte[pos + 1];
      System.arraycopy(data, 0, arrayOfByte, 0, pos + 1);
      data = arrayOfByte;
    }
  }
  
  public byte[] getData()
  {
    return data;
  }
  
  private void resize(int paramInt)
  {
    if (paramInt <= 2 * data.length) {
      paramInt = 2 * data.length;
    }
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(data, 0, arrayOfByte, 0, data.length);
    data = arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\ByteVectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.imageio.stream;

public class IIOByteBuffer
{
  private byte[] data;
  private int offset;
  private int length;
  
  public IIOByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    data = paramArrayOfByte;
    offset = paramInt1;
    length = paramInt2;
  }
  
  public byte[] getData()
  {
    return data;
  }
  
  public void setData(byte[] paramArrayOfByte)
  {
    data = paramArrayOfByte;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public void setOffset(int paramInt)
  {
    offset = paramInt;
  }
  
  public int getLength()
  {
    return length;
  }
  
  public void setLength(int paramInt)
  {
    length = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\IIOByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
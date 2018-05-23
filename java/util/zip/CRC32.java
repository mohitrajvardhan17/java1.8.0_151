package java.util.zip;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

public class CRC32
  implements Checksum
{
  private int crc;
  
  public CRC32() {}
  
  public void update(int paramInt)
  {
    crc = update(crc, paramInt);
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    crc = updateBytes(crc, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void update(byte[] paramArrayOfByte)
  {
    crc = updateBytes(crc, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void update(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.position();
    int j = paramByteBuffer.limit();
    assert (i <= j);
    int k = j - i;
    if (k <= 0) {
      return;
    }
    if ((paramByteBuffer instanceof DirectBuffer))
    {
      crc = updateByteBuffer(crc, ((DirectBuffer)paramByteBuffer).address(), i, k);
    }
    else if (paramByteBuffer.hasArray())
    {
      crc = updateBytes(crc, paramByteBuffer.array(), i + paramByteBuffer.arrayOffset(), k);
    }
    else
    {
      byte[] arrayOfByte = new byte[k];
      paramByteBuffer.get(arrayOfByte);
      crc = updateBytes(crc, arrayOfByte, 0, arrayOfByte.length);
    }
    paramByteBuffer.position(j);
  }
  
  public void reset()
  {
    crc = 0;
  }
  
  public long getValue()
  {
    return crc & 0xFFFFFFFF;
  }
  
  private static native int update(int paramInt1, int paramInt2);
  
  private static native int updateBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native int updateByteBuffer(int paramInt1, long paramLong, int paramInt2, int paramInt3);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\CRC32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
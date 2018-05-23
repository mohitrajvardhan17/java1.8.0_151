package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UCEncoder
  extends CharacterEncoder
{
  private static final byte[] map_array = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 40, 41 };
  private int sequence;
  private byte[] tmp = new byte[2];
  private CRC16 crc = new CRC16();
  
  public UCEncoder() {}
  
  protected int bytesPerAtom()
  {
    return 2;
  }
  
  protected int bytesPerLine()
  {
    return 48;
  }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int n = paramArrayOfByte[paramInt1];
    int i1;
    if (paramInt2 == 2) {
      i1 = paramArrayOfByte[(paramInt1 + 1)];
    } else {
      i1 = 0;
    }
    crc.update(n);
    if (paramInt2 == 2) {
      crc.update(i1);
    }
    paramOutputStream.write(map_array[((n >>> 2 & 0x38) + (i1 >>> 5 & 0x7))]);
    int k = 0;
    int m = 0;
    int i = 1;
    while (i < 256)
    {
      if ((n & i) != 0) {
        k++;
      }
      if ((i1 & i) != 0) {
        m++;
      }
      int j;
      i *= 2;
    }
    k = (k & 0x1) * 32;
    m = (m & 0x1) * 32;
    paramOutputStream.write(map_array[((n & 0x1F) + k)]);
    paramOutputStream.write(map_array[((i1 & 0x1F) + m)]);
  }
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write(42);
    crc.value = 0;
    tmp[0] = ((byte)paramInt);
    tmp[1] = ((byte)sequence);
    sequence = (sequence + 1 & 0xFF);
    encodeAtom(paramOutputStream, tmp, 0, 2);
  }
  
  protected void encodeLineSuffix(OutputStream paramOutputStream)
    throws IOException
  {
    tmp[0] = ((byte)(crc.value >>> 8 & 0xFF));
    tmp[1] = ((byte)(crc.value & 0xFF));
    encodeAtom(paramOutputStream, tmp, 0, 2);
    pStream.println();
  }
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream)
    throws IOException
  {
    sequence = 0;
    super.encodeBufferPrefix(paramOutputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\UCEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UCDecoder
  extends CharacterDecoder
{
  private static final byte[] map_array = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 40, 41 };
  private int sequence;
  private byte[] tmp = new byte[2];
  private CRC16 crc = new CRC16();
  private ByteArrayOutputStream lineAndSeq = new ByteArrayOutputStream(2);
  
  public UCDecoder() {}
  
  protected int bytesPerAtom()
  {
    return 2;
  }
  
  protected int bytesPerLine()
  {
    return 48;
  }
  
  protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    byte[] arrayOfByte = new byte[3];
    int i = paramPushbackInputStream.read(arrayOfByte);
    if (i != 3) {
      throw new CEStreamExhausted();
    }
    for (i = 0; (i < 64) && ((i1 == -1) || (i2 == -1) || (i3 == -1)); i++)
    {
      if (arrayOfByte[0] == map_array[i]) {
        i1 = (byte)i;
      }
      if (arrayOfByte[1] == map_array[i]) {
        i2 = (byte)i;
      }
      if (arrayOfByte[2] == map_array[i]) {
        i3 = (byte)i;
      }
    }
    int i4 = (byte)(((i1 & 0x38) << 2) + (i2 & 0x1F));
    int i5 = (byte)(((i1 & 0x7) << 5) + (i3 & 0x1F));
    int j = 0;
    int k = 0;
    i = 1;
    while (i < 256)
    {
      if ((i4 & i) != 0) {
        j++;
      }
      if ((i5 & i) != 0) {
        k++;
      }
      i *= 2;
    }
    int m = (i2 & 0x20) / 32;
    int n = (i3 & 0x20) / 32;
    if ((j & 0x1) != m) {
      throw new CEFormatException("UCDecoder: High byte parity error.");
    }
    if ((k & 0x1) != n) {
      throw new CEFormatException("UCDecoder: Low byte parity error.");
    }
    paramOutputStream.write(i4);
    crc.update(i4);
    if (paramInt == 2)
    {
      paramOutputStream.write(i5);
      crc.update(i5);
    }
  }
  
  protected void decodeBufferPrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
  {
    sequence = 0;
  }
  
  protected int decodeLinePrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    crc.value = 0;
    for (;;)
    {
      int k = paramPushbackInputStream.read(tmp, 0, 1);
      if (k == -1) {
        throw new CEStreamExhausted();
      }
      if (tmp[0] == 42) {
        break;
      }
    }
    lineAndSeq.reset();
    decodeAtom(paramPushbackInputStream, lineAndSeq, 2);
    byte[] arrayOfByte = lineAndSeq.toByteArray();
    int i = arrayOfByte[0] & 0xFF;
    int j = arrayOfByte[1] & 0xFF;
    if (j != sequence) {
      throw new CEFormatException("UCDecoder: Out of sequence line.");
    }
    sequence = (sequence + 1 & 0xFF);
    return i;
  }
  
  protected void decodeLineSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    int i = crc.value;
    lineAndSeq.reset();
    decodeAtom(paramPushbackInputStream, lineAndSeq, 2);
    byte[] arrayOfByte = lineAndSeq.toByteArray();
    int j = (arrayOfByte[0] << 8 & 0xFF00) + (arrayOfByte[1] & 0xFF);
    if (j != i) {
      throw new CEFormatException("UCDecoder: CRC check failed.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\UCDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UUDecoder
  extends CharacterDecoder
{
  public String bufferName;
  public int mode;
  private byte[] decoderBuffer = new byte[4];
  
  public UUDecoder() {}
  
  protected int bytesPerAtom()
  {
    return 3;
  }
  
  protected int bytesPerLine()
  {
    return 45;
  }
  
  protected void decodeAtom(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < 4; i++)
    {
      int j = paramPushbackInputStream.read();
      if (j == -1) {
        throw new CEStreamExhausted();
      }
      localStringBuffer.append((char)j);
      decoderBuffer[i] = ((byte)(j - 32 & 0x3F));
    }
    int k = decoderBuffer[0] << 2 & 0xFC | decoderBuffer[1] >>> 4 & 0x3;
    int m = decoderBuffer[1] << 4 & 0xF0 | decoderBuffer[2] >>> 2 & 0xF;
    int n = decoderBuffer[2] << 6 & 0xC0 | decoderBuffer[3] & 0x3F;
    paramOutputStream.write((byte)(k & 0xFF));
    if (paramInt > 1) {
      paramOutputStream.write((byte)(m & 0xFF));
    }
    if (paramInt > 2) {
      paramOutputStream.write((byte)(n & 0xFF));
    }
  }
  
  protected void decodeBufferPrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer(32);
    int i;
    for (int j = 1;; j = (i == 10) || (i == 13) ? 1 : 0)
    {
      i = paramPushbackInputStream.read();
      if (i == -1) {
        throw new CEFormatException("UUDecoder: No begin line.");
      }
      if ((i == 98) && (j != 0))
      {
        i = paramPushbackInputStream.read();
        if (i == 101) {
          break;
        }
      }
    }
    while ((i != 10) && (i != 13))
    {
      i = paramPushbackInputStream.read();
      if (i == -1) {
        throw new CEFormatException("UUDecoder: No begin line.");
      }
      if ((i != 10) && (i != 13)) {
        localStringBuffer.append((char)i);
      }
    }
    String str = localStringBuffer.toString();
    if (str.indexOf(' ') != 3) {
      throw new CEFormatException("UUDecoder: Malformed begin line.");
    }
    mode = Integer.parseInt(str.substring(4, 7));
    bufferName = str.substring(str.indexOf(' ', 6) + 1);
    if (i == 13)
    {
      i = paramPushbackInputStream.read();
      if ((i != 10) && (i != -1)) {
        paramPushbackInputStream.unread(i);
      }
    }
  }
  
  protected int decodeLinePrefix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramPushbackInputStream.read();
    if (i == 32)
    {
      i = paramPushbackInputStream.read();
      i = paramPushbackInputStream.read();
      if ((i != 10) && (i != -1)) {
        paramPushbackInputStream.unread(i);
      }
      throw new CEStreamExhausted();
    }
    if (i == -1) {
      throw new CEFormatException("UUDecoder: Short Buffer.");
    }
    i = i - 32 & 0x3F;
    if (i > bytesPerLine()) {
      throw new CEFormatException("UUDecoder: Bad Line Length.");
    }
    return i;
  }
  
  protected void decodeLineSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    do
    {
      i = paramPushbackInputStream.read();
      if (i == -1) {
        throw new CEStreamExhausted();
      }
      if (i == 10) {
        break;
      }
    } while (i != 13);
    int i = paramPushbackInputStream.read();
    if ((i != 10) && (i != -1)) {
      paramPushbackInputStream.unread(i);
    }
  }
  
  protected void decodeBufferSuffix(PushbackInputStream paramPushbackInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramPushbackInputStream.read(decoderBuffer);
    if ((decoderBuffer[0] != 101) || (decoderBuffer[1] != 110) || (decoderBuffer[2] != 100)) {
      throw new CEFormatException("UUDecoder: Missing 'end' line.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\UUDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
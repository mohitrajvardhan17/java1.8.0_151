package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoder
  extends CharacterEncoder
{
  private String bufferName;
  private int mode;
  
  public UUEncoder()
  {
    bufferName = "encoder.buf";
    mode = 644;
  }
  
  public UUEncoder(String paramString)
  {
    bufferName = paramString;
    mode = 644;
  }
  
  public UUEncoder(String paramString, int paramInt)
  {
    bufferName = paramString;
    mode = paramInt;
  }
  
  protected int bytesPerAtom()
  {
    return 3;
  }
  
  protected int bytesPerLine()
  {
    return 45;
  }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int j = 1;
    int k = 1;
    int i = paramArrayOfByte[paramInt1];
    if (paramInt2 > 1) {
      j = paramArrayOfByte[(paramInt1 + 1)];
    }
    if (paramInt2 > 2) {
      k = paramArrayOfByte[(paramInt1 + 2)];
    }
    int m = i >>> 2 & 0x3F;
    int n = i << 4 & 0x30 | j >>> 4 & 0xF;
    int i1 = j << 2 & 0x3C | k >>> 6 & 0x3;
    int i2 = k & 0x3F;
    paramOutputStream.write(m + 32);
    paramOutputStream.write(n + 32);
    paramOutputStream.write(i1 + 32);
    paramOutputStream.write(i2 + 32);
  }
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write((paramInt & 0x3F) + 32);
  }
  
  protected void encodeLineSuffix(OutputStream paramOutputStream)
    throws IOException
  {
    pStream.println();
  }
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream)
    throws IOException
  {
    pStream = new PrintStream(paramOutputStream);
    pStream.print("begin " + mode + " ");
    if (bufferName != null) {
      pStream.println(bufferName);
    } else {
      pStream.println("encoder.bin");
    }
    pStream.flush();
  }
  
  protected void encodeBufferSuffix(OutputStream paramOutputStream)
    throws IOException
  {
    pStream.println(" \nend");
    pStream.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\UUEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HexDumpEncoder
  extends CharacterEncoder
{
  private int offset;
  private int thisLineLength;
  private int currentByte;
  private byte[] thisLine = new byte[16];
  
  public HexDumpEncoder() {}
  
  static void hexDigit(PrintStream paramPrintStream, byte paramByte)
  {
    int i = (char)(paramByte >> 4 & 0xF);
    if (i > 9) {
      i = (char)(i - 10 + 65);
    } else {
      i = (char)(i + 48);
    }
    paramPrintStream.write(i);
    i = (char)(paramByte & 0xF);
    if (i > 9) {
      i = (char)(i - 10 + 65);
    } else {
      i = (char)(i + 48);
    }
    paramPrintStream.write(i);
  }
  
  protected int bytesPerAtom()
  {
    return 1;
  }
  
  protected int bytesPerLine()
  {
    return 16;
  }
  
  protected void encodeBufferPrefix(OutputStream paramOutputStream)
    throws IOException
  {
    offset = 0;
    super.encodeBufferPrefix(paramOutputStream);
  }
  
  protected void encodeLinePrefix(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    hexDigit(pStream, (byte)(offset >>> 8 & 0xFF));
    hexDigit(pStream, (byte)(offset & 0xFF));
    pStream.print(": ");
    currentByte = 0;
    thisLineLength = paramInt;
  }
  
  protected void encodeAtom(OutputStream paramOutputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    thisLine[currentByte] = paramArrayOfByte[paramInt1];
    hexDigit(pStream, paramArrayOfByte[paramInt1]);
    pStream.print(" ");
    currentByte += 1;
    if (currentByte == 8) {
      pStream.print("  ");
    }
  }
  
  protected void encodeLineSuffix(OutputStream paramOutputStream)
    throws IOException
  {
    if (thisLineLength < 16) {
      for (i = thisLineLength; i < 16; i++)
      {
        pStream.print("   ");
        if (i == 7) {
          pStream.print("  ");
        }
      }
    }
    pStream.print(" ");
    for (int i = 0; i < thisLineLength; i++) {
      if ((thisLine[i] < 32) || (thisLine[i] > 122)) {
        pStream.print(".");
      } else {
        pStream.write(thisLine[i]);
      }
    }
    pStream.println();
    offset += thisLineLength;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\HexDumpEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
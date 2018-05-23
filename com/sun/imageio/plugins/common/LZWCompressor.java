package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.stream.ImageOutputStream;

public class LZWCompressor
{
  int codeSize;
  int clearCode;
  int endOfInfo;
  int numBits;
  int limit;
  short prefix;
  BitFile bf;
  LZWStringTable lzss;
  boolean tiffFudge;
  
  public LZWCompressor(ImageOutputStream paramImageOutputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    bf = new BitFile(paramImageOutputStream, !paramBoolean);
    codeSize = paramInt;
    tiffFudge = paramBoolean;
    clearCode = (1 << paramInt);
    endOfInfo = (clearCode + 1);
    numBits = (paramInt + 1);
    limit = ((1 << numBits) - 1);
    if (tiffFudge) {
      limit -= 1;
    }
    prefix = -1;
    lzss = new LZWStringTable();
    lzss.clearTable(paramInt);
    bf.writeBits(clearCode, numBits);
  }
  
  public void compress(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int j = paramInt1 + paramInt2;
    for (int i = paramInt1; i < j; i++)
    {
      byte b = paramArrayOfByte[i];
      short s;
      if ((s = lzss.findCharString(prefix, b)) != -1)
      {
        prefix = s;
      }
      else
      {
        bf.writeBits(prefix, numBits);
        if (lzss.addCharString(prefix, b) > limit)
        {
          if (numBits == 12)
          {
            bf.writeBits(clearCode, numBits);
            lzss.clearTable(codeSize);
            numBits = (codeSize + 1);
          }
          else
          {
            numBits += 1;
          }
          limit = ((1 << numBits) - 1);
          if (tiffFudge) {
            limit -= 1;
          }
        }
        prefix = ((short)((short)b & 0xFF));
      }
    }
  }
  
  public void flush()
    throws IOException
  {
    if (prefix != -1) {
      bf.writeBits(prefix, numBits);
    }
    bf.writeBits(endOfInfo, numBits);
    bf.flush();
  }
  
  public void dump(PrintStream paramPrintStream)
  {
    lzss.dump(paramPrintStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\LZWCompressor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
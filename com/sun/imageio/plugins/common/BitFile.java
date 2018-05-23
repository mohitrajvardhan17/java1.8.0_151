package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class BitFile
{
  ImageOutputStream output;
  byte[] buffer;
  int index;
  int bitsLeft;
  boolean blocks = false;
  
  public BitFile(ImageOutputStream paramImageOutputStream, boolean paramBoolean)
  {
    output = paramImageOutputStream;
    blocks = paramBoolean;
    buffer = new byte['Ā'];
    index = 0;
    bitsLeft = 8;
  }
  
  public void flush()
    throws IOException
  {
    int i = index + (bitsLeft == 8 ? 0 : 1);
    if (i > 0)
    {
      if (blocks) {
        output.write(i);
      }
      output.write(buffer, 0, i);
      buffer[0] = 0;
      index = 0;
      bitsLeft = 8;
    }
  }
  
  public void writeBits(int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j = 255;
    do
    {
      if (((index == 254) && (bitsLeft == 0)) || (index > 254))
      {
        if (blocks) {
          output.write(j);
        }
        output.write(buffer, 0, j);
        buffer[0] = 0;
        index = 0;
        bitsLeft = 8;
      }
      if (paramInt2 <= bitsLeft)
      {
        if (blocks)
        {
          int tmp109_106 = index;
          byte[] tmp109_102 = buffer;
          tmp109_102[tmp109_106] = ((byte)(tmp109_102[tmp109_106] | (paramInt1 & (1 << paramInt2) - 1) << 8 - bitsLeft));
          i += paramInt2;
          bitsLeft -= paramInt2;
          paramInt2 = 0;
        }
        else
        {
          int tmp156_153 = index;
          byte[] tmp156_149 = buffer;
          tmp156_149[tmp156_153] = ((byte)(tmp156_149[tmp156_153] | (paramInt1 & (1 << paramInt2) - 1) << bitsLeft - paramInt2));
          i += paramInt2;
          bitsLeft -= paramInt2;
          paramInt2 = 0;
        }
      }
      else if (blocks)
      {
        int tmp209_206 = index;
        byte[] tmp209_202 = buffer;
        tmp209_202[tmp209_206] = ((byte)(tmp209_202[tmp209_206] | (paramInt1 & (1 << bitsLeft) - 1) << 8 - bitsLeft));
        i += bitsLeft;
        paramInt1 >>= bitsLeft;
        paramInt2 -= bitsLeft;
        buffer[(++index)] = 0;
        bitsLeft = 8;
      }
      else
      {
        int k = paramInt1 >>> paramInt2 - bitsLeft & (1 << bitsLeft) - 1;
        int tmp306_303 = index;
        byte[] tmp306_299 = buffer;
        tmp306_299[tmp306_303] = ((byte)(tmp306_299[tmp306_303] | k));
        paramInt2 -= bitsLeft;
        i += bitsLeft;
        buffer[(++index)] = 0;
        bitsLeft = 8;
      }
    } while (paramInt2 != 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\BitFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
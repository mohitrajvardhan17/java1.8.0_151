package com.sun.imageio.plugins.common;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class ReaderUtil
{
  public ReaderUtil() {}
  
  private static void computeUpdatedPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10)
  {
    int i = 0;
    int j = -1;
    int k = -1;
    int m = -1;
    for (int n = 0; n < paramInt8; n++)
    {
      int i1 = paramInt7 + n * paramInt9;
      if ((i1 >= paramInt1) && ((i1 - paramInt1) % paramInt6 == 0))
      {
        if (i1 >= paramInt1 + paramInt2) {
          break;
        }
        int i2 = paramInt3 + (i1 - paramInt1) / paramInt6;
        if (i2 >= paramInt4)
        {
          if (i2 > paramInt5) {
            break;
          }
          if (i == 0)
          {
            j = i2;
            i = 1;
          }
          else if (k == -1)
          {
            k = i2;
          }
          m = i2;
        }
      }
    }
    paramArrayOfInt[paramInt10] = j;
    if (i == 0) {
      paramArrayOfInt[(paramInt10 + 2)] = 0;
    } else {
      paramArrayOfInt[(paramInt10 + 2)] = (m - j + 1);
    }
    paramArrayOfInt[(paramInt10 + 4)] = Math.max(k - j, 1);
  }
  
  public static int[] computeUpdatedPixels(Rectangle paramRectangle, Point paramPoint, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12)
  {
    int[] arrayOfInt = new int[6];
    computeUpdatedPixels(x, width, x, paramInt1, paramInt3, paramInt5, paramInt7, paramInt9, paramInt11, arrayOfInt, 0);
    computeUpdatedPixels(y, height, y, paramInt2, paramInt4, paramInt6, paramInt8, paramInt10, paramInt12, arrayOfInt, 1);
    return arrayOfInt;
  }
  
  public static int readMultiByteInteger(ImageInputStream paramImageInputStream)
    throws IOException
  {
    int i = paramImageInputStream.readByte();
    int j = i & 0x7F;
    while ((i & 0x80) == 128)
    {
      j <<= 7;
      i = paramImageInputStream.readByte();
      j |= i & 0x7F;
    }
    return j;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\ReaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
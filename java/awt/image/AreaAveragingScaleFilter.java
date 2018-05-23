package java.awt.image;

public class AreaAveragingScaleFilter
  extends ReplicateScaleFilter
{
  private static final ColorModel rgbmodel = ;
  private static final int neededHints = 6;
  private boolean passthrough;
  private float[] reds;
  private float[] greens;
  private float[] blues;
  private float[] alphas;
  private int savedy;
  private int savedyrem;
  
  public AreaAveragingScaleFilter(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  
  public void setHints(int paramInt)
  {
    passthrough = ((paramInt & 0x6) != 6);
    super.setHints(paramInt);
  }
  
  private void makeAccumBuffers()
  {
    reds = new float[destWidth];
    greens = new float[destWidth];
    blues = new float[destWidth];
    alphas = new float[destWidth];
  }
  
  private int[] calcRow()
  {
    float f1 = srcWidth * srcHeight;
    if ((outpixbuf == null) || (!(outpixbuf instanceof int[]))) {
      outpixbuf = new int[destWidth];
    }
    int[] arrayOfInt = (int[])outpixbuf;
    for (int i = 0; i < destWidth; i++)
    {
      float f2 = f1;
      int j = Math.round(alphas[i] / f2);
      if (j <= 0) {
        j = 0;
      } else if (j >= 255) {
        j = 255;
      } else {
        f2 = alphas[i] / 255.0F;
      }
      int k = Math.round(reds[i] / f2);
      int m = Math.round(greens[i] / f2);
      int n = Math.round(blues[i] / f2);
      if (k < 0) {
        k = 0;
      } else if (k > 255) {
        k = 255;
      }
      if (m < 0) {
        m = 0;
      } else if (m > 255) {
        m = 255;
      }
      if (n < 0) {
        n = 0;
      } else if (n > 255) {
        n = 255;
      }
      arrayOfInt[i] = (j << 24 | k << 16 | m << 8 | n);
    }
    return arrayOfInt;
  }
  
  private void accumPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, Object paramObject, int paramInt5, int paramInt6)
  {
    if (reds == null) {
      makeAccumBuffers();
    }
    int i = paramInt2;
    int j = destHeight;
    int k;
    int m;
    if (i == 0)
    {
      k = 0;
      m = 0;
    }
    else
    {
      k = savedy;
      m = savedyrem;
    }
    while (i < paramInt2 + paramInt4)
    {
      if (m == 0)
      {
        for (i1 = 0; i1 < destWidth; i1++) {
          alphas[i1] = (reds[i1] = greens[i1] = blues[i1] = 0.0F);
        }
        m = srcHeight;
      }
      int n;
      if (j < m) {
        n = j;
      } else {
        n = m;
      }
      int i1 = 0;
      int i2 = 0;
      int i3 = 0;
      int i4 = srcWidth;
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      while (i1 < paramInt3)
      {
        int i5;
        if (i3 == 0)
        {
          i3 = destWidth;
          if ((paramObject instanceof byte[])) {
            i5 = ((byte[])(byte[])paramObject)[(paramInt5 + i1)] & 0xFF;
          } else {
            i5 = ((int[])(int[])paramObject)[(paramInt5 + i1)];
          }
          i5 = paramColorModel.getRGB(i5);
          f1 = i5 >>> 24;
          f2 = i5 >> 16 & 0xFF;
          f3 = i5 >> 8 & 0xFF;
          f4 = i5 & 0xFF;
          if (f1 != 255.0F)
          {
            f5 = f1 / 255.0F;
            f2 *= f5;
            f3 *= f5;
            f4 *= f5;
          }
        }
        if (i3 < i4) {
          i5 = i3;
        } else {
          i5 = i4;
        }
        float f5 = i5 * n;
        alphas[i2] += f5 * f1;
        reds[i2] += f5 * f2;
        greens[i2] += f5 * f3;
        blues[i2] += f5 * f4;
        if (i3 -= i5 == 0) {
          i1++;
        }
        if (i4 -= i5 == 0)
        {
          i2++;
          i4 = srcWidth;
        }
      }
      if (m -= n == 0)
      {
        int[] arrayOfInt = calcRow();
        do
        {
          consumer.setPixels(0, k, destWidth, 1, rgbmodel, arrayOfInt, 0, destWidth);
          k++;
        } while ((j -= n >= n) && (n == srcHeight));
      }
      else
      {
        j -= n;
      }
      if (j == 0)
      {
        j = destHeight;
        i++;
        paramInt5 += paramInt6;
      }
    }
    savedyrem = m;
    savedy = k;
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    if (passthrough) {
      super.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
    } else {
      accumPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, paramInt5, paramInt6);
    }
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if (passthrough) {
      super.setPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6);
    } else {
      accumPixels(paramInt1, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfInt, paramInt5, paramInt6);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\AreaAveragingScaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
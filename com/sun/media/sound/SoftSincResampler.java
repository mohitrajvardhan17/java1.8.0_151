package com.sun.media.sound;

public final class SoftSincResampler
  extends SoftAbstractResampler
{
  float[][][] sinc_table = new float[sinc_scale_size][sinc_table_fsize][];
  int sinc_scale_size = 100;
  int sinc_table_fsize = 800;
  int sinc_table_size = 30;
  int sinc_table_center = sinc_table_size / 2;
  
  public SoftSincResampler()
  {
    for (int i = 0; i < sinc_scale_size; i++)
    {
      float f = (float)(1.0D / (1.0D + Math.pow(i, 1.1D) / 10.0D));
      for (int j = 0; j < sinc_table_fsize; j++) {
        sinc_table[i][j] = sincTable(sinc_table_size, -j / sinc_table_fsize, f);
      }
    }
  }
  
  public static double sinc(double paramDouble)
  {
    return paramDouble == 0.0D ? 1.0D : Math.sin(3.141592653589793D * paramDouble) / (3.141592653589793D * paramDouble);
  }
  
  public static float[] wHanning(int paramInt, float paramFloat)
  {
    float[] arrayOfFloat = new float[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfFloat[i] = ((float)(-0.5D * Math.cos(6.283185307179586D * (i + paramFloat) / paramInt) + 0.5D));
    }
    return arrayOfFloat;
  }
  
  public static float[] sincTable(int paramInt, float paramFloat1, float paramFloat2)
  {
    int i = paramInt / 2;
    float[] arrayOfFloat = wHanning(paramInt, paramFloat1);
    for (int j = 0; j < paramInt; j++)
    {
      int tmp24_22 = j;
      float[] tmp24_20 = arrayOfFloat;
      tmp24_20[tmp24_22] = ((float)(tmp24_20[tmp24_22] * (sinc((-i + j + paramFloat1) * paramFloat2) * paramFloat2)));
    }
    return arrayOfFloat;
  }
  
  public int getPadding()
  {
    return sinc_table_size / 2 + 2;
  }
  
  public void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt)
  {
    float f1 = paramArrayOfFloat3[0];
    float f2 = paramArrayOfFloat2[0];
    int i = paramArrayOfInt[0];
    float f3 = paramFloat1;
    int j = paramInt;
    int k = sinc_scale_size - 1;
    int m;
    float[] arrayOfFloat;
    int i2;
    float f4;
    int i3;
    if (paramFloat2 == 0.0F)
    {
      m = (int)((f1 - 1.0F) * 10.0F);
      if (m < 0) {
        m = 0;
      } else if (m > k) {
        m = k;
      }
      float[][] arrayOfFloat1 = sinc_table[m];
      while ((f2 < f3) && (i < j))
      {
        int i1 = (int)f2;
        arrayOfFloat = arrayOfFloat1[((int)((f2 - i1) * sinc_table_fsize))];
        i2 = i1 - sinc_table_center;
        f4 = 0.0F;
        i3 = 0;
        while (i3 < sinc_table_size)
        {
          f4 += paramArrayOfFloat1[i2] * arrayOfFloat[i3];
          i3++;
          i2++;
        }
        paramArrayOfFloat4[(i++)] = f4;
        f2 += f1;
      }
    }
    else
    {
      while ((f2 < f3) && (i < j))
      {
        m = (int)f2;
        int n = (int)((f1 - 1.0F) * 10.0F);
        if (n < 0) {
          n = 0;
        } else if (n > k) {
          n = k;
        }
        float[][] arrayOfFloat2 = sinc_table[n];
        arrayOfFloat = arrayOfFloat2[((int)((f2 - m) * sinc_table_fsize))];
        i2 = m - sinc_table_center;
        f4 = 0.0F;
        i3 = 0;
        while (i3 < sinc_table_size)
        {
          f4 += paramArrayOfFloat1[i2] * arrayOfFloat[i3];
          i3++;
          i2++;
        }
        paramArrayOfFloat4[(i++)] = f4;
        f2 += f1;
        f1 += paramFloat2;
      }
    }
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftSincResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
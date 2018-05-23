package com.sun.media.sound;

public final class SoftLanczosResampler
  extends SoftAbstractResampler
{
  float[][] sinc_table = new float[sinc_table_fsize][];
  int sinc_table_fsize = 2000;
  int sinc_table_size = 5;
  int sinc_table_center = sinc_table_size / 2;
  
  public SoftLanczosResampler()
  {
    for (int i = 0; i < sinc_table_fsize; i++) {
      sinc_table[i] = sincTable(sinc_table_size, -i / sinc_table_fsize);
    }
  }
  
  public static double sinc(double paramDouble)
  {
    return paramDouble == 0.0D ? 1.0D : Math.sin(3.141592653589793D * paramDouble) / (3.141592653589793D * paramDouble);
  }
  
  public static float[] sincTable(int paramInt, float paramFloat)
  {
    int i = paramInt / 2;
    float[] arrayOfFloat = new float[paramInt];
    for (int j = 0; j < paramInt; j++)
    {
      float f = -i + j + paramFloat;
      if ((f < -2.0F) || (f > 2.0F)) {
        arrayOfFloat[j] = 0.0F;
      } else if (f == 0.0F) {
        arrayOfFloat[j] = 1.0F;
      } else {
        arrayOfFloat[j] = ((float)(2.0D * Math.sin(3.141592653589793D * f) * Math.sin(3.141592653589793D * f / 2.0D) / (3.141592653589793D * f * (3.141592653589793D * f))));
      }
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
    int k;
    float[] arrayOfFloat;
    int m;
    float f4;
    int n;
    if (paramFloat2 == 0.0F) {
      while ((f2 < f3) && (i < j))
      {
        k = (int)f2;
        arrayOfFloat = sinc_table[((int)((f2 - k) * sinc_table_fsize))];
        m = k - sinc_table_center;
        f4 = 0.0F;
        n = 0;
        while (n < sinc_table_size)
        {
          f4 += paramArrayOfFloat1[m] * arrayOfFloat[n];
          n++;
          m++;
        }
        paramArrayOfFloat4[(i++)] = f4;
        f2 += f1;
      }
    }
    while ((f2 < f3) && (i < j))
    {
      k = (int)f2;
      arrayOfFloat = sinc_table[((int)((f2 - k) * sinc_table_fsize))];
      m = k - sinc_table_center;
      f4 = 0.0F;
      n = 0;
      while (n < sinc_table_size)
      {
        f4 += paramArrayOfFloat1[m] * arrayOfFloat[n];
        n++;
        m++;
      }
      paramArrayOfFloat4[(i++)] = f4;
      f2 += f1;
      f1 += paramFloat2;
    }
    paramArrayOfFloat2[0] = f2;
    paramArrayOfInt[0] = i;
    paramArrayOfFloat3[0] = f1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftLanczosResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
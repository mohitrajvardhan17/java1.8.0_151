package com.sun.media.sound;

public final class FFT
{
  private final double[] w;
  private final int fftFrameSize;
  private final int sign;
  private final int[] bitm_array;
  private final int fftFrameSize2;
  
  public FFT(int paramInt1, int paramInt2)
  {
    w = computeTwiddleFactors(paramInt1, paramInt2);
    fftFrameSize = paramInt1;
    sign = paramInt2;
    fftFrameSize2 = (paramInt1 << 1);
    bitm_array = new int[fftFrameSize2];
    for (int i = 2; i < fftFrameSize2; i += 2)
    {
      int k = 2;
      int j = 0;
      while (k < fftFrameSize2)
      {
        if ((i & k) != 0) {
          j++;
        }
        j <<= 1;
        k <<= 1;
      }
      bitm_array[i] = j;
    }
  }
  
  public void transform(double[] paramArrayOfDouble)
  {
    bitreversal(paramArrayOfDouble);
    calc(fftFrameSize, paramArrayOfDouble, sign, w);
  }
  
  private static final double[] computeTwiddleFactors(int paramInt1, int paramInt2)
  {
    int i = (int)(Math.log(paramInt1) / Math.log(2.0D));
    double[] arrayOfDouble = new double[(paramInt1 - 1) * 4];
    double d1 = 0;
    int j = 0;
    double d2 = 2;
    double d4;
    while (j < i)
    {
      d3 = d2;
      d2 <<= 1;
      d4 = 1.0D;
      double d5 = 0.0D;
      double d8 = 3.141592653589793D / (d3 >> 1);
      double d10 = Math.cos(d8);
      double d12 = paramInt2 * Math.sin(d8);
      for (int m = 0; m < d3; m += 2)
      {
        arrayOfDouble[(d1++)] = d4;
        arrayOfDouble[(d1++)] = d5;
        double d14 = d4;
        d4 = d14 * d10 - d5 * d12;
        d5 = d14 * d12 + d5 * d10;
      }
      j++;
    }
    d1 = 0;
    j = arrayOfDouble.length >> 1;
    d2 = 0;
    double d3 = 2;
    while (d2 < i - 1)
    {
      d4 = d3;
      d3 *= 2;
      int k = d1 + d4;
      for (double d6 = 0; d6 < d4; d6 += 2)
      {
        double d7 = arrayOfDouble[(d1++)];
        double d9 = arrayOfDouble[(d1++)];
        double d11 = arrayOfDouble[(k++)];
        double d13 = arrayOfDouble[(k++)];
        arrayOfDouble[(j++)] = (d7 * d11 - d9 * d13);
        arrayOfDouble[(j++)] = (d7 * d13 + d9 * d11);
      }
      d2++;
    }
    return arrayOfDouble;
  }
  
  private static final void calc(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2)
  {
    int i = paramInt1 << 1;
    int j = 2;
    if (j >= i) {
      return;
    }
    int k = j - 2;
    if (paramInt2 == -1) {
      calcF4F(paramInt1, paramArrayOfDouble1, k, j, paramArrayOfDouble2);
    } else {
      calcF4I(paramInt1, paramArrayOfDouble1, k, j, paramArrayOfDouble2);
    }
  }
  
  private static final void calcF2E(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    int i = paramInt3;
    for (int j = 0; j < i; j += 2)
    {
      double d1 = paramArrayOfDouble2[(paramInt2++)];
      double d2 = paramArrayOfDouble2[(paramInt2++)];
      int k = j + i;
      double d3 = paramArrayOfDouble1[k];
      double d4 = paramArrayOfDouble1[(k + 1)];
      double d5 = paramArrayOfDouble1[j];
      double d6 = paramArrayOfDouble1[(j + 1)];
      double d7 = d3 * d1 - d4 * d2;
      double d8 = d3 * d2 + d4 * d1;
      paramArrayOfDouble1[k] = (d5 - d7);
      paramArrayOfDouble1[(k + 1)] = (d6 - d8);
      paramArrayOfDouble1[j] = (d5 + d7);
      paramArrayOfDouble1[(j + 1)] = (d6 + d8);
    }
  }
  
  private static final void calcF4F(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i)
    {
      if (paramInt3 << 2 == i)
      {
        calcF4FE(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      }
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i)
      {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      }
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      paramInt2 += 2;
      n += 2;
      i1 += 2;
      int i2 = 0;
      while (i2 < i)
      {
        int i3 = i2 + k;
        double d2 = paramArrayOfDouble1[i3];
        double d4 = paramArrayOfDouble1[(i3 + 1)];
        double d6 = paramArrayOfDouble1[i2];
        double d8 = paramArrayOfDouble1[(i2 + 1)];
        i2 += m;
        i3 += m;
        double d10 = paramArrayOfDouble1[i3];
        double d12 = paramArrayOfDouble1[(i3 + 1)];
        double d13 = paramArrayOfDouble1[i2];
        double d15 = paramArrayOfDouble1[(i2 + 1)];
        double d17 = d2;
        double d19 = d4;
        d2 = d6 - d17;
        d4 = d8 - d19;
        d6 += d17;
        d8 += d19;
        double d21 = d13;
        double d23 = d15;
        double d25 = d10;
        double d27 = d12;
        d17 = d25 - d21;
        d19 = d27 - d23;
        d10 = d2 + d19;
        d12 = d4 - d17;
        d2 -= d19;
        d4 += d17;
        d17 = d21 + d25;
        d19 = d23 + d27;
        d13 = d6 - d17;
        d15 = d8 - d19;
        d6 += d17;
        d8 += d19;
        paramArrayOfDouble1[i3] = d10;
        paramArrayOfDouble1[(i3 + 1)] = d12;
        paramArrayOfDouble1[i2] = d13;
        paramArrayOfDouble1[(i2 + 1)] = d15;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d2;
        paramArrayOfDouble1[(i3 + 1)] = d4;
        paramArrayOfDouble1[i2] = d6;
        paramArrayOfDouble1[(i2 + 1)] = d8;
        i2 += paramInt3;
      }
      for (i2 = 2; i2 < k; i2 += 2)
      {
        double d1 = paramArrayOfDouble2[(paramInt2++)];
        double d3 = paramArrayOfDouble2[(paramInt2++)];
        double d5 = paramArrayOfDouble2[(n++)];
        double d7 = paramArrayOfDouble2[(n++)];
        double d9 = paramArrayOfDouble2[(i1++)];
        double d11 = paramArrayOfDouble2[(i1++)];
        int i4 = i2;
        while (i4 < i)
        {
          int i5 = i4 + k;
          double d14 = paramArrayOfDouble1[i5];
          double d16 = paramArrayOfDouble1[(i5 + 1)];
          double d18 = paramArrayOfDouble1[i4];
          double d20 = paramArrayOfDouble1[(i4 + 1)];
          i4 += m;
          i5 += m;
          double d22 = paramArrayOfDouble1[i5];
          double d24 = paramArrayOfDouble1[(i5 + 1)];
          double d26 = paramArrayOfDouble1[i4];
          double d28 = paramArrayOfDouble1[(i4 + 1)];
          double d29 = d14 * d1 - d16 * d3;
          double d30 = d14 * d3 + d16 * d1;
          d14 = d18 - d29;
          d16 = d20 - d30;
          d18 += d29;
          d20 += d30;
          double d31 = d26 * d5 - d28 * d7;
          double d32 = d26 * d7 + d28 * d5;
          double d33 = d22 * d9 - d24 * d11;
          double d34 = d22 * d11 + d24 * d9;
          d29 = d33 - d31;
          d30 = d34 - d32;
          d22 = d14 + d30;
          d24 = d16 - d29;
          d14 -= d30;
          d16 += d29;
          d29 = d31 + d33;
          d30 = d32 + d34;
          d26 = d18 - d29;
          d28 = d20 - d30;
          d18 += d29;
          d20 += d30;
          paramArrayOfDouble1[i5] = d22;
          paramArrayOfDouble1[(i5 + 1)] = d24;
          paramArrayOfDouble1[i4] = d26;
          paramArrayOfDouble1[(i4 + 1)] = d28;
          i4 -= m;
          i5 -= m;
          paramArrayOfDouble1[i5] = d14;
          paramArrayOfDouble1[(i5 + 1)] = d16;
          paramArrayOfDouble1[i4] = d18;
          paramArrayOfDouble1[(i4 + 1)] = d20;
          i4 += paramInt3;
        }
      }
      paramInt2 += (k << 1);
    }
    calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
  }
  
  private static final void calcF4I(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i)
    {
      if (paramInt3 << 2 == i)
      {
        calcF4IE(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      }
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i)
      {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      }
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      paramInt2 += 2;
      n += 2;
      i1 += 2;
      int i2 = 0;
      while (i2 < i)
      {
        int i3 = i2 + k;
        double d2 = paramArrayOfDouble1[i3];
        double d4 = paramArrayOfDouble1[(i3 + 1)];
        double d6 = paramArrayOfDouble1[i2];
        double d8 = paramArrayOfDouble1[(i2 + 1)];
        i2 += m;
        i3 += m;
        double d10 = paramArrayOfDouble1[i3];
        double d12 = paramArrayOfDouble1[(i3 + 1)];
        double d13 = paramArrayOfDouble1[i2];
        double d15 = paramArrayOfDouble1[(i2 + 1)];
        double d17 = d2;
        double d19 = d4;
        d2 = d6 - d17;
        d4 = d8 - d19;
        d6 += d17;
        d8 += d19;
        double d21 = d13;
        double d23 = d15;
        double d25 = d10;
        double d27 = d12;
        d17 = d21 - d25;
        d19 = d23 - d27;
        d10 = d2 + d19;
        d12 = d4 - d17;
        d2 -= d19;
        d4 += d17;
        d17 = d21 + d25;
        d19 = d23 + d27;
        d13 = d6 - d17;
        d15 = d8 - d19;
        d6 += d17;
        d8 += d19;
        paramArrayOfDouble1[i3] = d10;
        paramArrayOfDouble1[(i3 + 1)] = d12;
        paramArrayOfDouble1[i2] = d13;
        paramArrayOfDouble1[(i2 + 1)] = d15;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d2;
        paramArrayOfDouble1[(i3 + 1)] = d4;
        paramArrayOfDouble1[i2] = d6;
        paramArrayOfDouble1[(i2 + 1)] = d8;
        i2 += paramInt3;
      }
      for (i2 = 2; i2 < k; i2 += 2)
      {
        double d1 = paramArrayOfDouble2[(paramInt2++)];
        double d3 = paramArrayOfDouble2[(paramInt2++)];
        double d5 = paramArrayOfDouble2[(n++)];
        double d7 = paramArrayOfDouble2[(n++)];
        double d9 = paramArrayOfDouble2[(i1++)];
        double d11 = paramArrayOfDouble2[(i1++)];
        int i4 = i2;
        while (i4 < i)
        {
          int i5 = i4 + k;
          double d14 = paramArrayOfDouble1[i5];
          double d16 = paramArrayOfDouble1[(i5 + 1)];
          double d18 = paramArrayOfDouble1[i4];
          double d20 = paramArrayOfDouble1[(i4 + 1)];
          i4 += m;
          i5 += m;
          double d22 = paramArrayOfDouble1[i5];
          double d24 = paramArrayOfDouble1[(i5 + 1)];
          double d26 = paramArrayOfDouble1[i4];
          double d28 = paramArrayOfDouble1[(i4 + 1)];
          double d29 = d14 * d1 - d16 * d3;
          double d30 = d14 * d3 + d16 * d1;
          d14 = d18 - d29;
          d16 = d20 - d30;
          d18 += d29;
          d20 += d30;
          double d31 = d26 * d5 - d28 * d7;
          double d32 = d26 * d7 + d28 * d5;
          double d33 = d22 * d9 - d24 * d11;
          double d34 = d22 * d11 + d24 * d9;
          d29 = d31 - d33;
          d30 = d32 - d34;
          d22 = d14 + d30;
          d24 = d16 - d29;
          d14 -= d30;
          d16 += d29;
          d29 = d31 + d33;
          d30 = d32 + d34;
          d26 = d18 - d29;
          d28 = d20 - d30;
          d18 += d29;
          d20 += d30;
          paramArrayOfDouble1[i5] = d22;
          paramArrayOfDouble1[(i5 + 1)] = d24;
          paramArrayOfDouble1[i4] = d26;
          paramArrayOfDouble1[(i4 + 1)] = d28;
          i4 -= m;
          i5 -= m;
          paramArrayOfDouble1[i5] = d14;
          paramArrayOfDouble1[(i5 + 1)] = d16;
          paramArrayOfDouble1[i4] = d18;
          paramArrayOfDouble1[(i4 + 1)] = d20;
          i4 += paramInt3;
        }
      }
      paramInt2 += (k << 1);
    }
    calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
  }
  
  private static final void calcF4FE(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i)
    {
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i)
      {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      }
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      for (int i2 = 0; i2 < k; i2 += 2)
      {
        double d1 = paramArrayOfDouble2[(paramInt2++)];
        double d2 = paramArrayOfDouble2[(paramInt2++)];
        double d3 = paramArrayOfDouble2[(n++)];
        double d4 = paramArrayOfDouble2[(n++)];
        double d5 = paramArrayOfDouble2[(i1++)];
        double d6 = paramArrayOfDouble2[(i1++)];
        int i3 = i2 + k;
        double d7 = paramArrayOfDouble1[i3];
        double d8 = paramArrayOfDouble1[(i3 + 1)];
        double d9 = paramArrayOfDouble1[i2];
        double d10 = paramArrayOfDouble1[(i2 + 1)];
        i2 += m;
        i3 += m;
        double d11 = paramArrayOfDouble1[i3];
        double d12 = paramArrayOfDouble1[(i3 + 1)];
        double d13 = paramArrayOfDouble1[i2];
        double d14 = paramArrayOfDouble1[(i2 + 1)];
        double d15 = d7 * d1 - d8 * d2;
        double d16 = d7 * d2 + d8 * d1;
        d7 = d9 - d15;
        d8 = d10 - d16;
        d9 += d15;
        d10 += d16;
        double d17 = d13 * d3 - d14 * d4;
        double d18 = d13 * d4 + d14 * d3;
        double d19 = d11 * d5 - d12 * d6;
        double d20 = d11 * d6 + d12 * d5;
        d15 = d19 - d17;
        d16 = d20 - d18;
        d11 = d7 + d16;
        d12 = d8 - d15;
        d7 -= d16;
        d8 += d15;
        d15 = d17 + d19;
        d16 = d18 + d20;
        d13 = d9 - d15;
        d14 = d10 - d16;
        d9 += d15;
        d10 += d16;
        paramArrayOfDouble1[i3] = d11;
        paramArrayOfDouble1[(i3 + 1)] = d12;
        paramArrayOfDouble1[i2] = d13;
        paramArrayOfDouble1[(i2 + 1)] = d14;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d7;
        paramArrayOfDouble1[(i3 + 1)] = d8;
        paramArrayOfDouble1[i2] = d9;
        paramArrayOfDouble1[(i2 + 1)] = d10;
      }
      paramInt2 += (k << 1);
    }
  }
  
  private static final void calcF4IE(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2)
  {
    int i = paramInt1 << 1;
    int j = paramArrayOfDouble2.length >> 1;
    while (paramInt3 < i)
    {
      int k = paramInt3;
      int m = paramInt3 << 1;
      if (m == i)
      {
        calcF2E(paramInt1, paramArrayOfDouble1, paramInt2, paramInt3, paramArrayOfDouble2);
        return;
      }
      paramInt3 <<= 2;
      int n = paramInt2 + k;
      int i1 = paramInt2 + j;
      for (int i2 = 0; i2 < k; i2 += 2)
      {
        double d1 = paramArrayOfDouble2[(paramInt2++)];
        double d2 = paramArrayOfDouble2[(paramInt2++)];
        double d3 = paramArrayOfDouble2[(n++)];
        double d4 = paramArrayOfDouble2[(n++)];
        double d5 = paramArrayOfDouble2[(i1++)];
        double d6 = paramArrayOfDouble2[(i1++)];
        int i3 = i2 + k;
        double d7 = paramArrayOfDouble1[i3];
        double d8 = paramArrayOfDouble1[(i3 + 1)];
        double d9 = paramArrayOfDouble1[i2];
        double d10 = paramArrayOfDouble1[(i2 + 1)];
        i2 += m;
        i3 += m;
        double d11 = paramArrayOfDouble1[i3];
        double d12 = paramArrayOfDouble1[(i3 + 1)];
        double d13 = paramArrayOfDouble1[i2];
        double d14 = paramArrayOfDouble1[(i2 + 1)];
        double d15 = d7 * d1 - d8 * d2;
        double d16 = d7 * d2 + d8 * d1;
        d7 = d9 - d15;
        d8 = d10 - d16;
        d9 += d15;
        d10 += d16;
        double d17 = d13 * d3 - d14 * d4;
        double d18 = d13 * d4 + d14 * d3;
        double d19 = d11 * d5 - d12 * d6;
        double d20 = d11 * d6 + d12 * d5;
        d15 = d17 - d19;
        d16 = d18 - d20;
        d11 = d7 + d16;
        d12 = d8 - d15;
        d7 -= d16;
        d8 += d15;
        d15 = d17 + d19;
        d16 = d18 + d20;
        d13 = d9 - d15;
        d14 = d10 - d16;
        d9 += d15;
        d10 += d16;
        paramArrayOfDouble1[i3] = d11;
        paramArrayOfDouble1[(i3 + 1)] = d12;
        paramArrayOfDouble1[i2] = d13;
        paramArrayOfDouble1[(i2 + 1)] = d14;
        i2 -= m;
        i3 -= m;
        paramArrayOfDouble1[i3] = d7;
        paramArrayOfDouble1[(i3 + 1)] = d8;
        paramArrayOfDouble1[i2] = d9;
        paramArrayOfDouble1[(i2 + 1)] = d10;
      }
      paramInt2 += (k << 1);
    }
  }
  
  private final void bitreversal(double[] paramArrayOfDouble)
  {
    if (fftFrameSize < 4) {
      return;
    }
    int i = fftFrameSize2 - 2;
    for (int j = 0; j < fftFrameSize; j += 4)
    {
      int k = bitm_array[j];
      if (j < k)
      {
        m = j;
        n = k;
        d1 = paramArrayOfDouble[m];
        paramArrayOfDouble[m] = paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d1;
        m++;
        n++;
        d2 = paramArrayOfDouble[m];
        paramArrayOfDouble[m] = paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d2;
        m = i - j;
        n = i - k;
        d1 = paramArrayOfDouble[m];
        paramArrayOfDouble[m] = paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d1;
        m++;
        n++;
        d2 = paramArrayOfDouble[m];
        paramArrayOfDouble[m] = paramArrayOfDouble[n];
        paramArrayOfDouble[n] = d2;
      }
      int m = k + fftFrameSize;
      int n = j + 2;
      double d1 = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = paramArrayOfDouble[m];
      paramArrayOfDouble[m] = d1;
      n++;
      m++;
      double d2 = paramArrayOfDouble[n];
      paramArrayOfDouble[n] = paramArrayOfDouble[m];
      paramArrayOfDouble[m] = d2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\FFT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
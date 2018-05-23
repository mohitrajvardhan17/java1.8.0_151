package com.sun.imageio.plugins.png;

public class RowFilter
{
  public RowFilter() {}
  
  private static final int abs(int paramInt)
  {
    return paramInt < 0 ? -paramInt : paramInt;
  }
  
  protected static int subFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
  {
    int i = 0;
    for (int j = paramInt1; j < paramInt2 + paramInt1; j++)
    {
      int k = paramArrayOfByte1[j] & 0xFF;
      int m = paramArrayOfByte1[(j - paramInt1)] & 0xFF;
      int n = k - m;
      paramArrayOfByte2[j] = ((byte)n);
      i += abs(n);
    }
    return i;
  }
  
  protected static int upFilter(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2)
  {
    int i = 0;
    for (int j = paramInt1; j < paramInt2 + paramInt1; j++)
    {
      int k = paramArrayOfByte1[j] & 0xFF;
      int m = paramArrayOfByte2[j] & 0xFF;
      int n = k - m;
      paramArrayOfByte3[j] = ((byte)n);
      i += abs(n);
    }
    return i;
  }
  
  protected final int paethPredictor(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 + paramInt2 - paramInt3;
    int j = abs(i - paramInt1);
    int k = abs(i - paramInt2);
    int m = abs(i - paramInt3);
    if ((j <= k) && (j <= m)) {
      return paramInt1;
    }
    if (k <= m) {
      return paramInt2;
    }
    return paramInt3;
  }
  
  public int filterRow(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[][] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if (paramInt1 != 3)
    {
      System.arraycopy(paramArrayOfByte1, paramInt3, paramArrayOfByte[0], paramInt3, paramInt2);
      return 0;
    }
    int[] arrayOfInt = new int[5];
    for (int i = 0; i < 5; i++) {
      arrayOfInt[i] = Integer.MAX_VALUE;
    }
    i = 0;
    for (int k = paramInt3; k < paramInt2 + paramInt3; k++)
    {
      m = paramArrayOfByte1[k] & 0xFF;
      i += m;
    }
    arrayOfInt[0] = i;
    byte[] arrayOfByte = paramArrayOfByte[1];
    k = subFilter(paramArrayOfByte1, arrayOfByte, paramInt3, paramInt2);
    arrayOfInt[1] = k;
    arrayOfByte = paramArrayOfByte[2];
    k = upFilter(paramArrayOfByte1, paramArrayOfByte2, arrayOfByte, paramInt3, paramInt2);
    arrayOfInt[2] = k;
    arrayOfByte = paramArrayOfByte[3];
    k = 0;
    int n;
    int i1;
    int i2;
    int i3;
    for (int m = paramInt3; m < paramInt2 + paramInt3; m++)
    {
      n = paramArrayOfByte1[m] & 0xFF;
      i1 = paramArrayOfByte1[(m - paramInt3)] & 0xFF;
      i2 = paramArrayOfByte2[m] & 0xFF;
      i3 = n - (i1 + i2) / 2;
      arrayOfByte[m] = ((byte)i3);
      k += abs(i3);
    }
    arrayOfInt[3] = k;
    arrayOfByte = paramArrayOfByte[4];
    k = 0;
    for (m = paramInt3; m < paramInt2 + paramInt3; m++)
    {
      n = paramArrayOfByte1[m] & 0xFF;
      i1 = paramArrayOfByte1[(m - paramInt3)] & 0xFF;
      i2 = paramArrayOfByte2[m] & 0xFF;
      i3 = paramArrayOfByte2[(m - paramInt3)] & 0xFF;
      int i4 = paethPredictor(i1, i2, i3);
      int i5 = n - i4;
      arrayOfByte[m] = ((byte)i5);
      k += abs(i5);
    }
    arrayOfInt[4] = k;
    int j = arrayOfInt[0];
    k = 0;
    for (m = 1; m < 5; m++) {
      if (arrayOfInt[m] < j)
      {
        j = arrayOfInt[m];
        k = m;
      }
    }
    if (k == 0) {
      System.arraycopy(paramArrayOfByte1, paramInt3, paramArrayOfByte[0], paramInt3, paramInt2);
    }
    return k;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\RowFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
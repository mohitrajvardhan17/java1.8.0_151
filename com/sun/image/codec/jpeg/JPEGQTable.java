package com.sun.image.codec.jpeg;

public class JPEGQTable
{
  private int[] quantval;
  private static final byte QTABLESIZE = 64;
  public static final JPEGQTable StdLuminance = new JPEGQTable();
  public static final JPEGQTable StdChrominance;
  
  private JPEGQTable()
  {
    quantval = new int[64];
  }
  
  public JPEGQTable(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length != 64) {
      throw new IllegalArgumentException("Quantization table is the wrong size.");
    }
    quantval = new int[64];
    System.arraycopy(paramArrayOfInt, 0, quantval, 0, 64);
  }
  
  public int[] getTable()
  {
    int[] arrayOfInt = new int[64];
    System.arraycopy(quantval, 0, arrayOfInt, 0, 64);
    return arrayOfInt;
  }
  
  public JPEGQTable getScaledInstance(float paramFloat, boolean paramBoolean)
  {
    long l1 = paramBoolean ? 255L : 32767L;
    int[] arrayOfInt = new int[64];
    for (int i = 0; i < 64; i++)
    {
      long l2 = (quantval[i] * paramFloat + 0.5D);
      if (l2 <= 0L) {
        l2 = 1L;
      }
      if (l2 > l1) {
        l2 = l1;
      }
      arrayOfInt[i] = ((int)l2);
    }
    return new JPEGQTable(arrayOfInt);
  }
  
  static
  {
    int[] arrayOfInt = { 16, 11, 12, 14, 12, 10, 16, 14, 13, 14, 18, 17, 16, 19, 24, 40, 26, 24, 22, 22, 24, 49, 35, 37, 29, 40, 58, 51, 61, 60, 57, 51, 56, 55, 64, 72, 92, 78, 64, 68, 87, 69, 55, 56, 80, 109, 81, 87, 95, 98, 103, 104, 103, 62, 77, 113, 121, 112, 100, 120, 92, 101, 103, 99 };
    StdLuminancequantval = arrayOfInt;
    StdChrominance = new JPEGQTable();
    arrayOfInt = new int[] { 17, 18, 18, 24, 21, 24, 47, 26, 26, 47, 99, 66, 56, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };
    StdChrominancequantval = arrayOfInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\image\codec\jpeg\JPEGQTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
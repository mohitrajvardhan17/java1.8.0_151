package sun.misc;

public class FpUtils
{
  private FpUtils() {}
  
  @Deprecated
  public static int getExponent(double paramDouble)
  {
    return Math.getExponent(paramDouble);
  }
  
  @Deprecated
  public static int getExponent(float paramFloat)
  {
    return Math.getExponent(paramFloat);
  }
  
  @Deprecated
  public static double rawCopySign(double paramDouble1, double paramDouble2)
  {
    return Math.copySign(paramDouble1, paramDouble2);
  }
  
  @Deprecated
  public static float rawCopySign(float paramFloat1, float paramFloat2)
  {
    return Math.copySign(paramFloat1, paramFloat2);
  }
  
  @Deprecated
  public static boolean isFinite(double paramDouble)
  {
    return Double.isFinite(paramDouble);
  }
  
  @Deprecated
  public static boolean isFinite(float paramFloat)
  {
    return Float.isFinite(paramFloat);
  }
  
  public static boolean isInfinite(double paramDouble)
  {
    return Double.isInfinite(paramDouble);
  }
  
  public static boolean isInfinite(float paramFloat)
  {
    return Float.isInfinite(paramFloat);
  }
  
  public static boolean isNaN(double paramDouble)
  {
    return Double.isNaN(paramDouble);
  }
  
  public static boolean isNaN(float paramFloat)
  {
    return Float.isNaN(paramFloat);
  }
  
  public static boolean isUnordered(double paramDouble1, double paramDouble2)
  {
    return (isNaN(paramDouble1)) || (isNaN(paramDouble2));
  }
  
  public static boolean isUnordered(float paramFloat1, float paramFloat2)
  {
    return (isNaN(paramFloat1)) || (isNaN(paramFloat2));
  }
  
  public static int ilogb(double paramDouble)
  {
    int i = getExponent(paramDouble);
    switch (i)
    {
    case 1024: 
      if (isNaN(paramDouble)) {
        return 1073741824;
      }
      return 268435456;
    case -1023: 
      if (paramDouble == 0.0D) {
        return -268435456;
      }
      long l = Double.doubleToRawLongBits(paramDouble);
      l &= 0xFFFFFFFFFFFFF;
      assert (l != 0L);
      while (l < 4503599627370496L)
      {
        l *= 2L;
        i--;
      }
      i++;
      assert ((i >= 64462) && (i < 64514));
      return i;
    }
    assert ((i >= 64514) && (i <= 1023));
    return i;
  }
  
  public static int ilogb(float paramFloat)
  {
    int i = getExponent(paramFloat);
    switch (i)
    {
    case 128: 
      if (isNaN(paramFloat)) {
        return 1073741824;
      }
      return 268435456;
    case -127: 
      if (paramFloat == 0.0F) {
        return -268435456;
      }
      int j = Float.floatToRawIntBits(paramFloat);
      j &= 0x7FFFFF;
      assert (j != 0);
      while (j < 8388608)
      {
        j *= 2;
        i--;
      }
      i++;
      assert ((i >= 65387) && (i < -126));
      return i;
    }
    assert ((i >= -126) && (i <= 127));
    return i;
  }
  
  @Deprecated
  public static double scalb(double paramDouble, int paramInt)
  {
    return Math.scalb(paramDouble, paramInt);
  }
  
  @Deprecated
  public static float scalb(float paramFloat, int paramInt)
  {
    return Math.scalb(paramFloat, paramInt);
  }
  
  @Deprecated
  public static double nextAfter(double paramDouble1, double paramDouble2)
  {
    return Math.nextAfter(paramDouble1, paramDouble2);
  }
  
  @Deprecated
  public static float nextAfter(float paramFloat, double paramDouble)
  {
    return Math.nextAfter(paramFloat, paramDouble);
  }
  
  @Deprecated
  public static double nextUp(double paramDouble)
  {
    return Math.nextUp(paramDouble);
  }
  
  @Deprecated
  public static float nextUp(float paramFloat)
  {
    return Math.nextUp(paramFloat);
  }
  
  @Deprecated
  public static double nextDown(double paramDouble)
  {
    return Math.nextDown(paramDouble);
  }
  
  @Deprecated
  public static double nextDown(float paramFloat)
  {
    return Math.nextDown(paramFloat);
  }
  
  @Deprecated
  public static double copySign(double paramDouble1, double paramDouble2)
  {
    return StrictMath.copySign(paramDouble1, paramDouble2);
  }
  
  @Deprecated
  public static float copySign(float paramFloat1, float paramFloat2)
  {
    return StrictMath.copySign(paramFloat1, paramFloat2);
  }
  
  @Deprecated
  public static double ulp(double paramDouble)
  {
    return Math.ulp(paramDouble);
  }
  
  @Deprecated
  public static float ulp(float paramFloat)
  {
    return Math.ulp(paramFloat);
  }
  
  @Deprecated
  public static double signum(double paramDouble)
  {
    return Math.signum(paramDouble);
  }
  
  @Deprecated
  public static float signum(float paramFloat)
  {
    return Math.signum(paramFloat);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\FpUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
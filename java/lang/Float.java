package java.lang;

import sun.misc.FloatingDecimal;

public final class Float
  extends Number
  implements Comparable<Float>
{
  public static final float POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
  public static final float NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;
  public static final float NaN = NaN.0F;
  public static final float MAX_VALUE = Float.MAX_VALUE;
  public static final float MIN_NORMAL = 1.17549435E-38F;
  public static final float MIN_VALUE = Float.MIN_VALUE;
  public static final int MAX_EXPONENT = 127;
  public static final int MIN_EXPONENT = -126;
  public static final int SIZE = 32;
  public static final int BYTES = 4;
  public static final Class<Float> TYPE = Class.getPrimitiveClass("float");
  private final float value;
  private static final long serialVersionUID = -2671257302660747028L;
  
  public static String toString(float paramFloat)
  {
    return FloatingDecimal.toJavaFormatString(paramFloat);
  }
  
  public static String toHexString(float paramFloat)
  {
    if ((Math.abs(paramFloat) < 1.17549435E-38F) && (paramFloat != 0.0F))
    {
      String str = Double.toHexString(Math.scalb(paramFloat, 64640));
      return str.replaceFirst("p-1022$", "p-126");
    }
    return Double.toHexString(paramFloat);
  }
  
  public static Float valueOf(String paramString)
    throws NumberFormatException
  {
    return new Float(parseFloat(paramString));
  }
  
  public static Float valueOf(float paramFloat)
  {
    return new Float(paramFloat);
  }
  
  public static float parseFloat(String paramString)
    throws NumberFormatException
  {
    return FloatingDecimal.parseFloat(paramString);
  }
  
  public static boolean isNaN(float paramFloat)
  {
    return paramFloat != paramFloat;
  }
  
  public static boolean isInfinite(float paramFloat)
  {
    return (paramFloat == Float.POSITIVE_INFINITY) || (paramFloat == Float.NEGATIVE_INFINITY);
  }
  
  public static boolean isFinite(float paramFloat)
  {
    return Math.abs(paramFloat) <= Float.MAX_VALUE;
  }
  
  public Float(float paramFloat)
  {
    value = paramFloat;
  }
  
  public Float(double paramDouble)
  {
    value = ((float)paramDouble);
  }
  
  public Float(String paramString)
    throws NumberFormatException
  {
    value = parseFloat(paramString);
  }
  
  public boolean isNaN()
  {
    return isNaN(value);
  }
  
  public boolean isInfinite()
  {
    return isInfinite(value);
  }
  
  public String toString()
  {
    return toString(value);
  }
  
  public byte byteValue()
  {
    return (byte)(int)value;
  }
  
  public short shortValue()
  {
    return (short)(int)value;
  }
  
  public int intValue()
  {
    return (int)value;
  }
  
  public long longValue()
  {
    return value;
  }
  
  public float floatValue()
  {
    return value;
  }
  
  public double doubleValue()
  {
    return value;
  }
  
  public int hashCode()
  {
    return hashCode(value);
  }
  
  public static int hashCode(float paramFloat)
  {
    return floatToIntBits(paramFloat);
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Float)) && (floatToIntBits(value) == floatToIntBits(value));
  }
  
  public static int floatToIntBits(float paramFloat)
  {
    int i = floatToRawIntBits(paramFloat);
    if (((i & 0x7F800000) == 2139095040) && ((i & 0x7FFFFF) != 0)) {
      i = 2143289344;
    }
    return i;
  }
  
  public static native int floatToRawIntBits(float paramFloat);
  
  public static native float intBitsToFloat(int paramInt);
  
  public int compareTo(Float paramFloat)
  {
    return compare(value, value);
  }
  
  public static int compare(float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 < paramFloat2) {
      return -1;
    }
    if (paramFloat1 > paramFloat2) {
      return 1;
    }
    int i = floatToIntBits(paramFloat1);
    int j = floatToIntBits(paramFloat2);
    return i < j ? -1 : i == j ? 0 : 1;
  }
  
  public static float sum(float paramFloat1, float paramFloat2)
  {
    return paramFloat1 + paramFloat2;
  }
  
  public static float max(float paramFloat1, float paramFloat2)
  {
    return Math.max(paramFloat1, paramFloat2);
  }
  
  public static float min(float paramFloat1, float paramFloat2)
  {
    return Math.min(paramFloat1, paramFloat2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Float.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.lang;

import sun.misc.VM;

public final class Integer
  extends Number
  implements Comparable<Integer>
{
  public static final int MIN_VALUE = Integer.MIN_VALUE;
  public static final int MAX_VALUE = Integer.MAX_VALUE;
  public static final Class<Integer> TYPE = Class.getPrimitiveClass("int");
  static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
  static final char[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9' };
  static final char[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
  private final int value;
  public static final int SIZE = 32;
  public static final int BYTES = 4;
  private static final long serialVersionUID = 1360826667806852920L;
  
  public static String toString(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 2) || (paramInt2 > 36)) {
      paramInt2 = 10;
    }
    if (paramInt2 == 10) {
      return toString(paramInt1);
    }
    char[] arrayOfChar = new char[33];
    int i = paramInt1 < 0 ? 1 : 0;
    int j = 32;
    if (i == 0) {
      paramInt1 = -paramInt1;
    }
    while (paramInt1 <= -paramInt2)
    {
      arrayOfChar[(j--)] = digits[(-(paramInt1 % paramInt2))];
      paramInt1 /= paramInt2;
    }
    arrayOfChar[j] = digits[(-paramInt1)];
    if (i != 0) {
      arrayOfChar[(--j)] = '-';
    }
    return new String(arrayOfChar, j, 33 - j);
  }
  
  public static String toUnsignedString(int paramInt1, int paramInt2)
  {
    return Long.toUnsignedString(toUnsignedLong(paramInt1), paramInt2);
  }
  
  public static String toHexString(int paramInt)
  {
    return toUnsignedString0(paramInt, 4);
  }
  
  public static String toOctalString(int paramInt)
  {
    return toUnsignedString0(paramInt, 3);
  }
  
  public static String toBinaryString(int paramInt)
  {
    return toUnsignedString0(paramInt, 1);
  }
  
  private static String toUnsignedString0(int paramInt1, int paramInt2)
  {
    int i = 32 - numberOfLeadingZeros(paramInt1);
    int j = Math.max((i + (paramInt2 - 1)) / paramInt2, 1);
    char[] arrayOfChar = new char[j];
    formatUnsignedInt(paramInt1, paramInt2, arrayOfChar, 0, j);
    return new String(arrayOfChar, true);
  }
  
  static int formatUnsignedInt(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4)
  {
    int i = paramInt4;
    int j = 1 << paramInt2;
    int k = j - 1;
    do
    {
      paramArrayOfChar[(paramInt3 + --i)] = digits[(paramInt1 & k)];
      paramInt1 >>>= paramInt2;
    } while ((paramInt1 != 0) && (i > 0));
    return i;
  }
  
  public static String toString(int paramInt)
  {
    if (paramInt == Integer.MIN_VALUE) {
      return "-2147483648";
    }
    int i = paramInt < 0 ? stringSize(-paramInt) + 1 : stringSize(paramInt);
    char[] arrayOfChar = new char[i];
    getChars(paramInt, i, arrayOfChar);
    return new String(arrayOfChar, true);
  }
  
  public static String toUnsignedString(int paramInt)
  {
    return Long.toString(toUnsignedLong(paramInt));
  }
  
  static void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar)
  {
    int k = paramInt2;
    int m = 0;
    if (paramInt1 < 0)
    {
      m = 45;
      paramInt1 = -paramInt1;
    }
    int i;
    int j;
    while (paramInt1 >= 65536)
    {
      i = paramInt1 / 100;
      j = paramInt1 - ((i << 6) + (i << 5) + (i << 2));
      paramInt1 = i;
      paramArrayOfChar[(--k)] = DigitOnes[j];
      paramArrayOfChar[(--k)] = DigitTens[j];
    }
    for (;;)
    {
      i = paramInt1 * 52429 >>> 19;
      j = paramInt1 - ((i << 3) + (i << 1));
      paramArrayOfChar[(--k)] = digits[j];
      paramInt1 = i;
      if (paramInt1 == 0) {
        break;
      }
    }
    if (m != 0) {
      paramArrayOfChar[(--k)] = m;
    }
  }
  
  static int stringSize(int paramInt)
  {
    for (int i = 0;; i++) {
      if (paramInt <= sizeTable[i]) {
        return i + 1;
      }
    }
  }
  
  public static int parseInt(String paramString, int paramInt)
    throws NumberFormatException
  {
    if (paramString == null) {
      throw new NumberFormatException("null");
    }
    if (paramInt < 2) {
      throw new NumberFormatException("radix " + paramInt + " less than Character.MIN_RADIX");
    }
    if (paramInt > 36) {
      throw new NumberFormatException("radix " + paramInt + " greater than Character.MAX_RADIX");
    }
    int i = 0;
    int j = 0;
    int k = 0;
    int m = paramString.length();
    int n = -2147483647;
    if (m > 0)
    {
      int i3 = paramString.charAt(0);
      if (i3 < 48)
      {
        if (i3 == 45)
        {
          j = 1;
          n = Integer.MIN_VALUE;
        }
        else if (i3 != 43)
        {
          throw NumberFormatException.forInputString(paramString);
        }
        if (m == 1) {
          throw NumberFormatException.forInputString(paramString);
        }
        k++;
      }
      int i1 = n / paramInt;
      while (k < m)
      {
        int i2 = Character.digit(paramString.charAt(k++), paramInt);
        if (i2 < 0) {
          throw NumberFormatException.forInputString(paramString);
        }
        if (i < i1) {
          throw NumberFormatException.forInputString(paramString);
        }
        i *= paramInt;
        if (i < n + i2) {
          throw NumberFormatException.forInputString(paramString);
        }
        i -= i2;
      }
    }
    else
    {
      throw NumberFormatException.forInputString(paramString);
    }
    return j != 0 ? i : -i;
  }
  
  public static int parseInt(String paramString)
    throws NumberFormatException
  {
    return parseInt(paramString, 10);
  }
  
  public static int parseUnsignedInt(String paramString, int paramInt)
    throws NumberFormatException
  {
    if (paramString == null) {
      throw new NumberFormatException("null");
    }
    int i = paramString.length();
    if (i > 0)
    {
      int j = paramString.charAt(0);
      if (j == 45) {
        throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", new Object[] { paramString }));
      }
      if ((i <= 5) || ((paramInt == 10) && (i <= 9))) {
        return parseInt(paramString, paramInt);
      }
      long l = Long.parseLong(paramString, paramInt);
      if ((l & 0xFFFFFFFF00000000) == 0L) {
        return (int)l;
      }
      throw new NumberFormatException(String.format("String value %s exceeds range of unsigned int.", new Object[] { paramString }));
    }
    throw NumberFormatException.forInputString(paramString);
  }
  
  public static int parseUnsignedInt(String paramString)
    throws NumberFormatException
  {
    return parseUnsignedInt(paramString, 10);
  }
  
  public static Integer valueOf(String paramString, int paramInt)
    throws NumberFormatException
  {
    return valueOf(parseInt(paramString, paramInt));
  }
  
  public static Integer valueOf(String paramString)
    throws NumberFormatException
  {
    return valueOf(parseInt(paramString, 10));
  }
  
  public static Integer valueOf(int paramInt)
  {
    if ((paramInt >= -128) && (paramInt <= IntegerCache.high)) {
      return IntegerCache.cache[(paramInt + 128)];
    }
    return new Integer(paramInt);
  }
  
  public Integer(int paramInt)
  {
    value = paramInt;
  }
  
  public Integer(String paramString)
    throws NumberFormatException
  {
    value = parseInt(paramString, 10);
  }
  
  public byte byteValue()
  {
    return (byte)value;
  }
  
  public short shortValue()
  {
    return (short)value;
  }
  
  public int intValue()
  {
    return value;
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
  
  public String toString()
  {
    return toString(value);
  }
  
  public int hashCode()
  {
    return hashCode(value);
  }
  
  public static int hashCode(int paramInt)
  {
    return paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Integer)) {
      return value == ((Integer)paramObject).intValue();
    }
    return false;
  }
  
  public static Integer getInteger(String paramString)
  {
    return getInteger(paramString, null);
  }
  
  public static Integer getInteger(String paramString, int paramInt)
  {
    Integer localInteger = getInteger(paramString, null);
    return localInteger == null ? valueOf(paramInt) : localInteger;
  }
  
  public static Integer getInteger(String paramString, Integer paramInteger)
  {
    String str = null;
    try
    {
      str = System.getProperty(paramString);
    }
    catch (IllegalArgumentException|NullPointerException localIllegalArgumentException) {}
    if (str != null) {
      try
      {
        return decode(str);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return paramInteger;
  }
  
  public static Integer decode(String paramString)
    throws NumberFormatException
  {
    int i = 10;
    int j = 0;
    int k = 0;
    if (paramString.length() == 0) {
      throw new NumberFormatException("Zero length string");
    }
    int m = paramString.charAt(0);
    if (m == 45)
    {
      k = 1;
      j++;
    }
    else if (m == 43)
    {
      j++;
    }
    if ((paramString.startsWith("0x", j)) || (paramString.startsWith("0X", j)))
    {
      j += 2;
      i = 16;
    }
    else if (paramString.startsWith("#", j))
    {
      j++;
      i = 16;
    }
    else if ((paramString.startsWith("0", j)) && (paramString.length() > 1 + j))
    {
      j++;
      i = 8;
    }
    if ((paramString.startsWith("-", j)) || (paramString.startsWith("+", j))) {
      throw new NumberFormatException("Sign character in wrong position");
    }
    Integer localInteger;
    try
    {
      localInteger = valueOf(paramString.substring(j), i);
      localInteger = k != 0 ? valueOf(-localInteger.intValue()) : localInteger;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      String str = k != 0 ? "-" + paramString.substring(j) : paramString.substring(j);
      localInteger = valueOf(str, i);
    }
    return localInteger;
  }
  
  public int compareTo(Integer paramInteger)
  {
    return compare(value, value);
  }
  
  public static int compare(int paramInt1, int paramInt2)
  {
    return paramInt1 == paramInt2 ? 0 : paramInt1 < paramInt2 ? -1 : 1;
  }
  
  public static int compareUnsigned(int paramInt1, int paramInt2)
  {
    return compare(paramInt1 + Integer.MIN_VALUE, paramInt2 + Integer.MIN_VALUE);
  }
  
  public static long toUnsignedLong(int paramInt)
  {
    return paramInt & 0xFFFFFFFF;
  }
  
  public static int divideUnsigned(int paramInt1, int paramInt2)
  {
    return (int)(toUnsignedLong(paramInt1) / toUnsignedLong(paramInt2));
  }
  
  public static int remainderUnsigned(int paramInt1, int paramInt2)
  {
    return (int)(toUnsignedLong(paramInt1) % toUnsignedLong(paramInt2));
  }
  
  public static int highestOneBit(int paramInt)
  {
    paramInt |= paramInt >> 1;
    paramInt |= paramInt >> 2;
    paramInt |= paramInt >> 4;
    paramInt |= paramInt >> 8;
    paramInt |= paramInt >> 16;
    return paramInt - (paramInt >>> 1);
  }
  
  public static int lowestOneBit(int paramInt)
  {
    return paramInt & -paramInt;
  }
  
  public static int numberOfLeadingZeros(int paramInt)
  {
    if (paramInt == 0) {
      return 32;
    }
    int i = 1;
    if (paramInt >>> 16 == 0)
    {
      i += 16;
      paramInt <<= 16;
    }
    if (paramInt >>> 24 == 0)
    {
      i += 8;
      paramInt <<= 8;
    }
    if (paramInt >>> 28 == 0)
    {
      i += 4;
      paramInt <<= 4;
    }
    if (paramInt >>> 30 == 0)
    {
      i += 2;
      paramInt <<= 2;
    }
    i -= (paramInt >>> 31);
    return i;
  }
  
  public static int numberOfTrailingZeros(int paramInt)
  {
    if (paramInt == 0) {
      return 32;
    }
    int j = 31;
    int i = paramInt << 16;
    if (i != 0)
    {
      j -= 16;
      paramInt = i;
    }
    i = paramInt << 8;
    if (i != 0)
    {
      j -= 8;
      paramInt = i;
    }
    i = paramInt << 4;
    if (i != 0)
    {
      j -= 4;
      paramInt = i;
    }
    i = paramInt << 2;
    if (i != 0)
    {
      j -= 2;
      paramInt = i;
    }
    return j - (paramInt << 1 >>> 31);
  }
  
  public static int bitCount(int paramInt)
  {
    paramInt -= (paramInt >>> 1 & 0x55555555);
    paramInt = (paramInt & 0x33333333) + (paramInt >>> 2 & 0x33333333);
    paramInt = paramInt + (paramInt >>> 4) & 0xF0F0F0F;
    paramInt += (paramInt >>> 8);
    paramInt += (paramInt >>> 16);
    return paramInt & 0x3F;
  }
  
  public static int rotateLeft(int paramInt1, int paramInt2)
  {
    return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2;
  }
  
  public static int rotateRight(int paramInt1, int paramInt2)
  {
    return paramInt1 >>> paramInt2 | paramInt1 << -paramInt2;
  }
  
  public static int reverse(int paramInt)
  {
    paramInt = (paramInt & 0x55555555) << 1 | paramInt >>> 1 & 0x55555555;
    paramInt = (paramInt & 0x33333333) << 2 | paramInt >>> 2 & 0x33333333;
    paramInt = (paramInt & 0xF0F0F0F) << 4 | paramInt >>> 4 & 0xF0F0F0F;
    paramInt = paramInt << 24 | (paramInt & 0xFF00) << 8 | paramInt >>> 8 & 0xFF00 | paramInt >>> 24;
    return paramInt;
  }
  
  public static int signum(int paramInt)
  {
    return paramInt >> 31 | -paramInt >>> 31;
  }
  
  public static int reverseBytes(int paramInt)
  {
    return paramInt >>> 24 | paramInt >> 8 & 0xFF00 | paramInt << 8 & 0xFF0000 | paramInt << 24;
  }
  
  public static int sum(int paramInt1, int paramInt2)
  {
    return paramInt1 + paramInt2;
  }
  
  public static int max(int paramInt1, int paramInt2)
  {
    return Math.max(paramInt1, paramInt2);
  }
  
  public static int min(int paramInt1, int paramInt2)
  {
    return Math.min(paramInt1, paramInt2);
  }
  
  private static class IntegerCache
  {
    static final int low = -128;
    static final int high;
    static final Integer[] cache;
    
    private IntegerCache() {}
    
    static
    {
      int i = 127;
      String str = VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
      if (str != null) {
        try
        {
          int j = Integer.parseInt(str);
          j = Math.max(j, 127);
          i = Math.min(j, 2147483518);
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
      high = i;
      cache = new Integer[high - -128 + 1];
      int k = -128;
      for (int m = 0; m < cache.length; m++) {
        cache[m] = new Integer(k++);
      }
      assert (high >= 127);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Integer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
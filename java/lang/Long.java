package java.lang;

import java.math.BigInteger;

public final class Long
  extends Number
  implements Comparable<Long>
{
  public static final long MIN_VALUE = Long.MIN_VALUE;
  public static final long MAX_VALUE = Long.MAX_VALUE;
  public static final Class<Long> TYPE = Class.getPrimitiveClass("long");
  private final long value;
  public static final int SIZE = 64;
  public static final int BYTES = 8;
  private static final long serialVersionUID = 4290774380558885855L;
  
  public static String toString(long paramLong, int paramInt)
  {
    if ((paramInt < 2) || (paramInt > 36)) {
      paramInt = 10;
    }
    if (paramInt == 10) {
      return toString(paramLong);
    }
    char[] arrayOfChar = new char[65];
    int i = 64;
    int j = paramLong < 0L ? 1 : 0;
    if (j == 0) {}
    for (paramLong = -paramLong; paramLong <= -paramInt; paramLong /= paramInt) {
      arrayOfChar[(i--)] = Integer.digits[((int)-(paramLong % paramInt))];
    }
    arrayOfChar[i] = Integer.digits[((int)-paramLong)];
    if (j != 0) {
      arrayOfChar[(--i)] = '-';
    }
    return new String(arrayOfChar, i, 65 - i);
  }
  
  public static String toUnsignedString(long paramLong, int paramInt)
  {
    if (paramLong >= 0L) {
      return toString(paramLong, paramInt);
    }
    switch (paramInt)
    {
    case 2: 
      return toBinaryString(paramLong);
    case 4: 
      return toUnsignedString0(paramLong, 2);
    case 8: 
      return toOctalString(paramLong);
    case 10: 
      long l1 = (paramLong >>> 1) / 5L;
      long l2 = paramLong - l1 * 10L;
      return toString(l1) + l2;
    case 16: 
      return toHexString(paramLong);
    case 32: 
      return toUnsignedString0(paramLong, 5);
    }
    return toUnsignedBigInteger(paramLong).toString(paramInt);
  }
  
  private static BigInteger toUnsignedBigInteger(long paramLong)
  {
    if (paramLong >= 0L) {
      return BigInteger.valueOf(paramLong);
    }
    int i = (int)(paramLong >>> 32);
    int j = (int)paramLong;
    return BigInteger.valueOf(Integer.toUnsignedLong(i)).shiftLeft(32).add(BigInteger.valueOf(Integer.toUnsignedLong(j)));
  }
  
  public static String toHexString(long paramLong)
  {
    return toUnsignedString0(paramLong, 4);
  }
  
  public static String toOctalString(long paramLong)
  {
    return toUnsignedString0(paramLong, 3);
  }
  
  public static String toBinaryString(long paramLong)
  {
    return toUnsignedString0(paramLong, 1);
  }
  
  static String toUnsignedString0(long paramLong, int paramInt)
  {
    int i = 64 - numberOfLeadingZeros(paramLong);
    int j = Math.max((i + (paramInt - 1)) / paramInt, 1);
    char[] arrayOfChar = new char[j];
    formatUnsignedLong(paramLong, paramInt, arrayOfChar, 0, j);
    return new String(arrayOfChar, true);
  }
  
  static int formatUnsignedLong(long paramLong, int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    int i = paramInt3;
    int j = 1 << paramInt1;
    int k = j - 1;
    do
    {
      paramArrayOfChar[(paramInt2 + --i)] = Integer.digits[((int)paramLong & k)];
      paramLong >>>= paramInt1;
    } while ((paramLong != 0L) && (i > 0));
    return i;
  }
  
  public static String toString(long paramLong)
  {
    if (paramLong == Long.MIN_VALUE) {
      return "-9223372036854775808";
    }
    int i = paramLong < 0L ? stringSize(-paramLong) + 1 : stringSize(paramLong);
    char[] arrayOfChar = new char[i];
    getChars(paramLong, i, arrayOfChar);
    return new String(arrayOfChar, true);
  }
  
  public static String toUnsignedString(long paramLong)
  {
    return toUnsignedString(paramLong, 10);
  }
  
  static void getChars(long paramLong, int paramInt, char[] paramArrayOfChar)
  {
    int j = paramInt;
    int k = 0;
    if (paramLong < 0L)
    {
      k = 45;
      paramLong = -paramLong;
    }
    int i;
    while (paramLong > 2147483647L)
    {
      long l = paramLong / 100L;
      i = (int)(paramLong - ((l << 6) + (l << 5) + (l << 2)));
      paramLong = l;
      paramArrayOfChar[(--j)] = Integer.DigitOnes[i];
      paramArrayOfChar[(--j)] = Integer.DigitTens[i];
    }
    int n = (int)paramLong;
    int m;
    while (n >= 65536)
    {
      m = n / 100;
      i = n - ((m << 6) + (m << 5) + (m << 2));
      n = m;
      paramArrayOfChar[(--j)] = Integer.DigitOnes[i];
      paramArrayOfChar[(--j)] = Integer.DigitTens[i];
    }
    for (;;)
    {
      m = n * 52429 >>> 19;
      i = n - ((m << 3) + (m << 1));
      paramArrayOfChar[(--j)] = Integer.digits[i];
      n = m;
      if (n == 0) {
        break;
      }
    }
    if (k != 0) {
      paramArrayOfChar[(--j)] = k;
    }
  }
  
  static int stringSize(long paramLong)
  {
    long l = 10L;
    for (int i = 1; i < 19; i++)
    {
      if (paramLong < l) {
        return i;
      }
      l = 10L * l;
    }
    return 19;
  }
  
  public static long parseLong(String paramString, int paramInt)
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
    long l1 = 0L;
    int i = 0;
    int j = 0;
    int k = paramString.length();
    long l2 = -9223372036854775807L;
    if (k > 0)
    {
      int n = paramString.charAt(0);
      if (n < 48)
      {
        if (n == 45)
        {
          i = 1;
          l2 = Long.MIN_VALUE;
        }
        else if (n != 43)
        {
          throw NumberFormatException.forInputString(paramString);
        }
        if (k == 1) {
          throw NumberFormatException.forInputString(paramString);
        }
        j++;
      }
      long l3 = l2 / paramInt;
      while (j < k)
      {
        int m = Character.digit(paramString.charAt(j++), paramInt);
        if (m < 0) {
          throw NumberFormatException.forInputString(paramString);
        }
        if (l1 < l3) {
          throw NumberFormatException.forInputString(paramString);
        }
        l1 *= paramInt;
        if (l1 < l2 + m) {
          throw NumberFormatException.forInputString(paramString);
        }
        l1 -= m;
      }
    }
    else
    {
      throw NumberFormatException.forInputString(paramString);
    }
    return i != 0 ? l1 : -l1;
  }
  
  public static long parseLong(String paramString)
    throws NumberFormatException
  {
    return parseLong(paramString, 10);
  }
  
  public static long parseUnsignedLong(String paramString, int paramInt)
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
      if ((i <= 12) || ((paramInt == 10) && (i <= 18))) {
        return parseLong(paramString, paramInt);
      }
      long l1 = parseLong(paramString.substring(0, i - 1), paramInt);
      int k = Character.digit(paramString.charAt(i - 1), paramInt);
      if (k < 0) {
        throw new NumberFormatException("Bad digit at end of " + paramString);
      }
      long l2 = l1 * paramInt + k;
      if (compareUnsigned(l2, l1) < 0) {
        throw new NumberFormatException(String.format("String value %s exceeds range of unsigned long.", new Object[] { paramString }));
      }
      return l2;
    }
    throw NumberFormatException.forInputString(paramString);
  }
  
  public static long parseUnsignedLong(String paramString)
    throws NumberFormatException
  {
    return parseUnsignedLong(paramString, 10);
  }
  
  public static Long valueOf(String paramString, int paramInt)
    throws NumberFormatException
  {
    return valueOf(parseLong(paramString, paramInt));
  }
  
  public static Long valueOf(String paramString)
    throws NumberFormatException
  {
    return valueOf(parseLong(paramString, 10));
  }
  
  public static Long valueOf(long paramLong)
  {
    if ((paramLong >= -128L) && (paramLong <= 127L)) {
      return LongCache.cache[((int)paramLong + 128)];
    }
    return new Long(paramLong);
  }
  
  public static Long decode(String paramString)
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
    Long localLong;
    try
    {
      localLong = valueOf(paramString.substring(j), i);
      localLong = k != 0 ? valueOf(-localLong.longValue()) : localLong;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      String str = k != 0 ? "-" + paramString.substring(j) : paramString.substring(j);
      localLong = valueOf(str, i);
    }
    return localLong;
  }
  
  public Long(long paramLong)
  {
    value = paramLong;
  }
  
  public Long(String paramString)
    throws NumberFormatException
  {
    value = parseLong(paramString, 10);
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
    return (float)value;
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
  
  public static int hashCode(long paramLong)
  {
    return (int)(paramLong ^ paramLong >>> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Long)) {
      return value == ((Long)paramObject).longValue();
    }
    return false;
  }
  
  public static Long getLong(String paramString)
  {
    return getLong(paramString, null);
  }
  
  public static Long getLong(String paramString, long paramLong)
  {
    Long localLong = getLong(paramString, null);
    return localLong == null ? valueOf(paramLong) : localLong;
  }
  
  public static Long getLong(String paramString, Long paramLong)
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
    return paramLong;
  }
  
  public int compareTo(Long paramLong)
  {
    return compare(value, value);
  }
  
  public static int compare(long paramLong1, long paramLong2)
  {
    return paramLong1 == paramLong2 ? 0 : paramLong1 < paramLong2 ? -1 : 1;
  }
  
  public static int compareUnsigned(long paramLong1, long paramLong2)
  {
    return compare(paramLong1 + Long.MIN_VALUE, paramLong2 + Long.MIN_VALUE);
  }
  
  public static long divideUnsigned(long paramLong1, long paramLong2)
  {
    if (paramLong2 < 0L) {
      return compareUnsigned(paramLong1, paramLong2) < 0 ? 0L : 1L;
    }
    if (paramLong1 > 0L) {
      return paramLong1 / paramLong2;
    }
    return toUnsignedBigInteger(paramLong1).divide(toUnsignedBigInteger(paramLong2)).longValue();
  }
  
  public static long remainderUnsigned(long paramLong1, long paramLong2)
  {
    if ((paramLong1 > 0L) && (paramLong2 > 0L)) {
      return paramLong1 % paramLong2;
    }
    if (compareUnsigned(paramLong1, paramLong2) < 0) {
      return paramLong1;
    }
    return toUnsignedBigInteger(paramLong1).remainder(toUnsignedBigInteger(paramLong2)).longValue();
  }
  
  public static long highestOneBit(long paramLong)
  {
    paramLong |= paramLong >> 1;
    paramLong |= paramLong >> 2;
    paramLong |= paramLong >> 4;
    paramLong |= paramLong >> 8;
    paramLong |= paramLong >> 16;
    paramLong |= paramLong >> 32;
    return paramLong - (paramLong >>> 1);
  }
  
  public static long lowestOneBit(long paramLong)
  {
    return paramLong & -paramLong;
  }
  
  public static int numberOfLeadingZeros(long paramLong)
  {
    if (paramLong == 0L) {
      return 64;
    }
    int i = 1;
    int j = (int)(paramLong >>> 32);
    if (j == 0)
    {
      i += 32;
      j = (int)paramLong;
    }
    if (j >>> 16 == 0)
    {
      i += 16;
      j <<= 16;
    }
    if (j >>> 24 == 0)
    {
      i += 8;
      j <<= 8;
    }
    if (j >>> 28 == 0)
    {
      i += 4;
      j <<= 4;
    }
    if (j >>> 30 == 0)
    {
      i += 2;
      j <<= 2;
    }
    i -= (j >>> 31);
    return i;
  }
  
  public static int numberOfTrailingZeros(long paramLong)
  {
    if (paramLong == 0L) {
      return 64;
    }
    int k = 63;
    int j = (int)paramLong;
    int i;
    if (j != 0)
    {
      k -= 32;
      i = j;
    }
    else
    {
      i = (int)(paramLong >>> 32);
    }
    j = i << 16;
    if (j != 0)
    {
      k -= 16;
      i = j;
    }
    j = i << 8;
    if (j != 0)
    {
      k -= 8;
      i = j;
    }
    j = i << 4;
    if (j != 0)
    {
      k -= 4;
      i = j;
    }
    j = i << 2;
    if (j != 0)
    {
      k -= 2;
      i = j;
    }
    return k - (i << 1 >>> 31);
  }
  
  public static int bitCount(long paramLong)
  {
    paramLong -= (paramLong >>> 1 & 0x5555555555555555);
    paramLong = (paramLong & 0x3333333333333333) + (paramLong >>> 2 & 0x3333333333333333);
    paramLong = paramLong + (paramLong >>> 4) & 0xF0F0F0F0F0F0F0F;
    paramLong += (paramLong >>> 8);
    paramLong += (paramLong >>> 16);
    paramLong += (paramLong >>> 32);
    return (int)paramLong & 0x7F;
  }
  
  public static long rotateLeft(long paramLong, int paramInt)
  {
    return paramLong << paramInt | paramLong >>> -paramInt;
  }
  
  public static long rotateRight(long paramLong, int paramInt)
  {
    return paramLong >>> paramInt | paramLong << -paramInt;
  }
  
  public static long reverse(long paramLong)
  {
    paramLong = (paramLong & 0x5555555555555555) << 1 | paramLong >>> 1 & 0x5555555555555555;
    paramLong = (paramLong & 0x3333333333333333) << 2 | paramLong >>> 2 & 0x3333333333333333;
    paramLong = (paramLong & 0xF0F0F0F0F0F0F0F) << 4 | paramLong >>> 4 & 0xF0F0F0F0F0F0F0F;
    paramLong = (paramLong & 0xFF00FF00FF00FF) << 8 | paramLong >>> 8 & 0xFF00FF00FF00FF;
    paramLong = paramLong << 48 | (paramLong & 0xFFFF0000) << 16 | paramLong >>> 16 & 0xFFFF0000 | paramLong >>> 48;
    return paramLong;
  }
  
  public static int signum(long paramLong)
  {
    return (int)(paramLong >> 63 | -paramLong >>> 63);
  }
  
  public static long reverseBytes(long paramLong)
  {
    paramLong = (paramLong & 0xFF00FF00FF00FF) << 8 | paramLong >>> 8 & 0xFF00FF00FF00FF;
    return paramLong << 48 | (paramLong & 0xFFFF0000) << 16 | paramLong >>> 16 & 0xFFFF0000 | paramLong >>> 48;
  }
  
  public static long sum(long paramLong1, long paramLong2)
  {
    return paramLong1 + paramLong2;
  }
  
  public static long max(long paramLong1, long paramLong2)
  {
    return Math.max(paramLong1, paramLong2);
  }
  
  public static long min(long paramLong1, long paramLong2)
  {
    return Math.min(paramLong1, paramLong2);
  }
  
  private static class LongCache
  {
    static final Long[] cache = new Long['Ā'];
    
    private LongCache() {}
    
    static
    {
      for (int i = 0; i < cache.length; i++) {
        cache[i] = new Long(i - 128);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Long.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
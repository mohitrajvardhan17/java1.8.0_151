package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import sun.misc.Unsafe;

public class BigDecimal
  extends Number
  implements Comparable<BigDecimal>
{
  private final BigInteger intVal;
  private final int scale;
  private transient int precision;
  private transient String stringCache;
  static final long INFLATED = Long.MIN_VALUE;
  private static final BigInteger INFLATED_BIGINT = BigInteger.valueOf(Long.MIN_VALUE);
  private final transient long intCompact;
  private static final int MAX_COMPACT_DIGITS = 18;
  private static final long serialVersionUID = 6108874887143696463L;
  private static final ThreadLocal<StringBuilderHelper> threadLocalStringBuilderHelper = new ThreadLocal()
  {
    protected BigDecimal.StringBuilderHelper initialValue()
    {
      return new BigDecimal.StringBuilderHelper();
    }
  };
  private static final BigDecimal[] zeroThroughTen = { new BigDecimal(BigInteger.ZERO, 0L, 0, 1), new BigDecimal(BigInteger.ONE, 1L, 0, 1), new BigDecimal(BigInteger.valueOf(2L), 2L, 0, 1), new BigDecimal(BigInteger.valueOf(3L), 3L, 0, 1), new BigDecimal(BigInteger.valueOf(4L), 4L, 0, 1), new BigDecimal(BigInteger.valueOf(5L), 5L, 0, 1), new BigDecimal(BigInteger.valueOf(6L), 6L, 0, 1), new BigDecimal(BigInteger.valueOf(7L), 7L, 0, 1), new BigDecimal(BigInteger.valueOf(8L), 8L, 0, 1), new BigDecimal(BigInteger.valueOf(9L), 9L, 0, 1), new BigDecimal(BigInteger.TEN, 10L, 0, 2) };
  private static final BigDecimal[] ZERO_SCALED_BY = { zeroThroughTen[0], new BigDecimal(BigInteger.ZERO, 0L, 1, 1), new BigDecimal(BigInteger.ZERO, 0L, 2, 1), new BigDecimal(BigInteger.ZERO, 0L, 3, 1), new BigDecimal(BigInteger.ZERO, 0L, 4, 1), new BigDecimal(BigInteger.ZERO, 0L, 5, 1), new BigDecimal(BigInteger.ZERO, 0L, 6, 1), new BigDecimal(BigInteger.ZERO, 0L, 7, 1), new BigDecimal(BigInteger.ZERO, 0L, 8, 1), new BigDecimal(BigInteger.ZERO, 0L, 9, 1), new BigDecimal(BigInteger.ZERO, 0L, 10, 1), new BigDecimal(BigInteger.ZERO, 0L, 11, 1), new BigDecimal(BigInteger.ZERO, 0L, 12, 1), new BigDecimal(BigInteger.ZERO, 0L, 13, 1), new BigDecimal(BigInteger.ZERO, 0L, 14, 1), new BigDecimal(BigInteger.ZERO, 0L, 15, 1) };
  private static final long HALF_LONG_MAX_VALUE = 4611686018427387903L;
  private static final long HALF_LONG_MIN_VALUE = -4611686018427387904L;
  public static final BigDecimal ZERO = zeroThroughTen[0];
  public static final BigDecimal ONE = zeroThroughTen[1];
  public static final BigDecimal TEN = zeroThroughTen[10];
  public static final int ROUND_UP = 0;
  public static final int ROUND_DOWN = 1;
  public static final int ROUND_CEILING = 2;
  public static final int ROUND_FLOOR = 3;
  public static final int ROUND_HALF_UP = 4;
  public static final int ROUND_HALF_DOWN = 5;
  public static final int ROUND_HALF_EVEN = 6;
  public static final int ROUND_UNNECESSARY = 7;
  private static final double[] double10pow = { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D };
  private static final float[] float10pow = { 1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 1.0E10F };
  private static final long[] LONG_TEN_POWERS_TABLE = { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
  private static volatile BigInteger[] BIG_TEN_POWERS_TABLE = { BigInteger.ONE, BigInteger.valueOf(10L), BigInteger.valueOf(100L), BigInteger.valueOf(1000L), BigInteger.valueOf(10000L), BigInteger.valueOf(100000L), BigInteger.valueOf(1000000L), BigInteger.valueOf(10000000L), BigInteger.valueOf(100000000L), BigInteger.valueOf(1000000000L), BigInteger.valueOf(10000000000L), BigInteger.valueOf(100000000000L), BigInteger.valueOf(1000000000000L), BigInteger.valueOf(10000000000000L), BigInteger.valueOf(100000000000000L), BigInteger.valueOf(1000000000000000L), BigInteger.valueOf(10000000000000000L), BigInteger.valueOf(100000000000000000L), BigInteger.valueOf(1000000000000000000L) };
  private static final int BIG_TEN_POWERS_TABLE_INITLEN = BIG_TEN_POWERS_TABLE.length;
  private static final int BIG_TEN_POWERS_TABLE_MAX = 16 * BIG_TEN_POWERS_TABLE_INITLEN;
  private static final long[] THRESHOLDS_TABLE = { Long.MAX_VALUE, 922337203685477580L, 92233720368547758L, 9223372036854775L, 922337203685477L, 92233720368547L, 9223372036854L, 922337203685L, 92233720368L, 9223372036L, 922337203L, 92233720L, 9223372L, 922337L, 92233L, 9223L, 922L, 92L, 9L };
  private static final long DIV_NUM_BASE = 4294967296L;
  private static final long[][] LONGLONG_TEN_POWERS_TABLE = { { 0L, -8446744073709551616L }, { 5L, 7766279631452241920L }, { 54L, 3875820019684212736L }, { 542L, 1864712049423024128L }, { 5421L, 200376420520689664L }, { 54210L, 2003764205206896640L }, { 542101L, 1590897978359414784L }, { 5421010L, -2537764290115403776L }, { 54210108L, -6930898827444486144L }, { 542101086L, 4477988020393345024L }, { 5421010862L, 7886392056514347008L }, { 54210108624L, 5076944270305263616L }, { 542101086242L, -4570789518076018688L }, { 5421010862427L, -8814407033341083648L }, { 54210108624275L, 4089650035136921600L }, { 542101086242752L, 4003012203950112768L }, { 5421010862427522L, 3136633892082024448L }, { 54210108624275221L, -5527149226598858752L }, { 542101086242752217L, 68739955140067328L }, { 5421010862427522170L, 687399551400673280L } };
  
  BigDecimal(BigInteger paramBigInteger, long paramLong, int paramInt1, int paramInt2)
  {
    scale = paramInt1;
    precision = paramInt2;
    intCompact = paramLong;
    intVal = paramBigInteger;
  }
  
  public BigDecimal(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    this(paramArrayOfChar, paramInt1, paramInt2, MathContext.UNLIMITED);
  }
  
  public BigDecimal(char[] paramArrayOfChar, int paramInt1, int paramInt2, MathContext paramMathContext)
  {
    if ((paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 < 0)) {
      throw new NumberFormatException("Bad offset or len arguments for char[] input.");
    }
    int i = 0;
    int j = 0;
    long l1 = 0L;
    BigInteger localBigInteger = null;
    try
    {
      int k = 0;
      if (paramArrayOfChar[paramInt1] == '-')
      {
        k = 1;
        paramInt1++;
        paramInt2--;
      }
      else if (paramArrayOfChar[paramInt1] == '+')
      {
        paramInt1++;
        paramInt2--;
      }
      int m = 0;
      long l2 = 0L;
      int n = paramInt2 <= 18 ? 1 : 0;
      int i1 = 0;
      char c;
      int i3;
      if (n != 0)
      {
        while (paramInt2 > 0)
        {
          c = paramArrayOfChar[paramInt1];
          if (c == '0')
          {
            if (i == 0)
            {
              i = 1;
            }
            else if (l1 != 0L)
            {
              l1 *= 10L;
              i++;
            }
            if (m != 0) {
              j++;
            }
          }
          else if ((c >= '1') && (c <= '9'))
          {
            i2 = c - '0';
            if ((i != 1) || (l1 != 0L)) {
              i++;
            }
            l1 = l1 * 10L + i2;
            if (m != 0) {
              j++;
            }
          }
          else if (c == '.')
          {
            if (m != 0) {
              throw new NumberFormatException();
            }
            m = 1;
          }
          else if (Character.isDigit(c))
          {
            i2 = Character.digit(c, 10);
            if (i2 == 0)
            {
              if (i == 0)
              {
                i = 1;
              }
              else if (l1 != 0L)
              {
                l1 *= 10L;
                i++;
              }
            }
            else
            {
              if ((i != 1) || (l1 != 0L)) {
                i++;
              }
              l1 = l1 * 10L + i2;
            }
            if (m != 0) {
              j++;
            }
          }
          else
          {
            if ((c == 'e') || (c == 'E'))
            {
              l2 = parseExp(paramArrayOfChar, paramInt1, paramInt2);
              if ((int)l2 == l2) {
                break;
              }
              throw new NumberFormatException();
            }
            throw new NumberFormatException();
          }
          paramInt1++;
          paramInt2--;
        }
        if (i == 0) {
          throw new NumberFormatException();
        }
        if (l2 != 0L) {
          j = adjustScale(j, l2);
        }
        l1 = k != 0 ? -l1 : l1;
        int i2 = precision;
        i3 = i - i2;
        if ((i2 > 0) && (i3 > 0)) {
          while (i3 > 0)
          {
            j = checkScaleNonZero(j - i3);
            l1 = divideAndRound(l1, LONG_TEN_POWERS_TABLE[i3], roundingMode.oldMode);
            i = longDigitLength(l1);
            i3 = i - i2;
          }
        }
      }
      else
      {
        char[] arrayOfChar = new char[paramInt2];
        while (paramInt2 > 0)
        {
          c = paramArrayOfChar[paramInt1];
          if (((c >= '0') && (c <= '9')) || (Character.isDigit(c)))
          {
            if ((c == '0') || (Character.digit(c, 10) == 0))
            {
              if (i == 0)
              {
                arrayOfChar[i1] = c;
                i = 1;
              }
              else if (i1 != 0)
              {
                arrayOfChar[(i1++)] = c;
                i++;
              }
            }
            else
            {
              if ((i != 1) || (i1 != 0)) {
                i++;
              }
              arrayOfChar[(i1++)] = c;
            }
            if (m != 0) {
              j++;
            }
          }
          else if (c == '.')
          {
            if (m != 0) {
              throw new NumberFormatException();
            }
            m = 1;
          }
          else
          {
            if ((c != 'e') && (c != 'E')) {
              throw new NumberFormatException();
            }
            l2 = parseExp(paramArrayOfChar, paramInt1, paramInt2);
            if ((int)l2 == l2) {
              break;
            }
            throw new NumberFormatException();
          }
          paramInt1++;
          paramInt2--;
        }
        if (i == 0) {
          throw new NumberFormatException();
        }
        if (l2 != 0L) {
          j = adjustScale(j, l2);
        }
        localBigInteger = new BigInteger(arrayOfChar, k != 0 ? -1 : 1, i);
        l1 = compactValFor(localBigInteger);
        i3 = precision;
        if ((i3 > 0) && (i > i3))
        {
          int i4;
          if (l1 == Long.MIN_VALUE) {
            for (i4 = i - i3; i4 > 0; i4 = i - i3)
            {
              j = checkScaleNonZero(j - i4);
              localBigInteger = divideAndRoundByTenPow(localBigInteger, i4, roundingMode.oldMode);
              l1 = compactValFor(localBigInteger);
              if (l1 != Long.MIN_VALUE)
              {
                i = longDigitLength(l1);
                break;
              }
              i = bigDigitLength(localBigInteger);
            }
          }
          if (l1 != Long.MIN_VALUE)
          {
            for (i4 = i - i3; i4 > 0; i4 = i - i3)
            {
              j = checkScaleNonZero(j - i4);
              l1 = divideAndRound(l1, LONG_TEN_POWERS_TABLE[i4], roundingMode.oldMode);
              i = longDigitLength(l1);
            }
            localBigInteger = null;
          }
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new NumberFormatException();
    }
    catch (NegativeArraySizeException localNegativeArraySizeException)
    {
      throw new NumberFormatException();
    }
    scale = j;
    precision = i;
    intCompact = l1;
    intVal = localBigInteger;
  }
  
  private int adjustScale(int paramInt, long paramLong)
  {
    long l = paramInt - paramLong;
    if ((l > 2147483647L) || (l < -2147483648L)) {
      throw new NumberFormatException("Scale out of range.");
    }
    paramInt = (int)l;
    return paramInt;
  }
  
  private static long parseExp(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    long l = 0L;
    paramInt1++;
    char c = paramArrayOfChar[paramInt1];
    paramInt2--;
    int i = c == '-' ? 1 : 0;
    if ((i != 0) || (c == '+'))
    {
      paramInt1++;
      c = paramArrayOfChar[paramInt1];
      paramInt2--;
    }
    if (paramInt2 <= 0) {
      throw new NumberFormatException();
    }
    while ((paramInt2 > 10) && ((c == '0') || (Character.digit(c, 10) == 0)))
    {
      paramInt1++;
      c = paramArrayOfChar[paramInt1];
      paramInt2--;
    }
    if (paramInt2 > 10) {
      throw new NumberFormatException();
    }
    for (;;)
    {
      int j;
      if ((c >= '0') && (c <= '9'))
      {
        j = c - '0';
      }
      else
      {
        j = Character.digit(c, 10);
        if (j < 0) {
          throw new NumberFormatException();
        }
      }
      l = l * 10L + j;
      if (paramInt2 == 1) {
        break;
      }
      paramInt1++;
      c = paramArrayOfChar[paramInt1];
      paramInt2--;
    }
    if (i != 0) {
      l = -l;
    }
    return l;
  }
  
  public BigDecimal(char[] paramArrayOfChar)
  {
    this(paramArrayOfChar, 0, paramArrayOfChar.length);
  }
  
  public BigDecimal(char[] paramArrayOfChar, MathContext paramMathContext)
  {
    this(paramArrayOfChar, 0, paramArrayOfChar.length, paramMathContext);
  }
  
  public BigDecimal(String paramString)
  {
    this(paramString.toCharArray(), 0, paramString.length());
  }
  
  public BigDecimal(String paramString, MathContext paramMathContext)
  {
    this(paramString.toCharArray(), 0, paramString.length(), paramMathContext);
  }
  
  public BigDecimal(double paramDouble)
  {
    this(paramDouble, MathContext.UNLIMITED);
  }
  
  public BigDecimal(double paramDouble, MathContext paramMathContext)
  {
    if ((Double.isInfinite(paramDouble)) || (Double.isNaN(paramDouble))) {
      throw new NumberFormatException("Infinite or NaN");
    }
    long l1 = Double.doubleToLongBits(paramDouble);
    int i = l1 >> 63 == 0L ? 1 : -1;
    int j = (int)(l1 >> 52 & 0x7FF);
    long l2 = j == 0 ? (l1 & 0xFFFFFFFFFFFFF) << 1 : l1 & 0xFFFFFFFFFFFFF | 0x10000000000000;
    j -= 1075;
    if (l2 == 0L)
    {
      intVal = BigInteger.ZERO;
      scale = 0;
      intCompact = 0L;
      precision = 1;
      return;
    }
    while ((l2 & 1L) == 0L)
    {
      l2 >>= 1;
      j++;
    }
    int k = 0;
    long l3 = i * l2;
    BigInteger localBigInteger;
    if (j == 0)
    {
      localBigInteger = l3 == Long.MIN_VALUE ? INFLATED_BIGINT : null;
    }
    else
    {
      if (j < 0)
      {
        localBigInteger = BigInteger.valueOf(5L).pow(-j).multiply(l3);
        k = -j;
      }
      else
      {
        localBigInteger = BigInteger.valueOf(2L).pow(j).multiply(l3);
      }
      l3 = compactValFor(localBigInteger);
    }
    int m = 0;
    int n = precision;
    if (n > 0)
    {
      int i1 = roundingMode.oldMode;
      int i2;
      if (l3 == Long.MIN_VALUE)
      {
        m = bigDigitLength(localBigInteger);
        for (i2 = m - n; i2 > 0; i2 = m - n)
        {
          k = checkScaleNonZero(k - i2);
          localBigInteger = divideAndRoundByTenPow(localBigInteger, i2, i1);
          l3 = compactValFor(localBigInteger);
          if (l3 != Long.MIN_VALUE) {
            break;
          }
          m = bigDigitLength(localBigInteger);
        }
      }
      if (l3 != Long.MIN_VALUE)
      {
        m = longDigitLength(l3);
        for (i2 = m - n; i2 > 0; i2 = m - n)
        {
          k = checkScaleNonZero(k - i2);
          l3 = divideAndRound(l3, LONG_TEN_POWERS_TABLE[i2], roundingMode.oldMode);
          m = longDigitLength(l3);
        }
        localBigInteger = null;
      }
    }
    intVal = localBigInteger;
    intCompact = l3;
    scale = k;
    precision = m;
  }
  
  public BigDecimal(BigInteger paramBigInteger)
  {
    scale = 0;
    intVal = paramBigInteger;
    intCompact = compactValFor(paramBigInteger);
  }
  
  public BigDecimal(BigInteger paramBigInteger, MathContext paramMathContext)
  {
    this(paramBigInteger, 0, paramMathContext);
  }
  
  public BigDecimal(BigInteger paramBigInteger, int paramInt)
  {
    intVal = paramBigInteger;
    intCompact = compactValFor(paramBigInteger);
    scale = paramInt;
  }
  
  public BigDecimal(BigInteger paramBigInteger, int paramInt, MathContext paramMathContext)
  {
    long l = compactValFor(paramBigInteger);
    int i = precision;
    int j = 0;
    if (i > 0)
    {
      int k = roundingMode.oldMode;
      int m;
      if (l == Long.MIN_VALUE)
      {
        j = bigDigitLength(paramBigInteger);
        for (m = j - i; m > 0; m = j - i)
        {
          paramInt = checkScaleNonZero(paramInt - m);
          paramBigInteger = divideAndRoundByTenPow(paramBigInteger, m, k);
          l = compactValFor(paramBigInteger);
          if (l != Long.MIN_VALUE) {
            break;
          }
          j = bigDigitLength(paramBigInteger);
        }
      }
      if (l != Long.MIN_VALUE)
      {
        j = longDigitLength(l);
        for (m = j - i; m > 0; m = j - i)
        {
          paramInt = checkScaleNonZero(paramInt - m);
          l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], k);
          j = longDigitLength(l);
        }
        paramBigInteger = null;
      }
    }
    intVal = paramBigInteger;
    intCompact = l;
    scale = paramInt;
    precision = j;
  }
  
  public BigDecimal(int paramInt)
  {
    intCompact = paramInt;
    scale = 0;
    intVal = null;
  }
  
  public BigDecimal(int paramInt, MathContext paramMathContext)
  {
    int i = precision;
    long l = paramInt;
    int j = 0;
    int k = 0;
    if (i > 0)
    {
      k = longDigitLength(l);
      for (int m = k - i; m > 0; m = k - i)
      {
        j = checkScaleNonZero(j - m);
        l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], roundingMode.oldMode);
        k = longDigitLength(l);
      }
    }
    intVal = null;
    intCompact = l;
    scale = j;
    precision = k;
  }
  
  public BigDecimal(long paramLong)
  {
    intCompact = paramLong;
    intVal = (paramLong == Long.MIN_VALUE ? INFLATED_BIGINT : null);
    scale = 0;
  }
  
  public BigDecimal(long paramLong, MathContext paramMathContext)
  {
    int i = precision;
    int j = roundingMode.oldMode;
    int k = 0;
    int m = 0;
    BigInteger localBigInteger = paramLong == Long.MIN_VALUE ? INFLATED_BIGINT : null;
    if (i > 0)
    {
      int n;
      if (paramLong == Long.MIN_VALUE)
      {
        k = 19;
        for (n = k - i; n > 0; n = k - i)
        {
          m = checkScaleNonZero(m - n);
          localBigInteger = divideAndRoundByTenPow(localBigInteger, n, j);
          paramLong = compactValFor(localBigInteger);
          if (paramLong != Long.MIN_VALUE) {
            break;
          }
          k = bigDigitLength(localBigInteger);
        }
      }
      if (paramLong != Long.MIN_VALUE)
      {
        k = longDigitLength(paramLong);
        for (n = k - i; n > 0; n = k - i)
        {
          m = checkScaleNonZero(m - n);
          paramLong = divideAndRound(paramLong, LONG_TEN_POWERS_TABLE[n], roundingMode.oldMode);
          k = longDigitLength(paramLong);
        }
        localBigInteger = null;
      }
    }
    intVal = localBigInteger;
    intCompact = paramLong;
    scale = m;
    precision = k;
  }
  
  public static BigDecimal valueOf(long paramLong, int paramInt)
  {
    if (paramInt == 0) {
      return valueOf(paramLong);
    }
    if (paramLong == 0L) {
      return zeroValueOf(paramInt);
    }
    return new BigDecimal(paramLong == Long.MIN_VALUE ? INFLATED_BIGINT : null, paramLong, paramInt, 0);
  }
  
  public static BigDecimal valueOf(long paramLong)
  {
    if ((paramLong >= 0L) && (paramLong < zeroThroughTen.length)) {
      return zeroThroughTen[((int)paramLong)];
    }
    if (paramLong != Long.MIN_VALUE) {
      return new BigDecimal(null, paramLong, 0, 0);
    }
    return new BigDecimal(INFLATED_BIGINT, paramLong, 0, 0);
  }
  
  static BigDecimal valueOf(long paramLong, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramLong >= 0L) && (paramLong < zeroThroughTen.length)) {
      return zeroThroughTen[((int)paramLong)];
    }
    if (paramLong == 0L) {
      return zeroValueOf(paramInt1);
    }
    return new BigDecimal(paramLong == Long.MIN_VALUE ? INFLATED_BIGINT : null, paramLong, paramInt1, paramInt2);
  }
  
  static BigDecimal valueOf(BigInteger paramBigInteger, int paramInt1, int paramInt2)
  {
    long l = compactValFor(paramBigInteger);
    if (l == 0L) {
      return zeroValueOf(paramInt1);
    }
    if ((paramInt1 == 0) && (l >= 0L) && (l < zeroThroughTen.length)) {
      return zeroThroughTen[((int)l)];
    }
    return new BigDecimal(paramBigInteger, l, paramInt1, paramInt2);
  }
  
  static BigDecimal zeroValueOf(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < ZERO_SCALED_BY.length)) {
      return ZERO_SCALED_BY[paramInt];
    }
    return new BigDecimal(BigInteger.ZERO, 0L, paramInt, 1);
  }
  
  public static BigDecimal valueOf(double paramDouble)
  {
    return new BigDecimal(Double.toString(paramDouble));
  }
  
  public BigDecimal add(BigDecimal paramBigDecimal)
  {
    if (intCompact != Long.MIN_VALUE)
    {
      if (intCompact != Long.MIN_VALUE) {
        return add(intCompact, scale, intCompact, scale);
      }
      return add(intCompact, scale, intVal, scale);
    }
    if (intCompact != Long.MIN_VALUE) {
      return add(intCompact, scale, intVal, scale);
    }
    return add(intVal, scale, intVal, scale);
  }
  
  public BigDecimal add(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    if (precision == 0) {
      return add(paramBigDecimal);
    }
    BigDecimal localBigDecimal1 = this;
    int i = localBigDecimal1.signum() == 0 ? 1 : 0;
    int j = paramBigDecimal.signum() == 0 ? 1 : 0;
    if ((i != 0) || (j != 0))
    {
      int k = Math.max(localBigDecimal1.scale(), paramBigDecimal.scale());
      if ((i != 0) && (j != 0)) {
        return zeroValueOf(k);
      }
      BigDecimal localBigDecimal2 = i != 0 ? doRound(paramBigDecimal, paramMathContext) : doRound(localBigDecimal1, paramMathContext);
      if (localBigDecimal2.scale() == k) {
        return localBigDecimal2;
      }
      if (localBigDecimal2.scale() > k) {
        return stripZerosToMatchScale(intVal, intCompact, scale, k);
      }
      int m = precision - localBigDecimal2.precision();
      int n = k - localBigDecimal2.scale();
      if (m >= n) {
        return localBigDecimal2.setScale(k);
      }
      return localBigDecimal2.setScale(localBigDecimal2.scale() + m);
    }
    long l = scale - scale;
    if (l != 0L)
    {
      BigDecimal[] arrayOfBigDecimal = preAlign(localBigDecimal1, paramBigDecimal, l, paramMathContext);
      matchScale(arrayOfBigDecimal);
      localBigDecimal1 = arrayOfBigDecimal[0];
      paramBigDecimal = arrayOfBigDecimal[1];
    }
    return doRound(localBigDecimal1.inflated().add(paramBigDecimal.inflated()), scale, paramMathContext);
  }
  
  private BigDecimal[] preAlign(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, long paramLong, MathContext paramMathContext)
  {
    assert (paramLong != 0L);
    BigDecimal localBigDecimal1;
    BigDecimal localBigDecimal2;
    if (paramLong < 0L)
    {
      localBigDecimal1 = paramBigDecimal1;
      localBigDecimal2 = paramBigDecimal2;
    }
    else
    {
      localBigDecimal1 = paramBigDecimal2;
      localBigDecimal2 = paramBigDecimal1;
    }
    long l1 = scale - localBigDecimal1.precision() + precision;
    long l2 = scale - localBigDecimal2.precision() + 1L;
    if ((l2 > scale + 2) && (l2 > l1 + 2L)) {
      localBigDecimal2 = valueOf(localBigDecimal2.signum(), checkScale(Math.max(scale, l1) + 3L));
    }
    BigDecimal[] arrayOfBigDecimal = { localBigDecimal1, localBigDecimal2 };
    return arrayOfBigDecimal;
  }
  
  public BigDecimal subtract(BigDecimal paramBigDecimal)
  {
    if (intCompact != Long.MIN_VALUE)
    {
      if (intCompact != Long.MIN_VALUE) {
        return add(intCompact, scale, -intCompact, scale);
      }
      return add(intCompact, scale, intVal.negate(), scale);
    }
    if (intCompact != Long.MIN_VALUE) {
      return add(-intCompact, scale, intVal, scale);
    }
    return add(intVal, scale, intVal.negate(), scale);
  }
  
  public BigDecimal subtract(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    if (precision == 0) {
      return subtract(paramBigDecimal);
    }
    return add(paramBigDecimal.negate(), paramMathContext);
  }
  
  public BigDecimal multiply(BigDecimal paramBigDecimal)
  {
    int i = checkScale(scale + scale);
    if (intCompact != Long.MIN_VALUE)
    {
      if (intCompact != Long.MIN_VALUE) {
        return multiply(intCompact, intCompact, i);
      }
      return multiply(intCompact, intVal, i);
    }
    if (intCompact != Long.MIN_VALUE) {
      return multiply(intCompact, intVal, i);
    }
    return multiply(intVal, intVal, i);
  }
  
  public BigDecimal multiply(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    if (precision == 0) {
      return multiply(paramBigDecimal);
    }
    int i = checkScale(scale + scale);
    if (intCompact != Long.MIN_VALUE)
    {
      if (intCompact != Long.MIN_VALUE) {
        return multiplyAndRound(intCompact, intCompact, i, paramMathContext);
      }
      return multiplyAndRound(intCompact, intVal, i, paramMathContext);
    }
    if (intCompact != Long.MIN_VALUE) {
      return multiplyAndRound(intCompact, intVal, i, paramMathContext);
    }
    return multiplyAndRound(intVal, intVal, i, paramMathContext);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt2 > 7)) {
      throw new IllegalArgumentException("Invalid rounding mode");
    }
    if (intCompact != Long.MIN_VALUE)
    {
      if (intCompact != Long.MIN_VALUE) {
        return divide(intCompact, scale, intCompact, scale, paramInt1, paramInt2);
      }
      return divide(intCompact, scale, intVal, scale, paramInt1, paramInt2);
    }
    if (intCompact != Long.MIN_VALUE) {
      return divide(intVal, scale, intCompact, scale, paramInt1, paramInt2);
    }
    return divide(intVal, scale, intVal, scale, paramInt1, paramInt2);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, int paramInt, RoundingMode paramRoundingMode)
  {
    return divide(paramBigDecimal, paramInt, oldMode);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, int paramInt)
  {
    return divide(paramBigDecimal, scale, paramInt);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, RoundingMode paramRoundingMode)
  {
    return divide(paramBigDecimal, scale, oldMode);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal)
  {
    if (paramBigDecimal.signum() == 0)
    {
      if (signum() == 0) {
        throw new ArithmeticException("Division undefined");
      }
      throw new ArithmeticException("Division by zero");
    }
    int i = saturateLong(scale - scale);
    if (signum() == 0) {
      return zeroValueOf(i);
    }
    MathContext localMathContext = new MathContext((int)Math.min(precision() + Math.ceil(10.0D * paramBigDecimal.precision() / 3.0D), 2147483647L), RoundingMode.UNNECESSARY);
    BigDecimal localBigDecimal;
    try
    {
      localBigDecimal = divide(paramBigDecimal, localMathContext);
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new ArithmeticException("Non-terminating decimal expansion; no exact representable decimal result.");
    }
    int j = localBigDecimal.scale();
    if (i > j) {
      return localBigDecimal.setScale(i, 7);
    }
    return localBigDecimal;
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    int i = precision;
    if (i == 0) {
      return divide(paramBigDecimal);
    }
    BigDecimal localBigDecimal = this;
    long l = scale - scale;
    if (paramBigDecimal.signum() == 0)
    {
      if (localBigDecimal.signum() == 0) {
        throw new ArithmeticException("Division undefined");
      }
      throw new ArithmeticException("Division by zero");
    }
    if (localBigDecimal.signum() == 0) {
      return zeroValueOf(saturateLong(l));
    }
    int j = localBigDecimal.precision();
    int k = paramBigDecimal.precision();
    if (intCompact != Long.MIN_VALUE)
    {
      if (intCompact != Long.MIN_VALUE) {
        return divide(intCompact, j, intCompact, k, l, paramMathContext);
      }
      return divide(intCompact, j, intVal, k, l, paramMathContext);
    }
    if (intCompact != Long.MIN_VALUE) {
      return divide(intVal, j, intCompact, k, l, paramMathContext);
    }
    return divide(intVal, j, intVal, k, l, paramMathContext);
  }
  
  public BigDecimal divideToIntegralValue(BigDecimal paramBigDecimal)
  {
    int i = saturateLong(scale - scale);
    if (compareMagnitude(paramBigDecimal) < 0) {
      return zeroValueOf(i);
    }
    if ((signum() == 0) && (paramBigDecimal.signum() != 0)) {
      return setScale(i, 7);
    }
    int j = (int)Math.min(precision() + Math.ceil(10.0D * paramBigDecimal.precision() / 3.0D) + Math.abs(scale() - paramBigDecimal.scale()) + 2L, 2147483647L);
    BigDecimal localBigDecimal = divide(paramBigDecimal, new MathContext(j, RoundingMode.DOWN));
    if (scale > 0)
    {
      localBigDecimal = localBigDecimal.setScale(0, RoundingMode.DOWN);
      localBigDecimal = stripZerosToMatchScale(intVal, intCompact, scale, i);
    }
    if (scale < i) {
      localBigDecimal = localBigDecimal.setScale(i, 7);
    }
    return localBigDecimal;
  }
  
  public BigDecimal divideToIntegralValue(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    if ((precision == 0) || (compareMagnitude(paramBigDecimal) < 0)) {
      return divideToIntegralValue(paramBigDecimal);
    }
    int i = saturateLong(scale - scale);
    BigDecimal localBigDecimal1 = divide(paramBigDecimal, new MathContext(precision, RoundingMode.DOWN));
    if (localBigDecimal1.scale() < 0)
    {
      BigDecimal localBigDecimal2 = localBigDecimal1.multiply(paramBigDecimal);
      if (subtract(localBigDecimal2).compareMagnitude(paramBigDecimal) >= 0) {
        throw new ArithmeticException("Division impossible");
      }
    }
    else if (localBigDecimal1.scale() > 0)
    {
      localBigDecimal1 = localBigDecimal1.setScale(0, RoundingMode.DOWN);
    }
    int j;
    if ((i > localBigDecimal1.scale()) && ((j = precision - localBigDecimal1.precision()) > 0)) {
      return localBigDecimal1.setScale(localBigDecimal1.scale() + Math.min(j, i - scale));
    }
    return stripZerosToMatchScale(intVal, intCompact, scale, i);
  }
  
  public BigDecimal remainder(BigDecimal paramBigDecimal)
  {
    BigDecimal[] arrayOfBigDecimal = divideAndRemainder(paramBigDecimal);
    return arrayOfBigDecimal[1];
  }
  
  public BigDecimal remainder(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    BigDecimal[] arrayOfBigDecimal = divideAndRemainder(paramBigDecimal, paramMathContext);
    return arrayOfBigDecimal[1];
  }
  
  public BigDecimal[] divideAndRemainder(BigDecimal paramBigDecimal)
  {
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[2];
    arrayOfBigDecimal[0] = divideToIntegralValue(paramBigDecimal);
    arrayOfBigDecimal[1] = subtract(arrayOfBigDecimal[0].multiply(paramBigDecimal));
    return arrayOfBigDecimal;
  }
  
  public BigDecimal[] divideAndRemainder(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    if (precision == 0) {
      return divideAndRemainder(paramBigDecimal);
    }
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[2];
    BigDecimal localBigDecimal = this;
    arrayOfBigDecimal[0] = localBigDecimal.divideToIntegralValue(paramBigDecimal, paramMathContext);
    arrayOfBigDecimal[1] = localBigDecimal.subtract(arrayOfBigDecimal[0].multiply(paramBigDecimal));
    return arrayOfBigDecimal;
  }
  
  public BigDecimal pow(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 999999999)) {
      throw new ArithmeticException("Invalid operation");
    }
    int i = checkScale(scale * paramInt);
    return new BigDecimal(inflated().pow(paramInt), i);
  }
  
  public BigDecimal pow(int paramInt, MathContext paramMathContext)
  {
    if (precision == 0) {
      return pow(paramInt);
    }
    if ((paramInt < -999999999) || (paramInt > 999999999)) {
      throw new ArithmeticException("Invalid operation");
    }
    if (paramInt == 0) {
      return ONE;
    }
    BigDecimal localBigDecimal1 = this;
    MathContext localMathContext = paramMathContext;
    int i = Math.abs(paramInt);
    if (precision > 0)
    {
      int j = longDigitLength(i);
      if (j > precision) {
        throw new ArithmeticException("Invalid operation");
      }
      localMathContext = new MathContext(precision + j + 1, roundingMode);
    }
    BigDecimal localBigDecimal2 = ONE;
    int k = 0;
    for (int m = 1;; m++)
    {
      i += i;
      if (i < 0)
      {
        k = 1;
        localBigDecimal2 = localBigDecimal2.multiply(localBigDecimal1, localMathContext);
      }
      if (m == 31) {
        break;
      }
      if (k != 0) {
        localBigDecimal2 = localBigDecimal2.multiply(localBigDecimal2, localMathContext);
      }
    }
    if (paramInt < 0) {
      localBigDecimal2 = ONE.divide(localBigDecimal2, localMathContext);
    }
    return doRound(localBigDecimal2, paramMathContext);
  }
  
  public BigDecimal abs()
  {
    return signum() < 0 ? negate() : this;
  }
  
  public BigDecimal abs(MathContext paramMathContext)
  {
    return signum() < 0 ? negate(paramMathContext) : plus(paramMathContext);
  }
  
  public BigDecimal negate()
  {
    if (intCompact == Long.MIN_VALUE) {
      return new BigDecimal(intVal.negate(), Long.MIN_VALUE, scale, precision);
    }
    return valueOf(-intCompact, scale, precision);
  }
  
  public BigDecimal negate(MathContext paramMathContext)
  {
    return negate().plus(paramMathContext);
  }
  
  public BigDecimal plus()
  {
    return this;
  }
  
  public BigDecimal plus(MathContext paramMathContext)
  {
    if (precision == 0) {
      return this;
    }
    return doRound(this, paramMathContext);
  }
  
  public int signum()
  {
    return intCompact != Long.MIN_VALUE ? Long.signum(intCompact) : intVal.signum();
  }
  
  public int scale()
  {
    return scale;
  }
  
  public int precision()
  {
    int i = precision;
    if (i == 0)
    {
      long l = intCompact;
      if (l != Long.MIN_VALUE) {
        i = longDigitLength(l);
      } else {
        i = bigDigitLength(intVal);
      }
      precision = i;
    }
    return i;
  }
  
  public BigInteger unscaledValue()
  {
    return inflated();
  }
  
  public BigDecimal round(MathContext paramMathContext)
  {
    return plus(paramMathContext);
  }
  
  public BigDecimal setScale(int paramInt, RoundingMode paramRoundingMode)
  {
    return setScale(paramInt, oldMode);
  }
  
  public BigDecimal setScale(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt2 > 7)) {
      throw new IllegalArgumentException("Invalid rounding mode");
    }
    int i = scale;
    if (paramInt1 == i) {
      return this;
    }
    if (signum() == 0) {
      return zeroValueOf(paramInt1);
    }
    if (intCompact != Long.MIN_VALUE)
    {
      long l = intCompact;
      if (paramInt1 > i)
      {
        k = checkScale(paramInt1 - i);
        if ((l = longMultiplyPowerTen(l, k)) != Long.MIN_VALUE) {
          return valueOf(l, paramInt1);
        }
        BigInteger localBigInteger2 = bigMultiplyPowerTen(k);
        return new BigDecimal(localBigInteger2, Long.MIN_VALUE, paramInt1, precision > 0 ? precision + k : 0);
      }
      int k = checkScale(i - paramInt1);
      if (k < LONG_TEN_POWERS_TABLE.length) {
        return divideAndRound(l, LONG_TEN_POWERS_TABLE[k], paramInt1, paramInt2, paramInt1);
      }
      return divideAndRound(inflated(), bigTenToThe(k), paramInt1, paramInt2, paramInt1);
    }
    if (paramInt1 > i)
    {
      j = checkScale(paramInt1 - i);
      BigInteger localBigInteger1 = bigMultiplyPowerTen(intVal, j);
      return new BigDecimal(localBigInteger1, Long.MIN_VALUE, paramInt1, precision > 0 ? precision + j : 0);
    }
    int j = checkScale(i - paramInt1);
    if (j < LONG_TEN_POWERS_TABLE.length) {
      return divideAndRound(intVal, LONG_TEN_POWERS_TABLE[j], paramInt1, paramInt2, paramInt1);
    }
    return divideAndRound(intVal, bigTenToThe(j), paramInt1, paramInt2, paramInt1);
  }
  
  public BigDecimal setScale(int paramInt)
  {
    return setScale(paramInt, 7);
  }
  
  public BigDecimal movePointLeft(int paramInt)
  {
    int i = checkScale(scale + paramInt);
    BigDecimal localBigDecimal = new BigDecimal(intVal, intCompact, i, 0);
    return scale < 0 ? localBigDecimal.setScale(0, 7) : localBigDecimal;
  }
  
  public BigDecimal movePointRight(int paramInt)
  {
    int i = checkScale(scale - paramInt);
    BigDecimal localBigDecimal = new BigDecimal(intVal, intCompact, i, 0);
    return scale < 0 ? localBigDecimal.setScale(0, 7) : localBigDecimal;
  }
  
  public BigDecimal scaleByPowerOfTen(int paramInt)
  {
    return new BigDecimal(intVal, intCompact, checkScale(scale - paramInt), precision);
  }
  
  public BigDecimal stripTrailingZeros()
  {
    if ((intCompact == 0L) || ((intVal != null) && (intVal.signum() == 0))) {
      return ZERO;
    }
    if (intCompact != Long.MIN_VALUE) {
      return createAndStripZerosToMatchScale(intCompact, scale, Long.MIN_VALUE);
    }
    return createAndStripZerosToMatchScale(intVal, scale, Long.MIN_VALUE);
  }
  
  public int compareTo(BigDecimal paramBigDecimal)
  {
    if (scale == scale)
    {
      long l1 = intCompact;
      long l2 = intCompact;
      if ((l1 != Long.MIN_VALUE) && (l2 != Long.MIN_VALUE)) {
        return l1 != l2 ? -1 : l1 > l2 ? 1 : 0;
      }
    }
    int i = signum();
    int j = paramBigDecimal.signum();
    if (i != j) {
      return i > j ? 1 : -1;
    }
    if (i == 0) {
      return 0;
    }
    int k = compareMagnitude(paramBigDecimal);
    return i > 0 ? k : -k;
  }
  
  private int compareMagnitude(BigDecimal paramBigDecimal)
  {
    long l1 = intCompact;
    long l2 = intCompact;
    if (l2 == 0L) {
      return l1 == 0L ? 0 : -1;
    }
    if (l1 == 0L) {
      return 1;
    }
    long l3 = scale - scale;
    if (l3 != 0L)
    {
      long l4 = precision() - scale;
      long l5 = paramBigDecimal.precision() - scale;
      if (l4 < l5) {
        return -1;
      }
      if (l4 > l5) {
        return 1;
      }
      BigInteger localBigInteger = null;
      if (l3 < 0L)
      {
        if ((l3 > -2147483648L) && ((l2 == Long.MIN_VALUE) || ((l2 = longMultiplyPowerTen(l2, (int)-l3)) == Long.MIN_VALUE)) && (l1 == Long.MIN_VALUE))
        {
          localBigInteger = bigMultiplyPowerTen((int)-l3);
          return localBigInteger.compareMagnitude(intVal);
        }
      }
      else if ((l3 <= 2147483647L) && ((l1 == Long.MIN_VALUE) || ((l1 = longMultiplyPowerTen(l1, (int)l3)) == Long.MIN_VALUE)) && (l2 == Long.MIN_VALUE))
      {
        localBigInteger = paramBigDecimal.bigMultiplyPowerTen((int)l3);
        return intVal.compareMagnitude(localBigInteger);
      }
    }
    if (l2 != Long.MIN_VALUE) {
      return l1 != Long.MIN_VALUE ? longCompareMagnitude(l2, l1) : -1;
    }
    if (l1 != Long.MIN_VALUE) {
      return 1;
    }
    return intVal.compareMagnitude(intVal);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof BigDecimal)) {
      return false;
    }
    BigDecimal localBigDecimal = (BigDecimal)paramObject;
    if (paramObject == this) {
      return true;
    }
    if (scale != scale) {
      return false;
    }
    long l1 = intCompact;
    long l2 = intCompact;
    if (l1 != Long.MIN_VALUE)
    {
      if (l2 == Long.MIN_VALUE) {
        l2 = compactValFor(intVal);
      }
      return l2 == l1;
    }
    if (l2 != Long.MIN_VALUE) {
      return l2 == compactValFor(intVal);
    }
    return inflated().equals(localBigDecimal.inflated());
  }
  
  public BigDecimal min(BigDecimal paramBigDecimal)
  {
    return compareTo(paramBigDecimal) <= 0 ? this : paramBigDecimal;
  }
  
  public BigDecimal max(BigDecimal paramBigDecimal)
  {
    return compareTo(paramBigDecimal) >= 0 ? this : paramBigDecimal;
  }
  
  public int hashCode()
  {
    if (intCompact != Long.MIN_VALUE)
    {
      long l = intCompact < 0L ? -intCompact : intCompact;
      int i = (int)((int)(l >>> 32) * 31 + (l & 0xFFFFFFFF));
      return 31 * (intCompact < 0L ? -i : i) + scale;
    }
    return 31 * intVal.hashCode() + scale;
  }
  
  public String toString()
  {
    String str = stringCache;
    if (str == null) {
      stringCache = (str = layoutChars(true));
    }
    return str;
  }
  
  public String toEngineeringString()
  {
    return layoutChars(false);
  }
  
  public String toPlainString()
  {
    if (scale == 0)
    {
      if (intCompact != Long.MIN_VALUE) {
        return Long.toString(intCompact);
      }
      return intVal.toString();
    }
    if (scale < 0)
    {
      if (signum() == 0) {
        return "0";
      }
      int i = checkScaleNonZero(-scale);
      StringBuilder localStringBuilder;
      if (intCompact != Long.MIN_VALUE)
      {
        localStringBuilder = new StringBuilder(20 + i);
        localStringBuilder.append(intCompact);
      }
      else
      {
        String str2 = intVal.toString();
        localStringBuilder = new StringBuilder(str2.length() + i);
        localStringBuilder.append(str2);
      }
      for (int j = 0; j < i; j++) {
        localStringBuilder.append('0');
      }
      return localStringBuilder.toString();
    }
    String str1;
    if (intCompact != Long.MIN_VALUE) {
      str1 = Long.toString(Math.abs(intCompact));
    } else {
      str1 = intVal.abs().toString();
    }
    return getValueString(signum(), str1, scale);
  }
  
  private String getValueString(int paramInt1, String paramString, int paramInt2)
  {
    int i = paramString.length() - paramInt2;
    if (i == 0) {
      return (paramInt1 < 0 ? "-0." : "0.") + paramString;
    }
    StringBuilder localStringBuilder;
    if (i > 0)
    {
      localStringBuilder = new StringBuilder(paramString);
      localStringBuilder.insert(i, '.');
      if (paramInt1 < 0) {
        localStringBuilder.insert(0, '-');
      }
    }
    else
    {
      localStringBuilder = new StringBuilder(3 - i + paramString.length());
      localStringBuilder.append(paramInt1 < 0 ? "-0." : "0.");
      for (int j = 0; j < -i; j++) {
        localStringBuilder.append('0');
      }
      localStringBuilder.append(paramString);
    }
    return localStringBuilder.toString();
  }
  
  public BigInteger toBigInteger()
  {
    return setScale(0, 1).inflated();
  }
  
  public BigInteger toBigIntegerExact()
  {
    return setScale(0, 7).inflated();
  }
  
  public long longValue()
  {
    return (intCompact != Long.MIN_VALUE) && (scale == 0) ? intCompact : toBigInteger().longValue();
  }
  
  public long longValueExact()
  {
    if ((intCompact != Long.MIN_VALUE) && (scale == 0)) {
      return intCompact;
    }
    if (precision() - scale > 19) {
      throw new ArithmeticException("Overflow");
    }
    if (signum() == 0) {
      return 0L;
    }
    if (precision() - scale <= 0) {
      throw new ArithmeticException("Rounding necessary");
    }
    BigDecimal localBigDecimal = setScale(0, 7);
    if (localBigDecimal.precision() >= 19) {
      LongOverflow.check(localBigDecimal);
    }
    return localBigDecimal.inflated().longValue();
  }
  
  public int intValue()
  {
    return (intCompact != Long.MIN_VALUE) && (scale == 0) ? (int)intCompact : toBigInteger().intValue();
  }
  
  public int intValueExact()
  {
    long l = longValueExact();
    if ((int)l != l) {
      throw new ArithmeticException("Overflow");
    }
    return (int)l;
  }
  
  public short shortValueExact()
  {
    long l = longValueExact();
    if ((short)(int)l != l) {
      throw new ArithmeticException("Overflow");
    }
    return (short)(int)l;
  }
  
  public byte byteValueExact()
  {
    long l = longValueExact();
    if ((byte)(int)l != l) {
      throw new ArithmeticException("Overflow");
    }
    return (byte)(int)l;
  }
  
  public float floatValue()
  {
    if (intCompact != Long.MIN_VALUE)
    {
      if (scale == 0) {
        return (float)intCompact;
      }
      if (Math.abs(intCompact) < 4194304L)
      {
        if ((scale > 0) && (scale < float10pow.length)) {
          return (float)intCompact / float10pow[scale];
        }
        if ((scale < 0) && (scale > -float10pow.length)) {
          return (float)intCompact * float10pow[(-scale)];
        }
      }
    }
    return Float.parseFloat(toString());
  }
  
  public double doubleValue()
  {
    if (intCompact != Long.MIN_VALUE)
    {
      if (scale == 0) {
        return intCompact;
      }
      if (Math.abs(intCompact) < 4503599627370496L)
      {
        if ((scale > 0) && (scale < double10pow.length)) {
          return intCompact / double10pow[scale];
        }
        if ((scale < 0) && (scale > -double10pow.length)) {
          return intCompact * double10pow[(-scale)];
        }
      }
    }
    return Double.parseDouble(toString());
  }
  
  public BigDecimal ulp()
  {
    return valueOf(1L, scale(), 1);
  }
  
  private String layoutChars(boolean paramBoolean)
  {
    if (scale == 0) {
      return intCompact != Long.MIN_VALUE ? Long.toString(intCompact) : intVal.toString();
    }
    if ((scale == 2) && (intCompact >= 0L) && (intCompact < 2147483647L))
    {
      int i = (int)intCompact % 100;
      int j = (int)intCompact / 100;
      return Integer.toString(j) + '.' + StringBuilderHelper.DIGIT_TENS[i] + StringBuilderHelper.DIGIT_ONES[i];
    }
    StringBuilderHelper localStringBuilderHelper = (StringBuilderHelper)threadLocalStringBuilderHelper.get();
    int k;
    char[] arrayOfChar;
    if (intCompact != Long.MIN_VALUE)
    {
      k = localStringBuilderHelper.putIntCompact(Math.abs(intCompact));
      arrayOfChar = localStringBuilderHelper.getCompactCharArray();
    }
    else
    {
      k = 0;
      arrayOfChar = intVal.abs().toString().toCharArray();
    }
    StringBuilder localStringBuilder = localStringBuilderHelper.getStringBuilder();
    if (signum() < 0) {
      localStringBuilder.append('-');
    }
    int m = arrayOfChar.length - k;
    long l = -scale + (m - 1);
    int n;
    if ((scale >= 0) && (l >= -6L))
    {
      n = scale - m;
      if (n >= 0)
      {
        localStringBuilder.append('0');
        localStringBuilder.append('.');
        while (n > 0)
        {
          localStringBuilder.append('0');
          n--;
        }
        localStringBuilder.append(arrayOfChar, k, m);
      }
      else
      {
        localStringBuilder.append(arrayOfChar, k, -n);
        localStringBuilder.append('.');
        localStringBuilder.append(arrayOfChar, -n + k, scale);
      }
    }
    else
    {
      if (paramBoolean)
      {
        localStringBuilder.append(arrayOfChar[k]);
        if (m > 1)
        {
          localStringBuilder.append('.');
          localStringBuilder.append(arrayOfChar, k + 1, m - 1);
        }
      }
      else
      {
        n = (int)(l % 3L);
        if (n < 0) {
          n += 3;
        }
        l -= n;
        n++;
        if (signum() == 0)
        {
          switch (n)
          {
          case 1: 
            localStringBuilder.append('0');
            break;
          case 2: 
            localStringBuilder.append("0.00");
            l += 3L;
            break;
          case 3: 
            localStringBuilder.append("0.0");
            l += 3L;
            break;
          default: 
            throw new AssertionError("Unexpected sig value " + n);
          }
        }
        else if (n >= m)
        {
          localStringBuilder.append(arrayOfChar, k, m);
          for (int i1 = n - m; i1 > 0; i1--) {
            localStringBuilder.append('0');
          }
        }
        else
        {
          localStringBuilder.append(arrayOfChar, k, n);
          localStringBuilder.append('.');
          localStringBuilder.append(arrayOfChar, k + n, m - n);
        }
      }
      if (l != 0L)
      {
        localStringBuilder.append('E');
        if (l > 0L) {
          localStringBuilder.append('+');
        }
        localStringBuilder.append(l);
      }
    }
    return localStringBuilder.toString();
  }
  
  private static BigInteger bigTenToThe(int paramInt)
  {
    if (paramInt < 0) {
      return BigInteger.ZERO;
    }
    if (paramInt < BIG_TEN_POWERS_TABLE_MAX)
    {
      BigInteger[] arrayOfBigInteger = BIG_TEN_POWERS_TABLE;
      if (paramInt < arrayOfBigInteger.length) {
        return arrayOfBigInteger[paramInt];
      }
      return expandBigIntegerTenPowers(paramInt);
    }
    return BigInteger.TEN.pow(paramInt);
  }
  
  private static BigInteger expandBigIntegerTenPowers(int paramInt)
  {
    synchronized (BigDecimal.class)
    {
      BigInteger[] arrayOfBigInteger = BIG_TEN_POWERS_TABLE;
      int i = arrayOfBigInteger.length;
      if (i <= paramInt)
      {
        int j = i << 1;
        while (j <= paramInt) {
          j <<= 1;
        }
        arrayOfBigInteger = (BigInteger[])Arrays.copyOf(arrayOfBigInteger, j);
        for (int k = i; k < j; k++) {
          arrayOfBigInteger[k] = arrayOfBigInteger[(k - 1)].multiply(BigInteger.TEN);
        }
        BIG_TEN_POWERS_TABLE = arrayOfBigInteger;
      }
      return arrayOfBigInteger[paramInt];
    }
  }
  
  private static long longMultiplyPowerTen(long paramLong, int paramInt)
  {
    if ((paramLong == 0L) || (paramInt <= 0)) {
      return paramLong;
    }
    long[] arrayOfLong1 = LONG_TEN_POWERS_TABLE;
    long[] arrayOfLong2 = THRESHOLDS_TABLE;
    if ((paramInt < arrayOfLong1.length) && (paramInt < arrayOfLong2.length))
    {
      long l = arrayOfLong1[paramInt];
      if (paramLong == 1L) {
        return l;
      }
      if (Math.abs(paramLong) <= arrayOfLong2[paramInt]) {
        return paramLong * l;
      }
    }
    return Long.MIN_VALUE;
  }
  
  private BigInteger bigMultiplyPowerTen(int paramInt)
  {
    if (paramInt <= 0) {
      return inflated();
    }
    if (intCompact != Long.MIN_VALUE) {
      return bigTenToThe(paramInt).multiply(intCompact);
    }
    return intVal.multiply(bigTenToThe(paramInt));
  }
  
  private BigInteger inflated()
  {
    if (intVal == null) {
      return BigInteger.valueOf(intCompact);
    }
    return intVal;
  }
  
  private static void matchScale(BigDecimal[] paramArrayOfBigDecimal)
  {
    if (0scale == 1scale) {
      return;
    }
    if (0scale < 1scale) {
      paramArrayOfBigDecimal[0] = paramArrayOfBigDecimal[0].setScale(1scale, 7);
    } else if (1scale < 0scale) {
      paramArrayOfBigDecimal[1] = paramArrayOfBigDecimal[1].setScale(0scale, 7);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (intVal == null)
    {
      String str = "BigDecimal: null intVal in stream";
      throw new StreamCorruptedException(str);
    }
    UnsafeHolder.setIntCompactVolatile(this, compactValFor(intVal));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (intVal == null) {
      UnsafeHolder.setIntValVolatile(this, BigInteger.valueOf(intCompact));
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  static int longDigitLength(long paramLong)
  {
    assert (paramLong != Long.MIN_VALUE);
    if (paramLong < 0L) {
      paramLong = -paramLong;
    }
    if (paramLong < 10L) {
      return 1;
    }
    int i = (64 - Long.numberOfLeadingZeros(paramLong) + 1) * 1233 >>> 12;
    long[] arrayOfLong = LONG_TEN_POWERS_TABLE;
    return (i >= arrayOfLong.length) || (paramLong < arrayOfLong[i]) ? i : i + 1;
  }
  
  private static int bigDigitLength(BigInteger paramBigInteger)
  {
    if (signum == 0) {
      return 1;
    }
    int i = (int)((paramBigInteger.bitLength() + 1L) * 646456993L >>> 31);
    return paramBigInteger.compareMagnitude(bigTenToThe(i)) < 0 ? i : i + 1;
  }
  
  private int checkScale(long paramLong)
  {
    int i = (int)paramLong;
    if (i != paramLong)
    {
      i = paramLong > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      BigInteger localBigInteger;
      if ((intCompact != 0L) && (((localBigInteger = intVal) == null) || (localBigInteger.signum() != 0))) {
        throw new ArithmeticException(i > 0 ? "Underflow" : "Overflow");
      }
    }
    return i;
  }
  
  private static long compactValFor(BigInteger paramBigInteger)
  {
    int[] arrayOfInt = mag;
    int i = arrayOfInt.length;
    if (i == 0) {
      return 0L;
    }
    int j = arrayOfInt[0];
    if ((i > 2) || ((i == 2) && (j < 0))) {
      return Long.MIN_VALUE;
    }
    long l = i == 2 ? (arrayOfInt[1] & 0xFFFFFFFF) + (j << 32) : j & 0xFFFFFFFF;
    return signum < 0 ? -l : l;
  }
  
  private static int longCompareMagnitude(long paramLong1, long paramLong2)
  {
    if (paramLong1 < 0L) {
      paramLong1 = -paramLong1;
    }
    if (paramLong2 < 0L) {
      paramLong2 = -paramLong2;
    }
    return paramLong1 == paramLong2 ? 0 : paramLong1 < paramLong2 ? -1 : 1;
  }
  
  private static int saturateLong(long paramLong)
  {
    int i = (int)paramLong;
    return paramLong < 0L ? Integer.MIN_VALUE : paramLong == i ? i : Integer.MAX_VALUE;
  }
  
  private static void print(String paramString, BigDecimal paramBigDecimal)
  {
    System.err.format("%s:\tintCompact %d\tintVal %d\tscale %d\tprecision %d%n", new Object[] { paramString, Long.valueOf(intCompact), intVal, Integer.valueOf(scale), Integer.valueOf(precision) });
  }
  
  private BigDecimal audit()
  {
    if (intCompact == Long.MIN_VALUE)
    {
      if (intVal == null)
      {
        print("audit", this);
        throw new AssertionError("null intVal");
      }
      if ((precision > 0) && (precision != bigDigitLength(intVal)))
      {
        print("audit", this);
        throw new AssertionError("precision mismatch");
      }
    }
    else
    {
      if (intVal != null)
      {
        long l = intVal.longValue();
        if (l != intCompact)
        {
          print("audit", this);
          throw new AssertionError("Inconsistent state, intCompact=" + intCompact + "\t intVal=" + l);
        }
      }
      if ((precision > 0) && (precision != longDigitLength(intCompact)))
      {
        print("audit", this);
        throw new AssertionError("precision mismatch");
      }
    }
    return this;
  }
  
  private static int checkScaleNonZero(long paramLong)
  {
    int i = (int)paramLong;
    if (i != paramLong) {
      throw new ArithmeticException(i > 0 ? "Underflow" : "Overflow");
    }
    return i;
  }
  
  private static int checkScale(long paramLong1, long paramLong2)
  {
    int i = (int)paramLong2;
    if (i != paramLong2)
    {
      i = paramLong2 > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      if (paramLong1 != 0L) {
        throw new ArithmeticException(i > 0 ? "Underflow" : "Overflow");
      }
    }
    return i;
  }
  
  private static int checkScale(BigInteger paramBigInteger, long paramLong)
  {
    int i = (int)paramLong;
    if (i != paramLong)
    {
      i = paramLong > 2147483647L ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      if (paramBigInteger.signum() != 0) {
        throw new ArithmeticException(i > 0 ? "Underflow" : "Overflow");
      }
    }
    return i;
  }
  
  private static BigDecimal doRound(BigDecimal paramBigDecimal, MathContext paramMathContext)
  {
    int i = precision;
    int j = 0;
    if (i > 0)
    {
      BigInteger localBigInteger = intVal;
      long l = intCompact;
      int k = scale;
      int m = paramBigDecimal.precision();
      int n = roundingMode.oldMode;
      int i1;
      if (l == Long.MIN_VALUE) {
        for (i1 = m - i; i1 > 0; i1 = m - i)
        {
          k = checkScaleNonZero(k - i1);
          localBigInteger = divideAndRoundByTenPow(localBigInteger, i1, n);
          j = 1;
          l = compactValFor(localBigInteger);
          if (l != Long.MIN_VALUE)
          {
            m = longDigitLength(l);
            break;
          }
          m = bigDigitLength(localBigInteger);
        }
      }
      if (l != Long.MIN_VALUE)
      {
        i1 = m - i;
        while (i1 > 0)
        {
          k = checkScaleNonZero(k - i1);
          l = divideAndRound(l, LONG_TEN_POWERS_TABLE[i1], roundingMode.oldMode);
          j = 1;
          m = longDigitLength(l);
          i1 = m - i;
          localBigInteger = null;
        }
      }
      return j != 0 ? new BigDecimal(localBigInteger, l, k, m) : paramBigDecimal;
    }
    return paramBigDecimal;
  }
  
  private static BigDecimal doRound(long paramLong, int paramInt, MathContext paramMathContext)
  {
    int i = precision;
    if ((i > 0) && (i < 19))
    {
      int j = longDigitLength(paramLong);
      for (int k = j - i; k > 0; k = j - i)
      {
        paramInt = checkScaleNonZero(paramInt - k);
        paramLong = divideAndRound(paramLong, LONG_TEN_POWERS_TABLE[k], roundingMode.oldMode);
        j = longDigitLength(paramLong);
      }
      return valueOf(paramLong, paramInt, j);
    }
    return valueOf(paramLong, paramInt);
  }
  
  private static BigDecimal doRound(BigInteger paramBigInteger, int paramInt, MathContext paramMathContext)
  {
    int i = precision;
    int j = 0;
    if (i > 0)
    {
      long l = compactValFor(paramBigInteger);
      int k = roundingMode.oldMode;
      int m;
      if (l == Long.MIN_VALUE)
      {
        j = bigDigitLength(paramBigInteger);
        for (m = j - i; m > 0; m = j - i)
        {
          paramInt = checkScaleNonZero(paramInt - m);
          paramBigInteger = divideAndRoundByTenPow(paramBigInteger, m, k);
          l = compactValFor(paramBigInteger);
          if (l != Long.MIN_VALUE) {
            break;
          }
          j = bigDigitLength(paramBigInteger);
        }
      }
      if (l != Long.MIN_VALUE)
      {
        j = longDigitLength(l);
        for (m = j - i; m > 0; m = j - i)
        {
          paramInt = checkScaleNonZero(paramInt - m);
          l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], roundingMode.oldMode);
          j = longDigitLength(l);
        }
        return valueOf(l, paramInt, j);
      }
    }
    return new BigDecimal(paramBigInteger, Long.MIN_VALUE, paramInt, j);
  }
  
  private static BigInteger divideAndRoundByTenPow(BigInteger paramBigInteger, int paramInt1, int paramInt2)
  {
    if (paramInt1 < LONG_TEN_POWERS_TABLE.length) {
      paramBigInteger = divideAndRound(paramBigInteger, LONG_TEN_POWERS_TABLE[paramInt1], paramInt2);
    } else {
      paramBigInteger = divideAndRound(paramBigInteger, bigTenToThe(paramInt1), paramInt2);
    }
    return paramBigInteger;
  }
  
  private static BigDecimal divideAndRound(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3)
  {
    long l1 = paramLong1 / paramLong2;
    if ((paramInt2 == 1) && (paramInt1 == paramInt3)) {
      return valueOf(l1, paramInt1);
    }
    long l2 = paramLong1 % paramLong2;
    int i = (paramLong1 < 0L ? 1 : 0) == (paramLong2 < 0L ? 1 : 0) ? 1 : -1;
    if (l2 != 0L)
    {
      boolean bool = needIncrement(paramLong2, paramInt2, i, l1, l2);
      return valueOf(bool ? l1 + i : l1, paramInt1);
    }
    if (paramInt3 != paramInt1) {
      return createAndStripZerosToMatchScale(l1, paramInt1, paramInt3);
    }
    return valueOf(l1, paramInt1);
  }
  
  private static long divideAndRound(long paramLong1, long paramLong2, int paramInt)
  {
    long l1 = paramLong1 / paramLong2;
    if (paramInt == 1) {
      return l1;
    }
    long l2 = paramLong1 % paramLong2;
    int i = (paramLong1 < 0L ? 1 : 0) == (paramLong2 < 0L ? 1 : 0) ? 1 : -1;
    if (l2 != 0L)
    {
      boolean bool = needIncrement(paramLong2, paramInt, i, l1, l2);
      return bool ? l1 + i : l1;
    }
    return l1;
  }
  
  private static boolean commonNeedIncrement(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    switch (paramInt1)
    {
    case 7: 
      throw new ArithmeticException("Rounding necessary");
    case 0: 
      return true;
    case 1: 
      return false;
    case 2: 
      return paramInt2 > 0;
    case 3: 
      return paramInt2 < 0;
    }
    assert ((paramInt1 >= 4) && (paramInt1 <= 6)) : ("Unexpected rounding mode" + RoundingMode.valueOf(paramInt1));
    if (paramInt3 < 0) {
      return false;
    }
    if (paramInt3 > 0) {
      return true;
    }
    assert (paramInt3 == 0);
    switch (paramInt1)
    {
    case 5: 
      return false;
    case 4: 
      return true;
    case 6: 
      return paramBoolean;
    }
    throw new AssertionError("Unexpected rounding mode" + paramInt1);
  }
  
  private static boolean needIncrement(long paramLong1, int paramInt1, int paramInt2, long paramLong2, long paramLong3)
  {
    assert (paramLong3 != 0L);
    int i;
    if ((paramLong3 <= -4611686018427387904L) || (paramLong3 > 4611686018427387903L)) {
      i = 1;
    } else {
      i = longCompareMagnitude(2L * paramLong3, paramLong1);
    }
    return commonNeedIncrement(paramInt1, paramInt2, i, (paramLong2 & 1L) != 0L);
  }
  
  private static BigInteger divideAndRound(BigInteger paramBigInteger, long paramLong, int paramInt)
  {
    long l = 0L;
    MutableBigInteger localMutableBigInteger1 = null;
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(mag);
    localMutableBigInteger1 = new MutableBigInteger();
    l = localMutableBigInteger2.divide(paramLong, localMutableBigInteger1);
    int i = l == 0L ? 1 : 0;
    int j = paramLong < 0L ? -signum : signum;
    if ((i == 0) && (needIncrement(paramLong, paramInt, j, localMutableBigInteger1, l))) {
      localMutableBigInteger1.add(MutableBigInteger.ONE);
    }
    return localMutableBigInteger1.toBigInteger(j);
  }
  
  private static BigDecimal divideAndRound(BigInteger paramBigInteger, long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    long l1 = 0L;
    MutableBigInteger localMutableBigInteger1 = null;
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(mag);
    localMutableBigInteger1 = new MutableBigInteger();
    l1 = localMutableBigInteger2.divide(paramLong, localMutableBigInteger1);
    int i = l1 == 0L ? 1 : 0;
    int j = paramLong < 0L ? -signum : signum;
    if (i == 0)
    {
      if (needIncrement(paramLong, paramInt2, j, localMutableBigInteger1, l1)) {
        localMutableBigInteger1.add(MutableBigInteger.ONE);
      }
      return localMutableBigInteger1.toBigDecimal(j, paramInt1);
    }
    if (paramInt3 != paramInt1)
    {
      long l2 = localMutableBigInteger1.toCompactValue(j);
      if (l2 != Long.MIN_VALUE) {
        return createAndStripZerosToMatchScale(l2, paramInt1, paramInt3);
      }
      BigInteger localBigInteger = localMutableBigInteger1.toBigInteger(j);
      return createAndStripZerosToMatchScale(localBigInteger, paramInt1, paramInt3);
    }
    return localMutableBigInteger1.toBigDecimal(j, paramInt1);
  }
  
  private static boolean needIncrement(long paramLong1, int paramInt1, int paramInt2, MutableBigInteger paramMutableBigInteger, long paramLong2)
  {
    assert (paramLong2 != 0L);
    int i;
    if ((paramLong2 <= -4611686018427387904L) || (paramLong2 > 4611686018427387903L)) {
      i = 1;
    } else {
      i = longCompareMagnitude(2L * paramLong2, paramLong1);
    }
    return commonNeedIncrement(paramInt1, paramInt2, i, paramMutableBigInteger.isOdd());
  }
  
  private static BigInteger divideAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt)
  {
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger4 = localMutableBigInteger1.divide(localMutableBigInteger3, localMutableBigInteger2);
    boolean bool = localMutableBigInteger4.isZero();
    int i = signum != signum ? -1 : 1;
    if ((!bool) && (needIncrement(localMutableBigInteger3, paramInt, i, localMutableBigInteger2, localMutableBigInteger4))) {
      localMutableBigInteger2.add(MutableBigInteger.ONE);
    }
    return localMutableBigInteger2.toBigInteger(i);
  }
  
  private static BigDecimal divideAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt1, int paramInt2, int paramInt3)
  {
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger4 = localMutableBigInteger1.divide(localMutableBigInteger3, localMutableBigInteger2);
    boolean bool = localMutableBigInteger4.isZero();
    int i = signum != signum ? -1 : 1;
    if (!bool)
    {
      if (needIncrement(localMutableBigInteger3, paramInt2, i, localMutableBigInteger2, localMutableBigInteger4)) {
        localMutableBigInteger2.add(MutableBigInteger.ONE);
      }
      return localMutableBigInteger2.toBigDecimal(i, paramInt1);
    }
    if (paramInt3 != paramInt1)
    {
      long l = localMutableBigInteger2.toCompactValue(i);
      if (l != Long.MIN_VALUE) {
        return createAndStripZerosToMatchScale(l, paramInt1, paramInt3);
      }
      BigInteger localBigInteger = localMutableBigInteger2.toBigInteger(i);
      return createAndStripZerosToMatchScale(localBigInteger, paramInt1, paramInt3);
    }
    return localMutableBigInteger2.toBigDecimal(i, paramInt1);
  }
  
  private static boolean needIncrement(MutableBigInteger paramMutableBigInteger1, int paramInt1, int paramInt2, MutableBigInteger paramMutableBigInteger2, MutableBigInteger paramMutableBigInteger3)
  {
    assert (!paramMutableBigInteger3.isZero());
    int i = paramMutableBigInteger3.compareHalf(paramMutableBigInteger1);
    return commonNeedIncrement(paramInt1, paramInt2, i, paramMutableBigInteger2.isOdd());
  }
  
  private static BigDecimal createAndStripZerosToMatchScale(BigInteger paramBigInteger, int paramInt, long paramLong)
  {
    while ((paramBigInteger.compareMagnitude(BigInteger.TEN) >= 0) && (paramInt > paramLong) && (!paramBigInteger.testBit(0)))
    {
      BigInteger[] arrayOfBigInteger = paramBigInteger.divideAndRemainder(BigInteger.TEN);
      if (arrayOfBigInteger[1].signum() != 0) {
        break;
      }
      paramBigInteger = arrayOfBigInteger[0];
      paramInt = checkScale(paramBigInteger, paramInt - 1L);
    }
    return valueOf(paramBigInteger, paramInt, 0);
  }
  
  private static BigDecimal createAndStripZerosToMatchScale(long paramLong1, int paramInt, long paramLong2)
  {
    while ((Math.abs(paramLong1) >= 10L) && (paramInt > paramLong2) && ((paramLong1 & 1L) == 0L))
    {
      long l = paramLong1 % 10L;
      if (l != 0L) {
        break;
      }
      paramLong1 /= 10L;
      paramInt = checkScale(paramLong1, paramInt - 1L);
    }
    return valueOf(paramLong1, paramInt);
  }
  
  private static BigDecimal stripZerosToMatchScale(BigInteger paramBigInteger, long paramLong, int paramInt1, int paramInt2)
  {
    if (paramLong != Long.MIN_VALUE) {
      return createAndStripZerosToMatchScale(paramLong, paramInt1, paramInt2);
    }
    return createAndStripZerosToMatchScale(paramBigInteger == null ? INFLATED_BIGINT : paramBigInteger, paramInt1, paramInt2);
  }
  
  private static long add(long paramLong1, long paramLong2)
  {
    long l = paramLong1 + paramLong2;
    if (((l ^ paramLong1) & (l ^ paramLong2)) >= 0L) {
      return l;
    }
    return Long.MIN_VALUE;
  }
  
  private static BigDecimal add(long paramLong1, long paramLong2, int paramInt)
  {
    long l = add(paramLong1, paramLong2);
    if (l != Long.MIN_VALUE) {
      return valueOf(l, paramInt);
    }
    return new BigDecimal(BigInteger.valueOf(paramLong1).add(paramLong2), paramInt);
  }
  
  private static BigDecimal add(long paramLong1, int paramInt1, long paramLong2, int paramInt2)
  {
    long l1 = paramInt1 - paramInt2;
    if (l1 == 0L) {
      return add(paramLong1, paramLong2, paramInt1);
    }
    if (l1 < 0L)
    {
      i = checkScale(paramLong1, -l1);
      l2 = longMultiplyPowerTen(paramLong1, i);
      if (l2 != Long.MIN_VALUE) {
        return add(l2, paramLong2, paramInt2);
      }
      localBigInteger = bigMultiplyPowerTen(paramLong1, i).add(paramLong2);
      return (paramLong1 ^ paramLong2) >= 0L ? new BigDecimal(localBigInteger, Long.MIN_VALUE, paramInt2, 0) : valueOf(localBigInteger, paramInt2, 0);
    }
    int i = checkScale(paramLong2, l1);
    long l2 = longMultiplyPowerTen(paramLong2, i);
    if (l2 != Long.MIN_VALUE) {
      return add(paramLong1, l2, paramInt1);
    }
    BigInteger localBigInteger = bigMultiplyPowerTen(paramLong2, i).add(paramLong1);
    return (paramLong1 ^ paramLong2) >= 0L ? new BigDecimal(localBigInteger, Long.MIN_VALUE, paramInt1, 0) : valueOf(localBigInteger, paramInt1, 0);
  }
  
  private static BigDecimal add(long paramLong, int paramInt1, BigInteger paramBigInteger, int paramInt2)
  {
    int i = paramInt1;
    long l1 = i - paramInt2;
    int j = Long.signum(paramLong) == signum ? 1 : 0;
    int k;
    BigInteger localBigInteger;
    if (l1 < 0L)
    {
      k = checkScale(paramLong, -l1);
      i = paramInt2;
      long l2 = longMultiplyPowerTen(paramLong, k);
      if (l2 == Long.MIN_VALUE) {
        localBigInteger = paramBigInteger.add(bigMultiplyPowerTen(paramLong, k));
      } else {
        localBigInteger = paramBigInteger.add(l2);
      }
    }
    else
    {
      k = checkScale(paramBigInteger, l1);
      paramBigInteger = bigMultiplyPowerTen(paramBigInteger, k);
      localBigInteger = paramBigInteger.add(paramLong);
    }
    return j != 0 ? new BigDecimal(localBigInteger, Long.MIN_VALUE, i, 0) : valueOf(localBigInteger, i, 0);
  }
  
  private static BigDecimal add(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2)
  {
    int i = paramInt1;
    long l = i - paramInt2;
    if (l != 0L)
    {
      int j;
      if (l < 0L)
      {
        j = checkScale(paramBigInteger1, -l);
        i = paramInt2;
        paramBigInteger1 = bigMultiplyPowerTen(paramBigInteger1, j);
      }
      else
      {
        j = checkScale(paramBigInteger2, l);
        paramBigInteger2 = bigMultiplyPowerTen(paramBigInteger2, j);
      }
    }
    BigInteger localBigInteger = paramBigInteger1.add(paramBigInteger2);
    return signum == signum ? new BigDecimal(localBigInteger, Long.MIN_VALUE, i, 0) : valueOf(localBigInteger, i, 0);
  }
  
  private static BigInteger bigMultiplyPowerTen(long paramLong, int paramInt)
  {
    if (paramInt <= 0) {
      return BigInteger.valueOf(paramLong);
    }
    return bigTenToThe(paramInt).multiply(paramLong);
  }
  
  private static BigInteger bigMultiplyPowerTen(BigInteger paramBigInteger, int paramInt)
  {
    if (paramInt <= 0) {
      return paramBigInteger;
    }
    if (paramInt < LONG_TEN_POWERS_TABLE.length) {
      return paramBigInteger.multiply(LONG_TEN_POWERS_TABLE[paramInt]);
    }
    return paramBigInteger.multiply(bigTenToThe(paramInt));
  }
  
  private static BigDecimal divideSmallFastPath(long paramLong1, int paramInt1, long paramLong2, int paramInt2, long paramLong3, MathContext paramMathContext)
  {
    int i = precision;
    int j = roundingMode.oldMode;
    assert ((paramInt1 <= paramInt2) && (paramInt2 < 18) && (i < 18));
    int k = paramInt2 - paramInt1;
    long l1 = k == 0 ? paramLong1 : longMultiplyPowerTen(paramLong1, k);
    int m = longCompareMagnitude(l1, paramLong2);
    int n;
    BigDecimal localBigDecimal;
    if (m > 0)
    {
      paramInt2--;
      n = checkScaleNonZero(paramLong3 + paramInt2 - paramInt1 + i);
      int i1;
      if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0)
      {
        i1 = checkScaleNonZero(i + paramInt2 - paramInt1);
        long l3;
        if ((l3 = longMultiplyPowerTen(paramLong1, i1)) == Long.MIN_VALUE)
        {
          localBigDecimal = null;
          if ((i - 1 >= 0) && (i - 1 < LONG_TEN_POWERS_TABLE.length)) {
            localBigDecimal = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[(i - 1)], l1, paramLong2, n, j, checkScaleNonZero(paramLong3));
          }
          if (localBigDecimal == null)
          {
            BigInteger localBigInteger2 = bigMultiplyPowerTen(l1, i - 1);
            localBigDecimal = divideAndRound(localBigInteger2, paramLong2, n, j, checkScaleNonZero(paramLong3));
          }
        }
        else
        {
          localBigDecimal = divideAndRound(l3, paramLong2, n, j, checkScaleNonZero(paramLong3));
        }
      }
      else
      {
        i1 = checkScaleNonZero(paramInt1 - i);
        if (i1 == paramInt2)
        {
          localBigDecimal = divideAndRound(paramLong1, paramLong2, n, j, checkScaleNonZero(paramLong3));
        }
        else
        {
          int i2 = checkScaleNonZero(i1 - paramInt2);
          long l4;
          if ((l4 = longMultiplyPowerTen(paramLong2, i2)) == Long.MIN_VALUE)
          {
            BigInteger localBigInteger3 = bigMultiplyPowerTen(paramLong2, i2);
            localBigDecimal = divideAndRound(BigInteger.valueOf(paramLong1), localBigInteger3, n, j, checkScaleNonZero(paramLong3));
          }
          else
          {
            localBigDecimal = divideAndRound(paramLong1, l4, n, j, checkScaleNonZero(paramLong3));
          }
        }
      }
    }
    else
    {
      n = checkScaleNonZero(paramLong3 + paramInt2 - paramInt1 + i);
      if (m == 0)
      {
        localBigDecimal = roundedTenPower((l1 < 0L ? 1 : 0) == (paramLong2 < 0L ? 1 : 0) ? 1 : -1, i, n, checkScaleNonZero(paramLong3));
      }
      else
      {
        long l2;
        if ((l2 = longMultiplyPowerTen(l1, i)) == Long.MIN_VALUE)
        {
          localBigDecimal = null;
          if (i < LONG_TEN_POWERS_TABLE.length) {
            localBigDecimal = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[i], l1, paramLong2, n, j, checkScaleNonZero(paramLong3));
          }
          if (localBigDecimal == null)
          {
            BigInteger localBigInteger1 = bigMultiplyPowerTen(l1, i);
            localBigDecimal = divideAndRound(localBigInteger1, paramLong2, n, j, checkScaleNonZero(paramLong3));
          }
        }
        else
        {
          localBigDecimal = divideAndRound(l2, paramLong2, n, j, checkScaleNonZero(paramLong3));
        }
      }
    }
    return doRound(localBigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(long paramLong1, int paramInt1, long paramLong2, int paramInt2, long paramLong3, MathContext paramMathContext)
  {
    int i = precision;
    if ((paramInt1 <= paramInt2) && (paramInt2 < 18) && (i < 18)) {
      return divideSmallFastPath(paramLong1, paramInt1, paramLong2, paramInt2, paramLong3, paramMathContext);
    }
    if (compareMagnitudeNormalized(paramLong1, paramInt1, paramLong2, paramInt2) > 0) {
      paramInt2--;
    }
    int j = roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong3 + paramInt2 - paramInt1 + i);
    int m;
    BigDecimal localBigDecimal;
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0)
    {
      m = checkScaleNonZero(i + paramInt2 - paramInt1);
      long l1;
      if ((l1 = longMultiplyPowerTen(paramLong1, m)) == Long.MIN_VALUE)
      {
        BigInteger localBigInteger1 = bigMultiplyPowerTen(paramLong1, m);
        localBigDecimal = divideAndRound(localBigInteger1, paramLong2, k, j, checkScaleNonZero(paramLong3));
      }
      else
      {
        localBigDecimal = divideAndRound(l1, paramLong2, k, j, checkScaleNonZero(paramLong3));
      }
    }
    else
    {
      m = checkScaleNonZero(paramInt1 - i);
      if (m == paramInt2)
      {
        localBigDecimal = divideAndRound(paramLong1, paramLong2, k, j, checkScaleNonZero(paramLong3));
      }
      else
      {
        int n = checkScaleNonZero(m - paramInt2);
        long l2;
        if ((l2 = longMultiplyPowerTen(paramLong2, n)) == Long.MIN_VALUE)
        {
          BigInteger localBigInteger2 = bigMultiplyPowerTen(paramLong2, n);
          localBigDecimal = divideAndRound(BigInteger.valueOf(paramLong1), localBigInteger2, k, j, checkScaleNonZero(paramLong3));
        }
        else
        {
          localBigDecimal = divideAndRound(paramLong1, l2, k, j, checkScaleNonZero(paramLong3));
        }
      }
    }
    return doRound(localBigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger, int paramInt1, long paramLong1, int paramInt2, long paramLong2, MathContext paramMathContext)
  {
    if (-compareMagnitudeNormalized(paramLong1, paramInt2, paramBigInteger, paramInt1) > 0) {
      paramInt2--;
    }
    int i = precision;
    int j = roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong2 + paramInt2 - paramInt1 + i);
    int m;
    BigDecimal localBigDecimal;
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0)
    {
      m = checkScaleNonZero(i + paramInt2 - paramInt1);
      BigInteger localBigInteger1 = bigMultiplyPowerTen(paramBigInteger, m);
      localBigDecimal = divideAndRound(localBigInteger1, paramLong1, k, j, checkScaleNonZero(paramLong2));
    }
    else
    {
      m = checkScaleNonZero(paramInt1 - i);
      if (m == paramInt2)
      {
        localBigDecimal = divideAndRound(paramBigInteger, paramLong1, k, j, checkScaleNonZero(paramLong2));
      }
      else
      {
        int n = checkScaleNonZero(m - paramInt2);
        long l;
        if ((l = longMultiplyPowerTen(paramLong1, n)) == Long.MIN_VALUE)
        {
          BigInteger localBigInteger2 = bigMultiplyPowerTen(paramLong1, n);
          localBigDecimal = divideAndRound(paramBigInteger, localBigInteger2, k, j, checkScaleNonZero(paramLong2));
        }
        else
        {
          localBigDecimal = divideAndRound(paramBigInteger, l, k, j, checkScaleNonZero(paramLong2));
        }
      }
    }
    return doRound(localBigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(long paramLong1, int paramInt1, BigInteger paramBigInteger, int paramInt2, long paramLong2, MathContext paramMathContext)
  {
    if (compareMagnitudeNormalized(paramLong1, paramInt1, paramBigInteger, paramInt2) > 0) {
      paramInt2--;
    }
    int i = precision;
    int j = roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong2 + paramInt2 - paramInt1 + i);
    int m;
    BigDecimal localBigDecimal;
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0)
    {
      m = checkScaleNonZero(i + paramInt2 - paramInt1);
      BigInteger localBigInteger1 = bigMultiplyPowerTen(paramLong1, m);
      localBigDecimal = divideAndRound(localBigInteger1, paramBigInteger, k, j, checkScaleNonZero(paramLong2));
    }
    else
    {
      m = checkScaleNonZero(paramInt1 - i);
      int n = checkScaleNonZero(m - paramInt2);
      BigInteger localBigInteger2 = bigMultiplyPowerTen(paramBigInteger, n);
      localBigDecimal = divideAndRound(BigInteger.valueOf(paramLong1), localBigInteger2, k, j, checkScaleNonZero(paramLong2));
    }
    return doRound(localBigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2, long paramLong, MathContext paramMathContext)
  {
    if (compareMagnitudeNormalized(paramBigInteger1, paramInt1, paramBigInteger2, paramInt2) > 0) {
      paramInt2--;
    }
    int i = precision;
    int j = roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong + paramInt2 - paramInt1 + i);
    int m;
    BigDecimal localBigDecimal;
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0)
    {
      m = checkScaleNonZero(i + paramInt2 - paramInt1);
      BigInteger localBigInteger1 = bigMultiplyPowerTen(paramBigInteger1, m);
      localBigDecimal = divideAndRound(localBigInteger1, paramBigInteger2, k, j, checkScaleNonZero(paramLong));
    }
    else
    {
      m = checkScaleNonZero(paramInt1 - i);
      int n = checkScaleNonZero(m - paramInt2);
      BigInteger localBigInteger2 = bigMultiplyPowerTen(paramBigInteger2, n);
      localBigDecimal = divideAndRound(paramBigInteger1, localBigInteger2, k, j, checkScaleNonZero(paramLong));
    }
    return doRound(localBigDecimal, paramMathContext);
  }
  
  private static BigDecimal multiplyDivideAndRound(long paramLong1, long paramLong2, long paramLong3, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = Long.signum(paramLong1) * Long.signum(paramLong2) * Long.signum(paramLong3);
    paramLong1 = Math.abs(paramLong1);
    paramLong2 = Math.abs(paramLong2);
    paramLong3 = Math.abs(paramLong3);
    long l1 = paramLong1 >>> 32;
    long l2 = paramLong1 & 0xFFFFFFFF;
    long l3 = paramLong2 >>> 32;
    long l4 = paramLong2 & 0xFFFFFFFF;
    long l5 = l2 * l4;
    long l6 = l5 & 0xFFFFFFFF;
    long l7 = l5 >>> 32;
    l5 = l1 * l4 + l7;
    l7 = l5 & 0xFFFFFFFF;
    long l8 = l5 >>> 32;
    l5 = l2 * l3 + l7;
    l7 = l5 & 0xFFFFFFFF;
    l8 += (l5 >>> 32);
    long l9 = l8 >>> 32;
    l8 &= 0xFFFFFFFF;
    l5 = l1 * l3 + l8;
    l8 = l5 & 0xFFFFFFFF;
    l9 = (l5 >>> 32) + l9 & 0xFFFFFFFF;
    long l10 = make64(l9, l8);
    long l11 = make64(l7, l6);
    return divideAndRound128(l10, l11, paramLong3, i, paramInt1, paramInt2, paramInt3);
  }
  
  private static BigDecimal divideAndRound128(long paramLong1, long paramLong2, long paramLong3, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramLong1 >= paramLong3) {
      return null;
    }
    int i = Long.numberOfLeadingZeros(paramLong3);
    paramLong3 <<= i;
    long l1 = paramLong3 >>> 32;
    long l2 = paramLong3 & 0xFFFFFFFF;
    long l3 = paramLong2 << i;
    long l4 = l3 >>> 32;
    long l5 = l3 & 0xFFFFFFFF;
    l3 = paramLong1 << i | paramLong2 >>> 64 - i;
    long l6 = l3 & 0xFFFFFFFF;
    long l7;
    long l8;
    if (l1 == 1L)
    {
      l7 = l3;
      l8 = 0L;
    }
    else if (l3 >= 0L)
    {
      l7 = l3 / l1;
      l8 = l3 - l7 * l1;
    }
    else
    {
      long[] arrayOfLong = divRemNegativeLong(l3, l1);
      l7 = arrayOfLong[1];
      l8 = arrayOfLong[0];
    }
    while ((l7 >= 4294967296L) || (unsignedLongCompare(l7 * l2, make64(l8, l4))))
    {
      l7 -= 1L;
      l8 += l1;
      if (l8 >= 4294967296L) {
        break;
      }
    }
    l3 = mulsub(l6, l4, l1, l2, l7);
    l4 = l3 & 0xFFFFFFFF;
    long l9;
    Object localObject;
    if (l1 == 1L)
    {
      l9 = l3;
      l8 = 0L;
    }
    else if (l3 >= 0L)
    {
      l9 = l3 / l1;
      l8 = l3 - l9 * l1;
    }
    else
    {
      localObject = divRemNegativeLong(l3, l1);
      l9 = localObject[1];
      l8 = localObject[0];
    }
    while ((l9 >= 4294967296L) || (unsignedLongCompare(l9 * l2, make64(l8, l5))))
    {
      l9 -= 1L;
      l8 += l1;
      if (l8 >= 4294967296L) {
        break;
      }
    }
    if ((int)l7 < 0)
    {
      localObject = new MutableBigInteger(new int[] { (int)l7, (int)l9 });
      if ((paramInt3 == 1) && (paramInt2 == paramInt4)) {
        return ((MutableBigInteger)localObject).toBigDecimal(paramInt1, paramInt2);
      }
      long l11 = mulsub(l4, l5, l1, l2, l9) >>> i;
      if (l11 != 0L)
      {
        if (needIncrement(paramLong3 >>> i, paramInt3, paramInt1, (MutableBigInteger)localObject, l11)) {
          ((MutableBigInteger)localObject).add(MutableBigInteger.ONE);
        }
        return ((MutableBigInteger)localObject).toBigDecimal(paramInt1, paramInt2);
      }
      if (paramInt4 != paramInt2)
      {
        BigInteger localBigInteger = ((MutableBigInteger)localObject).toBigInteger(paramInt1);
        return createAndStripZerosToMatchScale(localBigInteger, paramInt2, paramInt4);
      }
      return ((MutableBigInteger)localObject).toBigDecimal(paramInt1, paramInt2);
    }
    long l10 = make64(l7, l9);
    l10 *= paramInt1;
    if ((paramInt3 == 1) && (paramInt2 == paramInt4)) {
      return valueOf(l10, paramInt2);
    }
    long l12 = mulsub(l4, l5, l1, l2, l9) >>> i;
    if (l12 != 0L)
    {
      boolean bool = needIncrement(paramLong3 >>> i, paramInt3, paramInt1, l10, l12);
      return valueOf(bool ? l10 + paramInt1 : l10, paramInt2);
    }
    if (paramInt4 != paramInt2) {
      return createAndStripZerosToMatchScale(l10, paramInt2, paramInt4);
    }
    return valueOf(l10, paramInt2);
  }
  
  private static BigDecimal roundedTenPower(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt3 > paramInt4)
    {
      int i = paramInt3 - paramInt4;
      if (i < paramInt2) {
        return scaledTenPow(paramInt2 - i, paramInt1, paramInt4);
      }
      return valueOf(paramInt1, paramInt3 - paramInt2);
    }
    return scaledTenPow(paramInt2, paramInt1, paramInt3);
  }
  
  static BigDecimal scaledTenPow(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 < LONG_TEN_POWERS_TABLE.length) {
      return valueOf(paramInt2 * LONG_TEN_POWERS_TABLE[paramInt1], paramInt3);
    }
    BigInteger localBigInteger = bigTenToThe(paramInt1);
    if (paramInt2 == -1) {
      localBigInteger = localBigInteger.negate();
    }
    return new BigDecimal(localBigInteger, Long.MIN_VALUE, paramInt3, paramInt1 + 1);
  }
  
  private static long[] divRemNegativeLong(long paramLong1, long paramLong2)
  {
    assert (paramLong1 < 0L) : ("Non-negative numerator " + paramLong1);
    assert (paramLong2 != 1L) : "Unity denominator";
    long l1 = (paramLong1 >>> 1) / (paramLong2 >>> 1);
    long l2 = paramLong1 - l1 * paramLong2;
    while (l2 < 0L)
    {
      l2 += paramLong2;
      l1 -= 1L;
    }
    while (l2 >= paramLong2)
    {
      l2 -= paramLong2;
      l1 += 1L;
    }
    return new long[] { l2, l1 };
  }
  
  private static long make64(long paramLong1, long paramLong2)
  {
    return paramLong1 << 32 | paramLong2;
  }
  
  private static long mulsub(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    long l = paramLong2 - paramLong5 * paramLong4;
    return make64(paramLong1 + (l >>> 32) - paramLong5 * paramLong3, l & 0xFFFFFFFF);
  }
  
  private static boolean unsignedLongCompare(long paramLong1, long paramLong2)
  {
    return paramLong1 + Long.MIN_VALUE > paramLong2 + Long.MIN_VALUE;
  }
  
  private static boolean unsignedLongCompareEq(long paramLong1, long paramLong2)
  {
    return paramLong1 + Long.MIN_VALUE >= paramLong2 + Long.MIN_VALUE;
  }
  
  private static int compareMagnitudeNormalized(long paramLong1, int paramInt1, long paramLong2, int paramInt2)
  {
    int i = paramInt1 - paramInt2;
    if (i != 0) {
      if (i < 0) {
        paramLong1 = longMultiplyPowerTen(paramLong1, -i);
      } else {
        paramLong2 = longMultiplyPowerTen(paramLong2, i);
      }
    }
    if (paramLong1 != Long.MIN_VALUE) {
      return paramLong2 != Long.MIN_VALUE ? longCompareMagnitude(paramLong1, paramLong2) : -1;
    }
    return 1;
  }
  
  private static int compareMagnitudeNormalized(long paramLong, int paramInt1, BigInteger paramBigInteger, int paramInt2)
  {
    if (paramLong == 0L) {
      return -1;
    }
    int i = paramInt1 - paramInt2;
    if ((i < 0) && (longMultiplyPowerTen(paramLong, -i) == Long.MIN_VALUE)) {
      return bigMultiplyPowerTen(paramLong, -i).compareMagnitude(paramBigInteger);
    }
    return -1;
  }
  
  private static int compareMagnitudeNormalized(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2)
  {
    int i = paramInt1 - paramInt2;
    if (i < 0) {
      return bigMultiplyPowerTen(paramBigInteger1, -i).compareMagnitude(paramBigInteger2);
    }
    return paramBigInteger1.compareMagnitude(bigMultiplyPowerTen(paramBigInteger2, i));
  }
  
  private static long multiply(long paramLong1, long paramLong2)
  {
    long l1 = paramLong1 * paramLong2;
    long l2 = Math.abs(paramLong1);
    long l3 = Math.abs(paramLong2);
    if (((l2 | l3) >>> 31 == 0L) || (paramLong2 == 0L) || (l1 / paramLong2 == paramLong1)) {
      return l1;
    }
    return Long.MIN_VALUE;
  }
  
  private static BigDecimal multiply(long paramLong1, long paramLong2, int paramInt)
  {
    long l = multiply(paramLong1, paramLong2);
    if (l != Long.MIN_VALUE) {
      return valueOf(l, paramInt);
    }
    return new BigDecimal(BigInteger.valueOf(paramLong1).multiply(paramLong2), Long.MIN_VALUE, paramInt, 0);
  }
  
  private static BigDecimal multiply(long paramLong, BigInteger paramBigInteger, int paramInt)
  {
    if (paramLong == 0L) {
      return zeroValueOf(paramInt);
    }
    return new BigDecimal(paramBigInteger.multiply(paramLong), Long.MIN_VALUE, paramInt, 0);
  }
  
  private static BigDecimal multiply(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt)
  {
    return new BigDecimal(paramBigInteger1.multiply(paramBigInteger2), Long.MIN_VALUE, paramInt, 0);
  }
  
  private static BigDecimal multiplyAndRound(long paramLong1, long paramLong2, int paramInt, MathContext paramMathContext)
  {
    long l1 = multiply(paramLong1, paramLong2);
    if (l1 != Long.MIN_VALUE) {
      return doRound(l1, paramInt, paramMathContext);
    }
    int i = 1;
    if (paramLong1 < 0L)
    {
      paramLong1 = -paramLong1;
      i = -1;
    }
    if (paramLong2 < 0L)
    {
      paramLong2 = -paramLong2;
      i *= -1;
    }
    long l2 = paramLong1 >>> 32;
    long l3 = paramLong1 & 0xFFFFFFFF;
    long l4 = paramLong2 >>> 32;
    long l5 = paramLong2 & 0xFFFFFFFF;
    l1 = l3 * l5;
    long l6 = l1 & 0xFFFFFFFF;
    long l7 = l1 >>> 32;
    l1 = l2 * l5 + l7;
    l7 = l1 & 0xFFFFFFFF;
    long l8 = l1 >>> 32;
    l1 = l3 * l4 + l7;
    l7 = l1 & 0xFFFFFFFF;
    l8 += (l1 >>> 32);
    long l9 = l8 >>> 32;
    l8 &= 0xFFFFFFFF;
    l1 = l2 * l4 + l8;
    l8 = l1 & 0xFFFFFFFF;
    l9 = (l1 >>> 32) + l9 & 0xFFFFFFFF;
    long l10 = make64(l9, l8);
    long l11 = make64(l7, l6);
    BigDecimal localBigDecimal = doRound128(l10, l11, i, paramInt, paramMathContext);
    if (localBigDecimal != null) {
      return localBigDecimal;
    }
    localBigDecimal = new BigDecimal(BigInteger.valueOf(paramLong1).multiply(paramLong2 * i), Long.MIN_VALUE, paramInt, 0);
    return doRound(localBigDecimal, paramMathContext);
  }
  
  private static BigDecimal multiplyAndRound(long paramLong, BigInteger paramBigInteger, int paramInt, MathContext paramMathContext)
  {
    if (paramLong == 0L) {
      return zeroValueOf(paramInt);
    }
    return doRound(paramBigInteger.multiply(paramLong), paramInt, paramMathContext);
  }
  
  private static BigDecimal multiplyAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt, MathContext paramMathContext)
  {
    return doRound(paramBigInteger1.multiply(paramBigInteger2), paramInt, paramMathContext);
  }
  
  private static BigDecimal doRound128(long paramLong1, long paramLong2, int paramInt1, int paramInt2, MathContext paramMathContext)
  {
    int i = precision;
    BigDecimal localBigDecimal = null;
    int j;
    if (((j = precision(paramLong1, paramLong2) - i) > 0) && (j < LONG_TEN_POWERS_TABLE.length))
    {
      paramInt2 = checkScaleNonZero(paramInt2 - j);
      localBigDecimal = divideAndRound128(paramLong1, paramLong2, LONG_TEN_POWERS_TABLE[j], paramInt1, paramInt2, roundingMode.oldMode, paramInt2);
    }
    if (localBigDecimal != null) {
      return doRound(localBigDecimal, paramMathContext);
    }
    return null;
  }
  
  private static int precision(long paramLong1, long paramLong2)
  {
    if (paramLong1 == 0L)
    {
      if (paramLong2 >= 0L) {
        return longDigitLength(paramLong2);
      }
      return unsignedLongCompareEq(paramLong2, LONGLONG_TEN_POWERS_TABLE[0][1]) ? 20 : 19;
    }
    int i = (128 - Long.numberOfLeadingZeros(paramLong1) + 1) * 1233 >>> 12;
    int j = i - 19;
    return (j >= LONGLONG_TEN_POWERS_TABLE.length) || (longLongCompareMagnitude(paramLong1, paramLong2, LONGLONG_TEN_POWERS_TABLE[j][0], LONGLONG_TEN_POWERS_TABLE[j][1])) ? i : i + 1;
  }
  
  private static boolean longLongCompareMagnitude(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    if (paramLong1 != paramLong3) {
      return paramLong1 < paramLong3;
    }
    return paramLong2 + Long.MIN_VALUE < paramLong4 + Long.MIN_VALUE;
  }
  
  private static BigDecimal divide(long paramLong1, int paramInt1, long paramLong2, int paramInt2, int paramInt3, int paramInt4)
  {
    if (checkScale(paramLong1, paramInt3 + paramInt2) > paramInt1)
    {
      i = paramInt3 + paramInt2;
      j = i - paramInt1;
      if (j < LONG_TEN_POWERS_TABLE.length)
      {
        long l1 = paramLong1;
        if ((l1 = longMultiplyPowerTen(l1, j)) != Long.MIN_VALUE) {
          return divideAndRound(l1, paramLong2, paramInt3, paramInt4, paramInt3);
        }
        BigDecimal localBigDecimal = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[j], paramLong1, paramLong2, paramInt3, paramInt4, paramInt3);
        if (localBigDecimal != null) {
          return localBigDecimal;
        }
      }
      BigInteger localBigInteger1 = bigMultiplyPowerTen(paramLong1, j);
      return divideAndRound(localBigInteger1, paramLong2, paramInt3, paramInt4, paramInt3);
    }
    int i = checkScale(paramLong2, paramInt1 - paramInt3);
    int j = i - paramInt2;
    if (j < LONG_TEN_POWERS_TABLE.length)
    {
      long l2 = paramLong2;
      if ((l2 = longMultiplyPowerTen(l2, j)) != Long.MIN_VALUE) {
        return divideAndRound(paramLong1, l2, paramInt3, paramInt4, paramInt3);
      }
    }
    BigInteger localBigInteger2 = bigMultiplyPowerTen(paramLong2, j);
    return divideAndRound(BigInteger.valueOf(paramLong1), localBigInteger2, paramInt3, paramInt4, paramInt3);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4)
  {
    if (checkScale(paramBigInteger, paramInt3 + paramInt2) > paramInt1)
    {
      i = paramInt3 + paramInt2;
      j = i - paramInt1;
      BigInteger localBigInteger1 = bigMultiplyPowerTen(paramBigInteger, j);
      return divideAndRound(localBigInteger1, paramLong, paramInt3, paramInt4, paramInt3);
    }
    int i = checkScale(paramLong, paramInt1 - paramInt3);
    int j = i - paramInt2;
    if (j < LONG_TEN_POWERS_TABLE.length)
    {
      long l = paramLong;
      if ((l = longMultiplyPowerTen(l, j)) != Long.MIN_VALUE) {
        return divideAndRound(paramBigInteger, l, paramInt3, paramInt4, paramInt3);
      }
    }
    BigInteger localBigInteger2 = bigMultiplyPowerTen(paramLong, j);
    return divideAndRound(paramBigInteger, localBigInteger2, paramInt3, paramInt4, paramInt3);
  }
  
  private static BigDecimal divide(long paramLong, int paramInt1, BigInteger paramBigInteger, int paramInt2, int paramInt3, int paramInt4)
  {
    if (checkScale(paramLong, paramInt3 + paramInt2) > paramInt1)
    {
      i = paramInt3 + paramInt2;
      j = i - paramInt1;
      localBigInteger = bigMultiplyPowerTen(paramLong, j);
      return divideAndRound(localBigInteger, paramBigInteger, paramInt3, paramInt4, paramInt3);
    }
    int i = checkScale(paramBigInteger, paramInt1 - paramInt3);
    int j = i - paramInt2;
    BigInteger localBigInteger = bigMultiplyPowerTen(paramBigInteger, j);
    return divideAndRound(BigInteger.valueOf(paramLong), localBigInteger, paramInt3, paramInt4, paramInt3);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2, int paramInt3, int paramInt4)
  {
    if (checkScale(paramBigInteger1, paramInt3 + paramInt2) > paramInt1)
    {
      i = paramInt3 + paramInt2;
      j = i - paramInt1;
      localBigInteger = bigMultiplyPowerTen(paramBigInteger1, j);
      return divideAndRound(localBigInteger, paramBigInteger2, paramInt3, paramInt4, paramInt3);
    }
    int i = checkScale(paramBigInteger2, paramInt1 - paramInt3);
    int j = i - paramInt2;
    BigInteger localBigInteger = bigMultiplyPowerTen(paramBigInteger2, j);
    return divideAndRound(paramBigInteger1, localBigInteger, paramInt3, paramInt4, paramInt3);
  }
  
  private static class LongOverflow
  {
    private static final BigInteger LONGMIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONGMAX = BigInteger.valueOf(Long.MAX_VALUE);
    
    private LongOverflow() {}
    
    public static void check(BigDecimal paramBigDecimal)
    {
      BigInteger localBigInteger = paramBigDecimal.inflated();
      if ((localBigInteger.compareTo(LONGMIN) < 0) || (localBigInteger.compareTo(LONGMAX) > 0)) {
        throw new ArithmeticException("Overflow");
      }
    }
  }
  
  static class StringBuilderHelper
  {
    final StringBuilder sb = new StringBuilder();
    final char[] cmpCharArray = new char[19];
    static final char[] DIGIT_TENS = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9' };
    static final char[] DIGIT_ONES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    
    StringBuilderHelper() {}
    
    StringBuilder getStringBuilder()
    {
      sb.setLength(0);
      return sb;
    }
    
    char[] getCompactCharArray()
    {
      return cmpCharArray;
    }
    
    int putIntCompact(long paramLong)
    {
      assert (paramLong >= 0L);
      int j = cmpCharArray.length;
      int i;
      while (paramLong > 2147483647L)
      {
        long l = paramLong / 100L;
        i = (int)(paramLong - l * 100L);
        paramLong = l;
        cmpCharArray[(--j)] = DIGIT_ONES[i];
        cmpCharArray[(--j)] = DIGIT_TENS[i];
      }
      int m = (int)paramLong;
      while (m >= 100)
      {
        int k = m / 100;
        i = m - k * 100;
        m = k;
        cmpCharArray[(--j)] = DIGIT_ONES[i];
        cmpCharArray[(--j)] = DIGIT_TENS[i];
      }
      cmpCharArray[(--j)] = DIGIT_ONES[m];
      if (m >= 10) {
        cmpCharArray[(--j)] = DIGIT_TENS[m];
      }
      return j;
    }
  }
  
  private static class UnsafeHolder
  {
    private static final Unsafe unsafe;
    private static final long intCompactOffset;
    private static final long intValOffset;
    
    private UnsafeHolder() {}
    
    static void setIntCompactVolatile(BigDecimal paramBigDecimal, long paramLong)
    {
      unsafe.putLongVolatile(paramBigDecimal, intCompactOffset, paramLong);
    }
    
    static void setIntValVolatile(BigDecimal paramBigDecimal, BigInteger paramBigInteger)
    {
      unsafe.putObjectVolatile(paramBigDecimal, intValOffset, paramBigInteger);
    }
    
    static
    {
      try
      {
        unsafe = Unsafe.getUnsafe();
        intCompactOffset = unsafe.objectFieldOffset(BigDecimal.class.getDeclaredField("intCompact"));
        intValOffset = unsafe.objectFieldOffset(BigDecimal.class.getDeclaredField("intVal"));
      }
      catch (Exception localException)
      {
        throw new ExceptionInInitializerError(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\math\BigDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
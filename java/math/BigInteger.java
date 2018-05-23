package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import sun.misc.Unsafe;

public class BigInteger
  extends Number
  implements Comparable<BigInteger>
{
  final int signum;
  final int[] mag;
  @Deprecated
  private int bitCount;
  @Deprecated
  private int bitLength;
  @Deprecated
  private int lowestSetBit;
  @Deprecated
  private int firstNonzeroIntNum;
  static final long LONG_MASK = 4294967295L;
  private static final int MAX_MAG_LENGTH = 67108864;
  private static final int PRIME_SEARCH_BIT_LENGTH_LIMIT = 500000000;
  private static final int KARATSUBA_THRESHOLD = 80;
  private static final int TOOM_COOK_THRESHOLD = 240;
  private static final int KARATSUBA_SQUARE_THRESHOLD = 128;
  private static final int TOOM_COOK_SQUARE_THRESHOLD = 216;
  static final int BURNIKEL_ZIEGLER_THRESHOLD = 80;
  static final int BURNIKEL_ZIEGLER_OFFSET = 40;
  private static final int SCHOENHAGE_BASE_CONVERSION_THRESHOLD = 20;
  private static final int MULTIPLY_SQUARE_THRESHOLD = 20;
  private static final int MONTGOMERY_INTRINSIC_THRESHOLD = 512;
  private static long[] bitsPerDigit;
  private static final int SMALL_PRIME_THRESHOLD = 95;
  private static final int DEFAULT_PRIME_CERTAINTY = 100;
  private static final BigInteger SMALL_PRIME_PRODUCT;
  private static final int MAX_CONSTANT = 16;
  private static BigInteger[] posConst;
  private static BigInteger[] negConst;
  private static volatile BigInteger[][] powerCache;
  private static final double[] logCache;
  private static final double LOG_TWO;
  public static final BigInteger ZERO;
  public static final BigInteger ONE;
  private static final BigInteger TWO;
  private static final BigInteger NEGATIVE_ONE;
  public static final BigInteger TEN;
  static int[] bnExpModThreshTable;
  private static String[] zeros;
  private static int[] digitsPerLong = { 0, 0, 62, 39, 31, 27, 24, 22, 20, 19, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14, 14, 14, 14, 13, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 12, 12, 12 };
  private static BigInteger[] longRadix = { null, null, valueOf(4611686018427387904L), valueOf(4052555153018976267L), valueOf(4611686018427387904L), valueOf(7450580596923828125L), valueOf(4738381338321616896L), valueOf(3909821048582988049L), valueOf(1152921504606846976L), valueOf(1350851717672992089L), valueOf(1000000000000000000L), valueOf(5559917313492231481L), valueOf(2218611106740436992L), valueOf(8650415919381337933L), valueOf(2177953337809371136L), valueOf(6568408355712890625L), valueOf(1152921504606846976L), valueOf(2862423051509815793L), valueOf(6746640616477458432L), valueOf(799006685782884121L), valueOf(1638400000000000000L), valueOf(3243919932521508681L), valueOf(6221821273427820544L), valueOf(504036361936467383L), valueOf(876488338465357824L), valueOf(1490116119384765625L), valueOf(2481152873203736576L), valueOf(4052555153018976267L), valueOf(6502111422497947648L), valueOf(353814783205469041L), valueOf(531441000000000000L), valueOf(787662783788549761L), valueOf(1152921504606846976L), valueOf(1667889514952984961L), valueOf(2386420683693101056L), valueOf(3379220508056640625L), valueOf(4738381338321616896L) };
  private static int[] digitsPerInt = { 0, 0, 30, 19, 15, 13, 11, 11, 10, 9, 9, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5 };
  private static int[] intRadix = { 0, 0, 1073741824, 1162261467, 1073741824, 1220703125, 362797056, 1977326743, 1073741824, 387420489, 1000000000, 214358881, 429981696, 815730721, 1475789056, 170859375, 268435456, 410338673, 612220032, 893871739, 1280000000, 1801088541, 113379904, 148035889, 191102976, 244140625, 308915776, 387420489, 481890304, 594823321, 729000000, 887503681, 1073741824, 1291467969, 1544804416, 1838265625, 60466176 };
  private static final long serialVersionUID = -8287574255936472291L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("signum", Integer.TYPE), new ObjectStreamField("magnitude", byte[].class), new ObjectStreamField("bitCount", Integer.TYPE), new ObjectStreamField("bitLength", Integer.TYPE), new ObjectStreamField("firstNonzeroByteNum", Integer.TYPE), new ObjectStreamField("lowestSetBit", Integer.TYPE) };
  
  public BigInteger(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length == 0) {
      throw new NumberFormatException("Zero length BigInteger");
    }
    if (paramArrayOfByte[0] < 0)
    {
      mag = makePositive(paramArrayOfByte);
      signum = -1;
    }
    else
    {
      mag = stripLeadingZeroBytes(paramArrayOfByte);
      signum = (mag.length == 0 ? 0 : 1);
    }
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  private BigInteger(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length == 0) {
      throw new NumberFormatException("Zero length BigInteger");
    }
    if (paramArrayOfInt[0] < 0)
    {
      mag = makePositive(paramArrayOfInt);
      signum = -1;
    }
    else
    {
      mag = trustedStripLeadingZeroInts(paramArrayOfInt);
      signum = (mag.length == 0 ? 0 : 1);
    }
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  public BigInteger(int paramInt, byte[] paramArrayOfByte)
  {
    mag = stripLeadingZeroBytes(paramArrayOfByte);
    if ((paramInt < -1) || (paramInt > 1)) {
      throw new NumberFormatException("Invalid signum value");
    }
    if (mag.length == 0)
    {
      signum = 0;
    }
    else
    {
      if (paramInt == 0) {
        throw new NumberFormatException("signum-magnitude mismatch");
      }
      signum = paramInt;
    }
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  private BigInteger(int paramInt, int[] paramArrayOfInt)
  {
    mag = stripLeadingZeroInts(paramArrayOfInt);
    if ((paramInt < -1) || (paramInt > 1)) {
      throw new NumberFormatException("Invalid signum value");
    }
    if (mag.length == 0)
    {
      signum = 0;
    }
    else
    {
      if (paramInt == 0) {
        throw new NumberFormatException("signum-magnitude mismatch");
      }
      signum = paramInt;
    }
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  public BigInteger(String paramString, int paramInt)
  {
    int i = 0;
    int k = paramString.length();
    if ((paramInt < 2) || (paramInt > 36)) {
      throw new NumberFormatException("Radix out of range");
    }
    if (k == 0) {
      throw new NumberFormatException("Zero length BigInteger");
    }
    int m = 1;
    int n = paramString.lastIndexOf('-');
    int i1 = paramString.lastIndexOf('+');
    if (n >= 0)
    {
      if ((n != 0) || (i1 >= 0)) {
        throw new NumberFormatException("Illegal embedded sign character");
      }
      m = -1;
      i = 1;
    }
    else if (i1 >= 0)
    {
      if (i1 != 0) {
        throw new NumberFormatException("Illegal embedded sign character");
      }
      i = 1;
    }
    if (i == k) {
      throw new NumberFormatException("Zero length BigInteger");
    }
    while ((i < k) && (Character.digit(paramString.charAt(i), paramInt) == 0)) {
      i++;
    }
    if (i == k)
    {
      signum = 0;
      mag = ZEROmag;
      return;
    }
    int j = k - i;
    signum = m;
    long l = (j * bitsPerDigit[paramInt] >>> 10) + 1L;
    if (l + 31L >= 4294967296L) {
      reportOverflow();
    }
    int i2 = (int)(l + 31L) >>> 5;
    int[] arrayOfInt = new int[i2];
    int i3 = j % digitsPerInt[paramInt];
    if (i3 == 0) {
      i3 = digitsPerInt[paramInt];
    }
    String str = paramString.substring(i, i += i3);
    arrayOfInt[(i2 - 1)] = Integer.parseInt(str, paramInt);
    if (arrayOfInt[(i2 - 1)] < 0) {
      throw new NumberFormatException("Illegal digit");
    }
    int i4 = intRadix[paramInt];
    int i5 = 0;
    while (i < k)
    {
      str = paramString.substring(i, i += digitsPerInt[paramInt]);
      i5 = Integer.parseInt(str, paramInt);
      if (i5 < 0) {
        throw new NumberFormatException("Illegal digit");
      }
      destructiveMulAdd(arrayOfInt, i4, i5);
    }
    mag = trustedStripLeadingZeroInts(arrayOfInt);
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  BigInteger(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = 0; (i < paramInt2) && (Character.digit(paramArrayOfChar[i], 10) == 0); i++) {}
    if (i == paramInt2)
    {
      signum = 0;
      mag = ZEROmag;
      return;
    }
    int j = paramInt2 - i;
    signum = paramInt1;
    int k;
    if (paramInt2 < 10)
    {
      k = 1;
    }
    else
    {
      long l = (j * bitsPerDigit[10] >>> 10) + 1L;
      if (l + 31L >= 4294967296L) {
        reportOverflow();
      }
      k = (int)(l + 31L) >>> 5;
    }
    int[] arrayOfInt = new int[k];
    int m = j % digitsPerInt[10];
    if (m == 0) {
      m = digitsPerInt[10];
    }
    arrayOfInt[(k - 1)] = parseInt(paramArrayOfChar, i, i += m);
    while (i < paramInt2)
    {
      int n = parseInt(paramArrayOfChar, i, i += digitsPerInt[10]);
      destructiveMulAdd(arrayOfInt, intRadix[10], n);
    }
    mag = trustedStripLeadingZeroInts(arrayOfInt);
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  private int parseInt(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = Character.digit(paramArrayOfChar[(paramInt1++)], 10);
    if (i == -1) {
      throw new NumberFormatException(new String(paramArrayOfChar));
    }
    for (int j = paramInt1; j < paramInt2; j++)
    {
      int k = Character.digit(paramArrayOfChar[j], 10);
      if (k == -1) {
        throw new NumberFormatException(new String(paramArrayOfChar));
      }
      i = 10 * i + k;
    }
    return i;
  }
  
  private static void destructiveMulAdd(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    long l1 = paramInt1 & 0xFFFFFFFF;
    long l2 = paramInt2 & 0xFFFFFFFF;
    int i = paramArrayOfInt.length;
    long l3 = 0L;
    long l4 = 0L;
    for (int j = i - 1; j >= 0; j--)
    {
      l3 = l1 * (paramArrayOfInt[j] & 0xFFFFFFFF) + l4;
      paramArrayOfInt[j] = ((int)l3);
      l4 = l3 >>> 32;
    }
    long l5 = (paramArrayOfInt[(i - 1)] & 0xFFFFFFFF) + l2;
    paramArrayOfInt[(i - 1)] = ((int)l5);
    l4 = l5 >>> 32;
    for (int k = i - 2; k >= 0; k--)
    {
      l5 = (paramArrayOfInt[k] & 0xFFFFFFFF) + l4;
      paramArrayOfInt[k] = ((int)l5);
      l4 = l5 >>> 32;
    }
  }
  
  public BigInteger(String paramString)
  {
    this(paramString, 10);
  }
  
  public BigInteger(int paramInt, Random paramRandom)
  {
    this(1, randomBits(paramInt, paramRandom));
  }
  
  private static byte[] randomBits(int paramInt, Random paramRandom)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("numBits must be non-negative");
    }
    int i = (int)((paramInt + 7L) / 8L);
    byte[] arrayOfByte = new byte[i];
    if (i > 0)
    {
      paramRandom.nextBytes(arrayOfByte);
      int j = 8 * i - paramInt;
      int tmp49_48 = 0;
      byte[] tmp49_47 = arrayOfByte;
      tmp49_47[tmp49_48] = ((byte)(tmp49_47[tmp49_48] & (1 << 8 - j) - 1));
    }
    return arrayOfByte;
  }
  
  public BigInteger(int paramInt1, int paramInt2, Random paramRandom)
  {
    if (paramInt1 < 2) {
      throw new ArithmeticException("bitLength < 2");
    }
    BigInteger localBigInteger = paramInt1 < 95 ? smallPrime(paramInt1, paramInt2, paramRandom) : largePrime(paramInt1, paramInt2, paramRandom);
    signum = 1;
    mag = mag;
  }
  
  public static BigInteger probablePrime(int paramInt, Random paramRandom)
  {
    if (paramInt < 2) {
      throw new ArithmeticException("bitLength < 2");
    }
    return paramInt < 95 ? smallPrime(paramInt, 100, paramRandom) : largePrime(paramInt, 100, paramRandom);
  }
  
  private static BigInteger smallPrime(int paramInt1, int paramInt2, Random paramRandom)
  {
    int i = paramInt1 + 31 >>> 5;
    int[] arrayOfInt = new int[i];
    int j = 1 << (paramInt1 + 31 & 0x1F);
    int k = (j << 1) - 1;
    for (;;)
    {
      for (int m = 0; m < i; m++) {
        arrayOfInt[m] = paramRandom.nextInt();
      }
      arrayOfInt[0] = (arrayOfInt[0] & k | j);
      if (paramInt1 > 2) {
        arrayOfInt[(i - 1)] |= 0x1;
      }
      BigInteger localBigInteger = new BigInteger(arrayOfInt, 1);
      if (paramInt1 > 6)
      {
        long l = localBigInteger.remainder(SMALL_PRIME_PRODUCT).longValue();
        if ((l % 3L == 0L) || (l % 5L == 0L) || (l % 7L == 0L) || (l % 11L == 0L) || (l % 13L == 0L) || (l % 17L == 0L) || (l % 19L == 0L) || (l % 23L == 0L) || (l % 29L == 0L) || (l % 31L == 0L) || (l % 37L == 0L) || (l % 41L == 0L)) {}
      }
      else
      {
        if (paramInt1 < 4) {
          return localBigInteger;
        }
        if (localBigInteger.primeToCertainty(paramInt2, paramRandom)) {
          return localBigInteger;
        }
      }
    }
  }
  
  private static BigInteger largePrime(int paramInt1, int paramInt2, Random paramRandom)
  {
    BigInteger localBigInteger1 = new BigInteger(paramInt1, paramRandom).setBit(paramInt1 - 1);
    mag[(mag.length - 1)] &= 0xFFFFFFFE;
    int i = getPrimeSearchLen(paramInt1);
    BitSieve localBitSieve = new BitSieve(localBigInteger1, i);
    for (BigInteger localBigInteger2 = localBitSieve.retrieve(localBigInteger1, paramInt2, paramRandom); (localBigInteger2 == null) || (localBigInteger2.bitLength() != paramInt1); localBigInteger2 = localBitSieve.retrieve(localBigInteger1, paramInt2, paramRandom))
    {
      localBigInteger1 = localBigInteger1.add(valueOf(2 * i));
      if (localBigInteger1.bitLength() != paramInt1) {
        localBigInteger1 = new BigInteger(paramInt1, paramRandom).setBit(paramInt1 - 1);
      }
      mag[(mag.length - 1)] &= 0xFFFFFFFE;
      localBitSieve = new BitSieve(localBigInteger1, i);
    }
    return localBigInteger2;
  }
  
  public BigInteger nextProbablePrime()
  {
    if (signum < 0) {
      throw new ArithmeticException("start < 0: " + this);
    }
    if ((signum == 0) || (equals(ONE))) {
      return TWO;
    }
    BigInteger localBigInteger1 = add(ONE);
    if (localBigInteger1.bitLength() < 95)
    {
      if (!localBigInteger1.testBit(0)) {
        localBigInteger1 = localBigInteger1.add(ONE);
      }
      for (;;)
      {
        if (localBigInteger1.bitLength() > 6)
        {
          long l = localBigInteger1.remainder(SMALL_PRIME_PRODUCT).longValue();
          if ((l % 3L == 0L) || (l % 5L == 0L) || (l % 7L == 0L) || (l % 11L == 0L) || (l % 13L == 0L) || (l % 17L == 0L) || (l % 19L == 0L) || (l % 23L == 0L) || (l % 29L == 0L) || (l % 31L == 0L) || (l % 37L == 0L) || (l % 41L == 0L))
          {
            localBigInteger1 = localBigInteger1.add(TWO);
            continue;
          }
        }
        if (localBigInteger1.bitLength() < 4) {
          return localBigInteger1;
        }
        if (localBigInteger1.primeToCertainty(100, null)) {
          return localBigInteger1;
        }
        localBigInteger1 = localBigInteger1.add(TWO);
      }
    }
    if (localBigInteger1.testBit(0)) {
      localBigInteger1 = localBigInteger1.subtract(ONE);
    }
    int i = getPrimeSearchLen(localBigInteger1.bitLength());
    for (;;)
    {
      BitSieve localBitSieve = new BitSieve(localBigInteger1, i);
      BigInteger localBigInteger2 = localBitSieve.retrieve(localBigInteger1, 100, null);
      if (localBigInteger2 != null) {
        return localBigInteger2;
      }
      localBigInteger1 = localBigInteger1.add(valueOf(2 * i));
    }
  }
  
  private static int getPrimeSearchLen(int paramInt)
  {
    if (paramInt > 500000001) {
      throw new ArithmeticException("Prime search implementation restriction on bitLength");
    }
    return paramInt / 20 * 64;
  }
  
  boolean primeToCertainty(int paramInt, Random paramRandom)
  {
    int i = 0;
    int j = (Math.min(paramInt, 2147483646) + 1) / 2;
    int k = bitLength();
    if (k < 100)
    {
      i = 50;
      i = j < i ? j : i;
      return passesMillerRabin(i, paramRandom);
    }
    if (k < 256) {
      i = 27;
    } else if (k < 512) {
      i = 15;
    } else if (k < 768) {
      i = 8;
    } else if (k < 1024) {
      i = 4;
    } else {
      i = 2;
    }
    i = j < i ? j : i;
    return (passesMillerRabin(i, paramRandom)) && (passesLucasLehmer());
  }
  
  private boolean passesLucasLehmer()
  {
    BigInteger localBigInteger1 = add(ONE);
    for (int i = 5; jacobiSymbol(i, this) != -1; i = i < 0 ? Math.abs(i) + 2 : -(i + 2)) {}
    BigInteger localBigInteger2 = lucasLehmerSequence(i, localBigInteger1, this);
    return localBigInteger2.mod(this).equals(ZERO);
  }
  
  private static int jacobiSymbol(int paramInt, BigInteger paramBigInteger)
  {
    if (paramInt == 0) {
      return 0;
    }
    int i = 1;
    int j = mag[(mag.length - 1)];
    int k;
    if (paramInt < 0)
    {
      paramInt = -paramInt;
      k = j & 0x7;
      if ((k == 3) || (k == 7)) {
        i = -i;
      }
    }
    while ((paramInt & 0x3) == 0) {
      paramInt >>= 2;
    }
    if ((paramInt & 0x1) == 0)
    {
      paramInt >>= 1;
      if (((j ^ j >> 1) & 0x2) != 0) {
        i = -i;
      }
    }
    if (paramInt == 1) {
      return i;
    }
    if ((paramInt & j & 0x2) != 0) {
      i = -i;
    }
    j = paramBigInteger.mod(valueOf(paramInt)).intValue();
    while (j != 0)
    {
      while ((j & 0x3) == 0) {
        j >>= 2;
      }
      if ((j & 0x1) == 0)
      {
        j >>= 1;
        if (((paramInt ^ paramInt >> 1) & 0x2) != 0) {
          i = -i;
        }
      }
      if (j == 1) {
        return i;
      }
      assert (j < paramInt);
      k = j;
      j = paramInt;
      paramInt = k;
      if ((j & paramInt & 0x2) != 0) {
        i = -i;
      }
      j %= paramInt;
    }
    return 0;
  }
  
  private static BigInteger lucasLehmerSequence(int paramInt, BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    BigInteger localBigInteger1 = valueOf(paramInt);
    Object localObject1 = ONE;
    Object localObject2 = ONE;
    for (int i = paramBigInteger1.bitLength() - 2; i >= 0; i--)
    {
      BigInteger localBigInteger2 = ((BigInteger)localObject1).multiply((BigInteger)localObject2).mod(paramBigInteger2);
      BigInteger localBigInteger3 = ((BigInteger)localObject2).square().add(localBigInteger1.multiply(((BigInteger)localObject1).square())).mod(paramBigInteger2);
      if (localBigInteger3.testBit(0)) {
        localBigInteger3 = localBigInteger3.subtract(paramBigInteger2);
      }
      localBigInteger3 = localBigInteger3.shiftRight(1);
      localObject1 = localBigInteger2;
      localObject2 = localBigInteger3;
      if (paramBigInteger1.testBit(i))
      {
        localBigInteger2 = ((BigInteger)localObject1).add((BigInteger)localObject2).mod(paramBigInteger2);
        if (localBigInteger2.testBit(0)) {
          localBigInteger2 = localBigInteger2.subtract(paramBigInteger2);
        }
        localBigInteger2 = localBigInteger2.shiftRight(1);
        localBigInteger3 = ((BigInteger)localObject2).add(localBigInteger1.multiply((BigInteger)localObject1)).mod(paramBigInteger2);
        if (localBigInteger3.testBit(0)) {
          localBigInteger3 = localBigInteger3.subtract(paramBigInteger2);
        }
        localBigInteger3 = localBigInteger3.shiftRight(1);
        localObject1 = localBigInteger2;
        localObject2 = localBigInteger3;
      }
    }
    return (BigInteger)localObject1;
  }
  
  private boolean passesMillerRabin(int paramInt, Random paramRandom)
  {
    BigInteger localBigInteger1 = subtract(ONE);
    BigInteger localBigInteger2 = localBigInteger1;
    int i = localBigInteger2.getLowestSetBit();
    localBigInteger2 = localBigInteger2.shiftRight(i);
    if (paramRandom == null) {
      paramRandom = ThreadLocalRandom.current();
    }
    for (int j = 0; j < paramInt; j++)
    {
      BigInteger localBigInteger3;
      do
      {
        localBigInteger3 = new BigInteger(bitLength(), paramRandom);
      } while ((localBigInteger3.compareTo(ONE) <= 0) || (localBigInteger3.compareTo(this) >= 0));
      int k = 0;
      for (BigInteger localBigInteger4 = localBigInteger3.modPow(localBigInteger2, this); ((k != 0) || (!localBigInteger4.equals(ONE))) && (!localBigInteger4.equals(localBigInteger1)); localBigInteger4 = localBigInteger4.modPow(TWO, this)) {
        if ((k <= 0) || (!localBigInteger4.equals(ONE)))
        {
          k++;
          if (k != i) {}
        }
        else
        {
          return false;
        }
      }
    }
    return true;
  }
  
  BigInteger(int[] paramArrayOfInt, int paramInt)
  {
    signum = (paramArrayOfInt.length == 0 ? 0 : paramInt);
    mag = paramArrayOfInt;
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  private BigInteger(byte[] paramArrayOfByte, int paramInt)
  {
    signum = (paramArrayOfByte.length == 0 ? 0 : paramInt);
    mag = stripLeadingZeroBytes(paramArrayOfByte);
    if (mag.length >= 67108864) {
      checkRange();
    }
  }
  
  private void checkRange()
  {
    if ((mag.length > 67108864) || ((mag.length == 67108864) && (mag[0] < 0))) {
      reportOverflow();
    }
  }
  
  private static void reportOverflow()
  {
    throw new ArithmeticException("BigInteger would overflow supported range");
  }
  
  public static BigInteger valueOf(long paramLong)
  {
    if (paramLong == 0L) {
      return ZERO;
    }
    if ((paramLong > 0L) && (paramLong <= 16L)) {
      return posConst[((int)paramLong)];
    }
    if ((paramLong < 0L) && (paramLong >= -16L)) {
      return negConst[((int)-paramLong)];
    }
    return new BigInteger(paramLong);
  }
  
  private BigInteger(long paramLong)
  {
    if (paramLong < 0L)
    {
      paramLong = -paramLong;
      signum = -1;
    }
    else
    {
      signum = 1;
    }
    int i = (int)(paramLong >>> 32);
    if (i == 0)
    {
      mag = new int[1];
      mag[0] = ((int)paramLong);
    }
    else
    {
      mag = new int[2];
      mag[0] = i;
      mag[1] = ((int)paramLong);
    }
  }
  
  private static BigInteger valueOf(int[] paramArrayOfInt)
  {
    return paramArrayOfInt[0] > 0 ? new BigInteger(paramArrayOfInt, 1) : new BigInteger(paramArrayOfInt);
  }
  
  public BigInteger add(BigInteger paramBigInteger)
  {
    if (signum == 0) {
      return this;
    }
    if (signum == 0) {
      return paramBigInteger;
    }
    if (signum == signum) {
      return new BigInteger(add(mag, mag), signum);
    }
    int i = compareMagnitude(paramBigInteger);
    if (i == 0) {
      return ZERO;
    }
    int[] arrayOfInt = i > 0 ? subtract(mag, mag) : subtract(mag, mag);
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, i == signum ? 1 : -1);
  }
  
  BigInteger add(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    if (signum == 0) {
      return valueOf(paramLong);
    }
    if (Long.signum(paramLong) == signum) {
      return new BigInteger(add(mag, Math.abs(paramLong)), signum);
    }
    int i = compareMagnitude(paramLong);
    if (i == 0) {
      return ZERO;
    }
    int[] arrayOfInt = i > 0 ? subtract(mag, Math.abs(paramLong)) : subtract(Math.abs(paramLong), mag);
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, i == signum ? 1 : -1);
  }
  
  private static int[] add(int[] paramArrayOfInt, long paramLong)
  {
    long l = 0L;
    int i = paramArrayOfInt.length;
    int j = (int)(paramLong >>> 32);
    int[] arrayOfInt1;
    if (j == 0)
    {
      arrayOfInt1 = new int[i];
      l = (paramArrayOfInt[(--i)] & 0xFFFFFFFF) + paramLong;
      arrayOfInt1[i] = ((int)l);
    }
    else
    {
      if (i == 1)
      {
        arrayOfInt1 = new int[2];
        l = paramLong + (paramArrayOfInt[0] & 0xFFFFFFFF);
        arrayOfInt1[1] = ((int)l);
        arrayOfInt1[0] = ((int)(l >>> 32));
        return arrayOfInt1;
      }
      arrayOfInt1 = new int[i];
      l = (paramArrayOfInt[(--i)] & 0xFFFFFFFF) + (paramLong & 0xFFFFFFFF);
      arrayOfInt1[i] = ((int)l);
      l = (paramArrayOfInt[(--i)] & 0xFFFFFFFF) + (j & 0xFFFFFFFF) + (l >>> 32);
      arrayOfInt1[i] = ((int)l);
    }
    for (int k = l >>> 32 != 0L ? 1 : 0; (i > 0) && (k != 0); k = (arrayOfInt1[(--i)] = paramArrayOfInt[i] + 1) == 0 ? 1 : 0) {}
    while (i > 0) {
      arrayOfInt1[(--i)] = paramArrayOfInt[i];
    }
    if (k != 0)
    {
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 1, arrayOfInt1.length);
      arrayOfInt2[0] = 1;
      return arrayOfInt2;
    }
    return arrayOfInt1;
  }
  
  private static int[] add(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt1.length < paramArrayOfInt2.length)
    {
      int[] arrayOfInt1 = paramArrayOfInt1;
      paramArrayOfInt1 = paramArrayOfInt2;
      paramArrayOfInt2 = arrayOfInt1;
    }
    int i = paramArrayOfInt1.length;
    int j = paramArrayOfInt2.length;
    int[] arrayOfInt2 = new int[i];
    long l = 0L;
    if (j == 1)
    {
      l = (paramArrayOfInt1[(--i)] & 0xFFFFFFFF) + (paramArrayOfInt2[0] & 0xFFFFFFFF);
      arrayOfInt2[i] = ((int)l);
    }
    else
    {
      while (j > 0)
      {
        l = (paramArrayOfInt1[(--i)] & 0xFFFFFFFF) + (paramArrayOfInt2[(--j)] & 0xFFFFFFFF) + (l >>> 32);
        arrayOfInt2[i] = ((int)l);
      }
    }
    for (int k = l >>> 32 != 0L ? 1 : 0; (i > 0) && (k != 0); k = (arrayOfInt2[(--i)] = paramArrayOfInt1[i] + 1) == 0 ? 1 : 0) {}
    while (i > 0) {
      arrayOfInt2[(--i)] = paramArrayOfInt1[i];
    }
    if (k != 0)
    {
      int[] arrayOfInt3 = new int[arrayOfInt2.length + 1];
      System.arraycopy(arrayOfInt2, 0, arrayOfInt3, 1, arrayOfInt2.length);
      arrayOfInt3[0] = 1;
      return arrayOfInt3;
    }
    return arrayOfInt2;
  }
  
  private static int[] subtract(long paramLong, int[] paramArrayOfInt)
  {
    int i = (int)(paramLong >>> 32);
    if (i == 0)
    {
      arrayOfInt = new int[1];
      arrayOfInt[0] = ((int)(paramLong - (paramArrayOfInt[0] & 0xFFFFFFFF)));
      return arrayOfInt;
    }
    int[] arrayOfInt = new int[2];
    if (paramArrayOfInt.length == 1)
    {
      l = ((int)paramLong & 0xFFFFFFFF) - (paramArrayOfInt[0] & 0xFFFFFFFF);
      arrayOfInt[1] = ((int)l);
      int j = l >> 32 != 0L ? 1 : 0;
      if (j != 0) {
        arrayOfInt[0] = (i - 1);
      } else {
        arrayOfInt[0] = i;
      }
      return arrayOfInt;
    }
    long l = ((int)paramLong & 0xFFFFFFFF) - (paramArrayOfInt[1] & 0xFFFFFFFF);
    arrayOfInt[1] = ((int)l);
    l = (i & 0xFFFFFFFF) - (paramArrayOfInt[0] & 0xFFFFFFFF) + (l >> 32);
    arrayOfInt[0] = ((int)l);
    return arrayOfInt;
  }
  
  private static int[] subtract(int[] paramArrayOfInt, long paramLong)
  {
    int i = (int)(paramLong >>> 32);
    int j = paramArrayOfInt.length;
    int[] arrayOfInt = new int[j];
    long l = 0L;
    if (i == 0)
    {
      l = (paramArrayOfInt[(--j)] & 0xFFFFFFFF) - paramLong;
      arrayOfInt[j] = ((int)l);
    }
    else
    {
      l = (paramArrayOfInt[(--j)] & 0xFFFFFFFF) - (paramLong & 0xFFFFFFFF);
      arrayOfInt[j] = ((int)l);
      l = (paramArrayOfInt[(--j)] & 0xFFFFFFFF) - (i & 0xFFFFFFFF) + (l >> 32);
      arrayOfInt[j] = ((int)l);
    }
    for (int k = l >> 32 != 0L ? 1 : 0; (j > 0) && (k != 0); k = (arrayOfInt[(--j)] = paramArrayOfInt[j] - 1) == -1 ? 1 : 0) {}
    while (j > 0) {
      arrayOfInt[(--j)] = paramArrayOfInt[j];
    }
    return arrayOfInt;
  }
  
  public BigInteger subtract(BigInteger paramBigInteger)
  {
    if (signum == 0) {
      return this;
    }
    if (signum == 0) {
      return paramBigInteger.negate();
    }
    if (signum != signum) {
      return new BigInteger(add(mag, mag), signum);
    }
    int i = compareMagnitude(paramBigInteger);
    if (i == 0) {
      return ZERO;
    }
    int[] arrayOfInt = i > 0 ? subtract(mag, mag) : subtract(mag, mag);
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, i == signum ? 1 : -1);
  }
  
  private static int[] subtract(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    int i = paramArrayOfInt1.length;
    int[] arrayOfInt = new int[i];
    int j = paramArrayOfInt2.length;
    long l = 0L;
    while (j > 0)
    {
      l = (paramArrayOfInt1[(--i)] & 0xFFFFFFFF) - (paramArrayOfInt2[(--j)] & 0xFFFFFFFF) + (l >> 32);
      arrayOfInt[i] = ((int)l);
    }
    for (int k = l >> 32 != 0L ? 1 : 0; (i > 0) && (k != 0); k = (arrayOfInt[(--i)] = paramArrayOfInt1[i] - 1) == -1 ? 1 : 0) {}
    while (i > 0) {
      arrayOfInt[(--i)] = paramArrayOfInt1[i];
    }
    return arrayOfInt;
  }
  
  public BigInteger multiply(BigInteger paramBigInteger)
  {
    if ((signum == 0) || (signum == 0)) {
      return ZERO;
    }
    int i = mag.length;
    if ((paramBigInteger == this) && (i > 20)) {
      return square();
    }
    int j = mag.length;
    if ((i < 80) || (j < 80))
    {
      int k = signum == signum ? 1 : -1;
      if (mag.length == 1) {
        return multiplyByInt(mag, mag[0], k);
      }
      if (mag.length == 1) {
        return multiplyByInt(mag, mag[0], k);
      }
      int[] arrayOfInt = multiplyToLen(mag, i, mag, j, null);
      arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
      return new BigInteger(arrayOfInt, k);
    }
    if ((i < 240) && (j < 240)) {
      return multiplyKaratsuba(this, paramBigInteger);
    }
    return multiplyToomCook3(this, paramBigInteger);
  }
  
  private static BigInteger multiplyByInt(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (Integer.bitCount(paramInt1) == 1) {
      return new BigInteger(shiftLeft(paramArrayOfInt, Integer.numberOfTrailingZeros(paramInt1)), paramInt2);
    }
    int i = paramArrayOfInt.length;
    int[] arrayOfInt = new int[i + 1];
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFF;
    int j = arrayOfInt.length - 1;
    for (int k = i - 1; k >= 0; k--)
    {
      long l3 = (paramArrayOfInt[k] & 0xFFFFFFFF) * l2 + l1;
      arrayOfInt[(j--)] = ((int)l3);
      l1 = l3 >>> 32;
    }
    if (l1 == 0L) {
      arrayOfInt = Arrays.copyOfRange(arrayOfInt, 1, arrayOfInt.length);
    } else {
      arrayOfInt[j] = ((int)l1);
    }
    return new BigInteger(arrayOfInt, paramInt2);
  }
  
  BigInteger multiply(long paramLong)
  {
    if ((paramLong == 0L) || (signum == 0)) {
      return ZERO;
    }
    if (paramLong == Long.MIN_VALUE) {
      return multiply(valueOf(paramLong));
    }
    int i = paramLong > 0L ? signum : -signum;
    if (paramLong < 0L) {
      paramLong = -paramLong;
    }
    long l1 = paramLong >>> 32;
    long l2 = paramLong & 0xFFFFFFFF;
    int j = mag.length;
    int[] arrayOfInt1 = mag;
    int[] arrayOfInt2 = l1 == 0L ? new int[j + 1] : new int[j + 2];
    long l3 = 0L;
    int k = arrayOfInt2.length - 1;
    long l4;
    for (int m = j - 1; m >= 0; m--)
    {
      l4 = (arrayOfInt1[m] & 0xFFFFFFFF) * l2 + l3;
      arrayOfInt2[(k--)] = ((int)l4);
      l3 = l4 >>> 32;
    }
    arrayOfInt2[k] = ((int)l3);
    if (l1 != 0L)
    {
      l3 = 0L;
      k = arrayOfInt2.length - 2;
      for (m = j - 1; m >= 0; m--)
      {
        l4 = (arrayOfInt1[m] & 0xFFFFFFFF) * l1 + (arrayOfInt2[k] & 0xFFFFFFFF) + l3;
        arrayOfInt2[(k--)] = ((int)l4);
        l3 = l4 >>> 32;
      }
      arrayOfInt2[0] = ((int)l3);
    }
    if (l3 == 0L) {
      arrayOfInt2 = Arrays.copyOfRange(arrayOfInt2, 1, arrayOfInt2.length);
    }
    return new BigInteger(arrayOfInt2, i);
  }
  
  private static int[] multiplyToLen(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int[] paramArrayOfInt3)
  {
    int i = paramInt1 - 1;
    int j = paramInt2 - 1;
    if ((paramArrayOfInt3 == null) || (paramArrayOfInt3.length < paramInt1 + paramInt2)) {
      paramArrayOfInt3 = new int[paramInt1 + paramInt2];
    }
    long l1 = 0L;
    int k = j;
    for (int m = j + 1 + i; k >= 0; m--)
    {
      long l2 = (paramArrayOfInt2[k] & 0xFFFFFFFF) * (paramArrayOfInt1[i] & 0xFFFFFFFF) + l1;
      paramArrayOfInt3[m] = ((int)l2);
      l1 = l2 >>> 32;
      k--;
    }
    paramArrayOfInt3[i] = ((int)l1);
    for (k = i - 1; k >= 0; k--)
    {
      l1 = 0L;
      m = j;
      for (int n = j + 1 + k; m >= 0; n--)
      {
        long l3 = (paramArrayOfInt2[m] & 0xFFFFFFFF) * (paramArrayOfInt1[k] & 0xFFFFFFFF) + (paramArrayOfInt3[n] & 0xFFFFFFFF) + l1;
        paramArrayOfInt3[n] = ((int)l3);
        l1 = l3 >>> 32;
        m--;
      }
      paramArrayOfInt3[k] = ((int)l1);
    }
    return paramArrayOfInt3;
  }
  
  private static BigInteger multiplyKaratsuba(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    int i = mag.length;
    int j = mag.length;
    int k = (Math.max(i, j) + 1) / 2;
    BigInteger localBigInteger1 = paramBigInteger1.getLower(k);
    BigInteger localBigInteger2 = paramBigInteger1.getUpper(k);
    BigInteger localBigInteger3 = paramBigInteger2.getLower(k);
    BigInteger localBigInteger4 = paramBigInteger2.getUpper(k);
    BigInteger localBigInteger5 = localBigInteger2.multiply(localBigInteger4);
    BigInteger localBigInteger6 = localBigInteger1.multiply(localBigInteger3);
    BigInteger localBigInteger7 = localBigInteger2.add(localBigInteger1).multiply(localBigInteger4.add(localBigInteger3));
    BigInteger localBigInteger8 = localBigInteger5.shiftLeft(32 * k).add(localBigInteger7.subtract(localBigInteger5).subtract(localBigInteger6)).shiftLeft(32 * k).add(localBigInteger6);
    if (signum != signum) {
      return localBigInteger8.negate();
    }
    return localBigInteger8;
  }
  
  private static BigInteger multiplyToomCook3(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    int i = mag.length;
    int j = mag.length;
    int k = Math.max(i, j);
    int m = (k + 2) / 3;
    int n = k - 2 * m;
    BigInteger localBigInteger3 = paramBigInteger1.getToomSlice(m, n, 0, k);
    BigInteger localBigInteger2 = paramBigInteger1.getToomSlice(m, n, 1, k);
    BigInteger localBigInteger1 = paramBigInteger1.getToomSlice(m, n, 2, k);
    BigInteger localBigInteger6 = paramBigInteger2.getToomSlice(m, n, 0, k);
    BigInteger localBigInteger5 = paramBigInteger2.getToomSlice(m, n, 1, k);
    BigInteger localBigInteger4 = paramBigInteger2.getToomSlice(m, n, 2, k);
    BigInteger localBigInteger7 = localBigInteger1.multiply(localBigInteger4);
    BigInteger localBigInteger15 = localBigInteger3.add(localBigInteger1);
    BigInteger localBigInteger16 = localBigInteger6.add(localBigInteger4);
    BigInteger localBigInteger10 = localBigInteger15.subtract(localBigInteger2).multiply(localBigInteger16.subtract(localBigInteger5));
    localBigInteger15 = localBigInteger15.add(localBigInteger2);
    localBigInteger16 = localBigInteger16.add(localBigInteger5);
    BigInteger localBigInteger8 = localBigInteger15.multiply(localBigInteger16);
    BigInteger localBigInteger9 = localBigInteger15.add(localBigInteger3).shiftLeft(1).subtract(localBigInteger1).multiply(localBigInteger16.add(localBigInteger6).shiftLeft(1).subtract(localBigInteger4));
    BigInteger localBigInteger11 = localBigInteger3.multiply(localBigInteger6);
    BigInteger localBigInteger13 = localBigInteger9.subtract(localBigInteger10).exactDivideBy3();
    BigInteger localBigInteger14 = localBigInteger8.subtract(localBigInteger10).shiftRight(1);
    BigInteger localBigInteger12 = localBigInteger8.subtract(localBigInteger7);
    localBigInteger13 = localBigInteger13.subtract(localBigInteger12).shiftRight(1);
    localBigInteger12 = localBigInteger12.subtract(localBigInteger14).subtract(localBigInteger11);
    localBigInteger13 = localBigInteger13.subtract(localBigInteger11.shiftLeft(1));
    localBigInteger14 = localBigInteger14.subtract(localBigInteger13);
    int i1 = m * 32;
    BigInteger localBigInteger17 = localBigInteger11.shiftLeft(i1).add(localBigInteger13).shiftLeft(i1).add(localBigInteger12).shiftLeft(i1).add(localBigInteger14).shiftLeft(i1).add(localBigInteger7);
    if (signum != signum) {
      return localBigInteger17.negate();
    }
    return localBigInteger17;
  }
  
  private BigInteger getToomSlice(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int m = mag.length;
    int n = paramInt4 - m;
    int i;
    int j;
    if (paramInt3 == 0)
    {
      i = 0 - n;
      j = paramInt2 - 1 - n;
    }
    else
    {
      i = paramInt2 + (paramInt3 - 1) * paramInt1 - n;
      j = i + paramInt1 - 1;
    }
    if (i < 0) {
      i = 0;
    }
    if (j < 0) {
      return ZERO;
    }
    int k = j - i + 1;
    if (k <= 0) {
      return ZERO;
    }
    if ((i == 0) && (k >= m)) {
      return abs();
    }
    int[] arrayOfInt = new int[k];
    System.arraycopy(mag, i, arrayOfInt, 0, k);
    return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
  }
  
  private BigInteger exactDivideBy3()
  {
    int i = mag.length;
    int[] arrayOfInt = new int[i];
    long l4 = 0L;
    for (int j = i - 1; j >= 0; j--)
    {
      long l1 = mag[j] & 0xFFFFFFFF;
      long l2 = l1 - l4;
      if (l4 > l1) {
        l4 = 1L;
      } else {
        l4 = 0L;
      }
      long l3 = l2 * 2863311531L & 0xFFFFFFFF;
      arrayOfInt[j] = ((int)l3);
      if (l3 >= 1431655766L)
      {
        l4 += 1L;
        if (l3 >= 2863311531L) {
          l4 += 1L;
        }
      }
    }
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, signum);
  }
  
  private BigInteger getLower(int paramInt)
  {
    int i = mag.length;
    if (i <= paramInt) {
      return abs();
    }
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(mag, i - paramInt, arrayOfInt, 0, paramInt);
    return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
  }
  
  private BigInteger getUpper(int paramInt)
  {
    int i = mag.length;
    if (i <= paramInt) {
      return ZERO;
    }
    int j = i - paramInt;
    int[] arrayOfInt = new int[j];
    System.arraycopy(mag, 0, arrayOfInt, 0, j);
    return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
  }
  
  private BigInteger square()
  {
    if (signum == 0) {
      return ZERO;
    }
    int i = mag.length;
    if (i < 128)
    {
      int[] arrayOfInt = squareToLen(mag, i, null);
      return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
    }
    if (i < 216) {
      return squareKaratsuba();
    }
    return squareToomCook3();
  }
  
  private static final int[] squareToLen(int[] paramArrayOfInt1, int paramInt, int[] paramArrayOfInt2)
  {
    int i = paramInt << 1;
    if ((paramArrayOfInt2 == null) || (paramArrayOfInt2.length < i)) {
      paramArrayOfInt2 = new int[i];
    }
    implSquareToLenChecks(paramArrayOfInt1, paramInt, paramArrayOfInt2, i);
    return implSquareToLen(paramArrayOfInt1, paramInt, paramArrayOfInt2, i);
  }
  
  private static void implSquareToLenChecks(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2)
    throws RuntimeException
  {
    if (paramInt1 < 1) {
      throw new IllegalArgumentException("invalid input length: " + paramInt1);
    }
    if (paramInt1 > paramArrayOfInt1.length) {
      throw new IllegalArgumentException("input length out of bound: " + paramInt1 + " > " + paramArrayOfInt1.length);
    }
    if (paramInt1 * 2 > paramArrayOfInt2.length) {
      throw new IllegalArgumentException("input length out of bound: " + paramInt1 * 2 + " > " + paramArrayOfInt2.length);
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("invalid input length: " + paramInt2);
    }
    if (paramInt2 > paramArrayOfInt2.length) {
      throw new IllegalArgumentException("input length out of bound: " + paramInt1 + " > " + paramArrayOfInt2.length);
    }
  }
  
  private static final int[] implSquareToLen(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    while (j < paramInt1)
    {
      long l1 = paramArrayOfInt1[j] & 0xFFFFFFFF;
      long l2 = l1 * l1;
      paramArrayOfInt2[(k++)] = (i << 31 | (int)(l2 >>> 33));
      paramArrayOfInt2[(k++)] = ((int)(l2 >>> 1));
      i = (int)l2;
      j++;
    }
    j = paramInt1;
    for (k = 1; j > 0; k += 2)
    {
      int m = paramArrayOfInt1[(j - 1)];
      m = mulAdd(paramArrayOfInt2, paramArrayOfInt1, k, j - 1, m);
      addOne(paramArrayOfInt2, k - 1, j, m);
      j--;
    }
    primitiveLeftShift(paramArrayOfInt2, paramInt2, 1);
    paramArrayOfInt2[(paramInt2 - 1)] |= paramArrayOfInt1[(paramInt1 - 1)] & 0x1;
    return paramArrayOfInt2;
  }
  
  private BigInteger squareKaratsuba()
  {
    int i = (mag.length + 1) / 2;
    BigInteger localBigInteger1 = getLower(i);
    BigInteger localBigInteger2 = getUpper(i);
    BigInteger localBigInteger3 = localBigInteger2.square();
    BigInteger localBigInteger4 = localBigInteger1.square();
    return localBigInteger3.shiftLeft(i * 32).add(localBigInteger1.add(localBigInteger2).square().subtract(localBigInteger3.add(localBigInteger4))).shiftLeft(i * 32).add(localBigInteger4);
  }
  
  private BigInteger squareToomCook3()
  {
    int i = mag.length;
    int j = (i + 2) / 3;
    int k = i - 2 * j;
    BigInteger localBigInteger3 = getToomSlice(j, k, 0, i);
    BigInteger localBigInteger2 = getToomSlice(j, k, 1, i);
    BigInteger localBigInteger1 = getToomSlice(j, k, 2, i);
    BigInteger localBigInteger4 = localBigInteger1.square();
    BigInteger localBigInteger12 = localBigInteger3.add(localBigInteger1);
    BigInteger localBigInteger7 = localBigInteger12.subtract(localBigInteger2).square();
    localBigInteger12 = localBigInteger12.add(localBigInteger2);
    BigInteger localBigInteger5 = localBigInteger12.square();
    BigInteger localBigInteger8 = localBigInteger3.square();
    BigInteger localBigInteger6 = localBigInteger12.add(localBigInteger3).shiftLeft(1).subtract(localBigInteger1).square();
    BigInteger localBigInteger10 = localBigInteger6.subtract(localBigInteger7).exactDivideBy3();
    BigInteger localBigInteger11 = localBigInteger5.subtract(localBigInteger7).shiftRight(1);
    BigInteger localBigInteger9 = localBigInteger5.subtract(localBigInteger4);
    localBigInteger10 = localBigInteger10.subtract(localBigInteger9).shiftRight(1);
    localBigInteger9 = localBigInteger9.subtract(localBigInteger11).subtract(localBigInteger8);
    localBigInteger10 = localBigInteger10.subtract(localBigInteger8.shiftLeft(1));
    localBigInteger11 = localBigInteger11.subtract(localBigInteger10);
    int m = j * 32;
    return localBigInteger8.shiftLeft(m).add(localBigInteger10).shiftLeft(m).add(localBigInteger9).shiftLeft(m).add(localBigInteger11).shiftLeft(m).add(localBigInteger4);
  }
  
  public BigInteger divide(BigInteger paramBigInteger)
  {
    if ((mag.length < 80) || (mag.length - mag.length < 40)) {
      return divideKnuth(paramBigInteger);
    }
    return divideBurnikelZiegler(paramBigInteger);
  }
  
  private BigInteger divideKnuth(BigInteger paramBigInteger)
  {
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(mag);
    localMutableBigInteger2.divideKnuth(localMutableBigInteger3, localMutableBigInteger1, false);
    return localMutableBigInteger1.toBigInteger(signum * signum);
  }
  
  public BigInteger[] divideAndRemainder(BigInteger paramBigInteger)
  {
    if ((mag.length < 80) || (mag.length - mag.length < 40)) {
      return divideAndRemainderKnuth(paramBigInteger);
    }
    return divideAndRemainderBurnikelZiegler(paramBigInteger);
  }
  
  private BigInteger[] divideAndRemainderKnuth(BigInteger paramBigInteger)
  {
    BigInteger[] arrayOfBigInteger = new BigInteger[2];
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger4 = localMutableBigInteger2.divideKnuth(localMutableBigInteger3, localMutableBigInteger1);
    arrayOfBigInteger[0] = localMutableBigInteger1.toBigInteger(signum == signum ? 1 : -1);
    arrayOfBigInteger[1] = localMutableBigInteger4.toBigInteger(signum);
    return arrayOfBigInteger;
  }
  
  public BigInteger remainder(BigInteger paramBigInteger)
  {
    if ((mag.length < 80) || (mag.length - mag.length < 40)) {
      return remainderKnuth(paramBigInteger);
    }
    return remainderBurnikelZiegler(paramBigInteger);
  }
  
  private BigInteger remainderKnuth(BigInteger paramBigInteger)
  {
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(mag);
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(mag);
    return localMutableBigInteger2.divideKnuth(localMutableBigInteger3, localMutableBigInteger1).toBigInteger(signum);
  }
  
  private BigInteger divideBurnikelZiegler(BigInteger paramBigInteger)
  {
    return divideAndRemainderBurnikelZiegler(paramBigInteger)[0];
  }
  
  private BigInteger remainderBurnikelZiegler(BigInteger paramBigInteger)
  {
    return divideAndRemainderBurnikelZiegler(paramBigInteger)[1];
  }
  
  private BigInteger[] divideAndRemainderBurnikelZiegler(BigInteger paramBigInteger)
  {
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(this).divideAndRemainderBurnikelZiegler(new MutableBigInteger(paramBigInteger), localMutableBigInteger1);
    BigInteger localBigInteger1 = localMutableBigInteger1.isZero() ? ZERO : localMutableBigInteger1.toBigInteger(signum * signum);
    BigInteger localBigInteger2 = localMutableBigInteger2.isZero() ? ZERO : localMutableBigInteger2.toBigInteger(signum);
    return new BigInteger[] { localBigInteger1, localBigInteger2 };
  }
  
  public BigInteger pow(int paramInt)
  {
    if (paramInt < 0) {
      throw new ArithmeticException("Negative exponent");
    }
    if (signum == 0) {
      return paramInt == 0 ? ONE : this;
    }
    BigInteger localBigInteger1 = abs();
    int i = localBigInteger1.getLowestSetBit();
    long l1 = i * paramInt;
    if (l1 > 2147483647L) {
      reportOverflow();
    }
    int j;
    if (i > 0)
    {
      localBigInteger1 = localBigInteger1.shiftRight(i);
      j = localBigInteger1.bitLength();
      if (j == 1)
      {
        if ((signum < 0) && ((paramInt & 0x1) == 1)) {
          return NEGATIVE_ONE.shiftLeft(i * paramInt);
        }
        return ONE.shiftLeft(i * paramInt);
      }
    }
    else
    {
      j = localBigInteger1.bitLength();
      if (j == 1)
      {
        if ((signum < 0) && ((paramInt & 0x1) == 1)) {
          return NEGATIVE_ONE;
        }
        return ONE;
      }
    }
    long l2 = j * paramInt;
    if ((mag.length == 1) && (l2 <= 62L))
    {
      int k = (signum < 0) && ((paramInt & 0x1) == 1) ? -1 : 1;
      long l3 = 1L;
      long l4 = mag[0] & 0xFFFFFFFF;
      int n = paramInt;
      while (n != 0)
      {
        if ((n & 0x1) == 1) {
          l3 *= l4;
        }
        if (n >>>= 1 != 0) {
          l4 *= l4;
        }
      }
      if (i > 0)
      {
        if (l1 + l2 <= 62L) {
          return valueOf((l3 << (int)l1) * k);
        }
        return valueOf(l3 * k).shiftLeft((int)l1);
      }
      return valueOf(l3 * k);
    }
    BigInteger localBigInteger2 = ONE;
    int m = paramInt;
    while (m != 0)
    {
      if ((m & 0x1) == 1) {
        localBigInteger2 = localBigInteger2.multiply(localBigInteger1);
      }
      if (m >>>= 1 != 0) {
        localBigInteger1 = localBigInteger1.square();
      }
    }
    if (i > 0) {
      localBigInteger2 = localBigInteger2.shiftLeft(i * paramInt);
    }
    if ((signum < 0) && ((paramInt & 0x1) == 1)) {
      return localBigInteger2.negate();
    }
    return localBigInteger2;
  }
  
  public BigInteger gcd(BigInteger paramBigInteger)
  {
    if (signum == 0) {
      return abs();
    }
    if (signum == 0) {
      return paramBigInteger.abs();
    }
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(this);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(paramBigInteger);
    MutableBigInteger localMutableBigInteger3 = localMutableBigInteger1.hybridGCD(localMutableBigInteger2);
    return localMutableBigInteger3.toBigInteger(1);
  }
  
  static int bitLengthForInt(int paramInt)
  {
    return 32 - Integer.numberOfLeadingZeros(paramInt);
  }
  
  private static int[] leftShift(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramInt2 >>> 5;
    int j = paramInt2 & 0x1F;
    int k = bitLengthForInt(paramArrayOfInt[0]);
    if (paramInt2 <= 32 - k)
    {
      primitiveLeftShift(paramArrayOfInt, paramInt1, j);
      return paramArrayOfInt;
    }
    if (j <= 32 - k)
    {
      arrayOfInt = new int[i + paramInt1];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
      primitiveLeftShift(arrayOfInt, arrayOfInt.length, j);
      return arrayOfInt;
    }
    int[] arrayOfInt = new int[i + paramInt1 + 1];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
    primitiveRightShift(arrayOfInt, arrayOfInt.length, 32 - j);
    return arrayOfInt;
  }
  
  static void primitiveRightShift(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 32 - paramInt2;
    int j = paramInt1 - 1;
    int k = paramArrayOfInt[j];
    while (j > 0)
    {
      int m = k;
      k = paramArrayOfInt[(j - 1)];
      paramArrayOfInt[j] = (k << i | m >>> paramInt2);
      j--;
    }
    paramArrayOfInt[0] >>>= paramInt2;
  }
  
  static void primitiveLeftShift(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      return;
    }
    int i = 32 - paramInt2;
    int j = 0;
    int k = paramArrayOfInt[j];
    int m = j + paramInt1 - 1;
    while (j < m)
    {
      int n = k;
      k = paramArrayOfInt[(j + 1)];
      paramArrayOfInt[j] = (n << paramInt2 | k >>> i);
      j++;
    }
    paramArrayOfInt[(paramInt1 - 1)] <<= paramInt2;
  }
  
  private static int bitLength(int[] paramArrayOfInt, int paramInt)
  {
    if (paramInt == 0) {
      return 0;
    }
    return (paramInt - 1 << 5) + bitLengthForInt(paramArrayOfInt[0]);
  }
  
  public BigInteger abs()
  {
    return signum >= 0 ? this : negate();
  }
  
  public BigInteger negate()
  {
    return new BigInteger(mag, -signum);
  }
  
  public int signum()
  {
    return signum;
  }
  
  public BigInteger mod(BigInteger paramBigInteger)
  {
    if (signum <= 0) {
      throw new ArithmeticException("BigInteger: modulus not positive");
    }
    BigInteger localBigInteger = remainder(paramBigInteger);
    return signum >= 0 ? localBigInteger : localBigInteger.add(paramBigInteger);
  }
  
  public BigInteger modPow(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    if (signum <= 0) {
      throw new ArithmeticException("BigInteger: modulus not positive");
    }
    if (signum == 0) {
      return paramBigInteger2.equals(ONE) ? ZERO : ONE;
    }
    if (equals(ONE)) {
      return paramBigInteger2.equals(ONE) ? ZERO : ONE;
    }
    if ((equals(ZERO)) && (signum >= 0)) {
      return ZERO;
    }
    if ((equals(negConst[1])) && (!paramBigInteger1.testBit(0))) {
      return paramBigInteger2.equals(ONE) ? ZERO : ONE;
    }
    int i;
    if ((i = signum < 0 ? 1 : 0) != 0) {
      paramBigInteger1 = paramBigInteger1.negate();
    }
    BigInteger localBigInteger1 = (signum < 0) || (compareTo(paramBigInteger2) >= 0) ? mod(paramBigInteger2) : this;
    BigInteger localBigInteger2;
    if (paramBigInteger2.testBit(0))
    {
      localBigInteger2 = localBigInteger1.oddModPow(paramBigInteger1, paramBigInteger2);
    }
    else
    {
      int j = paramBigInteger2.getLowestSetBit();
      BigInteger localBigInteger3 = paramBigInteger2.shiftRight(j);
      BigInteger localBigInteger4 = ONE.shiftLeft(j);
      BigInteger localBigInteger5 = (signum < 0) || (compareTo(localBigInteger3) >= 0) ? mod(localBigInteger3) : this;
      BigInteger localBigInteger6 = localBigInteger3.equals(ONE) ? ZERO : localBigInteger5.oddModPow(paramBigInteger1, localBigInteger3);
      BigInteger localBigInteger7 = localBigInteger1.modPow2(paramBigInteger1, j);
      BigInteger localBigInteger8 = localBigInteger4.modInverse(localBigInteger3);
      BigInteger localBigInteger9 = localBigInteger3.modInverse(localBigInteger4);
      if (mag.length < 33554432)
      {
        localBigInteger2 = localBigInteger6.multiply(localBigInteger4).multiply(localBigInteger8).add(localBigInteger7.multiply(localBigInteger3).multiply(localBigInteger9)).mod(paramBigInteger2);
      }
      else
      {
        MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
        new MutableBigInteger(localBigInteger6.multiply(localBigInteger4)).multiply(new MutableBigInteger(localBigInteger8), localMutableBigInteger1);
        MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
        new MutableBigInteger(localBigInteger7.multiply(localBigInteger3)).multiply(new MutableBigInteger(localBigInteger9), localMutableBigInteger2);
        localMutableBigInteger1.add(localMutableBigInteger2);
        MutableBigInteger localMutableBigInteger3 = new MutableBigInteger();
        localBigInteger2 = localMutableBigInteger1.divide(new MutableBigInteger(paramBigInteger2), localMutableBigInteger3).toBigInteger();
      }
    }
    return i != 0 ? localBigInteger2.modInverse(paramBigInteger2) : localBigInteger2;
  }
  
  private static int[] montgomeryMultiply(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt, long paramLong, int[] paramArrayOfInt4)
  {
    implMontgomeryMultiplyChecks(paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramInt, paramArrayOfInt4);
    if (paramInt > 512)
    {
      paramArrayOfInt4 = multiplyToLen(paramArrayOfInt1, paramInt, paramArrayOfInt2, paramInt, paramArrayOfInt4);
      return montReduce(paramArrayOfInt4, paramArrayOfInt3, paramInt, (int)paramLong);
    }
    return implMontgomeryMultiply(paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramInt, paramLong, materialize(paramArrayOfInt4, paramInt));
  }
  
  private static int[] montgomerySquare(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, long paramLong, int[] paramArrayOfInt3)
  {
    implMontgomeryMultiplyChecks(paramArrayOfInt1, paramArrayOfInt1, paramArrayOfInt2, paramInt, paramArrayOfInt3);
    if (paramInt > 512)
    {
      paramArrayOfInt3 = squareToLen(paramArrayOfInt1, paramInt, paramArrayOfInt3);
      return montReduce(paramArrayOfInt3, paramArrayOfInt2, paramInt, (int)paramLong);
    }
    return implMontgomerySquare(paramArrayOfInt1, paramArrayOfInt2, paramInt, paramLong, materialize(paramArrayOfInt3, paramInt));
  }
  
  private static void implMontgomeryMultiplyChecks(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt, int[] paramArrayOfInt4)
    throws RuntimeException
  {
    if (paramInt % 2 != 0) {
      throw new IllegalArgumentException("input array length must be even: " + paramInt);
    }
    if (paramInt < 1) {
      throw new IllegalArgumentException("invalid input length: " + paramInt);
    }
    if ((paramInt > paramArrayOfInt1.length) || (paramInt > paramArrayOfInt2.length) || (paramInt > paramArrayOfInt3.length) || ((paramArrayOfInt4 != null) && (paramInt > paramArrayOfInt4.length))) {
      throw new IllegalArgumentException("input array length out of bound: " + paramInt);
    }
  }
  
  private static int[] materialize(int[] paramArrayOfInt, int paramInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length < paramInt)) {
      paramArrayOfInt = new int[paramInt];
    }
    return paramArrayOfInt;
  }
  
  private static int[] implMontgomeryMultiply(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt, long paramLong, int[] paramArrayOfInt4)
  {
    paramArrayOfInt4 = multiplyToLen(paramArrayOfInt1, paramInt, paramArrayOfInt2, paramInt, paramArrayOfInt4);
    return montReduce(paramArrayOfInt4, paramArrayOfInt3, paramInt, (int)paramLong);
  }
  
  private static int[] implMontgomerySquare(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, long paramLong, int[] paramArrayOfInt3)
  {
    paramArrayOfInt3 = squareToLen(paramArrayOfInt1, paramInt, paramArrayOfInt3);
    return montReduce(paramArrayOfInt3, paramArrayOfInt2, paramInt, (int)paramLong);
  }
  
  private BigInteger oddModPow(BigInteger paramBigInteger1, BigInteger paramBigInteger2)
  {
    if (paramBigInteger1.equals(ONE)) {
      return this;
    }
    if (signum == 0) {
      return ZERO;
    }
    int[] arrayOfInt1 = (int[])mag.clone();
    int[] arrayOfInt2 = mag;
    Object localObject1 = mag;
    int i = localObject1.length;
    if ((i & 0x1) != 0)
    {
      int[] arrayOfInt3 = new int[i + 1];
      System.arraycopy(localObject1, 0, arrayOfInt3, 1, i);
      localObject1 = arrayOfInt3;
      i++;
    }
    int j = 0;
    int k = bitLength(arrayOfInt2, arrayOfInt2.length);
    if ((k != 17) || (arrayOfInt2[0] != 65537)) {
      while (k > bnExpModThreshTable[j]) {
        j++;
      }
    }
    int m = 1 << j;
    int[][] arrayOfInt = new int[m][];
    for (int n = 0; n < m; n++) {
      arrayOfInt[n] = new int[i];
    }
    long l1 = (localObject1[(i - 1)] & 0xFFFFFFFF) + ((localObject1[(i - 2)] & 0xFFFFFFFF) << 32);
    long l2 = -MutableBigInteger.inverseMod64(l1);
    Object localObject2 = leftShift(arrayOfInt1, arrayOfInt1.length, i << 5);
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger((int[])localObject2);
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger((int[])localObject1);
    localMutableBigInteger3.normalize();
    MutableBigInteger localMutableBigInteger4 = localMutableBigInteger2.divide(localMutableBigInteger3, localMutableBigInteger1);
    arrayOfInt[0] = localMutableBigInteger4.toIntArray();
    if (arrayOfInt[0].length < i)
    {
      int i1 = i - arrayOfInt[0].length;
      localObject4 = new int[i];
      System.arraycopy(arrayOfInt[0], 0, localObject4, i1, arrayOfInt[0].length);
      arrayOfInt[0] = localObject4;
    }
    Object localObject3 = montgomerySquare(arrayOfInt[0], (int[])localObject1, i, l2, null);
    Object localObject4 = Arrays.copyOf((int[])localObject3, i);
    for (int i2 = 1; i2 < m; i2++) {
      arrayOfInt[i2] = montgomeryMultiply((int[])localObject4, arrayOfInt[(i2 - 1)], (int[])localObject1, i, l2, null);
    }
    i2 = 1 << (k - 1 & 0x1F);
    int i3 = 0;
    int i4 = arrayOfInt2.length;
    int i5 = 0;
    for (int i6 = 0; i6 <= j; i6++)
    {
      i3 = i3 << 1 | ((arrayOfInt2[i5] & i2) != 0 ? 1 : 0);
      i2 >>>= 1;
      if (i2 == 0)
      {
        i5++;
        i2 = Integer.MIN_VALUE;
        i4--;
      }
    }
    i6 = k;
    k--;
    int i7 = 1;
    for (i6 = k - j; (i3 & 0x1) == 0; i6++) {
      i3 >>>= 1;
    }
    int[] arrayOfInt4 = arrayOfInt[(i3 >>> 1)];
    i3 = 0;
    if (i6 == k) {
      i7 = 0;
    }
    for (;;)
    {
      k--;
      i3 <<= 1;
      if (i4 != 0)
      {
        i3 |= ((arrayOfInt2[i5] & i2) != 0 ? 1 : 0);
        i2 >>>= 1;
        if (i2 == 0)
        {
          i5++;
          i2 = Integer.MIN_VALUE;
          i4--;
        }
      }
      if ((i3 & m) != 0)
      {
        for (i6 = k - j; (i3 & 0x1) == 0; i6++) {
          i3 >>>= 1;
        }
        arrayOfInt4 = arrayOfInt[(i3 >>> 1)];
        i3 = 0;
      }
      if (k == i6) {
        if (i7 != 0)
        {
          localObject3 = (int[])arrayOfInt4.clone();
          i7 = 0;
        }
        else
        {
          localObject4 = localObject3;
          localObject2 = montgomeryMultiply((int[])localObject4, arrayOfInt4, (int[])localObject1, i, l2, (int[])localObject2);
          localObject4 = localObject2;
          localObject2 = localObject3;
          localObject3 = localObject4;
        }
      }
      if (k == 0) {
        break;
      }
      if (i7 == 0)
      {
        localObject4 = localObject3;
        localObject2 = montgomerySquare((int[])localObject4, (int[])localObject1, i, l2, (int[])localObject2);
        localObject4 = localObject2;
        localObject2 = localObject3;
        localObject3 = localObject4;
      }
    }
    int[] arrayOfInt5 = new int[2 * i];
    System.arraycopy(localObject3, 0, arrayOfInt5, i, i);
    localObject3 = montReduce(arrayOfInt5, (int[])localObject1, i, (int)l2);
    arrayOfInt5 = Arrays.copyOf((int[])localObject3, i);
    return new BigInteger(1, arrayOfInt5);
  }
  
  private static int[] montReduce(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramInt1;
    int k = 0;
    do
    {
      int m = paramArrayOfInt1[(paramArrayOfInt1.length - 1 - k)];
      int n = mulAdd(paramArrayOfInt1, paramArrayOfInt2, k, paramInt1, paramInt2 * m);
      i += addOne(paramArrayOfInt1, k, paramInt1, n);
      k++;
      j--;
    } while (j > 0);
    while (i > 0) {
      i += subN(paramArrayOfInt1, paramArrayOfInt2, paramInt1);
    }
    while (intArrayCmpToLen(paramArrayOfInt1, paramArrayOfInt2, paramInt1) >= 0) {
      subN(paramArrayOfInt1, paramArrayOfInt2, paramInt1);
    }
    return paramArrayOfInt1;
  }
  
  private static int intArrayCmpToLen(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
    {
      long l1 = paramArrayOfInt1[i] & 0xFFFFFFFF;
      long l2 = paramArrayOfInt2[i] & 0xFFFFFFFF;
      if (l1 < l2) {
        return -1;
      }
      if (l1 > l2) {
        return 1;
      }
    }
    return 0;
  }
  
  private static int subN(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    long l = 0L;
    for (;;)
    {
      paramInt--;
      if (paramInt < 0) {
        break;
      }
      l = (paramArrayOfInt1[paramInt] & 0xFFFFFFFF) - (paramArrayOfInt2[paramInt] & 0xFFFFFFFF) + (l >> 32);
      paramArrayOfInt1[paramInt] = ((int)l);
    }
    return (int)(l >> 32);
  }
  
  static int mulAdd(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3)
  {
    implMulAddCheck(paramArrayOfInt1, paramArrayOfInt2, paramInt1, paramInt2, paramInt3);
    return implMulAdd(paramArrayOfInt1, paramArrayOfInt2, paramInt1, paramInt2, paramInt3);
  }
  
  private static void implMulAddCheck(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 > paramArrayOfInt2.length) {
      throw new IllegalArgumentException("input length is out of bound: " + paramInt2 + " > " + paramArrayOfInt2.length);
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("input offset is invalid: " + paramInt1);
    }
    if (paramInt1 > paramArrayOfInt1.length - 1) {
      throw new IllegalArgumentException("input offset is out of bound: " + paramInt1 + " > " + (paramArrayOfInt1.length - 1));
    }
    if (paramInt2 > paramArrayOfInt1.length - paramInt1) {
      throw new IllegalArgumentException("input len is out of bound: " + paramInt2 + " > " + (paramArrayOfInt1.length - paramInt1));
    }
  }
  
  private static int implMulAdd(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3)
  {
    long l1 = paramInt3 & 0xFFFFFFFF;
    long l2 = 0L;
    paramInt1 = paramArrayOfInt1.length - paramInt1 - 1;
    for (int i = paramInt2 - 1; i >= 0; i--)
    {
      long l3 = (paramArrayOfInt2[i] & 0xFFFFFFFF) * l1 + (paramArrayOfInt1[paramInt1] & 0xFFFFFFFF) + l2;
      paramArrayOfInt1[(paramInt1--)] = ((int)l3);
      l2 = l3 >>> 32;
    }
    return (int)l2;
  }
  
  static int addOne(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 = paramArrayOfInt.length - 1 - paramInt2 - paramInt1;
    long l = (paramArrayOfInt[paramInt1] & 0xFFFFFFFF) + (paramInt3 & 0xFFFFFFFF);
    paramArrayOfInt[paramInt1] = ((int)l);
    if (l >>> 32 == 0L) {
      return 0;
    }
    do
    {
      paramInt2--;
      if (paramInt2 < 0) {
        break;
      }
      paramInt1--;
      if (paramInt1 < 0) {
        return 1;
      }
      paramArrayOfInt[paramInt1] += 1;
    } while (paramArrayOfInt[paramInt1] == 0);
    return 0;
    return 1;
  }
  
  private BigInteger modPow2(BigInteger paramBigInteger, int paramInt)
  {
    BigInteger localBigInteger1 = ONE;
    BigInteger localBigInteger2 = mod2(paramInt);
    int i = 0;
    int j = paramBigInteger.bitLength();
    if (testBit(0)) {
      j = paramInt - 1 < j ? paramInt - 1 : j;
    }
    while (i < j)
    {
      if (paramBigInteger.testBit(i)) {
        localBigInteger1 = localBigInteger1.multiply(localBigInteger2).mod2(paramInt);
      }
      i++;
      if (i < j) {
        localBigInteger2 = localBigInteger2.square().mod2(paramInt);
      }
    }
    return localBigInteger1;
  }
  
  private BigInteger mod2(int paramInt)
  {
    if (bitLength() <= paramInt) {
      return this;
    }
    int i = paramInt + 31 >>> 5;
    int[] arrayOfInt = new int[i];
    System.arraycopy(mag, mag.length - i, arrayOfInt, 0, i);
    int j = (i << 5) - paramInt;
    int tmp47_46 = 0;
    int[] tmp47_45 = arrayOfInt;
    tmp47_45[tmp47_46] = ((int)(tmp47_45[tmp47_46] & (1L << 32 - j) - 1L));
    return arrayOfInt[0] == 0 ? new BigInteger(1, arrayOfInt) : new BigInteger(arrayOfInt, 1);
  }
  
  public BigInteger modInverse(BigInteger paramBigInteger)
  {
    if (signum != 1) {
      throw new ArithmeticException("BigInteger: modulus not positive");
    }
    if (paramBigInteger.equals(ONE)) {
      return ZERO;
    }
    BigInteger localBigInteger = this;
    if ((signum < 0) || (compareMagnitude(paramBigInteger) >= 0)) {
      localBigInteger = mod(paramBigInteger);
    }
    if (localBigInteger.equals(ONE)) {
      return ONE;
    }
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(localBigInteger);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(paramBigInteger);
    MutableBigInteger localMutableBigInteger3 = localMutableBigInteger1.mutableModInverse(localMutableBigInteger2);
    return localMutableBigInteger3.toBigInteger(1);
  }
  
  public BigInteger shiftLeft(int paramInt)
  {
    if (signum == 0) {
      return ZERO;
    }
    if (paramInt > 0) {
      return new BigInteger(shiftLeft(mag, paramInt), signum);
    }
    if (paramInt == 0) {
      return this;
    }
    return shiftRightImpl(-paramInt);
  }
  
  private static int[] shiftLeft(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    int k = paramArrayOfInt.length;
    int[] arrayOfInt = null;
    if (j == 0)
    {
      arrayOfInt = new int[k + i];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, k);
    }
    else
    {
      int m = 0;
      int n = 32 - j;
      int i1 = paramArrayOfInt[0] >>> n;
      if (i1 != 0)
      {
        arrayOfInt = new int[k + i + 1];
        arrayOfInt[(m++)] = i1;
      }
      else
      {
        arrayOfInt = new int[k + i];
      }
      int i2 = 0;
      while (i2 < k - 1) {
        arrayOfInt[(m++)] = (paramArrayOfInt[(i2++)] << j | paramArrayOfInt[i2] >>> n);
      }
      paramArrayOfInt[i2] <<= j;
    }
    return arrayOfInt;
  }
  
  public BigInteger shiftRight(int paramInt)
  {
    if (signum == 0) {
      return ZERO;
    }
    if (paramInt > 0) {
      return shiftRightImpl(paramInt);
    }
    if (paramInt == 0) {
      return this;
    }
    return new BigInteger(shiftLeft(mag, -paramInt), signum);
  }
  
  private BigInteger shiftRightImpl(int paramInt)
  {
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    int k = mag.length;
    int[] arrayOfInt = null;
    if (i >= k) {
      return signum >= 0 ? ZERO : negConst[1];
    }
    int m;
    int n;
    int i1;
    if (j == 0)
    {
      m = k - i;
      arrayOfInt = Arrays.copyOf(mag, m);
    }
    else
    {
      m = 0;
      n = mag[0] >>> j;
      if (n != 0)
      {
        arrayOfInt = new int[k - i];
        arrayOfInt[(m++)] = n;
      }
      else
      {
        arrayOfInt = new int[k - i - 1];
      }
      i1 = 32 - j;
      int i2 = 0;
      while (i2 < k - i - 1) {
        arrayOfInt[(m++)] = (mag[(i2++)] << i1 | mag[i2] >>> j);
      }
    }
    if (signum < 0)
    {
      m = 0;
      n = k - 1;
      i1 = k - i;
      while ((n >= i1) && (m == 0))
      {
        m = mag[n] != 0 ? 1 : 0;
        n--;
      }
      if ((m == 0) && (j != 0)) {
        m = mag[(k - i - 1)] << 32 - j != 0 ? 1 : 0;
      }
      if (m != 0) {
        arrayOfInt = javaIncrement(arrayOfInt);
      }
    }
    return new BigInteger(arrayOfInt, signum);
  }
  
  int[] javaIncrement(int[] paramArrayOfInt)
  {
    int i = 0;
    for (int j = paramArrayOfInt.length - 1; (j >= 0) && (i == 0); j--) {
      i = paramArrayOfInt[j] += 1;
    }
    if (i == 0)
    {
      paramArrayOfInt = new int[paramArrayOfInt.length + 1];
      paramArrayOfInt[0] = 1;
    }
    return paramArrayOfInt;
  }
  
  public BigInteger and(BigInteger paramBigInteger)
  {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (getInt(arrayOfInt.length - i - 1) & paramBigInteger.getInt(arrayOfInt.length - i - 1));
    }
    return valueOf(arrayOfInt);
  }
  
  public BigInteger or(BigInteger paramBigInteger)
  {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (getInt(arrayOfInt.length - i - 1) | paramBigInteger.getInt(arrayOfInt.length - i - 1));
    }
    return valueOf(arrayOfInt);
  }
  
  public BigInteger xor(BigInteger paramBigInteger)
  {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (getInt(arrayOfInt.length - i - 1) ^ paramBigInteger.getInt(arrayOfInt.length - i - 1));
    }
    return valueOf(arrayOfInt);
  }
  
  public BigInteger not()
  {
    int[] arrayOfInt = new int[intLength()];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (getInt(arrayOfInt.length - i - 1) ^ 0xFFFFFFFF);
    }
    return valueOf(arrayOfInt);
  }
  
  public BigInteger andNot(BigInteger paramBigInteger)
  {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (getInt(arrayOfInt.length - i - 1) & (paramBigInteger.getInt(arrayOfInt.length - i - 1) ^ 0xFFFFFFFF));
    }
    return valueOf(arrayOfInt);
  }
  
  public boolean testBit(int paramInt)
  {
    if (paramInt < 0) {
      throw new ArithmeticException("Negative bit address");
    }
    return (getInt(paramInt >>> 5) & 1 << (paramInt & 0x1F)) != 0;
  }
  
  public BigInteger setBit(int paramInt)
  {
    if (paramInt < 0) {
      throw new ArithmeticException("Negative bit address");
    }
    int i = paramInt >>> 5;
    int[] arrayOfInt = new int[Math.max(intLength(), i + 2)];
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[(arrayOfInt.length - j - 1)] = getInt(j);
    }
    arrayOfInt[(arrayOfInt.length - i - 1)] |= 1 << (paramInt & 0x1F);
    return valueOf(arrayOfInt);
  }
  
  public BigInteger clearBit(int paramInt)
  {
    if (paramInt < 0) {
      throw new ArithmeticException("Negative bit address");
    }
    int i = paramInt >>> 5;
    int[] arrayOfInt = new int[Math.max(intLength(), (paramInt + 1 >>> 5) + 1)];
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[(arrayOfInt.length - j - 1)] = getInt(j);
    }
    arrayOfInt[(arrayOfInt.length - i - 1)] &= (1 << (paramInt & 0x1F) ^ 0xFFFFFFFF);
    return valueOf(arrayOfInt);
  }
  
  public BigInteger flipBit(int paramInt)
  {
    if (paramInt < 0) {
      throw new ArithmeticException("Negative bit address");
    }
    int i = paramInt >>> 5;
    int[] arrayOfInt = new int[Math.max(intLength(), i + 2)];
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[(arrayOfInt.length - j - 1)] = getInt(j);
    }
    arrayOfInt[(arrayOfInt.length - i - 1)] ^= 1 << (paramInt & 0x1F);
    return valueOf(arrayOfInt);
  }
  
  public int getLowestSetBit()
  {
    int i = lowestSetBit - 2;
    if (i == -2)
    {
      i = 0;
      if (signum == 0)
      {
        i--;
      }
      else
      {
        int k;
        for (int j = 0; (k = getInt(j)) == 0; j++) {}
        i += (j << 5) + Integer.numberOfTrailingZeros(k);
      }
      lowestSetBit = (i + 2);
    }
    return i;
  }
  
  public int bitLength()
  {
    int i = bitLength - 1;
    if (i == -1)
    {
      int[] arrayOfInt = mag;
      int j = arrayOfInt.length;
      if (j == 0)
      {
        i = 0;
      }
      else
      {
        int k = (j - 1 << 5) + bitLengthForInt(mag[0]);
        if (signum < 0)
        {
          int m = Integer.bitCount(mag[0]) == 1 ? 1 : 0;
          for (int n = 1; (n < j) && (m != 0); n++) {
            m = mag[n] == 0 ? 1 : 0;
          }
          i = m != 0 ? k - 1 : k;
        }
        else
        {
          i = k;
        }
      }
      bitLength = (i + 1);
    }
    return i;
  }
  
  public int bitCount()
  {
    int i = bitCount - 1;
    if (i == -1)
    {
      i = 0;
      for (int j = 0; j < mag.length; j++) {
        i += Integer.bitCount(mag[j]);
      }
      if (signum < 0)
      {
        j = 0;
        for (int k = mag.length - 1; mag[k] == 0; k--) {
          j += 32;
        }
        j += Integer.numberOfTrailingZeros(mag[k]);
        i += j - 1;
      }
      bitCount = (i + 1);
    }
    return i;
  }
  
  public boolean isProbablePrime(int paramInt)
  {
    if (paramInt <= 0) {
      return true;
    }
    BigInteger localBigInteger = abs();
    if (localBigInteger.equals(TWO)) {
      return true;
    }
    if ((!localBigInteger.testBit(0)) || (localBigInteger.equals(ONE))) {
      return false;
    }
    return localBigInteger.primeToCertainty(paramInt, null);
  }
  
  public int compareTo(BigInteger paramBigInteger)
  {
    if (signum == signum)
    {
      switch (signum)
      {
      case 1: 
        return compareMagnitude(paramBigInteger);
      case -1: 
        return paramBigInteger.compareMagnitude(this);
      }
      return 0;
    }
    return signum > signum ? 1 : -1;
  }
  
  final int compareMagnitude(BigInteger paramBigInteger)
  {
    int[] arrayOfInt1 = mag;
    int i = arrayOfInt1.length;
    int[] arrayOfInt2 = mag;
    int j = arrayOfInt2.length;
    if (i < j) {
      return -1;
    }
    if (i > j) {
      return 1;
    }
    for (int k = 0; k < i; k++)
    {
      int m = arrayOfInt1[k];
      int n = arrayOfInt2[k];
      if (m != n) {
        return (m & 0xFFFFFFFF) < (n & 0xFFFFFFFF) ? -1 : 1;
      }
    }
    return 0;
  }
  
  final int compareMagnitude(long paramLong)
  {
    assert (paramLong != Long.MIN_VALUE);
    int[] arrayOfInt = mag;
    int i = arrayOfInt.length;
    if (i > 2) {
      return 1;
    }
    if (paramLong < 0L) {
      paramLong = -paramLong;
    }
    int j = (int)(paramLong >>> 32);
    if (j == 0)
    {
      if (i < 1) {
        return -1;
      }
      if (i > 1) {
        return 1;
      }
      k = arrayOfInt[0];
      m = (int)paramLong;
      if (k != m) {
        return (k & 0xFFFFFFFF) < (m & 0xFFFFFFFF) ? -1 : 1;
      }
      return 0;
    }
    if (i < 2) {
      return -1;
    }
    int k = arrayOfInt[0];
    int m = j;
    if (k != m) {
      return (k & 0xFFFFFFFF) < (m & 0xFFFFFFFF) ? -1 : 1;
    }
    k = arrayOfInt[1];
    m = (int)paramLong;
    if (k != m) {
      return (k & 0xFFFFFFFF) < (m & 0xFFFFFFFF) ? -1 : 1;
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof BigInteger)) {
      return false;
    }
    BigInteger localBigInteger = (BigInteger)paramObject;
    if (signum != signum) {
      return false;
    }
    int[] arrayOfInt1 = mag;
    int i = arrayOfInt1.length;
    int[] arrayOfInt2 = mag;
    if (i != arrayOfInt2.length) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (arrayOfInt2[j] != arrayOfInt1[j]) {
        return false;
      }
    }
    return true;
  }
  
  public BigInteger min(BigInteger paramBigInteger)
  {
    return compareTo(paramBigInteger) < 0 ? this : paramBigInteger;
  }
  
  public BigInteger max(BigInteger paramBigInteger)
  {
    return compareTo(paramBigInteger) > 0 ? this : paramBigInteger;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < mag.length; j++) {
      i = (int)(31 * i + (mag[j] & 0xFFFFFFFF));
    }
    return i * signum;
  }
  
  public String toString(int paramInt)
  {
    if (signum == 0) {
      return "0";
    }
    if ((paramInt < 2) || (paramInt > 36)) {
      paramInt = 10;
    }
    if (mag.length <= 20) {
      return smallToString(paramInt);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (signum < 0)
    {
      toString(negate(), localStringBuilder, paramInt, 0);
      localStringBuilder.insert(0, '-');
    }
    else
    {
      toString(this, localStringBuilder, paramInt, 0);
    }
    return localStringBuilder.toString();
  }
  
  private String smallToString(int paramInt)
  {
    if (signum == 0) {
      return "0";
    }
    int i = (4 * mag.length + 6) / 7;
    String[] arrayOfString = new String[i];
    Object localObject1 = abs();
    int j = 0;
    while (signum != 0)
    {
      localObject2 = longRadix[paramInt];
      MutableBigInteger localMutableBigInteger1 = new MutableBigInteger();
      MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(mag);
      MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(mag);
      MutableBigInteger localMutableBigInteger4 = localMutableBigInteger2.divide(localMutableBigInteger3, localMutableBigInteger1);
      BigInteger localBigInteger1 = localMutableBigInteger1.toBigInteger(signum * signum);
      BigInteger localBigInteger2 = localMutableBigInteger4.toBigInteger(signum * signum);
      arrayOfString[(j++)] = Long.toString(localBigInteger2.longValue(), paramInt);
      localObject1 = localBigInteger1;
    }
    Object localObject2 = new StringBuilder(j * digitsPerLong[paramInt] + 1);
    if (signum < 0) {
      ((StringBuilder)localObject2).append('-');
    }
    ((StringBuilder)localObject2).append(arrayOfString[(j - 1)]);
    for (int k = j - 2; k >= 0; k--)
    {
      int m = digitsPerLong[paramInt] - arrayOfString[k].length();
      if (m != 0) {
        ((StringBuilder)localObject2).append(zeros[m]);
      }
      ((StringBuilder)localObject2).append(arrayOfString[k]);
    }
    return ((StringBuilder)localObject2).toString();
  }
  
  private static void toString(BigInteger paramBigInteger, StringBuilder paramStringBuilder, int paramInt1, int paramInt2)
  {
    if (mag.length <= 20)
    {
      String str = paramBigInteger.smallToString(paramInt1);
      if ((str.length() < paramInt2) && (paramStringBuilder.length() > 0)) {
        for (j = str.length(); j < paramInt2; j++) {
          paramStringBuilder.append('0');
        }
      }
      paramStringBuilder.append(str);
      return;
    }
    int i = paramBigInteger.bitLength();
    int j = (int)Math.round(Math.log(i * LOG_TWO / logCache[paramInt1]) / LOG_TWO - 1.0D);
    BigInteger localBigInteger = getRadixConversionCache(paramInt1, j);
    BigInteger[] arrayOfBigInteger = paramBigInteger.divideAndRemainder(localBigInteger);
    int k = 1 << j;
    toString(arrayOfBigInteger[0], paramStringBuilder, paramInt1, paramInt2 - k);
    toString(arrayOfBigInteger[1], paramStringBuilder, paramInt1, k);
  }
  
  private static BigInteger getRadixConversionCache(int paramInt1, int paramInt2)
  {
    BigInteger[] arrayOfBigInteger = powerCache[paramInt1];
    if (paramInt2 < arrayOfBigInteger.length) {
      return arrayOfBigInteger[paramInt2];
    }
    int i = arrayOfBigInteger.length;
    arrayOfBigInteger = (BigInteger[])Arrays.copyOf(arrayOfBigInteger, paramInt2 + 1);
    for (int j = i; j <= paramInt2; j++) {
      arrayOfBigInteger[j] = arrayOfBigInteger[(j - 1)].pow(2);
    }
    BigInteger[][] arrayOfBigInteger1 = powerCache;
    if (paramInt2 >= arrayOfBigInteger1[paramInt1].length)
    {
      arrayOfBigInteger1 = (BigInteger[][])arrayOfBigInteger1.clone();
      arrayOfBigInteger1[paramInt1] = arrayOfBigInteger;
      powerCache = arrayOfBigInteger1;
    }
    return arrayOfBigInteger[paramInt2];
  }
  
  public String toString()
  {
    return toString(10);
  }
  
  public byte[] toByteArray()
  {
    int i = bitLength() / 8 + 1;
    byte[] arrayOfByte = new byte[i];
    int j = i - 1;
    int k = 4;
    int m = 0;
    int n = 0;
    while (j >= 0)
    {
      if (k == 4)
      {
        m = getInt(n++);
        k = 1;
      }
      else
      {
        m >>>= 8;
        k++;
      }
      arrayOfByte[j] = ((byte)m);
      j--;
    }
    return arrayOfByte;
  }
  
  public int intValue()
  {
    int i = 0;
    i = getInt(0);
    return i;
  }
  
  public long longValue()
  {
    long l = 0L;
    for (int i = 1; i >= 0; i--) {
      l = (l << 32) + (getInt(i) & 0xFFFFFFFF);
    }
    return l;
  }
  
  public float floatValue()
  {
    if (signum == 0) {
      return 0.0F;
    }
    int i = (mag.length - 1 << 5) + bitLengthForInt(mag[0]) - 1;
    if (i < 63) {
      return (float)longValue();
    }
    if (i > 127) {
      return signum > 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
    }
    int j = i - 24;
    int m = j & 0x1F;
    int n = 32 - m;
    int k;
    if (m == 0)
    {
      k = mag[0];
    }
    else
    {
      k = mag[0] >>> m;
      if (k == 0) {
        k = mag[0] << n | mag[1] >>> m;
      }
    }
    int i1 = k >> 1;
    i1 &= 0x7FFFFF;
    int i2 = ((k & 0x1) != 0) && (((i1 & 0x1) != 0) || (abs().getLowestSetBit() < j)) ? 1 : 0;
    int i3 = i2 != 0 ? i1 + 1 : i1;
    int i4 = i + 127 << 23;
    i4 += i3;
    i4 |= signum & 0x80000000;
    return Float.intBitsToFloat(i4);
  }
  
  public double doubleValue()
  {
    if (signum == 0) {
      return 0.0D;
    }
    int i = (mag.length - 1 << 5) + bitLengthForInt(mag[0]) - 1;
    if (i < 63) {
      return longValue();
    }
    if (i > 1023) {
      return signum > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    int j = i - 53;
    int k = j & 0x1F;
    int m = 32 - k;
    int n;
    int i1;
    if (k == 0)
    {
      n = mag[0];
      i1 = mag[1];
    }
    else
    {
      n = mag[0] >>> k;
      i1 = mag[0] << m | mag[1] >>> k;
      if (n == 0)
      {
        n = i1;
        i1 = mag[1] << m | mag[2] >>> k;
      }
    }
    long l1 = (n & 0xFFFFFFFF) << 32 | i1 & 0xFFFFFFFF;
    long l2 = l1 >> 1;
    l2 &= 0xFFFFFFFFFFFFF;
    int i2 = ((l1 & 1L) != 0L) && (((l2 & 1L) != 0L) || (abs().getLowestSetBit() < j)) ? 1 : 0;
    long l3 = i2 != 0 ? l2 + 1L : l2;
    long l4 = i + 1023 << 52;
    l4 += l3;
    l4 |= signum & 0x8000000000000000;
    return Double.longBitsToDouble(l4);
  }
  
  private static int[] stripLeadingZeroInts(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    for (int j = 0; (j < i) && (paramArrayOfInt[j] == 0); j++) {}
    return Arrays.copyOfRange(paramArrayOfInt, j, i);
  }
  
  private static int[] trustedStripLeadingZeroInts(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    for (int j = 0; (j < i) && (paramArrayOfInt[j] == 0); j++) {}
    return j == 0 ? paramArrayOfInt : Arrays.copyOfRange(paramArrayOfInt, j, i);
  }
  
  private static int[] stripLeadingZeroBytes(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    for (int j = 0; (j < i) && (paramArrayOfByte[j] == 0); j++) {}
    int k = i - j + 3 >>> 2;
    int[] arrayOfInt = new int[k];
    int m = i - 1;
    for (int n = k - 1; n >= 0; n--)
    {
      arrayOfInt[n] = (paramArrayOfByte[(m--)] & 0xFF);
      int i1 = m - j + 1;
      int i2 = Math.min(3, i1);
      for (int i3 = 8; i3 <= i2 << 3; i3 += 8) {
        arrayOfInt[n] |= (paramArrayOfByte[(m--)] & 0xFF) << i3;
      }
    }
    return arrayOfInt;
  }
  
  private static int[] makePositive(byte[] paramArrayOfByte)
  {
    int k = paramArrayOfByte.length;
    for (int i = 0; (i < k) && (paramArrayOfByte[i] == -1); i++) {}
    for (int j = i; (j < k) && (paramArrayOfByte[j] == 0); j++) {}
    int m = j == k ? 1 : 0;
    int n = k - i + m + 3 >>> 2;
    int[] arrayOfInt = new int[n];
    int i1 = k - 1;
    for (int i2 = n - 1; i2 >= 0; i2--)
    {
      arrayOfInt[i2] = (paramArrayOfByte[(i1--)] & 0xFF);
      int i3 = Math.min(3, i1 - i + 1);
      if (i3 < 0) {
        i3 = 0;
      }
      for (int i4 = 8; i4 <= 8 * i3; i4 += 8) {
        arrayOfInt[i2] |= (paramArrayOfByte[(i1--)] & 0xFF) << i4;
      }
      i4 = -1 >>> 8 * (3 - i3);
      arrayOfInt[i2] = ((arrayOfInt[i2] ^ 0xFFFFFFFF) & i4);
    }
    for (i2 = arrayOfInt.length - 1; i2 >= 0; i2--)
    {
      arrayOfInt[i2] = ((int)((arrayOfInt[i2] & 0xFFFFFFFF) + 1L));
      if (arrayOfInt[i2] != 0) {
        break;
      }
    }
    return arrayOfInt;
  }
  
  private static int[] makePositive(int[] paramArrayOfInt)
  {
    for (int i = 0; (i < paramArrayOfInt.length) && (paramArrayOfInt[i] == -1); i++) {}
    for (int j = i; (j < paramArrayOfInt.length) && (paramArrayOfInt[j] == 0); j++) {}
    int k = j == paramArrayOfInt.length ? 1 : 0;
    int[] arrayOfInt = new int[paramArrayOfInt.length - i + k];
    for (int m = i; m < paramArrayOfInt.length; m++) {
      arrayOfInt[(m - i + k)] = (paramArrayOfInt[m] ^ 0xFFFFFFFF);
    }
    for (m = arrayOfInt.length - 1;; m--) {
      if (arrayOfInt[m] += 1 != 0) {
        break;
      }
    }
    return arrayOfInt;
  }
  
  private int intLength()
  {
    return (bitLength() >>> 5) + 1;
  }
  
  private int signBit()
  {
    return signum < 0 ? 1 : 0;
  }
  
  private int signInt()
  {
    return signum < 0 ? -1 : 0;
  }
  
  private int getInt(int paramInt)
  {
    if (paramInt < 0) {
      return 0;
    }
    if (paramInt >= mag.length) {
      return signInt();
    }
    int i = mag[(mag.length - paramInt - 1)];
    return paramInt <= firstNonzeroIntNum() ? -i : signum >= 0 ? i : i ^ 0xFFFFFFFF;
  }
  
  private int firstNonzeroIntNum()
  {
    int i = firstNonzeroIntNum - 2;
    if (i == -2)
    {
      i = 0;
      int k = mag.length;
      for (int j = k - 1; (j >= 0) && (mag[j] == 0); j--) {}
      i = k - j - 1;
      firstNonzeroIntNum = (i + 2);
    }
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    int i = localGetField.get("signum", -2);
    byte[] arrayOfByte = (byte[])localGetField.get("magnitude", null);
    if ((i < -1) || (i > 1))
    {
      localObject = "BigInteger: Invalid signum value";
      if (localGetField.defaulted("signum")) {
        localObject = "BigInteger: Signum not present in stream";
      }
      throw new StreamCorruptedException((String)localObject);
    }
    Object localObject = stripLeadingZeroBytes(arrayOfByte);
    if ((localObject.length == 0 ? 1 : 0) != (i == 0 ? 1 : 0))
    {
      String str = "BigInteger: signum-magnitude mismatch";
      if (localGetField.defaulted("magnitude")) {
        str = "BigInteger: Magnitude not present in stream";
      }
      throw new StreamCorruptedException(str);
    }
    UnsafeHolder.putSign(this, i);
    UnsafeHolder.putMag(this, (int[])localObject);
    if (localObject.length >= 67108864) {
      try
      {
        checkRange();
      }
      catch (ArithmeticException localArithmeticException)
      {
        throw new StreamCorruptedException("BigInteger: Out of the supported range");
      }
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("signum", signum);
    localPutField.put("magnitude", magSerializedForm());
    localPutField.put("bitCount", -1);
    localPutField.put("bitLength", -1);
    localPutField.put("lowestSetBit", -2);
    localPutField.put("firstNonzeroByteNum", -2);
    paramObjectOutputStream.writeFields();
  }
  
  private byte[] magSerializedForm()
  {
    int i = mag.length;
    int j = i == 0 ? 0 : (i - 1 << 5) + bitLengthForInt(mag[0]);
    int k = j + 7 >>> 3;
    byte[] arrayOfByte = new byte[k];
    int m = k - 1;
    int n = 4;
    int i1 = i - 1;
    int i2 = 0;
    while (m >= 0)
    {
      if (n == 4)
      {
        i2 = mag[(i1--)];
        n = 1;
      }
      else
      {
        i2 >>>= 8;
        n++;
      }
      arrayOfByte[m] = ((byte)i2);
      m--;
    }
    return arrayOfByte;
  }
  
  public long longValueExact()
  {
    if ((mag.length <= 2) && (bitLength() <= 63)) {
      return longValue();
    }
    throw new ArithmeticException("BigInteger out of long range");
  }
  
  public int intValueExact()
  {
    if ((mag.length <= 1) && (bitLength() <= 31)) {
      return intValue();
    }
    throw new ArithmeticException("BigInteger out of int range");
  }
  
  public short shortValueExact()
  {
    if ((mag.length <= 1) && (bitLength() <= 31))
    {
      int i = intValue();
      if ((i >= 32768) && (i <= 32767)) {
        return shortValue();
      }
    }
    throw new ArithmeticException("BigInteger out of short range");
  }
  
  public byte byteValueExact()
  {
    if ((mag.length <= 1) && (bitLength() <= 31))
    {
      int i = intValue();
      if ((i >= -128) && (i <= 127)) {
        return byteValue();
      }
    }
    throw new ArithmeticException("BigInteger out of byte range");
  }
  
  static
  {
    bitsPerDigit = new long[] { 0L, 0L, 1024L, 1624L, 2048L, 2378L, 2648L, 2875L, 3072L, 3247L, 3402L, 3543L, 3672L, 3790L, 3899L, 4001L, 4096L, 4186L, 4271L, 4350L, 4426L, 4498L, 4567L, 4633L, 4696L, 4756L, 4814L, 4870L, 4923L, 4975L, 5025L, 5074L, 5120L, 5166L, 5210L, 5253L, 5295L };
    SMALL_PRIME_PRODUCT = valueOf(152125131763605L);
    posConst = new BigInteger[17];
    negConst = new BigInteger[17];
    LOG_TWO = Math.log(2.0D);
    for (int i = 1; i <= 16; i++)
    {
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = i;
      posConst[i] = new BigInteger(arrayOfInt, 1);
      negConst[i] = new BigInteger(arrayOfInt, -1);
    }
    powerCache = new BigInteger[37][];
    logCache = new double[37];
    for (i = 2; i <= 36; i++)
    {
      powerCache[i] = { valueOf(i) };
      logCache[i] = Math.log(i);
    }
    ZERO = new BigInteger(new int[0], 0);
    ONE = valueOf(1L);
    TWO = valueOf(2L);
    NEGATIVE_ONE = valueOf(-1L);
    TEN = valueOf(10L);
    bnExpModThreshTable = new int[] { 7, 25, 81, 241, 673, 1793, Integer.MAX_VALUE };
    zeros = new String[64];
    zeros[63] = "000000000000000000000000000000000000000000000000000000000000000";
    for (i = 0; i < 63; i++) {
      zeros[i] = zeros[63].substring(0, i);
    }
  }
  
  private static class UnsafeHolder
  {
    private static final Unsafe unsafe;
    private static final long signumOffset;
    private static final long magOffset;
    
    private UnsafeHolder() {}
    
    static void putSign(BigInteger paramBigInteger, int paramInt)
    {
      unsafe.putIntVolatile(paramBigInteger, signumOffset, paramInt);
    }
    
    static void putMag(BigInteger paramBigInteger, int[] paramArrayOfInt)
    {
      unsafe.putObjectVolatile(paramBigInteger, magOffset, paramArrayOfInt);
    }
    
    static
    {
      try
      {
        unsafe = Unsafe.getUnsafe();
        signumOffset = unsafe.objectFieldOffset(BigInteger.class.getDeclaredField("signum"));
        magOffset = unsafe.objectFieldOffset(BigInteger.class.getDeclaredField("mag"));
      }
      catch (Exception localException)
      {
        throw new ExceptionInInitializerError(localException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\math\BigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.math;

import java.util.Random;

class BitSieve
{
  private long[] bits;
  private int length;
  private static BitSieve smallSieve = new BitSieve();
  
  private BitSieve()
  {
    length = 9600;
    bits = new long[unitIndex(length - 1) + 1];
    set(0);
    int i = 1;
    int j = 3;
    do
    {
      sieveSingle(length, i + j, j);
      i = sieveSearch(length, i + 1);
      j = 2 * i + 1;
    } while ((i > 0) && (j < length));
  }
  
  BitSieve(BigInteger paramBigInteger, int paramInt)
  {
    bits = new long[unitIndex(paramInt - 1) + 1];
    length = paramInt;
    int i = 0;
    int j = smallSieve.sieveSearch(smallSievelength, i);
    int k = j * 2 + 1;
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(paramBigInteger);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
    do
    {
      i = localMutableBigInteger1.divideOneWord(k, localMutableBigInteger2);
      i = k - i;
      if (i % 2 == 0) {
        i += k;
      }
      sieveSingle(paramInt, (i - 1) / 2, k);
      j = smallSieve.sieveSearch(smallSievelength, j + 1);
      k = j * 2 + 1;
    } while (j > 0);
  }
  
  private static int unitIndex(int paramInt)
  {
    return paramInt >>> 6;
  }
  
  private static long bit(int paramInt)
  {
    return 1L << (paramInt & 0x3F);
  }
  
  private boolean get(int paramInt)
  {
    int i = unitIndex(paramInt);
    return (bits[i] & bit(paramInt)) != 0L;
  }
  
  private void set(int paramInt)
  {
    int i = unitIndex(paramInt);
    bits[i] |= bit(paramInt);
  }
  
  private int sieveSearch(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= paramInt1) {
      return -1;
    }
    int i = paramInt2;
    do
    {
      if (!get(i)) {
        return i;
      }
      i++;
    } while (i < paramInt1 - 1);
    return -1;
  }
  
  private void sieveSingle(int paramInt1, int paramInt2, int paramInt3)
  {
    while (paramInt2 < paramInt1)
    {
      set(paramInt2);
      paramInt2 += paramInt3;
    }
  }
  
  BigInteger retrieve(BigInteger paramBigInteger, int paramInt, Random paramRandom)
  {
    int i = 1;
    for (int j = 0; j < bits.length; j++)
    {
      long l = bits[j] ^ 0xFFFFFFFFFFFFFFFF;
      for (int k = 0; k < 64; k++)
      {
        if ((l & 1L) == 1L)
        {
          BigInteger localBigInteger = paramBigInteger.add(BigInteger.valueOf(i));
          if (localBigInteger.primeToCertainty(paramInt, paramRandom)) {
            return localBigInteger;
          }
        }
        l >>>= 1;
        i += 2;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\math\BitSieve.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
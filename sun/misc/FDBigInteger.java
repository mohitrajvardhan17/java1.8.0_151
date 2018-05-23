package sun.misc;

import java.math.BigInteger;
import java.util.Arrays;

public class FDBigInteger
{
  static final int[] SMALL_5_POW;
  static final long[] LONG_5_POW;
  private static final int MAX_FIVE_POW = 340;
  private static final FDBigInteger[] POW_5_CACHE;
  public static final FDBigInteger ZERO;
  private static final long LONG_MASK = 4294967295L;
  private int[] data;
  private int offset;
  private int nWords;
  private boolean isImmutable = false;
  
  private FDBigInteger(int[] paramArrayOfInt, int paramInt)
  {
    data = paramArrayOfInt;
    offset = paramInt;
    nWords = paramArrayOfInt.length;
    trimLeadingZeros();
  }
  
  public FDBigInteger(long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = Math.max((paramInt2 + 8) / 9, 2);
    data = new int[i];
    data[0] = ((int)paramLong);
    data[1] = ((int)(paramLong >>> 32));
    offset = 0;
    nWords = 2;
    int j = paramInt1;
    int k = paramInt2 - 5;
    while (j < k)
    {
      n = j + 5;
      for (m = paramArrayOfChar[(j++)] - '0'; j < n; m = 10 * m + paramArrayOfChar[(j++)] - 48) {}
      multAddMe(100000, m);
    }
    int n = 1;
    int m = 0;
    while (j < paramInt2)
    {
      m = 10 * m + paramArrayOfChar[(j++)] - 48;
      n *= 10;
    }
    if (n != 1) {
      multAddMe(n, m);
    }
    trimLeadingZeros();
  }
  
  public static FDBigInteger valueOfPow52(int paramInt1, int paramInt2)
  {
    if (paramInt1 != 0)
    {
      if (paramInt2 == 0) {
        return big5pow(paramInt1);
      }
      if (paramInt1 < SMALL_5_POW.length)
      {
        int i = SMALL_5_POW[paramInt1];
        int j = paramInt2 >> 5;
        int k = paramInt2 & 0x1F;
        if (k == 0) {
          return new FDBigInteger(new int[] { i }, j);
        }
        return new FDBigInteger(new int[] { i << k, i >>> 32 - k }, j);
      }
      return big5pow(paramInt1).leftShift(paramInt2);
    }
    return valueOfPow2(paramInt2);
  }
  
  public static FDBigInteger valueOfMulPow52(long paramLong, int paramInt1, int paramInt2)
  {
    assert (paramInt1 >= 0) : paramInt1;
    assert (paramInt2 >= 0) : paramInt2;
    int i = (int)paramLong;
    int j = (int)(paramLong >>> 32);
    int k = paramInt2 >> 5;
    int m = paramInt2 & 0x1F;
    if (paramInt1 != 0)
    {
      if (paramInt1 < SMALL_5_POW.length)
      {
        long l1 = SMALL_5_POW[paramInt1] & 0xFFFFFFFF;
        long l2 = (i & 0xFFFFFFFF) * l1;
        i = (int)l2;
        l2 >>>= 32;
        l2 = (j & 0xFFFFFFFF) * l1 + l2;
        j = (int)l2;
        int n = (int)(l2 >>> 32);
        if (m == 0) {
          return new FDBigInteger(new int[] { i, j, n }, k);
        }
        return new FDBigInteger(new int[] { i << m, j << m | i >>> 32 - m, n << m | j >>> 32 - m, n >>> 32 - m }, k);
      }
      FDBigInteger localFDBigInteger = big5pow(paramInt1);
      int[] arrayOfInt;
      if (j == 0)
      {
        arrayOfInt = new int[nWords + 1 + (paramInt2 != 0 ? 1 : 0)];
        mult(data, nWords, i, arrayOfInt);
      }
      else
      {
        arrayOfInt = new int[nWords + 2 + (paramInt2 != 0 ? 1 : 0)];
        mult(data, nWords, i, j, arrayOfInt);
      }
      return new FDBigInteger(arrayOfInt, offset).leftShift(paramInt2);
    }
    if (paramInt2 != 0)
    {
      if (m == 0) {
        return new FDBigInteger(new int[] { i, j }, k);
      }
      return new FDBigInteger(new int[] { i << m, j << m | i >>> 32 - m, j >>> 32 - m }, k);
    }
    return new FDBigInteger(new int[] { i, j }, 0);
  }
  
  private static FDBigInteger valueOfPow2(int paramInt)
  {
    int i = paramInt >> 5;
    int j = paramInt & 0x1F;
    return new FDBigInteger(new int[] { 1 << j }, i);
  }
  
  private void trimLeadingZeros()
  {
    int i = nWords;
    if ((i > 0) && (data[(--i)] == 0))
    {
      while ((i > 0) && (data[(i - 1)] == 0)) {
        i--;
      }
      nWords = i;
      if (i == 0) {
        offset = 0;
      }
    }
  }
  
  public int getNormalizationBias()
  {
    if (nWords == 0) {
      throw new IllegalArgumentException("Zero value cannot be normalized");
    }
    int i = Integer.numberOfLeadingZeros(data[(nWords - 1)]);
    return i < 4 ? 28 + i : i - 4;
  }
  
  private static void leftShift(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4)
  {
    while (paramInt1 > 0)
    {
      i = paramInt4 << paramInt2;
      paramInt4 = paramArrayOfInt1[(paramInt1 - 1)];
      i |= paramInt4 >>> paramInt3;
      paramArrayOfInt2[paramInt1] = i;
      paramInt1--;
    }
    int i = paramInt4 << paramInt2;
    paramArrayOfInt2[0] = i;
  }
  
  public FDBigInteger leftShift(int paramInt)
  {
    if ((paramInt == 0) || (nWords == 0)) {
      return this;
    }
    int i = paramInt >> 5;
    int j = paramInt & 0x1F;
    int k;
    int m;
    int n;
    int i1;
    int[] arrayOfInt1;
    if (isImmutable)
    {
      if (j == 0) {
        return new FDBigInteger(Arrays.copyOf(data, nWords), offset + i);
      }
      k = 32 - j;
      m = nWords - 1;
      n = data[m];
      i1 = n >>> k;
      if (i1 != 0)
      {
        arrayOfInt1 = new int[nWords + 1];
        arrayOfInt1[nWords] = i1;
      }
      else
      {
        arrayOfInt1 = new int[nWords];
      }
      leftShift(data, m, arrayOfInt1, j, k, n);
      return new FDBigInteger(arrayOfInt1, offset + i);
    }
    if (j != 0)
    {
      k = 32 - j;
      if (data[0] << j == 0)
      {
        m = 0;
        n = data[m];
        while (m < nWords - 1)
        {
          i1 = n >>> k;
          n = data[(m + 1)];
          i1 |= n << j;
          data[m] = i1;
          m++;
        }
        i1 = n >>> k;
        data[m] = i1;
        if (i1 == 0) {
          nWords -= 1;
        }
        offset += 1;
      }
      else
      {
        m = nWords - 1;
        n = data[m];
        i1 = n >>> k;
        arrayOfInt1 = data;
        int[] arrayOfInt2 = data;
        if (i1 != 0)
        {
          if (nWords == data.length) {
            data = (arrayOfInt1 = new int[nWords + 1]);
          }
          arrayOfInt1[(nWords++)] = i1;
        }
        leftShift(arrayOfInt2, m, arrayOfInt1, j, k, n);
      }
    }
    offset += i;
    return this;
  }
  
  private int size()
  {
    return nWords + offset;
  }
  
  public int quoRemIteration(FDBigInteger paramFDBigInteger)
    throws IllegalArgumentException
  {
    assert (!isImmutable) : "cannot modify immutable value";
    int i = size();
    int j = paramFDBigInteger.size();
    if (i < j)
    {
      int k = multAndCarryBy10(data, nWords, data);
      if (k != 0) {
        data[(nWords++)] = k;
      } else {
        trimLeadingZeros();
      }
      return 0;
    }
    if (i > j) {
      throw new IllegalArgumentException("disparate values");
    }
    long l1 = (data[(nWords - 1)] & 0xFFFFFFFF) / (data[(nWords - 1)] & 0xFFFFFFFF);
    long l2 = multDiffMe(l1, paramFDBigInteger);
    if (l2 != 0L)
    {
      long l3 = 0L;
      int n = offset - offset;
      int[] arrayOfInt1 = data;
      int[] arrayOfInt2 = data;
      while (l3 == 0L)
      {
        int i1 = 0;
        for (int i2 = n; i2 < nWords; i2++)
        {
          l3 += (arrayOfInt2[i2] & 0xFFFFFFFF) + (arrayOfInt1[i1] & 0xFFFFFFFF);
          arrayOfInt2[i2] = ((int)l3);
          l3 >>>= 32;
          i1++;
        }
        assert ((l3 == 0L) || (l3 == 1L)) : l3;
        l1 -= 1L;
      }
    }
    int m = multAndCarryBy10(data, nWords, data);
    assert (m == 0) : m;
    trimLeadingZeros();
    return (int)l1;
  }
  
  public FDBigInteger multBy10()
  {
    if (nWords == 0) {
      return this;
    }
    if (isImmutable)
    {
      int[] arrayOfInt = new int[nWords + 1];
      arrayOfInt[nWords] = multAndCarryBy10(data, nWords, arrayOfInt);
      return new FDBigInteger(arrayOfInt, offset);
    }
    int i = multAndCarryBy10(data, nWords, data);
    if (i != 0)
    {
      if (nWords == data.length) {
        if (data[0] == 0)
        {
          System.arraycopy(data, 1, data, 0, --nWords);
          offset += 1;
        }
        else
        {
          data = Arrays.copyOf(data, data.length + 1);
        }
      }
      data[(nWords++)] = i;
    }
    else
    {
      trimLeadingZeros();
    }
    return this;
  }
  
  public FDBigInteger multByPow52(int paramInt1, int paramInt2)
  {
    if (nWords == 0) {
      return this;
    }
    FDBigInteger localFDBigInteger1 = this;
    if (paramInt1 != 0)
    {
      int i = paramInt2 != 0 ? 1 : 0;
      int[] arrayOfInt;
      if (paramInt1 < SMALL_5_POW.length)
      {
        arrayOfInt = new int[nWords + 1 + i];
        mult(data, nWords, SMALL_5_POW[paramInt1], arrayOfInt);
        localFDBigInteger1 = new FDBigInteger(arrayOfInt, offset);
      }
      else
      {
        FDBigInteger localFDBigInteger2 = big5pow(paramInt1);
        arrayOfInt = new int[nWords + localFDBigInteger2.size() + i];
        mult(data, nWords, data, nWords, arrayOfInt);
        localFDBigInteger1 = new FDBigInteger(arrayOfInt, offset + offset);
      }
    }
    return localFDBigInteger1.leftShift(paramInt2);
  }
  
  private static void mult(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int[] paramArrayOfInt3)
  {
    for (int i = 0; i < paramInt1; i++)
    {
      long l1 = paramArrayOfInt1[i] & 0xFFFFFFFF;
      long l2 = 0L;
      for (int j = 0; j < paramInt2; j++)
      {
        l2 += (paramArrayOfInt3[(i + j)] & 0xFFFFFFFF) + l1 * (paramArrayOfInt2[j] & 0xFFFFFFFF);
        paramArrayOfInt3[(i + j)] = ((int)l2);
        l2 >>>= 32;
      }
      paramArrayOfInt3[(i + paramInt2)] = ((int)l2);
    }
  }
  
  public FDBigInteger leftInplaceSub(FDBigInteger paramFDBigInteger)
  {
    assert (size() >= paramFDBigInteger.size()) : "result should be positive";
    FDBigInteger localFDBigInteger;
    if (isImmutable) {
      localFDBigInteger = new FDBigInteger((int[])data.clone(), offset);
    } else {
      localFDBigInteger = this;
    }
    int i = offset - offset;
    int[] arrayOfInt1 = data;
    Object localObject = data;
    int j = nWords;
    int k = nWords;
    if (i < 0)
    {
      int m = k - i;
      if (m < localObject.length)
      {
        System.arraycopy(localObject, 0, localObject, -i, k);
        Arrays.fill((int[])localObject, 0, -i, 0);
      }
      else
      {
        int[] arrayOfInt2 = new int[m];
        System.arraycopy(localObject, 0, arrayOfInt2, -i, k);
        data = (localObject = arrayOfInt2);
      }
      offset = offset;
      nWords = (k = m);
      i = 0;
    }
    long l1 = 0L;
    int n = i;
    int i1 = 0;
    while ((i1 < j) && (n < k))
    {
      long l3 = (localObject[n] & 0xFFFFFFFF) - (arrayOfInt1[i1] & 0xFFFFFFFF) + l1;
      localObject[n] = ((int)l3);
      l1 = l3 >> 32;
      i1++;
      n++;
    }
    while ((l1 != 0L) && (n < k))
    {
      long l2 = (localObject[n] & 0xFFFFFFFF) + l1;
      localObject[n] = ((int)l2);
      l1 = l2 >> 32;
      n++;
    }
    assert (l1 == 0L) : l1;
    localFDBigInteger.trimLeadingZeros();
    return localFDBigInteger;
  }
  
  public FDBigInteger rightInplaceSub(FDBigInteger paramFDBigInteger)
  {
    assert (size() >= paramFDBigInteger.size()) : "result should be positive";
    FDBigInteger localFDBigInteger = this;
    if (isImmutable) {
      paramFDBigInteger = new FDBigInteger((int[])data.clone(), offset);
    }
    int i = offset - offset;
    Object localObject = data;
    int[] arrayOfInt1 = data;
    int j = nWords;
    int k = nWords;
    if (i < 0)
    {
      m = k;
      if (m < localObject.length)
      {
        System.arraycopy(localObject, 0, localObject, -i, j);
        Arrays.fill((int[])localObject, 0, -i, 0);
      }
      else
      {
        int[] arrayOfInt2 = new int[m];
        System.arraycopy(localObject, 0, arrayOfInt2, -i, j);
        data = (localObject = arrayOfInt2);
      }
      offset = offset;
      j -= i;
      i = 0;
    }
    else
    {
      m = k + i;
      if (m >= localObject.length) {
        data = (localObject = Arrays.copyOf((int[])localObject, m));
      }
    }
    int m = 0;
    long l1 = 0L;
    while (m < i)
    {
      long l2 = 0L - (localObject[m] & 0xFFFFFFFF) + l1;
      localObject[m] = ((int)l2);
      l1 = l2 >> 32;
      m++;
    }
    for (int n = 0; n < k; n++)
    {
      long l3 = (arrayOfInt1[n] & 0xFFFFFFFF) - (localObject[m] & 0xFFFFFFFF) + l1;
      localObject[m] = ((int)l3);
      l1 = l3 >> 32;
      m++;
    }
    assert (l1 == 0L) : l1;
    nWords = m;
    paramFDBigInteger.trimLeadingZeros();
    return paramFDBigInteger;
  }
  
  private static int checkZeroTail(int[] paramArrayOfInt, int paramInt)
  {
    while (paramInt > 0) {
      if (paramArrayOfInt[(--paramInt)] != 0) {
        return 1;
      }
    }
    return 0;
  }
  
  public int cmp(FDBigInteger paramFDBigInteger)
  {
    int i = nWords + offset;
    int j = nWords + offset;
    if (i > j) {
      return 1;
    }
    if (i < j) {
      return -1;
    }
    int k = nWords;
    int m = nWords;
    while ((k > 0) && (m > 0))
    {
      int n = data[(--k)];
      int i1 = data[(--m)];
      if (n != i1) {
        return (n & 0xFFFFFFFF) < (i1 & 0xFFFFFFFF) ? -1 : 1;
      }
    }
    if (k > 0) {
      return checkZeroTail(data, k);
    }
    if (m > 0) {
      return -checkZeroTail(data, m);
    }
    return 0;
  }
  
  public int cmpPow52(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0)
    {
      int i = paramInt2 >> 5;
      int j = paramInt2 & 0x1F;
      int k = nWords + offset;
      if (k > i + 1) {
        return 1;
      }
      if (k < i + 1) {
        return -1;
      }
      int m = data[(nWords - 1)];
      int n = 1 << j;
      if (m != n) {
        return (m & 0xFFFFFFFF) < (n & 0xFFFFFFFF) ? -1 : 1;
      }
      return checkZeroTail(data, nWords - 1);
    }
    return cmp(big5pow(paramInt1).leftShift(paramInt2));
  }
  
  public int addAndCmp(FDBigInteger paramFDBigInteger1, FDBigInteger paramFDBigInteger2)
  {
    int i = paramFDBigInteger1.size();
    int j = paramFDBigInteger2.size();
    FDBigInteger localFDBigInteger1;
    FDBigInteger localFDBigInteger2;
    int k;
    int m;
    if (i >= j)
    {
      localFDBigInteger1 = paramFDBigInteger1;
      localFDBigInteger2 = paramFDBigInteger2;
      k = i;
      m = j;
    }
    else
    {
      localFDBigInteger1 = paramFDBigInteger2;
      localFDBigInteger2 = paramFDBigInteger1;
      k = j;
      m = i;
    }
    int n = size();
    if (k == 0) {
      return n == 0 ? 0 : 1;
    }
    if (m == 0) {
      return cmp(localFDBigInteger1);
    }
    if (k > n) {
      return -1;
    }
    if (k + 1 < n) {
      return 1;
    }
    long l1 = data[(nWords - 1)] & 0xFFFFFFFF;
    if (m == k) {
      l1 += (data[(nWords - 1)] & 0xFFFFFFFF);
    }
    long l2;
    if (l1 >>> 32 == 0L)
    {
      if (l1 + 1L >>> 32 == 0L)
      {
        if (k < n) {
          return 1;
        }
        l2 = data[(nWords - 1)] & 0xFFFFFFFF;
        if (l2 < l1) {
          return -1;
        }
        if (l2 > l1 + 1L) {
          return 1;
        }
      }
    }
    else
    {
      if (k + 1 > n) {
        return -1;
      }
      l1 >>>= 32;
      l2 = data[(nWords - 1)] & 0xFFFFFFFF;
      if (l2 < l1) {
        return -1;
      }
      if (l2 > l1 + 1L) {
        return 1;
      }
    }
    return cmp(localFDBigInteger1.add(localFDBigInteger2));
  }
  
  public void makeImmutable()
  {
    isImmutable = true;
  }
  
  private FDBigInteger mult(int paramInt)
  {
    if (nWords == 0) {
      return this;
    }
    int[] arrayOfInt = new int[nWords + 1];
    mult(data, nWords, paramInt, arrayOfInt);
    return new FDBigInteger(arrayOfInt, offset);
  }
  
  private FDBigInteger mult(FDBigInteger paramFDBigInteger)
  {
    if (nWords == 0) {
      return this;
    }
    if (size() == 1) {
      return paramFDBigInteger.mult(data[0]);
    }
    if (nWords == 0) {
      return paramFDBigInteger;
    }
    if (paramFDBigInteger.size() == 1) {
      return mult(data[0]);
    }
    int[] arrayOfInt = new int[nWords + nWords];
    mult(data, nWords, data, nWords, arrayOfInt);
    return new FDBigInteger(arrayOfInt, offset + offset);
  }
  
  private FDBigInteger add(FDBigInteger paramFDBigInteger)
  {
    int k = size();
    int m = paramFDBigInteger.size();
    FDBigInteger localFDBigInteger1;
    int i;
    FDBigInteger localFDBigInteger2;
    int j;
    if (k >= m)
    {
      localFDBigInteger1 = this;
      i = k;
      localFDBigInteger2 = paramFDBigInteger;
      j = m;
    }
    else
    {
      localFDBigInteger1 = paramFDBigInteger;
      i = m;
      localFDBigInteger2 = this;
      j = k;
    }
    int[] arrayOfInt = new int[i + 1];
    int n = 0;
    long l = 0L;
    while (n < j)
    {
      l += (n < offset ? 0L : data[(n - offset)] & 0xFFFFFFFF) + (n < offset ? 0L : data[(n - offset)] & 0xFFFFFFFF);
      arrayOfInt[n] = ((int)l);
      l >>= 32;
      n++;
    }
    while (n < i)
    {
      l += (n < offset ? 0L : data[(n - offset)] & 0xFFFFFFFF);
      arrayOfInt[n] = ((int)l);
      l >>= 32;
      n++;
    }
    arrayOfInt[i] = ((int)l);
    return new FDBigInteger(arrayOfInt, 0);
  }
  
  private void multAddMe(int paramInt1, int paramInt2)
  {
    long l1 = paramInt1 & 0xFFFFFFFF;
    long l2 = l1 * (data[0] & 0xFFFFFFFF) + (paramInt2 & 0xFFFFFFFF);
    data[0] = ((int)l2);
    l2 >>>= 32;
    for (int i = 1; i < nWords; i++)
    {
      l2 += l1 * (data[i] & 0xFFFFFFFF);
      data[i] = ((int)l2);
      l2 >>>= 32;
    }
    if (l2 != 0L) {
      data[(nWords++)] = ((int)l2);
    }
  }
  
  private long multDiffMe(long paramLong, FDBigInteger paramFDBigInteger)
  {
    long l = 0L;
    if (paramLong != 0L)
    {
      int i = offset - offset;
      int[] arrayOfInt1;
      int k;
      if (i >= 0)
      {
        arrayOfInt1 = data;
        int[] arrayOfInt2 = data;
        k = 0;
        for (int m = i; k < nWords; m++)
        {
          l += (arrayOfInt2[m] & 0xFFFFFFFF) - paramLong * (arrayOfInt1[k] & 0xFFFFFFFF);
          arrayOfInt2[m] = ((int)l);
          l >>= 32;
          k++;
        }
      }
      else
      {
        i = -i;
        arrayOfInt1 = new int[nWords + i];
        int j = 0;
        k = 0;
        int[] arrayOfInt3 = data;
        while ((k < i) && (j < nWords))
        {
          l -= paramLong * (arrayOfInt3[j] & 0xFFFFFFFF);
          arrayOfInt1[k] = ((int)l);
          l >>= 32;
          j++;
          k++;
        }
        int n = 0;
        int[] arrayOfInt4 = data;
        while (j < nWords)
        {
          l += (arrayOfInt4[n] & 0xFFFFFFFF) - paramLong * (arrayOfInt3[j] & 0xFFFFFFFF);
          arrayOfInt1[k] = ((int)l);
          l >>= 32;
          j++;
          n++;
          k++;
        }
        nWords += i;
        offset -= i;
        data = arrayOfInt1;
      }
    }
    return l;
  }
  
  private static int multAndCarryBy10(int[] paramArrayOfInt1, int paramInt, int[] paramArrayOfInt2)
  {
    long l1 = 0L;
    for (int i = 0; i < paramInt; i++)
    {
      long l2 = (paramArrayOfInt1[i] & 0xFFFFFFFF) * 10L + l1;
      paramArrayOfInt2[i] = ((int)l2);
      l1 = l2 >>> 32;
    }
    return (int)l1;
  }
  
  private static void mult(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2)
  {
    long l1 = paramInt2 & 0xFFFFFFFF;
    long l2 = 0L;
    for (int i = 0; i < paramInt1; i++)
    {
      long l3 = (paramArrayOfInt1[i] & 0xFFFFFFFF) * l1 + l2;
      paramArrayOfInt2[i] = ((int)l3);
      l2 = l3 >>> 32;
    }
    paramArrayOfInt2[paramInt1] = ((int)l2);
  }
  
  private static void mult(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt2)
  {
    long l1 = paramInt2 & 0xFFFFFFFF;
    long l2 = 0L;
    long l3;
    for (int i = 0; i < paramInt1; i++)
    {
      l3 = l1 * (paramArrayOfInt1[i] & 0xFFFFFFFF) + l2;
      paramArrayOfInt2[i] = ((int)l3);
      l2 = l3 >>> 32;
    }
    paramArrayOfInt2[paramInt1] = ((int)l2);
    l1 = paramInt3 & 0xFFFFFFFF;
    l2 = 0L;
    for (i = 0; i < paramInt1; i++)
    {
      l3 = (paramArrayOfInt2[(i + 1)] & 0xFFFFFFFF) + l1 * (paramArrayOfInt1[i] & 0xFFFFFFFF) + l2;
      paramArrayOfInt2[(i + 1)] = ((int)l3);
      l2 = l3 >>> 32;
    }
    paramArrayOfInt2[(paramInt1 + 1)] = ((int)l2);
  }
  
  private static FDBigInteger big5pow(int paramInt)
  {
    assert (paramInt >= 0) : paramInt;
    if (paramInt < 340) {
      return POW_5_CACHE[paramInt];
    }
    return big5powRec(paramInt);
  }
  
  private static FDBigInteger big5powRec(int paramInt)
  {
    if (paramInt < 340) {
      return POW_5_CACHE[paramInt];
    }
    int i = paramInt >> 1;
    int j = paramInt - i;
    FDBigInteger localFDBigInteger = big5powRec(i);
    if (j < SMALL_5_POW.length) {
      return localFDBigInteger.mult(SMALL_5_POW[j]);
    }
    return localFDBigInteger.mult(big5powRec(j));
  }
  
  public String toHexString()
  {
    if (nWords == 0) {
      return "0";
    }
    StringBuilder localStringBuilder = new StringBuilder((nWords + offset) * 8);
    for (int i = nWords - 1; i >= 0; i--)
    {
      String str = Integer.toHexString(data[i]);
      for (int j = str.length(); j < 8; j++) {
        localStringBuilder.append('0');
      }
      localStringBuilder.append(str);
    }
    for (i = offset; i > 0; i--) {
      localStringBuilder.append("00000000");
    }
    return localStringBuilder.toString();
  }
  
  public BigInteger toBigInteger()
  {
    byte[] arrayOfByte = new byte[nWords * 4 + 1];
    for (int i = 0; i < nWords; i++)
    {
      int j = data[i];
      arrayOfByte[(arrayOfByte.length - 4 * i - 1)] = ((byte)j);
      arrayOfByte[(arrayOfByte.length - 4 * i - 2)] = ((byte)(j >> 8));
      arrayOfByte[(arrayOfByte.length - 4 * i - 3)] = ((byte)(j >> 16));
      arrayOfByte[(arrayOfByte.length - 4 * i - 4)] = ((byte)(j >> 24));
    }
    return new BigInteger(arrayOfByte).shiftLeft(offset * 32);
  }
  
  public String toString()
  {
    return toBigInteger().toString();
  }
  
  static
  {
    SMALL_5_POW = new int[] { 1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125 };
    LONG_5_POW = new long[] { 1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L };
    POW_5_CACHE = new FDBigInteger['Ŕ'];
    for (int i = 0; i < SMALL_5_POW.length; i++)
    {
      localFDBigInteger = new FDBigInteger(new int[] { SMALL_5_POW[i] }, 0);
      localFDBigInteger.makeImmutable();
      POW_5_CACHE[i] = localFDBigInteger;
    }
    FDBigInteger localFDBigInteger = POW_5_CACHE[(i - 1)];
    while (i < 340)
    {
      POW_5_CACHE[i] = (localFDBigInteger = localFDBigInteger.mult(5));
      localFDBigInteger.makeImmutable();
      i++;
    }
    ZERO = new FDBigInteger(new int[0], 0);
    ZERO.makeImmutable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\FDBigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
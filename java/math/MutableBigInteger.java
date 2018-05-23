package java.math;

import java.util.Arrays;

class MutableBigInteger
{
  int[] value;
  int intLen;
  int offset = 0;
  static final MutableBigInteger ONE = new MutableBigInteger(1);
  static final int KNUTH_POW2_THRESH_LEN = 6;
  static final int KNUTH_POW2_THRESH_ZEROS = 3;
  
  MutableBigInteger()
  {
    value = new int[1];
    intLen = 0;
  }
  
  MutableBigInteger(int paramInt)
  {
    value = new int[1];
    intLen = 1;
    value[0] = paramInt;
  }
  
  MutableBigInteger(int[] paramArrayOfInt)
  {
    value = paramArrayOfInt;
    intLen = paramArrayOfInt.length;
  }
  
  MutableBigInteger(BigInteger paramBigInteger)
  {
    intLen = mag.length;
    value = Arrays.copyOf(mag, intLen);
  }
  
  MutableBigInteger(MutableBigInteger paramMutableBigInteger)
  {
    intLen = intLen;
    value = Arrays.copyOfRange(value, offset, offset + intLen);
  }
  
  private void ones(int paramInt)
  {
    if (paramInt > value.length) {
      value = new int[paramInt];
    }
    Arrays.fill(value, -1);
    offset = 0;
    intLen = paramInt;
  }
  
  private int[] getMagnitudeArray()
  {
    if ((offset > 0) || (value.length != intLen)) {
      return Arrays.copyOfRange(value, offset, offset + intLen);
    }
    return value;
  }
  
  private long toLong()
  {
    assert (intLen <= 2) : "this MutableBigInteger exceeds the range of long";
    if (intLen == 0) {
      return 0L;
    }
    long l = value[offset] & 0xFFFFFFFF;
    return intLen == 2 ? l << 32 | value[(offset + 1)] & 0xFFFFFFFF : l;
  }
  
  BigInteger toBigInteger(int paramInt)
  {
    if ((intLen == 0) || (paramInt == 0)) {
      return BigInteger.ZERO;
    }
    return new BigInteger(getMagnitudeArray(), paramInt);
  }
  
  BigInteger toBigInteger()
  {
    normalize();
    return toBigInteger(isZero() ? 0 : 1);
  }
  
  BigDecimal toBigDecimal(int paramInt1, int paramInt2)
  {
    if ((intLen == 0) || (paramInt1 == 0)) {
      return BigDecimal.zeroValueOf(paramInt2);
    }
    int[] arrayOfInt = getMagnitudeArray();
    int i = arrayOfInt.length;
    int j = arrayOfInt[0];
    if ((i > 2) || ((j < 0) && (i == 2))) {
      return new BigDecimal(new BigInteger(arrayOfInt, paramInt1), Long.MIN_VALUE, paramInt2, 0);
    }
    long l = i == 2 ? arrayOfInt[1] & 0xFFFFFFFF | (j & 0xFFFFFFFF) << 32 : j & 0xFFFFFFFF;
    return BigDecimal.valueOf(paramInt1 == -1 ? -l : l, paramInt2);
  }
  
  long toCompactValue(int paramInt)
  {
    if ((intLen == 0) || (paramInt == 0)) {
      return 0L;
    }
    int[] arrayOfInt = getMagnitudeArray();
    int i = arrayOfInt.length;
    int j = arrayOfInt[0];
    if ((i > 2) || ((j < 0) && (i == 2))) {
      return Long.MIN_VALUE;
    }
    long l = i == 2 ? arrayOfInt[1] & 0xFFFFFFFF | (j & 0xFFFFFFFF) << 32 : j & 0xFFFFFFFF;
    return paramInt == -1 ? -l : l;
  }
  
  void clear()
  {
    offset = (intLen = 0);
    int i = 0;
    int j = value.length;
    while (i < j)
    {
      value[i] = 0;
      i++;
    }
  }
  
  void reset()
  {
    offset = (intLen = 0);
  }
  
  final int compare(MutableBigInteger paramMutableBigInteger)
  {
    int i = intLen;
    if (intLen < i) {
      return -1;
    }
    if (intLen > i) {
      return 1;
    }
    int[] arrayOfInt = value;
    int j = offset;
    for (int k = offset; j < intLen + offset; k++)
    {
      int m = value[j] + Integer.MIN_VALUE;
      int n = arrayOfInt[k] + Integer.MIN_VALUE;
      if (m < n) {
        return -1;
      }
      if (m > n) {
        return 1;
      }
      j++;
    }
    return 0;
  }
  
  private int compareShifted(MutableBigInteger paramMutableBigInteger, int paramInt)
  {
    int i = intLen;
    int j = intLen - paramInt;
    if (j < i) {
      return -1;
    }
    if (j > i) {
      return 1;
    }
    int[] arrayOfInt = value;
    int k = offset;
    for (int m = offset; k < j + offset; m++)
    {
      int n = value[k] + Integer.MIN_VALUE;
      int i1 = arrayOfInt[m] + Integer.MIN_VALUE;
      if (n < i1) {
        return -1;
      }
      if (n > i1) {
        return 1;
      }
      k++;
    }
    return 0;
  }
  
  final int compareHalf(MutableBigInteger paramMutableBigInteger)
  {
    int i = intLen;
    int j = intLen;
    if (j <= 0) {
      return i <= 0 ? 0 : -1;
    }
    if (j > i) {
      return 1;
    }
    if (j < i - 1) {
      return -1;
    }
    int[] arrayOfInt1 = value;
    int k = 0;
    int m = 0;
    if (j != i) {
      if (arrayOfInt1[k] == 1)
      {
        k++;
        m = Integer.MIN_VALUE;
      }
      else
      {
        return -1;
      }
    }
    int[] arrayOfInt2 = value;
    int n = offset;
    int i1 = k;
    while (n < j + offset)
    {
      int i2 = arrayOfInt1[(i1++)];
      long l1 = (i2 >>> 1) + m & 0xFFFFFFFF;
      long l2 = arrayOfInt2[(n++)] & 0xFFFFFFFF;
      if (l2 != l1) {
        return l2 < l1 ? -1 : 1;
      }
      m = (i2 & 0x1) << 31;
    }
    return m == 0 ? 0 : -1;
  }
  
  private final int getLowestSetBit()
  {
    if (intLen == 0) {
      return -1;
    }
    for (int i = intLen - 1; (i > 0) && (value[(i + offset)] == 0); i--) {}
    int j = value[(i + offset)];
    if (j == 0) {
      return -1;
    }
    return (intLen - 1 - i << 5) + Integer.numberOfTrailingZeros(j);
  }
  
  private final int getInt(int paramInt)
  {
    return value[(offset + paramInt)];
  }
  
  private final long getLong(int paramInt)
  {
    return value[(offset + paramInt)] & 0xFFFFFFFF;
  }
  
  final void normalize()
  {
    if (intLen == 0)
    {
      offset = 0;
      return;
    }
    int i = offset;
    if (value[i] != 0) {
      return;
    }
    int j = i + intLen;
    do
    {
      i++;
    } while ((i < j) && (value[i] == 0));
    int k = i - offset;
    intLen -= k;
    offset = (intLen == 0 ? 0 : offset + k);
  }
  
  private final void ensureCapacity(int paramInt)
  {
    if (value.length < paramInt)
    {
      value = new int[paramInt];
      offset = 0;
      intLen = paramInt;
    }
  }
  
  int[] toIntArray()
  {
    int[] arrayOfInt = new int[intLen];
    for (int i = 0; i < intLen; i++) {
      arrayOfInt[i] = value[(offset + i)];
    }
    return arrayOfInt;
  }
  
  void setInt(int paramInt1, int paramInt2)
  {
    value[(offset + paramInt1)] = paramInt2;
  }
  
  void setValue(int[] paramArrayOfInt, int paramInt)
  {
    value = paramArrayOfInt;
    intLen = paramInt;
    offset = 0;
  }
  
  void copyValue(MutableBigInteger paramMutableBigInteger)
  {
    int i = intLen;
    if (value.length < i) {
      value = new int[i];
    }
    System.arraycopy(value, offset, value, 0, i);
    intLen = i;
    offset = 0;
  }
  
  void copyValue(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    if (value.length < i) {
      value = new int[i];
    }
    System.arraycopy(paramArrayOfInt, 0, value, 0, i);
    intLen = i;
    offset = 0;
  }
  
  boolean isOne()
  {
    return (intLen == 1) && (value[offset] == 1);
  }
  
  boolean isZero()
  {
    return intLen == 0;
  }
  
  boolean isEven()
  {
    return (intLen == 0) || ((value[(offset + intLen - 1)] & 0x1) == 0);
  }
  
  boolean isOdd()
  {
    return !isZero();
  }
  
  boolean isNormal()
  {
    if (intLen + offset > value.length) {
      return false;
    }
    if (intLen == 0) {
      return true;
    }
    return value[offset] != 0;
  }
  
  public String toString()
  {
    BigInteger localBigInteger = toBigInteger(1);
    return localBigInteger.toString();
  }
  
  void safeRightShift(int paramInt)
  {
    if (paramInt / 32 >= intLen) {
      reset();
    } else {
      rightShift(paramInt);
    }
  }
  
  void rightShift(int paramInt)
  {
    if (intLen == 0) {
      return;
    }
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    intLen -= i;
    if (j == 0) {
      return;
    }
    int k = BigInteger.bitLengthForInt(value[offset]);
    if (j >= k)
    {
      primitiveLeftShift(32 - j);
      intLen -= 1;
    }
    else
    {
      primitiveRightShift(j);
    }
  }
  
  void safeLeftShift(int paramInt)
  {
    if (paramInt > 0) {
      leftShift(paramInt);
    }
  }
  
  void leftShift(int paramInt)
  {
    if (intLen == 0) {
      return;
    }
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    int k = BigInteger.bitLengthForInt(value[offset]);
    if (paramInt <= 32 - k)
    {
      primitiveLeftShift(j);
      return;
    }
    int m = intLen + i + 1;
    if (j <= 32 - k) {
      m--;
    }
    if (value.length < m)
    {
      int[] arrayOfInt = new int[m];
      for (int i1 = 0; i1 < intLen; i1++) {
        arrayOfInt[i1] = value[(offset + i1)];
      }
      setValue(arrayOfInt, m);
    }
    else
    {
      int n;
      if (value.length - offset >= m)
      {
        for (n = 0; n < m - intLen; n++) {
          value[(offset + intLen + n)] = 0;
        }
      }
      else
      {
        for (n = 0; n < intLen; n++) {
          value[n] = value[(offset + n)];
        }
        for (n = intLen; n < m; n++) {
          value[n] = 0;
        }
        offset = 0;
      }
    }
    intLen = m;
    if (j == 0) {
      return;
    }
    if (j <= 32 - k) {
      primitiveLeftShift(j);
    } else {
      primitiveRightShift(32 - j);
    }
  }
  
  private int divadd(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    long l1 = 0L;
    for (int i = paramArrayOfInt1.length - 1; i >= 0; i--)
    {
      long l2 = (paramArrayOfInt1[i] & 0xFFFFFFFF) + (paramArrayOfInt2[(i + paramInt)] & 0xFFFFFFFF) + l1;
      paramArrayOfInt2[(i + paramInt)] = ((int)l2);
      l1 = l2 >>> 32;
    }
    return (int)l1;
  }
  
  private int mulsub(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3)
  {
    long l1 = paramInt1 & 0xFFFFFFFF;
    long l2 = 0L;
    paramInt3 += paramInt2;
    for (int i = paramInt2 - 1; i >= 0; i--)
    {
      long l3 = (paramArrayOfInt2[i] & 0xFFFFFFFF) * l1 + l2;
      long l4 = paramArrayOfInt1[paramInt3] - l3;
      paramArrayOfInt1[(paramInt3--)] = ((int)l4);
      l2 = (l3 >>> 32) + ((l4 & 0xFFFFFFFF) > (((int)l3 ^ 0xFFFFFFFF) & 0xFFFFFFFF) ? 1 : 0);
    }
    return (int)l2;
  }
  
  private int mulsubBorrow(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3)
  {
    long l1 = paramInt1 & 0xFFFFFFFF;
    long l2 = 0L;
    paramInt3 += paramInt2;
    for (int i = paramInt2 - 1; i >= 0; i--)
    {
      long l3 = (paramArrayOfInt2[i] & 0xFFFFFFFF) * l1 + l2;
      long l4 = paramArrayOfInt1[(paramInt3--)] - l3;
      l2 = (l3 >>> 32) + ((l4 & 0xFFFFFFFF) > (((int)l3 ^ 0xFFFFFFFF) & 0xFFFFFFFF) ? 1 : 0);
    }
    return (int)l2;
  }
  
  private final void primitiveRightShift(int paramInt)
  {
    int[] arrayOfInt = value;
    int i = 32 - paramInt;
    int j = offset + intLen - 1;
    int k = arrayOfInt[j];
    while (j > offset)
    {
      int m = k;
      k = arrayOfInt[(j - 1)];
      arrayOfInt[j] = (k << i | m >>> paramInt);
      j--;
    }
    arrayOfInt[offset] >>>= paramInt;
  }
  
  private final void primitiveLeftShift(int paramInt)
  {
    int[] arrayOfInt = value;
    int i = 32 - paramInt;
    int j = offset;
    int k = arrayOfInt[j];
    int m = j + intLen - 1;
    while (j < m)
    {
      int n = k;
      k = arrayOfInt[(j + 1)];
      arrayOfInt[j] = (n << paramInt | k >>> i);
      j++;
    }
    arrayOfInt[(offset + intLen - 1)] <<= paramInt;
  }
  
  private BigInteger getLower(int paramInt)
  {
    if (isZero()) {
      return BigInteger.ZERO;
    }
    if (intLen < paramInt) {
      return toBigInteger(1);
    }
    for (int i = paramInt; (i > 0) && (value[(offset + intLen - i)] == 0); i--) {}
    int j = i > 0 ? 1 : 0;
    return new BigInteger(Arrays.copyOfRange(value, offset + intLen - i, offset + intLen), j);
  }
  
  private void keepLower(int paramInt)
  {
    if (intLen >= paramInt)
    {
      offset += intLen - paramInt;
      intLen = paramInt;
    }
  }
  
  void add(MutableBigInteger paramMutableBigInteger)
  {
    int i = intLen;
    int j = intLen;
    int k = intLen > intLen ? intLen : intLen;
    Object localObject = value.length < k ? new int[k] : value;
    int m = localObject.length - 1;
    long l1;
    for (long l2 = 0L; (i > 0) && (j > 0); l2 = l1 >>> 32)
    {
      i--;
      j--;
      l1 = (value[(i + offset)] & 0xFFFFFFFF) + (value[(j + offset)] & 0xFFFFFFFF) + l2;
      localObject[(m--)] = ((int)l1);
    }
    while (i > 0)
    {
      i--;
      if ((l2 == 0L) && (localObject == value) && (m == i + offset)) {
        return;
      }
      l1 = (value[(i + offset)] & 0xFFFFFFFF) + l2;
      localObject[(m--)] = ((int)l1);
      l2 = l1 >>> 32;
    }
    while (j > 0)
    {
      j--;
      l1 = (value[(j + offset)] & 0xFFFFFFFF) + l2;
      localObject[(m--)] = ((int)l1);
      l2 = l1 >>> 32;
    }
    if (l2 > 0L)
    {
      k++;
      if (localObject.length < k)
      {
        int[] arrayOfInt = new int[k];
        System.arraycopy(localObject, 0, arrayOfInt, 1, localObject.length);
        arrayOfInt[0] = 1;
        localObject = arrayOfInt;
      }
      else
      {
        localObject[(m--)] = 1;
      }
    }
    value = ((int[])localObject);
    intLen = k;
    offset = (localObject.length - k);
  }
  
  void addShifted(MutableBigInteger paramMutableBigInteger, int paramInt)
  {
    if (paramMutableBigInteger.isZero()) {
      return;
    }
    int i = intLen;
    int j = intLen + paramInt;
    int k = intLen > j ? intLen : j;
    Object localObject = value.length < k ? new int[k] : value;
    int m = localObject.length - 1;
    int n;
    long l1;
    for (long l2 = 0L; (i > 0) && (j > 0); l2 = l1 >>> 32)
    {
      i--;
      j--;
      n = j + offset < value.length ? value[(j + offset)] : 0;
      l1 = (value[(i + offset)] & 0xFFFFFFFF) + (n & 0xFFFFFFFF) + l2;
      localObject[(m--)] = ((int)l1);
    }
    while (i > 0)
    {
      i--;
      if ((l2 == 0L) && (localObject == value) && (m == i + offset)) {
        return;
      }
      l1 = (value[(i + offset)] & 0xFFFFFFFF) + l2;
      localObject[(m--)] = ((int)l1);
      l2 = l1 >>> 32;
    }
    while (j > 0)
    {
      j--;
      n = j + offset < value.length ? value[(j + offset)] : 0;
      l1 = (n & 0xFFFFFFFF) + l2;
      localObject[(m--)] = ((int)l1);
      l2 = l1 >>> 32;
    }
    if (l2 > 0L)
    {
      k++;
      if (localObject.length < k)
      {
        int[] arrayOfInt = new int[k];
        System.arraycopy(localObject, 0, arrayOfInt, 1, localObject.length);
        arrayOfInt[0] = 1;
        localObject = arrayOfInt;
      }
      else
      {
        localObject[(m--)] = 1;
      }
    }
    value = ((int[])localObject);
    intLen = k;
    offset = (localObject.length - k);
  }
  
  void addDisjoint(MutableBigInteger paramMutableBigInteger, int paramInt)
  {
    if (paramMutableBigInteger.isZero()) {
      return;
    }
    int i = intLen;
    int j = intLen + paramInt;
    int k = intLen > j ? intLen : j;
    int[] arrayOfInt;
    if (value.length < k)
    {
      arrayOfInt = new int[k];
    }
    else
    {
      arrayOfInt = value;
      Arrays.fill(value, offset + intLen, value.length, 0);
    }
    int m = arrayOfInt.length - 1;
    System.arraycopy(value, offset, arrayOfInt, m + 1 - i, i);
    j -= i;
    m -= i;
    int n = Math.min(j, value.length - offset);
    System.arraycopy(value, offset, arrayOfInt, m + 1 - j, n);
    for (int i1 = m + 1 - j + n; i1 < m + 1; i1++) {
      arrayOfInt[i1] = 0;
    }
    value = arrayOfInt;
    intLen = k;
    offset = (arrayOfInt.length - k);
  }
  
  void addLower(MutableBigInteger paramMutableBigInteger, int paramInt)
  {
    MutableBigInteger localMutableBigInteger = new MutableBigInteger(paramMutableBigInteger);
    if (offset + intLen >= paramInt)
    {
      offset = (offset + intLen - paramInt);
      intLen = paramInt;
    }
    localMutableBigInteger.normalize();
    add(localMutableBigInteger);
  }
  
  int subtract(MutableBigInteger paramMutableBigInteger)
  {
    MutableBigInteger localMutableBigInteger1 = this;
    int[] arrayOfInt = value;
    int i = localMutableBigInteger1.compare(paramMutableBigInteger);
    if (i == 0)
    {
      reset();
      return 0;
    }
    if (i < 0)
    {
      MutableBigInteger localMutableBigInteger2 = localMutableBigInteger1;
      localMutableBigInteger1 = paramMutableBigInteger;
      paramMutableBigInteger = localMutableBigInteger2;
    }
    int j = intLen;
    if (arrayOfInt.length < j) {
      arrayOfInt = new int[j];
    }
    long l = 0L;
    int k = intLen;
    int m = intLen;
    int n = arrayOfInt.length - 1;
    while (m > 0)
    {
      k--;
      m--;
      l = (value[(k + offset)] & 0xFFFFFFFF) - (value[(m + offset)] & 0xFFFFFFFF) - (int)-(l >> 32);
      arrayOfInt[(n--)] = ((int)l);
    }
    while (k > 0)
    {
      k--;
      l = (value[(k + offset)] & 0xFFFFFFFF) - (int)-(l >> 32);
      arrayOfInt[(n--)] = ((int)l);
    }
    value = arrayOfInt;
    intLen = j;
    offset = (value.length - j);
    normalize();
    return i;
  }
  
  private int difference(MutableBigInteger paramMutableBigInteger)
  {
    MutableBigInteger localMutableBigInteger1 = this;
    int i = localMutableBigInteger1.compare(paramMutableBigInteger);
    if (i == 0) {
      return 0;
    }
    if (i < 0)
    {
      MutableBigInteger localMutableBigInteger2 = localMutableBigInteger1;
      localMutableBigInteger1 = paramMutableBigInteger;
      paramMutableBigInteger = localMutableBigInteger2;
    }
    long l = 0L;
    int j = intLen;
    int k = intLen;
    while (k > 0)
    {
      j--;
      k--;
      l = (value[(offset + j)] & 0xFFFFFFFF) - (value[(offset + k)] & 0xFFFFFFFF) - (int)-(l >> 32);
      value[(offset + j)] = ((int)l);
    }
    while (j > 0)
    {
      j--;
      l = (value[(offset + j)] & 0xFFFFFFFF) - (int)-(l >> 32);
      value[(offset + j)] = ((int)l);
    }
    localMutableBigInteger1.normalize();
    return i;
  }
  
  void multiply(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2)
  {
    int i = intLen;
    int j = intLen;
    int k = i + j;
    if (value.length < k) {
      value = new int[k];
    }
    offset = 0;
    intLen = k;
    long l1 = 0L;
    int m = j - 1;
    for (int n = j + i - 1; m >= 0; n--)
    {
      long l2 = (value[(m + offset)] & 0xFFFFFFFF) * (value[(i - 1 + offset)] & 0xFFFFFFFF) + l1;
      value[n] = ((int)l2);
      l1 = l2 >>> 32;
      m--;
    }
    value[(i - 1)] = ((int)l1);
    for (m = i - 2; m >= 0; m--)
    {
      l1 = 0L;
      n = j - 1;
      for (int i1 = j + m; n >= 0; i1--)
      {
        long l3 = (value[(n + offset)] & 0xFFFFFFFF) * (value[(m + offset)] & 0xFFFFFFFF) + (value[i1] & 0xFFFFFFFF) + l1;
        value[i1] = ((int)l3);
        l1 = l3 >>> 32;
        n--;
      }
      value[m] = ((int)l1);
    }
    paramMutableBigInteger2.normalize();
  }
  
  void mul(int paramInt, MutableBigInteger paramMutableBigInteger)
  {
    if (paramInt == 1)
    {
      paramMutableBigInteger.copyValue(this);
      return;
    }
    if (paramInt == 0)
    {
      paramMutableBigInteger.clear();
      return;
    }
    long l1 = paramInt & 0xFFFFFFFF;
    int[] arrayOfInt = value.length < intLen + 1 ? new int[intLen + 1] : value;
    long l2 = 0L;
    for (int i = intLen - 1; i >= 0; i--)
    {
      long l3 = l1 * (value[(i + offset)] & 0xFFFFFFFF) + l2;
      arrayOfInt[(i + 1)] = ((int)l3);
      l2 = l3 >>> 32;
    }
    if (l2 == 0L)
    {
      offset = 1;
      intLen = intLen;
    }
    else
    {
      offset = 0;
      intLen += 1;
      arrayOfInt[0] = ((int)l2);
    }
    value = arrayOfInt;
  }
  
  int divideOneWord(int paramInt, MutableBigInteger paramMutableBigInteger)
  {
    long l1 = paramInt & 0xFFFFFFFF;
    if (intLen == 1)
    {
      long l2 = value[offset] & 0xFFFFFFFF;
      int k = (int)(l2 / l1);
      int m = (int)(l2 - k * l1);
      value[0] = k;
      intLen = (k == 0 ? 0 : 1);
      offset = 0;
      return m;
    }
    if (value.length < intLen) {
      value = new int[intLen];
    }
    offset = 0;
    intLen = intLen;
    int i = Integer.numberOfLeadingZeros(paramInt);
    int j = value[offset];
    long l3 = j & 0xFFFFFFFF;
    if (l3 < l1)
    {
      value[0] = 0;
    }
    else
    {
      value[0] = ((int)(l3 / l1));
      j = (int)(l3 - value[0] * l1);
      l3 = j & 0xFFFFFFFF;
    }
    int n = intLen;
    for (;;)
    {
      n--;
      if (n <= 0) {
        break;
      }
      long l4 = l3 << 32 | value[(offset + intLen - n)] & 0xFFFFFFFF;
      int i1;
      if (l4 >= 0L)
      {
        i1 = (int)(l4 / l1);
        j = (int)(l4 - i1 * l1);
      }
      else
      {
        long l5 = divWord(l4, paramInt);
        i1 = (int)(l5 & 0xFFFFFFFF);
        j = (int)(l5 >>> 32);
      }
      value[(intLen - n)] = i1;
      l3 = j & 0xFFFFFFFF;
    }
    paramMutableBigInteger.normalize();
    if (i > 0) {
      return j % paramInt;
    }
    return j;
  }
  
  MutableBigInteger divide(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2)
  {
    return divide(paramMutableBigInteger1, paramMutableBigInteger2, true);
  }
  
  MutableBigInteger divide(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, boolean paramBoolean)
  {
    if ((intLen < 80) || (intLen - intLen < 40)) {
      return divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2, paramBoolean);
    }
    return divideAndRemainderBurnikelZiegler(paramMutableBigInteger1, paramMutableBigInteger2);
  }
  
  MutableBigInteger divideKnuth(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2)
  {
    return divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2, true);
  }
  
  MutableBigInteger divideKnuth(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, boolean paramBoolean)
  {
    if (intLen == 0) {
      throw new ArithmeticException("BigInteger divide by zero");
    }
    if (intLen == 0)
    {
      intLen = (offset = 0);
      return paramBoolean ? new MutableBigInteger() : null;
    }
    int i = compare(paramMutableBigInteger1);
    if (i < 0)
    {
      intLen = (offset = 0);
      return paramBoolean ? new MutableBigInteger(this) : null;
    }
    if (i == 0)
    {
      value[0] = (intLen = 1);
      offset = 0;
      return paramBoolean ? new MutableBigInteger() : null;
    }
    paramMutableBigInteger2.clear();
    int j;
    if (intLen == 1)
    {
      j = divideOneWord(value[offset], paramMutableBigInteger2);
      if (paramBoolean)
      {
        if (j == 0) {
          return new MutableBigInteger();
        }
        return new MutableBigInteger(j);
      }
      return null;
    }
    if (intLen >= 6)
    {
      j = Math.min(getLowestSetBit(), paramMutableBigInteger1.getLowestSetBit());
      if (j >= 96)
      {
        MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(this);
        paramMutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger1);
        localMutableBigInteger1.rightShift(j);
        paramMutableBigInteger1.rightShift(j);
        MutableBigInteger localMutableBigInteger2 = localMutableBigInteger1.divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2);
        localMutableBigInteger2.leftShift(j);
        return localMutableBigInteger2;
      }
    }
    return divideMagnitude(paramMutableBigInteger1, paramMutableBigInteger2, paramBoolean);
  }
  
  MutableBigInteger divideAndRemainderBurnikelZiegler(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2)
  {
    int i = intLen;
    int j = intLen;
    offset = (intLen = 0);
    if (i < j) {
      return this;
    }
    int k = 1 << 32 - Integer.numberOfLeadingZeros(j / 80);
    int m = (j + k - 1) / k;
    int n = m * k;
    long l = 32L * n;
    int i1 = (int)Math.max(0L, l - paramMutableBigInteger1.bitLength());
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger1);
    localMutableBigInteger1.safeLeftShift(i1);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(this);
    localMutableBigInteger2.safeLeftShift(i1);
    int i2 = (int)((localMutableBigInteger2.bitLength() + l) / l);
    if (i2 < 2) {
      i2 = 2;
    }
    MutableBigInteger localMutableBigInteger3 = localMutableBigInteger2.getBlock(i2 - 1, i2, n);
    MutableBigInteger localMutableBigInteger4 = localMutableBigInteger2.getBlock(i2 - 2, i2, n);
    localMutableBigInteger4.addDisjoint(localMutableBigInteger3, n);
    MutableBigInteger localMutableBigInteger5 = new MutableBigInteger();
    for (int i3 = i2 - 2; i3 > 0; i3--)
    {
      localMutableBigInteger6 = localMutableBigInteger4.divide2n1n(localMutableBigInteger1, localMutableBigInteger5);
      localMutableBigInteger4 = localMutableBigInteger2.getBlock(i3 - 1, i2, n);
      localMutableBigInteger4.addDisjoint(localMutableBigInteger6, n);
      paramMutableBigInteger2.addShifted(localMutableBigInteger5, i3 * n);
    }
    MutableBigInteger localMutableBigInteger6 = localMutableBigInteger4.divide2n1n(localMutableBigInteger1, localMutableBigInteger5);
    paramMutableBigInteger2.add(localMutableBigInteger5);
    localMutableBigInteger6.rightShift(i1);
    return localMutableBigInteger6;
  }
  
  private MutableBigInteger divide2n1n(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2)
  {
    int i = intLen;
    if ((i % 2 != 0) || (i < 80)) {
      return divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2);
    }
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(this);
    localMutableBigInteger1.safeRightShift(32 * (i / 2));
    keepLower(i / 2);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger3 = localMutableBigInteger1.divide3n2n(paramMutableBigInteger1, localMutableBigInteger2);
    addDisjoint(localMutableBigInteger3, i / 2);
    MutableBigInteger localMutableBigInteger4 = divide3n2n(paramMutableBigInteger1, paramMutableBigInteger2);
    paramMutableBigInteger2.addDisjoint(localMutableBigInteger2, i / 2);
    return localMutableBigInteger4;
  }
  
  private MutableBigInteger divide3n2n(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2)
  {
    int i = intLen / 2;
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(this);
    localMutableBigInteger1.safeRightShift(32 * i);
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger(paramMutableBigInteger1);
    localMutableBigInteger2.safeRightShift(i * 32);
    BigInteger localBigInteger = paramMutableBigInteger1.getLower(i);
    MutableBigInteger localMutableBigInteger3;
    MutableBigInteger localMutableBigInteger4;
    if (compareShifted(paramMutableBigInteger1, i) < 0)
    {
      localMutableBigInteger3 = localMutableBigInteger1.divide2n1n(localMutableBigInteger2, paramMutableBigInteger2);
      localMutableBigInteger4 = new MutableBigInteger(paramMutableBigInteger2.toBigInteger().multiply(localBigInteger));
    }
    else
    {
      paramMutableBigInteger2.ones(i);
      localMutableBigInteger1.add(localMutableBigInteger2);
      localMutableBigInteger2.leftShift(32 * i);
      localMutableBigInteger1.subtract(localMutableBigInteger2);
      localMutableBigInteger3 = localMutableBigInteger1;
      localMutableBigInteger4 = new MutableBigInteger(localBigInteger);
      localMutableBigInteger4.leftShift(32 * i);
      localMutableBigInteger4.subtract(new MutableBigInteger(localBigInteger));
    }
    localMutableBigInteger3.leftShift(32 * i);
    localMutableBigInteger3.addLower(this, i);
    while (localMutableBigInteger3.compare(localMutableBigInteger4) < 0)
    {
      localMutableBigInteger3.add(paramMutableBigInteger1);
      paramMutableBigInteger2.subtract(ONE);
    }
    localMutableBigInteger3.subtract(localMutableBigInteger4);
    return localMutableBigInteger3;
  }
  
  private MutableBigInteger getBlock(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 * paramInt3;
    if (i >= intLen) {
      return new MutableBigInteger();
    }
    int j;
    if (paramInt1 == paramInt2 - 1) {
      j = intLen;
    } else {
      j = (paramInt1 + 1) * paramInt3;
    }
    if (j > intLen) {
      return new MutableBigInteger();
    }
    int[] arrayOfInt = Arrays.copyOfRange(value, offset + intLen - j, offset + intLen - i);
    return new MutableBigInteger(arrayOfInt);
  }
  
  long bitLength()
  {
    if (intLen == 0) {
      return 0L;
    }
    return intLen * 32L - Integer.numberOfLeadingZeros(value[offset]);
  }
  
  long divide(long paramLong, MutableBigInteger paramMutableBigInteger)
  {
    if (paramLong == 0L) {
      throw new ArithmeticException("BigInteger divide by zero");
    }
    if (intLen == 0)
    {
      intLen = (offset = 0);
      return 0L;
    }
    if (paramLong < 0L) {
      paramLong = -paramLong;
    }
    int i = (int)(paramLong >>> 32);
    paramMutableBigInteger.clear();
    if (i == 0) {
      return divideOneWord((int)paramLong, paramMutableBigInteger) & 0xFFFFFFFF;
    }
    return divideLongMagnitude(paramLong, paramMutableBigInteger).toLong();
  }
  
  private static void copyAndShift(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4)
  {
    int i = 32 - paramInt4;
    int j = paramArrayOfInt1[paramInt1];
    for (int k = 0; k < paramInt2 - 1; k++)
    {
      int m = j;
      j = paramArrayOfInt1[(++paramInt1)];
      paramArrayOfInt2[(paramInt3 + k)] = (m << paramInt4 | j >>> i);
    }
    paramArrayOfInt2[(paramInt3 + paramInt2 - 1)] = (j << paramInt4);
  }
  
  private MutableBigInteger divideMagnitude(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, boolean paramBoolean)
  {
    int i = Integer.numberOfLeadingZeros(value[offset]);
    int j = intLen;
    int[] arrayOfInt1;
    MutableBigInteger localMutableBigInteger;
    if (i > 0)
    {
      arrayOfInt1 = new int[j];
      copyAndShift(value, offset, j, arrayOfInt1, 0, i);
      int[] arrayOfInt2;
      if (Integer.numberOfLeadingZeros(value[offset]) >= i)
      {
        arrayOfInt2 = new int[intLen + 1];
        localMutableBigInteger = new MutableBigInteger(arrayOfInt2);
        intLen = intLen;
        offset = 1;
        copyAndShift(value, offset, intLen, arrayOfInt2, 1, i);
      }
      else
      {
        arrayOfInt2 = new int[intLen + 2];
        localMutableBigInteger = new MutableBigInteger(arrayOfInt2);
        intLen += 1;
        offset = 1;
        m = offset;
        int n = 0;
        i1 = 32 - i;
        int i2 = 1;
        while (i2 < intLen + 1)
        {
          int i3 = n;
          n = value[m];
          arrayOfInt2[i2] = (i3 << i | n >>> i1);
          i2++;
          m++;
        }
        arrayOfInt2[(intLen + 1)] = (n << i);
      }
    }
    else
    {
      arrayOfInt1 = Arrays.copyOfRange(value, offset, offset + intLen);
      localMutableBigInteger = new MutableBigInteger(new int[intLen + 1]);
      System.arraycopy(value, offset, value, 1, intLen);
      intLen = intLen;
      offset = 1;
    }
    int k = intLen;
    int m = k - j + 1;
    if (value.length < m)
    {
      value = new int[m];
      offset = 0;
    }
    intLen = m;
    int[] arrayOfInt3 = value;
    if (intLen == k)
    {
      offset = 0;
      value[0] = 0;
      intLen += 1;
    }
    int i1 = arrayOfInt1[0];
    long l1 = i1 & 0xFFFFFFFF;
    int i4 = arrayOfInt1[1];
    for (int i5 = 0; i5 < m - 1; i5++)
    {
      i6 = 0;
      i7 = 0;
      i8 = 0;
      i9 = value[(i5 + offset)];
      i10 = i9 + Integer.MIN_VALUE;
      int i11 = value[(i5 + 1 + offset)];
      long l3;
      long l5;
      if (i9 == i1)
      {
        i6 = -1;
        i7 = i9 + i11;
        i8 = i7 + Integer.MIN_VALUE < i10 ? 1 : 0;
      }
      else
      {
        l3 = i9 << 32 | i11 & 0xFFFFFFFF;
        if (l3 >= 0L)
        {
          i6 = (int)(l3 / l1);
          i7 = (int)(l3 - i6 * l1);
        }
        else
        {
          l5 = divWord(l3, i1);
          i6 = (int)(l5 & 0xFFFFFFFF);
          i7 = (int)(l5 >>> 32);
        }
      }
      if (i6 != 0)
      {
        if (i8 == 0)
        {
          l3 = value[(i5 + 2 + offset)] & 0xFFFFFFFF;
          l5 = (i7 & 0xFFFFFFFF) << 32 | l3;
          long l7 = (i4 & 0xFFFFFFFF) * (i6 & 0xFFFFFFFF);
          if (unsignedLongCompare(l7, l5))
          {
            i6--;
            i7 = (int)((i7 & 0xFFFFFFFF) + l1);
            if ((i7 & 0xFFFFFFFF) >= l1)
            {
              l7 -= (i4 & 0xFFFFFFFF);
              l5 = (i7 & 0xFFFFFFFF) << 32 | l3;
              if (unsignedLongCompare(l7, l5)) {
                i6--;
              }
            }
          }
        }
        value[(i5 + offset)] = 0;
        int i13 = mulsub(value, arrayOfInt1, i6, j, i5 + offset);
        if (i13 + Integer.MIN_VALUE > i10)
        {
          divadd(arrayOfInt1, value, i5 + 1 + offset);
          i6--;
        }
        arrayOfInt3[i5] = i6;
      }
    }
    i5 = 0;
    int i6 = 0;
    int i7 = 0;
    int i8 = value[(m - 1 + offset)];
    int i9 = i8 + Integer.MIN_VALUE;
    int i10 = value[(m + offset)];
    long l2;
    long l4;
    if (i8 == i1)
    {
      i5 = -1;
      i6 = i8 + i10;
      i7 = i6 + Integer.MIN_VALUE < i9 ? 1 : 0;
    }
    else
    {
      l2 = i8 << 32 | i10 & 0xFFFFFFFF;
      if (l2 >= 0L)
      {
        i5 = (int)(l2 / l1);
        i6 = (int)(l2 - i5 * l1);
      }
      else
      {
        l4 = divWord(l2, i1);
        i5 = (int)(l4 & 0xFFFFFFFF);
        i6 = (int)(l4 >>> 32);
      }
    }
    if (i5 != 0)
    {
      if (i7 == 0)
      {
        l2 = value[(m + 1 + offset)] & 0xFFFFFFFF;
        l4 = (i6 & 0xFFFFFFFF) << 32 | l2;
        long l6 = (i4 & 0xFFFFFFFF) * (i5 & 0xFFFFFFFF);
        if (unsignedLongCompare(l6, l4))
        {
          i5--;
          i6 = (int)((i6 & 0xFFFFFFFF) + l1);
          if ((i6 & 0xFFFFFFFF) >= l1)
          {
            l6 -= (i4 & 0xFFFFFFFF);
            l4 = (i6 & 0xFFFFFFFF) << 32 | l2;
            if (unsignedLongCompare(l6, l4)) {
              i5--;
            }
          }
        }
      }
      value[(m - 1 + offset)] = 0;
      int i12;
      if (paramBoolean) {
        i12 = mulsub(value, arrayOfInt1, i5, j, m - 1 + offset);
      } else {
        i12 = mulsubBorrow(value, arrayOfInt1, i5, j, m - 1 + offset);
      }
      if (i12 + Integer.MIN_VALUE > i9)
      {
        if (paramBoolean) {
          divadd(arrayOfInt1, value, m - 1 + 1 + offset);
        }
        i5--;
      }
      arrayOfInt3[(m - 1)] = i5;
    }
    if (paramBoolean)
    {
      if (i > 0) {
        localMutableBigInteger.rightShift(i);
      }
      localMutableBigInteger.normalize();
    }
    paramMutableBigInteger2.normalize();
    return paramBoolean ? localMutableBigInteger : null;
  }
  
  private MutableBigInteger divideLongMagnitude(long paramLong, MutableBigInteger paramMutableBigInteger)
  {
    MutableBigInteger localMutableBigInteger = new MutableBigInteger(new int[intLen + 1]);
    System.arraycopy(value, offset, value, 1, intLen);
    intLen = intLen;
    offset = 1;
    int i = intLen;
    int j = i - 2 + 1;
    if (value.length < j)
    {
      value = new int[j];
      offset = 0;
    }
    intLen = j;
    int[] arrayOfInt = value;
    int k = Long.numberOfLeadingZeros(paramLong);
    if (k > 0)
    {
      paramLong <<= k;
      localMutableBigInteger.leftShift(k);
    }
    if (intLen == i)
    {
      offset = 0;
      value[0] = 0;
      intLen += 1;
    }
    int m = (int)(paramLong >>> 32);
    long l1 = m & 0xFFFFFFFF;
    int n = (int)(paramLong & 0xFFFFFFFF);
    for (int i1 = 0; i1 < j; i1++)
    {
      int i2 = 0;
      int i3 = 0;
      int i4 = 0;
      int i5 = value[(i1 + offset)];
      int i6 = i5 + Integer.MIN_VALUE;
      int i7 = value[(i1 + 1 + offset)];
      long l2;
      long l3;
      if (i5 == m)
      {
        i2 = -1;
        i3 = i5 + i7;
        i4 = i3 + Integer.MIN_VALUE < i6 ? 1 : 0;
      }
      else
      {
        l2 = i5 << 32 | i7 & 0xFFFFFFFF;
        if (l2 >= 0L)
        {
          i2 = (int)(l2 / l1);
          i3 = (int)(l2 - i2 * l1);
        }
        else
        {
          l3 = divWord(l2, m);
          i2 = (int)(l3 & 0xFFFFFFFF);
          i3 = (int)(l3 >>> 32);
        }
      }
      if (i2 != 0)
      {
        if (i4 == 0)
        {
          l2 = value[(i1 + 2 + offset)] & 0xFFFFFFFF;
          l3 = (i3 & 0xFFFFFFFF) << 32 | l2;
          long l4 = (n & 0xFFFFFFFF) * (i2 & 0xFFFFFFFF);
          if (unsignedLongCompare(l4, l3))
          {
            i2--;
            i3 = (int)((i3 & 0xFFFFFFFF) + l1);
            if ((i3 & 0xFFFFFFFF) >= l1)
            {
              l4 -= (n & 0xFFFFFFFF);
              l3 = (i3 & 0xFFFFFFFF) << 32 | l2;
              if (unsignedLongCompare(l4, l3)) {
                i2--;
              }
            }
          }
        }
        value[(i1 + offset)] = 0;
        int i8 = mulsubLong(value, m, n, i2, i1 + offset);
        if (i8 + Integer.MIN_VALUE > i6)
        {
          divaddLong(m, n, value, i1 + 1 + offset);
          i2--;
        }
        arrayOfInt[i1] = i2;
      }
    }
    if (k > 0) {
      localMutableBigInteger.rightShift(k);
    }
    paramMutableBigInteger.normalize();
    localMutableBigInteger.normalize();
    return localMutableBigInteger;
  }
  
  private int divaddLong(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3)
  {
    long l1 = 0L;
    long l2 = (paramInt2 & 0xFFFFFFFF) + (paramArrayOfInt[(1 + paramInt3)] & 0xFFFFFFFF);
    paramArrayOfInt[(1 + paramInt3)] = ((int)l2);
    l2 = (paramInt1 & 0xFFFFFFFF) + (paramArrayOfInt[paramInt3] & 0xFFFFFFFF) + l1;
    paramArrayOfInt[paramInt3] = ((int)l2);
    l1 = l2 >>> 32;
    return (int)l1;
  }
  
  private int mulsubLong(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    long l1 = paramInt3 & 0xFFFFFFFF;
    paramInt4 += 2;
    long l2 = (paramInt2 & 0xFFFFFFFF) * l1;
    long l3 = paramArrayOfInt[paramInt4] - l2;
    paramArrayOfInt[(paramInt4--)] = ((int)l3);
    long l4 = (l2 >>> 32) + ((l3 & 0xFFFFFFFF) > (((int)l2 ^ 0xFFFFFFFF) & 0xFFFFFFFF) ? 1 : 0);
    l2 = (paramInt1 & 0xFFFFFFFF) * l1 + l4;
    l3 = paramArrayOfInt[paramInt4] - l2;
    paramArrayOfInt[(paramInt4--)] = ((int)l3);
    l4 = (l2 >>> 32) + ((l3 & 0xFFFFFFFF) > (((int)l2 ^ 0xFFFFFFFF) & 0xFFFFFFFF) ? 1 : 0);
    return (int)l4;
  }
  
  private boolean unsignedLongCompare(long paramLong1, long paramLong2)
  {
    return paramLong1 + Long.MIN_VALUE > paramLong2 + Long.MIN_VALUE;
  }
  
  static long divWord(long paramLong, int paramInt)
  {
    long l1 = paramInt & 0xFFFFFFFF;
    if (l1 == 1L)
    {
      l3 = (int)paramLong;
      l2 = 0L;
      return l2 << 32 | l3 & 0xFFFFFFFF;
    }
    long l3 = (paramLong >>> 1) / (l1 >>> 1);
    long l2 = paramLong - l3 * l1;
    while (l2 < 0L)
    {
      l2 += l1;
      l3 -= 1L;
    }
    while (l2 >= l1)
    {
      l2 -= l1;
      l3 += 1L;
    }
    return l2 << 32 | l3 & 0xFFFFFFFF;
  }
  
  MutableBigInteger hybridGCD(MutableBigInteger paramMutableBigInteger)
  {
    MutableBigInteger localMutableBigInteger1 = this;
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger();
    while (intLen != 0)
    {
      if (Math.abs(intLen - intLen) < 2) {
        return localMutableBigInteger1.binaryGCD(paramMutableBigInteger);
      }
      MutableBigInteger localMutableBigInteger3 = localMutableBigInteger1.divide(paramMutableBigInteger, localMutableBigInteger2);
      localMutableBigInteger1 = paramMutableBigInteger;
      paramMutableBigInteger = localMutableBigInteger3;
    }
    return localMutableBigInteger1;
  }
  
  private MutableBigInteger binaryGCD(MutableBigInteger paramMutableBigInteger)
  {
    Object localObject1 = this;
    MutableBigInteger localMutableBigInteger = new MutableBigInteger();
    int i = ((MutableBigInteger)localObject1).getLowestSetBit();
    int j = paramMutableBigInteger.getLowestSetBit();
    int k = i < j ? i : j;
    if (k != 0)
    {
      ((MutableBigInteger)localObject1).rightShift(k);
      paramMutableBigInteger.rightShift(k);
    }
    int m = k == i ? 1 : 0;
    Object localObject2 = m != 0 ? paramMutableBigInteger : localObject1;
    int n = m != 0 ? -1 : 1;
    int i1;
    while ((i1 = ((MutableBigInteger)localObject2).getLowestSetBit()) >= 0)
    {
      ((MutableBigInteger)localObject2).rightShift(i1);
      if (n > 0) {
        localObject1 = localObject2;
      } else {
        paramMutableBigInteger = (MutableBigInteger)localObject2;
      }
      if ((intLen < 2) && (intLen < 2))
      {
        int i2 = value[offset];
        int i3 = value[offset];
        i2 = binaryGcd(i2, i3);
        value[0] = i2;
        intLen = 1;
        offset = 0;
        if (k > 0) {
          localMutableBigInteger.leftShift(k);
        }
        return localMutableBigInteger;
      }
      if ((n = ((MutableBigInteger)localObject1).difference(paramMutableBigInteger)) == 0) {
        break;
      }
      localObject2 = n >= 0 ? localObject1 : paramMutableBigInteger;
    }
    if (k > 0) {
      ((MutableBigInteger)localObject1).leftShift(k);
    }
    return (MutableBigInteger)localObject1;
  }
  
  static int binaryGcd(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return paramInt1;
    }
    if (paramInt1 == 0) {
      return paramInt2;
    }
    int i = Integer.numberOfTrailingZeros(paramInt1);
    int j = Integer.numberOfTrailingZeros(paramInt2);
    paramInt1 >>>= i;
    paramInt2 >>>= j;
    int k = i < j ? i : j;
    while (paramInt1 != paramInt2) {
      if (paramInt1 + Integer.MIN_VALUE > paramInt2 + Integer.MIN_VALUE)
      {
        paramInt1 -= paramInt2;
        paramInt1 >>>= Integer.numberOfTrailingZeros(paramInt1);
      }
      else
      {
        paramInt2 -= paramInt1;
        paramInt2 >>>= Integer.numberOfTrailingZeros(paramInt2);
      }
    }
    return paramInt1 << k;
  }
  
  MutableBigInteger mutableModInverse(MutableBigInteger paramMutableBigInteger)
  {
    if (paramMutableBigInteger.isOdd()) {
      return modInverse(paramMutableBigInteger);
    }
    if (isEven()) {
      throw new ArithmeticException("BigInteger not invertible.");
    }
    int i = paramMutableBigInteger.getLowestSetBit();
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger);
    localMutableBigInteger1.rightShift(i);
    if (localMutableBigInteger1.isOne()) {
      return modInverseMP2(i);
    }
    MutableBigInteger localMutableBigInteger2 = modInverse(localMutableBigInteger1);
    MutableBigInteger localMutableBigInteger3 = modInverseMP2(i);
    MutableBigInteger localMutableBigInteger4 = modInverseBP2(localMutableBigInteger1, i);
    MutableBigInteger localMutableBigInteger5 = localMutableBigInteger1.modInverseMP2(i);
    MutableBigInteger localMutableBigInteger6 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger7 = new MutableBigInteger();
    MutableBigInteger localMutableBigInteger8 = new MutableBigInteger();
    localMutableBigInteger2.leftShift(i);
    localMutableBigInteger2.multiply(localMutableBigInteger4, localMutableBigInteger8);
    localMutableBigInteger3.multiply(localMutableBigInteger1, localMutableBigInteger6);
    localMutableBigInteger6.multiply(localMutableBigInteger5, localMutableBigInteger7);
    localMutableBigInteger8.add(localMutableBigInteger7);
    return localMutableBigInteger8.divide(paramMutableBigInteger, localMutableBigInteger6);
  }
  
  MutableBigInteger modInverseMP2(int paramInt)
  {
    if (isEven()) {
      throw new ArithmeticException("Non-invertible. (GCD != 1)");
    }
    if (paramInt > 64) {
      return euclidModInverse(paramInt);
    }
    int i = inverseMod32(value[(offset + intLen - 1)]);
    if (paramInt < 33)
    {
      i = paramInt == 32 ? i : i & (1 << paramInt) - 1;
      return new MutableBigInteger(i);
    }
    long l1 = value[(offset + intLen - 1)] & 0xFFFFFFFF;
    if (intLen > 1) {
      l1 |= value[(offset + intLen - 2)] << 32;
    }
    long l2 = i & 0xFFFFFFFF;
    l2 *= (2L - l1 * l2);
    l2 = paramInt == 64 ? l2 : l2 & (1L << paramInt) - 1L;
    MutableBigInteger localMutableBigInteger = new MutableBigInteger(new int[2]);
    value[0] = ((int)(l2 >>> 32));
    value[1] = ((int)l2);
    intLen = 2;
    localMutableBigInteger.normalize();
    return localMutableBigInteger;
  }
  
  static int inverseMod32(int paramInt)
  {
    int i = paramInt;
    i *= (2 - paramInt * i);
    i *= (2 - paramInt * i);
    i *= (2 - paramInt * i);
    i *= (2 - paramInt * i);
    return i;
  }
  
  static long inverseMod64(long paramLong)
  {
    long l = paramLong;
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    assert (l * paramLong == 1L);
    return l;
  }
  
  static MutableBigInteger modInverseBP2(MutableBigInteger paramMutableBigInteger, int paramInt)
  {
    return fixup(new MutableBigInteger(1), new MutableBigInteger(paramMutableBigInteger), paramInt);
  }
  
  private MutableBigInteger modInverse(MutableBigInteger paramMutableBigInteger)
  {
    MutableBigInteger localMutableBigInteger = new MutableBigInteger(paramMutableBigInteger);
    Object localObject1 = new MutableBigInteger(this);
    Object localObject2 = new MutableBigInteger(localMutableBigInteger);
    Object localObject3 = new SignedMutableBigInteger(1);
    Object localObject4 = new SignedMutableBigInteger();
    Object localObject5 = null;
    Object localObject6 = null;
    int i = 0;
    int j;
    if (((MutableBigInteger)localObject1).isEven())
    {
      j = ((MutableBigInteger)localObject1).getLowestSetBit();
      ((MutableBigInteger)localObject1).rightShift(j);
      ((SignedMutableBigInteger)localObject4).leftShift(j);
      i = j;
    }
    while (!((MutableBigInteger)localObject1).isOne())
    {
      if (((MutableBigInteger)localObject1).isZero()) {
        throw new ArithmeticException("BigInteger not invertible.");
      }
      if (((MutableBigInteger)localObject1).compare((MutableBigInteger)localObject2) < 0)
      {
        localObject5 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject5;
        localObject6 = localObject4;
        localObject4 = localObject3;
        localObject3 = localObject6;
      }
      if (((value[(offset + intLen - 1)] ^ value[(offset + intLen - 1)]) & 0x3) == 0)
      {
        ((MutableBigInteger)localObject1).subtract((MutableBigInteger)localObject2);
        ((SignedMutableBigInteger)localObject3).signedSubtract((SignedMutableBigInteger)localObject4);
      }
      else
      {
        ((MutableBigInteger)localObject1).add((MutableBigInteger)localObject2);
        ((SignedMutableBigInteger)localObject3).signedAdd((SignedMutableBigInteger)localObject4);
      }
      j = ((MutableBigInteger)localObject1).getLowestSetBit();
      ((MutableBigInteger)localObject1).rightShift(j);
      ((SignedMutableBigInteger)localObject4).leftShift(j);
      i += j;
    }
    while (sign < 0) {
      ((SignedMutableBigInteger)localObject3).signedAdd(localMutableBigInteger);
    }
    return fixup((MutableBigInteger)localObject3, localMutableBigInteger, i);
  }
  
  static MutableBigInteger fixup(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, int paramInt)
  {
    MutableBigInteger localMutableBigInteger = new MutableBigInteger();
    int i = -inverseMod32(value[(offset + intLen - 1)]);
    int j = 0;
    int k = paramInt >> 5;
    while (j < k)
    {
      int m = i * value[(offset + intLen - 1)];
      paramMutableBigInteger2.mul(m, localMutableBigInteger);
      paramMutableBigInteger1.add(localMutableBigInteger);
      intLen -= 1;
      j++;
    }
    j = paramInt & 0x1F;
    if (j != 0)
    {
      k = i * value[(offset + intLen - 1)];
      k &= (1 << j) - 1;
      paramMutableBigInteger2.mul(k, localMutableBigInteger);
      paramMutableBigInteger1.add(localMutableBigInteger);
      paramMutableBigInteger1.rightShift(j);
    }
    while (paramMutableBigInteger1.compare(paramMutableBigInteger2) >= 0) {
      paramMutableBigInteger1.subtract(paramMutableBigInteger2);
    }
    return paramMutableBigInteger1;
  }
  
  MutableBigInteger euclidModInverse(int paramInt)
  {
    Object localObject1 = new MutableBigInteger(1);
    ((MutableBigInteger)localObject1).leftShift(paramInt);
    MutableBigInteger localMutableBigInteger1 = new MutableBigInteger((MutableBigInteger)localObject1);
    Object localObject2 = new MutableBigInteger(this);
    Object localObject3 = new MutableBigInteger();
    Object localObject4 = ((MutableBigInteger)localObject1).divide((MutableBigInteger)localObject2, (MutableBigInteger)localObject3);
    Object localObject5 = localObject1;
    localObject1 = localObject4;
    localObject4 = localObject5;
    MutableBigInteger localMutableBigInteger2 = new MutableBigInteger((MutableBigInteger)localObject3);
    MutableBigInteger localMutableBigInteger3 = new MutableBigInteger(1);
    Object localObject6 = new MutableBigInteger();
    while (!((MutableBigInteger)localObject1).isOne())
    {
      localObject4 = ((MutableBigInteger)localObject2).divide((MutableBigInteger)localObject1, (MutableBigInteger)localObject3);
      if (intLen == 0) {
        throw new ArithmeticException("BigInteger not invertible.");
      }
      localObject5 = localObject4;
      localObject2 = localObject5;
      if (intLen == 1) {
        localMutableBigInteger2.mul(value[offset], (MutableBigInteger)localObject6);
      } else {
        ((MutableBigInteger)localObject3).multiply(localMutableBigInteger2, (MutableBigInteger)localObject6);
      }
      localObject5 = localObject3;
      localObject3 = localObject6;
      localObject6 = localObject5;
      localMutableBigInteger3.add((MutableBigInteger)localObject3);
      if (((MutableBigInteger)localObject2).isOne()) {
        return localMutableBigInteger3;
      }
      localObject4 = ((MutableBigInteger)localObject1).divide((MutableBigInteger)localObject2, (MutableBigInteger)localObject3);
      if (intLen == 0) {
        throw new ArithmeticException("BigInteger not invertible.");
      }
      localObject5 = localObject1;
      localObject1 = localObject4;
      if (intLen == 1) {
        localMutableBigInteger3.mul(value[offset], (MutableBigInteger)localObject6);
      } else {
        ((MutableBigInteger)localObject3).multiply(localMutableBigInteger3, (MutableBigInteger)localObject6);
      }
      localObject5 = localObject3;
      localObject3 = localObject6;
      localObject6 = localObject5;
      localMutableBigInteger2.add((MutableBigInteger)localObject3);
    }
    localMutableBigInteger1.subtract(localMutableBigInteger2);
    return localMutableBigInteger1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\math\MutableBigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
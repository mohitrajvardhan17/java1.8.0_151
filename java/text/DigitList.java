package java.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import sun.misc.FloatingDecimal;
import sun.misc.FloatingDecimal.BinaryToASCIIConverter;

final class DigitList
  implements Cloneable
{
  public static final int MAX_COUNT = 19;
  public int decimalAt = 0;
  public int count = 0;
  public char[] digits = new char[19];
  private char[] data;
  private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
  private boolean isNegative = false;
  private static final char[] LONG_MIN_REP = "9223372036854775808".toCharArray();
  private StringBuffer tempBuffer;
  
  DigitList() {}
  
  boolean isZero()
  {
    for (int i = 0; i < count; i++) {
      if (digits[i] != '0') {
        return false;
      }
    }
    return true;
  }
  
  void setRoundingMode(RoundingMode paramRoundingMode)
  {
    roundingMode = paramRoundingMode;
  }
  
  public void clear()
  {
    decimalAt = 0;
    count = 0;
  }
  
  public void append(char paramChar)
  {
    if (count == digits.length)
    {
      char[] arrayOfChar = new char[count + 100];
      System.arraycopy(digits, 0, arrayOfChar, 0, count);
      digits = arrayOfChar;
    }
    digits[(count++)] = paramChar;
  }
  
  public final double getDouble()
  {
    if (count == 0) {
      return 0.0D;
    }
    StringBuffer localStringBuffer = getStringBuffer();
    localStringBuffer.append('.');
    localStringBuffer.append(digits, 0, count);
    localStringBuffer.append('E');
    localStringBuffer.append(decimalAt);
    return Double.parseDouble(localStringBuffer.toString());
  }
  
  public final long getLong()
  {
    if (count == 0) {
      return 0L;
    }
    if (isLongMIN_VALUE()) {
      return Long.MIN_VALUE;
    }
    StringBuffer localStringBuffer = getStringBuffer();
    localStringBuffer.append(digits, 0, count);
    for (int i = count; i < decimalAt; i++) {
      localStringBuffer.append('0');
    }
    return Long.parseLong(localStringBuffer.toString());
  }
  
  public final BigDecimal getBigDecimal()
  {
    if (count == 0)
    {
      if (decimalAt == 0) {
        return BigDecimal.ZERO;
      }
      return new BigDecimal("0E" + decimalAt);
    }
    if (decimalAt == count) {
      return new BigDecimal(digits, 0, count);
    }
    return new BigDecimal(digits, 0, count).scaleByPowerOfTen(decimalAt - count);
  }
  
  boolean fitsIntoLong(boolean paramBoolean1, boolean paramBoolean2)
  {
    while ((count > 0) && (digits[(count - 1)] == '0')) {
      count -= 1;
    }
    if (count == 0) {
      return (paramBoolean1) || (paramBoolean2);
    }
    if ((decimalAt < count) || (decimalAt > 19)) {
      return false;
    }
    if (decimalAt < 19) {
      return true;
    }
    for (int i = 0; i < count; i++)
    {
      int j = digits[i];
      int k = LONG_MIN_REP[i];
      if (j > k) {
        return false;
      }
      if (j < k) {
        return true;
      }
    }
    if (count < decimalAt) {
      return true;
    }
    return !paramBoolean1;
  }
  
  final void set(boolean paramBoolean, double paramDouble, int paramInt)
  {
    set(paramBoolean, paramDouble, paramInt, true);
  }
  
  final void set(boolean paramBoolean1, double paramDouble, int paramInt, boolean paramBoolean2)
  {
    FloatingDecimal.BinaryToASCIIConverter localBinaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(paramDouble);
    boolean bool1 = localBinaryToASCIIConverter.digitsRoundedUp();
    boolean bool2 = localBinaryToASCIIConverter.decimalDigitsExact();
    assert (!localBinaryToASCIIConverter.isExceptional());
    String str = localBinaryToASCIIConverter.toJavaFormatString();
    set(paramBoolean1, str, bool1, bool2, paramInt, paramBoolean2);
  }
  
  private void set(boolean paramBoolean1, String paramString, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
  {
    isNegative = paramBoolean1;
    int i = paramString.length();
    char[] arrayOfChar = getDataChars(i);
    paramString.getChars(0, i, arrayOfChar, 0);
    decimalAt = -1;
    count = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    while (n < i)
    {
      int i1 = arrayOfChar[(n++)];
      if (i1 == 46)
      {
        decimalAt = count;
      }
      else
      {
        if ((i1 == 101) || (i1 == 69))
        {
          j = parseInt(arrayOfChar, n, i);
          break;
        }
        if (m == 0)
        {
          m = i1 != 48 ? 1 : 0;
          if ((m == 0) && (decimalAt != -1)) {
            k++;
          }
        }
        if (m != 0) {
          digits[(count++)] = i1;
        }
      }
    }
    if (decimalAt == -1) {
      decimalAt = count;
    }
    if (m != 0) {
      decimalAt += j - k;
    }
    if (paramBoolean4)
    {
      if (-decimalAt > paramInt)
      {
        count = 0;
        return;
      }
      if (-decimalAt == paramInt)
      {
        if (shouldRoundUp(0, paramBoolean2, paramBoolean3))
        {
          count = 1;
          decimalAt += 1;
          digits[0] = '1';
        }
        else
        {
          count = 0;
        }
        return;
      }
    }
    while ((count > 1) && (digits[(count - 1)] == '0')) {
      count -= 1;
    }
    round(paramBoolean4 ? paramInt + decimalAt : paramInt, paramBoolean2, paramBoolean3);
  }
  
  private final void round(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramInt >= 0) && (paramInt < count))
    {
      if (shouldRoundUp(paramInt, paramBoolean1, paramBoolean2))
      {
        for (;;)
        {
          paramInt--;
          if (paramInt < 0)
          {
            digits[0] = '1';
            decimalAt += 1;
            paramInt = 0;
          }
          else
          {
            int tmp57_56 = paramInt;
            char[] tmp57_53 = digits;
            tmp57_53[tmp57_56] = ((char)(tmp57_53[tmp57_56] + '\001'));
            if (digits[paramInt] <= '9') {
              break;
            }
          }
        }
        paramInt++;
      }
      for (count = paramInt; (count > 1) && (digits[(count - 1)] == '0'); count -= 1) {}
    }
  }
  
  private boolean shouldRoundUp(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramInt < count)
    {
      int i;
      switch (roundingMode)
      {
      case UP: 
        for (i = paramInt; i < count; i++) {
          if (digits[i] != '0') {
            return true;
          }
        }
        break;
      case DOWN: 
        break;
      case CEILING: 
        for (i = paramInt; i < count; i++) {
          if (digits[i] != '0') {
            return !isNegative;
          }
        }
        break;
      case FLOOR: 
        for (i = paramInt; i < count; i++) {
          if (digits[i] != '0') {
            return isNegative;
          }
        }
        break;
      case HALF_UP: 
      case HALF_DOWN: 
        if (digits[paramInt] > '5') {
          return true;
        }
        if (digits[paramInt] == '5')
        {
          if (paramInt != count - 1) {
            return true;
          }
          if (paramBoolean2) {
            return roundingMode == RoundingMode.HALF_UP;
          }
          return !paramBoolean1;
        }
        break;
      case HALF_EVEN: 
        if (digits[paramInt] > '5') {
          return true;
        }
        if (digits[paramInt] == '5')
        {
          if (paramInt == count - 1)
          {
            if (paramBoolean1) {
              return false;
            }
            if (!paramBoolean2) {
              return true;
            }
            return (paramInt > 0) && (digits[(paramInt - 1)] % '\002' != 0);
          }
          for (i = paramInt + 1; i < count; i++) {
            if (digits[i] != '0') {
              return true;
            }
          }
        }
        break;
      case UNNECESSARY: 
        for (i = paramInt; i < count; i++) {
          if (digits[i] != '0') {
            throw new ArithmeticException("Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY");
          }
        }
        break;
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
    return false;
  }
  
  final void set(boolean paramBoolean, long paramLong)
  {
    set(paramBoolean, paramLong, 0);
  }
  
  final void set(boolean paramBoolean, long paramLong, int paramInt)
  {
    isNegative = paramBoolean;
    if (paramLong <= 0L)
    {
      if (paramLong == Long.MIN_VALUE)
      {
        decimalAt = (count = 19);
        System.arraycopy(LONG_MIN_REP, 0, digits, 0, count);
      }
      else
      {
        decimalAt = (count = 0);
      }
    }
    else
    {
      int i = 19;
      while (paramLong > 0L)
      {
        digits[(--i)] = ((char)(int)(48L + paramLong % 10L));
        paramLong /= 10L;
      }
      decimalAt = (19 - i);
      for (int j = 18; digits[j] == '0'; j--) {}
      count = (j - i + 1);
      System.arraycopy(digits, i, digits, 0, count);
    }
    if (paramInt > 0) {
      round(paramInt, false, true);
    }
  }
  
  final void set(boolean paramBoolean1, BigDecimal paramBigDecimal, int paramInt, boolean paramBoolean2)
  {
    String str = paramBigDecimal.toString();
    extendDigits(str.length());
    set(paramBoolean1, str, false, true, paramInt, paramBoolean2);
  }
  
  final void set(boolean paramBoolean, BigInteger paramBigInteger, int paramInt)
  {
    isNegative = paramBoolean;
    String str = paramBigInteger.toString();
    int i = str.length();
    extendDigits(i);
    str.getChars(0, i, digits, 0);
    decimalAt = i;
    for (int j = i - 1; (j >= 0) && (digits[j] == '0'); j--) {}
    count = (j + 1);
    if (paramInt > 0) {
      round(paramInt, false, true);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DigitList)) {
      return false;
    }
    DigitList localDigitList = (DigitList)paramObject;
    if ((count != count) || (decimalAt != decimalAt)) {
      return false;
    }
    for (int i = 0; i < count; i++) {
      if (digits[i] != digits[i]) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = decimalAt;
    for (int j = 0; j < count; j++) {
      i = i * 37 + digits[j];
    }
    return i;
  }
  
  public Object clone()
  {
    try
    {
      DigitList localDigitList = (DigitList)super.clone();
      char[] arrayOfChar = new char[digits.length];
      System.arraycopy(digits, 0, arrayOfChar, 0, digits.length);
      digits = arrayOfChar;
      tempBuffer = null;
      return localDigitList;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  private boolean isLongMIN_VALUE()
  {
    if ((decimalAt != count) || (count != 19)) {
      return false;
    }
    for (int i = 0; i < count; i++) {
      if (digits[i] != LONG_MIN_REP[i]) {
        return false;
      }
    }
    return true;
  }
  
  private static final int parseInt(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int j = 1;
    int i;
    if ((i = paramArrayOfChar[paramInt1]) == '-')
    {
      j = 0;
      paramInt1++;
    }
    else if (i == 43)
    {
      paramInt1++;
    }
    for (int k = 0; paramInt1 < paramInt2; k = k * 10 + (i - 48))
    {
      i = paramArrayOfChar[(paramInt1++)];
      if ((i < 48) || (i > 57)) {
        break;
      }
    }
    return j != 0 ? k : -k;
  }
  
  public String toString()
  {
    if (isZero()) {
      return "0";
    }
    StringBuffer localStringBuffer = getStringBuffer();
    localStringBuffer.append("0.");
    localStringBuffer.append(digits, 0, count);
    localStringBuffer.append("x10^");
    localStringBuffer.append(decimalAt);
    return localStringBuffer.toString();
  }
  
  private StringBuffer getStringBuffer()
  {
    if (tempBuffer == null) {
      tempBuffer = new StringBuffer(19);
    } else {
      tempBuffer.setLength(0);
    }
    return tempBuffer;
  }
  
  private void extendDigits(int paramInt)
  {
    if (paramInt > digits.length) {
      digits = new char[paramInt];
    }
  }
  
  private final char[] getDataChars(int paramInt)
  {
    if ((data == null) || (data.length < paramInt)) {
      data = new char[paramInt];
    }
    return data;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\DigitList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
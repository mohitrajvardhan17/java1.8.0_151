package sun.net.idn;

import java.text.ParseException;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UTF16;

public final class Punycode
{
  private static final int BASE = 36;
  private static final int TMIN = 1;
  private static final int TMAX = 26;
  private static final int SKEW = 38;
  private static final int DAMP = 700;
  private static final int INITIAL_BIAS = 72;
  private static final int INITIAL_N = 128;
  private static final int HYPHEN = 45;
  private static final int DELIMITER = 45;
  private static final int ZERO = 48;
  private static final int NINE = 57;
  private static final int SMALL_A = 97;
  private static final int SMALL_Z = 122;
  private static final int CAPITAL_A = 65;
  private static final int CAPITAL_Z = 90;
  private static final int MAX_CP_COUNT = 256;
  private static final int UINT_MAGIC = Integer.MIN_VALUE;
  private static final long ULONG_MAGIC = Long.MIN_VALUE;
  static final int[] basicToDigit = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
  
  public Punycode() {}
  
  private static int adaptBias(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramInt1 /= 700;
    } else {
      paramInt1 /= 2;
    }
    paramInt1 += paramInt1 / paramInt2;
    for (int i = 0; paramInt1 > 455; i += 36) {
      paramInt1 /= 35;
    }
    return i + 36 * paramInt1 / (paramInt1 + 38);
  }
  
  private static char asciiCaseMap(char paramChar, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (('a' <= paramChar) && (paramChar <= 'z')) {
        paramChar = (char)(paramChar - ' ');
      }
    }
    else if (('A' <= paramChar) && (paramChar <= 'Z')) {
      paramChar = (char)(paramChar + ' ');
    }
    return paramChar;
  }
  
  private static char digitToBasic(int paramInt, boolean paramBoolean)
  {
    if (paramInt < 26)
    {
      if (paramBoolean) {
        return (char)(65 + paramInt);
      }
      return (char)(97 + paramInt);
    }
    return (char)(22 + paramInt);
  }
  
  public static StringBuffer encode(StringBuffer paramStringBuffer, boolean[] paramArrayOfBoolean)
    throws ParseException
  {
    int[] arrayOfInt = new int['Ā'];
    int i10 = paramStringBuffer.length();
    int i11 = 256;
    char[] arrayOfChar = new char[i11];
    StringBuffer localStringBuffer = new StringBuffer();
    int i1;
    int i8 = i1 = 0;
    for (int i3 = 0; i3 < i10; i3++)
    {
      if (i8 == 256) {
        throw new IndexOutOfBoundsException();
      }
      int i9 = paramStringBuffer.charAt(i3);
      if (isBasic(i9))
      {
        if (i1 < i11)
        {
          arrayOfInt[(i8++)] = 0;
          arrayOfChar[i1] = (paramArrayOfBoolean != null ? asciiCaseMap(i9, paramArrayOfBoolean[i3]) : i9);
        }
        i1++;
      }
      else
      {
        int i = ((paramArrayOfBoolean != null) && (paramArrayOfBoolean[i3] != 0) ? 1 : 0) << 31;
        if (!UTF16.isSurrogate(i9))
        {
          i |= i9;
        }
        else
        {
          char c;
          if ((UTF16.isLeadSurrogate(i9)) && (i3 + 1 < i10) && (UTF16.isTrailSurrogate(c = paramStringBuffer.charAt(i3 + 1))))
          {
            i3++;
            i |= UCharacter.getCodePoint(i9, c);
          }
          else
          {
            throw new ParseException("Illegal char found", -1);
          }
        }
        arrayOfInt[(i8++)] = j;
      }
    }
    int n = i1;
    if (n > 0)
    {
      if (i1 < i11) {
        arrayOfChar[i1] = '-';
      }
      i1++;
    }
    int j = 128;
    int k = 0;
    int i2 = 72;
    int m = n;
    while (m < i8)
    {
      int i4 = Integer.MAX_VALUE;
      int i5;
      for (i3 = 0; i3 < i8; i3++)
      {
        i5 = arrayOfInt[i3] & 0x7FFFFFFF;
        if ((j <= i5) && (i5 < i4)) {
          i4 = i5;
        }
      }
      if (i4 - j > (2147483391 - k) / (m + 1)) {
        throw new RuntimeException("Internal program error");
      }
      k += (i4 - j) * (m + 1);
      j = i4;
      for (i3 = 0; i3 < i8; i3++)
      {
        i5 = arrayOfInt[i3] & 0x7FFFFFFF;
        if (i5 < j)
        {
          k++;
        }
        else if (i5 == j)
        {
          i5 = k;
          for (int i6 = 36;; i6 += 36)
          {
            int i7 = i6 - i2;
            if (i7 < 1) {
              i7 = 1;
            } else if (i6 >= i2 + 26) {
              i7 = 26;
            }
            if (i5 < i7) {
              break;
            }
            if (i1 < i11) {
              arrayOfChar[(i1++)] = digitToBasic(i7 + (i5 - i7) % (36 - i7), false);
            }
            i5 = (i5 - i7) / (36 - i7);
          }
          if (i1 < i11) {
            arrayOfChar[(i1++)] = digitToBasic(i5, arrayOfInt[i3] < 0 ? 1 : false);
          }
          i2 = adaptBias(k, m + 1, m == n);
          k = 0;
          m++;
        }
      }
      k++;
      j++;
    }
    return localStringBuffer.append(arrayOfChar, 0, i1);
  }
  
  private static boolean isBasic(int paramInt)
  {
    return paramInt < 128;
  }
  
  private static boolean isBasicUpperCase(int paramInt)
  {
    return (65 <= paramInt) && (paramInt <= 90);
  }
  
  private static boolean isSurrogate(int paramInt)
  {
    return (paramInt & 0xF800) == 55296;
  }
  
  public static StringBuffer decode(StringBuffer paramStringBuffer, boolean[] paramArrayOfBoolean)
    throws ParseException
  {
    int i = paramStringBuffer.length();
    StringBuffer localStringBuffer = new StringBuffer();
    int i13 = 256;
    char[] arrayOfChar = new char[i13];
    int i2 = i;
    while (i2 > 0) {
      if (paramStringBuffer.charAt(--i2) == '-') {
        break;
      }
    }
    int i9;
    int i1;
    int k = i1 = i9 = i2;
    while (i2 > 0)
    {
      int i12 = paramStringBuffer.charAt(--i2);
      if (!isBasic(i12)) {
        throw new ParseException("Illegal char found", -1);
      }
      if (i2 < i13)
      {
        arrayOfChar[i2] = i12;
        if (paramArrayOfBoolean != null) {
          paramArrayOfBoolean[i2] = isBasicUpperCase(i12);
        }
      }
    }
    int j = 128;
    int m = 0;
    int n = 72;
    int i10 = 1000000000;
    int i3 = i1 > 0 ? i1 + 1 : 0;
    while (i3 < i)
    {
      int i4 = m;
      int i5 = 1;
      for (int i6 = 36;; i6 += 36)
      {
        if (i3 >= i) {
          throw new ParseException("Illegal char found", -1);
        }
        int i7 = basicToDigit[((byte)paramStringBuffer.charAt(i3++))];
        if (i7 < 0) {
          throw new ParseException("Invalid char found", -1);
        }
        if (i7 > (Integer.MAX_VALUE - m) / i5) {
          throw new ParseException("Illegal char found", -1);
        }
        m += i7 * i5;
        int i8 = i6 - n;
        if (i8 < 1) {
          i8 = 1;
        } else if (i6 >= n + 26) {
          i8 = 26;
        }
        if (i7 < i8) {
          break;
        }
        if (i5 > Integer.MAX_VALUE / (36 - i8)) {
          throw new ParseException("Illegal char found", -1);
        }
        i5 *= (36 - i8);
      }
      i9++;
      n = adaptBias(m - i4, i9, i4 == 0);
      if (m / i9 > Integer.MAX_VALUE - j) {
        throw new ParseException("Illegal char found", -1);
      }
      j += m / i9;
      m %= i9;
      if ((j > 1114111) || (isSurrogate(j))) {
        throw new ParseException("Illegal char found", -1);
      }
      int i11 = UTF16.getCharCount(j);
      if (k + i11 < i13)
      {
        int i14;
        if (m <= i10)
        {
          i14 = m;
          if (i11 > 1) {
            i10 = i14;
          } else {
            i10++;
          }
        }
        else
        {
          i14 = i10;
          i14 = UTF16.moveCodePointOffset(arrayOfChar, 0, k, i14, m - i14);
        }
        if (i14 < k)
        {
          System.arraycopy(arrayOfChar, i14, arrayOfChar, i14 + i11, k - i14);
          if (paramArrayOfBoolean != null) {
            System.arraycopy(paramArrayOfBoolean, i14, paramArrayOfBoolean, i14 + i11, k - i14);
          }
        }
        if (i11 == 1)
        {
          arrayOfChar[i14] = ((char)j);
        }
        else
        {
          arrayOfChar[i14] = UTF16.getLeadSurrogate(j);
          arrayOfChar[(i14 + 1)] = UTF16.getTrailSurrogate(j);
        }
        if (paramArrayOfBoolean != null)
        {
          paramArrayOfBoolean[i14] = isBasicUpperCase(paramStringBuffer.charAt(i3 - 1));
          if (i11 == 2) {
            paramArrayOfBoolean[(i14 + 1)] = false;
          }
        }
      }
      k += i11;
      m++;
    }
    localStringBuffer.append(arrayOfChar, 0, k);
    return localStringBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\idn\Punycode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
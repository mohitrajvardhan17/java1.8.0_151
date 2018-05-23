package sun.text.normalizer;

public final class UTF16
{
  public static final int CODEPOINT_MIN_VALUE = 0;
  public static final int CODEPOINT_MAX_VALUE = 1114111;
  public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
  public static final int LEAD_SURROGATE_MIN_VALUE = 55296;
  public static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
  public static final int LEAD_SURROGATE_MAX_VALUE = 56319;
  public static final int TRAIL_SURROGATE_MAX_VALUE = 57343;
  public static final int SURROGATE_MIN_VALUE = 55296;
  private static final int LEAD_SURROGATE_SHIFT_ = 10;
  private static final int TRAIL_SURROGATE_MASK_ = 1023;
  private static final int LEAD_SURROGATE_OFFSET_ = 55232;
  
  public UTF16() {}
  
  public static int charAt(String paramString, int paramInt)
  {
    int i = paramString.charAt(paramInt);
    if (i < 55296) {
      return i;
    }
    return _charAt(paramString, paramInt, i);
  }
  
  private static int _charAt(String paramString, int paramInt, char paramChar)
  {
    if (paramChar > 57343) {
      return paramChar;
    }
    if (paramChar <= 56319)
    {
      paramInt++;
      if (paramString.length() != paramInt)
      {
        int i = paramString.charAt(paramInt);
        if ((i >= 56320) && (i <= 57343)) {
          return UCharacterProperty.getRawSupplementary(paramChar, i);
        }
      }
    }
    else
    {
      paramInt--;
      if (paramInt >= 0)
      {
        int j = paramString.charAt(paramInt);
        if ((j >= 55296) && (j <= 56319)) {
          return UCharacterProperty.getRawSupplementary(j, paramChar);
        }
      }
    }
    return paramChar;
  }
  
  public static int charAt(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt3 += paramInt1;
    if ((paramInt3 < paramInt1) || (paramInt3 >= paramInt2)) {
      throw new ArrayIndexOutOfBoundsException(paramInt3);
    }
    int i = paramArrayOfChar[paramInt3];
    if (!isSurrogate(i)) {
      return i;
    }
    char c;
    if (i <= 56319)
    {
      paramInt3++;
      if (paramInt3 >= paramInt2) {
        return i;
      }
      c = paramArrayOfChar[paramInt3];
      if (isTrailSurrogate(c)) {
        return UCharacterProperty.getRawSupplementary(i, c);
      }
    }
    else
    {
      if (paramInt3 == paramInt1) {
        return i;
      }
      paramInt3--;
      c = paramArrayOfChar[paramInt3];
      if (isLeadSurrogate(c)) {
        return UCharacterProperty.getRawSupplementary(c, i);
      }
    }
    return i;
  }
  
  public static int getCharCount(int paramInt)
  {
    if (paramInt < 65536) {
      return 1;
    }
    return 2;
  }
  
  public static boolean isSurrogate(char paramChar)
  {
    return (55296 <= paramChar) && (paramChar <= 57343);
  }
  
  public static boolean isTrailSurrogate(char paramChar)
  {
    return (56320 <= paramChar) && (paramChar <= 57343);
  }
  
  public static boolean isLeadSurrogate(char paramChar)
  {
    return (55296 <= paramChar) && (paramChar <= 56319);
  }
  
  public static char getLeadSurrogate(int paramInt)
  {
    if (paramInt >= 65536) {
      return (char)(55232 + (paramInt >> 10));
    }
    return '\000';
  }
  
  public static char getTrailSurrogate(int paramInt)
  {
    if (paramInt >= 65536) {
      return (char)(56320 + (paramInt & 0x3FF));
    }
    return (char)paramInt;
  }
  
  public static String valueOf(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 1114111)) {
      throw new IllegalArgumentException("Illegal codepoint");
    }
    return toString(paramInt);
  }
  
  public static StringBuffer append(StringBuffer paramStringBuffer, int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 1114111)) {
      throw new IllegalArgumentException("Illegal codepoint: " + Integer.toHexString(paramInt));
    }
    if (paramInt >= 65536)
    {
      paramStringBuffer.append(getLeadSurrogate(paramInt));
      paramStringBuffer.append(getTrailSurrogate(paramInt));
    }
    else
    {
      paramStringBuffer.append((char)paramInt);
    }
    return paramStringBuffer;
  }
  
  public static int moveCodePointOffset(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramArrayOfChar.length;
    int k = paramInt3 + paramInt1;
    if ((paramInt1 < 0) || (paramInt2 < paramInt1)) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > i) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if ((paramInt3 < 0) || (k > paramInt2)) {
      throw new StringIndexOutOfBoundsException(paramInt3);
    }
    char c;
    if (paramInt4 > 0)
    {
      if (paramInt4 + k > i) {
        throw new StringIndexOutOfBoundsException(k);
      }
      j = paramInt4;
      while ((k < paramInt2) && (j > 0))
      {
        c = paramArrayOfChar[k];
        if ((isLeadSurrogate(c)) && (k + 1 < paramInt2) && (isTrailSurrogate(paramArrayOfChar[(k + 1)]))) {
          k++;
        }
        j--;
        k++;
      }
    }
    if (k + paramInt4 < paramInt1) {
      throw new StringIndexOutOfBoundsException(k);
    }
    for (int j = -paramInt4; j > 0; j--)
    {
      k--;
      if (k < paramInt1) {
        break;
      }
      c = paramArrayOfChar[k];
      if ((isTrailSurrogate(c)) && (k > paramInt1) && (isLeadSurrogate(paramArrayOfChar[(k - 1)]))) {
        k--;
      }
    }
    if (j != 0) {
      throw new StringIndexOutOfBoundsException(paramInt4);
    }
    k -= paramInt1;
    return k;
  }
  
  private static String toString(int paramInt)
  {
    if (paramInt < 65536) {
      return String.valueOf((char)paramInt);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getLeadSurrogate(paramInt));
    localStringBuffer.append(getTrailSurrogate(paramInt));
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\UTF16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
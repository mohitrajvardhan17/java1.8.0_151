package java.lang;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class String
  implements Serializable, Comparable<String>, CharSequence
{
  private final char[] value;
  private int hash;
  private static final long serialVersionUID = -6849794470754667710L;
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[0];
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator(null);
  
  public String()
  {
    value = ""value;
  }
  
  public String(String paramString)
  {
    value = value;
    hash = hash;
  }
  
  public String(char[] paramArrayOfChar)
  {
    value = Arrays.copyOf(paramArrayOfChar, paramArrayOfChar.length);
  }
  
  public String(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 <= 0)
    {
      if (paramInt2 < 0) {
        throw new StringIndexOutOfBoundsException(paramInt2);
      }
      if (paramInt1 <= paramArrayOfChar.length)
      {
        value = ""value;
        return;
      }
    }
    if (paramInt1 > paramArrayOfChar.length - paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt1 + paramInt2);
    }
    value = Arrays.copyOfRange(paramArrayOfChar, paramInt1, paramInt1 + paramInt2);
  }
  
  public String(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 <= 0)
    {
      if (paramInt2 < 0) {
        throw new StringIndexOutOfBoundsException(paramInt2);
      }
      if (paramInt1 <= paramArrayOfInt.length)
      {
        value = ""value;
        return;
      }
    }
    if (paramInt1 > paramArrayOfInt.length - paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt1 + paramInt2);
    }
    int i = paramInt1 + paramInt2;
    int j = paramInt2;
    for (int k = paramInt1; k < i; k++)
    {
      m = paramArrayOfInt[k];
      if (!Character.isBmpCodePoint(m)) {
        if (Character.isValidCodePoint(m)) {
          j++;
        } else {
          throw new IllegalArgumentException(Integer.toString(m));
        }
      }
    }
    char[] arrayOfChar = new char[j];
    int m = paramInt1;
    for (int n = 0; m < i; n++)
    {
      int i1 = paramArrayOfInt[m];
      if (Character.isBmpCodePoint(i1)) {
        arrayOfChar[n] = ((char)i1);
      } else {
        Character.toSurrogates(i1, arrayOfChar, n++);
      }
      m++;
    }
    value = arrayOfChar;
  }
  
  @Deprecated
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    checkBounds(paramArrayOfByte, paramInt2, paramInt3);
    char[] arrayOfChar = new char[paramInt3];
    int i;
    if (paramInt1 == 0)
    {
      i = paramInt3;
      while (i-- > 0) {
        arrayOfChar[i] = ((char)(paramArrayOfByte[(i + paramInt2)] & 0xFF));
      }
    }
    else
    {
      paramInt1 <<= 8;
      i = paramInt3;
      while (i-- > 0) {
        arrayOfChar[i] = ((char)(paramInt1 | paramArrayOfByte[(i + paramInt2)] & 0xFF));
      }
    }
    value = arrayOfChar;
  }
  
  @Deprecated
  public String(byte[] paramArrayOfByte, int paramInt)
  {
    this(paramArrayOfByte, paramInt, 0, paramArrayOfByte.length);
  }
  
  private static void checkBounds(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt1 > paramArrayOfByte.length - paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt1 + paramInt2);
    }
  }
  
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString)
    throws UnsupportedEncodingException
  {
    if (paramString == null) {
      throw new NullPointerException("charsetName");
    }
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    value = StringCoding.decode(paramString, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Charset paramCharset)
  {
    if (paramCharset == null) {
      throw new NullPointerException("charset");
    }
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    value = StringCoding.decode(paramCharset, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String(byte[] paramArrayOfByte, String paramString)
    throws UnsupportedEncodingException
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length, paramString);
  }
  
  public String(byte[] paramArrayOfByte, Charset paramCharset)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length, paramCharset);
  }
  
  public String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    value = StringCoding.decode(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public String(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public String(StringBuffer paramStringBuffer)
  {
    synchronized (paramStringBuffer)
    {
      value = Arrays.copyOf(paramStringBuffer.getValue(), paramStringBuffer.length());
    }
  }
  
  public String(StringBuilder paramStringBuilder)
  {
    value = Arrays.copyOf(paramStringBuilder.getValue(), paramStringBuilder.length());
  }
  
  String(char[] paramArrayOfChar, boolean paramBoolean)
  {
    value = paramArrayOfChar;
  }
  
  public int length()
  {
    return value.length;
  }
  
  public boolean isEmpty()
  {
    return value.length == 0;
  }
  
  public char charAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= value.length)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return value[paramInt];
  }
  
  public int codePointAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= value.length)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return Character.codePointAtImpl(value, paramInt, value.length);
  }
  
  public int codePointBefore(int paramInt)
  {
    int i = paramInt - 1;
    if ((i < 0) || (i >= value.length)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return Character.codePointBeforeImpl(value, paramInt, 0);
  }
  
  public int codePointCount(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 > value.length) || (paramInt1 > paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    return Character.codePointCountImpl(value, paramInt1, paramInt2 - paramInt1);
  }
  
  public int offsetByCodePoints(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > value.length)) {
      throw new IndexOutOfBoundsException();
    }
    return Character.offsetByCodePointsImpl(value, 0, value.length, paramInt1, paramInt2);
  }
  
  void getChars(char[] paramArrayOfChar, int paramInt)
  {
    System.arraycopy(value, 0, paramArrayOfChar, paramInt, value.length);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > value.length) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1);
    }
    System.arraycopy(value, paramInt1, paramArrayOfChar, paramInt3, paramInt2 - paramInt1);
  }
  
  @Deprecated
  public void getBytes(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > value.length) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1);
    }
    Objects.requireNonNull(paramArrayOfByte);
    int i = paramInt3;
    int j = paramInt2;
    int k = paramInt1;
    char[] arrayOfChar = value;
    while (k < j) {
      paramArrayOfByte[(i++)] = ((byte)arrayOfChar[(k++)]);
    }
  }
  
  public byte[] getBytes(String paramString)
    throws UnsupportedEncodingException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    return StringCoding.encode(paramString, value, 0, value.length);
  }
  
  public byte[] getBytes(Charset paramCharset)
  {
    if (paramCharset == null) {
      throw new NullPointerException();
    }
    return StringCoding.encode(paramCharset, value, 0, value.length);
  }
  
  public byte[] getBytes()
  {
    return StringCoding.encode(value, 0, value.length);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof String))
    {
      String str = (String)paramObject;
      int i = value.length;
      if (i == value.length)
      {
        char[] arrayOfChar1 = value;
        char[] arrayOfChar2 = value;
        for (int j = 0; i-- != 0; j++) {
          if (arrayOfChar1[j] != arrayOfChar2[j]) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean contentEquals(StringBuffer paramStringBuffer)
  {
    return contentEquals(paramStringBuffer);
  }
  
  private boolean nonSyncContentEquals(AbstractStringBuilder paramAbstractStringBuilder)
  {
    char[] arrayOfChar1 = value;
    char[] arrayOfChar2 = paramAbstractStringBuilder.getValue();
    int i = arrayOfChar1.length;
    if (i != paramAbstractStringBuilder.length()) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (arrayOfChar1[j] != arrayOfChar2[j]) {
        return false;
      }
    }
    return true;
  }
  
  /* Error */
  public boolean contentEquals(CharSequence paramCharSequence)
  {
    // Byte code:
    //   0: aload_1
    //   1: instanceof 222
    //   4: ifeq +39 -> 43
    //   7: aload_1
    //   8: instanceof 240
    //   11: ifeq +23 -> 34
    //   14: aload_1
    //   15: dup
    //   16: astore_2
    //   17: monitorenter
    //   18: aload_0
    //   19: aload_1
    //   20: checkcast 222	java/lang/AbstractStringBuilder
    //   23: invokespecial 475	java/lang/String:nonSyncContentEquals	(Ljava/lang/AbstractStringBuilder;)Z
    //   26: aload_2
    //   27: monitorexit
    //   28: ireturn
    //   29: astore_3
    //   30: aload_2
    //   31: monitorexit
    //   32: aload_3
    //   33: athrow
    //   34: aload_0
    //   35: aload_1
    //   36: checkcast 222	java/lang/AbstractStringBuilder
    //   39: invokespecial 475	java/lang/String:nonSyncContentEquals	(Ljava/lang/AbstractStringBuilder;)Z
    //   42: ireturn
    //   43: aload_1
    //   44: instanceof 237
    //   47: ifeq +9 -> 56
    //   50: aload_0
    //   51: aload_1
    //   52: invokevirtual 477	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   55: ireturn
    //   56: aload_0
    //   57: getfield 421	java/lang/String:value	[C
    //   60: astore_2
    //   61: aload_2
    //   62: arraylength
    //   63: istore_3
    //   64: iload_3
    //   65: aload_1
    //   66: invokeinterface 528 1 0
    //   71: if_icmpeq +5 -> 76
    //   74: iconst_0
    //   75: ireturn
    //   76: iconst_0
    //   77: istore 4
    //   79: iload 4
    //   81: iload_3
    //   82: if_icmpge +26 -> 108
    //   85: aload_2
    //   86: iload 4
    //   88: caload
    //   89: aload_1
    //   90: iload 4
    //   92: invokeinterface 529 2 0
    //   97: if_icmpeq +5 -> 102
    //   100: iconst_0
    //   101: ireturn
    //   102: iinc 4 1
    //   105: goto -26 -> 79
    //   108: iconst_1
    //   109: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	String
    //   0	110	1	paramCharSequence	CharSequence
    //   16	70	2	Ljava/lang/Object;	Object
    //   29	4	3	localObject1	Object
    //   63	20	3	i	int
    //   77	26	4	j	int
    // Exception table:
    //   from	to	target	type
    //   18	28	29	finally
    //   29	32	29	finally
  }
  
  public boolean equalsIgnoreCase(String paramString)
  {
    return this == paramString;
  }
  
  public int compareTo(String paramString)
  {
    int i = value.length;
    int j = value.length;
    int k = Math.min(i, j);
    char[] arrayOfChar1 = value;
    char[] arrayOfChar2 = value;
    for (int m = 0; m < k; m++)
    {
      int n = arrayOfChar1[m];
      int i1 = arrayOfChar2[m];
      if (n != i1) {
        return n - i1;
      }
    }
    return i - j;
  }
  
  public int compareToIgnoreCase(String paramString)
  {
    return CASE_INSENSITIVE_ORDER.compare(this, paramString);
  }
  
  public boolean regionMatches(int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    char[] arrayOfChar1 = value;
    int i = paramInt1;
    char[] arrayOfChar2 = value;
    int j = paramInt2;
    if ((paramInt2 < 0) || (paramInt1 < 0) || (paramInt1 > value.length - paramInt3) || (paramInt2 > value.length - paramInt3)) {
      return false;
    }
    while (paramInt3-- > 0) {
      if (arrayOfChar1[(i++)] != arrayOfChar2[(j++)]) {
        return false;
      }
    }
    return true;
  }
  
  public boolean regionMatches(boolean paramBoolean, int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    char[] arrayOfChar1 = value;
    int i = paramInt1;
    char[] arrayOfChar2 = value;
    int j = paramInt2;
    if ((paramInt2 < 0) || (paramInt1 < 0) || (paramInt1 > value.length - paramInt3) || (paramInt2 > value.length - paramInt3)) {
      return false;
    }
    while (paramInt3-- > 0)
    {
      char c1 = arrayOfChar1[(i++)];
      char c2 = arrayOfChar2[(j++)];
      if (c1 != c2) {
        if (paramBoolean)
        {
          char c3 = Character.toUpperCase(c1);
          char c4 = Character.toUpperCase(c2);
          if ((c3 == c4) || (Character.toLowerCase(c3) == Character.toLowerCase(c4))) {
            break;
          }
        }
        else
        {
          return false;
        }
      }
    }
    return true;
  }
  
  public boolean startsWith(String paramString, int paramInt)
  {
    char[] arrayOfChar1 = value;
    int i = paramInt;
    char[] arrayOfChar2 = value;
    int j = 0;
    int k = value.length;
    if ((paramInt < 0) || (paramInt > value.length - k)) {
      return false;
    }
    do
    {
      k--;
      if (k < 0) {
        break;
      }
    } while (arrayOfChar1[(i++)] == arrayOfChar2[(j++)]);
    return false;
    return true;
  }
  
  public boolean startsWith(String paramString)
  {
    return startsWith(paramString, 0);
  }
  
  public boolean endsWith(String paramString)
  {
    return startsWith(paramString, value.length - value.length);
  }
  
  public int hashCode()
  {
    int i = hash;
    if ((i == 0) && (value.length > 0))
    {
      char[] arrayOfChar = value;
      for (int j = 0; j < value.length; j++) {
        i = 31 * i + arrayOfChar[j];
      }
      hash = i;
    }
    return i;
  }
  
  public int indexOf(int paramInt)
  {
    return indexOf(paramInt, 0);
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    int i = value.length;
    if (paramInt2 < 0) {
      paramInt2 = 0;
    } else if (paramInt2 >= i) {
      return -1;
    }
    if (paramInt1 < 65536)
    {
      char[] arrayOfChar = value;
      for (int j = paramInt2; j < i; j++) {
        if (arrayOfChar[j] == paramInt1) {
          return j;
        }
      }
      return -1;
    }
    return indexOfSupplementary(paramInt1, paramInt2);
  }
  
  private int indexOfSupplementary(int paramInt1, int paramInt2)
  {
    if (Character.isValidCodePoint(paramInt1))
    {
      char[] arrayOfChar = value;
      int i = Character.highSurrogate(paramInt1);
      int j = Character.lowSurrogate(paramInt1);
      int k = arrayOfChar.length - 1;
      for (int m = paramInt2; m < k; m++) {
        if ((arrayOfChar[m] == i) && (arrayOfChar[(m + 1)] == j)) {
          return m;
        }
      }
    }
    return -1;
  }
  
  public int lastIndexOf(int paramInt)
  {
    return lastIndexOf(paramInt, value.length - 1);
  }
  
  public int lastIndexOf(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 65536)
    {
      char[] arrayOfChar = value;
      for (int i = Math.min(paramInt2, arrayOfChar.length - 1); i >= 0; i--) {
        if (arrayOfChar[i] == paramInt1) {
          return i;
        }
      }
      return -1;
    }
    return lastIndexOfSupplementary(paramInt1, paramInt2);
  }
  
  private int lastIndexOfSupplementary(int paramInt1, int paramInt2)
  {
    if (Character.isValidCodePoint(paramInt1))
    {
      char[] arrayOfChar = value;
      int i = Character.highSurrogate(paramInt1);
      int j = Character.lowSurrogate(paramInt1);
      for (int k = Math.min(paramInt2, arrayOfChar.length - 2); k >= 0; k--) {
        if ((arrayOfChar[k] == i) && (arrayOfChar[(k + 1)] == j)) {
          return k;
        }
      }
    }
    return -1;
  }
  
  public int indexOf(String paramString)
  {
    return indexOf(paramString, 0);
  }
  
  public int indexOf(String paramString, int paramInt)
  {
    return indexOf(value, 0, value.length, value, 0, value.length, paramInt);
  }
  
  static int indexOf(char[] paramArrayOfChar, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    return indexOf(paramArrayOfChar, paramInt1, paramInt2, value, 0, value.length, paramInt3);
  }
  
  static int indexOf(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt5 >= paramInt2) {
      return paramInt4 == 0 ? paramInt2 : -1;
    }
    if (paramInt5 < 0) {
      paramInt5 = 0;
    }
    if (paramInt4 == 0) {
      return paramInt5;
    }
    int i = paramArrayOfChar2[paramInt3];
    int j = paramInt1 + (paramInt2 - paramInt4);
    for (int k = paramInt1 + paramInt5; k <= j; k++)
    {
      if (paramArrayOfChar1[k] != i) {
        do
        {
          k++;
        } while ((k <= j) && (paramArrayOfChar1[k] != i));
      }
      if (k <= j)
      {
        int m = k + 1;
        int n = m + paramInt4 - 1;
        for (int i1 = paramInt3 + 1; (m < n) && (paramArrayOfChar1[m] == paramArrayOfChar2[i1]); i1++) {
          m++;
        }
        if (m == n) {
          return k - paramInt1;
        }
      }
    }
    return -1;
  }
  
  public int lastIndexOf(String paramString)
  {
    return lastIndexOf(paramString, value.length);
  }
  
  public int lastIndexOf(String paramString, int paramInt)
  {
    return lastIndexOf(value, 0, value.length, value, 0, value.length, paramInt);
  }
  
  static int lastIndexOf(char[] paramArrayOfChar, int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    return lastIndexOf(paramArrayOfChar, paramInt1, paramInt2, value, 0, value.length, paramInt3);
  }
  
  static int lastIndexOf(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = paramInt2 - paramInt4;
    if (paramInt5 < 0) {
      return -1;
    }
    if (paramInt5 > i) {
      paramInt5 = i;
    }
    if (paramInt4 == 0) {
      return paramInt5;
    }
    int j = paramInt3 + paramInt4 - 1;
    int k = paramArrayOfChar2[j];
    int m = paramInt1 + paramInt4 - 1;
    int n = m + paramInt5;
    int i2;
    for (;;)
    {
      if ((n >= m) && (paramArrayOfChar1[n] != k))
      {
        n--;
      }
      else
      {
        if (n < m) {
          return -1;
        }
        int i1 = n - 1;
        i2 = i1 - (paramInt4 - 1);
        int i3 = j - 1;
        do
        {
          if (i1 <= i2) {
            break;
          }
        } while (paramArrayOfChar1[(i1--)] == paramArrayOfChar2[(i3--)]);
        n--;
      }
    }
    return i2 - paramInt1 + 1;
  }
  
  public String substring(int paramInt)
  {
    if (paramInt < 0) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    int i = value.length - paramInt;
    if (i < 0) {
      throw new StringIndexOutOfBoundsException(i);
    }
    return paramInt == 0 ? this : new String(value, paramInt, i);
  }
  
  public String substring(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > value.length) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    int i = paramInt2 - paramInt1;
    if (i < 0) {
      throw new StringIndexOutOfBoundsException(i);
    }
    return (paramInt1 == 0) && (paramInt2 == value.length) ? this : new String(value, paramInt1, i);
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return substring(paramInt1, paramInt2);
  }
  
  public String concat(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return this;
    }
    int j = value.length;
    char[] arrayOfChar = Arrays.copyOf(value, j + i);
    paramString.getChars(arrayOfChar, j);
    return new String(arrayOfChar, true);
  }
  
  public String replace(char paramChar1, char paramChar2)
  {
    if (paramChar1 != paramChar2)
    {
      int i = value.length;
      int j = -1;
      char[] arrayOfChar1 = value;
      for (;;)
      {
        j++;
        if (j < i) {
          if (arrayOfChar1[j] == paramChar1) {
            break;
          }
        }
      }
      if (j < i)
      {
        char[] arrayOfChar2 = new char[i];
        for (char c = '\000'; c < j; c++) {
          arrayOfChar2[c] = arrayOfChar1[c];
        }
        while (j < i)
        {
          c = arrayOfChar1[j];
          arrayOfChar2[j] = (c == paramChar1 ? paramChar2 : c);
          j++;
        }
        return new String(arrayOfChar2, true);
      }
    }
    return this;
  }
  
  public boolean matches(String paramString)
  {
    return Pattern.matches(paramString, this);
  }
  
  public boolean contains(CharSequence paramCharSequence)
  {
    return indexOf(paramCharSequence.toString()) > -1;
  }
  
  public String replaceFirst(String paramString1, String paramString2)
  {
    return Pattern.compile(paramString1).matcher(this).replaceFirst(paramString2);
  }
  
  public String replaceAll(String paramString1, String paramString2)
  {
    return Pattern.compile(paramString1).matcher(this).replaceAll(paramString2);
  }
  
  public String replace(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return Pattern.compile(paramCharSequence1.toString(), 16).matcher(this).replaceAll(Matcher.quoteReplacement(paramCharSequence2.toString()));
  }
  
  public String[] split(String paramString, int paramInt)
  {
    int i = 0;
    if (((value.length == 1) && (".$|()[{^?*+\\".indexOf(i = paramString.charAt(0)) == -1)) || ((paramString.length() == 2) && (paramString.charAt(0) == '\\') && (((i = paramString.charAt(1)) - '0' | 57 - i) < 0) && ((i - 97 | 122 - i) < 0) && ((i - 65 | 90 - i) < 0) && ((i < 55296) || (i > 57343))))
    {
      int j = 0;
      int k = 0;
      int m = paramInt > 0 ? 1 : 0;
      ArrayList localArrayList = new ArrayList();
      while ((k = indexOf(i, j)) != -1) {
        if ((m == 0) || (localArrayList.size() < paramInt - 1))
        {
          localArrayList.add(substring(j, k));
          j = k + 1;
        }
        else
        {
          localArrayList.add(substring(j, value.length));
          j = value.length;
        }
      }
      if (j == 0) {
        return new String[] { this };
      }
      if ((m == 0) || (localArrayList.size() < paramInt)) {
        localArrayList.add(substring(j, value.length));
      }
      int n = localArrayList.size();
      if (paramInt == 0) {
        while ((n > 0) && (((String)localArrayList.get(n - 1)).length() == 0)) {
          n--;
        }
      }
      String[] arrayOfString = new String[n];
      return (String[])localArrayList.subList(0, n).toArray(arrayOfString);
    }
    return Pattern.compile(paramString).split(this, paramInt);
  }
  
  public String[] split(String paramString)
  {
    return split(paramString, 0);
  }
  
  public static String join(CharSequence paramCharSequence, CharSequence... paramVarArgs)
  {
    Objects.requireNonNull(paramCharSequence);
    Objects.requireNonNull(paramVarArgs);
    StringJoiner localStringJoiner = new StringJoiner(paramCharSequence);
    for (CharSequence localCharSequence : paramVarArgs) {
      localStringJoiner.add(localCharSequence);
    }
    return localStringJoiner.toString();
  }
  
  public static String join(CharSequence paramCharSequence, Iterable<? extends CharSequence> paramIterable)
  {
    Objects.requireNonNull(paramCharSequence);
    Objects.requireNonNull(paramIterable);
    StringJoiner localStringJoiner = new StringJoiner(paramCharSequence);
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      CharSequence localCharSequence = (CharSequence)localIterator.next();
      localStringJoiner.add(localCharSequence);
    }
    return localStringJoiner.toString();
  }
  
  public String toLowerCase(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    int j = value.length;
    int i = 0;
    while (i < j)
    {
      int k = value[i];
      if ((k >= 55296) && (k <= 56319))
      {
        m = codePointAt(i);
        if (m != Character.toLowerCase(m)) {
          break label99;
        }
        i += Character.charCount(m);
      }
      else
      {
        if (k != Character.toLowerCase(k)) {
          break label99;
        }
        i++;
      }
    }
    return this;
    label99:
    Object localObject = new char[j];
    int m = 0;
    System.arraycopy(value, 0, localObject, 0, i);
    String str = paramLocale.getLanguage();
    int n = (str == "tr") || (str == "az") || (str == "lt") ? 1 : 0;
    int i4 = i;
    while (i4 < j)
    {
      int i2 = value[i4];
      int i3;
      if (((char)i2 >= 55296) && ((char)i2 <= 56319))
      {
        i2 = codePointAt(i4);
        i3 = Character.charCount(i2);
      }
      else
      {
        i3 = 1;
      }
      int i1;
      if ((n != 0) || (i2 == 931) || (i2 == 304)) {
        i1 = ConditionalSpecialCasing.toLowerCaseEx(this, i4, paramLocale);
      } else {
        i1 = Character.toLowerCase(i2);
      }
      if ((i1 == -1) || (i1 >= 65536))
      {
        char[] arrayOfChar1;
        if (i1 == -1)
        {
          arrayOfChar1 = ConditionalSpecialCasing.toLowerCaseCharArray(this, i4, paramLocale);
        }
        else
        {
          if (i3 == 2)
          {
            m += Character.toChars(i1, (char[])localObject, i4 + m) - i3;
            break label414;
          }
          arrayOfChar1 = Character.toChars(i1);
        }
        int i5 = arrayOfChar1.length;
        if (i5 > i3)
        {
          char[] arrayOfChar2 = new char[localObject.length + i5 - i3];
          System.arraycopy(localObject, 0, arrayOfChar2, 0, i4 + m);
          localObject = arrayOfChar2;
        }
        for (int i6 = 0; i6 < i5; i6++) {
          localObject[(i4 + m + i6)] = arrayOfChar1[i6];
        }
        m += i5 - i3;
      }
      else
      {
        localObject[(i4 + m)] = ((char)i1);
      }
      label414:
      i4 += i3;
    }
    return new String((char[])localObject, 0, j + m);
  }
  
  public String toLowerCase()
  {
    return toLowerCase(Locale.getDefault());
  }
  
  public String toUpperCase(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    int j = value.length;
    int i = 0;
    while (i < j)
    {
      k = value[i];
      int m;
      if ((k >= 55296) && (k <= 56319))
      {
        k = codePointAt(i);
        m = Character.charCount(k);
      }
      else
      {
        m = 1;
      }
      int n = Character.toUpperCaseEx(k);
      if ((n == -1) || (k != n)) {
        break label100;
      }
      i += m;
    }
    return this;
    label100:
    int k = 0;
    Object localObject = new char[j];
    System.arraycopy(value, 0, localObject, 0, i);
    String str = paramLocale.getLanguage();
    int i1 = (str == "tr") || (str == "az") || (str == "lt") ? 1 : 0;
    int i5 = i;
    while (i5 < j)
    {
      int i3 = value[i5];
      int i4;
      if (((char)i3 >= 55296) && ((char)i3 <= 56319))
      {
        i3 = codePointAt(i5);
        i4 = Character.charCount(i3);
      }
      else
      {
        i4 = 1;
      }
      int i2;
      if (i1 != 0) {
        i2 = ConditionalSpecialCasing.toUpperCaseEx(this, i5, paramLocale);
      } else {
        i2 = Character.toUpperCaseEx(i3);
      }
      if ((i2 == -1) || (i2 >= 65536))
      {
        char[] arrayOfChar1;
        if (i2 == -1)
        {
          if (i1 != 0) {
            arrayOfChar1 = ConditionalSpecialCasing.toUpperCaseCharArray(this, i5, paramLocale);
          } else {
            arrayOfChar1 = Character.toUpperCaseCharArray(i3);
          }
        }
        else
        {
          if (i4 == 2)
          {
            k += Character.toChars(i2, (char[])localObject, i5 + k) - i4;
            break label414;
          }
          arrayOfChar1 = Character.toChars(i2);
        }
        int i6 = arrayOfChar1.length;
        if (i6 > i4)
        {
          char[] arrayOfChar2 = new char[localObject.length + i6 - i4];
          System.arraycopy(localObject, 0, arrayOfChar2, 0, i5 + k);
          localObject = arrayOfChar2;
        }
        for (int i7 = 0; i7 < i6; i7++) {
          localObject[(i5 + k + i7)] = arrayOfChar1[i7];
        }
        k += i6 - i4;
      }
      else
      {
        localObject[(i5 + k)] = ((char)i2);
      }
      label414:
      i5 += i4;
    }
    return new String((char[])localObject, 0, j + k);
  }
  
  public String toUpperCase()
  {
    return toUpperCase(Locale.getDefault());
  }
  
  public String trim()
  {
    int i = value.length;
    int j = 0;
    char[] arrayOfChar = value;
    while ((j < i) && (arrayOfChar[j] <= ' ')) {
      j++;
    }
    while ((j < i) && (arrayOfChar[(i - 1)] <= ' ')) {
      i--;
    }
    return (j > 0) || (i < value.length) ? substring(j, i) : this;
  }
  
  public String toString()
  {
    return this;
  }
  
  public char[] toCharArray()
  {
    char[] arrayOfChar = new char[value.length];
    System.arraycopy(value, 0, arrayOfChar, 0, value.length);
    return arrayOfChar;
  }
  
  public static String format(String paramString, Object... paramVarArgs)
  {
    return new Formatter().format(paramString, paramVarArgs).toString();
  }
  
  public static String format(Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    return new Formatter(paramLocale).format(paramString, paramVarArgs).toString();
  }
  
  public static String valueOf(Object paramObject)
  {
    return paramObject == null ? "null" : paramObject.toString();
  }
  
  public static String valueOf(char[] paramArrayOfChar)
  {
    return new String(paramArrayOfChar);
  }
  
  public static String valueOf(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return new String(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public static String copyValueOf(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return new String(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public static String copyValueOf(char[] paramArrayOfChar)
  {
    return new String(paramArrayOfChar);
  }
  
  public static String valueOf(boolean paramBoolean)
  {
    return paramBoolean ? "true" : "false";
  }
  
  public static String valueOf(char paramChar)
  {
    char[] arrayOfChar = { paramChar };
    return new String(arrayOfChar, true);
  }
  
  public static String valueOf(int paramInt)
  {
    return Integer.toString(paramInt);
  }
  
  public static String valueOf(long paramLong)
  {
    return Long.toString(paramLong);
  }
  
  public static String valueOf(float paramFloat)
  {
    return Float.toString(paramFloat);
  }
  
  public static String valueOf(double paramDouble)
  {
    return Double.toString(paramDouble);
  }
  
  public native String intern();
  
  private static class CaseInsensitiveComparator
    implements Comparator<String>, Serializable
  {
    private static final long serialVersionUID = 8575799808933029326L;
    
    private CaseInsensitiveComparator() {}
    
    public int compare(String paramString1, String paramString2)
    {
      int i = paramString1.length();
      int j = paramString2.length();
      int k = Math.min(i, j);
      for (int m = 0; m < k; m++)
      {
        char c1 = paramString1.charAt(m);
        char c2 = paramString2.charAt(m);
        if (c1 != c2)
        {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2)
          {
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);
            if (c1 != c2) {
              return c1 - c2;
            }
          }
        }
      }
      return i - j;
    }
    
    private Object readResolve()
    {
      return String.CASE_INSENSITIVE_ORDER;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\String.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
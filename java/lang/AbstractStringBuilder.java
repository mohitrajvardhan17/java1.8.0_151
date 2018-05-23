package java.lang;

import java.util.Arrays;
import sun.misc.FloatingDecimal;

abstract class AbstractStringBuilder
  implements Appendable, CharSequence
{
  char[] value;
  int count;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  AbstractStringBuilder() {}
  
  AbstractStringBuilder(int paramInt)
  {
    value = new char[paramInt];
  }
  
  public int length()
  {
    return count;
  }
  
  public int capacity()
  {
    return value.length;
  }
  
  public void ensureCapacity(int paramInt)
  {
    if (paramInt > 0) {
      ensureCapacityInternal(paramInt);
    }
  }
  
  private void ensureCapacityInternal(int paramInt)
  {
    if (paramInt - value.length > 0) {
      value = Arrays.copyOf(value, newCapacity(paramInt));
    }
  }
  
  private int newCapacity(int paramInt)
  {
    int i = (value.length << 1) + 2;
    if (i - paramInt < 0) {
      i = paramInt;
    }
    return (i <= 0) || (2147483639 - i < 0) ? hugeCapacity(paramInt) : i;
  }
  
  private int hugeCapacity(int paramInt)
  {
    if (Integer.MAX_VALUE - paramInt < 0) {
      throw new OutOfMemoryError();
    }
    return paramInt > 2147483639 ? paramInt : 2147483639;
  }
  
  public void trimToSize()
  {
    if (count < value.length) {
      value = Arrays.copyOf(value, count);
    }
  }
  
  public void setLength(int paramInt)
  {
    if (paramInt < 0) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    ensureCapacityInternal(paramInt);
    if (count < paramInt) {
      Arrays.fill(value, count, paramInt, '\000');
    }
    count = paramInt;
  }
  
  public char charAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return value[paramInt];
  }
  
  public int codePointAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return Character.codePointAtImpl(value, paramInt, count);
  }
  
  public int codePointBefore(int paramInt)
  {
    int i = paramInt - 1;
    if ((i < 0) || (i >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return Character.codePointBeforeImpl(value, paramInt, 0);
  }
  
  public int codePointCount(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 > count) || (paramInt1 > paramInt2)) {
      throw new IndexOutOfBoundsException();
    }
    return Character.codePointCountImpl(value, paramInt1, paramInt2 - paramInt1);
  }
  
  public int offsetByCodePoints(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > count)) {
      throw new IndexOutOfBoundsException();
    }
    return Character.offsetByCodePointsImpl(value, 0, count, paramInt1, paramInt2);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt2 > count)) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
    }
    System.arraycopy(value, paramInt1, paramArrayOfChar, paramInt3, paramInt2 - paramInt1);
  }
  
  public void setCharAt(int paramInt, char paramChar)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    value[paramInt] = paramChar;
  }
  
  public AbstractStringBuilder append(Object paramObject)
  {
    return append(String.valueOf(paramObject));
  }
  
  public AbstractStringBuilder append(String paramString)
  {
    if (paramString == null) {
      return appendNull();
    }
    int i = paramString.length();
    ensureCapacityInternal(count + i);
    paramString.getChars(0, i, value, count);
    count += i;
    return this;
  }
  
  public AbstractStringBuilder append(StringBuffer paramStringBuffer)
  {
    if (paramStringBuffer == null) {
      return appendNull();
    }
    int i = paramStringBuffer.length();
    ensureCapacityInternal(count + i);
    paramStringBuffer.getChars(0, i, value, count);
    count += i;
    return this;
  }
  
  AbstractStringBuilder append(AbstractStringBuilder paramAbstractStringBuilder)
  {
    if (paramAbstractStringBuilder == null) {
      return appendNull();
    }
    int i = paramAbstractStringBuilder.length();
    ensureCapacityInternal(count + i);
    paramAbstractStringBuilder.getChars(0, i, value, count);
    count += i;
    return this;
  }
  
  public AbstractStringBuilder append(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return appendNull();
    }
    if ((paramCharSequence instanceof String)) {
      return append((String)paramCharSequence);
    }
    if ((paramCharSequence instanceof AbstractStringBuilder)) {
      return append((AbstractStringBuilder)paramCharSequence);
    }
    return append(paramCharSequence, 0, paramCharSequence.length());
  }
  
  private AbstractStringBuilder appendNull()
  {
    int i = count;
    ensureCapacityInternal(i + 4);
    char[] arrayOfChar = value;
    arrayOfChar[(i++)] = 'n';
    arrayOfChar[(i++)] = 'u';
    arrayOfChar[(i++)] = 'l';
    arrayOfChar[(i++)] = 'l';
    count = i;
    return this;
  }
  
  public AbstractStringBuilder append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    if (paramCharSequence == null) {
      paramCharSequence = "null";
    }
    if ((paramInt1 < 0) || (paramInt1 > paramInt2) || (paramInt2 > paramCharSequence.length())) {
      throw new IndexOutOfBoundsException("start " + paramInt1 + ", end " + paramInt2 + ", s.length() " + paramCharSequence.length());
    }
    int i = paramInt2 - paramInt1;
    ensureCapacityInternal(count + i);
    int j = paramInt1;
    for (int k = count; j < paramInt2; k++)
    {
      value[k] = paramCharSequence.charAt(j);
      j++;
    }
    count += i;
    return this;
  }
  
  public AbstractStringBuilder append(char[] paramArrayOfChar)
  {
    int i = paramArrayOfChar.length;
    ensureCapacityInternal(count + i);
    System.arraycopy(paramArrayOfChar, 0, value, count, i);
    count += i;
    return this;
  }
  
  public AbstractStringBuilder append(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 0) {
      ensureCapacityInternal(count + paramInt2);
    }
    System.arraycopy(paramArrayOfChar, paramInt1, value, count, paramInt2);
    count += paramInt2;
    return this;
  }
  
  public AbstractStringBuilder append(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      ensureCapacityInternal(count + 4);
      value[(count++)] = 't';
      value[(count++)] = 'r';
      value[(count++)] = 'u';
      value[(count++)] = 'e';
    }
    else
    {
      ensureCapacityInternal(count + 5);
      value[(count++)] = 'f';
      value[(count++)] = 'a';
      value[(count++)] = 'l';
      value[(count++)] = 's';
      value[(count++)] = 'e';
    }
    return this;
  }
  
  public AbstractStringBuilder append(char paramChar)
  {
    ensureCapacityInternal(count + 1);
    value[(count++)] = paramChar;
    return this;
  }
  
  public AbstractStringBuilder append(int paramInt)
  {
    if (paramInt == Integer.MIN_VALUE)
    {
      append("-2147483648");
      return this;
    }
    int i = paramInt < 0 ? Integer.stringSize(-paramInt) + 1 : Integer.stringSize(paramInt);
    int j = count + i;
    ensureCapacityInternal(j);
    Integer.getChars(paramInt, j, value);
    count = j;
    return this;
  }
  
  public AbstractStringBuilder append(long paramLong)
  {
    if (paramLong == Long.MIN_VALUE)
    {
      append("-9223372036854775808");
      return this;
    }
    int i = paramLong < 0L ? Long.stringSize(-paramLong) + 1 : Long.stringSize(paramLong);
    int j = count + i;
    ensureCapacityInternal(j);
    Long.getChars(paramLong, j, value);
    count = j;
    return this;
  }
  
  public AbstractStringBuilder append(float paramFloat)
  {
    FloatingDecimal.appendTo(paramFloat, this);
    return this;
  }
  
  public AbstractStringBuilder append(double paramDouble)
  {
    FloatingDecimal.appendTo(paramDouble, this);
    return this;
  }
  
  public AbstractStringBuilder delete(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > count) {
      paramInt2 = count;
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException();
    }
    int i = paramInt2 - paramInt1;
    if (i > 0)
    {
      System.arraycopy(value, paramInt1 + i, value, paramInt1, count - paramInt2);
      count -= i;
    }
    return this;
  }
  
  public AbstractStringBuilder appendCodePoint(int paramInt)
  {
    int i = count;
    if (Character.isBmpCodePoint(paramInt))
    {
      ensureCapacityInternal(i + 1);
      value[i] = ((char)paramInt);
      count = (i + 1);
    }
    else if (Character.isValidCodePoint(paramInt))
    {
      ensureCapacityInternal(i + 2);
      Character.toSurrogates(paramInt, value, i);
      count = (i + 2);
    }
    else
    {
      throw new IllegalArgumentException();
    }
    return this;
  }
  
  public AbstractStringBuilder deleteCharAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    System.arraycopy(value, paramInt + 1, value, paramInt, count - paramInt - 1);
    count -= 1;
    return this;
  }
  
  public AbstractStringBuilder replace(int paramInt1, int paramInt2, String paramString)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt1 > count) {
      throw new StringIndexOutOfBoundsException("start > length()");
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException("start > end");
    }
    if (paramInt2 > count) {
      paramInt2 = count;
    }
    int i = paramString.length();
    int j = count + i - (paramInt2 - paramInt1);
    ensureCapacityInternal(j);
    System.arraycopy(value, paramInt2, value, paramInt1 + i, count - paramInt2);
    paramString.getChars(value, paramInt1);
    count = j;
    return this;
  }
  
  public String substring(int paramInt)
  {
    return substring(paramInt, count);
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return substring(paramInt1, paramInt2);
  }
  
  public String substring(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt2 > count) {
      throw new StringIndexOutOfBoundsException(paramInt2);
    }
    if (paramInt1 > paramInt2) {
      throw new StringIndexOutOfBoundsException(paramInt2 - paramInt1);
    }
    return new String(value, paramInt1, paramInt2 - paramInt1);
  }
  
  public AbstractStringBuilder insert(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > length())) {
      throw new StringIndexOutOfBoundsException(paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt3 < 0) || (paramInt2 > paramArrayOfChar.length - paramInt3)) {
      throw new StringIndexOutOfBoundsException("offset " + paramInt2 + ", len " + paramInt3 + ", str.length " + paramArrayOfChar.length);
    }
    ensureCapacityInternal(count + paramInt3);
    System.arraycopy(value, paramInt1, value, paramInt1 + paramInt3, count - paramInt1);
    System.arraycopy(paramArrayOfChar, paramInt2, value, paramInt1, paramInt3);
    count += paramInt3;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, Object paramObject)
  {
    return insert(paramInt, String.valueOf(paramObject));
  }
  
  public AbstractStringBuilder insert(int paramInt, String paramString)
  {
    if ((paramInt < 0) || (paramInt > length())) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    if (paramString == null) {
      paramString = "null";
    }
    int i = paramString.length();
    ensureCapacityInternal(count + i);
    System.arraycopy(value, paramInt, value, paramInt + i, count - paramInt);
    paramString.getChars(value, paramInt);
    count += i;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, char[] paramArrayOfChar)
  {
    if ((paramInt < 0) || (paramInt > length())) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    int i = paramArrayOfChar.length;
    ensureCapacityInternal(count + i);
    System.arraycopy(value, paramInt, value, paramInt + i, count - paramInt);
    System.arraycopy(paramArrayOfChar, 0, value, paramInt, i);
    count += i;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      paramCharSequence = "null";
    }
    if ((paramCharSequence instanceof String)) {
      return insert(paramInt, (String)paramCharSequence);
    }
    return insert(paramInt, paramCharSequence, 0, paramCharSequence.length());
  }
  
  public AbstractStringBuilder insert(int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3)
  {
    if (paramCharSequence == null) {
      paramCharSequence = "null";
    }
    if ((paramInt1 < 0) || (paramInt1 > length())) {
      throw new IndexOutOfBoundsException("dstOffset " + paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt3 < 0) || (paramInt2 > paramInt3) || (paramInt3 > paramCharSequence.length())) {
      throw new IndexOutOfBoundsException("start " + paramInt2 + ", end " + paramInt3 + ", s.length() " + paramCharSequence.length());
    }
    int i = paramInt3 - paramInt2;
    ensureCapacityInternal(count + i);
    System.arraycopy(value, paramInt1, value, paramInt1 + i, count - paramInt1);
    for (int j = paramInt2; j < paramInt3; j++) {
      value[(paramInt1++)] = paramCharSequence.charAt(j);
    }
    count += i;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt, boolean paramBoolean)
  {
    return insert(paramInt, String.valueOf(paramBoolean));
  }
  
  public AbstractStringBuilder insert(int paramInt, char paramChar)
  {
    ensureCapacityInternal(count + 1);
    System.arraycopy(value, paramInt, value, paramInt + 1, count - paramInt);
    value[paramInt] = paramChar;
    count += 1;
    return this;
  }
  
  public AbstractStringBuilder insert(int paramInt1, int paramInt2)
  {
    return insert(paramInt1, String.valueOf(paramInt2));
  }
  
  public AbstractStringBuilder insert(int paramInt, long paramLong)
  {
    return insert(paramInt, String.valueOf(paramLong));
  }
  
  public AbstractStringBuilder insert(int paramInt, float paramFloat)
  {
    return insert(paramInt, String.valueOf(paramFloat));
  }
  
  public AbstractStringBuilder insert(int paramInt, double paramDouble)
  {
    return insert(paramInt, String.valueOf(paramDouble));
  }
  
  public int indexOf(String paramString)
  {
    return indexOf(paramString, 0);
  }
  
  public int indexOf(String paramString, int paramInt)
  {
    return String.indexOf(value, 0, count, paramString, paramInt);
  }
  
  public int lastIndexOf(String paramString)
  {
    return lastIndexOf(paramString, count);
  }
  
  public int lastIndexOf(String paramString, int paramInt)
  {
    return String.lastIndexOf(value, 0, count, paramString, paramInt);
  }
  
  public AbstractStringBuilder reverse()
  {
    int i = 0;
    int j = count - 1;
    for (int k = j - 1 >> 1; k >= 0; k--)
    {
      int m = j - k;
      char c1 = value[k];
      char c2 = value[m];
      value[k] = c2;
      value[m] = c1;
      if ((Character.isSurrogate(c1)) || (Character.isSurrogate(c2))) {
        i = 1;
      }
    }
    if (i != 0) {
      reverseAllValidSurrogatePairs();
    }
    return this;
  }
  
  private void reverseAllValidSurrogatePairs()
  {
    for (int i = 0; i < count - 1; i++)
    {
      char c1 = value[i];
      if (Character.isLowSurrogate(c1))
      {
        char c2 = value[(i + 1)];
        if (Character.isHighSurrogate(c2))
        {
          value[(i++)] = c2;
          value[i] = c1;
        }
      }
    }
  }
  
  public abstract String toString();
  
  final char[] getValue()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\AbstractStringBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.sql;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class Timestamp
  extends Date
{
  private int nanos;
  static final long serialVersionUID = 2745179027874758501L;
  private static final int MILLIS_PER_SECOND = 1000;
  
  @Deprecated
  public Timestamp(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    if ((paramInt7 > 999999999) || (paramInt7 < 0)) {
      throw new IllegalArgumentException("nanos > 999999999 or < 0");
    }
    nanos = paramInt7;
  }
  
  public Timestamp(long paramLong)
  {
    super(paramLong / 1000L * 1000L);
    nanos = ((int)(paramLong % 1000L * 1000000L));
    if (nanos < 0)
    {
      nanos = (1000000000 + nanos);
      super.setTime((paramLong / 1000L - 1L) * 1000L);
    }
  }
  
  public void setTime(long paramLong)
  {
    super.setTime(paramLong / 1000L * 1000L);
    nanos = ((int)(paramLong % 1000L * 1000000L));
    if (nanos < 0)
    {
      nanos = (1000000000 + nanos);
      super.setTime((paramLong / 1000L - 1L) * 1000L);
    }
  }
  
  public long getTime()
  {
    long l = super.getTime();
    return l + nanos / 1000000;
  }
  
  public static Timestamp valueOf(String paramString)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int i2 = 0;
    int i6 = 0;
    int i7 = 0;
    int i8 = 0;
    String str4 = "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]";
    String str5 = "000000000";
    String str6 = "-";
    String str7 = ":";
    if (paramString == null) {
      throw new IllegalArgumentException("null string");
    }
    paramString = paramString.trim();
    int i5 = paramString.indexOf(' ');
    String str1;
    String str2;
    if (i5 > 0)
    {
      str1 = paramString.substring(0, i5);
      str2 = paramString.substring(i5 + 1);
    }
    else
    {
      throw new IllegalArgumentException(str4);
    }
    int i3 = str1.indexOf('-');
    int i4 = str1.indexOf('-', i3 + 1);
    if (str2 == null) {
      throw new IllegalArgumentException(str4);
    }
    i6 = str2.indexOf(':');
    i7 = str2.indexOf(':', i6 + 1);
    i8 = str2.indexOf('.', i7 + 1);
    int i9 = 0;
    if ((i3 > 0) && (i4 > 0) && (i4 < str1.length() - 1))
    {
      String str8 = str1.substring(0, i3);
      String str9 = str1.substring(i3 + 1, i4);
      String str10 = str1.substring(i4 + 1);
      if ((str8.length() == 4) && (str9.length() >= 1) && (str9.length() <= 2) && (str10.length() >= 1) && (str10.length() <= 2))
      {
        i = Integer.parseInt(str8);
        j = Integer.parseInt(str9);
        k = Integer.parseInt(str10);
        if ((j >= 1) && (j <= 12) && (k >= 1) && (k <= 31)) {
          i9 = 1;
        }
      }
    }
    if (i9 == 0) {
      throw new IllegalArgumentException(str4);
    }
    int m;
    int n;
    int i1;
    if (((i6 > 0 ? 1 : 0) & (i7 > 0 ? 1 : 0) & (i7 < str2.length() - 1 ? 1 : 0)) != 0)
    {
      m = Integer.parseInt(str2.substring(0, i6));
      n = Integer.parseInt(str2.substring(i6 + 1, i7));
      if (((i8 > 0 ? 1 : 0) & (i8 < str2.length() - 1 ? 1 : 0)) != 0)
      {
        i1 = Integer.parseInt(str2.substring(i7 + 1, i8));
        String str3 = str2.substring(i8 + 1);
        if (str3.length() > 9) {
          throw new IllegalArgumentException(str4);
        }
        if (!Character.isDigit(str3.charAt(0))) {
          throw new IllegalArgumentException(str4);
        }
        str3 = str3 + str5.substring(0, 9 - str3.length());
        i2 = Integer.parseInt(str3);
      }
      else
      {
        if (i8 > 0) {
          throw new IllegalArgumentException(str4);
        }
        i1 = Integer.parseInt(str2.substring(i7 + 1));
      }
    }
    else
    {
      throw new IllegalArgumentException(str4);
    }
    return new Timestamp(i - 1900, j - 1, k, m, n, i1, i2);
  }
  
  public String toString()
  {
    int i = super.getYear() + 1900;
    int j = super.getMonth() + 1;
    int k = super.getDate();
    int m = super.getHours();
    int n = super.getMinutes();
    int i1 = super.getSeconds();
    String str8 = "000000000";
    String str9 = "0000";
    String str1;
    if (i < 1000)
    {
      str1 = "" + i;
      str1 = str9.substring(0, 4 - str1.length()) + str1;
    }
    else
    {
      str1 = "" + i;
    }
    String str2;
    if (j < 10) {
      str2 = "0" + j;
    } else {
      str2 = Integer.toString(j);
    }
    String str3;
    if (k < 10) {
      str3 = "0" + k;
    } else {
      str3 = Integer.toString(k);
    }
    String str4;
    if (m < 10) {
      str4 = "0" + m;
    } else {
      str4 = Integer.toString(m);
    }
    String str5;
    if (n < 10) {
      str5 = "0" + n;
    } else {
      str5 = Integer.toString(n);
    }
    String str6;
    if (i1 < 10) {
      str6 = "0" + i1;
    } else {
      str6 = Integer.toString(i1);
    }
    String str7;
    if (nanos == 0)
    {
      str7 = "0";
    }
    else
    {
      str7 = Integer.toString(nanos);
      str7 = str8.substring(0, 9 - str7.length()) + str7;
      char[] arrayOfChar = new char[str7.length()];
      str7.getChars(0, str7.length(), arrayOfChar, 0);
      for (int i2 = 8; arrayOfChar[i2] == '0'; i2--) {}
      str7 = new String(arrayOfChar, 0, i2 + 1);
    }
    StringBuffer localStringBuffer = new StringBuffer(20 + str7.length());
    localStringBuffer.append(str1);
    localStringBuffer.append("-");
    localStringBuffer.append(str2);
    localStringBuffer.append("-");
    localStringBuffer.append(str3);
    localStringBuffer.append(" ");
    localStringBuffer.append(str4);
    localStringBuffer.append(":");
    localStringBuffer.append(str5);
    localStringBuffer.append(":");
    localStringBuffer.append(str6);
    localStringBuffer.append(".");
    localStringBuffer.append(str7);
    return localStringBuffer.toString();
  }
  
  public int getNanos()
  {
    return nanos;
  }
  
  public void setNanos(int paramInt)
  {
    if ((paramInt > 999999999) || (paramInt < 0)) {
      throw new IllegalArgumentException("nanos > 999999999 or < 0");
    }
    nanos = paramInt;
  }
  
  public boolean equals(Timestamp paramTimestamp)
  {
    if (super.equals(paramTimestamp)) {
      return nanos == nanos;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Timestamp)) {
      return equals((Timestamp)paramObject);
    }
    return false;
  }
  
  public boolean before(Timestamp paramTimestamp)
  {
    return compareTo(paramTimestamp) < 0;
  }
  
  public boolean after(Timestamp paramTimestamp)
  {
    return compareTo(paramTimestamp) > 0;
  }
  
  public int compareTo(Timestamp paramTimestamp)
  {
    long l1 = getTime();
    long l2 = paramTimestamp.getTime();
    int i = l1 == l2 ? 0 : l1 < l2 ? -1 : 1;
    if (i == 0)
    {
      if (nanos > nanos) {
        return 1;
      }
      if (nanos < nanos) {
        return -1;
      }
    }
    return i;
  }
  
  public int compareTo(Date paramDate)
  {
    if ((paramDate instanceof Timestamp)) {
      return compareTo((Timestamp)paramDate);
    }
    Timestamp localTimestamp = new Timestamp(paramDate.getTime());
    return compareTo(localTimestamp);
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public static Timestamp valueOf(LocalDateTime paramLocalDateTime)
  {
    return new Timestamp(paramLocalDateTime.getYear() - 1900, paramLocalDateTime.getMonthValue() - 1, paramLocalDateTime.getDayOfMonth(), paramLocalDateTime.getHour(), paramLocalDateTime.getMinute(), paramLocalDateTime.getSecond(), paramLocalDateTime.getNano());
  }
  
  public LocalDateTime toLocalDateTime()
  {
    return LocalDateTime.of(getYear() + 1900, getMonth() + 1, getDate(), getHours(), getMinutes(), getSeconds(), getNanos());
  }
  
  public static Timestamp from(Instant paramInstant)
  {
    try
    {
      Timestamp localTimestamp = new Timestamp(paramInstant.getEpochSecond() * 1000L);
      nanos = paramInstant.getNano();
      return localTimestamp;
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new IllegalArgumentException(localArithmeticException);
    }
  }
  
  public Instant toInstant()
  {
    return Instant.ofEpochSecond(super.getTime() / 1000L, nanos);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\Timestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
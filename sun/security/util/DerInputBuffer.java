package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.Gregorian;

class DerInputBuffer
  extends ByteArrayInputStream
  implements Cloneable
{
  boolean allowBER = true;
  
  DerInputBuffer(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, true);
  }
  
  DerInputBuffer(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    super(paramArrayOfByte);
    allowBER = paramBoolean;
  }
  
  DerInputBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramArrayOfByte, paramInt1, paramInt2);
    allowBER = paramBoolean;
  }
  
  DerInputBuffer dup()
  {
    try
    {
      DerInputBuffer localDerInputBuffer = (DerInputBuffer)clone();
      localDerInputBuffer.mark(Integer.MAX_VALUE);
      return localDerInputBuffer;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new IllegalArgumentException(localCloneNotSupportedException.toString());
    }
  }
  
  byte[] toByteArray()
  {
    int i = available();
    if (i <= 0) {
      return null;
    }
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(buf, pos, arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  int peek()
    throws IOException
  {
    if (pos >= count) {
      throw new IOException("out of data");
    }
    return buf[pos];
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DerInputBuffer)) {
      return equals((DerInputBuffer)paramObject);
    }
    return false;
  }
  
  boolean equals(DerInputBuffer paramDerInputBuffer)
  {
    if (this == paramDerInputBuffer) {
      return true;
    }
    int i = available();
    if (paramDerInputBuffer.available() != i) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (buf[(pos + j)] != buf[(pos + j)]) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    int j = available();
    int k = pos;
    for (int m = 0; m < j; m++) {
      i += buf[(k + m)] * m;
    }
    return i;
  }
  
  void truncate(int paramInt)
    throws IOException
  {
    if (paramInt > available()) {
      throw new IOException("insufficient data");
    }
    count = (pos + paramInt);
  }
  
  BigInteger getBigInteger(int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (paramInt > available()) {
      throw new IOException("short read of integer");
    }
    if (paramInt == 0) {
      throw new IOException("Invalid encoding: zero length Int value");
    }
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(buf, pos, arrayOfByte, 0, paramInt);
    skip(paramInt);
    if ((!allowBER) && (paramInt >= 2) && (arrayOfByte[0] == 0) && (arrayOfByte[1] >= 0)) {
      throw new IOException("Invalid encoding: redundant leading 0s");
    }
    if (paramBoolean) {
      return new BigInteger(1, arrayOfByte);
    }
    return new BigInteger(arrayOfByte);
  }
  
  public int getInteger(int paramInt)
    throws IOException
  {
    BigInteger localBigInteger = getBigInteger(paramInt, false);
    if (localBigInteger.compareTo(BigInteger.valueOf(-2147483648L)) < 0) {
      throw new IOException("Integer below minimum valid value");
    }
    if (localBigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0) {
      throw new IOException("Integer exceeds maximum valid value");
    }
    return localBigInteger.intValue();
  }
  
  public byte[] getBitString(int paramInt)
    throws IOException
  {
    if (paramInt > available()) {
      throw new IOException("short read of bit string");
    }
    if (paramInt == 0) {
      throw new IOException("Invalid encoding: zero length bit string");
    }
    int i = buf[pos];
    if ((i < 0) || (i > 7)) {
      throw new IOException("Invalid number of padding bits");
    }
    byte[] arrayOfByte = new byte[paramInt - 1];
    System.arraycopy(buf, pos + 1, arrayOfByte, 0, paramInt - 1);
    if (i != 0)
    {
      int tmp94_93 = (paramInt - 2);
      byte[] tmp94_90 = arrayOfByte;
      tmp94_90[tmp94_93] = ((byte)(tmp94_90[tmp94_93] & 255 << i));
    }
    skip(paramInt);
    return arrayOfByte;
  }
  
  byte[] getBitString()
    throws IOException
  {
    return getBitString(available());
  }
  
  BitArray getUnalignedBitString()
    throws IOException
  {
    if (pos >= count) {
      return null;
    }
    int i = available();
    int j = buf[pos] & 0xFF;
    if (j > 7) {
      throw new IOException("Invalid value for unused bits: " + j);
    }
    byte[] arrayOfByte = new byte[i - 1];
    int k = arrayOfByte.length == 0 ? 0 : arrayOfByte.length * 8 - j;
    System.arraycopy(buf, pos + 1, arrayOfByte, 0, i - 1);
    BitArray localBitArray = new BitArray(k, arrayOfByte);
    pos = count;
    return localBitArray;
  }
  
  public Date getUTCTime(int paramInt)
    throws IOException
  {
    if (paramInt > available()) {
      throw new IOException("short read of DER UTC Time");
    }
    if ((paramInt < 11) || (paramInt > 17)) {
      throw new IOException("DER UTC Time length error");
    }
    return getTime(paramInt, false);
  }
  
  public Date getGeneralizedTime(int paramInt)
    throws IOException
  {
    if (paramInt > available()) {
      throw new IOException("short read of DER Generalized Time");
    }
    if ((paramInt < 13) || (paramInt > 23)) {
      throw new IOException("DER Generalized Time length error");
    }
    return getTime(paramInt, true);
  }
  
  private Date getTime(int paramInt, boolean paramBoolean)
    throws IOException
  {
    String str = null;
    int i;
    if (paramBoolean)
    {
      str = "Generalized";
      i = 1000 * Character.digit((char)buf[(pos++)], 10);
      i += 100 * Character.digit((char)buf[(pos++)], 10);
      i += 10 * Character.digit((char)buf[(pos++)], 10);
      i += Character.digit((char)buf[(pos++)], 10);
      paramInt -= 2;
    }
    else
    {
      str = "UTC";
      i = 10 * Character.digit((char)buf[(pos++)], 10);
      i += Character.digit((char)buf[(pos++)], 10);
      if (i < 50) {
        i += 2000;
      } else {
        i += 1900;
      }
    }
    int j = 10 * Character.digit((char)buf[(pos++)], 10);
    j += Character.digit((char)buf[(pos++)], 10);
    int k = 10 * Character.digit((char)buf[(pos++)], 10);
    k += Character.digit((char)buf[(pos++)], 10);
    int m = 10 * Character.digit((char)buf[(pos++)], 10);
    m += Character.digit((char)buf[(pos++)], 10);
    int n = 10 * Character.digit((char)buf[(pos++)], 10);
    n += Character.digit((char)buf[(pos++)], 10);
    paramInt -= 10;
    int i2 = 0;
    int i1;
    if ((paramInt > 2) && (paramInt < 12))
    {
      i1 = 10 * Character.digit((char)buf[(pos++)], 10);
      i1 += Character.digit((char)buf[(pos++)], 10);
      paramInt -= 2;
      if ((buf[pos] == 46) || (buf[pos] == 44))
      {
        paramInt--;
        pos += 1;
        int i3 = 0;
        int i4 = pos;
        while ((buf[i4] != 90) && (buf[i4] != 43) && (buf[i4] != 45))
        {
          i4++;
          i3++;
        }
        switch (i3)
        {
        case 3: 
          i2 += 100 * Character.digit((char)buf[(pos++)], 10);
          i2 += 10 * Character.digit((char)buf[(pos++)], 10);
          i2 += Character.digit((char)buf[(pos++)], 10);
          break;
        case 2: 
          i2 += 100 * Character.digit((char)buf[(pos++)], 10);
          i2 += 10 * Character.digit((char)buf[(pos++)], 10);
          break;
        case 1: 
          i2 += 100 * Character.digit((char)buf[(pos++)], 10);
          break;
        default: 
          throw new IOException("Parse " + str + " time, unsupported precision for seconds value");
        }
        paramInt -= i3;
      }
    }
    else
    {
      i1 = 0;
    }
    if ((j == 0) || (k == 0) || (j > 12) || (k > 31) || (m >= 24) || (n >= 60) || (i1 >= 60)) {
      throw new IOException("Parse " + str + " time, invalid format");
    }
    Gregorian localGregorian = CalendarSystem.getGregorianCalendar();
    CalendarDate localCalendarDate = localGregorian.newCalendarDate(null);
    localCalendarDate.setDate(i, j, k);
    localCalendarDate.setTimeOfDay(m, n, i1, i2);
    long l = localGregorian.getTime(localCalendarDate);
    if ((paramInt != 1) && (paramInt != 5)) {
      throw new IOException("Parse " + str + " time, invalid offset");
    }
    int i5;
    int i6;
    switch (buf[(pos++)])
    {
    case 43: 
      i5 = 10 * Character.digit((char)buf[(pos++)], 10);
      i5 += Character.digit((char)buf[(pos++)], 10);
      i6 = 10 * Character.digit((char)buf[(pos++)], 10);
      i6 += Character.digit((char)buf[(pos++)], 10);
      if ((i5 >= 24) || (i6 >= 60)) {
        throw new IOException("Parse " + str + " time, +hhmm");
      }
      l -= (i5 * 60 + i6) * 60 * 1000;
      break;
    case 45: 
      i5 = 10 * Character.digit((char)buf[(pos++)], 10);
      i5 += Character.digit((char)buf[(pos++)], 10);
      i6 = 10 * Character.digit((char)buf[(pos++)], 10);
      i6 += Character.digit((char)buf[(pos++)], 10);
      if ((i5 >= 24) || (i6 >= 60)) {
        throw new IOException("Parse " + str + " time, -hhmm");
      }
      l += (i5 * 60 + i6) * 60 * 1000;
      break;
    case 90: 
      break;
    default: 
      throw new IOException("Parse " + str + " time, garbage offset");
    }
    return new Date(l);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\DerInputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
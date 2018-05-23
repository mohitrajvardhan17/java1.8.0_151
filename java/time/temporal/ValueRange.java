package java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;

public final class ValueRange
  implements Serializable
{
  private static final long serialVersionUID = -7317881728594519368L;
  private final long minSmallest;
  private final long minLargest;
  private final long maxSmallest;
  private final long maxLargest;
  
  public static ValueRange of(long paramLong1, long paramLong2)
  {
    if (paramLong1 > paramLong2) {
      throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }
    return new ValueRange(paramLong1, paramLong1, paramLong2, paramLong2);
  }
  
  public static ValueRange of(long paramLong1, long paramLong2, long paramLong3)
  {
    return of(paramLong1, paramLong1, paramLong2, paramLong3);
  }
  
  public static ValueRange of(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    if (paramLong1 > paramLong2) {
      throw new IllegalArgumentException("Smallest minimum value must be less than largest minimum value");
    }
    if (paramLong3 > paramLong4) {
      throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
    }
    if (paramLong2 > paramLong4) {
      throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }
    return new ValueRange(paramLong1, paramLong2, paramLong3, paramLong4);
  }
  
  private ValueRange(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    minSmallest = paramLong1;
    minLargest = paramLong2;
    maxSmallest = paramLong3;
    maxLargest = paramLong4;
  }
  
  public boolean isFixed()
  {
    return (minSmallest == minLargest) && (maxSmallest == maxLargest);
  }
  
  public long getMinimum()
  {
    return minSmallest;
  }
  
  public long getLargestMinimum()
  {
    return minLargest;
  }
  
  public long getSmallestMaximum()
  {
    return maxSmallest;
  }
  
  public long getMaximum()
  {
    return maxLargest;
  }
  
  public boolean isIntValue()
  {
    return (getMinimum() >= -2147483648L) && (getMaximum() <= 2147483647L);
  }
  
  public boolean isValidValue(long paramLong)
  {
    return (paramLong >= getMinimum()) && (paramLong <= getMaximum());
  }
  
  public boolean isValidIntValue(long paramLong)
  {
    return (isIntValue()) && (isValidValue(paramLong));
  }
  
  public long checkValidValue(long paramLong, TemporalField paramTemporalField)
  {
    if (!isValidValue(paramLong)) {
      throw new DateTimeException(genInvalidFieldMessage(paramTemporalField, paramLong));
    }
    return paramLong;
  }
  
  public int checkValidIntValue(long paramLong, TemporalField paramTemporalField)
  {
    if (!isValidIntValue(paramLong)) {
      throw new DateTimeException(genInvalidFieldMessage(paramTemporalField, paramLong));
    }
    return (int)paramLong;
  }
  
  private String genInvalidFieldMessage(TemporalField paramTemporalField, long paramLong)
  {
    if (paramTemporalField != null) {
      return "Invalid value for " + paramTemporalField + " (valid values " + this + "): " + paramLong;
    }
    return "Invalid value (valid values " + this + "): " + paramLong;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException, InvalidObjectException
  {
    paramObjectInputStream.defaultReadObject();
    if (minSmallest > minLargest) {
      throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value");
    }
    if (maxSmallest > maxLargest) {
      throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value");
    }
    if (minLargest > maxLargest) {
      throw new InvalidObjectException("Minimum value must be less than maximum value");
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ValueRange))
    {
      ValueRange localValueRange = (ValueRange)paramObject;
      return (minSmallest == minSmallest) && (minLargest == minLargest) && (maxSmallest == maxSmallest) && (maxLargest == maxLargest);
    }
    return false;
  }
  
  public int hashCode()
  {
    long l = minSmallest + minLargest << (int)(16L + minLargest) >> (int)(48L + maxSmallest) << (int)(32L + maxSmallest) >> (int)(32L + maxLargest) << (int)(48L + maxLargest) >> 16;
    return (int)(l ^ l >>> 32);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(minSmallest);
    if (minSmallest != minLargest) {
      localStringBuilder.append('/').append(minLargest);
    }
    localStringBuilder.append(" - ").append(maxSmallest);
    if (maxSmallest != maxLargest) {
      localStringBuilder.append('/').append(maxLargest);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\temporal\ValueRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
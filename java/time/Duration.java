package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Duration
  implements TemporalAmount, Comparable<Duration>, Serializable
{
  public static final Duration ZERO = new Duration(0L, 0);
  private static final long serialVersionUID = 3078945930695997490L;
  private static final BigInteger BI_NANOS_PER_SECOND = BigInteger.valueOf(1000000000L);
  private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);
  private final long seconds;
  private final int nanos;
  
  public static Duration ofDays(long paramLong)
  {
    return create(Math.multiplyExact(paramLong, 86400L), 0);
  }
  
  public static Duration ofHours(long paramLong)
  {
    return create(Math.multiplyExact(paramLong, 3600L), 0);
  }
  
  public static Duration ofMinutes(long paramLong)
  {
    return create(Math.multiplyExact(paramLong, 60L), 0);
  }
  
  public static Duration ofSeconds(long paramLong)
  {
    return create(paramLong, 0);
  }
  
  public static Duration ofSeconds(long paramLong1, long paramLong2)
  {
    long l = Math.addExact(paramLong1, Math.floorDiv(paramLong2, 1000000000L));
    int i = (int)Math.floorMod(paramLong2, 1000000000L);
    return create(l, i);
  }
  
  public static Duration ofMillis(long paramLong)
  {
    long l = paramLong / 1000L;
    int i = (int)(paramLong % 1000L);
    if (i < 0)
    {
      i += 1000;
      l -= 1L;
    }
    return create(l, i * 1000000);
  }
  
  public static Duration ofNanos(long paramLong)
  {
    long l = paramLong / 1000000000L;
    int i = (int)(paramLong % 1000000000L);
    if (i < 0)
    {
      i = (int)(i + 1000000000L);
      l -= 1L;
    }
    return create(l, i);
  }
  
  public static Duration of(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return ZERO.plus(paramLong, paramTemporalUnit);
  }
  
  public static Duration from(TemporalAmount paramTemporalAmount)
  {
    Objects.requireNonNull(paramTemporalAmount, "amount");
    Duration localDuration = ZERO;
    Iterator localIterator = paramTemporalAmount.getUnits().iterator();
    while (localIterator.hasNext())
    {
      TemporalUnit localTemporalUnit = (TemporalUnit)localIterator.next();
      localDuration = localDuration.plus(paramTemporalAmount.get(localTemporalUnit), localTemporalUnit);
    }
    return localDuration;
  }
  
  public static Duration parse(CharSequence paramCharSequence)
  {
    Objects.requireNonNull(paramCharSequence, "text");
    Matcher localMatcher = PATTERN.matcher(paramCharSequence);
    if ((localMatcher.matches()) && (!"T".equals(localMatcher.group(3))))
    {
      boolean bool = "-".equals(localMatcher.group(1));
      String str1 = localMatcher.group(2);
      String str2 = localMatcher.group(4);
      String str3 = localMatcher.group(5);
      String str4 = localMatcher.group(6);
      String str5 = localMatcher.group(7);
      if ((str1 != null) || (str2 != null) || (str3 != null) || (str4 != null))
      {
        long l1 = parseNumber(paramCharSequence, str1, 86400, "days");
        long l2 = parseNumber(paramCharSequence, str2, 3600, "hours");
        long l3 = parseNumber(paramCharSequence, str3, 60, "minutes");
        long l4 = parseNumber(paramCharSequence, str4, 1, "seconds");
        int i = parseFraction(paramCharSequence, str5, l4 < 0L ? -1 : 1);
        try
        {
          return create(bool, l1, l2, l3, l4, i);
        }
        catch (ArithmeticException localArithmeticException)
        {
          throw ((DateTimeParseException)new DateTimeParseException("Text cannot be parsed to a Duration: overflow", paramCharSequence, 0).initCause(localArithmeticException));
        }
      }
    }
    throw new DateTimeParseException("Text cannot be parsed to a Duration", paramCharSequence, 0);
  }
  
  private static long parseNumber(CharSequence paramCharSequence, String paramString1, int paramInt, String paramString2)
  {
    if (paramString1 == null) {
      return 0L;
    }
    try
    {
      long l = Long.parseLong(paramString1);
      return Math.multiplyExact(l, paramInt);
    }
    catch (NumberFormatException|ArithmeticException localNumberFormatException)
    {
      throw ((DateTimeParseException)new DateTimeParseException("Text cannot be parsed to a Duration: " + paramString2, paramCharSequence, 0).initCause(localNumberFormatException));
    }
  }
  
  private static int parseFraction(CharSequence paramCharSequence, String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return 0;
    }
    try
    {
      paramString = (paramString + "000000000").substring(0, 9);
      return Integer.parseInt(paramString) * paramInt;
    }
    catch (NumberFormatException|ArithmeticException localNumberFormatException)
    {
      throw ((DateTimeParseException)new DateTimeParseException("Text cannot be parsed to a Duration: fraction", paramCharSequence, 0).initCause(localNumberFormatException));
    }
  }
  
  private static Duration create(boolean paramBoolean, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt)
  {
    long l = Math.addExact(paramLong1, Math.addExact(paramLong2, Math.addExact(paramLong3, paramLong4)));
    if (paramBoolean) {
      return ofSeconds(l, paramInt).negated();
    }
    return ofSeconds(l, paramInt);
  }
  
  public static Duration between(Temporal paramTemporal1, Temporal paramTemporal2)
  {
    try
    {
      return ofNanos(paramTemporal1.until(paramTemporal2, ChronoUnit.NANOS));
    }
    catch (DateTimeException|ArithmeticException localDateTimeException1)
    {
      long l1 = paramTemporal1.until(paramTemporal2, ChronoUnit.SECONDS);
      long l2;
      try
      {
        l2 = paramTemporal2.getLong(ChronoField.NANO_OF_SECOND) - paramTemporal1.getLong(ChronoField.NANO_OF_SECOND);
        if ((l1 > 0L) && (l2 < 0L)) {
          l1 += 1L;
        } else if ((l1 < 0L) && (l2 > 0L)) {
          l1 -= 1L;
        }
      }
      catch (DateTimeException localDateTimeException2)
      {
        l2 = 0L;
      }
      return ofSeconds(l1, l2);
    }
  }
  
  private static Duration create(long paramLong, int paramInt)
  {
    if ((paramLong | paramInt) == 0L) {
      return ZERO;
    }
    return new Duration(paramLong, paramInt);
  }
  
  private Duration(long paramLong, int paramInt)
  {
    seconds = paramLong;
    nanos = paramInt;
  }
  
  public long get(TemporalUnit paramTemporalUnit)
  {
    if (paramTemporalUnit == ChronoUnit.SECONDS) {
      return seconds;
    }
    if (paramTemporalUnit == ChronoUnit.NANOS) {
      return nanos;
    }
    throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
  }
  
  public List<TemporalUnit> getUnits()
  {
    return DurationUnits.UNITS;
  }
  
  public boolean isZero()
  {
    return (seconds | nanos) == 0L;
  }
  
  public boolean isNegative()
  {
    return seconds < 0L;
  }
  
  public long getSeconds()
  {
    return seconds;
  }
  
  public int getNano()
  {
    return nanos;
  }
  
  public Duration withSeconds(long paramLong)
  {
    return create(paramLong, nanos);
  }
  
  public Duration withNanos(int paramInt)
  {
    ChronoField.NANO_OF_SECOND.checkValidIntValue(paramInt);
    return create(seconds, paramInt);
  }
  
  public Duration plus(Duration paramDuration)
  {
    return plus(paramDuration.getSeconds(), paramDuration.getNano());
  }
  
  public Duration plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    Objects.requireNonNull(paramTemporalUnit, "unit");
    if (paramTemporalUnit == ChronoUnit.DAYS) {
      return plus(Math.multiplyExact(paramLong, 86400L), 0L);
    }
    if (paramTemporalUnit.isDurationEstimated()) {
      throw new UnsupportedTemporalTypeException("Unit must not have an estimated duration");
    }
    if (paramLong == 0L) {
      return this;
    }
    if ((paramTemporalUnit instanceof ChronoUnit))
    {
      switch ((ChronoUnit)paramTemporalUnit)
      {
      case NANOS: 
        return plusNanos(paramLong);
      case MICROS: 
        return plusSeconds(paramLong / 1000000000L * 1000L).plusNanos(paramLong % 1000000000L * 1000L);
      case MILLIS: 
        return plusMillis(paramLong);
      case SECONDS: 
        return plusSeconds(paramLong);
      }
      return plusSeconds(Math.multiplyExact(getDurationseconds, paramLong));
    }
    Duration localDuration = paramTemporalUnit.getDuration().multipliedBy(paramLong);
    return plusSeconds(localDuration.getSeconds()).plusNanos(localDuration.getNano());
  }
  
  public Duration plusDays(long paramLong)
  {
    return plus(Math.multiplyExact(paramLong, 86400L), 0L);
  }
  
  public Duration plusHours(long paramLong)
  {
    return plus(Math.multiplyExact(paramLong, 3600L), 0L);
  }
  
  public Duration plusMinutes(long paramLong)
  {
    return plus(Math.multiplyExact(paramLong, 60L), 0L);
  }
  
  public Duration plusSeconds(long paramLong)
  {
    return plus(paramLong, 0L);
  }
  
  public Duration plusMillis(long paramLong)
  {
    return plus(paramLong / 1000L, paramLong % 1000L * 1000000L);
  }
  
  public Duration plusNanos(long paramLong)
  {
    return plus(0L, paramLong);
  }
  
  private Duration plus(long paramLong1, long paramLong2)
  {
    if ((paramLong1 | paramLong2) == 0L) {
      return this;
    }
    long l1 = Math.addExact(seconds, paramLong1);
    l1 = Math.addExact(l1, paramLong2 / 1000000000L);
    paramLong2 %= 1000000000L;
    long l2 = nanos + paramLong2;
    return ofSeconds(l1, l2);
  }
  
  public Duration minus(Duration paramDuration)
  {
    long l = paramDuration.getSeconds();
    int i = paramDuration.getNano();
    if (l == Long.MIN_VALUE) {
      return plus(Long.MAX_VALUE, -i).plus(1L, 0L);
    }
    return plus(-l, -i);
  }
  
  public Duration minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return paramLong == Long.MIN_VALUE ? plus(Long.MAX_VALUE, paramTemporalUnit).plus(1L, paramTemporalUnit) : plus(-paramLong, paramTemporalUnit);
  }
  
  public Duration minusDays(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1L) : plusDays(-paramLong);
  }
  
  public Duration minusHours(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1L) : plusHours(-paramLong);
  }
  
  public Duration minusMinutes(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1L) : plusMinutes(-paramLong);
  }
  
  public Duration minusSeconds(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1L) : plusSeconds(-paramLong);
  }
  
  public Duration minusMillis(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusMillis(Long.MAX_VALUE).plusMillis(1L) : plusMillis(-paramLong);
  }
  
  public Duration minusNanos(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1L) : plusNanos(-paramLong);
  }
  
  public Duration multipliedBy(long paramLong)
  {
    if (paramLong == 0L) {
      return ZERO;
    }
    if (paramLong == 1L) {
      return this;
    }
    return create(toSeconds().multiply(BigDecimal.valueOf(paramLong)));
  }
  
  public Duration dividedBy(long paramLong)
  {
    if (paramLong == 0L) {
      throw new ArithmeticException("Cannot divide by zero");
    }
    if (paramLong == 1L) {
      return this;
    }
    return create(toSeconds().divide(BigDecimal.valueOf(paramLong), RoundingMode.DOWN));
  }
  
  private BigDecimal toSeconds()
  {
    return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9));
  }
  
  private static Duration create(BigDecimal paramBigDecimal)
  {
    BigInteger localBigInteger = paramBigDecimal.movePointRight(9).toBigIntegerExact();
    BigInteger[] arrayOfBigInteger = localBigInteger.divideAndRemainder(BI_NANOS_PER_SECOND);
    if (arrayOfBigInteger[0].bitLength() > 63) {
      throw new ArithmeticException("Exceeds capacity of Duration: " + localBigInteger);
    }
    return ofSeconds(arrayOfBigInteger[0].longValue(), arrayOfBigInteger[1].intValue());
  }
  
  public Duration negated()
  {
    return multipliedBy(-1L);
  }
  
  public Duration abs()
  {
    return isNegative() ? negated() : this;
  }
  
  public Temporal addTo(Temporal paramTemporal)
  {
    if (seconds != 0L) {
      paramTemporal = paramTemporal.plus(seconds, ChronoUnit.SECONDS);
    }
    if (nanos != 0) {
      paramTemporal = paramTemporal.plus(nanos, ChronoUnit.NANOS);
    }
    return paramTemporal;
  }
  
  public Temporal subtractFrom(Temporal paramTemporal)
  {
    if (seconds != 0L) {
      paramTemporal = paramTemporal.minus(seconds, ChronoUnit.SECONDS);
    }
    if (nanos != 0) {
      paramTemporal = paramTemporal.minus(nanos, ChronoUnit.NANOS);
    }
    return paramTemporal;
  }
  
  public long toDays()
  {
    return seconds / 86400L;
  }
  
  public long toHours()
  {
    return seconds / 3600L;
  }
  
  public long toMinutes()
  {
    return seconds / 60L;
  }
  
  public long toMillis()
  {
    long l = Math.multiplyExact(seconds, 1000L);
    l = Math.addExact(l, nanos / 1000000);
    return l;
  }
  
  public long toNanos()
  {
    long l = Math.multiplyExact(seconds, 1000000000L);
    l = Math.addExact(l, nanos);
    return l;
  }
  
  public int compareTo(Duration paramDuration)
  {
    int i = Long.compare(seconds, seconds);
    if (i != 0) {
      return i;
    }
    return nanos - nanos;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Duration))
    {
      Duration localDuration = (Duration)paramObject;
      return (seconds == seconds) && (nanos == nanos);
    }
    return false;
  }
  
  public int hashCode()
  {
    return (int)(seconds ^ seconds >>> 32) + 51 * nanos;
  }
  
  public String toString()
  {
    if (this == ZERO) {
      return "PT0S";
    }
    long l = seconds / 3600L;
    int i = (int)(seconds % 3600L / 60L);
    int j = (int)(seconds % 60L);
    StringBuilder localStringBuilder = new StringBuilder(24);
    localStringBuilder.append("PT");
    if (l != 0L) {
      localStringBuilder.append(l).append('H');
    }
    if (i != 0) {
      localStringBuilder.append(i).append('M');
    }
    if ((j == 0) && (nanos == 0) && (localStringBuilder.length() > 2)) {
      return localStringBuilder.toString();
    }
    if ((j < 0) && (nanos > 0))
    {
      if (j == -1) {
        localStringBuilder.append("-0");
      } else {
        localStringBuilder.append(j + 1);
      }
    }
    else {
      localStringBuilder.append(j);
    }
    if (nanos > 0)
    {
      int k = localStringBuilder.length();
      if (j < 0) {
        localStringBuilder.append(2000000000L - nanos);
      } else {
        localStringBuilder.append(nanos + 1000000000L);
      }
      while (localStringBuilder.charAt(localStringBuilder.length() - 1) == '0') {
        localStringBuilder.setLength(localStringBuilder.length() - 1);
      }
      localStringBuilder.setCharAt(k, '.');
    }
    localStringBuilder.append('S');
    return localStringBuilder.toString();
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)1, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeLong(seconds);
    paramDataOutput.writeInt(nanos);
  }
  
  static Duration readExternal(DataInput paramDataInput)
    throws IOException
  {
    long l = paramDataInput.readLong();
    int i = paramDataInput.readInt();
    return ofSeconds(l, i);
  }
  
  private static class DurationUnits
  {
    static final List<TemporalUnit> UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[] { ChronoUnit.SECONDS, ChronoUnit.NANOS }));
    
    private DurationUnits() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\Duration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
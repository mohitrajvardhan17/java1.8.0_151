package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Period
  implements ChronoPeriod, Serializable
{
  public static final Period ZERO = new Period(0, 0, 0);
  private static final long serialVersionUID = -3587258372562876L;
  private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
  private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[] { ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS }));
  private final int years;
  private final int months;
  private final int days;
  
  public static Period ofYears(int paramInt)
  {
    return create(paramInt, 0, 0);
  }
  
  public static Period ofMonths(int paramInt)
  {
    return create(0, paramInt, 0);
  }
  
  public static Period ofWeeks(int paramInt)
  {
    return create(0, 0, Math.multiplyExact(paramInt, 7));
  }
  
  public static Period ofDays(int paramInt)
  {
    return create(0, 0, paramInt);
  }
  
  public static Period of(int paramInt1, int paramInt2, int paramInt3)
  {
    return create(paramInt1, paramInt2, paramInt3);
  }
  
  public static Period from(TemporalAmount paramTemporalAmount)
  {
    if ((paramTemporalAmount instanceof Period)) {
      return (Period)paramTemporalAmount;
    }
    if (((paramTemporalAmount instanceof ChronoPeriod)) && (!IsoChronology.INSTANCE.equals(((ChronoPeriod)paramTemporalAmount).getChronology()))) {
      throw new DateTimeException("Period requires ISO chronology: " + paramTemporalAmount);
    }
    Objects.requireNonNull(paramTemporalAmount, "amount");
    int i = 0;
    int j = 0;
    int k = 0;
    Iterator localIterator = paramTemporalAmount.getUnits().iterator();
    while (localIterator.hasNext())
    {
      TemporalUnit localTemporalUnit = (TemporalUnit)localIterator.next();
      long l = paramTemporalAmount.get(localTemporalUnit);
      if (localTemporalUnit == ChronoUnit.YEARS) {
        i = Math.toIntExact(l);
      } else if (localTemporalUnit == ChronoUnit.MONTHS) {
        j = Math.toIntExact(l);
      } else if (localTemporalUnit == ChronoUnit.DAYS) {
        k = Math.toIntExact(l);
      } else {
        throw new DateTimeException("Unit must be Years, Months or Days, but was " + localTemporalUnit);
      }
    }
    return create(i, j, k);
  }
  
  public static Period parse(CharSequence paramCharSequence)
  {
    Objects.requireNonNull(paramCharSequence, "text");
    Matcher localMatcher = PATTERN.matcher(paramCharSequence);
    if (localMatcher.matches())
    {
      int i = "-".equals(localMatcher.group(1)) ? -1 : 1;
      String str1 = localMatcher.group(2);
      String str2 = localMatcher.group(3);
      String str3 = localMatcher.group(4);
      String str4 = localMatcher.group(5);
      if ((str1 != null) || (str2 != null) || (str4 != null) || (str3 != null)) {
        try
        {
          int j = parseNumber(paramCharSequence, str1, i);
          int k = parseNumber(paramCharSequence, str2, i);
          int m = parseNumber(paramCharSequence, str3, i);
          int n = parseNumber(paramCharSequence, str4, i);
          n = Math.addExact(n, Math.multiplyExact(m, 7));
          return create(j, k, n);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new DateTimeParseException("Text cannot be parsed to a Period", paramCharSequence, 0, localNumberFormatException);
        }
      }
    }
    throw new DateTimeParseException("Text cannot be parsed to a Period", paramCharSequence, 0);
  }
  
  private static int parseNumber(CharSequence paramCharSequence, String paramString, int paramInt)
  {
    if (paramString == null) {
      return 0;
    }
    int i = Integer.parseInt(paramString);
    try
    {
      return Math.multiplyExact(i, paramInt);
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new DateTimeParseException("Text cannot be parsed to a Period", paramCharSequence, 0, localArithmeticException);
    }
  }
  
  public static Period between(LocalDate paramLocalDate1, LocalDate paramLocalDate2)
  {
    return paramLocalDate1.until(paramLocalDate2);
  }
  
  private static Period create(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 | paramInt2 | paramInt3) == 0) {
      return ZERO;
    }
    return new Period(paramInt1, paramInt2, paramInt3);
  }
  
  private Period(int paramInt1, int paramInt2, int paramInt3)
  {
    years = paramInt1;
    months = paramInt2;
    days = paramInt3;
  }
  
  public long get(TemporalUnit paramTemporalUnit)
  {
    if (paramTemporalUnit == ChronoUnit.YEARS) {
      return getYears();
    }
    if (paramTemporalUnit == ChronoUnit.MONTHS) {
      return getMonths();
    }
    if (paramTemporalUnit == ChronoUnit.DAYS) {
      return getDays();
    }
    throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
  }
  
  public List<TemporalUnit> getUnits()
  {
    return SUPPORTED_UNITS;
  }
  
  public IsoChronology getChronology()
  {
    return IsoChronology.INSTANCE;
  }
  
  public boolean isZero()
  {
    return this == ZERO;
  }
  
  public boolean isNegative()
  {
    return (years < 0) || (months < 0) || (days < 0);
  }
  
  public int getYears()
  {
    return years;
  }
  
  public int getMonths()
  {
    return months;
  }
  
  public int getDays()
  {
    return days;
  }
  
  public Period withYears(int paramInt)
  {
    if (paramInt == years) {
      return this;
    }
    return create(paramInt, months, days);
  }
  
  public Period withMonths(int paramInt)
  {
    if (paramInt == months) {
      return this;
    }
    return create(years, paramInt, days);
  }
  
  public Period withDays(int paramInt)
  {
    if (paramInt == days) {
      return this;
    }
    return create(years, months, paramInt);
  }
  
  public Period plus(TemporalAmount paramTemporalAmount)
  {
    Period localPeriod = from(paramTemporalAmount);
    return create(Math.addExact(years, years), Math.addExact(months, months), Math.addExact(days, days));
  }
  
  public Period plusYears(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    return create(Math.toIntExact(Math.addExact(years, paramLong)), months, days);
  }
  
  public Period plusMonths(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    return create(years, Math.toIntExact(Math.addExact(months, paramLong)), days);
  }
  
  public Period plusDays(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    return create(years, months, Math.toIntExact(Math.addExact(days, paramLong)));
  }
  
  public Period minus(TemporalAmount paramTemporalAmount)
  {
    Period localPeriod = from(paramTemporalAmount);
    return create(Math.subtractExact(years, years), Math.subtractExact(months, months), Math.subtractExact(days, days));
  }
  
  public Period minusYears(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1L) : plusYears(-paramLong);
  }
  
  public Period minusMonths(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1L) : plusMonths(-paramLong);
  }
  
  public Period minusDays(long paramLong)
  {
    return paramLong == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1L) : plusDays(-paramLong);
  }
  
  public Period multipliedBy(int paramInt)
  {
    if ((this == ZERO) || (paramInt == 1)) {
      return this;
    }
    return create(Math.multiplyExact(years, paramInt), Math.multiplyExact(months, paramInt), Math.multiplyExact(days, paramInt));
  }
  
  public Period negated()
  {
    return multipliedBy(-1);
  }
  
  public Period normalized()
  {
    long l1 = toTotalMonths();
    long l2 = l1 / 12L;
    int i = (int)(l1 % 12L);
    if ((l2 == years) && (i == months)) {
      return this;
    }
    return create(Math.toIntExact(l2), i, days);
  }
  
  public long toTotalMonths()
  {
    return years * 12L + months;
  }
  
  public Temporal addTo(Temporal paramTemporal)
  {
    validateChrono(paramTemporal);
    if (months == 0)
    {
      if (years != 0) {
        paramTemporal = paramTemporal.plus(years, ChronoUnit.YEARS);
      }
    }
    else
    {
      long l = toTotalMonths();
      if (l != 0L) {
        paramTemporal = paramTemporal.plus(l, ChronoUnit.MONTHS);
      }
    }
    if (days != 0) {
      paramTemporal = paramTemporal.plus(days, ChronoUnit.DAYS);
    }
    return paramTemporal;
  }
  
  public Temporal subtractFrom(Temporal paramTemporal)
  {
    validateChrono(paramTemporal);
    if (months == 0)
    {
      if (years != 0) {
        paramTemporal = paramTemporal.minus(years, ChronoUnit.YEARS);
      }
    }
    else
    {
      long l = toTotalMonths();
      if (l != 0L) {
        paramTemporal = paramTemporal.minus(l, ChronoUnit.MONTHS);
      }
    }
    if (days != 0) {
      paramTemporal = paramTemporal.minus(days, ChronoUnit.DAYS);
    }
    return paramTemporal;
  }
  
  private void validateChrono(TemporalAccessor paramTemporalAccessor)
  {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    Chronology localChronology = (Chronology)paramTemporalAccessor.query(TemporalQueries.chronology());
    if ((localChronology != null) && (!IsoChronology.INSTANCE.equals(localChronology))) {
      throw new DateTimeException("Chronology mismatch, expected: ISO, actual: " + localChronology.getId());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Period))
    {
      Period localPeriod = (Period)paramObject;
      return (years == years) && (months == months) && (days == days);
    }
    return false;
  }
  
  public int hashCode()
  {
    return years + Integer.rotateLeft(months, 8) + Integer.rotateLeft(days, 16);
  }
  
  public String toString()
  {
    if (this == ZERO) {
      return "P0D";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('P');
    if (years != 0) {
      localStringBuilder.append(years).append('Y');
    }
    if (months != 0) {
      localStringBuilder.append(months).append('M');
    }
    if (days != 0) {
      localStringBuilder.append(days).append('D');
    }
    return localStringBuilder.toString();
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)14, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeInt(years);
    paramDataOutput.writeInt(months);
    paramDataOutput.writeInt(days);
  }
  
  static Period readExternal(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readInt();
    int j = paramDataInput.readInt();
    int k = paramDataInput.readInt();
    return of(i, j, k);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\Period.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
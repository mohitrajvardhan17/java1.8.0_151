package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

public final class HijrahDate
  extends ChronoLocalDateImpl<HijrahDate>
  implements ChronoLocalDate, Serializable
{
  private static final long serialVersionUID = -5207853542612002020L;
  private final transient HijrahChronology chrono;
  private final transient int prolepticYear;
  private final transient int monthOfYear;
  private final transient int dayOfMonth;
  
  static HijrahDate of(HijrahChronology paramHijrahChronology, int paramInt1, int paramInt2, int paramInt3)
  {
    return new HijrahDate(paramHijrahChronology, paramInt1, paramInt2, paramInt3);
  }
  
  static HijrahDate ofEpochDay(HijrahChronology paramHijrahChronology, long paramLong)
  {
    return new HijrahDate(paramHijrahChronology, paramLong);
  }
  
  public static HijrahDate now()
  {
    return now(Clock.systemDefaultZone());
  }
  
  public static HijrahDate now(ZoneId paramZoneId)
  {
    return now(Clock.system(paramZoneId));
  }
  
  public static HijrahDate now(Clock paramClock)
  {
    return ofEpochDay(HijrahChronology.INSTANCE, LocalDate.now(paramClock).toEpochDay());
  }
  
  public static HijrahDate of(int paramInt1, int paramInt2, int paramInt3)
  {
    return HijrahChronology.INSTANCE.date(paramInt1, paramInt2, paramInt3);
  }
  
  public static HijrahDate from(TemporalAccessor paramTemporalAccessor)
  {
    return HijrahChronology.INSTANCE.date(paramTemporalAccessor);
  }
  
  private HijrahDate(HijrahChronology paramHijrahChronology, int paramInt1, int paramInt2, int paramInt3)
  {
    paramHijrahChronology.getEpochDay(paramInt1, paramInt2, paramInt3);
    chrono = paramHijrahChronology;
    prolepticYear = paramInt1;
    monthOfYear = paramInt2;
    dayOfMonth = paramInt3;
  }
  
  private HijrahDate(HijrahChronology paramHijrahChronology, long paramLong)
  {
    int[] arrayOfInt = paramHijrahChronology.getHijrahDateInfo((int)paramLong);
    chrono = paramHijrahChronology;
    prolepticYear = arrayOfInt[0];
    monthOfYear = arrayOfInt[1];
    dayOfMonth = arrayOfInt[2];
  }
  
  public HijrahChronology getChronology()
  {
    return chrono;
  }
  
  public HijrahEra getEra()
  {
    return HijrahEra.AH;
  }
  
  public int lengthOfMonth()
  {
    return chrono.getMonthLength(prolepticYear, monthOfYear);
  }
  
  public int lengthOfYear()
  {
    return chrono.getYearLength(prolepticYear);
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      if (isSupported(paramTemporalField))
      {
        ChronoField localChronoField = (ChronoField)paramTemporalField;
        switch (localChronoField)
        {
        case DAY_OF_MONTH: 
          return ValueRange.of(1L, lengthOfMonth());
        case DAY_OF_YEAR: 
          return ValueRange.of(1L, lengthOfYear());
        case ALIGNED_WEEK_OF_MONTH: 
          return ValueRange.of(1L, 5L);
        }
        return getChronology().range(localChronoField);
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      switch ((ChronoField)paramTemporalField)
      {
      case DAY_OF_WEEK: 
        return getDayOfWeek();
      case ALIGNED_DAY_OF_WEEK_IN_MONTH: 
        return (getDayOfWeek() - 1) % 7 + 1;
      case ALIGNED_DAY_OF_WEEK_IN_YEAR: 
        return (getDayOfYear() - 1) % 7 + 1;
      case DAY_OF_MONTH: 
        return dayOfMonth;
      case DAY_OF_YEAR: 
        return getDayOfYear();
      case EPOCH_DAY: 
        return toEpochDay();
      case ALIGNED_WEEK_OF_MONTH: 
        return (dayOfMonth - 1) / 7 + 1;
      case ALIGNED_WEEK_OF_YEAR: 
        return (getDayOfYear() - 1) / 7 + 1;
      case MONTH_OF_YEAR: 
        return monthOfYear;
      case PROLEPTIC_MONTH: 
        return getProlepticMonth();
      case YEAR_OF_ERA: 
        return prolepticYear;
      case YEAR: 
        return prolepticYear;
      case ERA: 
        return getEraValue();
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  private long getProlepticMonth()
  {
    return prolepticYear * 12L + monthOfYear - 1L;
  }
  
  public HijrahDate with(TemporalField paramTemporalField, long paramLong)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      chrono.range(localChronoField).checkValidValue(paramLong, localChronoField);
      int i = (int)paramLong;
      switch (localChronoField)
      {
      case DAY_OF_WEEK: 
        return plusDays(paramLong - getDayOfWeek());
      case ALIGNED_DAY_OF_WEEK_IN_MONTH: 
        return plusDays(paramLong - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
      case ALIGNED_DAY_OF_WEEK_IN_YEAR: 
        return plusDays(paramLong - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
      case DAY_OF_MONTH: 
        return resolvePreviousValid(prolepticYear, monthOfYear, i);
      case DAY_OF_YEAR: 
        return plusDays(Math.min(i, lengthOfYear()) - getDayOfYear());
      case EPOCH_DAY: 
        return new HijrahDate(chrono, paramLong);
      case ALIGNED_WEEK_OF_MONTH: 
        return plusDays((paramLong - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH)) * 7L);
      case ALIGNED_WEEK_OF_YEAR: 
        return plusDays((paramLong - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR)) * 7L);
      case MONTH_OF_YEAR: 
        return resolvePreviousValid(prolepticYear, i, dayOfMonth);
      case PROLEPTIC_MONTH: 
        return plusMonths(paramLong - getProlepticMonth());
      case YEAR_OF_ERA: 
        return resolvePreviousValid(prolepticYear >= 1 ? i : 1 - i, monthOfYear, dayOfMonth);
      case YEAR: 
        return resolvePreviousValid(i, monthOfYear, dayOfMonth);
      case ERA: 
        return resolvePreviousValid(1 - prolepticYear, monthOfYear, dayOfMonth);
      }
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return (HijrahDate)super.with(paramTemporalField, paramLong);
  }
  
  private HijrahDate resolvePreviousValid(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = chrono.getMonthLength(paramInt1, paramInt2);
    if (paramInt3 > i) {
      paramInt3 = i;
    }
    return of(chrono, paramInt1, paramInt2, paramInt3);
  }
  
  public HijrahDate with(TemporalAdjuster paramTemporalAdjuster)
  {
    return (HijrahDate)super.with(paramTemporalAdjuster);
  }
  
  public HijrahDate withVariant(HijrahChronology paramHijrahChronology)
  {
    if (chrono == paramHijrahChronology) {
      return this;
    }
    int i = paramHijrahChronology.getDayOfYear(prolepticYear, monthOfYear);
    return of(paramHijrahChronology, prolepticYear, monthOfYear, dayOfMonth > i ? i : dayOfMonth);
  }
  
  public HijrahDate plus(TemporalAmount paramTemporalAmount)
  {
    return (HijrahDate)super.plus(paramTemporalAmount);
  }
  
  public HijrahDate minus(TemporalAmount paramTemporalAmount)
  {
    return (HijrahDate)super.minus(paramTemporalAmount);
  }
  
  public long toEpochDay()
  {
    return chrono.getEpochDay(prolepticYear, monthOfYear, dayOfMonth);
  }
  
  private int getDayOfYear()
  {
    return chrono.getDayOfYear(prolepticYear, monthOfYear) + dayOfMonth;
  }
  
  private int getDayOfWeek()
  {
    int i = (int)Math.floorMod(toEpochDay() + 3L, 7L);
    return i + 1;
  }
  
  private int getEraValue()
  {
    return prolepticYear > 1 ? 1 : 0;
  }
  
  public boolean isLeapYear()
  {
    return chrono.isLeapYear(prolepticYear);
  }
  
  HijrahDate plusYears(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    int i = Math.addExact(prolepticYear, (int)paramLong);
    return resolvePreviousValid(i, monthOfYear, dayOfMonth);
  }
  
  HijrahDate plusMonths(long paramLong)
  {
    if (paramLong == 0L) {
      return this;
    }
    long l1 = prolepticYear * 12L + (monthOfYear - 1);
    long l2 = l1 + paramLong;
    int i = chrono.checkValidYear(Math.floorDiv(l2, 12L));
    int j = (int)Math.floorMod(l2, 12L) + 1;
    return resolvePreviousValid(i, j, dayOfMonth);
  }
  
  HijrahDate plusWeeks(long paramLong)
  {
    return (HijrahDate)super.plusWeeks(paramLong);
  }
  
  HijrahDate plusDays(long paramLong)
  {
    return new HijrahDate(chrono, toEpochDay() + paramLong);
  }
  
  public HijrahDate plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return (HijrahDate)super.plus(paramLong, paramTemporalUnit);
  }
  
  public HijrahDate minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return (HijrahDate)super.minus(paramLong, paramTemporalUnit);
  }
  
  HijrahDate minusYears(long paramLong)
  {
    return (HijrahDate)super.minusYears(paramLong);
  }
  
  HijrahDate minusMonths(long paramLong)
  {
    return (HijrahDate)super.minusMonths(paramLong);
  }
  
  HijrahDate minusWeeks(long paramLong)
  {
    return (HijrahDate)super.minusWeeks(paramLong);
  }
  
  HijrahDate minusDays(long paramLong)
  {
    return (HijrahDate)super.minusDays(paramLong);
  }
  
  public final ChronoLocalDateTime<HijrahDate> atTime(LocalTime paramLocalTime)
  {
    return super.atTime(paramLocalTime);
  }
  
  public ChronoPeriod until(ChronoLocalDate paramChronoLocalDate)
  {
    HijrahDate localHijrahDate1 = getChronology().date(paramChronoLocalDate);
    long l1 = (prolepticYear - prolepticYear) * 12 + (monthOfYear - monthOfYear);
    int i = dayOfMonth - dayOfMonth;
    if ((l1 > 0L) && (i < 0))
    {
      l1 -= 1L;
      HijrahDate localHijrahDate2 = plusMonths(l1);
      i = (int)(localHijrahDate1.toEpochDay() - localHijrahDate2.toEpochDay());
    }
    else if ((l1 < 0L) && (i > 0))
    {
      l1 += 1L;
      i -= localHijrahDate1.lengthOfMonth();
    }
    long l2 = l1 / 12L;
    int j = (int)(l1 % 12L);
    return getChronology().period(Math.toIntExact(l2), j, i);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof HijrahDate))
    {
      HijrahDate localHijrahDate = (HijrahDate)paramObject;
      return (prolepticYear == prolepticYear) && (monthOfYear == monthOfYear) && (dayOfMonth == dayOfMonth) && (getChronology().equals(localHijrahDate.getChronology()));
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = prolepticYear;
    int j = monthOfYear;
    int k = dayOfMonth;
    return getChronology().getId().hashCode() ^ i & 0xF800 ^ (i << 11) + (j << 6) + k;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)6, this);
  }
  
  void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeObject(getChronology());
    paramObjectOutput.writeInt(get(ChronoField.YEAR));
    paramObjectOutput.writeByte(get(ChronoField.MONTH_OF_YEAR));
    paramObjectOutput.writeByte(get(ChronoField.DAY_OF_MONTH));
  }
  
  static HijrahDate readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    HijrahChronology localHijrahChronology = (HijrahChronology)paramObjectInput.readObject();
    int i = paramObjectInput.readInt();
    int j = paramObjectInput.readByte();
    int k = paramObjectInput.readByte();
    return localHijrahChronology.date(i, j, k);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\HijrahDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Calendar;
import java.util.Objects;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.Era;
import sun.util.calendar.LocalGregorianCalendar;
import sun.util.calendar.LocalGregorianCalendar.Date;

public final class JapaneseDate
  extends ChronoLocalDateImpl<JapaneseDate>
  implements ChronoLocalDate, Serializable
{
  private static final long serialVersionUID = -305327627230580483L;
  private final transient LocalDate isoDate;
  private transient JapaneseEra era;
  private transient int yearOfEra;
  static final LocalDate MEIJI_6_ISODATE = LocalDate.of(1873, 1, 1);
  
  public static JapaneseDate now()
  {
    return now(Clock.systemDefaultZone());
  }
  
  public static JapaneseDate now(ZoneId paramZoneId)
  {
    return now(Clock.system(paramZoneId));
  }
  
  public static JapaneseDate now(Clock paramClock)
  {
    return new JapaneseDate(LocalDate.now(paramClock));
  }
  
  public static JapaneseDate of(JapaneseEra paramJapaneseEra, int paramInt1, int paramInt2, int paramInt3)
  {
    Objects.requireNonNull(paramJapaneseEra, "era");
    LocalGregorianCalendar.Date localDate = JapaneseChronology.JCAL.newCalendarDate(null);
    localDate.setEra(paramJapaneseEra.getPrivateEra()).setDate(paramInt1, paramInt2, paramInt3);
    if (!JapaneseChronology.JCAL.validate(localDate)) {
      throw new DateTimeException("year, month, and day not valid for Era");
    }
    LocalDate localLocalDate = LocalDate.of(localDate.getNormalizedYear(), paramInt2, paramInt3);
    return new JapaneseDate(paramJapaneseEra, paramInt1, localLocalDate);
  }
  
  public static JapaneseDate of(int paramInt1, int paramInt2, int paramInt3)
  {
    return new JapaneseDate(LocalDate.of(paramInt1, paramInt2, paramInt3));
  }
  
  static JapaneseDate ofYearDay(JapaneseEra paramJapaneseEra, int paramInt1, int paramInt2)
  {
    Objects.requireNonNull(paramJapaneseEra, "era");
    CalendarDate localCalendarDate = paramJapaneseEra.getPrivateEra().getSinceDate();
    LocalGregorianCalendar.Date localDate = JapaneseChronology.JCAL.newCalendarDate(null);
    localDate.setEra(paramJapaneseEra.getPrivateEra());
    if (paramInt1 == 1) {
      localDate.setDate(paramInt1, localCalendarDate.getMonth(), localCalendarDate.getDayOfMonth() + paramInt2 - 1);
    } else {
      localDate.setDate(paramInt1, 1, paramInt2);
    }
    JapaneseChronology.JCAL.normalize(localDate);
    if ((paramJapaneseEra.getPrivateEra() != localDate.getEra()) || (paramInt1 != localDate.getYear())) {
      throw new DateTimeException("Invalid parameters");
    }
    LocalDate localLocalDate = LocalDate.of(localDate.getNormalizedYear(), localDate.getMonth(), localDate.getDayOfMonth());
    return new JapaneseDate(paramJapaneseEra, paramInt1, localLocalDate);
  }
  
  public static JapaneseDate from(TemporalAccessor paramTemporalAccessor)
  {
    return JapaneseChronology.INSTANCE.date(paramTemporalAccessor);
  }
  
  JapaneseDate(LocalDate paramLocalDate)
  {
    if (paramLocalDate.isBefore(MEIJI_6_ISODATE)) {
      throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
    }
    LocalGregorianCalendar.Date localDate = toPrivateJapaneseDate(paramLocalDate);
    era = JapaneseEra.toJapaneseEra(localDate.getEra());
    yearOfEra = localDate.getYear();
    isoDate = paramLocalDate;
  }
  
  JapaneseDate(JapaneseEra paramJapaneseEra, int paramInt, LocalDate paramLocalDate)
  {
    if (paramLocalDate.isBefore(MEIJI_6_ISODATE)) {
      throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
    }
    era = paramJapaneseEra;
    yearOfEra = paramInt;
    isoDate = paramLocalDate;
  }
  
  public JapaneseChronology getChronology()
  {
    return JapaneseChronology.INSTANCE;
  }
  
  public JapaneseEra getEra()
  {
    return era;
  }
  
  public int lengthOfMonth()
  {
    return isoDate.lengthOfMonth();
  }
  
  public int lengthOfYear()
  {
    Calendar localCalendar = Calendar.getInstance(JapaneseChronology.LOCALE);
    localCalendar.set(0, era.getValue() + 2);
    localCalendar.set(yearOfEra, isoDate.getMonthValue() - 1, isoDate.getDayOfMonth());
    return localCalendar.getActualMaximum(6);
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((paramTemporalField == ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) || (paramTemporalField == ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) || (paramTemporalField == ChronoField.ALIGNED_WEEK_OF_MONTH) || (paramTemporalField == ChronoField.ALIGNED_WEEK_OF_YEAR)) {
      return false;
    }
    return super.isSupported(paramTemporalField);
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
        case YEAR_OF_ERA: 
          Calendar localCalendar = Calendar.getInstance(JapaneseChronology.LOCALE);
          localCalendar.set(0, era.getValue() + 2);
          localCalendar.set(yearOfEra, isoDate.getMonthValue() - 1, isoDate.getDayOfMonth());
          return ValueRange.of(1L, localCalendar.getActualMaximum(1));
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
      case ALIGNED_DAY_OF_WEEK_IN_MONTH: 
      case ALIGNED_DAY_OF_WEEK_IN_YEAR: 
      case ALIGNED_WEEK_OF_MONTH: 
      case ALIGNED_WEEK_OF_YEAR: 
        throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
      case YEAR_OF_ERA: 
        return yearOfEra;
      case ERA: 
        return era.getValue();
      case DAY_OF_YEAR: 
        Calendar localCalendar = Calendar.getInstance(JapaneseChronology.LOCALE);
        localCalendar.set(0, era.getValue() + 2);
        localCalendar.set(yearOfEra, isoDate.getMonthValue() - 1, isoDate.getDayOfMonth());
        return localCalendar.get(6);
      }
      return isoDate.getLong(paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  private static LocalGregorianCalendar.Date toPrivateJapaneseDate(LocalDate paramLocalDate)
  {
    LocalGregorianCalendar.Date localDate = JapaneseChronology.JCAL.newCalendarDate(null);
    Era localEra = JapaneseEra.privateEraFrom(paramLocalDate);
    int i = paramLocalDate.getYear();
    if (localEra != null) {
      i -= localEra.getSinceDate().getYear() - 1;
    }
    localDate.setEra(localEra).setYear(i).setMonth(paramLocalDate.getMonthValue()).setDayOfMonth(paramLocalDate.getDayOfMonth());
    JapaneseChronology.JCAL.normalize(localDate);
    return localDate;
  }
  
  public JapaneseDate with(TemporalField paramTemporalField, long paramLong)
  {
    if ((paramTemporalField instanceof ChronoField))
    {
      ChronoField localChronoField = (ChronoField)paramTemporalField;
      if (getLong(localChronoField) == paramLong) {
        return this;
      }
      switch (localChronoField)
      {
      case YEAR_OF_ERA: 
      case ERA: 
      case YEAR: 
        int i = getChronology().range(localChronoField).checkValidIntValue(paramLong, localChronoField);
        switch (localChronoField)
        {
        case YEAR_OF_ERA: 
          return withYear(i);
        case YEAR: 
          return with(isoDate.withYear(i));
        case ERA: 
          return withYear(JapaneseEra.of(i), yearOfEra);
        }
        break;
      }
      return with(isoDate.with(paramTemporalField, paramLong));
    }
    return (JapaneseDate)super.with(paramTemporalField, paramLong);
  }
  
  public JapaneseDate with(TemporalAdjuster paramTemporalAdjuster)
  {
    return (JapaneseDate)super.with(paramTemporalAdjuster);
  }
  
  public JapaneseDate plus(TemporalAmount paramTemporalAmount)
  {
    return (JapaneseDate)super.plus(paramTemporalAmount);
  }
  
  public JapaneseDate minus(TemporalAmount paramTemporalAmount)
  {
    return (JapaneseDate)super.minus(paramTemporalAmount);
  }
  
  private JapaneseDate withYear(JapaneseEra paramJapaneseEra, int paramInt)
  {
    int i = JapaneseChronology.INSTANCE.prolepticYear(paramJapaneseEra, paramInt);
    return with(isoDate.withYear(i));
  }
  
  private JapaneseDate withYear(int paramInt)
  {
    return withYear(getEra(), paramInt);
  }
  
  JapaneseDate plusYears(long paramLong)
  {
    return with(isoDate.plusYears(paramLong));
  }
  
  JapaneseDate plusMonths(long paramLong)
  {
    return with(isoDate.plusMonths(paramLong));
  }
  
  JapaneseDate plusWeeks(long paramLong)
  {
    return with(isoDate.plusWeeks(paramLong));
  }
  
  JapaneseDate plusDays(long paramLong)
  {
    return with(isoDate.plusDays(paramLong));
  }
  
  public JapaneseDate plus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return (JapaneseDate)super.plus(paramLong, paramTemporalUnit);
  }
  
  public JapaneseDate minus(long paramLong, TemporalUnit paramTemporalUnit)
  {
    return (JapaneseDate)super.minus(paramLong, paramTemporalUnit);
  }
  
  JapaneseDate minusYears(long paramLong)
  {
    return (JapaneseDate)super.minusYears(paramLong);
  }
  
  JapaneseDate minusMonths(long paramLong)
  {
    return (JapaneseDate)super.minusMonths(paramLong);
  }
  
  JapaneseDate minusWeeks(long paramLong)
  {
    return (JapaneseDate)super.minusWeeks(paramLong);
  }
  
  JapaneseDate minusDays(long paramLong)
  {
    return (JapaneseDate)super.minusDays(paramLong);
  }
  
  private JapaneseDate with(LocalDate paramLocalDate)
  {
    return paramLocalDate.equals(isoDate) ? this : new JapaneseDate(paramLocalDate);
  }
  
  public final ChronoLocalDateTime<JapaneseDate> atTime(LocalTime paramLocalTime)
  {
    return super.atTime(paramLocalTime);
  }
  
  public ChronoPeriod until(ChronoLocalDate paramChronoLocalDate)
  {
    Period localPeriod = isoDate.until(paramChronoLocalDate);
    return getChronology().period(localPeriod.getYears(), localPeriod.getMonths(), localPeriod.getDays());
  }
  
  public long toEpochDay()
  {
    return isoDate.toEpochDay();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof JapaneseDate))
    {
      JapaneseDate localJapaneseDate = (JapaneseDate)paramObject;
      return isoDate.equals(isoDate);
    }
    return false;
  }
  
  public int hashCode()
  {
    return getChronology().getId().hashCode() ^ isoDate.hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)4, this);
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeInt(get(ChronoField.YEAR));
    paramDataOutput.writeByte(get(ChronoField.MONTH_OF_YEAR));
    paramDataOutput.writeByte(get(ChronoField.DAY_OF_MONTH));
  }
  
  static JapaneseDate readExternal(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readInt();
    int j = paramDataInput.readByte();
    int k = paramDataInput.readByte();
    return JapaneseChronology.INSTANCE.date(i, j, k);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\JapaneseDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
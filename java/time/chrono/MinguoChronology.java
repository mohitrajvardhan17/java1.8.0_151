package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MinguoChronology
  extends AbstractChronology
  implements Serializable
{
  public static final MinguoChronology INSTANCE = new MinguoChronology();
  private static final long serialVersionUID = 1039765215346859963L;
  static final int YEARS_DIFFERENCE = 1911;
  
  private MinguoChronology() {}
  
  public String getId()
  {
    return "Minguo";
  }
  
  public String getCalendarType()
  {
    return "roc";
  }
  
  public MinguoDate date(Era paramEra, int paramInt1, int paramInt2, int paramInt3)
  {
    return date(prolepticYear(paramEra, paramInt1), paramInt2, paramInt3);
  }
  
  public MinguoDate date(int paramInt1, int paramInt2, int paramInt3)
  {
    return new MinguoDate(LocalDate.of(paramInt1 + 1911, paramInt2, paramInt3));
  }
  
  public MinguoDate dateYearDay(Era paramEra, int paramInt1, int paramInt2)
  {
    return dateYearDay(prolepticYear(paramEra, paramInt1), paramInt2);
  }
  
  public MinguoDate dateYearDay(int paramInt1, int paramInt2)
  {
    return new MinguoDate(LocalDate.ofYearDay(paramInt1 + 1911, paramInt2));
  }
  
  public MinguoDate dateEpochDay(long paramLong)
  {
    return new MinguoDate(LocalDate.ofEpochDay(paramLong));
  }
  
  public MinguoDate dateNow()
  {
    return dateNow(Clock.systemDefaultZone());
  }
  
  public MinguoDate dateNow(ZoneId paramZoneId)
  {
    return dateNow(Clock.system(paramZoneId));
  }
  
  public MinguoDate dateNow(Clock paramClock)
  {
    return date(LocalDate.now(paramClock));
  }
  
  public MinguoDate date(TemporalAccessor paramTemporalAccessor)
  {
    if ((paramTemporalAccessor instanceof MinguoDate)) {
      return (MinguoDate)paramTemporalAccessor;
    }
    return new MinguoDate(LocalDate.from(paramTemporalAccessor));
  }
  
  public ChronoLocalDateTime<MinguoDate> localDateTime(TemporalAccessor paramTemporalAccessor)
  {
    return super.localDateTime(paramTemporalAccessor);
  }
  
  public ChronoZonedDateTime<MinguoDate> zonedDateTime(TemporalAccessor paramTemporalAccessor)
  {
    return super.zonedDateTime(paramTemporalAccessor);
  }
  
  public ChronoZonedDateTime<MinguoDate> zonedDateTime(Instant paramInstant, ZoneId paramZoneId)
  {
    return super.zonedDateTime(paramInstant, paramZoneId);
  }
  
  public boolean isLeapYear(long paramLong)
  {
    return IsoChronology.INSTANCE.isLeapYear(paramLong + 1911L);
  }
  
  public int prolepticYear(Era paramEra, int paramInt)
  {
    if (!(paramEra instanceof MinguoEra)) {
      throw new ClassCastException("Era must be MinguoEra");
    }
    return paramEra == MinguoEra.ROC ? paramInt : 1 - paramInt;
  }
  
  public MinguoEra eraOf(int paramInt)
  {
    return MinguoEra.of(paramInt);
  }
  
  public List<Era> eras()
  {
    return Arrays.asList(MinguoEra.values());
  }
  
  public ValueRange range(ChronoField paramChronoField)
  {
    ValueRange localValueRange;
    switch (paramChronoField)
    {
    case PROLEPTIC_MONTH: 
      localValueRange = ChronoField.PROLEPTIC_MONTH.range();
      return ValueRange.of(localValueRange.getMinimum() - 22932L, localValueRange.getMaximum() - 22932L);
    case YEAR_OF_ERA: 
      localValueRange = ChronoField.YEAR.range();
      return ValueRange.of(1L, localValueRange.getMaximum() - 1911L, -localValueRange.getMinimum() + 1L + 1911L);
    case YEAR: 
      localValueRange = ChronoField.YEAR.range();
      return ValueRange.of(localValueRange.getMinimum() - 1911L, localValueRange.getMaximum() - 1911L);
    }
    return paramChronoField.range();
  }
  
  public MinguoDate resolveDate(Map<TemporalField, Long> paramMap, ResolverStyle paramResolverStyle)
  {
    return (MinguoDate)super.resolveDate(paramMap, paramResolverStyle);
  }
  
  Object writeReplace()
  {
    return super.writeReplace();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\MinguoChronology.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
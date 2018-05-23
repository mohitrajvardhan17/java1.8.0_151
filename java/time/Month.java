package java.time;

import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Locale;

public enum Month
  implements TemporalAccessor, TemporalAdjuster
{
  JANUARY,  FEBRUARY,  MARCH,  APRIL,  MAY,  JUNE,  JULY,  AUGUST,  SEPTEMBER,  OCTOBER,  NOVEMBER,  DECEMBER;
  
  private static final Month[] ENUMS = values();
  
  private Month() {}
  
  public static Month of(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 12)) {
      throw new DateTimeException("Invalid value for MonthOfYear: " + paramInt);
    }
    return ENUMS[(paramInt - 1)];
  }
  
  public static Month from(TemporalAccessor paramTemporalAccessor)
  {
    if ((paramTemporalAccessor instanceof Month)) {
      return (Month)paramTemporalAccessor;
    }
    try
    {
      if (!IsoChronology.INSTANCE.equals(Chronology.from(paramTemporalAccessor))) {
        paramTemporalAccessor = LocalDate.from(paramTemporalAccessor);
      }
      return of(paramTemporalAccessor.get(ChronoField.MONTH_OF_YEAR));
    }
    catch (DateTimeException localDateTimeException)
    {
      throw new DateTimeException("Unable to obtain Month from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName(), localDateTimeException);
    }
  }
  
  public int getValue()
  {
    return ordinal() + 1;
  }
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale)
  {
    return new DateTimeFormatterBuilder().appendText(ChronoField.MONTH_OF_YEAR, paramTextStyle).toFormatter(paramLocale).format(this);
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField)) {
      return paramTemporalField == ChronoField.MONTH_OF_YEAR;
    }
    return (paramTemporalField != null) && (paramTemporalField.isSupportedBy(this));
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.MONTH_OF_YEAR) {
      return paramTemporalField.range();
    }
    return super.range(paramTemporalField);
  }
  
  public int get(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.MONTH_OF_YEAR) {
      return getValue();
    }
    return super.get(paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.MONTH_OF_YEAR) {
      return getValue();
    }
    if ((paramTemporalField instanceof ChronoField)) {
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  public Month plus(long paramLong)
  {
    int i = (int)(paramLong % 12L);
    return ENUMS[((ordinal() + (i + 12)) % 12)];
  }
  
  public Month minus(long paramLong)
  {
    return plus(-(paramLong % 12L));
  }
  
  public int length(boolean paramBoolean)
  {
    switch (this)
    {
    case FEBRUARY: 
      return paramBoolean ? 29 : 28;
    case APRIL: 
    case JUNE: 
    case SEPTEMBER: 
    case NOVEMBER: 
      return 30;
    }
    return 31;
  }
  
  public int minLength()
  {
    switch (this)
    {
    case FEBRUARY: 
      return 28;
    case APRIL: 
    case JUNE: 
    case SEPTEMBER: 
    case NOVEMBER: 
      return 30;
    }
    return 31;
  }
  
  public int maxLength()
  {
    switch (this)
    {
    case FEBRUARY: 
      return 29;
    case APRIL: 
    case JUNE: 
    case SEPTEMBER: 
    case NOVEMBER: 
      return 30;
    }
    return 31;
  }
  
  public int firstDayOfYear(boolean paramBoolean)
  {
    int i = paramBoolean ? 1 : 0;
    switch (this)
    {
    case JANUARY: 
      return 1;
    case FEBRUARY: 
      return 32;
    case MARCH: 
      return 60 + i;
    case APRIL: 
      return 91 + i;
    case MAY: 
      return 121 + i;
    case JUNE: 
      return 152 + i;
    case JULY: 
      return 182 + i;
    case AUGUST: 
      return 213 + i;
    case SEPTEMBER: 
      return 244 + i;
    case OCTOBER: 
      return 274 + i;
    case NOVEMBER: 
      return 305 + i;
    }
    return 335 + i;
  }
  
  public Month firstMonthOfQuarter()
  {
    return ENUMS[(ordinal() / 3 * 3)];
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if (paramTemporalQuery == TemporalQueries.chronology()) {
      return IsoChronology.INSTANCE;
    }
    if (paramTemporalQuery == TemporalQueries.precision()) {
      return ChronoUnit.MONTHS;
    }
    return (R)super.query(paramTemporalQuery);
  }
  
  public Temporal adjustInto(Temporal paramTemporal)
  {
    if (!Chronology.from(paramTemporal).equals(IsoChronology.INSTANCE)) {
      throw new DateTimeException("Adjustment only supported on ISO date-time");
    }
    return paramTemporal.with(ChronoField.MONTH_OF_YEAR, getValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\Month.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
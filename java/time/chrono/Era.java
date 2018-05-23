package java.time.chrono;

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

public abstract interface Era
  extends TemporalAccessor, TemporalAdjuster
{
  public abstract int getValue();
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField)) {
      return paramTemporalField == ChronoField.ERA;
    }
    return (paramTemporalField != null) && (paramTemporalField.isSupportedBy(this));
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    return super.range(paramTemporalField);
  }
  
  public int get(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.ERA) {
      return getValue();
    }
    return super.get(paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.ERA) {
      return getValue();
    }
    if ((paramTemporalField instanceof ChronoField)) {
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if (paramTemporalQuery == TemporalQueries.precision()) {
      return ChronoUnit.ERAS;
    }
    return (R)super.query(paramTemporalQuery);
  }
  
  public Temporal adjustInto(Temporal paramTemporal)
  {
    return paramTemporal.with(ChronoField.ERA, getValue());
  }
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale)
  {
    return new DateTimeFormatterBuilder().appendText(ChronoField.ERA, paramTextStyle).toFormatter(paramLocale).format(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\Era.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
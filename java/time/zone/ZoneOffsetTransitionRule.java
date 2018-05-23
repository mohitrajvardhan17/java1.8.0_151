package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public final class ZoneOffsetTransitionRule
  implements Serializable
{
  private static final long serialVersionUID = 6889046316657758795L;
  private final Month month;
  private final byte dom;
  private final DayOfWeek dow;
  private final LocalTime time;
  private final boolean timeEndOfDay;
  private final TimeDefinition timeDefinition;
  private final ZoneOffset standardOffset;
  private final ZoneOffset offsetBefore;
  private final ZoneOffset offsetAfter;
  
  public static ZoneOffsetTransitionRule of(Month paramMonth, int paramInt, DayOfWeek paramDayOfWeek, LocalTime paramLocalTime, boolean paramBoolean, TimeDefinition paramTimeDefinition, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, ZoneOffset paramZoneOffset3)
  {
    Objects.requireNonNull(paramMonth, "month");
    Objects.requireNonNull(paramLocalTime, "time");
    Objects.requireNonNull(paramTimeDefinition, "timeDefnition");
    Objects.requireNonNull(paramZoneOffset1, "standardOffset");
    Objects.requireNonNull(paramZoneOffset2, "offsetBefore");
    Objects.requireNonNull(paramZoneOffset3, "offsetAfter");
    if ((paramInt < -28) || (paramInt > 31) || (paramInt == 0)) {
      throw new IllegalArgumentException("Day of month indicator must be between -28 and 31 inclusive excluding zero");
    }
    if ((paramBoolean) && (!paramLocalTime.equals(LocalTime.MIDNIGHT))) {
      throw new IllegalArgumentException("Time must be midnight when end of day flag is true");
    }
    return new ZoneOffsetTransitionRule(paramMonth, paramInt, paramDayOfWeek, paramLocalTime, paramBoolean, paramTimeDefinition, paramZoneOffset1, paramZoneOffset2, paramZoneOffset3);
  }
  
  ZoneOffsetTransitionRule(Month paramMonth, int paramInt, DayOfWeek paramDayOfWeek, LocalTime paramLocalTime, boolean paramBoolean, TimeDefinition paramTimeDefinition, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2, ZoneOffset paramZoneOffset3)
  {
    month = paramMonth;
    dom = ((byte)paramInt);
    dow = paramDayOfWeek;
    time = paramLocalTime;
    timeEndOfDay = paramBoolean;
    timeDefinition = paramTimeDefinition;
    standardOffset = paramZoneOffset1;
    offsetBefore = paramZoneOffset2;
    offsetAfter = paramZoneOffset3;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)3, this);
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    int i = timeEndOfDay ? 86400 : time.toSecondOfDay();
    int j = standardOffset.getTotalSeconds();
    int k = offsetBefore.getTotalSeconds() - j;
    int m = offsetAfter.getTotalSeconds() - j;
    int n = i % 3600 == 0 ? time.getHour() : timeEndOfDay ? 24 : 31;
    int i1 = j % 900 == 0 ? j / 900 + 128 : 255;
    int i2 = (k == 0) || (k == 1800) || (k == 3600) ? k / 1800 : 3;
    int i3 = (m == 0) || (m == 1800) || (m == 3600) ? m / 1800 : 3;
    int i4 = dow == null ? 0 : dow.getValue();
    int i5 = (month.getValue() << 28) + (dom + 32 << 22) + (i4 << 19) + (n << 14) + (timeDefinition.ordinal() << 12) + (i1 << 4) + (i2 << 2) + i3;
    paramDataOutput.writeInt(i5);
    if (n == 31) {
      paramDataOutput.writeInt(i);
    }
    if (i1 == 255) {
      paramDataOutput.writeInt(j);
    }
    if (i2 == 3) {
      paramDataOutput.writeInt(offsetBefore.getTotalSeconds());
    }
    if (i3 == 3) {
      paramDataOutput.writeInt(offsetAfter.getTotalSeconds());
    }
  }
  
  static ZoneOffsetTransitionRule readExternal(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readInt();
    Month localMonth = Month.of(i >>> 28);
    int j = ((i & 0xFC00000) >>> 22) - 32;
    int k = (i & 0x380000) >>> 19;
    DayOfWeek localDayOfWeek = k == 0 ? null : DayOfWeek.of(k);
    int m = (i & 0x7C000) >>> 14;
    TimeDefinition localTimeDefinition = TimeDefinition.values()[((i & 0x3000) >>> 12)];
    int n = (i & 0xFF0) >>> 4;
    int i1 = (i & 0xC) >>> 2;
    int i2 = i & 0x3;
    LocalTime localLocalTime = m == 31 ? LocalTime.ofSecondOfDay(paramDataInput.readInt()) : LocalTime.of(m % 24, 0);
    ZoneOffset localZoneOffset1 = n == 255 ? ZoneOffset.ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds((n - 128) * 900);
    ZoneOffset localZoneOffset2 = i1 == 3 ? ZoneOffset.ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds(localZoneOffset1.getTotalSeconds() + i1 * 1800);
    ZoneOffset localZoneOffset3 = i2 == 3 ? ZoneOffset.ofTotalSeconds(paramDataInput.readInt()) : ZoneOffset.ofTotalSeconds(localZoneOffset1.getTotalSeconds() + i2 * 1800);
    return of(localMonth, j, localDayOfWeek, localLocalTime, m == 24, localTimeDefinition, localZoneOffset1, localZoneOffset2, localZoneOffset3);
  }
  
  public Month getMonth()
  {
    return month;
  }
  
  public int getDayOfMonthIndicator()
  {
    return dom;
  }
  
  public DayOfWeek getDayOfWeek()
  {
    return dow;
  }
  
  public LocalTime getLocalTime()
  {
    return time;
  }
  
  public boolean isMidnightEndOfDay()
  {
    return timeEndOfDay;
  }
  
  public TimeDefinition getTimeDefinition()
  {
    return timeDefinition;
  }
  
  public ZoneOffset getStandardOffset()
  {
    return standardOffset;
  }
  
  public ZoneOffset getOffsetBefore()
  {
    return offsetBefore;
  }
  
  public ZoneOffset getOffsetAfter()
  {
    return offsetAfter;
  }
  
  public ZoneOffsetTransition createTransition(int paramInt)
  {
    LocalDate localLocalDate;
    if (dom < 0)
    {
      localLocalDate = LocalDate.of(paramInt, month, month.length(IsoChronology.INSTANCE.isLeapYear(paramInt)) + 1 + dom);
      if (dow != null) {
        localLocalDate = localLocalDate.with(TemporalAdjusters.previousOrSame(dow));
      }
    }
    else
    {
      localLocalDate = LocalDate.of(paramInt, month, dom);
      if (dow != null) {
        localLocalDate = localLocalDate.with(TemporalAdjusters.nextOrSame(dow));
      }
    }
    if (timeEndOfDay) {
      localLocalDate = localLocalDate.plusDays(1L);
    }
    LocalDateTime localLocalDateTime1 = LocalDateTime.of(localLocalDate, time);
    LocalDateTime localLocalDateTime2 = timeDefinition.createDateTime(localLocalDateTime1, standardOffset, offsetBefore);
    return new ZoneOffsetTransition(localLocalDateTime2, offsetBefore, offsetAfter);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ZoneOffsetTransitionRule))
    {
      ZoneOffsetTransitionRule localZoneOffsetTransitionRule = (ZoneOffsetTransitionRule)paramObject;
      return (month == month) && (dom == dom) && (dow == dow) && (timeDefinition == timeDefinition) && (time.equals(time)) && (timeEndOfDay == timeEndOfDay) && (standardOffset.equals(standardOffset)) && (offsetBefore.equals(offsetBefore)) && (offsetAfter.equals(offsetAfter));
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = (time.toSecondOfDay() + (timeEndOfDay ? 1 : 0) << 15) + (month.ordinal() << 11) + (dom + 32 << 5) + ((dow == null ? 7 : dow.ordinal()) << 2) + timeDefinition.ordinal();
    return i ^ standardOffset.hashCode() ^ offsetBefore.hashCode() ^ offsetAfter.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TransitionRule[").append(offsetBefore.compareTo(offsetAfter) > 0 ? "Gap " : "Overlap ").append(offsetBefore).append(" to ").append(offsetAfter).append(", ");
    if (dow != null)
    {
      if (dom == -1) {
        localStringBuilder.append(dow.name()).append(" on or before last day of ").append(month.name());
      } else if (dom < 0) {
        localStringBuilder.append(dow.name()).append(" on or before last day minus ").append(-dom - 1).append(" of ").append(month.name());
      } else {
        localStringBuilder.append(dow.name()).append(" on or after ").append(month.name()).append(' ').append(dom);
      }
    }
    else {
      localStringBuilder.append(month.name()).append(' ').append(dom);
    }
    localStringBuilder.append(" at ").append(timeEndOfDay ? "24:00" : time.toString()).append(" ").append(timeDefinition).append(", standard offset ").append(standardOffset).append(']');
    return localStringBuilder.toString();
  }
  
  public static enum TimeDefinition
  {
    UTC,  WALL,  STANDARD;
    
    private TimeDefinition() {}
    
    public LocalDateTime createDateTime(LocalDateTime paramLocalDateTime, ZoneOffset paramZoneOffset1, ZoneOffset paramZoneOffset2)
    {
      int i;
      switch (ZoneOffsetTransitionRule.1.$SwitchMap$java$time$zone$ZoneOffsetTransitionRule$TimeDefinition[ordinal()])
      {
      case 1: 
        i = paramZoneOffset2.getTotalSeconds() - ZoneOffset.UTC.getTotalSeconds();
        return paramLocalDateTime.plusSeconds(i);
      case 2: 
        i = paramZoneOffset2.getTotalSeconds() - paramZoneOffset1.getTotalSeconds();
        return paramLocalDateTime.plusSeconds(i);
      }
      return paramLocalDateTime;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\zone\ZoneOffsetTransitionRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
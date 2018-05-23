package java.time.format;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

final class Parsed
  implements TemporalAccessor
{
  final Map<TemporalField, Long> fieldValues = new HashMap();
  ZoneId zone;
  Chronology chrono;
  boolean leapSecond;
  private ResolverStyle resolverStyle;
  private ChronoLocalDate date;
  private LocalTime time;
  Period excessDays = Period.ZERO;
  
  Parsed() {}
  
  Parsed copy()
  {
    Parsed localParsed = new Parsed();
    fieldValues.putAll(fieldValues);
    zone = zone;
    chrono = chrono;
    leapSecond = leapSecond;
    return localParsed;
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((fieldValues.containsKey(paramTemporalField)) || ((date != null) && (date.isSupported(paramTemporalField))) || ((time != null) && (time.isSupported(paramTemporalField)))) {
      return true;
    }
    return (paramTemporalField != null) && (!(paramTemporalField instanceof ChronoField)) && (paramTemporalField.isSupportedBy(this));
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    Objects.requireNonNull(paramTemporalField, "field");
    Long localLong = (Long)fieldValues.get(paramTemporalField);
    if (localLong != null) {
      return localLong.longValue();
    }
    if ((date != null) && (date.isSupported(paramTemporalField))) {
      return date.getLong(paramTemporalField);
    }
    if ((time != null) && (time.isSupported(paramTemporalField))) {
      return time.getLong(paramTemporalField);
    }
    if ((paramTemporalField instanceof ChronoField)) {
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if (paramTemporalQuery == TemporalQueries.zoneId()) {
      return zone;
    }
    if (paramTemporalQuery == TemporalQueries.chronology()) {
      return chrono;
    }
    if (paramTemporalQuery == TemporalQueries.localDate()) {
      return date != null ? LocalDate.from(date) : null;
    }
    if (paramTemporalQuery == TemporalQueries.localTime()) {
      return time;
    }
    if ((paramTemporalQuery == TemporalQueries.zone()) || (paramTemporalQuery == TemporalQueries.offset())) {
      return (R)paramTemporalQuery.queryFrom(this);
    }
    if (paramTemporalQuery == TemporalQueries.precision()) {
      return null;
    }
    return (R)paramTemporalQuery.queryFrom(this);
  }
  
  TemporalAccessor resolve(ResolverStyle paramResolverStyle, Set<TemporalField> paramSet)
  {
    if (paramSet != null) {
      fieldValues.keySet().retainAll(paramSet);
    }
    resolverStyle = paramResolverStyle;
    resolveFields();
    resolveTimeLenient();
    crossCheck();
    resolvePeriod();
    resolveFractional();
    resolveInstant();
    return this;
  }
  
  private void resolveFields()
  {
    resolveInstantFields();
    resolveDateFields();
    resolveTimeFields();
    if (fieldValues.size() > 0)
    {
      int i = 0;
      label320:
      if (i < 50)
      {
        Iterator localIterator = fieldValues.entrySet().iterator();
        for (;;)
        {
          if (!localIterator.hasNext()) {
            break label320;
          }
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          TemporalField localTemporalField = (TemporalField)localEntry.getKey();
          Object localObject1 = localTemporalField.resolve(fieldValues, this, resolverStyle);
          if (localObject1 != null)
          {
            Object localObject2;
            if ((localObject1 instanceof ChronoZonedDateTime))
            {
              localObject2 = (ChronoZonedDateTime)localObject1;
              if (zone == null) {
                zone = ((ChronoZonedDateTime)localObject2).getZone();
              } else if (!zone.equals(((ChronoZonedDateTime)localObject2).getZone())) {
                throw new DateTimeException("ChronoZonedDateTime must use the effective parsed zone: " + zone);
              }
              localObject1 = ((ChronoZonedDateTime)localObject2).toLocalDateTime();
            }
            if ((localObject1 instanceof ChronoLocalDateTime))
            {
              localObject2 = (ChronoLocalDateTime)localObject1;
              updateCheckConflict(((ChronoLocalDateTime)localObject2).toLocalTime(), Period.ZERO);
              updateCheckConflict(((ChronoLocalDateTime)localObject2).toLocalDate());
              i++;
              break;
            }
            if ((localObject1 instanceof ChronoLocalDate))
            {
              updateCheckConflict((ChronoLocalDate)localObject1);
              i++;
              break;
            }
            if ((localObject1 instanceof LocalTime))
            {
              updateCheckConflict((LocalTime)localObject1, Period.ZERO);
              i++;
              break;
            }
            throw new DateTimeException("Method resolve() can only return ChronoZonedDateTime, ChronoLocalDateTime, ChronoLocalDate or LocalTime");
          }
          if (!fieldValues.containsKey(localTemporalField))
          {
            i++;
            break;
          }
        }
      }
      if (i == 50) {
        throw new DateTimeException("One of the parsed fields has an incorrectly implemented resolve method");
      }
      if (i > 0)
      {
        resolveInstantFields();
        resolveDateFields();
        resolveTimeFields();
      }
    }
  }
  
  private void updateCheckConflict(TemporalField paramTemporalField1, TemporalField paramTemporalField2, Long paramLong)
  {
    Long localLong = (Long)fieldValues.put(paramTemporalField2, paramLong);
    if ((localLong != null) && (localLong.longValue() != paramLong.longValue())) {
      throw new DateTimeException("Conflict found: " + paramTemporalField2 + " " + localLong + " differs from " + paramTemporalField2 + " " + paramLong + " while resolving  " + paramTemporalField1);
    }
  }
  
  private void resolveInstantFields()
  {
    if (fieldValues.containsKey(ChronoField.INSTANT_SECONDS)) {
      if (zone != null)
      {
        resolveInstantFields0(zone);
      }
      else
      {
        Long localLong = (Long)fieldValues.get(ChronoField.OFFSET_SECONDS);
        if (localLong != null)
        {
          ZoneOffset localZoneOffset = ZoneOffset.ofTotalSeconds(localLong.intValue());
          resolveInstantFields0(localZoneOffset);
        }
      }
    }
  }
  
  private void resolveInstantFields0(ZoneId paramZoneId)
  {
    Instant localInstant = Instant.ofEpochSecond(((Long)fieldValues.remove(ChronoField.INSTANT_SECONDS)).longValue());
    ChronoZonedDateTime localChronoZonedDateTime = chrono.zonedDateTime(localInstant, paramZoneId);
    updateCheckConflict(localChronoZonedDateTime.toLocalDate());
    updateCheckConflict(ChronoField.INSTANT_SECONDS, ChronoField.SECOND_OF_DAY, Long.valueOf(localChronoZonedDateTime.toLocalTime().toSecondOfDay()));
  }
  
  private void resolveDateFields()
  {
    updateCheckConflict(chrono.resolveDate(fieldValues, resolverStyle));
  }
  
  private void updateCheckConflict(ChronoLocalDate paramChronoLocalDate)
  {
    if (date != null)
    {
      if ((paramChronoLocalDate != null) && (!date.equals(paramChronoLocalDate))) {
        throw new DateTimeException("Conflict found: Fields resolved to two different dates: " + date + " " + paramChronoLocalDate);
      }
    }
    else if (paramChronoLocalDate != null)
    {
      if (!chrono.equals(paramChronoLocalDate.getChronology())) {
        throw new DateTimeException("ChronoLocalDate must use the effective parsed chronology: " + chrono);
      }
      date = paramChronoLocalDate;
    }
  }
  
  private void resolveTimeFields()
  {
    long l1;
    if (fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_DAY))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.CLOCK_HOUR_OF_DAY)).longValue();
      if ((resolverStyle == ResolverStyle.STRICT) || ((resolverStyle == ResolverStyle.SMART) && (l1 != 0L))) {
        ChronoField.CLOCK_HOUR_OF_DAY.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.CLOCK_HOUR_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l1 == 24L ? 0L : l1));
    }
    if (fieldValues.containsKey(ChronoField.CLOCK_HOUR_OF_AMPM))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.CLOCK_HOUR_OF_AMPM)).longValue();
      if ((resolverStyle == ResolverStyle.STRICT) || ((resolverStyle == ResolverStyle.SMART) && (l1 != 0L))) {
        ChronoField.CLOCK_HOUR_OF_AMPM.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.CLOCK_HOUR_OF_AMPM, ChronoField.HOUR_OF_AMPM, Long.valueOf(l1 == 12L ? 0L : l1));
    }
    long l2;
    if ((fieldValues.containsKey(ChronoField.AMPM_OF_DAY)) && (fieldValues.containsKey(ChronoField.HOUR_OF_AMPM)))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.AMPM_OF_DAY)).longValue();
      l2 = ((Long)fieldValues.remove(ChronoField.HOUR_OF_AMPM)).longValue();
      if (resolverStyle == ResolverStyle.LENIENT)
      {
        updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(Math.addExact(Math.multiplyExact(l1, 12L), l2)));
      }
      else
      {
        ChronoField.AMPM_OF_DAY.checkValidValue(l1);
        ChronoField.HOUR_OF_AMPM.checkValidValue(l1);
        updateCheckConflict(ChronoField.AMPM_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l1 * 12L + l2));
      }
    }
    if (fieldValues.containsKey(ChronoField.NANO_OF_DAY))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.NANO_OF_DAY)).longValue();
      if (resolverStyle != ResolverStyle.LENIENT) {
        ChronoField.NANO_OF_DAY.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l1 / 3600000000000L));
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(l1 / 60000000000L % 60L));
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.SECOND_OF_MINUTE, Long.valueOf(l1 / 1000000000L % 60L));
      updateCheckConflict(ChronoField.NANO_OF_DAY, ChronoField.NANO_OF_SECOND, Long.valueOf(l1 % 1000000000L));
    }
    if (fieldValues.containsKey(ChronoField.MICRO_OF_DAY))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.MICRO_OF_DAY)).longValue();
      if (resolverStyle != ResolverStyle.LENIENT) {
        ChronoField.MICRO_OF_DAY.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.SECOND_OF_DAY, Long.valueOf(l1 / 1000000L));
      updateCheckConflict(ChronoField.MICRO_OF_DAY, ChronoField.MICRO_OF_SECOND, Long.valueOf(l1 % 1000000L));
    }
    if (fieldValues.containsKey(ChronoField.MILLI_OF_DAY))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.MILLI_OF_DAY)).longValue();
      if (resolverStyle != ResolverStyle.LENIENT) {
        ChronoField.MILLI_OF_DAY.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.SECOND_OF_DAY, Long.valueOf(l1 / 1000L));
      updateCheckConflict(ChronoField.MILLI_OF_DAY, ChronoField.MILLI_OF_SECOND, Long.valueOf(l1 % 1000L));
    }
    if (fieldValues.containsKey(ChronoField.SECOND_OF_DAY))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.SECOND_OF_DAY)).longValue();
      if (resolverStyle != ResolverStyle.LENIENT) {
        ChronoField.SECOND_OF_DAY.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l1 / 3600L));
      updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(l1 / 60L % 60L));
      updateCheckConflict(ChronoField.SECOND_OF_DAY, ChronoField.SECOND_OF_MINUTE, Long.valueOf(l1 % 60L));
    }
    if (fieldValues.containsKey(ChronoField.MINUTE_OF_DAY))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.MINUTE_OF_DAY)).longValue();
      if (resolverStyle != ResolverStyle.LENIENT) {
        ChronoField.MINUTE_OF_DAY.checkValidValue(l1);
      }
      updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.HOUR_OF_DAY, Long.valueOf(l1 / 60L));
      updateCheckConflict(ChronoField.MINUTE_OF_DAY, ChronoField.MINUTE_OF_HOUR, Long.valueOf(l1 % 60L));
    }
    if (fieldValues.containsKey(ChronoField.NANO_OF_SECOND))
    {
      l1 = ((Long)fieldValues.get(ChronoField.NANO_OF_SECOND)).longValue();
      if (resolverStyle != ResolverStyle.LENIENT) {
        ChronoField.NANO_OF_SECOND.checkValidValue(l1);
      }
      if (fieldValues.containsKey(ChronoField.MICRO_OF_SECOND))
      {
        l2 = ((Long)fieldValues.remove(ChronoField.MICRO_OF_SECOND)).longValue();
        if (resolverStyle != ResolverStyle.LENIENT) {
          ChronoField.MICRO_OF_SECOND.checkValidValue(l2);
        }
        l1 = l2 * 1000L + l1 % 1000L;
        updateCheckConflict(ChronoField.MICRO_OF_SECOND, ChronoField.NANO_OF_SECOND, Long.valueOf(l1));
      }
      if (fieldValues.containsKey(ChronoField.MILLI_OF_SECOND))
      {
        l2 = ((Long)fieldValues.remove(ChronoField.MILLI_OF_SECOND)).longValue();
        if (resolverStyle != ResolverStyle.LENIENT) {
          ChronoField.MILLI_OF_SECOND.checkValidValue(l2);
        }
        updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.NANO_OF_SECOND, Long.valueOf(l2 * 1000000L + l1 % 1000000L));
      }
    }
    if ((fieldValues.containsKey(ChronoField.HOUR_OF_DAY)) && (fieldValues.containsKey(ChronoField.MINUTE_OF_HOUR)) && (fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE)) && (fieldValues.containsKey(ChronoField.NANO_OF_SECOND)))
    {
      l1 = ((Long)fieldValues.remove(ChronoField.HOUR_OF_DAY)).longValue();
      l2 = ((Long)fieldValues.remove(ChronoField.MINUTE_OF_HOUR)).longValue();
      long l3 = ((Long)fieldValues.remove(ChronoField.SECOND_OF_MINUTE)).longValue();
      long l4 = ((Long)fieldValues.remove(ChronoField.NANO_OF_SECOND)).longValue();
      resolveTime(l1, l2, l3, l4);
    }
  }
  
  private void resolveTimeLenient()
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (time == null)
    {
      long l1;
      if (fieldValues.containsKey(ChronoField.MILLI_OF_SECOND))
      {
        l1 = ((Long)fieldValues.remove(ChronoField.MILLI_OF_SECOND)).longValue();
        if (fieldValues.containsKey(ChronoField.MICRO_OF_SECOND))
        {
          long l2 = l1 * 1000L + ((Long)fieldValues.get(ChronoField.MICRO_OF_SECOND)).longValue() % 1000L;
          updateCheckConflict(ChronoField.MILLI_OF_SECOND, ChronoField.MICRO_OF_SECOND, Long.valueOf(l2));
          fieldValues.remove(ChronoField.MICRO_OF_SECOND);
          fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(l2 * 1000L));
        }
        else
        {
          fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(l1 * 1000000L));
        }
      }
      else if (fieldValues.containsKey(ChronoField.MICRO_OF_SECOND))
      {
        l1 = ((Long)fieldValues.remove(ChronoField.MICRO_OF_SECOND)).longValue();
        fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(l1 * 1000L));
      }
      localObject1 = (Long)fieldValues.get(ChronoField.HOUR_OF_DAY);
      if (localObject1 != null)
      {
        localObject2 = (Long)fieldValues.get(ChronoField.MINUTE_OF_HOUR);
        localObject3 = (Long)fieldValues.get(ChronoField.SECOND_OF_MINUTE);
        Long localLong = (Long)fieldValues.get(ChronoField.NANO_OF_SECOND);
        if (((localObject2 == null) && ((localObject3 != null) || (localLong != null))) || ((localObject2 != null) && (localObject3 == null) && (localLong != null))) {
          return;
        }
        long l3 = localObject2 != null ? ((Long)localObject2).longValue() : 0L;
        long l4 = localObject3 != null ? ((Long)localObject3).longValue() : 0L;
        long l5 = localLong != null ? localLong.longValue() : 0L;
        resolveTime(((Long)localObject1).longValue(), l3, l4, l5);
        fieldValues.remove(ChronoField.HOUR_OF_DAY);
        fieldValues.remove(ChronoField.MINUTE_OF_HOUR);
        fieldValues.remove(ChronoField.SECOND_OF_MINUTE);
        fieldValues.remove(ChronoField.NANO_OF_SECOND);
      }
    }
    if ((resolverStyle != ResolverStyle.LENIENT) && (fieldValues.size() > 0))
    {
      localObject1 = fieldValues.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localObject3 = (TemporalField)((Map.Entry)localObject2).getKey();
        if (((localObject3 instanceof ChronoField)) && (((TemporalField)localObject3).isTimeBased())) {
          ((ChronoField)localObject3).checkValidValue(((Long)((Map.Entry)localObject2).getValue()).longValue());
        }
      }
    }
  }
  
  private void resolveTime(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    int k;
    if (resolverStyle == ResolverStyle.LENIENT)
    {
      long l1 = Math.multiplyExact(paramLong1, 3600000000000L);
      l1 = Math.addExact(l1, Math.multiplyExact(paramLong2, 60000000000L));
      l1 = Math.addExact(l1, Math.multiplyExact(paramLong3, 1000000000L));
      l1 = Math.addExact(l1, paramLong4);
      k = (int)Math.floorDiv(l1, 86400000000000L);
      long l2 = Math.floorMod(l1, 86400000000000L);
      updateCheckConflict(LocalTime.ofNanoOfDay(l2), Period.ofDays(k));
    }
    else
    {
      int i = ChronoField.MINUTE_OF_HOUR.checkValidIntValue(paramLong2);
      int j = ChronoField.NANO_OF_SECOND.checkValidIntValue(paramLong4);
      if ((resolverStyle == ResolverStyle.SMART) && (paramLong1 == 24L) && (i == 0) && (paramLong3 == 0L) && (j == 0))
      {
        updateCheckConflict(LocalTime.MIDNIGHT, Period.ofDays(1));
      }
      else
      {
        k = ChronoField.HOUR_OF_DAY.checkValidIntValue(paramLong1);
        int m = ChronoField.SECOND_OF_MINUTE.checkValidIntValue(paramLong3);
        updateCheckConflict(LocalTime.of(k, i, m, j), Period.ZERO);
      }
    }
  }
  
  private void resolvePeriod()
  {
    if ((date != null) && (time != null) && (!excessDays.isZero()))
    {
      date = date.plus(excessDays);
      excessDays = Period.ZERO;
    }
  }
  
  private void resolveFractional()
  {
    if ((time == null) && ((fieldValues.containsKey(ChronoField.INSTANT_SECONDS)) || (fieldValues.containsKey(ChronoField.SECOND_OF_DAY)) || (fieldValues.containsKey(ChronoField.SECOND_OF_MINUTE)))) {
      if (fieldValues.containsKey(ChronoField.NANO_OF_SECOND))
      {
        long l = ((Long)fieldValues.get(ChronoField.NANO_OF_SECOND)).longValue();
        fieldValues.put(ChronoField.MICRO_OF_SECOND, Long.valueOf(l / 1000L));
        fieldValues.put(ChronoField.MILLI_OF_SECOND, Long.valueOf(l / 1000000L));
      }
      else
      {
        fieldValues.put(ChronoField.NANO_OF_SECOND, Long.valueOf(0L));
        fieldValues.put(ChronoField.MICRO_OF_SECOND, Long.valueOf(0L));
        fieldValues.put(ChronoField.MILLI_OF_SECOND, Long.valueOf(0L));
      }
    }
  }
  
  private void resolveInstant()
  {
    if ((date != null) && (time != null)) {
      if (zone != null)
      {
        long l1 = date.atTime(time).atZone(zone).getLong(ChronoField.INSTANT_SECONDS);
        fieldValues.put(ChronoField.INSTANT_SECONDS, Long.valueOf(l1));
      }
      else
      {
        Long localLong = (Long)fieldValues.get(ChronoField.OFFSET_SECONDS);
        if (localLong != null)
        {
          ZoneOffset localZoneOffset = ZoneOffset.ofTotalSeconds(localLong.intValue());
          long l2 = date.atTime(time).atZone(localZoneOffset).getLong(ChronoField.INSTANT_SECONDS);
          fieldValues.put(ChronoField.INSTANT_SECONDS, Long.valueOf(l2));
        }
      }
    }
  }
  
  private void updateCheckConflict(LocalTime paramLocalTime, Period paramPeriod)
  {
    if (time != null)
    {
      if (!time.equals(paramLocalTime)) {
        throw new DateTimeException("Conflict found: Fields resolved to different times: " + time + " " + paramLocalTime);
      }
      if ((!excessDays.isZero()) && (!paramPeriod.isZero()) && (!excessDays.equals(paramPeriod))) {
        throw new DateTimeException("Conflict found: Fields resolved to different excess periods: " + excessDays + " " + paramPeriod);
      }
      excessDays = paramPeriod;
    }
    else
    {
      time = paramLocalTime;
      excessDays = paramPeriod;
    }
  }
  
  private void crossCheck()
  {
    if (date != null) {
      crossCheck(date);
    }
    if (time != null)
    {
      crossCheck(time);
      if ((date != null) && (fieldValues.size() > 0)) {
        crossCheck(date.atTime(time));
      }
    }
  }
  
  private void crossCheck(TemporalAccessor paramTemporalAccessor)
  {
    Iterator localIterator = fieldValues.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      TemporalField localTemporalField = (TemporalField)localEntry.getKey();
      if (paramTemporalAccessor.isSupported(localTemporalField))
      {
        long l1;
        try
        {
          l1 = paramTemporalAccessor.getLong(localTemporalField);
        }
        catch (RuntimeException localRuntimeException) {}
        continue;
        long l2 = ((Long)localEntry.getValue()).longValue();
        if (l1 != l2) {
          throw new DateTimeException("Conflict found: Field " + localTemporalField + " " + l1 + " differs from " + localTemporalField + " " + l2 + " derived from " + paramTemporalAccessor);
        }
        localIterator.remove();
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    localStringBuilder.append(fieldValues).append(',').append(chrono);
    if (zone != null) {
      localStringBuilder.append(',').append(zone);
    }
    if ((date != null) || (time != null))
    {
      localStringBuilder.append(" resolved to ");
      if (date != null)
      {
        localStringBuilder.append(date);
        if (time != null) {
          localStringBuilder.append('T').append(time);
        }
      }
      else
      {
        localStringBuilder.append(time);
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\format\Parsed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
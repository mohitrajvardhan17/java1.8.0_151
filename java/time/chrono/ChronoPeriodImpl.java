package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class ChronoPeriodImpl
  implements ChronoPeriod, Serializable
{
  private static final long serialVersionUID = 57387258289L;
  private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[] { ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS }));
  private final Chronology chrono;
  final int years;
  final int months;
  final int days;
  
  ChronoPeriodImpl(Chronology paramChronology, int paramInt1, int paramInt2, int paramInt3)
  {
    Objects.requireNonNull(paramChronology, "chrono");
    chrono = paramChronology;
    years = paramInt1;
    months = paramInt2;
    days = paramInt3;
  }
  
  public long get(TemporalUnit paramTemporalUnit)
  {
    if (paramTemporalUnit == ChronoUnit.YEARS) {
      return years;
    }
    if (paramTemporalUnit == ChronoUnit.MONTHS) {
      return months;
    }
    if (paramTemporalUnit == ChronoUnit.DAYS) {
      return days;
    }
    throw new UnsupportedTemporalTypeException("Unsupported unit: " + paramTemporalUnit);
  }
  
  public List<TemporalUnit> getUnits()
  {
    return SUPPORTED_UNITS;
  }
  
  public Chronology getChronology()
  {
    return chrono;
  }
  
  public boolean isZero()
  {
    return (years == 0) && (months == 0) && (days == 0);
  }
  
  public boolean isNegative()
  {
    return (years < 0) || (months < 0) || (days < 0);
  }
  
  public ChronoPeriod plus(TemporalAmount paramTemporalAmount)
  {
    ChronoPeriodImpl localChronoPeriodImpl = validateAmount(paramTemporalAmount);
    return new ChronoPeriodImpl(chrono, Math.addExact(years, years), Math.addExact(months, months), Math.addExact(days, days));
  }
  
  public ChronoPeriod minus(TemporalAmount paramTemporalAmount)
  {
    ChronoPeriodImpl localChronoPeriodImpl = validateAmount(paramTemporalAmount);
    return new ChronoPeriodImpl(chrono, Math.subtractExact(years, years), Math.subtractExact(months, months), Math.subtractExact(days, days));
  }
  
  private ChronoPeriodImpl validateAmount(TemporalAmount paramTemporalAmount)
  {
    Objects.requireNonNull(paramTemporalAmount, "amount");
    if (!(paramTemporalAmount instanceof ChronoPeriodImpl)) {
      throw new DateTimeException("Unable to obtain ChronoPeriod from TemporalAmount: " + paramTemporalAmount.getClass());
    }
    ChronoPeriodImpl localChronoPeriodImpl = (ChronoPeriodImpl)paramTemporalAmount;
    if (!chrono.equals(localChronoPeriodImpl.getChronology())) {
      throw new ClassCastException("Chronology mismatch, expected: " + chrono.getId() + ", actual: " + localChronoPeriodImpl.getChronology().getId());
    }
    return localChronoPeriodImpl;
  }
  
  public ChronoPeriod multipliedBy(int paramInt)
  {
    if ((isZero()) || (paramInt == 1)) {
      return this;
    }
    return new ChronoPeriodImpl(chrono, Math.multiplyExact(years, paramInt), Math.multiplyExact(months, paramInt), Math.multiplyExact(days, paramInt));
  }
  
  public ChronoPeriod normalized()
  {
    long l1 = monthRange();
    if (l1 > 0L)
    {
      long l2 = years * l1 + months;
      long l3 = l2 / l1;
      int i = (int)(l2 % l1);
      if ((l3 == years) && (i == months)) {
        return this;
      }
      return new ChronoPeriodImpl(chrono, Math.toIntExact(l3), i, days);
    }
    return this;
  }
  
  private long monthRange()
  {
    ValueRange localValueRange = chrono.range(ChronoField.MONTH_OF_YEAR);
    if ((localValueRange.isFixed()) && (localValueRange.isIntValue())) {
      return localValueRange.getMaximum() - localValueRange.getMinimum() + 1L;
    }
    return -1L;
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
      long l = monthRange();
      if (l > 0L)
      {
        paramTemporal = paramTemporal.plus(years * l + months, ChronoUnit.MONTHS);
      }
      else
      {
        if (years != 0) {
          paramTemporal = paramTemporal.plus(years, ChronoUnit.YEARS);
        }
        paramTemporal = paramTemporal.plus(months, ChronoUnit.MONTHS);
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
      long l = monthRange();
      if (l > 0L)
      {
        paramTemporal = paramTemporal.minus(years * l + months, ChronoUnit.MONTHS);
      }
      else
      {
        if (years != 0) {
          paramTemporal = paramTemporal.minus(years, ChronoUnit.YEARS);
        }
        paramTemporal = paramTemporal.minus(months, ChronoUnit.MONTHS);
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
    if ((localChronology != null) && (!chrono.equals(localChronology))) {
      throw new DateTimeException("Chronology mismatch, expected: " + chrono.getId() + ", actual: " + localChronology.getId());
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ChronoPeriodImpl))
    {
      ChronoPeriodImpl localChronoPeriodImpl = (ChronoPeriodImpl)paramObject;
      return (years == years) && (months == months) && (days == days) && (chrono.equals(chrono));
    }
    return false;
  }
  
  public int hashCode()
  {
    return years + Integer.rotateLeft(months, 8) + Integer.rotateLeft(days, 16) ^ chrono.hashCode();
  }
  
  public String toString()
  {
    if (isZero()) {
      return getChronology().toString() + " P0D";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getChronology().toString()).append(' ').append('P');
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
  
  protected Object writeReplace()
  {
    return new Ser((byte)9, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ObjectStreamException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeUTF(chrono.getId());
    paramDataOutput.writeInt(years);
    paramDataOutput.writeInt(months);
    paramDataOutput.writeInt(days);
  }
  
  static ChronoPeriodImpl readExternal(DataInput paramDataInput)
    throws IOException
  {
    Chronology localChronology = Chronology.of(paramDataInput.readUTF());
    int i = paramDataInput.readInt();
    int j = paramDataInput.readInt();
    int k = paramDataInput.readInt();
    return new ChronoPeriodImpl(localChronology, i, j, k);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\ChronoPeriodImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
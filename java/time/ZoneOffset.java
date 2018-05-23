package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ZoneOffset
  extends ZoneId
  implements TemporalAccessor, TemporalAdjuster, Comparable<ZoneOffset>, Serializable
{
  private static final ConcurrentMap<Integer, ZoneOffset> SECONDS_CACHE = new ConcurrentHashMap(16, 0.75F, 4);
  private static final ConcurrentMap<String, ZoneOffset> ID_CACHE = new ConcurrentHashMap(16, 0.75F, 4);
  private static final int MAX_SECONDS = 64800;
  private static final long serialVersionUID = 2357656521762053153L;
  public static final ZoneOffset UTC = ofTotalSeconds(0);
  public static final ZoneOffset MIN = ofTotalSeconds(-64800);
  public static final ZoneOffset MAX = ofTotalSeconds(64800);
  private final int totalSeconds;
  private final transient String id;
  
  public static ZoneOffset of(String paramString)
  {
    Objects.requireNonNull(paramString, "offsetId");
    ZoneOffset localZoneOffset = (ZoneOffset)ID_CACHE.get(paramString);
    if (localZoneOffset != null) {
      return localZoneOffset;
    }
    int i;
    int j;
    int k;
    switch (paramString.length())
    {
    case 2: 
      paramString = paramString.charAt(0) + "0" + paramString.charAt(1);
    case 3: 
      i = parseNumber(paramString, 1, false);
      j = 0;
      k = 0;
      break;
    case 5: 
      i = parseNumber(paramString, 1, false);
      j = parseNumber(paramString, 3, false);
      k = 0;
      break;
    case 6: 
      i = parseNumber(paramString, 1, false);
      j = parseNumber(paramString, 4, true);
      k = 0;
      break;
    case 7: 
      i = parseNumber(paramString, 1, false);
      j = parseNumber(paramString, 3, false);
      k = parseNumber(paramString, 5, false);
      break;
    case 9: 
      i = parseNumber(paramString, 1, false);
      j = parseNumber(paramString, 4, true);
      k = parseNumber(paramString, 7, true);
      break;
    case 4: 
    case 8: 
    default: 
      throw new DateTimeException("Invalid ID for ZoneOffset, invalid format: " + paramString);
    }
    int m = paramString.charAt(0);
    if ((m != 43) && (m != 45)) {
      throw new DateTimeException("Invalid ID for ZoneOffset, plus/minus not found when expected: " + paramString);
    }
    if (m == 45) {
      return ofHoursMinutesSeconds(-i, -j, -k);
    }
    return ofHoursMinutesSeconds(i, j, k);
  }
  
  private static int parseNumber(CharSequence paramCharSequence, int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) && (paramCharSequence.charAt(paramInt - 1) != ':')) {
      throw new DateTimeException("Invalid ID for ZoneOffset, colon not found when expected: " + paramCharSequence);
    }
    int i = paramCharSequence.charAt(paramInt);
    int j = paramCharSequence.charAt(paramInt + 1);
    if ((i < 48) || (i > 57) || (j < 48) || (j > 57)) {
      throw new DateTimeException("Invalid ID for ZoneOffset, non numeric characters found: " + paramCharSequence);
    }
    return (i - 48) * 10 + (j - 48);
  }
  
  public static ZoneOffset ofHours(int paramInt)
  {
    return ofHoursMinutesSeconds(paramInt, 0, 0);
  }
  
  public static ZoneOffset ofHoursMinutes(int paramInt1, int paramInt2)
  {
    return ofHoursMinutesSeconds(paramInt1, paramInt2, 0);
  }
  
  public static ZoneOffset ofHoursMinutesSeconds(int paramInt1, int paramInt2, int paramInt3)
  {
    validate(paramInt1, paramInt2, paramInt3);
    int i = totalSeconds(paramInt1, paramInt2, paramInt3);
    return ofTotalSeconds(i);
  }
  
  public static ZoneOffset from(TemporalAccessor paramTemporalAccessor)
  {
    Objects.requireNonNull(paramTemporalAccessor, "temporal");
    ZoneOffset localZoneOffset = (ZoneOffset)paramTemporalAccessor.query(TemporalQueries.offset());
    if (localZoneOffset == null) {
      throw new DateTimeException("Unable to obtain ZoneOffset from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName());
    }
    return localZoneOffset;
  }
  
  private static void validate(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < -18) || (paramInt1 > 18)) {
      throw new DateTimeException("Zone offset hours not in valid range: value " + paramInt1 + " is not in the range -18 to 18");
    }
    if (paramInt1 > 0)
    {
      if ((paramInt2 < 0) || (paramInt3 < 0)) {
        throw new DateTimeException("Zone offset minutes and seconds must be positive because hours is positive");
      }
    }
    else if (paramInt1 < 0)
    {
      if ((paramInt2 > 0) || (paramInt3 > 0)) {
        throw new DateTimeException("Zone offset minutes and seconds must be negative because hours is negative");
      }
    }
    else if (((paramInt2 > 0) && (paramInt3 < 0)) || ((paramInt2 < 0) && (paramInt3 > 0))) {
      throw new DateTimeException("Zone offset minutes and seconds must have the same sign");
    }
    if (Math.abs(paramInt2) > 59) {
      throw new DateTimeException("Zone offset minutes not in valid range: abs(value) " + Math.abs(paramInt2) + " is not in the range 0 to 59");
    }
    if (Math.abs(paramInt3) > 59) {
      throw new DateTimeException("Zone offset seconds not in valid range: abs(value) " + Math.abs(paramInt3) + " is not in the range 0 to 59");
    }
    if ((Math.abs(paramInt1) == 18) && ((Math.abs(paramInt2) > 0) || (Math.abs(paramInt3) > 0))) {
      throw new DateTimeException("Zone offset not in valid range: -18:00 to +18:00");
    }
  }
  
  private static int totalSeconds(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 * 3600 + paramInt2 * 60 + paramInt3;
  }
  
  public static ZoneOffset ofTotalSeconds(int paramInt)
  {
    if (Math.abs(paramInt) > 64800) {
      throw new DateTimeException("Zone offset not in valid range: -18:00 to +18:00");
    }
    if (paramInt % 900 == 0)
    {
      Integer localInteger = Integer.valueOf(paramInt);
      ZoneOffset localZoneOffset = (ZoneOffset)SECONDS_CACHE.get(localInteger);
      if (localZoneOffset == null)
      {
        localZoneOffset = new ZoneOffset(paramInt);
        SECONDS_CACHE.putIfAbsent(localInteger, localZoneOffset);
        localZoneOffset = (ZoneOffset)SECONDS_CACHE.get(localInteger);
        ID_CACHE.putIfAbsent(localZoneOffset.getId(), localZoneOffset);
      }
      return localZoneOffset;
    }
    return new ZoneOffset(paramInt);
  }
  
  private ZoneOffset(int paramInt)
  {
    totalSeconds = paramInt;
    id = buildId(paramInt);
  }
  
  private static String buildId(int paramInt)
  {
    if (paramInt == 0) {
      return "Z";
    }
    int i = Math.abs(paramInt);
    StringBuilder localStringBuilder = new StringBuilder();
    int j = i / 3600;
    int k = i / 60 % 60;
    localStringBuilder.append(paramInt < 0 ? "-" : "+").append(j < 10 ? "0" : "").append(j).append(k < 10 ? ":0" : ":").append(k);
    int m = i % 60;
    if (m != 0) {
      localStringBuilder.append(m < 10 ? ":0" : ":").append(m);
    }
    return localStringBuilder.toString();
  }
  
  public int getTotalSeconds()
  {
    return totalSeconds;
  }
  
  public String getId()
  {
    return id;
  }
  
  public ZoneRules getRules()
  {
    return ZoneRules.of(this);
  }
  
  public boolean isSupported(TemporalField paramTemporalField)
  {
    if ((paramTemporalField instanceof ChronoField)) {
      return paramTemporalField == ChronoField.OFFSET_SECONDS;
    }
    return (paramTemporalField != null) && (paramTemporalField.isSupportedBy(this));
  }
  
  public ValueRange range(TemporalField paramTemporalField)
  {
    return super.range(paramTemporalField);
  }
  
  public int get(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.OFFSET_SECONDS) {
      return totalSeconds;
    }
    if ((paramTemporalField instanceof ChronoField)) {
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return range(paramTemporalField).checkValidIntValue(getLong(paramTemporalField), paramTemporalField);
  }
  
  public long getLong(TemporalField paramTemporalField)
  {
    if (paramTemporalField == ChronoField.OFFSET_SECONDS) {
      return totalSeconds;
    }
    if ((paramTemporalField instanceof ChronoField)) {
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    }
    return paramTemporalField.getFrom(this);
  }
  
  public <R> R query(TemporalQuery<R> paramTemporalQuery)
  {
    if ((paramTemporalQuery == TemporalQueries.offset()) || (paramTemporalQuery == TemporalQueries.zone())) {
      return this;
    }
    return (R)super.query(paramTemporalQuery);
  }
  
  public Temporal adjustInto(Temporal paramTemporal)
  {
    return paramTemporal.with(ChronoField.OFFSET_SECONDS, totalSeconds);
  }
  
  public int compareTo(ZoneOffset paramZoneOffset)
  {
    return totalSeconds - totalSeconds;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ZoneOffset)) {
      return totalSeconds == totalSeconds;
    }
    return false;
  }
  
  public int hashCode()
  {
    return totalSeconds;
  }
  
  public String toString()
  {
    return id;
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)8, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  void write(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeByte(8);
    writeExternal(paramDataOutput);
  }
  
  void writeExternal(DataOutput paramDataOutput)
    throws IOException
  {
    int i = totalSeconds;
    int j = i % 900 == 0 ? i / 900 : 127;
    paramDataOutput.writeByte(j);
    if (j == 127) {
      paramDataOutput.writeInt(i);
    }
  }
  
  static ZoneOffset readExternal(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readByte();
    return i == 127 ? ofTotalSeconds(paramDataInput.readInt()) : ofTotalSeconds(i * 900);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\ZoneOffset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
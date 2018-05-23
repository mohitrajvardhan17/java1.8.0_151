package java.time;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

public abstract class ZoneId
  implements Serializable
{
  public static final Map<String, String> SHORT_IDS;
  private static final long serialVersionUID = 8352817235686L;
  
  public static ZoneId systemDefault()
  {
    return TimeZone.getDefault().toZoneId();
  }
  
  public static Set<String> getAvailableZoneIds()
  {
    return ZoneRulesProvider.getAvailableZoneIds();
  }
  
  public static ZoneId of(String paramString, Map<String, String> paramMap)
  {
    Objects.requireNonNull(paramString, "zoneId");
    Objects.requireNonNull(paramMap, "aliasMap");
    String str = (String)paramMap.get(paramString);
    str = str != null ? str : paramString;
    return of(str);
  }
  
  public static ZoneId of(String paramString)
  {
    return of(paramString, true);
  }
  
  public static ZoneId ofOffset(String paramString, ZoneOffset paramZoneOffset)
  {
    Objects.requireNonNull(paramString, "prefix");
    Objects.requireNonNull(paramZoneOffset, "offset");
    if (paramString.length() == 0) {
      return paramZoneOffset;
    }
    if ((!paramString.equals("GMT")) && (!paramString.equals("UTC")) && (!paramString.equals("UT"))) {
      throw new IllegalArgumentException("prefix should be GMT, UTC or UT, is: " + paramString);
    }
    if (paramZoneOffset.getTotalSeconds() != 0) {
      paramString = paramString.concat(paramZoneOffset.getId());
    }
    return new ZoneRegion(paramString, paramZoneOffset.getRules());
  }
  
  static ZoneId of(String paramString, boolean paramBoolean)
  {
    Objects.requireNonNull(paramString, "zoneId");
    if ((paramString.length() <= 1) || (paramString.startsWith("+")) || (paramString.startsWith("-"))) {
      return ZoneOffset.of(paramString);
    }
    if ((paramString.startsWith("UTC")) || (paramString.startsWith("GMT"))) {
      return ofWithPrefix(paramString, 3, paramBoolean);
    }
    if (paramString.startsWith("UT")) {
      return ofWithPrefix(paramString, 2, paramBoolean);
    }
    return ZoneRegion.ofId(paramString, paramBoolean);
  }
  
  private static ZoneId ofWithPrefix(String paramString, int paramInt, boolean paramBoolean)
  {
    String str = paramString.substring(0, paramInt);
    if (paramString.length() == paramInt) {
      return ofOffset(str, ZoneOffset.UTC);
    }
    if ((paramString.charAt(paramInt) != '+') && (paramString.charAt(paramInt) != '-')) {
      return ZoneRegion.ofId(paramString, paramBoolean);
    }
    try
    {
      ZoneOffset localZoneOffset = ZoneOffset.of(paramString.substring(paramInt));
      if (localZoneOffset == ZoneOffset.UTC) {
        return ofOffset(str, localZoneOffset);
      }
      return ofOffset(str, localZoneOffset);
    }
    catch (DateTimeException localDateTimeException)
    {
      throw new DateTimeException("Invalid ID for offset-based ZoneId: " + paramString, localDateTimeException);
    }
  }
  
  public static ZoneId from(TemporalAccessor paramTemporalAccessor)
  {
    ZoneId localZoneId = (ZoneId)paramTemporalAccessor.query(TemporalQueries.zone());
    if (localZoneId == null) {
      throw new DateTimeException("Unable to obtain ZoneId from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName());
    }
    return localZoneId;
  }
  
  ZoneId()
  {
    if ((getClass() != ZoneOffset.class) && (getClass() != ZoneRegion.class)) {
      throw new AssertionError("Invalid subclass");
    }
  }
  
  public abstract String getId();
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale)
  {
    return new DateTimeFormatterBuilder().appendZoneText(paramTextStyle).toFormatter(paramLocale).format(toTemporal());
  }
  
  private TemporalAccessor toTemporal()
  {
    new TemporalAccessor()
    {
      public boolean isSupported(TemporalField paramAnonymousTemporalField)
      {
        return false;
      }
      
      public long getLong(TemporalField paramAnonymousTemporalField)
      {
        throw new UnsupportedTemporalTypeException("Unsupported field: " + paramAnonymousTemporalField);
      }
      
      public <R> R query(TemporalQuery<R> paramAnonymousTemporalQuery)
      {
        if (paramAnonymousTemporalQuery == TemporalQueries.zoneId()) {
          return ZoneId.this;
        }
        return (R)super.query(paramAnonymousTemporalQuery);
      }
    };
  }
  
  public abstract ZoneRules getRules();
  
  public ZoneId normalized()
  {
    try
    {
      ZoneRules localZoneRules = getRules();
      if (localZoneRules.isFixedOffset()) {
        return localZoneRules.getOffset(Instant.EPOCH);
      }
    }
    catch (ZoneRulesException localZoneRulesException) {}
    return this;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ZoneId))
    {
      ZoneId localZoneId = (ZoneId)paramObject;
      return getId().equals(localZoneId.getId());
    }
    return false;
  }
  
  public int hashCode()
  {
    return getId().hashCode();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws InvalidObjectException
  {
    throw new InvalidObjectException("Deserialization via serialization delegate");
  }
  
  public String toString()
  {
    return getId();
  }
  
  private Object writeReplace()
  {
    return new Ser((byte)7, this);
  }
  
  abstract void write(DataOutput paramDataOutput)
    throws IOException;
  
  static
  {
    HashMap localHashMap = new HashMap(64);
    localHashMap.put("ACT", "Australia/Darwin");
    localHashMap.put("AET", "Australia/Sydney");
    localHashMap.put("AGT", "America/Argentina/Buenos_Aires");
    localHashMap.put("ART", "Africa/Cairo");
    localHashMap.put("AST", "America/Anchorage");
    localHashMap.put("BET", "America/Sao_Paulo");
    localHashMap.put("BST", "Asia/Dhaka");
    localHashMap.put("CAT", "Africa/Harare");
    localHashMap.put("CNT", "America/St_Johns");
    localHashMap.put("CST", "America/Chicago");
    localHashMap.put("CTT", "Asia/Shanghai");
    localHashMap.put("EAT", "Africa/Addis_Ababa");
    localHashMap.put("ECT", "Europe/Paris");
    localHashMap.put("IET", "America/Indiana/Indianapolis");
    localHashMap.put("IST", "Asia/Kolkata");
    localHashMap.put("JST", "Asia/Tokyo");
    localHashMap.put("MIT", "Pacific/Apia");
    localHashMap.put("NET", "Asia/Yerevan");
    localHashMap.put("NST", "Pacific/Auckland");
    localHashMap.put("PLT", "Asia/Karachi");
    localHashMap.put("PNT", "America/Phoenix");
    localHashMap.put("PRT", "America/Puerto_Rico");
    localHashMap.put("PST", "America/Los_Angeles");
    localHashMap.put("SST", "Pacific/Guadalcanal");
    localHashMap.put("VST", "Asia/Ho_Chi_Minh");
    localHashMap.put("EST", "-05:00");
    localHashMap.put("MST", "-07:00");
    localHashMap.put("HST", "-10:00");
    SHORT_IDS = Collections.unmodifiableMap(localHashMap);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\ZoneId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
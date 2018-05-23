package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

public final class Era
{
  private final String name;
  private final String abbr;
  private final long since;
  private final CalendarDate sinceDate;
  private final boolean localTime;
  private int hash = 0;
  
  public Era(String paramString1, String paramString2, long paramLong, boolean paramBoolean)
  {
    name = paramString1;
    abbr = paramString2;
    since = paramLong;
    localTime = paramBoolean;
    Gregorian localGregorian = CalendarSystem.getGregorianCalendar();
    Gregorian.Date localDate = localGregorian.newCalendarDate(null);
    localGregorian.getCalendarDate(paramLong, localDate);
    sinceDate = new ImmutableGregorianDate(localDate);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDisplayName(Locale paramLocale)
  {
    return name;
  }
  
  public String getAbbreviation()
  {
    return abbr;
  }
  
  public String getDiaplayAbbreviation(Locale paramLocale)
  {
    return abbr;
  }
  
  public long getSince(TimeZone paramTimeZone)
  {
    if ((paramTimeZone == null) || (!localTime)) {
      return since;
    }
    int i = paramTimeZone.getOffset(since);
    return since - i;
  }
  
  public CalendarDate getSinceDate()
  {
    return sinceDate;
  }
  
  public boolean isLocalTime()
  {
    return localTime;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Era)) {
      return false;
    }
    Era localEra = (Era)paramObject;
    return (name.equals(name)) && (abbr.equals(abbr)) && (since == since) && (localTime == localTime);
  }
  
  public int hashCode()
  {
    if (hash == 0) {
      hash = (name.hashCode() ^ abbr.hashCode() ^ (int)since ^ (int)(since >> 32) ^ (localTime ? 1 : 0));
    }
    return hash;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    localStringBuilder.append(getName()).append(" (");
    localStringBuilder.append(getAbbreviation()).append(')');
    localStringBuilder.append(" since ").append(getSinceDate());
    if (localTime)
    {
      localStringBuilder.setLength(localStringBuilder.length() - 1);
      localStringBuilder.append(" local time");
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\Era.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
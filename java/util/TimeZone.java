package java.util;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.ZoneId;
import sun.security.action.GetPropertyAction;
import sun.util.calendar.ZoneInfo;
import sun.util.calendar.ZoneInfoFile;
import sun.util.locale.provider.TimeZoneNameUtility;

public abstract class TimeZone
  implements Serializable, Cloneable
{
  public static final int SHORT = 0;
  public static final int LONG = 1;
  private static final int ONE_MINUTE = 60000;
  private static final int ONE_HOUR = 3600000;
  private static final int ONE_DAY = 86400000;
  static final long serialVersionUID = 3581463369166924961L;
  static final TimeZone NO_TIMEZONE = null;
  private String ID;
  private static volatile TimeZone defaultTimeZone;
  static final String GMT_ID = "GMT";
  private static final int GMT_ID_LENGTH = 3;
  private static volatile TimeZone mainAppContextDefault;
  
  public TimeZone() {}
  
  public abstract int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public int getOffset(long paramLong)
  {
    if (inDaylightTime(new Date(paramLong))) {
      return getRawOffset() + getDSTSavings();
    }
    return getRawOffset();
  }
  
  int getOffsets(long paramLong, int[] paramArrayOfInt)
  {
    int i = getRawOffset();
    int j = 0;
    if (inDaylightTime(new Date(paramLong))) {
      j = getDSTSavings();
    }
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = i;
      paramArrayOfInt[1] = j;
    }
    return i + j;
  }
  
  public abstract void setRawOffset(int paramInt);
  
  public abstract int getRawOffset();
  
  public String getID()
  {
    return ID;
  }
  
  public void setID(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    ID = paramString;
  }
  
  public final String getDisplayName()
  {
    return getDisplayName(false, 1, Locale.getDefault(Locale.Category.DISPLAY));
  }
  
  public final String getDisplayName(Locale paramLocale)
  {
    return getDisplayName(false, 1, paramLocale);
  }
  
  public final String getDisplayName(boolean paramBoolean, int paramInt)
  {
    return getDisplayName(paramBoolean, paramInt, Locale.getDefault(Locale.Category.DISPLAY));
  }
  
  public String getDisplayName(boolean paramBoolean, int paramInt, Locale paramLocale)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Illegal style: " + paramInt);
    }
    String str1 = getID();
    String str2 = TimeZoneNameUtility.retrieveDisplayName(str1, paramBoolean, paramInt, paramLocale);
    if (str2 != null) {
      return str2;
    }
    if ((str1.startsWith("GMT")) && (str1.length() > 3))
    {
      i = str1.charAt(3);
      if ((i == 43) || (i == 45)) {
        return str1;
      }
    }
    int i = getRawOffset();
    if (paramBoolean) {
      i += getDSTSavings();
    }
    return ZoneInfoFile.toCustomID(i);
  }
  
  private static String[] getDisplayNames(String paramString, Locale paramLocale)
  {
    return TimeZoneNameUtility.retrieveDisplayNames(paramString, paramLocale);
  }
  
  public int getDSTSavings()
  {
    if (useDaylightTime()) {
      return 3600000;
    }
    return 0;
  }
  
  public abstract boolean useDaylightTime();
  
  public boolean observesDaylightTime()
  {
    return (useDaylightTime()) || (inDaylightTime(new Date()));
  }
  
  public abstract boolean inDaylightTime(Date paramDate);
  
  public static synchronized TimeZone getTimeZone(String paramString)
  {
    return getTimeZone(paramString, true);
  }
  
  public static TimeZone getTimeZone(ZoneId paramZoneId)
  {
    String str = paramZoneId.getId();
    int i = str.charAt(0);
    if ((i == 43) || (i == 45)) {
      str = "GMT" + str;
    } else if ((i == 90) && (str.length() == 1)) {
      str = "UTC";
    }
    return getTimeZone(str, true);
  }
  
  public ZoneId toZoneId()
  {
    String str = getID();
    if ((ZoneInfoFile.useOldMapping()) && (str.length() == 3))
    {
      if ("EST".equals(str)) {
        return ZoneId.of("America/New_York");
      }
      if ("MST".equals(str)) {
        return ZoneId.of("America/Denver");
      }
      if ("HST".equals(str)) {
        return ZoneId.of("America/Honolulu");
      }
    }
    return ZoneId.of(str, ZoneId.SHORT_IDS);
  }
  
  private static TimeZone getTimeZone(String paramString, boolean paramBoolean)
  {
    Object localObject = ZoneInfo.getTimeZone(paramString);
    if (localObject == null)
    {
      localObject = parseCustomTimeZone(paramString);
      if ((localObject == null) && (paramBoolean)) {
        localObject = new ZoneInfo("GMT", 0);
      }
    }
    return (TimeZone)localObject;
  }
  
  public static synchronized String[] getAvailableIDs(int paramInt)
  {
    return ZoneInfo.getAvailableIDs(paramInt);
  }
  
  public static synchronized String[] getAvailableIDs()
  {
    return ZoneInfo.getAvailableIDs();
  }
  
  private static native String getSystemTimeZoneID(String paramString);
  
  private static native String getSystemGMTOffsetID();
  
  public static TimeZone getDefault()
  {
    return (TimeZone)getDefaultRef().clone();
  }
  
  static TimeZone getDefaultRef()
  {
    TimeZone localTimeZone = defaultTimeZone;
    if (localTimeZone == null)
    {
      localTimeZone = setDefaultZone();
      assert (localTimeZone != null);
    }
    return localTimeZone;
  }
  
  private static synchronized TimeZone setDefaultZone()
  {
    Object localObject1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.timezone"));
    if ((localObject1 == null) || (((String)localObject1).isEmpty()))
    {
      localObject2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
      try
      {
        localObject1 = getSystemTimeZoneID((String)localObject2);
        if (localObject1 == null) {
          localObject1 = "GMT";
        }
      }
      catch (NullPointerException localNullPointerException)
      {
        localObject1 = "GMT";
      }
    }
    TimeZone localTimeZone = getTimeZone((String)localObject1, false);
    if (localTimeZone == null)
    {
      localObject2 = getSystemGMTOffsetID();
      if (localObject2 != null) {
        localObject1 = localObject2;
      }
      localTimeZone = getTimeZone((String)localObject1, true);
    }
    assert (localTimeZone != null);
    Object localObject2 = localObject1;
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.setProperty("user.timezone", val$id);
        return null;
      }
    });
    defaultTimeZone = localTimeZone;
    return localTimeZone;
  }
  
  public static void setDefault(TimeZone paramTimeZone)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new PropertyPermission("user.timezone", "write"));
    }
    defaultTimeZone = paramTimeZone;
  }
  
  public boolean hasSameRules(TimeZone paramTimeZone)
  {
    return (paramTimeZone != null) && (getRawOffset() == paramTimeZone.getRawOffset()) && (useDaylightTime() == paramTimeZone.useDaylightTime());
  }
  
  public Object clone()
  {
    try
    {
      TimeZone localTimeZone = (TimeZone)super.clone();
      ID = ID;
      return localTimeZone;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  private static final TimeZone parseCustomTimeZone(String paramString)
  {
    int i;
    if (((i = paramString.length()) < 5) || (paramString.indexOf("GMT") != 0)) {
      return null;
    }
    ZoneInfo localZoneInfo = ZoneInfoFile.getZoneInfo(paramString);
    if (localZoneInfo != null) {
      return localZoneInfo;
    }
    int j = 3;
    int k = 0;
    int m = paramString.charAt(j++);
    if (m == 45) {
      k = 1;
    } else if (m != 43) {
      return null;
    }
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    while (j < i)
    {
      m = paramString.charAt(j++);
      if (m == 58)
      {
        if (i2 > 0) {
          return null;
        }
        if (i3 > 2) {
          return null;
        }
        n = i1;
        i2++;
        i1 = 0;
        i3 = 0;
      }
      else
      {
        if ((m < 48) || (m > 57)) {
          return null;
        }
        i1 = i1 * 10 + (m - 48);
        i3++;
      }
    }
    if (j != i) {
      return null;
    }
    if (i2 == 0)
    {
      if (i3 <= 2)
      {
        n = i1;
        i1 = 0;
      }
      else
      {
        n = i1 / 100;
        i1 %= 100;
      }
    }
    else if (i3 != 2) {
      return null;
    }
    if ((n > 23) || (i1 > 59)) {
      return null;
    }
    int i4 = (n * 60 + i1) * 60 * 1000;
    if (i4 == 0)
    {
      localZoneInfo = ZoneInfoFile.getZoneInfo("GMT");
      if (k != 0) {
        localZoneInfo.setID("GMT-00:00");
      } else {
        localZoneInfo.setID("GMT+00:00");
      }
    }
    else
    {
      localZoneInfo = ZoneInfoFile.getCustomTimeZone(paramString, k != 0 ? -i4 : i4);
    }
    return localZoneInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\TimeZone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
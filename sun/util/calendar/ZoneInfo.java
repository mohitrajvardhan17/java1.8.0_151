package sun.util.calendar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class ZoneInfo
  extends TimeZone
{
  private static final int UTC_TIME = 0;
  private static final int STANDARD_TIME = 1;
  private static final int WALL_TIME = 2;
  private static final long OFFSET_MASK = 15L;
  private static final long DST_MASK = 240L;
  private static final int DST_NSHIFT = 4;
  private static final long ABBR_MASK = 3840L;
  private static final int TRANSITION_NSHIFT = 12;
  private static final CalendarSystem gcal = ;
  private int rawOffset;
  private int rawOffsetDiff = 0;
  private int checksum;
  private int dstSavings;
  private long[] transitions;
  private int[] offsets;
  private int[] simpleTimeZoneParams;
  private boolean willGMTOffsetChange = false;
  private transient boolean dirty = false;
  private static final long serialVersionUID = 2653134537216586139L;
  private transient SimpleTimeZone lastRule;
  
  public ZoneInfo() {}
  
  public ZoneInfo(String paramString, int paramInt)
  {
    this(paramString, paramInt, 0, 0, null, null, null, false);
  }
  
  ZoneInfo(String paramString, int paramInt1, int paramInt2, int paramInt3, long[] paramArrayOfLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
  {
    setID(paramString);
    rawOffset = paramInt1;
    dstSavings = paramInt2;
    checksum = paramInt3;
    transitions = paramArrayOfLong;
    offsets = paramArrayOfInt1;
    simpleTimeZoneParams = paramArrayOfInt2;
    willGMTOffsetChange = paramBoolean;
  }
  
  public int getOffset(long paramLong)
  {
    return getOffsets(paramLong, null, 0);
  }
  
  public int getOffsets(long paramLong, int[] paramArrayOfInt)
  {
    return getOffsets(paramLong, paramArrayOfInt, 0);
  }
  
  public int getOffsetsByStandard(long paramLong, int[] paramArrayOfInt)
  {
    return getOffsets(paramLong, paramArrayOfInt, 1);
  }
  
  public int getOffsetsByWall(long paramLong, int[] paramArrayOfInt)
  {
    return getOffsets(paramLong, paramArrayOfInt, 2);
  }
  
  private int getOffsets(long paramLong, int[] paramArrayOfInt, int paramInt)
  {
    if (transitions == null)
    {
      i = getLastRawOffset();
      if (paramArrayOfInt != null)
      {
        paramArrayOfInt[0] = i;
        paramArrayOfInt[1] = 0;
      }
      return i;
    }
    paramLong -= rawOffsetDiff;
    int i = getTransitionIndex(paramLong, paramInt);
    if (i < 0)
    {
      int j = getLastRawOffset();
      if (paramArrayOfInt != null)
      {
        paramArrayOfInt[0] = j;
        paramArrayOfInt[1] = 0;
      }
      return j;
    }
    int i1;
    if (i < transitions.length)
    {
      long l1 = transitions[i];
      int m = offsets[((int)(l1 & 0xF))] + rawOffsetDiff;
      if (paramArrayOfInt != null)
      {
        int n = (int)(l1 >>> 4 & 0xF);
        i1 = n == 0 ? 0 : offsets[n];
        paramArrayOfInt[0] = (m - i1);
        paramArrayOfInt[1] = i1;
      }
      return m;
    }
    SimpleTimeZone localSimpleTimeZone = getLastRule();
    if (localSimpleTimeZone != null)
    {
      k = localSimpleTimeZone.getRawOffset();
      long l2 = paramLong;
      if (paramInt != 0) {
        l2 -= rawOffset;
      }
      i1 = localSimpleTimeZone.getOffset(l2) - rawOffset;
      if ((i1 > 0) && (localSimpleTimeZone.getOffset(l2 - i1) == k)) {
        i1 = 0;
      }
      if (paramArrayOfInt != null)
      {
        paramArrayOfInt[0] = k;
        paramArrayOfInt[1] = i1;
      }
      return k + i1;
    }
    int k = getLastRawOffset();
    if (paramArrayOfInt != null)
    {
      paramArrayOfInt[0] = k;
      paramArrayOfInt[1] = 0;
    }
    return k;
  }
  
  private int getTransitionIndex(long paramLong, int paramInt)
  {
    int i = 0;
    int j = transitions.length - 1;
    while (i <= j)
    {
      int k = (i + j) / 2;
      long l1 = transitions[k];
      long l2 = l1 >> 12;
      if (paramInt != 0) {
        l2 += offsets[((int)(l1 & 0xF))];
      }
      if (paramInt == 1)
      {
        int m = (int)(l1 >>> 4 & 0xF);
        if (m != 0) {
          l2 -= offsets[m];
        }
      }
      if (l2 < paramLong) {
        i = k + 1;
      } else if (l2 > paramLong) {
        j = k - 1;
      } else {
        return k;
      }
    }
    if (i >= transitions.length) {
      return i;
    }
    return i - 1;
  }
  
  public int getOffset(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if ((paramInt6 < 0) || (paramInt6 >= 86400000)) {
      throw new IllegalArgumentException();
    }
    if (paramInt1 == 0) {
      paramInt2 = 1 - paramInt2;
    } else if (paramInt1 != 1) {
      throw new IllegalArgumentException();
    }
    CalendarDate localCalendarDate = gcal.newCalendarDate(null);
    localCalendarDate.setDate(paramInt2, paramInt3 + 1, paramInt4);
    if (!gcal.validate(localCalendarDate)) {
      throw new IllegalArgumentException();
    }
    if ((paramInt5 < 1) || (paramInt5 > 7)) {
      throw new IllegalArgumentException();
    }
    if (transitions == null) {
      return getLastRawOffset();
    }
    long l = gcal.getTime(localCalendarDate) + paramInt6;
    l -= rawOffset;
    return getOffsets(l, null, 0);
  }
  
  public synchronized void setRawOffset(int paramInt)
  {
    if (paramInt == rawOffset + rawOffsetDiff) {
      return;
    }
    rawOffsetDiff = (paramInt - rawOffset);
    if (lastRule != null) {
      lastRule.setRawOffset(paramInt);
    }
    dirty = true;
  }
  
  public int getRawOffset()
  {
    if (!willGMTOffsetChange) {
      return rawOffset + rawOffsetDiff;
    }
    int[] arrayOfInt = new int[2];
    getOffsets(System.currentTimeMillis(), arrayOfInt, 0);
    return arrayOfInt[0];
  }
  
  public boolean isDirty()
  {
    return dirty;
  }
  
  private int getLastRawOffset()
  {
    return rawOffset + rawOffsetDiff;
  }
  
  public boolean useDaylightTime()
  {
    return simpleTimeZoneParams != null;
  }
  
  public boolean observesDaylightTime()
  {
    if (simpleTimeZoneParams != null) {
      return true;
    }
    if (transitions == null) {
      return false;
    }
    long l = System.currentTimeMillis() - rawOffsetDiff;
    int i = getTransitionIndex(l, 0);
    if (i < 0) {
      return false;
    }
    for (int j = i; j < transitions.length; j++) {
      if ((transitions[j] & 0xF0) != 0L) {
        return true;
      }
    }
    return false;
  }
  
  public boolean inDaylightTime(Date paramDate)
  {
    if (paramDate == null) {
      throw new NullPointerException();
    }
    if (transitions == null) {
      return false;
    }
    long l = paramDate.getTime() - rawOffsetDiff;
    int i = getTransitionIndex(l, 0);
    if (i < 0) {
      return false;
    }
    if (i < transitions.length) {
      return (transitions[i] & 0xF0) != 0L;
    }
    SimpleTimeZone localSimpleTimeZone = getLastRule();
    if (localSimpleTimeZone != null) {
      return localSimpleTimeZone.inDaylightTime(paramDate);
    }
    return false;
  }
  
  public int getDSTSavings()
  {
    return dstSavings;
  }
  
  public String toString()
  {
    return getClass().getName() + "[id=\"" + getID() + "\",offset=" + getLastRawOffset() + ",dstSavings=" + dstSavings + ",useDaylight=" + useDaylightTime() + ",transitions=" + (transitions != null ? transitions.length : 0) + ",lastRule=" + (lastRule == null ? getLastRuleInstance() : lastRule) + "]";
  }
  
  public static String[] getAvailableIDs()
  {
    return ZoneInfoFile.getZoneIds();
  }
  
  public static String[] getAvailableIDs(int paramInt)
  {
    return ZoneInfoFile.getZoneIds(paramInt);
  }
  
  public static TimeZone getTimeZone(String paramString)
  {
    return ZoneInfoFile.getZoneInfo(paramString);
  }
  
  private synchronized SimpleTimeZone getLastRule()
  {
    if (lastRule == null) {
      lastRule = getLastRuleInstance();
    }
    return lastRule;
  }
  
  public SimpleTimeZone getLastRuleInstance()
  {
    if (simpleTimeZoneParams == null) {
      return null;
    }
    if (simpleTimeZoneParams.length == 10) {
      return new SimpleTimeZone(getLastRawOffset(), getID(), simpleTimeZoneParams[0], simpleTimeZoneParams[1], simpleTimeZoneParams[2], simpleTimeZoneParams[3], simpleTimeZoneParams[4], simpleTimeZoneParams[5], simpleTimeZoneParams[6], simpleTimeZoneParams[7], simpleTimeZoneParams[8], simpleTimeZoneParams[9], dstSavings);
    }
    return new SimpleTimeZone(getLastRawOffset(), getID(), simpleTimeZoneParams[0], simpleTimeZoneParams[1], simpleTimeZoneParams[2], simpleTimeZoneParams[3], simpleTimeZoneParams[4], simpleTimeZoneParams[5], simpleTimeZoneParams[6], simpleTimeZoneParams[7], dstSavings);
  }
  
  public Object clone()
  {
    ZoneInfo localZoneInfo = (ZoneInfo)super.clone();
    lastRule = null;
    return localZoneInfo;
  }
  
  public int hashCode()
  {
    return getLastRawOffset() ^ checksum;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ZoneInfo)) {
      return false;
    }
    ZoneInfo localZoneInfo = (ZoneInfo)paramObject;
    return (getID().equals(localZoneInfo.getID())) && (getLastRawOffset() == localZoneInfo.getLastRawOffset()) && (checksum == checksum);
  }
  
  public boolean hasSameRules(TimeZone paramTimeZone)
  {
    if (this == paramTimeZone) {
      return true;
    }
    if (paramTimeZone == null) {
      return false;
    }
    if (!(paramTimeZone instanceof ZoneInfo))
    {
      if (getRawOffset() != paramTimeZone.getRawOffset()) {
        return false;
      }
      return (transitions == null) && (!useDaylightTime()) && (!paramTimeZone.useDaylightTime());
    }
    if (getLastRawOffset() != ((ZoneInfo)paramTimeZone).getLastRawOffset()) {
      return false;
    }
    return checksum == checksum;
  }
  
  public static Map<String, String> getAliasTable()
  {
    return ZoneInfoFile.getAliasMap();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    dirty = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\ZoneInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
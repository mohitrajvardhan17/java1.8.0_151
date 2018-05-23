package sun.util.calendar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;
import sun.security.action.GetPropertyAction;

public final class ZoneInfoFile
{
  private static String versionId;
  private static final Map<String, ZoneInfo> zones = new ConcurrentHashMap();
  private static Map<String, String> aliases = new HashMap();
  private static byte[][] ruleArray;
  private static String[] regions;
  private static int[] indices;
  private static final boolean USE_OLDMAPPING;
  private static String[][] oldMappings = { { "ACT", "Australia/Darwin" }, { "AET", "Australia/Sydney" }, { "AGT", "America/Argentina/Buenos_Aires" }, { "ART", "Africa/Cairo" }, { "AST", "America/Anchorage" }, { "BET", "America/Sao_Paulo" }, { "BST", "Asia/Dhaka" }, { "CAT", "Africa/Harare" }, { "CNT", "America/St_Johns" }, { "CST", "America/Chicago" }, { "CTT", "Asia/Shanghai" }, { "EAT", "Africa/Addis_Ababa" }, { "ECT", "Europe/Paris" }, { "IET", "America/Indiana/Indianapolis" }, { "IST", "Asia/Kolkata" }, { "JST", "Asia/Tokyo" }, { "MIT", "Pacific/Apia" }, { "NET", "Asia/Yerevan" }, { "NST", "Pacific/Auckland" }, { "PLT", "Asia/Karachi" }, { "PNT", "America/Phoenix" }, { "PRT", "America/Puerto_Rico" }, { "PST", "America/Los_Angeles" }, { "SST", "Pacific/Guadalcanal" }, { "VST", "Asia/Ho_Chi_Minh" } };
  private static final long UTC1900 = -2208988800L;
  private static final long UTC2037 = 2145916799L;
  private static final long LDT2037 = 2114380800L;
  private static final long CURRT = System.currentTimeMillis() / 1000L;
  static final int SECONDS_PER_DAY = 86400;
  static final int DAYS_PER_CYCLE = 146097;
  static final long DAYS_0000_TO_1970 = 719528L;
  private static final int[] toCalendarDOW = { -1, 2, 3, 4, 5, 6, 7, 1 };
  private static final int[] toSTZTime = { 2, 0, 1 };
  private static final long OFFSET_MASK = 15L;
  private static final long DST_MASK = 240L;
  private static final int DST_NSHIFT = 4;
  private static final int TRANSITION_NSHIFT = 12;
  private static final int LASTYEAR = 2037;
  
  public static String[] getZoneIds()
  {
    int i = regions.length + oldMappings.length;
    if (!USE_OLDMAPPING) {
      i += 3;
    }
    String[] arrayOfString = (String[])Arrays.copyOf(regions, i);
    int j = regions.length;
    if (!USE_OLDMAPPING)
    {
      arrayOfString[(j++)] = "EST";
      arrayOfString[(j++)] = "HST";
      arrayOfString[(j++)] = "MST";
    }
    for (int k = 0; k < oldMappings.length; k++) {
      arrayOfString[(j++)] = oldMappings[k][0];
    }
    return arrayOfString;
  }
  
  public static String[] getZoneIds(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    for (String str : getZoneIds())
    {
      ZoneInfo localZoneInfo = getZoneInfo(str);
      if (localZoneInfo.getRawOffset() == paramInt) {
        localArrayList.add(str);
      }
    }
    ??? = (String[])localArrayList.toArray(new String[localArrayList.size()]);
    Arrays.sort(???);
    return (String[])???;
  }
  
  public static ZoneInfo getZoneInfo(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    ZoneInfo localZoneInfo = getZoneInfo0(paramString);
    if (localZoneInfo != null)
    {
      localZoneInfo = (ZoneInfo)localZoneInfo.clone();
      localZoneInfo.setID(paramString);
    }
    return localZoneInfo;
  }
  
  private static ZoneInfo getZoneInfo0(String paramString)
  {
    try
    {
      ZoneInfo localZoneInfo = (ZoneInfo)zones.get(paramString);
      if (localZoneInfo != null) {
        return localZoneInfo;
      }
      String str = paramString;
      if (aliases.containsKey(paramString)) {
        str = (String)aliases.get(paramString);
      }
      int i = Arrays.binarySearch(regions, str);
      if (i < 0) {
        return null;
      }
      byte[] arrayOfByte = ruleArray[indices[i]];
      DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
      localZoneInfo = getZoneInfo(localDataInputStream, str);
      zones.put(paramString, localZoneInfo);
      return localZoneInfo;
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Invalid binary time-zone data: TZDB:" + paramString + ", version: " + versionId, localException);
    }
  }
  
  public static Map<String, String> getAliasMap()
  {
    return Collections.unmodifiableMap(aliases);
  }
  
  public static String getVersion()
  {
    return versionId;
  }
  
  public static ZoneInfo getCustomTimeZone(String paramString, int paramInt)
  {
    String str = toCustomID(paramInt);
    return new ZoneInfo(str, paramInt);
  }
  
  public static String toCustomID(int paramInt)
  {
    int j = paramInt / 60000;
    int i;
    if (j >= 0)
    {
      i = 43;
    }
    else
    {
      i = 45;
      j = -j;
    }
    int k = j / 60;
    int m = j % 60;
    char[] arrayOfChar = { 'G', 'M', 'T', i, '0', '0', ':', '0', '0' };
    if (k >= 10)
    {
      int tmp94_93 = 4;
      char[] tmp94_91 = arrayOfChar;
      tmp94_91[tmp94_93] = ((char)(tmp94_91[tmp94_93] + k / 10));
    }
    int tmp106_105 = 5;
    char[] tmp106_103 = arrayOfChar;
    tmp106_103[tmp106_105] = ((char)(tmp106_103[tmp106_105] + k % 10));
    if (m != 0)
    {
      char[] tmp124_120 = arrayOfChar;
      tmp124_120[7] = ((char)(tmp124_120[7] + m / 10));
      char[] tmp138_134 = arrayOfChar;
      tmp138_134[8] = ((char)(tmp138_134[8] + m % 10));
    }
    return new String(arrayOfChar);
  }
  
  private ZoneInfoFile() {}
  
  private static void addOldMapping()
  {
    for (String[] arrayOfString1 : oldMappings) {
      aliases.put(arrayOfString1[0], arrayOfString1[1]);
    }
    if (USE_OLDMAPPING)
    {
      aliases.put("EST", "America/New_York");
      aliases.put("MST", "America/Denver");
      aliases.put("HST", "Pacific/Honolulu");
    }
    else
    {
      zones.put("EST", new ZoneInfo("EST", -18000000));
      zones.put("MST", new ZoneInfo("MST", -25200000));
      zones.put("HST", new ZoneInfo("HST", -36000000));
    }
  }
  
  public static boolean useOldMapping()
  {
    return USE_OLDMAPPING;
  }
  
  private static void load(DataInputStream paramDataInputStream)
    throws ClassNotFoundException, IOException
  {
    if (paramDataInputStream.readByte() != 1) {
      throw new StreamCorruptedException("File format not recognised");
    }
    String str1 = paramDataInputStream.readUTF();
    if (!"TZDB".equals(str1)) {
      throw new StreamCorruptedException("File format not recognised");
    }
    int i = paramDataInputStream.readShort();
    for (int j = 0; j < i; j++) {
      versionId = paramDataInputStream.readUTF();
    }
    j = paramDataInputStream.readShort();
    String[] arrayOfString = new String[j];
    for (int k = 0; k < j; k++) {
      arrayOfString[k] = paramDataInputStream.readUTF();
    }
    k = paramDataInputStream.readShort();
    ruleArray = new byte[k][];
    for (int m = 0; m < k; m++)
    {
      byte[] arrayOfByte = new byte[paramDataInputStream.readShort()];
      paramDataInputStream.readFully(arrayOfByte);
      ruleArray[m] = arrayOfByte;
    }
    int n;
    for (m = 0; m < i; m++)
    {
      j = paramDataInputStream.readShort();
      regions = new String[j];
      indices = new int[j];
      for (n = 0; n < j; n++)
      {
        regions[n] = arrayOfString[paramDataInputStream.readShort()];
        indices[n] = paramDataInputStream.readShort();
      }
    }
    zones.remove("ROC");
    for (m = 0; m < i; m++)
    {
      n = paramDataInputStream.readShort();
      aliases.clear();
      for (int i1 = 0; i1 < n; i1++)
      {
        String str2 = arrayOfString[paramDataInputStream.readShort()];
        String str3 = arrayOfString[paramDataInputStream.readShort()];
        aliases.put(str2, str3);
      }
    }
    addOldMapping();
  }
  
  public static ZoneInfo getZoneInfo(DataInput paramDataInput, String paramString)
    throws Exception
  {
    int i = paramDataInput.readByte();
    int j = paramDataInput.readInt();
    long[] arrayOfLong1 = new long[j];
    for (int k = 0; k < j; k++) {
      arrayOfLong1[k] = readEpochSec(paramDataInput);
    }
    int[] arrayOfInt1 = new int[j + 1];
    for (int m = 0; m < arrayOfInt1.length; m++) {
      arrayOfInt1[m] = readOffset(paramDataInput);
    }
    m = paramDataInput.readInt();
    long[] arrayOfLong2 = new long[m];
    for (int n = 0; n < m; n++) {
      arrayOfLong2[n] = readEpochSec(paramDataInput);
    }
    int[] arrayOfInt2 = new int[m + 1];
    for (int i1 = 0; i1 < arrayOfInt2.length; i1++) {
      arrayOfInt2[i1] = readOffset(paramDataInput);
    }
    i1 = paramDataInput.readByte();
    ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = new ZoneOffsetTransitionRule[i1];
    for (int i2 = 0; i2 < i1; i2++) {
      arrayOfZoneOffsetTransitionRule[i2] = new ZoneOffsetTransitionRule(paramDataInput);
    }
    return getZoneInfo(paramString, arrayOfLong1, arrayOfInt1, arrayOfLong2, arrayOfInt2, arrayOfZoneOffsetTransitionRule);
  }
  
  public static int readOffset(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readByte();
    return i == 127 ? paramDataInput.readInt() : i * 900;
  }
  
  static long readEpochSec(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readByte() & 0xFF;
    if (i == 255) {
      return paramDataInput.readLong();
    }
    int j = paramDataInput.readByte() & 0xFF;
    int k = paramDataInput.readByte() & 0xFF;
    long l = (i << 16) + (j << 8) + k;
    return l * 900L - 4575744000L;
  }
  
  private static ZoneInfo getZoneInfo(String paramString, long[] paramArrayOfLong1, int[] paramArrayOfInt1, long[] paramArrayOfLong2, int[] paramArrayOfInt2, ZoneOffsetTransitionRule[] paramArrayOfZoneOffsetTransitionRule)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int[] arrayOfInt1 = null;
    boolean bool = false;
    if (paramArrayOfLong1.length > 0)
    {
      i = paramArrayOfInt1[(paramArrayOfInt1.length - 1)] * 1000;
      bool = paramArrayOfLong1[(paramArrayOfLong1.length - 1)] > CURRT;
    }
    else
    {
      i = paramArrayOfInt1[0] * 1000;
    }
    long[] arrayOfLong = null;
    int[] arrayOfInt2 = null;
    int m = 0;
    int n = 0;
    if (paramArrayOfLong2.length != 0)
    {
      arrayOfLong = new long['ú'];
      arrayOfInt2 = new int[100];
      int i1 = getYear(paramArrayOfLong2[(paramArrayOfLong2.length - 1)], paramArrayOfInt2[(paramArrayOfLong2.length - 1)]);
      int i2 = 0;
      int i3 = 1;
      while ((i2 < paramArrayOfLong2.length) && (paramArrayOfLong2[i2] < -2208988800L)) {
        i2++;
      }
      if (i2 < paramArrayOfLong2.length)
      {
        if (i2 < paramArrayOfLong2.length)
        {
          arrayOfInt2[0] = (paramArrayOfInt1[(paramArrayOfInt1.length - 1)] * 1000);
          m = 1;
        }
        m = addTrans(arrayOfLong, n++, arrayOfInt2, m, -2208988800L, paramArrayOfInt2[i2], getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, -2208988800L));
      }
      long l1;
      while (i2 < paramArrayOfLong2.length)
      {
        l1 = paramArrayOfLong2[i2];
        if (l1 > 2145916799L)
        {
          i1 = 2037;
          break;
        }
        while (i3 < paramArrayOfLong1.length)
        {
          long l4 = paramArrayOfLong1[i3];
          if (l4 >= -2208988800L)
          {
            if (l4 > l1) {
              break;
            }
            if (l4 < l1)
            {
              if (m + 2 >= arrayOfInt2.length) {
                arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100);
              }
              if (n + 1 >= arrayOfLong.length) {
                arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100);
              }
              m = addTrans(arrayOfLong, n++, arrayOfInt2, m, l4, paramArrayOfInt2[i2], paramArrayOfInt1[(i3 + 1)]);
            }
          }
          i3++;
        }
        if (m + 2 >= arrayOfInt2.length) {
          arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100);
        }
        if (n + 1 >= arrayOfLong.length) {
          arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100);
        }
        m = addTrans(arrayOfLong, n++, arrayOfInt2, m, l1, paramArrayOfInt2[(i2 + 1)], getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l1));
        i2++;
      }
      int i6;
      while (i3 < paramArrayOfLong1.length)
      {
        l1 = paramArrayOfLong1[i3];
        if (l1 >= -2208988800L)
        {
          i6 = paramArrayOfInt2[i2];
          int i8 = indexOf(arrayOfInt2, 0, m, i6);
          if (i8 == m) {
            m++;
          }
          arrayOfLong[(n++)] = (l1 * 1000L << 12 | i8 & 0xF);
        }
        i3++;
      }
      int i7;
      int i9;
      long l3;
      int i10;
      if (paramArrayOfZoneOffsetTransitionRule.length > 1)
      {
        while (i1++ < 2037) {
          for (ZoneOffsetTransitionRule localZoneOffsetTransitionRule : paramArrayOfZoneOffsetTransitionRule)
          {
            long l5 = localZoneOffsetTransitionRule.getTransitionEpochSecond(i1);
            if (m + 2 >= arrayOfInt2.length) {
              arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100);
            }
            if (n + 1 >= arrayOfLong.length) {
              arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100);
            }
            m = addTrans(arrayOfLong, n++, arrayOfInt2, m, l5, offsetAfter, standardOffset);
          }
        }
        ??? = paramArrayOfZoneOffsetTransitionRule[(paramArrayOfZoneOffsetTransitionRule.length - 2)];
        Object localObject2 = paramArrayOfZoneOffsetTransitionRule[(paramArrayOfZoneOffsetTransitionRule.length - 1)];
        arrayOfInt1 = new int[10];
        if ((offsetAfter - offsetBefore < 0) && (offsetAfter - offsetBefore > 0))
        {
          Object localObject3 = ???;
          ??? = localObject2;
          localObject2 = localObject3;
        }
        arrayOfInt1[0] = (month - 1);
        i7 = dom;
        i9 = dow;
        if (i9 == -1)
        {
          arrayOfInt1[1] = i7;
          arrayOfInt1[2] = 0;
        }
        else if ((i7 < 0) || (i7 >= 24))
        {
          arrayOfInt1[1] = -1;
          arrayOfInt1[2] = toCalendarDOW[i9];
        }
        else
        {
          arrayOfInt1[1] = i7;
          arrayOfInt1[2] = (-toCalendarDOW[i9]);
        }
        arrayOfInt1[3] = (secondOfDay * 1000);
        arrayOfInt1[4] = toSTZTime[timeDefinition];
        arrayOfInt1[5] = (month - 1);
        i7 = dom;
        i9 = dow;
        if (i9 == -1)
        {
          arrayOfInt1[6] = i7;
          arrayOfInt1[7] = 0;
        }
        else if ((i7 < 0) || (i7 >= 24))
        {
          arrayOfInt1[6] = -1;
          arrayOfInt1[7] = toCalendarDOW[i9];
        }
        else
        {
          arrayOfInt1[6] = i7;
          arrayOfInt1[7] = (-toCalendarDOW[i9]);
        }
        arrayOfInt1[8] = (secondOfDay * 1000);
        arrayOfInt1[9] = toSTZTime[timeDefinition];
        j = (offsetAfter - offsetBefore) * 1000;
        if ((arrayOfInt1[2] == 6) && (arrayOfInt1[3] == 0) && ((paramString.equals("Asia/Amman")) || (paramString.equals("Asia/Gaza")) || (paramString.equals("Asia/Hebron"))))
        {
          arrayOfInt1[2] = 5;
          arrayOfInt1[3] = 86400000;
        }
        if ((arrayOfInt1[2] == 7) && (arrayOfInt1[3] == 0) && ((paramString.equals("Asia/Amman")) || (paramString.equals("Asia/Gaza")) || (paramString.equals("Asia/Hebron"))))
        {
          arrayOfInt1[2] = 6;
          arrayOfInt1[3] = 86400000;
        }
        if ((arrayOfInt1[7] == 6) && (arrayOfInt1[8] == 0) && (paramString.equals("Africa/Cairo")))
        {
          arrayOfInt1[7] = 5;
          arrayOfInt1[8] = 86400000;
        }
      }
      else if (n > 0)
      {
        if (i1 < 2037)
        {
          long l2 = 2114380800L - i / 1000;
          i7 = indexOf(arrayOfInt2, 0, m, i / 1000);
          if (i7 == m) {
            m++;
          }
          arrayOfLong[(n++)] = (l2 * 1000L << 12 | i7 & 0xF);
        }
        else if (paramArrayOfLong2.length > 2)
        {
          int i4 = paramArrayOfLong2.length;
          l3 = paramArrayOfLong2[(i4 - 2)];
          i9 = paramArrayOfInt2[(i4 - 2 + 1)];
          i10 = getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l3);
          long l6 = paramArrayOfLong2[(i4 - 1)];
          int i13 = paramArrayOfInt2[(i4 - 1 + 1)];
          int i14 = getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l6);
          if ((i9 > i10) && (i13 == i14))
          {
            i4 = paramArrayOfLong2.length - 2;
            ZoneOffset localZoneOffset1 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[i4]);
            ZoneOffset localZoneOffset2 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[(i4 + 1)]);
            LocalDateTime localLocalDateTime1 = LocalDateTime.ofEpochSecond(paramArrayOfLong2[i4], 0, localZoneOffset1);
            LocalDateTime localLocalDateTime2;
            if (localZoneOffset2.getTotalSeconds() > localZoneOffset1.getTotalSeconds()) {
              localLocalDateTime2 = localLocalDateTime1;
            } else {
              localLocalDateTime2 = localLocalDateTime1.plusSeconds(paramArrayOfInt2[(i4 + 1)] - paramArrayOfInt2[i4]);
            }
            i4 = paramArrayOfLong2.length - 1;
            localZoneOffset1 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[i4]);
            localZoneOffset2 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[(i4 + 1)]);
            localLocalDateTime1 = LocalDateTime.ofEpochSecond(paramArrayOfLong2[i4], 0, localZoneOffset1);
            LocalDateTime localLocalDateTime3;
            if (localZoneOffset2.getTotalSeconds() > localZoneOffset1.getTotalSeconds()) {
              localLocalDateTime3 = localLocalDateTime1.plusSeconds(paramArrayOfInt2[(i4 + 1)] - paramArrayOfInt2[i4]);
            } else {
              localLocalDateTime3 = localLocalDateTime1;
            }
            arrayOfInt1 = new int[10];
            arrayOfInt1[0] = (localLocalDateTime2.getMonthValue() - 1);
            arrayOfInt1[1] = localLocalDateTime2.getDayOfMonth();
            arrayOfInt1[2] = 0;
            arrayOfInt1[3] = (localLocalDateTime2.toLocalTime().toSecondOfDay() * 1000);
            arrayOfInt1[4] = 0;
            arrayOfInt1[5] = (localLocalDateTime3.getMonthValue() - 1);
            arrayOfInt1[6] = localLocalDateTime3.getDayOfMonth();
            arrayOfInt1[7] = 0;
            arrayOfInt1[8] = (localLocalDateTime3.toLocalTime().toSecondOfDay() * 1000);
            arrayOfInt1[9] = 0;
            j = (i9 - i10) * 1000;
          }
        }
      }
      if ((arrayOfLong != null) && (arrayOfLong.length != n)) {
        if (n == 0) {
          arrayOfLong = null;
        } else {
          arrayOfLong = Arrays.copyOf(arrayOfLong, n);
        }
      }
      if ((arrayOfInt2 != null) && (arrayOfInt2.length != m)) {
        if (m == 0) {
          arrayOfInt2 = null;
        } else {
          arrayOfInt2 = Arrays.copyOf(arrayOfInt2, m);
        }
      }
      if (arrayOfLong != null)
      {
        Checksum localChecksum = new Checksum(null);
        for (i2 = 0; i2 < arrayOfLong.length; i2++)
        {
          l3 = arrayOfLong[i2];
          i9 = (int)(l3 >>> 4 & 0xF);
          i10 = i9 == 0 ? 0 : arrayOfInt2[i9];
          int i11 = (int)(l3 & 0xF);
          int i12 = arrayOfInt2[i11];
          long l7 = l3 >> 12;
          localChecksum.update(l7 + i11);
          localChecksum.update(i11);
          localChecksum.update(i9 == 0 ? -1 : i9);
        }
        k = (int)localChecksum.getValue();
      }
    }
    return new ZoneInfo(paramString, i, j, k, arrayOfLong, arrayOfInt2, arrayOfInt1, bool);
  }
  
  private static int getStandardOffset(long[] paramArrayOfLong, int[] paramArrayOfInt, long paramLong)
  {
    for (int i = 0; (i < paramArrayOfLong.length) && (paramLong >= paramArrayOfLong[i]); i++) {}
    return paramArrayOfInt[i];
  }
  
  private static int getYear(long paramLong, int paramInt)
  {
    long l1 = paramLong + paramInt;
    long l2 = Math.floorDiv(l1, 86400L);
    long l3 = l2 + 719528L;
    l3 -= 60L;
    long l4 = 0L;
    if (l3 < 0L)
    {
      l5 = (l3 + 1L) / 146097L - 1L;
      l4 = l5 * 400L;
      l3 += -l5 * 146097L;
    }
    long l5 = (400L * l3 + 591L) / 146097L;
    long l6 = l3 - (365L * l5 + l5 / 4L - l5 / 100L + l5 / 400L);
    if (l6 < 0L)
    {
      l5 -= 1L;
      l6 = l3 - (365L * l5 + l5 / 4L - l5 / 100L + l5 / 400L);
    }
    l5 += l4;
    int i = (int)l6;
    int j = (i * 5 + 2) / 153;
    int k = (j + 2) % 12 + 1;
    int m = i - (j * 306 + 5) / 10 + 1;
    l5 += j / 10;
    return (int)l5;
  }
  
  private static int indexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt3 *= 1000;
    while (paramInt1 < paramInt2)
    {
      if (paramArrayOfInt[paramInt1] == paramInt3) {
        return paramInt1;
      }
      paramInt1++;
    }
    paramArrayOfInt[paramInt1] = paramInt3;
    return paramInt1;
  }
  
  private static int addTrans(long[] paramArrayOfLong, int paramInt1, int[] paramArrayOfInt, int paramInt2, long paramLong, int paramInt3, int paramInt4)
  {
    int i = indexOf(paramArrayOfInt, 0, paramInt2, paramInt3);
    if (i == paramInt2) {
      paramInt2++;
    }
    int j = 0;
    if (paramInt3 != paramInt4)
    {
      j = indexOf(paramArrayOfInt, 1, paramInt2, paramInt3 - paramInt4);
      if (j == paramInt2) {
        paramInt2++;
      }
    }
    paramArrayOfLong[paramInt1] = (paramLong * 1000L << 12 | j << 4 & 0xF0 | i & 0xF);
    return paramInt2;
  }
  
  static
  {
    String str = ((String)AccessController.doPrivileged(new GetPropertyAction("sun.timezone.ids.oldmapping", "false"))).toLowerCase(Locale.ROOT);
    USE_OLDMAPPING = (str.equals("yes")) || (str.equals("true"));
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          String str = System.getProperty("java.home") + File.separator + "lib";
          DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(str, "tzdb.dat"))));
          Object localObject1 = null;
          try
          {
            ZoneInfoFile.load(localDataInputStream);
          }
          catch (Throwable localThrowable2)
          {
            localObject1 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localDataInputStream != null) {
              if (localObject1 != null) {
                try
                {
                  localDataInputStream.close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject1).addSuppressed(localThrowable3);
                }
              } else {
                localDataInputStream.close();
              }
            }
          }
        }
        catch (Exception localException)
        {
          throw new Error(localException);
        }
        return null;
      }
    });
  }
  
  private static class Checksum
    extends CRC32
  {
    private Checksum() {}
    
    public void update(int paramInt)
    {
      byte[] arrayOfByte = new byte[4];
      arrayOfByte[0] = ((byte)(paramInt >>> 24));
      arrayOfByte[1] = ((byte)(paramInt >>> 16));
      arrayOfByte[2] = ((byte)(paramInt >>> 8));
      arrayOfByte[3] = ((byte)paramInt);
      update(arrayOfByte);
    }
    
    void update(long paramLong)
    {
      byte[] arrayOfByte = new byte[8];
      arrayOfByte[0] = ((byte)(int)(paramLong >>> 56));
      arrayOfByte[1] = ((byte)(int)(paramLong >>> 48));
      arrayOfByte[2] = ((byte)(int)(paramLong >>> 40));
      arrayOfByte[3] = ((byte)(int)(paramLong >>> 32));
      arrayOfByte[4] = ((byte)(int)(paramLong >>> 24));
      arrayOfByte[5] = ((byte)(int)(paramLong >>> 16));
      arrayOfByte[6] = ((byte)(int)(paramLong >>> 8));
      arrayOfByte[7] = ((byte)(int)paramLong);
      update(arrayOfByte);
    }
  }
  
  private static class ZoneOffsetTransitionRule
  {
    private final int month;
    private final byte dom;
    private final int dow;
    private final int secondOfDay;
    private final boolean timeEndOfDay;
    private final int timeDefinition;
    private final int standardOffset;
    private final int offsetBefore;
    private final int offsetAfter;
    
    ZoneOffsetTransitionRule(DataInput paramDataInput)
      throws IOException
    {
      int i = paramDataInput.readInt();
      int j = (i & 0x380000) >>> 19;
      int k = (i & 0x7C000) >>> 14;
      int m = (i & 0xFF0) >>> 4;
      int n = (i & 0xC) >>> 2;
      int i1 = i & 0x3;
      month = (i >>> 28);
      dom = ((byte)(((i & 0xFC00000) >>> 22) - 32));
      dow = (j == 0 ? -1 : j);
      secondOfDay = (k == 31 ? paramDataInput.readInt() : k * 3600);
      timeEndOfDay = (k == 24);
      timeDefinition = ((i & 0x3000) >>> 12);
      standardOffset = (m == 255 ? paramDataInput.readInt() : (m - 128) * 900);
      offsetBefore = (n == 3 ? paramDataInput.readInt() : standardOffset + n * 1800);
      offsetAfter = (i1 == 3 ? paramDataInput.readInt() : standardOffset + i1 * 1800);
    }
    
    long getTransitionEpochSecond(int paramInt)
    {
      long l = 0L;
      if (dom < 0)
      {
        l = toEpochDay(paramInt, month, lengthOfMonth(paramInt, month) + 1 + dom);
        if (dow != -1) {
          l = previousOrSame(l, dow);
        }
      }
      else
      {
        l = toEpochDay(paramInt, month, dom);
        if (dow != -1) {
          l = nextOrSame(l, dow);
        }
      }
      if (timeEndOfDay) {
        l += 1L;
      }
      int i = 0;
      switch (timeDefinition)
      {
      case 0: 
        i = 0;
        break;
      case 1: 
        i = -offsetBefore;
        break;
      case 2: 
        i = -standardOffset;
      }
      return l * 86400L + secondOfDay + i;
    }
    
    static final boolean isLeapYear(int paramInt)
    {
      return ((paramInt & 0x3) == 0) && ((paramInt % 100 != 0) || (paramInt % 400 == 0));
    }
    
    static final int lengthOfMonth(int paramInt1, int paramInt2)
    {
      switch (paramInt2)
      {
      case 2: 
        return isLeapYear(paramInt1) ? 29 : 28;
      case 4: 
      case 6: 
      case 9: 
      case 11: 
        return 30;
      }
      return 31;
    }
    
    static final long toEpochDay(int paramInt1, int paramInt2, int paramInt3)
    {
      long l1 = paramInt1;
      long l2 = paramInt2;
      long l3 = 0L;
      l3 += 365L * l1;
      if (l1 >= 0L) {
        l3 += (l1 + 3L) / 4L - (l1 + 99L) / 100L + (l1 + 399L) / 400L;
      } else {
        l3 -= l1 / -4L - l1 / -100L + l1 / -400L;
      }
      l3 += (367L * l2 - 362L) / 12L;
      l3 += paramInt3 - 1;
      if (l2 > 2L)
      {
        l3 -= 1L;
        if (!isLeapYear(paramInt1)) {
          l3 -= 1L;
        }
      }
      return l3 - 719528L;
    }
    
    static final long previousOrSame(long paramLong, int paramInt)
    {
      return adjust(paramLong, paramInt, 1);
    }
    
    static final long nextOrSame(long paramLong, int paramInt)
    {
      return adjust(paramLong, paramInt, 0);
    }
    
    static final long adjust(long paramLong, int paramInt1, int paramInt2)
    {
      int i = (int)Math.floorMod(paramLong + 3L, 7L) + 1;
      if ((paramInt2 < 2) && (i == paramInt1)) {
        return paramLong;
      }
      if ((paramInt2 & 0x1) == 0)
      {
        j = i - paramInt1;
        return paramLong + (j >= 0 ? 7 - j : -j);
      }
      int j = paramInt1 - i;
      return paramLong - (j >= 0 ? 7 - j : -j);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\calendar\ZoneInfoFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
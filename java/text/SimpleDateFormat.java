package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.ZoneInfoFile;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public class SimpleDateFormat
  extends DateFormat
{
  static final long serialVersionUID = 4774881970558875024L;
  static final int currentSerialVersion = 1;
  private int serialVersionOnStream = 1;
  private String pattern;
  private transient NumberFormat originalNumberFormat;
  private transient String originalNumberPattern;
  private transient char minusSign = '-';
  private transient boolean hasFollowingMinusSign = false;
  private transient boolean forceStandaloneForm = false;
  private transient char[] compiledPattern;
  private static final int TAG_QUOTE_ASCII_CHAR = 100;
  private static final int TAG_QUOTE_CHARS = 101;
  private transient char zeroDigit;
  private DateFormatSymbols formatData;
  private Date defaultCenturyStart;
  private transient int defaultCenturyStartYear;
  private static final int MILLIS_PER_MINUTE = 60000;
  private static final String GMT = "GMT";
  private static final ConcurrentMap<Locale, NumberFormat> cachedNumberFormatData = new ConcurrentHashMap(3);
  private Locale locale;
  transient boolean useDateFormatSymbols;
  private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = { 0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15, 15, 17, 1000, 15, 2 };
  private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 17, 1, 9, 17, 2 };
  private static final DateFormat.Field[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID = { DateFormat.Field.ERA, DateFormat.Field.YEAR, DateFormat.Field.MONTH, DateFormat.Field.DAY_OF_MONTH, DateFormat.Field.HOUR_OF_DAY1, DateFormat.Field.HOUR_OF_DAY0, DateFormat.Field.MINUTE, DateFormat.Field.SECOND, DateFormat.Field.MILLISECOND, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.DAY_OF_YEAR, DateFormat.Field.DAY_OF_WEEK_IN_MONTH, DateFormat.Field.WEEK_OF_YEAR, DateFormat.Field.WEEK_OF_MONTH, DateFormat.Field.AM_PM, DateFormat.Field.HOUR1, DateFormat.Field.HOUR0, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.TIME_ZONE, DateFormat.Field.MONTH };
  private static final int[] REST_OF_STYLES = { 32769, 2, 32770 };
  
  public SimpleDateFormat()
  {
    this("", Locale.getDefault(Locale.Category.FORMAT));
    applyPatternImpl(LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(locale).getDateTimePattern(3, 3, calendar));
  }
  
  public SimpleDateFormat(String paramString)
  {
    this(paramString, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public SimpleDateFormat(String paramString, Locale paramLocale)
  {
    if ((paramString == null) || (paramLocale == null)) {
      throw new NullPointerException();
    }
    initializeCalendar(paramLocale);
    pattern = paramString;
    formatData = DateFormatSymbols.getInstanceRef(paramLocale);
    locale = paramLocale;
    initialize(paramLocale);
  }
  
  public SimpleDateFormat(String paramString, DateFormatSymbols paramDateFormatSymbols)
  {
    if ((paramString == null) || (paramDateFormatSymbols == null)) {
      throw new NullPointerException();
    }
    pattern = paramString;
    formatData = ((DateFormatSymbols)paramDateFormatSymbols.clone());
    locale = Locale.getDefault(Locale.Category.FORMAT);
    initializeCalendar(locale);
    initialize(locale);
    useDateFormatSymbols = true;
  }
  
  private void initialize(Locale paramLocale)
  {
    compiledPattern = compile(pattern);
    numberFormat = ((NumberFormat)cachedNumberFormatData.get(paramLocale));
    if (numberFormat == null)
    {
      numberFormat = NumberFormat.getIntegerInstance(paramLocale);
      numberFormat.setGroupingUsed(false);
      cachedNumberFormatData.putIfAbsent(paramLocale, numberFormat);
    }
    numberFormat = ((NumberFormat)numberFormat.clone());
    initializeDefaultCentury();
  }
  
  private void initializeCalendar(Locale paramLocale)
  {
    if (calendar == null)
    {
      assert (paramLocale != null);
      calendar = Calendar.getInstance(TimeZone.getDefault(), paramLocale);
    }
  }
  
  private char[] compile(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    StringBuilder localStringBuilder1 = new StringBuilder(i * 2);
    StringBuilder localStringBuilder2 = null;
    int k = 0;
    int m = 0;
    int n = -1;
    int i1 = -1;
    for (int i2 = 0; i2 < i; i2++)
    {
      char c1 = paramString.charAt(i2);
      int i3;
      char c2;
      if (c1 == '\'')
      {
        if (i2 + 1 < i)
        {
          c1 = paramString.charAt(i2 + 1);
          if (c1 == '\'')
          {
            i2++;
            if (k != 0)
            {
              encode(n, k, localStringBuilder1);
              m++;
              i1 = n;
              n = -1;
              k = 0;
            }
            if (j != 0)
            {
              localStringBuilder2.append(c1);
              continue;
            }
            localStringBuilder1.append((char)(0x6400 | c1));
            continue;
          }
        }
        if (j == 0)
        {
          if (k != 0)
          {
            encode(n, k, localStringBuilder1);
            m++;
            i1 = n;
            n = -1;
            k = 0;
          }
          if (localStringBuilder2 == null) {
            localStringBuilder2 = new StringBuilder(i);
          } else {
            localStringBuilder2.setLength(0);
          }
          j = 1;
        }
        else
        {
          i3 = localStringBuilder2.length();
          if (i3 == 1)
          {
            c2 = localStringBuilder2.charAt(0);
            if (c2 < '')
            {
              localStringBuilder1.append((char)(0x6400 | c2));
            }
            else
            {
              localStringBuilder1.append('攁');
              localStringBuilder1.append(c2);
            }
          }
          else
          {
            encode(101, i3, localStringBuilder1);
            localStringBuilder1.append(localStringBuilder2);
          }
          j = 0;
        }
      }
      else if (j != 0)
      {
        localStringBuilder2.append(c1);
      }
      else if (((c1 < 'a') || (c1 > 'z')) && ((c1 < 'A') || (c1 > 'Z')))
      {
        if (k != 0)
        {
          encode(n, k, localStringBuilder1);
          m++;
          i1 = n;
          n = -1;
          k = 0;
        }
        if (c1 < '')
        {
          localStringBuilder1.append((char)(0x6400 | c1));
        }
        else
        {
          for (i3 = i2 + 1; i3 < i; i3++)
          {
            c2 = paramString.charAt(i3);
            if ((c2 == '\'') || ((c2 >= 'a') && (c2 <= 'z')) || ((c2 >= 'A') && (c2 <= 'Z'))) {
              break;
            }
          }
          localStringBuilder1.append((char)(0x6500 | i3 - i2));
          while (i2 < i3)
          {
            localStringBuilder1.append(paramString.charAt(i2));
            i2++;
          }
          i2--;
        }
      }
      else
      {
        if ((i3 = "GyMdkHmsSEDFwWahKzZYuXL".indexOf(c1)) == -1) {
          throw new IllegalArgumentException("Illegal pattern character '" + c1 + "'");
        }
        if ((n == -1) || (n == i3))
        {
          n = i3;
          k++;
        }
        else
        {
          encode(n, k, localStringBuilder1);
          m++;
          i1 = n;
          n = i3;
          k = 1;
        }
      }
    }
    if (j != 0) {
      throw new IllegalArgumentException("Unterminated quote");
    }
    if (k != 0)
    {
      encode(n, k, localStringBuilder1);
      m++;
      i1 = n;
    }
    forceStandaloneForm = ((m == 1) && (i1 == 2));
    i2 = localStringBuilder1.length();
    char[] arrayOfChar = new char[i2];
    localStringBuilder1.getChars(0, i2, arrayOfChar, 0);
    return arrayOfChar;
  }
  
  private static void encode(int paramInt1, int paramInt2, StringBuilder paramStringBuilder)
  {
    if ((paramInt1 == 21) && (paramInt2 >= 4)) {
      throw new IllegalArgumentException("invalid ISO 8601 format: length=" + paramInt2);
    }
    if (paramInt2 < 255)
    {
      paramStringBuilder.append((char)(paramInt1 << 8 | paramInt2));
    }
    else
    {
      paramStringBuilder.append((char)(paramInt1 << 8 | 0xFF));
      paramStringBuilder.append((char)(paramInt2 >>> 16));
      paramStringBuilder.append((char)(paramInt2 & 0xFFFF));
    }
  }
  
  private void initializeDefaultCentury()
  {
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.add(1, -80);
    parseAmbiguousDatesAsAfter(calendar.getTime());
  }
  
  private void parseAmbiguousDatesAsAfter(Date paramDate)
  {
    defaultCenturyStart = paramDate;
    calendar.setTime(paramDate);
    defaultCenturyStartYear = calendar.get(1);
  }
  
  public void set2DigitYearStart(Date paramDate)
  {
    parseAmbiguousDatesAsAfter(new Date(paramDate.getTime()));
  }
  
  public Date get2DigitYearStart()
  {
    return (Date)defaultCenturyStart.clone();
  }
  
  public StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    beginIndex = (endIndex = 0);
    return format(paramDate, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate)
  {
    calendar.setTime(paramDate);
    boolean bool = useDateFormatSymbols();
    int i = 0;
    while (i < compiledPattern.length)
    {
      int j = compiledPattern[i] >>> '\b';
      int k = compiledPattern[(i++)] & 0xFF;
      if (k == 255)
      {
        k = compiledPattern[(i++)] << '\020';
        k |= compiledPattern[(i++)];
      }
      switch (j)
      {
      case 100: 
        paramStringBuffer.append((char)k);
        break;
      case 101: 
        paramStringBuffer.append(compiledPattern, i, k);
        i += k;
        break;
      default: 
        subFormat(j, k, paramFieldDelegate, paramStringBuffer, bool);
      }
    }
    return paramStringBuffer;
  }
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    CharacterIteratorFieldDelegate localCharacterIteratorFieldDelegate = new CharacterIteratorFieldDelegate();
    if ((paramObject instanceof Date))
    {
      format((Date)paramObject, localStringBuffer, localCharacterIteratorFieldDelegate);
    }
    else if ((paramObject instanceof Number))
    {
      format(new Date(((Number)paramObject).longValue()), localStringBuffer, localCharacterIteratorFieldDelegate);
    }
    else
    {
      if (paramObject == null) {
        throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
      }
      throw new IllegalArgumentException("Cannot format given Object as a Date");
    }
    return localCharacterIteratorFieldDelegate.getIterator(localStringBuffer.toString());
  }
  
  private void subFormat(int paramInt1, int paramInt2, Format.FieldDelegate paramFieldDelegate, StringBuffer paramStringBuffer, boolean paramBoolean)
  {
    int i = Integer.MAX_VALUE;
    String str = null;
    int j = paramStringBuffer.length();
    int k = PATTERN_INDEX_TO_CALENDAR_FIELD[paramInt1];
    int m;
    if (k == 17)
    {
      if (calendar.isWeekDateSupported())
      {
        m = calendar.getWeekYear();
      }
      else
      {
        paramInt1 = 1;
        k = PATTERN_INDEX_TO_CALENDAR_FIELD[paramInt1];
        m = calendar.get(k);
      }
    }
    else if (k == 1000) {
      m = CalendarBuilder.toISODayOfWeek(calendar.get(7));
    } else {
      m = calendar.get(k);
    }
    int n = paramInt2 >= 4 ? 2 : 1;
    if ((!paramBoolean) && (k < 15) && (paramInt1 != 22)) {
      str = calendar.getDisplayName(k, n, locale);
    }
    String[] arrayOfString;
    switch (paramInt1)
    {
    case 0: 
      if (paramBoolean)
      {
        arrayOfString = formatData.getEras();
        if (m < arrayOfString.length) {
          str = arrayOfString[m];
        }
      }
      if (str == null) {
        str = "";
      }
      break;
    case 1: 
    case 19: 
      if ((calendar instanceof GregorianCalendar))
      {
        if (paramInt2 != 2) {
          zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
        } else {
          zeroPaddingNumber(m, 2, 2, paramStringBuffer);
        }
      }
      else if (str == null) {
        zeroPaddingNumber(m, n == 2 ? 1 : paramInt2, i, paramStringBuffer);
      }
      break;
    case 2: 
      if (paramBoolean)
      {
        if (paramInt2 >= 4)
        {
          arrayOfString = formatData.getMonths();
          str = arrayOfString[m];
        }
        else if (paramInt2 == 3)
        {
          arrayOfString = formatData.getShortMonths();
          str = arrayOfString[m];
        }
      }
      else if (paramInt2 < 3)
      {
        str = null;
      }
      else if (forceStandaloneForm)
      {
        str = calendar.getDisplayName(k, n | 0x8000, locale);
        if (str == null) {
          str = calendar.getDisplayName(k, n, locale);
        }
      }
      if (str == null) {
        zeroPaddingNumber(m + 1, paramInt2, i, paramStringBuffer);
      }
      break;
    case 22: 
      assert (str == null);
      if (locale == null)
      {
        if (paramInt2 >= 4)
        {
          arrayOfString = formatData.getMonths();
          str = arrayOfString[m];
        }
        else if (paramInt2 == 3)
        {
          arrayOfString = formatData.getShortMonths();
          str = arrayOfString[m];
        }
      }
      else if (paramInt2 >= 3) {
        str = calendar.getDisplayName(k, n | 0x8000, locale);
      }
      if (str == null) {
        zeroPaddingNumber(m + 1, paramInt2, i, paramStringBuffer);
      }
      break;
    case 4: 
      if (str == null) {
        if (m == 0) {
          zeroPaddingNumber(calendar.getMaximum(11) + 1, paramInt2, i, paramStringBuffer);
        } else {
          zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
        }
      }
      break;
    case 9: 
      if (paramBoolean) {
        if (paramInt2 >= 4)
        {
          arrayOfString = formatData.getWeekdays();
          str = arrayOfString[m];
        }
        else
        {
          arrayOfString = formatData.getShortWeekdays();
          str = arrayOfString[m];
        }
      }
      break;
    case 14: 
      if (paramBoolean)
      {
        arrayOfString = formatData.getAmPmStrings();
        str = arrayOfString[m];
      }
      break;
    case 15: 
      if (str == null) {
        if (m == 0) {
          zeroPaddingNumber(calendar.getLeastMaximum(10) + 1, paramInt2, i, paramStringBuffer);
        } else {
          zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
        }
      }
      break;
    case 17: 
      if (str == null)
      {
        int i3;
        if ((formatData.locale == null) || (formatData.isZoneStringsSet))
        {
          int i1 = formatData.getZoneIndex(calendar.getTimeZone().getID());
          if (i1 == -1)
          {
            m = calendar.get(15) + calendar.get(16);
            paramStringBuffer.append(ZoneInfoFile.toCustomID(m));
          }
          else
          {
            i3 = calendar.get(16) == 0 ? 1 : 3;
            if (paramInt2 < 4) {
              i3++;
            }
            String[][] arrayOfString1 = formatData.getZoneStringsWrapper();
            paramStringBuffer.append(arrayOfString1[i1][i3]);
          }
        }
        else
        {
          TimeZone localTimeZone = calendar.getTimeZone();
          i3 = calendar.get(16) != 0 ? 1 : 0;
          int i5 = paramInt2 < 4 ? 0 : 1;
          paramStringBuffer.append(localTimeZone.getDisplayName(i3, i5, formatData.locale));
        }
      }
      break;
    case 18: 
      m = (calendar.get(15) + calendar.get(16)) / 60000;
      i2 = 4;
      if (m >= 0) {
        paramStringBuffer.append('+');
      } else {
        i2++;
      }
      int i4 = m / 60 * 100 + m % 60;
      CalendarUtils.sprintf0d(paramStringBuffer, i4, i2);
      break;
    case 21: 
      m = calendar.get(15) + calendar.get(16);
      if (m == 0)
      {
        paramStringBuffer.append('Z');
      }
      else
      {
        m /= 60000;
        if (m >= 0)
        {
          paramStringBuffer.append('+');
        }
        else
        {
          paramStringBuffer.append('-');
          m = -m;
        }
        CalendarUtils.sprintf0d(paramStringBuffer, m / 60, 2);
        if (paramInt2 != 1)
        {
          if (paramInt2 == 3) {
            paramStringBuffer.append(':');
          }
          CalendarUtils.sprintf0d(paramStringBuffer, m % 60, 2);
        }
      }
      break;
    case 3: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 16: 
    case 20: 
    default: 
      if (str == null) {
        zeroPaddingNumber(m, paramInt2, i, paramStringBuffer);
      }
      break;
    }
    if (str != null) {
      paramStringBuffer.append(str);
    }
    int i2 = PATTERN_INDEX_TO_DATE_FORMAT_FIELD[paramInt1];
    DateFormat.Field localField = PATTERN_INDEX_TO_DATE_FORMAT_FIELD_ID[paramInt1];
    paramFieldDelegate.formatted(i2, localField, localField, j, paramStringBuffer.length(), paramStringBuffer);
  }
  
  private void zeroPaddingNumber(int paramInt1, int paramInt2, int paramInt3, StringBuffer paramStringBuffer)
  {
    try
    {
      if (zeroDigit == 0) {
        zeroDigit = ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getZeroDigit();
      }
      if (paramInt1 >= 0)
      {
        if ((paramInt1 < 100) && (paramInt2 >= 1) && (paramInt2 <= 2))
        {
          if (paramInt1 < 10)
          {
            if (paramInt2 == 2) {
              paramStringBuffer.append(zeroDigit);
            }
            paramStringBuffer.append((char)(zeroDigit + paramInt1));
          }
          else
          {
            paramStringBuffer.append((char)(zeroDigit + paramInt1 / 10));
            paramStringBuffer.append((char)(zeroDigit + paramInt1 % 10));
          }
          return;
        }
        if ((paramInt1 >= 1000) && (paramInt1 < 10000))
        {
          if (paramInt2 == 4)
          {
            paramStringBuffer.append((char)(zeroDigit + paramInt1 / 1000));
            paramInt1 %= 1000;
            paramStringBuffer.append((char)(zeroDigit + paramInt1 / 100));
            paramInt1 %= 100;
            paramStringBuffer.append((char)(zeroDigit + paramInt1 / 10));
            paramStringBuffer.append((char)(zeroDigit + paramInt1 % 10));
            return;
          }
          if ((paramInt2 == 2) && (paramInt3 == 2))
          {
            zeroPaddingNumber(paramInt1 % 100, 2, 2, paramStringBuffer);
            return;
          }
        }
      }
    }
    catch (Exception localException) {}
    numberFormat.setMinimumIntegerDigits(paramInt2);
    numberFormat.setMaximumIntegerDigits(paramInt3);
    numberFormat.format(paramInt1, paramStringBuffer, DontCareFieldPosition.INSTANCE);
  }
  
  public Date parse(String paramString, ParsePosition paramParsePosition)
  {
    checkNegativeNumberExpression();
    int i = index;
    int j = i;
    int k = paramString.length();
    boolean[] arrayOfBoolean = { false };
    CalendarBuilder localCalendarBuilder = new CalendarBuilder();
    int m = 0;
    while (m < compiledPattern.length)
    {
      int n = compiledPattern[m] >>> '\b';
      int i1 = compiledPattern[(m++)] & 0xFF;
      if (i1 == 255)
      {
        i1 = compiledPattern[(m++)] << '\020';
        i1 |= compiledPattern[(m++)];
      }
      switch (n)
      {
      case 100: 
        if ((i >= k) || (paramString.charAt(i) != (char)i1))
        {
          index = j;
          errorIndex = i;
          return null;
        }
        i++;
        break;
      case 101: 
      default: 
        while (i1-- > 0)
        {
          if ((i >= k) || (paramString.charAt(i) != compiledPattern[(m++)]))
          {
            index = j;
            errorIndex = i;
            return null;
          }
          i++;
          continue;
          boolean bool1 = false;
          boolean bool2 = false;
          if (m < compiledPattern.length)
          {
            int i2 = compiledPattern[m] >>> '\b';
            if ((i2 != 100) && (i2 != 101)) {
              bool1 = true;
            }
            if ((hasFollowingMinusSign) && ((i2 == 100) || (i2 == 101)))
            {
              int i3;
              if (i2 == 100) {
                i3 = compiledPattern[m] & 0xFF;
              } else {
                i3 = compiledPattern[(m + 1)];
              }
              if (i3 == minusSign) {
                bool2 = true;
              }
            }
          }
          i = subParse(paramString, i, n, i1, bool1, arrayOfBoolean, paramParsePosition, bool2, localCalendarBuilder);
          if (i < 0)
          {
            index = j;
            return null;
          }
        }
      }
    }
    index = i;
    Date localDate;
    try
    {
      localDate = localCalendarBuilder.establish(calendar).getTime();
      if ((arrayOfBoolean[0] != 0) && (localDate.before(defaultCenturyStart))) {
        localDate = localCalendarBuilder.addYear(100).establish(calendar).getTime();
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      errorIndex = i;
      index = j;
      return null;
    }
    return localDate;
  }
  
  private int matchString(String paramString, int paramInt1, int paramInt2, String[] paramArrayOfString, CalendarBuilder paramCalendarBuilder)
  {
    int i = 0;
    int j = paramArrayOfString.length;
    if (paramInt2 == 7) {
      i = 1;
    }
    int k = 0;
    int m = -1;
    while (i < j)
    {
      int n = paramArrayOfString[i].length();
      if ((n > k) && (paramString.regionMatches(true, paramInt1, paramArrayOfString[i], 0, n)))
      {
        m = i;
        k = n;
      }
      i++;
    }
    if (m >= 0)
    {
      paramCalendarBuilder.set(paramInt2, m);
      return paramInt1 + k;
    }
    return -paramInt1;
  }
  
  private int matchString(String paramString, int paramInt1, int paramInt2, Map<String, Integer> paramMap, CalendarBuilder paramCalendarBuilder)
  {
    if (paramMap != null)
    {
      if ((paramMap instanceof SortedMap))
      {
        localObject1 = paramMap.keySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (String)((Iterator)localObject1).next();
          if (paramString.regionMatches(true, paramInt1, (String)localObject2, 0, ((String)localObject2).length()))
          {
            paramCalendarBuilder.set(paramInt2, ((Integer)paramMap.get(localObject2)).intValue());
            return paramInt1 + ((String)localObject2).length();
          }
        }
        return -paramInt1;
      }
      Object localObject1 = null;
      Object localObject2 = paramMap.keySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        String str = (String)((Iterator)localObject2).next();
        int i = str.length();
        if (((localObject1 == null) || (i > ((String)localObject1).length())) && (paramString.regionMatches(true, paramInt1, str, 0, i))) {
          localObject1 = str;
        }
      }
      if (localObject1 != null)
      {
        paramCalendarBuilder.set(paramInt2, ((Integer)paramMap.get(localObject1)).intValue());
        return paramInt1 + ((String)localObject1).length();
      }
    }
    return -paramInt1;
  }
  
  private int matchZoneString(String paramString, int paramInt, String[] paramArrayOfString)
  {
    for (int i = 1; i <= 4; i++)
    {
      String str = paramArrayOfString[i];
      if (paramString.regionMatches(true, paramInt, str, 0, str.length())) {
        return i;
      }
    }
    return -1;
  }
  
  private boolean matchDSTString(String paramString, int paramInt1, int paramInt2, int paramInt3, String[][] paramArrayOfString)
  {
    int i = paramInt3 + 2;
    String str = paramArrayOfString[paramInt2][i];
    return paramString.regionMatches(true, paramInt1, str, 0, str.length());
  }
  
  private int subParseZoneString(String paramString, int paramInt, CalendarBuilder paramCalendarBuilder)
  {
    boolean bool = false;
    TimeZone localTimeZone1 = getTimeZone();
    int i = formatData.getZoneIndex(localTimeZone1.getID());
    TimeZone localTimeZone2 = null;
    String[][] arrayOfString = formatData.getZoneStringsWrapper();
    String[] arrayOfString1 = null;
    int j = 0;
    if (i != -1)
    {
      arrayOfString1 = arrayOfString[i];
      if ((j = matchZoneString(paramString, paramInt, arrayOfString1)) > 0)
      {
        if (j <= 2) {
          bool = arrayOfString1[j].equalsIgnoreCase(arrayOfString1[(j + 2)]);
        }
        localTimeZone2 = TimeZone.getTimeZone(arrayOfString1[0]);
      }
    }
    if (localTimeZone2 == null)
    {
      i = formatData.getZoneIndex(TimeZone.getDefault().getID());
      if (i != -1)
      {
        arrayOfString1 = arrayOfString[i];
        if ((j = matchZoneString(paramString, paramInt, arrayOfString1)) > 0)
        {
          if (j <= 2) {
            bool = arrayOfString1[j].equalsIgnoreCase(arrayOfString1[(j + 2)]);
          }
          localTimeZone2 = TimeZone.getTimeZone(arrayOfString1[0]);
        }
      }
    }
    int k;
    if (localTimeZone2 == null)
    {
      k = arrayOfString.length;
      for (int m = 0; m < k; m++)
      {
        arrayOfString1 = arrayOfString[m];
        if ((j = matchZoneString(paramString, paramInt, arrayOfString1)) > 0)
        {
          if (j <= 2) {
            bool = arrayOfString1[j].equalsIgnoreCase(arrayOfString1[(j + 2)]);
          }
          localTimeZone2 = TimeZone.getTimeZone(arrayOfString1[0]);
          break;
        }
      }
    }
    if (localTimeZone2 != null)
    {
      if (!localTimeZone2.equals(localTimeZone1)) {
        setTimeZone(localTimeZone2);
      }
      k = j >= 3 ? localTimeZone2.getDSTSavings() : 0;
      if ((!bool) && ((j < 3) || (k != 0))) {
        paramCalendarBuilder.clear(15).set(16, k);
      }
      return paramInt + arrayOfString1[j].length();
    }
    return -paramInt;
  }
  
  private int subParseNumericZone(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, CalendarBuilder paramCalendarBuilder)
  {
    int i = paramInt1;
    try
    {
      char c = paramString.charAt(i++);
      if (isDigit(c))
      {
        int j = c - '0';
        c = paramString.charAt(i++);
        if (isDigit(c))
        {
          j = j * 10 + (c - '0');
        }
        else
        {
          if ((paramInt3 <= 0) && (!paramBoolean)) {
            break label242;
          }
          i--;
        }
        if (j <= 23)
        {
          int k = 0;
          if (paramInt3 != 1)
          {
            c = paramString.charAt(i++);
            if (paramBoolean)
            {
              if (c == ':') {
                c = paramString.charAt(i++);
              }
            }
            else if (isDigit(c))
            {
              k = c - '0';
              c = paramString.charAt(i++);
              if (isDigit(c))
              {
                k = k * 10 + (c - '0');
                if (k > 59) {}
              }
            }
          }
          else
          {
            k += j * 60;
            paramCalendarBuilder.set(15, k * 60000 * paramInt2).set(16, 0);
            return i;
          }
        }
      }
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
    label242:
    return 1 - i;
  }
  
  private boolean isDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  private int subParse(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean[] paramArrayOfBoolean, ParsePosition paramParsePosition, boolean paramBoolean2, CalendarBuilder paramCalendarBuilder)
  {
    int i = 0;
    ParsePosition localParsePosition = new ParsePosition(0);
    index = paramInt1;
    if ((paramInt2 == 19) && (!calendar.isWeekDateSupported())) {
      paramInt2 = 1;
    }
    int j = PATTERN_INDEX_TO_CALENDAR_FIELD[paramInt2];
    for (;;)
    {
      if (index >= paramString.length())
      {
        errorIndex = paramInt1;
        return -1;
      }
      k = paramString.charAt(index);
      if ((k != 32) && (k != 9)) {
        break;
      }
      index += 1;
    }
    int k = index;
    Number localNumber;
    if ((paramInt2 == 4) || (paramInt2 == 15) || ((paramInt2 == 2) && (paramInt3 <= 2)) || (paramInt2 == 1) || (paramInt2 == 19))
    {
      if (paramBoolean1)
      {
        if (paramInt1 + paramInt3 > paramString.length()) {
          break label1753;
        }
        localNumber = numberFormat.parse(paramString.substring(0, paramInt1 + paramInt3), localParsePosition);
      }
      else
      {
        localNumber = numberFormat.parse(paramString, localParsePosition);
      }
      if (localNumber == null)
      {
        if (paramInt2 != 1) {
          break label1753;
        }
        if ((calendar instanceof GregorianCalendar)) {
          break label1753;
        }
      }
      else
      {
        i = localNumber.intValue();
        if ((paramBoolean2) && (i < 0) && (((index < paramString.length()) && (paramString.charAt(index) != minusSign)) || ((index == paramString.length()) && (paramString.charAt(index - 1) == minusSign))))
        {
          i = -i;
          index -= 1;
        }
      }
    }
    boolean bool = useDateFormatSymbols();
    int m;
    int n;
    Object localObject2;
    Object localObject1;
    int i2;
    switch (paramInt2)
    {
    case 0: 
      if (bool)
      {
        if ((m = matchString(paramString, paramInt1, 0, formatData.getEras(), paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      else
      {
        Map localMap1 = getDisplayNamesMap(j, locale);
        if ((m = matchString(paramString, paramInt1, j, localMap1, paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      break;
    case 1: 
    case 19: 
      if (!(calendar instanceof GregorianCalendar))
      {
        n = paramInt3 >= 4 ? 2 : 1;
        localObject2 = calendar.getDisplayNames(j, n, locale);
        if ((localObject2 != null) && ((m = matchString(paramString, paramInt1, j, (Map)localObject2, paramCalendarBuilder)) > 0)) {
          return m;
        }
        paramCalendarBuilder.set(j, i);
        return index;
      }
      if ((paramInt3 <= 2) && (index - k == 2) && (Character.isDigit(paramString.charAt(k))) && (Character.isDigit(paramString.charAt(k + 1))))
      {
        n = defaultCenturyStartYear % 100;
        paramArrayOfBoolean[0] = (i == n ? 1 : false);
        i += defaultCenturyStartYear / 100 * 100 + (i < n ? 100 : 0);
      }
      paramCalendarBuilder.set(j, i);
      return index;
    case 2: 
      if (paramInt3 <= 2)
      {
        paramCalendarBuilder.set(2, i - 1);
        return index;
      }
      if (bool)
      {
        if ((n = matchString(paramString, paramInt1, 2, formatData.getMonths(), paramCalendarBuilder)) > 0) {
          return n;
        }
        if ((m = matchString(paramString, paramInt1, 2, formatData.getShortMonths(), paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      else
      {
        Map localMap2 = getDisplayNamesMap(j, locale);
        if ((m = matchString(paramString, paramInt1, j, localMap2, paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      break;
    case 4: 
      if ((isLenient()) || ((i >= 1) && (i <= 24)))
      {
        if (i == calendar.getMaximum(11) + 1) {
          i = 0;
        }
        paramCalendarBuilder.set(11, i);
        return index;
      }
      break;
    case 9: 
      if (bool)
      {
        int i1;
        if ((i1 = matchString(paramString, paramInt1, 7, formatData.getWeekdays(), paramCalendarBuilder)) > 0) {
          return i1;
        }
        if ((m = matchString(paramString, paramInt1, 7, formatData.getShortWeekdays(), paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      else
      {
        localObject1 = new int[] { 2, 1 };
        for (int i7 : localObject1)
        {
          Map localMap3 = calendar.getDisplayNames(j, i7, locale);
          if ((m = matchString(paramString, paramInt1, j, localMap3, paramCalendarBuilder)) > 0) {
            return m;
          }
        }
      }
      break;
    case 14: 
      if (bool)
      {
        if ((m = matchString(paramString, paramInt1, 9, formatData.getAmPmStrings(), paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      else
      {
        localObject1 = getDisplayNamesMap(j, locale);
        if ((m = matchString(paramString, paramInt1, j, (Map)localObject1, paramCalendarBuilder)) > 0) {
          return m;
        }
      }
      break;
    case 15: 
      if ((isLenient()) || ((i >= 1) && (i <= 12)))
      {
        if (i == calendar.getLeastMaximum(10) + 1) {
          i = 0;
        }
        paramCalendarBuilder.set(10, i);
        return index;
      }
      break;
    case 17: 
    case 18: 
      i2 = 0;
      try
      {
        int i3 = paramString.charAt(index);
        if (i3 == 43) {
          i2 = 1;
        } else if (i3 == 45) {
          i2 = -1;
        }
        if (i2 == 0)
        {
          if (((i3 == 71) || (i3 == 103)) && (paramString.length() - paramInt1 >= "GMT".length()) && (paramString.regionMatches(true, paramInt1, "GMT", 0, "GMT".length())))
          {
            index = (paramInt1 + "GMT".length());
            if (paramString.length() - index > 0)
            {
              i3 = paramString.charAt(index);
              if (i3 == 43) {
                i2 = 1;
              } else if (i3 == 45) {
                i2 = -1;
              }
            }
            if (i2 == 0)
            {
              paramCalendarBuilder.set(15, 0).set(16, 0);
              return index;
            }
            ??? = subParseNumericZone(paramString, ++index, i2, 0, true, paramCalendarBuilder);
            if (??? > 0) {
              return ???;
            }
            index = (-???);
          }
          else
          {
            ??? = subParseZoneString(paramString, index, paramCalendarBuilder);
            if (??? > 0) {
              return ???;
            }
            index = (-???);
          }
        }
        else
        {
          ??? = subParseNumericZone(paramString, ++index, i2, 0, false, paramCalendarBuilder);
          if (??? > 0) {
            return ???;
          }
          index = (-???);
        }
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {}
      break;
    case 21: 
      if (paramString.length() - index > 0)
      {
        int i4 = paramString.charAt(index);
        if (i4 == 90)
        {
          paramCalendarBuilder.set(15, 0).set(16, 0);
          return ++index;
        }
        if (i4 == 43)
        {
          i2 = 1;
        }
        else if (i4 == 45)
        {
          i2 = -1;
        }
        else
        {
          index += 1;
          break;
        }
        ??? = subParseNumericZone(paramString, ++index, i2, paramInt3, paramInt3 == 3, paramCalendarBuilder);
        if (??? > 0) {
          return ???;
        }
        index = (-???);
      }
      break;
    case 3: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 16: 
    case 20: 
    default: 
      if (paramBoolean1)
      {
        if (paramInt1 + paramInt3 > paramString.length()) {
          break;
        }
        localNumber = numberFormat.parse(paramString.substring(0, paramInt1 + paramInt3), localParsePosition);
      }
      else
      {
        localNumber = numberFormat.parse(paramString, localParsePosition);
      }
      if (localNumber != null)
      {
        i = localNumber.intValue();
        if ((paramBoolean2) && (i < 0) && (((index < paramString.length()) && (paramString.charAt(index) != minusSign)) || ((index == paramString.length()) && (paramString.charAt(index - 1) == minusSign))))
        {
          i = -i;
          index -= 1;
        }
        paramCalendarBuilder.set(j, i);
        return index;
      }
      break;
    }
    label1753:
    errorIndex = index;
    return -1;
  }
  
  private boolean useDateFormatSymbols()
  {
    return (useDateFormatSymbols) || (locale == null);
  }
  
  private String translatePattern(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    for (int j = 0; j < paramString1.length(); j++)
    {
      char c = paramString1.charAt(j);
      if (i != 0)
      {
        if (c == '\'') {
          i = 0;
        }
      }
      else if (c == '\'')
      {
        i = 1;
      }
      else if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')))
      {
        int k = paramString2.indexOf(c);
        if (k >= 0)
        {
          if (k < paramString3.length()) {
            c = paramString3.charAt(k);
          }
        }
        else {
          throw new IllegalArgumentException("Illegal pattern  character '" + c + "'");
        }
      }
      localStringBuilder.append(c);
    }
    if (i != 0) {
      throw new IllegalArgumentException("Unfinished quote in pattern");
    }
    return localStringBuilder.toString();
  }
  
  public String toPattern()
  {
    return pattern;
  }
  
  public String toLocalizedPattern()
  {
    return translatePattern(pattern, "GyMdkHmsSEDFwWahKzZYuXL", formatData.getLocalPatternChars());
  }
  
  public void applyPattern(String paramString)
  {
    applyPatternImpl(paramString);
  }
  
  private void applyPatternImpl(String paramString)
  {
    compiledPattern = compile(paramString);
    pattern = paramString;
  }
  
  public void applyLocalizedPattern(String paramString)
  {
    String str = translatePattern(paramString, formatData.getLocalPatternChars(), "GyMdkHmsSEDFwWahKzZYuXL");
    compiledPattern = compile(str);
    pattern = str;
  }
  
  public DateFormatSymbols getDateFormatSymbols()
  {
    return (DateFormatSymbols)formatData.clone();
  }
  
  public void setDateFormatSymbols(DateFormatSymbols paramDateFormatSymbols)
  {
    formatData = ((DateFormatSymbols)paramDateFormatSymbols.clone());
    useDateFormatSymbols = true;
  }
  
  public Object clone()
  {
    SimpleDateFormat localSimpleDateFormat = (SimpleDateFormat)super.clone();
    formatData = ((DateFormatSymbols)formatData.clone());
    return localSimpleDateFormat;
  }
  
  public int hashCode()
  {
    return pattern.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (!super.equals(paramObject)) {
      return false;
    }
    SimpleDateFormat localSimpleDateFormat = (SimpleDateFormat)paramObject;
    return (pattern.equals(pattern)) && (formatData.equals(formatData));
  }
  
  private Map<String, Integer> getDisplayNamesMap(int paramInt, Locale paramLocale)
  {
    Map localMap1 = calendar.getDisplayNames(paramInt, 1, paramLocale);
    for (int k : REST_OF_STYLES)
    {
      Map localMap2 = calendar.getDisplayNames(paramInt, k, paramLocale);
      if (localMap2 != null) {
        localMap1.putAll(localMap2);
      }
    }
    return localMap1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      compiledPattern = compile(pattern);
    }
    catch (Exception localException)
    {
      throw new InvalidObjectException("invalid pattern");
    }
    if (serialVersionOnStream < 1) {
      initializeDefaultCentury();
    } else {
      parseAmbiguousDatesAsAfter(defaultCenturyStart);
    }
    serialVersionOnStream = 1;
    TimeZone localTimeZone1 = getTimeZone();
    if ((localTimeZone1 instanceof SimpleTimeZone))
    {
      String str = localTimeZone1.getID();
      TimeZone localTimeZone2 = TimeZone.getTimeZone(str);
      if ((localTimeZone2 != null) && (localTimeZone2.hasSameRules(localTimeZone1)) && (localTimeZone2.getID().equals(str))) {
        setTimeZone(localTimeZone2);
      }
    }
  }
  
  private void checkNegativeNumberExpression()
  {
    if (((numberFormat instanceof DecimalFormat)) && (!numberFormat.equals(originalNumberFormat)))
    {
      String str = ((DecimalFormat)numberFormat).toPattern();
      if (!str.equals(originalNumberPattern))
      {
        hasFollowingMinusSign = false;
        int i = str.indexOf(';');
        if (i > -1)
        {
          int j = str.indexOf('-', i);
          if ((j > str.lastIndexOf('0')) && (j > str.lastIndexOf('#')))
          {
            hasFollowingMinusSign = true;
            minusSign = ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getMinusSign();
          }
        }
        originalNumberPattern = str;
      }
      originalNumberFormat = numberFormat;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\SimpleDateFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
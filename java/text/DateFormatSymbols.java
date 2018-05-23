package java.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.locale.provider.TimeZoneNameUtility;
import sun.util.resources.LocaleData;

public class DateFormatSymbols
  implements Serializable, Cloneable
{
  String[] eras = null;
  String[] months = null;
  String[] shortMonths = null;
  String[] weekdays = null;
  String[] shortWeekdays = null;
  String[] ampms = null;
  String[][] zoneStrings = (String[][])null;
  transient boolean isZoneStringsSet = false;
  static final String patternChars = "GyMdkHmsSEDFwWahKzZYuXL";
  static final int PATTERN_ERA = 0;
  static final int PATTERN_YEAR = 1;
  static final int PATTERN_MONTH = 2;
  static final int PATTERN_DAY_OF_MONTH = 3;
  static final int PATTERN_HOUR_OF_DAY1 = 4;
  static final int PATTERN_HOUR_OF_DAY0 = 5;
  static final int PATTERN_MINUTE = 6;
  static final int PATTERN_SECOND = 7;
  static final int PATTERN_MILLISECOND = 8;
  static final int PATTERN_DAY_OF_WEEK = 9;
  static final int PATTERN_DAY_OF_YEAR = 10;
  static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11;
  static final int PATTERN_WEEK_OF_YEAR = 12;
  static final int PATTERN_WEEK_OF_MONTH = 13;
  static final int PATTERN_AM_PM = 14;
  static final int PATTERN_HOUR1 = 15;
  static final int PATTERN_HOUR0 = 16;
  static final int PATTERN_ZONE_NAME = 17;
  static final int PATTERN_ZONE_VALUE = 18;
  static final int PATTERN_WEEK_YEAR = 19;
  static final int PATTERN_ISO_DAY_OF_WEEK = 20;
  static final int PATTERN_ISO_ZONE = 21;
  static final int PATTERN_MONTH_STANDALONE = 22;
  String localPatternChars = null;
  Locale locale = null;
  static final long serialVersionUID = -5987973545549424702L;
  static final int millisPerHour = 3600000;
  private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances = new ConcurrentHashMap(3);
  private transient int lastZoneIndex = 0;
  volatile transient int cachedHashCode = 0;
  
  public DateFormatSymbols()
  {
    initializeData(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public DateFormatSymbols(Locale paramLocale)
  {
    initializeData(paramLocale);
  }
  
  private DateFormatSymbols(boolean paramBoolean) {}
  
  public static Locale[] getAvailableLocales()
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
    return localLocaleServiceProviderPool.getAvailableLocales();
  }
  
  public static final DateFormatSymbols getInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static final DateFormatSymbols getInstance(Locale paramLocale)
  {
    DateFormatSymbols localDateFormatSymbols = getProviderInstance(paramLocale);
    if (localDateFormatSymbols != null) {
      return localDateFormatSymbols;
    }
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  static final DateFormatSymbols getInstanceRef(Locale paramLocale)
  {
    DateFormatSymbols localDateFormatSymbols = getProviderInstance(paramLocale);
    if (localDateFormatSymbols != null) {
      return localDateFormatSymbols;
    }
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  private static DateFormatSymbols getProviderInstance(Locale paramLocale)
  {
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, paramLocale);
    DateFormatSymbolsProvider localDateFormatSymbolsProvider = localLocaleProviderAdapter.getDateFormatSymbolsProvider();
    DateFormatSymbols localDateFormatSymbols = localDateFormatSymbolsProvider.getInstance(paramLocale);
    if (localDateFormatSymbols == null)
    {
      localDateFormatSymbolsProvider = LocaleProviderAdapter.forJRE().getDateFormatSymbolsProvider();
      localDateFormatSymbols = localDateFormatSymbolsProvider.getInstance(paramLocale);
    }
    return localDateFormatSymbols;
  }
  
  public String[] getEras()
  {
    return (String[])Arrays.copyOf(eras, eras.length);
  }
  
  public void setEras(String[] paramArrayOfString)
  {
    eras = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    cachedHashCode = 0;
  }
  
  public String[] getMonths()
  {
    return (String[])Arrays.copyOf(months, months.length);
  }
  
  public void setMonths(String[] paramArrayOfString)
  {
    months = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    cachedHashCode = 0;
  }
  
  public String[] getShortMonths()
  {
    return (String[])Arrays.copyOf(shortMonths, shortMonths.length);
  }
  
  public void setShortMonths(String[] paramArrayOfString)
  {
    shortMonths = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    cachedHashCode = 0;
  }
  
  public String[] getWeekdays()
  {
    return (String[])Arrays.copyOf(weekdays, weekdays.length);
  }
  
  public void setWeekdays(String[] paramArrayOfString)
  {
    weekdays = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    cachedHashCode = 0;
  }
  
  public String[] getShortWeekdays()
  {
    return (String[])Arrays.copyOf(shortWeekdays, shortWeekdays.length);
  }
  
  public void setShortWeekdays(String[] paramArrayOfString)
  {
    shortWeekdays = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    cachedHashCode = 0;
  }
  
  public String[] getAmPmStrings()
  {
    return (String[])Arrays.copyOf(ampms, ampms.length);
  }
  
  public void setAmPmStrings(String[] paramArrayOfString)
  {
    ampms = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    cachedHashCode = 0;
  }
  
  public String[][] getZoneStrings()
  {
    return getZoneStringsImpl(true);
  }
  
  public void setZoneStrings(String[][] paramArrayOfString)
  {
    String[][] arrayOfString = new String[paramArrayOfString.length][];
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      int j = paramArrayOfString[i].length;
      if (j < 5) {
        throw new IllegalArgumentException();
      }
      arrayOfString[i] = ((String[])Arrays.copyOf(paramArrayOfString[i], j));
    }
    zoneStrings = arrayOfString;
    isZoneStringsSet = true;
    cachedHashCode = 0;
  }
  
  public String getLocalPatternChars()
  {
    return localPatternChars;
  }
  
  public void setLocalPatternChars(String paramString)
  {
    localPatternChars = paramString.toString();
    cachedHashCode = 0;
  }
  
  public Object clone()
  {
    try
    {
      DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)super.clone();
      copyMembers(this, localDateFormatSymbols);
      return localDateFormatSymbols;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public int hashCode()
  {
    int i = cachedHashCode;
    if (i == 0)
    {
      i = 5;
      i = 11 * i + Arrays.hashCode(eras);
      i = 11 * i + Arrays.hashCode(months);
      i = 11 * i + Arrays.hashCode(shortMonths);
      i = 11 * i + Arrays.hashCode(weekdays);
      i = 11 * i + Arrays.hashCode(shortWeekdays);
      i = 11 * i + Arrays.hashCode(ampms);
      i = 11 * i + Arrays.deepHashCode(getZoneStringsWrapper());
      i = 11 * i + Objects.hashCode(localPatternChars);
      cachedHashCode = i;
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    DateFormatSymbols localDateFormatSymbols = (DateFormatSymbols)paramObject;
    return (Arrays.equals(eras, eras)) && (Arrays.equals(months, months)) && (Arrays.equals(shortMonths, shortMonths)) && (Arrays.equals(weekdays, weekdays)) && (Arrays.equals(shortWeekdays, shortWeekdays)) && (Arrays.equals(ampms, ampms)) && (Arrays.deepEquals(getZoneStringsWrapper(), localDateFormatSymbols.getZoneStringsWrapper())) && (((localPatternChars != null) && (localPatternChars.equals(localPatternChars))) || ((localPatternChars == null) && (localPatternChars == null)));
  }
  
  private void initializeData(Locale paramLocale)
  {
    Object localObject1 = (SoftReference)cachedInstances.get(paramLocale);
    Object localObject2;
    if ((localObject1 == null) || ((localObject2 = (DateFormatSymbols)((SoftReference)localObject1).get()) == null))
    {
      if (localObject1 != null) {
        cachedInstances.remove(paramLocale, localObject1);
      }
      localObject2 = new DateFormatSymbols(false);
      LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, paramLocale);
      if (!(localLocaleProviderAdapter instanceof ResourceBundleBasedAdapter)) {
        localLocaleProviderAdapter = LocaleProviderAdapter.getResourceBundleBased();
      }
      ResourceBundle localResourceBundle = ((ResourceBundleBasedAdapter)localLocaleProviderAdapter).getLocaleData().getDateFormatData(paramLocale);
      locale = paramLocale;
      if (localResourceBundle.containsKey("Eras")) {
        eras = localResourceBundle.getStringArray("Eras");
      } else if (localResourceBundle.containsKey("long.Eras")) {
        eras = localResourceBundle.getStringArray("long.Eras");
      } else if (localResourceBundle.containsKey("short.Eras")) {
        eras = localResourceBundle.getStringArray("short.Eras");
      }
      months = localResourceBundle.getStringArray("MonthNames");
      shortMonths = localResourceBundle.getStringArray("MonthAbbreviations");
      ampms = localResourceBundle.getStringArray("AmPmMarkers");
      localPatternChars = localResourceBundle.getString("DateTimePatternChars");
      weekdays = toOneBasedArray(localResourceBundle.getStringArray("DayNames"));
      shortWeekdays = toOneBasedArray(localResourceBundle.getStringArray("DayAbbreviations"));
      localObject1 = new SoftReference(localObject2);
      SoftReference localSoftReference1 = (SoftReference)cachedInstances.putIfAbsent(paramLocale, localObject1);
      if (localSoftReference1 != null)
      {
        localObject3 = (DateFormatSymbols)localSoftReference1.get();
        if (localObject3 == null)
        {
          cachedInstances.replace(paramLocale, localSoftReference1, localObject1);
        }
        else
        {
          localObject1 = localSoftReference1;
          localObject2 = localObject3;
        }
      }
      Object localObject3 = localResourceBundle.getLocale();
      if (!((Locale)localObject3).equals(paramLocale))
      {
        SoftReference localSoftReference2 = (SoftReference)cachedInstances.putIfAbsent(localObject3, localObject1);
        if ((localSoftReference2 != null) && (localSoftReference2.get() == null)) {
          cachedInstances.replace(localObject3, localSoftReference2, localObject1);
        }
      }
    }
    copyMembers((DateFormatSymbols)localObject2, this);
  }
  
  private static String[] toOneBasedArray(String[] paramArrayOfString)
  {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i + 1];
    arrayOfString[0] = "";
    for (int j = 0; j < i; j++) {
      arrayOfString[(j + 1)] = paramArrayOfString[j];
    }
    return arrayOfString;
  }
  
  final int getZoneIndex(String paramString)
  {
    String[][] arrayOfString = getZoneStringsWrapper();
    if ((lastZoneIndex < arrayOfString.length) && (paramString.equals(arrayOfString[lastZoneIndex][0]))) {
      return lastZoneIndex;
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      if (paramString.equals(arrayOfString[i][0]))
      {
        lastZoneIndex = i;
        return i;
      }
    }
    return -1;
  }
  
  final String[][] getZoneStringsWrapper()
  {
    if (isSubclassObject()) {
      return getZoneStrings();
    }
    return getZoneStringsImpl(false);
  }
  
  private String[][] getZoneStringsImpl(boolean paramBoolean)
  {
    if (zoneStrings == null) {
      zoneStrings = TimeZoneNameUtility.getZoneStrings(locale);
    }
    if (!paramBoolean) {
      return zoneStrings;
    }
    int i = zoneStrings.length;
    String[][] arrayOfString = new String[i][];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = ((String[])Arrays.copyOf(zoneStrings[j], zoneStrings[j].length));
    }
    return arrayOfString;
  }
  
  private boolean isSubclassObject()
  {
    return !getClass().getName().equals("java.text.DateFormatSymbols");
  }
  
  private void copyMembers(DateFormatSymbols paramDateFormatSymbols1, DateFormatSymbols paramDateFormatSymbols2)
  {
    locale = locale;
    eras = ((String[])Arrays.copyOf(eras, eras.length));
    months = ((String[])Arrays.copyOf(months, months.length));
    shortMonths = ((String[])Arrays.copyOf(shortMonths, shortMonths.length));
    weekdays = ((String[])Arrays.copyOf(weekdays, weekdays.length));
    shortWeekdays = ((String[])Arrays.copyOf(shortWeekdays, shortWeekdays.length));
    ampms = ((String[])Arrays.copyOf(ampms, ampms.length));
    if (zoneStrings != null) {
      zoneStrings = paramDateFormatSymbols1.getZoneStringsImpl(true);
    } else {
      zoneStrings = ((String[][])null);
    }
    localPatternChars = localPatternChars;
    cachedHashCode = 0;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (zoneStrings == null) {
      zoneStrings = TimeZoneNameUtility.getZoneStrings(locale);
    }
    paramObjectOutputStream.defaultWriteObject();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\DateFormatSymbols.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
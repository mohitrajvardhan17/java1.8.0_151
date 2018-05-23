package sun.util.locale.provider;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.resources.LocaleData;
import sun.util.spi.CalendarProvider;

public class JRELocaleProviderAdapter
  extends LocaleProviderAdapter
  implements ResourceBundleBasedAdapter
{
  private static final String LOCALE_DATA_JAR_NAME = "localedata.jar";
  private final ConcurrentMap<String, Set<String>> langtagSets = new ConcurrentHashMap();
  private final ConcurrentMap<Locale, LocaleResources> localeResourcesMap = new ConcurrentHashMap();
  private volatile LocaleData localeData;
  private volatile BreakIteratorProvider breakIteratorProvider = null;
  private volatile CollatorProvider collatorProvider = null;
  private volatile DateFormatProvider dateFormatProvider = null;
  private volatile DateFormatSymbolsProvider dateFormatSymbolsProvider = null;
  private volatile DecimalFormatSymbolsProvider decimalFormatSymbolsProvider = null;
  private volatile NumberFormatProvider numberFormatProvider = null;
  private volatile CurrencyNameProvider currencyNameProvider = null;
  private volatile LocaleNameProvider localeNameProvider = null;
  private volatile TimeZoneNameProvider timeZoneNameProvider = null;
  private volatile CalendarDataProvider calendarDataProvider = null;
  private volatile CalendarNameProvider calendarNameProvider = null;
  private volatile CalendarProvider calendarProvider = null;
  private static volatile Boolean isNonENSupported = null;
  
  public JRELocaleProviderAdapter() {}
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.JRE;
  }
  
  public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass)
  {
    switch (paramClass.getSimpleName())
    {
    case "BreakIteratorProvider": 
      return getBreakIteratorProvider();
    case "CollatorProvider": 
      return getCollatorProvider();
    case "DateFormatProvider": 
      return getDateFormatProvider();
    case "DateFormatSymbolsProvider": 
      return getDateFormatSymbolsProvider();
    case "DecimalFormatSymbolsProvider": 
      return getDecimalFormatSymbolsProvider();
    case "NumberFormatProvider": 
      return getNumberFormatProvider();
    case "CurrencyNameProvider": 
      return getCurrencyNameProvider();
    case "LocaleNameProvider": 
      return getLocaleNameProvider();
    case "TimeZoneNameProvider": 
      return getTimeZoneNameProvider();
    case "CalendarDataProvider": 
      return getCalendarDataProvider();
    case "CalendarNameProvider": 
      return getCalendarNameProvider();
    case "CalendarProvider": 
      return getCalendarProvider();
    }
    throw new InternalError("should not come down here");
  }
  
  public BreakIteratorProvider getBreakIteratorProvider()
  {
    if (breakIteratorProvider == null)
    {
      BreakIteratorProviderImpl localBreakIteratorProviderImpl = new BreakIteratorProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (breakIteratorProvider == null) {
          breakIteratorProvider = localBreakIteratorProviderImpl;
        }
      }
    }
    return breakIteratorProvider;
  }
  
  public CollatorProvider getCollatorProvider()
  {
    if (collatorProvider == null)
    {
      CollatorProviderImpl localCollatorProviderImpl = new CollatorProviderImpl(getAdapterType(), getLanguageTagSet("CollationData"));
      synchronized (this)
      {
        if (collatorProvider == null) {
          collatorProvider = localCollatorProviderImpl;
        }
      }
    }
    return collatorProvider;
  }
  
  public DateFormatProvider getDateFormatProvider()
  {
    if (dateFormatProvider == null)
    {
      DateFormatProviderImpl localDateFormatProviderImpl = new DateFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (dateFormatProvider == null) {
          dateFormatProvider = localDateFormatProviderImpl;
        }
      }
    }
    return dateFormatProvider;
  }
  
  public DateFormatSymbolsProvider getDateFormatSymbolsProvider()
  {
    if (dateFormatSymbolsProvider == null)
    {
      DateFormatSymbolsProviderImpl localDateFormatSymbolsProviderImpl = new DateFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (dateFormatSymbolsProvider == null) {
          dateFormatSymbolsProvider = localDateFormatSymbolsProviderImpl;
        }
      }
    }
    return dateFormatSymbolsProvider;
  }
  
  public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider()
  {
    if (decimalFormatSymbolsProvider == null)
    {
      DecimalFormatSymbolsProviderImpl localDecimalFormatSymbolsProviderImpl = new DecimalFormatSymbolsProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (decimalFormatSymbolsProvider == null) {
          decimalFormatSymbolsProvider = localDecimalFormatSymbolsProviderImpl;
        }
      }
    }
    return decimalFormatSymbolsProvider;
  }
  
  public NumberFormatProvider getNumberFormatProvider()
  {
    if (numberFormatProvider == null)
    {
      NumberFormatProviderImpl localNumberFormatProviderImpl = new NumberFormatProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (numberFormatProvider == null) {
          numberFormatProvider = localNumberFormatProviderImpl;
        }
      }
    }
    return numberFormatProvider;
  }
  
  public CurrencyNameProvider getCurrencyNameProvider()
  {
    if (currencyNameProvider == null)
    {
      CurrencyNameProviderImpl localCurrencyNameProviderImpl = new CurrencyNameProviderImpl(getAdapterType(), getLanguageTagSet("CurrencyNames"));
      synchronized (this)
      {
        if (currencyNameProvider == null) {
          currencyNameProvider = localCurrencyNameProviderImpl;
        }
      }
    }
    return currencyNameProvider;
  }
  
  public LocaleNameProvider getLocaleNameProvider()
  {
    if (localeNameProvider == null)
    {
      LocaleNameProviderImpl localLocaleNameProviderImpl = new LocaleNameProviderImpl(getAdapterType(), getLanguageTagSet("LocaleNames"));
      synchronized (this)
      {
        if (localeNameProvider == null) {
          localeNameProvider = localLocaleNameProviderImpl;
        }
      }
    }
    return localeNameProvider;
  }
  
  public TimeZoneNameProvider getTimeZoneNameProvider()
  {
    if (timeZoneNameProvider == null)
    {
      TimeZoneNameProviderImpl localTimeZoneNameProviderImpl = new TimeZoneNameProviderImpl(getAdapterType(), getLanguageTagSet("TimeZoneNames"));
      synchronized (this)
      {
        if (timeZoneNameProvider == null) {
          timeZoneNameProvider = localTimeZoneNameProviderImpl;
        }
      }
    }
    return timeZoneNameProvider;
  }
  
  public CalendarDataProvider getCalendarDataProvider()
  {
    if (calendarDataProvider == null)
    {
      CalendarDataProviderImpl localCalendarDataProviderImpl = new CalendarDataProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
      synchronized (this)
      {
        if (calendarDataProvider == null) {
          calendarDataProvider = localCalendarDataProviderImpl;
        }
      }
    }
    return calendarDataProvider;
  }
  
  public CalendarNameProvider getCalendarNameProvider()
  {
    if (calendarNameProvider == null)
    {
      CalendarNameProviderImpl localCalendarNameProviderImpl = new CalendarNameProviderImpl(getAdapterType(), getLanguageTagSet("FormatData"));
      synchronized (this)
      {
        if (calendarNameProvider == null) {
          calendarNameProvider = localCalendarNameProviderImpl;
        }
      }
    }
    return calendarNameProvider;
  }
  
  public CalendarProvider getCalendarProvider()
  {
    if (calendarProvider == null)
    {
      CalendarProviderImpl localCalendarProviderImpl = new CalendarProviderImpl(getAdapterType(), getLanguageTagSet("CalendarData"));
      synchronized (this)
      {
        if (calendarProvider == null) {
          calendarProvider = localCalendarProviderImpl;
        }
      }
    }
    return calendarProvider;
  }
  
  public LocaleResources getLocaleResources(Locale paramLocale)
  {
    Object localObject = (LocaleResources)localeResourcesMap.get(paramLocale);
    if (localObject == null)
    {
      localObject = new LocaleResources(this, paramLocale);
      LocaleResources localLocaleResources = (LocaleResources)localeResourcesMap.putIfAbsent(paramLocale, localObject);
      if (localLocaleResources != null) {
        localObject = localLocaleResources;
      }
    }
    return (LocaleResources)localObject;
  }
  
  public LocaleData getLocaleData()
  {
    if (localeData == null) {
      synchronized (this)
      {
        if (localeData == null) {
          localeData = new LocaleData(getAdapterType());
        }
      }
    }
    return localeData;
  }
  
  public Locale[] getAvailableLocales()
  {
    return (Locale[])AvailableJRELocales.localeList.clone();
  }
  
  public Set<String> getLanguageTagSet(String paramString)
  {
    Object localObject = (Set)langtagSets.get(paramString);
    if (localObject == null)
    {
      localObject = createLanguageTagSet(paramString);
      Set localSet = (Set)langtagSets.putIfAbsent(paramString, localObject);
      if (localSet != null) {
        localObject = localSet;
      }
    }
    return (Set<String>)localObject;
  }
  
  protected Set<String> createLanguageTagSet(String paramString)
  {
    String str1 = LocaleDataMetaInfo.getSupportedLocaleString(paramString);
    if (str1 == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1);
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      if (str2.equals("|")) {
        if (!isNonENLangSupported()) {
          break;
        }
      } else {
        localHashSet.add(str2);
      }
    }
    return localHashSet;
  }
  
  private static Locale[] createAvailableLocales()
  {
    String str1 = LocaleDataMetaInfo.getSupportedLocaleString("AvailableLocales");
    if (str1.length() == 0) {
      throw new InternalError("No available locales for JRE");
    }
    int i = str1.indexOf('|');
    StringTokenizer localStringTokenizer;
    if (isNonENLangSupported()) {
      localStringTokenizer = new StringTokenizer(str1.substring(0, i) + str1.substring(i + 1));
    } else {
      localStringTokenizer = new StringTokenizer(str1.substring(0, i));
    }
    int j = localStringTokenizer.countTokens();
    Locale[] arrayOfLocale = new Locale[j + 1];
    arrayOfLocale[0] = Locale.ROOT;
    for (int k = 1; k <= j; k++)
    {
      String str2 = localStringTokenizer.nextToken();
      switch (str2)
      {
      case "ja-JP-JP": 
        arrayOfLocale[k] = JRELocaleConstants.JA_JP_JP;
        break;
      case "no-NO-NY": 
        arrayOfLocale[k] = JRELocaleConstants.NO_NO_NY;
        break;
      case "th-TH-TH": 
        arrayOfLocale[k] = JRELocaleConstants.TH_TH_TH;
        break;
      default: 
        arrayOfLocale[k] = Locale.forLanguageTag(str2);
      }
    }
    return arrayOfLocale;
  }
  
  private static boolean isNonENLangSupported()
  {
    if (isNonENSupported == null) {
      synchronized (JRELocaleProviderAdapter.class)
      {
        if (isNonENSupported == null)
        {
          String str1 = File.separator;
          String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "localedata.jar";
          File localFile = new File(str2);
          isNonENSupported = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
          {
            public Boolean run()
            {
              return Boolean.valueOf(val$f.exists());
            }
          });
        }
      }
    }
    return isNonENSupported.booleanValue();
  }
  
  private static class AvailableJRELocales
  {
    private static final Locale[] localeList = ;
    
    private AvailableJRELocales() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\JRELocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import sun.util.spi.CalendarProvider;

public class HostLocaleProviderAdapterImpl
{
  private static final int CAT_DISPLAY = 0;
  private static final int CAT_FORMAT = 1;
  private static final int NF_NUMBER = 0;
  private static final int NF_CURRENCY = 1;
  private static final int NF_PERCENT = 2;
  private static final int NF_INTEGER = 3;
  private static final int NF_MAX = 3;
  private static final int CD_FIRSTDAYOFWEEK = 0;
  private static final int CD_MINIMALDAYSINFIRSTWEEK = 1;
  private static final int DN_CURRENCY_NAME = 0;
  private static final int DN_CURRENCY_SYMBOL = 1;
  private static final int DN_LOCALE_LANGUAGE = 2;
  private static final int DN_LOCALE_SCRIPT = 3;
  private static final int DN_LOCALE_REGION = 4;
  private static final int DN_LOCALE_VARIANT = 5;
  private static final String[] calIDToLDML = { "", "gregory", "gregory_en-US", "japanese", "roc", "", "islamic", "buddhist", "hebrew", "gregory_fr", "gregory_ar", "gregory_en", "gregory_fr" };
  private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> dateFormatCache = new ConcurrentHashMap();
  private static ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> dateFormatSymbolsCache = new ConcurrentHashMap();
  private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> numberFormatCache = new ConcurrentHashMap();
  private static ConcurrentMap<Locale, SoftReference<DecimalFormatSymbols>> decimalFormatSymbolsCache = new ConcurrentHashMap();
  private static final Set<Locale> supportedLocaleSet;
  private static final String nativeDisplayLanguage;
  private static final Locale[] supportedLocale = (Locale[])supportedLocaleSet.toArray(new Locale[0]);
  
  public HostLocaleProviderAdapterImpl() {}
  
  public static DateFormatProvider getDateFormatProvider()
  {
    new DateFormatProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.access$000();
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
      }
      
      public DateFormat getDateInstance(int paramAnonymousInt, Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getDateTimePatterns(paramAnonymousLocale);
        return new SimpleDateFormat((String)localAtomicReferenceArray.get(paramAnonymousInt / 2), HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale));
      }
      
      public DateFormat getTimeInstance(int paramAnonymousInt, Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getDateTimePatterns(paramAnonymousLocale);
        return new SimpleDateFormat((String)localAtomicReferenceArray.get(paramAnonymousInt / 2 + 2), HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale));
      }
      
      public DateFormat getDateTimeInstance(int paramAnonymousInt1, int paramAnonymousInt2, Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getDateTimePatterns(paramAnonymousLocale);
        String str = (String)localAtomicReferenceArray.get(paramAnonymousInt1 / 2) + " " + (String)localAtomicReferenceArray.get(paramAnonymousInt2 / 2 + 2);
        return new SimpleDateFormat(str, HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale));
      }
      
      private AtomicReferenceArray<String> getDateTimePatterns(Locale paramAnonymousLocale)
      {
        SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatCache.get(paramAnonymousLocale);
        AtomicReferenceArray localAtomicReferenceArray;
        if ((localSoftReference == null) || ((localAtomicReferenceArray = (AtomicReferenceArray)localSoftReference.get()) == null))
        {
          String str = HostLocaleProviderAdapterImpl.removeExtensions(paramAnonymousLocale).toLanguageTag();
          localAtomicReferenceArray = new AtomicReferenceArray(4);
          localAtomicReferenceArray.compareAndSet(0, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.access$500(1, -1, str)));
          localAtomicReferenceArray.compareAndSet(1, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.access$500(3, -1, str)));
          localAtomicReferenceArray.compareAndSet(2, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.access$500(-1, 1, str)));
          localAtomicReferenceArray.compareAndSet(3, null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.access$500(-1, 3, str)));
          localSoftReference = new SoftReference(localAtomicReferenceArray);
          HostLocaleProviderAdapterImpl.dateFormatCache.put(paramAnonymousLocale, localSoftReference);
        }
        return localAtomicReferenceArray;
      }
    };
  }
  
  public static DateFormatSymbolsProvider getDateFormatSymbolsProvider()
  {
    new DateFormatSymbolsProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.access$000();
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
      }
      
      public DateFormatSymbols getInstance(Locale paramAnonymousLocale)
      {
        SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.get(paramAnonymousLocale);
        DateFormatSymbols localDateFormatSymbols;
        if ((localSoftReference == null) || ((localDateFormatSymbols = (DateFormatSymbols)localSoftReference.get()) == null))
        {
          localDateFormatSymbols = new DateFormatSymbols(paramAnonymousLocale);
          String str = HostLocaleProviderAdapterImpl.removeExtensions(paramAnonymousLocale).toLanguageTag();
          localDateFormatSymbols.setAmPmStrings(HostLocaleProviderAdapterImpl.getAmPmStrings(str, localDateFormatSymbols.getAmPmStrings()));
          localDateFormatSymbols.setEras(HostLocaleProviderAdapterImpl.getEras(str, localDateFormatSymbols.getEras()));
          localDateFormatSymbols.setMonths(HostLocaleProviderAdapterImpl.getMonths(str, localDateFormatSymbols.getMonths()));
          localDateFormatSymbols.setShortMonths(HostLocaleProviderAdapterImpl.getShortMonths(str, localDateFormatSymbols.getShortMonths()));
          localDateFormatSymbols.setWeekdays(HostLocaleProviderAdapterImpl.getWeekdays(str, localDateFormatSymbols.getWeekdays()));
          localDateFormatSymbols.setShortWeekdays(HostLocaleProviderAdapterImpl.getShortWeekdays(str, localDateFormatSymbols.getShortWeekdays()));
          localSoftReference = new SoftReference(localDateFormatSymbols);
          HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.put(paramAnonymousLocale, localSoftReference);
        }
        return (DateFormatSymbols)localDateFormatSymbols.clone();
      }
    };
  }
  
  public static NumberFormatProvider getNumberFormatProvider()
  {
    new NumberFormatProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.access$1400();
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(paramAnonymousLocale);
      }
      
      public NumberFormat getCurrencyInstance(Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
        return new DecimalFormat((String)localAtomicReferenceArray.get(1), DecimalFormatSymbols.getInstance(paramAnonymousLocale));
      }
      
      public NumberFormat getIntegerInstance(Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
        return new DecimalFormat((String)localAtomicReferenceArray.get(3), DecimalFormatSymbols.getInstance(paramAnonymousLocale));
      }
      
      public NumberFormat getNumberInstance(Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
        return new DecimalFormat((String)localAtomicReferenceArray.get(0), DecimalFormatSymbols.getInstance(paramAnonymousLocale));
      }
      
      public NumberFormat getPercentInstance(Locale paramAnonymousLocale)
      {
        AtomicReferenceArray localAtomicReferenceArray = getNumberPatterns(paramAnonymousLocale);
        return new DecimalFormat((String)localAtomicReferenceArray.get(2), DecimalFormatSymbols.getInstance(paramAnonymousLocale));
      }
      
      private AtomicReferenceArray<String> getNumberPatterns(Locale paramAnonymousLocale)
      {
        SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.numberFormatCache.get(paramAnonymousLocale);
        AtomicReferenceArray localAtomicReferenceArray;
        if ((localSoftReference == null) || ((localAtomicReferenceArray = (AtomicReferenceArray)localSoftReference.get()) == null))
        {
          String str = paramAnonymousLocale.toLanguageTag();
          localAtomicReferenceArray = new AtomicReferenceArray(4);
          for (int i = 0; i <= 3; i++) {
            localAtomicReferenceArray.compareAndSet(i, null, HostLocaleProviderAdapterImpl.getNumberPattern(i, str));
          }
          localSoftReference = new SoftReference(localAtomicReferenceArray);
          HostLocaleProviderAdapterImpl.numberFormatCache.put(paramAnonymousLocale, localSoftReference);
        }
        return localAtomicReferenceArray;
      }
    };
  }
  
  public static DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider()
  {
    new DecimalFormatSymbolsProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.access$1400();
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(paramAnonymousLocale);
      }
      
      public DecimalFormatSymbols getInstance(Locale paramAnonymousLocale)
      {
        SoftReference localSoftReference = (SoftReference)HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.get(paramAnonymousLocale);
        DecimalFormatSymbols localDecimalFormatSymbols;
        if ((localSoftReference == null) || ((localDecimalFormatSymbols = (DecimalFormatSymbols)localSoftReference.get()) == null))
        {
          localDecimalFormatSymbols = new DecimalFormatSymbols(HostLocaleProviderAdapterImpl.getNumberLocale(paramAnonymousLocale));
          String str = HostLocaleProviderAdapterImpl.removeExtensions(paramAnonymousLocale).toLanguageTag();
          localDecimalFormatSymbols.setInternationalCurrencySymbol(HostLocaleProviderAdapterImpl.getInternationalCurrencySymbol(str, localDecimalFormatSymbols.getInternationalCurrencySymbol()));
          localDecimalFormatSymbols.setCurrencySymbol(HostLocaleProviderAdapterImpl.getCurrencySymbol(str, localDecimalFormatSymbols.getCurrencySymbol()));
          localDecimalFormatSymbols.setDecimalSeparator(HostLocaleProviderAdapterImpl.getDecimalSeparator(str, localDecimalFormatSymbols.getDecimalSeparator()));
          localDecimalFormatSymbols.setGroupingSeparator(HostLocaleProviderAdapterImpl.getGroupingSeparator(str, localDecimalFormatSymbols.getGroupingSeparator()));
          localDecimalFormatSymbols.setInfinity(HostLocaleProviderAdapterImpl.getInfinity(str, localDecimalFormatSymbols.getInfinity()));
          localDecimalFormatSymbols.setMinusSign(HostLocaleProviderAdapterImpl.getMinusSign(str, localDecimalFormatSymbols.getMinusSign()));
          localDecimalFormatSymbols.setMonetaryDecimalSeparator(HostLocaleProviderAdapterImpl.getMonetaryDecimalSeparator(str, localDecimalFormatSymbols.getMonetaryDecimalSeparator()));
          localDecimalFormatSymbols.setNaN(HostLocaleProviderAdapterImpl.getNaN(str, localDecimalFormatSymbols.getNaN()));
          localDecimalFormatSymbols.setPercent(HostLocaleProviderAdapterImpl.getPercent(str, localDecimalFormatSymbols.getPercent()));
          localDecimalFormatSymbols.setPerMill(HostLocaleProviderAdapterImpl.getPerMill(str, localDecimalFormatSymbols.getPerMill()));
          localDecimalFormatSymbols.setZeroDigit(HostLocaleProviderAdapterImpl.getZeroDigit(str, localDecimalFormatSymbols.getZeroDigit()));
          localSoftReference = new SoftReference(localDecimalFormatSymbols);
          HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.put(paramAnonymousLocale, localSoftReference);
        }
        return (DecimalFormatSymbols)localDecimalFormatSymbols.clone();
      }
    };
  }
  
  public static CalendarDataProvider getCalendarDataProvider()
  {
    new CalendarDataProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.access$000();
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
      }
      
      public int getFirstDayOfWeek(Locale paramAnonymousLocale)
      {
        int i = HostLocaleProviderAdapterImpl.getCalendarDataValue(HostLocaleProviderAdapterImpl.access$400(paramAnonymousLocale).toLanguageTag(), 0);
        if (i != -1) {
          return (i + 1) % 7 + 1;
        }
        return 0;
      }
      
      public int getMinimalDaysInFirstWeek(Locale paramAnonymousLocale)
      {
        return 0;
      }
    };
  }
  
  public static CalendarProvider getCalendarProvider()
  {
    new CalendarProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.access$000();
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(paramAnonymousLocale);
      }
      
      public Calendar getInstance(TimeZone paramAnonymousTimeZone, Locale paramAnonymousLocale)
      {
        return new Calendar.Builder().setLocale(HostLocaleProviderAdapterImpl.getCalendarLocale(paramAnonymousLocale)).setTimeZone(paramAnonymousTimeZone).setInstant(System.currentTimeMillis()).build();
      }
    };
  }
  
  public static CurrencyNameProvider getCurrencyNameProvider()
  {
    new CurrencyNameProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.supportedLocale;
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return (HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(paramAnonymousLocale.stripExtensions())) && (paramAnonymousLocale.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage));
      }
      
      public String getSymbol(String paramAnonymousString, Locale paramAnonymousLocale)
      {
        try
        {
          if (Currency.getInstance(paramAnonymousLocale).getCurrencyCode().equals(paramAnonymousString)) {
            return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 1, paramAnonymousString);
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
        return null;
      }
      
      public String getDisplayName(String paramAnonymousString, Locale paramAnonymousLocale)
      {
        try
        {
          if (Currency.getInstance(paramAnonymousLocale).getCurrencyCode().equals(paramAnonymousString)) {
            return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 0, paramAnonymousString);
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException) {}
        return null;
      }
    };
  }
  
  public static LocaleNameProvider getLocaleNameProvider()
  {
    new LocaleNameProvider()
    {
      public Locale[] getAvailableLocales()
      {
        return HostLocaleProviderAdapterImpl.supportedLocale;
      }
      
      public boolean isSupportedLocale(Locale paramAnonymousLocale)
      {
        return (HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(paramAnonymousLocale.stripExtensions())) && (paramAnonymousLocale.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage));
      }
      
      public String getDisplayLanguage(String paramAnonymousString, Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 2, paramAnonymousString);
      }
      
      public String getDisplayCountry(String paramAnonymousString, Locale paramAnonymousLocale)
      {
        return HostLocaleProviderAdapterImpl.getDisplayString(paramAnonymousLocale.toLanguageTag(), 4, HostLocaleProviderAdapterImpl.nativeDisplayLanguage + "-" + paramAnonymousString);
      }
      
      public String getDisplayScript(String paramAnonymousString, Locale paramAnonymousLocale)
      {
        return null;
      }
      
      public String getDisplayVariant(String paramAnonymousString, Locale paramAnonymousLocale)
      {
        return null;
      }
    };
  }
  
  private static String convertDateTimePattern(String paramString)
  {
    String str = paramString.replaceAll("dddd", "EEEE");
    str = str.replaceAll("ddd", "EEE");
    str = str.replaceAll("tt", "aa");
    str = str.replaceAll("g", "GG");
    return str;
  }
  
  private static Locale[] getSupportedCalendarLocales()
  {
    if ((supportedLocale.length != 0) && (supportedLocaleSet.contains(Locale.JAPAN)) && (isJapaneseCalendar()))
    {
      Locale[] arrayOfLocale = new Locale[supportedLocale.length + 1];
      arrayOfLocale[0] = JRELocaleConstants.JA_JP_JP;
      System.arraycopy(supportedLocale, 0, arrayOfLocale, 1, supportedLocale.length);
      return arrayOfLocale;
    }
    return supportedLocale;
  }
  
  private static boolean isSupportedCalendarLocale(Locale paramLocale)
  {
    Locale localLocale = paramLocale;
    if ((localLocale.hasExtensions()) || (localLocale.getVariant() != "")) {
      localLocale = new Locale.Builder().setLocale(paramLocale).clearExtensions().build();
    }
    if (!supportedLocaleSet.contains(localLocale)) {
      return false;
    }
    int i = getCalendarID(localLocale.toLanguageTag());
    if ((i <= 0) || (i >= calIDToLDML.length)) {
      return false;
    }
    String str1 = paramLocale.getUnicodeLocaleType("ca");
    String str2 = calIDToLDML[i].replaceFirst("_.*", "");
    if (str1 == null) {
      return Calendar.getAvailableCalendarTypes().contains(str2);
    }
    return str1.equals(str2);
  }
  
  private static Locale[] getSupportedNativeDigitLocales()
  {
    if ((supportedLocale.length != 0) && (supportedLocaleSet.contains(JRELocaleConstants.TH_TH)) && (isNativeDigit("th-TH")))
    {
      Locale[] arrayOfLocale = new Locale[supportedLocale.length + 1];
      arrayOfLocale[0] = JRELocaleConstants.TH_TH_TH;
      System.arraycopy(supportedLocale, 0, arrayOfLocale, 1, supportedLocale.length);
      return arrayOfLocale;
    }
    return supportedLocale;
  }
  
  private static boolean isSupportedNativeDigitLocale(Locale paramLocale)
  {
    if (JRELocaleConstants.TH_TH_TH.equals(paramLocale)) {
      return isNativeDigit("th-TH");
    }
    String str = null;
    Locale localLocale = paramLocale;
    if (paramLocale.hasExtensions())
    {
      str = paramLocale.getUnicodeLocaleType("nu");
      localLocale = paramLocale.stripExtensions();
    }
    if (supportedLocaleSet.contains(localLocale))
    {
      if ((str == null) || (str.equals("latn"))) {
        return true;
      }
      if (paramLocale.getLanguage().equals("th")) {
        return ("thai".equals(str)) && (isNativeDigit(paramLocale.toLanguageTag()));
      }
    }
    return false;
  }
  
  private static Locale removeExtensions(Locale paramLocale)
  {
    return new Locale.Builder().setLocale(paramLocale).clearExtensions().build();
  }
  
  private static boolean isJapaneseCalendar()
  {
    return getCalendarID("ja-JP") == 3;
  }
  
  private static Locale getCalendarLocale(Locale paramLocale)
  {
    int i = getCalendarID(paramLocale.toLanguageTag());
    if ((i > 0) && (i < calIDToLDML.length))
    {
      Locale.Builder localBuilder = new Locale.Builder();
      String[] arrayOfString = calIDToLDML[i].split("_");
      if (arrayOfString.length > 1) {
        localBuilder.setLocale(Locale.forLanguageTag(arrayOfString[1]));
      } else {
        localBuilder.setLocale(paramLocale);
      }
      localBuilder.setUnicodeLocaleKeyword("ca", arrayOfString[0]);
      return localBuilder.build();
    }
    return paramLocale;
  }
  
  private static Locale getNumberLocale(Locale paramLocale)
  {
    if ((JRELocaleConstants.TH_TH.equals(paramLocale)) && (isNativeDigit("th-TH")))
    {
      Locale.Builder localBuilder = new Locale.Builder().setLocale(paramLocale);
      localBuilder.setUnicodeLocaleKeyword("nu", "thai");
      return localBuilder.build();
    }
    return paramLocale;
  }
  
  private static native boolean initialize();
  
  private static native String getDefaultLocale(int paramInt);
  
  private static native String getDateTimePattern(int paramInt1, int paramInt2, String paramString);
  
  private static native int getCalendarID(String paramString);
  
  private static native String[] getAmPmStrings(String paramString, String[] paramArrayOfString);
  
  private static native String[] getEras(String paramString, String[] paramArrayOfString);
  
  private static native String[] getMonths(String paramString, String[] paramArrayOfString);
  
  private static native String[] getShortMonths(String paramString, String[] paramArrayOfString);
  
  private static native String[] getWeekdays(String paramString, String[] paramArrayOfString);
  
  private static native String[] getShortWeekdays(String paramString, String[] paramArrayOfString);
  
  private static native String getNumberPattern(int paramInt, String paramString);
  
  private static native boolean isNativeDigit(String paramString);
  
  private static native String getCurrencySymbol(String paramString1, String paramString2);
  
  private static native char getDecimalSeparator(String paramString, char paramChar);
  
  private static native char getGroupingSeparator(String paramString, char paramChar);
  
  private static native String getInfinity(String paramString1, String paramString2);
  
  private static native String getInternationalCurrencySymbol(String paramString1, String paramString2);
  
  private static native char getMinusSign(String paramString, char paramChar);
  
  private static native char getMonetaryDecimalSeparator(String paramString, char paramChar);
  
  private static native String getNaN(String paramString1, String paramString2);
  
  private static native char getPercent(String paramString, char paramChar);
  
  private static native char getPerMill(String paramString, char paramChar);
  
  private static native char getZeroDigit(String paramString, char paramChar);
  
  private static native int getCalendarDataValue(String paramString, int paramInt);
  
  private static native String getDisplayString(String paramString1, int paramInt, String paramString2);
  
  static
  {
    HashSet localHashSet = new HashSet();
    if (initialize())
    {
      ResourceBundle.Control localControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
      String str1 = getDefaultLocale(0);
      Locale localLocale = Locale.forLanguageTag(str1.replace('_', '-'));
      localHashSet.addAll(localControl.getCandidateLocales("", localLocale));
      nativeDisplayLanguage = localLocale.getLanguage();
      String str2 = getDefaultLocale(1);
      if (!str2.equals(str1))
      {
        localLocale = Locale.forLanguageTag(str2.replace('_', '-'));
        localHashSet.addAll(localControl.getCandidateLocales("", localLocale));
      }
    }
    else
    {
      nativeDisplayLanguage = "";
    }
    supportedLocaleSet = Collections.unmodifiableSet(localHashSet);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\HostLocaleProviderAdapterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
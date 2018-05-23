package sun.util.locale.provider;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.BreakIterator;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;

public class SPILocaleProviderAdapter
  extends AuxLocaleProviderAdapter
{
  public SPILocaleProviderAdapter() {}
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.SPI;
  }
  
  protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> paramClass)
  {
    try
    {
      (LocaleServiceProvider)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public P run()
        {
          LocaleServiceProvider localLocaleServiceProvider1 = null;
          Iterator localIterator = ServiceLoader.loadInstalled(paramClass).iterator();
          while (localIterator.hasNext())
          {
            LocaleServiceProvider localLocaleServiceProvider2 = (LocaleServiceProvider)localIterator.next();
            if (localLocaleServiceProvider1 == null) {
              try
              {
                localLocaleServiceProvider1 = (LocaleServiceProvider)Class.forName(SPILocaleProviderAdapter.class.getCanonicalName() + "$" + paramClass.getSimpleName() + "Delegate").newInstance();
              }
              catch (ClassNotFoundException|InstantiationException|IllegalAccessException localClassNotFoundException)
              {
                LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, localClassNotFoundException.toString());
                return null;
              }
            }
            ((SPILocaleProviderAdapter.Delegate)localLocaleServiceProvider1).addImpl(localLocaleServiceProvider2);
          }
          return localLocaleServiceProvider1;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, localPrivilegedActionException.toString());
    }
    return null;
  }
  
  private static <P extends LocaleServiceProvider> P getImpl(Map<Locale, P> paramMap, Locale paramLocale)
  {
    Iterator localIterator = LocaleServiceProviderPool.getLookupLocales(paramLocale).iterator();
    while (localIterator.hasNext())
    {
      Locale localLocale = (Locale)localIterator.next();
      LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)paramMap.get(localLocale);
      if (localLocaleServiceProvider != null) {
        return localLocaleServiceProvider;
      }
    }
    return null;
  }
  
  static class BreakIteratorProviderDelegate
    extends BreakIteratorProvider
    implements SPILocaleProviderAdapter.Delegate<BreakIteratorProvider>
  {
    private ConcurrentMap<Locale, BreakIteratorProvider> map = new ConcurrentHashMap();
    
    BreakIteratorProviderDelegate() {}
    
    public void addImpl(BreakIteratorProvider paramBreakIteratorProvider)
    {
      for (Locale localLocale : paramBreakIteratorProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramBreakIteratorProvider);
      }
    }
    
    public BreakIteratorProvider getImpl(Locale paramLocale)
    {
      return (BreakIteratorProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public BreakIterator getWordInstance(Locale paramLocale)
    {
      BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
      assert (localBreakIteratorProvider != null);
      return localBreakIteratorProvider.getWordInstance(paramLocale);
    }
    
    public BreakIterator getLineInstance(Locale paramLocale)
    {
      BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
      assert (localBreakIteratorProvider != null);
      return localBreakIteratorProvider.getLineInstance(paramLocale);
    }
    
    public BreakIterator getCharacterInstance(Locale paramLocale)
    {
      BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
      assert (localBreakIteratorProvider != null);
      return localBreakIteratorProvider.getCharacterInstance(paramLocale);
    }
    
    public BreakIterator getSentenceInstance(Locale paramLocale)
    {
      BreakIteratorProvider localBreakIteratorProvider = getImpl(paramLocale);
      assert (localBreakIteratorProvider != null);
      return localBreakIteratorProvider.getSentenceInstance(paramLocale);
    }
  }
  
  static class CalendarDataProviderDelegate
    extends CalendarDataProvider
    implements SPILocaleProviderAdapter.Delegate<CalendarDataProvider>
  {
    private ConcurrentMap<Locale, CalendarDataProvider> map = new ConcurrentHashMap();
    
    CalendarDataProviderDelegate() {}
    
    public void addImpl(CalendarDataProvider paramCalendarDataProvider)
    {
      for (Locale localLocale : paramCalendarDataProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramCalendarDataProvider);
      }
    }
    
    public CalendarDataProvider getImpl(Locale paramLocale)
    {
      return (CalendarDataProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public int getFirstDayOfWeek(Locale paramLocale)
    {
      CalendarDataProvider localCalendarDataProvider = getImpl(paramLocale);
      assert (localCalendarDataProvider != null);
      return localCalendarDataProvider.getFirstDayOfWeek(paramLocale);
    }
    
    public int getMinimalDaysInFirstWeek(Locale paramLocale)
    {
      CalendarDataProvider localCalendarDataProvider = getImpl(paramLocale);
      assert (localCalendarDataProvider != null);
      return localCalendarDataProvider.getMinimalDaysInFirstWeek(paramLocale);
    }
  }
  
  static class CalendarNameProviderDelegate
    extends CalendarNameProvider
    implements SPILocaleProviderAdapter.Delegate<CalendarNameProvider>
  {
    private ConcurrentMap<Locale, CalendarNameProvider> map = new ConcurrentHashMap();
    
    CalendarNameProviderDelegate() {}
    
    public void addImpl(CalendarNameProvider paramCalendarNameProvider)
    {
      for (Locale localLocale : paramCalendarNameProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramCalendarNameProvider);
      }
    }
    
    public CalendarNameProvider getImpl(Locale paramLocale)
    {
      return (CalendarNameProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public String getDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale)
    {
      CalendarNameProvider localCalendarNameProvider = getImpl(paramLocale);
      assert (localCalendarNameProvider != null);
      return localCalendarNameProvider.getDisplayName(paramString, paramInt1, paramInt2, paramInt3, paramLocale);
    }
    
    public Map<String, Integer> getDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale)
    {
      CalendarNameProvider localCalendarNameProvider = getImpl(paramLocale);
      assert (localCalendarNameProvider != null);
      return localCalendarNameProvider.getDisplayNames(paramString, paramInt1, paramInt2, paramLocale);
    }
  }
  
  static class CollatorProviderDelegate
    extends CollatorProvider
    implements SPILocaleProviderAdapter.Delegate<CollatorProvider>
  {
    private ConcurrentMap<Locale, CollatorProvider> map = new ConcurrentHashMap();
    
    CollatorProviderDelegate() {}
    
    public void addImpl(CollatorProvider paramCollatorProvider)
    {
      for (Locale localLocale : paramCollatorProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramCollatorProvider);
      }
    }
    
    public CollatorProvider getImpl(Locale paramLocale)
    {
      return (CollatorProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public Collator getInstance(Locale paramLocale)
    {
      CollatorProvider localCollatorProvider = getImpl(paramLocale);
      assert (localCollatorProvider != null);
      return localCollatorProvider.getInstance(paramLocale);
    }
  }
  
  static class CurrencyNameProviderDelegate
    extends CurrencyNameProvider
    implements SPILocaleProviderAdapter.Delegate<CurrencyNameProvider>
  {
    private ConcurrentMap<Locale, CurrencyNameProvider> map = new ConcurrentHashMap();
    
    CurrencyNameProviderDelegate() {}
    
    public void addImpl(CurrencyNameProvider paramCurrencyNameProvider)
    {
      for (Locale localLocale : paramCurrencyNameProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramCurrencyNameProvider);
      }
    }
    
    public CurrencyNameProvider getImpl(Locale paramLocale)
    {
      return (CurrencyNameProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public String getSymbol(String paramString, Locale paramLocale)
    {
      CurrencyNameProvider localCurrencyNameProvider = getImpl(paramLocale);
      assert (localCurrencyNameProvider != null);
      return localCurrencyNameProvider.getSymbol(paramString, paramLocale);
    }
    
    public String getDisplayName(String paramString, Locale paramLocale)
    {
      CurrencyNameProvider localCurrencyNameProvider = getImpl(paramLocale);
      assert (localCurrencyNameProvider != null);
      return localCurrencyNameProvider.getDisplayName(paramString, paramLocale);
    }
  }
  
  static class DateFormatProviderDelegate
    extends DateFormatProvider
    implements SPILocaleProviderAdapter.Delegate<DateFormatProvider>
  {
    private ConcurrentMap<Locale, DateFormatProvider> map = new ConcurrentHashMap();
    
    DateFormatProviderDelegate() {}
    
    public void addImpl(DateFormatProvider paramDateFormatProvider)
    {
      for (Locale localLocale : paramDateFormatProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramDateFormatProvider);
      }
    }
    
    public DateFormatProvider getImpl(Locale paramLocale)
    {
      return (DateFormatProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public DateFormat getTimeInstance(int paramInt, Locale paramLocale)
    {
      DateFormatProvider localDateFormatProvider = getImpl(paramLocale);
      assert (localDateFormatProvider != null);
      return localDateFormatProvider.getTimeInstance(paramInt, paramLocale);
    }
    
    public DateFormat getDateInstance(int paramInt, Locale paramLocale)
    {
      DateFormatProvider localDateFormatProvider = getImpl(paramLocale);
      assert (localDateFormatProvider != null);
      return localDateFormatProvider.getDateInstance(paramInt, paramLocale);
    }
    
    public DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale)
    {
      DateFormatProvider localDateFormatProvider = getImpl(paramLocale);
      assert (localDateFormatProvider != null);
      return localDateFormatProvider.getDateTimeInstance(paramInt1, paramInt2, paramLocale);
    }
  }
  
  static class DateFormatSymbolsProviderDelegate
    extends DateFormatSymbolsProvider
    implements SPILocaleProviderAdapter.Delegate<DateFormatSymbolsProvider>
  {
    private ConcurrentMap<Locale, DateFormatSymbolsProvider> map = new ConcurrentHashMap();
    
    DateFormatSymbolsProviderDelegate() {}
    
    public void addImpl(DateFormatSymbolsProvider paramDateFormatSymbolsProvider)
    {
      for (Locale localLocale : paramDateFormatSymbolsProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramDateFormatSymbolsProvider);
      }
    }
    
    public DateFormatSymbolsProvider getImpl(Locale paramLocale)
    {
      return (DateFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public DateFormatSymbols getInstance(Locale paramLocale)
    {
      DateFormatSymbolsProvider localDateFormatSymbolsProvider = getImpl(paramLocale);
      assert (localDateFormatSymbolsProvider != null);
      return localDateFormatSymbolsProvider.getInstance(paramLocale);
    }
  }
  
  static class DecimalFormatSymbolsProviderDelegate
    extends DecimalFormatSymbolsProvider
    implements SPILocaleProviderAdapter.Delegate<DecimalFormatSymbolsProvider>
  {
    private ConcurrentMap<Locale, DecimalFormatSymbolsProvider> map = new ConcurrentHashMap();
    
    DecimalFormatSymbolsProviderDelegate() {}
    
    public void addImpl(DecimalFormatSymbolsProvider paramDecimalFormatSymbolsProvider)
    {
      for (Locale localLocale : paramDecimalFormatSymbolsProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramDecimalFormatSymbolsProvider);
      }
    }
    
    public DecimalFormatSymbolsProvider getImpl(Locale paramLocale)
    {
      return (DecimalFormatSymbolsProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public DecimalFormatSymbols getInstance(Locale paramLocale)
    {
      DecimalFormatSymbolsProvider localDecimalFormatSymbolsProvider = getImpl(paramLocale);
      assert (localDecimalFormatSymbolsProvider != null);
      return localDecimalFormatSymbolsProvider.getInstance(paramLocale);
    }
  }
  
  static abstract interface Delegate<P extends LocaleServiceProvider>
  {
    public abstract void addImpl(P paramP);
    
    public abstract P getImpl(Locale paramLocale);
  }
  
  static class LocaleNameProviderDelegate
    extends LocaleNameProvider
    implements SPILocaleProviderAdapter.Delegate<LocaleNameProvider>
  {
    private ConcurrentMap<Locale, LocaleNameProvider> map = new ConcurrentHashMap();
    
    LocaleNameProviderDelegate() {}
    
    public void addImpl(LocaleNameProvider paramLocaleNameProvider)
    {
      for (Locale localLocale : paramLocaleNameProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramLocaleNameProvider);
      }
    }
    
    public LocaleNameProvider getImpl(Locale paramLocale)
    {
      return (LocaleNameProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public String getDisplayLanguage(String paramString, Locale paramLocale)
    {
      LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
      assert (localLocaleNameProvider != null);
      return localLocaleNameProvider.getDisplayLanguage(paramString, paramLocale);
    }
    
    public String getDisplayScript(String paramString, Locale paramLocale)
    {
      LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
      assert (localLocaleNameProvider != null);
      return localLocaleNameProvider.getDisplayScript(paramString, paramLocale);
    }
    
    public String getDisplayCountry(String paramString, Locale paramLocale)
    {
      LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
      assert (localLocaleNameProvider != null);
      return localLocaleNameProvider.getDisplayCountry(paramString, paramLocale);
    }
    
    public String getDisplayVariant(String paramString, Locale paramLocale)
    {
      LocaleNameProvider localLocaleNameProvider = getImpl(paramLocale);
      assert (localLocaleNameProvider != null);
      return localLocaleNameProvider.getDisplayVariant(paramString, paramLocale);
    }
  }
  
  static class NumberFormatProviderDelegate
    extends NumberFormatProvider
    implements SPILocaleProviderAdapter.Delegate<NumberFormatProvider>
  {
    private ConcurrentMap<Locale, NumberFormatProvider> map = new ConcurrentHashMap();
    
    NumberFormatProviderDelegate() {}
    
    public void addImpl(NumberFormatProvider paramNumberFormatProvider)
    {
      for (Locale localLocale : paramNumberFormatProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramNumberFormatProvider);
      }
    }
    
    public NumberFormatProvider getImpl(Locale paramLocale)
    {
      return (NumberFormatProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public NumberFormat getCurrencyInstance(Locale paramLocale)
    {
      NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
      assert (localNumberFormatProvider != null);
      return localNumberFormatProvider.getCurrencyInstance(paramLocale);
    }
    
    public NumberFormat getIntegerInstance(Locale paramLocale)
    {
      NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
      assert (localNumberFormatProvider != null);
      return localNumberFormatProvider.getIntegerInstance(paramLocale);
    }
    
    public NumberFormat getNumberInstance(Locale paramLocale)
    {
      NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
      assert (localNumberFormatProvider != null);
      return localNumberFormatProvider.getNumberInstance(paramLocale);
    }
    
    public NumberFormat getPercentInstance(Locale paramLocale)
    {
      NumberFormatProvider localNumberFormatProvider = getImpl(paramLocale);
      assert (localNumberFormatProvider != null);
      return localNumberFormatProvider.getPercentInstance(paramLocale);
    }
  }
  
  static class TimeZoneNameProviderDelegate
    extends TimeZoneNameProvider
    implements SPILocaleProviderAdapter.Delegate<TimeZoneNameProvider>
  {
    private ConcurrentMap<Locale, TimeZoneNameProvider> map = new ConcurrentHashMap();
    
    TimeZoneNameProviderDelegate() {}
    
    public void addImpl(TimeZoneNameProvider paramTimeZoneNameProvider)
    {
      for (Locale localLocale : paramTimeZoneNameProvider.getAvailableLocales()) {
        map.putIfAbsent(localLocale, paramTimeZoneNameProvider);
      }
    }
    
    public TimeZoneNameProvider getImpl(Locale paramLocale)
    {
      return (TimeZoneNameProvider)SPILocaleProviderAdapter.getImpl(map, paramLocale);
    }
    
    public Locale[] getAvailableLocales()
    {
      return (Locale[])map.keySet().toArray(new Locale[0]);
    }
    
    public boolean isSupportedLocale(Locale paramLocale)
    {
      return map.containsKey(paramLocale);
    }
    
    public String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale)
    {
      TimeZoneNameProvider localTimeZoneNameProvider = getImpl(paramLocale);
      assert (localTimeZoneNameProvider != null);
      return localTimeZoneNameProvider.getDisplayName(paramString, paramBoolean, paramInt, paramLocale);
    }
    
    public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale)
    {
      TimeZoneNameProvider localTimeZoneNameProvider = getImpl(paramLocale);
      assert (localTimeZoneNameProvider != null);
      return localTimeZoneNameProvider.getGenericDisplayName(paramString, paramInt, paramLocale);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\SPILocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
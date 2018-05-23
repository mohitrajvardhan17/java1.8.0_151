package sun.util.locale.provider;

import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.logging.PlatformLogger;

public final class LocaleServiceProviderPool
{
  private static ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProviderPool> poolOfPools = new ConcurrentHashMap();
  private ConcurrentMap<LocaleProviderAdapter.Type, LocaleServiceProvider> providers = new ConcurrentHashMap();
  private ConcurrentMap<Locale, List<LocaleProviderAdapter.Type>> providersCache = new ConcurrentHashMap();
  private Set<Locale> availableLocales = null;
  private Class<? extends LocaleServiceProvider> providerClass;
  static final Class<LocaleServiceProvider>[] spiClasses = (Class[])new Class[] { BreakIteratorProvider.class, CollatorProvider.class, DateFormatProvider.class, DateFormatSymbolsProvider.class, DecimalFormatSymbolsProvider.class, NumberFormatProvider.class, CurrencyNameProvider.class, LocaleNameProvider.class, TimeZoneNameProvider.class, CalendarDataProvider.class };
  private static List<LocaleProviderAdapter.Type> NULL_LIST = Collections.emptyList();
  
  public static LocaleServiceProviderPool getPool(Class<? extends LocaleServiceProvider> paramClass)
  {
    Object localObject = (LocaleServiceProviderPool)poolOfPools.get(paramClass);
    if (localObject == null)
    {
      LocaleServiceProviderPool localLocaleServiceProviderPool = new LocaleServiceProviderPool(paramClass);
      localObject = (LocaleServiceProviderPool)poolOfPools.putIfAbsent(paramClass, localLocaleServiceProviderPool);
      if (localObject == null) {
        localObject = localLocaleServiceProviderPool;
      }
    }
    return (LocaleServiceProviderPool)localObject;
  }
  
  private LocaleServiceProviderPool(Class<? extends LocaleServiceProvider> paramClass)
  {
    providerClass = paramClass;
    Iterator localIterator = LocaleProviderAdapter.getAdapterPreference().iterator();
    while (localIterator.hasNext())
    {
      LocaleProviderAdapter.Type localType = (LocaleProviderAdapter.Type)localIterator.next();
      LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.forType(localType);
      if (localLocaleProviderAdapter != null)
      {
        LocaleServiceProvider localLocaleServiceProvider = localLocaleProviderAdapter.getLocaleServiceProvider(paramClass);
        if (localLocaleServiceProvider != null) {
          providers.putIfAbsent(localType, localLocaleServiceProvider);
        }
      }
    }
  }
  
  static void config(Class<? extends Object> paramClass, String paramString)
  {
    PlatformLogger localPlatformLogger = PlatformLogger.getLogger(paramClass.getCanonicalName());
    localPlatformLogger.config(paramString);
  }
  
  public static Locale[] getAllAvailableLocales()
  {
    return (Locale[])AllAvailableLocales.allAvailableLocales.clone();
  }
  
  public Locale[] getAvailableLocales()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(getAvailableLocaleSet());
    localHashSet.addAll(Arrays.asList(LocaleProviderAdapter.forJRE().getAvailableLocales()));
    Locale[] arrayOfLocale = new Locale[localHashSet.size()];
    localHashSet.toArray(arrayOfLocale);
    return arrayOfLocale;
  }
  
  private synchronized Set<Locale> getAvailableLocaleSet()
  {
    if (availableLocales == null)
    {
      availableLocales = new HashSet();
      Iterator localIterator = providers.values().iterator();
      while (localIterator.hasNext())
      {
        LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)localIterator.next();
        Locale[] arrayOfLocale1 = localLocaleServiceProvider.getAvailableLocales();
        for (Locale localLocale : arrayOfLocale1) {
          availableLocales.add(getLookupLocale(localLocale));
        }
      }
    }
    return availableLocales;
  }
  
  boolean hasProviders()
  {
    return (providers.size() != 1) || ((providers.get(LocaleProviderAdapter.Type.JRE) == null) && (providers.get(LocaleProviderAdapter.Type.FALLBACK) == null));
  }
  
  public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, Object... paramVarArgs)
  {
    return (S)getLocalizedObjectImpl(paramLocalizedObjectGetter, paramLocale, true, null, paramVarArgs);
  }
  
  public <P extends LocaleServiceProvider, S> S getLocalizedObject(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    return (S)getLocalizedObjectImpl(paramLocalizedObjectGetter, paramLocale, false, paramString, paramVarArgs);
  }
  
  private <P extends LocaleServiceProvider, S> S getLocalizedObjectImpl(LocalizedObjectGetter<P, S> paramLocalizedObjectGetter, Locale paramLocale, boolean paramBoolean, String paramString, Object... paramVarArgs)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    if (!hasProviders()) {
      return (S)paramLocalizedObjectGetter.getObject((LocaleServiceProvider)providers.get(LocaleProviderAdapter.defaultLocaleProviderAdapter), paramLocale, paramString, paramVarArgs);
    }
    List localList = getLookupLocales(paramLocale);
    Set localSet = getAvailableLocaleSet();
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      Locale localLocale = (Locale)localIterator1.next();
      if (localSet.contains(localLocale))
      {
        Iterator localIterator2 = findProviders(localLocale).iterator();
        while (localIterator2.hasNext())
        {
          LocaleProviderAdapter.Type localType = (LocaleProviderAdapter.Type)localIterator2.next();
          LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)providers.get(localType);
          Object localObject = paramLocalizedObjectGetter.getObject(localLocaleServiceProvider, paramLocale, paramString, paramVarArgs);
          if (localObject != null) {
            return (S)localObject;
          }
          if (paramBoolean) {
            config(LocaleServiceProviderPool.class, "A locale sensitive service provider returned null for a localized objects,  which should not happen.  provider: " + localLocaleServiceProvider + " locale: " + paramLocale);
          }
        }
      }
    }
    return null;
  }
  
  private List<LocaleProviderAdapter.Type> findProviders(Locale paramLocale)
  {
    Object localObject1 = (List)providersCache.get(paramLocale);
    if (localObject1 == null)
    {
      Object localObject2 = LocaleProviderAdapter.getAdapterPreference().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        LocaleProviderAdapter.Type localType = (LocaleProviderAdapter.Type)((Iterator)localObject2).next();
        LocaleServiceProvider localLocaleServiceProvider = (LocaleServiceProvider)providers.get(localType);
        if ((localLocaleServiceProvider != null) && (localLocaleServiceProvider.isSupportedLocale(paramLocale)))
        {
          if (localObject1 == null) {
            localObject1 = new ArrayList(2);
          }
          ((List)localObject1).add(localType);
        }
      }
      if (localObject1 == null) {
        localObject1 = NULL_LIST;
      }
      localObject2 = (List)providersCache.putIfAbsent(paramLocale, localObject1);
      if (localObject2 != null) {
        localObject1 = localObject2;
      }
    }
    return (List<LocaleProviderAdapter.Type>)localObject1;
  }
  
  static List<Locale> getLookupLocales(Locale paramLocale)
  {
    List localList = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", paramLocale);
    return localList;
  }
  
  static Locale getLookupLocale(Locale paramLocale)
  {
    Locale localLocale = paramLocale;
    if ((paramLocale.hasExtensions()) && (!paramLocale.equals(JRELocaleConstants.JA_JP_JP)) && (!paramLocale.equals(JRELocaleConstants.TH_TH_TH)))
    {
      Locale.Builder localBuilder = new Locale.Builder();
      try
      {
        localBuilder.setLocale(paramLocale);
        localBuilder.clearExtensions();
        localLocale = localBuilder.build();
      }
      catch (IllformedLocaleException localIllformedLocaleException)
      {
        config(LocaleServiceProviderPool.class, "A locale(" + paramLocale + ") has non-empty extensions, but has illformed fields.");
        localLocale = new Locale(paramLocale.getLanguage(), paramLocale.getCountry(), paramLocale.getVariant());
      }
    }
    return localLocale;
  }
  
  private static class AllAvailableLocales
  {
    static final Locale[] allAvailableLocales;
    
    private AllAvailableLocales() {}
    
    static
    {
      HashSet localHashSet = new HashSet();
      for (Class localClass : LocaleServiceProviderPool.spiClasses)
      {
        LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(localClass);
        localHashSet.addAll(localLocaleServiceProviderPool.getAvailableLocaleSet());
      }
      allAvailableLocales = (Locale[])localHashSet.toArray(new Locale[0]);
    }
  }
  
  public static abstract interface LocalizedObjectGetter<P extends LocaleServiceProvider, S>
  {
    public abstract S getObject(P paramP, Locale paramLocale, String paramString, Object... paramVarArgs);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\LocaleServiceProviderPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
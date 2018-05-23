package sun.util.locale.provider;

import java.security.AccessController;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.security.action.GetPropertyAction;
import sun.util.cldr.CLDRLocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class LocaleProviderAdapter
{
  private static final List<Type> adapterPreference;
  private static LocaleProviderAdapter jreLocaleProviderAdapter;
  private static LocaleProviderAdapter spiLocaleProviderAdapter;
  private static LocaleProviderAdapter cldrLocaleProviderAdapter;
  private static LocaleProviderAdapter hostLocaleProviderAdapter;
  private static LocaleProviderAdapter fallbackLocaleProviderAdapter;
  static Type defaultLocaleProviderAdapter;
  private static ConcurrentMap<Class<? extends LocaleServiceProvider>, ConcurrentMap<Locale, LocaleProviderAdapter>> adapterCache;
  
  public LocaleProviderAdapter() {}
  
  public static LocaleProviderAdapter forType(Type paramType)
  {
    switch (paramType)
    {
    case JRE: 
      return jreLocaleProviderAdapter;
    case CLDR: 
      return cldrLocaleProviderAdapter;
    case SPI: 
      return spiLocaleProviderAdapter;
    case HOST: 
      return hostLocaleProviderAdapter;
    case FALLBACK: 
      return fallbackLocaleProviderAdapter;
    }
    throw new InternalError("unknown locale data adapter type");
  }
  
  public static LocaleProviderAdapter forJRE()
  {
    return jreLocaleProviderAdapter;
  }
  
  public static LocaleProviderAdapter getResourceBundleBased()
  {
    Iterator localIterator = getAdapterPreference().iterator();
    while (localIterator.hasNext())
    {
      Type localType = (Type)localIterator.next();
      if ((localType == Type.JRE) || (localType == Type.CLDR) || (localType == Type.FALLBACK)) {
        return forType(localType);
      }
    }
    throw new InternalError();
  }
  
  public static List<Type> getAdapterPreference()
  {
    return adapterPreference;
  }
  
  public static LocaleProviderAdapter getAdapter(Class<? extends LocaleServiceProvider> paramClass, Locale paramLocale)
  {
    Object localObject = (ConcurrentMap)adapterCache.get(paramClass);
    if (localObject != null)
    {
      if ((localLocaleProviderAdapter = (LocaleProviderAdapter)((ConcurrentMap)localObject).get(paramLocale)) != null) {
        return localLocaleProviderAdapter;
      }
    }
    else
    {
      localObject = new ConcurrentHashMap();
      adapterCache.putIfAbsent(paramClass, localObject);
    }
    LocaleProviderAdapter localLocaleProviderAdapter = findAdapter(paramClass, paramLocale);
    if (localLocaleProviderAdapter != null)
    {
      ((ConcurrentMap)localObject).putIfAbsent(paramLocale, localLocaleProviderAdapter);
      return localLocaleProviderAdapter;
    }
    List localList = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", paramLocale);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Locale localLocale = (Locale)localIterator.next();
      if (!localLocale.equals(paramLocale))
      {
        localLocaleProviderAdapter = findAdapter(paramClass, localLocale);
        if (localLocaleProviderAdapter != null)
        {
          ((ConcurrentMap)localObject).putIfAbsent(paramLocale, localLocaleProviderAdapter);
          return localLocaleProviderAdapter;
        }
      }
    }
    ((ConcurrentMap)localObject).putIfAbsent(paramLocale, fallbackLocaleProviderAdapter);
    return fallbackLocaleProviderAdapter;
  }
  
  private static LocaleProviderAdapter findAdapter(Class<? extends LocaleServiceProvider> paramClass, Locale paramLocale)
  {
    Iterator localIterator = getAdapterPreference().iterator();
    while (localIterator.hasNext())
    {
      Type localType = (Type)localIterator.next();
      LocaleProviderAdapter localLocaleProviderAdapter = forType(localType);
      LocaleServiceProvider localLocaleServiceProvider = localLocaleProviderAdapter.getLocaleServiceProvider(paramClass);
      if ((localLocaleServiceProvider != null) && (localLocaleServiceProvider.isSupportedLocale(paramLocale))) {
        return localLocaleProviderAdapter;
      }
    }
    return null;
  }
  
  public static boolean isSupportedLocale(Locale paramLocale, Type paramType, Set<String> paramSet)
  {
    assert ((paramType == Type.JRE) || (paramType == Type.CLDR) || (paramType == Type.FALLBACK));
    if (Locale.ROOT.equals(paramLocale)) {
      return true;
    }
    if (paramType == Type.FALLBACK) {
      return false;
    }
    paramLocale = paramLocale.stripExtensions();
    if (paramSet.contains(paramLocale.toLanguageTag())) {
      return true;
    }
    if (paramType == Type.JRE)
    {
      String str = paramLocale.toString().replace('_', '-');
      return (paramSet.contains(str)) || ("ja-JP-JP".equals(str)) || ("th-TH-TH".equals(str)) || ("no-NO-NY".equals(str));
    }
    return false;
  }
  
  public static Locale[] toLocaleArray(Set<String> paramSet)
  {
    Locale[] arrayOfLocale = new Locale[paramSet.size() + 1];
    int i = 0;
    arrayOfLocale[(i++)] = Locale.ROOT;
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      switch (str1)
      {
      case "ja-JP-JP": 
        arrayOfLocale[(i++)] = JRELocaleConstants.JA_JP_JP;
        break;
      case "th-TH-TH": 
        arrayOfLocale[(i++)] = JRELocaleConstants.TH_TH_TH;
        break;
      default: 
        arrayOfLocale[(i++)] = Locale.forLanguageTag(str1);
      }
    }
    return arrayOfLocale;
  }
  
  public abstract Type getAdapterType();
  
  public abstract <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass);
  
  public abstract BreakIteratorProvider getBreakIteratorProvider();
  
  public abstract CollatorProvider getCollatorProvider();
  
  public abstract DateFormatProvider getDateFormatProvider();
  
  public abstract DateFormatSymbolsProvider getDateFormatSymbolsProvider();
  
  public abstract DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider();
  
  public abstract NumberFormatProvider getNumberFormatProvider();
  
  public abstract CurrencyNameProvider getCurrencyNameProvider();
  
  public abstract LocaleNameProvider getLocaleNameProvider();
  
  public abstract TimeZoneNameProvider getTimeZoneNameProvider();
  
  public abstract CalendarDataProvider getCalendarDataProvider();
  
  public abstract CalendarNameProvider getCalendarNameProvider();
  
  public abstract CalendarProvider getCalendarProvider();
  
  public abstract LocaleResources getLocaleResources(Locale paramLocale);
  
  public abstract Locale[] getAvailableLocales();
  
  static
  {
    jreLocaleProviderAdapter = new JRELocaleProviderAdapter();
    spiLocaleProviderAdapter = new SPILocaleProviderAdapter();
    cldrLocaleProviderAdapter = null;
    hostLocaleProviderAdapter = null;
    fallbackLocaleProviderAdapter = null;
    defaultLocaleProviderAdapter = null;
    adapterCache = new ConcurrentHashMap();
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("java.locale.providers"));
    ArrayList localArrayList = new ArrayList();
    if ((str1 != null) && (str1.length() != 0))
    {
      String[] arrayOfString1 = str1.split(",");
      for (String str2 : arrayOfString1) {
        try
        {
          Type localType = Type.valueOf(str2.trim().toUpperCase(Locale.ROOT));
          switch (localType)
          {
          case CLDR: 
            if (cldrLocaleProviderAdapter == null) {
              cldrLocaleProviderAdapter = new CLDRLocaleProviderAdapter();
            }
            break;
          case HOST: 
            if (hostLocaleProviderAdapter == null) {
              hostLocaleProviderAdapter = new HostLocaleProviderAdapter();
            }
            break;
          }
          if (!localArrayList.contains(localType)) {
            localArrayList.add(localType);
          }
        }
        catch (IllegalArgumentException|UnsupportedOperationException localIllegalArgumentException)
        {
          LocaleServiceProviderPool.config(LocaleProviderAdapter.class, localIllegalArgumentException.toString());
        }
      }
    }
    if (!localArrayList.isEmpty())
    {
      if (!localArrayList.contains(Type.JRE))
      {
        fallbackLocaleProviderAdapter = new FallbackLocaleProviderAdapter();
        localArrayList.add(Type.FALLBACK);
        defaultLocaleProviderAdapter = Type.FALLBACK;
      }
      else
      {
        defaultLocaleProviderAdapter = Type.JRE;
      }
    }
    else
    {
      localArrayList.add(Type.JRE);
      localArrayList.add(Type.SPI);
      defaultLocaleProviderAdapter = Type.JRE;
    }
    adapterPreference = Collections.unmodifiableList(localArrayList);
  }
  
  public static enum Type
  {
    JRE("sun.util.resources", "sun.text.resources"),  CLDR("sun.util.resources.cldr", "sun.text.resources.cldr"),  SPI,  HOST,  FALLBACK("sun.util.resources", "sun.text.resources");
    
    private final String UTIL_RESOURCES_PACKAGE;
    private final String TEXT_RESOURCES_PACKAGE;
    
    private Type()
    {
      this(null, null);
    }
    
    private Type(String paramString1, String paramString2)
    {
      UTIL_RESOURCES_PACKAGE = paramString1;
      TEXT_RESOURCES_PACKAGE = paramString2;
    }
    
    public String getUtilResourcesPackage()
    {
      return UTIL_RESOURCES_PACKAGE;
    }
    
    public String getTextResourcesPackage()
    {
      return TEXT_RESOURCES_PACKAGE;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\LocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
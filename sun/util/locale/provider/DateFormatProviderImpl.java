package sun.util.locale.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public class DateFormatProviderImpl
  extends DateFormatProvider
  implements AvailableLanguageTags
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public DateFormatProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    type = paramType;
    langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.toLocaleArray(langtags);
  }
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    return LocaleProviderAdapter.isSupportedLocale(paramLocale, type, langtags);
  }
  
  public DateFormat getTimeInstance(int paramInt, Locale paramLocale)
  {
    return getInstance(-1, paramInt, paramLocale);
  }
  
  public DateFormat getDateInstance(int paramInt, Locale paramLocale)
  {
    return getInstance(paramInt, -1, paramLocale);
  }
  
  public DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale)
  {
    return getInstance(paramInt1, paramInt2, paramLocale);
  }
  
  private DateFormat getInstance(int paramInt1, int paramInt2, Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("", paramLocale);
    Calendar localCalendar = localSimpleDateFormat.getCalendar();
    try
    {
      String str = LocaleProviderAdapter.forType(type).getLocaleResources(paramLocale).getDateTimePattern(paramInt2, paramInt1, localCalendar);
      localSimpleDateFormat.applyPattern(str);
    }
    catch (MissingResourceException localMissingResourceException)
    {
      localSimpleDateFormat.applyPattern("M/d/yy h:mm a");
    }
    return localSimpleDateFormat;
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\DateFormatProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.util.locale.provider;

import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import sun.util.spi.CalendarProvider;

public class CalendarProviderImpl
  extends CalendarProvider
  implements AvailableLanguageTags
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public CalendarProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
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
    return true;
  }
  
  public Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale)
  {
    return new Calendar.Builder().setLocale(paramLocale).setTimeZone(paramTimeZone).setInstant(System.currentTimeMillis()).build();
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\CalendarProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
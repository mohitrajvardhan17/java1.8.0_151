package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CalendarDataProvider;

public class CalendarDataProviderImpl
  extends CalendarDataProvider
  implements AvailableLanguageTags
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public CalendarDataProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    type = paramType;
    langtags = paramSet;
  }
  
  public int getFirstDayOfWeek(Locale paramLocale)
  {
    return LocaleProviderAdapter.forType(type).getLocaleResources(paramLocale).getCalendarData("firstDayOfWeek");
  }
  
  public int getMinimalDaysInFirstWeek(Locale paramLocale)
  {
    return LocaleProviderAdapter.forType(type).getLocaleResources(paramLocale).getCalendarData("minimalDaysInFirstWeek");
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.toLocaleArray(langtags);
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\CalendarDataProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
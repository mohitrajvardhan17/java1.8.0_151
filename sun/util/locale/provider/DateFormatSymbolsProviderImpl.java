package sun.util.locale.provider;

import java.text.DateFormatSymbols;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Locale;
import java.util.Set;

public class DateFormatSymbolsProviderImpl
  extends DateFormatSymbolsProvider
  implements AvailableLanguageTags
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public DateFormatSymbolsProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
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
  
  public DateFormatSymbols getInstance(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    return new DateFormatSymbols(paramLocale);
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\DateFormatSymbolsProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
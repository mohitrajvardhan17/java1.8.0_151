package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CurrencyNameProvider;

public class CurrencyNameProviderImpl
  extends CurrencyNameProvider
  implements AvailableLanguageTags
{
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public CurrencyNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    type = paramType;
    langtags = paramSet;
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.toLocaleArray(langtags);
  }
  
  public String getSymbol(String paramString, Locale paramLocale)
  {
    return getString(paramString.toUpperCase(Locale.ROOT), paramLocale);
  }
  
  public String getDisplayName(String paramString, Locale paramLocale)
  {
    return getString(paramString.toLowerCase(Locale.ROOT), paramLocale);
  }
  
  private String getString(String paramString, Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    return LocaleProviderAdapter.forType(type).getLocaleResources(paramLocale).getCurrencyName(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\CurrencyNameProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
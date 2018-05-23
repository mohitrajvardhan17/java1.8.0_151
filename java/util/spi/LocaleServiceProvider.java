package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider
{
  protected LocaleServiceProvider() {}
  
  public abstract Locale[] getAvailableLocales();
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    paramLocale = paramLocale.stripExtensions();
    for (Locale localLocale : getAvailableLocales()) {
      if (paramLocale.equals(localLocale.stripExtensions())) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\spi\LocaleServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
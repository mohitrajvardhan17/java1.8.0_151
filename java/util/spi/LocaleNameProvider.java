package java.util.spi;

import java.util.Locale;

public abstract class LocaleNameProvider
  extends LocaleServiceProvider
{
  protected LocaleNameProvider() {}
  
  public abstract String getDisplayLanguage(String paramString, Locale paramLocale);
  
  public String getDisplayScript(String paramString, Locale paramLocale)
  {
    return null;
  }
  
  public abstract String getDisplayCountry(String paramString, Locale paramLocale);
  
  public abstract String getDisplayVariant(String paramString, Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\spi\LocaleNameProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
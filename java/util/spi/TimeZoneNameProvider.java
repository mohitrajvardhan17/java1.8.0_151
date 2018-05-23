package java.util.spi;

import java.util.Locale;

public abstract class TimeZoneNameProvider
  extends LocaleServiceProvider
{
  protected TimeZoneNameProvider() {}
  
  public abstract String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale);
  
  public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\spi\TimeZoneNameProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
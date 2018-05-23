package java.util.spi;

import java.util.Locale;
import java.util.Map;

public abstract class CalendarNameProvider
  extends LocaleServiceProvider
{
  protected CalendarNameProvider() {}
  
  public abstract String getDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale);
  
  public abstract Map<String, Integer> getDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\spi\CalendarNameProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
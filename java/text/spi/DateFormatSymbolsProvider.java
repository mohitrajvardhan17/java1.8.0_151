package java.text.spi;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatSymbolsProvider
  extends LocaleServiceProvider
{
  protected DateFormatSymbolsProvider() {}
  
  public abstract DateFormatSymbols getInstance(Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\spi\DateFormatSymbolsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
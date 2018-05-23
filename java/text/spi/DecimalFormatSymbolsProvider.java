package java.text.spi;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DecimalFormatSymbolsProvider
  extends LocaleServiceProvider
{
  protected DecimalFormatSymbolsProvider() {}
  
  public abstract DecimalFormatSymbols getInstance(Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\spi\DecimalFormatSymbolsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
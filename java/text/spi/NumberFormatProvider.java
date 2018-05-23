package java.text.spi;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class NumberFormatProvider
  extends LocaleServiceProvider
{
  protected NumberFormatProvider() {}
  
  public abstract NumberFormat getCurrencyInstance(Locale paramLocale);
  
  public abstract NumberFormat getIntegerInstance(Locale paramLocale);
  
  public abstract NumberFormat getNumberInstance(Locale paramLocale);
  
  public abstract NumberFormat getPercentInstance(Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\spi\NumberFormatProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
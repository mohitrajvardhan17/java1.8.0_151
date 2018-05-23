package java.text.spi;

import java.text.DateFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatProvider
  extends LocaleServiceProvider
{
  protected DateFormatProvider() {}
  
  public abstract DateFormat getTimeInstance(int paramInt, Locale paramLocale);
  
  public abstract DateFormat getDateInstance(int paramInt, Locale paramLocale);
  
  public abstract DateFormat getDateTimeInstance(int paramInt1, int paramInt2, Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\spi\DateFormatProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
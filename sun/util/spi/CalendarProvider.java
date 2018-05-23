package sun.util.spi;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.spi.LocaleServiceProvider;

public abstract class CalendarProvider
  extends LocaleServiceProvider
{
  protected CalendarProvider() {}
  
  public abstract Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\spi\CalendarProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
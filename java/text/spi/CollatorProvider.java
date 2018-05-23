package java.text.spi;

import java.text.Collator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class CollatorProvider
  extends LocaleServiceProvider
{
  protected CollatorProvider() {}
  
  public abstract Collator getInstance(Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\spi\CollatorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.util.locale.provider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public class NumberFormatProviderImpl
  extends NumberFormatProvider
  implements AvailableLanguageTags
{
  private static final int NUMBERSTYLE = 0;
  private static final int CURRENCYSTYLE = 1;
  private static final int PERCENTSTYLE = 2;
  private static final int SCIENTIFICSTYLE = 3;
  private static final int INTEGERSTYLE = 4;
  private final LocaleProviderAdapter.Type type;
  private final Set<String> langtags;
  
  public NumberFormatProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet)
  {
    type = paramType;
    langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales()
  {
    return LocaleProviderAdapter.forType(type).getAvailableLocales();
  }
  
  public boolean isSupportedLocale(Locale paramLocale)
  {
    return LocaleProviderAdapter.isSupportedLocale(paramLocale, type, langtags);
  }
  
  public NumberFormat getCurrencyInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 1);
  }
  
  public NumberFormat getIntegerInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 4);
  }
  
  public NumberFormat getNumberInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 0);
  }
  
  public NumberFormat getPercentInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 2);
  }
  
  private NumberFormat getInstance(Locale paramLocale, int paramInt)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.forType(type);
    String[] arrayOfString = localLocaleProviderAdapter.getLocaleResources(paramLocale).getNumberPatterns();
    DecimalFormatSymbols localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
    int i = paramInt == 4 ? 0 : paramInt;
    DecimalFormat localDecimalFormat = new DecimalFormat(arrayOfString[i], localDecimalFormatSymbols);
    if (paramInt == 4)
    {
      localDecimalFormat.setMaximumFractionDigits(0);
      localDecimalFormat.setDecimalSeparatorAlwaysShown(false);
      localDecimalFormat.setParseIntegerOnly(true);
    }
    else if (paramInt == 1)
    {
      adjustForCurrencyDefaultFractionDigits(localDecimalFormat, localDecimalFormatSymbols);
    }
    return localDecimalFormat;
  }
  
  private static void adjustForCurrencyDefaultFractionDigits(DecimalFormat paramDecimalFormat, DecimalFormatSymbols paramDecimalFormatSymbols)
  {
    Currency localCurrency = paramDecimalFormatSymbols.getCurrency();
    if (localCurrency == null) {
      try
      {
        localCurrency = Currency.getInstance(paramDecimalFormatSymbols.getInternationalCurrencySymbol());
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    if (localCurrency != null)
    {
      int i = localCurrency.getDefaultFractionDigits();
      if (i != -1)
      {
        int j = paramDecimalFormat.getMinimumFractionDigits();
        if (j == paramDecimalFormat.getMaximumFractionDigits())
        {
          paramDecimalFormat.setMinimumFractionDigits(i);
          paramDecimalFormat.setMaximumFractionDigits(i);
        }
        else
        {
          paramDecimalFormat.setMinimumFractionDigits(Math.min(i, j));
          paramDecimalFormat.setMaximumFractionDigits(i);
        }
      }
    }
  }
  
  public Set<String> getAvailableLanguageTags()
  {
    return langtags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\NumberFormatProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
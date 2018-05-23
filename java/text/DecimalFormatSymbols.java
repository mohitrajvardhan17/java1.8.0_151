package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.util.Currency;
import java.util.Locale;
import java.util.Locale.Category;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;

public class DecimalFormatSymbols
  implements Cloneable, Serializable
{
  private char zeroDigit;
  private char groupingSeparator;
  private char decimalSeparator;
  private char perMill;
  private char percent;
  private char digit;
  private char patternSeparator;
  private String infinity;
  private String NaN;
  private char minusSign;
  private String currencySymbol;
  private String intlCurrencySymbol;
  private char monetarySeparator;
  private char exponential;
  private String exponentialSeparator;
  private Locale locale;
  private transient Currency currency;
  static final long serialVersionUID = 5772796243397350300L;
  private static final int currentSerialVersion = 3;
  private int serialVersionOnStream = 3;
  
  public DecimalFormatSymbols()
  {
    initialize(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public DecimalFormatSymbols(Locale paramLocale)
  {
    initialize(paramLocale);
  }
  
  public static Locale[] getAvailableLocales()
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(DecimalFormatSymbolsProvider.class);
    return localLocaleServiceProviderPool.getAvailableLocales();
  }
  
  public static final DecimalFormatSymbols getInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static final DecimalFormatSymbols getInstance(Locale paramLocale)
  {
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, paramLocale);
    DecimalFormatSymbolsProvider localDecimalFormatSymbolsProvider = localLocaleProviderAdapter.getDecimalFormatSymbolsProvider();
    DecimalFormatSymbols localDecimalFormatSymbols = localDecimalFormatSymbolsProvider.getInstance(paramLocale);
    if (localDecimalFormatSymbols == null)
    {
      localDecimalFormatSymbolsProvider = LocaleProviderAdapter.forJRE().getDecimalFormatSymbolsProvider();
      localDecimalFormatSymbols = localDecimalFormatSymbolsProvider.getInstance(paramLocale);
    }
    return localDecimalFormatSymbols;
  }
  
  public char getZeroDigit()
  {
    return zeroDigit;
  }
  
  public void setZeroDigit(char paramChar)
  {
    zeroDigit = paramChar;
  }
  
  public char getGroupingSeparator()
  {
    return groupingSeparator;
  }
  
  public void setGroupingSeparator(char paramChar)
  {
    groupingSeparator = paramChar;
  }
  
  public char getDecimalSeparator()
  {
    return decimalSeparator;
  }
  
  public void setDecimalSeparator(char paramChar)
  {
    decimalSeparator = paramChar;
  }
  
  public char getPerMill()
  {
    return perMill;
  }
  
  public void setPerMill(char paramChar)
  {
    perMill = paramChar;
  }
  
  public char getPercent()
  {
    return percent;
  }
  
  public void setPercent(char paramChar)
  {
    percent = paramChar;
  }
  
  public char getDigit()
  {
    return digit;
  }
  
  public void setDigit(char paramChar)
  {
    digit = paramChar;
  }
  
  public char getPatternSeparator()
  {
    return patternSeparator;
  }
  
  public void setPatternSeparator(char paramChar)
  {
    patternSeparator = paramChar;
  }
  
  public String getInfinity()
  {
    return infinity;
  }
  
  public void setInfinity(String paramString)
  {
    infinity = paramString;
  }
  
  public String getNaN()
  {
    return NaN;
  }
  
  public void setNaN(String paramString)
  {
    NaN = paramString;
  }
  
  public char getMinusSign()
  {
    return minusSign;
  }
  
  public void setMinusSign(char paramChar)
  {
    minusSign = paramChar;
  }
  
  public String getCurrencySymbol()
  {
    return currencySymbol;
  }
  
  public void setCurrencySymbol(String paramString)
  {
    currencySymbol = paramString;
  }
  
  public String getInternationalCurrencySymbol()
  {
    return intlCurrencySymbol;
  }
  
  public void setInternationalCurrencySymbol(String paramString)
  {
    intlCurrencySymbol = paramString;
    currency = null;
    if (paramString != null) {
      try
      {
        currency = Currency.getInstance(paramString);
        currencySymbol = currency.getSymbol();
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
  }
  
  public Currency getCurrency()
  {
    return currency;
  }
  
  public void setCurrency(Currency paramCurrency)
  {
    if (paramCurrency == null) {
      throw new NullPointerException();
    }
    currency = paramCurrency;
    intlCurrencySymbol = paramCurrency.getCurrencyCode();
    currencySymbol = paramCurrency.getSymbol(locale);
  }
  
  public char getMonetaryDecimalSeparator()
  {
    return monetarySeparator;
  }
  
  public void setMonetaryDecimalSeparator(char paramChar)
  {
    monetarySeparator = paramChar;
  }
  
  char getExponentialSymbol()
  {
    return exponential;
  }
  
  public String getExponentSeparator()
  {
    return exponentialSeparator;
  }
  
  void setExponentialSymbol(char paramChar)
  {
    exponential = paramChar;
  }
  
  public void setExponentSeparator(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    exponentialSeparator = paramString;
  }
  
  public Object clone()
  {
    try
    {
      return (DecimalFormatSymbols)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    DecimalFormatSymbols localDecimalFormatSymbols = (DecimalFormatSymbols)paramObject;
    return (zeroDigit == zeroDigit) && (groupingSeparator == groupingSeparator) && (decimalSeparator == decimalSeparator) && (percent == percent) && (perMill == perMill) && (digit == digit) && (minusSign == minusSign) && (patternSeparator == patternSeparator) && (infinity.equals(infinity)) && (NaN.equals(NaN)) && (currencySymbol.equals(currencySymbol)) && (intlCurrencySymbol.equals(intlCurrencySymbol)) && (currency == currency) && (monetarySeparator == monetarySeparator) && (exponentialSeparator.equals(exponentialSeparator)) && (locale.equals(locale));
  }
  
  public int hashCode()
  {
    int i = zeroDigit;
    i = i * 37 + groupingSeparator;
    i = i * 37 + decimalSeparator;
    return i;
  }
  
  private void initialize(Locale paramLocale)
  {
    locale = paramLocale;
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, paramLocale);
    if (!(localLocaleProviderAdapter instanceof ResourceBundleBasedAdapter)) {
      localLocaleProviderAdapter = LocaleProviderAdapter.getResourceBundleBased();
    }
    Object[] arrayOfObject = localLocaleProviderAdapter.getLocaleResources(paramLocale).getDecimalFormatSymbolsData();
    String[] arrayOfString = (String[])arrayOfObject[0];
    decimalSeparator = arrayOfString[0].charAt(0);
    groupingSeparator = arrayOfString[1].charAt(0);
    patternSeparator = arrayOfString[2].charAt(0);
    percent = arrayOfString[3].charAt(0);
    zeroDigit = arrayOfString[4].charAt(0);
    digit = arrayOfString[5].charAt(0);
    minusSign = arrayOfString[6].charAt(0);
    exponential = arrayOfString[7].charAt(0);
    exponentialSeparator = arrayOfString[7];
    perMill = arrayOfString[8].charAt(0);
    infinity = arrayOfString[9];
    NaN = arrayOfString[10];
    if (paramLocale.getCountry().length() > 0) {
      try
      {
        currency = Currency.getInstance(paramLocale);
      }
      catch (IllegalArgumentException localIllegalArgumentException1) {}
    }
    if (currency != null)
    {
      intlCurrencySymbol = currency.getCurrencyCode();
      if ((arrayOfObject[1] != null) && (arrayOfObject[1] == intlCurrencySymbol))
      {
        currencySymbol = ((String)arrayOfObject[2]);
      }
      else
      {
        currencySymbol = currency.getSymbol(paramLocale);
        arrayOfObject[1] = intlCurrencySymbol;
        arrayOfObject[2] = currencySymbol;
      }
    }
    else
    {
      intlCurrencySymbol = "XXX";
      try
      {
        currency = Currency.getInstance(intlCurrencySymbol);
      }
      catch (IllegalArgumentException localIllegalArgumentException2) {}
      currencySymbol = "¤";
    }
    monetarySeparator = decimalSeparator;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (serialVersionOnStream < 1)
    {
      monetarySeparator = decimalSeparator;
      exponential = 'E';
    }
    if (serialVersionOnStream < 2) {
      locale = Locale.ROOT;
    }
    if (serialVersionOnStream < 3) {
      exponentialSeparator = Character.toString(exponential);
    }
    serialVersionOnStream = 3;
    if (intlCurrencySymbol != null) {
      try
      {
        currency = Currency.getInstance(intlCurrencySymbol);
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\DecimalFormatSymbols.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
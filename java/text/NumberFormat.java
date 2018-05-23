package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class NumberFormat
  extends Format
{
  public static final int INTEGER_FIELD = 0;
  public static final int FRACTION_FIELD = 1;
  private static final int NUMBERSTYLE = 0;
  private static final int CURRENCYSTYLE = 1;
  private static final int PERCENTSTYLE = 2;
  private static final int SCIENTIFICSTYLE = 3;
  private static final int INTEGERSTYLE = 4;
  private boolean groupingUsed = true;
  private byte maxIntegerDigits = 40;
  private byte minIntegerDigits = 1;
  private byte maxFractionDigits = 3;
  private byte minFractionDigits = 0;
  private boolean parseIntegerOnly = false;
  private int maximumIntegerDigits = 40;
  private int minimumIntegerDigits = 1;
  private int maximumFractionDigits = 3;
  private int minimumFractionDigits = 0;
  static final int currentSerialVersion = 1;
  private int serialVersionOnStream = 1;
  static final long serialVersionUID = -2308460125733713944L;
  
  protected NumberFormat() {}
  
  public StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    if (((paramObject instanceof Long)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Short)) || ((paramObject instanceof Byte)) || ((paramObject instanceof AtomicInteger)) || ((paramObject instanceof AtomicLong)) || (((paramObject instanceof BigInteger)) && (((BigInteger)paramObject).bitLength() < 64))) {
      return format(((Number)paramObject).longValue(), paramStringBuffer, paramFieldPosition);
    }
    if ((paramObject instanceof Number)) {
      return format(((Number)paramObject).doubleValue(), paramStringBuffer, paramFieldPosition);
    }
    throw new IllegalArgumentException("Cannot format given Object as a Number");
  }
  
  public final Object parseObject(String paramString, ParsePosition paramParsePosition)
  {
    return parse(paramString, paramParsePosition);
  }
  
  public final String format(double paramDouble)
  {
    String str = fastFormat(paramDouble);
    if (str != null) {
      return str;
    }
    return format(paramDouble, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString();
  }
  
  String fastFormat(double paramDouble)
  {
    return null;
  }
  
  public final String format(long paramLong)
  {
    return format(paramLong, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString();
  }
  
  public abstract StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public abstract StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public abstract Number parse(String paramString, ParsePosition paramParsePosition);
  
  public Number parse(String paramString)
    throws ParseException
  {
    ParsePosition localParsePosition = new ParsePosition(0);
    Number localNumber = parse(paramString, localParsePosition);
    if (index == 0) {
      throw new ParseException("Unparseable number: \"" + paramString + "\"", errorIndex);
    }
    return localNumber;
  }
  
  public boolean isParseIntegerOnly()
  {
    return parseIntegerOnly;
  }
  
  public void setParseIntegerOnly(boolean paramBoolean)
  {
    parseIntegerOnly = paramBoolean;
  }
  
  public static final NumberFormat getInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT), 0);
  }
  
  public static NumberFormat getInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 0);
  }
  
  public static final NumberFormat getNumberInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT), 0);
  }
  
  public static NumberFormat getNumberInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 0);
  }
  
  public static final NumberFormat getIntegerInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT), 4);
  }
  
  public static NumberFormat getIntegerInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 4);
  }
  
  public static final NumberFormat getCurrencyInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT), 1);
  }
  
  public static NumberFormat getCurrencyInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 1);
  }
  
  public static final NumberFormat getPercentInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT), 2);
  }
  
  public static NumberFormat getPercentInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 2);
  }
  
  static final NumberFormat getScientificInstance()
  {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT), 3);
  }
  
  static NumberFormat getScientificInstance(Locale paramLocale)
  {
    return getInstance(paramLocale, 3);
  }
  
  public static Locale[] getAvailableLocales()
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(NumberFormatProvider.class);
    return localLocaleServiceProviderPool.getAvailableLocales();
  }
  
  public int hashCode()
  {
    return maximumIntegerDigits * 37 + maxFractionDigits;
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
    NumberFormat localNumberFormat = (NumberFormat)paramObject;
    return (maximumIntegerDigits == maximumIntegerDigits) && (minimumIntegerDigits == minimumIntegerDigits) && (maximumFractionDigits == maximumFractionDigits) && (minimumFractionDigits == minimumFractionDigits) && (groupingUsed == groupingUsed) && (parseIntegerOnly == parseIntegerOnly);
  }
  
  public Object clone()
  {
    NumberFormat localNumberFormat = (NumberFormat)super.clone();
    return localNumberFormat;
  }
  
  public boolean isGroupingUsed()
  {
    return groupingUsed;
  }
  
  public void setGroupingUsed(boolean paramBoolean)
  {
    groupingUsed = paramBoolean;
  }
  
  public int getMaximumIntegerDigits()
  {
    return maximumIntegerDigits;
  }
  
  public void setMaximumIntegerDigits(int paramInt)
  {
    maximumIntegerDigits = Math.max(0, paramInt);
    if (minimumIntegerDigits > maximumIntegerDigits) {
      minimumIntegerDigits = maximumIntegerDigits;
    }
  }
  
  public int getMinimumIntegerDigits()
  {
    return minimumIntegerDigits;
  }
  
  public void setMinimumIntegerDigits(int paramInt)
  {
    minimumIntegerDigits = Math.max(0, paramInt);
    if (minimumIntegerDigits > maximumIntegerDigits) {
      maximumIntegerDigits = minimumIntegerDigits;
    }
  }
  
  public int getMaximumFractionDigits()
  {
    return maximumFractionDigits;
  }
  
  public void setMaximumFractionDigits(int paramInt)
  {
    maximumFractionDigits = Math.max(0, paramInt);
    if (maximumFractionDigits < minimumFractionDigits) {
      minimumFractionDigits = maximumFractionDigits;
    }
  }
  
  public int getMinimumFractionDigits()
  {
    return minimumFractionDigits;
  }
  
  public void setMinimumFractionDigits(int paramInt)
  {
    minimumFractionDigits = Math.max(0, paramInt);
    if (maximumFractionDigits < minimumFractionDigits) {
      maximumFractionDigits = minimumFractionDigits;
    }
  }
  
  public Currency getCurrency()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setCurrency(Currency paramCurrency)
  {
    throw new UnsupportedOperationException();
  }
  
  public RoundingMode getRoundingMode()
  {
    throw new UnsupportedOperationException();
  }
  
  public void setRoundingMode(RoundingMode paramRoundingMode)
  {
    throw new UnsupportedOperationException();
  }
  
  private static NumberFormat getInstance(Locale paramLocale, int paramInt)
  {
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, paramLocale);
    NumberFormat localNumberFormat = getInstance(localLocaleProviderAdapter, paramLocale, paramInt);
    if (localNumberFormat == null) {
      localNumberFormat = getInstance(LocaleProviderAdapter.forJRE(), paramLocale, paramInt);
    }
    return localNumberFormat;
  }
  
  private static NumberFormat getInstance(LocaleProviderAdapter paramLocaleProviderAdapter, Locale paramLocale, int paramInt)
  {
    NumberFormatProvider localNumberFormatProvider = paramLocaleProviderAdapter.getNumberFormatProvider();
    NumberFormat localNumberFormat = null;
    switch (paramInt)
    {
    case 0: 
      localNumberFormat = localNumberFormatProvider.getNumberInstance(paramLocale);
      break;
    case 2: 
      localNumberFormat = localNumberFormatProvider.getPercentInstance(paramLocale);
      break;
    case 1: 
      localNumberFormat = localNumberFormatProvider.getCurrencyInstance(paramLocale);
      break;
    case 4: 
      localNumberFormat = localNumberFormatProvider.getIntegerInstance(paramLocale);
    }
    return localNumberFormat;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (serialVersionOnStream < 1)
    {
      maximumIntegerDigits = maxIntegerDigits;
      minimumIntegerDigits = minIntegerDigits;
      maximumFractionDigits = maxFractionDigits;
      minimumFractionDigits = minFractionDigits;
    }
    if ((minimumIntegerDigits > maximumIntegerDigits) || (minimumFractionDigits > maximumFractionDigits) || (minimumIntegerDigits < 0) || (minimumFractionDigits < 0)) {
      throw new InvalidObjectException("Digit count range invalid");
    }
    serialVersionOnStream = 1;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    maxIntegerDigits = (maximumIntegerDigits > 127 ? Byte.MAX_VALUE : (byte)maximumIntegerDigits);
    minIntegerDigits = (minimumIntegerDigits > 127 ? Byte.MAX_VALUE : (byte)minimumIntegerDigits);
    maxFractionDigits = (maximumFractionDigits > 127 ? Byte.MAX_VALUE : (byte)maximumFractionDigits);
    minFractionDigits = (minimumFractionDigits > 127 ? Byte.MAX_VALUE : (byte)minimumFractionDigits);
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public static class Field
    extends Format.Field
  {
    private static final long serialVersionUID = 7494728892700160890L;
    private static final Map<String, Field> instanceMap = new HashMap(11);
    public static final Field INTEGER = new Field("integer");
    public static final Field FRACTION = new Field("fraction");
    public static final Field EXPONENT = new Field("exponent");
    public static final Field DECIMAL_SEPARATOR = new Field("decimal separator");
    public static final Field SIGN = new Field("sign");
    public static final Field GROUPING_SEPARATOR = new Field("grouping separator");
    public static final Field EXPONENT_SYMBOL = new Field("exponent symbol");
    public static final Field PERCENT = new Field("percent");
    public static final Field PERMILLE = new Field("per mille");
    public static final Field CURRENCY = new Field("currency");
    public static final Field EXPONENT_SIGN = new Field("exponent sign");
    
    protected Field(String paramString)
    {
      super();
      if (getClass() == Field.class) {
        instanceMap.put(paramString, this);
      }
    }
    
    protected Object readResolve()
      throws InvalidObjectException
    {
      if (getClass() != Field.class) {
        throw new InvalidObjectException("subclass didn't correctly implement readResolve");
      }
      Object localObject = instanceMap.get(getName());
      if (localObject != null) {
        return localObject;
      }
      throw new InvalidObjectException("unknown attribute name");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\NumberFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.time.format;

import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class DecimalStyle
{
  public static final DecimalStyle STANDARD = new DecimalStyle('0', '+', '-', '.');
  private static final ConcurrentMap<Locale, DecimalStyle> CACHE = new ConcurrentHashMap(16, 0.75F, 2);
  private final char zeroDigit;
  private final char positiveSign;
  private final char negativeSign;
  private final char decimalSeparator;
  
  public static Set<Locale> getAvailableLocales()
  {
    Locale[] arrayOfLocale = DecimalFormatSymbols.getAvailableLocales();
    HashSet localHashSet = new HashSet(arrayOfLocale.length);
    Collections.addAll(localHashSet, arrayOfLocale);
    return localHashSet;
  }
  
  public static DecimalStyle ofDefaultLocale()
  {
    return of(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static DecimalStyle of(Locale paramLocale)
  {
    Objects.requireNonNull(paramLocale, "locale");
    DecimalStyle localDecimalStyle = (DecimalStyle)CACHE.get(paramLocale);
    if (localDecimalStyle == null)
    {
      localDecimalStyle = create(paramLocale);
      CACHE.putIfAbsent(paramLocale, localDecimalStyle);
      localDecimalStyle = (DecimalStyle)CACHE.get(paramLocale);
    }
    return localDecimalStyle;
  }
  
  private static DecimalStyle create(Locale paramLocale)
  {
    DecimalFormatSymbols localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
    char c1 = localDecimalFormatSymbols.getZeroDigit();
    char c2 = '+';
    char c3 = localDecimalFormatSymbols.getMinusSign();
    char c4 = localDecimalFormatSymbols.getDecimalSeparator();
    if ((c1 == '0') && (c3 == '-') && (c4 == '.')) {
      return STANDARD;
    }
    return new DecimalStyle(c1, c2, c3, c4);
  }
  
  private DecimalStyle(char paramChar1, char paramChar2, char paramChar3, char paramChar4)
  {
    zeroDigit = paramChar1;
    positiveSign = paramChar2;
    negativeSign = paramChar3;
    decimalSeparator = paramChar4;
  }
  
  public char getZeroDigit()
  {
    return zeroDigit;
  }
  
  public DecimalStyle withZeroDigit(char paramChar)
  {
    if (paramChar == zeroDigit) {
      return this;
    }
    return new DecimalStyle(paramChar, positiveSign, negativeSign, decimalSeparator);
  }
  
  public char getPositiveSign()
  {
    return positiveSign;
  }
  
  public DecimalStyle withPositiveSign(char paramChar)
  {
    if (paramChar == positiveSign) {
      return this;
    }
    return new DecimalStyle(zeroDigit, paramChar, negativeSign, decimalSeparator);
  }
  
  public char getNegativeSign()
  {
    return negativeSign;
  }
  
  public DecimalStyle withNegativeSign(char paramChar)
  {
    if (paramChar == negativeSign) {
      return this;
    }
    return new DecimalStyle(zeroDigit, positiveSign, paramChar, decimalSeparator);
  }
  
  public char getDecimalSeparator()
  {
    return decimalSeparator;
  }
  
  public DecimalStyle withDecimalSeparator(char paramChar)
  {
    if (paramChar == decimalSeparator) {
      return this;
    }
    return new DecimalStyle(zeroDigit, positiveSign, negativeSign, paramChar);
  }
  
  int convertToDigit(char paramChar)
  {
    int i = paramChar - zeroDigit;
    return (i >= 0) && (i <= 9) ? i : -1;
  }
  
  String convertNumberToI18N(String paramString)
  {
    if (zeroDigit == '0') {
      return paramString;
    }
    int i = zeroDigit - '0';
    char[] arrayOfChar = paramString.toCharArray();
    for (int j = 0; j < arrayOfChar.length; j++) {
      arrayOfChar[j] = ((char)(arrayOfChar[j] + i));
    }
    return new String(arrayOfChar);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof DecimalStyle))
    {
      DecimalStyle localDecimalStyle = (DecimalStyle)paramObject;
      return (zeroDigit == zeroDigit) && (positiveSign == positiveSign) && (negativeSign == negativeSign) && (decimalSeparator == decimalSeparator);
    }
    return false;
  }
  
  public int hashCode()
  {
    return zeroDigit + positiveSign + negativeSign + decimalSeparator;
  }
  
  public String toString()
  {
    return "DecimalStyle[" + zeroDigit + positiveSign + negativeSign + decimalSeparator + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\format\DecimalStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
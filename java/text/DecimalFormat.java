package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;
import sun.util.locale.provider.ResourceBundleBasedAdapter;

public class DecimalFormat
  extends NumberFormat
{
  private transient BigInteger bigIntegerMultiplier;
  private transient BigDecimal bigDecimalMultiplier;
  private static final int STATUS_INFINITE = 0;
  private static final int STATUS_POSITIVE = 1;
  private static final int STATUS_LENGTH = 2;
  private transient DigitList digitList = new DigitList();
  private String positivePrefix = "";
  private String positiveSuffix = "";
  private String negativePrefix = "-";
  private String negativeSuffix = "";
  private String posPrefixPattern;
  private String posSuffixPattern;
  private String negPrefixPattern;
  private String negSuffixPattern;
  private int multiplier = 1;
  private byte groupingSize = 3;
  private boolean decimalSeparatorAlwaysShown = false;
  private boolean parseBigDecimal = false;
  private transient boolean isCurrencyFormat = false;
  private DecimalFormatSymbols symbols = null;
  private boolean useExponentialNotation;
  private transient FieldPosition[] positivePrefixFieldPositions;
  private transient FieldPosition[] positiveSuffixFieldPositions;
  private transient FieldPosition[] negativePrefixFieldPositions;
  private transient FieldPosition[] negativeSuffixFieldPositions;
  private byte minExponentDigits;
  private int maximumIntegerDigits = super.getMaximumIntegerDigits();
  private int minimumIntegerDigits = super.getMinimumIntegerDigits();
  private int maximumFractionDigits = super.getMaximumFractionDigits();
  private int minimumFractionDigits = super.getMinimumFractionDigits();
  private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
  private transient boolean isFastPath = false;
  private transient boolean fastPathCheckNeeded = true;
  private transient FastPathData fastPathData;
  static final int currentSerialVersion = 4;
  private int serialVersionOnStream = 4;
  private static final double MAX_INT_AS_DOUBLE = 2.147483647E9D;
  private static final char PATTERN_ZERO_DIGIT = '0';
  private static final char PATTERN_GROUPING_SEPARATOR = ',';
  private static final char PATTERN_DECIMAL_SEPARATOR = '.';
  private static final char PATTERN_PER_MILLE = '‰';
  private static final char PATTERN_PERCENT = '%';
  private static final char PATTERN_DIGIT = '#';
  private static final char PATTERN_SEPARATOR = ';';
  private static final String PATTERN_EXPONENT = "E";
  private static final char PATTERN_MINUS = '-';
  private static final char CURRENCY_SIGN = '¤';
  private static final char QUOTE = '\'';
  private static FieldPosition[] EmptyFieldPositionArray = new FieldPosition[0];
  static final int DOUBLE_INTEGER_DIGITS = 309;
  static final int DOUBLE_FRACTION_DIGITS = 340;
  static final int MAXIMUM_INTEGER_DIGITS = Integer.MAX_VALUE;
  static final int MAXIMUM_FRACTION_DIGITS = Integer.MAX_VALUE;
  static final long serialVersionUID = 864413376551465018L;
  
  public DecimalFormat()
  {
    Locale localLocale = Locale.getDefault(Locale.Category.FORMAT);
    LocaleProviderAdapter localLocaleProviderAdapter = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, localLocale);
    if (!(localLocaleProviderAdapter instanceof ResourceBundleBasedAdapter)) {
      localLocaleProviderAdapter = LocaleProviderAdapter.getResourceBundleBased();
    }
    String[] arrayOfString = localLocaleProviderAdapter.getLocaleResources(localLocale).getNumberPatterns();
    symbols = DecimalFormatSymbols.getInstance(localLocale);
    applyPattern(arrayOfString[0], false);
  }
  
  public DecimalFormat(String paramString)
  {
    symbols = DecimalFormatSymbols.getInstance(Locale.getDefault(Locale.Category.FORMAT));
    applyPattern(paramString, false);
  }
  
  public DecimalFormat(String paramString, DecimalFormatSymbols paramDecimalFormatSymbols)
  {
    symbols = ((DecimalFormatSymbols)paramDecimalFormatSymbols.clone());
    applyPattern(paramString, false);
  }
  
  public final StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    if (((paramObject instanceof Long)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Short)) || ((paramObject instanceof Byte)) || ((paramObject instanceof AtomicInteger)) || ((paramObject instanceof AtomicLong)) || (((paramObject instanceof BigInteger)) && (((BigInteger)paramObject).bitLength() < 64))) {
      return format(((Number)paramObject).longValue(), paramStringBuffer, paramFieldPosition);
    }
    if ((paramObject instanceof BigDecimal)) {
      return format((BigDecimal)paramObject, paramStringBuffer, paramFieldPosition);
    }
    if ((paramObject instanceof BigInteger)) {
      return format((BigInteger)paramObject, paramStringBuffer, paramFieldPosition);
    }
    if ((paramObject instanceof Number)) {
      return format(((Number)paramObject).doubleValue(), paramStringBuffer, paramFieldPosition);
    }
    throw new IllegalArgumentException("Cannot format given Object as a Number");
  }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    int i = 0;
    if (paramFieldPosition == DontCareFieldPosition.INSTANCE)
    {
      i = 1;
    }
    else
    {
      paramFieldPosition.setBeginIndex(0);
      paramFieldPosition.setEndIndex(0);
    }
    if (i != 0)
    {
      String str = fastFormat(paramDouble);
      if (str != null)
      {
        paramStringBuffer.append(str);
        return paramStringBuffer;
      }
    }
    return format(paramDouble, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate)
  {
    if ((Double.isNaN(paramDouble)) || ((Double.isInfinite(paramDouble)) && (multiplier == 0)))
    {
      i = paramStringBuffer.length();
      paramStringBuffer.append(symbols.getNaN());
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, i, paramStringBuffer.length(), paramStringBuffer);
      return paramStringBuffer;
    }
    int i = ((paramDouble < 0.0D) || ((paramDouble == 0.0D) && (1.0D / paramDouble < 0.0D)) ? 1 : 0) ^ (multiplier < 0 ? 1 : 0);
    if (multiplier != 1) {
      paramDouble *= multiplier;
    }
    if (Double.isInfinite(paramDouble))
    {
      if (i != 0) {
        append(paramStringBuffer, negativePrefix, paramFieldDelegate, getNegativePrefixFieldPositions(), NumberFormat.Field.SIGN);
      } else {
        append(paramStringBuffer, positivePrefix, paramFieldDelegate, getPositivePrefixFieldPositions(), NumberFormat.Field.SIGN);
      }
      int j = paramStringBuffer.length();
      paramStringBuffer.append(symbols.getInfinity());
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, j, paramStringBuffer.length(), paramStringBuffer);
      if (i != 0) {
        append(paramStringBuffer, negativeSuffix, paramFieldDelegate, getNegativeSuffixFieldPositions(), NumberFormat.Field.SIGN);
      } else {
        append(paramStringBuffer, positiveSuffix, paramFieldDelegate, getPositiveSuffixFieldPositions(), NumberFormat.Field.SIGN);
      }
      return paramStringBuffer;
    }
    if (i != 0) {
      paramDouble = -paramDouble;
    }
    assert ((paramDouble >= 0.0D) && (!Double.isInfinite(paramDouble)));
    synchronized (digitList)
    {
      int k = super.getMaximumIntegerDigits();
      int m = super.getMinimumIntegerDigits();
      int n = super.getMaximumFractionDigits();
      int i1 = super.getMinimumFractionDigits();
      digitList.set(i, paramDouble, useExponentialNotation ? k + n : n, !useExponentialNotation);
      return subformat(paramStringBuffer, paramFieldDelegate, i, false, k, m, n, i1);
    }
  }
  
  public StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    return format(paramLong, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(long paramLong, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate)
  {
    boolean bool = paramLong < 0L;
    if (bool) {
      paramLong = -paramLong;
    }
    int i = 0;
    if (paramLong < 0L)
    {
      if (multiplier != 0) {
        i = 1;
      }
    }
    else if ((multiplier != 1) && (multiplier != 0))
    {
      long l = Long.MAX_VALUE / multiplier;
      if (l < 0L) {
        l = -l;
      }
      i = paramLong > l ? 1 : 0;
    }
    if (i != 0)
    {
      if (bool) {
        paramLong = -paramLong;
      }
      BigInteger localBigInteger = BigInteger.valueOf(paramLong);
      return format(localBigInteger, paramStringBuffer, paramFieldDelegate, true);
    }
    paramLong *= multiplier;
    if (paramLong == 0L)
    {
      bool = false;
    }
    else if (multiplier < 0)
    {
      paramLong = -paramLong;
      bool = !bool;
    }
    synchronized (digitList)
    {
      int j = super.getMaximumIntegerDigits();
      int k = super.getMinimumIntegerDigits();
      int m = super.getMaximumFractionDigits();
      int n = super.getMinimumFractionDigits();
      digitList.set(bool, paramLong, useExponentialNotation ? j + m : 0);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, true, j, k, m, n);
    }
  }
  
  private StringBuffer format(BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    return format(paramBigDecimal, paramStringBuffer, paramFieldPosition.getFieldDelegate());
  }
  
  private StringBuffer format(BigDecimal paramBigDecimal, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate)
  {
    if (multiplier != 1) {
      paramBigDecimal = paramBigDecimal.multiply(getBigDecimalMultiplier());
    }
    boolean bool = paramBigDecimal.signum() == -1;
    if (bool) {
      paramBigDecimal = paramBigDecimal.negate();
    }
    synchronized (digitList)
    {
      int i = getMaximumIntegerDigits();
      int j = getMinimumIntegerDigits();
      int k = getMaximumFractionDigits();
      int m = getMinimumFractionDigits();
      int n = i + k;
      digitList.set(bool, paramBigDecimal, useExponentialNotation ? n : n < 0 ? Integer.MAX_VALUE : k, !useExponentialNotation);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, false, i, j, k, m);
    }
  }
  
  private StringBuffer format(BigInteger paramBigInteger, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    paramFieldPosition.setBeginIndex(0);
    paramFieldPosition.setEndIndex(0);
    return format(paramBigInteger, paramStringBuffer, paramFieldPosition.getFieldDelegate(), false);
  }
  
  private StringBuffer format(BigInteger paramBigInteger, StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate, boolean paramBoolean)
  {
    if (multiplier != 1) {
      paramBigInteger = paramBigInteger.multiply(getBigIntegerMultiplier());
    }
    boolean bool = paramBigInteger.signum() == -1;
    if (bool) {
      paramBigInteger = paramBigInteger.negate();
    }
    synchronized (digitList)
    {
      int i;
      int j;
      int k;
      int m;
      int n;
      if (paramBoolean)
      {
        i = super.getMaximumIntegerDigits();
        j = super.getMinimumIntegerDigits();
        k = super.getMaximumFractionDigits();
        m = super.getMinimumFractionDigits();
        n = i + k;
      }
      else
      {
        i = getMaximumIntegerDigits();
        j = getMinimumIntegerDigits();
        k = getMaximumFractionDigits();
        m = getMinimumFractionDigits();
        n = i + k;
        if (n < 0) {
          n = Integer.MAX_VALUE;
        }
      }
      digitList.set(bool, paramBigInteger, useExponentialNotation ? n : 0);
      return subformat(paramStringBuffer, paramFieldDelegate, bool, true, i, j, k, m);
    }
  }
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject)
  {
    CharacterIteratorFieldDelegate localCharacterIteratorFieldDelegate = new CharacterIteratorFieldDelegate();
    StringBuffer localStringBuffer = new StringBuffer();
    if (((paramObject instanceof Double)) || ((paramObject instanceof Float)))
    {
      format(((Number)paramObject).doubleValue(), localStringBuffer, localCharacterIteratorFieldDelegate);
    }
    else if (((paramObject instanceof Long)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Short)) || ((paramObject instanceof Byte)) || ((paramObject instanceof AtomicInteger)) || ((paramObject instanceof AtomicLong)))
    {
      format(((Number)paramObject).longValue(), localStringBuffer, localCharacterIteratorFieldDelegate);
    }
    else if ((paramObject instanceof BigDecimal))
    {
      format((BigDecimal)paramObject, localStringBuffer, localCharacterIteratorFieldDelegate);
    }
    else if ((paramObject instanceof BigInteger))
    {
      format((BigInteger)paramObject, localStringBuffer, localCharacterIteratorFieldDelegate, false);
    }
    else
    {
      if (paramObject == null) {
        throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
      }
      throw new IllegalArgumentException("Cannot format given Object as a Number");
    }
    return localCharacterIteratorFieldDelegate.getIterator(localStringBuffer.toString());
  }
  
  private void checkAndSetFastPathStatus()
  {
    boolean bool = isFastPath;
    if ((roundingMode == RoundingMode.HALF_EVEN) && (isGroupingUsed()) && (groupingSize == 3) && (multiplier == 1) && (!decimalSeparatorAlwaysShown) && (!useExponentialNotation))
    {
      isFastPath = ((minimumIntegerDigits == 1) && (maximumIntegerDigits >= 10));
      if (isFastPath) {
        if (isCurrencyFormat)
        {
          if ((minimumFractionDigits != 2) || (maximumFractionDigits != 2)) {
            isFastPath = false;
          }
        }
        else if ((minimumFractionDigits != 0) || (maximumFractionDigits != 3)) {
          isFastPath = false;
        }
      }
    }
    else
    {
      isFastPath = false;
    }
    if (isFastPath)
    {
      if (fastPathData == null) {
        fastPathData = new FastPathData(null);
      }
      fastPathData.zeroDelta = (symbols.getZeroDigit() - '0');
      fastPathData.groupingChar = symbols.getGroupingSeparator();
      fastPathData.fractionalMaxIntBound = (isCurrencyFormat ? 99 : 999);
      fastPathData.fractionalScaleFactor = (isCurrencyFormat ? 100.0D : 1000.0D);
      fastPathData.positiveAffixesRequired = ((positivePrefix.length() != 0) || (positiveSuffix.length() != 0));
      fastPathData.negativeAffixesRequired = ((negativePrefix.length() != 0) || (negativeSuffix.length() != 0));
      int i = 10;
      int j = 3;
      int k = Math.max(positivePrefix.length(), negativePrefix.length()) + i + j + 1 + maximumFractionDigits + Math.max(positiveSuffix.length(), negativeSuffix.length());
      fastPathData.fastPathContainer = new char[k];
      fastPathData.charsPositiveSuffix = positiveSuffix.toCharArray();
      fastPathData.charsNegativeSuffix = negativeSuffix.toCharArray();
      fastPathData.charsPositivePrefix = positivePrefix.toCharArray();
      fastPathData.charsNegativePrefix = negativePrefix.toCharArray();
      int m = Math.max(positivePrefix.length(), negativePrefix.length());
      int n = i + j + m;
      fastPathData.integralLastIndex = (n - 1);
      fastPathData.fractionalFirstIndex = (n + 1);
      fastPathData.fastPathContainer[n] = (isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator());
    }
    else if (bool)
    {
      fastPathData.fastPathContainer = null;
      fastPathData.charsPositiveSuffix = null;
      fastPathData.charsNegativeSuffix = null;
      fastPathData.charsPositivePrefix = null;
      fastPathData.charsNegativePrefix = null;
    }
    fastPathCheckNeeded = false;
  }
  
  private boolean exactRoundUp(double paramDouble, int paramInt)
  {
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    double d1;
    double d2;
    double d3;
    if (isCurrencyFormat)
    {
      d1 = paramDouble * 128.0D;
      d2 = -(paramDouble * 32.0D);
      d3 = paramDouble * 4.0D;
    }
    else
    {
      d1 = paramDouble * 1024.0D;
      d2 = -(paramDouble * 16.0D);
      d3 = -(paramDouble * 8.0D);
    }
    assert (-d2 >= Math.abs(d3));
    d4 = d2 + d3;
    d6 = d4 - d2;
    d5 = d3 - d6;
    double d7 = d4;
    double d8 = d5;
    assert (d1 >= Math.abs(d7));
    d4 = d1 + d7;
    d6 = d4 - d1;
    d5 = d7 - d6;
    double d9 = d5;
    double d10 = d4;
    double d11 = d8 + d9;
    assert (d10 >= Math.abs(d11));
    d4 = d10 + d11;
    d6 = d4 - d10;
    double d12 = d11 - d6;
    if (d12 > 0.0D) {
      return true;
    }
    if (d12 < 0.0D) {
      return false;
    }
    return (paramInt & 0x1) != 0;
  }
  
  private void collectIntegralDigits(int paramInt1, char[] paramArrayOfChar, int paramInt2)
  {
    int i = paramInt2;
    while (paramInt1 > 999)
    {
      int j = paramInt1 / 1000;
      int k = paramInt1 - (j << 10) + (j << 4) + (j << 3);
      paramInt1 = j;
      paramArrayOfChar[(i--)] = DigitArrays.DigitOnes1000[k];
      paramArrayOfChar[(i--)] = DigitArrays.DigitTens1000[k];
      paramArrayOfChar[(i--)] = DigitArrays.DigitHundreds1000[k];
      paramArrayOfChar[(i--)] = fastPathData.groupingChar;
    }
    paramArrayOfChar[i] = DigitArrays.DigitOnes1000[paramInt1];
    if (paramInt1 > 9)
    {
      paramArrayOfChar[(--i)] = DigitArrays.DigitTens1000[paramInt1];
      if (paramInt1 > 99) {
        paramArrayOfChar[(--i)] = DigitArrays.DigitHundreds1000[paramInt1];
      }
    }
    fastPathData.firstUsedIndex = i;
  }
  
  private void collectFractionalDigits(int paramInt1, char[] paramArrayOfChar, int paramInt2)
  {
    int i = paramInt2;
    int j = DigitArrays.DigitOnes1000[paramInt1];
    int k = DigitArrays.DigitTens1000[paramInt1];
    if (isCurrencyFormat)
    {
      paramArrayOfChar[(i++)] = k;
      paramArrayOfChar[(i++)] = j;
    }
    else if (paramInt1 != 0)
    {
      paramArrayOfChar[(i++)] = DigitArrays.DigitHundreds1000[paramInt1];
      if (j != 48)
      {
        paramArrayOfChar[(i++)] = k;
        paramArrayOfChar[(i++)] = j;
      }
      else if (k != 48)
      {
        paramArrayOfChar[(i++)] = k;
      }
    }
    else
    {
      i--;
    }
    fastPathData.lastFreeIndex = i;
  }
  
  private void addAffixes(char[] paramArrayOfChar1, char[] paramArrayOfChar2, char[] paramArrayOfChar3)
  {
    int i = paramArrayOfChar2.length;
    int j = paramArrayOfChar3.length;
    if (i != 0) {
      prependPrefix(paramArrayOfChar2, i, paramArrayOfChar1);
    }
    if (j != 0) {
      appendSuffix(paramArrayOfChar3, j, paramArrayOfChar1);
    }
  }
  
  private void prependPrefix(char[] paramArrayOfChar1, int paramInt, char[] paramArrayOfChar2)
  {
    fastPathData.firstUsedIndex -= paramInt;
    int i = fastPathData.firstUsedIndex;
    if (paramInt == 1)
    {
      paramArrayOfChar2[i] = paramArrayOfChar1[0];
    }
    else if (paramInt <= 4)
    {
      int j = i;
      int k = j + paramInt - 1;
      int m = paramInt - 1;
      paramArrayOfChar2[j] = paramArrayOfChar1[0];
      paramArrayOfChar2[k] = paramArrayOfChar1[m];
      if (paramInt > 2) {
        paramArrayOfChar2[(++j)] = paramArrayOfChar1[1];
      }
      if (paramInt == 4) {
        paramArrayOfChar2[(--k)] = paramArrayOfChar1[2];
      }
    }
    else
    {
      System.arraycopy(paramArrayOfChar1, 0, paramArrayOfChar2, i, paramInt);
    }
  }
  
  private void appendSuffix(char[] paramArrayOfChar1, int paramInt, char[] paramArrayOfChar2)
  {
    int i = fastPathData.lastFreeIndex;
    if (paramInt == 1)
    {
      paramArrayOfChar2[i] = paramArrayOfChar1[0];
    }
    else if (paramInt <= 4)
    {
      int j = i;
      int k = j + paramInt - 1;
      int m = paramInt - 1;
      paramArrayOfChar2[j] = paramArrayOfChar1[0];
      paramArrayOfChar2[k] = paramArrayOfChar1[m];
      if (paramInt > 2) {
        paramArrayOfChar2[(++j)] = paramArrayOfChar1[1];
      }
      if (paramInt == 4) {
        paramArrayOfChar2[(--k)] = paramArrayOfChar1[2];
      }
    }
    else
    {
      System.arraycopy(paramArrayOfChar1, 0, paramArrayOfChar2, i, paramInt);
    }
    fastPathData.lastFreeIndex += paramInt;
  }
  
  private void localizeDigits(char[] paramArrayOfChar)
  {
    int i = fastPathData.lastFreeIndex - fastPathData.fractionalFirstIndex;
    if (i < 0) {
      i = groupingSize;
    }
    for (int j = fastPathData.lastFreeIndex - 1; j >= fastPathData.firstUsedIndex; j--) {
      if (i != 0)
      {
        int tmp52_51 = j;
        char[] tmp52_50 = paramArrayOfChar;
        tmp52_50[tmp52_51] = ((char)(tmp52_50[tmp52_51] + fastPathData.zeroDelta));
        i--;
      }
      else
      {
        i = groupingSize;
      }
    }
  }
  
  private void fastDoubleFormat(double paramDouble, boolean paramBoolean)
  {
    char[] arrayOfChar = fastPathData.fastPathContainer;
    int i = (int)paramDouble;
    double d1 = paramDouble - i;
    double d2 = d1 * fastPathData.fractionalScaleFactor;
    int j = (int)d2;
    d2 -= j;
    boolean bool = false;
    if (d2 >= 0.5D)
    {
      if (d2 == 0.5D) {
        bool = exactRoundUp(d1, j);
      } else {
        bool = true;
      }
      if (bool) {
        if (j < fastPathData.fractionalMaxIntBound)
        {
          j++;
        }
        else
        {
          j = 0;
          i++;
        }
      }
    }
    collectFractionalDigits(j, arrayOfChar, fastPathData.fractionalFirstIndex);
    collectIntegralDigits(i, arrayOfChar, fastPathData.integralLastIndex);
    if (fastPathData.zeroDelta != 0) {
      localizeDigits(arrayOfChar);
    }
    if (paramBoolean)
    {
      if (fastPathData.negativeAffixesRequired) {
        addAffixes(arrayOfChar, fastPathData.charsNegativePrefix, fastPathData.charsNegativeSuffix);
      }
    }
    else if (fastPathData.positiveAffixesRequired) {
      addAffixes(arrayOfChar, fastPathData.charsPositivePrefix, fastPathData.charsPositiveSuffix);
    }
  }
  
  String fastFormat(double paramDouble)
  {
    if (fastPathCheckNeeded) {
      checkAndSetFastPathStatus();
    }
    if (!isFastPath) {
      return null;
    }
    if (!Double.isFinite(paramDouble)) {
      return null;
    }
    boolean bool = false;
    if (paramDouble < 0.0D)
    {
      bool = true;
      paramDouble = -paramDouble;
    }
    else if (paramDouble == 0.0D)
    {
      bool = Math.copySign(1.0D, paramDouble) == -1.0D;
      paramDouble = 0.0D;
    }
    if (paramDouble > 2.147483647E9D) {
      return null;
    }
    fastDoubleFormat(paramDouble, bool);
    return new String(fastPathData.fastPathContainer, fastPathData.firstUsedIndex, fastPathData.lastFreeIndex - fastPathData.firstUsedIndex);
  }
  
  private StringBuffer subformat(StringBuffer paramStringBuffer, Format.FieldDelegate paramFieldDelegate, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    char c1 = symbols.getZeroDigit();
    int i = c1 - '0';
    char c2 = symbols.getGroupingSeparator();
    char c3 = isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
    if (digitList.isZero()) {
      digitList.decimalAt = 0;
    }
    if (paramBoolean1) {
      append(paramStringBuffer, negativePrefix, paramFieldDelegate, getNegativePrefixFieldPositions(), NumberFormat.Field.SIGN);
    } else {
      append(paramStringBuffer, positivePrefix, paramFieldDelegate, getPositivePrefixFieldPositions(), NumberFormat.Field.SIGN);
    }
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    if (useExponentialNotation)
    {
      j = paramStringBuffer.length();
      k = -1;
      m = -1;
      n = digitList.decimalAt;
      i1 = paramInt1;
      i2 = paramInt2;
      if ((i1 > 1) && (i1 > paramInt2))
      {
        if (n >= 1) {
          n = (n - 1) / i1 * i1;
        } else {
          n = (n - i1) / i1 * i1;
        }
        i2 = 1;
      }
      else
      {
        n -= i2;
      }
      i3 = paramInt2 + paramInt4;
      if (i3 < 0) {
        i3 = Integer.MAX_VALUE;
      }
      i4 = digitList.isZero() ? i2 : digitList.decimalAt - n;
      if (i3 < i4) {
        i3 = i4;
      }
      int i5 = digitList.count;
      if (i3 > i5) {
        i5 = i3;
      }
      int i6 = 0;
      for (int i7 = 0; i7 < i5; i7++)
      {
        if (i7 == i4)
        {
          k = paramStringBuffer.length();
          paramStringBuffer.append(c3);
          i6 = 1;
          m = paramStringBuffer.length();
        }
        paramStringBuffer.append(i7 < digitList.count ? (char)(digitList.digits[i7] + i) : c1);
      }
      if ((decimalSeparatorAlwaysShown) && (i5 == i4))
      {
        k = paramStringBuffer.length();
        paramStringBuffer.append(c3);
        i6 = 1;
        m = paramStringBuffer.length();
      }
      if (k == -1) {
        k = paramStringBuffer.length();
      }
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, j, k, paramStringBuffer);
      if (i6 != 0) {
        paramFieldDelegate.formatted(NumberFormat.Field.DECIMAL_SEPARATOR, NumberFormat.Field.DECIMAL_SEPARATOR, k, m, paramStringBuffer);
      }
      if (m == -1) {
        m = paramStringBuffer.length();
      }
      paramFieldDelegate.formatted(1, NumberFormat.Field.FRACTION, NumberFormat.Field.FRACTION, m, paramStringBuffer.length(), paramStringBuffer);
      i7 = paramStringBuffer.length();
      paramStringBuffer.append(symbols.getExponentSeparator());
      paramFieldDelegate.formatted(NumberFormat.Field.EXPONENT_SYMBOL, NumberFormat.Field.EXPONENT_SYMBOL, i7, paramStringBuffer.length(), paramStringBuffer);
      if (digitList.isZero()) {
        n = 0;
      }
      boolean bool = n < 0;
      if (bool)
      {
        n = -n;
        i7 = paramStringBuffer.length();
        paramStringBuffer.append(symbols.getMinusSign());
        paramFieldDelegate.formatted(NumberFormat.Field.EXPONENT_SIGN, NumberFormat.Field.EXPONENT_SIGN, i7, paramStringBuffer.length(), paramStringBuffer);
      }
      digitList.set(bool, n);
      int i8 = paramStringBuffer.length();
      for (int i9 = digitList.decimalAt; i9 < minExponentDigits; i9++) {
        paramStringBuffer.append(c1);
      }
      for (i9 = 0; i9 < digitList.decimalAt; i9++) {
        paramStringBuffer.append(i9 < digitList.count ? (char)(digitList.digits[i9] + i) : c1);
      }
      paramFieldDelegate.formatted(NumberFormat.Field.EXPONENT, NumberFormat.Field.EXPONENT, i8, paramStringBuffer.length(), paramStringBuffer);
    }
    else
    {
      j = paramStringBuffer.length();
      k = paramInt2;
      m = 0;
      if ((digitList.decimalAt > 0) && (k < digitList.decimalAt)) {
        k = digitList.decimalAt;
      }
      if (k > paramInt1)
      {
        k = paramInt1;
        m = digitList.decimalAt - k;
      }
      n = paramStringBuffer.length();
      for (i1 = k - 1; i1 >= 0; i1--)
      {
        if ((i1 < digitList.decimalAt) && (m < digitList.count)) {
          paramStringBuffer.append((char)(digitList.digits[(m++)] + i));
        } else {
          paramStringBuffer.append(c1);
        }
        if ((isGroupingUsed()) && (i1 > 0) && (groupingSize != 0) && (i1 % groupingSize == 0))
        {
          i2 = paramStringBuffer.length();
          paramStringBuffer.append(c2);
          paramFieldDelegate.formatted(NumberFormat.Field.GROUPING_SEPARATOR, NumberFormat.Field.GROUPING_SEPARATOR, i2, paramStringBuffer.length(), paramStringBuffer);
        }
      }
      i1 = (paramInt4 > 0) || ((!paramBoolean2) && (m < digitList.count)) ? 1 : 0;
      if ((i1 == 0) && (paramStringBuffer.length() == n)) {
        paramStringBuffer.append(c1);
      }
      paramFieldDelegate.formatted(0, NumberFormat.Field.INTEGER, NumberFormat.Field.INTEGER, j, paramStringBuffer.length(), paramStringBuffer);
      i2 = paramStringBuffer.length();
      if ((decimalSeparatorAlwaysShown) || (i1 != 0)) {
        paramStringBuffer.append(c3);
      }
      if (i2 != paramStringBuffer.length()) {
        paramFieldDelegate.formatted(NumberFormat.Field.DECIMAL_SEPARATOR, NumberFormat.Field.DECIMAL_SEPARATOR, i2, paramStringBuffer.length(), paramStringBuffer);
      }
      i3 = paramStringBuffer.length();
      for (i4 = 0; (i4 < paramInt3) && ((i4 < paramInt4) || ((!paramBoolean2) && (m < digitList.count))); i4++) {
        if (-1 - i4 > digitList.decimalAt - 1) {
          paramStringBuffer.append(c1);
        } else if ((!paramBoolean2) && (m < digitList.count)) {
          paramStringBuffer.append((char)(digitList.digits[(m++)] + i));
        } else {
          paramStringBuffer.append(c1);
        }
      }
      paramFieldDelegate.formatted(1, NumberFormat.Field.FRACTION, NumberFormat.Field.FRACTION, i3, paramStringBuffer.length(), paramStringBuffer);
    }
    if (paramBoolean1) {
      append(paramStringBuffer, negativeSuffix, paramFieldDelegate, getNegativeSuffixFieldPositions(), NumberFormat.Field.SIGN);
    } else {
      append(paramStringBuffer, positiveSuffix, paramFieldDelegate, getPositiveSuffixFieldPositions(), NumberFormat.Field.SIGN);
    }
    return paramStringBuffer;
  }
  
  private void append(StringBuffer paramStringBuffer, String paramString, Format.FieldDelegate paramFieldDelegate, FieldPosition[] paramArrayOfFieldPosition, Format.Field paramField)
  {
    int i = paramStringBuffer.length();
    if (paramString.length() > 0)
    {
      paramStringBuffer.append(paramString);
      int j = 0;
      int k = paramArrayOfFieldPosition.length;
      while (j < k)
      {
        FieldPosition localFieldPosition = paramArrayOfFieldPosition[j];
        Format.Field localField = localFieldPosition.getFieldAttribute();
        if (localField == NumberFormat.Field.SIGN) {
          localField = paramField;
        }
        paramFieldDelegate.formatted(localField, localField, i + localFieldPosition.getBeginIndex(), i + localFieldPosition.getEndIndex(), paramStringBuffer);
        j++;
      }
    }
  }
  
  public Number parse(String paramString, ParsePosition paramParsePosition)
  {
    if (paramString.regionMatches(index, symbols.getNaN(), 0, symbols.getNaN().length()))
    {
      index += symbols.getNaN().length();
      return new Double(NaN.0D);
    }
    boolean[] arrayOfBoolean = new boolean[2];
    if (!subparse(paramString, paramParsePosition, positivePrefix, negativePrefix, digitList, false, arrayOfBoolean)) {
      return null;
    }
    if (arrayOfBoolean[0] != 0)
    {
      if (arrayOfBoolean[1] == (multiplier >= 0 ? 1 : 0)) {
        return new Double(Double.POSITIVE_INFINITY);
      }
      return new Double(Double.NEGATIVE_INFINITY);
    }
    if (multiplier == 0)
    {
      if (digitList.isZero()) {
        return new Double(NaN.0D);
      }
      if (arrayOfBoolean[1] != 0) {
        return new Double(Double.POSITIVE_INFINITY);
      }
      return new Double(Double.NEGATIVE_INFINITY);
    }
    if (isParseBigDecimal())
    {
      BigDecimal localBigDecimal = digitList.getBigDecimal();
      if (multiplier != 1) {
        try
        {
          localBigDecimal = localBigDecimal.divide(getBigDecimalMultiplier());
        }
        catch (ArithmeticException localArithmeticException)
        {
          localBigDecimal = localBigDecimal.divide(getBigDecimalMultiplier(), roundingMode);
        }
      }
      if (arrayOfBoolean[1] == 0) {
        localBigDecimal = localBigDecimal.negate();
      }
      return localBigDecimal;
    }
    int i = 1;
    int j = 0;
    double d = 0.0D;
    long l = 0L;
    if (digitList.fitsIntoLong(arrayOfBoolean[1], isParseIntegerOnly()))
    {
      i = 0;
      l = digitList.getLong();
      if (l < 0L) {
        j = 1;
      }
    }
    else
    {
      d = digitList.getDouble();
    }
    if (multiplier != 1) {
      if (i != 0)
      {
        d /= multiplier;
      }
      else if (l % multiplier == 0L)
      {
        l /= multiplier;
      }
      else
      {
        d = l / multiplier;
        i = 1;
      }
    }
    if ((arrayOfBoolean[1] == 0) && (j == 0))
    {
      d = -d;
      l = -l;
    }
    if ((multiplier != 1) && (i != 0))
    {
      l = d;
      i = ((d != l) || ((d == 0.0D) && (1.0D / d < 0.0D))) && (!isParseIntegerOnly()) ? 1 : 0;
    }
    return i != 0 ? new Double(d) : new Long(l);
  }
  
  private BigInteger getBigIntegerMultiplier()
  {
    if (bigIntegerMultiplier == null) {
      bigIntegerMultiplier = BigInteger.valueOf(multiplier);
    }
    return bigIntegerMultiplier;
  }
  
  private BigDecimal getBigDecimalMultiplier()
  {
    if (bigDecimalMultiplier == null) {
      bigDecimalMultiplier = new BigDecimal(multiplier);
    }
    return bigDecimalMultiplier;
  }
  
  private final boolean subparse(String paramString1, ParsePosition paramParsePosition, String paramString2, String paramString3, DigitList paramDigitList, boolean paramBoolean, boolean[] paramArrayOfBoolean)
  {
    int i = index;
    int j = index;
    boolean bool1 = paramString1.regionMatches(i, paramString2, 0, paramString2.length());
    boolean bool2 = paramString1.regionMatches(i, paramString3, 0, paramString3.length());
    if ((bool1) && (bool2)) {
      if (paramString2.length() > paramString3.length()) {
        bool2 = false;
      } else if (paramString2.length() < paramString3.length()) {
        bool1 = false;
      }
    }
    if (bool1)
    {
      i += paramString2.length();
    }
    else if (bool2)
    {
      i += paramString3.length();
    }
    else
    {
      errorIndex = i;
      return false;
    }
    paramArrayOfBoolean[0] = false;
    if ((!paramBoolean) && (paramString1.regionMatches(i, symbols.getInfinity(), 0, symbols.getInfinity().length())))
    {
      i += symbols.getInfinity().length();
      paramArrayOfBoolean[0] = true;
    }
    else
    {
      decimalAt = (count = 0);
      int m = symbols.getZeroDigit();
      char c1 = isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
      char c2 = symbols.getGroupingSeparator();
      String str = symbols.getExponentSeparator();
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      int i3 = 0;
      int i4 = 0;
      int k = -1;
      while (i < paramString1.length())
      {
        char c3 = paramString1.charAt(i);
        int i5 = c3 - m;
        if ((i5 < 0) || (i5 > 9)) {
          i5 = Character.digit(c3, 10);
        }
        if (i5 == 0)
        {
          k = -1;
          i2 = 1;
          if (count == 0)
          {
            if (n != 0) {
              decimalAt -= 1;
            }
          }
          else
          {
            i4++;
            paramDigitList.append((char)(i5 + 48));
          }
        }
        else if ((i5 > 0) && (i5 <= 9))
        {
          i2 = 1;
          i4++;
          paramDigitList.append((char)(i5 + 48));
          k = -1;
        }
        else if ((!paramBoolean) && (c3 == c1))
        {
          if ((isParseIntegerOnly()) || (n != 0)) {
            break;
          }
          decimalAt = i4;
          n = 1;
        }
        else if ((!paramBoolean) && (c3 == c2) && (isGroupingUsed()))
        {
          if (n != 0) {
            break;
          }
          k = i;
        }
        else
        {
          if ((paramBoolean) || (!paramString1.regionMatches(i, str, 0, str.length())) || (i1 != 0)) {
            break;
          }
          ParsePosition localParsePosition = new ParsePosition(i + str.length());
          boolean[] arrayOfBoolean = new boolean[2];
          DigitList localDigitList = new DigitList();
          if ((!subparse(paramString1, localParsePosition, "", Character.toString(symbols.getMinusSign()), localDigitList, true, arrayOfBoolean)) || (!localDigitList.fitsIntoLong(arrayOfBoolean[1], true))) {
            break;
          }
          i = index;
          i3 = (int)localDigitList.getLong();
          if (arrayOfBoolean[1] == 0) {
            i3 = -i3;
          }
          i1 = 1;
          break;
        }
        i++;
      }
      if (k != -1) {
        i = k;
      }
      if (n == 0) {
        decimalAt = i4;
      }
      decimalAt += i3;
      if ((i2 == 0) && (i4 == 0))
      {
        index = j;
        errorIndex = j;
        return false;
      }
    }
    if (!paramBoolean)
    {
      if (bool1) {
        bool1 = paramString1.regionMatches(i, positiveSuffix, 0, positiveSuffix.length());
      }
      if (bool2) {
        bool2 = paramString1.regionMatches(i, negativeSuffix, 0, negativeSuffix.length());
      }
      if ((bool1) && (bool2)) {
        if (positiveSuffix.length() > negativeSuffix.length()) {
          bool2 = false;
        } else if (positiveSuffix.length() < negativeSuffix.length()) {
          bool1 = false;
        }
      }
      if (bool1 == bool2)
      {
        errorIndex = i;
        return false;
      }
      index = (i + (bool1 ? positiveSuffix.length() : negativeSuffix.length()));
    }
    else
    {
      index = i;
    }
    paramArrayOfBoolean[1] = bool1;
    if (index == j)
    {
      errorIndex = i;
      return false;
    }
    return true;
  }
  
  public DecimalFormatSymbols getDecimalFormatSymbols()
  {
    try
    {
      return (DecimalFormatSymbols)symbols.clone();
    }
    catch (Exception localException) {}
    return null;
  }
  
  public void setDecimalFormatSymbols(DecimalFormatSymbols paramDecimalFormatSymbols)
  {
    try
    {
      symbols = ((DecimalFormatSymbols)paramDecimalFormatSymbols.clone());
      expandAffixes();
      fastPathCheckNeeded = true;
    }
    catch (Exception localException) {}
  }
  
  public String getPositivePrefix()
  {
    return positivePrefix;
  }
  
  public void setPositivePrefix(String paramString)
  {
    positivePrefix = paramString;
    posPrefixPattern = null;
    positivePrefixFieldPositions = null;
    fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getPositivePrefixFieldPositions()
  {
    if (positivePrefixFieldPositions == null) {
      if (posPrefixPattern != null) {
        positivePrefixFieldPositions = expandAffix(posPrefixPattern);
      } else {
        positivePrefixFieldPositions = EmptyFieldPositionArray;
      }
    }
    return positivePrefixFieldPositions;
  }
  
  public String getNegativePrefix()
  {
    return negativePrefix;
  }
  
  public void setNegativePrefix(String paramString)
  {
    negativePrefix = paramString;
    negPrefixPattern = null;
    fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getNegativePrefixFieldPositions()
  {
    if (negativePrefixFieldPositions == null) {
      if (negPrefixPattern != null) {
        negativePrefixFieldPositions = expandAffix(negPrefixPattern);
      } else {
        negativePrefixFieldPositions = EmptyFieldPositionArray;
      }
    }
    return negativePrefixFieldPositions;
  }
  
  public String getPositiveSuffix()
  {
    return positiveSuffix;
  }
  
  public void setPositiveSuffix(String paramString)
  {
    positiveSuffix = paramString;
    posSuffixPattern = null;
    fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getPositiveSuffixFieldPositions()
  {
    if (positiveSuffixFieldPositions == null) {
      if (posSuffixPattern != null) {
        positiveSuffixFieldPositions = expandAffix(posSuffixPattern);
      } else {
        positiveSuffixFieldPositions = EmptyFieldPositionArray;
      }
    }
    return positiveSuffixFieldPositions;
  }
  
  public String getNegativeSuffix()
  {
    return negativeSuffix;
  }
  
  public void setNegativeSuffix(String paramString)
  {
    negativeSuffix = paramString;
    negSuffixPattern = null;
    fastPathCheckNeeded = true;
  }
  
  private FieldPosition[] getNegativeSuffixFieldPositions()
  {
    if (negativeSuffixFieldPositions == null) {
      if (negSuffixPattern != null) {
        negativeSuffixFieldPositions = expandAffix(negSuffixPattern);
      } else {
        negativeSuffixFieldPositions = EmptyFieldPositionArray;
      }
    }
    return negativeSuffixFieldPositions;
  }
  
  public int getMultiplier()
  {
    return multiplier;
  }
  
  public void setMultiplier(int paramInt)
  {
    multiplier = paramInt;
    bigDecimalMultiplier = null;
    bigIntegerMultiplier = null;
    fastPathCheckNeeded = true;
  }
  
  public void setGroupingUsed(boolean paramBoolean)
  {
    super.setGroupingUsed(paramBoolean);
    fastPathCheckNeeded = true;
  }
  
  public int getGroupingSize()
  {
    return groupingSize;
  }
  
  public void setGroupingSize(int paramInt)
  {
    groupingSize = ((byte)paramInt);
    fastPathCheckNeeded = true;
  }
  
  public boolean isDecimalSeparatorAlwaysShown()
  {
    return decimalSeparatorAlwaysShown;
  }
  
  public void setDecimalSeparatorAlwaysShown(boolean paramBoolean)
  {
    decimalSeparatorAlwaysShown = paramBoolean;
    fastPathCheckNeeded = true;
  }
  
  public boolean isParseBigDecimal()
  {
    return parseBigDecimal;
  }
  
  public void setParseBigDecimal(boolean paramBoolean)
  {
    parseBigDecimal = paramBoolean;
  }
  
  public Object clone()
  {
    DecimalFormat localDecimalFormat = (DecimalFormat)super.clone();
    symbols = ((DecimalFormatSymbols)symbols.clone());
    digitList = ((DigitList)digitList.clone());
    fastPathCheckNeeded = true;
    isFastPath = false;
    fastPathData = null;
    return localDecimalFormat;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    DecimalFormat localDecimalFormat = (DecimalFormat)paramObject;
    return ((posPrefixPattern == posPrefixPattern) && (positivePrefix.equals(positivePrefix))) || ((posPrefixPattern != null) && (posPrefixPattern.equals(posPrefixPattern)) && (((posSuffixPattern == posSuffixPattern) && (positiveSuffix.equals(positiveSuffix))) || ((posSuffixPattern != null) && (posSuffixPattern.equals(posSuffixPattern)) && (((negPrefixPattern == negPrefixPattern) && (negativePrefix.equals(negativePrefix))) || ((negPrefixPattern != null) && (negPrefixPattern.equals(negPrefixPattern)) && (((negSuffixPattern == negSuffixPattern) && (negativeSuffix.equals(negativeSuffix))) || ((negSuffixPattern != null) && (negSuffixPattern.equals(negSuffixPattern)) && (multiplier == multiplier) && (groupingSize == groupingSize) && (decimalSeparatorAlwaysShown == decimalSeparatorAlwaysShown) && (parseBigDecimal == parseBigDecimal) && (useExponentialNotation == useExponentialNotation) && ((!useExponentialNotation) || (minExponentDigits == minExponentDigits)) && (maximumIntegerDigits == maximumIntegerDigits) && (minimumIntegerDigits == minimumIntegerDigits) && (maximumFractionDigits == maximumFractionDigits) && (minimumFractionDigits == minimumFractionDigits) && (roundingMode == roundingMode) && (symbols.equals(symbols)))))))));
  }
  
  public int hashCode()
  {
    return super.hashCode() * 37 + positivePrefix.hashCode();
  }
  
  public String toPattern()
  {
    return toPattern(false);
  }
  
  public String toLocalizedPattern()
  {
    return toPattern(true);
  }
  
  private void expandAffixes()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (posPrefixPattern != null)
    {
      positivePrefix = expandAffix(posPrefixPattern, localStringBuffer);
      positivePrefixFieldPositions = null;
    }
    if (posSuffixPattern != null)
    {
      positiveSuffix = expandAffix(posSuffixPattern, localStringBuffer);
      positiveSuffixFieldPositions = null;
    }
    if (negPrefixPattern != null)
    {
      negativePrefix = expandAffix(negPrefixPattern, localStringBuffer);
      negativePrefixFieldPositions = null;
    }
    if (negSuffixPattern != null)
    {
      negativeSuffix = expandAffix(negSuffixPattern, localStringBuffer);
      negativeSuffixFieldPositions = null;
    }
  }
  
  private String expandAffix(String paramString, StringBuffer paramStringBuffer)
  {
    paramStringBuffer.setLength(0);
    int i = 0;
    while (i < paramString.length())
    {
      char c = paramString.charAt(i++);
      if (c == '\'')
      {
        c = paramString.charAt(i++);
        switch (c)
        {
        case '¤': 
          if ((i < paramString.length()) && (paramString.charAt(i) == '¤'))
          {
            i++;
            paramStringBuffer.append(symbols.getInternationalCurrencySymbol());
            continue;
          }
          paramStringBuffer.append(symbols.getCurrencySymbol());
          break;
        case '%': 
          c = symbols.getPercent();
          break;
        case '‰': 
          c = symbols.getPerMill();
          break;
        case '-': 
          c = symbols.getMinusSign();
        }
      }
      else
      {
        paramStringBuffer.append(c);
      }
    }
    return paramStringBuffer.toString();
  }
  
  private FieldPosition[] expandAffix(String paramString)
  {
    ArrayList localArrayList = null;
    int i = 0;
    int j = 0;
    while (j < paramString.length())
    {
      int k = paramString.charAt(j++);
      if (k == 39)
      {
        int m = -1;
        NumberFormat.Field localField = null;
        k = paramString.charAt(j++);
        Object localObject;
        switch (k)
        {
        case 164: 
          if ((j < paramString.length()) && (paramString.charAt(j) == '¤'))
          {
            j++;
            localObject = symbols.getInternationalCurrencySymbol();
          }
          else
          {
            localObject = symbols.getCurrencySymbol();
          }
          if (((String)localObject).length() <= 0) {
            continue;
          }
          if (localArrayList == null) {
            localArrayList = new ArrayList(2);
          }
          FieldPosition localFieldPosition = new FieldPosition(NumberFormat.Field.CURRENCY);
          localFieldPosition.setBeginIndex(i);
          localFieldPosition.setEndIndex(i + ((String)localObject).length());
          localArrayList.add(localFieldPosition);
          i += ((String)localObject).length();
          break;
        case 37: 
          k = symbols.getPercent();
          m = -1;
          localField = NumberFormat.Field.PERCENT;
          break;
        case 8240: 
          k = symbols.getPerMill();
          m = -1;
          localField = NumberFormat.Field.PERMILLE;
          break;
        case 45: 
          k = symbols.getMinusSign();
          m = -1;
          localField = NumberFormat.Field.SIGN;
        default: 
          if (localField != null)
          {
            if (localArrayList == null) {
              localArrayList = new ArrayList(2);
            }
            localObject = new FieldPosition(localField, m);
            ((FieldPosition)localObject).setBeginIndex(i);
            ((FieldPosition)localObject).setEndIndex(i + 1);
            localArrayList.add(localObject);
          }
          break;
        }
      }
      else
      {
        i++;
      }
    }
    if (localArrayList != null) {
      return (FieldPosition[])localArrayList.toArray(EmptyFieldPositionArray);
    }
    return EmptyFieldPositionArray;
  }
  
  private void appendAffix(StringBuffer paramStringBuffer, String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramString1 == null)
    {
      appendAffix(paramStringBuffer, paramString2, paramBoolean);
    }
    else
    {
      int i;
      for (int j = 0; j < paramString1.length(); j = i)
      {
        i = paramString1.indexOf('\'', j);
        if (i < 0)
        {
          appendAffix(paramStringBuffer, paramString1.substring(j), paramBoolean);
          break;
        }
        if (i > j) {
          appendAffix(paramStringBuffer, paramString1.substring(j, i), paramBoolean);
        }
        char c = paramString1.charAt(++i);
        i++;
        if (c == '\'')
        {
          paramStringBuffer.append(c);
        }
        else if ((c == '¤') && (i < paramString1.length()) && (paramString1.charAt(i) == '¤'))
        {
          i++;
          paramStringBuffer.append(c);
        }
        else if (paramBoolean)
        {
          switch (c)
          {
          case '%': 
            c = symbols.getPercent();
            break;
          case '‰': 
            c = symbols.getPerMill();
            break;
          case '-': 
            c = symbols.getMinusSign();
          }
        }
        paramStringBuffer.append(c);
      }
    }
  }
  
  private void appendAffix(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      i = (paramString.indexOf(symbols.getZeroDigit()) >= 0) || (paramString.indexOf(symbols.getGroupingSeparator()) >= 0) || (paramString.indexOf(symbols.getDecimalSeparator()) >= 0) || (paramString.indexOf(symbols.getPercent()) >= 0) || (paramString.indexOf(symbols.getPerMill()) >= 0) || (paramString.indexOf(symbols.getDigit()) >= 0) || (paramString.indexOf(symbols.getPatternSeparator()) >= 0) || (paramString.indexOf(symbols.getMinusSign()) >= 0) || (paramString.indexOf('¤') >= 0) ? 1 : 0;
    } else {
      i = (paramString.indexOf('0') >= 0) || (paramString.indexOf(',') >= 0) || (paramString.indexOf('.') >= 0) || (paramString.indexOf('%') >= 0) || (paramString.indexOf('‰') >= 0) || (paramString.indexOf('#') >= 0) || (paramString.indexOf(';') >= 0) || (paramString.indexOf('-') >= 0) || (paramString.indexOf('¤') >= 0) ? 1 : 0;
    }
    if (i != 0) {
      paramStringBuffer.append('\'');
    }
    if (paramString.indexOf('\'') < 0) {
      paramStringBuffer.append(paramString);
    } else {
      for (int j = 0; j < paramString.length(); j++)
      {
        char c = paramString.charAt(j);
        paramStringBuffer.append(c);
        if (c == '\'') {
          paramStringBuffer.append(c);
        }
      }
    }
    if (i != 0) {
      paramStringBuffer.append('\'');
    }
  }
  
  private String toPattern(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 1; i >= 0; i--)
    {
      if (i == 1) {
        appendAffix(localStringBuffer, posPrefixPattern, positivePrefix, paramBoolean);
      } else {
        appendAffix(localStringBuffer, negPrefixPattern, negativePrefix, paramBoolean);
      }
      int k = useExponentialNotation ? getMaximumIntegerDigits() : Math.max(groupingSize, getMinimumIntegerDigits()) + 1;
      for (int j = k; j > 0; j--)
      {
        if ((j != k) && (isGroupingUsed()) && (groupingSize != 0) && (j % groupingSize == 0)) {
          localStringBuffer.append(paramBoolean ? symbols.getGroupingSeparator() : ',');
        }
        localStringBuffer.append(paramBoolean ? symbols.getDigit() : j <= getMinimumIntegerDigits() ? '0' : paramBoolean ? symbols.getZeroDigit() : '#');
      }
      if ((getMaximumFractionDigits() > 0) || (decimalSeparatorAlwaysShown)) {
        localStringBuffer.append(paramBoolean ? symbols.getDecimalSeparator() : '.');
      }
      for (j = 0; j < getMaximumFractionDigits(); j++) {
        if (j < getMinimumFractionDigits()) {
          localStringBuffer.append(paramBoolean ? symbols.getZeroDigit() : '0');
        } else {
          localStringBuffer.append(paramBoolean ? symbols.getDigit() : '#');
        }
      }
      if (useExponentialNotation)
      {
        localStringBuffer.append(paramBoolean ? symbols.getExponentSeparator() : "E");
        for (j = 0; j < minExponentDigits; j++) {
          localStringBuffer.append(paramBoolean ? symbols.getZeroDigit() : '0');
        }
      }
      if (i == 1)
      {
        appendAffix(localStringBuffer, posSuffixPattern, positiveSuffix, paramBoolean);
        if (((negSuffixPattern != posSuffixPattern) || (!negativeSuffix.equals(positiveSuffix))) && ((negSuffixPattern != null) && (negSuffixPattern.equals(posSuffixPattern)) && (((negPrefixPattern != null) && (posPrefixPattern != null) && (negPrefixPattern.equals("'-" + posPrefixPattern))) || ((negPrefixPattern == posPrefixPattern) && (negativePrefix.equals(symbols.getMinusSign() + positivePrefix)))))) {
          break;
        }
        localStringBuffer.append(paramBoolean ? symbols.getPatternSeparator() : ';');
      }
      else
      {
        appendAffix(localStringBuffer, negSuffixPattern, negativeSuffix, paramBoolean);
      }
    }
    return localStringBuffer.toString();
  }
  
  public void applyPattern(String paramString)
  {
    applyPattern(paramString, false);
  }
  
  public void applyLocalizedPattern(String paramString)
  {
    applyPattern(paramString, true);
  }
  
  private void applyPattern(String paramString, boolean paramBoolean)
  {
    char c1 = '0';
    char c2 = ',';
    char c3 = '.';
    char c4 = '%';
    char c5 = '‰';
    char c6 = '#';
    int i = 59;
    String str = "E";
    char c7 = '-';
    if (paramBoolean)
    {
      c1 = symbols.getZeroDigit();
      c2 = symbols.getGroupingSeparator();
      c3 = symbols.getDecimalSeparator();
      c4 = symbols.getPercent();
      c5 = symbols.getPerMill();
      c6 = symbols.getDigit();
      i = symbols.getPatternSeparator();
      str = symbols.getExponentSeparator();
      c7 = symbols.getMinusSign();
    }
    int j = 0;
    decimalSeparatorAlwaysShown = false;
    isCurrencyFormat = false;
    useExponentialNotation = false;
    int k = 0;
    int m = 0;
    int n = 0;
    for (int i1 = 1; (i1 >= 0) && (n < paramString.length()); i1--)
    {
      int i2 = 0;
      StringBuffer localStringBuffer1 = new StringBuffer();
      StringBuffer localStringBuffer2 = new StringBuffer();
      int i3 = -1;
      int i4 = 1;
      int i5 = 0;
      int i6 = 0;
      int i7 = 0;
      int i8 = -1;
      int i9 = 0;
      StringBuffer localStringBuffer3 = localStringBuffer1;
      int i11;
      for (int i10 = n; i10 < paramString.length(); i10++)
      {
        i11 = paramString.charAt(i10);
        switch (i9)
        {
        case 0: 
        case 2: 
          if (i2 != 0)
          {
            if (i11 == 39)
            {
              if ((i10 + 1 < paramString.length()) && (paramString.charAt(i10 + 1) == '\''))
              {
                i10++;
                localStringBuffer3.append("''");
                continue;
              }
              i2 = 0;
              continue;
            }
          }
          else
          {
            if ((i11 == c6) || (i11 == c1) || (i11 == c2) || (i11 == c3))
            {
              i9 = 1;
              if (i1 == 1) {
                k = i10;
              }
              i10--;
              continue;
            }
            if (i11 == 164)
            {
              int i12 = (i10 + 1 < paramString.length()) && (paramString.charAt(i10 + 1) == '¤') ? 1 : 0;
              if (i12 != 0) {
                i10++;
              }
              isCurrencyFormat = true;
              localStringBuffer3.append(i12 != 0 ? "'¤¤" : "'¤");
              continue;
            }
            if (i11 == 39)
            {
              if (i11 == 39)
              {
                if ((i10 + 1 < paramString.length()) && (paramString.charAt(i10 + 1) == '\''))
                {
                  i10++;
                  localStringBuffer3.append("''");
                  continue;
                }
                i2 = 1;
                continue;
              }
            }
            else
            {
              if (i11 == i)
              {
                if ((i9 == 0) || (i1 == 0)) {
                  throw new IllegalArgumentException("Unquoted special character '" + i11 + "' in pattern \"" + paramString + '"');
                }
                n = i10 + 1;
                i10 = paramString.length();
                continue;
              }
              if (i11 == c4)
              {
                if (i4 != 1) {
                  throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + paramString + '"');
                }
                i4 = 100;
                localStringBuffer3.append("'%");
                continue;
              }
              if (i11 == c5)
              {
                if (i4 != 1) {
                  throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + paramString + '"');
                }
                i4 = 1000;
                localStringBuffer3.append("'‰");
                continue;
              }
              if (i11 == c7)
              {
                localStringBuffer3.append("'-");
                continue;
              }
            }
          }
          localStringBuffer3.append(i11);
          break;
        case 1: 
          if (i1 == 1)
          {
            m++;
          }
          else
          {
            m--;
            if (m != 0) {
              continue;
            }
            i9 = 2;
            localStringBuffer3 = localStringBuffer2;
            continue;
          }
          if (i11 == c6)
          {
            if (i6 > 0) {
              i7++;
            } else {
              i5++;
            }
            if ((i8 >= 0) && (i3 < 0)) {
              i8 = (byte)(i8 + 1);
            }
          }
          else if (i11 == c1)
          {
            if (i7 > 0) {
              throw new IllegalArgumentException("Unexpected '0' in pattern \"" + paramString + '"');
            }
            i6++;
            if ((i8 >= 0) && (i3 < 0)) {
              i8 = (byte)(i8 + 1);
            }
          }
          else if (i11 == c2)
          {
            i8 = 0;
          }
          else if (i11 == c3)
          {
            if (i3 >= 0) {
              throw new IllegalArgumentException("Multiple decimal separators in pattern \"" + paramString + '"');
            }
            i3 = i5 + i6 + i7;
          }
          else if (paramString.regionMatches(i10, str, 0, str.length()))
          {
            if (useExponentialNotation) {
              throw new IllegalArgumentException("Multiple exponential symbols in pattern \"" + paramString + '"');
            }
            useExponentialNotation = true;
            minExponentDigits = 0;
            i10 += str.length();
            while ((i10 < paramString.length()) && (paramString.charAt(i10) == c1))
            {
              minExponentDigits = ((byte)(minExponentDigits + 1));
              m++;
              i10++;
            }
            if ((i5 + i6 < 1) || (minExponentDigits < 1)) {
              throw new IllegalArgumentException("Malformed exponential pattern \"" + paramString + '"');
            }
            i9 = 2;
            localStringBuffer3 = localStringBuffer2;
            i10--;
          }
          else
          {
            i9 = 2;
            localStringBuffer3 = localStringBuffer2;
            i10--;
            m--;
          }
          break;
        }
      }
      if ((i6 == 0) && (i5 > 0) && (i3 >= 0))
      {
        i10 = i3;
        if (i10 == 0) {
          i10++;
        }
        i7 = i5 - i10;
        i5 = i10 - 1;
        i6 = 1;
      }
      if (((i3 < 0) && (i7 > 0)) || ((i3 >= 0) && ((i3 < i5) || (i3 > i5 + i6))) || (i8 == 0) || (i2 != 0)) {
        throw new IllegalArgumentException("Malformed pattern \"" + paramString + '"');
      }
      if (i1 == 1)
      {
        posPrefixPattern = localStringBuffer1.toString();
        posSuffixPattern = localStringBuffer2.toString();
        negPrefixPattern = posPrefixPattern;
        negSuffixPattern = posSuffixPattern;
        i10 = i5 + i6 + i7;
        i11 = i3 >= 0 ? i3 : i10;
        setMinimumIntegerDigits(i11 - i5);
        setMaximumIntegerDigits(useExponentialNotation ? i5 + getMinimumIntegerDigits() : Integer.MAX_VALUE);
        setMaximumFractionDigits(i3 >= 0 ? i10 - i3 : 0);
        setMinimumFractionDigits(i3 >= 0 ? i5 + i6 - i3 : 0);
        setGroupingUsed(i8 > 0);
        groupingSize = (i8 > 0 ? i8 : 0);
        multiplier = i4;
        setDecimalSeparatorAlwaysShown((i3 == 0) || (i3 == i10));
      }
      else
      {
        negPrefixPattern = localStringBuffer1.toString();
        negSuffixPattern = localStringBuffer2.toString();
        j = 1;
      }
    }
    if (paramString.length() == 0)
    {
      posPrefixPattern = (posSuffixPattern = "");
      setMinimumIntegerDigits(0);
      setMaximumIntegerDigits(Integer.MAX_VALUE);
      setMinimumFractionDigits(0);
      setMaximumFractionDigits(Integer.MAX_VALUE);
    }
    if ((j == 0) || ((negPrefixPattern.equals(posPrefixPattern)) && (negSuffixPattern.equals(posSuffixPattern))))
    {
      negSuffixPattern = posSuffixPattern;
      negPrefixPattern = ("'-" + posPrefixPattern);
    }
    expandAffixes();
  }
  
  public void setMaximumIntegerDigits(int paramInt)
  {
    maximumIntegerDigits = Math.min(Math.max(0, paramInt), Integer.MAX_VALUE);
    super.setMaximumIntegerDigits(maximumIntegerDigits > 309 ? 309 : maximumIntegerDigits);
    if (minimumIntegerDigits > maximumIntegerDigits)
    {
      minimumIntegerDigits = maximumIntegerDigits;
      super.setMinimumIntegerDigits(minimumIntegerDigits > 309 ? 309 : minimumIntegerDigits);
    }
    fastPathCheckNeeded = true;
  }
  
  public void setMinimumIntegerDigits(int paramInt)
  {
    minimumIntegerDigits = Math.min(Math.max(0, paramInt), Integer.MAX_VALUE);
    super.setMinimumIntegerDigits(minimumIntegerDigits > 309 ? 309 : minimumIntegerDigits);
    if (minimumIntegerDigits > maximumIntegerDigits)
    {
      maximumIntegerDigits = minimumIntegerDigits;
      super.setMaximumIntegerDigits(maximumIntegerDigits > 309 ? 309 : maximumIntegerDigits);
    }
    fastPathCheckNeeded = true;
  }
  
  public void setMaximumFractionDigits(int paramInt)
  {
    maximumFractionDigits = Math.min(Math.max(0, paramInt), Integer.MAX_VALUE);
    super.setMaximumFractionDigits(maximumFractionDigits > 340 ? 340 : maximumFractionDigits);
    if (minimumFractionDigits > maximumFractionDigits)
    {
      minimumFractionDigits = maximumFractionDigits;
      super.setMinimumFractionDigits(minimumFractionDigits > 340 ? 340 : minimumFractionDigits);
    }
    fastPathCheckNeeded = true;
  }
  
  public void setMinimumFractionDigits(int paramInt)
  {
    minimumFractionDigits = Math.min(Math.max(0, paramInt), Integer.MAX_VALUE);
    super.setMinimumFractionDigits(minimumFractionDigits > 340 ? 340 : minimumFractionDigits);
    if (minimumFractionDigits > maximumFractionDigits)
    {
      maximumFractionDigits = minimumFractionDigits;
      super.setMaximumFractionDigits(maximumFractionDigits > 340 ? 340 : maximumFractionDigits);
    }
    fastPathCheckNeeded = true;
  }
  
  public int getMaximumIntegerDigits()
  {
    return maximumIntegerDigits;
  }
  
  public int getMinimumIntegerDigits()
  {
    return minimumIntegerDigits;
  }
  
  public int getMaximumFractionDigits()
  {
    return maximumFractionDigits;
  }
  
  public int getMinimumFractionDigits()
  {
    return minimumFractionDigits;
  }
  
  public Currency getCurrency()
  {
    return symbols.getCurrency();
  }
  
  public void setCurrency(Currency paramCurrency)
  {
    if (paramCurrency != symbols.getCurrency())
    {
      symbols.setCurrency(paramCurrency);
      if (isCurrencyFormat) {
        expandAffixes();
      }
    }
    fastPathCheckNeeded = true;
  }
  
  public RoundingMode getRoundingMode()
  {
    return roundingMode;
  }
  
  public void setRoundingMode(RoundingMode paramRoundingMode)
  {
    if (paramRoundingMode == null) {
      throw new NullPointerException();
    }
    roundingMode = paramRoundingMode;
    digitList.setRoundingMode(paramRoundingMode);
    fastPathCheckNeeded = true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    digitList = new DigitList();
    fastPathCheckNeeded = true;
    isFastPath = false;
    fastPathData = null;
    if (serialVersionOnStream < 4) {
      setRoundingMode(RoundingMode.HALF_EVEN);
    } else {
      setRoundingMode(getRoundingMode());
    }
    if ((super.getMaximumIntegerDigits() > 309) || (super.getMaximumFractionDigits() > 340)) {
      throw new InvalidObjectException("Digit count out of range");
    }
    if (serialVersionOnStream < 3)
    {
      setMaximumIntegerDigits(super.getMaximumIntegerDigits());
      setMinimumIntegerDigits(super.getMinimumIntegerDigits());
      setMaximumFractionDigits(super.getMaximumFractionDigits());
      setMinimumFractionDigits(super.getMinimumFractionDigits());
    }
    if (serialVersionOnStream < 1) {
      useExponentialNotation = false;
    }
    serialVersionOnStream = 4;
  }
  
  private static class DigitArrays
  {
    static final char[] DigitOnes1000 = new char['Ϩ'];
    static final char[] DigitTens1000 = new char['Ϩ'];
    static final char[] DigitHundreds1000 = new char['Ϩ'];
    
    private DigitArrays() {}
    
    static
    {
      int i = 0;
      int j = 0;
      int k = 48;
      int m = 48;
      int n = 48;
      for (int i1 = 0; i1 < 1000; i1++)
      {
        DigitOnes1000[i1] = k;
        if (k == 57) {
          k = 48;
        } else {
          k = (char)(k + 1);
        }
        DigitTens1000[i1] = m;
        if (i1 == i + 9)
        {
          i += 10;
          if (m == 57) {
            m = 48;
          } else {
            m = (char)(m + 1);
          }
        }
        DigitHundreds1000[i1] = n;
        if (i1 == j + 99)
        {
          n = (char)(n + 1);
          j += 100;
        }
      }
    }
  }
  
  private static class FastPathData
  {
    int lastFreeIndex;
    int firstUsedIndex;
    int zeroDelta;
    char groupingChar;
    int integralLastIndex;
    int fractionalFirstIndex;
    double fractionalScaleFactor;
    int fractionalMaxIntBound;
    char[] fastPathContainer;
    char[] charsPositivePrefix;
    char[] charsNegativePrefix;
    char[] charsPositiveSuffix;
    char[] charsNegativeSuffix;
    boolean positiveAffixesRequired = true;
    boolean negativeAffixesRequired = true;
    
    private FastPathData() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\DecimalFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.CurrencyNameProvider;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.LocaleServiceProviderPool.LocalizedObjectGetter;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public final class Currency
  implements Serializable
{
  private static final long serialVersionUID = -158308464356906721L;
  private final String currencyCode;
  private final transient int defaultFractionDigits;
  private final transient int numericCode;
  private static ConcurrentMap<String, Currency> instances = new ConcurrentHashMap(7);
  private static HashSet<Currency> available;
  static int formatVersion;
  static int dataVersion;
  static int[] mainTable;
  static long[] scCutOverTimes;
  static String[] scOldCurrencies;
  static String[] scNewCurrencies;
  static int[] scOldCurrenciesDFD;
  static int[] scNewCurrenciesDFD;
  static int[] scOldCurrenciesNumericCode;
  static int[] scNewCurrenciesNumericCode;
  static String otherCurrencies;
  static int[] otherCurrenciesDFD;
  static int[] otherCurrenciesNumericCode;
  private static final int MAGIC_NUMBER = 1131770436;
  private static final int A_TO_Z = 26;
  private static final int INVALID_COUNTRY_ENTRY = 127;
  private static final int COUNTRY_WITHOUT_CURRENCY_ENTRY = 512;
  private static final int SIMPLE_CASE_COUNTRY_MASK = 0;
  private static final int SIMPLE_CASE_COUNTRY_FINAL_CHAR_MASK = 31;
  private static final int SIMPLE_CASE_COUNTRY_DEFAULT_DIGITS_MASK = 480;
  private static final int SIMPLE_CASE_COUNTRY_DEFAULT_DIGITS_SHIFT = 5;
  private static final int SIMPLE_CASE_COUNTRY_MAX_DEFAULT_DIGITS = 9;
  private static final int SPECIAL_CASE_COUNTRY_MASK = 512;
  private static final int SPECIAL_CASE_COUNTRY_INDEX_MASK = 31;
  private static final int SPECIAL_CASE_COUNTRY_INDEX_DELTA = 1;
  private static final int COUNTRY_TYPE_MASK = 512;
  private static final int NUMERIC_CODE_MASK = 1047552;
  private static final int NUMERIC_CODE_SHIFT = 10;
  private static final int VALID_FORMAT_VERSION = 2;
  private static final int SYMBOL = 0;
  private static final int DISPLAYNAME = 1;
  
  private Currency(String paramString, int paramInt1, int paramInt2)
  {
    currencyCode = paramString;
    defaultFractionDigits = paramInt1;
    numericCode = paramInt2;
  }
  
  public static Currency getInstance(String paramString)
  {
    return getInstance(paramString, Integer.MIN_VALUE, 0);
  }
  
  private static Currency getInstance(String paramString, int paramInt1, int paramInt2)
  {
    Currency localCurrency1 = (Currency)instances.get(paramString);
    if (localCurrency1 != null) {
      return localCurrency1;
    }
    if (paramInt1 == Integer.MIN_VALUE)
    {
      if (paramString.length() != 3) {
        throw new IllegalArgumentException();
      }
      char c1 = paramString.charAt(0);
      char c2 = paramString.charAt(1);
      int i = getMainTableEntry(c1, c2);
      if (((i & 0x200) == 0) && (i != 127) && (paramString.charAt(2) - 'A' == (i & 0x1F)))
      {
        paramInt1 = (i & 0x1E0) >> 5;
        paramInt2 = (i & 0xFFC00) >> 10;
      }
      else
      {
        if (paramString.charAt(2) == '-') {
          throw new IllegalArgumentException();
        }
        int j = otherCurrencies.indexOf(paramString);
        if (j == -1) {
          throw new IllegalArgumentException();
        }
        paramInt1 = otherCurrenciesDFD[(j / 4)];
        paramInt2 = otherCurrenciesNumericCode[(j / 4)];
      }
    }
    Currency localCurrency2 = new Currency(paramString, paramInt1, paramInt2);
    localCurrency1 = (Currency)instances.putIfAbsent(paramString, localCurrency2);
    return localCurrency1 != null ? localCurrency1 : localCurrency2;
  }
  
  public static Currency getInstance(Locale paramLocale)
  {
    String str = paramLocale.getCountry();
    if (str == null) {
      throw new NullPointerException();
    }
    if (str.length() != 2) {
      throw new IllegalArgumentException();
    }
    char c1 = str.charAt(0);
    char c2 = str.charAt(1);
    int i = getMainTableEntry(c1, c2);
    if (((i & 0x200) == 0) && (i != 127))
    {
      char c3 = (char)((i & 0x1F) + 65);
      int k = (i & 0x1E0) >> 5;
      int m = (i & 0xFFC00) >> 10;
      StringBuilder localStringBuilder = new StringBuilder(str);
      localStringBuilder.append(c3);
      return getInstance(localStringBuilder.toString(), k, m);
    }
    if (i == 127) {
      throw new IllegalArgumentException();
    }
    if (i == 512) {
      return null;
    }
    int j = (i & 0x1F) - 1;
    if ((scCutOverTimes[j] == Long.MAX_VALUE) || (System.currentTimeMillis() < scCutOverTimes[j])) {
      return getInstance(scOldCurrencies[j], scOldCurrenciesDFD[j], scOldCurrenciesNumericCode[j]);
    }
    return getInstance(scNewCurrencies[j], scNewCurrenciesDFD[j], scNewCurrenciesNumericCode[j]);
  }
  
  public static Set<Currency> getAvailableCurrencies()
  {
    synchronized (Currency.class)
    {
      if (available == null)
      {
        available = new HashSet(256);
        for (char c1 = 'A'; c1 <= 'Z'; c1 = (char)(c1 + '\001')) {
          for (char c2 = 'A'; c2 <= 'Z'; c2 = (char)(c2 + '\001'))
          {
            int i = getMainTableEntry(c1, c2);
            if (((i & 0x200) == 0) && (i != 127))
            {
              char c3 = (char)((i & 0x1F) + 65);
              int j = (i & 0x1E0) >> 5;
              int k = (i & 0xFFC00) >> 10;
              StringBuilder localStringBuilder = new StringBuilder();
              localStringBuilder.append(c1);
              localStringBuilder.append(c2);
              localStringBuilder.append(c3);
              available.add(getInstance(localStringBuilder.toString(), j, k));
            }
          }
        }
        StringTokenizer localStringTokenizer = new StringTokenizer(otherCurrencies, "-");
        while (localStringTokenizer.hasMoreElements()) {
          available.add(getInstance((String)localStringTokenizer.nextElement()));
        }
      }
    }
    ??? = (Set)available.clone();
    return (Set<Currency>)???;
  }
  
  public String getCurrencyCode()
  {
    return currencyCode;
  }
  
  public String getSymbol()
  {
    return getSymbol(Locale.getDefault(Locale.Category.DISPLAY));
  }
  
  public String getSymbol(Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CurrencyNameProvider.class);
    String str = (String)localLocaleServiceProviderPool.getLocalizedObject(CurrencyNameGetter.INSTANCE, paramLocale, currencyCode, new Object[] { Integer.valueOf(0) });
    if (str != null) {
      return str;
    }
    return currencyCode;
  }
  
  public int getDefaultFractionDigits()
  {
    return defaultFractionDigits;
  }
  
  public int getNumericCode()
  {
    return numericCode;
  }
  
  public String getDisplayName()
  {
    return getDisplayName(Locale.getDefault(Locale.Category.DISPLAY));
  }
  
  public String getDisplayName(Locale paramLocale)
  {
    LocaleServiceProviderPool localLocaleServiceProviderPool = LocaleServiceProviderPool.getPool(CurrencyNameProvider.class);
    String str = (String)localLocaleServiceProviderPool.getLocalizedObject(CurrencyNameGetter.INSTANCE, paramLocale, currencyCode, new Object[] { Integer.valueOf(1) });
    if (str != null) {
      return str;
    }
    return currencyCode;
  }
  
  public String toString()
  {
    return currencyCode;
  }
  
  private Object readResolve()
  {
    return getInstance(currencyCode);
  }
  
  private static int getMainTableEntry(char paramChar1, char paramChar2)
  {
    if ((paramChar1 < 'A') || (paramChar1 > 'Z') || (paramChar2 < 'A') || (paramChar2 > 'Z')) {
      throw new IllegalArgumentException();
    }
    return mainTable[((paramChar1 - 'A') * 26 + (paramChar2 - 'A'))];
  }
  
  private static void setMainTableEntry(char paramChar1, char paramChar2, int paramInt)
  {
    if ((paramChar1 < 'A') || (paramChar1 > 'Z') || (paramChar2 < 'A') || (paramChar2 > 'Z')) {
      throw new IllegalArgumentException();
    }
    mainTable[((paramChar1 - 'A') * 26 + (paramChar2 - 'A'))] = paramInt;
  }
  
  private static int[] readIntArray(DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    int[] arrayOfInt = new int[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfInt[i] = paramDataInputStream.readInt();
    }
    return arrayOfInt;
  }
  
  private static long[] readLongArray(DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    long[] arrayOfLong = new long[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfLong[i] = paramDataInputStream.readLong();
    }
    return arrayOfLong;
  }
  
  private static String[] readStringArray(DataInputStream paramDataInputStream, int paramInt)
    throws IOException
  {
    String[] arrayOfString = new String[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfString[i] = paramDataInputStream.readUTF();
    }
    return arrayOfString;
  }
  
  private static void replaceCurrencyData(Pattern paramPattern, String paramString1, String paramString2)
  {
    if (paramString1.length() != 2)
    {
      info("currency.properties entry for " + paramString1 + " is ignored because of the invalid country code.", null);
      return;
    }
    Matcher localMatcher = paramPattern.matcher(paramString2);
    if ((!localMatcher.find()) || ((localMatcher.group(4) == null) && (countOccurrences(paramString2, ',') >= 3)))
    {
      info("currency.properties entry for " + paramString1 + " ignored because the value format is not recognized.", null);
      return;
    }
    try
    {
      if ((localMatcher.group(4) != null) && (!isPastCutoverDate(localMatcher.group(4))))
      {
        info("currency.properties entry for " + paramString1 + " ignored since cutover date has not passed :" + paramString2, null);
        return;
      }
    }
    catch (ParseException localParseException)
    {
      info("currency.properties entry for " + paramString1 + " ignored since exception encountered :" + localParseException.getMessage(), null);
      return;
    }
    String str = localMatcher.group(1);
    int i = Integer.parseInt(localMatcher.group(2));
    int j = i << 10;
    int k = Integer.parseInt(localMatcher.group(3));
    if (k > 9)
    {
      info("currency.properties entry for " + paramString1 + " ignored since the fraction is more than " + 9 + ":" + paramString2, null);
      return;
    }
    for (int m = 0; (m < scOldCurrencies.length) && (!scOldCurrencies[m].equals(str)); m++) {}
    if (m == scOldCurrencies.length) {
      j |= k << 5 | str.charAt(2) - 'A';
    } else {
      j |= 0x200 | m + 1;
    }
    setMainTableEntry(paramString1.charAt(0), paramString1.charAt(1), j);
  }
  
  private static boolean isPastCutoverDate(String paramString)
    throws ParseException
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
    localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    localSimpleDateFormat.setLenient(false);
    long l = localSimpleDateFormat.parse(paramString.trim()).getTime();
    return System.currentTimeMillis() > l;
  }
  
  private static int countOccurrences(String paramString, char paramChar)
  {
    int i = 0;
    for (char c : paramString.toCharArray()) {
      if (c == paramChar) {
        i++;
      }
    }
    return i;
  }
  
  private static void info(String paramString, Throwable paramThrowable)
  {
    PlatformLogger localPlatformLogger = PlatformLogger.getLogger("java.util.Currency");
    if (localPlatformLogger.isLoggable(PlatformLogger.Level.INFO)) {
      if (paramThrowable != null) {
        localPlatformLogger.info(paramString, paramThrowable);
      } else {
        localPlatformLogger.info(paramString);
      }
    }
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        String str1 = System.getProperty("java.home");
        Object localObject1;
        Object localObject2;
        try
        {
          String str2 = str1 + File.separator + "lib" + File.separator + "currency.data";
          localObject1 = new DataInputStream(new BufferedInputStream(new FileInputStream(str2)));
          localObject2 = null;
          try
          {
            if (((DataInputStream)localObject1).readInt() != 1131770436) {
              throw new InternalError("Currency data is possibly corrupted");
            }
            Currency.formatVersion = ((DataInputStream)localObject1).readInt();
            if (Currency.formatVersion != 2) {
              throw new InternalError("Currency data format is incorrect");
            }
            Currency.dataVersion = ((DataInputStream)localObject1).readInt();
            Currency.mainTable = Currency.readIntArray((DataInputStream)localObject1, 676);
            int i = ((DataInputStream)localObject1).readInt();
            Currency.scCutOverTimes = Currency.readLongArray((DataInputStream)localObject1, i);
            Currency.scOldCurrencies = Currency.readStringArray((DataInputStream)localObject1, i);
            Currency.scNewCurrencies = Currency.readStringArray((DataInputStream)localObject1, i);
            Currency.scOldCurrenciesDFD = Currency.readIntArray((DataInputStream)localObject1, i);
            Currency.scNewCurrenciesDFD = Currency.readIntArray((DataInputStream)localObject1, i);
            Currency.scOldCurrenciesNumericCode = Currency.readIntArray((DataInputStream)localObject1, i);
            Currency.scNewCurrenciesNumericCode = Currency.readIntArray((DataInputStream)localObject1, i);
            int j = ((DataInputStream)localObject1).readInt();
            Currency.otherCurrencies = ((DataInputStream)localObject1).readUTF();
            Currency.otherCurrenciesDFD = Currency.readIntArray((DataInputStream)localObject1, j);
            Currency.otherCurrenciesNumericCode = Currency.readIntArray((DataInputStream)localObject1, j);
          }
          catch (Throwable localThrowable2)
          {
            localObject2 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localObject1 != null) {
              if (localObject2 != null) {
                try
                {
                  ((DataInputStream)localObject1).close();
                }
                catch (Throwable localThrowable5)
                {
                  ((Throwable)localObject2).addSuppressed(localThrowable5);
                }
              } else {
                ((DataInputStream)localObject1).close();
              }
            }
          }
        }
        catch (IOException localIOException1)
        {
          throw new InternalError(localIOException1);
        }
        String str3 = System.getProperty("java.util.currency.data");
        if (str3 == null) {
          str3 = str1 + File.separator + "lib" + File.separator + "currency.properties";
        }
        try
        {
          localObject1 = new File(str3);
          if (((File)localObject1).exists())
          {
            localObject2 = new Properties();
            Object localObject3 = new FileReader((File)localObject1);
            Object localObject4 = null;
            try
            {
              ((Properties)localObject2).load((Reader)localObject3);
            }
            catch (Throwable localThrowable4)
            {
              localObject4 = localThrowable4;
              throw localThrowable4;
            }
            finally
            {
              if (localObject3 != null) {
                if (localObject4 != null) {
                  try
                  {
                    ((FileReader)localObject3).close();
                  }
                  catch (Throwable localThrowable6)
                  {
                    ((Throwable)localObject4).addSuppressed(localThrowable6);
                  }
                } else {
                  ((FileReader)localObject3).close();
                }
              }
            }
            localObject3 = ((Properties)localObject2).stringPropertyNames();
            localObject4 = Pattern.compile("([A-Z]{3})\\s*,\\s*(\\d{3})\\s*,\\s*(\\d+)\\s*,?\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})?");
            Iterator localIterator = ((Set)localObject3).iterator();
            while (localIterator.hasNext())
            {
              String str4 = (String)localIterator.next();
              Currency.replaceCurrencyData((Pattern)localObject4, str4.toUpperCase(Locale.ROOT), ((Properties)localObject2).getProperty(str4).toUpperCase(Locale.ROOT));
            }
          }
        }
        catch (IOException localIOException2)
        {
          Currency.info("currency.properties is ignored because of an IOException", localIOException2);
        }
        return null;
      }
    });
  }
  
  private static class CurrencyNameGetter
    implements LocaleServiceProviderPool.LocalizedObjectGetter<CurrencyNameProvider, String>
  {
    private static final CurrencyNameGetter INSTANCE = new CurrencyNameGetter();
    
    private CurrencyNameGetter() {}
    
    public String getObject(CurrencyNameProvider paramCurrencyNameProvider, Locale paramLocale, String paramString, Object... paramVarArgs)
    {
      assert (paramVarArgs.length == 1);
      int i = ((Integer)paramVarArgs[0]).intValue();
      switch (i)
      {
      case 0: 
        return paramCurrencyNameProvider.getSymbol(paramString, paramLocale);
      case 1: 
        return paramCurrencyNameProvider.getDisplayName(paramString, paramLocale);
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Currency.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.zone.ZoneRules;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.FormattedFloatingDecimal;
import sun.misc.FormattedFloatingDecimal.Form;

public final class Formatter
  implements Closeable, Flushable
{
  private Appendable a;
  private final Locale l;
  private IOException lastException;
  private final char zero;
  private static double scaleUp;
  private static final int MAX_FD_CHARS = 30;
  private static final String formatSpecifier = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
  private static Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");
  
  private static Charset toCharset(String paramString)
    throws UnsupportedEncodingException
  {
    Objects.requireNonNull(paramString, "charsetName");
    try
    {
      return Charset.forName(paramString);
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new UnsupportedEncodingException(paramString);
    }
  }
  
  private static final Appendable nonNullAppendable(Appendable paramAppendable)
  {
    if (paramAppendable == null) {
      return new StringBuilder();
    }
    return paramAppendable;
  }
  
  private Formatter(Locale paramLocale, Appendable paramAppendable)
  {
    a = paramAppendable;
    l = paramLocale;
    zero = getZero(paramLocale);
  }
  
  private Formatter(Charset paramCharset, Locale paramLocale, File paramFile)
    throws FileNotFoundException
  {
    this(paramLocale, new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile), paramCharset)));
  }
  
  public Formatter()
  {
    this(Locale.getDefault(Locale.Category.FORMAT), new StringBuilder());
  }
  
  public Formatter(Appendable paramAppendable)
  {
    this(Locale.getDefault(Locale.Category.FORMAT), nonNullAppendable(paramAppendable));
  }
  
  public Formatter(Locale paramLocale)
  {
    this(paramLocale, new StringBuilder());
  }
  
  public Formatter(Appendable paramAppendable, Locale paramLocale)
  {
    this(paramLocale, nonNullAppendable(paramAppendable));
  }
  
  public Formatter(String paramString)
    throws FileNotFoundException
  {
    this(Locale.getDefault(Locale.Category.FORMAT), new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramString))));
  }
  
  public Formatter(String paramString1, String paramString2)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(paramString1, paramString2, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public Formatter(String paramString1, String paramString2, Locale paramLocale)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(toCharset(paramString2), paramLocale, new File(paramString1));
  }
  
  public Formatter(File paramFile)
    throws FileNotFoundException
  {
    this(Locale.getDefault(Locale.Category.FORMAT), new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile))));
  }
  
  public Formatter(File paramFile, String paramString)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(paramFile, paramString, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public Formatter(File paramFile, String paramString, Locale paramLocale)
    throws FileNotFoundException, UnsupportedEncodingException
  {
    this(toCharset(paramString), paramLocale, paramFile);
  }
  
  public Formatter(PrintStream paramPrintStream)
  {
    this(Locale.getDefault(Locale.Category.FORMAT), (Appendable)Objects.requireNonNull(paramPrintStream));
  }
  
  public Formatter(OutputStream paramOutputStream)
  {
    this(Locale.getDefault(Locale.Category.FORMAT), new BufferedWriter(new OutputStreamWriter(paramOutputStream)));
  }
  
  public Formatter(OutputStream paramOutputStream, String paramString)
    throws UnsupportedEncodingException
  {
    this(paramOutputStream, paramString, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public Formatter(OutputStream paramOutputStream, String paramString, Locale paramLocale)
    throws UnsupportedEncodingException
  {
    this(paramLocale, new BufferedWriter(new OutputStreamWriter(paramOutputStream, paramString)));
  }
  
  private static char getZero(Locale paramLocale)
  {
    if ((paramLocale != null) && (!paramLocale.equals(Locale.US)))
    {
      DecimalFormatSymbols localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
      return localDecimalFormatSymbols.getZeroDigit();
    }
    return '0';
  }
  
  public Locale locale()
  {
    ensureOpen();
    return l;
  }
  
  public Appendable out()
  {
    ensureOpen();
    return a;
  }
  
  public String toString()
  {
    ensureOpen();
    return a.toString();
  }
  
  public void flush()
  {
    ensureOpen();
    if ((a instanceof Flushable)) {
      try
      {
        ((Flushable)a).flush();
      }
      catch (IOException localIOException)
      {
        lastException = localIOException;
      }
    }
  }
  
  public void close()
  {
    if (a == null) {
      return;
    }
    try
    {
      if ((a instanceof Closeable)) {
        ((Closeable)a).close();
      }
    }
    catch (IOException localIOException)
    {
      lastException = localIOException;
    }
    finally
    {
      a = null;
    }
  }
  
  private void ensureOpen()
  {
    if (a == null) {
      throw new FormatterClosedException();
    }
  }
  
  public IOException ioException()
  {
    return lastException;
  }
  
  public Formatter format(String paramString, Object... paramVarArgs)
  {
    return format(l, paramString, paramVarArgs);
  }
  
  public Formatter format(Locale paramLocale, String paramString, Object... paramVarArgs)
  {
    ensureOpen();
    int i = -1;
    int j = -1;
    FormatString[] arrayOfFormatString = parse(paramString);
    for (int k = 0; k < arrayOfFormatString.length; k++)
    {
      FormatString localFormatString = arrayOfFormatString[k];
      int m = localFormatString.index();
      try
      {
        switch (m)
        {
        case -2: 
          localFormatString.print(null, paramLocale);
          break;
        case -1: 
          if ((i < 0) || ((paramVarArgs != null) && (i > paramVarArgs.length - 1))) {
            throw new MissingFormatArgumentException(localFormatString.toString());
          }
          localFormatString.print(paramVarArgs == null ? null : paramVarArgs[i], paramLocale);
          break;
        case 0: 
          j++;
          i = j;
          if ((paramVarArgs != null) && (j > paramVarArgs.length - 1)) {
            throw new MissingFormatArgumentException(localFormatString.toString());
          }
          localFormatString.print(paramVarArgs == null ? null : paramVarArgs[j], paramLocale);
          break;
        default: 
          i = m - 1;
          if ((paramVarArgs != null) && (i > paramVarArgs.length - 1)) {
            throw new MissingFormatArgumentException(localFormatString.toString());
          }
          localFormatString.print(paramVarArgs == null ? null : paramVarArgs[i], paramLocale);
        }
      }
      catch (IOException localIOException)
      {
        lastException = localIOException;
      }
    }
    return this;
  }
  
  private FormatString[] parse(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Matcher localMatcher = fsPattern.matcher(paramString);
    int i = 0;
    int j = paramString.length();
    while (i < j) {
      if (localMatcher.find(i))
      {
        if (localMatcher.start() != i)
        {
          checkText(paramString, i, localMatcher.start());
          localArrayList.add(new FixedString(paramString.substring(i, localMatcher.start())));
        }
        localArrayList.add(new FormatSpecifier(localMatcher));
        i = localMatcher.end();
      }
      else
      {
        checkText(paramString, i, j);
        localArrayList.add(new FixedString(paramString.substring(i)));
      }
    }
    return (FormatString[])localArrayList.toArray(new FormatString[localArrayList.size()]);
  }
  
  private static void checkText(String paramString, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramString.charAt(i) == '%')
      {
        char c = i == paramInt2 - 1 ? '%' : paramString.charAt(i + 1);
        throw new UnknownFormatConversionException(String.valueOf(c));
      }
    }
  }
  
  public static enum BigDecimalLayoutForm
  {
    SCIENTIFIC,  DECIMAL_FLOAT;
    
    private BigDecimalLayoutForm() {}
  }
  
  private static class Conversion
  {
    static final char DECIMAL_INTEGER = 'd';
    static final char OCTAL_INTEGER = 'o';
    static final char HEXADECIMAL_INTEGER = 'x';
    static final char HEXADECIMAL_INTEGER_UPPER = 'X';
    static final char SCIENTIFIC = 'e';
    static final char SCIENTIFIC_UPPER = 'E';
    static final char GENERAL = 'g';
    static final char GENERAL_UPPER = 'G';
    static final char DECIMAL_FLOAT = 'f';
    static final char HEXADECIMAL_FLOAT = 'a';
    static final char HEXADECIMAL_FLOAT_UPPER = 'A';
    static final char CHARACTER = 'c';
    static final char CHARACTER_UPPER = 'C';
    static final char DATE_TIME = 't';
    static final char DATE_TIME_UPPER = 'T';
    static final char BOOLEAN = 'b';
    static final char BOOLEAN_UPPER = 'B';
    static final char STRING = 's';
    static final char STRING_UPPER = 'S';
    static final char HASHCODE = 'h';
    static final char HASHCODE_UPPER = 'H';
    static final char LINE_SEPARATOR = 'n';
    static final char PERCENT_SIGN = '%';
    
    private Conversion() {}
    
    static boolean isValid(char paramChar)
    {
      return (isGeneral(paramChar)) || (isInteger(paramChar)) || (isFloat(paramChar)) || (isText(paramChar)) || (paramChar == 't') || (isCharacter(paramChar));
    }
    
    static boolean isGeneral(char paramChar)
    {
      switch (paramChar)
      {
      case 'B': 
      case 'H': 
      case 'S': 
      case 'b': 
      case 'h': 
      case 's': 
        return true;
      }
      return false;
    }
    
    static boolean isCharacter(char paramChar)
    {
      switch (paramChar)
      {
      case 'C': 
      case 'c': 
        return true;
      }
      return false;
    }
    
    static boolean isInteger(char paramChar)
    {
      switch (paramChar)
      {
      case 'X': 
      case 'd': 
      case 'o': 
      case 'x': 
        return true;
      }
      return false;
    }
    
    static boolean isFloat(char paramChar)
    {
      switch (paramChar)
      {
      case 'A': 
      case 'E': 
      case 'G': 
      case 'a': 
      case 'e': 
      case 'f': 
      case 'g': 
        return true;
      }
      return false;
    }
    
    static boolean isText(char paramChar)
    {
      switch (paramChar)
      {
      case '%': 
      case 'n': 
        return true;
      }
      return false;
    }
  }
  
  private static class DateTime
  {
    static final char HOUR_OF_DAY_0 = 'H';
    static final char HOUR_0 = 'I';
    static final char HOUR_OF_DAY = 'k';
    static final char HOUR = 'l';
    static final char MINUTE = 'M';
    static final char NANOSECOND = 'N';
    static final char MILLISECOND = 'L';
    static final char MILLISECOND_SINCE_EPOCH = 'Q';
    static final char AM_PM = 'p';
    static final char SECONDS_SINCE_EPOCH = 's';
    static final char SECOND = 'S';
    static final char TIME = 'T';
    static final char ZONE_NUMERIC = 'z';
    static final char ZONE = 'Z';
    static final char NAME_OF_DAY_ABBREV = 'a';
    static final char NAME_OF_DAY = 'A';
    static final char NAME_OF_MONTH_ABBREV = 'b';
    static final char NAME_OF_MONTH = 'B';
    static final char CENTURY = 'C';
    static final char DAY_OF_MONTH_0 = 'd';
    static final char DAY_OF_MONTH = 'e';
    static final char NAME_OF_MONTH_ABBREV_X = 'h';
    static final char DAY_OF_YEAR = 'j';
    static final char MONTH = 'm';
    static final char YEAR_2 = 'y';
    static final char YEAR_4 = 'Y';
    static final char TIME_12_HOUR = 'r';
    static final char TIME_24_HOUR = 'R';
    static final char DATE_TIME = 'c';
    static final char DATE = 'D';
    static final char ISO_STANDARD_DATE = 'F';
    
    private DateTime() {}
    
    static boolean isValid(char paramChar)
    {
      switch (paramChar)
      {
      case 'A': 
      case 'B': 
      case 'C': 
      case 'D': 
      case 'F': 
      case 'H': 
      case 'I': 
      case 'L': 
      case 'M': 
      case 'N': 
      case 'Q': 
      case 'R': 
      case 'S': 
      case 'T': 
      case 'Y': 
      case 'Z': 
      case 'a': 
      case 'b': 
      case 'c': 
      case 'd': 
      case 'e': 
      case 'h': 
      case 'j': 
      case 'k': 
      case 'l': 
      case 'm': 
      case 'p': 
      case 'r': 
      case 's': 
      case 'y': 
      case 'z': 
        return true;
      }
      return false;
    }
  }
  
  private class FixedString
    implements Formatter.FormatString
  {
    private String s;
    
    FixedString(String paramString)
    {
      s = paramString;
    }
    
    public int index()
    {
      return -2;
    }
    
    public void print(Object paramObject, Locale paramLocale)
      throws IOException
    {
      a.append(s);
    }
    
    public String toString()
    {
      return s;
    }
  }
  
  private static class Flags
  {
    private int flags;
    static final Flags NONE = new Flags(0);
    static final Flags LEFT_JUSTIFY = new Flags(1);
    static final Flags UPPERCASE = new Flags(2);
    static final Flags ALTERNATE = new Flags(4);
    static final Flags PLUS = new Flags(8);
    static final Flags LEADING_SPACE = new Flags(16);
    static final Flags ZERO_PAD = new Flags(32);
    static final Flags GROUP = new Flags(64);
    static final Flags PARENTHESES = new Flags(128);
    static final Flags PREVIOUS = new Flags(256);
    
    private Flags(int paramInt)
    {
      flags = paramInt;
    }
    
    public int valueOf()
    {
      return flags;
    }
    
    public boolean contains(Flags paramFlags)
    {
      return (flags & paramFlags.valueOf()) == paramFlags.valueOf();
    }
    
    public Flags dup()
    {
      return new Flags(flags);
    }
    
    private Flags add(Flags paramFlags)
    {
      flags |= paramFlags.valueOf();
      return this;
    }
    
    public Flags remove(Flags paramFlags)
    {
      flags &= (paramFlags.valueOf() ^ 0xFFFFFFFF);
      return this;
    }
    
    public static Flags parse(String paramString)
    {
      char[] arrayOfChar = paramString.toCharArray();
      Flags localFlags1 = new Flags(0);
      for (int i = 0; i < arrayOfChar.length; i++)
      {
        Flags localFlags2 = parse(arrayOfChar[i]);
        if (localFlags1.contains(localFlags2)) {
          throw new DuplicateFormatFlagsException(localFlags2.toString());
        }
        localFlags1.add(localFlags2);
      }
      return localFlags1;
    }
    
    private static Flags parse(char paramChar)
    {
      switch (paramChar)
      {
      case '-': 
        return LEFT_JUSTIFY;
      case '#': 
        return ALTERNATE;
      case '+': 
        return PLUS;
      case ' ': 
        return LEADING_SPACE;
      case '0': 
        return ZERO_PAD;
      case ',': 
        return GROUP;
      case '(': 
        return PARENTHESES;
      case '<': 
        return PREVIOUS;
      }
      throw new UnknownFormatFlagsException(String.valueOf(paramChar));
    }
    
    public static String toString(Flags paramFlags)
    {
      return paramFlags.toString();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (contains(LEFT_JUSTIFY)) {
        localStringBuilder.append('-');
      }
      if (contains(UPPERCASE)) {
        localStringBuilder.append('^');
      }
      if (contains(ALTERNATE)) {
        localStringBuilder.append('#');
      }
      if (contains(PLUS)) {
        localStringBuilder.append('+');
      }
      if (contains(LEADING_SPACE)) {
        localStringBuilder.append(' ');
      }
      if (contains(ZERO_PAD)) {
        localStringBuilder.append('0');
      }
      if (contains(GROUP)) {
        localStringBuilder.append(',');
      }
      if (contains(PARENTHESES)) {
        localStringBuilder.append('(');
      }
      if (contains(PREVIOUS)) {
        localStringBuilder.append('<');
      }
      return localStringBuilder.toString();
    }
  }
  
  private class FormatSpecifier
    implements Formatter.FormatString
  {
    private int index = -1;
    private Formatter.Flags f = Formatter.Flags.NONE;
    private int width;
    private int precision;
    private boolean dt = false;
    private char c;
    
    private int index(String paramString)
    {
      if (paramString != null) {
        try
        {
          index = Integer.parseInt(paramString.substring(0, paramString.length() - 1));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      } else {
        index = 0;
      }
      return index;
    }
    
    public int index()
    {
      return index;
    }
    
    private Formatter.Flags flags(String paramString)
    {
      f = Formatter.Flags.parse(paramString);
      if (f.contains(Formatter.Flags.PREVIOUS)) {
        index = -1;
      }
      return f;
    }
    
    Formatter.Flags flags()
    {
      return f;
    }
    
    private int width(String paramString)
    {
      width = -1;
      if (paramString != null) {
        try
        {
          width = Integer.parseInt(paramString);
          if (width < 0) {
            throw new IllegalFormatWidthException(width);
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      }
      return width;
    }
    
    int width()
    {
      return width;
    }
    
    private int precision(String paramString)
    {
      precision = -1;
      if (paramString != null) {
        try
        {
          precision = Integer.parseInt(paramString.substring(1));
          if (precision < 0) {
            throw new IllegalFormatPrecisionException(precision);
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      }
      return precision;
    }
    
    int precision()
    {
      return precision;
    }
    
    private char conversion(String paramString)
    {
      c = paramString.charAt(0);
      if (!dt)
      {
        if (!Formatter.Conversion.isValid(c)) {
          throw new UnknownFormatConversionException(String.valueOf(c));
        }
        if (Character.isUpperCase(c)) {
          f.add(Formatter.Flags.UPPERCASE);
        }
        c = Character.toLowerCase(c);
        if (Formatter.Conversion.isText(c)) {
          index = -2;
        }
      }
      return c;
    }
    
    private char conversion()
    {
      return c;
    }
    
    FormatSpecifier(Matcher paramMatcher)
    {
      int i = 1;
      index(paramMatcher.group(i++));
      flags(paramMatcher.group(i++));
      width(paramMatcher.group(i++));
      precision(paramMatcher.group(i++));
      String str = paramMatcher.group(i++);
      if (str != null)
      {
        dt = true;
        if (str.equals("T")) {
          f.add(Formatter.Flags.UPPERCASE);
        }
      }
      conversion(paramMatcher.group(i));
      if (dt) {
        checkDateTime();
      } else if (Formatter.Conversion.isGeneral(c)) {
        checkGeneral();
      } else if (Formatter.Conversion.isCharacter(c)) {
        checkCharacter();
      } else if (Formatter.Conversion.isInteger(c)) {
        checkInteger();
      } else if (Formatter.Conversion.isFloat(c)) {
        checkFloat();
      } else if (Formatter.Conversion.isText(c)) {
        checkText();
      } else {
        throw new UnknownFormatConversionException(String.valueOf(c));
      }
    }
    
    public void print(Object paramObject, Locale paramLocale)
      throws IOException
    {
      if (dt)
      {
        printDateTime(paramObject, paramLocale);
        return;
      }
      switch (c)
      {
      case 'd': 
      case 'o': 
      case 'x': 
        printInteger(paramObject, paramLocale);
        break;
      case 'a': 
      case 'e': 
      case 'f': 
      case 'g': 
        printFloat(paramObject, paramLocale);
        break;
      case 'C': 
      case 'c': 
        printCharacter(paramObject);
        break;
      case 'b': 
        printBoolean(paramObject);
        break;
      case 's': 
        printString(paramObject, paramLocale);
        break;
      case 'h': 
        printHashCode(paramObject);
        break;
      case 'n': 
        a.append(System.lineSeparator());
        break;
      case '%': 
        a.append('%');
        break;
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
    
    private void printInteger(Object paramObject, Locale paramLocale)
      throws IOException
    {
      if (paramObject == null) {
        print("null");
      } else if ((paramObject instanceof Byte)) {
        print(((Byte)paramObject).byteValue(), paramLocale);
      } else if ((paramObject instanceof Short)) {
        print(((Short)paramObject).shortValue(), paramLocale);
      } else if ((paramObject instanceof Integer)) {
        print(((Integer)paramObject).intValue(), paramLocale);
      } else if ((paramObject instanceof Long)) {
        print(((Long)paramObject).longValue(), paramLocale);
      } else if ((paramObject instanceof BigInteger)) {
        print((BigInteger)paramObject, paramLocale);
      } else {
        failConversion(c, paramObject);
      }
    }
    
    private void printFloat(Object paramObject, Locale paramLocale)
      throws IOException
    {
      if (paramObject == null) {
        print("null");
      } else if ((paramObject instanceof Float)) {
        print(((Float)paramObject).floatValue(), paramLocale);
      } else if ((paramObject instanceof Double)) {
        print(((Double)paramObject).doubleValue(), paramLocale);
      } else if ((paramObject instanceof BigDecimal)) {
        print((BigDecimal)paramObject, paramLocale);
      } else {
        failConversion(c, paramObject);
      }
    }
    
    private void printDateTime(Object paramObject, Locale paramLocale)
      throws IOException
    {
      if (paramObject == null)
      {
        print("null");
        return;
      }
      Calendar localCalendar = null;
      if ((paramObject instanceof Long))
      {
        localCalendar = Calendar.getInstance(paramLocale == null ? Locale.US : paramLocale);
        localCalendar.setTimeInMillis(((Long)paramObject).longValue());
      }
      else if ((paramObject instanceof Date))
      {
        localCalendar = Calendar.getInstance(paramLocale == null ? Locale.US : paramLocale);
        localCalendar.setTime((Date)paramObject);
      }
      else if ((paramObject instanceof Calendar))
      {
        localCalendar = (Calendar)((Calendar)paramObject).clone();
        localCalendar.setLenient(true);
      }
      else
      {
        if ((paramObject instanceof TemporalAccessor))
        {
          print((TemporalAccessor)paramObject, c, paramLocale);
          return;
        }
        failConversion(c, paramObject);
      }
      print(localCalendar, c, paramLocale);
    }
    
    private void printCharacter(Object paramObject)
      throws IOException
    {
      if (paramObject == null)
      {
        print("null");
        return;
      }
      String str = null;
      if ((paramObject instanceof Character))
      {
        str = ((Character)paramObject).toString();
      }
      else
      {
        int i;
        if ((paramObject instanceof Byte))
        {
          i = ((Byte)paramObject).byteValue();
          if (Character.isValidCodePoint(i)) {
            str = new String(Character.toChars(i));
          } else {
            throw new IllegalFormatCodePointException(i);
          }
        }
        else if ((paramObject instanceof Short))
        {
          i = ((Short)paramObject).shortValue();
          if (Character.isValidCodePoint(i)) {
            str = new String(Character.toChars(i));
          } else {
            throw new IllegalFormatCodePointException(i);
          }
        }
        else if ((paramObject instanceof Integer))
        {
          i = ((Integer)paramObject).intValue();
          if (Character.isValidCodePoint(i)) {
            str = new String(Character.toChars(i));
          } else {
            throw new IllegalFormatCodePointException(i);
          }
        }
        else
        {
          failConversion(c, paramObject);
        }
      }
      print(str);
    }
    
    private void printString(Object paramObject, Locale paramLocale)
      throws IOException
    {
      if ((paramObject instanceof Formattable))
      {
        Formatter localFormatter = Formatter.this;
        if (localFormatter.locale() != paramLocale) {
          localFormatter = new Formatter(localFormatter.out(), paramLocale);
        }
        ((Formattable)paramObject).formatTo(localFormatter, f.valueOf(), width, precision);
      }
      else
      {
        if (f.contains(Formatter.Flags.ALTERNATE)) {
          failMismatch(Formatter.Flags.ALTERNATE, 's');
        }
        if (paramObject == null) {
          print("null");
        } else {
          print(paramObject.toString());
        }
      }
    }
    
    private void printBoolean(Object paramObject)
      throws IOException
    {
      String str;
      if (paramObject != null) {
        str = (paramObject instanceof Boolean) ? ((Boolean)paramObject).toString() : Boolean.toString(true);
      } else {
        str = Boolean.toString(false);
      }
      print(str);
    }
    
    private void printHashCode(Object paramObject)
      throws IOException
    {
      String str = paramObject == null ? "null" : Integer.toHexString(paramObject.hashCode());
      print(str);
    }
    
    private void print(String paramString)
      throws IOException
    {
      if ((precision != -1) && (precision < paramString.length())) {
        paramString = paramString.substring(0, precision);
      }
      if (f.contains(Formatter.Flags.UPPERCASE)) {
        paramString = paramString.toUpperCase();
      }
      a.append(justify(paramString));
    }
    
    private String justify(String paramString)
    {
      if (width == -1) {
        return paramString;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      boolean bool = f.contains(Formatter.Flags.LEFT_JUSTIFY);
      int i = width - paramString.length();
      int j;
      if (!bool) {
        for (j = 0; j < i; j++) {
          localStringBuilder.append(' ');
        }
      }
      localStringBuilder.append(paramString);
      if (bool) {
        for (j = 0; j < i; j++) {
          localStringBuilder.append(' ');
        }
      }
      return localStringBuilder.toString();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("%");
      Formatter.Flags localFlags = f.dup().remove(Formatter.Flags.UPPERCASE);
      localStringBuilder.append(localFlags.toString());
      if (index > 0) {
        localStringBuilder.append(index).append('$');
      }
      if (width != -1) {
        localStringBuilder.append(width);
      }
      if (precision != -1) {
        localStringBuilder.append('.').append(precision);
      }
      if (dt) {
        localStringBuilder.append(f.contains(Formatter.Flags.UPPERCASE) ? 'T' : 't');
      }
      localStringBuilder.append(f.contains(Formatter.Flags.UPPERCASE) ? Character.toUpperCase(c) : c);
      return localStringBuilder.toString();
    }
    
    private void checkGeneral()
    {
      if (((c == 'b') || (c == 'h')) && (f.contains(Formatter.Flags.ALTERNATE))) {
        failMismatch(Formatter.Flags.ALTERNATE, c);
      }
      if ((width == -1) && (f.contains(Formatter.Flags.LEFT_JUSTIFY))) {
        throw new MissingFormatWidthException(toString());
      }
      checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES });
    }
    
    private void checkDateTime()
    {
      if (precision != -1) {
        throw new IllegalFormatPrecisionException(precision);
      }
      if (!Formatter.DateTime.isValid(c)) {
        throw new UnknownFormatConversionException("t" + c);
      }
      checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE, Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES });
      if ((width == -1) && (f.contains(Formatter.Flags.LEFT_JUSTIFY))) {
        throw new MissingFormatWidthException(toString());
      }
    }
    
    private void checkCharacter()
    {
      if (precision != -1) {
        throw new IllegalFormatPrecisionException(precision);
      }
      checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE, Formatter.Flags.PLUS, Formatter.Flags.LEADING_SPACE, Formatter.Flags.ZERO_PAD, Formatter.Flags.GROUP, Formatter.Flags.PARENTHESES });
      if ((width == -1) && (f.contains(Formatter.Flags.LEFT_JUSTIFY))) {
        throw new MissingFormatWidthException(toString());
      }
    }
    
    private void checkInteger()
    {
      checkNumeric();
      if (precision != -1) {
        throw new IllegalFormatPrecisionException(precision);
      }
      if (c == 'd') {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE });
      } else if (c == 'o') {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.GROUP });
      } else {
        checkBadFlags(new Formatter.Flags[] { Formatter.Flags.GROUP });
      }
    }
    
    private void checkBadFlags(Formatter.Flags... paramVarArgs)
    {
      for (int i = 0; i < paramVarArgs.length; i++) {
        if (f.contains(paramVarArgs[i])) {
          failMismatch(paramVarArgs[i], c);
        }
      }
    }
    
    private void checkFloat()
    {
      checkNumeric();
      if (c != 'f') {
        if (c == 'a') {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PARENTHESES, Formatter.Flags.GROUP });
        } else if (c == 'e') {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.GROUP });
        } else if (c == 'g') {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.ALTERNATE });
        }
      }
    }
    
    private void checkNumeric()
    {
      if ((width != -1) && (width < 0)) {
        throw new IllegalFormatWidthException(width);
      }
      if ((precision != -1) && (precision < 0)) {
        throw new IllegalFormatPrecisionException(precision);
      }
      if ((width == -1) && ((f.contains(Formatter.Flags.LEFT_JUSTIFY)) || (f.contains(Formatter.Flags.ZERO_PAD)))) {
        throw new MissingFormatWidthException(toString());
      }
      if (((f.contains(Formatter.Flags.PLUS)) && (f.contains(Formatter.Flags.LEADING_SPACE))) || ((f.contains(Formatter.Flags.LEFT_JUSTIFY)) && (f.contains(Formatter.Flags.ZERO_PAD)))) {
        throw new IllegalFormatFlagsException(f.toString());
      }
    }
    
    private void checkText()
    {
      if (precision != -1) {
        throw new IllegalFormatPrecisionException(precision);
      }
      switch (c)
      {
      case '%': 
        if ((f.valueOf() != Formatter.Flags.LEFT_JUSTIFY.valueOf()) && (f.valueOf() != Formatter.Flags.NONE.valueOf())) {
          throw new IllegalFormatFlagsException(f.toString());
        }
        if ((width == -1) && (f.contains(Formatter.Flags.LEFT_JUSTIFY))) {
          throw new MissingFormatWidthException(toString());
        }
        break;
      case 'n': 
        if (width != -1) {
          throw new IllegalFormatWidthException(width);
        }
        if (f.valueOf() != Formatter.Flags.NONE.valueOf()) {
          throw new IllegalFormatFlagsException(f.toString());
        }
        break;
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
    
    private void print(byte paramByte, Locale paramLocale)
      throws IOException
    {
      long l = paramByte;
      if ((paramByte < 0) && ((c == 'o') || (c == 'x')))
      {
        l += 256L;
        assert (l >= 0L) : l;
      }
      print(l, paramLocale);
    }
    
    private void print(short paramShort, Locale paramLocale)
      throws IOException
    {
      long l = paramShort;
      if ((paramShort < 0) && ((c == 'o') || (c == 'x')))
      {
        l += 65536L;
        assert (l >= 0L) : l;
      }
      print(l, paramLocale);
    }
    
    private void print(int paramInt, Locale paramLocale)
      throws IOException
    {
      long l = paramInt;
      if ((paramInt < 0) && ((c == 'o') || (c == 'x')))
      {
        l += 4294967296L;
        assert (l >= 0L) : l;
      }
      print(l, paramLocale);
    }
    
    private void print(long paramLong, Locale paramLocale)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (c == 'd')
      {
        boolean bool = paramLong < 0L;
        char[] arrayOfChar;
        if (paramLong < 0L) {
          arrayOfChar = Long.toString(paramLong, 10).substring(1).toCharArray();
        } else {
          arrayOfChar = Long.toString(paramLong, 10).toCharArray();
        }
        leadingSign(localStringBuilder, bool);
        localizedMagnitude(localStringBuilder, arrayOfChar, f, adjustWidth(width, f, bool), paramLocale);
        trailingSign(localStringBuilder, bool);
      }
      else
      {
        String str;
        int i;
        int j;
        if (c == 'o')
        {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PARENTHESES, Formatter.Flags.LEADING_SPACE, Formatter.Flags.PLUS });
          str = Long.toOctalString(paramLong);
          i = f.contains(Formatter.Flags.ALTERNATE) ? str.length() + 1 : str.length();
          if (f.contains(Formatter.Flags.ALTERNATE)) {
            localStringBuilder.append('0');
          }
          if (f.contains(Formatter.Flags.ZERO_PAD)) {
            for (j = 0; j < width - i; j++) {
              localStringBuilder.append('0');
            }
          }
          localStringBuilder.append(str);
        }
        else if (c == 'x')
        {
          checkBadFlags(new Formatter.Flags[] { Formatter.Flags.PARENTHESES, Formatter.Flags.LEADING_SPACE, Formatter.Flags.PLUS });
          str = Long.toHexString(paramLong);
          i = f.contains(Formatter.Flags.ALTERNATE) ? str.length() + 2 : str.length();
          if (f.contains(Formatter.Flags.ALTERNATE)) {
            localStringBuilder.append(f.contains(Formatter.Flags.UPPERCASE) ? "0X" : "0x");
          }
          if (f.contains(Formatter.Flags.ZERO_PAD)) {
            for (j = 0; j < width - i; j++) {
              localStringBuilder.append('0');
            }
          }
          if (f.contains(Formatter.Flags.UPPERCASE)) {
            str = str.toUpperCase();
          }
          localStringBuilder.append(str);
        }
      }
      a.append(justify(localStringBuilder.toString()));
    }
    
    private StringBuilder leadingSign(StringBuilder paramStringBuilder, boolean paramBoolean)
    {
      if (!paramBoolean)
      {
        if (f.contains(Formatter.Flags.PLUS)) {
          paramStringBuilder.append('+');
        } else if (f.contains(Formatter.Flags.LEADING_SPACE)) {
          paramStringBuilder.append(' ');
        }
      }
      else if (f.contains(Formatter.Flags.PARENTHESES)) {
        paramStringBuilder.append('(');
      } else {
        paramStringBuilder.append('-');
      }
      return paramStringBuilder;
    }
    
    private StringBuilder trailingSign(StringBuilder paramStringBuilder, boolean paramBoolean)
    {
      if ((paramBoolean) && (f.contains(Formatter.Flags.PARENTHESES))) {
        paramStringBuilder.append(')');
      }
      return paramStringBuilder;
    }
    
    private void print(BigInteger paramBigInteger, Locale paramLocale)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      boolean bool = paramBigInteger.signum() == -1;
      BigInteger localBigInteger = paramBigInteger.abs();
      leadingSign(localStringBuilder, bool);
      Object localObject;
      if (c == 'd')
      {
        localObject = localBigInteger.toString().toCharArray();
        localizedMagnitude(localStringBuilder, (char[])localObject, f, adjustWidth(width, f, bool), paramLocale);
      }
      else
      {
        int i;
        int j;
        if (c == 'o')
        {
          localObject = localBigInteger.toString(8);
          i = ((String)localObject).length() + localStringBuilder.length();
          if ((bool) && (f.contains(Formatter.Flags.PARENTHESES))) {
            i++;
          }
          if (f.contains(Formatter.Flags.ALTERNATE))
          {
            i++;
            localStringBuilder.append('0');
          }
          if (f.contains(Formatter.Flags.ZERO_PAD)) {
            for (j = 0; j < width - i; j++) {
              localStringBuilder.append('0');
            }
          }
          localStringBuilder.append((String)localObject);
        }
        else if (c == 'x')
        {
          localObject = localBigInteger.toString(16);
          i = ((String)localObject).length() + localStringBuilder.length();
          if ((bool) && (f.contains(Formatter.Flags.PARENTHESES))) {
            i++;
          }
          if (f.contains(Formatter.Flags.ALTERNATE))
          {
            i += 2;
            localStringBuilder.append(f.contains(Formatter.Flags.UPPERCASE) ? "0X" : "0x");
          }
          if (f.contains(Formatter.Flags.ZERO_PAD)) {
            for (j = 0; j < width - i; j++) {
              localStringBuilder.append('0');
            }
          }
          if (f.contains(Formatter.Flags.UPPERCASE)) {
            localObject = ((String)localObject).toUpperCase();
          }
          localStringBuilder.append((String)localObject);
        }
      }
      trailingSign(localStringBuilder, paramBigInteger.signum() == -1);
      a.append(justify(localStringBuilder.toString()));
    }
    
    private void print(float paramFloat, Locale paramLocale)
      throws IOException
    {
      print(paramFloat, paramLocale);
    }
    
    private void print(double paramDouble, Locale paramLocale)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      boolean bool = Double.compare(paramDouble, 0.0D) == -1;
      if (!Double.isNaN(paramDouble))
      {
        double d = Math.abs(paramDouble);
        leadingSign(localStringBuilder, bool);
        if (!Double.isInfinite(d)) {
          print(localStringBuilder, d, paramLocale, f, c, precision, bool);
        } else {
          localStringBuilder.append(f.contains(Formatter.Flags.UPPERCASE) ? "INFINITY" : "Infinity");
        }
        trailingSign(localStringBuilder, bool);
      }
      else
      {
        localStringBuilder.append(f.contains(Formatter.Flags.UPPERCASE) ? "NAN" : "NaN");
      }
      a.append(justify(localStringBuilder.toString()));
    }
    
    private void print(StringBuilder paramStringBuilder, double paramDouble, Locale paramLocale, Formatter.Flags paramFlags, char paramChar, int paramInt, boolean paramBoolean)
      throws IOException
    {
      int i;
      Object localObject1;
      char[] arrayOfChar1;
      Object localObject2;
      char c1;
      char[] arrayOfChar3;
      if (paramChar == 'e')
      {
        i = paramInt == -1 ? 6 : paramInt;
        localObject1 = FormattedFloatingDecimal.valueOf(paramDouble, i, FormattedFloatingDecimal.Form.SCIENTIFIC);
        arrayOfChar1 = addZeros(((FormattedFloatingDecimal)localObject1).getMantissa(), i);
        if ((paramFlags.contains(Formatter.Flags.ALTERNATE)) && (i == 0)) {
          arrayOfChar1 = addDot(arrayOfChar1);
        }
        char[] arrayOfChar2 = paramDouble == 0.0D ? new char[] { '+', '0', '0' } : ((FormattedFloatingDecimal)localObject1).getExponent();
        int k = width;
        if (width != -1) {
          k = adjustWidth(width - arrayOfChar2.length - 1, paramFlags, paramBoolean);
        }
        localizedMagnitude(paramStringBuilder, arrayOfChar1, paramFlags, k, paramLocale);
        paramStringBuilder.append(paramFlags.contains(Formatter.Flags.UPPERCASE) ? 'E' : 'e');
        localObject2 = paramFlags.dup().remove(Formatter.Flags.GROUP);
        c1 = arrayOfChar2[0];
        assert ((c1 == '+') || (c1 == '-'));
        paramStringBuilder.append(c1);
        arrayOfChar3 = new char[arrayOfChar2.length - 1];
        System.arraycopy(arrayOfChar2, 1, arrayOfChar3, 0, arrayOfChar2.length - 1);
        paramStringBuilder.append(localizedMagnitude(null, arrayOfChar3, (Formatter.Flags)localObject2, -1, paramLocale));
      }
      else
      {
        int j;
        if (paramChar == 'f')
        {
          i = paramInt == -1 ? 6 : paramInt;
          localObject1 = FormattedFloatingDecimal.valueOf(paramDouble, i, FormattedFloatingDecimal.Form.DECIMAL_FLOAT);
          arrayOfChar1 = addZeros(((FormattedFloatingDecimal)localObject1).getMantissa(), i);
          if ((paramFlags.contains(Formatter.Flags.ALTERNATE)) && (i == 0)) {
            arrayOfChar1 = addDot(arrayOfChar1);
          }
          j = width;
          if (width != -1) {
            j = adjustWidth(width, paramFlags, paramBoolean);
          }
          localizedMagnitude(paramStringBuilder, arrayOfChar1, paramFlags, j, paramLocale);
        }
        else
        {
          int m;
          if (paramChar == 'g')
          {
            i = paramInt;
            if (paramInt == -1) {
              i = 6;
            } else if (paramInt == 0) {
              i = 1;
            }
            if (paramDouble == 0.0D)
            {
              localObject1 = null;
              arrayOfChar1 = new char[] { '0' };
              j = 0;
            }
            else
            {
              FormattedFloatingDecimal localFormattedFloatingDecimal = FormattedFloatingDecimal.valueOf(paramDouble, i, FormattedFloatingDecimal.Form.GENERAL);
              localObject1 = localFormattedFloatingDecimal.getExponent();
              arrayOfChar1 = localFormattedFloatingDecimal.getMantissa();
              j = localFormattedFloatingDecimal.getExponentRounded();
            }
            if (localObject1 != null) {
              i--;
            } else {
              i -= j + 1;
            }
            arrayOfChar1 = addZeros(arrayOfChar1, i);
            if ((paramFlags.contains(Formatter.Flags.ALTERNATE)) && (i == 0)) {
              arrayOfChar1 = addDot(arrayOfChar1);
            }
            m = width;
            if (width != -1) {
              if (localObject1 != null) {
                m = adjustWidth(width - localObject1.length - 1, paramFlags, paramBoolean);
              } else {
                m = adjustWidth(width, paramFlags, paramBoolean);
              }
            }
            localizedMagnitude(paramStringBuilder, arrayOfChar1, paramFlags, m, paramLocale);
            if (localObject1 != null)
            {
              paramStringBuilder.append(paramFlags.contains(Formatter.Flags.UPPERCASE) ? 'E' : 'e');
              localObject2 = paramFlags.dup().remove(Formatter.Flags.GROUP);
              c1 = localObject1[0];
              assert ((c1 == '+') || (c1 == '-'));
              paramStringBuilder.append(c1);
              arrayOfChar3 = new char[localObject1.length - 1];
              System.arraycopy(localObject1, 1, arrayOfChar3, 0, localObject1.length - 1);
              paramStringBuilder.append(localizedMagnitude(null, arrayOfChar3, (Formatter.Flags)localObject2, -1, paramLocale));
            }
          }
          else if (paramChar == 'a')
          {
            i = paramInt;
            if (paramInt == -1) {
              i = 0;
            } else if (paramInt == 0) {
              i = 1;
            }
            localObject1 = hexDouble(paramDouble, i);
            boolean bool = paramFlags.contains(Formatter.Flags.UPPERCASE);
            paramStringBuilder.append(bool ? "0X" : "0x");
            if (paramFlags.contains(Formatter.Flags.ZERO_PAD)) {
              for (m = 0; m < width - ((String)localObject1).length() - 2; m++) {
                paramStringBuilder.append('0');
              }
            }
            m = ((String)localObject1).indexOf('p');
            arrayOfChar1 = ((String)localObject1).substring(0, m).toCharArray();
            if (bool)
            {
              localObject2 = new String(arrayOfChar1);
              localObject2 = ((String)localObject2).toUpperCase(Locale.US);
              arrayOfChar1 = ((String)localObject2).toCharArray();
            }
            paramStringBuilder.append(i != 0 ? addZeros(arrayOfChar1, i) : arrayOfChar1);
            paramStringBuilder.append(bool ? 'P' : 'p');
            paramStringBuilder.append(((String)localObject1).substring(m + 1));
          }
        }
      }
    }
    
    private char[] addZeros(char[] paramArrayOfChar, int paramInt)
    {
      for (int i = 0; (i < paramArrayOfChar.length) && (paramArrayOfChar[i] != '.'); i++) {}
      int j = 0;
      if (i == paramArrayOfChar.length) {
        j = 1;
      }
      int k = paramArrayOfChar.length - i - (j != 0 ? 0 : 1);
      assert (k <= paramInt);
      if (k == paramInt) {
        return paramArrayOfChar;
      }
      char[] arrayOfChar = new char[paramArrayOfChar.length + paramInt - k + (j != 0 ? 1 : 0)];
      System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, paramArrayOfChar.length);
      int m = paramArrayOfChar.length;
      if (j != 0)
      {
        arrayOfChar[paramArrayOfChar.length] = '.';
        m++;
      }
      for (int n = m; n < arrayOfChar.length; n++) {
        arrayOfChar[n] = '0';
      }
      return arrayOfChar;
    }
    
    private String hexDouble(double paramDouble, int paramInt)
    {
      if ((!Double.isFinite(paramDouble)) || (paramDouble == 0.0D) || (paramInt == 0) || (paramInt >= 13)) {
        return Double.toHexString(paramDouble).substring(2);
      }
      assert ((paramInt >= 1) && (paramInt <= 12));
      int i = Math.getExponent(paramDouble);
      int j = i == 64513 ? 1 : 0;
      if (j != 0)
      {
        Formatter.access$202(Math.scalb(1.0D, 54));
        paramDouble *= Formatter.scaleUp;
        i = Math.getExponent(paramDouble);
        assert ((i >= 64514) && (i <= 1023)) : i;
      }
      int k = 1 + paramInt * 4;
      int m = 53 - k;
      assert ((m >= 1) && (m < 53));
      long l1 = Double.doubleToLongBits(paramDouble);
      long l2 = (l1 & 0x7FFFFFFFFFFFFFFF) >> m;
      long l3 = l1 & (-1L << m ^ 0xFFFFFFFFFFFFFFFF);
      int n = (l2 & 1L) == 0L ? 1 : 0;
      int i1 = (1L << m - 1 & l3) != 0L ? 1 : 0;
      int i2 = (m > 1) && (((1L << m - 1 ^ 0xFFFFFFFFFFFFFFFF) & l3) != 0L) ? 1 : 0;
      if (((n != 0) && (i1 != 0) && (i2 != 0)) || ((n == 0) && (i1 != 0))) {
        l2 += 1L;
      }
      long l4 = l1 & 0x8000000000000000;
      l2 = l4 | l2 << m;
      double d = Double.longBitsToDouble(l2);
      if (Double.isInfinite(d)) {
        return "1.0p1024";
      }
      String str1 = Double.toHexString(d).substring(2);
      if (j == 0) {
        return str1;
      }
      int i3 = str1.indexOf('p');
      if (i3 == -1)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        return null;
      }
      String str2 = str1.substring(i3 + 1);
      int i4 = Integer.parseInt(str2) - 54;
      return str1.substring(0, i3) + "p" + Integer.toString(i4);
    }
    
    private void print(BigDecimal paramBigDecimal, Locale paramLocale)
      throws IOException
    {
      if (c == 'a') {
        failConversion(c, paramBigDecimal);
      }
      StringBuilder localStringBuilder = new StringBuilder();
      boolean bool = paramBigDecimal.signum() == -1;
      BigDecimal localBigDecimal = paramBigDecimal.abs();
      leadingSign(localStringBuilder, bool);
      print(localStringBuilder, localBigDecimal, paramLocale, f, c, precision, bool);
      trailingSign(localStringBuilder, bool);
      a.append(justify(localStringBuilder.toString()));
    }
    
    private void print(StringBuilder paramStringBuilder, BigDecimal paramBigDecimal, Locale paramLocale, Formatter.Flags paramFlags, char paramChar, int paramInt, boolean paramBoolean)
      throws IOException
    {
      int i;
      int j;
      int k;
      int i1;
      if (paramChar == 'e')
      {
        i = paramInt == -1 ? 6 : paramInt;
        j = paramBigDecimal.scale();
        k = paramBigDecimal.precision();
        int m = 0;
        if (i > k - 1)
        {
          i1 = k;
          m = i - (k - 1);
        }
        else
        {
          i1 = i + 1;
        }
        MathContext localMathContext = new MathContext(i1);
        BigDecimal localBigDecimal2 = new BigDecimal(paramBigDecimal.unscaledValue(), j, localMathContext);
        BigDecimalLayout localBigDecimalLayout = new BigDecimalLayout(localBigDecimal2.unscaledValue(), localBigDecimal2.scale(), Formatter.BigDecimalLayoutForm.SCIENTIFIC);
        char[] arrayOfChar2 = localBigDecimalLayout.mantissa();
        if (((k == 1) || (!localBigDecimalLayout.hasDot())) && ((m > 0) || (paramFlags.contains(Formatter.Flags.ALTERNATE)))) {
          arrayOfChar2 = addDot(arrayOfChar2);
        }
        arrayOfChar2 = trailingZeros(arrayOfChar2, m);
        char[] arrayOfChar3 = localBigDecimalLayout.exponent();
        int i2 = width;
        if (width != -1) {
          i2 = adjustWidth(width - arrayOfChar3.length - 1, paramFlags, paramBoolean);
        }
        localizedMagnitude(paramStringBuilder, arrayOfChar2, paramFlags, i2, paramLocale);
        paramStringBuilder.append(paramFlags.contains(Formatter.Flags.UPPERCASE) ? 'E' : 'e');
        Formatter.Flags localFlags = paramFlags.dup().remove(Formatter.Flags.GROUP);
        int i3 = arrayOfChar3[0];
        assert ((i3 == 43) || (i3 == 45));
        paramStringBuilder.append(arrayOfChar3[0]);
        char[] arrayOfChar4 = new char[arrayOfChar3.length - 1];
        System.arraycopy(arrayOfChar3, 1, arrayOfChar4, 0, arrayOfChar3.length - 1);
        paramStringBuilder.append(localizedMagnitude(null, arrayOfChar4, localFlags, -1, paramLocale));
      }
      else
      {
        Object localObject;
        if (paramChar == 'f')
        {
          i = paramInt == -1 ? 6 : paramInt;
          j = paramBigDecimal.scale();
          if (j > i)
          {
            k = paramBigDecimal.precision();
            if (k <= j)
            {
              paramBigDecimal = paramBigDecimal.setScale(i, RoundingMode.HALF_UP);
            }
            else
            {
              k -= j - i;
              paramBigDecimal = new BigDecimal(paramBigDecimal.unscaledValue(), j, new MathContext(k));
            }
          }
          localObject = new BigDecimalLayout(paramBigDecimal.unscaledValue(), paramBigDecimal.scale(), Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT);
          char[] arrayOfChar1 = ((BigDecimalLayout)localObject).mantissa();
          i1 = ((BigDecimalLayout)localObject).scale() < i ? i - ((BigDecimalLayout)localObject).scale() : 0;
          if ((((BigDecimalLayout)localObject).scale() == 0) && ((paramFlags.contains(Formatter.Flags.ALTERNATE)) || (i1 > 0))) {
            arrayOfChar1 = addDot(((BigDecimalLayout)localObject).mantissa());
          }
          arrayOfChar1 = trailingZeros(arrayOfChar1, i1);
          localizedMagnitude(paramStringBuilder, arrayOfChar1, paramFlags, adjustWidth(width, paramFlags, paramBoolean), paramLocale);
        }
        else if (paramChar == 'g')
        {
          i = paramInt;
          if (paramInt == -1) {
            i = 6;
          } else if (paramInt == 0) {
            i = 1;
          }
          BigDecimal localBigDecimal1 = BigDecimal.valueOf(1L, 4);
          localObject = BigDecimal.valueOf(1L, -i);
          if ((paramBigDecimal.equals(BigDecimal.ZERO)) || ((paramBigDecimal.compareTo(localBigDecimal1) != -1) && (paramBigDecimal.compareTo((BigDecimal)localObject) == -1)))
          {
            int n = -paramBigDecimal.scale() + (paramBigDecimal.unscaledValue().toString().length() - 1);
            i = i - n - 1;
            print(paramStringBuilder, paramBigDecimal, paramLocale, paramFlags, 'f', i, paramBoolean);
          }
          else
          {
            print(paramStringBuilder, paramBigDecimal, paramLocale, paramFlags, 'e', i - 1, paramBoolean);
          }
        }
        else if ((paramChar == 'a') && (!$assertionsDisabled))
        {
          throw new AssertionError();
        }
      }
    }
    
    private int adjustWidth(int paramInt, Formatter.Flags paramFlags, boolean paramBoolean)
    {
      int i = paramInt;
      if ((i != -1) && (paramBoolean) && (paramFlags.contains(Formatter.Flags.PARENTHESES))) {
        i--;
      }
      return i;
    }
    
    private char[] addDot(char[] paramArrayOfChar)
    {
      char[] arrayOfChar = paramArrayOfChar;
      arrayOfChar = new char[paramArrayOfChar.length + 1];
      System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, paramArrayOfChar.length);
      arrayOfChar[(arrayOfChar.length - 1)] = '.';
      return arrayOfChar;
    }
    
    private char[] trailingZeros(char[] paramArrayOfChar, int paramInt)
    {
      char[] arrayOfChar = paramArrayOfChar;
      if (paramInt > 0)
      {
        arrayOfChar = new char[paramArrayOfChar.length + paramInt];
        System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, paramArrayOfChar.length);
        for (int i = paramArrayOfChar.length; i < arrayOfChar.length; i++) {
          arrayOfChar[i] = '0';
        }
      }
      return arrayOfChar;
    }
    
    private void print(Calendar paramCalendar, char paramChar, Locale paramLocale)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      print(localStringBuilder, paramCalendar, paramChar, paramLocale);
      String str = justify(localStringBuilder.toString());
      if (f.contains(Formatter.Flags.UPPERCASE)) {
        str = str.toUpperCase();
      }
      a.append(str);
    }
    
    private Appendable print(StringBuilder paramStringBuilder, Calendar paramCalendar, char paramChar, Locale paramLocale)
      throws IOException
    {
      if (paramStringBuilder == null) {
        paramStringBuilder = new StringBuilder();
      }
      int i;
      Object localObject1;
      Formatter.Flags localFlags1;
      int j;
      int k;
      Locale localLocale;
      Object localObject3;
      Object localObject2;
      char c1;
      switch (paramChar)
      {
      case 'H': 
      case 'I': 
      case 'k': 
      case 'l': 
        i = paramCalendar.get(11);
        if ((paramChar == 'I') || (paramChar == 'l')) {
          i = (i == 0) || (i == 12) ? 12 : i % 12;
        }
        localObject1 = (paramChar == 'H') || (paramChar == 'I') ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
        paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 2, paramLocale));
        break;
      case 'M': 
        i = paramCalendar.get(12);
        localObject1 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 2, paramLocale));
        break;
      case 'N': 
        i = paramCalendar.get(14) * 1000000;
        localObject1 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 9, paramLocale));
        break;
      case 'L': 
        i = paramCalendar.get(14);
        localObject1 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 3, paramLocale));
        break;
      case 'Q': 
        long l1 = paramCalendar.getTimeInMillis();
        localFlags1 = Formatter.Flags.NONE;
        paramStringBuilder.append(localizedMagnitude(null, l1, localFlags1, width, paramLocale));
        break;
      case 'p': 
        String[] arrayOfString = { "AM", "PM" };
        if ((paramLocale != null) && (paramLocale != Locale.US))
        {
          localObject1 = DateFormatSymbols.getInstance(paramLocale);
          arrayOfString = ((DateFormatSymbols)localObject1).getAmPmStrings();
        }
        localObject1 = arrayOfString[paramCalendar.get(9)];
        paramStringBuilder.append(((String)localObject1).toLowerCase(paramLocale != null ? paramLocale : Locale.US));
        break;
      case 's': 
        long l2 = paramCalendar.getTimeInMillis() / 1000L;
        localFlags1 = Formatter.Flags.NONE;
        paramStringBuilder.append(localizedMagnitude(null, l2, localFlags1, width, paramLocale));
        break;
      case 'S': 
        j = paramCalendar.get(13);
        localObject1 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, j, (Formatter.Flags)localObject1, 2, paramLocale));
        break;
      case 'z': 
        j = paramCalendar.get(15) + paramCalendar.get(16);
        int m = j < 0 ? 1 : 0;
        paramStringBuilder.append(m != 0 ? '-' : '+');
        if (m != 0) {
          j = -j;
        }
        int i1 = j / 60000;
        int i2 = i1 / 60 * 100 + i1 % 60;
        Formatter.Flags localFlags2 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, i2, localFlags2, 4, paramLocale));
        break;
      case 'Z': 
        TimeZone localTimeZone = paramCalendar.getTimeZone();
        paramStringBuilder.append(localTimeZone.getDisplayName(paramCalendar.get(16) != 0, 0, paramLocale == null ? Locale.US : paramLocale));
        break;
      case 'A': 
      case 'a': 
        k = paramCalendar.get(7);
        localLocale = paramLocale == null ? Locale.US : paramLocale;
        localObject3 = DateFormatSymbols.getInstance(localLocale);
        if (paramChar == 'A') {
          paramStringBuilder.append(localObject3.getWeekdays()[k]);
        } else {
          paramStringBuilder.append(localObject3.getShortWeekdays()[k]);
        }
        break;
      case 'B': 
      case 'b': 
      case 'h': 
        k = paramCalendar.get(2);
        localLocale = paramLocale == null ? Locale.US : paramLocale;
        localObject3 = DateFormatSymbols.getInstance(localLocale);
        if (paramChar == 'B') {
          paramStringBuilder.append(localObject3.getMonths()[k]);
        } else {
          paramStringBuilder.append(localObject3.getShortMonths()[k]);
        }
        break;
      case 'C': 
      case 'Y': 
      case 'y': 
        k = paramCalendar.get(1);
        int n = 2;
        switch (paramChar)
        {
        case 'C': 
          k /= 100;
          break;
        case 'y': 
          k %= 100;
          break;
        case 'Y': 
          n = 4;
        }
        localObject3 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject3, n, paramLocale));
        break;
      case 'd': 
      case 'e': 
        k = paramCalendar.get(5);
        localObject2 = paramChar == 'd' ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
        paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject2, 2, paramLocale));
        break;
      case 'j': 
        k = paramCalendar.get(6);
        localObject2 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject2, 3, paramLocale));
        break;
      case 'm': 
        k = paramCalendar.get(2) + 1;
        localObject2 = Formatter.Flags.ZERO_PAD;
        paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject2, 2, paramLocale));
        break;
      case 'R': 
      case 'T': 
        k = 58;
        print(paramStringBuilder, paramCalendar, 'H', paramLocale).append(k);
        print(paramStringBuilder, paramCalendar, 'M', paramLocale);
        if (paramChar == 'T')
        {
          paramStringBuilder.append(k);
          print(paramStringBuilder, paramCalendar, 'S', paramLocale);
        }
        break;
      case 'r': 
        c1 = ':';
        print(paramStringBuilder, paramCalendar, 'I', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'M', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'S', paramLocale).append(' ');
        localObject2 = new StringBuilder();
        print((StringBuilder)localObject2, paramCalendar, 'p', paramLocale);
        paramStringBuilder.append(((StringBuilder)localObject2).toString().toUpperCase(paramLocale != null ? paramLocale : Locale.US));
        break;
      case 'c': 
        c1 = ' ';
        print(paramStringBuilder, paramCalendar, 'a', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'b', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'd', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'T', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'Z', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'Y', paramLocale);
        break;
      case 'D': 
        c1 = '/';
        print(paramStringBuilder, paramCalendar, 'm', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'd', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'y', paramLocale);
        break;
      case 'F': 
        c1 = '-';
        print(paramStringBuilder, paramCalendar, 'Y', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'm', paramLocale).append(c1);
        print(paramStringBuilder, paramCalendar, 'd', paramLocale);
        break;
      case 'E': 
      case 'G': 
      case 'J': 
      case 'K': 
      case 'O': 
      case 'P': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case '[': 
      case '\\': 
      case ']': 
      case '^': 
      case '_': 
      case '`': 
      case 'f': 
      case 'g': 
      case 'i': 
      case 'n': 
      case 'o': 
      case 'q': 
      case 't': 
      case 'u': 
      case 'v': 
      case 'w': 
      case 'x': 
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
      return paramStringBuilder;
    }
    
    private void print(TemporalAccessor paramTemporalAccessor, char paramChar, Locale paramLocale)
      throws IOException
    {
      StringBuilder localStringBuilder = new StringBuilder();
      print(localStringBuilder, paramTemporalAccessor, paramChar, paramLocale);
      String str = justify(localStringBuilder.toString());
      if (f.contains(Formatter.Flags.UPPERCASE)) {
        str = str.toUpperCase();
      }
      a.append(str);
    }
    
    private Appendable print(StringBuilder paramStringBuilder, TemporalAccessor paramTemporalAccessor, char paramChar, Locale paramLocale)
      throws IOException
    {
      if (paramStringBuilder == null) {
        paramStringBuilder = new StringBuilder();
      }
      try
      {
        int i;
        Object localObject1;
        Formatter.Flags localFlags1;
        int j;
        Object localObject2;
        int k;
        Object localObject4;
        Object localObject3;
        char c1;
        switch (paramChar)
        {
        case 'H': 
          i = paramTemporalAccessor.get(ChronoField.HOUR_OF_DAY);
          paramStringBuilder.append(localizedMagnitude(null, i, Formatter.Flags.ZERO_PAD, 2, paramLocale));
          break;
        case 'k': 
          i = paramTemporalAccessor.get(ChronoField.HOUR_OF_DAY);
          paramStringBuilder.append(localizedMagnitude(null, i, Formatter.Flags.NONE, 2, paramLocale));
          break;
        case 'I': 
          i = paramTemporalAccessor.get(ChronoField.CLOCK_HOUR_OF_AMPM);
          paramStringBuilder.append(localizedMagnitude(null, i, Formatter.Flags.ZERO_PAD, 2, paramLocale));
          break;
        case 'l': 
          i = paramTemporalAccessor.get(ChronoField.CLOCK_HOUR_OF_AMPM);
          paramStringBuilder.append(localizedMagnitude(null, i, Formatter.Flags.NONE, 2, paramLocale));
          break;
        case 'M': 
          i = paramTemporalAccessor.get(ChronoField.MINUTE_OF_HOUR);
          localObject1 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 2, paramLocale));
          break;
        case 'N': 
          i = paramTemporalAccessor.get(ChronoField.MILLI_OF_SECOND) * 1000000;
          localObject1 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 9, paramLocale));
          break;
        case 'L': 
          i = paramTemporalAccessor.get(ChronoField.MILLI_OF_SECOND);
          localObject1 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, i, (Formatter.Flags)localObject1, 3, paramLocale));
          break;
        case 'Q': 
          long l1 = paramTemporalAccessor.getLong(ChronoField.INSTANT_SECONDS) * 1000L + paramTemporalAccessor.getLong(ChronoField.MILLI_OF_SECOND);
          localFlags1 = Formatter.Flags.NONE;
          paramStringBuilder.append(localizedMagnitude(null, l1, localFlags1, width, paramLocale));
          break;
        case 'p': 
          String[] arrayOfString = { "AM", "PM" };
          if ((paramLocale != null) && (paramLocale != Locale.US))
          {
            localObject1 = DateFormatSymbols.getInstance(paramLocale);
            arrayOfString = ((DateFormatSymbols)localObject1).getAmPmStrings();
          }
          localObject1 = arrayOfString[paramTemporalAccessor.get(ChronoField.AMPM_OF_DAY)];
          paramStringBuilder.append(((String)localObject1).toLowerCase(paramLocale != null ? paramLocale : Locale.US));
          break;
        case 's': 
          long l2 = paramTemporalAccessor.getLong(ChronoField.INSTANT_SECONDS);
          localFlags1 = Formatter.Flags.NONE;
          paramStringBuilder.append(localizedMagnitude(null, l2, localFlags1, width, paramLocale));
          break;
        case 'S': 
          j = paramTemporalAccessor.get(ChronoField.SECOND_OF_MINUTE);
          localObject1 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, j, (Formatter.Flags)localObject1, 2, paramLocale));
          break;
        case 'z': 
          j = paramTemporalAccessor.get(ChronoField.OFFSET_SECONDS);
          int m = j < 0 ? 1 : 0;
          paramStringBuilder.append(m != 0 ? '-' : '+');
          if (m != 0) {
            j = -j;
          }
          int i1 = j / 60;
          int i2 = i1 / 60 * 100 + i1 % 60;
          Formatter.Flags localFlags2 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, i2, localFlags2, 4, paramLocale));
          break;
        case 'Z': 
          ZoneId localZoneId = (ZoneId)paramTemporalAccessor.query(TemporalQueries.zone());
          if (localZoneId == null) {
            throw new IllegalFormatConversionException(paramChar, paramTemporalAccessor.getClass());
          }
          if ((!(localZoneId instanceof ZoneOffset)) && (paramTemporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)))
          {
            localObject2 = Instant.from(paramTemporalAccessor);
            paramStringBuilder.append(TimeZone.getTimeZone(localZoneId.getId()).getDisplayName(localZoneId.getRules().isDaylightSavings((Instant)localObject2), 0, paramLocale == null ? Locale.US : paramLocale));
          }
          else
          {
            paramStringBuilder.append(localZoneId.getId());
          }
          break;
        case 'A': 
        case 'a': 
          k = paramTemporalAccessor.get(ChronoField.DAY_OF_WEEK) % 7 + 1;
          localObject2 = paramLocale == null ? Locale.US : paramLocale;
          localObject4 = DateFormatSymbols.getInstance((Locale)localObject2);
          if (paramChar == 'A') {
            paramStringBuilder.append(localObject4.getWeekdays()[k]);
          } else {
            paramStringBuilder.append(localObject4.getShortWeekdays()[k]);
          }
          break;
        case 'B': 
        case 'b': 
        case 'h': 
          k = paramTemporalAccessor.get(ChronoField.MONTH_OF_YEAR) - 1;
          localObject2 = paramLocale == null ? Locale.US : paramLocale;
          localObject4 = DateFormatSymbols.getInstance((Locale)localObject2);
          if (paramChar == 'B') {
            paramStringBuilder.append(localObject4.getMonths()[k]);
          } else {
            paramStringBuilder.append(localObject4.getShortMonths()[k]);
          }
          break;
        case 'C': 
        case 'Y': 
        case 'y': 
          k = paramTemporalAccessor.get(ChronoField.YEAR_OF_ERA);
          int n = 2;
          switch (paramChar)
          {
          case 'C': 
            k /= 100;
            break;
          case 'y': 
            k %= 100;
            break;
          case 'Y': 
            n = 4;
          }
          localObject4 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject4, n, paramLocale));
          break;
        case 'd': 
        case 'e': 
          k = paramTemporalAccessor.get(ChronoField.DAY_OF_MONTH);
          localObject3 = paramChar == 'd' ? Formatter.Flags.ZERO_PAD : Formatter.Flags.NONE;
          paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject3, 2, paramLocale));
          break;
        case 'j': 
          k = paramTemporalAccessor.get(ChronoField.DAY_OF_YEAR);
          localObject3 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject3, 3, paramLocale));
          break;
        case 'm': 
          k = paramTemporalAccessor.get(ChronoField.MONTH_OF_YEAR);
          localObject3 = Formatter.Flags.ZERO_PAD;
          paramStringBuilder.append(localizedMagnitude(null, k, (Formatter.Flags)localObject3, 2, paramLocale));
          break;
        case 'R': 
        case 'T': 
          k = 58;
          print(paramStringBuilder, paramTemporalAccessor, 'H', paramLocale).append(k);
          print(paramStringBuilder, paramTemporalAccessor, 'M', paramLocale);
          if (paramChar == 'T')
          {
            paramStringBuilder.append(k);
            print(paramStringBuilder, paramTemporalAccessor, 'S', paramLocale);
          }
          break;
        case 'r': 
          c1 = ':';
          print(paramStringBuilder, paramTemporalAccessor, 'I', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'M', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'S', paramLocale).append(' ');
          localObject3 = new StringBuilder();
          print((StringBuilder)localObject3, paramTemporalAccessor, 'p', paramLocale);
          paramStringBuilder.append(((StringBuilder)localObject3).toString().toUpperCase(paramLocale != null ? paramLocale : Locale.US));
          break;
        case 'c': 
          c1 = ' ';
          print(paramStringBuilder, paramTemporalAccessor, 'a', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'b', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'd', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'T', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'Z', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'Y', paramLocale);
          break;
        case 'D': 
          c1 = '/';
          print(paramStringBuilder, paramTemporalAccessor, 'm', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'd', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'y', paramLocale);
          break;
        case 'F': 
          c1 = '-';
          print(paramStringBuilder, paramTemporalAccessor, 'Y', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'm', paramLocale).append(c1);
          print(paramStringBuilder, paramTemporalAccessor, 'd', paramLocale);
          break;
        case 'E': 
        case 'G': 
        case 'J': 
        case 'K': 
        case 'O': 
        case 'P': 
        case 'U': 
        case 'V': 
        case 'W': 
        case 'X': 
        case '[': 
        case '\\': 
        case ']': 
        case '^': 
        case '_': 
        case '`': 
        case 'f': 
        case 'g': 
        case 'i': 
        case 'n': 
        case 'o': 
        case 'q': 
        case 't': 
        case 'u': 
        case 'v': 
        case 'w': 
        case 'x': 
        default: 
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
          break;
        }
      }
      catch (DateTimeException localDateTimeException)
      {
        throw new IllegalFormatConversionException(paramChar, paramTemporalAccessor.getClass());
      }
      return paramStringBuilder;
    }
    
    private void failMismatch(Formatter.Flags paramFlags, char paramChar)
    {
      String str = paramFlags.toString();
      throw new FormatFlagsConversionMismatchException(str, paramChar);
    }
    
    private void failConversion(char paramChar, Object paramObject)
    {
      throw new IllegalFormatConversionException(paramChar, paramObject.getClass());
    }
    
    private char getZero(Locale paramLocale)
    {
      if ((paramLocale != null) && (!paramLocale.equals(locale())))
      {
        DecimalFormatSymbols localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
        return localDecimalFormatSymbols.getZeroDigit();
      }
      return zero;
    }
    
    private StringBuilder localizedMagnitude(StringBuilder paramStringBuilder, long paramLong, Formatter.Flags paramFlags, int paramInt, Locale paramLocale)
    {
      char[] arrayOfChar = Long.toString(paramLong, 10).toCharArray();
      return localizedMagnitude(paramStringBuilder, arrayOfChar, paramFlags, paramInt, paramLocale);
    }
    
    private StringBuilder localizedMagnitude(StringBuilder paramStringBuilder, char[] paramArrayOfChar, Formatter.Flags paramFlags, int paramInt, Locale paramLocale)
    {
      if (paramStringBuilder == null) {
        paramStringBuilder = new StringBuilder();
      }
      int i = paramStringBuilder.length();
      int j = getZero(paramLocale);
      char c1 = '\000';
      int k = -1;
      char c2 = '\000';
      int m = paramArrayOfChar.length;
      int n = m;
      for (int i1 = 0; i1 < m; i1++) {
        if (paramArrayOfChar[i1] == '.')
        {
          n = i1;
          break;
        }
      }
      DecimalFormatSymbols localDecimalFormatSymbols;
      if (n < m) {
        if ((paramLocale == null) || (paramLocale.equals(Locale.US)))
        {
          c2 = '.';
        }
        else
        {
          localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
          c2 = localDecimalFormatSymbols.getDecimalSeparator();
        }
      }
      if (paramFlags.contains(Formatter.Flags.GROUP)) {
        if ((paramLocale == null) || (paramLocale.equals(Locale.US)))
        {
          c1 = ',';
          k = 3;
        }
        else
        {
          localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
          c1 = localDecimalFormatSymbols.getGroupingSeparator();
          DecimalFormat localDecimalFormat = (DecimalFormat)NumberFormat.getIntegerInstance(paramLocale);
          k = localDecimalFormat.getGroupingSize();
        }
      }
      for (int i2 = 0; i2 < m; i2++) {
        if (i2 == n)
        {
          paramStringBuilder.append(c2);
          c1 = '\000';
        }
        else
        {
          int i3 = paramArrayOfChar[i2];
          paramStringBuilder.append((char)(i3 - 48 + j));
          if ((c1 != 0) && (i2 != n - 1) && ((n - i2) % k == 1)) {
            paramStringBuilder.append(c1);
          }
        }
      }
      m = paramStringBuilder.length();
      if ((paramInt != -1) && (paramFlags.contains(Formatter.Flags.ZERO_PAD))) {
        for (i2 = 0; i2 < paramInt - m; i2++) {
          paramStringBuilder.insert(i, j);
        }
      }
      return paramStringBuilder;
    }
    
    private class BigDecimalLayout
    {
      private StringBuilder mant;
      private StringBuilder exp;
      private boolean dot = false;
      private int scale;
      
      public BigDecimalLayout(BigInteger paramBigInteger, int paramInt, Formatter.BigDecimalLayoutForm paramBigDecimalLayoutForm)
      {
        layout(paramBigInteger, paramInt, paramBigDecimalLayoutForm);
      }
      
      public boolean hasDot()
      {
        return dot;
      }
      
      public int scale()
      {
        return scale;
      }
      
      public char[] layoutChars()
      {
        StringBuilder localStringBuilder = new StringBuilder(mant);
        if (exp != null)
        {
          localStringBuilder.append('E');
          localStringBuilder.append(exp);
        }
        return toCharArray(localStringBuilder);
      }
      
      public char[] mantissa()
      {
        return toCharArray(mant);
      }
      
      public char[] exponent()
      {
        return toCharArray(exp);
      }
      
      private char[] toCharArray(StringBuilder paramStringBuilder)
      {
        if (paramStringBuilder == null) {
          return null;
        }
        char[] arrayOfChar = new char[paramStringBuilder.length()];
        paramStringBuilder.getChars(0, arrayOfChar.length, arrayOfChar, 0);
        return arrayOfChar;
      }
      
      private void layout(BigInteger paramBigInteger, int paramInt, Formatter.BigDecimalLayoutForm paramBigDecimalLayoutForm)
      {
        char[] arrayOfChar = paramBigInteger.toString().toCharArray();
        scale = paramInt;
        mant = new StringBuilder(arrayOfChar.length + 14);
        if (paramInt == 0)
        {
          int i = arrayOfChar.length;
          if (i > 1)
          {
            mant.append(arrayOfChar[0]);
            if (paramBigDecimalLayoutForm == Formatter.BigDecimalLayoutForm.SCIENTIFIC)
            {
              mant.append('.');
              dot = true;
              mant.append(arrayOfChar, 1, i - 1);
              exp = new StringBuilder("+");
              if (i < 10) {
                exp.append("0").append(i - 1);
              } else {
                exp.append(i - 1);
              }
            }
            else
            {
              mant.append(arrayOfChar, 1, i - 1);
            }
          }
          else
          {
            mant.append(arrayOfChar);
            if (paramBigDecimalLayoutForm == Formatter.BigDecimalLayoutForm.SCIENTIFIC) {
              exp = new StringBuilder("+00");
            }
          }
          return;
        }
        long l1 = -paramInt + (arrayOfChar.length - 1);
        if (paramBigDecimalLayoutForm == Formatter.BigDecimalLayoutForm.DECIMAL_FLOAT)
        {
          int j = paramInt - arrayOfChar.length;
          if (j >= 0)
          {
            mant.append("0.");
            dot = true;
            while (j > 0)
            {
              mant.append('0');
              j--;
            }
            mant.append(arrayOfChar);
          }
          else if (-j < arrayOfChar.length)
          {
            mant.append(arrayOfChar, 0, -j);
            mant.append('.');
            dot = true;
            mant.append(arrayOfChar, -j, paramInt);
          }
          else
          {
            mant.append(arrayOfChar, 0, arrayOfChar.length);
            for (int k = 0; k < -paramInt; k++) {
              mant.append('0');
            }
            scale = 0;
          }
        }
        else
        {
          mant.append(arrayOfChar[0]);
          if (arrayOfChar.length > 1)
          {
            mant.append('.');
            dot = true;
            mant.append(arrayOfChar, 1, arrayOfChar.length - 1);
          }
          exp = new StringBuilder();
          if (l1 != 0L)
          {
            long l2 = Math.abs(l1);
            exp.append(l1 < 0L ? '-' : '+');
            if (l2 < 10L) {
              exp.append('0');
            }
            exp.append(l2);
          }
          else
          {
            exp.append("+00");
          }
        }
      }
    }
  }
  
  private static abstract interface FormatString
  {
    public abstract int index();
    
    public abstract void print(Object paramObject, Locale paramLocale)
      throws IOException;
    
    public abstract String toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Formatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
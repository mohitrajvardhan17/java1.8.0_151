package java.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.LRUCache;

public final class Scanner
  implements Iterator<String>, Closeable
{
  private CharBuffer buf;
  private static final int BUFFER_SIZE = 1024;
  private int position;
  private Matcher matcher;
  private Pattern delimPattern;
  private Pattern hasNextPattern;
  private int hasNextPosition;
  private String hasNextResult;
  private Readable source;
  private boolean sourceClosed = false;
  private boolean needInput = false;
  private boolean skipped = false;
  private int savedScannerPosition = -1;
  private Object typeCache = null;
  private boolean matchValid = false;
  private boolean closed = false;
  private int radix = 10;
  private int defaultRadix = 10;
  private Locale locale = null;
  private LRUCache<String, Pattern> patternCache = new LRUCache(7)
  {
    protected Pattern create(String paramAnonymousString)
    {
      return Pattern.compile(paramAnonymousString);
    }
    
    protected boolean hasName(Pattern paramAnonymousPattern, String paramAnonymousString)
    {
      return paramAnonymousPattern.pattern().equals(paramAnonymousString);
    }
  };
  private IOException lastException;
  private static Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");
  private static Pattern FIND_ANY_PATTERN = Pattern.compile("(?s).*");
  private static Pattern NON_ASCII_DIGIT = Pattern.compile("[\\p{javaDigit}&&[^0-9]]");
  private String groupSeparator = "\\,";
  private String decimalSeparator = "\\.";
  private String nanString = "NaN";
  private String infinityString = "Infinity";
  private String positivePrefix = "";
  private String negativePrefix = "\\-";
  private String positiveSuffix = "";
  private String negativeSuffix = "";
  private static volatile Pattern boolPattern;
  private static final String BOOLEAN_PATTERN = "true|false";
  private Pattern integerPattern;
  private String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
  private String non0Digit = "[\\p{javaDigit}&&[^0]]";
  private int SIMPLE_GROUP_INDEX = 5;
  private static volatile Pattern separatorPattern;
  private static volatile Pattern linePattern;
  private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r  ]";
  private static final String LINE_PATTERN = ".*(\r\n|[\n\r  ])|.+$";
  private Pattern floatPattern;
  private Pattern decimalPattern;
  
  private static Pattern boolPattern()
  {
    Pattern localPattern = boolPattern;
    if (localPattern == null) {
      boolPattern = localPattern = Pattern.compile("true|false", 2);
    }
    return localPattern;
  }
  
  private String buildIntegerPatternString()
  {
    String str1 = digits.substring(0, radix);
    String str2 = "((?i)[" + str1 + "]|\\p{javaDigit})";
    String str3 = "(" + non0Digit + str2 + "?" + str2 + "?(" + groupSeparator + str2 + str2 + str2 + ")+)";
    String str4 = "((" + str2 + "++)|" + str3 + ")";
    String str5 = "([-+]?(" + str4 + "))";
    String str6 = negativePrefix + str4 + negativeSuffix;
    String str7 = positivePrefix + str4 + positiveSuffix;
    return "(" + str5 + ")|(" + str7 + ")|(" + str6 + ")";
  }
  
  private Pattern integerPattern()
  {
    if (integerPattern == null) {
      integerPattern = ((Pattern)patternCache.forName(buildIntegerPatternString()));
    }
    return integerPattern;
  }
  
  private static Pattern separatorPattern()
  {
    Pattern localPattern = separatorPattern;
    if (localPattern == null) {
      separatorPattern = localPattern = Pattern.compile("\r\n|[\n\r  ]");
    }
    return localPattern;
  }
  
  private static Pattern linePattern()
  {
    Pattern localPattern = linePattern;
    if (localPattern == null) {
      linePattern = localPattern = Pattern.compile(".*(\r\n|[\n\r  ])|.+$");
    }
    return localPattern;
  }
  
  private void buildFloatAndDecimalPattern()
  {
    String str1 = "([0-9]|(\\p{javaDigit}))";
    String str2 = "([eE][+-]?" + str1 + "+)?";
    String str3 = "(" + non0Digit + str1 + "?" + str1 + "?(" + groupSeparator + str1 + str1 + str1 + ")+)";
    String str4 = "((" + str1 + "++)|" + str3 + ")";
    String str5 = "(" + str4 + "|" + str4 + decimalSeparator + str1 + "*+|" + decimalSeparator + str1 + "++)";
    String str6 = "(NaN|" + nanString + "|Infinity|" + infinityString + ")";
    String str7 = "(" + positivePrefix + str5 + positiveSuffix + str2 + ")";
    String str8 = "(" + negativePrefix + str5 + negativeSuffix + str2 + ")";
    String str9 = "(([-+]?" + str5 + str2 + ")|" + str7 + "|" + str8 + ")";
    String str10 = "[-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?";
    String str11 = "(" + positivePrefix + str6 + positiveSuffix + ")";
    String str12 = "(" + negativePrefix + str6 + negativeSuffix + ")";
    String str13 = "(([-+]?" + str6 + ")|" + str11 + "|" + str12 + ")";
    floatPattern = Pattern.compile(str9 + "|" + str10 + "|" + str13);
    decimalPattern = Pattern.compile(str9);
  }
  
  private Pattern floatPattern()
  {
    if (floatPattern == null) {
      buildFloatAndDecimalPattern();
    }
    return floatPattern;
  }
  
  private Pattern decimalPattern()
  {
    if (decimalPattern == null) {
      buildFloatAndDecimalPattern();
    }
    return decimalPattern;
  }
  
  private Scanner(Readable paramReadable, Pattern paramPattern)
  {
    assert (paramReadable != null) : "source should not be null";
    assert (paramPattern != null) : "pattern should not be null";
    source = paramReadable;
    delimPattern = paramPattern;
    buf = CharBuffer.allocate(1024);
    buf.limit(0);
    matcher = delimPattern.matcher(buf);
    matcher.useTransparentBounds(true);
    matcher.useAnchoringBounds(false);
    useLocale(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public Scanner(Readable paramReadable)
  {
    this((Readable)Objects.requireNonNull(paramReadable, "source"), WHITESPACE_PATTERN);
  }
  
  public Scanner(InputStream paramInputStream)
  {
    this(new InputStreamReader(paramInputStream), WHITESPACE_PATTERN);
  }
  
  public Scanner(InputStream paramInputStream, String paramString)
  {
    this(makeReadable((InputStream)Objects.requireNonNull(paramInputStream, "source"), toCharset(paramString)), WHITESPACE_PATTERN);
  }
  
  private static Charset toCharset(String paramString)
  {
    Objects.requireNonNull(paramString, "charsetName");
    try
    {
      return Charset.forName(paramString);
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new IllegalArgumentException(localIllegalCharsetNameException);
    }
  }
  
  private static Readable makeReadable(InputStream paramInputStream, Charset paramCharset)
  {
    return new InputStreamReader(paramInputStream, paramCharset);
  }
  
  public Scanner(File paramFile)
    throws FileNotFoundException
  {
    this(new FileInputStream(paramFile).getChannel());
  }
  
  public Scanner(File paramFile, String paramString)
    throws FileNotFoundException
  {
    this((File)Objects.requireNonNull(paramFile), toDecoder(paramString));
  }
  
  private Scanner(File paramFile, CharsetDecoder paramCharsetDecoder)
    throws FileNotFoundException
  {
    this(makeReadable(new FileInputStream(paramFile).getChannel(), paramCharsetDecoder));
  }
  
  private static CharsetDecoder toDecoder(String paramString)
  {
    Objects.requireNonNull(paramString, "charsetName");
    try
    {
      return Charset.forName(paramString).newDecoder();
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new IllegalArgumentException(paramString);
    }
  }
  
  private static Readable makeReadable(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder)
  {
    return Channels.newReader(paramReadableByteChannel, paramCharsetDecoder, -1);
  }
  
  public Scanner(Path paramPath)
    throws IOException
  {
    this(Files.newInputStream(paramPath, new OpenOption[0]));
  }
  
  public Scanner(Path paramPath, String paramString)
    throws IOException
  {
    this((Path)Objects.requireNonNull(paramPath), toCharset(paramString));
  }
  
  private Scanner(Path paramPath, Charset paramCharset)
    throws IOException
  {
    this(makeReadable(Files.newInputStream(paramPath, new OpenOption[0]), paramCharset));
  }
  
  public Scanner(String paramString)
  {
    this(new StringReader(paramString), WHITESPACE_PATTERN);
  }
  
  public Scanner(ReadableByteChannel paramReadableByteChannel)
  {
    this(makeReadable((ReadableByteChannel)Objects.requireNonNull(paramReadableByteChannel, "source")), WHITESPACE_PATTERN);
  }
  
  private static Readable makeReadable(ReadableByteChannel paramReadableByteChannel)
  {
    return makeReadable(paramReadableByteChannel, Charset.defaultCharset().newDecoder());
  }
  
  public Scanner(ReadableByteChannel paramReadableByteChannel, String paramString)
  {
    this(makeReadable((ReadableByteChannel)Objects.requireNonNull(paramReadableByteChannel, "source"), toDecoder(paramString)), WHITESPACE_PATTERN);
  }
  
  private void saveState()
  {
    savedScannerPosition = position;
  }
  
  private void revertState()
  {
    position = savedScannerPosition;
    savedScannerPosition = -1;
    skipped = false;
  }
  
  private boolean revertState(boolean paramBoolean)
  {
    position = savedScannerPosition;
    savedScannerPosition = -1;
    skipped = false;
    return paramBoolean;
  }
  
  private void cacheResult()
  {
    hasNextResult = matcher.group();
    hasNextPosition = matcher.end();
    hasNextPattern = matcher.pattern();
  }
  
  private void cacheResult(String paramString)
  {
    hasNextResult = paramString;
    hasNextPosition = matcher.end();
    hasNextPattern = matcher.pattern();
  }
  
  private void clearCaches()
  {
    hasNextPattern = null;
    typeCache = null;
  }
  
  private String getCachedResult()
  {
    position = hasNextPosition;
    hasNextPattern = null;
    typeCache = null;
    return hasNextResult;
  }
  
  private void useTypeCache()
  {
    if (closed) {
      throw new IllegalStateException("Scanner closed");
    }
    position = hasNextPosition;
    hasNextPattern = null;
    typeCache = null;
  }
  
  private void readInput()
  {
    if (buf.limit() == buf.capacity()) {
      makeSpace();
    }
    int i = buf.position();
    buf.position(buf.limit());
    buf.limit(buf.capacity());
    int j = 0;
    try
    {
      j = source.read(buf);
    }
    catch (IOException localIOException)
    {
      lastException = localIOException;
      j = -1;
    }
    if (j == -1)
    {
      sourceClosed = true;
      needInput = false;
    }
    if (j > 0) {
      needInput = false;
    }
    buf.limit(buf.position());
    buf.position(i);
  }
  
  private boolean makeSpace()
  {
    clearCaches();
    int i = savedScannerPosition == -1 ? position : savedScannerPosition;
    buf.position(i);
    if (i > 0)
    {
      buf.compact();
      translateSavedIndexes(i);
      position -= i;
      buf.flip();
      return true;
    }
    int j = buf.capacity() * 2;
    CharBuffer localCharBuffer = CharBuffer.allocate(j);
    localCharBuffer.put(buf);
    localCharBuffer.flip();
    translateSavedIndexes(i);
    position -= i;
    buf = localCharBuffer;
    matcher.reset(buf);
    return true;
  }
  
  private void translateSavedIndexes(int paramInt)
  {
    if (savedScannerPosition != -1) {
      savedScannerPosition -= paramInt;
    }
  }
  
  private void throwFor()
  {
    skipped = false;
    if ((sourceClosed) && (position == buf.limit())) {
      throw new NoSuchElementException();
    }
    throw new InputMismatchException();
  }
  
  private boolean hasTokenInBuffer()
  {
    matchValid = false;
    matcher.usePattern(delimPattern);
    matcher.region(position, buf.limit());
    if (matcher.lookingAt()) {
      position = matcher.end();
    }
    return position != buf.limit();
  }
  
  private String getCompleteTokenInBuffer(Pattern paramPattern)
  {
    matchValid = false;
    matcher.usePattern(delimPattern);
    if (!skipped)
    {
      matcher.region(position, buf.limit());
      if (matcher.lookingAt())
      {
        if ((matcher.hitEnd()) && (!sourceClosed))
        {
          needInput = true;
          return null;
        }
        skipped = true;
        position = matcher.end();
      }
    }
    if (position == buf.limit())
    {
      if (sourceClosed) {
        return null;
      }
      needInput = true;
      return null;
    }
    matcher.region(position, buf.limit());
    boolean bool = matcher.find();
    if ((bool) && (matcher.end() == position)) {
      bool = matcher.find();
    }
    if (bool)
    {
      if ((matcher.requireEnd()) && (!sourceClosed))
      {
        needInput = true;
        return null;
      }
      int i = matcher.start();
      if (paramPattern == null) {
        paramPattern = FIND_ANY_PATTERN;
      }
      matcher.usePattern(paramPattern);
      matcher.region(position, i);
      if (matcher.matches())
      {
        String str2 = matcher.group();
        position = matcher.end();
        return str2;
      }
      return null;
    }
    if (sourceClosed)
    {
      if (paramPattern == null) {
        paramPattern = FIND_ANY_PATTERN;
      }
      matcher.usePattern(paramPattern);
      matcher.region(position, buf.limit());
      if (matcher.matches())
      {
        String str1 = matcher.group();
        position = matcher.end();
        return str1;
      }
      return null;
    }
    needInput = true;
    return null;
  }
  
  private String findPatternInBuffer(Pattern paramPattern, int paramInt)
  {
    matchValid = false;
    matcher.usePattern(paramPattern);
    int i = buf.limit();
    int j = -1;
    int k = i;
    if (paramInt > 0)
    {
      j = position + paramInt;
      if (j < i) {
        k = j;
      }
    }
    matcher.region(position, k);
    if (matcher.find())
    {
      if ((matcher.hitEnd()) && (!sourceClosed))
      {
        if (k != j)
        {
          needInput = true;
          return null;
        }
        if ((k == j) && (matcher.requireEnd()))
        {
          needInput = true;
          return null;
        }
      }
      position = matcher.end();
      return matcher.group();
    }
    if (sourceClosed) {
      return null;
    }
    if ((paramInt == 0) || (k != j)) {
      needInput = true;
    }
    return null;
  }
  
  private String matchPatternInBuffer(Pattern paramPattern)
  {
    matchValid = false;
    matcher.usePattern(paramPattern);
    matcher.region(position, buf.limit());
    if (matcher.lookingAt())
    {
      if ((matcher.hitEnd()) && (!sourceClosed))
      {
        needInput = true;
        return null;
      }
      position = matcher.end();
      return matcher.group();
    }
    if (sourceClosed) {
      return null;
    }
    needInput = true;
    return null;
  }
  
  private void ensureOpen()
  {
    if (closed) {
      throw new IllegalStateException("Scanner closed");
    }
  }
  
  public void close()
  {
    if (closed) {
      return;
    }
    if ((source instanceof Closeable)) {
      try
      {
        ((Closeable)source).close();
      }
      catch (IOException localIOException)
      {
        lastException = localIOException;
      }
    }
    sourceClosed = true;
    source = null;
    closed = true;
  }
  
  public IOException ioException()
  {
    return lastException;
  }
  
  public Pattern delimiter()
  {
    return delimPattern;
  }
  
  public Scanner useDelimiter(Pattern paramPattern)
  {
    delimPattern = paramPattern;
    return this;
  }
  
  public Scanner useDelimiter(String paramString)
  {
    delimPattern = ((Pattern)patternCache.forName(paramString));
    return this;
  }
  
  public Locale locale()
  {
    return locale;
  }
  
  public Scanner useLocale(Locale paramLocale)
  {
    if (paramLocale.equals(locale)) {
      return this;
    }
    locale = paramLocale;
    DecimalFormat localDecimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(paramLocale);
    DecimalFormatSymbols localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
    groupSeparator = ("\\" + localDecimalFormatSymbols.getGroupingSeparator());
    decimalSeparator = ("\\" + localDecimalFormatSymbols.getDecimalSeparator());
    nanString = ("\\Q" + localDecimalFormatSymbols.getNaN() + "\\E");
    infinityString = ("\\Q" + localDecimalFormatSymbols.getInfinity() + "\\E");
    positivePrefix = localDecimalFormat.getPositivePrefix();
    if (positivePrefix.length() > 0) {
      positivePrefix = ("\\Q" + positivePrefix + "\\E");
    }
    negativePrefix = localDecimalFormat.getNegativePrefix();
    if (negativePrefix.length() > 0) {
      negativePrefix = ("\\Q" + negativePrefix + "\\E");
    }
    positiveSuffix = localDecimalFormat.getPositiveSuffix();
    if (positiveSuffix.length() > 0) {
      positiveSuffix = ("\\Q" + positiveSuffix + "\\E");
    }
    negativeSuffix = localDecimalFormat.getNegativeSuffix();
    if (negativeSuffix.length() > 0) {
      negativeSuffix = ("\\Q" + negativeSuffix + "\\E");
    }
    integerPattern = null;
    floatPattern = null;
    return this;
  }
  
  public int radix()
  {
    return defaultRadix;
  }
  
  public Scanner useRadix(int paramInt)
  {
    if ((paramInt < 2) || (paramInt > 36)) {
      throw new IllegalArgumentException("radix:" + paramInt);
    }
    if (defaultRadix == paramInt) {
      return this;
    }
    defaultRadix = paramInt;
    integerPattern = null;
    return this;
  }
  
  private void setRadix(int paramInt)
  {
    if (radix != paramInt)
    {
      integerPattern = null;
      radix = paramInt;
    }
  }
  
  public MatchResult match()
  {
    if (!matchValid) {
      throw new IllegalStateException("No match result available");
    }
    return matcher.toMatchResult();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("java.util.Scanner");
    localStringBuilder.append("[delimiters=" + delimPattern + "]");
    localStringBuilder.append("[position=" + position + "]");
    localStringBuilder.append("[match valid=" + matchValid + "]");
    localStringBuilder.append("[need input=" + needInput + "]");
    localStringBuilder.append("[source closed=" + sourceClosed + "]");
    localStringBuilder.append("[skipped=" + skipped + "]");
    localStringBuilder.append("[group separator=" + groupSeparator + "]");
    localStringBuilder.append("[decimal separator=" + decimalSeparator + "]");
    localStringBuilder.append("[positive prefix=" + positivePrefix + "]");
    localStringBuilder.append("[negative prefix=" + negativePrefix + "]");
    localStringBuilder.append("[positive suffix=" + positiveSuffix + "]");
    localStringBuilder.append("[negative suffix=" + negativeSuffix + "]");
    localStringBuilder.append("[NaN string=" + nanString + "]");
    localStringBuilder.append("[infinity string=" + infinityString + "]");
    return localStringBuilder.toString();
  }
  
  public boolean hasNext()
  {
    ensureOpen();
    saveState();
    while (!sourceClosed)
    {
      if (hasTokenInBuffer()) {
        return revertState(true);
      }
      readInput();
    }
    boolean bool = hasTokenInBuffer();
    return revertState(bool);
  }
  
  public String next()
  {
    ensureOpen();
    clearCaches();
    for (;;)
    {
      String str = getCompleteTokenInBuffer(null);
      if (str != null)
      {
        matchValid = true;
        skipped = false;
        return str;
      }
      if (needInput) {
        readInput();
      } else {
        throwFor();
      }
    }
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean hasNext(String paramString)
  {
    return hasNext((Pattern)patternCache.forName(paramString));
  }
  
  public String next(String paramString)
  {
    return next((Pattern)patternCache.forName(paramString));
  }
  
  public boolean hasNext(Pattern paramPattern)
  {
    ensureOpen();
    if (paramPattern == null) {
      throw new NullPointerException();
    }
    hasNextPattern = null;
    saveState();
    for (;;)
    {
      if (getCompleteTokenInBuffer(paramPattern) != null)
      {
        matchValid = true;
        cacheResult();
        return revertState(true);
      }
      if (!needInput) {
        break;
      }
      readInput();
    }
    return revertState(false);
  }
  
  public String next(Pattern paramPattern)
  {
    ensureOpen();
    if (paramPattern == null) {
      throw new NullPointerException();
    }
    if (hasNextPattern == paramPattern) {
      return getCachedResult();
    }
    clearCaches();
    for (;;)
    {
      String str = getCompleteTokenInBuffer(paramPattern);
      if (str != null)
      {
        matchValid = true;
        skipped = false;
        return str;
      }
      if (needInput) {
        readInput();
      } else {
        throwFor();
      }
    }
  }
  
  public boolean hasNextLine()
  {
    saveState();
    String str1 = findWithinHorizon(linePattern(), 0);
    if (str1 != null)
    {
      MatchResult localMatchResult = match();
      String str2 = localMatchResult.group(1);
      if (str2 != null)
      {
        str1 = str1.substring(0, str1.length() - str2.length());
        cacheResult(str1);
      }
      else
      {
        cacheResult();
      }
    }
    revertState();
    return str1 != null;
  }
  
  public String nextLine()
  {
    if (hasNextPattern == linePattern()) {
      return getCachedResult();
    }
    clearCaches();
    String str1 = findWithinHorizon(linePattern, 0);
    if (str1 == null) {
      throw new NoSuchElementException("No line found");
    }
    MatchResult localMatchResult = match();
    String str2 = localMatchResult.group(1);
    if (str2 != null) {
      str1 = str1.substring(0, str1.length() - str2.length());
    }
    if (str1 == null) {
      throw new NoSuchElementException();
    }
    return str1;
  }
  
  public String findInLine(String paramString)
  {
    return findInLine((Pattern)patternCache.forName(paramString));
  }
  
  public String findInLine(Pattern paramPattern)
  {
    ensureOpen();
    if (paramPattern == null) {
      throw new NullPointerException();
    }
    clearCaches();
    int i = 0;
    saveState();
    for (;;)
    {
      String str = findPatternInBuffer(separatorPattern(), 0);
      if (str != null)
      {
        i = matcher.start();
        break;
      }
      if (needInput)
      {
        readInput();
      }
      else
      {
        i = buf.limit();
        break;
      }
    }
    revertState();
    int j = i - position;
    if (j == 0) {
      return null;
    }
    return findWithinHorizon(paramPattern, j);
  }
  
  public String findWithinHorizon(String paramString, int paramInt)
  {
    return findWithinHorizon((Pattern)patternCache.forName(paramString), paramInt);
  }
  
  public String findWithinHorizon(Pattern paramPattern, int paramInt)
  {
    ensureOpen();
    if (paramPattern == null) {
      throw new NullPointerException();
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("horizon < 0");
    }
    clearCaches();
    for (;;)
    {
      String str = findPatternInBuffer(paramPattern, paramInt);
      if (str != null)
      {
        matchValid = true;
        return str;
      }
      if (!needInput) {
        break;
      }
      readInput();
    }
    return null;
  }
  
  public Scanner skip(Pattern paramPattern)
  {
    ensureOpen();
    if (paramPattern == null) {
      throw new NullPointerException();
    }
    clearCaches();
    for (;;)
    {
      String str = matchPatternInBuffer(paramPattern);
      if (str != null)
      {
        matchValid = true;
        position = matcher.end();
        return this;
      }
      if (needInput) {
        readInput();
      } else {
        throw new NoSuchElementException();
      }
    }
  }
  
  public Scanner skip(String paramString)
  {
    return skip((Pattern)patternCache.forName(paramString));
  }
  
  public boolean hasNextBoolean()
  {
    return hasNext(boolPattern());
  }
  
  public boolean nextBoolean()
  {
    clearCaches();
    return Boolean.parseBoolean(next(boolPattern()));
  }
  
  public boolean hasNextByte()
  {
    return hasNextByte(defaultRadix);
  }
  
  public boolean hasNextByte(int paramInt)
  {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool) {
      try
      {
        String str = matcher.group(SIMPLE_GROUP_INDEX) == null ? processIntegerToken(hasNextResult) : hasNextResult;
        typeCache = Byte.valueOf(Byte.parseByte(str, paramInt));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public byte nextByte()
  {
    return nextByte(defaultRadix);
  }
  
  public byte nextByte(int paramInt)
  {
    if ((typeCache != null) && ((typeCache instanceof Byte)) && (radix == paramInt))
    {
      byte b = ((Byte)typeCache).byteValue();
      useTypeCache();
      return b;
    }
    setRadix(paramInt);
    clearCaches();
    try
    {
      String str = next(integerPattern());
      if (matcher.group(SIMPLE_GROUP_INDEX) == null) {
        str = processIntegerToken(str);
      }
      return Byte.parseByte(str, paramInt);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public boolean hasNextShort()
  {
    return hasNextShort(defaultRadix);
  }
  
  public boolean hasNextShort(int paramInt)
  {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool) {
      try
      {
        String str = matcher.group(SIMPLE_GROUP_INDEX) == null ? processIntegerToken(hasNextResult) : hasNextResult;
        typeCache = Short.valueOf(Short.parseShort(str, paramInt));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public short nextShort()
  {
    return nextShort(defaultRadix);
  }
  
  public short nextShort(int paramInt)
  {
    if ((typeCache != null) && ((typeCache instanceof Short)) && (radix == paramInt))
    {
      short s = ((Short)typeCache).shortValue();
      useTypeCache();
      return s;
    }
    setRadix(paramInt);
    clearCaches();
    try
    {
      String str = next(integerPattern());
      if (matcher.group(SIMPLE_GROUP_INDEX) == null) {
        str = processIntegerToken(str);
      }
      return Short.parseShort(str, paramInt);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public boolean hasNextInt()
  {
    return hasNextInt(defaultRadix);
  }
  
  public boolean hasNextInt(int paramInt)
  {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool) {
      try
      {
        String str = matcher.group(SIMPLE_GROUP_INDEX) == null ? processIntegerToken(hasNextResult) : hasNextResult;
        typeCache = Integer.valueOf(Integer.parseInt(str, paramInt));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  private String processIntegerToken(String paramString)
  {
    String str = paramString.replaceAll("" + groupSeparator, "");
    int i = 0;
    int j = negativePrefix.length();
    if ((j > 0) && (str.startsWith(negativePrefix)))
    {
      i = 1;
      str = str.substring(j);
    }
    int k = negativeSuffix.length();
    if ((k > 0) && (str.endsWith(negativeSuffix)))
    {
      i = 1;
      str = str.substring(str.length() - k, str.length());
    }
    if (i != 0) {
      str = "-" + str;
    }
    return str;
  }
  
  public int nextInt()
  {
    return nextInt(defaultRadix);
  }
  
  public int nextInt(int paramInt)
  {
    if ((typeCache != null) && ((typeCache instanceof Integer)) && (radix == paramInt))
    {
      int i = ((Integer)typeCache).intValue();
      useTypeCache();
      return i;
    }
    setRadix(paramInt);
    clearCaches();
    try
    {
      String str = next(integerPattern());
      if (matcher.group(SIMPLE_GROUP_INDEX) == null) {
        str = processIntegerToken(str);
      }
      return Integer.parseInt(str, paramInt);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public boolean hasNextLong()
  {
    return hasNextLong(defaultRadix);
  }
  
  public boolean hasNextLong(int paramInt)
  {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool) {
      try
      {
        String str = matcher.group(SIMPLE_GROUP_INDEX) == null ? processIntegerToken(hasNextResult) : hasNextResult;
        typeCache = Long.valueOf(Long.parseLong(str, paramInt));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public long nextLong()
  {
    return nextLong(defaultRadix);
  }
  
  public long nextLong(int paramInt)
  {
    if ((typeCache != null) && ((typeCache instanceof Long)) && (radix == paramInt))
    {
      long l = ((Long)typeCache).longValue();
      useTypeCache();
      return l;
    }
    setRadix(paramInt);
    clearCaches();
    try
    {
      String str = next(integerPattern());
      if (matcher.group(SIMPLE_GROUP_INDEX) == null) {
        str = processIntegerToken(str);
      }
      return Long.parseLong(str, paramInt);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  private String processFloatToken(String paramString)
  {
    String str = paramString.replaceAll(groupSeparator, "");
    if (!decimalSeparator.equals("\\.")) {
      str = str.replaceAll(decimalSeparator, ".");
    }
    int i = 0;
    int j = negativePrefix.length();
    if ((j > 0) && (str.startsWith(negativePrefix)))
    {
      i = 1;
      str = str.substring(j);
    }
    int k = negativeSuffix.length();
    if ((k > 0) && (str.endsWith(negativeSuffix)))
    {
      i = 1;
      str = str.substring(str.length() - k, str.length());
    }
    if (str.equals(nanString)) {
      str = "NaN";
    }
    if (str.equals(infinityString)) {
      str = "Infinity";
    }
    if (i != 0) {
      str = "-" + str;
    }
    Matcher localMatcher = NON_ASCII_DIGIT.matcher(str);
    if (localMatcher.find())
    {
      StringBuilder localStringBuilder = new StringBuilder();
      for (int m = 0; m < str.length(); m++)
      {
        char c = str.charAt(m);
        if (Character.isDigit(c))
        {
          int n = Character.digit(c, 10);
          if (n != -1) {
            localStringBuilder.append(n);
          } else {
            localStringBuilder.append(c);
          }
        }
        else
        {
          localStringBuilder.append(c);
        }
      }
      str = localStringBuilder.toString();
    }
    return str;
  }
  
  public boolean hasNextFloat()
  {
    setRadix(10);
    boolean bool = hasNext(floatPattern());
    if (bool) {
      try
      {
        String str = processFloatToken(hasNextResult);
        typeCache = Float.valueOf(Float.parseFloat(str));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public float nextFloat()
  {
    if ((typeCache != null) && ((typeCache instanceof Float)))
    {
      float f = ((Float)typeCache).floatValue();
      useTypeCache();
      return f;
    }
    setRadix(10);
    clearCaches();
    try
    {
      return Float.parseFloat(processFloatToken(next(floatPattern())));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public boolean hasNextDouble()
  {
    setRadix(10);
    boolean bool = hasNext(floatPattern());
    if (bool) {
      try
      {
        String str = processFloatToken(hasNextResult);
        typeCache = Double.valueOf(Double.parseDouble(str));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public double nextDouble()
  {
    if ((typeCache != null) && ((typeCache instanceof Double)))
    {
      double d = ((Double)typeCache).doubleValue();
      useTypeCache();
      return d;
    }
    setRadix(10);
    clearCaches();
    try
    {
      return Double.parseDouble(processFloatToken(next(floatPattern())));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public boolean hasNextBigInteger()
  {
    return hasNextBigInteger(defaultRadix);
  }
  
  public boolean hasNextBigInteger(int paramInt)
  {
    setRadix(paramInt);
    boolean bool = hasNext(integerPattern());
    if (bool) {
      try
      {
        String str = matcher.group(SIMPLE_GROUP_INDEX) == null ? processIntegerToken(hasNextResult) : hasNextResult;
        typeCache = new BigInteger(str, paramInt);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public BigInteger nextBigInteger()
  {
    return nextBigInteger(defaultRadix);
  }
  
  public BigInteger nextBigInteger(int paramInt)
  {
    Object localObject;
    if ((typeCache != null) && ((typeCache instanceof BigInteger)) && (radix == paramInt))
    {
      localObject = (BigInteger)typeCache;
      useTypeCache();
      return (BigInteger)localObject;
    }
    setRadix(paramInt);
    clearCaches();
    try
    {
      localObject = next(integerPattern());
      if (matcher.group(SIMPLE_GROUP_INDEX) == null) {
        localObject = processIntegerToken((String)localObject);
      }
      return new BigInteger((String)localObject, paramInt);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public boolean hasNextBigDecimal()
  {
    setRadix(10);
    boolean bool = hasNext(decimalPattern());
    if (bool) {
      try
      {
        String str = processFloatToken(hasNextResult);
        typeCache = new BigDecimal(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        bool = false;
      }
    }
    return bool;
  }
  
  public BigDecimal nextBigDecimal()
  {
    Object localObject;
    if ((typeCache != null) && ((typeCache instanceof BigDecimal)))
    {
      localObject = (BigDecimal)typeCache;
      useTypeCache();
      return (BigDecimal)localObject;
    }
    setRadix(10);
    clearCaches();
    try
    {
      localObject = processFloatToken(next(decimalPattern()));
      return new BigDecimal((String)localObject);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      position = matcher.start();
      throw new InputMismatchException(localNumberFormatException.getMessage());
    }
  }
  
  public Scanner reset()
  {
    delimPattern = WHITESPACE_PATTERN;
    useLocale(Locale.getDefault(Locale.Category.FORMAT));
    useRadix(10);
    clearCaches();
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\Scanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
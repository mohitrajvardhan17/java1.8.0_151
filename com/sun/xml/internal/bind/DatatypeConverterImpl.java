package com.sun.xml.internal.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@Deprecated
public final class DatatypeConverterImpl
  implements DatatypeConverterInterface
{
  @Deprecated
  public static final DatatypeConverterInterface theInstance = new DatatypeConverterImpl();
  private static final byte[] decodeMap = initDecodeMap();
  private static final byte PADDING = 127;
  private static final char[] encodeMap = initEncodeMap();
  private static final Map<ClassLoader, DatatypeFactory> DF_CACHE = Collections.synchronizedMap(new WeakHashMap());
  @Deprecated
  private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
  
  protected DatatypeConverterImpl() {}
  
  public static BigInteger _parseInteger(CharSequence paramCharSequence)
  {
    return new BigInteger(removeOptionalPlus(WhiteSpaceProcessor.trim(paramCharSequence)).toString());
  }
  
  public static String _printInteger(BigInteger paramBigInteger)
  {
    return paramBigInteger.toString();
  }
  
  public static int _parseInt(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    int j = 1;
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      char c = paramCharSequence.charAt(m);
      if (!WhiteSpaceProcessor.isWhiteSpace(c)) {
        if (('0' <= c) && (c <= '9')) {
          k = k * 10 + (c - '0');
        } else if (c == '-') {
          j = -1;
        } else if (c != '+') {
          throw new NumberFormatException("Not a number: " + paramCharSequence);
        }
      }
    }
    return k * j;
  }
  
  public static long _parseLong(CharSequence paramCharSequence)
  {
    return Long.valueOf(removeOptionalPlus(WhiteSpaceProcessor.trim(paramCharSequence)).toString()).longValue();
  }
  
  public static short _parseShort(CharSequence paramCharSequence)
  {
    return (short)_parseInt(paramCharSequence);
  }
  
  public static String _printShort(short paramShort)
  {
    return String.valueOf(paramShort);
  }
  
  public static BigDecimal _parseDecimal(CharSequence paramCharSequence)
  {
    paramCharSequence = WhiteSpaceProcessor.trim(paramCharSequence);
    if (paramCharSequence.length() <= 0) {
      return null;
    }
    return new BigDecimal(paramCharSequence.toString());
  }
  
  public static float _parseFloat(CharSequence paramCharSequence)
  {
    String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
    if (str.equals("NaN")) {
      return NaN.0F;
    }
    if (str.equals("INF")) {
      return Float.POSITIVE_INFINITY;
    }
    if (str.equals("-INF")) {
      return Float.NEGATIVE_INFINITY;
    }
    if ((str.length() == 0) || (!isDigitOrPeriodOrSign(str.charAt(0))) || (!isDigitOrPeriodOrSign(str.charAt(str.length() - 1)))) {
      throw new NumberFormatException();
    }
    return Float.parseFloat(str);
  }
  
  public static String _printFloat(float paramFloat)
  {
    if (Float.isNaN(paramFloat)) {
      return "NaN";
    }
    if (paramFloat == Float.POSITIVE_INFINITY) {
      return "INF";
    }
    if (paramFloat == Float.NEGATIVE_INFINITY) {
      return "-INF";
    }
    return String.valueOf(paramFloat);
  }
  
  public static double _parseDouble(CharSequence paramCharSequence)
  {
    String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
    if (str.equals("NaN")) {
      return NaN.0D;
    }
    if (str.equals("INF")) {
      return Double.POSITIVE_INFINITY;
    }
    if (str.equals("-INF")) {
      return Double.NEGATIVE_INFINITY;
    }
    if ((str.length() == 0) || (!isDigitOrPeriodOrSign(str.charAt(0))) || (!isDigitOrPeriodOrSign(str.charAt(str.length() - 1)))) {
      throw new NumberFormatException(str);
    }
    return Double.parseDouble(str);
  }
  
  public static Boolean _parseBoolean(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return null;
    }
    int i = 0;
    int j = paramCharSequence.length();
    boolean bool = false;
    if (paramCharSequence.length() <= 0) {
      return null;
    }
    char c;
    do
    {
      c = paramCharSequence.charAt(i++);
    } while ((WhiteSpaceProcessor.isWhiteSpace(c)) && (i < j));
    int k = 0;
    switch (c)
    {
    case '1': 
      bool = true;
      break;
    case '0': 
      bool = false;
      break;
    case 't': 
      String str1 = "rue";
      do
      {
        c = paramCharSequence.charAt(i++);
      } while ((str1.charAt(k++) == c) && (i < j) && (k < 3));
      if (k == 3) {
        bool = true;
      } else {
        return Boolean.valueOf(false);
      }
      break;
    case 'f': 
      String str2 = "alse";
      do
      {
        c = paramCharSequence.charAt(i++);
      } while ((str2.charAt(k++) == c) && (i < j) && (k < 4));
      if (k == 4) {
        bool = false;
      } else {
        return Boolean.valueOf(false);
      }
      break;
    }
    if (i < j) {
      do
      {
        c = paramCharSequence.charAt(i++);
      } while ((WhiteSpaceProcessor.isWhiteSpace(c)) && (i < j));
    }
    if (i == j) {
      return Boolean.valueOf(bool);
    }
    return null;
  }
  
  public static String _printBoolean(boolean paramBoolean)
  {
    return paramBoolean ? "true" : "false";
  }
  
  public static byte _parseByte(CharSequence paramCharSequence)
  {
    return (byte)_parseInt(paramCharSequence);
  }
  
  public static String _printByte(byte paramByte)
  {
    return String.valueOf(paramByte);
  }
  
  public static QName _parseQName(CharSequence paramCharSequence, NamespaceContext paramNamespaceContext)
  {
    int i = paramCharSequence.length();
    for (int j = 0; (j < i) && (WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(j))); j++) {}
    for (int k = i; (k > j) && (WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(k - 1))); k--) {}
    if (k == j) {
      throw new IllegalArgumentException("input is empty");
    }
    for (int m = j + 1; (m < k) && (paramCharSequence.charAt(m) != ':'); m++) {}
    String str1;
    String str2;
    String str3;
    if (m == k)
    {
      str1 = paramNamespaceContext.getNamespaceURI("");
      str2 = paramCharSequence.subSequence(j, k).toString();
      str3 = "";
    }
    else
    {
      str3 = paramCharSequence.subSequence(j, m).toString();
      str2 = paramCharSequence.subSequence(m + 1, k).toString();
      str1 = paramNamespaceContext.getNamespaceURI(str3);
      if ((str1 == null) || (str1.length() == 0)) {
        throw new IllegalArgumentException("prefix " + str3 + " is not bound to a namespace");
      }
    }
    return new QName(str1, str2, str3);
  }
  
  public static GregorianCalendar _parseDateTime(CharSequence paramCharSequence)
  {
    String str = WhiteSpaceProcessor.trim(paramCharSequence).toString();
    return getDatatypeFactory().newXMLGregorianCalendar(str).toGregorianCalendar();
  }
  
  public static String _printDateTime(Calendar paramCalendar)
  {
    return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", paramCalendar);
  }
  
  public static String _printDate(Calendar paramCalendar)
  {
    return CalendarFormatter.doFormat("%Y-%M-%D" + "%z", paramCalendar);
  }
  
  public static String _printInt(int paramInt)
  {
    return String.valueOf(paramInt);
  }
  
  public static String _printLong(long paramLong)
  {
    return String.valueOf(paramLong);
  }
  
  public static String _printDecimal(BigDecimal paramBigDecimal)
  {
    return paramBigDecimal.toPlainString();
  }
  
  public static String _printDouble(double paramDouble)
  {
    if (Double.isNaN(paramDouble)) {
      return "NaN";
    }
    if (paramDouble == Double.POSITIVE_INFINITY) {
      return "INF";
    }
    if (paramDouble == Double.NEGATIVE_INFINITY) {
      return "-INF";
    }
    return String.valueOf(paramDouble);
  }
  
  public static String _printQName(QName paramQName, NamespaceContext paramNamespaceContext)
  {
    String str2 = paramNamespaceContext.getPrefix(paramQName.getNamespaceURI());
    String str3 = paramQName.getLocalPart();
    String str1;
    if ((str2 == null) || (str2.length() == 0)) {
      str1 = str3;
    } else {
      str1 = str2 + ':' + str3;
    }
    return str1;
  }
  
  private static byte[] initDecodeMap()
  {
    byte[] arrayOfByte = new byte[''];
    for (int i = 0; i < 128; i++) {
      arrayOfByte[i] = -1;
    }
    for (i = 65; i <= 90; i++) {
      arrayOfByte[i] = ((byte)(i - 65));
    }
    for (i = 97; i <= 122; i++) {
      arrayOfByte[i] = ((byte)(i - 97 + 26));
    }
    for (i = 48; i <= 57; i++) {
      arrayOfByte[i] = ((byte)(i - 48 + 52));
    }
    arrayOfByte[43] = 62;
    arrayOfByte[47] = 63;
    arrayOfByte[61] = Byte.MAX_VALUE;
    return arrayOfByte;
  }
  
  private static int guessLength(String paramString)
  {
    int i = paramString.length();
    for (int j = i - 1; j >= 0; j--)
    {
      k = decodeMap[paramString.charAt(j)];
      if (k != 127)
      {
        if (k != -1) {
          break;
        }
        return paramString.length() / 4 * 3;
      }
    }
    j++;
    int k = i - j;
    if (k > 2) {
      return paramString.length() / 4 * 3;
    }
    return paramString.length() / 4 * 3 - k;
  }
  
  public static byte[] _parseBase64Binary(String paramString)
  {
    int i = guessLength(paramString);
    byte[] arrayOfByte1 = new byte[i];
    int j = 0;
    int k = paramString.length();
    byte[] arrayOfByte2 = new byte[4];
    int n = 0;
    for (int m = 0; m < k; m++)
    {
      int i1 = paramString.charAt(m);
      int i2 = decodeMap[i1];
      if (i2 != -1) {
        arrayOfByte2[(n++)] = i2;
      }
      if (n == 4)
      {
        arrayOfByte1[(j++)] = ((byte)(arrayOfByte2[0] << 2 | arrayOfByte2[1] >> 4));
        if (arrayOfByte2[2] != Byte.MAX_VALUE) {
          arrayOfByte1[(j++)] = ((byte)(arrayOfByte2[1] << 4 | arrayOfByte2[2] >> 2));
        }
        if (arrayOfByte2[3] != Byte.MAX_VALUE) {
          arrayOfByte1[(j++)] = ((byte)(arrayOfByte2[2] << 6 | arrayOfByte2[3]));
        }
        n = 0;
      }
    }
    if (i == j) {
      return arrayOfByte1;
    }
    byte[] arrayOfByte3 = new byte[j];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, j);
    return arrayOfByte3;
  }
  
  private static char[] initEncodeMap()
  {
    char[] arrayOfChar = new char[64];
    for (int i = 0; i < 26; i++) {
      arrayOfChar[i] = ((char)(65 + i));
    }
    for (i = 26; i < 52; i++) {
      arrayOfChar[i] = ((char)(97 + (i - 26)));
    }
    for (i = 52; i < 62; i++) {
      arrayOfChar[i] = ((char)(48 + (i - 52)));
    }
    arrayOfChar[62] = '+';
    arrayOfChar[63] = '/';
    return arrayOfChar;
  }
  
  public static char encode(int paramInt)
  {
    return encodeMap[(paramInt & 0x3F)];
  }
  
  public static byte encodeByte(int paramInt)
  {
    return (byte)encodeMap[(paramInt & 0x3F)];
  }
  
  public static String _printBase64Binary(byte[] paramArrayOfByte)
  {
    return _printBase64Binary(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static String _printBase64Binary(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[(paramInt2 + 2) / 3 * 4];
    int i = _printBase64Binary(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar, 0);
    assert (i == arrayOfChar.length);
    return new String(arrayOfChar);
  }
  
  public static int _printBase64Binary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    int i = paramInt2;
    for (int j = paramInt1; i >= 3; j += 3)
    {
      paramArrayOfChar[(paramInt3++)] = encode(paramArrayOfByte[j] >> 2);
      paramArrayOfChar[(paramInt3++)] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[(j + 1)] >> 4 & 0xF);
      paramArrayOfChar[(paramInt3++)] = encode((paramArrayOfByte[(j + 1)] & 0xF) << 2 | paramArrayOfByte[(j + 2)] >> 6 & 0x3);
      paramArrayOfChar[(paramInt3++)] = encode(paramArrayOfByte[(j + 2)] & 0x3F);
      i -= 3;
    }
    if (i == 1)
    {
      paramArrayOfChar[(paramInt3++)] = encode(paramArrayOfByte[j] >> 2);
      paramArrayOfChar[(paramInt3++)] = encode((paramArrayOfByte[j] & 0x3) << 4);
      paramArrayOfChar[(paramInt3++)] = '=';
      paramArrayOfChar[(paramInt3++)] = '=';
    }
    if (i == 2)
    {
      paramArrayOfChar[(paramInt3++)] = encode(paramArrayOfByte[j] >> 2);
      paramArrayOfChar[(paramInt3++)] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[(j + 1)] >> 4 & 0xF);
      paramArrayOfChar[(paramInt3++)] = encode((paramArrayOfByte[(j + 1)] & 0xF) << 2);
      paramArrayOfChar[(paramInt3++)] = '=';
    }
    return paramInt3;
  }
  
  public static void _printBase64Binary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    int i = paramInt2;
    char[] arrayOfChar = new char[4];
    for (int j = paramInt1; i >= 3; j += 3)
    {
      arrayOfChar[0] = encode(paramArrayOfByte[j] >> 2);
      arrayOfChar[1] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[(j + 1)] >> 4 & 0xF);
      arrayOfChar[2] = encode((paramArrayOfByte[(j + 1)] & 0xF) << 2 | paramArrayOfByte[(j + 2)] >> 6 & 0x3);
      arrayOfChar[3] = encode(paramArrayOfByte[(j + 2)] & 0x3F);
      paramXMLStreamWriter.writeCharacters(arrayOfChar, 0, 4);
      i -= 3;
    }
    if (i == 1)
    {
      arrayOfChar[0] = encode(paramArrayOfByte[j] >> 2);
      arrayOfChar[1] = encode((paramArrayOfByte[j] & 0x3) << 4);
      arrayOfChar[2] = '=';
      arrayOfChar[3] = '=';
      paramXMLStreamWriter.writeCharacters(arrayOfChar, 0, 4);
    }
    if (i == 2)
    {
      arrayOfChar[0] = encode(paramArrayOfByte[j] >> 2);
      arrayOfChar[1] = encode((paramArrayOfByte[j] & 0x3) << 4 | paramArrayOfByte[(j + 1)] >> 4 & 0xF);
      arrayOfChar[2] = encode((paramArrayOfByte[(j + 1)] & 0xF) << 2);
      arrayOfChar[3] = '=';
      paramXMLStreamWriter.writeCharacters(arrayOfChar, 0, 4);
    }
  }
  
  public static int _printBase64Binary(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3)
  {
    byte[] arrayOfByte = paramArrayOfByte2;
    int i = paramInt2;
    for (int j = paramInt1; i >= 3; j += 3)
    {
      arrayOfByte[(paramInt3++)] = encodeByte(paramArrayOfByte1[j] >> 2);
      arrayOfByte[(paramInt3++)] = encodeByte((paramArrayOfByte1[j] & 0x3) << 4 | paramArrayOfByte1[(j + 1)] >> 4 & 0xF);
      arrayOfByte[(paramInt3++)] = encodeByte((paramArrayOfByte1[(j + 1)] & 0xF) << 2 | paramArrayOfByte1[(j + 2)] >> 6 & 0x3);
      arrayOfByte[(paramInt3++)] = encodeByte(paramArrayOfByte1[(j + 2)] & 0x3F);
      i -= 3;
    }
    if (i == 1)
    {
      arrayOfByte[(paramInt3++)] = encodeByte(paramArrayOfByte1[j] >> 2);
      arrayOfByte[(paramInt3++)] = encodeByte((paramArrayOfByte1[j] & 0x3) << 4);
      arrayOfByte[(paramInt3++)] = 61;
      arrayOfByte[(paramInt3++)] = 61;
    }
    if (i == 2)
    {
      arrayOfByte[(paramInt3++)] = encodeByte(paramArrayOfByte1[j] >> 2);
      arrayOfByte[(paramInt3++)] = encodeByte((paramArrayOfByte1[j] & 0x3) << 4 | paramArrayOfByte1[(j + 1)] >> 4 & 0xF);
      arrayOfByte[(paramInt3++)] = encodeByte((paramArrayOfByte1[(j + 1)] & 0xF) << 2);
      arrayOfByte[(paramInt3++)] = 61;
    }
    return paramInt3;
  }
  
  private static CharSequence removeOptionalPlus(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    if ((i <= 1) || (paramCharSequence.charAt(0) != '+')) {
      return paramCharSequence;
    }
    paramCharSequence = paramCharSequence.subSequence(1, i);
    int j = paramCharSequence.charAt(0);
    if ((48 <= j) && (j <= 57)) {
      return paramCharSequence;
    }
    if (46 == j) {
      return paramCharSequence;
    }
    throw new NumberFormatException();
  }
  
  private static boolean isDigitOrPeriodOrSign(char paramChar)
  {
    if (('0' <= paramChar) && (paramChar <= '9')) {
      return true;
    }
    return (paramChar == '+') || (paramChar == '-') || (paramChar == '.');
  }
  
  public static DatatypeFactory getDatatypeFactory()
  {
    ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
    DatatypeFactory localDatatypeFactory = (DatatypeFactory)DF_CACHE.get(localClassLoader);
    if (localDatatypeFactory == null) {
      synchronized (DatatypeConverterImpl.class)
      {
        localDatatypeFactory = (DatatypeFactory)DF_CACHE.get(localClassLoader);
        if (localDatatypeFactory == null)
        {
          try
          {
            localDatatypeFactory = DatatypeFactory.newInstance();
          }
          catch (DatatypeConfigurationException localDatatypeConfigurationException)
          {
            throw new Error(Messages.FAILED_TO_INITIALE_DATATYPE_FACTORY.format(new Object[0]), localDatatypeConfigurationException);
          }
          DF_CACHE.put(localClassLoader, localDatatypeFactory);
        }
      }
    }
    return localDatatypeFactory;
  }
  
  @Deprecated
  public String parseString(String paramString)
  {
    return paramString;
  }
  
  @Deprecated
  public BigInteger parseInteger(String paramString)
  {
    return _parseInteger(paramString);
  }
  
  @Deprecated
  public String printInteger(BigInteger paramBigInteger)
  {
    return _printInteger(paramBigInteger);
  }
  
  @Deprecated
  public int parseInt(String paramString)
  {
    return _parseInt(paramString);
  }
  
  @Deprecated
  public long parseLong(String paramString)
  {
    return _parseLong(paramString);
  }
  
  @Deprecated
  public short parseShort(String paramString)
  {
    return _parseShort(paramString);
  }
  
  @Deprecated
  public String printShort(short paramShort)
  {
    return _printShort(paramShort);
  }
  
  @Deprecated
  public BigDecimal parseDecimal(String paramString)
  {
    return _parseDecimal(paramString);
  }
  
  @Deprecated
  public float parseFloat(String paramString)
  {
    return _parseFloat(paramString);
  }
  
  @Deprecated
  public String printFloat(float paramFloat)
  {
    return _printFloat(paramFloat);
  }
  
  @Deprecated
  public double parseDouble(String paramString)
  {
    return _parseDouble(paramString);
  }
  
  @Deprecated
  public boolean parseBoolean(String paramString)
  {
    Boolean localBoolean = _parseBoolean(paramString);
    return localBoolean == null ? false : localBoolean.booleanValue();
  }
  
  @Deprecated
  public String printBoolean(boolean paramBoolean)
  {
    return paramBoolean ? "true" : "false";
  }
  
  @Deprecated
  public byte parseByte(String paramString)
  {
    return _parseByte(paramString);
  }
  
  @Deprecated
  public String printByte(byte paramByte)
  {
    return _printByte(paramByte);
  }
  
  @Deprecated
  public QName parseQName(String paramString, NamespaceContext paramNamespaceContext)
  {
    return _parseQName(paramString, paramNamespaceContext);
  }
  
  @Deprecated
  public Calendar parseDateTime(String paramString)
  {
    return _parseDateTime(paramString);
  }
  
  @Deprecated
  public String printDateTime(Calendar paramCalendar)
  {
    return _printDateTime(paramCalendar);
  }
  
  @Deprecated
  public byte[] parseBase64Binary(String paramString)
  {
    return _parseBase64Binary(paramString);
  }
  
  @Deprecated
  public byte[] parseHexBinary(String paramString)
  {
    int i = paramString.length();
    if (i % 2 != 0) {
      throw new IllegalArgumentException("hexBinary needs to be even-length: " + paramString);
    }
    byte[] arrayOfByte = new byte[i / 2];
    for (int j = 0; j < i; j += 2)
    {
      int k = hexToBin(paramString.charAt(j));
      int m = hexToBin(paramString.charAt(j + 1));
      if ((k == -1) || (m == -1)) {
        throw new IllegalArgumentException("contains illegal character for hexBinary: " + paramString);
      }
      arrayOfByte[(j / 2)] = ((byte)(k * 16 + m));
    }
    return arrayOfByte;
  }
  
  @Deprecated
  private static int hexToBin(char paramChar)
  {
    if (('0' <= paramChar) && (paramChar <= '9')) {
      return paramChar - '0';
    }
    if (('A' <= paramChar) && (paramChar <= 'F')) {
      return paramChar - 'A' + 10;
    }
    if (('a' <= paramChar) && (paramChar <= 'f')) {
      return paramChar - 'a' + 10;
    }
    return -1;
  }
  
  @Deprecated
  public String printHexBinary(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
    for (int k : paramArrayOfByte)
    {
      localStringBuilder.append(hexCode[(k >> 4 & 0xF)]);
      localStringBuilder.append(hexCode[(k & 0xF)]);
    }
    return localStringBuilder.toString();
  }
  
  @Deprecated
  public long parseUnsignedInt(String paramString)
  {
    return _parseLong(paramString);
  }
  
  @Deprecated
  public String printUnsignedInt(long paramLong)
  {
    return _printLong(paramLong);
  }
  
  @Deprecated
  public int parseUnsignedShort(String paramString)
  {
    return _parseInt(paramString);
  }
  
  @Deprecated
  public Calendar parseTime(String paramString)
  {
    return getDatatypeFactory().newXMLGregorianCalendar(paramString).toGregorianCalendar();
  }
  
  @Deprecated
  public String printTime(Calendar paramCalendar)
  {
    return CalendarFormatter.doFormat("%h:%m:%s%z", paramCalendar);
  }
  
  @Deprecated
  public Calendar parseDate(String paramString)
  {
    return getDatatypeFactory().newXMLGregorianCalendar(paramString).toGregorianCalendar();
  }
  
  @Deprecated
  public String printDate(Calendar paramCalendar)
  {
    return _printDate(paramCalendar);
  }
  
  @Deprecated
  public String parseAnySimpleType(String paramString)
  {
    return paramString;
  }
  
  @Deprecated
  public String printString(String paramString)
  {
    return paramString;
  }
  
  @Deprecated
  public String printInt(int paramInt)
  {
    return _printInt(paramInt);
  }
  
  @Deprecated
  public String printLong(long paramLong)
  {
    return _printLong(paramLong);
  }
  
  @Deprecated
  public String printDecimal(BigDecimal paramBigDecimal)
  {
    return _printDecimal(paramBigDecimal);
  }
  
  @Deprecated
  public String printDouble(double paramDouble)
  {
    return _printDouble(paramDouble);
  }
  
  @Deprecated
  public String printQName(QName paramQName, NamespaceContext paramNamespaceContext)
  {
    return _printQName(paramQName, paramNamespaceContext);
  }
  
  @Deprecated
  public String printBase64Binary(byte[] paramArrayOfByte)
  {
    return _printBase64Binary(paramArrayOfByte);
  }
  
  @Deprecated
  public String printUnsignedShort(int paramInt)
  {
    return String.valueOf(paramInt);
  }
  
  @Deprecated
  public String printAnySimpleType(String paramString)
  {
    return paramString;
  }
  
  private static final class CalendarFormatter
  {
    private CalendarFormatter() {}
    
    public static String doFormat(String paramString, Calendar paramCalendar)
      throws IllegalArgumentException
    {
      int i = 0;
      int j = paramString.length();
      StringBuilder localStringBuilder = new StringBuilder();
      while (i < j)
      {
        char c = paramString.charAt(i++);
        if (c != '%') {
          localStringBuilder.append(c);
        } else {
          switch (paramString.charAt(i++))
          {
          case 'Y': 
            formatYear(paramCalendar, localStringBuilder);
            break;
          case 'M': 
            formatMonth(paramCalendar, localStringBuilder);
            break;
          case 'D': 
            formatDays(paramCalendar, localStringBuilder);
            break;
          case 'h': 
            formatHours(paramCalendar, localStringBuilder);
            break;
          case 'm': 
            formatMinutes(paramCalendar, localStringBuilder);
            break;
          case 's': 
            formatSeconds(paramCalendar, localStringBuilder);
            break;
          case 'z': 
            formatTimeZone(paramCalendar, localStringBuilder);
            break;
          default: 
            throw new InternalError();
          }
        }
      }
      return localStringBuilder.toString();
    }
    
    private static void formatYear(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      int i = paramCalendar.get(1);
      if (i <= 0) {
        str = Integer.toString(1 - i);
      }
      for (String str = Integer.toString(i); str.length() < 4; str = '0' + str) {}
      if (i <= 0) {
        str = '-' + str;
      }
      paramStringBuilder.append(str);
    }
    
    private static void formatMonth(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      formatTwoDigits(paramCalendar.get(2) + 1, paramStringBuilder);
    }
    
    private static void formatDays(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      formatTwoDigits(paramCalendar.get(5), paramStringBuilder);
    }
    
    private static void formatHours(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      formatTwoDigits(paramCalendar.get(11), paramStringBuilder);
    }
    
    private static void formatMinutes(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      formatTwoDigits(paramCalendar.get(12), paramStringBuilder);
    }
    
    private static void formatSeconds(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      formatTwoDigits(paramCalendar.get(13), paramStringBuilder);
      if (paramCalendar.isSet(14))
      {
        int i = paramCalendar.get(14);
        if (i != 0)
        {
          for (String str = Integer.toString(i); str.length() < 3; str = '0' + str) {}
          paramStringBuilder.append('.');
          paramStringBuilder.append(str);
        }
      }
    }
    
    private static void formatTimeZone(Calendar paramCalendar, StringBuilder paramStringBuilder)
    {
      TimeZone localTimeZone = paramCalendar.getTimeZone();
      if (localTimeZone == null) {
        return;
      }
      int i = localTimeZone.getOffset(paramCalendar.getTime().getTime());
      if (i == 0)
      {
        paramStringBuilder.append('Z');
        return;
      }
      if (i >= 0)
      {
        paramStringBuilder.append('+');
      }
      else
      {
        paramStringBuilder.append('-');
        i *= -1;
      }
      i /= 60000;
      formatTwoDigits(i / 60, paramStringBuilder);
      paramStringBuilder.append(':');
      formatTwoDigits(i % 60, paramStringBuilder);
    }
    
    private static void formatTwoDigits(int paramInt, StringBuilder paramStringBuilder)
    {
      if (paramInt < 10) {
        paramStringBuilder.append('0');
      }
      paramStringBuilder.append(paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\DatatypeConverterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
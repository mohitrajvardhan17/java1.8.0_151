package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QPDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QPEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.UUDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.UUEncoderStream;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class MimeUtility
{
  public static final int ALL = -1;
  private static final int BUFFER_SIZE = 1024;
  private static boolean decodeStrict = true;
  private static boolean encodeEolStrict = false;
  private static boolean foldEncodedWords = false;
  private static boolean foldText = true;
  private static String defaultJavaCharset;
  private static String defaultMIMECharset;
  private static Hashtable mime2java;
  private static Hashtable java2mime;
  static final int ALL_ASCII = 1;
  static final int MOSTLY_ASCII = 2;
  static final int MOSTLY_NONASCII = 3;
  
  private MimeUtility() {}
  
  public static String getEncoding(DataSource paramDataSource)
  {
    ContentType localContentType = null;
    InputStream localInputStream = null;
    String str = null;
    try
    {
      localContentType = new ContentType(paramDataSource.getContentType());
      localInputStream = paramDataSource.getInputStream();
    }
    catch (Exception localException)
    {
      return "base64";
    }
    boolean bool = localContentType.match("text/*");
    int i = checkAscii(localInputStream, -1, !bool);
    switch (i)
    {
    case 1: 
      str = "7bit";
      break;
    case 2: 
      str = "quoted-printable";
      break;
    default: 
      str = "base64";
    }
    try
    {
      localInputStream.close();
    }
    catch (IOException localIOException) {}
    return str;
  }
  
  public static String getEncoding(DataHandler paramDataHandler)
  {
    ContentType localContentType = null;
    String str = null;
    if (paramDataHandler.getName() != null) {
      return getEncoding(paramDataHandler.getDataSource());
    }
    try
    {
      localContentType = new ContentType(paramDataHandler.getContentType());
    }
    catch (Exception localException)
    {
      return "base64";
    }
    AsciiOutputStream localAsciiOutputStream;
    if (localContentType.match("text/*"))
    {
      localAsciiOutputStream = new AsciiOutputStream(false, false);
      try
      {
        paramDataHandler.writeTo(localAsciiOutputStream);
      }
      catch (IOException localIOException1) {}
      switch (localAsciiOutputStream.getAscii())
      {
      case 1: 
        str = "7bit";
        break;
      case 2: 
        str = "quoted-printable";
        break;
      default: 
        str = "base64";
      }
    }
    else
    {
      localAsciiOutputStream = new AsciiOutputStream(true, encodeEolStrict);
      try
      {
        paramDataHandler.writeTo(localAsciiOutputStream);
      }
      catch (IOException localIOException2) {}
      if (localAsciiOutputStream.getAscii() == 1) {
        str = "7bit";
      } else {
        str = "base64";
      }
    }
    return str;
  }
  
  public static InputStream decode(InputStream paramInputStream, String paramString)
    throws MessagingException
  {
    if (paramString.equalsIgnoreCase("base64")) {
      return new BASE64DecoderStream(paramInputStream);
    }
    if (paramString.equalsIgnoreCase("quoted-printable")) {
      return new QPDecoderStream(paramInputStream);
    }
    if ((paramString.equalsIgnoreCase("uuencode")) || (paramString.equalsIgnoreCase("x-uuencode")) || (paramString.equalsIgnoreCase("x-uue"))) {
      return new UUDecoderStream(paramInputStream);
    }
    if ((paramString.equalsIgnoreCase("binary")) || (paramString.equalsIgnoreCase("7bit")) || (paramString.equalsIgnoreCase("8bit"))) {
      return paramInputStream;
    }
    throw new MessagingException("Unknown encoding: " + paramString);
  }
  
  public static OutputStream encode(OutputStream paramOutputStream, String paramString)
    throws MessagingException
  {
    if (paramString == null) {
      return paramOutputStream;
    }
    if (paramString.equalsIgnoreCase("base64")) {
      return new BASE64EncoderStream(paramOutputStream);
    }
    if (paramString.equalsIgnoreCase("quoted-printable")) {
      return new QPEncoderStream(paramOutputStream);
    }
    if ((paramString.equalsIgnoreCase("uuencode")) || (paramString.equalsIgnoreCase("x-uuencode")) || (paramString.equalsIgnoreCase("x-uue"))) {
      return new UUEncoderStream(paramOutputStream);
    }
    if ((paramString.equalsIgnoreCase("binary")) || (paramString.equalsIgnoreCase("7bit")) || (paramString.equalsIgnoreCase("8bit"))) {
      return paramOutputStream;
    }
    throw new MessagingException("Unknown encoding: " + paramString);
  }
  
  public static OutputStream encode(OutputStream paramOutputStream, String paramString1, String paramString2)
    throws MessagingException
  {
    if (paramString1 == null) {
      return paramOutputStream;
    }
    if (paramString1.equalsIgnoreCase("base64")) {
      return new BASE64EncoderStream(paramOutputStream);
    }
    if (paramString1.equalsIgnoreCase("quoted-printable")) {
      return new QPEncoderStream(paramOutputStream);
    }
    if ((paramString1.equalsIgnoreCase("uuencode")) || (paramString1.equalsIgnoreCase("x-uuencode")) || (paramString1.equalsIgnoreCase("x-uue"))) {
      return new UUEncoderStream(paramOutputStream, paramString2);
    }
    if ((paramString1.equalsIgnoreCase("binary")) || (paramString1.equalsIgnoreCase("7bit")) || (paramString1.equalsIgnoreCase("8bit"))) {
      return paramOutputStream;
    }
    throw new MessagingException("Unknown encoding: " + paramString1);
  }
  
  public static String encodeText(String paramString)
    throws UnsupportedEncodingException
  {
    return encodeText(paramString, null, null);
  }
  
  public static String encodeText(String paramString1, String paramString2, String paramString3)
    throws UnsupportedEncodingException
  {
    return encodeWord(paramString1, paramString2, paramString3, false);
  }
  
  public static String decodeText(String paramString)
    throws UnsupportedEncodingException
  {
    String str1 = " \t\n\r";
    if (paramString.indexOf("=?") == -1) {
      return paramString;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, str1, true);
    StringBuffer localStringBuffer1 = new StringBuffer();
    StringBuffer localStringBuffer2 = new StringBuffer();
    int i = 0;
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      char c;
      if (((c = str2.charAt(0)) == ' ') || (c == '\t') || (c == '\r') || (c == '\n'))
      {
        localStringBuffer2.append(c);
      }
      else
      {
        String str3;
        try
        {
          str3 = decodeWord(str2);
          if ((i == 0) && (localStringBuffer2.length() > 0)) {
            localStringBuffer1.append(localStringBuffer2);
          }
          i = 1;
        }
        catch (ParseException localParseException)
        {
          str3 = str2;
          if (!decodeStrict) {
            str3 = decodeInnerWords(str3);
          }
          if (localStringBuffer2.length() > 0) {
            localStringBuffer1.append(localStringBuffer2);
          }
          i = 0;
        }
        localStringBuffer1.append(str3);
        localStringBuffer2.setLength(0);
      }
    }
    return localStringBuffer1.toString();
  }
  
  public static String encodeWord(String paramString)
    throws UnsupportedEncodingException
  {
    return encodeWord(paramString, null, null);
  }
  
  public static String encodeWord(String paramString1, String paramString2, String paramString3)
    throws UnsupportedEncodingException
  {
    return encodeWord(paramString1, paramString2, paramString3, true);
  }
  
  private static String encodeWord(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws UnsupportedEncodingException
  {
    int i = checkAscii(paramString1);
    if (i == 1) {
      return paramString1;
    }
    String str;
    if (paramString2 == null)
    {
      str = getDefaultJavaCharset();
      paramString2 = getDefaultMIMECharset();
    }
    else
    {
      str = javaCharset(paramString2);
    }
    if (paramString3 == null) {
      if (i != 3) {
        paramString3 = "Q";
      } else {
        paramString3 = "B";
      }
    }
    boolean bool;
    if (paramString3.equalsIgnoreCase("B")) {
      bool = true;
    } else if (paramString3.equalsIgnoreCase("Q")) {
      bool = false;
    } else {
      throw new UnsupportedEncodingException("Unknown transfer encoding: " + paramString3);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    doEncode(paramString1, bool, str, 68 - paramString2.length(), "=?" + paramString2 + "?" + paramString3 + "?", true, paramBoolean, localStringBuffer);
    return localStringBuffer.toString();
  }
  
  private static void doEncode(String paramString1, boolean paramBoolean1, String paramString2, int paramInt, String paramString3, boolean paramBoolean2, boolean paramBoolean3, StringBuffer paramStringBuffer)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte1 = paramString1.getBytes(paramString2);
    int i;
    if (paramBoolean1) {
      i = BEncoderStream.encodedLength(arrayOfByte1);
    } else {
      i = QEncoderStream.encodedLength(arrayOfByte1, paramBoolean3);
    }
    int j;
    if ((i > paramInt) && ((j = paramString1.length()) > 1))
    {
      doEncode(paramString1.substring(0, j / 2), paramBoolean1, paramString2, paramInt, paramString3, paramBoolean2, paramBoolean3, paramStringBuffer);
      doEncode(paramString1.substring(j / 2, j), paramBoolean1, paramString2, paramInt, paramString3, false, paramBoolean3, paramStringBuffer);
    }
    else
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
      Object localObject;
      if (paramBoolean1) {
        localObject = new BEncoderStream(localByteArrayOutputStream);
      } else {
        localObject = new QEncoderStream(localByteArrayOutputStream, paramBoolean3);
      }
      try
      {
        ((OutputStream)localObject).write(arrayOfByte1);
        ((OutputStream)localObject).close();
      }
      catch (IOException localIOException) {}
      byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
      if (!paramBoolean2) {
        if (foldEncodedWords) {
          paramStringBuffer.append("\r\n ");
        } else {
          paramStringBuffer.append(" ");
        }
      }
      paramStringBuffer.append(paramString3);
      for (int k = 0; k < arrayOfByte2.length; k++) {
        paramStringBuffer.append((char)arrayOfByte2[k]);
      }
      paramStringBuffer.append("?=");
    }
  }
  
  public static String decodeWord(String paramString)
    throws ParseException, UnsupportedEncodingException
  {
    if (!paramString.startsWith("=?")) {
      throw new ParseException();
    }
    int i = 2;
    int j;
    if ((j = paramString.indexOf('?', i)) == -1) {
      throw new ParseException();
    }
    String str1 = javaCharset(paramString.substring(i, j));
    i = j + 1;
    if ((j = paramString.indexOf('?', i)) == -1) {
      throw new ParseException();
    }
    String str2 = paramString.substring(i, j);
    i = j + 1;
    if ((j = paramString.indexOf("?=", i)) == -1) {
      throw new ParseException();
    }
    String str3 = paramString.substring(i, j);
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(ASCIIUtility.getBytes(str3));
      Object localObject;
      if (str2.equalsIgnoreCase("B")) {
        localObject = new BASE64DecoderStream(localByteArrayInputStream);
      } else if (str2.equalsIgnoreCase("Q")) {
        localObject = new QDecoderStream(localByteArrayInputStream);
      } else {
        throw new UnsupportedEncodingException("unknown encoding: " + str2);
      }
      int k = localByteArrayInputStream.available();
      byte[] arrayOfByte = new byte[k];
      k = ((InputStream)localObject).read(arrayOfByte, 0, k);
      String str4 = new String(arrayOfByte, 0, k, str1);
      if (j + 2 < paramString.length())
      {
        String str5 = paramString.substring(j + 2);
        if (!decodeStrict) {
          str5 = decodeInnerWords(str5);
        }
        str4 = str4 + str5;
      }
      return str4;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw localUnsupportedEncodingException;
    }
    catch (IOException localIOException)
    {
      throw new ParseException();
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new UnsupportedEncodingException();
    }
  }
  
  private static String decodeInnerWords(String paramString)
    throws UnsupportedEncodingException
  {
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    int j;
    while ((j = paramString.indexOf("=?", i)) >= 0)
    {
      localStringBuffer.append(paramString.substring(i, j));
      int k = paramString.indexOf("?=", j);
      if (k < 0) {
        break;
      }
      String str = paramString.substring(j, k + 2);
      try
      {
        str = decodeWord(str);
      }
      catch (ParseException localParseException) {}
      localStringBuffer.append(str);
      i = k + 2;
    }
    if (i == 0) {
      return paramString;
    }
    if (i < paramString.length()) {
      localStringBuffer.append(paramString.substring(i));
    }
    return localStringBuffer.toString();
  }
  
  public static String quote(String paramString1, String paramString2)
  {
    int i = paramString1.length();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      int m = paramString1.charAt(k);
      if ((m == 34) || (m == 92) || (m == 13) || (m == 10))
      {
        StringBuffer localStringBuffer2 = new StringBuffer(i + 3);
        localStringBuffer2.append('"');
        localStringBuffer2.append(paramString1.substring(0, k));
        int n = 0;
        for (int i1 = k; i1 < i; i1++)
        {
          char c = paramString1.charAt(i1);
          if (((c == '"') || (c == '\\') || (c == '\r') || (c == '\n')) && ((c != '\n') || (n != 13))) {
            localStringBuffer2.append('\\');
          }
          localStringBuffer2.append(c);
          n = c;
        }
        localStringBuffer2.append('"');
        return localStringBuffer2.toString();
      }
      if ((m < 32) || (m >= 127) || (paramString2.indexOf(m) >= 0)) {
        j = 1;
      }
    }
    if (j != 0)
    {
      StringBuffer localStringBuffer1 = new StringBuffer(i + 2);
      localStringBuffer1.append('"').append(paramString1).append('"');
      return localStringBuffer1.toString();
    }
    return paramString1;
  }
  
  static String fold(int paramInt, String paramString)
  {
    if (!foldText) {
      return paramString;
    }
    char c1;
    for (int i = paramString.length() - 1; i >= 0; i--)
    {
      c1 = paramString.charAt(i);
      if ((c1 != ' ') && (c1 != '\t')) {
        break;
      }
    }
    if (i != paramString.length() - 1) {
      paramString = paramString.substring(0, i + 1);
    }
    if (paramInt + paramString.length() <= 76) {
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer(paramString.length() + 4);
    char c2 = '\000';
    while (paramInt + paramString.length() > 76)
    {
      int j = -1;
      for (int k = 0; (k < paramString.length()) && ((j == -1) || (paramInt + k <= 76)); k++)
      {
        c1 = paramString.charAt(k);
        if (((c1 == ' ') || (c1 == '\t')) && (c2 != ' ') && (c2 != '\t')) {
          j = k;
        }
        c2 = c1;
      }
      if (j == -1)
      {
        localStringBuffer.append(paramString);
        paramString = "";
        paramInt = 0;
        break;
      }
      localStringBuffer.append(paramString.substring(0, j));
      localStringBuffer.append("\r\n");
      c2 = paramString.charAt(j);
      localStringBuffer.append(c2);
      paramString = paramString.substring(j + 1);
      paramInt = 1;
    }
    localStringBuffer.append(paramString);
    return localStringBuffer.toString();
  }
  
  static String unfold(String paramString)
  {
    if (!foldText) {
      return paramString;
    }
    StringBuffer localStringBuffer = null;
    int i;
    while ((i = indexOfAny(paramString, "\r\n")) >= 0)
    {
      int j = i;
      int k = paramString.length();
      i++;
      if ((i < k) && (paramString.charAt(i - 1) == '\r') && (paramString.charAt(i) == '\n')) {
        i++;
      }
      if ((j == 0) || (paramString.charAt(j - 1) != '\\'))
      {
        int m;
        if ((i < k) && (((m = paramString.charAt(i)) == ' ') || (m == 9)))
        {
          i++;
          while ((i < k) && (((m = paramString.charAt(i)) == ' ') || (m == 9))) {
            i++;
          }
          if (localStringBuffer == null) {
            localStringBuffer = new StringBuffer(paramString.length());
          }
          if (j != 0)
          {
            localStringBuffer.append(paramString.substring(0, j));
            localStringBuffer.append(' ');
          }
          paramString = paramString.substring(i);
        }
        else
        {
          if (localStringBuffer == null) {
            localStringBuffer = new StringBuffer(paramString.length());
          }
          localStringBuffer.append(paramString.substring(0, i));
          paramString = paramString.substring(i);
        }
      }
      else
      {
        if (localStringBuffer == null) {
          localStringBuffer = new StringBuffer(paramString.length());
        }
        localStringBuffer.append(paramString.substring(0, j - 1));
        localStringBuffer.append(paramString.substring(j, i));
        paramString = paramString.substring(i);
      }
    }
    if (localStringBuffer != null)
    {
      localStringBuffer.append(paramString);
      return localStringBuffer.toString();
    }
    return paramString;
  }
  
  private static int indexOfAny(String paramString1, String paramString2)
  {
    return indexOfAny(paramString1, paramString2, 0);
  }
  
  private static int indexOfAny(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      int i = paramString1.length();
      for (int j = paramInt; j < i; j++) {
        if (paramString2.indexOf(paramString1.charAt(j)) >= 0) {
          return j;
        }
      }
      return -1;
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    return -1;
  }
  
  public static String javaCharset(String paramString)
  {
    if ((mime2java == null) || (paramString == null)) {
      return paramString;
    }
    String str = (String)mime2java.get(paramString.toLowerCase());
    return str == null ? paramString : str;
  }
  
  public static String mimeCharset(String paramString)
  {
    if ((java2mime == null) || (paramString == null)) {
      return paramString;
    }
    String str = (String)java2mime.get(paramString.toLowerCase());
    return str == null ? paramString : str;
  }
  
  public static String getDefaultJavaCharset()
  {
    if (defaultJavaCharset == null)
    {
      String str = null;
      str = SAAJUtil.getSystemProperty("mail.mime.charset");
      if ((str != null) && (str.length() > 0))
      {
        defaultJavaCharset = javaCharset(str);
        return defaultJavaCharset;
      }
      try
      {
        defaultJavaCharset = System.getProperty("file.encoding", "8859_1");
      }
      catch (SecurityException localSecurityException)
      {
        InputStreamReader localInputStreamReader = new InputStreamReader(new InputStream()
        {
          public int read()
          {
            return 0;
          }
        });
        defaultJavaCharset = localInputStreamReader.getEncoding();
        if (defaultJavaCharset == null) {
          defaultJavaCharset = "8859_1";
        }
      }
    }
    return defaultJavaCharset;
  }
  
  static String getDefaultMIMECharset()
  {
    if (defaultMIMECharset == null) {
      defaultMIMECharset = SAAJUtil.getSystemProperty("mail.mime.charset");
    }
    if (defaultMIMECharset == null) {
      defaultMIMECharset = mimeCharset(getDefaultJavaCharset());
    }
    return defaultMIMECharset;
  }
  
  private static void loadMappings(LineInputStream paramLineInputStream, Hashtable paramHashtable)
  {
    for (;;)
    {
      String str1;
      try
      {
        str1 = paramLineInputStream.readLine();
      }
      catch (IOException localIOException)
      {
        break;
      }
      if ((str1 == null) || ((str1.startsWith("--")) && (str1.endsWith("--")))) {
        break;
      }
      if ((str1.trim().length() != 0) && (!str1.startsWith("#")))
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str1, " \t");
        try
        {
          String str2 = localStringTokenizer.nextToken();
          String str3 = localStringTokenizer.nextToken();
          paramHashtable.put(str2.toLowerCase(), str3);
        }
        catch (NoSuchElementException localNoSuchElementException) {}
      }
    }
  }
  
  static int checkAscii(String paramString)
  {
    int i = 0;
    int j = 0;
    int k = paramString.length();
    for (int m = 0; m < k; m++) {
      if (nonascii(paramString.charAt(m))) {
        j++;
      } else {
        i++;
      }
    }
    if (j == 0) {
      return 1;
    }
    if (i > j) {
      return 2;
    }
    return 3;
  }
  
  static int checkAscii(byte[] paramArrayOfByte)
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramArrayOfByte.length; k++) {
      if (nonascii(paramArrayOfByte[k] & 0xFF)) {
        j++;
      } else {
        i++;
      }
    }
    if (j == 0) {
      return 1;
    }
    if (i > j) {
      return 2;
    }
    return 3;
  }
  
  static int checkAscii(InputStream paramInputStream, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    int j = 0;
    int m = 4096;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = (encodeEolStrict) && (paramBoolean) ? 1 : 0;
    byte[] arrayOfByte = null;
    if (paramInt != 0)
    {
      m = paramInt == -1 ? 4096 : Math.min(paramInt, 4096);
      arrayOfByte = new byte[m];
    }
    while (paramInt != 0)
    {
      int k;
      try
      {
        if ((k = paramInputStream.read(arrayOfByte, 0, m)) == -1) {
          break;
        }
        int i4 = 0;
        for (int i5 = 0; i5 < k; i5++)
        {
          int i6 = arrayOfByte[i5] & 0xFF;
          if ((i3 != 0) && (((i4 == 13) && (i6 != 10)) || ((i4 != 13) && (i6 == 10)))) {
            i2 = 1;
          }
          if ((i6 == 13) || (i6 == 10))
          {
            n = 0;
          }
          else
          {
            n++;
            if (n > 998) {
              i1 = 1;
            }
          }
          if (nonascii(i6))
          {
            if (paramBoolean) {
              return 3;
            }
            j++;
          }
          else
          {
            i++;
          }
          i4 = i6;
        }
      }
      catch (IOException localIOException)
      {
        break;
      }
      if (paramInt != -1) {
        paramInt -= k;
      }
    }
    if ((paramInt == 0) && (paramBoolean)) {
      return 3;
    }
    if (j == 0)
    {
      if (i2 != 0) {
        return 3;
      }
      if (i1 != 0) {
        return 2;
      }
      return 1;
    }
    if (i > j) {
      return 2;
    }
    return 3;
  }
  
  static final boolean nonascii(int paramInt)
  {
    return (paramInt >= 127) || ((paramInt < 32) && (paramInt != 13) && (paramInt != 10) && (paramInt != 9));
  }
  
  static
  {
    try
    {
      String str = SAAJUtil.getSystemProperty("mail.mime.decodetext.strict");
      decodeStrict = (str == null) || (!str.equalsIgnoreCase("false"));
      str = SAAJUtil.getSystemProperty("mail.mime.encodeeol.strict");
      encodeEolStrict = (str != null) && (str.equalsIgnoreCase("true"));
      str = SAAJUtil.getSystemProperty("mail.mime.foldencodedwords");
      foldEncodedWords = (str != null) && (str.equalsIgnoreCase("true"));
      str = SAAJUtil.getSystemProperty("mail.mime.foldtext");
      foldText = (str == null) || (!str.equalsIgnoreCase("false"));
    }
    catch (SecurityException localSecurityException) {}
    java2mime = new Hashtable(40);
    mime2java = new Hashtable(10);
    try
    {
      Object localObject = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
      if (localObject != null)
      {
        localObject = new LineInputStream((InputStream)localObject);
        loadMappings((LineInputStream)localObject, java2mime);
        loadMappings((LineInputStream)localObject, mime2java);
      }
    }
    catch (Exception localException) {}
    if (java2mime.isEmpty())
    {
      java2mime.put("8859_1", "ISO-8859-1");
      java2mime.put("iso8859_1", "ISO-8859-1");
      java2mime.put("ISO8859-1", "ISO-8859-1");
      java2mime.put("8859_2", "ISO-8859-2");
      java2mime.put("iso8859_2", "ISO-8859-2");
      java2mime.put("ISO8859-2", "ISO-8859-2");
      java2mime.put("8859_3", "ISO-8859-3");
      java2mime.put("iso8859_3", "ISO-8859-3");
      java2mime.put("ISO8859-3", "ISO-8859-3");
      java2mime.put("8859_4", "ISO-8859-4");
      java2mime.put("iso8859_4", "ISO-8859-4");
      java2mime.put("ISO8859-4", "ISO-8859-4");
      java2mime.put("8859_5", "ISO-8859-5");
      java2mime.put("iso8859_5", "ISO-8859-5");
      java2mime.put("ISO8859-5", "ISO-8859-5");
      java2mime.put("8859_6", "ISO-8859-6");
      java2mime.put("iso8859_6", "ISO-8859-6");
      java2mime.put("ISO8859-6", "ISO-8859-6");
      java2mime.put("8859_7", "ISO-8859-7");
      java2mime.put("iso8859_7", "ISO-8859-7");
      java2mime.put("ISO8859-7", "ISO-8859-7");
      java2mime.put("8859_8", "ISO-8859-8");
      java2mime.put("iso8859_8", "ISO-8859-8");
      java2mime.put("ISO8859-8", "ISO-8859-8");
      java2mime.put("8859_9", "ISO-8859-9");
      java2mime.put("iso8859_9", "ISO-8859-9");
      java2mime.put("ISO8859-9", "ISO-8859-9");
      java2mime.put("SJIS", "Shift_JIS");
      java2mime.put("MS932", "Shift_JIS");
      java2mime.put("JIS", "ISO-2022-JP");
      java2mime.put("ISO2022JP", "ISO-2022-JP");
      java2mime.put("EUC_JP", "euc-jp");
      java2mime.put("KOI8_R", "koi8-r");
      java2mime.put("EUC_CN", "euc-cn");
      java2mime.put("EUC_TW", "euc-tw");
      java2mime.put("EUC_KR", "euc-kr");
    }
    if (mime2java.isEmpty())
    {
      mime2java.put("iso-2022-cn", "ISO2022CN");
      mime2java.put("iso-2022-kr", "ISO2022KR");
      mime2java.put("utf-8", "UTF8");
      mime2java.put("utf8", "UTF8");
      mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
      mime2java.put("ja_jp.eucjp", "EUCJIS");
      mime2java.put("euc-kr", "KSC5601");
      mime2java.put("euckr", "KSC5601");
      mime2java.put("us-ascii", "ISO-8859-1");
      mime2java.put("x-us-ascii", "ISO-8859-1");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimeUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
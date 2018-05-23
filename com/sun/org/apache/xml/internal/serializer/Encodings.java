package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public final class Encodings
{
  private static final int m_defaultLastPrintable = 127;
  private static final String ENCODINGS_FILE = "com/sun/org/apache/xml/internal/serializer/Encodings.properties";
  private static final String ENCODINGS_PROP = "com.sun.org.apache.xalan.internal.serialize.encodings";
  static final String DEFAULT_MIME_ENCODING = "UTF-8";
  private static final EncodingInfos _encodingInfos = new EncodingInfos(null);
  
  public Encodings() {}
  
  static Writer getWriter(OutputStream paramOutputStream, String paramString)
    throws UnsupportedEncodingException
  {
    EncodingInfo localEncodingInfo = _encodingInfos.findEncoding(toUpperCaseFast(paramString));
    if (localEncodingInfo != null) {
      try
      {
        return new BufferedWriter(new OutputStreamWriter(paramOutputStream, javaName));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    }
    return new BufferedWriter(new OutputStreamWriter(paramOutputStream, paramString));
  }
  
  public static int getLastPrintable()
  {
    return 127;
  }
  
  static EncodingInfo getEncodingInfo(String paramString)
  {
    String str1 = toUpperCaseFast(paramString);
    EncodingInfo localEncodingInfo = _encodingInfos.findEncoding(str1);
    if (localEncodingInfo == null) {
      try
      {
        Charset localCharset = Charset.forName(paramString);
        String str2 = localCharset.name();
        localEncodingInfo = new EncodingInfo(str2, str2);
        _encodingInfos.putEncoding(str1, localEncodingInfo);
      }
      catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
      {
        localEncodingInfo = new EncodingInfo(null, null);
      }
    }
    return localEncodingInfo;
  }
  
  private static String toUpperCaseFast(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    char[] arrayOfChar = new char[j];
    for (int k = 0; k < j; k++)
    {
      int m = paramString.charAt(k);
      if ((97 <= m) && (m <= 122))
      {
        m = (char)(m + -32);
        i = 1;
      }
      arrayOfChar[k] = m;
    }
    String str;
    if (i != 0) {
      str = String.valueOf(arrayOfChar);
    } else {
      str = paramString;
    }
    return str;
  }
  
  static String getMimeEncoding(String paramString)
  {
    if (null == paramString) {
      try
      {
        paramString = SecuritySupport.getSystemProperty("file.encoding", "UTF8");
        if (null != paramString)
        {
          String str = (paramString.equalsIgnoreCase("Cp1252")) || (paramString.equalsIgnoreCase("ISO8859_1")) || (paramString.equalsIgnoreCase("8859_1")) || (paramString.equalsIgnoreCase("UTF8")) ? "UTF-8" : convertJava2MimeEncoding(paramString);
          paramString = null != str ? str : "UTF-8";
        }
        else
        {
          paramString = "UTF-8";
        }
      }
      catch (SecurityException localSecurityException)
      {
        paramString = "UTF-8";
      }
    } else {
      paramString = convertJava2MimeEncoding(paramString);
    }
    return paramString;
  }
  
  private static String convertJava2MimeEncoding(String paramString)
  {
    EncodingInfo localEncodingInfo = _encodingInfos.getEncodingFromJavaKey(toUpperCaseFast(paramString));
    if (null != localEncodingInfo) {
      return name;
    }
    return paramString;
  }
  
  public static String convertMime2JavaEncoding(String paramString)
  {
    EncodingInfo localEncodingInfo = _encodingInfos.findEncoding(toUpperCaseFast(paramString));
    return localEncodingInfo != null ? javaName : paramString;
  }
  
  static boolean isHighUTF16Surrogate(char paramChar)
  {
    return (55296 <= paramChar) && (paramChar <= 56319);
  }
  
  static boolean isLowUTF16Surrogate(char paramChar)
  {
    return (56320 <= paramChar) && (paramChar <= 57343);
  }
  
  static int toCodePoint(char paramChar1, char paramChar2)
  {
    int i = (paramChar1 - 55296 << 10) + (paramChar2 - 56320) + 65536;
    return i;
  }
  
  static int toCodePoint(char paramChar)
  {
    int i = paramChar;
    return i;
  }
  
  private static final class EncodingInfos
  {
    private final Map<String, EncodingInfo> _encodingTableKeyJava = new HashMap();
    private final Map<String, EncodingInfo> _encodingTableKeyMime = new HashMap();
    private final Map<String, EncodingInfo> _encodingDynamicTable = Collections.synchronizedMap(new HashMap());
    
    private EncodingInfos()
    {
      loadEncodingInfo();
    }
    
    private InputStream openEncodingsFileStream()
      throws MalformedURLException, IOException
    {
      String str = null;
      InputStream localInputStream = null;
      try
      {
        str = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.serialize.encodings", "");
      }
      catch (SecurityException localSecurityException) {}
      if ((str != null) && (str.length() > 0))
      {
        URL localURL = new URL(str);
        localInputStream = localURL.openStream();
      }
      if (localInputStream == null) {
        localInputStream = SecuritySupport.getResourceAsStream("com/sun/org/apache/xml/internal/serializer/Encodings.properties");
      }
      return localInputStream;
    }
    
    private Properties loadProperties()
      throws MalformedURLException, IOException
    {
      Properties localProperties = new Properties();
      InputStream localInputStream = openEncodingsFileStream();
      Object localObject1 = null;
      try
      {
        if (localInputStream != null) {
          localProperties.load(localInputStream);
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localInputStream.close();
          }
        }
      }
      return localProperties;
    }
    
    private String[] parseMimeTypes(String paramString)
    {
      int i = paramString.indexOf(' ');
      if (i < 0) {
        return new String[] { paramString };
      }
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString.substring(0, i), ",");
      String[] arrayOfString = new String[localStringTokenizer.countTokens()];
      for (int j = 0; localStringTokenizer.hasMoreTokens(); j++) {
        arrayOfString[j] = localStringTokenizer.nextToken();
      }
      return arrayOfString;
    }
    
    private String findCharsetNameFor(String paramString)
    {
      try
      {
        return Charset.forName(paramString).name();
      }
      catch (Exception localException) {}
      return null;
    }
    
    private String findCharsetNameFor(String paramString, String[] paramArrayOfString)
    {
      String str1 = findCharsetNameFor(paramString);
      if (str1 != null) {
        return paramString;
      }
      for (String str2 : paramArrayOfString)
      {
        str1 = findCharsetNameFor(str2);
        if (str1 != null) {
          break;
        }
      }
      return str1;
    }
    
    private void loadEncodingInfo()
    {
      try
      {
        Properties localProperties = loadProperties();
        Enumeration localEnumeration = localProperties.keys();
        HashMap localHashMap = new HashMap();
        Object localObject2;
        while (localEnumeration.hasMoreElements())
        {
          localObject1 = (String)localEnumeration.nextElement();
          localObject2 = parseMimeTypes(localProperties.getProperty((String)localObject1));
          String str1 = findCharsetNameFor((String)localObject1, (String[])localObject2);
          if (str1 != null)
          {
            String str2 = Encodings.toUpperCaseFast((String)localObject1);
            String str3 = Encodings.toUpperCaseFast(str1);
            for (int i = 0; i < localObject2.length; i++)
            {
              String str4 = localObject2[i];
              String str5 = Encodings.toUpperCaseFast(str4);
              EncodingInfo localEncodingInfo = new EncodingInfo(str4, str1);
              _encodingTableKeyMime.put(str5, localEncodingInfo);
              if (!localHashMap.containsKey(str3))
              {
                localHashMap.put(str3, localEncodingInfo);
                _encodingTableKeyJava.put(str3, localEncodingInfo);
              }
              _encodingTableKeyJava.put(str2, localEncodingInfo);
            }
          }
        }
        Object localObject1 = _encodingTableKeyJava.entrySet().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Map.Entry)((Iterator)localObject1).next();
          ((Map.Entry)localObject2).setValue(localHashMap.get(Encodings.toUpperCaseFast(getValuejavaName)));
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new WrappedRuntimeException(localMalformedURLException);
      }
      catch (IOException localIOException)
      {
        throw new WrappedRuntimeException(localIOException);
      }
    }
    
    EncodingInfo findEncoding(String paramString)
    {
      EncodingInfo localEncodingInfo = (EncodingInfo)_encodingTableKeyJava.get(paramString);
      if (localEncodingInfo == null) {
        localEncodingInfo = (EncodingInfo)_encodingTableKeyMime.get(paramString);
      }
      if (localEncodingInfo == null) {
        localEncodingInfo = (EncodingInfo)_encodingDynamicTable.get(paramString);
      }
      return localEncodingInfo;
    }
    
    EncodingInfo getEncodingFromMimeKey(String paramString)
    {
      return (EncodingInfo)_encodingTableKeyMime.get(paramString);
    }
    
    EncodingInfo getEncodingFromJavaKey(String paramString)
    {
      return (EncodingInfo)_encodingTableKeyJava.get(paramString);
    }
    
    void putEncoding(String paramString, EncodingInfo paramEncodingInfo)
    {
      _encodingDynamicTable.put(paramString, paramEncodingInfo);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\Encodings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
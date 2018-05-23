package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.StringReader;

public class RFC2253Parser
{
  public RFC2253Parser() {}
  
  public static String rfc2253toXMLdsig(String paramString)
  {
    String str = normalize(paramString, true);
    return rfctoXML(str);
  }
  
  public static String xmldsigtoRFC2253(String paramString)
  {
    String str = normalize(paramString, false);
    return xmltoRFC(str);
  }
  
  public static String normalize(String paramString)
  {
    return normalize(paramString, true);
  }
  
  public static String normalize(String paramString, boolean paramBoolean)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return "";
    }
    try
    {
      String str = semicolonToComma(paramString);
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      int j = 0;
      int k;
      for (int m = 0; (k = str.indexOf(',', m)) >= 0; m = k + 1)
      {
        j += countQuotes(str, m, k);
        if ((k > 0) && (str.charAt(k - 1) != '\\') && (j % 2 == 0))
        {
          localStringBuilder.append(parseRDN(str.substring(i, k).trim(), paramBoolean) + ",");
          i = k + 1;
          j = 0;
        }
      }
      localStringBuilder.append(parseRDN(trim(str.substring(i)), paramBoolean));
      return localStringBuilder.toString();
    }
    catch (IOException localIOException) {}
    return paramString;
  }
  
  static String parseRDN(String paramString, boolean paramBoolean)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    int k;
    for (int m = 0; (k = paramString.indexOf('+', m)) >= 0; m = k + 1)
    {
      j += countQuotes(paramString, m, k);
      if ((k > 0) && (paramString.charAt(k - 1) != '\\') && (j % 2 == 0))
      {
        localStringBuilder.append(parseATAV(trim(paramString.substring(i, k)), paramBoolean) + "+");
        i = k + 1;
        j = 0;
      }
    }
    localStringBuilder.append(parseATAV(trim(paramString.substring(i)), paramBoolean));
    return localStringBuilder.toString();
  }
  
  static String parseATAV(String paramString, boolean paramBoolean)
    throws IOException
  {
    int i = paramString.indexOf('=');
    if ((i == -1) || ((i > 0) && (paramString.charAt(i - 1) == '\\'))) {
      return paramString;
    }
    String str1 = normalizeAT(paramString.substring(0, i));
    String str2 = null;
    if ((str1.charAt(0) >= '0') && (str1.charAt(0) <= '9')) {
      str2 = paramString.substring(i + 1);
    } else {
      str2 = normalizeV(paramString.substring(i + 1), paramBoolean);
    }
    return str1 + "=" + str2;
  }
  
  static String normalizeAT(String paramString)
  {
    String str = paramString.toUpperCase().trim();
    if (str.startsWith("OID")) {
      str = str.substring(3);
    }
    return str;
  }
  
  static String normalizeV(String paramString, boolean paramBoolean)
    throws IOException
  {
    String str = trim(paramString);
    if (str.startsWith("\""))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      StringReader localStringReader = new StringReader(str.substring(1, str.length() - 1));
      int i = 0;
      while ((i = localStringReader.read()) > -1)
      {
        char c = (char)i;
        if ((c == ',') || (c == '=') || (c == '+') || (c == '<') || (c == '>') || (c == '#') || (c == ';')) {
          localStringBuilder.append('\\');
        }
        localStringBuilder.append(c);
      }
      str = trim(localStringBuilder.toString());
    }
    if (paramBoolean)
    {
      if (str.startsWith("#")) {
        str = '\\' + str;
      }
    }
    else if (str.startsWith("\\#")) {
      str = str.substring(1);
    }
    return str;
  }
  
  static String rfctoXML(String paramString)
  {
    try
    {
      String str = changeLess32toXML(paramString);
      return changeWStoXML(str);
    }
    catch (Exception localException) {}
    return paramString;
  }
  
  static String xmltoRFC(String paramString)
  {
    try
    {
      String str = changeLess32toRFC(paramString);
      return changeWStoRFC(str);
    }
    catch (Exception localException) {}
    return paramString;
  }
  
  static String changeLess32toRFC(String paramString)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    StringReader localStringReader = new StringReader(paramString);
    int i = 0;
    while ((i = localStringReader.read()) > -1)
    {
      char c1 = (char)i;
      if (c1 == '\\')
      {
        localStringBuilder.append(c1);
        char c2 = (char)localStringReader.read();
        char c3 = (char)localStringReader.read();
        if (((c2 >= '0') && (c2 <= '9')) || ((c2 >= 'A') && (c2 <= 'F')) || ((c2 >= 'a') && (c2 <= 'f') && (((c3 >= '0') && (c3 <= '9')) || ((c3 >= 'A') && (c3 <= 'F')) || ((c3 >= 'a') && (c3 <= 'f')))))
        {
          char c4 = (char)Byte.parseByte("" + c2 + c3, 16);
          localStringBuilder.append(c4);
        }
        else
        {
          localStringBuilder.append(c2);
          localStringBuilder.append(c3);
        }
      }
      else
      {
        localStringBuilder.append(c1);
      }
    }
    return localStringBuilder.toString();
  }
  
  static String changeLess32toXML(String paramString)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    StringReader localStringReader = new StringReader(paramString);
    int i = 0;
    while ((i = localStringReader.read()) > -1) {
      if (i < 32)
      {
        localStringBuilder.append('\\');
        localStringBuilder.append(Integer.toHexString(i));
      }
      else
      {
        localStringBuilder.append((char)i);
      }
    }
    return localStringBuilder.toString();
  }
  
  static String changeWStoXML(String paramString)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    StringReader localStringReader = new StringReader(paramString);
    int i = 0;
    while ((i = localStringReader.read()) > -1)
    {
      char c1 = (char)i;
      if (c1 == '\\')
      {
        char c2 = (char)localStringReader.read();
        if (c2 == ' ')
        {
          localStringBuilder.append('\\');
          String str = "20";
          localStringBuilder.append(str);
        }
        else
        {
          localStringBuilder.append('\\');
          localStringBuilder.append(c2);
        }
      }
      else
      {
        localStringBuilder.append(c1);
      }
    }
    return localStringBuilder.toString();
  }
  
  static String changeWStoRFC(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j;
    for (int k = 0; (j = paramString.indexOf("\\20", k)) >= 0; k = j + 3)
    {
      localStringBuilder.append(trim(paramString.substring(i, j)) + "\\ ");
      i = j + 3;
    }
    localStringBuilder.append(paramString.substring(i));
    return localStringBuilder.toString();
  }
  
  static String semicolonToComma(String paramString)
  {
    return removeWSandReplace(paramString, ";", ",");
  }
  
  static String removeWhiteSpace(String paramString1, String paramString2)
  {
    return removeWSandReplace(paramString1, paramString2, paramString2);
  }
  
  static String removeWSandReplace(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    int k;
    for (int m = 0; (k = paramString1.indexOf(paramString2, m)) >= 0; m = k + 1)
    {
      j += countQuotes(paramString1, m, k);
      if ((k > 0) && (paramString1.charAt(k - 1) != '\\') && (j % 2 == 0))
      {
        localStringBuilder.append(trim(paramString1.substring(i, k)) + paramString3);
        i = k + 1;
        j = 0;
      }
    }
    localStringBuilder.append(trim(paramString1.substring(i)));
    return localStringBuilder.toString();
  }
  
  private static int countQuotes(String paramString, int paramInt1, int paramInt2)
  {
    int i = 0;
    for (int j = paramInt1; j < paramInt2; j++) {
      if (paramString.charAt(j) == '"') {
        i++;
      }
    }
    return i;
  }
  
  static String trim(String paramString)
  {
    String str = paramString.trim();
    int i = paramString.indexOf(str) + str.length();
    if ((paramString.length() > i) && (str.endsWith("\\")) && (!str.endsWith("\\\\")) && (paramString.charAt(i) == ' ')) {
      str = str + " ";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\RFC2253Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
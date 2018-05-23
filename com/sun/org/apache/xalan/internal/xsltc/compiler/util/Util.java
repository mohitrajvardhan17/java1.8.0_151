package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.PrintStream;
import java.util.StringTokenizer;

public final class Util
{
  private static char filesep;
  
  public Util() {}
  
  public static String noExtName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    return paramString.substring(0, i >= 0 ? i : paramString.length());
  }
  
  public static String baseName(String paramString)
  {
    int i = paramString.lastIndexOf('\\');
    if (i < 0) {
      i = paramString.lastIndexOf('/');
    }
    if (i >= 0) {
      return paramString.substring(i + 1);
    }
    int j = paramString.lastIndexOf(':');
    if (j > 0) {
      return paramString.substring(j + 1);
    }
    return paramString;
  }
  
  public static String pathName(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    if (i < 0) {
      i = paramString.lastIndexOf('\\');
    }
    return paramString.substring(0, i + 1);
  }
  
  public static String toJavaName(String paramString)
  {
    if (paramString.length() > 0)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      char c = paramString.charAt(0);
      localStringBuffer.append(Character.isJavaIdentifierStart(c) ? c : '_');
      int i = paramString.length();
      for (int j = 1; j < i; j++)
      {
        c = paramString.charAt(j);
        localStringBuffer.append(Character.isJavaIdentifierPart(c) ? c : '_');
      }
      return localStringBuffer.toString();
    }
    return paramString;
  }
  
  public static Type getJCRefType(String paramString)
  {
    return Type.getType(paramString);
  }
  
  public static String internalName(String paramString)
  {
    return paramString.replace('.', filesep);
  }
  
  public static void println(String paramString)
  {
    System.out.println(paramString);
  }
  
  public static void println(char paramChar)
  {
    System.out.println(paramChar);
  }
  
  public static void TRACE1()
  {
    System.out.println("TRACE1");
  }
  
  public static void TRACE2()
  {
    System.out.println("TRACE2");
  }
  
  public static void TRACE3()
  {
    System.out.println("TRACE3");
  }
  
  public static String replace(String paramString1, char paramChar, String paramString2)
  {
    return paramString1.indexOf(paramChar) < 0 ? paramString1 : replace(paramString1, String.valueOf(paramChar), new String[] { paramString2 });
  }
  
  public static String replace(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    int i = paramString1.length();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int j = 0; j < i; j++)
    {
      char c = paramString1.charAt(j);
      int k = paramString2.indexOf(c);
      if (k >= 0) {
        localStringBuffer.append(paramArrayOfString[k]);
      } else {
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }
  
  public static String escape(String paramString)
  {
    return replace(paramString, ".-/:", new String[] { "$dot$", "$dash$", "$slash$", "$colon$" });
  }
  
  public static String getLocalName(String paramString)
  {
    int i = paramString.lastIndexOf(":");
    return i > 0 ? paramString.substring(i + 1) : paramString;
  }
  
  public static String getPrefix(String paramString)
  {
    int i = paramString.lastIndexOf(":");
    return i > 0 ? paramString.substring(0, i) : "";
  }
  
  public static boolean isLiteral(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i - 1; j++) {
      if ((paramString.charAt(j) == '{') && (paramString.charAt(j + 1) != '{')) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isValidQNames(String paramString)
  {
    if ((paramString != null) && (!paramString.equals("")))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      while (localStringTokenizer.hasMoreTokens()) {
        if (!XML11Char.isXML11ValidQName(localStringTokenizer.nextToken())) {
          return false;
        }
      }
    }
    return true;
  }
  
  static
  {
    String str = SecuritySupport.getSystemProperty("file.separator", "/");
    filesep = str.charAt(0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
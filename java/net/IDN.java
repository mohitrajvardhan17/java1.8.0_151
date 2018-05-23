package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import sun.net.idn.Punycode;
import sun.net.idn.StringPrep;
import sun.text.normalizer.UCharacterIterator;

public final class IDN
{
  public static final int ALLOW_UNASSIGNED = 1;
  public static final int USE_STD3_ASCII_RULES = 2;
  private static final String ACE_PREFIX = "xn--";
  private static final int ACE_PREFIX_LENGTH;
  private static final int MAX_LABEL_LENGTH = 63;
  private static StringPrep namePrep;
  
  public static String toASCII(String paramString, int paramInt)
  {
    int i = 0;
    int j = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    if (isRootLabel(paramString)) {
      return ".";
    }
    while (i < paramString.length())
    {
      j = searchDots(paramString, i);
      localStringBuffer.append(toASCIIInternal(paramString.substring(i, j), paramInt));
      if (j != paramString.length()) {
        localStringBuffer.append('.');
      }
      i = j + 1;
    }
    return localStringBuffer.toString();
  }
  
  public static String toASCII(String paramString)
  {
    return toASCII(paramString, 0);
  }
  
  public static String toUnicode(String paramString, int paramInt)
  {
    int i = 0;
    int j = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    if (isRootLabel(paramString)) {
      return ".";
    }
    while (i < paramString.length())
    {
      j = searchDots(paramString, i);
      localStringBuffer.append(toUnicodeInternal(paramString.substring(i, j), paramInt));
      if (j != paramString.length()) {
        localStringBuffer.append('.');
      }
      i = j + 1;
    }
    return localStringBuffer.toString();
  }
  
  public static String toUnicode(String paramString)
  {
    return toUnicode(paramString, 0);
  }
  
  private IDN() {}
  
  private static String toASCIIInternal(String paramString, int paramInt)
  {
    boolean bool = isAllASCII(paramString);
    StringBuffer localStringBuffer;
    if (!bool)
    {
      UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramString);
      try
      {
        localStringBuffer = namePrep.prepare(localUCharacterIterator, paramInt);
      }
      catch (ParseException localParseException1)
      {
        throw new IllegalArgumentException(localParseException1);
      }
    }
    else
    {
      localStringBuffer = new StringBuffer(paramString);
    }
    if (localStringBuffer.length() == 0) {
      throw new IllegalArgumentException("Empty label is not a legal name");
    }
    int i = (paramInt & 0x2) != 0 ? 1 : 0;
    if (i != 0)
    {
      for (int j = 0; j < localStringBuffer.length(); j++)
      {
        int k = localStringBuffer.charAt(j);
        if (isNonLDHAsciiCodePoint(k)) {
          throw new IllegalArgumentException("Contains non-LDH ASCII characters");
        }
      }
      if ((localStringBuffer.charAt(0) == '-') || (localStringBuffer.charAt(localStringBuffer.length() - 1) == '-')) {
        throw new IllegalArgumentException("Has leading or trailing hyphen");
      }
    }
    if ((!bool) && (!isAllASCII(localStringBuffer.toString()))) {
      if (!startsWithACEPrefix(localStringBuffer))
      {
        try
        {
          localStringBuffer = Punycode.encode(localStringBuffer, null);
        }
        catch (ParseException localParseException2)
        {
          throw new IllegalArgumentException(localParseException2);
        }
        localStringBuffer = toASCIILower(localStringBuffer);
        localStringBuffer.insert(0, "xn--");
      }
      else
      {
        throw new IllegalArgumentException("The input starts with the ACE Prefix");
      }
    }
    if (localStringBuffer.length() > 63) {
      throw new IllegalArgumentException("The label in the input is too long");
    }
    return localStringBuffer.toString();
  }
  
  private static String toUnicodeInternal(String paramString, int paramInt)
  {
    Object localObject = null;
    boolean bool = isAllASCII(paramString);
    StringBuffer localStringBuffer1;
    if (!bool) {
      try
      {
        UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramString);
        localStringBuffer1 = namePrep.prepare(localUCharacterIterator, paramInt);
      }
      catch (Exception localException1)
      {
        return paramString;
      }
    } else {
      localStringBuffer1 = new StringBuffer(paramString);
    }
    if (startsWithACEPrefix(localStringBuffer1))
    {
      String str1 = localStringBuffer1.substring(ACE_PREFIX_LENGTH, localStringBuffer1.length());
      try
      {
        StringBuffer localStringBuffer2 = Punycode.decode(new StringBuffer(str1), null);
        String str2 = toASCII(localStringBuffer2.toString(), paramInt);
        if (str2.equalsIgnoreCase(localStringBuffer1.toString())) {
          return localStringBuffer2.toString();
        }
      }
      catch (Exception localException2) {}
    }
    return paramString;
  }
  
  private static boolean isNonLDHAsciiCodePoint(int paramInt)
  {
    return ((0 <= paramInt) && (paramInt <= 44)) || ((46 <= paramInt) && (paramInt <= 47)) || ((58 <= paramInt) && (paramInt <= 64)) || ((91 <= paramInt) && (paramInt <= 96)) || ((123 <= paramInt) && (paramInt <= 127));
  }
  
  private static int searchDots(String paramString, int paramInt)
  {
    for (int i = paramInt; (i < paramString.length()) && (!isLabelSeparator(paramString.charAt(i))); i++) {}
    return i;
  }
  
  private static boolean isRootLabel(String paramString)
  {
    return (paramString.length() == 1) && (isLabelSeparator(paramString.charAt(0)));
  }
  
  private static boolean isLabelSeparator(char paramChar)
  {
    return (paramChar == '.') || (paramChar == '。') || (paramChar == 65294) || (paramChar == 65377);
  }
  
  private static boolean isAllASCII(String paramString)
  {
    boolean bool = true;
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      if (j > 127)
      {
        bool = false;
        break;
      }
    }
    return bool;
  }
  
  private static boolean startsWithACEPrefix(StringBuffer paramStringBuffer)
  {
    boolean bool = true;
    if (paramStringBuffer.length() < ACE_PREFIX_LENGTH) {
      return false;
    }
    for (int i = 0; i < ACE_PREFIX_LENGTH; i++) {
      if (toASCIILower(paramStringBuffer.charAt(i)) != "xn--".charAt(i)) {
        bool = false;
      }
    }
    return bool;
  }
  
  private static char toASCIILower(char paramChar)
  {
    if (('A' <= paramChar) && (paramChar <= 'Z')) {
      return (char)(paramChar + 'a' - 65);
    }
    return paramChar;
  }
  
  private static StringBuffer toASCIILower(StringBuffer paramStringBuffer)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramStringBuffer.length(); i++) {
      localStringBuffer.append(toASCIILower(paramStringBuffer.charAt(i)));
    }
    return localStringBuffer;
  }
  
  static
  {
    ACE_PREFIX_LENGTH = "xn--".length();
    namePrep = null;
    InputStream localInputStream = null;
    try
    {
      if (System.getSecurityManager() != null) {
        localInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction()
        {
          public InputStream run()
          {
            return StringPrep.class.getResourceAsStream("uidna.spp");
          }
        });
      } else {
        localInputStream = StringPrep.class.getResourceAsStream("uidna.spp");
      }
      namePrep = new StringPrep(localInputStream);
      localInputStream.close();
    }
    catch (IOException localIOException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\IDN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
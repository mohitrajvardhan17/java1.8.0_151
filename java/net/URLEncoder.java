package java.net;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.util.BitSet;
import sun.security.action.GetPropertyAction;

public class URLEncoder
{
  static BitSet dontNeedEncoding;
  static final int caseDiff = 32;
  static String dfltEncName = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
  
  private URLEncoder() {}
  
  @Deprecated
  public static String encode(String paramString)
  {
    String str = null;
    try
    {
      str = encode(paramString, dfltEncName);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return str;
  }
  
  public static String encode(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer(paramString1.length());
    CharArrayWriter localCharArrayWriter = new CharArrayWriter();
    if (paramString2 == null) {
      throw new NullPointerException("charsetName");
    }
    Charset localCharset;
    try
    {
      localCharset = Charset.forName(paramString2);
    }
    catch (IllegalCharsetNameException localIllegalCharsetNameException)
    {
      throw new UnsupportedEncodingException(paramString2);
    }
    catch (UnsupportedCharsetException localUnsupportedCharsetException)
    {
      throw new UnsupportedEncodingException(paramString2);
    }
    int j = 0;
    while (j < paramString1.length())
    {
      int k = paramString1.charAt(j);
      if (dontNeedEncoding.get(k))
      {
        if (k == 32)
        {
          k = 43;
          i = 1;
        }
        localStringBuffer.append((char)k);
        j++;
      }
      else
      {
        do
        {
          localCharArrayWriter.write(k);
          if ((k >= 55296) && (k <= 56319) && (j + 1 < paramString1.length()))
          {
            int m = paramString1.charAt(j + 1);
            if ((m >= 56320) && (m <= 57343))
            {
              localCharArrayWriter.write(m);
              j++;
            }
          }
          j++;
        } while ((j < paramString1.length()) && (!dontNeedEncoding.get(k = paramString1.charAt(j))));
        localCharArrayWriter.flush();
        String str = new String(localCharArrayWriter.toCharArray());
        byte[] arrayOfByte = str.getBytes(localCharset);
        for (int n = 0; n < arrayOfByte.length; n++)
        {
          localStringBuffer.append('%');
          char c = Character.forDigit(arrayOfByte[n] >> 4 & 0xF, 16);
          if (Character.isLetter(c)) {
            c = (char)(c - ' ');
          }
          localStringBuffer.append(c);
          c = Character.forDigit(arrayOfByte[n] & 0xF, 16);
          if (Character.isLetter(c)) {
            c = (char)(c - ' ');
          }
          localStringBuffer.append(c);
        }
        localCharArrayWriter.reset();
        i = 1;
      }
    }
    return i != 0 ? localStringBuffer.toString() : paramString1;
  }
  
  static
  {
    dontNeedEncoding = new BitSet(256);
    for (int i = 97; i <= 122; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = 65; i <= 90; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = 48; i <= 57; i++) {
      dontNeedEncoding.set(i);
    }
    dontNeedEncoding.set(32);
    dontNeedEncoding.set(45);
    dontNeedEncoding.set(95);
    dontNeedEncoding.set(46);
    dontNeedEncoding.set(42);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URLEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
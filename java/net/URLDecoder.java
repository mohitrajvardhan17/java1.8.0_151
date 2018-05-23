package java.net;

import java.io.UnsupportedEncodingException;

public class URLDecoder
{
  static String dfltEncName = URLEncoder.dfltEncName;
  
  public URLDecoder() {}
  
  @Deprecated
  public static String decode(String paramString)
  {
    String str = null;
    try
    {
      str = decode(paramString, dfltEncName);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return str;
  }
  
  public static String decode(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    int i = 0;
    int j = paramString1.length();
    StringBuffer localStringBuffer = new StringBuffer(j > 500 ? j / 2 : j);
    int k = 0;
    if (paramString2.length() == 0) {
      throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
    }
    byte[] arrayOfByte = null;
    while (k < j)
    {
      char c = paramString1.charAt(k);
      switch (c)
      {
      case '+': 
        localStringBuffer.append(' ');
        k++;
        i = 1;
        break;
      case '%': 
        try
        {
          if (arrayOfByte == null) {
            arrayOfByte = new byte[(j - k) / 3];
          }
          int m = 0;
          while ((k + 2 < j) && (c == '%'))
          {
            int n = Integer.parseInt(paramString1.substring(k + 1, k + 3), 16);
            if (n < 0) {
              throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
            }
            arrayOfByte[(m++)] = ((byte)n);
            k += 3;
            if (k < j) {
              c = paramString1.charAt(k);
            }
          }
          if ((k < j) && (c == '%')) {
            throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
          }
          localStringBuffer.append(new String(arrayOfByte, 0, m, paramString2));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + localNumberFormatException.getMessage());
        }
        i = 1;
        break;
      default: 
        localStringBuffer.append(c);
        k++;
      }
    }
    return i != 0 ? localStringBuffer.toString() : paramString1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URLDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
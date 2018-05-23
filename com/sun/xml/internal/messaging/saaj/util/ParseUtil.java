package com.sun.xml.internal.messaging.saaj.util;

public class ParseUtil
{
  public ParseUtil() {}
  
  private static char unescape(String paramString, int paramInt)
  {
    return (char)Integer.parseInt(paramString.substring(paramInt + 1, paramInt + 3), 16);
  }
  
  public static String decode(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if (c != '%') {
        i++;
      } else {
        try
        {
          c = unescape(paramString, i);
          i += 3;
          if ((c & 0x80) != 0)
          {
            int j;
            switch (c >> '\004')
            {
            case 12: 
            case 13: 
              j = unescape(paramString, i);
              i += 3;
              c = (char)((c & 0x1F) << '\006' | j & 0x3F);
              break;
            case 14: 
              j = unescape(paramString, i);
              i += 3;
              int k = unescape(paramString, i);
              i += 3;
              c = (char)((c & 0xF) << '\f' | (j & 0x3F) << 6 | k & 0x3F);
              break;
            default: 
              throw new IllegalArgumentException();
            }
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new IllegalArgumentException();
        }
      }
      localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\ParseUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
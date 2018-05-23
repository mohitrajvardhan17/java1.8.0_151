package com.sun.jndi.toolkit.url;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

public final class UrlUtil
{
  private UrlUtil() {}
  
  public static final String decode(String paramString)
    throws MalformedURLException
  {
    try
    {
      return decode(paramString, "8859_1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new MalformedURLException("ISO-Latin-1 decoder unavailable");
    }
  }
  
  public static final String decode(String paramString1, String paramString2)
    throws MalformedURLException, UnsupportedEncodingException
  {
    try
    {
      return URLDecoder.decode(paramString1, paramString2);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      MalformedURLException localMalformedURLException = new MalformedURLException("Invalid URI encoding: " + paramString1);
      localMalformedURLException.initCause(localIllegalArgumentException);
      throw localMalformedURLException;
    }
  }
  
  public static final String encode(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    byte[] arrayOfByte = paramString1.getBytes(paramString2);
    int i = arrayOfByte.length;
    char[] arrayOfChar = new char[3 * i];
    int j = 0;
    for (int k = 0; k < i; k++) {
      if (((arrayOfByte[k] >= 97) && (arrayOfByte[k] <= 122)) || ((arrayOfByte[k] >= 65) && (arrayOfByte[k] <= 90)) || ((arrayOfByte[k] >= 48) && (arrayOfByte[k] <= 57)) || ("=,+;.'-@&/$_()!~*:".indexOf(arrayOfByte[k]) >= 0))
      {
        arrayOfChar[(j++)] = ((char)arrayOfByte[k]);
      }
      else
      {
        arrayOfChar[(j++)] = '%';
        arrayOfChar[(j++)] = Character.forDigit(0xF & arrayOfByte[k] >>> 4, 16);
        arrayOfChar[(j++)] = Character.forDigit(0xF & arrayOfByte[k], 16);
      }
    }
    return new String(arrayOfChar, 0, j);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\url\UrlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
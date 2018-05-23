package java.net;

import java.util.Formatter;
import java.util.Locale;
import sun.net.util.IPAddressUtil;

class HostPortrange
{
  String hostname;
  String scheme;
  int[] portrange;
  boolean wildcard;
  boolean literal;
  boolean ipv6;
  boolean ipv4;
  static final int PORT_MIN = 0;
  static final int PORT_MAX = 65535;
  static final int CASE_DIFF = -32;
  static final int[] HTTP_PORT = { 80, 80 };
  static final int[] HTTPS_PORT = { 443, 443 };
  static final int[] NO_PORT = { -1, -1 };
  
  boolean equals(HostPortrange paramHostPortrange)
  {
    return (hostname.equals(hostname)) && (portrange[0] == portrange[0]) && (portrange[1] == portrange[1]) && (wildcard == wildcard) && (literal == literal);
  }
  
  public int hashCode()
  {
    return hostname.hashCode() + portrange[0] + portrange[1];
  }
  
  HostPortrange(String paramString1, String paramString2)
  {
    String str2 = null;
    scheme = paramString1;
    int i;
    String str1;
    int j;
    if (paramString2.charAt(0) == '[')
    {
      ipv6 = (literal = 1);
      i = paramString2.indexOf(']');
      if (i != -1) {
        str1 = paramString2.substring(1, i);
      } else {
        throw new IllegalArgumentException("invalid IPv6 address: " + paramString2);
      }
      j = paramString2.indexOf(':', i + 1);
      if ((j != -1) && (paramString2.length() > j)) {
        str2 = paramString2.substring(j + 1);
      }
      byte[] arrayOfByte1 = IPAddressUtil.textToNumericFormatV6(str1);
      if (arrayOfByte1 == null) {
        throw new IllegalArgumentException("illegal IPv6 address");
      }
      StringBuilder localStringBuilder1 = new StringBuilder();
      Formatter localFormatter1 = new Formatter(localStringBuilder1, Locale.US);
      localFormatter1.format("%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x", new Object[] { Byte.valueOf(arrayOfByte1[0]), Byte.valueOf(arrayOfByte1[1]), Byte.valueOf(arrayOfByte1[2]), Byte.valueOf(arrayOfByte1[3]), Byte.valueOf(arrayOfByte1[4]), Byte.valueOf(arrayOfByte1[5]), Byte.valueOf(arrayOfByte1[6]), Byte.valueOf(arrayOfByte1[7]), Byte.valueOf(arrayOfByte1[8]), Byte.valueOf(arrayOfByte1[9]), Byte.valueOf(arrayOfByte1[10]), Byte.valueOf(arrayOfByte1[11]), Byte.valueOf(arrayOfByte1[12]), Byte.valueOf(arrayOfByte1[13]), Byte.valueOf(arrayOfByte1[14]), Byte.valueOf(arrayOfByte1[15]) });
      hostname = localStringBuilder1.toString();
    }
    else
    {
      i = paramString2.indexOf(':');
      if ((i != -1) && (paramString2.length() > i))
      {
        str1 = paramString2.substring(0, i);
        str2 = paramString2.substring(i + 1);
      }
      else
      {
        str1 = i == -1 ? paramString2 : paramString2.substring(0, i);
      }
      if (str1.lastIndexOf('*') > 0) {
        throw new IllegalArgumentException("invalid host wildcard specification");
      }
      if (str1.startsWith("*"))
      {
        wildcard = true;
        if (str1.equals("*")) {
          str1 = "";
        } else if (str1.startsWith("*.")) {
          str1 = toLowerCase(str1.substring(1));
        } else {
          throw new IllegalArgumentException("invalid host wildcard specification");
        }
      }
      else
      {
        j = str1.lastIndexOf('.');
        if ((j != -1) && (str1.length() > 1))
        {
          int k = 1;
          int m = j + 1;
          int n = str1.length();
          while (m < n)
          {
            int i1 = str1.charAt(m);
            if ((i1 < 48) || (i1 > 57))
            {
              k = 0;
              break;
            }
            m++;
          }
          ipv4 = (literal = k);
          if (k != 0)
          {
            byte[] arrayOfByte2 = IPAddressUtil.textToNumericFormatV4(str1);
            if (arrayOfByte2 == null) {
              throw new IllegalArgumentException("illegal IPv4 address");
            }
            StringBuilder localStringBuilder2 = new StringBuilder();
            Formatter localFormatter2 = new Formatter(localStringBuilder2, Locale.US);
            localFormatter2.format("%d.%d.%d.%d", new Object[] { Byte.valueOf(arrayOfByte2[0]), Byte.valueOf(arrayOfByte2[1]), Byte.valueOf(arrayOfByte2[2]), Byte.valueOf(arrayOfByte2[3]) });
            str1 = localStringBuilder2.toString();
          }
          else
          {
            str1 = toLowerCase(str1);
          }
        }
      }
      hostname = str1;
    }
    try
    {
      portrange = parsePort(str2);
    }
    catch (Exception localException)
    {
      throw new IllegalArgumentException("invalid port range: " + str2);
    }
  }
  
  static String toLowerCase(String paramString)
  {
    int i = paramString.length();
    StringBuilder localStringBuilder = null;
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if (((k >= 97) && (k <= 122)) || (k == 46))
      {
        if (localStringBuilder != null) {
          localStringBuilder.append(k);
        }
      }
      else if (((k >= 48) && (k <= 57)) || (k == 45))
      {
        if (localStringBuilder != null) {
          localStringBuilder.append(k);
        }
      }
      else if ((k >= 65) && (k <= 90))
      {
        if (localStringBuilder == null)
        {
          localStringBuilder = new StringBuilder(i);
          localStringBuilder.append(paramString, 0, j);
        }
        localStringBuilder.append((char)(k - -32));
      }
      else
      {
        throw new IllegalArgumentException("Invalid characters in hostname");
      }
    }
    return localStringBuilder == null ? paramString : localStringBuilder.toString();
  }
  
  public boolean literal()
  {
    return literal;
  }
  
  public boolean ipv4Literal()
  {
    return ipv4;
  }
  
  public boolean ipv6Literal()
  {
    return ipv6;
  }
  
  public String hostname()
  {
    return hostname;
  }
  
  public int[] portrange()
  {
    return portrange;
  }
  
  public boolean wildcard()
  {
    return wildcard;
  }
  
  int[] defaultPort()
  {
    if (scheme.equals("http")) {
      return HTTP_PORT;
    }
    if (scheme.equals("https")) {
      return HTTPS_PORT;
    }
    return NO_PORT;
  }
  
  int[] parsePort(String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return defaultPort();
    }
    if (paramString.equals("*")) {
      return new int[] { 0, 65535 };
    }
    try
    {
      int i = paramString.indexOf('-');
      if (i == -1)
      {
        int j = Integer.parseInt(paramString);
        return new int[] { j, j };
      }
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      int k;
      if (str1.equals("")) {
        k = 0;
      } else {
        k = Integer.parseInt(str1);
      }
      int m;
      if (str2.equals("")) {
        m = 65535;
      } else {
        m = Integer.parseInt(str2);
      }
      if ((k < 0) || (m < 0) || (m < k)) {
        return defaultPort();
      }
      return new int[] { k, m };
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return defaultPort();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\HostPortrange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
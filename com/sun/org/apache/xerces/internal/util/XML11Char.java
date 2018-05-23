package com.sun.org.apache.xerces.internal.util;

import java.util.Arrays;

public class XML11Char
{
  private static final byte[] XML11CHARS = new byte[65536];
  public static final int MASK_XML11_VALID = 1;
  public static final int MASK_XML11_SPACE = 2;
  public static final int MASK_XML11_NAME_START = 4;
  public static final int MASK_XML11_NAME = 8;
  public static final int MASK_XML11_CONTROL = 16;
  public static final int MASK_XML11_CONTENT = 32;
  public static final int MASK_XML11_NCNAME_START = 64;
  public static final int MASK_XML11_NCNAME = 128;
  public static final int MASK_XML11_CONTENT_INTERNAL = 48;
  
  public XML11Char() {}
  
  public static boolean isXML11Space(int paramInt)
  {
    return (paramInt < 65536) && ((XML11CHARS[paramInt] & 0x2) != 0);
  }
  
  public static boolean isXML11Valid(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x1) != 0)) || ((65536 <= paramInt) && (paramInt <= 1114111));
  }
  
  public static boolean isXML11Invalid(int paramInt)
  {
    return !isXML11Valid(paramInt);
  }
  
  public static boolean isXML11ValidLiteral(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x1) != 0) && ((XML11CHARS[paramInt] & 0x10) == 0)) || ((65536 <= paramInt) && (paramInt <= 1114111));
  }
  
  public static boolean isXML11Content(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x20) != 0)) || ((65536 <= paramInt) && (paramInt <= 1114111));
  }
  
  public static boolean isXML11InternalEntityContent(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x30) != 0)) || ((65536 <= paramInt) && (paramInt <= 1114111));
  }
  
  public static boolean isXML11NameStart(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x4) != 0)) || ((65536 <= paramInt) && (paramInt < 983040));
  }
  
  public static boolean isXML11Name(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x8) != 0)) || ((paramInt >= 65536) && (paramInt < 983040));
  }
  
  public static boolean isXML11NCNameStart(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x40) != 0)) || ((65536 <= paramInt) && (paramInt < 983040));
  }
  
  public static boolean isXML11NCName(int paramInt)
  {
    return ((paramInt < 65536) && ((XML11CHARS[paramInt] & 0x80) != 0)) || ((65536 <= paramInt) && (paramInt < 983040));
  }
  
  public static boolean isXML11NameHighSurrogate(int paramInt)
  {
    return (55296 <= paramInt) && (paramInt <= 56191);
  }
  
  public static boolean isXML11ValidName(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    int j = 1;
    char c1 = paramString.charAt(0);
    char c2;
    if (!isXML11NameStart(c1)) {
      if ((i > 1) && (isXML11NameHighSurrogate(c1)))
      {
        c2 = paramString.charAt(1);
        if ((!XMLChar.isLowSurrogate(c2)) || (!isXML11NameStart(XMLChar.supplemental(c1, c2)))) {
          return false;
        }
        j = 2;
      }
      else
      {
        return false;
      }
    }
    while (j < i)
    {
      c1 = paramString.charAt(j);
      if (!isXML11Name(c1))
      {
        j++;
        if ((j < i) && (isXML11NameHighSurrogate(c1)))
        {
          c2 = paramString.charAt(j);
          if ((!XMLChar.isLowSurrogate(c2)) || (!isXML11Name(XMLChar.supplemental(c1, c2)))) {
            return false;
          }
        }
        else
        {
          return false;
        }
      }
      j++;
    }
    return true;
  }
  
  public static boolean isXML11ValidNCName(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    int j = 1;
    char c1 = paramString.charAt(0);
    char c2;
    if (!isXML11NCNameStart(c1)) {
      if ((i > 1) && (isXML11NameHighSurrogate(c1)))
      {
        c2 = paramString.charAt(1);
        if ((!XMLChar.isLowSurrogate(c2)) || (!isXML11NCNameStart(XMLChar.supplemental(c1, c2)))) {
          return false;
        }
        j = 2;
      }
      else
      {
        return false;
      }
    }
    while (j < i)
    {
      c1 = paramString.charAt(j);
      if (!isXML11NCName(c1))
      {
        j++;
        if ((j < i) && (isXML11NameHighSurrogate(c1)))
        {
          c2 = paramString.charAt(j);
          if ((!XMLChar.isLowSurrogate(c2)) || (!isXML11NCName(XMLChar.supplemental(c1, c2)))) {
            return false;
          }
        }
        else
        {
          return false;
        }
      }
      j++;
    }
    return true;
  }
  
  public static boolean isXML11ValidNmtoken(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      char c1 = paramString.charAt(j);
      if (!isXML11Name(c1))
      {
        j++;
        if ((j < i) && (isXML11NameHighSurrogate(c1)))
        {
          char c2 = paramString.charAt(j);
          if ((!XMLChar.isLowSurrogate(c2)) || (!isXML11Name(XMLChar.supplemental(c1, c2)))) {
            return false;
          }
        }
        else
        {
          return false;
        }
      }
    }
    return true;
  }
  
  static
  {
    Arrays.fill(XML11CHARS, 1, 9, (byte)17);
    XML11CHARS[9] = 35;
    XML11CHARS[10] = 3;
    Arrays.fill(XML11CHARS, 11, 13, (byte)17);
    XML11CHARS[13] = 3;
    Arrays.fill(XML11CHARS, 14, 32, (byte)17);
    XML11CHARS[32] = 35;
    Arrays.fill(XML11CHARS, 33, 38, (byte)33);
    XML11CHARS[38] = 1;
    Arrays.fill(XML11CHARS, 39, 45, (byte)33);
    Arrays.fill(XML11CHARS, 45, 47, (byte)-87);
    XML11CHARS[47] = 33;
    Arrays.fill(XML11CHARS, 48, 58, (byte)-87);
    XML11CHARS[58] = 45;
    XML11CHARS[59] = 33;
    XML11CHARS[60] = 1;
    Arrays.fill(XML11CHARS, 61, 65, (byte)33);
    Arrays.fill(XML11CHARS, 65, 91, (byte)-19);
    Arrays.fill(XML11CHARS, 91, 93, (byte)33);
    XML11CHARS[93] = 1;
    XML11CHARS[94] = 33;
    XML11CHARS[95] = -19;
    XML11CHARS[96] = 33;
    Arrays.fill(XML11CHARS, 97, 123, (byte)-19);
    Arrays.fill(XML11CHARS, 123, 127, (byte)33);
    Arrays.fill(XML11CHARS, 127, 133, (byte)17);
    XML11CHARS[''] = 35;
    Arrays.fill(XML11CHARS, 134, 160, (byte)17);
    Arrays.fill(XML11CHARS, 160, 183, (byte)33);
    XML11CHARS['·'] = -87;
    Arrays.fill(XML11CHARS, 184, 192, (byte)33);
    Arrays.fill(XML11CHARS, 192, 215, (byte)-19);
    XML11CHARS['×'] = 33;
    Arrays.fill(XML11CHARS, 216, 247, (byte)-19);
    XML11CHARS['÷'] = 33;
    Arrays.fill(XML11CHARS, 248, 768, (byte)-19);
    Arrays.fill(XML11CHARS, 768, 880, (byte)-87);
    Arrays.fill(XML11CHARS, 880, 894, (byte)-19);
    XML11CHARS[';'] = 33;
    Arrays.fill(XML11CHARS, 895, 8192, (byte)-19);
    Arrays.fill(XML11CHARS, 8192, 8204, (byte)33);
    Arrays.fill(XML11CHARS, 8204, 8206, (byte)-19);
    Arrays.fill(XML11CHARS, 8206, 8232, (byte)33);
    XML11CHARS[' '] = 35;
    Arrays.fill(XML11CHARS, 8233, 8255, (byte)33);
    Arrays.fill(XML11CHARS, 8255, 8257, (byte)-87);
    Arrays.fill(XML11CHARS, 8257, 8304, (byte)33);
    Arrays.fill(XML11CHARS, 8304, 8592, (byte)-19);
    Arrays.fill(XML11CHARS, 8592, 11264, (byte)33);
    Arrays.fill(XML11CHARS, 11264, 12272, (byte)-19);
    Arrays.fill(XML11CHARS, 12272, 12289, (byte)33);
    Arrays.fill(XML11CHARS, 12289, 55296, (byte)-19);
    Arrays.fill(XML11CHARS, 57344, 63744, (byte)33);
    Arrays.fill(XML11CHARS, 63744, 64976, (byte)-19);
    Arrays.fill(XML11CHARS, 64976, 65008, (byte)33);
    Arrays.fill(XML11CHARS, 65008, 65534, (byte)-19);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\XML11Char.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
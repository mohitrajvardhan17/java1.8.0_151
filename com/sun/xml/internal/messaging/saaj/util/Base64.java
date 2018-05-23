package com.sun.xml.internal.messaging.saaj.util;

import java.io.PrintStream;

public final class Base64
{
  private static final int BASELENGTH = 255;
  private static final int LOOKUPLENGTH = 63;
  private static final int TWENTYFOURBITGROUP = 24;
  private static final int EIGHTBIT = 8;
  private static final int SIXTEENBIT = 16;
  private static final int SIXBIT = 6;
  private static final int FOURBYTE = 4;
  private static final byte PAD = 61;
  private static byte[] base64Alphabet = new byte['ÿ'];
  private static byte[] lookUpBase64Alphabet = new byte[63];
  static final int[] base64 = { 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64 };
  
  public Base64() {}
  
  static boolean isBase64(byte paramByte)
  {
    return (paramByte == 61) || (base64Alphabet[paramByte] != -1);
  }
  
  static boolean isArrayByteBase64(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    if (i == 0) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (!isBase64(paramArrayOfByte[j])) {
        return false;
      }
    }
    return true;
  }
  
  public static byte[] encode(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length * 8;
    int j = i % 24;
    int k = i / 24;
    byte[] arrayOfByte = null;
    if (j != 0) {
      arrayOfByte = new byte[(k + 1) * 4];
    } else {
      arrayOfByte = new byte[k * 4];
    }
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    for (i6 = 0; i6 < k; i6++)
    {
      i5 = i6 * 3;
      i1 = paramArrayOfByte[i5];
      i2 = paramArrayOfByte[(i5 + 1)];
      i3 = paramArrayOfByte[(i5 + 2)];
      n = (byte)(i2 & 0xF);
      m = (byte)(i1 & 0x3);
      i4 = i6 * 4;
      arrayOfByte[i4] = lookUpBase64Alphabet[(i1 >> 2)];
      arrayOfByte[(i4 + 1)] = lookUpBase64Alphabet[(i2 >> 4 | m << 4)];
      arrayOfByte[(i4 + 2)] = lookUpBase64Alphabet[(n << 2 | i3 >> 6)];
      arrayOfByte[(i4 + 3)] = lookUpBase64Alphabet[(i3 & 0x3F)];
    }
    i5 = i6 * 3;
    i4 = i6 * 4;
    if (j == 8)
    {
      i1 = paramArrayOfByte[i5];
      m = (byte)(i1 & 0x3);
      arrayOfByte[i4] = lookUpBase64Alphabet[(i1 >> 2)];
      arrayOfByte[(i4 + 1)] = lookUpBase64Alphabet[(m << 4)];
      arrayOfByte[(i4 + 2)] = 61;
      arrayOfByte[(i4 + 3)] = 61;
    }
    else if (j == 16)
    {
      i1 = paramArrayOfByte[i5];
      i2 = paramArrayOfByte[(i5 + 1)];
      n = (byte)(i2 & 0xF);
      m = (byte)(i1 & 0x3);
      arrayOfByte[i4] = lookUpBase64Alphabet[(i1 >> 2)];
      arrayOfByte[(i4 + 1)] = lookUpBase64Alphabet[(i2 >> 4 | m << 4)];
      arrayOfByte[(i4 + 2)] = lookUpBase64Alphabet[(n << 2)];
      arrayOfByte[(i4 + 3)] = 61;
    }
    return arrayOfByte;
  }
  
  public byte[] decode(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length / 4;
    byte[] arrayOfByte = null;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    arrayOfByte = new byte[i * 3 + 1];
    for (int i5 = 0; i5 < i; i5++)
    {
      i4 = i5 * 4;
      i1 = paramArrayOfByte[(i4 + 2)];
      i2 = paramArrayOfByte[(i4 + 3)];
      j = base64Alphabet[paramArrayOfByte[i4]];
      k = base64Alphabet[paramArrayOfByte[(i4 + 1)]];
      if ((i1 != 61) && (i2 != 61))
      {
        m = base64Alphabet[i1];
        n = base64Alphabet[i2];
        arrayOfByte[i3] = ((byte)(j << 2 | k >> 4));
        arrayOfByte[(i3 + 1)] = ((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
        arrayOfByte[(i3 + 2)] = ((byte)(m << 6 | n));
      }
      else if (i1 == 61)
      {
        arrayOfByte[i3] = ((byte)(j << 2 | k >> 4));
        arrayOfByte[(i3 + 1)] = ((byte)((k & 0xF) << 4));
        arrayOfByte[(i3 + 2)] = 0;
      }
      else if (i2 == 61)
      {
        m = base64Alphabet[i1];
        arrayOfByte[i3] = ((byte)(j << 2 | k >> 4));
        arrayOfByte[(i3 + 1)] = ((byte)((k & 0xF) << 4 | m >> 2 & 0xF));
        arrayOfByte[(i3 + 2)] = ((byte)(m << 6));
      }
      i3 += 3;
    }
    return arrayOfByte;
  }
  
  public static String base64Decode(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = 0;
    int k = 0;
    for (i = 0; i < arrayOfChar.length; i++)
    {
      int m = base64[(arrayOfChar[i] & 0xFF)];
      if (m >= 64)
      {
        if (arrayOfChar[i] != '=') {
          System.out.println("Wrong char in base64: " + arrayOfChar[i]);
        }
      }
      else
      {
        k = k << 6 | m;
        j += 6;
        if (j >= 8)
        {
          j -= 8;
          localStringBuffer.append((char)(k >> j & 0xFF));
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    for (int i = 0; i < 255; i++) {
      base64Alphabet[i] = -1;
    }
    for (i = 90; i >= 65; i--) {
      base64Alphabet[i] = ((byte)(i - 65));
    }
    for (i = 122; i >= 97; i--) {
      base64Alphabet[i] = ((byte)(i - 97 + 26));
    }
    for (i = 57; i >= 48; i--) {
      base64Alphabet[i] = ((byte)(i - 48 + 52));
    }
    base64Alphabet[43] = 62;
    base64Alphabet[47] = 63;
    for (i = 0; i <= 25; i++) {
      lookUpBase64Alphabet[i] = ((byte)(65 + i));
    }
    i = 26;
    for (int j = 0; i <= 51; j++)
    {
      lookUpBase64Alphabet[i] = ((byte)(97 + j));
      i++;
    }
    i = 52;
    for (j = 0; i <= 61; j++)
    {
      lookUpBase64Alphabet[i] = ((byte)(48 + j));
      i++;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
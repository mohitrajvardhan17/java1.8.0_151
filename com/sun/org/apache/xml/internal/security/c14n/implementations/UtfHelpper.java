package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UtfHelpper
{
  public UtfHelpper() {}
  
  static final void writeByte(String paramString, OutputStream paramOutputStream, Map<String, byte[]> paramMap)
    throws IOException
  {
    byte[] arrayOfByte = (byte[])paramMap.get(paramString);
    if (arrayOfByte == null)
    {
      arrayOfByte = getStringInUtf8(paramString);
      paramMap.put(paramString, arrayOfByte);
    }
    paramOutputStream.write(arrayOfByte);
  }
  
  static final void writeCharToUtf8(char paramChar, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramChar < '')
    {
      paramOutputStream.write(paramChar);
      return;
    }
    if (((paramChar >= 55296) && (paramChar <= 56319)) || ((paramChar >= 56320) && (paramChar <= 57343)))
    {
      paramOutputStream.write(63);
      return;
    }
    int j;
    int i;
    if (paramChar > '߿')
    {
      k = (char)(paramChar >>> '\f');
      j = 224;
      if (k > 0) {
        j |= k & 0xF;
      }
      paramOutputStream.write(j);
      j = 128;
      i = 63;
    }
    else
    {
      j = 192;
      i = 31;
    }
    int k = (char)(paramChar >>> '\006');
    if (k > 0) {
      j |= k & i;
    }
    paramOutputStream.write(j);
    paramOutputStream.write(0x80 | paramChar & 0x3F);
  }
  
  static final void writeStringToUtf8(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    int i = paramString.length();
    int j = 0;
    while (j < i)
    {
      int k = paramString.charAt(j++);
      if (k < 128)
      {
        paramOutputStream.write(k);
      }
      else if (((k >= 55296) && (k <= 56319)) || ((k >= 56320) && (k <= 57343)))
      {
        paramOutputStream.write(63);
      }
      else
      {
        int i1;
        int n;
        if (k > 2047)
        {
          m = (char)(k >>> 12);
          i1 = 224;
          if (m > 0) {
            i1 |= m & 0xF;
          }
          paramOutputStream.write(i1);
          i1 = 128;
          n = 63;
        }
        else
        {
          i1 = 192;
          n = 31;
        }
        int m = (char)(k >>> 6);
        if (m > 0) {
          i1 |= m & n;
        }
        paramOutputStream.write(i1);
        paramOutputStream.write(0x80 | k & 0x3F);
      }
    }
  }
  
  public static final byte[] getStringInUtf8(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    Object localObject = new byte[i];
    int k = 0;
    int m = 0;
    while (k < i)
    {
      int n = paramString.charAt(k++);
      if (n < 128)
      {
        localObject[(m++)] = ((byte)n);
      }
      else if (((n >= 55296) && (n <= 56319)) || ((n >= 56320) && (n <= 57343)))
      {
        localObject[(m++)] = 63;
      }
      else
      {
        if (j == 0)
        {
          byte[] arrayOfByte1 = new byte[3 * i];
          System.arraycopy(localObject, 0, arrayOfByte1, 0, m);
          localObject = arrayOfByte1;
          j = 1;
        }
        int i3;
        int i2;
        if (n > 2047)
        {
          i1 = (char)(n >>> 12);
          i3 = -32;
          if (i1 > 0) {
            i3 = (byte)(i3 | i1 & 0xF);
          }
          localObject[(m++)] = i3;
          i3 = -128;
          i2 = 63;
        }
        else
        {
          i3 = -64;
          i2 = 31;
        }
        int i1 = (char)(n >>> 6);
        if (i1 > 0) {
          i3 = (byte)(i3 | i1 & i2);
        }
        localObject[(m++)] = i3;
        localObject[(m++)] = ((byte)(0x80 | n & 0x3F));
      }
    }
    if (j != 0)
    {
      byte[] arrayOfByte2 = new byte[m];
      System.arraycopy(localObject, 0, arrayOfByte2, 0, m);
      localObject = arrayOfByte2;
    }
    return (byte[])localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\UtfHelpper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
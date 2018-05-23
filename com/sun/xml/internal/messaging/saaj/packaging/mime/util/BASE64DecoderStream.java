package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BASE64DecoderStream
  extends FilterInputStream
{
  private byte[] buffer = new byte[3];
  private int bufsize = 0;
  private int index = 0;
  private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static final byte[] pem_convert_array = new byte['Ā'];
  private byte[] decode_buffer = new byte[4];
  
  public BASE64DecoderStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public int read()
    throws IOException
  {
    if (index >= bufsize)
    {
      decode();
      if (bufsize == 0) {
        return -1;
      }
      index = 0;
    }
    return buffer[(index++)] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++)
    {
      int j;
      if ((j = read()) == -1)
      {
        if (i != 0) {
          break;
        }
        i = -1;
        break;
      }
      paramArrayOfByte[(paramInt1 + i)] = ((byte)j);
    }
    return i;
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public int available()
    throws IOException
  {
    return in.available() * 3 / 4 + (bufsize - index);
  }
  
  private void decode()
    throws IOException
  {
    bufsize = 0;
    int i = 0;
    while (i < 4)
    {
      j = in.read();
      if (j == -1)
      {
        if (i == 0) {
          return;
        }
        throw new IOException("Error in encoded stream, got " + i);
      }
      if (((j >= 0) && (j < 256) && (j == 61)) || (pem_convert_array[j] != -1)) {
        decode_buffer[(i++)] = ((byte)j);
      }
    }
    int j = pem_convert_array[(decode_buffer[0] & 0xFF)];
    int k = pem_convert_array[(decode_buffer[1] & 0xFF)];
    buffer[(bufsize++)] = ((byte)(j << 2 & 0xFC | k >>> 4 & 0x3));
    if (decode_buffer[2] == 61) {
      return;
    }
    j = k;
    k = pem_convert_array[(decode_buffer[2] & 0xFF)];
    buffer[(bufsize++)] = ((byte)(j << 4 & 0xF0 | k >>> 2 & 0xF));
    if (decode_buffer[3] == 61) {
      return;
    }
    j = k;
    k = pem_convert_array[(decode_buffer[3] & 0xFF)];
    buffer[(bufsize++)] = ((byte)(j << 6 & 0xC0 | k & 0x3F));
  }
  
  public static byte[] decode(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length / 4 * 3;
    if (i == 0) {
      return paramArrayOfByte;
    }
    if (paramArrayOfByte[(paramArrayOfByte.length - 1)] == 61)
    {
      i--;
      if (paramArrayOfByte[(paramArrayOfByte.length - 2)] == 61) {
        i--;
      }
    }
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    int k = 0;
    for (i = paramArrayOfByte.length; i > 0; i -= 4)
    {
      int m = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      int n = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      arrayOfByte[(k++)] = ((byte)(m << 2 & 0xFC | n >>> 4 & 0x3));
      if (paramArrayOfByte[j] == 61) {
        return arrayOfByte;
      }
      m = n;
      n = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      arrayOfByte[(k++)] = ((byte)(m << 4 & 0xF0 | n >>> 2 & 0xF));
      if (paramArrayOfByte[j] == 61) {
        return arrayOfByte;
      }
      m = n;
      n = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      arrayOfByte[(k++)] = ((byte)(m << 6 & 0xC0 | n & 0x3F));
    }
    return arrayOfByte;
  }
  
  static
  {
    for (int i = 0; i < 255; i++) {
      pem_convert_array[i] = -1;
    }
    for (i = 0; i < pem_array.length; i++) {
      pem_convert_array[pem_array[i]] = ((byte)i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\BASE64DecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
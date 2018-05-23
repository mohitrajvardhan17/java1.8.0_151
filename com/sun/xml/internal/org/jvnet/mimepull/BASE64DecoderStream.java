package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class BASE64DecoderStream
  extends FilterInputStream
{
  private byte[] buffer = new byte[3];
  private int bufsize = 0;
  private int index = 0;
  private byte[] input_buffer = new byte['῾'];
  private int input_pos = 0;
  private int input_len = 0;
  private boolean ignoreErrors = false;
  private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  private static final byte[] pem_convert_array = new byte['Ā'];
  
  public BASE64DecoderStream(InputStream paramInputStream)
  {
    super(paramInputStream);
    ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.base64.ignoreerrors", false);
  }
  
  public BASE64DecoderStream(InputStream paramInputStream, boolean paramBoolean)
  {
    super(paramInputStream);
    ignoreErrors = paramBoolean;
  }
  
  public int read()
    throws IOException
  {
    if (index >= bufsize)
    {
      bufsize = decode(buffer, 0, buffer.length);
      if (bufsize <= 0) {
        return -1;
      }
      index = 0;
    }
    return buffer[(index++)] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    while ((index < bufsize) && (paramInt2 > 0))
    {
      paramArrayOfByte[(paramInt1++)] = buffer[(index++)];
      paramInt2--;
    }
    if (index >= bufsize) {
      bufsize = (index = 0);
    }
    int j = paramInt2 / 3 * 3;
    int k;
    if (j > 0)
    {
      k = decode(paramArrayOfByte, paramInt1, j);
      paramInt1 += k;
      paramInt2 -= k;
      if (k != j)
      {
        if (paramInt1 == i) {
          return -1;
        }
        return paramInt1 - i;
      }
    }
    while (paramInt2 > 0)
    {
      k = read();
      if (k == -1) {
        break;
      }
      paramArrayOfByte[(paramInt1++)] = ((byte)k);
      paramInt2--;
    }
    if (paramInt1 == i) {
      return -1;
    }
    return paramInt1 - i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    for (long l = 0L; (paramLong-- > 0L) && (read() >= 0); l += 1L) {}
    return l;
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
  
  private int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    while (paramInt2 >= 3)
    {
      int j = 0;
      int k = 0;
      while (j < 4)
      {
        int m = getByte();
        if ((m == -1) || (m == -2))
        {
          int n;
          if (m == -1)
          {
            if (j == 0) {
              return paramInt1 - i;
            }
            if (!ignoreErrors) {
              throw new DecodingException("BASE64Decoder: Error in encoded stream: needed 4 valid base64 characters but only got " + j + " before EOF" + recentChars());
            }
            n = 1;
          }
          else
          {
            if ((j < 2) && (!ignoreErrors)) {
              throw new DecodingException("BASE64Decoder: Error in encoded stream: needed at least 2 valid base64 characters, but only got " + j + " before padding character (=)" + recentChars());
            }
            if (j == 0) {
              return paramInt1 - i;
            }
            n = 0;
          }
          int i1 = j - 1;
          if (i1 == 0) {
            i1 = 1;
          }
          j++;
          k <<= 6;
          while (j < 4)
          {
            if (n == 0)
            {
              m = getByte();
              if (m == -1)
              {
                if (!ignoreErrors) {
                  throw new DecodingException("BASE64Decoder: Error in encoded stream: hit EOF while looking for padding characters (=)" + recentChars());
                }
              }
              else if ((m != -2) && (!ignoreErrors)) {
                throw new DecodingException("BASE64Decoder: Error in encoded stream: found valid base64 character after a padding character (=)" + recentChars());
              }
            }
            k <<= 6;
            j++;
          }
          k >>= 8;
          if (i1 == 2) {
            paramArrayOfByte[(paramInt1 + 1)] = ((byte)(k & 0xFF));
          }
          k >>= 8;
          paramArrayOfByte[paramInt1] = ((byte)(k & 0xFF));
          paramInt1 += i1;
          return paramInt1 - i;
        }
        k <<= 6;
        j++;
        k |= m;
      }
      paramArrayOfByte[(paramInt1 + 2)] = ((byte)(k & 0xFF));
      k >>= 8;
      paramArrayOfByte[(paramInt1 + 1)] = ((byte)(k & 0xFF));
      k >>= 8;
      paramArrayOfByte[paramInt1] = ((byte)(k & 0xFF));
      paramInt2 -= 3;
      paramInt1 += 3;
    }
    return paramInt1 - i;
  }
  
  private int getByte()
    throws IOException
  {
    int i;
    do
    {
      if (input_pos >= input_len)
      {
        try
        {
          input_len = in.read(input_buffer);
        }
        catch (EOFException localEOFException)
        {
          return -1;
        }
        if (input_len <= 0) {
          return -1;
        }
        input_pos = 0;
      }
      i = input_buffer[(input_pos++)] & 0xFF;
      if (i == 61) {
        return -2;
      }
      i = pem_convert_array[i];
    } while (i == -1);
    return i;
  }
  
  private String recentChars()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = input_pos > 10 ? 10 : input_pos;
    if (i > 0)
    {
      localStringBuilder.append(", the ").append(i).append(" most recent characters were: \"");
      for (int j = input_pos - i; j < input_pos; j++)
      {
        char c = (char)(input_buffer[j] & 0xFF);
        switch (c)
        {
        case '\r': 
          localStringBuilder.append("\\r");
          break;
        case '\n': 
          localStringBuilder.append("\\n");
          break;
        case '\t': 
          localStringBuilder.append("\\t");
          break;
        case '\013': 
        case '\f': 
        default: 
          if ((c >= ' ') && (c < '')) {
            localStringBuilder.append(c);
          } else {
            localStringBuilder.append("\\").append(c);
          }
          break;
        }
      }
      localStringBuilder.append("\"");
    }
    return localStringBuilder.toString();
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
      int n = 3;
      int m = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      m <<= 6;
      m |= pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      m <<= 6;
      if (paramArrayOfByte[j] != 61) {
        m |= pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      } else {
        n--;
      }
      m <<= 6;
      if (paramArrayOfByte[j] != 61) {
        m |= pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      } else {
        n--;
      }
      if (n > 2) {
        arrayOfByte[(k + 2)] = ((byte)(m & 0xFF));
      }
      m >>= 8;
      if (n > 1) {
        arrayOfByte[(k + 1)] = ((byte)(m & 0xFF));
      }
      m >>= 8;
      arrayOfByte[k] = ((byte)(m & 0xFF));
      k += n;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\BASE64DecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
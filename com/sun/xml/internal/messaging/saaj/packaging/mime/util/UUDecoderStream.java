package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUDecoderStream
  extends FilterInputStream
{
  private String name;
  private int mode;
  private byte[] buffer;
  private int bufsize = 0;
  private int index = 0;
  private boolean gotPrefix = false;
  private boolean gotEnd = false;
  private LineInputStream lin;
  
  public UUDecoderStream(InputStream paramInputStream)
  {
    super(paramInputStream);
    lin = new LineInputStream(paramInputStream);
    buffer = new byte[45];
  }
  
  public int read()
    throws IOException
  {
    if (index >= bufsize)
    {
      readPrefix();
      if (!decode()) {
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
  
  public String getName()
    throws IOException
  {
    readPrefix();
    return name;
  }
  
  public int getMode()
    throws IOException
  {
    readPrefix();
    return mode;
  }
  
  private void readPrefix()
    throws IOException
  {
    if (gotPrefix) {
      return;
    }
    String str;
    do
    {
      str = lin.readLine();
      if (str == null) {
        throw new IOException("UUDecoder error: No Begin");
      }
    } while (!str.regionMatches(true, 0, "begin", 0, 5));
    try
    {
      mode = Integer.parseInt(str.substring(6, 9));
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IOException("UUDecoder error: " + localNumberFormatException.toString());
    }
    name = str.substring(10);
    gotPrefix = true;
  }
  
  private boolean decode()
    throws IOException
  {
    if (gotEnd) {
      return false;
    }
    bufsize = 0;
    String str;
    do
    {
      str = lin.readLine();
      if (str == null) {
        throw new IOException("Missing End");
      }
      if (str.regionMatches(true, 0, "end", 0, 3))
      {
        gotEnd = true;
        return false;
      }
    } while (str.length() == 0);
    int i = str.charAt(0);
    if (i < 32) {
      throw new IOException("Buffer format error");
    }
    i = i - 32 & 0x3F;
    if (i == 0)
    {
      str = lin.readLine();
      if ((str == null) || (!str.regionMatches(true, 0, "end", 0, 3))) {
        throw new IOException("Missing End");
      }
      gotEnd = true;
      return false;
    }
    int j = (i * 8 + 5) / 6;
    if (str.length() < j + 1) {
      throw new IOException("Short buffer error");
    }
    int k = 1;
    while (bufsize < i)
    {
      int m = (byte)(str.charAt(k++) - ' ' & 0x3F);
      int n = (byte)(str.charAt(k++) - ' ' & 0x3F);
      buffer[(bufsize++)] = ((byte)(m << 2 & 0xFC | n >>> 4 & 0x3));
      if (bufsize < i)
      {
        m = n;
        n = (byte)(str.charAt(k++) - ' ' & 0x3F);
        buffer[(bufsize++)] = ((byte)(m << 4 & 0xF0 | n >>> 2 & 0xF));
      }
      if (bufsize < i)
      {
        m = n;
        n = (byte)(str.charAt(k++) - ' ' & 0x3F);
        buffer[(bufsize++)] = ((byte)(m << 6 & 0xC0 | n & 0x3F));
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\UUDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class UUDecoderStream
  extends FilterInputStream
{
  private String name;
  private int mode;
  private byte[] buffer = new byte[45];
  private int bufsize = 0;
  private int index = 0;
  private boolean gotPrefix = false;
  private boolean gotEnd = false;
  private LineInputStream lin;
  private boolean ignoreErrors;
  private boolean ignoreMissingBeginEnd;
  private String readAhead;
  
  public UUDecoderStream(InputStream paramInputStream)
  {
    super(paramInputStream);
    lin = new LineInputStream(paramInputStream);
    ignoreErrors = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoreerrors", false);
    ignoreMissingBeginEnd = PropUtil.getBooleanSystemProperty("mail.mime.uudecode.ignoremissingbeginend", false);
  }
  
  public UUDecoderStream(InputStream paramInputStream, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramInputStream);
    lin = new LineInputStream(paramInputStream);
    ignoreErrors = paramBoolean1;
    ignoreMissingBeginEnd = paramBoolean2;
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
    mode = 438;
    name = "encoder.buf";
    for (;;)
    {
      String str = lin.readLine();
      if (str == null)
      {
        if (!ignoreMissingBeginEnd) {
          throw new DecodingException("UUDecoder: Missing begin");
        }
        gotPrefix = true;
        gotEnd = true;
        break;
      }
      if (str.regionMatches(false, 0, "begin", 0, 5))
      {
        try
        {
          mode = Integer.parseInt(str.substring(6, 9));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          if (!ignoreErrors) {
            throw new DecodingException("UUDecoder: Error in mode: " + localNumberFormatException.toString());
          }
        }
        if (str.length() > 10) {
          name = str.substring(10);
        } else if (!ignoreErrors) {
          throw new DecodingException("UUDecoder: Missing name: " + str);
        }
        gotPrefix = true;
        break;
      }
      if ((ignoreMissingBeginEnd) && (str.length() != 0))
      {
        int i = str.charAt(0);
        i = i - 32 & 0x3F;
        int j = (i * 8 + 5) / 6;
        if ((j == 0) || (str.length() >= j + 1))
        {
          readAhead = str;
          gotPrefix = true;
          break;
        }
      }
    }
  }
  
  private boolean decode()
    throws IOException
  {
    if (gotEnd) {
      return false;
    }
    bufsize = 0;
    int i = 0;
    String str;
    do
    {
      do
      {
        do
        {
          if (readAhead != null)
          {
            str = readAhead;
            readAhead = null;
          }
          else
          {
            str = lin.readLine();
          }
          if (str == null)
          {
            if (!ignoreMissingBeginEnd) {
              throw new DecodingException("UUDecoder: Missing end at EOF");
            }
            gotEnd = true;
            return false;
          }
          if (str.equals("end"))
          {
            gotEnd = true;
            return false;
          }
        } while (str.length() == 0);
        i = str.charAt(0);
        if (i >= 32) {
          break;
        }
      } while (ignoreErrors);
      throw new DecodingException("UUDecoder: Buffer format error");
      i = i - 32 & 0x3F;
      if (i == 0)
      {
        str = lin.readLine();
        if (((str == null) || (!str.equals("end"))) && (!ignoreMissingBeginEnd)) {
          throw new DecodingException("UUDecoder: Missing End after count 0 line");
        }
        gotEnd = true;
        return false;
      }
      j = (i * 8 + 5) / 6;
      if (str.length() >= j + 1) {
        break;
      }
    } while (ignoreErrors);
    throw new DecodingException("UUDecoder: Short buffer error");
    int j = 1;
    while (bufsize < i)
    {
      int k = (byte)(str.charAt(j++) - ' ' & 0x3F);
      int m = (byte)(str.charAt(j++) - ' ' & 0x3F);
      buffer[(bufsize++)] = ((byte)(k << 2 & 0xFC | m >>> 4 & 0x3));
      if (bufsize < i)
      {
        k = m;
        m = (byte)(str.charAt(j++) - ' ' & 0x3F);
        buffer[(bufsize++)] = ((byte)(k << 4 & 0xF0 | m >>> 2 & 0xF));
      }
      if (bufsize < i)
      {
        k = m;
        m = (byte)(str.charAt(j++) - ' ' & 0x3F);
        buffer[(bufsize++)] = ((byte)(k << 6 & 0xC0 | m & 0x3F));
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\UUDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
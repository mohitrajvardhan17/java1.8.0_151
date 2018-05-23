package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

final class QPDecoderStream
  extends FilterInputStream
{
  private byte[] ba = new byte[2];
  private int spaces = 0;
  
  public QPDecoderStream(InputStream paramInputStream)
  {
    super(new PushbackInputStream(paramInputStream, 2));
  }
  
  public int read()
    throws IOException
  {
    if (spaces > 0)
    {
      spaces -= 1;
      return 32;
    }
    int i = in.read();
    if (i == 32)
    {
      while ((i = in.read()) == 32) {
        spaces += 1;
      }
      if ((i == 13) || (i == 10) || (i == -1))
      {
        spaces = 0;
      }
      else
      {
        ((PushbackInputStream)in).unread(i);
        i = 32;
      }
      return i;
    }
    if (i == 61)
    {
      int j = in.read();
      if (j == 10) {
        return read();
      }
      if (j == 13)
      {
        int k = in.read();
        if (k != 10) {
          ((PushbackInputStream)in).unread(k);
        }
        return read();
      }
      if (j == -1) {
        return -1;
      }
      ba[0] = ((byte)j);
      ba[1] = ((byte)in.read());
      try
      {
        return ASCIIUtility.parseInt(ba, 0, 2, 16);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        ((PushbackInputStream)in).unread(ba);
        return i;
      }
    }
    return i;
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
    return in.available();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\QPDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
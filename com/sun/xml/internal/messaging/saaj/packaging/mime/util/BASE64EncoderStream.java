package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BASE64EncoderStream
  extends FilterOutputStream
{
  private byte[] buffer = new byte[3];
  private int bufsize = 0;
  private int count = 0;
  private int bytesPerLine;
  private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  
  public BASE64EncoderStream(OutputStream paramOutputStream, int paramInt)
  {
    super(paramOutputStream);
    bytesPerLine = paramInt;
  }
  
  public BASE64EncoderStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, 76);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    buffer[(bufsize++)] = ((byte)paramInt);
    if (bufsize == 3)
    {
      encode();
      bufsize = 0;
    }
  }
  
  public void flush()
    throws IOException
  {
    if (bufsize > 0)
    {
      encode();
      bufsize = 0;
    }
    out.flush();
  }
  
  public void close()
    throws IOException
  {
    flush();
    out.close();
  }
  
  private void encode()
    throws IOException
  {
    if (count + 4 > bytesPerLine)
    {
      out.write(13);
      out.write(10);
      count = 0;
    }
    int i;
    int j;
    int k;
    if (bufsize == 1)
    {
      i = buffer[0];
      j = 0;
      k = 0;
      out.write(pem_array[(i >>> 2 & 0x3F)]);
      out.write(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
      out.write(61);
      out.write(61);
    }
    else if (bufsize == 2)
    {
      i = buffer[0];
      j = buffer[1];
      k = 0;
      out.write(pem_array[(i >>> 2 & 0x3F)]);
      out.write(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
      out.write(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
      out.write(61);
    }
    else
    {
      i = buffer[0];
      j = buffer[1];
      k = buffer[2];
      out.write(pem_array[(i >>> 2 & 0x3F)]);
      out.write(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
      out.write(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
      out.write(pem_array[(k & 0x3F)]);
    }
    count += 4;
  }
  
  public static byte[] encode(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length == 0) {
      return paramArrayOfByte;
    }
    byte[] arrayOfByte = new byte[(paramArrayOfByte.length + 2) / 3 * 4];
    int i = 0;
    int j = 0;
    for (int k = paramArrayOfByte.length; k > 0; k -= 3)
    {
      int m;
      int n;
      int i1;
      if (k == 1)
      {
        m = paramArrayOfByte[(i++)];
        n = 0;
        i1 = 0;
        arrayOfByte[(j++)] = ((byte)pem_array[(m >>> 2 & 0x3F)]);
        arrayOfByte[(j++)] = ((byte)pem_array[((m << 4 & 0x30) + (n >>> 4 & 0xF))]);
        arrayOfByte[(j++)] = 61;
        arrayOfByte[(j++)] = 61;
      }
      else if (k == 2)
      {
        m = paramArrayOfByte[(i++)];
        n = paramArrayOfByte[(i++)];
        i1 = 0;
        arrayOfByte[(j++)] = ((byte)pem_array[(m >>> 2 & 0x3F)]);
        arrayOfByte[(j++)] = ((byte)pem_array[((m << 4 & 0x30) + (n >>> 4 & 0xF))]);
        arrayOfByte[(j++)] = ((byte)pem_array[((n << 2 & 0x3C) + (i1 >>> 6 & 0x3))]);
        arrayOfByte[(j++)] = 61;
      }
      else
      {
        m = paramArrayOfByte[(i++)];
        n = paramArrayOfByte[(i++)];
        i1 = paramArrayOfByte[(i++)];
        arrayOfByte[(j++)] = ((byte)pem_array[(m >>> 2 & 0x3F)]);
        arrayOfByte[(j++)] = ((byte)pem_array[((m << 4 & 0x30) + (n >>> 4 & 0xF))]);
        arrayOfByte[(j++)] = ((byte)pem_array[((n << 2 & 0x3C) + (i1 >>> 6 & 0x3))]);
        arrayOfByte[(j++)] = ((byte)pem_array[(i1 & 0x3F)]);
      }
    }
    return arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\util\BASE64EncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
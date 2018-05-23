package com.sun.xml.internal.ws.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayBuffer
  extends OutputStream
{
  protected byte[] buf;
  private int count;
  private static final int CHUNK_SIZE = 4096;
  
  public ByteArrayBuffer()
  {
    this(32);
  }
  
  public ByteArrayBuffer(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException();
    }
    buf = new byte[paramInt];
  }
  
  public ByteArrayBuffer(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public ByteArrayBuffer(byte[] paramArrayOfByte, int paramInt)
  {
    buf = paramArrayOfByte;
    count = paramInt;
  }
  
  public final void write(InputStream paramInputStream)
    throws IOException
  {
    for (;;)
    {
      int i = buf.length - count;
      int j = paramInputStream.read(buf, count, i);
      if (j < 0) {
        return;
      }
      count += j;
      if (i == j) {
        ensureCapacity(buf.length * 2);
      }
    }
  }
  
  public final void write(int paramInt)
  {
    int i = count + 1;
    ensureCapacity(i);
    buf[count] = ((byte)paramInt);
    count = i;
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = count + paramInt2;
    ensureCapacity(i);
    System.arraycopy(paramArrayOfByte, paramInt1, buf, count, paramInt2);
    count = i;
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (paramInt > buf.length)
    {
      byte[] arrayOfByte = new byte[Math.max(buf.length << 1, paramInt)];
      System.arraycopy(buf, 0, arrayOfByte, 0, count);
      buf = arrayOfByte;
    }
  }
  
  public final void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    int i = count;
    int j = 0;
    while (i > 0)
    {
      int k = i > 4096 ? 4096 : i;
      paramOutputStream.write(buf, j, k);
      i -= k;
      j += k;
    }
  }
  
  public final void reset()
  {
    count = 0;
  }
  
  /**
   * @deprecated
   */
  public final byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[count];
    System.arraycopy(buf, 0, arrayOfByte, 0, count);
    return arrayOfByte;
  }
  
  public final int size()
  {
    return count;
  }
  
  public final byte[] getRawData()
  {
    return buf;
  }
  
  public void close()
    throws IOException
  {}
  
  public final InputStream newInputStream()
  {
    return new ByteArrayInputStream(buf, 0, count);
  }
  
  public final InputStream newInputStream(int paramInt1, int paramInt2)
  {
    return new ByteArrayInputStream(buf, paramInt1, paramInt2);
  }
  
  public String toString()
  {
    return new String(buf, 0, count);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\ByteArrayBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
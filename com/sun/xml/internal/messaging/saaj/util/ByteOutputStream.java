package com.sun.xml.internal.messaging.saaj.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteOutputStream
  extends OutputStream
{
  protected byte[] buf;
  protected int count = 0;
  
  public ByteOutputStream()
  {
    this(1024);
  }
  
  public ByteOutputStream(int paramInt)
  {
    buf = new byte[paramInt];
  }
  
  public void write(InputStream paramInputStream)
    throws IOException
  {
    int i;
    if ((paramInputStream instanceof ByteArrayInputStream))
    {
      i = paramInputStream.available();
      ensureCapacity(i);
      count += paramInputStream.read(buf, count, i);
      return;
    }
    for (;;)
    {
      i = buf.length - count;
      int j = paramInputStream.read(buf, count, i);
      if (j < 0) {
        return;
      }
      count += j;
      if (i == j) {
        ensureCapacity(count);
      }
    }
  }
  
  public void write(int paramInt)
  {
    ensureCapacity(1);
    buf[count] = ((byte)paramInt);
    count += 1;
  }
  
  private void ensureCapacity(int paramInt)
  {
    int i = paramInt + count;
    if (i > buf.length)
    {
      byte[] arrayOfByte = new byte[Math.max(buf.length << 1, i)];
      System.arraycopy(buf, 0, arrayOfByte, 0, count);
      buf = arrayOfByte;
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    ensureCapacity(paramInt2);
    System.arraycopy(paramArrayOfByte, paramInt1, buf, count, paramInt2);
    count += paramInt2;
  }
  
  public void write(byte[] paramArrayOfByte)
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void writeAsAscii(String paramString)
  {
    int i = paramString.length();
    ensureCapacity(i);
    int j = count;
    for (int k = 0; k < i; k++) {
      buf[(j++)] = ((byte)paramString.charAt(k));
    }
    count = j;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(buf, 0, count);
  }
  
  public void reset()
  {
    count = 0;
  }
  
  /**
   * @deprecated
   */
  public byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[count];
    System.arraycopy(buf, 0, arrayOfByte, 0, count);
    return arrayOfByte;
  }
  
  public int size()
  {
    return count;
  }
  
  public ByteInputStream newInputStream()
  {
    return new ByteInputStream(buf, count);
  }
  
  public String toString()
  {
    return new String(buf, 0, count);
  }
  
  public void close() {}
  
  public byte[] getBytes()
  {
    return buf;
  }
  
  public int getCount()
  {
    return count;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\ByteOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
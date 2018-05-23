package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.OutputStream;

public class UnsyncBufferedOutputStream
  extends OutputStream
{
  static final int size = 8192;
  private int pointer = 0;
  private final OutputStream out;
  private final byte[] buf = new byte[' '];
  
  public UnsyncBufferedOutputStream(OutputStream paramOutputStream)
  {
    out = paramOutputStream;
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = pointer + paramInt2;
    if (i > 8192)
    {
      flushBuffer();
      if (paramInt2 > 8192)
      {
        out.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      i = paramInt2;
    }
    System.arraycopy(paramArrayOfByte, paramInt1, buf, pointer, paramInt2);
    pointer = i;
  }
  
  private void flushBuffer()
    throws IOException
  {
    if (pointer > 0) {
      out.write(buf, 0, pointer);
    }
    pointer = 0;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (pointer >= 8192) {
      flushBuffer();
    }
    buf[(pointer++)] = ((byte)paramInt);
  }
  
  public void flush()
    throws IOException
  {
    flushBuffer();
    out.flush();
  }
  
  public void close()
    throws IOException
  {
    flush();
    out.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\UnsyncBufferedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
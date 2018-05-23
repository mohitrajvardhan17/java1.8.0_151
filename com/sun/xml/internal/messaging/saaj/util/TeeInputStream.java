package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream
  extends InputStream
{
  protected InputStream source;
  protected OutputStream copySink;
  
  public TeeInputStream(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    copySink = paramOutputStream;
    source = paramInputStream;
  }
  
  public int read()
    throws IOException
  {
    int i = source.read();
    copySink.write(i);
    return i;
  }
  
  public int available()
    throws IOException
  {
    return source.available();
  }
  
  public void close()
    throws IOException
  {
    source.close();
  }
  
  public synchronized void mark(int paramInt)
  {
    source.mark(paramInt);
  }
  
  public boolean markSupported()
  {
    return source.markSupported();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = source.read(paramArrayOfByte, paramInt1, paramInt2);
    copySink.write(paramArrayOfByte, paramInt1, paramInt2);
    return i;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = source.read(paramArrayOfByte);
    copySink.write(paramArrayOfByte);
    return i;
  }
  
  public synchronized void reset()
    throws IOException
  {
    source.reset();
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return source.skip(paramLong);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\TeeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
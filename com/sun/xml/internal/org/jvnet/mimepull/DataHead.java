package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class DataHead
{
  volatile Chunk head;
  volatile Chunk tail;
  DataFile dataFile;
  private final MIMEPart part;
  boolean readOnce;
  volatile long inMemory;
  private Throwable consumedAt;
  
  DataHead(MIMEPart paramMIMEPart)
  {
    part = paramMIMEPart;
  }
  
  void addBody(ByteBuffer paramByteBuffer)
  {
    synchronized (this)
    {
      inMemory += paramByteBuffer.limit();
    }
    if (tail != null) {
      tail = tail.createNext(this, paramByteBuffer);
    } else {
      head = (tail = new Chunk(new MemoryData(paramByteBuffer, part.msg.config)));
    }
  }
  
  void doneParsing() {}
  
  void moveTo(File paramFile)
  {
    if (dataFile != null) {
      dataFile.renameTo(paramFile);
    } else {
      try
      {
        FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
        try
        {
          InputStream localInputStream = readOnce();
          byte[] arrayOfByte = new byte[' '];
          int i;
          while ((i = localInputStream.read(arrayOfByte)) != -1) {
            localFileOutputStream.write(arrayOfByte, 0, i);
          }
        }
        finally
        {
          if (localFileOutputStream != null) {
            localFileOutputStream.close();
          }
        }
      }
      catch (IOException localIOException)
      {
        throw new MIMEParsingException(localIOException);
      }
    }
  }
  
  void close()
  {
    head = (tail = null);
    if (dataFile != null) {
      dataFile.close();
    }
  }
  
  public InputStream read()
  {
    if (readOnce) {
      throw new IllegalStateException("readOnce() is called before, read() cannot be called later.");
    }
    while (tail == null) {
      if (!part.msg.makeProgress()) {
        throw new IllegalStateException("No such MIME Part: " + part);
      }
    }
    if (head == null) {
      throw new IllegalStateException("Already read. Probably readOnce() is called before.");
    }
    return new ReadMultiStream();
  }
  
  private boolean unconsumed()
  {
    if (consumedAt != null)
    {
      AssertionError localAssertionError = new AssertionError("readOnce() is already called before. See the nested exception from where it's called.");
      localAssertionError.initCause(consumedAt);
      throw localAssertionError;
    }
    consumedAt = new Exception().fillInStackTrace();
    return true;
  }
  
  public InputStream readOnce()
  {
    assert (unconsumed());
    if (readOnce) {
      throw new IllegalStateException("readOnce() is called before. It can only be called once.");
    }
    readOnce = true;
    while (tail == null) {
      if ((!part.msg.makeProgress()) && (tail == null)) {
        throw new IllegalStateException("No such Part: " + part);
      }
    }
    ReadOnceStream localReadOnceStream = new ReadOnceStream();
    head = null;
    return localReadOnceStream;
  }
  
  class ReadMultiStream
    extends InputStream
  {
    Chunk current = head;
    int offset;
    int len = current.data.size();
    byte[] buf = current.data.read();
    boolean closed;
    
    public ReadMultiStream() {}
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (!fetch()) {
        return -1;
      }
      paramInt2 = Math.min(paramInt2, len - offset);
      System.arraycopy(buf, offset, paramArrayOfByte, paramInt1, paramInt2);
      offset += paramInt2;
      return paramInt2;
    }
    
    public int read()
      throws IOException
    {
      if (!fetch()) {
        return -1;
      }
      return buf[(offset++)] & 0xFF;
    }
    
    void adjustInMemoryUsage() {}
    
    private boolean fetch()
      throws IOException
    {
      if (closed) {
        throw new IOException("Stream already closed");
      }
      if (current == null) {
        return false;
      }
      while (offset == len)
      {
        while ((!part.parsed) && (current.next == null)) {
          part.msg.makeProgress();
        }
        current = current.next;
        if (current == null) {
          return false;
        }
        adjustInMemoryUsage();
        offset = 0;
        buf = current.data.read();
        len = current.data.size();
      }
      return true;
    }
    
    public void close()
      throws IOException
    {
      super.close();
      current = null;
      closed = true;
    }
  }
  
  final class ReadOnceStream
    extends DataHead.ReadMultiStream
  {
    ReadOnceStream()
    {
      super();
    }
    
    void adjustInMemoryUsage()
    {
      synchronized (DataHead.this)
      {
        inMemory -= current.data.size();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\DataHead.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
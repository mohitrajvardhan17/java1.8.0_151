package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadAllStream
  extends InputStream
{
  @NotNull
  private final MemoryStream memStream = new MemoryStream(null);
  @NotNull
  private final FileStream fileStream = new FileStream(null);
  private boolean readAll;
  private boolean closed;
  private static final Logger LOGGER = Logger.getLogger(ReadAllStream.class.getName());
  
  public ReadAllStream() {}
  
  public void readAll(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    assert (!readAll);
    readAll = true;
    boolean bool = memStream.readAll(paramInputStream, paramLong);
    if (!bool) {
      fileStream.readAll(paramInputStream);
    }
  }
  
  public int read()
    throws IOException
  {
    int i = memStream.read();
    if (i == -1) {
      i = fileStream.read();
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = memStream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1) {
      i = fileStream.read(paramArrayOfByte, paramInt1, paramInt2);
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    if (!closed)
    {
      memStream.close();
      fileStream.close();
      closed = true;
    }
  }
  
  private static class FileStream
    extends InputStream
  {
    @Nullable
    private File tempFile;
    @Nullable
    private FileInputStream fin;
    
    private FileStream() {}
    
    void readAll(InputStream paramInputStream)
      throws IOException
    {
      tempFile = File.createTempFile("jaxws", ".bin");
      FileOutputStream localFileOutputStream = new FileOutputStream(tempFile);
      try
      {
        byte[] arrayOfByte = new byte[' '];
        int i;
        while ((i = paramInputStream.read(arrayOfByte)) != -1) {
          localFileOutputStream.write(arrayOfByte, 0, i);
        }
      }
      finally
      {
        localFileOutputStream.close();
      }
      fin = new FileInputStream(tempFile);
    }
    
    public int read()
      throws IOException
    {
      return fin != null ? fin.read() : -1;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      return fin != null ? fin.read(paramArrayOfByte, paramInt1, paramInt2) : -1;
    }
    
    public void close()
      throws IOException
    {
      if (fin != null) {
        fin.close();
      }
      if (tempFile != null)
      {
        boolean bool = tempFile.delete();
        if (!bool) {
          ReadAllStream.LOGGER.log(Level.INFO, "File {0} could not be deleted", tempFile);
        }
      }
    }
  }
  
  private static class MemoryStream
    extends InputStream
  {
    private Chunk head;
    private Chunk tail;
    private int curOff;
    
    private MemoryStream() {}
    
    private void add(byte[] paramArrayOfByte, int paramInt)
    {
      if (tail != null) {
        tail = tail.createNext(paramArrayOfByte, 0, paramInt);
      } else {
        head = (tail = new Chunk(paramArrayOfByte, 0, paramInt));
      }
    }
    
    boolean readAll(InputStream paramInputStream, long paramLong)
      throws IOException
    {
      long l = 0L;
      for (;;)
      {
        byte[] arrayOfByte = new byte[' '];
        int i = fill(paramInputStream, arrayOfByte);
        l += i;
        if (i != 0) {
          add(arrayOfByte, i);
        }
        if (i != arrayOfByte.length) {
          return true;
        }
        if (l > paramLong) {
          return false;
        }
      }
    }
    
    private int fill(InputStream paramInputStream, byte[] paramArrayOfByte)
      throws IOException
    {
      int j = 0;
      int i;
      while ((j < paramArrayOfByte.length) && ((i = paramInputStream.read(paramArrayOfByte, j, paramArrayOfByte.length - j)) != -1)) {
        j += i;
      }
      return j;
    }
    
    public int read()
      throws IOException
    {
      if (!fetch()) {
        return -1;
      }
      return head.buf[(curOff++)] & 0xFF;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (!fetch()) {
        return -1;
      }
      paramInt2 = Math.min(paramInt2, head.len - (curOff - head.off));
      System.arraycopy(head.buf, curOff, paramArrayOfByte, paramInt1, paramInt2);
      curOff += paramInt2;
      return paramInt2;
    }
    
    private boolean fetch()
    {
      if (head == null) {
        return false;
      }
      if (curOff == head.off + head.len)
      {
        head = head.next;
        if (head == null) {
          return false;
        }
        curOff = head.off;
      }
      return true;
    }
    
    private static final class Chunk
    {
      Chunk next;
      final byte[] buf;
      final int off;
      final int len;
      
      public Chunk(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        buf = paramArrayOfByte;
        off = paramInt1;
        len = paramInt2;
      }
      
      public Chunk createNext(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        return next = new Chunk(paramArrayOfByte, paramInt1, paramInt2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\ReadAllStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
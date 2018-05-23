package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;

public final class ModelByteBuffer
{
  private ModelByteBuffer root = this;
  private File file;
  private long fileoffset;
  private byte[] buffer;
  private long offset;
  private final long len;
  
  private ModelByteBuffer(ModelByteBuffer paramModelByteBuffer, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    root = root;
    offset = 0L;
    long l = len;
    if (paramLong1 < 0L) {
      paramLong1 = 0L;
    }
    if (paramLong1 > l) {
      paramLong1 = l;
    }
    if (paramLong2 < 0L) {
      paramLong2 = 0L;
    }
    if (paramLong2 > l) {
      paramLong2 = l;
    }
    if (paramLong1 > paramLong2) {
      paramLong1 = paramLong2;
    }
    offset = paramLong1;
    len = (paramLong2 - paramLong1);
    if (paramBoolean)
    {
      buffer = root.buffer;
      if (root.file != null)
      {
        file = root.file;
        fileoffset = (root.fileoffset + arrayOffset());
        offset = 0L;
      }
      else
      {
        offset = arrayOffset();
      }
      root = this;
    }
  }
  
  public ModelByteBuffer(byte[] paramArrayOfByte)
  {
    buffer = paramArrayOfByte;
    offset = 0L;
    len = paramArrayOfByte.length;
  }
  
  public ModelByteBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    buffer = paramArrayOfByte;
    offset = paramInt1;
    len = paramInt2;
  }
  
  public ModelByteBuffer(File paramFile)
  {
    file = paramFile;
    fileoffset = 0L;
    len = paramFile.length();
  }
  
  public ModelByteBuffer(File paramFile, long paramLong1, long paramLong2)
  {
    file = paramFile;
    fileoffset = paramLong1;
    len = paramLong2;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    if ((root.file != null) && (root.buffer == null))
    {
      InputStream localInputStream = getInputStream();
      byte[] arrayOfByte = new byte['Ѐ'];
      int i;
      while ((i = localInputStream.read(arrayOfByte)) != -1) {
        paramOutputStream.write(arrayOfByte, 0, i);
      }
    }
    else
    {
      paramOutputStream.write(array(), (int)arrayOffset(), (int)capacity());
    }
  }
  
  public InputStream getInputStream()
  {
    if ((root.file != null) && (root.buffer == null)) {
      try
      {
        return new RandomFileInputStream();
      }
      catch (IOException localIOException)
      {
        return null;
      }
    }
    return new ByteArrayInputStream(array(), (int)arrayOffset(), (int)capacity());
  }
  
  public ModelByteBuffer subbuffer(long paramLong)
  {
    return subbuffer(paramLong, capacity());
  }
  
  public ModelByteBuffer subbuffer(long paramLong1, long paramLong2)
  {
    return subbuffer(paramLong1, paramLong2, false);
  }
  
  public ModelByteBuffer subbuffer(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    return new ModelByteBuffer(this, paramLong1, paramLong2, paramBoolean);
  }
  
  public byte[] array()
  {
    return root.buffer;
  }
  
  public long arrayOffset()
  {
    if (root != this) {
      return root.arrayOffset() + offset;
    }
    return offset;
  }
  
  public long capacity()
  {
    return len;
  }
  
  public ModelByteBuffer getRoot()
  {
    return root;
  }
  
  public File getFile()
  {
    return file;
  }
  
  public long getFilePointer()
  {
    return fileoffset;
  }
  
  public static void loadAll(Collection<ModelByteBuffer> paramCollection)
    throws IOException
  {
    File localFile = null;
    RandomAccessFile localRandomAccessFile = null;
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        ModelByteBuffer localModelByteBuffer = (ModelByteBuffer)localIterator.next();
        localModelByteBuffer = root;
        if ((file != null) && (buffer == null))
        {
          if ((localFile == null) || (!localFile.equals(file)))
          {
            if (localRandomAccessFile != null)
            {
              localRandomAccessFile.close();
              localRandomAccessFile = null;
            }
            localFile = file;
            localRandomAccessFile = new RandomAccessFile(file, "r");
          }
          localRandomAccessFile.seek(fileoffset);
          byte[] arrayOfByte = new byte[(int)localModelByteBuffer.capacity()];
          int i = 0;
          int j = arrayOfByte.length;
          while (i != j) {
            if (j - i > 65536)
            {
              localRandomAccessFile.readFully(arrayOfByte, i, 65536);
              i += 65536;
            }
            else
            {
              localRandomAccessFile.readFully(arrayOfByte, i, j - i);
              i = j;
            }
          }
          buffer = arrayOfByte;
          offset = 0L;
        }
      }
    }
    finally
    {
      if (localRandomAccessFile != null) {
        localRandomAccessFile.close();
      }
    }
  }
  
  public void load()
    throws IOException
  {
    if (root != this)
    {
      root.load();
      return;
    }
    if (buffer != null) {
      return;
    }
    if (file == null) {
      throw new IllegalStateException("No file associated with this ByteBuffer!");
    }
    DataInputStream localDataInputStream = new DataInputStream(getInputStream());
    buffer = new byte[(int)capacity()];
    offset = 0L;
    localDataInputStream.readFully(buffer);
    localDataInputStream.close();
  }
  
  public void unload()
  {
    if (root != this)
    {
      root.unload();
      return;
    }
    if (file == null) {
      throw new IllegalStateException("No file associated with this ByteBuffer!");
    }
    root.buffer = null;
  }
  
  private class RandomFileInputStream
    extends InputStream
  {
    private final RandomAccessFile raf = new RandomAccessFile(access$000file, "r");
    private long left;
    private long mark = 0L;
    private long markleft = 0L;
    
    RandomFileInputStream()
      throws IOException
    {
      raf.seek(access$000fileoffset + arrayOffset());
      left = capacity();
    }
    
    public int available()
      throws IOException
    {
      if (left > 2147483647L) {
        return Integer.MAX_VALUE;
      }
      return (int)left;
    }
    
    public synchronized void mark(int paramInt)
    {
      try
      {
        mark = raf.getFilePointer();
        markleft = left;
      }
      catch (IOException localIOException) {}
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public synchronized void reset()
      throws IOException
    {
      raf.seek(mark);
      left = markleft;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong < 0L) {
        return 0L;
      }
      if (paramLong > left) {
        paramLong = left;
      }
      long l = raf.getFilePointer();
      raf.seek(l + paramLong);
      left -= paramLong;
      return paramLong;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramInt2 > left) {
        paramInt2 = (int)left;
      }
      if (left == 0L) {
        return -1;
      }
      paramInt2 = raf.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt2 == -1) {
        return -1;
      }
      left -= paramInt2;
      return paramInt2;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      int i = paramArrayOfByte.length;
      if (i > left) {
        i = (int)left;
      }
      if (left == 0L) {
        return -1;
      }
      i = raf.read(paramArrayOfByte, 0, i);
      if (i == -1) {
        return -1;
      }
      left -= i;
      return i;
    }
    
    public int read()
      throws IOException
    {
      if (left == 0L) {
        return -1;
      }
      int i = raf.read();
      if (i == -1) {
        return -1;
      }
      left -= 1L;
      return i;
    }
    
    public void close()
      throws IOException
    {
      raf.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelByteBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
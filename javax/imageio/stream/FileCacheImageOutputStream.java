package javax.imageio.stream;

import com.sun.imageio.stream.StreamCloser;
import com.sun.imageio.stream.StreamCloser.CloseAction;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class FileCacheImageOutputStream
  extends ImageOutputStreamImpl
{
  private OutputStream stream;
  private File cacheFile;
  private RandomAccessFile cache;
  private long maxStreamPos = 0L;
  private final StreamCloser.CloseAction closeAction;
  
  public FileCacheImageOutputStream(OutputStream paramOutputStream, File paramFile)
    throws IOException
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("stream == null!");
    }
    if ((paramFile != null) && (!paramFile.isDirectory())) {
      throw new IllegalArgumentException("Not a directory!");
    }
    stream = paramOutputStream;
    if (paramFile == null) {
      cacheFile = Files.createTempFile("imageio", ".tmp", new FileAttribute[0]).toFile();
    } else {
      cacheFile = Files.createTempFile(paramFile.toPath(), "imageio", ".tmp", new FileAttribute[0]).toFile();
    }
    cache = new RandomAccessFile(cacheFile, "rw");
    closeAction = StreamCloser.createCloseAction(this);
    StreamCloser.addToQueue(closeAction);
  }
  
  public int read()
    throws IOException
  {
    checkClosed();
    bitOffset = 0;
    int i = cache.read();
    if (i != -1) {
      streamPos += 1L;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    checkClosed();
    if (paramArrayOfByte == null) {
      throw new NullPointerException("b == null!");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
    }
    bitOffset = 0;
    if (paramInt2 == 0) {
      return 0;
    }
    int i = cache.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i != -1) {
      streamPos += i;
    }
    return i;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    flushBits();
    cache.write(paramInt);
    streamPos += 1L;
    maxStreamPos = Math.max(maxStreamPos, streamPos);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    flushBits();
    cache.write(paramArrayOfByte, paramInt1, paramInt2);
    streamPos += paramInt2;
    maxStreamPos = Math.max(maxStreamPos, streamPos);
  }
  
  public long length()
  {
    try
    {
      checkClosed();
      return cache.length();
    }
    catch (IOException localIOException) {}
    return -1L;
  }
  
  public void seek(long paramLong)
    throws IOException
  {
    checkClosed();
    if (paramLong < flushedPos) {
      throw new IndexOutOfBoundsException();
    }
    cache.seek(paramLong);
    streamPos = cache.getFilePointer();
    maxStreamPos = Math.max(maxStreamPos, streamPos);
    bitOffset = 0;
  }
  
  public boolean isCached()
  {
    return true;
  }
  
  public boolean isCachedFile()
  {
    return true;
  }
  
  public boolean isCachedMemory()
  {
    return false;
  }
  
  public void close()
    throws IOException
  {
    maxStreamPos = cache.length();
    seek(maxStreamPos);
    flushBefore(maxStreamPos);
    super.close();
    cache.close();
    cache = null;
    cacheFile.delete();
    cacheFile = null;
    stream.flush();
    stream = null;
    StreamCloser.removeFromQueue(closeAction);
  }
  
  public void flushBefore(long paramLong)
    throws IOException
  {
    long l1 = flushedPos;
    super.flushBefore(paramLong);
    long l2 = flushedPos - l1;
    if (l2 > 0L)
    {
      int i = 512;
      byte[] arrayOfByte = new byte[i];
      cache.seek(l1);
      while (l2 > 0L)
      {
        int j = (int)Math.min(l2, i);
        cache.readFully(arrayOfByte, 0, j);
        stream.write(arrayOfByte, 0, j);
        l2 -= j;
      }
      stream.flush();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\FileCacheImageOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
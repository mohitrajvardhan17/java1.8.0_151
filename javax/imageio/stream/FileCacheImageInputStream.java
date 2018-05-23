package javax.imageio.stream;

import com.sun.imageio.stream.StreamCloser;
import com.sun.imageio.stream.StreamCloser.CloseAction;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class FileCacheImageInputStream
  extends ImageInputStreamImpl
{
  private InputStream stream;
  private File cacheFile;
  private RandomAccessFile cache;
  private static final int BUFFER_LENGTH = 1024;
  private byte[] buf = new byte['Ѐ'];
  private long length = 0L;
  private boolean foundEOF = false;
  private final Object disposerReferent;
  private final DisposerRecord disposerRecord;
  private final StreamCloser.CloseAction closeAction;
  
  public FileCacheImageInputStream(InputStream paramInputStream, File paramFile)
    throws IOException
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("stream == null!");
    }
    if ((paramFile != null) && (!paramFile.isDirectory())) {
      throw new IllegalArgumentException("Not a directory!");
    }
    stream = paramInputStream;
    if (paramFile == null) {
      cacheFile = Files.createTempFile("imageio", ".tmp", new FileAttribute[0]).toFile();
    } else {
      cacheFile = Files.createTempFile(paramFile.toPath(), "imageio", ".tmp", new FileAttribute[0]).toFile();
    }
    cache = new RandomAccessFile(cacheFile, "rw");
    closeAction = StreamCloser.createCloseAction(this);
    StreamCloser.addToQueue(closeAction);
    disposerRecord = new StreamDisposerRecord(cacheFile, cache);
    if (getClass() == FileCacheImageInputStream.class)
    {
      disposerReferent = new Object();
      Disposer.addRecord(disposerReferent, disposerRecord);
    }
    else
    {
      disposerReferent = new StreamFinalizer(this);
    }
  }
  
  private long readUntil(long paramLong)
    throws IOException
  {
    if (paramLong < length) {
      return paramLong;
    }
    if (foundEOF) {
      return length;
    }
    long l = paramLong - length;
    cache.seek(length);
    while (l > 0L)
    {
      int i = stream.read(buf, 0, (int)Math.min(l, 1024L));
      if (i == -1)
      {
        foundEOF = true;
        return length;
      }
      cache.write(buf, 0, i);
      l -= i;
      length += i;
    }
    return paramLong;
  }
  
  public int read()
    throws IOException
  {
    checkClosed();
    bitOffset = 0;
    long l1 = streamPos + 1L;
    long l2 = readUntil(l1);
    if (l2 >= l1)
    {
      cache.seek(streamPos++);
      return cache.read();
    }
    return -1;
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
    long l = readUntil(streamPos + paramInt2);
    paramInt2 = (int)Math.min(paramInt2, l - streamPos);
    if (paramInt2 > 0)
    {
      cache.seek(streamPos);
      cache.readFully(paramArrayOfByte, paramInt1, paramInt2);
      streamPos += paramInt2;
      return paramInt2;
    }
    return -1;
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
    super.close();
    disposerRecord.dispose();
    stream = null;
    cache = null;
    cacheFile = null;
    StreamCloser.removeFromQueue(closeAction);
  }
  
  protected void finalize()
    throws Throwable
  {}
  
  private static class StreamDisposerRecord
    implements DisposerRecord
  {
    private File cacheFile;
    private RandomAccessFile cache;
    
    public StreamDisposerRecord(File paramFile, RandomAccessFile paramRandomAccessFile)
    {
      cacheFile = paramFile;
      cache = paramRandomAccessFile;
    }
    
    /* Error */
    public synchronized void dispose()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 37	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cache	Ljava/io/RandomAccessFile;
      //   4: ifnull +35 -> 39
      //   7: aload_0
      //   8: getfield 37	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cache	Ljava/io/RandomAccessFile;
      //   11: invokevirtual 39	java/io/RandomAccessFile:close	()V
      //   14: aload_0
      //   15: aconst_null
      //   16: putfield 37	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cache	Ljava/io/RandomAccessFile;
      //   19: goto +20 -> 39
      //   22: astore_1
      //   23: aload_0
      //   24: aconst_null
      //   25: putfield 37	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cache	Ljava/io/RandomAccessFile;
      //   28: goto +11 -> 39
      //   31: astore_2
      //   32: aload_0
      //   33: aconst_null
      //   34: putfield 37	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cache	Ljava/io/RandomAccessFile;
      //   37: aload_2
      //   38: athrow
      //   39: aload_0
      //   40: getfield 36	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cacheFile	Ljava/io/File;
      //   43: ifnull +16 -> 59
      //   46: aload_0
      //   47: getfield 36	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cacheFile	Ljava/io/File;
      //   50: invokevirtual 38	java/io/File:delete	()Z
      //   53: pop
      //   54: aload_0
      //   55: aconst_null
      //   56: putfield 36	javax/imageio/stream/FileCacheImageInputStream$StreamDisposerRecord:cacheFile	Ljava/io/File;
      //   59: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	60	0	this	StreamDisposerRecord
      //   22	1	1	localIOException	IOException
      //   31	7	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   7	14	22	java/io/IOException
      //   7	14	31	finally
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\FileCacheImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
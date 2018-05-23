package javax.imageio.stream;

import com.sun.imageio.stream.StreamFinalizer;
import java.io.IOException;
import java.io.InputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class MemoryCacheImageInputStream
  extends ImageInputStreamImpl
{
  private InputStream stream;
  private MemoryCache cache = new MemoryCache();
  private final Object disposerReferent;
  private final DisposerRecord disposerRecord;
  
  public MemoryCacheImageInputStream(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("stream == null!");
    }
    stream = paramInputStream;
    disposerRecord = new StreamDisposerRecord(cache);
    if (getClass() == MemoryCacheImageInputStream.class)
    {
      disposerReferent = new Object();
      Disposer.addRecord(disposerReferent, disposerRecord);
    }
    else
    {
      disposerReferent = new StreamFinalizer(this);
    }
  }
  
  public int read()
    throws IOException
  {
    checkClosed();
    bitOffset = 0;
    long l = cache.loadFromStream(stream, streamPos + 1L);
    if (l >= streamPos + 1L) {
      return cache.read(streamPos++);
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
    long l = cache.loadFromStream(stream, streamPos + paramInt2);
    paramInt2 = (int)(l - streamPos);
    if (paramInt2 > 0)
    {
      cache.read(paramArrayOfByte, paramInt1, paramInt2, streamPos);
      streamPos += paramInt2;
      return paramInt2;
    }
    return -1;
  }
  
  public void flushBefore(long paramLong)
    throws IOException
  {
    super.flushBefore(paramLong);
    cache.disposeBefore(paramLong);
  }
  
  public boolean isCached()
  {
    return true;
  }
  
  public boolean isCachedFile()
  {
    return false;
  }
  
  public boolean isCachedMemory()
  {
    return true;
  }
  
  public void close()
    throws IOException
  {
    super.close();
    disposerRecord.dispose();
    stream = null;
    cache = null;
  }
  
  protected void finalize()
    throws Throwable
  {}
  
  private static class StreamDisposerRecord
    implements DisposerRecord
  {
    private MemoryCache cache;
    
    public StreamDisposerRecord(MemoryCache paramMemoryCache)
    {
      cache = paramMemoryCache;
    }
    
    public synchronized void dispose()
    {
      if (cache != null)
      {
        cache.reset();
        cache = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\MemoryCacheImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.imageio.stream;

import java.io.IOException;
import java.io.OutputStream;

public class MemoryCacheImageOutputStream
  extends ImageOutputStreamImpl
{
  private OutputStream stream;
  private MemoryCache cache = new MemoryCache();
  
  public MemoryCacheImageOutputStream(OutputStream paramOutputStream)
  {
    if (paramOutputStream == null) {
      throw new IllegalArgumentException("stream == null!");
    }
    stream = paramOutputStream;
  }
  
  public int read()
    throws IOException
  {
    checkClosed();
    bitOffset = 0;
    int i = cache.read(streamPos);
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
    long l = cache.getLength() - streamPos;
    if (l <= 0L) {
      return -1;
    }
    paramInt2 = (int)Math.min(l, paramInt2);
    cache.read(paramArrayOfByte, paramInt1, paramInt2, streamPos);
    streamPos += paramInt2;
    return paramInt2;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    flushBits();
    cache.write(paramInt, streamPos);
    streamPos += 1L;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    flushBits();
    cache.write(paramArrayOfByte, paramInt1, paramInt2, streamPos);
    streamPos += paramInt2;
  }
  
  public long length()
  {
    try
    {
      checkClosed();
      return cache.getLength();
    }
    catch (IOException localIOException) {}
    return -1L;
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
    long l = cache.getLength();
    seek(l);
    flushBefore(l);
    super.close();
    cache.reset();
    cache = null;
    stream = null;
  }
  
  public void flushBefore(long paramLong)
    throws IOException
  {
    long l1 = flushedPos;
    super.flushBefore(paramLong);
    long l2 = flushedPos - l1;
    cache.writeToStream(stream, l1, l2);
    cache.disposeBefore(flushedPos);
    stream.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\MemoryCacheImageOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
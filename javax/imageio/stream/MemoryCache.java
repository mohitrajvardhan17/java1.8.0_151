package javax.imageio.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

class MemoryCache
{
  private static final int BUFFER_LENGTH = 8192;
  private ArrayList cache = new ArrayList();
  private long cacheStart = 0L;
  private long length = 0L;
  
  MemoryCache() {}
  
  private byte[] getCacheBlock(long paramLong)
    throws IOException
  {
    long l = paramLong - cacheStart;
    if (l > 2147483647L) {
      throw new IOException("Cache addressing limit exceeded!");
    }
    return (byte[])cache.get((int)l);
  }
  
  public long loadFromStream(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong < length) {
      return paramLong;
    }
    int i = (int)(length % 8192L);
    byte[] arrayOfByte = null;
    long l = paramLong - length;
    if (i != 0) {
      arrayOfByte = getCacheBlock(length / 8192L);
    }
    while (l > 0L)
    {
      if (arrayOfByte == null)
      {
        try
        {
          arrayOfByte = new byte[' '];
        }
        catch (OutOfMemoryError localOutOfMemoryError)
        {
          throw new IOException("No memory left for cache!");
        }
        i = 0;
      }
      int j = 8192 - i;
      int k = (int)Math.min(l, j);
      k = paramInputStream.read(arrayOfByte, i, k);
      if (k == -1) {
        return length;
      }
      if (i == 0) {
        cache.add(arrayOfByte);
      }
      l -= k;
      length += k;
      i += k;
      if (i >= 8192) {
        arrayOfByte = null;
      }
    }
    return paramLong;
  }
  
  public void writeToStream(OutputStream paramOutputStream, long paramLong1, long paramLong2)
    throws IOException
  {
    if (paramLong1 + paramLong2 > length) {
      throw new IndexOutOfBoundsException("Argument out of cache");
    }
    if ((paramLong1 < 0L) || (paramLong2 < 0L)) {
      throw new IndexOutOfBoundsException("Negative pos or len");
    }
    if (paramLong2 == 0L) {
      return;
    }
    long l = paramLong1 / 8192L;
    if (l < cacheStart) {
      throw new IndexOutOfBoundsException("pos already disposed");
    }
    int i = (int)(paramLong1 % 8192L);
    byte[] arrayOfByte = getCacheBlock(l++);
    while (paramLong2 > 0L)
    {
      if (arrayOfByte == null)
      {
        arrayOfByte = getCacheBlock(l++);
        i = 0;
      }
      int j = (int)Math.min(paramLong2, 8192 - i);
      paramOutputStream.write(arrayOfByte, i, j);
      arrayOfByte = null;
      paramLong2 -= j;
    }
  }
  
  private void pad(long paramLong)
    throws IOException
  {
    long l1 = cacheStart + cache.size() - 1L;
    long l2 = paramLong / 8192L;
    long l3 = l2 - l1;
    for (long l4 = 0L; l4 < l3; l4 += 1L) {
      try
      {
        cache.add(new byte[' ']);
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        throw new IOException("No memory left for cache!");
      }
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("b == null!");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramLong < 0L) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    long l = paramLong + paramInt2 - 1L;
    if (l >= length)
    {
      pad(l);
      length = (l + 1L);
    }
    for (int i = (int)(paramLong % 8192L); paramInt2 > 0; i = 0)
    {
      byte[] arrayOfByte = getCacheBlock(paramLong / 8192L);
      int j = Math.min(paramInt2, 8192 - i);
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, i, j);
      paramLong += j;
      paramInt1 += j;
      paramInt2 -= j;
    }
  }
  
  public void write(int paramInt, long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new ArrayIndexOutOfBoundsException("pos < 0");
    }
    if (paramLong >= length)
    {
      pad(paramLong);
      length = (paramLong + 1L);
    }
    byte[] arrayOfByte = getCacheBlock(paramLong / 8192L);
    int i = (int)(paramLong % 8192L);
    arrayOfByte[i] = ((byte)paramInt);
  }
  
  public long getLength()
  {
    return length;
  }
  
  public int read(long paramLong)
    throws IOException
  {
    if (paramLong >= length) {
      return -1;
    }
    byte[] arrayOfByte = getCacheBlock(paramLong / 8192L);
    if (arrayOfByte == null) {
      return -1;
    }
    return arrayOfByte[((int)(paramLong % 8192L))] & 0xFF;
  }
  
  public void read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("b == null!");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramLong < 0L) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramLong + paramInt2 > length) {
      throw new IndexOutOfBoundsException();
    }
    long l = paramLong / 8192L;
    for (int i = (int)paramLong % 8192; paramInt2 > 0; i = 0)
    {
      int j = Math.min(paramInt2, 8192 - i);
      byte[] arrayOfByte = getCacheBlock(l++);
      System.arraycopy(arrayOfByte, i, paramArrayOfByte, paramInt1, j);
      paramInt2 -= j;
      paramInt1 += j;
    }
  }
  
  public void disposeBefore(long paramLong)
  {
    long l1 = paramLong / 8192L;
    if (l1 < cacheStart) {
      throw new IndexOutOfBoundsException("pos already disposed");
    }
    long l2 = Math.min(l1 - cacheStart, cache.size());
    for (long l3 = 0L; l3 < l2; l3 += 1L) {
      cache.remove(0);
    }
    cacheStart = l1;
  }
  
  public void reset()
  {
    cache.clear();
    cacheStart = 0L;
    length = 0L;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
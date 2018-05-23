package java.io;

public abstract class InputStream
  implements Closeable
{
  private static final int MAX_SKIP_BUFFER_SIZE = 2048;
  
  public InputStream() {}
  
  public abstract int read()
    throws IOException;
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = read();
    if (i == -1) {
      return -1;
    }
    paramArrayOfByte[paramInt1] = ((byte)i);
    int j = 1;
    try
    {
      while (j < paramInt2)
      {
        i = read();
        if (i == -1) {
          break;
        }
        paramArrayOfByte[(paramInt1 + j)] = ((byte)i);
        j++;
      }
    }
    catch (IOException localIOException) {}
    return j;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    long l = paramLong;
    if (paramLong <= 0L) {
      return 0L;
    }
    int j = (int)Math.min(2048L, l);
    byte[] arrayOfByte = new byte[j];
    while (l > 0L)
    {
      int i = read(arrayOfByte, 0, (int)Math.min(j, l));
      if (i < 0) {
        break;
      }
      l -= i;
    }
    return paramLong - l;
  }
  
  public int available()
    throws IOException
  {
    return 0;
  }
  
  public void close()
    throws IOException
  {}
  
  public synchronized void mark(int paramInt) {}
  
  public synchronized void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
  
  public boolean markSupported()
  {
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\InputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
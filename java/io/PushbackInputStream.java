package java.io;

public class PushbackInputStream
  extends FilterInputStream
{
  protected byte[] buf;
  protected int pos;
  
  private void ensureOpen()
    throws IOException
  {
    if (in == null) {
      throw new IOException("Stream closed");
    }
  }
  
  public PushbackInputStream(InputStream paramInputStream, int paramInt)
  {
    super(paramInputStream);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("size <= 0");
    }
    buf = new byte[paramInt];
    pos = paramInt;
  }
  
  public PushbackInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, 1);
  }
  
  public int read()
    throws IOException
  {
    ensureOpen();
    if (pos < buf.length) {
      return buf[(pos++)] & 0xFF;
    }
    return super.read();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = buf.length - pos;
    if (i > 0)
    {
      if (paramInt2 < i) {
        i = paramInt2;
      }
      System.arraycopy(buf, pos, paramArrayOfByte, paramInt1, i);
      pos += i;
      paramInt1 += i;
      paramInt2 -= i;
    }
    if (paramInt2 > 0)
    {
      paramInt2 = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt2 == -1) {
        return i == 0 ? -1 : i;
      }
      return i + paramInt2;
    }
    return i;
  }
  
  public void unread(int paramInt)
    throws IOException
  {
    ensureOpen();
    if (pos == 0) {
      throw new IOException("Push back buffer is full");
    }
    buf[(--pos)] = ((byte)paramInt);
  }
  
  public void unread(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if (paramInt2 > pos) {
      throw new IOException("Push back buffer is full");
    }
    pos -= paramInt2;
    System.arraycopy(paramArrayOfByte, paramInt1, buf, pos, paramInt2);
  }
  
  public void unread(byte[] paramArrayOfByte)
    throws IOException
  {
    unread(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int available()
    throws IOException
  {
    ensureOpen();
    int i = buf.length - pos;
    int j = super.available();
    return i > Integer.MAX_VALUE - j ? Integer.MAX_VALUE : i + j;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    ensureOpen();
    if (paramLong <= 0L) {
      return 0L;
    }
    long l = buf.length - pos;
    if (l > 0L)
    {
      if (paramLong < l) {
        l = paramLong;
      }
      pos = ((int)(pos + l));
      paramLong -= l;
    }
    if (paramLong > 0L) {
      l += super.skip(paramLong);
    }
    return l;
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public synchronized void mark(int paramInt) {}
  
  public synchronized void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
  
  public synchronized void close()
    throws IOException
  {
    if (in == null) {
      return;
    }
    in.close();
    in = null;
    buf = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PushbackInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
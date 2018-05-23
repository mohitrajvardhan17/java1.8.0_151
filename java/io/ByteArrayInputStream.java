package java.io;

public class ByteArrayInputStream
  extends InputStream
{
  protected byte[] buf;
  protected int pos;
  protected int mark = 0;
  protected int count;
  
  public ByteArrayInputStream(byte[] paramArrayOfByte)
  {
    buf = paramArrayOfByte;
    pos = 0;
    count = paramArrayOfByte.length;
  }
  
  public ByteArrayInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    buf = paramArrayOfByte;
    pos = paramInt1;
    count = Math.min(paramInt1 + paramInt2, paramArrayOfByte.length);
    mark = paramInt1;
  }
  
  public synchronized int read()
  {
    return pos < count ? buf[(pos++)] & 0xFF : -1;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (pos >= count) {
      return -1;
    }
    int i = count - pos;
    if (paramInt2 > i) {
      paramInt2 = i;
    }
    if (paramInt2 <= 0) {
      return 0;
    }
    System.arraycopy(buf, pos, paramArrayOfByte, paramInt1, paramInt2);
    pos += paramInt2;
    return paramInt2;
  }
  
  public synchronized long skip(long paramLong)
  {
    long l = count - pos;
    if (paramLong < l) {
      l = paramLong < 0L ? 0L : paramLong;
    }
    pos = ((int)(pos + l));
    return l;
  }
  
  public synchronized int available()
  {
    return count - pos;
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public void mark(int paramInt)
  {
    mark = pos;
  }
  
  public synchronized void reset()
  {
    pos = mark;
  }
  
  public void close()
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ByteArrayInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
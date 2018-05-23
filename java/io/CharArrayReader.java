package java.io;

public class CharArrayReader
  extends Reader
{
  protected char[] buf;
  protected int pos;
  protected int markedPos = 0;
  protected int count;
  
  public CharArrayReader(char[] paramArrayOfChar)
  {
    buf = paramArrayOfChar;
    pos = 0;
    count = paramArrayOfChar.length;
  }
  
  public CharArrayReader(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 < 0)) {
      throw new IllegalArgumentException();
    }
    buf = paramArrayOfChar;
    pos = paramInt1;
    count = Math.min(paramInt1 + paramInt2, paramArrayOfChar.length);
    markedPos = paramInt1;
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (buf == null) {
      throw new IOException("Stream closed");
    }
  }
  
  public int read()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (pos >= count) {
        return -1;
      }
      return buf[(pos++)];
    }
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return 0;
      }
      if (pos >= count) {
        return -1;
      }
      if (pos + paramInt2 > count) {
        paramInt2 = count - pos;
      }
      if (paramInt2 <= 0) {
        return 0;
      }
      System.arraycopy(buf, pos, paramArrayOfChar, paramInt1, paramInt2);
      pos += paramInt2;
      return paramInt2;
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (pos + paramLong > count) {
        paramLong = count - pos;
      }
      if (paramLong < 0L) {
        return 0L;
      }
      pos = ((int)(pos + paramLong));
      return paramLong;
    }
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      return count - pos > 0;
    }
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      markedPos = pos;
    }
  }
  
  public void reset()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      pos = markedPos;
    }
  }
  
  public void close()
  {
    buf = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\CharArrayReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
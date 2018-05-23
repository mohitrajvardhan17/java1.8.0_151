package java.io;

public class StringReader
  extends Reader
{
  private String str;
  private int length;
  private int next = 0;
  private int mark = 0;
  
  public StringReader(String paramString)
  {
    str = paramString;
    length = paramString.length();
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (str == null) {
      throw new IOException("Stream closed");
    }
  }
  
  public int read()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (next >= length) {
        return -1;
      }
      return str.charAt(next++);
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
      if (next >= length) {
        return -1;
      }
      int i = Math.min(length - next, paramInt2);
      str.getChars(next, next + i, paramArrayOfChar, paramInt1);
      next += i;
      return i;
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (next >= length) {
        return 0L;
      }
      long l = Math.min(length - next, paramLong);
      l = Math.max(-next, l);
      next = ((int)(next + l));
      return l;
    }
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      return true;
    }
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Read-ahead limit < 0");
    }
    synchronized (lock)
    {
      ensureOpen();
      mark = next;
    }
  }
  
  public void reset()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      next = mark;
    }
  }
  
  public void close()
  {
    str = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\StringReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.io;

public class PushbackReader
  extends FilterReader
{
  private char[] buf;
  private int pos;
  
  public PushbackReader(Reader paramReader, int paramInt)
  {
    super(paramReader);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("size <= 0");
    }
    buf = new char[paramInt];
    pos = paramInt;
  }
  
  public PushbackReader(Reader paramReader)
  {
    this(paramReader, 1);
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
      if (pos < buf.length) {
        return buf[(pos++)];
      }
      return super.read();
    }
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      try
      {
        if (paramInt2 <= 0)
        {
          if (paramInt2 < 0) {
            throw new IndexOutOfBoundsException();
          }
          if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length)) {
            throw new IndexOutOfBoundsException();
          }
          return 0;
        }
        int i = buf.length - pos;
        if (i > 0)
        {
          if (paramInt2 < i) {
            i = paramInt2;
          }
          System.arraycopy(buf, pos, paramArrayOfChar, paramInt1, i);
          pos += i;
          paramInt1 += i;
          paramInt2 -= i;
        }
        if (paramInt2 > 0)
        {
          paramInt2 = super.read(paramArrayOfChar, paramInt1, paramInt2);
          if (paramInt2 == -1) {
            return i == 0 ? -1 : i;
          }
          return i + paramInt2;
        }
        return i;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        throw new IndexOutOfBoundsException();
      }
    }
  }
  
  public void unread(int paramInt)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (pos == 0) {
        throw new IOException("Pushback buffer overflow");
      }
      buf[(--pos)] = ((char)paramInt);
    }
  }
  
  public void unread(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (paramInt2 > pos) {
        throw new IOException("Pushback buffer overflow");
      }
      pos -= paramInt2;
      System.arraycopy(paramArrayOfChar, paramInt1, buf, pos, paramInt2);
    }
  }
  
  public void unread(char[] paramArrayOfChar)
    throws IOException
  {
    unread(paramArrayOfChar, 0, paramArrayOfChar.length);
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      return (pos < buf.length) || (super.ready());
    }
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
  
  public void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void close()
    throws IOException
  {
    super.close();
    buf = null;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("skip value is negative");
    }
    synchronized (lock)
    {
      ensureOpen();
      int i = buf.length - pos;
      if (i > 0)
      {
        if (paramLong <= i)
        {
          pos = ((int)(pos + paramLong));
          return paramLong;
        }
        pos = buf.length;
        paramLong -= i;
      }
      return i + super.skip(paramLong);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PushbackReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
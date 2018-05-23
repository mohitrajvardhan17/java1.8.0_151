package java.io;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class BufferedWriter
  extends Writer
{
  private Writer out;
  private char[] cb;
  private int nChars;
  private int nextChar;
  private static int defaultCharBufferSize = 8192;
  private String lineSeparator;
  
  public BufferedWriter(Writer paramWriter)
  {
    this(paramWriter, defaultCharBufferSize);
  }
  
  public BufferedWriter(Writer paramWriter, int paramInt)
  {
    super(paramWriter);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    out = paramWriter;
    cb = new char[paramInt];
    nChars = paramInt;
    nextChar = 0;
    lineSeparator = ((String)AccessController.doPrivileged(new GetPropertyAction("line.separator")));
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (out == null) {
      throw new IOException("Stream closed");
    }
  }
  
  void flushBuffer()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (nextChar == 0) {
        return;
      }
      out.write(cb, 0, nextChar);
      nextChar = 0;
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (nextChar >= nChars) {
        flushBuffer();
      }
      cb[(nextChar++)] = ((char)paramInt);
    }
  }
  
  private int min(int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2) {
      return paramInt1;
    }
    return paramInt2;
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return;
      }
      if (paramInt2 >= nChars)
      {
        flushBuffer();
        out.write(paramArrayOfChar, paramInt1, paramInt2);
        return;
      }
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      while (i < j)
      {
        int k = min(nChars - nextChar, j - i);
        System.arraycopy(paramArrayOfChar, i, cb, nextChar, k);
        i += k;
        nextChar += k;
        if (nextChar >= nChars) {
          flushBuffer();
        }
      }
    }
  }
  
  public void write(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      while (i < j)
      {
        int k = min(nChars - nextChar, j - i);
        paramString.getChars(i, i + k, cb, nextChar);
        i += k;
        nextChar += k;
        if (nextChar >= nChars) {
          flushBuffer();
        }
      }
    }
  }
  
  public void newLine()
    throws IOException
  {
    write(lineSeparator);
  }
  
  public void flush()
    throws IOException
  {
    synchronized (lock)
    {
      flushBuffer();
      out.flush();
    }
  }
  
  public void close()
    throws IOException
  {
    synchronized (lock)
    {
      if (out == null) {
        return;
      }
      try
      {
        Writer localWriter = out;
        Object localObject1 = null;
        try
        {
          flushBuffer();
        }
        catch (Throwable localThrowable2)
        {
          localObject1 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localWriter != null) {
            if (localObject1 != null) {
              try
              {
                localWriter.close();
              }
              catch (Throwable localThrowable3)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable3);
              }
            } else {
              localWriter.close();
            }
          }
        }
      }
      finally
      {
        out = null;
        cb = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\BufferedWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
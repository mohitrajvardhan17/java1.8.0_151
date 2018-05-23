package java.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BufferedReader
  extends Reader
{
  private Reader in;
  private char[] cb;
  private int nChars;
  private int nextChar;
  private static final int INVALIDATED = -2;
  private static final int UNMARKED = -1;
  private int markedChar = -1;
  private int readAheadLimit = 0;
  private boolean skipLF = false;
  private boolean markedSkipLF = false;
  private static int defaultCharBufferSize = 8192;
  private static int defaultExpectedLineLength = 80;
  
  public BufferedReader(Reader paramReader, int paramInt)
  {
    super(paramReader);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    in = paramReader;
    cb = new char[paramInt];
    nextChar = (nChars = 0);
  }
  
  public BufferedReader(Reader paramReader)
  {
    this(paramReader, defaultCharBufferSize);
  }
  
  private void ensureOpen()
    throws IOException
  {
    if (in == null) {
      throw new IOException("Stream closed");
    }
  }
  
  private void fill()
    throws IOException
  {
    int i;
    int j;
    if (markedChar <= -1)
    {
      i = 0;
    }
    else
    {
      j = nextChar - markedChar;
      if (j >= readAheadLimit)
      {
        markedChar = -2;
        readAheadLimit = 0;
        i = 0;
      }
      else
      {
        if (readAheadLimit <= cb.length)
        {
          System.arraycopy(cb, markedChar, cb, 0, j);
          markedChar = 0;
          i = j;
        }
        else
        {
          char[] arrayOfChar = new char[readAheadLimit];
          System.arraycopy(cb, markedChar, arrayOfChar, 0, j);
          cb = arrayOfChar;
          markedChar = 0;
          i = j;
        }
        nextChar = (nChars = j);
      }
    }
    do
    {
      j = in.read(cb, i, cb.length - i);
    } while (j == 0);
    if (j > 0)
    {
      nChars = (i + j);
      nextChar = i;
    }
  }
  
  public int read()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      for (;;)
      {
        if (nextChar >= nChars)
        {
          fill();
          if (nextChar >= nChars) {
            return -1;
          }
        }
        if (!skipLF) {
          break;
        }
        skipLF = false;
        if (cb[nextChar] != '\n') {
          break;
        }
        nextChar += 1;
      }
      return cb[(nextChar++)];
    }
  }
  
  private int read1(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (nextChar >= nChars)
    {
      if ((paramInt2 >= cb.length) && (markedChar <= -1) && (!skipLF)) {
        return in.read(paramArrayOfChar, paramInt1, paramInt2);
      }
      fill();
    }
    if (nextChar >= nChars) {
      return -1;
    }
    if (skipLF)
    {
      skipLF = false;
      if (cb[nextChar] == '\n')
      {
        nextChar += 1;
        if (nextChar >= nChars) {
          fill();
        }
        if (nextChar >= nChars) {
          return -1;
        }
      }
    }
    int i = Math.min(paramInt2, nChars - nextChar);
    System.arraycopy(cb, nextChar, paramArrayOfChar, paramInt1, i);
    nextChar += i;
    return i;
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
      int i = read1(paramArrayOfChar, paramInt1, paramInt2);
      if (i <= 0) {
        return i;
      }
      while ((i < paramInt2) && (in.ready()))
      {
        int j = read1(paramArrayOfChar, paramInt1 + i, paramInt2 - i);
        if (j <= 0) {
          break;
        }
        i += j;
      }
      return i;
    }
  }
  
  String readLine(boolean paramBoolean)
    throws IOException
  {
    StringBuffer localStringBuffer = null;
    synchronized (lock)
    {
      ensureOpen();
      int j = (paramBoolean) || (skipLF) ? 1 : 0;
      if (nextChar >= nChars) {
        fill();
      }
      if (nextChar >= nChars)
      {
        if ((localStringBuffer != null) && (localStringBuffer.length() > 0)) {
          return localStringBuffer.toString();
        }
        return null;
      }
      int k = 0;
      int m = 0;
      if ((j != 0) && (cb[nextChar] == '\n')) {
        nextChar += 1;
      }
      skipLF = false;
      j = 0;
      for (int n = nextChar; n < nChars; n++)
      {
        m = cb[n];
        if ((m == 10) || (m == 13))
        {
          k = 1;
          break;
        }
      }
      int i = nextChar;
      nextChar = n;
      if (k != 0)
      {
        String str;
        if (localStringBuffer == null)
        {
          str = new String(cb, i, n - i);
        }
        else
        {
          localStringBuffer.append(cb, i, n - i);
          str = localStringBuffer.toString();
        }
        nextChar += 1;
        if (m == 13) {
          skipLF = true;
        }
        return str;
      }
      if (localStringBuffer == null) {
        localStringBuffer = new StringBuffer(defaultExpectedLineLength);
      }
      localStringBuffer.append(cb, i, n - i);
    }
  }
  
  public String readLine()
    throws IOException
  {
    return readLine(false);
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
      long l1 = paramLong;
      while (l1 > 0L)
      {
        if (nextChar >= nChars) {
          fill();
        }
        if (nextChar >= nChars) {
          break;
        }
        if (skipLF)
        {
          skipLF = false;
          if (cb[nextChar] == '\n') {
            nextChar += 1;
          }
        }
        long l2 = nChars - nextChar;
        if (l1 <= l2)
        {
          nextChar = ((int)(nextChar + l1));
          l1 = 0L;
          break;
        }
        l1 -= l2;
        nextChar = nChars;
      }
      return paramLong - l1;
    }
  }
  
  public boolean ready()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (skipLF)
      {
        if ((nextChar >= nChars) && (in.ready())) {
          fill();
        }
        if (nextChar < nChars)
        {
          if (cb[nextChar] == '\n') {
            nextChar += 1;
          }
          skipLF = false;
        }
      }
      return (nextChar < nChars) || (in.ready());
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
      readAheadLimit = paramInt;
      markedChar = nextChar;
      markedSkipLF = skipLF;
    }
  }
  
  public void reset()
    throws IOException
  {
    synchronized (lock)
    {
      ensureOpen();
      if (markedChar < 0) {
        throw new IOException(markedChar == -2 ? "Mark invalid" : "Stream not marked");
      }
      nextChar = markedChar;
      skipLF = markedSkipLF;
    }
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 152	java/io/BufferedReader:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 151	java/io/BufferedReader:in	Ljava/io/Reader;
    //   11: ifnonnull +6 -> 17
    //   14: aload_1
    //   15: monitorexit
    //   16: return
    //   17: aload_0
    //   18: getfield 151	java/io/BufferedReader:in	Ljava/io/Reader;
    //   21: invokevirtual 160	java/io/Reader:close	()V
    //   24: aload_0
    //   25: aconst_null
    //   26: putfield 151	java/io/BufferedReader:in	Ljava/io/Reader;
    //   29: aload_0
    //   30: aconst_null
    //   31: putfield 150	java/io/BufferedReader:cb	[C
    //   34: goto +16 -> 50
    //   37: astore_2
    //   38: aload_0
    //   39: aconst_null
    //   40: putfield 151	java/io/BufferedReader:in	Ljava/io/Reader;
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield 150	java/io/BufferedReader:cb	[C
    //   48: aload_2
    //   49: athrow
    //   50: aload_1
    //   51: monitorexit
    //   52: goto +8 -> 60
    //   55: astore_3
    //   56: aload_1
    //   57: monitorexit
    //   58: aload_3
    //   59: athrow
    //   60: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	BufferedReader
    //   5	52	1	Ljava/lang/Object;	Object
    //   37	12	2	localObject1	Object
    //   55	4	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   17	24	37	finally
    //   7	16	55	finally
    //   17	52	55	finally
    //   55	58	55	finally
  }
  
  public Stream<String> lines()
  {
    Iterator local1 = new Iterator()
    {
      String nextLine = null;
      
      public boolean hasNext()
      {
        if (nextLine != null) {
          return true;
        }
        try
        {
          nextLine = readLine();
          return nextLine != null;
        }
        catch (IOException localIOException)
        {
          throw new UncheckedIOException(localIOException);
        }
      }
      
      public String next()
      {
        if ((nextLine != null) || (hasNext()))
        {
          String str = nextLine;
          nextLine = null;
          return str;
        }
        throw new NoSuchElementException();
      }
    };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(local1, 272), false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\BufferedReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
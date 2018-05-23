package java.io;

public class LineNumberReader
  extends BufferedReader
{
  private int lineNumber = 0;
  private int markedLineNumber;
  private boolean skipLF;
  private boolean markedSkipLF;
  private static final int maxSkipBufferSize = 8192;
  private char[] skipBuffer = null;
  
  public LineNumberReader(Reader paramReader)
  {
    super(paramReader);
  }
  
  public LineNumberReader(Reader paramReader, int paramInt)
  {
    super(paramReader, paramInt);
  }
  
  public void setLineNumber(int paramInt)
  {
    lineNumber = paramInt;
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public int read()
    throws IOException
  {
    synchronized (lock)
    {
      int i = super.read();
      if (skipLF)
      {
        if (i == 10) {
          i = super.read();
        }
        skipLF = false;
      }
      switch (i)
      {
      case 13: 
        skipLF = true;
      case 10: 
        lineNumber += 1;
        return 10;
      }
      return i;
    }
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (lock)
    {
      int i = super.read(paramArrayOfChar, paramInt1, paramInt2);
      for (int j = paramInt1; j < paramInt1 + i; j++)
      {
        int k = paramArrayOfChar[j];
        if (skipLF)
        {
          skipLF = false;
          if (k == 10) {}
        }
        else
        {
          switch (k)
          {
          case 13: 
            skipLF = true;
          case 10: 
            lineNumber += 1;
          }
        }
      }
      return i;
    }
  }
  
  public String readLine()
    throws IOException
  {
    synchronized (lock)
    {
      String str = super.readLine(skipLF);
      skipLF = false;
      if (str != null) {
        lineNumber += 1;
      }
      return str;
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("skip() value is negative");
    }
    int i = (int)Math.min(paramLong, 8192L);
    synchronized (lock)
    {
      if ((skipBuffer == null) || (skipBuffer.length < i)) {
        skipBuffer = new char[i];
      }
      int j;
      for (long l = paramLong; l > 0L; l -= j)
      {
        j = read(skipBuffer, 0, (int)Math.min(l, i));
        if (j == -1) {
          break;
        }
      }
      return paramLong - l;
    }
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    synchronized (lock)
    {
      super.mark(paramInt);
      markedLineNumber = lineNumber;
      markedSkipLF = skipLF;
    }
  }
  
  public void reset()
    throws IOException
  {
    synchronized (lock)
    {
      super.reset();
      lineNumber = markedLineNumber;
      skipLF = markedSkipLF;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\LineNumberReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
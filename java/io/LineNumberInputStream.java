package java.io;

@Deprecated
public class LineNumberInputStream
  extends FilterInputStream
{
  int pushBack = -1;
  int lineNumber;
  int markLineNumber;
  int markPushBack = -1;
  
  public LineNumberInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public int read()
    throws IOException
  {
    int i = pushBack;
    if (i != -1) {
      pushBack = -1;
    } else {
      i = in.read();
    }
    switch (i)
    {
    case 13: 
      pushBack = in.read();
      if (pushBack == 10) {
        pushBack = -1;
      }
    case 10: 
      lineNumber += 1;
      return 10;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
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
        if (paramArrayOfByte != null) {
          paramArrayOfByte[(paramInt1 + j)] = ((byte)i);
        }
        j++;
      }
    }
    catch (IOException localIOException) {}
    return j;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    int i = 2048;
    long l = paramLong;
    if (paramLong <= 0L) {
      return 0L;
    }
    byte[] arrayOfByte = new byte[i];
    while (l > 0L)
    {
      int j = read(arrayOfByte, 0, (int)Math.min(i, l));
      if (j < 0) {
        break;
      }
      l -= j;
    }
    return paramLong - l;
  }
  
  public void setLineNumber(int paramInt)
  {
    lineNumber = paramInt;
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public int available()
    throws IOException
  {
    return pushBack == -1 ? super.available() / 2 : super.available() / 2 + 1;
  }
  
  public void mark(int paramInt)
  {
    markLineNumber = lineNumber;
    markPushBack = pushBack;
    in.mark(paramInt);
  }
  
  public void reset()
    throws IOException
  {
    lineNumber = markLineNumber;
    pushBack = markPushBack;
    in.reset();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\LineNumberInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.io;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class BufferedInputStream
  extends FilterInputStream
{
  private static int DEFAULT_BUFFER_SIZE = 8192;
  private static int MAX_BUFFER_SIZE = 2147483639;
  protected volatile byte[] buf;
  private static final AtomicReferenceFieldUpdater<BufferedInputStream, byte[]> bufUpdater = AtomicReferenceFieldUpdater.newUpdater(BufferedInputStream.class, byte[].class, "buf");
  protected int count;
  protected int pos;
  protected int markpos = -1;
  protected int marklimit;
  
  private InputStream getInIfOpen()
    throws IOException
  {
    InputStream localInputStream = in;
    if (localInputStream == null) {
      throw new IOException("Stream closed");
    }
    return localInputStream;
  }
  
  private byte[] getBufIfOpen()
    throws IOException
  {
    byte[] arrayOfByte = buf;
    if (arrayOfByte == null) {
      throw new IOException("Stream closed");
    }
    return arrayOfByte;
  }
  
  public BufferedInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, DEFAULT_BUFFER_SIZE);
  }
  
  public BufferedInputStream(InputStream paramInputStream, int paramInt)
  {
    super(paramInputStream);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    buf = new byte[paramInt];
  }
  
  private void fill()
    throws IOException
  {
    Object localObject = getBufIfOpen();
    if (markpos < 0) {
      pos = 0;
    } else if (pos >= localObject.length) {
      if (markpos > 0)
      {
        i = pos - markpos;
        System.arraycopy(localObject, markpos, localObject, 0, i);
        pos = i;
        markpos = 0;
      }
      else if (localObject.length >= marklimit)
      {
        markpos = -1;
        pos = 0;
      }
      else
      {
        if (localObject.length >= MAX_BUFFER_SIZE) {
          throw new OutOfMemoryError("Required array size too large");
        }
        i = pos <= MAX_BUFFER_SIZE - pos ? pos * 2 : MAX_BUFFER_SIZE;
        if (i > marklimit) {
          i = marklimit;
        }
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(localObject, 0, arrayOfByte, 0, pos);
        if (!bufUpdater.compareAndSet(this, localObject, arrayOfByte)) {
          throw new IOException("Stream closed");
        }
        localObject = arrayOfByte;
      }
    }
    count = pos;
    int i = getInIfOpen().read((byte[])localObject, pos, localObject.length - pos);
    if (i > 0) {
      count = (i + pos);
    }
  }
  
  public synchronized int read()
    throws IOException
  {
    if (pos >= count)
    {
      fill();
      if (pos >= count) {
        return -1;
      }
    }
    return getBufIfOpen()[(pos++)] & 0xFF;
  }
  
  private int read1(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = count - pos;
    if (i <= 0)
    {
      if ((paramInt2 >= getBufIfOpen().length) && (markpos < 0)) {
        return getInIfOpen().read(paramArrayOfByte, paramInt1, paramInt2);
      }
      fill();
      i = count - pos;
      if (i <= 0) {
        return -1;
      }
    }
    int j = i < paramInt2 ? i : paramInt2;
    System.arraycopy(getBufIfOpen(), pos, paramArrayOfByte, paramInt1, j);
    pos += j;
    return j;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    getBufIfOpen();
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfByte.length - (paramInt1 + paramInt2)) < 0) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = 0;
    for (;;)
    {
      int j = read1(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j <= 0) {
        return i == 0 ? j : i;
      }
      i += j;
      if (i >= paramInt2) {
        return i;
      }
      InputStream localInputStream = in;
      if ((localInputStream != null) && (localInputStream.available() <= 0)) {
        return i;
      }
    }
  }
  
  public synchronized long skip(long paramLong)
    throws IOException
  {
    getBufIfOpen();
    if (paramLong <= 0L) {
      return 0L;
    }
    long l1 = count - pos;
    if (l1 <= 0L)
    {
      if (markpos < 0) {
        return getInIfOpen().skip(paramLong);
      }
      fill();
      l1 = count - pos;
      if (l1 <= 0L) {
        return 0L;
      }
    }
    long l2 = l1 < paramLong ? l1 : paramLong;
    pos = ((int)(pos + l2));
    return l2;
  }
  
  public synchronized int available()
    throws IOException
  {
    int i = count - pos;
    int j = getInIfOpen().available();
    return i > Integer.MAX_VALUE - j ? Integer.MAX_VALUE : i + j;
  }
  
  public synchronized void mark(int paramInt)
  {
    marklimit = paramInt;
    markpos = pos;
  }
  
  public synchronized void reset()
    throws IOException
  {
    getBufIfOpen();
    if (markpos < 0) {
      throw new IOException("Resetting to invalid mark");
    }
    pos = markpos;
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public void close()
    throws IOException
  {
    byte[] arrayOfByte;
    while ((arrayOfByte = buf) != null) {
      if (bufUpdater.compareAndSet(this, arrayOfByte, null))
      {
        InputStream localInputStream = in;
        in = null;
        if (localInputStream != null) {
          localInputStream.close();
        }
        return;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\BufferedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
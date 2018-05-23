package java.util.zip;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InflaterInputStream
  extends FilterInputStream
{
  protected Inflater inf;
  protected byte[] buf;
  protected int len;
  private boolean closed = false;
  private boolean reachEOF = false;
  boolean usesDefaultInflater = false;
  private byte[] singleByteBuf = new byte[1];
  private byte[] b = new byte['Ȁ'];
  
  private void ensureOpen()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream closed");
    }
  }
  
  public InflaterInputStream(InputStream paramInputStream, Inflater paramInflater, int paramInt)
  {
    super(paramInputStream);
    if ((paramInputStream == null) || (paramInflater == null)) {
      throw new NullPointerException();
    }
    if (paramInt <= 0) {
      throw new IllegalArgumentException("buffer size <= 0");
    }
    inf = paramInflater;
    buf = new byte[paramInt];
  }
  
  public InflaterInputStream(InputStream paramInputStream, Inflater paramInflater)
  {
    this(paramInputStream, paramInflater, 512);
  }
  
  public InflaterInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, new Inflater());
    usesDefaultInflater = true;
  }
  
  public int read()
    throws IOException
  {
    ensureOpen();
    return read(singleByteBuf, 0, 1) == -1 ? -1 : Byte.toUnsignedInt(singleByteBuf[0]);
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
    try
    {
      int i;
      while ((i = inf.inflate(paramArrayOfByte, paramInt1, paramInt2)) == 0)
      {
        if ((inf.finished()) || (inf.needsDictionary()))
        {
          reachEOF = true;
          return -1;
        }
        if (inf.needsInput()) {
          fill();
        }
      }
      return i;
    }
    catch (DataFormatException localDataFormatException)
    {
      String str = localDataFormatException.getMessage();
      throw new ZipException(str != null ? str : "Invalid ZLIB data format");
    }
  }
  
  public int available()
    throws IOException
  {
    ensureOpen();
    if (reachEOF) {
      return 0;
    }
    return 1;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("negative skip length");
    }
    ensureOpen();
    int i = (int)Math.min(paramLong, 2147483647L);
    int j = 0;
    while (j < i)
    {
      int k = i - j;
      if (k > b.length) {
        k = b.length;
      }
      k = read(b, 0, k);
      if (k == -1)
      {
        reachEOF = true;
        break;
      }
      j += k;
    }
    return j;
  }
  
  public void close()
    throws IOException
  {
    if (!closed)
    {
      if (usesDefaultInflater) {
        inf.end();
      }
      in.close();
      closed = true;
    }
  }
  
  protected void fill()
    throws IOException
  {
    ensureOpen();
    len = in.read(buf, 0, buf.length);
    if (len == -1) {
      throw new EOFException("Unexpected end of ZLIB input stream");
    }
    inf.setInput(buf, 0, len);
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\InflaterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
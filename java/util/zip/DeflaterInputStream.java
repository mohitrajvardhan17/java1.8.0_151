package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DeflaterInputStream
  extends FilterInputStream
{
  protected final Deflater def;
  protected final byte[] buf;
  private byte[] rbuf = new byte[1];
  private boolean usesDefaultDeflater = false;
  private boolean reachEOF = false;
  
  private void ensureOpen()
    throws IOException
  {
    if (in == null) {
      throw new IOException("Stream closed");
    }
  }
  
  public DeflaterInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, new Deflater());
    usesDefaultDeflater = true;
  }
  
  public DeflaterInputStream(InputStream paramInputStream, Deflater paramDeflater)
  {
    this(paramInputStream, paramDeflater, 512);
  }
  
  public DeflaterInputStream(InputStream paramInputStream, Deflater paramDeflater, int paramInt)
  {
    super(paramInputStream);
    if (paramInputStream == null) {
      throw new NullPointerException("Null input");
    }
    if (paramDeflater == null) {
      throw new NullPointerException("Null deflater");
    }
    if (paramInt < 1) {
      throw new IllegalArgumentException("Buffer size < 1");
    }
    def = paramDeflater;
    buf = new byte[paramInt];
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 104	java/util/zip/DeflaterInputStream:in	Ljava/io/InputStream;
    //   4: ifnull +40 -> 44
    //   7: aload_0
    //   8: getfield 101	java/util/zip/DeflaterInputStream:usesDefaultDeflater	Z
    //   11: ifeq +10 -> 21
    //   14: aload_0
    //   15: getfield 105	java/util/zip/DeflaterInputStream:def	Ljava/util/zip/Deflater;
    //   18: invokevirtual 115	java/util/zip/Deflater:end	()V
    //   21: aload_0
    //   22: getfield 104	java/util/zip/DeflaterInputStream:in	Ljava/io/InputStream;
    //   25: invokevirtual 108	java/io/InputStream:close	()V
    //   28: aload_0
    //   29: aconst_null
    //   30: putfield 104	java/util/zip/DeflaterInputStream:in	Ljava/io/InputStream;
    //   33: goto +11 -> 44
    //   36: astore_1
    //   37: aload_0
    //   38: aconst_null
    //   39: putfield 104	java/util/zip/DeflaterInputStream:in	Ljava/io/InputStream;
    //   42: aload_1
    //   43: athrow
    //   44: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	DeflaterInputStream
    //   36	7	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	28	36	finally
  }
  
  public int read()
    throws IOException
  {
    int i = read(rbuf, 0, 1);
    if (i <= 0) {
      return -1;
    }
    return rbuf[0] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if (paramArrayOfByte == null) {
      throw new NullPointerException("Null buffer for read");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = 0;
    while ((paramInt2 > 0) && (!def.finished()))
    {
      if (def.needsInput())
      {
        j = in.read(buf, 0, buf.length);
        if (j < 0) {
          def.finish();
        } else if (j > 0) {
          def.setInput(buf, 0, j);
        }
      }
      int j = def.deflate(paramArrayOfByte, paramInt1, paramInt2);
      i += j;
      paramInt1 += j;
      paramInt2 -= j;
    }
    if ((i == 0) && (def.finished()))
    {
      reachEOF = true;
      i = -1;
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("negative skip length");
    }
    ensureOpen();
    if (rbuf.length < 512) {
      rbuf = new byte['Ȁ'];
    }
    int i = (int)Math.min(paramLong, 2147483647L);
    long l = 0L;
    while (i > 0)
    {
      int j = read(rbuf, 0, i <= rbuf.length ? i : rbuf.length);
      if (j < 0) {
        break;
      }
      l += j;
      i -= j;
    }
    return l;
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
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void mark(int paramInt) {}
  
  public void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\DeflaterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
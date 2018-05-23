package sun.net.www;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.net.ProgressSource;
import sun.net.www.http.ChunkedInputStream;

public class MeteredStream
  extends FilterInputStream
{
  protected boolean closed = false;
  protected long expected;
  protected long count = 0L;
  protected long markedCount = 0L;
  protected int markLimit = -1;
  protected ProgressSource pi;
  
  public MeteredStream(InputStream paramInputStream, ProgressSource paramProgressSource, long paramLong)
  {
    super(paramInputStream);
    pi = paramProgressSource;
    expected = paramLong;
    if (paramProgressSource != null) {
      paramProgressSource.updateProgress(0L, paramLong);
    }
  }
  
  private final void justRead(long paramLong)
    throws IOException
  {
    if (paramLong == -1L)
    {
      if (!isMarked()) {
        close();
      }
      return;
    }
    count += paramLong;
    if (count - markedCount > markLimit) {
      markLimit = -1;
    }
    if (pi != null) {
      pi.updateProgress(count, expected);
    }
    if (isMarked()) {
      return;
    }
    if ((expected > 0L) && (count >= expected)) {
      close();
    }
  }
  
  private boolean isMarked()
  {
    if (markLimit < 0) {
      return false;
    }
    return count - markedCount <= markLimit;
  }
  
  public synchronized int read()
    throws IOException
  {
    if (closed) {
      return -1;
    }
    int i = in.read();
    if (i != -1) {
      justRead(1L);
    } else {
      justRead(i);
    }
    return i;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (closed) {
      return -1;
    }
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    justRead(i);
    return i;
  }
  
  public synchronized long skip(long paramLong)
    throws IOException
  {
    if (closed) {
      return 0L;
    }
    if ((in instanceof ChunkedInputStream))
    {
      paramLong = in.skip(paramLong);
    }
    else
    {
      long l = paramLong > expected - count ? expected - count : paramLong;
      paramLong = in.skip(l);
    }
    justRead(paramLong);
    return paramLong;
  }
  
  public void close()
    throws IOException
  {
    if (closed) {
      return;
    }
    if (pi != null) {
      pi.finishTracking();
    }
    closed = true;
    in.close();
  }
  
  public synchronized int available()
    throws IOException
  {
    return closed ? 0 : in.available();
  }
  
  public synchronized void mark(int paramInt)
  {
    if (closed) {
      return;
    }
    super.mark(paramInt);
    markedCount = count;
    markLimit = paramInt;
  }
  
  public synchronized void reset()
    throws IOException
  {
    if (closed) {
      return;
    }
    if (!isMarked()) {
      throw new IOException("Resetting to an invalid mark");
    }
    count = markedCount;
    super.reset();
  }
  
  public boolean markSupported()
  {
    if (closed) {
      return false;
    }
    return super.markSupported();
  }
  
  /* Error */
  protected void finalize()
    throws java.lang.Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 103	sun/net/www/MeteredStream:close	()V
    //   4: aload_0
    //   5: getfield 88	sun/net/www/MeteredStream:pi	Lsun/net/ProgressSource;
    //   8: ifnull +10 -> 18
    //   11: aload_0
    //   12: getfield 88	sun/net/www/MeteredStream:pi	Lsun/net/ProgressSource;
    //   15: invokevirtual 100	sun/net/ProgressSource:close	()V
    //   18: aload_0
    //   19: invokespecial 99	java/lang/Object:finalize	()V
    //   22: goto +10 -> 32
    //   25: astore_1
    //   26: aload_0
    //   27: invokespecial 99	java/lang/Object:finalize	()V
    //   30: aload_1
    //   31: athrow
    //   32: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	MeteredStream
    //   25	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	18	25	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\MeteredStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
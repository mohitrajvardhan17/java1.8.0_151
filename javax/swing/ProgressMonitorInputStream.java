package javax.swing;

import java.awt.Component;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class ProgressMonitorInputStream
  extends FilterInputStream
{
  private ProgressMonitor monitor;
  private int nread = 0;
  private int size = 0;
  
  public ProgressMonitorInputStream(Component paramComponent, Object paramObject, InputStream paramInputStream)
  {
    super(paramInputStream);
    try
    {
      size = paramInputStream.available();
    }
    catch (IOException localIOException)
    {
      size = 0;
    }
    monitor = new ProgressMonitor(paramComponent, paramObject, null, 0, size);
  }
  
  public ProgressMonitor getProgressMonitor()
  {
    return monitor;
  }
  
  public int read()
    throws IOException
  {
    int i = in.read();
    if (i >= 0) {
      monitor.setProgress(++nread);
    }
    if (monitor.isCanceled())
    {
      InterruptedIOException localInterruptedIOException = new InterruptedIOException("progress");
      bytesTransferred = nread;
      throw localInterruptedIOException;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = in.read(paramArrayOfByte);
    if (i > 0) {
      monitor.setProgress(nread += i);
    }
    if (monitor.isCanceled())
    {
      InterruptedIOException localInterruptedIOException = new InterruptedIOException("progress");
      bytesTransferred = nread;
      throw localInterruptedIOException;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i > 0) {
      monitor.setProgress(nread += i);
    }
    if (monitor.isCanceled())
    {
      InterruptedIOException localInterruptedIOException = new InterruptedIOException("progress");
      bytesTransferred = nread;
      throw localInterruptedIOException;
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    long l = in.skip(paramLong);
    if (l > 0L) {
      monitor.setProgress(nread = (int)(nread + l));
    }
    return l;
  }
  
  public void close()
    throws IOException
  {
    in.close();
    monitor.close();
  }
  
  public synchronized void reset()
    throws IOException
  {
    in.reset();
    nread = (size - in.available());
    monitor.setProgress(nread);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ProgressMonitorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
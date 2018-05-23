package java.io;

public class BufferedOutputStream
  extends FilterOutputStream
{
  protected byte[] buf;
  protected int count;
  
  public BufferedOutputStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, 8192);
  }
  
  public BufferedOutputStream(OutputStream paramOutputStream, int paramInt)
  {
    super(paramOutputStream);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Buffer size <= 0");
    }
    buf = new byte[paramInt];
  }
  
  private void flushBuffer()
    throws IOException
  {
    if (count > 0)
    {
      out.write(buf, 0, count);
      count = 0;
    }
  }
  
  public synchronized void write(int paramInt)
    throws IOException
  {
    if (count >= buf.length) {
      flushBuffer();
    }
    buf[(count++)] = ((byte)paramInt);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 >= buf.length)
    {
      flushBuffer();
      out.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    if (paramInt2 > buf.length - count) {
      flushBuffer();
    }
    System.arraycopy(paramArrayOfByte, paramInt1, buf, count, paramInt2);
    count += paramInt2;
  }
  
  public synchronized void flush()
    throws IOException
  {
    flushBuffer();
    out.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\BufferedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
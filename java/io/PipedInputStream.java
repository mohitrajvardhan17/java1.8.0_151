package java.io;

public class PipedInputStream
  extends InputStream
{
  boolean closedByWriter = false;
  volatile boolean closedByReader = false;
  boolean connected = false;
  Thread readSide;
  Thread writeSide;
  private static final int DEFAULT_PIPE_SIZE = 1024;
  protected static final int PIPE_SIZE = 1024;
  protected byte[] buffer;
  protected int in = -1;
  protected int out = 0;
  
  public PipedInputStream(PipedOutputStream paramPipedOutputStream)
    throws IOException
  {
    this(paramPipedOutputStream, 1024);
  }
  
  public PipedInputStream(PipedOutputStream paramPipedOutputStream, int paramInt)
    throws IOException
  {
    initPipe(paramInt);
    connect(paramPipedOutputStream);
  }
  
  public PipedInputStream()
  {
    initPipe(1024);
  }
  
  public PipedInputStream(int paramInt)
  {
    initPipe(paramInt);
  }
  
  private void initPipe(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Pipe Size <= 0");
    }
    buffer = new byte[paramInt];
  }
  
  public void connect(PipedOutputStream paramPipedOutputStream)
    throws IOException
  {
    paramPipedOutputStream.connect(this);
  }
  
  protected synchronized void receive(int paramInt)
    throws IOException
  {
    checkStateForReceive();
    writeSide = Thread.currentThread();
    if (in == out) {
      awaitSpace();
    }
    if (in < 0)
    {
      in = 0;
      out = 0;
    }
    buffer[(in++)] = ((byte)(paramInt & 0xFF));
    if (in >= buffer.length) {
      in = 0;
    }
  }
  
  synchronized void receive(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    checkStateForReceive();
    writeSide = Thread.currentThread();
    int i = paramInt2;
    while (i > 0)
    {
      if (in == out) {
        awaitSpace();
      }
      int j = 0;
      if (out < in) {
        j = buffer.length - in;
      } else if (in < out) {
        if (in == -1)
        {
          in = (out = 0);
          j = buffer.length - in;
        }
        else
        {
          j = out - in;
        }
      }
      if (j > i) {
        j = i;
      }
      assert (j > 0);
      System.arraycopy(paramArrayOfByte, paramInt1, buffer, in, j);
      i -= j;
      paramInt1 += j;
      in += j;
      if (in >= buffer.length) {
        in = 0;
      }
    }
  }
  
  private void checkStateForReceive()
    throws IOException
  {
    if (!connected) {
      throw new IOException("Pipe not connected");
    }
    if ((closedByWriter) || (closedByReader)) {
      throw new IOException("Pipe closed");
    }
    if ((readSide != null) && (!readSide.isAlive())) {
      throw new IOException("Read end dead");
    }
  }
  
  private void awaitSpace()
    throws IOException
  {
    while (in == out)
    {
      checkStateForReceive();
      notifyAll();
      try
      {
        wait(1000L);
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new InterruptedIOException();
      }
    }
  }
  
  synchronized void receivedLast()
  {
    closedByWriter = true;
    notifyAll();
  }
  
  public synchronized int read()
    throws IOException
  {
    if (!connected) {
      throw new IOException("Pipe not connected");
    }
    if (closedByReader) {
      throw new IOException("Pipe closed");
    }
    if ((writeSide != null) && (!writeSide.isAlive()) && (!closedByWriter) && (in < 0)) {
      throw new IOException("Write end dead");
    }
    readSide = Thread.currentThread();
    int i = 2;
    while (in < 0)
    {
      if (closedByWriter) {
        return -1;
      }
      if ((writeSide != null) && (!writeSide.isAlive()))
      {
        i--;
        if (i < 0) {
          throw new IOException("Pipe broken");
        }
      }
      notifyAll();
      try
      {
        wait(1000L);
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new InterruptedIOException();
      }
    }
    int j = buffer[(out++)] & 0xFF;
    if (out >= buffer.length) {
      out = 0;
    }
    if (in == out) {
      in = -1;
    }
    return j;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = read();
    if (i < 0) {
      return -1;
    }
    paramArrayOfByte[paramInt1] = ((byte)i);
    int j = 1;
    while ((in >= 0) && (paramInt2 > 1))
    {
      int k;
      if (in > out) {
        k = Math.min(buffer.length - out, in - out);
      } else {
        k = buffer.length - out;
      }
      if (k > paramInt2 - 1) {
        k = paramInt2 - 1;
      }
      System.arraycopy(buffer, out, paramArrayOfByte, paramInt1 + j, k);
      out += k;
      j += k;
      paramInt2 -= k;
      if (out >= buffer.length) {
        out = 0;
      }
      if (in == out) {
        in = -1;
      }
    }
    return j;
  }
  
  public synchronized int available()
    throws IOException
  {
    if (in < 0) {
      return 0;
    }
    if (in == out) {
      return buffer.length;
    }
    if (in > out) {
      return in - out;
    }
    return in + buffer.length - out;
  }
  
  public void close()
    throws IOException
  {
    closedByReader = true;
    synchronized (this)
    {
      in = -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PipedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
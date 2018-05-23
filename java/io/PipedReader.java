package java.io;

public class PipedReader
  extends Reader
{
  boolean closedByWriter = false;
  boolean closedByReader = false;
  boolean connected = false;
  Thread readSide;
  Thread writeSide;
  private static final int DEFAULT_PIPE_SIZE = 1024;
  char[] buffer;
  int in = -1;
  int out = 0;
  
  public PipedReader(PipedWriter paramPipedWriter)
    throws IOException
  {
    this(paramPipedWriter, 1024);
  }
  
  public PipedReader(PipedWriter paramPipedWriter, int paramInt)
    throws IOException
  {
    initPipe(paramInt);
    connect(paramPipedWriter);
  }
  
  public PipedReader()
  {
    initPipe(1024);
  }
  
  public PipedReader(int paramInt)
  {
    initPipe(paramInt);
  }
  
  private void initPipe(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Pipe size <= 0");
    }
    buffer = new char[paramInt];
  }
  
  public void connect(PipedWriter paramPipedWriter)
    throws IOException
  {
    paramPipedWriter.connect(this);
  }
  
  synchronized void receive(int paramInt)
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
    writeSide = Thread.currentThread();
    while (in == out)
    {
      if ((readSide != null) && (!readSide.isAlive())) {
        throw new IOException("Pipe broken");
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
    if (in < 0)
    {
      in = 0;
      out = 0;
    }
    buffer[(in++)] = ((char)paramInt);
    if (in >= buffer.length) {
      in = 0;
    }
  }
  
  synchronized void receive(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    for (;;)
    {
      
      if (paramInt2 < 0) {
        break;
      }
      receive(paramArrayOfChar[(paramInt1++)]);
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
    int j = buffer[(out++)];
    if (out >= buffer.length) {
      out = 0;
    }
    if (in == out) {
      in = -1;
    }
    return j;
  }
  
  public synchronized int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
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
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = read();
    if (i < 0) {
      return -1;
    }
    paramArrayOfChar[paramInt1] = ((char)i);
    int j = 1;
    while (in >= 0)
    {
      paramInt2--;
      if (paramInt2 <= 0) {
        break;
      }
      paramArrayOfChar[(paramInt1 + j)] = buffer[(out++)];
      j++;
      if (out >= buffer.length) {
        out = 0;
      }
      if (in == out) {
        in = -1;
      }
    }
    return j;
  }
  
  public synchronized boolean ready()
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
    return in >= 0;
  }
  
  public void close()
    throws IOException
  {
    in = -1;
    closedByReader = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PipedReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
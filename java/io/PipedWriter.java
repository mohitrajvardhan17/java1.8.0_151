package java.io;

public class PipedWriter
  extends Writer
{
  private PipedReader sink;
  private boolean closed = false;
  
  public PipedWriter(PipedReader paramPipedReader)
    throws IOException
  {
    connect(paramPipedReader);
  }
  
  public PipedWriter() {}
  
  public synchronized void connect(PipedReader paramPipedReader)
    throws IOException
  {
    if (paramPipedReader == null) {
      throw new NullPointerException();
    }
    if ((sink != null) || (connected)) {
      throw new IOException("Already connected");
    }
    if ((closedByReader) || (closed)) {
      throw new IOException("Pipe closed");
    }
    sink = paramPipedReader;
    in = -1;
    out = 0;
    connected = true;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (sink == null) {
      throw new IOException("Pipe not connected");
    }
    sink.receive(paramInt);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (sink == null) {
      throw new IOException("Pipe not connected");
    }
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfChar.length - (paramInt1 + paramInt2)) < 0) {
      throw new IndexOutOfBoundsException();
    }
    sink.receive(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public synchronized void flush()
    throws IOException
  {
    if (sink != null)
    {
      if ((sink.closedByReader) || (closed)) {
        throw new IOException("Pipe closed");
      }
      synchronized (sink)
      {
        sink.notifyAll();
      }
    }
  }
  
  public void close()
    throws IOException
  {
    closed = true;
    if (sink != null) {
      sink.receivedLast();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PipedWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
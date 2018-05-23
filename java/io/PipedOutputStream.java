package java.io;

public class PipedOutputStream
  extends OutputStream
{
  private PipedInputStream sink;
  
  public PipedOutputStream(PipedInputStream paramPipedInputStream)
    throws IOException
  {
    connect(paramPipedInputStream);
  }
  
  public PipedOutputStream() {}
  
  public synchronized void connect(PipedInputStream paramPipedInputStream)
    throws IOException
  {
    if (paramPipedInputStream == null) {
      throw new NullPointerException();
    }
    if ((sink != null) || (connected)) {
      throw new IOException("Already connected");
    }
    sink = paramPipedInputStream;
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
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (sink == null) {
      throw new IOException("Pipe not connected");
    }
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return;
    }
    sink.receive(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public synchronized void flush()
    throws IOException
  {
    if (sink != null) {
      synchronized (sink)
      {
        sink.notifyAll();
      }
    }
  }
  
  public void close()
    throws IOException
  {
    if (sink != null) {
      sink.receivedLast();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\PipedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
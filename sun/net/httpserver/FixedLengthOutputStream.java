package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class FixedLengthOutputStream
  extends FilterOutputStream
{
  private long remaining;
  private boolean eof = false;
  private boolean closed = false;
  ExchangeImpl t;
  
  FixedLengthOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream, long paramLong)
  {
    super(paramOutputStream);
    t = paramExchangeImpl;
    remaining = paramLong;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (closed) {
      throw new IOException("stream closed");
    }
    eof = (remaining == 0L);
    if (eof) {
      throw new StreamClosedException();
    }
    out.write(paramInt);
    remaining -= 1L;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (closed) {
      throw new IOException("stream closed");
    }
    eof = (remaining == 0L);
    if (eof) {
      throw new StreamClosedException();
    }
    if (paramInt2 > remaining) {
      throw new IOException("too many bytes to write to stream");
    }
    out.write(paramArrayOfByte, paramInt1, paramInt2);
    remaining -= paramInt2;
  }
  
  public void close()
    throws IOException
  {
    if (closed) {
      return;
    }
    closed = true;
    if (remaining > 0L)
    {
      t.close();
      throw new IOException("insufficient bytes written to stream");
    }
    flush();
    eof = true;
    LeftOverInputStream localLeftOverInputStream = t.getOriginalInputStream();
    if (!localLeftOverInputStream.isClosed()) {
      try
      {
        localLeftOverInputStream.close();
      }
      catch (IOException localIOException) {}
    }
    WriteFinishedEvent localWriteFinishedEvent = new WriteFinishedEvent(t);
    t.getHttpContext().getServerImpl().addEvent(localWriteFinishedEvent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\FixedLengthOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
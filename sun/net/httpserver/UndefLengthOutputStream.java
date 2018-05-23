package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class UndefLengthOutputStream
  extends FilterOutputStream
{
  private boolean closed = false;
  ExchangeImpl t;
  
  UndefLengthOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    t = paramExchangeImpl;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (closed) {
      throw new IOException("stream closed");
    }
    out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (closed) {
      throw new IOException("stream closed");
    }
    out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void close()
    throws IOException
  {
    if (closed) {
      return;
    }
    closed = true;
    flush();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\UndefLengthOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
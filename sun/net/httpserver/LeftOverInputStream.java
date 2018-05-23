package sun.net.httpserver;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

abstract class LeftOverInputStream
  extends FilterInputStream
{
  ExchangeImpl t;
  ServerImpl server;
  protected boolean closed = false;
  protected boolean eof = false;
  byte[] one = new byte[1];
  
  public LeftOverInputStream(ExchangeImpl paramExchangeImpl, InputStream paramInputStream)
  {
    super(paramInputStream);
    t = paramExchangeImpl;
    server = paramExchangeImpl.getServerImpl();
  }
  
  public boolean isDataBuffered()
    throws IOException
  {
    assert (eof);
    return super.available() > 0;
  }
  
  public void close()
    throws IOException
  {
    if (closed) {
      return;
    }
    closed = true;
    if (!eof) {
      eof = drain(ServerConfig.getDrainAmount());
    }
  }
  
  public boolean isClosed()
  {
    return closed;
  }
  
  public boolean isEOF()
  {
    return eof;
  }
  
  protected abstract int readImpl(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public synchronized int read()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream is closed");
    }
    int i = readImpl(one, 0, 1);
    if ((i == -1) || (i == 0)) {
      return i;
    }
    return one[0] & 0xFF;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream is closed");
    }
    return readImpl(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public boolean drain(long paramLong)
    throws IOException
  {
    int i = 2048;
    byte[] arrayOfByte = new byte[i];
    while (paramLong > 0L)
    {
      long l = readImpl(arrayOfByte, 0, i);
      if (l == -1L)
      {
        eof = true;
        return true;
      }
      paramLong -= l;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\LeftOverInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
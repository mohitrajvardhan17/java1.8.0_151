package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class FixedLengthInputStream
  extends LeftOverInputStream
{
  private long remaining;
  
  FixedLengthInputStream(ExchangeImpl paramExchangeImpl, InputStream paramInputStream, long paramLong)
  {
    super(paramExchangeImpl, paramInputStream);
    remaining = paramLong;
  }
  
  protected int readImpl(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    eof = (remaining == 0L);
    if (eof) {
      return -1;
    }
    if (paramInt2 > remaining) {
      paramInt2 = (int)remaining;
    }
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i > -1)
    {
      remaining -= i;
      if (remaining == 0L) {
        t.getServerImpl().requestCompleted(t.getConnection());
      }
    }
    return i;
  }
  
  public int available()
    throws IOException
  {
    if (eof) {
      return 0;
    }
    int i = in.available();
    return i < remaining ? i : (int)remaining;
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void mark(int paramInt) {}
  
  public void reset()
    throws IOException
  {
    throw new IOException("mark/reset not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\FixedLengthInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
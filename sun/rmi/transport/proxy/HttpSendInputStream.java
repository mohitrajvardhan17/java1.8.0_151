package sun.rmi.transport.proxy;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpSendInputStream
  extends FilterInputStream
{
  HttpSendSocket owner;
  
  public HttpSendInputStream(InputStream paramInputStream, HttpSendSocket paramHttpSendSocket)
    throws IOException
  {
    super(paramInputStream);
    owner = paramHttpSendSocket;
  }
  
  public void deactivate()
  {
    in = null;
  }
  
  public int read()
    throws IOException
  {
    if (in == null) {
      in = owner.readNotify();
    }
    return in.read();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (in == null) {
      in = owner.readNotify();
    }
    return in.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong == 0L) {
      return 0L;
    }
    if (in == null) {
      in = owner.readNotify();
    }
    return in.skip(paramLong);
  }
  
  public int available()
    throws IOException
  {
    if (in == null) {
      in = owner.readNotify();
    }
    return in.available();
  }
  
  public void close()
    throws IOException
  {
    owner.close();
  }
  
  public synchronized void mark(int paramInt)
  {
    if (in == null) {
      try
      {
        in = owner.readNotify();
      }
      catch (IOException localIOException)
      {
        return;
      }
    }
    in.mark(paramInt);
  }
  
  public synchronized void reset()
    throws IOException
  {
    if (in == null) {
      in = owner.readNotify();
    }
    in.reset();
  }
  
  public boolean markSupported()
  {
    if (in == null) {
      try
      {
        in = owner.readNotify();
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
    return in.markSupported();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\HttpSendInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
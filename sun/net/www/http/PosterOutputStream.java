package sun.net.www.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PosterOutputStream
  extends ByteArrayOutputStream
{
  private boolean closed;
  
  public PosterOutputStream()
  {
    super(256);
  }
  
  public synchronized void write(int paramInt)
  {
    if (closed) {
      return;
    }
    super.write(paramInt);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (closed) {
      return;
    }
    super.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public synchronized void reset()
  {
    if (closed) {
      return;
    }
    super.reset();
  }
  
  public synchronized void close()
    throws IOException
  {
    closed = true;
    super.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\PosterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
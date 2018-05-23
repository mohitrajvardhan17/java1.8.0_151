package sun.awt.windows;

import java.io.IOException;
import java.io.InputStream;

final class WDropTargetContextPeerIStream
  extends InputStream
{
  private long istream;
  
  WDropTargetContextPeerIStream(long paramLong)
    throws IOException
  {
    if (paramLong == 0L) {
      throw new IOException("No IStream");
    }
    istream = paramLong;
  }
  
  public int available()
    throws IOException
  {
    if (istream == 0L) {
      throw new IOException("No IStream");
    }
    return Available(istream);
  }
  
  private native int Available(long paramLong);
  
  public int read()
    throws IOException
  {
    if (istream == 0L) {
      throw new IOException("No IStream");
    }
    return Read(istream);
  }
  
  private native int Read(long paramLong)
    throws IOException;
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (istream == 0L) {
      throw new IOException("No IStream");
    }
    return ReadBytes(istream, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private native int ReadBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public void close()
    throws IOException
  {
    if (istream != 0L)
    {
      super.close();
      Close(istream);
      istream = 0L;
    }
  }
  
  private native void Close(long paramLong);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDropTargetContextPeerIStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
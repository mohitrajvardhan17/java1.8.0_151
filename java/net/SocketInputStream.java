package java.net;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import sun.net.ConnectionResetException;

class SocketInputStream
  extends FileInputStream
{
  private boolean eof;
  private AbstractPlainSocketImpl impl = null;
  private byte[] temp;
  private Socket socket = null;
  private boolean closing = false;
  
  SocketInputStream(AbstractPlainSocketImpl paramAbstractPlainSocketImpl)
    throws IOException
  {
    super(paramAbstractPlainSocketImpl.getFileDescriptor());
    impl = paramAbstractPlainSocketImpl;
    socket = paramAbstractPlainSocketImpl.getSocket();
  }
  
  public final FileChannel getChannel()
  {
    return null;
  }
  
  private native int socketRead0(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException;
  
  private int socketRead(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    return socketRead0(paramFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return read(paramArrayOfByte, paramInt1, paramInt2, impl.getTimeout());
  }
  
  int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (eof) {
      return -1;
    }
    if (impl.isConnectionReset()) {
      throw new SocketException("Connection reset");
    }
    if ((paramInt2 <= 0) || (paramInt1 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1))
    {
      if (paramInt2 == 0) {
        return 0;
      }
      throw new ArrayIndexOutOfBoundsException("length == " + paramInt2 + " off == " + paramInt1 + " buffer length == " + paramArrayOfByte.length);
    }
    int i = 0;
    FileDescriptor localFileDescriptor = impl.acquireFD();
    ConnectionResetException localConnectionResetException1;
    try
    {
      localConnectionResetException1 = socketRead(localFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      if (localConnectionResetException1 > 0)
      {
        int j = localConnectionResetException1;
        return j;
      }
    }
    catch (ConnectionResetException localConnectionResetException2)
    {
      i = 1;
    }
    finally
    {
      impl.releaseFD();
    }
    if (i != 0)
    {
      impl.setConnectionResetPending();
      impl.acquireFD();
      try
      {
        localConnectionResetException1 = socketRead(localFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
        if (localConnectionResetException1 > 0)
        {
          localConnectionResetException2 = localConnectionResetException1;
          return localConnectionResetException2;
        }
      }
      catch (ConnectionResetException localConnectionResetException3) {}finally
      {
        impl.releaseFD();
      }
    }
    if (impl.isClosedOrPending()) {
      throw new SocketException("Socket closed");
    }
    if (impl.isConnectionResetPending()) {
      impl.setConnectionReset();
    }
    if (impl.isConnectionReset()) {
      throw new SocketException("Connection reset");
    }
    eof = true;
    return -1;
  }
  
  public int read()
    throws IOException
  {
    if (eof) {
      return -1;
    }
    temp = new byte[1];
    int i = read(temp, 0, 1);
    if (i <= 0) {
      return -1;
    }
    return temp[0] & 0xFF;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong <= 0L) {
      return 0L;
    }
    long l = paramLong;
    int i = (int)Math.min(1024L, l);
    byte[] arrayOfByte = new byte[i];
    while (l > 0L)
    {
      int j = read(arrayOfByte, 0, (int)Math.min(i, l));
      if (j < 0) {
        break;
      }
      l -= j;
    }
    return paramLong - l;
  }
  
  public int available()
    throws IOException
  {
    return impl.available();
  }
  
  public void close()
    throws IOException
  {
    if (closing) {
      return;
    }
    closing = true;
    if (socket != null)
    {
      if (!socket.isClosed()) {
        socket.close();
      }
    }
    else {
      impl.close();
    }
    closing = false;
  }
  
  void setEOF(boolean paramBoolean)
  {
    eof = paramBoolean;
  }
  
  protected void finalize() {}
  
  private static native void init();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocketInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
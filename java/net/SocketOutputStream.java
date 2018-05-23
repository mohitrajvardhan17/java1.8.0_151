package java.net;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import sun.net.ConnectionResetException;

class SocketOutputStream
  extends FileOutputStream
{
  private AbstractPlainSocketImpl impl = null;
  private byte[] temp = new byte[1];
  private Socket socket = null;
  private boolean closing = false;
  
  SocketOutputStream(AbstractPlainSocketImpl paramAbstractPlainSocketImpl)
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
  
  private native void socketWrite0(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  private void socketWrite(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt2 <= 0) || (paramInt1 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1))
    {
      if (paramInt2 == 0) {
        return;
      }
      throw new ArrayIndexOutOfBoundsException("len == " + paramInt2 + " off == " + paramInt1 + " buffer length == " + paramArrayOfByte.length);
    }
    FileDescriptor localFileDescriptor = impl.acquireFD();
    try
    {
      socketWrite0(localFileDescriptor, paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (SocketException localSocketException1)
    {
      SocketException localSocketException2;
      if ((localSocketException1 instanceof ConnectionResetException))
      {
        impl.setConnectionResetPending();
        localSocketException2 = new SocketException("Connection reset");
      }
      if (impl.isClosedOrPending()) {
        throw new SocketException("Socket closed");
      }
      throw localSocketException2;
    }
    finally
    {
      impl.releaseFD();
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    temp[0] = ((byte)paramInt);
    socketWrite(temp, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    socketWrite(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    socketWrite(paramArrayOfByte, paramInt1, paramInt2);
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
  
  protected void finalize() {}
  
  private static native void init();
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocketOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
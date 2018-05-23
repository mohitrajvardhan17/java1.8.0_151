package sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SelectableChannel;

public class ChannelInputStream
  extends InputStream
{
  protected final ReadableByteChannel ch;
  private ByteBuffer bb = null;
  private byte[] bs = null;
  private byte[] b1 = null;
  
  public static int read(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, boolean paramBoolean)
    throws IOException
  {
    if ((paramReadableByteChannel instanceof SelectableChannel))
    {
      SelectableChannel localSelectableChannel = (SelectableChannel)paramReadableByteChannel;
      synchronized (localSelectableChannel.blockingLock())
      {
        boolean bool = localSelectableChannel.isBlocking();
        if (!bool) {
          throw new IllegalBlockingModeException();
        }
        if (bool != paramBoolean) {
          localSelectableChannel.configureBlocking(paramBoolean);
        }
        int i = paramReadableByteChannel.read(paramByteBuffer);
        if (bool != paramBoolean) {
          localSelectableChannel.configureBlocking(bool);
        }
        return i;
      }
    }
    return paramReadableByteChannel.read(paramByteBuffer);
  }
  
  public ChannelInputStream(ReadableByteChannel paramReadableByteChannel)
  {
    ch = paramReadableByteChannel;
  }
  
  public synchronized int read()
    throws IOException
  {
    if (b1 == null) {
      b1 = new byte[1];
    }
    int i = read(b1);
    if (i == 1) {
      return b1[0] & 0xFF;
    }
    return -1;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    ByteBuffer localByteBuffer = bs == paramArrayOfByte ? bb : ByteBuffer.wrap(paramArrayOfByte);
    localByteBuffer.limit(Math.min(paramInt1 + paramInt2, localByteBuffer.capacity()));
    localByteBuffer.position(paramInt1);
    bb = localByteBuffer;
    bs = paramArrayOfByte;
    return read(localByteBuffer);
  }
  
  protected int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    return read(ch, paramByteBuffer, true);
  }
  
  public int available()
    throws IOException
  {
    if ((ch instanceof SeekableByteChannel))
    {
      SeekableByteChannel localSeekableByteChannel = (SeekableByteChannel)ch;
      long l = Math.max(0L, localSeekableByteChannel.size() - localSeekableByteChannel.position());
      return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
    }
    return 0;
  }
  
  public void close()
    throws IOException
  {
    ch.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\ChannelInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */